/*
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
 */
package hydrograph.engine.spark.components

import hydrograph.engine.core.component.entity.OutputFileXMLEntity
import hydrograph.engine.spark.components.base.SparkFlow
import hydrograph.engine.spark.components.platform.BaseComponentParams
import hydrograph.engine.spark.components.utils.SchemaCreator
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, SaveMode}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/**
  * The Class OutputFileXMLComponent.
  *
  * @author Bitwise
  *
  */

case class Node(element: String, parent: String, level: Int) {
  var columnExpr: Column = null
}

class OutputFileXMLComponent(outputFileXMLEntity: OutputFileXMLEntity, cp:
BaseComponentParams) extends SparkFlow with Serializable {

  private val LOG: Logger = LoggerFactory.getLogger(classOf[OutputFileXMLEntity])

  override def execute() = {
    LOG.trace("In method execute()")
    val schemaCreator = SchemaCreator(outputFileXMLEntity)
    try {

      val nestedColumns = getNestedColumns(getXpathsWithFieldNames())

      cp.getDataFrame().select(nestedColumns: _*).write
        .option("componentId", outputFileXMLEntity.getComponentId)
        .option("charset", outputFileXMLEntity.getCharset)
        .option("rowTag", outputFileXMLEntity.getRowTag)
        .option("rootTag", outputFileXMLEntity.getRootTag)
        .option("nullValue", "")
        .mode(if (outputFileXMLEntity.isOverWrite) SaveMode.Overwrite else SaveMode.ErrorIfExists)
        .option("dateFormats", schemaCreator.getDateFormats)
        .format("hydrograph.engine.spark.datasource.xml")
        .save(outputFileXMLEntity.getPath)
    } catch {
      case e: Exception =>
        LOG.error("Error in Output File XML Component " + outputFileXMLEntity.getComponentId, e)
        throw new RuntimeException("Error in Output File XML Component " + outputFileXMLEntity.getComponentId, e)
    }
    LOG.info("Created Output File XML Component " + outputFileXMLEntity.getComponentId
      + " in Batch " + outputFileXMLEntity.getBatch + " with path " + outputFileXMLEntity.getPath)
    LOG.debug("Component Id: '" + outputFileXMLEntity.getComponentId
      + "' in Batch: " + outputFileXMLEntity.getBatch
      + " having schema: [ " + outputFileXMLEntity.getFieldsList.asScala.mkString(",")
      + " ] with strict as " + outputFileXMLEntity.isStrict + " safe as " + outputFileXMLEntity.isSafe
      + " at Path: " + outputFileXMLEntity.getPath)
  }

  private def getXpathsWithFieldNames(): List[String] = {
    val xpathList = new ListBuffer[String]
    outputFileXMLEntity.getFieldsList.asScala.foreach({
      field => {
        xpathList += getRelativePath(field.getAbsoluteOrRelativeXPath) + "/" + field.getFieldName
      }
    })
    xpathList.toList
  }

  private def getRelativePath(absPath: String): String = absPath.replace(outputFileXMLEntity.getAbsoluteXPath + "/", "")

  private def getNestedColumns(xpathList: List[String]): List[Column] = {

    val nodeList = new ListBuffer[Node];

    xpathList.foreach(field => {
      val xpathArray = field.split("\\/")
      var tempParent: String = ""
      xpathArray.zipWithIndex.foreach(element => {
        nodeList += Node(element._1, tempParent, element._2)
        tempParent += "/" + element._1
      })
    })

    val maxLevel: Int = nodeList.map(f => f.level).toList.max

    for (l <- maxLevel to 1 by -1) {

      val filteredNodesAtLevel = nodeList.filter(n => n.level == l)

      val structsList = new ListBuffer[(Any, String)]

      filteredNodesAtLevel.foreach(n => {
        if (n.columnExpr == null) {
          structsList += ((n.element, n.parent))
        }
        else {
          structsList += ((n.columnExpr, n.parent))
        }
      })

      val filteredNodesAtLevelMinusOne = nodeList.filter(n => n.level == (l - 1))

      filteredNodesAtLevelMinusOne.foreach(n => {
        val filteredStructs = structsList.filter(p => p._2 == n.parent + "/" + n.element)

        if (filteredStructs.nonEmpty) {

          val tempStruct = {
            if (filteredStructs.length == 1) {
              val tempStruct = filteredStructs.map(f => f._1).head
              tempStruct match {
                case str: String => col(str).as(n.element)
                case _ => struct(tempStruct.asInstanceOf[Column]).as(n.element)
              }
            } else {
              val columnsList = filteredStructs.map(f => {
                f._1 match {
                  case str: String => col(str)
                  case _ => f._1.asInstanceOf[Column]
                }
              })

              val uniqueColumnsList = new ListBuffer[Column]
              columnsList.foreach(c => {

                val tempList = uniqueColumnsList.map(c => c.toString())

                if (!tempList.contains(c.toString()))
                  uniqueColumnsList += c
              })

              struct(uniqueColumnsList: _*).as(n.element)

            }
          }

          for (node <- nodeList) {
            if (node.element == n.element && node.parent == n.parent) {
              node.columnExpr = tempStruct
            }
          }
        }
      })
    }

    val colList = nodeList.filter(n => n.level == 0).map(x => x.columnExpr)
    val uniqueColList = new ListBuffer[Column]

    colList.foreach(c => {
      val tempList = uniqueColList.map(c => c.toString())

      if (!tempList.contains(c.toString()))
        uniqueColList += c
    })

    uniqueColList.toList
  }

}