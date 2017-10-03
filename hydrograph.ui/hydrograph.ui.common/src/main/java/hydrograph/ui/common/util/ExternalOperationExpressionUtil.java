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

package hydrograph.ui.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import hydrograph.ui.common.component.config.Operations;
import hydrograph.ui.common.component.config.TypeInfo;
import hydrograph.ui.common.exceptions.ExternalTransformException;
import hydrograph.ui.common.message.Messages;
import hydrograph.ui.common.schema.Field;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.mapping.ExternalWidgetData;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.external.common.ExpressionOutputFields;
import hydrograph.ui.external.common.InputField;
import hydrograph.ui.external.common.InputFields;
import hydrograph.ui.external.common.OperationOutputFields;
import hydrograph.ui.external.common.Properties;
import hydrograph.ui.external.common.Properties.Property;
import hydrograph.ui.external.expression.Expression;
import hydrograph.ui.external.expression.ExternalExpression;
import hydrograph.ui.external.expression.ExternalExpressions;
import hydrograph.ui.external.mapping.ExpressionField;
import hydrograph.ui.external.mapping.ExternalMapping;
import hydrograph.ui.external.mapping.ExternalMappings;
import hydrograph.ui.external.mapping.MapField;
import hydrograph.ui.external.mapping.OperationField;
import hydrograph.ui.external.operation.ExternalOperation;
import hydrograph.ui.external.operation.ExternalOperations;
import hydrograph.ui.external.operation.Operation;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * Utility class for external operation, expression, mapping-fields
 * 
 * @author Bitwise
 *
 */
public class ExternalOperationExpressionUtil {

