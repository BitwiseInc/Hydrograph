package hydrograph.ui.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import hydrograph.ui.common.datastructure.filter.ExpressionData;
import hydrograph.ui.common.datastructure.filter.OperationClassData;
import hydrograph.ui.common.exceptions.ExternalTransformException;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.external.common.InputField;
import hydrograph.ui.external.common.InputFields;
import hydrograph.ui.external.common.Properties;
import hydrograph.ui.external.expression.Expression;
import hydrograph.ui.external.expression.ExternalExpression;
import hydrograph.ui.external.expression.ExternalExpressions;
import hydrograph.ui.external.operation.ExternalOperation;
import hydrograph.ui.external.operation.ExternalOperations;
import hydrograph.ui.external.operation.Operation;
import hydrograph.ui.logging.factory.LogFactory;

public class FilterLogicExternalOperationExpressionUtil {
	
	private static Logger LOGGER = LogFactory.INSTANCE.getLogger(FilterLogicExternalOperationExpressionUtil.class);
	public static final FilterLogicExternalOperationExpressionUtil INSTANCE = new FilterLogicExternalOperationExpressionUtil();
	
	public boolean validateUIExpressionWithExternalFile(ExpressionData expressionData, File file) throws RuntimeException {
		if (expressionData != null 
			&& expressionData.getExternalExpressionData().isExternal() && isValidFile(expressionData.getExternalExpressionData().getFilePath(),file)) {
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				ExternalExpressions externalExpressions = (ExternalExpressions) ExternalOperationExpressionUtil.INSTANCE.unmarshal(fileInputStream,
						ExternalExpressions.class);
				return ValidateExpressionOperation.INSTANCE.validateUIExpressionVsJaxb(externalExpressions.getExternalExpressions(), expressionData);
			} catch (IOException | ParserConfigurationException | SAXException | JAXBException exception) {
				throw new RuntimeException(expressionData.getId() +"- Exception occured while reading external file "+exception.getMessage());
			}
		}
		return false;
	}
	
	public static boolean isValidFile(String filePath, File file) {
		if (StringUtils.isNotBlank(filePath) && file == null) {
			throw new RuntimeException(filePath + " file does not exists");
		}
		if (StringUtils.isBlank(filePath) && file == null) {
			throw new RuntimeException("Invalid external file path");
		}
		return true;
	}

	public boolean validateUIOperationWithExternalFile(OperationClassData operationClassData, File file) throws RuntimeException {
		if (operationClassData != null  
				&& operationClassData.getExternalOperationClassData().isExternal() && isValidFile(operationClassData.getExternalOperationClassData().getFilePath(), file)) {
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				ExternalOperations externalOperations = (ExternalOperations) ExternalOperationExpressionUtil.INSTANCE.unmarshal(fileInputStream,
						ExternalOperations.class);
				return ValidateExpressionOperation.INSTANCE.validateUIOperationVsJaxb(externalOperations.getExternalOperations(), operationClassData);
			} catch (IOException | ParserConfigurationException | SAXException | JAXBException exception) {
				throw new RuntimeException(operationClassData.getId() +"- Exception occured while reading external file "+exception.getMessage());
			}
		}
		return false;
	}
	
	public ExpressionData importExpression(File file, ExpressionData expressionData,boolean showErrorMessage,
		 String componentName) {
		
		if (file!=null) {
			try (FileInputStream fileInputStream=new FileInputStream(file)){
				ExternalExpressions externalExpression = (ExternalExpressions) ExternalOperationExpressionUtil.INSTANCE.unmarshal(fileInputStream,ExternalExpressions.class);
				
				expressionData = convertToUIExpression(externalExpression.getExternalExpressions(),expressionData, componentName);
				if(showErrorMessage && expressionData!=null){
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", "Expression imported sucessfully");
				}
			} catch (Exception exception) {
				LOGGER.warn("Error ", exception);
				if(showErrorMessage){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Failed to import expression - Invalid XML");
				}
			}
		}
		return expressionData;
	}
	
	
	public OperationClassData importOperation(File file, OperationClassData operationClassData ,boolean showErrorMessage,
			 String componentName) throws ExternalTransformException {
		if (file!=null) {
			try (FileInputStream fileInputStream=new FileInputStream(file)){
				ExternalOperations externalOperation=(ExternalOperations) ExternalOperationExpressionUtil.INSTANCE.unmarshal(fileInputStream,ExternalOperations.class);
				operationClassData=convertToUIOperation(operationClassData, externalOperation.getExternalOperations(), componentName);
				if(operationClassData!=null && showErrorMessage){
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", "Operation imported sucessfully");
				}
			} catch (Exception exception) {
				LOGGER.warn("Error ", exception);
				if(showErrorMessage){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Failed to import operation - Invalid XML");
				}
			}
		}
		return operationClassData;
	}
	
	public void exportExpression(File file, ExpressionData expressionData ,boolean showErrorMessage	) throws ExternalTransformException {
		if (file!=null) {
			try{
				Object object=convertUIExpressionToJaxb(expressionData);
				ExternalOperationExpressionUtil.INSTANCE.marshal(ExternalExpressions.class, file, object);
			} catch (Exception exception) {
				LOGGER.warn("Error ", exception);
				if(showErrorMessage){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Failed to export expression - \n"+exception.getMessage());
				}
				return;
			}
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", "Expression exported sucessfully");
		}
	}
	
	public void exportOperation(File file, OperationClassData operationClassData ,boolean showErrorMessage) throws ExternalTransformException {
		if (file!=null) {
			try{
				Object object=convertUIOperationToJaxb(operationClassData);
				ExternalOperationExpressionUtil.INSTANCE.marshal(ExternalOperations.class, file, object);
			} catch (Exception exception) {
				LOGGER.warn("Error ", exception);
				if(showErrorMessage){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Failed to export operation - \n"+exception.getMessage());
				}
				return;
			}
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", "Operation exported sucessfully");
		}
	}
	
	private Object convertUIOperationToJaxb(OperationClassData operationClassData) {
		ExternalOperations externalOperations=new ExternalOperations();
		ExternalOperation externalOperation=new ExternalOperation();
		externalOperations.setExternalOperations(externalOperation);
		Operation operation=new Operation();
		operation.setId(operationClassData.getId());
		operation.setClazz(operationClassData.getQualifiedOperationClassName());
		operation.setInputFields(getOperationInputFields(operationClassData));
		if(operationClassData.getClassProperties()!=null && !operationClassData.getClassProperties().isEmpty()){
			operation.setProperties(ExternalOperationExpressionUtil.INSTANCE.getOperationProperties(operationClassData.getClassProperties()));
		}
		externalOperation.setOperation(operation);
		return externalOperations;
	}
	
	
	private Object convertUIExpressionToJaxb(ExpressionData expressionData) {
		ExternalExpressions externalExpressions=new ExternalExpressions();
		ExternalExpression externalExpression=new ExternalExpression();
		externalExpressions.setExternalExpressions(externalExpression);
		Expression expression = new Expression();
		expression.setExpr(expressionData.getExpressionEditorData().getExpression());
		expression.setId(expressionData.getId());
		expression.setInputFields(getExpressionInputFields(expressionData));
		externalExpression.setExpression(expression);
		return externalExpressions;
	}
	
	private InputFields getExpressionInputFields(ExpressionData expressionData) {
		InputFields inputFields = null;
		if (!expressionData.getInputFields().isEmpty()) {
			inputFields = new InputFields();
			for (String fiedlName : expressionData.getInputFields()) {
				InputField field = new InputField();
				field.setName(fiedlName);
				field.setInSocketId(Constants.FIXED_INSOCKET_ID);
				inputFields.getField().add(field);
			}
		}
		return inputFields;
	}
	
	private InputFields getOperationInputFields(OperationClassData operationClassData) {
		InputFields inputFields = null;
		if (!operationClassData.getInputFields().isEmpty()) {
			inputFields = new InputFields();
			for (String fiedlName : operationClassData.getInputFields()) {
				InputField field = new InputField();
				field.setName(fiedlName);
				field.setInSocketId(Constants.FIXED_INSOCKET_ID);
				inputFields.getField().add(field);
			}
		}
		return inputFields;
	}
	
	
	private OperationClassData convertToUIOperation(OperationClassData operationClassData, ExternalOperation externalOperation,
			String componentName) throws ExternalTransformException{
		Operation operation=externalOperation.getOperation();
		if(operationClassData==null ){
			operationClassData=new OperationClassData();
		}
		operationClassData.setId(operation.getId());
		loadInputFieldsForOperationOrExpression(operationClassData.getInputFields(), operation.getInputFields());
		
		operationClassData.setQualifiedOperationClassName(operation.getClazz());
		loadPropertiesForOperationClass(operationClassData,operation.getProperties());
		return operationClassData;
	}
	
	private void loadPropertiesForOperationClass(OperationClassData operationClassData, Properties properties) {
		operationClassData.getClassProperties().clear();
		if(properties!=null){
			properties.getProperty().forEach(property->{
				NameValueProperty nameValueProperty=new NameValueProperty();
				nameValueProperty.setPropertyName(property.getName());
				nameValueProperty.setPropertyValue(property.getValue());
				operationClassData.getClassProperties().add(nameValueProperty);
			});
		}
	}


	private ExpressionData convertToUIExpression(ExternalExpression externalExpression,
			ExpressionData expressionData, String componentName) throws ExternalTransformException{
		Expression expression=externalExpression.getExpression();
		if(expressionData==null){
			expressionData=new ExpressionData(componentName);
		}
		expressionData.setId(expression.getId());
		loadInputFieldsForOperationOrExpression(expressionData.getInputFields(),expression.getInputFields());
		
		
		if(expressionData.getExpressionEditorData()!=null){
			updateExpressionEditorData(expressionData,expressionData.getExpressionEditorData(), expression.getExpr());
		}
		
		return expressionData;
	}

	private void loadInputFieldsForOperationOrExpression(List<String> inputFields, InputFields expressionInputFields) {
		inputFields.clear();
		if(expressionInputFields!=null && expressionInputFields.getField()!=null){
			expressionInputFields.getField().forEach(inputField->inputFields.add(inputField.getName()));
		}
	}
     
	private void updateExpressionEditorData(ExpressionData expressionData, ExpressionEditorData expressionEditorData,
			String expression) {
		if (expressionEditorData != null && StringUtils.isNotBlank(expression)) {
			expressionEditorData.setExpression(expression);
			expressionData.getInputFields().forEach(
					inputField -> expressionEditorData.getfieldsUsedInExpression().add(inputField));
		}
	}
	

	
}
