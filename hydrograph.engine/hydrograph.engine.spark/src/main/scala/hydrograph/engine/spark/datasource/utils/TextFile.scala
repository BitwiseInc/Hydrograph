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
package hydrograph.engine.spark.datasource.utils

import java.nio.charset.Charset

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.SequenceFile.CompressionType
import org.apache.hadoop.io.compress._
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred.TextInputFormat
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
/**
  * The Object TextFile .
  *
  * @author Bitwise
  *
  */
object TextFile {

  val DEFAULT_CHARSET = Charset.forName("UTF-8")

  /**
    * Reads a TextFile with specified Charset
    *
    * @param context
    * @param location
    * @param charset
    * @return RDD[String]
    */
  def withCharset(context: SparkContext, location: String, charset: String): RDD[String] = {
    if (Charset.forName(charset) == DEFAULT_CHARSET) {
      context.textFile(location)
    } else {
      // can't pass a Charset object here cause its not serializable
      context.hadoopFile[LongWritable, Text, TextInputFormat](location).mapPartitions {
        iter =>
        {

          iter.map(pair => new String(pair._2.getBytes, 0, pair._2.getLength, charset))

        }
      }
    }
  }
}

object CompressionCodecs {
  /*  private val shortCompressionCodecNames: Map[String, String] = {
      val codecMap = collection.mutable.Map.empty[String, String]
      allCatch toTry(codecMap += "bzip2" -> classOf[BZip2Codec].getName)
      allCatch toTry(codecMap += "gzip" -> classOf[GzipCodec].getName)
      allCatch toTry(codecMap += "lz4" -> classOf[Lz4Codec].getName)
      allCatch toTry(codecMap += "snappy" -> classOf[SnappyCodec].getName)
      codecMap.toMap
    }*/

  private val shortCompressionCodecNames = Map(
    "none" -> null,
    "uncompressed" -> null,
    "bzip2" -> classOf[BZip2Codec].getName,
    "deflate" -> classOf[DeflateCodec].getName,
    "gzip" -> classOf[GzipCodec].getName,
    "lz4" -> classOf[Lz4Codec].getName,
    "snappy" -> classOf[SnappyCodec].getName)

  /**
    * Return the codec class of the given name.
    */
  def getCodecClass: String => Class[_ <: CompressionCodec] = {
    case null => null
    case codec =>
      val codecName = shortCompressionCodecNames.getOrElse(codec.toLowerCase, codec)
      try {
        // scalastyle:off classforname
        Class.forName(codecName).asInstanceOf[Class[CompressionCodec]]
        // scalastyle:on classforname
      } catch {
        case e: ClassNotFoundException =>
          throw new IllegalArgumentException(s"Codec [$codecName] is not " +
            s"available. Known codecs are ${shortCompressionCodecNames.keys.mkString(", ")}.",e)
      }
  }

  def getCodec(sparkContext: SparkContext, s: String): String = {
    if (s == null){
      if (sparkContext.getConf.contains("spark.io.compression.codec")){
        sparkContext.getConf.get("spark.io.compression.codec")
      } else {
        null
      }
    } else {
      s
    }
  }


  /**
    * Set compression configurations to Hadoop `Configuration`.
    * `codec` should be a full class path
    */
  def setCodecConfiguration(conf: Configuration, codec: String): Unit = {
    if (codec != null) {
      conf.set("mapreduce.output.fileoutputformat.compress", "true")
      conf.set("mapreduce.output.fileoutputformat.compress.type", CompressionType.BLOCK.toString)
      conf.set("mapreduce.output.fileoutputformat.compress.codec", codec)
      conf.set("mapreduce.map.output.compress", "true")
      conf.set("mapreduce.map.output.compress.codec", codec)
    } else {
      // This infers the option `compression` is set to `uncompressed` or `none`.
      conf.set("mapreduce.output.fileoutputformat.compress", "false")
      conf.set("mapreduce.map.output.compress", "false")
    }
  }

  def getCodecClassName(name: String): String = {
    val codecName = shortCompressionCodecNames.getOrElse(name.toLowerCase, name)
    try {
      // Validate the codec name
      if (codecName != null) {
        Utils.classForName(codecName)
      }
      codecName
    } catch {
      case e: ClassNotFoundException =>
        throw new IllegalArgumentException(s"Codec [$codecName] " +
          s"is not available. Known codecs are ${shortCompressionCodecNames.keys.mkString(", ")}.",e)
    }
  }
}