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

import hydrograph.engine.core.component.entity.{InputFileXMLEntity, OutputFileXMLEntity}
import hydrograph.engine.core.component.entity.base.InputOutputEntityBase
import hydrograph.engine.core.component.entity.elements.SchemaField
import hydrograph.engine.spark.datasource.xml.util.{FieldContext, TreeNode, XMLTree}
import org.apache.spark.sql.Column
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * The Class SchemaCreator.
  *
  * @author Bitwise
  *
  */

case class SchemaCreator[T <: InputOutputEntityBase](inputOutputEntityBase: T) {


  private val LOG:Logger = LoggerFactory.getLogger(classOf[SchemaCreator[T]])

  /** Creates a List of StructField by recursively traversing TreeNode.
    *
    * @param rootNode
    *                 root node of XMLTree to traverse
    * @return
    *         List of StructField
    */
  def buildSchema(rootNode: TreeNode): List[StructField] = {

    def schema(currentNode: TreeNode, parent: String, structList: List[StructField]): List[StructField] = currentNode match {
      case x if x.children.isEmpty => {

        structList ++ List(StructField(x.fieldContext.name, x.fieldContext.datatype, x.fieldContext.isNullable,getMetadataWithProperty("dateFormat",x.fieldContext.format)))
      }
      case x => {
        List(StructField(x.fieldContext.name,StructType(x.children.flatMap(a => schema(a, a.fieldContext.name, List[StructField]())).toArray),x.fieldContext.isNullable,getMetadataWithProperty("dateFormat",x.fieldContext.format)))
      }
    }

    def getMetadataWithProperty(key: String,value: String):Metadata={
      new MetadataBuilder().putString(key,value).build()
    }

    rootNode.children.toList.flatMap(x => schema(x, rootNode.fieldContext.name, List[StructField]()))
  }

  def getRelativePath(absPath:String):String = inputOutputEntityBase match {
    case x if x.isInstanceOf[InputFileXMLEntity] => absPath.replace(inputOutputEntityBase.asInstanceOf[InputFileXMLEntity].getAbsoluteXPath,"")
    case x if x.isInstanceOf[OutputFileXMLEntity] => absPath.replace(inputOutputEntityBase.asInstanceOf[OutputFileXMLEntity].getAbsoluteXPath,"")
  }



  /** Returns a list of paired elements each containing a xpath and field name.
    * @return
    *         List[(String,String)]
    *         each pair of list contains xpath and respsective field to read
    */

  def extractXPathWithFieldName():List[(String,String)] = {

    def extract(schemaFieldList:List[SchemaField],relativeXPath:List[(String,String)]):List[(String,String)] ={
      if(schemaFieldList.isEmpty)relativeXPath
      else
        extract(schemaFieldList.tail, relativeXPath :+ (getRelativePath(schemaFieldList.head.getAbsoluteOrRelativeXPath),schemaFieldList.head.getFieldName) )

    }


    extract(inputOutputEntityBase.getFieldsList.asScala.toList,List[(String,String)]())
  }

  /**
    * Creates an Array of StructField by fetching SchemaField list from InputOutputEntityBase and returns the same.
    * @return
    *         Array of StructField
    */

  private def createStructFieldsForXMLInputOutputComponents(): Array[StructField] = {
    LOG.trace("In method createStructFieldsForXMLInputOutputComponents() which returns Array[StructField] for Input and Output components")
    val relativeXPathWithFieldName = extractXPathWithFieldName()
    val rowTag: String = inputOutputEntityBase match {
      case x if x.isInstanceOf[InputFileXMLEntity] => inputOutputEntityBase.asInstanceOf[InputFileXMLEntity].getRowTag
      case x if x.isInstanceOf[OutputFileXMLEntity] => inputOutputEntityBase.asInstanceOf[OutputFileXMLEntity].getRowTag
    }
    var fcMap:mutable.HashMap[String,FieldContext] = mutable.HashMap[String,FieldContext]()


    for (schemaField <- inputOutputEntityBase.getFieldsList) {
      fcMap += (schemaField.getFieldName -> FieldContext(schemaField.getFieldName, schemaField.getAbsoluteOrRelativeXPath,
        getDataType(schemaField), true, schemaField.getFieldFormat))
    }


    fcMap += (rowTag -> FieldContext(rowTag,rowTag, DataTypes.StringType, true,"yyyy-MM-dd")) // add context of rowTag to be used by schema fields to check XPaths

    val xmlTree: XMLTree = XMLTree(fcMap(rowTag))// add rowTag as root of tree to be used as parent of fields

    relativeXPathWithFieldName.foreach { xpathAndFieldPair=>{// iterating on list of field and its XPath's pair
      if (!xpathAndFieldPair._1.contains('/'))
        xmlTree.addChild(rowTag, FieldContext(xpathAndFieldPair._1, rowTag + "/" + xpathAndFieldPair._1, fcMap(xpathAndFieldPair._2).datatype, true, fcMap(xpathAndFieldPair._2).format))// add field as child of root if its XPath doesnt contains "/"
      else  {
        var parentTag = rowTag
        var xpath = rowTag + "/"
        xpathAndFieldPair._1.split("/").foreach(currentField => {
          xpath = xpath + currentField
          if (!xmlTree.isPresent(currentField, xpath)) {//check if tree contains field on given XPath

            xmlTree.addChild(parentTag, FieldContext(currentField, xpath, fcMap(xpathAndFieldPair._2).datatype, true, fcMap(xpathAndFieldPair._2).format))
          }
          parentTag = currentField // make currentField parent of next field as its a nested schema
          xpath += "/"
        })
      }
    }}

    val structFields = buildSchema(xmlTree.rootNode).toArray
    LOG.debug("Array of StructField created for XML Component: "+inputOutputEntityBase.getComponentId+" from schema is : " + structFields.mkString)
    structFields
  }

  def makeSchema(): StructType = inputOutputEntityBase match {
    case x if x.isInstanceOf[InputFileXMLEntity] ||  x.isInstanceOf[OutputFileXMLEntity] => StructType(createStructFieldsForXMLInputOutputComponents())
    case x if !x.isInstanceOf[InputFileXMLEntity] &&  !x.isInstanceOf[OutputFileXMLEntity]  => StructType(createStructFields())
  }

  def getTypeNameFromDataType(dataType: String): String = {
    Class.forName(dataType).getSimpleName
  }

  def getDataType(schemaField: SchemaField): DataType = {
    getTypeNameFromDataType(schemaField.getFieldDataType) match {
      case "Integer" => DataTypes.IntegerType
      case "String" => DataTypes.StringType
      case "Long" => DataTypes.LongType
      case "Short" => DataTypes.ShortType
      case "Boolean" => DataTypes.BooleanType
      case "Float" => DataTypes.FloatType
      case "Double" => DataTypes.DoubleType
      case "Date" if schemaField.getFieldFormat.matches(".*[H|m|s|S].*") => DataTypes.TimestampType
      case "Date" => DataTypes.DateType
      case "BigDecimal" => DataTypes.createDecimalType(checkPrecision(schemaField.getFieldPrecision),schemaField.getFieldScale)
    }
  }



  def checkPrecision(precision:Int):Int={
    if(precision== -999) 38 else precision
  }

  private def createStructFields(): Array[StructField] = {
    LOG.trace("In method createStructFields() which returns Array[StructField] for Input and Output components")
    val structFields = new Array[StructField](inputOutputEntityBase.getFieldsList.size)

    for (i <- 0 until inputOutputEntityBase.getFieldsList.size()) {
      val schemaField: SchemaField = inputOutputEntityBase.getFieldsList.get(i)
      structFields(i) = StructField(schemaField.getFieldName, getDataType(schemaField), true)
    }
    LOG.debug("Array of StructField created from schema is : " + structFields.mkString)
    structFields
  }



  def createSchema(): Array[Column] ={
    LOG.trace("In method createSchema()")
    val schema=inputOutputEntityBase.getFieldsList.map(sf=>col(sf.getFieldName)).toArray
    LOG.debug("Schema created : " + schema.mkString )
    schema
  }

  def getDateFormats(): String = {
    LOG.trace("In method getDateFormats() which returns \\t separated date formats for Date fields")
    var dateFormats: String = ""
    val fields = inputOutputEntityBase.getFieldsList
    for (i <- 0 until fields.size()) {
      if (fields.get(i).getFieldDataType.equals("java.util.Date")){
        dateFormats += fields.get(i).getFieldFormat + "\t"
      }else {
        dateFormats += "null" + "\t"
      }

    }
    LOG.debug("Date Formats for Date fields : " + dateFormats)
    dateFormats
  }

}