	private static final String VALUE = "*";
	private static final String STRING = "String";
	private static final String BIGDECIMAL = "Bigdecimal";
	private static final String INTEGER = "Integer";
	private static final String CUSTOM = "Custom";
	public static final ExternalOperationExpressionUtil INSTANCE = new ExternalOperationExpressionUtil();
	public final static String EXTERNAL_OPERATION_CONFIG_XSD_PATH = Platform.getInstallLocation().getURL().getPath()+ Messages.EXTERNAL_OPERATION_CONFIG_XSD_PATH;
	public final static String EXTERNAL_EXPRESSION_CONFIG_XSD_PATH = Platform.getInstallLocation().getURL().getPath()+ Messages.EXTERNAL_EXPRESSION_CONFIG_XSD_PATH;
	public final static String EXTERNAL_MAPPING_FIELDS_CONFIG_XSD_PATH = Platform.getInstallLocation().getURL().getPath()+ Messages.EXTERNAL_MAPPING_FIELDS_CONFIG_XSD_PATH;
	private static Logger LOGGER = LogFactory.INSTANCE.getLogger(ExternalOperationExpressionUtil.class);

	
	/**
	 * Validates UI-Expression data with its external file
	 * 
	 * @param mappingSheetRow
	 * @param file
	 * @return
	 * @throws RuntimeException
	 */
	public boolean validateUIExpressionWithExternalFile(MappingSheetRow mappingSheetRow, File file) throws RuntimeException {
		if (mappingSheetRow != null && mappingSheetRow.isActive()
				&& mappingSheetRow.getExternalExpresion().isExternal()) {
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				ExternalExpressions externalExpressions = (ExternalExpressions) unmarshal(fileInputStream,
						ExternalExpressions.class);
				return ValidateExpressionOperation.INSTANCE.validateUIExpressionVsJaxb(externalExpressions.getExternalExpressions(), mappingSheetRow);
			} catch (IOException | ParserConfigurationException | SAXException | JAXBException exception) {
				throw new RuntimeException(mappingSheetRow.getOperationID() +"- Exception occured while reading external file "+exception.getMessage());
			}
		}
		return false;
	}
	
	/**
	 * Validates UI-Operation data with its external file
	 * 
	 * @param mappingSheetRow
	 * @param file
	 * @return
	 * @throws RuntimeException
	 */
	public boolean validateUIOperationWithExternalFile(MappingSheetRow mappingSheetRow, File file) throws RuntimeException {
		if (mappingSheetRow != null && mappingSheetRow.isActive() && !mappingSheetRow.isExpression()
				&& mappingSheetRow.getExternalOperation().isExternal()) {
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				ExternalOperations externalOperations = (ExternalOperations) unmarshal(fileInputStream,
						ExternalOperations.class);
				return ValidateExpressionOperation.INSTANCE.validateUIOperationVsJaxb(externalOperations.getExternalOperations(), mappingSheetRow);
			} catch (IOException | ParserConfigurationException | SAXException | JAXBException exception) {
				throw new RuntimeException(mappingSheetRow.getOperationID() +"- Exception occured while reading external file "+exception.getMessage());
			}
		}
		return false;
	}
	
	/**
	 * Validates UI-mapping data with its external file
	 * 
	 * @param transformMapping
	 * @param file
	 * @return
	 * @throws RuntimeException
	 */
	public boolean validateUIMappingFieldsWithExternalFile(TransformMapping transformMapping, File file) throws RuntimeException {
		ExternalWidgetData externalOutputFieldsData = transformMapping.getExternalOutputFieldsData();
		if (externalOutputFieldsData != null && externalOutputFieldsData.isExternal()) {
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				ExternalMappings externalMappings = (ExternalMappings) unmarshal(fileInputStream, ExternalMappings.class);
				return ValidateExpressionOperation.INSTANCE.validateUIMappingFieldsVsJaxb(externalMappings.getExternalMappings(), transformMapping);
			} catch (IOException | ParserConfigurationException | SAXException | JAXBException exception) {
				throw new RuntimeException("Output Fields" + "- Exception occured while reading external file "
						+ exception.getMessage() == null ? "" : exception.getMessage());
			}
		}
		return false;
	}
	
	
	/**
	 * Imports UI-operation data from external operation file
	 * 
	 * @param file
	 * @param mappingSheetRow
	 * @param showErrorMessage
	 * @param transformMapping
	 * @param componentName
	 * @return
	 * @throws ExternalTransformException
	 */
	public MappingSheetRow importOperation(File file, MappingSheetRow mappingSheetRow ,boolean showErrorMessage,
			TransformMapping transformMapping, String componentName) throws ExternalTransformException {
		if (file!=null) {
			try (FileInputStream fileInputStream=new FileInputStream(file)){
				ExternalOperations externalOperation=(ExternalOperations) unmarshal(fileInputStream,ExternalOperations.class);
				mappingSheetRow=convertToUIOperation(mappingSheetRow, externalOperation.getExternalOperations(),transformMapping, componentName);
				if(mappingSheetRow!=null && showErrorMessage){
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", "Operation imported sucessfully");
				}
			} catch (Exception exception) {
				LOGGER.warn("Error ", exception);
				if(showErrorMessage){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Failed to import operation - Invalid XML");
				}
			}
		}
		return mappingSheetRow;
	}
	
	/**
	 * Imports UI-expression data from external file
	 * 
	 * @param file
	 * @param mappingSheetRow
	 * @param showErrorMessage
	 * @param transformMapping
	 * @param componentName
	 * @return
	 */
	public MappingSheetRow importExpression(File file, MappingSheetRow mappingSheetRow, boolean showErrorMessage,
			TransformMapping transformMapping, String componentName) {
		if (file!=null) {
			try (FileInputStream fileInputStream=new FileInputStream(file)){
				ExternalExpressions externalExpression = (ExternalExpressions) unmarshal(fileInputStream,ExternalExpressions.class);
				mappingSheetRow = convertToUIExpression(mappingSheetRow, externalExpression.getExternalExpressions(),transformMapping, componentName);
				if(showErrorMessage && mappingSheetRow!=null){
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", "Expression imported sucessfully");
				}
			} catch (Exception exception) {
				LOGGER.warn("Error ", exception);
				if(showErrorMessage){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Failed to import expression - Invalid XML");
				}
			}
		}
		return mappingSheetRow;
	}
	
	/**
	 * Imports UI-mapping data from external file
	 * 
	 * @param file
	 * @param transformMapping
	 * @param showErrorMessage
	 * @param componentName
	 * @throws ExternalTransformException
	 */
	public void importOutputFields(File file, TransformMapping transformMapping ,boolean showErrorMessage, String componentName) throws ExternalTransformException {
		if (file!=null && isconfirmedFromUser()) {
			try (FileInputStream fileInputStream=new FileInputStream(file)){
				ExternalMappings externalOperation=(ExternalMappings) unmarshal(fileInputStream, ExternalMappings.class);
				convertToUIOperationField(transformMapping, externalOperation.getExternalMappings(), componentName);
				if(transformMapping.isAllInputFieldsArePassthrough()){
					addPassThroughField(transformMapping);
				}
			} catch (Exception exception) {
				LOGGER.warn("Error ", exception);
				if(showErrorMessage){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Failed to import output fields - Invalid XML");
				}
				return;
			}
			Display.getCurrent().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if(showErrorMessage) {
						MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", "Output-Fields imported sucessfully");
					}
				}
			});
		}
	}
	
	private boolean isconfirmedFromUser() {
		
		return true;
	}

	private void addPassThroughField(TransformMapping transformMapping) {
		transformMapping.getInputFields().forEach(inputField->{
			NameValueProperty nameValueProperty=new NameValueProperty();
			nameValueProperty.setPropertyName(inputField.getFieldName());
			nameValueProperty.setPropertyValue(inputField.getFieldName());
			nameValueProperty.getFilterProperty().setPropertyname(inputField.getFieldName());
			transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());
			transformMapping.getMapAndPassthroughField().add(nameValueProperty);
		});
	}

	public void exportExpression(File file, MappingSheetRow mappingSheetRow ,boolean showErrorMessage,List<GridRow> schemaFieldList
			) throws ExternalTransformException {
		if (file!=null) {
			try{
				Object object=convertUIExpressionToJaxb(mappingSheetRow, schemaFieldList);
				marshal(ExternalExpressions.class, file, object);
			} catch (Exception exception) {
				LOGGER.warn("Error ", exception);
				if(showErrorMessage){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Failed to export output fields - \n"+exception.getMessage());
				}
				return;
			}
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", "Expression exported sucessfully");
		}
	}
	
	/**
	 * Exports UI-operation data to external file
	 * 
	 * @param file
	 * @param mappingSheetRow
	 * @param showErrorMessage
	 * @param list
	 * @throws ExternalTransformException
	 */
	public void exportOperation(File file, MappingSheetRow mappingSheetRow ,boolean showErrorMessage,List<GridRow> list) throws ExternalTransformException {
		if (file!=null) {
			try{
				Object object=convertUIOperationToJaxb(mappingSheetRow, list);
				marshal(ExternalOperations.class, file, object);
			} catch (Exception exception) {
				LOGGER.warn("Error ", exception);
				if(showErrorMessage){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Failed to export output fields - \n"+exception.getMessage());
				}
				return;
			}
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", "Operation exported sucessfully");
		}
	}
	

	/**
	 * Exports UI-mapping data to external file
	 * 
	 * @param file
	 * @param transformMapping
	 * @param showErrorMessage
	 * @param componentName
	 * @throws ExternalTransformException
	 */
	public void exportOutputFields(File file, TransformMapping transformMapping ,boolean showErrorMessage,String componentName) throws ExternalTransformException {
		if (file!=null) {
			try{
				Object object=convertUiOutputFieldsToJaxb(transformMapping,componentName);
				marshal(ExternalMappings.class, file, object);
			} catch (Exception exception) {
				LOGGER.warn("Error ", exception);
				if(showErrorMessage){
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Failed to export output fields - \n"+exception.getMessage());
				}
				return;
			}
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Information", "Fields exported sucessfully");
		}
	}
	
	private Object convertUIOperationToJaxb(MappingSheetRow mappingSheetRow, List<GridRow> list) {
		ExternalOperations externalOperations=new ExternalOperations();
		ExternalOperation externalOperation=new ExternalOperation();
		externalOperations.setExternalOperations(externalOperation);
		Operation operation=new Operation();
		operation.setId(mappingSheetRow.getOperationID());
		operation.setClazz(mappingSheetRow.getOperationClassPath());
		operation.setInputFields(getInputFields(mappingSheetRow));
		if(mappingSheetRow.getNameValueProperty()!=null && !mappingSheetRow.getNameValueProperty().isEmpty()){
			operation.setProperties(getOperationProperties(mappingSheetRow.getNameValueProperty()));
		}
		addOutputFieldsToJaxbExpression(mappingSheetRow, list, operation);
		externalOperation.setOperation(operation);
		return externalOperations;
	}
	
	/**
	 * Converts ui-property list data to Jaxb Properties
	 * 
	 * @param nameValueProperties
	 * @return
	 */
	public Properties getOperationProperties(List<NameValueProperty> nameValueProperties) {
		Properties properties=new Properties();
		nameValueProperties.forEach(uiProperty->{
			Property property = new Property();
			property.setName(uiProperty.getPropertyName());
			property.setValue(uiProperty.getPropertyValue());
			properties.getProperty().add(property);
		});
		return properties;
	}
	
	private Object convertUIExpressionToJaxb(MappingSheetRow mappingSheetRow, List<GridRow> schemaFieldList) {
		ExternalExpressions externalExpressions=new ExternalExpressions();
		ExternalExpression externalExpression=new ExternalExpression();
		externalExpressions.setExternalExpressions(externalExpression);
		Expression expression = new Expression();
		expression.setAccumulatorInitalValue(mappingSheetRow.getAccumulator());
		expression.setExpr(mappingSheetRow.getExpressionEditorData().getExpression());
		expression.setId(mappingSheetRow.getOperationID());
		expression.setInputFields(getInputFields(mappingSheetRow));
		if(StringUtils.isNotBlank(mappingSheetRow.getMergeExpressionDataForGroupCombine().getExpression())){
			expression.setMergeExpr(mappingSheetRow.getMergeExpressionDataForGroupCombine().getExpression());
		}
		addOutputFieldsToJaxbExpression(mappingSheetRow, schemaFieldList, expression);
		externalExpression.setExpression(expression);
		return externalExpressions;
	}
	
	private void addOutputFieldsToJaxbExpression(MappingSheetRow mappingSheetRow, List<GridRow> list,
			Operation operation) {
		if (!mappingSheetRow.getOutputList().isEmpty()) {
			OperationOutputFields outputFields = new OperationOutputFields();
			for (FilterProperties outPutField : mappingSheetRow.getOutputList()) {
				if (StringUtils.isNotBlank(outPutField.getPropertyname())) {
					GridRow schemaField = getSchemaField(outPutField.getPropertyname(), list);
					Field jaxbSchemaField = ExternalSchemaUtil.INSTANCE.convertGridRowToJaxbSchemaField(schemaField);
					outputFields.getField().add(jaxbSchemaField);
				}
			}
			operation.setOutputFields(outputFields);
		}
	}

	private void addOutputFieldsToJaxbExpression(MappingSheetRow mappingSheetRow, List<GridRow> schemaFieldList,
			Expression expression) {
		if (!mappingSheetRow.getOutputList().isEmpty()) {
			ExpressionOutputFields outputFields = new ExpressionOutputFields();
			for (FilterProperties outPutField : mappingSheetRow.getOutputList()) {
				if (StringUtils.isNotBlank(outPutField.getPropertyname())) {
					GridRow schemaField = getSchemaField(outPutField.getPropertyname(), schemaFieldList);
					Field jaxbSchemaField = ExternalSchemaUtil.INSTANCE.convertGridRowToJaxbSchemaField(schemaField);
					outputFields.setField(jaxbSchemaField);
				}
			}
			expression.setOutputFields(outputFields);
		}
	}

	private InputFields getInputFields(MappingSheetRow mappingSheetRow) {
		InputFields inputFields = null;
		if (!mappingSheetRow.getInputFields().isEmpty()) {
			inputFields = new InputFields();
			for (FilterProperties filterProperties : mappingSheetRow.getInputFields()) {
				InputField field = new InputField();
				field.setName(filterProperties.getPropertyname());
				field.setInSocketId(Constants.FIXED_INSOCKET_ID);
				inputFields.getField().add(field);
			}
		}
		return inputFields;
	}

	private GridRow getSchemaField(String outPutFieldName, List<GridRow> list) {
		for(GridRow schemaField:list){
			if(StringUtils.equalsIgnoreCase(schemaField.getFieldName(), outPutFieldName)){
				return schemaField;
			}
		}
		return ExternalSchemaUtil.INSTANCE.createSchemaGridRow(outPutFieldName);
	}

	
	
	private Object convertUiOutputFieldsToJaxb(TransformMapping transformMapping,String componentName) {
		ExternalMappings externalMappings=new ExternalMappings();
		ExternalMapping externalMapping = new ExternalMapping();
		externalMappings.setExternalMappings(externalMapping);
		for (NameValueProperty nameValueProperty : transformMapping.getMapAndPassthroughField()) {
			if (StringUtils.equalsIgnoreCase(nameValueProperty.getPropertyName(),
					nameValueProperty.getPropertyValue())) {
				if(transformMapping.isAllInputFieldsArePassthrough()){
					continue;
				}
				addPassthroughFieldsToJaxb(externalMapping, nameValueProperty);
			} else {
				addmapFieldsToJaxb(externalMapping, nameValueProperty);
			}
		}
		if(transformMapping.isAllInputFieldsArePassthrough()){
			addStarFieldsToJaxb(externalMapping);
		}
		for (MappingSheetRow mappingSheetRow : transformMapping.getMappingSheetRows()) {
			if (mappingSheetRow.isActive()) {
				if (mappingSheetRow.getOutputList().size() > 0
						&& StringUtils.isNotBlank(mappingSheetRow.getOutputList().get(0).getPropertyname())) {
					if(isNormalizeOuterExpression(transformMapping, componentName, mappingSheetRow)){
							continue;
					}
					if (mappingSheetRow.isExpression()) {
						addExpressionFieldTojaxb(externalMapping, mappingSheetRow);
					} else {
						for (FilterProperties opOutputField : mappingSheetRow.getOutputList()) {
							addOperationFieldToJaxb(externalMapping, mappingSheetRow, opOutputField);
						}
					}
				}
			}
		}
		return externalMappings;
	}

	private boolean isNormalizeOuterExpression(TransformMapping transformMapping, String componentName,
			MappingSheetRow mappingSheetRow) {
		if (Constants.NORMALIZE.equalsIgnoreCase(componentName)) {
			if ((transformMapping.isExpression() && mappingSheetRow.isExpression())
					|| (!transformMapping.isExpression() && !mappingSheetRow.isExpression())) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private void addStarFieldsToJaxb(ExternalMapping externalMapping) {
		InputField passThroughField=new InputField();
		passThroughField.setInSocketId(Constants.FIXED_INSOCKET_ID);
		passThroughField.setName(VALUE);
		externalMapping.getPassThroughFieldOrOperationFieldOrExpressionField().add(passThroughField);
	}

	private void addOperationFieldToJaxb(ExternalMapping field, MappingSheetRow mappingSheetRow,
			FilterProperties opOutputField) {
		OperationField operationField=new OperationField();
		operationField.setName(opOutputField.getPropertyname());
		operationField.setOperationId(mappingSheetRow.getOperationID());
		field.getPassThroughFieldOrOperationFieldOrExpressionField().add(operationField);
	}

	private void addExpressionFieldTojaxb(ExternalMapping field, MappingSheetRow mappingSheetRow) {
		ExpressionField expressionField = new ExpressionField();
		expressionField.setExpressionId(mappingSheetRow.getOperationID());
		expressionField.setName(mappingSheetRow.getOutputList().get(0).getPropertyname());
		field.getPassThroughFieldOrOperationFieldOrExpressionField().add(expressionField);
	}

	private void addmapFieldsToJaxb(ExternalMapping field, NameValueProperty nameValueProperty) {
		MapField mapField=new MapField();
		mapField.setInSocketId(Constants.FIXED_INSOCKET_ID);
		mapField.setName(nameValueProperty.getPropertyValue());
		mapField.setSourceName(nameValueProperty.getPropertyName());
		field.getPassThroughFieldOrOperationFieldOrExpressionField().add(mapField);
	}

	private void addPassthroughFieldsToJaxb(ExternalMapping field, NameValueProperty nameValueProperty) {
		InputField passThroughField=new InputField();
		passThroughField.setInSocketId(Constants.FIXED_INSOCKET_ID);
		passThroughField.setName(nameValueProperty.getPropertyName());
		field.getPassThroughFieldOrOperationFieldOrExpressionField().add(passThroughField);
	}
	
	public void marshal(Class<?> clazz, File file,Object object) throws JAXBException{
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(object, file);
		
	}
	
	private void convertToUIOperationField(TransformMapping transformMapping, ExternalMapping externalMapping, String componentName) {
		transformMapping.clear();
		for (Object field : externalMapping.getPassThroughFieldOrOperationFieldOrExpressionField()) {
			if (field instanceof InputField) {
				if(VALUE.equals(((InputField) field).getName())){
					transformMapping.setAllInputFieldsArePassthrough(true);
				}else{
					addPassThroughField((InputField) field, transformMapping);
				}
			} else if (field instanceof MapField) {
				addMapField((MapField) field, transformMapping);
			} else if (field instanceof OperationField) {
				if(Constants.NORMALIZE.equalsIgnoreCase(componentName) && transformMapping.isExpression()){
					throw new RuntimeException("Invalid XML");
				}
				addOperationField((OperationField) field, transformMapping, componentName);
			} else if (field instanceof ExpressionField) {
				if(Constants.NORMALIZE.equalsIgnoreCase(componentName) && !transformMapping.isExpression()){
					throw new RuntimeException("Invalid XML");
				}
				addExpressionField((ExpressionField) field, transformMapping, componentName);
			}
		}
	}

	private void addExpressionField(ExpressionField field, TransformMapping transformMapping, String componentName) {
		MappingSheetRow expression=getOperationOrExpression(field.getExpressionId(), transformMapping, true, componentName);
		FilterProperties expressionField = new FilterProperties(field.getName());
		expression.getOutputList().add(expressionField);
		transformMapping.getOutputFieldList().add(expressionField);
	}

	private void addOperationField(OperationField field, TransformMapping transformMapping, String componentName) {
		MappingSheetRow operation=getOperationOrExpression(field.getOperationId(), transformMapping, false, componentName);
		FilterProperties operationField = new FilterProperties(field.getName());
		operation.getOutputList().add(operationField);
		transformMapping.getOutputFieldList().add(operationField);
			}
	
	
	private MappingSheetRow getOperationOrExpression(String operationId, TransformMapping transformMapping,
			boolean isExpression, String componentName) {
		for (MappingSheetRow operationOrExpression : transformMapping.getMappingSheetRows()) {
			if (StringUtils.equalsIgnoreCase(operationId, operationOrExpression.getOperationID())) {
				return operationOrExpression;
			}
		}
		MappingSheetRow mappingSheetRow = new MappingSheetRow(new ArrayList<FilterProperties>(),
				new ArrayList<FilterProperties>(), "", "", false, operationId, new ArrayList<NameValueProperty>(),
				isExpression, new ExpressionEditorData("",componentName),
				new ExpressionEditorData("", componentName), true);
		mappingSheetRow.setAccumulator("");
		transformMapping.getMappingSheetRows().add(mappingSheetRow);
		return mappingSheetRow;
	}
	
	private void addMapField(MapField field, TransformMapping transformMapping) {
		NameValueProperty nameValueProperty=new NameValueProperty();
		nameValueProperty.setPropertyName(field.getSourceName());
		nameValueProperty.setPropertyValue(field.getName());
		nameValueProperty.getFilterProperty().setPropertyname(field.getName());
		transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());
		transformMapping.getMapAndPassthroughField().add(nameValueProperty);
	}
	private void addPassThroughField(InputField field, TransformMapping transformMapping) {
		NameValueProperty nameValueProperty=new NameValueProperty();
		nameValueProperty.setPropertyName(field.getName());
		nameValueProperty.setPropertyValue(field.getName());
		nameValueProperty.getFilterProperty().setPropertyname(field.getName());
		transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());
		transformMapping.getMapAndPassthroughField().add(nameValueProperty);
	}

	private MappingSheetRow convertToUIExpression(MappingSheetRow mappingSheetRow, ExternalExpression externalExpression,
			TransformMapping transformMapping, String componentName) throws ExternalTransformException{
		Expression expression=externalExpression.getExpression();
		if(mappingSheetRow==null){
			mappingSheetRow=getOperationOrExpression(expression.getId(), transformMapping,true, componentName);
		}
		mappingSheetRow.setOperationID(expression.getId());
		loadInputFieldsToMappingSheetRow(mappingSheetRow.getInputFields(),expression.getInputFields());
		loadExpressionOutputFieldToMappingSheetRow(mappingSheetRow.getOutputList(), expression.getOutputFields(),
				transformMapping.getOutputFieldList(), mappingSheetRow.getGridRows());
		if(StringUtils.isNotBlank(expression.getAccumulatorInitalValue())){
			mappingSheetRow.setAccumulator(expression.getAccumulatorInitalValue());
			setAccumulatorDatattype(expression.getAccumulatorInitalValue(),mappingSheetRow);
		}
		if(mappingSheetRow.getExpressionEditorData()!=null){
			updateExpressionEditorData(mappingSheetRow,mappingSheetRow.getExpressionEditorData(), expression.getExpr());
		}
		if(mappingSheetRow.getMergeExpressionDataForGroupCombine()!=null && StringUtils.isNotBlank(expression.getMergeExpr())){
			updateExpressionEditorData(mappingSheetRow,mappingSheetRow.getMergeExpressionDataForGroupCombine(), expression.getMergeExpr());
		}
		loadPropertiesToMappingSheetRow(mappingSheetRow,expression.getProperties());
		return mappingSheetRow;
	}

	private void updateExpressionEditorData(MappingSheetRow mappingSheetRow, ExpressionEditorData expressionEditorData,
			String expression) {
		if (expressionEditorData != null && StringUtils.isNotBlank(expression)) {
			expressionEditorData.setExpression(expression);
			mappingSheetRow.getInputFields().forEach(
					inputField -> expressionEditorData.getfieldsUsedInExpression().add(inputField.getPropertyname()));
		}
	}
	
	
	private void setAccumulatorDatattype(String accumulatorInitalValue,MappingSheetRow mappingSheetRow) {
		try{
			Integer.valueOf(accumulatorInitalValue);
			mappingSheetRow.setComboDataType(INTEGER);
		}catch(Exception e){
			try{
				new BigDecimal(accumulatorInitalValue);
				mappingSheetRow.setComboDataType(BIGDECIMAL);
				}
			catch(Exception exception){
				mappingSheetRow.setComboDataType(STRING);
			}
		}
	}

	private MappingSheetRow convertToUIOperation(MappingSheetRow mappingSheetRow, ExternalOperation externalOperation,
			TransformMapping transformMapping,String componentName) throws ExternalTransformException{
		Operation operation=externalOperation.getOperation();
		if(mappingSheetRow==null ){
			mappingSheetRow=getOperationOrExpression(operation.getId(), transformMapping, false, componentName);
		}
		mappingSheetRow.setOperationID(operation.getId());
		loadInputFieldsToMappingSheetRow(mappingSheetRow.getInputFields(),operation.getInputFields());
		loadOperationOutputFieldsToMappingSheetRow(mappingSheetRow.getOutputList(),operation.getOutputFields(),transformMapping.getOutputFieldList(),mappingSheetRow.getGridRows());
		mappingSheetRow.setOperationClassPath(operation.getClazz());
		mappingSheetRow.setComboBoxValue(getOperationClassName(operation.getClazz(),componentName));
		loadPropertiesToMappingSheetRow(mappingSheetRow,operation.getProperties());
		return mappingSheetRow;
	}
	
	private String getOperationClassName(String fullClassPath,String componentName ) {
		String operationClassName = CUSTOM;
		Operations operations = XMLConfigUtil.INSTANCE.getComponent(componentName).getOperations();
		List<TypeInfo> typeInfos = operations.getStdOperation();
		if (StringUtils.isNotBlank(fullClassPath) && !ParameterUtil.isParameter(fullClassPath)) {
			String str[] = fullClassPath.split("\\.");
			for (int i = 0; i < typeInfos.size(); i++) {
				if(typeInfos.get(i).getName().equalsIgnoreCase(str[str.length - 1]))
				{
					operationClassName = str[str.length - 1];
				}
			}
		}
		return operationClassName;
	}
	
	private void loadPropertiesToMappingSheetRow(MappingSheetRow mappingSheetRow, Properties properties) {
		mappingSheetRow.getNameValueProperty().clear();
		if(properties!=null){
		for(Property property:properties.getProperty()){
			NameValueProperty nameValueProperty=new NameValueProperty();
			nameValueProperty.setPropertyName(property.getName());
			nameValueProperty.setPropertyValue(property.getValue());
			mappingSheetRow.getNameValueProperty().add(nameValueProperty);
		}
		}
	}

	private void loadOperationOutputFieldsToMappingSheetRow(List<FilterProperties> outputList,
			OperationOutputFields operationOutputFields, List<FilterProperties> combinedOutputList,
			List<GridRow> mappingSheetRowSchemaFields) {
		mappingSheetRowSchemaFields.clear();
		if (operationOutputFields != null && operationOutputFields.getField() != null) {
			for (Field field : operationOutputFields.getField()) {
				FilterProperties filterProperties = new FilterProperties();
				filterProperties.setPropertyname(field.getName());
				if (!outputList.contains(filterProperties)) {
					outputList.add(filterProperties);
					combinedOutputList.add(filterProperties);
					mappingSheetRowSchemaFields.add(SchemaFieldUtil.INSTANCE.getBasicSchemaGridRow(field));
				}else{
					mappingSheetRowSchemaFields.add(SchemaFieldUtil.INSTANCE.getBasicSchemaGridRow(field));
				}
			}
		}
	}
    
	private void loadExpressionOutputFieldToMappingSheetRow(List<FilterProperties> outputList, ExpressionOutputFields expressionOutputFields,
			List<FilterProperties> combinedOutputList, List<GridRow> mappingSheetRowSchemaFields) {
		mappingSheetRowSchemaFields.clear();
		if(expressionOutputFields!=null && expressionOutputFields.getField()!=null){
				FilterProperties filterProperties=new FilterProperties();
				filterProperties.setPropertyname(expressionOutputFields.getField().getName());
					if(!outputList.isEmpty()){
						if(!outputList.contains(filterProperties)){
							outputList.forEach(expressionOutputField->expressionOutputField.setPropertyname(filterProperties.getPropertyname()));
							mappingSheetRowSchemaFields.add(SchemaFieldUtil.INSTANCE.getBasicSchemaGridRow(expressionOutputFields.getField()));
						}else{
							mappingSheetRowSchemaFields.add(SchemaFieldUtil.INSTANCE.getBasicSchemaGridRow(expressionOutputFields.getField()));
						}
					}else{
						outputList.add(filterProperties);
						combinedOutputList.add(filterProperties);
						mappingSheetRowSchemaFields.add(SchemaFieldUtil.INSTANCE.getBasicSchemaGridRow(expressionOutputFields.getField()));
					}
		}
	}
	
	private void loadInputFieldsToMappingSheetRow(List<FilterProperties> inputFields, InputFields operationInputFields) {
		inputFields.clear();
		if(operationInputFields!=null && operationInputFields.getField()!=null){
			for(InputField inputField:operationInputFields.getField()){
				FilterProperties filterProperties=new FilterProperties();
				filterProperties.setPropertyname(inputField.getName());
				inputFields.add(filterProperties);
			}
		}
	}

	/**
	 *Converts xml-stream into its equivalent jaxb object
	 * 
	 * @param xmlInputStream
	 * @param type
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws JAXBException
	 */
	public Object unmarshal(InputStream xmlInputStream, Class<?> type)
			throws ParserConfigurationException, SAXException, IOException, JAXBException {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setExpandEntityReferences(false);
			builderFactory.setNamespaceAware(true);
			builderFactory.setFeature(Constants.DISALLOW_DOCTYPE_DECLARATION, true);
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(xmlInputStream);
			JAXBContext jaxbContext = JAXBContext.newInstance(type);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return jaxbUnmarshaller.unmarshal(document);
	}
	
	
}
