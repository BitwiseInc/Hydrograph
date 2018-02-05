/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hydrograph.engine.spark.datasource.csvwithunivocity

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{NullWritable, Text}
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import org.apache.hadoop.mapreduce.{RecordWriter, TaskAttemptContext}
import org.apache.spark.io.CompressionCodec
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.{GenericInternalRow}
import org.apache.spark.sql.catalyst.util.DateTimeUtils
import org.apache.spark.sql.execution.datasources.{OutputWriter, OutputWriterFactory, PartitionedFile}
import org.apache.spark.sql.types._
import org.slf4j.{Logger, LoggerFactory}

import scala.util.control.NonFatal

/**
  * The Object CSVRelation.
  *
  * @author Bitwise
  *
  */
object CSVRelation {
  private val LOG: Logger = LoggerFactory.getLogger(CSVRelation.getClass.getCanonicalName)

  def univocityTokenizer(
                          file: RDD[String],
                          header: Seq[String],
                          firstLine: String,
                          params: CSVOptions): RDD[Array[String]] = {
    // If header is set, make sure firstLine is materialized before sending to executors.
    file.mapPartitions { iter =>
      new BulkCsvReader(
        if (params.headerFlag) iter.filterNot(_ == firstLine) else iter,
        params,
        headers = header)
    }
  }

  /**
    * Returns a function that parses a single CSV record (in the form of an array of strings in which
    * each element represents a column) and turns it into either one resulting row or no row (if the
    * the record is malformed).
    *
    * The 2nd argument in the returned function represents the total number of malformed rows
    * observed so far.
    */
  // This is pretty convoluted and we should probably rewrite the entire CSV parsing soon.
  def csvParser(
                 schema: StructType,
                 requiredColumns: Array[String],
                 params: CSVOptions): (Array[String], Int) => Option[InternalRow] = {
    val schemaFields = schema.fields
    val requiredFields = StructType(requiredColumns.map(schema(_))).fields
    val safeRequiredFields = if (params.dropMalformed) {
      // If `dropMalformed` is enabled, then it needs to parse all the values
      // so that we can decide which row is malformed.
      requiredFields ++ schemaFields.filterNot(requiredFields.contains(_))
    } else {
      requiredFields
    }
    val safeRequiredIndices = new Array[Int](safeRequiredFields.length)
    schemaFields.zipWithIndex.filter {
      case (field, _) => safeRequiredFields.contains(field)
    }.foreach {
      case (field, index) => safeRequiredIndices(safeRequiredFields.indexOf(field)) = index
    }
    val requiredSize = requiredFields.length
    val row = new GenericInternalRow(requiredSize)

    (tokens: Array[String], numMalformedRows) => {
      if (params.dropMalformed && schemaFields.length != tokens.length) {
        if (numMalformedRows < params.maxMalformedLogPerPartition) {
          LOG.warn(s"Dropping malformed line: ${tokens.mkString(params.delimiter.toString)}")
        }
        if (numMalformedRows == params.maxMalformedLogPerPartition - 1) {
          LOG.warn(
            s"More than ${params.maxMalformedLogPerPartition} malformed records have been " +
              "found on this partition. Malformed records from now on will not be logged.")
        }
        None
      } else if (params.failFast && schemaFields.length != tokens.length) {
        throw new RuntimeException(s"Malformed line found in strict mode: '" +
          s"${tokens.mkString(params.delimiter.toString)}" + "'.Number of fields specified in schema is "
          + schemaFields.length + " and number of fields in line is " + tokens.length)
      } else {
        val indexSafeTokens = if (params.permissive && schemaFields.length > tokens.length) {
          tokens ++ new Array[String](schemaFields.length - tokens.length)
        } else if (params.permissive && schemaFields.length < tokens.length) {
          tokens.take(schemaFields.length)
        } else {
          tokens
        }
        try {

          def getSafeMessage(value: Any, i: Int): String = {
            try
              return "field " + schemaFields.apply(i).name + " cannot be coerced from : " + value + " to: " + schema.apply(i).dataType

            catch {
              case throwable: Throwable => {
                return "field pos " + i + " cannot be coerced from: " + value + ", pos has no corresponding field name or coercion type"
              }
            }
          }

          var index = 0
          var subIndex = 0
          while (subIndex < safeRequiredIndices.length) {
            index = safeRequiredIndices(subIndex)
            val field = schemaFields(index)
            // It anyway needs to try to parse since it decides if this row is malformed
            // or not after trying to cast in `DROPMALFORMED` mode even if the casted
            // value is not stored in the row.
            var value: Any = ""
            try {
              value = CSVTypeCast.castTo(
                indexSafeTokens(index),
                field.dataType,
                field.nullable,
                params.dateFormat(index),
                params)

              if (subIndex < requiredSize) row(subIndex) = value
              subIndex = subIndex + 1
            } catch {
              case NonFatal(e) => if (params.isSafe) {
                if (subIndex < requiredSize) {
                  row(subIndex) = null
                }
                subIndex = subIndex + 1
              } else {
                LOG.error(getSafeMessage(indexSafeTokens(index), index) + "\n Line being parsed => " + s"${tokens.mkString(params.delimiter.toString)}")
                throw new RuntimeException(getSafeMessage(indexSafeTokens(index), index) + "\n Line being parsed => " + s"${tokens.mkString(params.delimiter.toString)}", e)
              }
            }
          }
          Some(row)
        } catch {
          case NonFatal(e) if params.dropMalformed =>
            if (numMalformedRows < params.maxMalformedLogPerPartition) {
              LOG.warn("Parse exception. " +
                s"Dropping malformed line: ${tokens.mkString(params.delimiter.toString)}")
            }
            if (numMalformedRows == params.maxMalformedLogPerPartition - 1) {
              LOG.warn(
                s"More than ${params.maxMalformedLogPerPartition} malformed records have been " +
                  "found on this partition. Malformed records from now on will not be logged.")
            }
            None
        }
      }
    }
  }


