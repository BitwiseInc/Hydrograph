/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package hydrograph.engine.spark.datasource.avro

import org.apache.hadoop.mapreduce.TaskAttemptContext
import org.apache.spark.sql.execution.datasources.{OutputWriter, OutputWriterFactory}
import org.apache.spark.sql.types.StructType
/**
  * The Class AvroOutputGeneratorFactory.
  *
  * @author Bitwise
  *
  */
 private class AvroOutputGeneratorFactory(schema: StructType, recordName: String, recordNamespace: String) extends OutputWriterFactory {

   override def newInstance(
      path: String,
      bucketId: Option[Int],
      dataSchema: StructType,
      context: TaskAttemptContext): OutputWriter = {
     new AvroOutputGenerator(path,context,schema,recordName,recordNamespace)
   }

   def getFileExtension(context: TaskAttemptContext): String ={
     ".avro"
   }
 }