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
  ******************************************************************************/

package hydrograph.engine.spark.components.utils.excel

import java.io.OutputStream
import java.text.{DateFormat, SimpleDateFormat}
import java.util

import hydrograph.engine.core.component.entity.OutputFileExcelEntity
import hydrograph.engine.core.component.entity.elements.{FieldFormat, KeyField}
import hydrograph.engine.core.component.entity.elements.FieldFormat.Property
import hydrograph.engine.spark.components.utils.SchemaCreator
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.poi.hssf.usermodel.{HSSFPalette, HSSFWorkbook}
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel._
import org.apache.poi.xssf.streaming.{SXSSFSheet, SXSSFWorkbook}
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * The Class ExcelOutputUtil.
  *
  * @author Bitwise
  *
  */
case class KeyInfo(key:KeyField, index:Int, dataType:String)
class ExcelOutputUtil(frame: DataFrame, outputFileExcelEntity: OutputFileExcelEntity) extends Serializable {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[ExcelOutputUtil])
  val path: String = outputFileExcelEntity.getPath
  val hdpPath: Path = new Path(path)
  val fs: FileSystem = FileSystem.get(new Configuration)
  val fileExtension: String = path.substring(path.lastIndexOf(".") + 1, path.length)
  val fontDataStringSize = 21
  val schemaCreator = SchemaCreator(outputFileExcelEntity)
  val dateFormats=schemaCreator.getDateFormats().split("\t")

  var outputStream : OutputStream = outputFileExcelEntity.getWriteMode match {
    case "Overwrite" => {
      fs.setWriteChecksum(false)
      fs.create(hdpPath, true)
    }
    case "Append" => null
    case "FailIfFileExists" => {
      fs.setWriteChecksum(false)
      fs.create(hdpPath, false)
    }
  }

  val dataSchema: StructType = frame.schema
  var workbook: Workbook =
    if (outputFileExcelEntity.getWriteMode.equals("Append")) {
      fs.setWriteChecksum(false)
      if ("XLSX".equalsIgnoreCase(fileExtension)) new SXSSFWorkbook(new XSSFWorkbook(fs.open(hdpPath))) else new HSSFWorkbook(fs.open(hdpPath))

    }else
    if ("XLSX".equalsIgnoreCase(fileExtension)) new SXSSFWorkbook else new HSSFWorkbook

  val set: util.HashSet[String] = new util.HashSet[String]()
  private val valueDataTypes: Array[DataType] = dataSchema.map(_.dataType).toArray
  private val cellTypes: Array[CellType] = dataSchema.map(_.dataType).map(getCellType).toArray
  private val headerCellFormat: java.util.List[FieldFormat] = outputFileExcelEntity.getHeaderFormats
  private val dataCellFormat: java.util.List[FieldFormat] = outputFileExcelEntity.getDataFormats
  private val worksheetName: String = outputFileExcelEntity.getWorksheetName
  private val dFCollect:Array[Row] = frame.collect()
  
  private val headerCellFontStyle: Map[String, Font] = getCellFontStyle(headerCellFormat)
  private val dataCellFontStyle: Map[String, Font] = getCellFontStyle(dataCellFormat)
  private val headerCellStyleFormat: Map[String, CellStyle] = updateCellBorderAlignmentStyle(getCellStyle(dataSchema),headerCellFormat)
  private val dataCellStyleFormat: Map[String,CellStyle] = updateCellBorderAlignmentStyle(getCellStyle(dataSchema),dataCellFormat)

  val keyList = outputFileExcelEntity.getKeyField.asScala.toArray
  val k: java.util.List[Int]=new util.ArrayList[Int]()
  val k1: java.util.List[String] =new util.ArrayList[String]()

  for (e <- keyList) {
    var i = -1
    frame.schema.foreach(schema => {
      i = i + 1
      if (schema.name.equals(e.getName)) {
        k.add(i)
        k1.add(schema.dataType.typeName)
      }
    })
  }

  val keyIndex = k.asScala.toArray
  val keySchema = k1.asScala.toArray


  val keyInfo = keyList.zip(keyIndex).zip(keySchema).map(f => KeyInfo(f._1._1, f._1._2, f._2))
  val sortedDF = sortArray(dFCollect, keyInfo)

  def sortArray(inArray: Array[Row], keyInfo: Array[KeyInfo]): Array[Row] = {

    def comparisonFunction(r1: Row, r2: Row): Boolean = {

      def compare(k: KeyInfo): Int = k.dataType match {
        case "integer" => r1.getInt(k.index) compare r2.getInt(k.index)
        case "string" => r1.getString(k.index) compare r2.getString(k.index)
        case "double" => r1.getDouble(k.index) compare r2.getDouble(k.index)
        case "float" => r1.getFloat(k.index) compare r2.getFloat(k.index)
        case "short" => r1.getShort(k.index) compare r2.getShort(k.index)
        case "boolean" => r1.getBoolean(k.index) compare r2.getBoolean(k.index)
        case "long" => r1.getLong(k.index) compare r2.getLong(k.index)
        case "date" => r1.getDate(k.index) compareTo r2.getDate(k.index)
        case "decimal" =>  r1.getDecimal(k.index) compareTo r2.getDecimal(k.index)
        case "timestamp" => r1.getTimestamp(k.index) compareTo  r2.getTimestamp(k.index)
      }

      for (k <- keyInfo) {
        val temp = if (k.key.getSortOrder == "asc") compare(k) else -1 * compare(k)
        if (temp < 0)
          return true
        else if (temp > 0)
          return false
      }
      true
    }
    inArray.sortWith((r1, r2) => comparisonFunction(r1, r2))
  }

  private val sheet: util.Map[String, Sheet] = new util.HashMap[String, Sheet]()

  val strippedLeadingQuoteDF=sortedDF.map(row=> Row.fromSeq(row.toSeq.toList.map(r=> if(r.isInstanceOf[String] && outputFileExcelEntity.isStripLeadingQuote)r.toString.replaceAll("^\'","") else r)))

  var columnIdex: Int = 0
  if (outputFileExcelEntity.isColumnAsWorksheetName) {
    columnIdex = getColumnIndex(dataSchema.fieldNames, worksheetName)
    strippedLeadingQuoteDF.foreach(element => {
      set.add(element.get(columnIdex).toString)
    })
  } else sheet.put(worksheetName, workbook.createSheet(worksheetName))

  set.asScala.toList.foreach(book => sheet.put(book.toString, workbook.createSheet(book.toString)))

  def getColumnIndex(fieldNames: Array[String], s: String) = {
    var i = -1
    var returnVal = 0
    fieldNames.foreach(e => {
      i = i + 1
      if (e.equals(s)) returnVal = i
    })
    returnVal
  }


  def getCellFontStyle(userFormat: java.util.List[FieldFormat]): Map[String, Font] = {
    var userFontFormatMap: scala.collection.mutable.Map[String, Font] = scala.collection.mutable.Map[String, Font]()
    for (i <- 0 until userFormat.size()) {
      val format: FieldFormat = userFormat.get(i)
      if (format.getName() != null && format.getProperty != null) {
        var j = 0
        var fontStyle: Font = workbook.createFont()
        for (j <- 0 until format.getProperty.size()) {
          val formatProperty: Property = format.getProperty.get(j)
          (formatProperty.getType.toUpperCase(), formatProperty.getName.toUpperCase) match {
            case ("FONT", "SIZE") => fontStyle.setFontHeightInPoints(formatProperty.getValue.toShort)
            case ("FONT", "NAME") => fontStyle.setFontName(formatProperty.getValue)
            case ("FONT", "ITALIC") => fontStyle.setItalic("true".equalsIgnoreCase(formatProperty.getValue))
            case ("FONT", "STRIKEOUT") => fontStyle.setStrikeout("true".equalsIgnoreCase(formatProperty.getValue))
            case ("FONT", "BOLD") => fontStyle.setBold("true".equalsIgnoreCase(formatProperty.getValue))
            case ("FONT", "UNDERLINE") => fontStyle.setUnderline(if ("true".equalsIgnoreCase(formatProperty.getValue)) 1 else 0)
            case ("FONT", "FONT") => fontStyle = getCellFontStyle(formatProperty.getValue)
            case (_, _) =>
          }
        }
        userFontFormatMap += (format.getName.toLowerCase -> fontStyle)
      }
    }
    userFontFormatMap.toMap
  }

  def getCellFontStyle(fontData: String): Font = {
    val font: Font = workbook.createFont()
    val fontDataSplit = fontData.split("\\|")
    if(fontDataSplit.size == fontDataStringSize) {
      font.setFontName(fontDataSplit(1))
      font.setFontHeightInPoints(fontDataSplit(2).toFloat.toShort)
      val fontStyle = fontDataSplit(3).toShort
      fontStyle match {
        case 1 => font.setBold(true)
        case 2 => font.setItalic(true)
        case 3 => {
          font.setBold (true)
          font.setItalic(true)
        }
        case _ =>
      }
      if(fontDataSplit(12).toShort == 1) font.setUnderline(1)
      if(fontDataSplit(13).toShort == 1) font.setStrikeout(true)
      val fcolor = fontDataSplit(20)
      val ucolor: java.awt.Color = java.awt.Color.decode(fcolor)
      val tempHSSFWorkBook: HSSFWorkbook = new HSSFWorkbook()
      val tempHSSFPalette : HSSFPalette = tempHSSFWorkBook.getCustomPalette
      val tempHSSFcolor: HSSFColor = tempHSSFPalette.findSimilarColor(ucolor.getRed,ucolor.getGreen,ucolor.getBlue)
      font.setColor(tempHSSFcolor.getIndex)
    }
    else
      LOG.error("Data font string is not of expected size, fontData :"+fontData)
    font
  }

  def writeDF(): Unit = {
    val headerRow = Row(frame.schema.fieldNames)
    sheet.keySet().asScala.toList.foreach(e => write(sheet.get(e), headerRow))
    if (outputFileExcelEntity.isColumnAsWorksheetName)
      strippedLeadingQuoteDF.foreach(row => write(sheet.get(row.get(columnIdex)), row))
    else
      strippedLeadingQuoteDF.foreach(row => write(sheet.get(worksheetName), row))
    if (outputFileExcelEntity.isAutoColumnSize) {
      sheet.keySet().asScala.toList.foreach(e => setAutoSizeColumn(sheet.get(e)))
    }
    if(outputStream == null){
      fs.close()
      outputStream = fs.create(hdpPath)
    }
    workbook.write(outputStream)
    workbook.close()
    fs.setWriteChecksum(false)
    if (outputStream != null) outputStream.close()
  }


  def write(sheet: Sheet, rowValue: Row): Unit = {

    if ("XLSX".equalsIgnoreCase(fileExtension) && outputFileExcelEntity.isAutoColumnSize && !(sheet.asInstanceOf[SXSSFSheet].isColumnTrackedForAutoSizing(0))) {
      sheet.asInstanceOf[SXSSFSheet].trackAllColumnsForAutoSizing()
    }

    val currentRowPosition = sheet.getPhysicalNumberOfRows
    if (currentRowPosition == 0) {
      writeRowHeader(sheet, currentRowPosition)
    } else
      writeRow(sheet, currentRowPosition, rowValue)
    if ("XLSX".equals(fileExtension) && !outputFileExcelEntity.isAutoColumnSize) flush(sheet)
  }

  private def writeRow(sheet: Sheet, sheetRowPosition: Int, rowValue: Row): Unit = {
    var i = 0
    val sheetRow = sheet.createRow(sheetRowPosition)
    while (i < rowValue.length) {
      val cell = sheetRow.createCell(i)
      cell.setCellType(cellTypes(i))
      if (dataCellFontStyle.get(dataSchema.fieldNames(i).toLowerCase()) != None && dataCellFontStyle.get(dataSchema.fieldNames(i).toLowerCase()) != null) if(dataCellStyleFormat.get(dataSchema.fieldNames(i).toLowerCase()) != None && dataCellStyleFormat.get(dataSchema.fieldNames(i).toLowerCase()) != null) dataCellStyleFormat.get(dataSchema.fieldNames(i).toLowerCase).get.setFont(dataCellFontStyle.get(dataSchema.fieldNames(i).toLowerCase()).get)
      if(dataCellStyleFormat.get(dataSchema.fieldNames(i).toLowerCase()) != None && dataCellStyleFormat.get(dataSchema.fieldNames(i).toLowerCase()) != null) cell.setCellStyle(dataCellStyleFormat.get(dataSchema.fieldNames(i).toLowerCase).get)
      if (!rowValue.isNullAt(i))
        setCellDataValue(cell, rowValue, i)
      else cell.setCellType(CellType.BLANK)
      i += 1
    }
  }

  private def setCellDataValue(cell: Cell, row: Row, position: Int): Any = valueDataTypes(position) match {
    case BooleanType => cell.setCellValue(row.getBoolean(position))
    case ShortType => cell.setCellValue(row.getShort(position))
    case IntegerType => cell.setCellValue(row.getInt(position))
    case LongType => cell.setCellValue(row.getLong(position))
    case FloatType => cell.setCellValue(row.getFloat(position))
    case DoubleType => cell.setCellValue(row.getDouble(position))
    case DateType => {  val df = new SimpleDateFormat(dateFormats(position));
      cell.setCellValue( df.format(row.getDate(position)))}
    case TimestampType => {  val df = new SimpleDateFormat(dateFormats(position));
      cell.setCellValue( df.format(row.getTimestamp(position)))}
    case dt: DecimalType => cell.setCellValue(row.get(position).toString)
    case dt: StringType => cell.setCellValue( row.getString(position))
    case dt: DataType => cell.setCellValue(row.get(position).toString)
  }

  private def writeRowHeader(sheet: Sheet, sheetRowPosition: Int): Unit = {
    var i = 0
    val sheetRow = sheet.createRow(sheetRowPosition)
    while (i < dataSchema.size) {
      val cell = sheetRow.createCell(i)
      cell.setCellType(CellType.STRING)

      if (headerCellFontStyle.get(dataSchema.fieldNames(i).toLowerCase()) != null && headerCellFontStyle.get(dataSchema.fieldNames(i).toLowerCase()) != None) if(headerCellStyleFormat.get(dataSchema.fieldNames(i).toLowerCase())!=null && headerCellFontStyle.get(dataSchema.fieldNames(i).toLowerCase()) != None) headerCellStyleFormat.get(dataSchema.fieldNames(i).toLowerCase()).get.setFont(headerCellFontStyle.get(dataSchema.fieldNames(i).toLowerCase()).get)
      if(headerCellStyleFormat.get(dataSchema.fieldNames(i).toLowerCase())!=null && headerCellFontStyle.get(dataSchema.fieldNames(i).toLowerCase()) != None) cell.setCellStyle(headerCellStyleFormat.get(dataSchema.fieldNames(i).toLowerCase()).get)
      cell.setCellValue(dataSchema.fieldNames(i))
      i += 1
    }
  }

  def flush(sheet: Sheet): Unit = {
    sheet.asInstanceOf[SXSSFSheet].flushRows(100); // retain 100 last rows and flush all others
  }


  def setAutoSizeColumn(sheet: Sheet ) = {
    for (i <- 0 to dataSchema.size - 1)
      sheet.autoSizeColumn(i)
  }

  private def getCellStyle(dataSchema: StructType): Map[String, CellStyle] = {

    val cellStyleFormatMap: scala.collection.mutable.Map[String, CellStyle] = scala.collection.mutable.Map[String, CellStyle]()
    for (i <- 0 until dataSchema.fieldNames.size) {
      val cellStyle = workbook.createCellStyle()
      val creationHelper = workbook.getCreationHelper
      dataSchema.fields(i).dataType match {
        case BooleanType => cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0"))
        case ShortType => cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0"))
        case IntegerType => cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0"))
        case LongType => cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0"))
        case FloatType => cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0.0"))
        case DoubleType => cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("0.0"))
        case dt: DecimalType => cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("General"))
        case StringType => cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("@"))
        case DateType => cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("@"))
        case TimestampType => cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("@"))
        case _ => cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("General"))
      }
      cellStyleFormatMap += (dataSchema.fieldNames(i).toLowerCase -> cellStyle)
    }
    cellStyleFormatMap.toMap
  }

  private def getCellType(dataType: DataType): CellType = dataType match {
    case BooleanType => CellType.BOOLEAN
    case ShortType => CellType.NUMERIC
    case IntegerType => CellType.NUMERIC
    case LongType => CellType.NUMERIC
    case FloatType => CellType.NUMERIC
    case DoubleType => CellType.NUMERIC
    case dt: DecimalType => CellType.STRING
    case StringType => CellType.STRING
    case DateType => CellType.STRING
    case TimestampType => CellType.STRING
    case _ => CellType.NUMERIC
  }


  private def updateCellBorderAlignmentStyle(cellStyleFormatMap: Map[String,CellStyle],userFormat: java.util.List[FieldFormat]): Map[String,CellStyle] = {
    val tempHSSFWorkBook: HSSFWorkbook = new HSSFWorkbook()
    val tempHSSFPalette : HSSFPalette = tempHSSFWorkBook.getCustomPalette
    for (i <- 0 until userFormat.size()) {
      val format: FieldFormat = userFormat.get(i)
      if (format.getName() != null && format.getProperty != null) {
        var j = 0
        var cellStyle: CellStyle = if(cellStyleFormatMap.get(format.getName.toLowerCase)!=null && cellStyleFormatMap.get(format.getName.toLowerCase)!= None) cellStyleFormatMap.get(format.getName.toLowerCase).get else workbook.createCellStyle()
        var borderStyle: BorderStyle = BorderStyle.THIN
        for (j <- 0 until format.getProperty.size()) {
          val formatProperty: Property = format.getProperty.get(j)
          (formatProperty.getType.toUpperCase(), formatProperty.getName.toUpperCase, formatProperty.getValue.toUpperCase()) match {

            case("COLOR","BACKGROUNDCOLOR",dcolor: String) => {
              cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
              val ucolor: java.awt.Color = java.awt.Color.decode(dcolor)
              val tempHSSFcolor: HSSFColor = tempHSSFPalette.findSimilarColor(ucolor.getRed,ucolor.getGreen,ucolor.getBlue)
              cellStyle.setFillForegroundColor(tempHSSFcolor.getIndex)
            }
            case("BORDER","BORDERSTYLE","THIN") => borderStyle = BorderStyle.THICK
            case("BORDER","BORDERSTYLE","MEDIUM") => borderStyle = BorderStyle.MEDIUM
            case("BORDER","BORDERSTYLE","DASHED") => borderStyle = BorderStyle.DASHED
            case("BORDER","BORDERSTYLE","DOTTED") => borderStyle = BorderStyle.DOTTED
            case("BORDER","BORDERSTYLE","THICK") => borderStyle = BorderStyle.THICK
            case("BORDER","BORDERSTYLE","DOUBLE") => borderStyle = BorderStyle.DOUBLE
            case("BORDER","BORDERSTYLE","HAIR") => borderStyle = BorderStyle.HAIR
            case("BORDER","BORDERSTYLE","MEDIUM_DASHED") => borderStyle = BorderStyle.MEDIUM_DASH_DOT
            case("BORDER","BORDERSTYLE","DASH_DOT") => borderStyle = BorderStyle.DASH_DOT
            case("BORDER","BORDERSTYLE","MEDIUM_DASH_DOT") => borderStyle = BorderStyle.MEDIUM_DASH_DOT
            case("BORDER","BORDERSTYLE","MEDIUM_DASH_DOT_DOT") => borderStyle = BorderStyle.MEDIUM_DASH_DOT_DOT
            case("BORDER","BORDERSTYLE","SLANTED_DASH_DOT") => borderStyle = BorderStyle.SLANTED_DASH_DOT
            case("BORDER","BORDERCOLOR",bcolor:String) => cellStyle = setCellStyleBorderColor(cellStyle,bcolor,tempHSSFPalette)
            case("BORDER","BORDERRANGE","TOP") => cellStyle.setBorderTop(borderStyle)
            case("BORDER","BORDERRANGE","BOTTOM") => cellStyle.setBorderBottom(borderStyle)
            case("BORDER","BORDERRANGE","LEFT") => cellStyle.setBorderLeft(borderStyle)
            case("BORDER","BORDERRANGE","RIGHT") => cellStyle.setBorderRight(borderStyle)
            case("BORDER","BORDERRANGE","ALL") => {
              cellStyle.setBorderTop(borderStyle)
              cellStyle.setBorderBottom(borderStyle)
              cellStyle.setBorderLeft(borderStyle)
              cellStyle.setBorderRight(borderStyle)
            }
            case("ALIGNMENT","HORIZONTALALIGNMENT","GENERAL") => cellStyle.setAlignment(HorizontalAlignment.GENERAL)
            case("ALIGNMENT","HORIZONTALALIGNMENT","LEFT") => cellStyle.setAlignment(HorizontalAlignment.LEFT)
            case("ALIGNMENT","HORIZONTALALIGNMENT","CENTER") => cellStyle.setAlignment(HorizontalAlignment.CENTER)
            case("ALIGNMENT","HORIZONTALALIGNMENT","RIGHT") => cellStyle.setAlignment(HorizontalAlignment.RIGHT)
            case("ALIGNMENT","HORIZONTALALIGNMENT","FILL") => cellStyle.setAlignment(HorizontalAlignment.FILL)
            case("ALIGNMENT","HORIZONTALALIGNMENT","JUSTIFY") => cellStyle.setAlignment(HorizontalAlignment.JUSTIFY)
            case("ALIGNMENT","HORIZONTALALIGNMENT","CENTER_SELECTION") => cellStyle.setAlignment(HorizontalAlignment.CENTER_SELECTION)
            case("ALIGNMENT","HORIZONTALALIGNMENT","DISTRIBUTED") => cellStyle.setAlignment(HorizontalAlignment.DISTRIBUTED)
            case("ALIGNMENT","VERTICALALIGNMENT","TOP") => cellStyle.setVerticalAlignment(VerticalAlignment.TOP)
            case("ALIGNMENT","VERTICALALIGNMENT","CENTER") => cellStyle.setVerticalAlignment(VerticalAlignment.CENTER)
            case("ALIGNMENT","VERTICALALIGNMENT","BOTTOM") => cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM)
            case("ALIGNMENT","VERTICALALIGNMENT","JUSTIFY") => cellStyle.setVerticalAlignment(VerticalAlignment.JUSTIFY)
            case("ALIGNMENT","VERTICALALIGNMENT","DISTRIBUTED") => cellStyle.setVerticalAlignment(VerticalAlignment.DISTRIBUTED)
            case("FONT",_,_) =>
            case(stype:String,sname:String,svalue:String) => LOG.error("Invalid Property type: "+stype+" Name:"+sname+" Value:"+svalue)
          }
        }
      }
    }
    cellStyleFormatMap
  }

  private def setCellStyleBorderColor(cellStyle: CellStyle,color: String, tempHSSFPalette : HSSFPalette): CellStyle= {
    val uColor: java.awt.Color = java.awt.Color.decode(color)
    val tempHSSFColor: HSSFColor = tempHSSFPalette.findSimilarColor(uColor.getRed,uColor.getGreen,uColor.getBlue)
    cellStyle.setTopBorderColor(tempHSSFColor.getIndex)
    cellStyle.setBottomBorderColor(tempHSSFColor.getIndex)
    cellStyle.setLeftBorderColor(tempHSSFColor.getIndex)
    cellStyle.setRightBorderColor(tempHSSFColor.getIndex)
    cellStyle
  }

}
