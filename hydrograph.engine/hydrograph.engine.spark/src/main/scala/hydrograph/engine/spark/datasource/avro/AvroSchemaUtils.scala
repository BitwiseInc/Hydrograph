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

import org.apache.avro.Schema

import scala.beans.BeanProperty
/**
  * The Object AvroSchemaUtils .
  *
  * @author Bitwise
  *
  */
object AvroSchemaUtils {

  object AvroTableProperties extends Enumeration {

    val SCHEMA_LITERAL = new AvroTableProperties("avro.schema.literal")

    val SCHEMA_URL = new AvroTableProperties("avro.schema.url")

    val SCHEMA_NAMESPACE = new AvroTableProperties("avro.schema.namespace")

    val SCHEMA_NAME = new AvroTableProperties("avro.schema.name")

    val SCHEMA_DOC = new AvroTableProperties("avro.schema.doc")

    val AVRO_SERDE_SCHEMA = new AvroTableProperties("avro.serde.schema")

    val SCHEMA_RETRIEVER = new AvroTableProperties("avro.schema.retriever")

    class AvroTableProperties(@BeanProperty val propName: String) extends Val

    implicit def convertValue(v: Value): AvroTableProperties = v.asInstanceOf[AvroTableProperties]
  }

  @Deprecated
  val SCHEMA_LITERAL = "avro.schema.literal"

  @Deprecated
  val SCHEMA_URL = "avro.schema.url"

  @Deprecated
  val SCHEMA_NAMESPACE = "avro.schema.namespace"

  @Deprecated
  val SCHEMA_NAME = "avro.schema.name"

  @Deprecated
  val SCHEMA_DOC = "avro.schema.doc"

  @Deprecated
  val AVRO_SERDE_SCHEMA = AvroTableProperties.AVRO_SERDE_SCHEMA.getPropName

  @Deprecated
  val SCHEMA_RETRIEVER = AvroTableProperties.SCHEMA_RETRIEVER.getPropName

  val SCHEMA_NONE = "none"

  val EXCEPTION_MESSAGE = "Neither " + AvroTableProperties.SCHEMA_LITERAL.getPropName + 
    " nor " + 
    AvroTableProperties.SCHEMA_URL.getPropName + 
    " specified, can't determine table schema"

  def getSchemaFor(str: String): Schema = {
    val parser = new Schema.Parser()
    parser.parse(str)
  }
}
