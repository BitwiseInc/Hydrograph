/** *****************************************************************************
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
  * ******************************************************************************/
package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.FilterEntity
import hydrograph.engine.expression.userfunctions.FilterForExpression
import hydrograph.engine.spark.components.base.OperationComponentBase
import hydrograph.engine.spark.components.handler.OperationHelper
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.EncoderHelper
import hydrograph.engine.transformation.userfunctions.base.FilterBase
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
  * The Class FilterComponentWithUDF.
  *
  * @author Bitwise
  *
  */
class FilterComponentWithUDF(filterEntity: FilterEntity, componentsParams: BaseComponentParams) extends
  OperationComponentBase with OperationHelper[FilterBase] with Serializable {
  val LOG = LoggerFactory.getLogger(classOf[FilterComponentWithUDF])

  override def createComponent(): Map[String, DataFrame] = {

    LOG.info("Filter Component Called with input Schema [in the form of(Column_name,DataType,IsNullable)]: {}", componentsParams.getDataFrame().schema)
    val inputSchema: StructType = componentsParams.getDataFrame().schema
    val operationSchema: StructType = EncoderHelper().getEncoder(filterEntity.getOperation.getOperationInputFields.toList, componentsParams.getSchemaFields())
    val outputSchema = inputSchema
    val filterDF = componentsParams.getDataFrame()
    val sparkSession = componentsParams.getSparkSession()

    var map: Map[String, DataFrame] = Map()
    val filterSparkOperations = initializeOperationList[FilterForExpression](filterEntity.getOperationsList,
      operationSchema, outputSchema).head
    val filterClass = filterSparkOperations.baseClassInstance


    try {
      filterClass match {
        case expression: FilterForExpression => expression.setValidationAPI(filterSparkOperations.validatioinAPI)
          expression.callPrepare(filterSparkOperations.fieldName, filterSparkOperations.fieldType)
        case f: FilterBase => f.prepare(filterSparkOperations.operationEntity.getOperationProperties, filterSparkOperations.operationEntity.getOperationInputFields)
        case _ =>
      }
    } catch {
      case e: Exception =>
        LOG.error("Error in callPrepare method of: " + filterClass.getClass + " ", e)
        throw new InitializationException("Exception in field initialization of: " + filterClass.getClass + " ", e)

    }
    val opProps = filterSparkOperations.operationEntity.getOperationProperties

    LOG.info("Operation Properties: " + opProps)
    if (opProps != null) FilterBase.properties.putAll(opProps)

    def FilterUDF(cols: Row): Boolean = {
      try {
        filterClass.isRemove(filterSparkOperations.inputRow.setRow(cols))
      } catch {
        case e: Exception =>
          LOG.error("Error in isRemove method of: " + filterClass.getClass + " ", e)
          throw new InitializationException("Exception in Filter Component:[\"" + filterEntity.getComponentId + "\"]" + filterClass.getClass, e)
      }
    }

    val UDFName = filterEntity.getComponentId + "UDF"
    sparkSession.udf.register(UDFName, FilterUDF(_: Row))

    val operationInFields = filterEntity.getOperation.getOperationInputFields.toList.mkString(",")
    val filterQuery: String = UDFName + "(struct(" + operationInFields + "))"


    filterEntity.getOutSocketList.asScala.foreach { outSocket =>
      val outSocketID = outSocket.getSocketId
      LOG.info("Creating filter Component for '" + filterEntity.getComponentId + "' for socket: '"
        + outSocketID + "' of type: '" + outSocket.getSocketType + "'")

      if (outSocket.getSocketType.equalsIgnoreCase("out")) {
        val outDF = filterDF.filter("!" + filterQuery)
        map += (outSocketID -> outDF)
      }
      else {
        val unusedDF = filterDF.filter(filterQuery)
        map += (outSocketID -> unusedDF)
      }
    }
    map
  }
}
