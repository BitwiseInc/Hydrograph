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

package hydrograph.ui.engine.ui.converter;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeExpressionField;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeInputField;
import hydrograph.engine.jaxb.commontypes.TypeMapField;
import hydrograph.engine.jaxb.commontypes.TypeOperationField;
import hydrograph.engine.jaxb.commontypes.TypeOperationInputFields;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.commontypes.TypeTransformExpression;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;
import hydrograph.engine.jaxb.operationstypes.Normalize;
import hydrograph.ui.common.component.config.Operations;
import hydrograph.ui.common.component.config.TypeInfo;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ExternalOperationExpressionUtil;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.PortTypeEnum;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.schema.propagation.helper.SchemaPropagationHelper;
/**
 * The class TransformUiConverter
 * 
 * @author Bitwise
 * 
 */
public abstract class TransformUiConverter extends UiConverter {
	public static final String STAR_PROPERTY = "*";
	private static final Logger LOGGER = LogFactory.INSTANCE
			.getLogger(TransformUiConverter.class);

	/**
	 * Generate common properties of transform-component that will appear in
	 * property window.
	 * 
	 */
	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching common properties for -{}", componentName);
		getInPort((TypeOperationsComponent) typeBaseComponent);
		getOutPort((TypeOperationsComponent) typeBaseComponent);
		uiComponent.setCategory(UIComponentsConstants.TRANSFORM_CATEGORY
				.value());
		propertyMap.put(PropertyNameConstants.RUNTIME_PROPERTIES.value(),
				getRuntimeProperties());
	}

	/**
	 * Create input ports for transform component.
	 * 
	 * @param TypeOperationsComponent
	 *            the operationsComponent
	 * 
	 */
	protected void getInPort(TypeOperationsComponent operationsComponent) {
		LOGGER.debug("Generating InPut Ports for -{}", componentName);
		if (operationsComponent.getInSocket() != null) {
			for (TypeBaseInSocket inSocket : operationsComponent.getInSocket()) {
				uiComponent.engageInputPort(inSocket.getId());
				currentRepository.getComponentLinkList().add(
						new LinkingData(inSocket.getFromComponentId(),
								operationsComponent.getId(), inSocket
										.getFromSocketId(), inSocket.getId()));
				uiComponent.getPorts().get(inSocket.getId()).setPortType(PortTypeEnum.IN);
			}
		}
	}

	/**
	 * Create output ports for transform component.
	 * 
	 * @param TypeOperationsComponent
	 *            the operationsComponent
	 * 
	 */
	protected void getOutPort(TypeOperationsComponent operationsComponent) {
		LOGGER.debug("Generating OutPut Ports for -{}", componentName);
		if (operationsComponent.getOutSocket() != null) {
			for (TypeOperationsOutSocket outSocket : operationsComponent
					.getOutSocket()) {
				uiComponent.engageOutputPort(outSocket.getId());
				if (outSocket.getPassThroughFieldOrOperationFieldOrExpressionField() != null){
					propertyMap.put(Constants.PARAM_OPERATION,getUiPassThroughOrOperationFieldsOrMapFieldGrid(outSocket));
					createPassThroughAndMappingFieldsForSchemaPropagation(outSocket);
					}
				}

		}
	}

	/**
	 * Create transform property grid for transform component.
	 * 
	 * @param TypeOperationsOutSocket
	 *            the outSocket
	 * 
	 * @return TransformPropertyGrid, transformPropertyGrid object which is
	 *         responsible to display transform grid.
	 */
	protected TransformMapping getUiPassThroughOrOperationFieldsOrMapFieldGrid(
			TypeOperationsOutSocket outSocket) {
		TransformMapping atMapping = new TransformMapping();
		validateAndConvertOperationField(atMapping, outSocket, uiComponent.getComponentName());
		return atMapping;

	}
	
	private void validateAndConvertOperationField(TransformMapping transformMapping, TypeOperationsOutSocket externalMapping, String componentName) {
		transformMapping.clear();
		for (Object field : externalMapping.getPassThroughFieldOrOperationFieldOrExpressionField()) {
			if (field instanceof TypeInputField) {
				if(STAR_PROPERTY.equals(((TypeInputField) field).getName())){
					transformMapping.setAllInputFieldsArePassthrough(true);
				}else{
					addPassThroughField((TypeInputField) field, transformMapping);
				}
			} else if (field instanceof TypeMapField) {
				addMapField((TypeMapField) field, transformMapping);
			} else if (field instanceof TypeOperationField) {
				addOperationField((TypeOperationField)field, transformMapping, componentName);
			} else if (field instanceof TypeExpressionField) {
				addExpressionField((TypeExpressionField) field, transformMapping, componentName);
			}else if(field instanceof TypeExternalSchema){
				String filePath = StringUtils.replace(((TypeExternalSchema)field).getUri(), "../", "");
				 IWorkspace workspace = ResourcesPlugin.getWorkspace();
				 IPath relativePath=null;
					 relativePath=workspace.getRoot().getFile(new Path(filePath)).getLocation();
				ExternalOperationExpressionUtil.INSTANCE.importOutputFields(relativePath.toFile(), transformMapping, false,Constants.TRANSFORM_DISPLAYNAME);
				transformMapping.getExternalOutputFieldsData().setExternal(true);
				transformMapping.getExternalOutputFieldsData().setFilePath(filePath);
			}
		}
	}
	private void addExpressionField(TypeExpressionField field, TransformMapping transformMapping, String componentName) {
		MappingSheetRow expression=ConverterUiHelper.getOperationOrExpression(field.getExpressionId(), transformMapping, true, componentName);
		FilterProperties expressionField = new FilterProperties(field.getName());
		expression.getOutputList().add(expressionField);
		transformMapping.getOutputFieldList().add(expressionField);
	}

	
	private void addOperationField(TypeOperationField field, TransformMapping transformMapping, String componentName) {
		MappingSheetRow operation=ConverterUiHelper.getOperationOrExpression(field.getOperationId(), transformMapping, false, componentName);
		FilterProperties operationField = new FilterProperties(field.getName());
		operation.getOutputList().add(operationField);
		transformMapping.getOutputFieldList().add(operationField);
			}
	private void addMapField(TypeMapField field, TransformMapping transformMapping) {
		NameValueProperty nameValueProperty=new NameValueProperty();
		nameValueProperty.setPropertyName(field.getSourceName());
		nameValueProperty.setPropertyValue(field.getName());
		nameValueProperty.getFilterProperty().setPropertyname(field.getName());
		transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());
		transformMapping.getMapAndPassthroughField().add(nameValueProperty);
	}
	private void addPassThroughField(TypeInputField field, TransformMapping transformMapping) {
		NameValueProperty nameValueProperty=new NameValueProperty();
		nameValueProperty.setPropertyName(field.getName());
		nameValueProperty.setPropertyValue(field.getName());
		nameValueProperty.getFilterProperty().setPropertyname(field.getName());
		transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());
		transformMapping.getMapAndPassthroughField().add(nameValueProperty);
	}

	private void getOperationData(TransformMapping atMapping) 
	{
		List<JAXBElement<?>> typeTransformOpertaionList = ((TypeOperationsComponent) typeBaseComponent)
				.getOperationOrExpressionOrIncludeExternalOperation();

		List<MappingSheetRow> mappingSheetRows = atMapping
				.getMappingSheetRows();
		for (Object item : typeTransformOpertaionList) 
		{
			if (((JAXBElement<?>) item).getValue() instanceof TypeExternalSchema) {
				MappingSheetRow mappingSheetRow;
				TypeExternalSchema typeExternalSchema = (TypeExternalSchema) ((JAXBElement<?>) item).getValue();
				String filePath = typeExternalSchema.getUri();
				filePath = StringUtils.replace(filePath, "../","");
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				 IPath relativePath=null;
					 relativePath=workspace.getRoot().getFile(new Path(filePath)).getLocation();
					 if(StringUtils.equals("includeExternalExpression", ((JAXBElement<?>) item).getName().getLocalPart())){
							mappingSheetRow = ExternalOperationExpressionUtil.INSTANCE.importExpression(new File(relativePath.toString()), null, false, atMapping, uiComponent.getComponentName());
							mappingSheetRow.getExternalExpresion().setExternal(true);
							mappingSheetRow.getExternalExpresion().setFilePath(filePath);
							} else {
								mappingSheetRow = ExternalOperationExpressionUtil.INSTANCE.importOperation(new File(relativePath.toString()), null, false, atMapping, uiComponent.getComponentName());
								mappingSheetRow.getExternalOperation().setExternal(true);
								mappingSheetRow.getExternalOperation().setFilePath(filePath);
							} 
					 mappingSheetRow.getGridRows().forEach(gridRow->{
						 createPropagationDataForOperationFileds(gridRow);
					 });
			}
			
			else if(((JAXBElement)item).getValue() instanceof TypeTransformOperation)
			{
				if(Constants.NORMALIZE.equalsIgnoreCase(uiComponent.getComponentName()))
				atMapping.setExpression(false);	
				TypeTransformOperation transformOperation=(TypeTransformOperation)((JAXBElement)item).getValue();
				MappingSheetRow operationMappingSheetRow = ConverterUiHelper.getOperationOrExpression(transformOperation.getId(), atMapping, false, uiComponent.getComponentName());
				operationMappingSheetRow.getInputFields().addAll(getInputFieldList(transformOperation));operationMappingSheetRow.getOutputList();
				operationMappingSheetRow.getOutputList().clear();
				operationMappingSheetRow.getOutputList().addAll(getOutputFieldList(transformOperation));
				operationMappingSheetRow.setComboBoxValue((getOperationClassName(transformOperation.getClazz())));
				operationMappingSheetRow.setOperationClassPath(transformOperation.getClazz());
				operationMappingSheetRow.setClassParameter(ParameterUtil.isParameter(transformOperation.getClazz()));
				operationMappingSheetRow.setNameValueProperty(getProperties(transformOperation));
             }
			else if(((JAXBElement)item).getValue() instanceof TypeTransformExpression)
			{
					
				TypeTransformExpression transformExpression=(TypeTransformExpression)((JAXBElement)item).getValue();
				MappingSheetRow mappingSheetRow;
				if(Constants.GROUP_COMBINE.equalsIgnoreCase(uiComponent.getComponentName()))
					{
					mappingSheetRow=ConverterUiHelper.getOperationOrExpression(transformExpression.getId(), atMapping, true, uiComponent.getComponentName());
					mappingSheetRow.getInputFields().addAll(getInputFieldList(transformExpression));
					mappingSheetRow.getOutputList().clear();
					mappingSheetRow.getOutputList().addAll(getOutputFieldList(transformExpression));
					mappingSheetRow.getNameValueProperty().addAll(getProperties(transformExpression));
					mappingSheetRow.setExpressionEditorData(getExpressionEditorData(transformExpression));
					mappingSheetRow.setMergeExpressionDataForGroupCombine(getMergeExpressionEditorData(transformExpression));
				}
				else{
					 mappingSheetRow=ConverterUiHelper.getOperationOrExpression(transformExpression.getId(), atMapping, true, uiComponent.getComponentName());
					 mappingSheetRow.getInputFields().addAll(getInputFieldList(transformExpression));
					 mappingSheetRow.getOutputList().clear();
					 mappingSheetRow.getOutputList().addAll(getOutputFieldList(transformExpression));
					 mappingSheetRow.setExpressionEditorData(getExpressionEditorData(transformExpression));					 
				}
				
				if(Constants.NORMALIZE.equalsIgnoreCase(uiComponent.getComponentName())){	
				 mappingSheetRow.getExpressionEditorData().getExtraFieldDatatypeMap().put(Constants._INDEX,java.lang.Integer.class);
				 atMapping.setExpression(true);
				}
				else if(Constants.AGGREGATE.equalsIgnoreCase(uiComponent.getComponentName())
				  ||Constants.CUMULATE.equalsIgnoreCase(uiComponent.getComponentName())
				  ||Constants.GROUP_COMBINE.equalsIgnoreCase(uiComponent.getComponentName())
				 )
				{	
					mappingSheetRow.setAccumulator(transformExpression.getAccumulatorInitalValue());
					if(StringUtils.isBlank(transformExpression.getAccumulatorInitalValue()))
					{
					mappingSheetRow.setComboDataType(Messages.DATATYPE_INTEGER);
					}
					else if(transformExpression.getAccumulatorInitalValue().startsWith("@{") && 
							transformExpression.getAccumulatorInitalValue().
					endsWith("}")){
					mappingSheetRow.setAccumulatorParameter(true);
					mappingSheetRow.setComboDataType(Messages.DATATYPE_INTEGER);
					
					}else if(validateInteger(transformExpression.getAccumulatorInitalValue())){
					mappingSheetRow.setComboDataType(Messages.DATATYPE_INTEGER);
					mappingSheetRow.getExpressionEditorData().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE,
							java.lang.Integer.class);
						if(mappingSheetRow.getMergeExpressionDataForGroupCombine()!=null){
							mappingSheetRow.getMergeExpressionDataForGroupCombine().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE,
									java.lang.Integer.class);
							mappingSheetRow.getMergeExpressionDataForGroupCombine().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE_1,
									java.lang.Integer.class);
							mappingSheetRow.getMergeExpressionDataForGroupCombine().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE_2,
									java.lang.Integer.class);
						}
					}else if(validateBigDecimal(transformExpression.getAccumulatorInitalValue())){
					mappingSheetRow.setComboDataType(Messages.DATATYPE_BIGDECIMAL);
					mappingSheetRow.getExpressionEditorData().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE,
							java.math.BigDecimal.class);
						if(mappingSheetRow.getMergeExpressionDataForGroupCombine()!=null){
							mappingSheetRow.getMergeExpressionDataForGroupCombine().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE,
									java.math.BigDecimal.class);
							mappingSheetRow.getMergeExpressionDataForGroupCombine().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE_1,
									java.math.BigDecimal.class);
							mappingSheetRow.getMergeExpressionDataForGroupCombine().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE_2,
									java.math.BigDecimal.class);
						
						}
					}else{
					mappingSheetRow.setComboDataType(Messages.DATATYPE_STRING);
					mappingSheetRow.getExpressionEditorData().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE,
							java.lang.String.class);
						if(mappingSheetRow.getMergeExpressionDataForGroupCombine()!=null){
						mappingSheetRow.getMergeExpressionDataForGroupCombine().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE,
								java.lang.String.class);
						mappingSheetRow.getMergeExpressionDataForGroupCombine().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE_1,
								java.lang.String.class);
						mappingSheetRow.getMergeExpressionDataForGroupCombine().getExtraFieldDatatypeMap().put(Constants.ACCUMULATOR_VARIABLE_2,
								java.lang.String.class);
					
						}
					}
					
				}
			}	
		}
	}

	private ExpressionEditorData getMergeExpressionEditorData(TypeTransformExpression typeTransformExpression) {
		String expression;
		if(StringUtils.isBlank(typeTransformExpression.getMergeExpr())){
			expression="";
		}
		else{
			expression=typeTransformExpression.getMergeExpr();
		}
		ExpressionEditorData editorData=new ExpressionEditorData(expression,uiComponent.getComponentName());
		if(typeTransformExpression.getInputFields()!=null){
		for(TypeInputField inputField:typeTransformExpression.getInputFields().getField()){
			editorData.getfieldsUsedInExpression().add(inputField.getName());
			}
		}
		return editorData;
	}

	private boolean validateBigDecimal(String value){
		try{
			new BigDecimal(value);
			return true;
		}catch(Exception exception){
			return false;
		}
	}
	private boolean validateInteger(String value){
		try{
			Integer.valueOf(value);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	protected ExpressionEditorData getExpressionEditorData(TypeTransformExpression typeTransformExpression) {
		String expression;
		if(StringUtils.isBlank(typeTransformExpression.getExpr())){
			expression="";
		}
		else{
			expression=typeTransformExpression.getExpr();
		}
		ExpressionEditorData editorData=new ExpressionEditorData(expression,uiComponent.getComponentName());
		if(typeTransformExpression.getInputFields()!=null){
		for(TypeInputField inputField:typeTransformExpression.getInputFields().getField()){
			editorData.getfieldsUsedInExpression().add(inputField.getName());
			}
		}
		return editorData;
	}

	protected String getOperationClassName(String fullClassPath) {
		String operationClassName = Messages.CUSTOM;
		Operations operations = XMLConfigUtil.INSTANCE.getComponent(uiComponent.getComponentName()).getOperations();
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

	protected List<NameValueProperty> getProperties(Object item)
	{
		
		
		List<NameValueProperty> nameValueProperties=new LinkedList<>();
		
		TypeProperties typeProperties = null;
		if(item instanceof TypeTransformOperation)
		{
			TypeTransformOperation	typeTransformOperation=(TypeTransformOperation)item;
			typeProperties=typeTransformOperation.getProperties();
		}	
		else if(item instanceof TypeTransformExpression)
		{
			TypeTransformExpression	typeTransformExpression=(TypeTransformExpression)item;
			typeProperties=typeTransformExpression.getProperties();
		}	
		
		if(item!=null && typeProperties!=null)
		{
			for(Property property:typeProperties.getProperty())
			{
				NameValueProperty nameValueProperty=new NameValueProperty();
				nameValueProperty.setPropertyName(property.getName());
				nameValueProperty.setPropertyValue(property.getValue());
				nameValueProperties.add(nameValueProperty);
			}	
		}	
		return nameValueProperties;
	}
	
	
	
	private List<FilterProperties> getOutputFieldList(Object item) {
		List<FilterProperties> outputFieldList = new LinkedList<>();
		
		if(item !=null )
		{
		ConverterUiHelper converterUiHelper = new ConverterUiHelper(uiComponent);
		List<GridRow> gridRowList = new ArrayList<>();
		if(item instanceof TypeTransformOperation)
		{
			TypeTransformOperation typeTransformOperation=(TypeTransformOperation)item;
			if (typeTransformOperation.getOutputFields() != null) {
                for (TypeBaseField record : typeTransformOperation.getOutputFields().getField()) {
					GridRow gridRow=converterUiHelper.getFixedWidthSchema(record);
					gridRowList.add(gridRow);
					createPropagationDataForOperationFileds(gridRow);
					FilterProperties filterProperties=new FilterProperties();
					filterProperties.setPropertyname(record.getName());
					outputFieldList.add(filterProperties);
				}

			}
		}	
		else if(item instanceof TypeTransformExpression)
		{
			TypeTransformExpression	typeTransformExpression=(TypeTransformExpression)item;
			if (typeTransformExpression.getOutputFields() != null) 
			{
                    TypeBaseField record =typeTransformExpression.getOutputFields().getField();
                    if(record!=null){
					GridRow gridRow=converterUiHelper.getFixedWidthSchema(record);
					gridRowList.add(gridRow);
					createPropagationDataForOperationFileds(gridRow);
					FilterProperties filterProperties=new FilterProperties();
					filterProperties.setPropertyname(record.getName());
					outputFieldList.add(filterProperties);
			}}
		}	
		}
	
		return outputFieldList;
	}

	private void createPropagationDataForOperationFileds(GridRow gridRow) {
		ComponentsOutputSchema componentsOutputSchema = null;
		Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) propertyMap.get(Constants.SCHEMA_TO_PROPAGATE);
		if (schemaMap == null) {
			schemaMap = new LinkedHashMap<>();
			componentsOutputSchema = new ComponentsOutputSchema();
			schemaMap.put(Constants.FIXED_OUTSOCKET_ID, componentsOutputSchema);
			 propertyMap.put(Constants.SCHEMA_TO_PROPAGATE, schemaMap);
		} else if (schemaMap.get(Constants.FIXED_OUTSOCKET_ID) == null) {
			componentsOutputSchema = new ComponentsOutputSchema();
			schemaMap.put(Constants.FIXED_OUTSOCKET_ID, componentsOutputSchema);
		}
		componentsOutputSchema = schemaMap.get(Constants.FIXED_OUTSOCKET_ID);
		componentsOutputSchema.addSchemaFields(gridRow);
	}

	private List<FilterProperties> getInputFieldList(Object item) {
		 
		TypeOperationInputFields typeOperationInputFields = null;
		if(item instanceof TypeTransformOperation)
		{
			TypeTransformOperation	typeTransformOperation=(TypeTransformOperation)item;
			typeOperationInputFields=typeTransformOperation.getInputFields();
		}	
		else if(item instanceof TypeTransformExpression)
		{
			TypeTransformExpression	typeTransformExpression=(TypeTransformExpression)item;
			typeOperationInputFields=typeTransformExpression.getInputFields();
		}	
		List<FilterProperties> inputfieldList = new LinkedList<>();
		if(typeOperationInputFields!=null){
			for (TypeInputField inputField : typeOperationInputFields.getField()) {
				FilterProperties filterProperties=new FilterProperties();
				filterProperties.setPropertyname(inputField.getName());
				inputfieldList.add(filterProperties);
			}
		}		
		return inputfieldList;
	}
	
	
	
	private List<NameValueProperty> getMapAndPassThroughField(TransformMapping transformMapping)
	{
		List<TypeOperationsOutSocket> xsdOpertaionList = ((TypeOperationsComponent) typeBaseComponent)
				.getOutSocket();
		List<NameValueProperty> nameValueproperties=new LinkedList<>();
		for(TypeOperationsOutSocket typeOperationsOutSocket:xsdOpertaionList)
		{
			for (Object property : typeOperationsOutSocket
					.getPassThroughFieldOrOperationFieldOrExpressionField())
			{
				NameValueProperty nameValueProperty=new NameValueProperty();
				if(property instanceof TypeInputField)
				{
					if(STAR_PROPERTY.equals(((TypeInputField) property).getName())){
						transformMapping.setAllInputFieldsArePassthrough(true);
						continue;
					}
					nameValueProperty.setPropertyName(((TypeInputField) property).getName());
					nameValueProperty.setPropertyValue(((TypeInputField) property).getName());
					nameValueProperty.getFilterProperty().setPropertyname(((TypeInputField) property).getName());
					nameValueproperties.add(nameValueProperty);
				}
				else if(property instanceof TypeMapField )
				{
					nameValueProperty.setPropertyName(((TypeMapField) property).getSourceName());
					nameValueProperty.setPropertyValue(((TypeMapField) property).getName());
					nameValueProperty.getFilterProperty().setPropertyname(((TypeMapField) property).getName());
					nameValueproperties.add(nameValueProperty);
				}
				else if(property instanceof TypeExternalSchema){
					
				}
			}	
		}
		return nameValueproperties;
	}

	protected Object createTransformPropertyGrid() {
		TransformMapping atMapping = (TransformMapping) propertyMap.get(Constants.PARAM_OPERATION);
        getOperationData(atMapping);
        if(!atMapping.getExternalOutputFieldsData().isExternal()){
        	atMapping.setMapAndPassthroughField(getMapAndPassThroughField(atMapping));
        }
		if(Constants.NORMALIZE.equalsIgnoreCase(uiComponent.getComponentName()))
		intializeOutputRecordCount(atMapping);
		return atMapping;
	}

	private void intializeOutputRecordCount(TransformMapping atMapping) {
		Normalize normalize=(Normalize)typeBaseComponent;
		String expression;
		if(!atMapping.isExpression()){
			expression="";
		}else{
			expression=normalize.getOutputRecordCount().getValue();
		}
		ExpressionEditorData expressionEditorData=new ExpressionEditorData(expression,
				uiComponent.getComponentName());
		atMapping.setExpressionEditorData(expressionEditorData);
	}

	/**
	 * Generate runtime properties for component.
	 * 
	 * @return Map<String,String>
	 */
	
	@Override
	protected Map<String, String> getRuntimeProperties() {
		LOGGER.debug("Generating Runtime Properties for -{}", componentName);
		TreeMap<String, String> runtimeMap = null;
		TypeProperties typeProperties = ((TypeOperationsComponent) typeBaseComponent)
				.getRuntimeProperties();
		if (typeProperties != null) {
			runtimeMap = new TreeMap<>();
			for (Property runtimeProperty : typeProperties.getProperty()) {
				runtimeMap.put(runtimeProperty.getName(),
						runtimeProperty.getValue());
			}
		}
		return runtimeMap;
	}

	/**
	 * This method creates ComponentOutputSchema for pass-through and mapping fields of operation components. Also
	 * maintains sequence of pass-through , mapping and operation filed sequence as a temporary property within component.
	 * 
	 * @param operationsOutSockets
	 */
	protected void createPassThroughAndMappingFieldsForSchemaPropagation(TypeOperationsOutSocket operationsOutSockets) {
		final ComponentsOutputSchema componentsOutputSchema;
		List<String> schemaFieldSequence;
		Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) propertyMap
				.get(Constants.SCHEMA_TO_PROPAGATE);
		if (operationsOutSockets != null
				&& !operationsOutSockets.getPassThroughFieldOrOperationFieldOrExpressionField().isEmpty()) {
			componentsOutputSchema = new ComponentsOutputSchema();
			schemaFieldSequence = new ArrayList<>();
			for (Object property : operationsOutSockets.getPassThroughFieldOrOperationFieldOrExpressionField()) {

				if (property instanceof TypeInputField ) {
					if(!STAR_PROPERTY.equals(((TypeInputField) property).getName())){
						schemaFieldSequence.add(((TypeInputField) property).getName());
						componentsOutputSchema.getPassthroughFields().add(((TypeInputField) property).getName());
						componentsOutputSchema.addSchemaFields(SchemaPropagationHelper.INSTANCE
								.createFixedWidthGridRow(((TypeInputField) property).getName()));
					}
				} else if (property instanceof TypeMapField) {
					schemaFieldSequence.add(((TypeMapField) property).getName());
					componentsOutputSchema.getMapFields().put(((TypeMapField) property).getName(),
							((TypeMapField) property).getSourceName());
					componentsOutputSchema.addSchemaFields(SchemaPropagationHelper.INSTANCE
							.createFixedWidthGridRow(((TypeMapField) property).getName()));
				} else if (property instanceof TypeOperationField) {
					schemaFieldSequence.add(((TypeOperationField) property).getName());
				}
				 else if (property instanceof TypeExpressionField) 
				    {
						schemaFieldSequence.add(((TypeExpressionField) property).getName());
					}
				 else if(property instanceof TypeExternalSchema) {
					 TransformMapping atMapping = (TransformMapping) propertyMap.get(Constants.PARAM_OPERATION);
					 
					 atMapping.getMapAndPassthroughField().forEach(field->{
						 if(StringUtils.equals(field.getPropertyName(),field.getPropertyValue())){
							 //Its a pass through fields.
							 if(!STAR_PROPERTY.equals(field.getPropertyName())){
									schemaFieldSequence.add(field.getPropertyName());
									componentsOutputSchema.getPassthroughFields().add(field.getPropertyName());
									componentsOutputSchema.addSchemaFields(SchemaPropagationHelper.INSTANCE
											.createFixedWidthGridRow(field.getPropertyName()));
								}
							 
						 }else {
								schemaFieldSequence.add(field.getPropertyValue());
								componentsOutputSchema.getMapFields().put(field.getPropertyValue(),field.getPropertyName());
								componentsOutputSchema.addSchemaFields(SchemaPropagationHelper.INSTANCE.createFixedWidthGridRow(field.getPropertyValue()));
							} 
					 });
					atMapping.getMappingSheetRows().forEach(mappingRow -> {
						mappingRow.getOutputList().forEach(outputField -> {
							schemaFieldSequence.add(outputField.getPropertyname());
						});
					});
					;
				 }
			}
		}else{
			componentsOutputSchema = null;
			schemaFieldSequence = null;
		}
		if (schemaMap == null) {
			schemaMap = new LinkedHashMap<>();
		 }
		if(operationsOutSockets!=null){
		schemaMap.put(operationsOutSockets.getId(), componentsOutputSchema);
		}
		propertyMap.put(Constants.SCHEMA_TO_PROPAGATE, schemaMap);
		propertyMap.put(Constants.SCHEMA_FIELD_SEQUENCE, schemaFieldSequence);
	}
	
	protected void copySchemaFromInputPort(String inSocketId) {
		if(StringUtils.isNotBlank(inSocketId))
		propertyMap.put(Constants.COPY_FROM_INPUT_PORT_PROPERTY, inSocketId);
	}
}
