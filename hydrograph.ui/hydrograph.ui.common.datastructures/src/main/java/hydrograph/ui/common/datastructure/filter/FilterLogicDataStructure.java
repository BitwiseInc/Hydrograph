package hydrograph.ui.common.datastructure.filter;

import java.util.ArrayList;
import java.util.List;

import hydrograph.ui.common.cloneableinterface.IDataStructure;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.mapping.ExternalWidgetData;

/**
 * @author Bitwise
 * 
 *represents filter data
 *
 */
public class FilterLogicDataStructure implements IDataStructure{
	
	private boolean isOperation;
	private ExpressionData expressionEditorData;
	private OperationClassData operationClassData;
	private List<String> availableFields;
	
	public FilterLogicDataStructure(String componentName) {
		availableFields = new ArrayList<>();
		operationClassData = new OperationClassData();
		expressionEditorData = new ExpressionData(new ExpressionEditorData("", componentName),
				new ExternalWidgetData(false, null));
	}
	
	public boolean isOperation() {
		return isOperation;
	}

	public void setOperation(boolean isOperation) {
		this.isOperation = isOperation;
	}

	public void setComponentName(String componentName) {
		if (expressionEditorData != null && expressionEditorData.getExpressionEditorData() != null) {
			expressionEditorData.getExpressionEditorData().setComponentName(componentName);
		}
	}
	
	/**
	 * @return the availableFields
	 */
	public List<String> getAvailableFields() {
		if(availableFields==null){
			availableFields=new ArrayList<>();
		}
		return availableFields;
	}


	/**
	 * @return the expressionEditorData
	 */
	public ExpressionData getExpressionEditorData() {
		return expressionEditorData;
	}
	/**
	 * @param expressionEditorData the expressionEditorData to set
	 */
	public void setExpressionEditorData(ExpressionData expressionEditorData) {
		this.expressionEditorData = expressionEditorData;
	}
	/**
	 * @return the operationClassData
	 */
	public OperationClassData getOperationClassData() {
		return operationClassData;
	}
	/**
	 * @param operationClassData the operationClassData to set
	 */
	public void setOperationClassData(OperationClassData operationClassData) {
		this.operationClassData = operationClassData;
	}
	
	public Object clone(){
		FilterLogicDataStructure dataStructure=new FilterLogicDataStructure(expressionEditorData.getExpressionEditorData().getComponentName());
		dataStructure.isOperation=isOperation;
		dataStructure.availableFields=new ArrayList<>(getAvailableFields());
		dataStructure.expressionEditorData=(ExpressionData) expressionEditorData.clone();
		dataStructure.operationClassData=(OperationClassData) operationClassData.clone();
		return dataStructure;
	}
	
	
	
}
