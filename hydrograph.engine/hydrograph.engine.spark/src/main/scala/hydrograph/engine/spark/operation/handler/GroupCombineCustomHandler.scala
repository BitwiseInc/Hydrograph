package hydrograph.engine.spark.operation.handler

import hydrograph.engine.spark.core.reusablerow._
import hydrograph.engine.transformation.schema
import hydrograph.engine.transformation.schema.{Field, Schema}
import hydrograph.engine.transformation.userfunctions.base.GroupCombineTransformBase
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{StructField, _}

import scala.collection.JavaConversions._
/**
  * The Class GroupCombineCustomHandler.
  *
  * @author Bitwise
  *
  */
case class GroupCombineCustomHandler(groupCombineTransform: GroupCombineTransformBase, inSchema: StructType, outSchema: StructType, isDeterministic: Boolean) extends UserDefinedAggregateFunction {

  val bufSchemaVal = bufferSchema

  def inputSchema: StructType = inSchema

  def deterministic = isDeterministic



  val bufferMapper = new RowToReusableMapper(bufSchemaVal, bufSchemaVal.fieldNames)
  val inputMapper = new RowToReusableMapper(inSchema, inSchema.fieldNames)
  val outputMapper = new RowToReusableMapper(dataType.asInstanceOf[StructType], dataType.asInstanceOf[StructType].fieldNames)

  def initialize(buffer: MutableAggregationBuffer) = {
    try {
      var brr = BufferReusableRow(buffer, bufferMapper)
      groupCombineTransform.initialize(brr)
    } catch {
      case e: Exception => throw new RuntimeException("Exception in initialize() for Transform Class:[\"" + groupCombineTransform + "\"] for row:[\"" + buffer.toString() + "\"] error being:" ,e)
    }
  }


  def update(buffer: MutableAggregationBuffer, input: Row) = {
    try {
      val brr = BufferReusableRow(buffer, bufferMapper)
      groupCombineTransform.update(brr, InputReusableRow(input, inputMapper))
    } catch {
      case e: Exception => throw new RuntimeException("Exception in update() for Transform Class:[\"" + groupCombineTransform + "\"] for row:[\"" + input.toString() + "\"] error being:" ,e)
    }
  }

  def merge(buffer1: MutableAggregationBuffer, buffer2: Row) = {
    try {
      val brr = BufferReusableRow(buffer1, bufferMapper)
      val irr = InputReusableRow(buffer2, bufferMapper)
      groupCombineTransform.merge(brr, irr)
    } catch {
      case e: Exception => throw new RuntimeException("Exception in merge() for Transform Class:[\"" + groupCombineTransform + "\"] for row:[\"" + buffer1.toString() + "\"] error being:" ,e)
    }
  }

  def evaluate(buffer: Row) = {
    val output = new Array[Any](outSchema.size)
    try {
      val orr: OutputReusableRow = OutputReusableRow(output, outputMapper)
      val irr = InputReusableRow(buffer, bufferMapper)
      groupCombineTransform.evaluate(irr, orr)
    } catch {
      case e: Exception => throw new RuntimeException("Exception in evaluate() for Transform Class:[\"" + groupCombineTransform + "\"] for row:[\"" + buffer.toString() + "\"] error being:" ,e)
    }
    Row.fromSeq(output)
  }

  def bufferSchema = createBufferSchema(groupCombineTransform)

  def createBufferSchema(aggregatorTransformBase: GroupCombineTransformBase): StructType = {
    val inputSchema:Schema=new Schema
    inSchema.foreach(sf=>inputSchema.addField(new Field.Builder(sf.name,getJavaDataType(sf.dataType)).build()))

    val outputSchema:Schema=new Schema
    outSchema.foreach(sf=>outputSchema.addField(new Field.Builder(sf.name,getJavaDataType(sf.dataType)).build()))

    val bufferSchema: Schema = aggregatorTransformBase.initBufferSchema(inputSchema,outputSchema)
    var bufferFieldMap: Map[String, Field] = Map()
    for (bufferField <- bufferSchema.getSchema) {
      bufferFieldMap += bufferField._1 -> bufferField._2
    }

    val array: Array[StructField] = new Array[StructField](bufferFieldMap.size())
    var i: Int = 0
    for (bs <- bufferFieldMap.values) {
      array(i) = new StructField(bs.getFieldName, getSparkDataType(bs.getFieldType.toString, bs.getFieldFormat, bs.getFieldPrecision, bs.getFieldScale))
      i = i + 1
    }
    StructType(array)
  }

  def getJavaDataType(structType: DataType): schema.DataType = structType match {
    case _:IntegerType=> schema.DataType.Integer
    case _:StringType=>schema.DataType.String
    case _:LongType=> schema.DataType.Long
    case _:ShortType=> schema.DataType.Short
    case _:BooleanType=> schema.DataType.Boolean
    case _:FloatType=> schema.DataType.Float
    case _:DoubleType=> schema.DataType.Double
    case _:TimestampType=> schema.DataType.Date
    case _:DateType=> schema.DataType.Date
    case _:DecimalType=> schema.DataType.BigDecimal
  }


  def getSparkDataType(dataType: String, format: String, precision: Int, scale: Int): DataType = dataType match {
    case "Integer" => DataTypes.IntegerType
    case "String" => DataTypes.StringType
    case "Long" => DataTypes.LongType
    case "Short" => DataTypes.ShortType
    case "Boolean" => DataTypes.BooleanType
    case "Float" => DataTypes.FloatType
    case "Double" => DataTypes.DoubleType
    case "Date" if format.matches(".*[H|m|s|S].*") => DataTypes.TimestampType
    case "Date" => DataTypes.DateType
    case "BigDecimal" => DataTypes.createDecimalType(checkPrecision(precision), scale)
  }

  def checkPrecision(precision: Int): Int = if (precision == -999) 38 else precision

  def dataType: DataType = outSchema

}
