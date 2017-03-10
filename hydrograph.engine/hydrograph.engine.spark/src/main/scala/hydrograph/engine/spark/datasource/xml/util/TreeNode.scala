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
package hydrograph.engine.spark.datasource.xml.util

import org.apache.spark.sql.types.DataType

import scala.collection.mutable.ListBuffer

/**
  * The Class FieldContext.
  *
  * @author Bitwise
  *
  */
case class FieldContext(name: String, xPath: String, datatype: DataType, isNullable: Boolean, format: String) {
}

case class TreeNode(fieldContext: FieldContext) {
  var children: ListBuffer[TreeNode] = ListBuffer()

  def addChild(data: FieldContext): Unit = children match {
    case x if x == ListBuffer => x.append(TreeNode(data))
    case y => y.append(TreeNode(data))
  }
}

case class XMLTree(fc: FieldContext) {
  val rootNode = TreeNode(fc)

  /** Adds a child FieldContext to designated parent node in XMLTree by
    * checking if the respective xpaths satisfy the parent-child relationship.
    *
    * @param parent
    * parent node.
    * @param child
    * child node to be added into tree.
    */
  def addChild(parent: String, child: FieldContext): Unit = {
    def findAndAdd(node: TreeNode): Unit = node match {

      case currentNode if currentNode.fieldContext.name.equals(parent) && checkXpaths(currentNode.fieldContext, child) => currentNode.addChild(child)
      case currentNode => currentNode.children.foreach(a => findAndAdd(a))
    }

    def checkXpaths(parent: FieldContext, child: FieldContext): Boolean = {

      parent.xPath.equals(child.xPath.substring(0, child.xPath.lastIndexOf("/")))
    }

    findAndAdd(rootNode)
  }

  /** Checks for the presence of given prospect node at specified xpath in XMLTree.
    *
    * @param prospectNodeName
    * prospect node
    * @param prospectNodeXPath
    * xpath of prospect node
    * @return
    * true if prospect node exists false otherwise
    */
  def isPresent(prospectNodeName: String, prospectNodeXPath: String): Boolean = {
    var present: Boolean = false

    def find(treeNode: TreeNode): Unit = treeNode match {
      case x if x.fieldContext.name.equals(prospectNodeName) && x.fieldContext.xPath.equals(prospectNodeXPath) => present = true
      case x => x.children.foreach(a => find(a))
    }


    find(rootNode)
    present
  }
}

