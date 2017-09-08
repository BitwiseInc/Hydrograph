package hydrograph.ui.validators.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.datastructure.filter.ExpressionData;
import hydrograph.ui.common.datastructure.filter.FilterLogicDataStructure;
import hydrograph.ui.common.datastructure.filter.OperationClassData;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;
import hydrograph.ui.expression.editor.util.FieldDataTypeMap;
import hydrograph.ui.validators.Messages;
import hydrograph.ui.validators.utils.ValidatorUtility;

/**
 * @author Bitwise
 * 
 * Validate filter data
 *
 */
public class FilterDataValidation implements IValidator {
	
	private String errorMessage;
	private String propertyName;
	private List<String> availableFields;
	
	@Override
	public boolean validateMap(Object object, String propertyName,
			Map<String, List<FixedWidthGridRow>> inputSchemaMap) {
		Map<String, Object> propertyMap = (Map<String, Object>) object;
		if(propertyMap != null && !propertyMap.isEmpty()){ 
			return validate(propertyMap.get(propertyName), propertyName,inputSchemaMap,false);
		}
		return false;
	}

	@Override
	public boolean validate(Object object, String propertyName, Map<String, List<FixedWidthGridRow>> inputSchemaMap,
			boolean isJobFileImported) {
		this.propertyName=propertyName;
		FilterLogicDataStructure filterData=(FilterLogicDataStructure) object;
		if(filterData==null){
			setErrorMessage(Messages.IS_MANDATORY);
			return false;
		}
		availableFields=filterData.getAvailableFields();
		if(((filterData.isOperation()) && !validateOperationClassData(filterData.getOperationClassData()))
		||!validationExpressionData(filterData.getExpressionEditorData(),inputSchemaMap))
		{
			return false;
		}
		return true;
	}

	private boolean isInputFieldsPresentInAvailableFields(List<String> inputFields) {
		if(!inputFields.stream().allMatch(inputField->availableFields.contains(inputField))){
			setErrorMessage(Messages.INPUT_FIELD_S_DOESN_T_MATCH_WITH_AVAILABLE_FIELDS);
			return false;
		}
		return true;
	}

	private boolean isIDBlank (String id,String errorText) {
		if(StringUtils.isBlank(id)){
		   setErrorMessage(errorText);
		   return false;
		}
		return true;
	}

	private boolean validateOperationClassData(OperationClassData operationClassData) {
		if(!isIDBlank(operationClassData.getId(),Messages.OPERATION_ID_IS_BLANK)
		||!isInputFieldsPresentInAvailableFields(operationClassData.getInputFields())){
			return false;
		}
		else if(StringUtils.isBlank(operationClassData.getQualifiedOperationClassName()))
		{
			setErrorMessage(Messages.OPERATION_CLASS_IS_BLANK);
			return false;
		}
		else if(
			!ParameterUtil.isParameter(operationClassData.getQualifiedOperationClassName())
			&&!(ValidatorUtility.INSTANCE.isClassFilePresentOnBuildPath(operationClassData.getQualifiedOperationClassName())))
			
		{
			setErrorMessage(Messages.OPERATION_CLASS_IS_NOT_PRESENT);
			return false;
		}else if(operationClassData.getExternalOperationClassData()!=null 
		        && operationClassData.getExternalOperationClassData().isExternal()
		       )
		{
			if(StringUtils.isBlank(operationClassData.getExternalOperationClassData().getFilePath())){
				setErrorMessage(Messages.EXTERNAL_FILE_PATH_IS_BLANK);
				return false;
			}
		}
		return true;
	}

	private boolean validationExpressionData(ExpressionData expressionData,Map<String, List<FixedWidthGridRow>>  inputSchemaMap) {
		
		if(!isIDBlank(expressionData.getId(),Messages.EXPRESSION_ID_IS_BLANK)
				||!isInputFieldsPresentInAvailableFields(expressionData.getInputFields())){
					return false;
		}
		else if(expressionData.getExternalExpressionData()!=null 
		&& expressionData.getExternalExpressionData().isExternal() 
		&& StringUtils.isBlank(expressionData.getExternalExpressionData().getFilePath()) ){
			setErrorMessage(Messages.EXTERNAL_FILE_PATH_IS_BLANK);
			return false;
		}
		else{
			ExpressionEditorData expressionEditorData=expressionData.getExpressionEditorData();
			if(expressionEditorData==null){
				return false;
			}
			if(StringUtils.isBlank(expressionEditorData.getExpression())){
				setErrorMessage(Messages.EXPRESSION_IS_BLANK);
				return false;
			}
			ExpressionEditorUtil.validateExpression(expressionEditorData.getExpression(),
					FieldDataTypeMap.INSTANCE.createFieldDataTypeMap(
					expressionEditorData.getfieldsUsedInExpression(),
					inputSchemaMap.get(Constants.FIXED_INSOCKET_ID)),expressionEditorData);
			if(!expressionEditorData.isValid()){
				setErrorMessage(Messages.EXPRESSION_IS_INVALID);
				return false;
			}
		}
		return true;
		
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
    
	private void setErrorMessage(String errorMessage){
		errorMessage = propertyName +" "+errorMessage;
	}
}
