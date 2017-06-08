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

package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.RemoveDupsEntity
import hydrograph.engine.core.component.entity.elements.KeyField
import hydrograph.engine.core.constants.Keep
import hydrograph.engine.spark.components.base.StraightPullComponentBase
import hydrograph.engine.spark.components.platform.BaseComponentParams
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{Column, DataFrame, Row}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters.asScalaBufferConverter

/**
  * The Class RemoveDupsComponent.
  *
  * @author Bitwise
  *
  */
class RemoveDupsComponent(removeDupsEntity: RemoveDupsEntity, componentsParams: BaseComponentParams)
  extends StraightPullComponentBase {
  val logger = LoggerFactory.getLogger(classOf[RemoveDupsComponent])

  override def createComponent(): Map[String, DataFrame] = {

    try {
      logger.trace(removeDupsEntity.toString())
      var map: Map[String, DataFrame] = Map()

      val keep = removeDupsEntity.getKeep
      val isUnusedRequired = removeDupsEntity.getOutSocketList.asScala.filter(p => p.getSocketType.equals("unused")).size > 0
      val primaryKeys = if (removeDupsEntity.getKeyFields == null) (Array[KeyField]()) else (removeDupsEntity.getKeyFields)
      val keyFieldsIndexArray = determineKeyFieldPos
      val secondaryKeys = if (removeDupsEntity.getSecondaryKeyFields == null) (Array[KeyField]()) else (removeDupsEntity.getSecondaryKeyFields)
      val sourceDf = componentsParams.getDataFrame()
      val operationalSchema =  RowEncoder(componentsParams.getDataFrame().schema)
      val repartitionedDf = if (primaryKeys.isEmpty) (sourceDf.repartition(1)) else (sourceDf.repartition(primaryKeys.map { field => col(field.getName) }: _*))
      val sortedDf = repartitionedDf.sortWithinPartitions(populateSortKeys(primaryKeys ++ secondaryKeys): _*)
      val outDf = sortedDf.mapPartitions(itr => {
        def compare(row: Row, previousRow: Row): Boolean = {
      keyFieldsIndexArray.forall(i => ((row(i) == null && previousRow(i)==null) || (row(i)!=null && row(i).equals(previousRow(i))))
      )
        }
        var firstRowFlag: Boolean = true
        var previousRow: Row = null
        itr.flatMap { row => {
          val isPrevKeyDifferent: Boolean = {
            if (previousRow == null)
              (true)
            else (!compare(row, previousRow))
          }
          var flag = false

          if (keep == Keep.last) {
            flag = if (previousRow == null) false else isPrevKeyDifferent
          } else if (keep == Keep.first) {
            flag = if (previousRow == null) false else firstRowFlag
          }
          else {
            flag = if (previousRow == null) false else isPrevKeyDifferent && firstRowFlag
          }
          val tempRow = previousRow
          previousRow = row
          firstRowFlag = isPrevKeyDifferent
          if (itr.isEmpty) {
            if (flag == true) {
              if (keep == Keep.first && !firstRowFlag) {
                Iterator(tempRow)
              } else
                Iterator(tempRow, previousRow)
            } else if (keep == Keep.last) {
              Iterator(previousRow)
            } else if (firstRowFlag) {
              Iterator(previousRow)
            }
            else if (tempRow == null) {
              Iterator(previousRow)
            }
            else {
              Iterator()
            }
          } else if (flag == true) {
            Iterator(tempRow)
          } else {
            Iterator()
          }
        }
        }
      })(operationalSchema)

      val outKey = removeDupsEntity.getOutSocketList.asScala.filter(p => p.getSocketType.equals("out"))(0).getSocketId
      map += (outKey -> outDf)

      if (isUnusedRequired) {
        val unUsedDf = sortedDf.mapPartitions(itr => {
          def compare(row: Row, previousRow: Row): Boolean = {
      keyFieldsIndexArray.forall(i => ((row(i) == null && previousRow(i)==null) || (row(i)!=null && row(i).equals(previousRow(i))))
              )
          }
          var firstRowFlag: Boolean = true
          var previousRow: Row = null
          itr.flatMap { row => {
            val isPrevKeyDifferent: Boolean = {
              if (previousRow == null)
                (true)
              else (!compare(row, previousRow))
            }
            var flag = true
            if (keep == Keep.last) {
              flag = if (previousRow == null) true else isPrevKeyDifferent
            } else if (keep == Keep.first) {
              flag = if (previousRow == null) true else firstRowFlag
            }
            else {
              flag = if (previousRow == null) true else isPrevKeyDifferent && firstRowFlag
            }
            val tempRow = previousRow
            previousRow = row
            firstRowFlag = isPrevKeyDifferent
            if (itr.isEmpty) {

              if (!(keep == Keep.last) && flag == false) {
                if (firstRowFlag) {
                  Iterator(tempRow)
                } else {
                  Iterator(tempRow, previousRow)
                }
              } else if (keep == Keep.last && flag == false) {
                Iterator(tempRow)
              } else if (flag == true && keep == Keep.first && !firstRowFlag) {
                Iterator(previousRow)
              }
              else {
                Iterator()
              }
            } else if (flag == false) {
              Iterator(tempRow)
            } else {
              Iterator()
            }
          }
          }
        })(operationalSchema)

        val unusedKey = removeDupsEntity.getOutSocketList.asScala.filter(p => p.getSocketType.equals("unused"))(0).getSocketId
        map += (unusedKey -> unUsedDf)
      }

      map
    } catch {
      case e: RuntimeException => logger.error("Error in RemoveDups Component : " + removeDupsEntity.getComponentId() + "\n" , e); throw e
    }
  }


  def determineKeyFieldPos(): Array[Int] = {
    if (removeDupsEntity.getKeyFields == null) {
      Array[Int]()
    } else {
      removeDupsEntity.getKeyFields.map(keyfield => {
        componentsParams.getDataFrame().schema.fieldIndex(keyfield.getName)
      })
    }
  }


  def populateSortKeys(keysArray: Array[KeyField]): Array[Column] = {
    keysArray.map { field => if (field.getSortOrder.toLowerCase() == "desc") (col(field.getName).desc) else (col(field.getName)) }
  }
}

