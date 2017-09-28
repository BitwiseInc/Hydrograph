package hydrograph.ui.propertywindow.widgets.utility;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Display;

import hydrograph.ui.common.datastructure.filter.ExpressionData;
import hydrograph.ui.common.datastructure.filter.FilterLogicDataStructure;
import hydrograph.ui.common.datastructure.filter.OperationClassData;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.FilterLogicExternalOperationExpressionUtil;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.common.util.PathUtility;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;
import hydrograph.ui.propertywindow.filter.viewer.ErrorLogTableViewer;
import hydrograph.ui.validators.Messages;
import hydrograph.ui.validators.utils.ValidatorUtility;

/**
 * @author Bitwise
 * 
 * validation for filter operation and expression
 *
 */
public class FilterOperationExpressionValidation {
	
	public static final FilterOperationExpressionValidation INSTANCE = new FilterOperationExpressionValidation();
	
	/**
	 * 
	 * validate expression data
	 * 
	 * @param filterLogicData
	 * @param inputSchemaFields
	 * @param errors
	 * @param errorLogTableViewer 
	 */
	public void validationExpressionData(FilterLogicDataStructure filterLogicData,Map<String, Class<?>> inputSchemaFields
			,Set<String> errors, ErrorLogTableViewer errorLogTableViewer) {
		
		ExpressionData expressionData=filterLogicData.getExpressionEditorData();
		if(StringUtils.isBlank(expressionData.getId())){
			errors.add(Messages.EXPRESSION_ID_IS_BLANK);
		}
		if(!expressionData.getInputFields().stream().allMatch(inputField->filterLogicData.getAvailableFields().contains(inputField))){
			errors.add(Messages.INPUT_FIELD_S_DOESN_T_MATCH_WITH_AVAILABLE_FIELDS);
		}
		checkIfGivenExpressionIsValid(inputSchemaFields, errors, expressionData, errorLogTableViewer);
		
		if(expressionData.getExternalExpressionData()!=null 
		&& expressionData.getExternalExpressionData().isExternal())
		{
			if( StringUtils.isBlank(expressionData.getExternalExpressionData().getFilePath()) ){
				errors.add(Messages.EXTERNAL_FILE_PATH_IS_BLANK);
			}else{
				checkIfUIDataAndFileIsOutOfSync(expressionData,errors, errorLogTableViewer);
			}
		}
	}


	private void checkIfGivenExpressionIsValid(Map<String, Class<?>> inputSchemaFields, Set<String> errors,
			ExpressionData expressionData, ErrorLogTableViewer errorLogTableViewer) {
		if(StringUtils.isBlank(expressionData.getExpressionEditorData().getExpression())){
			errors.add(Messages.EXPRESSION_IS_BLANK);
		}
		else{
			Display.getCurrent().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					ExpressionEditorUtil.validateExpression(expressionData.getExpressionEditorData().getExpression(),
							inputSchemaFields,expressionData.getExpressionEditorData());
					if(!expressionData.getExpressionEditorData().isValid()){
						errors.add(Messages.EXPRESSION_IS_INVALID);
						errorLogTableViewer.refresh();
					}
				}
			});
			
		}
	}
	
	
	/**
	 * validate operation class data
	 * 
	 * @param filterLogicData
	 * @param errors
	 * @param errorLogTableViewer 
	 */
	public void validateOperationClassData(FilterLogicDataStructure filterLogicData,Set<String> errors, ErrorLogTableViewer errorLogTableViewer) {
		OperationClassData operationClassData=filterLogicData.getOperationClassData();
		if(StringUtils.isBlank(operationClassData.getId())){
			errors.add(Messages.OPERATION_ID_IS_BLANK);
		}
		if(!operationClassData.getInputFields().stream().allMatch(inputField->filterLogicData.getAvailableFields().contains(inputField))){
			errors.add(Messages.INPUT_FIELD_S_DOESN_T_MATCH_WITH_AVAILABLE_FIELDS);
		}
		if(StringUtils.isBlank(operationClassData.getQualifiedOperationClassName()))
		{
			errors.add(Messages.OPERATION_CLASS_IS_BLANK);
		}
		if(
			!ParameterUtil.isParameter(operationClassData.getQualifiedOperationClassName())
			&&!(ValidatorUtility.INSTANCE.isClassFilePresentOnBuildPath(operationClassData.getQualifiedOperationClassName())))
			
		{
			errors.add(Messages.OPERATION_CLASS_IS_NOT_PRESENT);
		}
		if(operationClassData.getExternalOperationClassData()!=null 
		   && operationClassData.getExternalOperationClassData().isExternal()
		   )
		{
			if( StringUtils.isBlank(operationClassData.getExternalOperationClassData().getFilePath())){
				errors.add(Messages.EXTERNAL_FILE_PATH_IS_BLANK);
			}else{
				checkIfUIDataAndFileIsOutOfSync(operationClassData,errors, errorLogTableViewer);
			}
		}
		
	}
	
	private void checkIfUIDataAndFileIsOutOfSync(OperationClassData operationClassData,Set<String> errors, ErrorLogTableViewer errorLogTableViewer) {
		Display.getCurrent().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try{
					FilterLogicExternalOperationExpressionUtil.INSTANCE.validateUIOperationWithExternalFile(operationClassData, 
							PathUtility.INSTANCE.getPath(operationClassData.getExternalOperationClassData().getFilePath(),
									Constants.XML_EXTENSION, false, Constants.XML_EXTENSION));	
					
				}catch(RuntimeException exception){
					errors.add(exception.getMessage());
					errorLogTableViewer.refresh();
				}
			}
		});
	}
	
	private void checkIfUIDataAndFileIsOutOfSync(ExpressionData expressionData,Set<String> errors, ErrorLogTableViewer errorLogTableViewer) {
		Display.getCurrent().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try{
					FilterLogicExternalOperationExpressionUtil.INSTANCE.validateUIExpressionWithExternalFile(expressionData, 
							PathUtility.INSTANCE.getPath(expressionData.getExternalExpressionData().getFilePath(),
									Constants.XML_EXTENSION, false, Constants.XML_EXTENSION));	
				}catch(RuntimeException exception){
					if(StringUtils.isNotBlank(exception.getMessage())){
						errors.add(exception.getMessage());
					}
					errorLogTableViewer.refresh();
				}
			}
		});
	}
}
