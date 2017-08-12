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
import hydrograph.engine.spark.components.utils.{ReusableRowHelper, RowHelper}
import hydrograph.engine.transformation.userfunctions.base.FilterBase
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
/**
  * The Class FilterComponentWithMapPartitions.
  *
  * @author Bitwise
  *
  */
class FilterComponentWithMapPartitions(filterEntity: FilterEntity, componentsParams: BaseComponentParams) extends
  OperationComponentBase with OperationHelper[FilterBase] with Serializable {
  val LOG = LoggerFactory.getLogger(classOf[FilterComponent])

  override def createComponent(): Map[String, DataFrame] = {

    LOG.info("Filter Component Called with input Schema [in the form of(Column_name,DataType,IsNullable)]: {}", componentsParams.getDataFrame().schema)
    val inputSchema: StructType = componentsParams.getDataFrame().schema
    val outputSchema = inputSchema
    val operationalSchema =  RowEncoder(componentsParams.getDataFrame().schema)

    var map: Map[String, DataFrame] = Map()

    val filterSparkOperations = initializeOperationList[FilterForExpression](filterEntity.getOperationsList,
      inputSchema, outputSchema).head
    val filterClass = filterSparkOperations.baseClassInstance

    val opProps = filterSparkOperations.operationEntity.getOperationProperties

    LOG.info("Operation Properties: " + opProps)
    if (opProps != null) FilterBase.properties.putAll(opProps)



    filterEntity.getOutSocketList.asScala.foreach { outSocket =>
      LOG.info("Creating filter Component for '" + filterEntity.getComponentId + "' for socket: '"
        + outSocket.getSocketId + "' of type: '" + outSocket.getSocketType + "'")

      val df = componentsParams.getDataFrame.mapPartitions(itr => {

        filterClass match {
          case expression: FilterForExpression => expression.setValidationAPI(filterSparkOperations.validatioinAPI)
            try {
              expression.callPrepare(filterSparkOperations.fieldName, filterSparkOperations.fieldType)
            } catch {
              case e: Exception =>
                LOG.error("Exception in callPrepare method of: " + expression.getClass + " and message is " + e.getMessage, e)
                throw new InitializationException("Exception in initialization of: " + expression.getClass + " and message is " + e.getMessage, e)
            }
          case f: FilterBase => f.prepare(filterSparkOperations.operationEntity.getOperationProperties, filterSparkOperations.operationEntity.getOperationInputFields)
          case _ =>
        }

        itr.filter( row =>{


          if (outSocket.getSocketType.equalsIgnoreCase("out")) {
            !filterClass.isRemove(filterSparkOperations.inputRow.setRow(row))
          }
          else {
            filterClass.isRemove(filterSparkOperations.inputRow.setRow(row))
          }

        })

      })(operationalSchema)

      map += (outSocket.getSocketId -> df)

    }
    map
  }
}
