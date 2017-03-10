package hydrograph.engine.expression.api.wrapper

import org.codehaus.janino.ExpressionEvaluator
/**
  * Created by gurdits on 2/17/2017.
  */
class ValidationAPIWrapper(expression:String,fieldNames:Array[String],fieldTypes:Array[Class[_]]) extends Serializable{

  @transient lazy val expressionEvaluator=new ExpressionEvaluator(expression,classOf[Object],fieldNames,fieldTypes)

  def execute(data:Array[Object]): Object ={
    try{
      expressionEvaluator.evaluate(data)
    } catch {
      case e:Exception => throw new RuntimeException(e)
    }

  }

}
