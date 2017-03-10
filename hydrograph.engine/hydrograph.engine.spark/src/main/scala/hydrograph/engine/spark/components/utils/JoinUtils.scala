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
package hydrograph.engine.spark.components.utils

import hydrograph.engine.core.component.entity.base.OperationEntityBase
import hydrograph.engine.core.component.entity.elements.PassThroughField
import hydrograph.engine.core.component.entity.{JoinEntity, LookupEntity}
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.sql.DataFrame

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/**
  * The Class JoinOperation.
  *
  * @author Bitwise
  *
  */

case class JoinOperation(compID: String, inSocketId: String, dataFrame: DataFrame, keyFields: Array[String], unused: Boolean, recordRequired: Boolean, outSocketId: String, unusedSocketId: String)

class JoinUtils(operationEntity: OperationEntityBase, baseComponentParams: BaseComponentParams) {

  def prepareJoinOperation(): Array[JoinOperation] = {

    val joinHelperArray = new Array[JoinOperation](operationEntity.getInSocketList.size())

    operationEntity.getInSocketList().asScala.zipWithIndex.foreach {
      case (in, i) =>
        val unusedSocket = operationEntity.getOutSocketList.asScala.filter(out => out.getSocketType.equals("unused") && out
          .getCopyOfInSocketId
          .equals(in.getInSocketId))

        val unusedSocketId = if (unusedSocket.size > 0) unusedSocket(0).getSocketId else ""

        val keyFields = {
            operationEntity.asInstanceOf[JoinEntity].getKeyFields.asScala.filter(key => key.getInSocketId.equals(in.getInSocketId))(0)
        }

        val outSocket = operationEntity.getOutSocketList.asScala.filter(out => out.getSocketType.equals("out"))

        val outSocketId = if (outSocket.size > 0) outSocket(0).getSocketId else ""

        val dataFrame = baseComponentParams.getDataFrameMap().getOrElse(in.getFromComponentId, sys.error("input data " +
          "frame should be present"))

        joinHelperArray(i) = JoinOperation(in.getFromComponentId, in.getInSocketId, dataFrame, keyFields.getFields, unusedSocket.size > 0, keyFields
          .isRecordRequired, outSocketId, unusedSocketId)

    }
    joinHelperArray
  }

  def prepareLookupOperation(): Array[JoinOperation] = {

    val joinHelperArray = new Array[JoinOperation](operationEntity.getInSocketList.size())

    operationEntity.getInSocketList().asScala.zipWithIndex.foreach {
      case (in, i) =>
        val unusedSocket = operationEntity.getOutSocketList.asScala.filter(out => out.getSocketType.equals("unused") && out
          .getCopyOfInSocketId
          .equals(in.getInSocketId))

        val unusedSocketId = if (unusedSocket.size > 0) unusedSocket(0).getSocketId else ""

        val keyFields =
            operationEntity.asInstanceOf[LookupEntity].getKeyFields.asScala.filter(key => key.getInSocketId.equals(in.getInSocketId))(0)

        val outSocket = operationEntity.getOutSocketList.asScala.filter(out => out.getSocketType.equals("out"))

        val outSocketId = if (outSocket.size > 0) outSocket(0).getSocketId else ""

        val dataFrame = baseComponentParams.getDataFrameMap().getOrElse(in.getFromComponentId, sys.error("input data " +
          "frame should be present"))

        joinHelperArray(i) = JoinOperation(in.getFromComponentId, in.getInSocketId, dataFrame, keyFields.getFields, unusedSocket.size > 0, keyFields
          .isRecordRequired, outSocketId, unusedSocketId)

    }
    joinHelperArray
  }

  def getCopyOfInSocketFields(): List[(String, String)] = {
    val copyOfInSocketFields = ListBuffer[(String, String)]()
    val outSocket = operationEntity.getOutSocketList.asScala.filter(out => out.getSocketType.equals("out"))(0)
    val copyOfInSocketId = outSocket.getCopyOfInSocketId
    if (copyOfInSocketId != null) {
      val inSocket = operationEntity.getInSocketList.filter { soc => soc.getInSocketId.equals(copyOfInSocketId) }(0)
      val fromCompId = inSocket.getFromComponentId
      val tempMap = baseComponentParams.getSchemaFieldMap()
      val fieldsList = tempMap(fromCompId)
      fieldsList.foreach { f => copyOfInSocketFields += ((inSocket.getInSocketId + "_" + f.getFieldName, f.getFieldName)) }
    }
    copyOfInSocketFields.toList
  }

  def getPassthroughFields(): List[(String, String)] = {
    val passthroughFields = List[(String, String)]()
    val outSocket = operationEntity.getOutSocketList.asScala.filter(out => out.getSocketType.equals("out"))(0)
    val inSocket = operationEntity.getInSocketList

    def populatePassthroughFields(outList: List[(String, String)], passthroughFieldsList: java.util.List[hydrograph.engine.core.component.entity.elements.PassThroughField]): List[(String, String)] = {
      if (passthroughFieldsList.isEmpty()) outList
      else if (passthroughFieldsList.head.getName.equals("*")) {
        val fromCompId = inSocket.filter { soc => soc.getInSocketId.equals(passthroughFieldsList.head.getInSocketId) }(0).getFromComponentId
        val tempMap = baseComponentParams.getSchemaFieldMap()

        val expandedList = tempMap(fromCompId).map(f => new PassThroughField(f.getFieldName, passthroughFieldsList.head.getInSocketId))

        populatePassthroughFields(outList, expandedList.toList ++ passthroughFieldsList.tail)
      } else populatePassthroughFields(outList :+ (passthroughFieldsList.head.getInSocketId + "_" + passthroughFieldsList.head.getName, passthroughFieldsList.head.getName), passthroughFieldsList.tail)
    }
    populatePassthroughFields(passthroughFields, outSocket.getPassThroughFieldsList).distinct
  }

  def getMapFields(): List[(String, String)] = {
    val mapFields = List[(String, String)]()
    val outSocket = operationEntity.getOutSocketList.asScala.filter(out => out.getSocketType.equals("out"))(0)

    def populateMapFields(outList: List[(String, String)], mapFieldsList: java.util.List[hydrograph.engine.core.component.entity.elements.MapField]): List[(String, String)] = {
      if (mapFieldsList.isEmpty()) outList
      else populateMapFields(outList :+ (mapFieldsList.head.getInSocketId + "_" + mapFieldsList.head.getSourceName, mapFieldsList.head.getName), mapFieldsList.tail)
    }

    populateMapFields(mapFields, outSocket.getMapFieldsList).distinct
  }

}
