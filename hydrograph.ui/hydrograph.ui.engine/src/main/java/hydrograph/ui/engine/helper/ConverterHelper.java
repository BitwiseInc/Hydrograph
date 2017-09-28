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

package hydrograph.ui.engine.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.FieldDataTypes;
import hydrograph.engine.jaxb.commontypes.ScaleTypeList;
import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeExpressionField;
import hydrograph.engine.jaxb.commontypes.TypeExpressionOutputFields;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeInputField;
import hydrograph.engine.jaxb.commontypes.TypeMapField;
import hydrograph.engine.jaxb.commontypes.TypeOperationField;
import hydrograph.engine.jaxb.commontypes.TypeOperationInputFields;
import hydrograph.engine.jaxb.commontypes.TypeOperationOutputFields;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeOutSocketAsInSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.commontypes.TypeTransformExpression;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.common.util.TransformMappingFeatureUtility;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.LookupMapProperty;
import hydrograph.ui.datastructure.property.LookupMappingGrid;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.ExternalWidgetData;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.engine.qnames.OperationsExpressionType;
import hydrograph.ui.engine.xpath.ComponentXpath;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
import hydrograph.ui.engine.xpath.ComponentsAttributeAndValue;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.Port;
import hydrograph.ui.graph.model.PortDetails;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * This is a helper class for converter implementation. Contains the helper methods for conversion.
 * @author Bitwise 
 */
