package hydrograph.engine.expression.api.wrapper

import org.codehaus.janino.ExpressionEvaluator

/**
  * The Class ValidationAPIWrapper .
  *
  * @author Bitwise
  */
class ValidationAPIWrapper(expression:String,fieldNames:Array[String],fieldTypes:Array[Class[_]]) extends Serializable{

  @transient lazy val expressionEvaluator=new ExpressionEvaluator(expression,classOf[Object],fieldNames,fieldTypes)

  def execute(data:Array[Object]): Object = try
    expressionEvaluator.evaluate(data)
  catch {
    case e: Exception => throw new RuntimeException(e)
  }

}
