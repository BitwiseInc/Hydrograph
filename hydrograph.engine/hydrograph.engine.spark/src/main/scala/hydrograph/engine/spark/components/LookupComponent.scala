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

import hydrograph.engine.core.component.entity.LookupEntity
import hydrograph.engine.spark.components.base.OperationComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * The Class LookupComponent.
  *
  * @author Bitwise
  *
  */

class LookupComponent(lookupEntity: LookupEntity, componentsParams: BaseComponentParams) extends OperationComponentBase with Serializable {

  private val LOG: Logger = LoggerFactory.getLogger(classOf[LookupComponent])

  override def createComponent(): Map[String, DataFrame] = {
    try {
      val joinUtils = new JoinUtils(lookupEntity, componentsParams)
      val lookupOperations = joinUtils.prepareLookupOperation().map { joinOp => getJoinOpWithPrefixAdded(joinOp, joinOp.inSocketId) }

      val passthroughFields = joinUtils.getPassthroughFields()
      val mapFields = joinUtils.getMapFields()
      val copyOfInSocketFields = joinUtils.getCopyOfInSocketFields()
      val mergedFields = (passthroughFields ++ mapFields ++ copyOfInSocketFields).distinct
      val mergedInputs = mergedFields.map(f => f._1)
      val matchType = lookupEntity.getMatch

      def getDFWithRequiredFields(df: DataFrame): DataFrame = {
        df.select(mergedFields.map(field => col(field._1).as(field._2)): _*)
      }

      val driverInSocketId = lookupEntity.getInSocketList.asScala.filter { in => (in.getInSocketType == "driver") }(0).getInSocketId
      val lookupInSocketId = lookupEntity.getInSocketList.asScala.filter { in => (in.getInSocketType == "lookup") }(0).getInSocketId

      val driverJoinOp = lookupOperations.filter { j => (j.inSocketId == driverInSocketId) }(0)
      val lookupJoinOp = lookupOperations.filter { j => (j.inSocketId == lookupInSocketId) }(0)

      val driverDF = driverJoinOp.dataFrame.select((driverJoinOp.keyFields ++ driverJoinOp.dataFrame.columns.filter(p => mergedInputs.contains(p))).distinct.map(f => col(f)): _*)      
      val lookupDF = lookupJoinOp.dataFrame.select((lookupJoinOp.keyFields ++ lookupJoinOp.dataFrame.columns.filter(p => mergedInputs.contains(p))).distinct.map(f => col(f)): _*)      
      
      val broadcastDF = broadcast({
        if (matchType == "all")
          lookupDF
        else {
          val lookupKeyFields = lookupJoinOp.keyFields.map { str => col(str) }
          val lookupOtherFields = lookupDF.columns
            .filter { str => !lookupJoinOp.keyFields.contains(str) }
            .map { str => if (matchType == "first") (first(str).as(str)) else (last(str).as(str)) }

          if (lookupOtherFields.isEmpty)
            lookupDF.dropDuplicates()
          else
            lookupDF.groupBy(lookupKeyFields: _*).agg(lookupOtherFields.head, lookupOtherFields.tail: _*)
        }
      })
      
      val joinKey = createJoinKey(driverJoinOp.keyFields, lookupJoinOp.keyFields)
      val outputDF = getDFWithRequiredFields(driverDF.join(broadcastDF, joinKey, "leftouter"))

      val key = driverJoinOp.outSocketId

      LOG.debug("Created Lookup Component '" + lookupEntity.getComponentId + "' in Batch" + lookupEntity.getBatch)
      Map(key -> outputDF)
    }
    catch {
      case ex: RuntimeException =>
        LOG.error("Error in Lookup component '" + lookupEntity.getComponentId + "', Error", ex); throw ex
    }

  }


  /**
    * Creates key for Join
    *
    *@param  lhsKeys
    *              an array of String
    *
    *@param rhsKeys
    *             an array of String
    *
    *@return an array of Column
    *
  * */
  def createJoinKey(lhsKeys: Array[String], rhsKeys: Array[String]): Column = (lhsKeys, rhsKeys) match {
    case (l, r) if l.length != r.length => sys.error("key fields should be same")
    case (l, r) if r.tail.length == 0 => return col(l.head) <=> col(r.head)
    case (l, r) => return (col(l.head) <=> col(r.head)).&&(createJoinKey(l.tail, r.tail))
  }

  /**
  * Add the prefix with Join operations
  *
  * @param joinOp
  *               JoinOperation
  *
  * @param prefix
  *               String
  * @return JoinOperation
  * */
  def getJoinOpWithPrefixAdded(joinOp: JoinOperation, prefix: String): JoinOperation = {
    val originalDF = joinOp.dataFrame
    val modifiedDF = originalDF.select(originalDF.columns.map { c => col(c).as(prefix + "_" + c) }: _*)

    val originalKeys = joinOp.keyFields
    val modifiedKeys = originalKeys.map { colName => prefix + "_" + colName }

    JoinOperation(joinOp.compID, joinOp.inSocketId, modifiedDF, modifiedKeys, joinOp.unused, joinOp.recordRequired, joinOp.outSocketId, joinOp.unusedSocketId)
  }

}