public class ConverterHelper {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ConverterHelper.class);
	protected Map<String, Object> properties = new LinkedHashMap<String, Object>();
	protected Component component = null;
	protected String componentName = null;
	private static final String ID = "$id";
	
	public ConverterHelper(Component component) {
		this.component = component;
		this.properties = component.getProperties();
		this.componentName = (String) properties.get(Constants.PARAM_NAME);
	}

	public List<JAXBElement<?>> getOperationsOrExpression(TransformMapping transformPropertyGrid, List<BasicSchemaGridRow> schemaGridRows) {
		logger.debug("Generating TypeTransformOperation data :{}", properties.get(Constants.PARAM_NAME));
		List<JAXBElement<?>> operationList = new ArrayList<JAXBElement<?>>();
		if (transformPropertyGrid != null) {
			List<MappingSheetRow> mappingsheetRowList = TransformMappingFeatureUtility.INSTANCE.
					getActiveMappingSheetRow(transformPropertyGrid.getMappingSheetRows());
			if(Constants.NORMALIZE.equalsIgnoreCase(component.getComponentName()))
				mappingsheetRowList=filterMappigSheetRowList(mappingsheetRowList,transformPropertyGrid);
			if (mappingsheetRowList != null) {
				int OperationID = 0;
				for (MappingSheetRow mappingsheetRow : mappingsheetRowList) {
					if (!mappingsheetRow.isWholeOperationParameter()) {
						if (mappingsheetRow.isActive()) {
							if (mappingsheetRow.isExpression()) {
								if (mappingsheetRow.getExternalExpresion().isExternal()) {
									addExternalJaxbExpression(operationList, mappingsheetRow);
									continue;
								}
							} else {
								if (mappingsheetRow.getExternalOperation().isExternal()) {
									addExternalJaxbOperation(operationList, mappingsheetRow);
									continue;
								}
							}
						}
						JAXBElement<?> operation = getOperationOrExpression(mappingsheetRow, OperationID,
								schemaGridRows);
						if (operation != null) {
							operationList.add(operation);
							OperationID++;
						}

					} else {
						addWholeOperationParam(mappingsheetRow);
					}
				}
			}			
		}
		return operationList;
	}

	private void addExternalJaxbOperation(List<JAXBElement<?>> operationList, MappingSheetRow mappingsheetRow) {
		TypeExternalSchema typeExternalSchema = new TypeExternalSchema();
		typeExternalSchema.setUri("../"+mappingsheetRow.getExternalOperation().getFilePath());
		JAXBElement<TypeExternalSchema> externalOperation = new JAXBElement<TypeExternalSchema>(
				OperationsExpressionType.INCLUDE_EXTERNAL_OPERATION.getQName(), TypeExternalSchema.class,
				typeExternalSchema);
		operationList.add(externalOperation);
	}

	private void addExternalJaxbExpression(List<JAXBElement<?>> operationList, MappingSheetRow mappingsheetRow) {
		TypeExternalSchema typeExternalSchema = new TypeExternalSchema();
		typeExternalSchema.setUri("../"+mappingsheetRow.getExternalExpresion().getFilePath());
		JAXBElement<TypeExternalSchema> externalExpression = new JAXBElement<TypeExternalSchema>(
				OperationsExpressionType.INCLUDE_EXTERNAL_EXPRESSION.getQName(), TypeExternalSchema.class,
				typeExternalSchema);
		operationList.add(externalExpression);
	}

	public void addExternalJaxbExpression(List<JAXBElement<?>> operationList,
			ExternalWidgetData externalExpressionData) {
		if (externalExpressionData != null) {
			TypeExternalSchema typeExternalSchema = new TypeExternalSchema();
			typeExternalSchema.setUri("../" + externalExpressionData.getFilePath());
			JAXBElement<TypeExternalSchema> externalExpression = new JAXBElement<TypeExternalSchema>(
					OperationsExpressionType.INCLUDE_EXTERNAL_EXPRESSION.getQName(), TypeExternalSchema.class, typeExternalSchema);
			operationList.add(externalExpression);
		}
	}
	
	public void addExternalJaxbOperation(List<JAXBElement<?>> operationList,
			ExternalWidgetData externalExpressionData) {
		if (externalExpressionData != null) {
			TypeExternalSchema typeExternalSchema = new TypeExternalSchema();
			typeExternalSchema.setUri("../" + externalExpressionData.getFilePath());
			JAXBElement<TypeExternalSchema> externalExpression = new JAXBElement<TypeExternalSchema>(
					OperationsExpressionType.INCLUDE_EXTERNAL_OPERATION.getQName(), TypeExternalSchema.class, typeExternalSchema);
			operationList.add(externalExpression);
		}
	}
	
	private List<MappingSheetRow> filterMappigSheetRowList(List<MappingSheetRow> mappingSheetRows,TransformMapping transformMapping) {
		List<MappingSheetRow> filterSheetRowList=new ArrayList<>();
 		for(MappingSheetRow mappingSheetRow:mappingSheetRows)
		{
			if((transformMapping.isExpression()&&mappingSheetRow.isExpression())
				||(!transformMapping.isExpression()&&!mappingSheetRow.isExpression()))
            filterSheetRowList.add(mappingSheetRow);
		}	
 		return filterSheetRowList;
	}

	private JAXBElement<?> getOperationOrExpression(MappingSheetRow mappingSheetRow, int operationID, List<BasicSchemaGridRow> schemaGridRows) {
		if (mappingSheetRow != null) {
			if (!mappingSheetRow.isExpression()) {
				
				TypeTransformOperation operation = new TypeTransformOperation();
				operation.setId(mappingSheetRow.getOperationID());
				operation.setInputFields(getOperationInputFields(mappingSheetRow));
				operation.setProperties(getOperationProperties(mappingSheetRow.getNameValueProperty()));
				operation.setOutputFields(getOperationOutputFields(mappingSheetRow, schemaGridRows));
				if (StringUtils.isNotBlank(mappingSheetRow.getOperationClassPath())) {
					operation.setClazz(mappingSheetRow.getOperationClassPath());
				}
				JAXBElement<TypeTransformOperation> jaxbElement =  new JAXBElement( 
						OperationsExpressionType.OPERATION.getQName(), TypeTransformOperation.class,operation);
				return jaxbElement;
			} else {
				TypeTransformExpression expression = new TypeTransformExpression();
				expression.setId(mappingSheetRow.getOperationID());
				expression.setInputFields(getExpressionInputFields(mappingSheetRow));
				expression.setProperties(getOperationProperties(mappingSheetRow.getNameValueProperty()));
				expression.setOutputFields(getExpressionOutputField(mappingSheetRow, schemaGridRows));
				if (Constants.AGGREGATE.equalsIgnoreCase(component.getComponentName())
						|| Constants.CUMULATE.equalsIgnoreCase(component.getComponentName())) {
					expression.setAccumulatorInitalValue(mappingSheetRow.getAccumulator());
				} else if (Constants.GROUP_COMBINE.equalsIgnoreCase(component.getComponentName())) {
					expression.setAccumulatorInitalValue(mappingSheetRow.getAccumulator());
					if (StringUtils
							.isNotBlank(mappingSheetRow.getMergeExpressionDataForGroupCombine().getExpression())) {
						expression
								.setMergeExpr(mappingSheetRow.getMergeExpressionDataForGroupCombine().getExpression());
					}
				}
				if (StringUtils.isNotBlank(mappingSheetRow.getExpressionEditorData().getExpression())) {
					expression.setExpr(mappingSheetRow.getExpressionEditorData().getExpression());
				}
				JAXBElement<TypeTransformExpression> jaxbElement =  new JAXBElement( 
						OperationsExpressionType.EXPRESSION.getQName(), TypeTransformExpression.class,expression);
				return jaxbElement;
			}
		}
		return null;
	}

    private TypeExpressionOutputFields getExpressionOutputField(MappingSheetRow mappingSheetRow, List<BasicSchemaGridRow> schemaGrid)
    {
    	TypeExpressionOutputFields typeExpressionOutputFields=new TypeExpressionOutputFields();
    	for(FilterProperties outputFieldName:mappingSheetRow.getOutputList())
    	{
    		for(GridRow gridRow : schemaGrid){
				if(gridRow.getFieldName().equals(outputFieldName.getPropertyname())){					

					typeExpressionOutputFields.setField(getSchemaGridTargetData(gridRow));
					break;
				}
			}
    	}	
    	
     return typeExpressionOutputFields;
    }
	public TypeProperties getOperationProperties(List<NameValueProperty> nameValueProperties) {
		TypeProperties properties=null;
		if(!nameValueProperties.isEmpty()){
			properties = new TypeProperties();
			for (NameValueProperty nameValueProperty : nameValueProperties) {
				Property property = new Property();
				property.setName(nameValueProperty.getPropertyName());
				property.setValue(nameValueProperty.getPropertyValue());
				properties.getProperty().add(property);
			}	

		}	
		return properties;
	}
    
	private TypeOperationInputFields getExpressionInputFields(MappingSheetRow mappingSheetRow) 
	{
		TypeOperationInputFields inputFields = null;
		if (mappingSheetRow != null)
		{		
		List<FilterProperties> expressionInputFields=new ArrayList<>
		(mappingSheetRow.getInputFields());
		if (!expressionInputFields.isEmpty()) 
		{
			inputFields = new TypeOperationInputFields();

			if (!hasAllFilterPropertiesAsParams(expressionInputFields)) {	
				for (FilterProperties field : expressionInputFields){
					if(!ParameterUtil.isParameter(field.getPropertyname())){
						TypeInputField typeInputField = new TypeInputField();
						typeInputField.setInSocketId(Constants.FIXED_INSOCKET_ID);
						typeInputField.setName(field.getPropertyname().trim());
						inputFields.getField().add(typeInputField);
					}else{
						addParamTag(ID, field.getPropertyname(),
								ComponentXpathConstants.TRANSFORM_INPUT_FIELDS.value().replace("$operationId", mappingSheetRow.getOperationID()), false);
					}
				}
			}else
			{
				StringBuffer parameterFieldNames = new StringBuffer();
				TypeInputField typeInputField = new TypeInputField();
				typeInputField.setInSocketId(Constants.FIXED_INSOCKET_ID);
				typeInputField.setName("");
				inputFields.getField().add(typeInputField);
				for (FilterProperties field : expressionInputFields){
					parameterFieldNames.append(field.getPropertyname().trim() + " ");
				}
				addParamTag(ID, parameterFieldNames.toString(),
						ComponentXpathConstants.TRANSFORM_INPUT_FIELDS.value().replace("$operationId", mappingSheetRow.getOperationID()), true);
			}
		}
		}
		return inputFields;
	}
	private TypeOperationInputFields getOperationInputFields(MappingSheetRow mappingSheetRow) {
		TypeOperationInputFields inputFields = null;
		if (mappingSheetRow != null && !mappingSheetRow.getInputFields().isEmpty()) {
			inputFields = new TypeOperationInputFields();

			if (!hasAllFilterPropertiesAsParams(mappingSheetRow.getInputFields())) {	
				for (FilterProperties operationField : mappingSheetRow.getInputFields()) {
					if(!ParameterUtil.isParameter(operationField.getPropertyname())){
						TypeInputField typeInputField = new TypeInputField();
						typeInputField.setInSocketId(Constants.FIXED_INSOCKET_ID);
						typeInputField.setName(operationField.getPropertyname().trim());
						inputFields.getField().add(typeInputField);
					}else{
						addParamTag(ID, operationField.getPropertyname(),
								ComponentXpathConstants.TRANSFORM_INPUT_FIELDS.value().replace("$operationId", mappingSheetRow.getOperationID()), false);
					}
				}
			}else{
				StringBuffer parameterFieldNames = new StringBuffer();
				TypeInputField typeInputField = new TypeInputField();
				typeInputField.setInSocketId(Constants.FIXED_INSOCKET_ID);
				typeInputField.setName("");
				inputFields.getField().add(typeInputField);
				for (FilterProperties operationField : mappingSheetRow.getInputFields()) {
					parameterFieldNames.append(operationField.getPropertyname().trim() + " ");
				}
				addParamTag(ID, parameterFieldNames.toString(),
						ComponentXpathConstants.TRANSFORM_INPUT_FIELDS.value().replace("$operationId", mappingSheetRow.getOperationID()), true);
			}
		}
		return inputFields;
	}

	private TypeOperationOutputFields getOperationOutputFields(MappingSheetRow mappingSheetRow, List<BasicSchemaGridRow> schemaGrid) {
		TypeOperationOutputFields outputFields = new TypeOperationOutputFields();
		if (mappingSheetRow != null) {
			if (!hasAllFilterPropertiesAsParams(mappingSheetRow.getOutputList())) {	
				for(FilterProperties outputFieldName : mappingSheetRow.getOutputList()){	
					if(!ParameterUtil.isParameter(outputFieldName.getPropertyname())){
						for(GridRow gridRow : schemaGrid){
							if(gridRow.getFieldName().equals(outputFieldName.getPropertyname())){					

								outputFields.getField().add(getSchemaGridTargetData(gridRow));
							}
						}
					}else{
						addParamTag(ID, outputFieldName.getPropertyname(),
								ComponentXpathConstants.TRANSFORM_OUTPUT_FIELDS.value().replace("$operationId", mappingSheetRow.getOperationID()), false);
					}
				}
			}else{
				StringBuffer parameterFieldNames = new StringBuffer();
				TypeBaseField typeBaseField = new TypeBaseField();
				typeBaseField.setName("");
				outputFields.getField().add(typeBaseField);

				for (FilterProperties operationField : mappingSheetRow.getOutputList()) {
					parameterFieldNames.append(operationField.getPropertyname().trim() + " ");
				}
				addParamTag(ID, parameterFieldNames.toString(),
						ComponentXpathConstants.TRANSFORM_OUTPUT_FIELDS.value().replace("$operationId", mappingSheetRow.getOperationID()), true);

			}
		}
		return outputFields;
	}


	/**
	 * 
	 * returns output socket
	 * 
	 * 
	 * @param transformMapping
	 * @param gridRows
	 * @return list of {@link TypeOperationsOutSocket}
	 */
	public List<TypeOperationsOutSocket> getOutSocket(TransformMapping transformMapping, List<BasicSchemaGridRow> gridRows) {
		logger.debug("Generating TypeOperationsOutSocket data for : {}", properties.get(Constants.PARAM_NAME));
		List<TypeOperationsOutSocket> outSocketList = new ArrayList<TypeOperationsOutSocket>();
		if (component.getSourceConnections() != null && !component.getSourceConnections().isEmpty()) {
			for (Link link : component.getSourceConnections()) {
				TypeOperationsOutSocket outSocket = new TypeOperationsOutSocket();
				setOutSocketProperties(outSocket, transformMapping, gridRows, link);
				outSocket.getOtherAttributes();
				outSocketList.add(outSocket);
			}
		}
		
		syncOutSocketWithSchemaTabList(outSocketList);
		
		return outSocketList;
	}

	private void syncOutSocketWithSchemaTabList(List<TypeOperationsOutSocket> outSocketList){
		Schema componenetSchema = (Schema)component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
		TransformMapping transformMapping=(TransformMapping)component.getProperties().get(Constants.OPERATION);
		if(outSocketList.size()==0 || componenetSchema==null){
			return;
		}
		List<Object> fields = outSocketList.get(0).getPassThroughFieldOrOperationFieldOrExpressionField();
		if(fields.size() > 0 && fields.get(0) instanceof TypeExternalSchema){
			return;
		}
		List<Object> newFieldList = new LinkedList<>();
		
		for(int index=0;index<componenetSchema.getGridRow().size();index++){
			newFieldList.add(getField(componenetSchema.getGridRow().get(index).getFieldName(),fields));
		}
		
		if(transformMapping!=null && transformMapping.isAllInputFieldsArePassthrough()){
			TypeInputField typeInputField=new TypeInputField();
			typeInputField.setInSocketId(Constants.FIXED_INSOCKET_ID);
			typeInputField.setName("*");
			newFieldList.add(typeInputField);
			
		}
		outSocketList.get(0).getPassThroughFieldOrOperationFieldOrExpressionField().clear();
		outSocketList.get(0).getPassThroughFieldOrOperationFieldOrExpressionField().addAll(newFieldList);
	}
	

	private Object getField(String fieldName, List<Object> fields) {
		Object fieldToReturn = null;
		for(Object field:fields){
			if(field instanceof TypeInputField){				
				if(StringUtils.equals(((TypeInputField)field).getName(), fieldName)){
					fieldToReturn = field;
					break;
				}
			}else if(field instanceof TypeMapField){
				if(StringUtils.equals(((TypeMapField)field).getName(), fieldName)){
					fieldToReturn = field;
					break;
				}
			}else if(field instanceof TypeOperationField){
				if(StringUtils.equals(((TypeOperationField)field).getName(), fieldName)){
					fieldToReturn = field;
					break;
				}
			}else if(field instanceof TypeExpressionField){
				if(StringUtils.equals(((TypeExpressionField)field).getName(), fieldName)){
					fieldToReturn = field;
					break;
				}
			}else if(field instanceof TypeExternalSchema){
				fieldToReturn = ((TypeExternalSchema)field).getUri();
				break;
			}
			
		}

		return fieldToReturn;
	}

	private void setOutSocketProperties(TypeOperationsOutSocket outSocket, TransformMapping transformMapping,
			List<BasicSchemaGridRow> gridRows, Link link) {
		
		
		outSocket.setId(link.getSourceTerminal());
		outSocket.setType(link.getSource().getPort(link.getSourceTerminal()).getPortType());
	
		if(transformMapping!=null && transformMapping.getExternalOutputFieldsData().isExternal()){
			TypeExternalSchema exter=new TypeExternalSchema();
			exter.setUri("../"+transformMapping.getExternalOutputFieldsData().getFilePath());
			outSocket.getPassThroughFieldOrOperationFieldOrExpressionField().add(exter);
			return;
		}
		
		if(transformMapping!=null && !transformMapping.isAllInputFieldsArePassthrough()){
			outSocket.getPassThroughFieldOrOperationFieldOrExpressionField().addAll(addPassThroughFields(transformMapping,gridRows));
		}
		outSocket.getPassThroughFieldOrOperationFieldOrExpressionField().addAll(addMapFields(transformMapping,gridRows));
		outSocket.getPassThroughFieldOrOperationFieldOrExpressionField().addAll(addOperationOrExpressionFields(transformMapping,gridRows));
		addMapFieldParams(transformMapping);
		addOutputFieldParams(transformMapping);
		if (outSocket.getPassThroughFieldOrOperationFieldOrExpressionField().isEmpty()&& (transformMapping !=null && !transformMapping.isAllInputFieldsArePassthrough())) {
			TypeOutSocketAsInSocket outSocketAsInsocket = new TypeOutSocketAsInSocket();
			outSocketAsInsocket.setInSocketId(link.getTargetTerminal());
			outSocket.setCopyOfInsocket(outSocketAsInsocket);

		}
	}
	private List<TypeInputField> addPassThroughFields(TransformMapping transformMapping, List<BasicSchemaGridRow> schemaGridRows) {
		List<TypeInputField> typeOperationFieldsList = new ArrayList<>();
		if (transformMapping != null) {	
			{
				for (NameValueProperty nameValueProperty : transformMapping.getMapAndPassthroughField()) {
					if (nameValueProperty.getPropertyName().trim().equals(nameValueProperty.getPropertyValue().trim())
							&& !(ParameterUtil.isParameter(nameValueProperty.getPropertyName().trim()))) {
						TypeInputField typeInputField = new TypeInputField();
						typeInputField.setInSocketId(Constants.FIXED_INSOCKET_ID);
						typeInputField.setName(nameValueProperty.getPropertyName().trim());
						typeOperationFieldsList.add(typeInputField);
					}

				}
			}
		}

		return typeOperationFieldsList;
	}

	private void addMapFieldParams(TransformMapping transformMapping) {
		if (transformMapping != null) {
			StringBuffer parameterFieldNames = new StringBuffer();		
			for (NameValueProperty nameValueProperty : transformMapping.getMapAndPassthroughField()) {

				if(ParameterUtil.isParameter(nameValueProperty.getPropertyName())){
					parameterFieldNames.append(nameValueProperty.getPropertyName().trim() + " ");
				}
			}
			addParamTag(ID, parameterFieldNames.toString(),
					ComponentXpathConstants.OPERATIONS_OUTSOCKET.value(), false);

		}
	}

	private void addWholeOperationParam(MappingSheetRow transformMapping) {
		if(transformMapping!= null && ParameterUtil.isParameter(transformMapping.getWholeOperationParameterValue())){
			addParamTag(ID, transformMapping.getWholeOperationParameterValue(),
					ComponentXpathConstants.TRANSFORM_OPERATION.value(), false);	
		}
	}

	private void addOutputFieldParams(TransformMapping transformMapping) {
		if (transformMapping != null) {	
			StringBuffer parameterFieldNames = new StringBuffer();

			for (FilterProperties filterProperty : transformMapping.getOutputFieldList()) {
				if(ParameterUtil.isParameter(filterProperty.getPropertyname())){
					parameterFieldNames.append(filterProperty.getPropertyname().trim() + " ");
				}

			}
			addParamTag(ID, parameterFieldNames.toString(),
					ComponentXpathConstants.OPERATIONS_OUTSOCKET.value(), false);

		}
	}

	public boolean hasAllLookupMapPropertiesAsParams(List<LookupMapProperty> lookupMapProperties){
		for (LookupMapProperty lookupMapProperty : lookupMapProperties) 
			if (!ParameterUtil.isParameter(lookupMapProperty.getSource_Field())) 
				return false;
		return true;
	}

	public boolean hasAllFilterPropertiesAsParams(List<FilterProperties> filterProperties){
		for (FilterProperties filterProperty : filterProperties) 
			if (!ParameterUtil.isParameter(filterProperty.getPropertyname())) 
				return false;
		return true;
	}

	public boolean hasAllStringsInListAsParams(List<String> componentOperationFields) {
		for (String fieldName : componentOperationFields)
			if (!ParameterUtil.isParameter(fieldName))
				return false;
		return true;
	}

	public boolean hasAllKeysAsParams(Map<String, String> secondaryKeyRow) {
		for (Entry<String, String> secondaryKeyRowEntry : secondaryKeyRow.entrySet())
			if (!ParameterUtil.isParameter(secondaryKeyRowEntry.getKey()))
				return false;
		return true;
	}

	public boolean hasAllStringsInArrayAsParams(String keys[]){
		for (String fieldName : keys) 
			if (!ParameterUtil.isParameter(fieldName)) 
				return false;
		return true;
	}


	private List<TypeMapField> addMapFields(TransformMapping transformMapping, List<BasicSchemaGridRow> gridRows) {
		List<TypeMapField> typeMapFieldList = new ArrayList<>();

		if (transformMapping != null) {

			for (NameValueProperty nameValueProperty : transformMapping.getMapAndPassthroughField()) {
				if (!nameValueProperty.getPropertyName().trim().equals(nameValueProperty.getPropertyValue().trim())) {
					TypeMapField mapField = new TypeMapField();
					mapField.setSourceName(nameValueProperty.getPropertyName().trim());
					mapField.setName(nameValueProperty.getPropertyValue().trim());
					mapField.setInSocketId(Constants.FIXED_INSOCKET_ID);
					typeMapFieldList.add(mapField);
				}

			}

		}

		return typeMapFieldList;
	}

	private List<Object> addOperationOrExpressionFields(TransformMapping transformMapping, List<BasicSchemaGridRow> gridRows) {
		List<Object> typeOperationFieldList = new ArrayList<>();

		if (transformMapping != null) {			
			for(MappingSheetRow operationRow : transformMapping.getMappingSheetRows()){				

				for(FilterProperties outputField : operationRow.getOutputList())
				{
					if(!operationRow.isExpression())
					{	
					if( !(ParameterUtil.isParameter(outputField.getPropertyname())))
					 {
						TypeOperationField typeOperationField = new TypeOperationField();
						typeOperationField.setName(outputField.getPropertyname());
						typeOperationField.setOperationId(operationRow.getOperationID());
					 	typeOperationFieldList.add(typeOperationField);	
					 }
					}
					else
					{
						TypeExpressionField typeExpressionField=new TypeExpressionField();
						typeExpressionField.setExpressionId(operationRow.getOperationID());
						typeExpressionField.setName(operationRow.getOutputList().get(0).getPropertyname());
						typeOperationFieldList.add(typeExpressionField);
					}	
				}


			}
		}

		return typeOperationFieldList;
	}

	/**
	 * 
	 * returns Input sockets
	 * 
	 * @return list of {@link TypeBaseInSocket}
	 */
	public List<TypeBaseInSocket> getInSocket() {
		logger.debug("Generating TypeBaseInSocket data for :{}", component.getProperties().get(Constants.PARAM_NAME));
		List<TypeBaseInSocket> inSocketsList = new ArrayList<>();
		if (component.getTargetConnections() != null || !component.getTargetConnections().isEmpty()) {
			for (Link link : component.getTargetConnections()) {
				TypeBaseInSocket inSocket = new TypeBaseInSocket();
				inSocket.setFromComponentId(link.getSource().getComponentId());
				inSocket.setFromSocketId(getFromSocketId(link));
				inSocket.setFromSocketType(link.getSource().getPorts().get(link.getSourceTerminal()).getPortType());
				inSocket.setId(link.getTargetTerminal());
				inSocket.setType(link.getTarget().getPort(link.getTargetTerminal()).getPortType());
				inSocket.getOtherAttributes();
				inSocketsList.add(inSocket);
			}
		}
		return inSocketsList;
	}

	/**
	 * returns Schema
	 * 
	 * @param {@link FixedWidthGridRow}
	 * @return {@link TypeBaseField}
	 */
	public TypeBaseField getFixedWidthTargetData(FixedWidthGridRow object) {
		TypeBaseField typeBaseField = new TypeBaseField();
		typeBaseField.setName(object.getFieldName());

		if (object.getDataTypeValue().equals(FieldDataTypes.JAVA_UTIL_DATE.value())
				&& !object.getDateFormat().trim().isEmpty())
			typeBaseField.setFormat(object.getDateFormat());

		if (!object.getScale().trim().isEmpty())
			typeBaseField.setScale(Integer.parseInt(object.getScale()));

		if (object.getDataTypeValue().equals(FieldDataTypes.JAVA_LANG_DOUBLE.value())
				|| object.getDataTypeValue().equals(FieldDataTypes.JAVA_MATH_BIG_DECIMAL.value())) {
			typeBaseField.setScaleType(ScaleTypeList.EXPLICIT);
			if (!object.getScale().trim().isEmpty())
				typeBaseField.setScale(Integer.parseInt(object.getScale()));
		}

		for (FieldDataTypes fieldDataType : FieldDataTypes.values()) {
			if (fieldDataType.value().equalsIgnoreCase(object.getDataTypeValue()))
				typeBaseField.setType(fieldDataType);
		}
		if (object.getLength() != null && !object.getLength().trim().isEmpty()) {
			typeBaseField.getOtherAttributes().put(new QName(Constants.LENGTH_QNAME), object.getLength());
		}
		if(!StringUtils.isEmpty(object.getDescription())){
			typeBaseField.setDescription(object.getDescription());
			}
		
		return typeBaseField;
	}
	
	/**
	 * returns Schema
	 * 
	 * @param {@link MixedSchemeGridRow}
	 * @return {@link TypeBaseField}
	 */	
	public TypeBaseField getFileMixedSchemeTargetData(FixedWidthGridRow object) {
		TypeBaseField typeBaseField = new TypeBaseField();
		typeBaseField.setName(object.getFieldName());

		if (object.getDataTypeValue().equals(FieldDataTypes.JAVA_UTIL_DATE.value())
				&& !object.getDateFormat().trim().isEmpty())
			typeBaseField.setFormat(object.getDateFormat());

		if (!object.getScale().trim().isEmpty())
			typeBaseField.setScale(Integer.parseInt(object.getScale()));

		if (object.getDataTypeValue().equals(FieldDataTypes.JAVA_LANG_DOUBLE.value())
				|| object.getDataTypeValue().equals(FieldDataTypes.JAVA_MATH_BIG_DECIMAL.value())) {
			typeBaseField.setScaleType(ScaleTypeList.EXPLICIT);
			if (!object.getScale().trim().isEmpty())
				typeBaseField.setScale(Integer.parseInt(object.getScale()));
		}

		for (FieldDataTypes fieldDataType : FieldDataTypes.values()) {
			if (fieldDataType.value().equalsIgnoreCase(object.getDataTypeValue()))
				typeBaseField.setType(fieldDataType);
		}
		if (object.getLength() != null && !object.getLength().trim().isEmpty()) {
			typeBaseField.getOtherAttributes().put(new QName(Constants.LENGTH_QNAME), object.getLength());
		}
		
		if (object.getDelimiter() != null && !object.getDelimiter().isEmpty()) {
			typeBaseField.getOtherAttributes().put(new QName(Constants.DELIMITER_QNAME), object.getDelimiter());
		}	
		if(!StringUtils.isEmpty(object.getDescription())){
			typeBaseField.setDescription(object.getDescription());
			}
		return typeBaseField;
	}	

	/**
	 * returns Schema
	 * 
	 * @param {@link GridRow}
	 * @return {@link TypeBaseField}
	 */
	public TypeBaseField getSchemaGridTargetData(GridRow object) {
		TypeBaseField typeBaseField = new TypeBaseField();
		typeBaseField.setName(object.getFieldName());
		if (object.getDataTypeValue().equals(FieldDataTypes.JAVA_UTIL_DATE.value())
				&& !object.getDateFormat().trim().isEmpty())
			typeBaseField.setFormat(object.getDateFormat());


		if (object.getDataTypeValue().equals(FieldDataTypes.JAVA_MATH_BIG_DECIMAL.value())) {
			
			if (!object.getScale().trim().isEmpty())
				typeBaseField.setScale(Integer.parseInt(object.getScale()));
			
			for (ScaleTypeList scaleType : ScaleTypeList.values()) {
				if (scaleType.value().equalsIgnoreCase(object.getScaleTypeValue())){
					typeBaseField.setScaleType(scaleType);
					break;
				}
			}
			
			if (!object.getPrecision().trim().isEmpty())
				typeBaseField.setPrecision(Integer.parseInt(object.getPrecision()));
				
		}
			
		for (FieldDataTypes fieldDataType : FieldDataTypes.values()) {
			if (fieldDataType.value().equalsIgnoreCase(object.getDataTypeValue()))
				typeBaseField.setType(fieldDataType);
		}
		
		if(!StringUtils.isEmpty(object.getDescription())){
			typeBaseField.setDescription(object.getDescription());
		}
		return typeBaseField;
	}

	/**
	 * returns mapping rows list
	 * 
	 * @param lookupPropertyGrid
	 * @return {@link Object}
	 */
	public List<Object> getLookuporJoinOutputMaping(LookupMappingGrid lookupPropertyGrid) {
		List<Object> passThroughFieldorMapFieldList = null;
		if (lookupPropertyGrid != null) {
			passThroughFieldorMapFieldList = new ArrayList<>();
			TypeInputField typeInputField = null;
			TypeMapField mapField = null;
			if (!hasAllLookupMapPropertiesAsParams(lookupPropertyGrid.getLookupMapProperties())) {
				for (LookupMapProperty entry : lookupPropertyGrid.getLookupMapProperties()) {
					if(!ParameterUtil.isParameter(entry.getSource_Field())){
						if(StringUtils.isBlank(entry.getSource_Field())){
							continue;
						}
						String[] sourceNameValue = entry.getSource_Field().split(Pattern.quote("."));

						if(sourceNameValue.length == 2){
							if (sourceNameValue[1].equalsIgnoreCase(entry.getOutput_Field())) {
								typeInputField = new TypeInputField();
								typeInputField.setName(sourceNameValue[1]);
								typeInputField.setInSocketId(sourceNameValue[0]);
								passThroughFieldorMapFieldList.add(typeInputField);
							} else {
								mapField = new TypeMapField();
								mapField.setSourceName(sourceNameValue[1]);
								mapField.setName(entry.getOutput_Field());
								mapField.setInSocketId(sourceNameValue[0]);
								passThroughFieldorMapFieldList.add(mapField);
							}
						}
					}else{
						addParamTag(this.ID, entry.getSource_Field(),
								ComponentXpathConstants.OPERATIONS_OUTSOCKET.value(), false);
					}

				}
			}else{

				StringBuffer parameterFieldNames = new StringBuffer();
				TypeInputField inputField = new TypeInputField();
				inputField.setName("");
				inputField.setInSocketId("");
				passThroughFieldorMapFieldList.add(inputField);
				for (LookupMapProperty lookupMapProperty : lookupPropertyGrid.getLookupMapProperties())
					parameterFieldNames.append(lookupMapProperty.getOutput_Field() + " ");
				addParamTag(this.ID, parameterFieldNames.toString(),
						ComponentXpathConstants.OPERATIONS_OUTSOCKET.value(), true);
			}
		}
		return passThroughFieldorMapFieldList;
	}


	/**
	 * 
	 * returns true if multiple links allowed at given component and at given port
	 * 
	 * @param sourceComponent
	 * @param portName
	 * @return
	 */
	public static boolean isMultipleLinkAllowed(Component sourceComponent, String portName) {
		logger.debug("Getting port specification for port" + portName);
		for (PortDetails portDetails : sourceComponent.getPortDetails()) {
			for( Port port: portDetails.getPorts().values()){
				if(port.getTerminal().equals(portName)){
					return port.isAllowMultipleLinks();
				}
			}
		}
		return false;
	}

	public String getFromSocketId(Link link) {
		String inSocketId = link.getSourceTerminal();

		if (isMultipleLinkAllowed(link.getSource(), link.getSourceTerminal()))
			inSocketId = link.getSource().getPort(link.getSourceTerminal()).getPortType() + link.getLinkNumber();
		if (link.getSource().getComponentName().equals("InputSubjobComponent")) {
			return inSocketId.replace(Constants.OUTPUT_SOCKET_TYPE, Constants.INPUT_SOCKET_TYPE);
		}
		return inSocketId;

	}

	public void addParamTag(String ID, String fieldName, String paramXpath, boolean hasEmptyNode) {
		ComponentsAttributeAndValue tempAndValue=ComponentXpath.INSTANCE.getXpathMap().get(paramXpath.replace(ID, componentName));
		if(tempAndValue==null)
			ComponentXpath.INSTANCE.getXpathMap().put(
					(paramXpath.replace(ID, componentName)),
					new ComponentsAttributeAndValue(true,fieldName,hasEmptyNode));
		else
			tempAndValue.setNewNodeText(tempAndValue.getNewNodeText()+" "+fieldName);
	}
}