  // Skips the header line of each file if the `header` option is set to true.
  def dropHeaderLine(
                      file: PartitionedFile, lines: Iterator[String], csvOptions: CSVOptions): Unit = {
    // TODO What if the first partitioned file consists of only comments and empty lines?
    if (csvOptions.headerFlag && file.start == 0) {
      val nonEmptyLines = if (csvOptions.isCommentSet) {
        val commentPrefix = csvOptions.comment.toString
        lines.dropWhile { line =>
          line.trim.isEmpty || line.trim.startsWith(commentPrefix)
        }
      } else {
        lines.dropWhile(_.trim.isEmpty)
      }

      if (nonEmptyLines.hasNext) nonEmptyLines.drop(1)
    }
  }
}

private class CSVOutputWriterFactory(params: CSVOptions) extends OutputWriterFactory {
  override def getFileExtension(context: TaskAttemptContext): String = ".csv"
  override def newInstance(
                            path: String,
                            dataSchema: StructType,
                            context: TaskAttemptContext): OutputWriter = {
    new CsvOutputWriter(path, dataSchema, context, params)
  }
}

private class CsvOutputWriter(
                               path: String,
                               dataSchema: StructType,
                               context: TaskAttemptContext,
                               params: CSVOptions) extends OutputWriter {

  // A `ValueConverter` is responsible for converting a value of an `InternalRow` to `String`.
  // When the value is null, this converter should not be called.
  private type ValueConverter = (InternalRow, Int) => String
  // create the Generator without separator inserted between 2 records
  private[this] val text = new Text()
  // `ValueConverter`s for all values in the fields of the schema
  private val valueConverters: Array[ValueConverter] =
    dataSchema.map(_.dataType).map(makeConverter).toArray

  private val recordWriter: RecordWriter[NullWritable, Text] = {
    new TextOutputFormat[NullWritable, Text]() {
      override def getDefaultWorkFile(context: TaskAttemptContext, extension: String): Path = {
        new Path(path)
      }
    }.getRecordWriter(context)
  }

  private val FLUSH_BATCH_SIZE = 1024L
  private val csvWriter = new LineCsvWriter(params, dataSchema.fieldNames.toSeq)
  private var records: Long = 0L

  override def write(row: InternalRow): Unit = {
    csvWriter.writeRow(rowToString(row), records == 0L && params.headerFlag)
    records += 1
    if (records % FLUSH_BATCH_SIZE == 0) {
      flush()
    }
  }

  private def rowToString(row: InternalRow): Seq[String] = {
    var i = 0
    val values = new Array[String](row.numFields)
    while (i < row.numFields) {
      if (!row.isNullAt(i)) {
        values(i) = valueConverters(i).apply(row, i)
      } else {
        values(i) = params.nullValue
      }
      i += 1
    }
    values
  }

  override def close(): Unit = {
    flush()
    recordWriter.close(context)
  }

  private def flush(): Unit = {
    val lines = csvWriter.flush()
    if (lines.nonEmpty) {
      text.set(lines)
      recordWriter.write(NullWritable.get(), text)
    }
  }

  private def makeConverter(dataType: DataType): ValueConverter = dataType match {
    case DateType =>
      (row: InternalRow, ordinal: Int) =>
        params.dateFormat(ordinal).format(DateTimeUtils.toJavaDate(row.getInt(ordinal)))

    case TimestampType =>
      (row: InternalRow, ordinal: Int) =>
        params.dateFormat(ordinal).format(DateTimeUtils.toJavaTimestamp(row.getLong(ordinal)))

    //    case udt: UserDefinedType[_] => makeConverter(udt.sqlType)

    case dt: DataType =>
      (row: InternalRow, ordinal: Int) =>
        row.get(ordinal, dt).toString
  }
}
