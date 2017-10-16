package hydrograph.ui.common.datastructure.filter;

import java.util.ArrayList;
import java.util.List;

import hydrograph.ui.common.cloneableinterface.IDataStructure;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.mapping.ExternalWidgetData;

/**
 * @author Bitwise
 * 
 * represents expression data
 *
 */
public class ExpressionData implements IDataStructure{
	
	private String id;	
	private List<String> inputFields;
	private ExpressionEditorData expressionEditorData;
	private ExternalWidgetData externalExpressionData;
	
	private ExpressionData(){}
	
	public ExpressionData(ExpressionEditorData expressionEditorData,ExternalWidgetData externalExpressionData){
		id="Expression:1";
		this.expressionEditorData=expressionEditorData;
		this.externalExpressionData=externalExpressionData;
	}
	
	public ExpressionData(String componentName){
		id="Expression:1";
		this.externalExpressionData=new ExternalWidgetData(false, null);
		this.expressionEditorData=new ExpressionEditorData("", componentName);
	}
	
	/**
	 * @return the expressionEditorData
	 */
	public ExpressionEditorData getExpressionEditorData() {
		return expressionEditorData;
	}
	/**
	 * @param expressionEditorData the expressionEditorData to set
	 */
	public void setExpressionEditorData(ExpressionEditorData expressionEditorData) {
		this.expressionEditorData = expressionEditorData;
	}
	/**
	 * @return the externalExpressionData
	 */
	public ExternalWidgetData getExternalExpressionData() {
		return externalExpressionData;
	}
	/**
	 * @param externalExpressionData the externalExpressionData to set
	 */
	public void setExternalExpressionData(ExternalWidgetData externalExpressionData) {
		this.externalExpressionData = externalExpressionData;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getInputFields() {
		if(inputFields==null){
			inputFields=new ArrayList<>();
		}
		return inputFields;
	}

	public String getExpression() {
		if(expressionEditorData==null){
			return "";
		}
		return expressionEditorData.getExpression();
	}

	
	public Object clone() {
		ExpressionData expressionData = new ExpressionData();
		expressionData.id = id;
		expressionData.expressionEditorData = expressionEditorData.clone();
		expressionData.externalExpressionData = (ExternalWidgetData) externalExpressionData.clone();
		expressionData.inputFields = new ArrayList<>(getInputFields());
		return expressionData;
	}
}
