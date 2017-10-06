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

package hydrograph.ui.propertywindow.widgets.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.common.util.TransformMappingFeatureUtility;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.ErrorObject;
import hydrograph.ui.datastructure.property.mapping.InputField;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.schema.propagation.SchemaPropagation;
import hydrograph.ui.propertywindow.schema.propagation.helper.SchemaPropagationHelper;

/**
 * @author Bitwise
 *
 *helper class for output record count widget
 */
public class OutputRecordCountUtility {
	public static final OutputRecordCountUtility INSTANCE= new OutputRecordCountUtility();
	
	
	private GridRow getFieldSchema(String fieldName,Component component) 
	{
		List<GridRow> schemaGridRows = getInputFieldSchema(component);
		for (GridRow schemaGridRow : schemaGridRows) 
		{
			if (schemaGridRow.getFieldName().equals(fieldName)) 
			{
				return schemaGridRow;
			}
		}
		return null;
	}
	private List<GridRow> getInputFieldSchema(Component component) {
		ComponentsOutputSchema outputSchema = null;
		List<GridRow> schemaGridRows = new LinkedList<>();
		for (Link link : component.getTargetConnections()) 
		{
			outputSchema = SchemaPropagation.INSTANCE.getComponentsOutputSchema(link);
			if (outputSchema != null)
				for (GridRow row : outputSchema.getSchemaGridOutputFields(null)) 
				{
					schemaGridRows.add(row);
				}
		}
		return schemaGridRows;
	}
	private GridRow getCurrentSchemaField(String fieldName,Component component) 
	{
		Schema schema = (Schema) component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
		if (schema != null) {
			for (GridRow gridRow : schema.getGridRow()) {
				if(gridRow.getFieldName().equals(fieldName))
					return gridRow;
			}
		}
		return null;
	}
	private String getPortCount(String sourceTerminalId) {
		String portCount=null;
		if(StringUtils.startsWithIgnoreCase(sourceTerminalId, Constants.UNUSED_SOCKET_TYPE)){
			portCount=StringUtils.remove(sourceTerminalId, Constants.UNUSED_SOCKET_TYPE);
		}else if(StringUtils.startsWithIgnoreCase(sourceTerminalId, Constants.OUTPUT_SOCKET_TYPE)){
			portCount=StringUtils.remove(sourceTerminalId, Constants.OUTPUT_SOCKET_TYPE);
		}
		return portCount;
	}
    
	private void backwardJobComapatabilityCode(TransformMapping transformMapping)
    {
    		List<NameValueProperty> tempNameValuePropertyList=new ArrayList<>();
    		for(NameValueProperty nameValueProperty:transformMapping.getMapAndPassthroughField())
    		{
    			NameValueProperty newNameValueProperty=new NameValueProperty();
    			newNameValueProperty.setPropertyName(nameValueProperty.getPropertyName());
    			newNameValueProperty.setPropertyValue(nameValueProperty.getPropertyValue());
    			newNameValueProperty.getFilterProperty().setPropertyname(nameValueProperty.getPropertyValue());
    			tempNameValuePropertyList.add(newNameValueProperty);
    			transformMapping.getOutputFieldList().add(newNameValueProperty.getFilterProperty());
    		}	
    		transformMapping.getMapAndPassthroughField().clear();
    		transformMapping.getMapAndPassthroughField().addAll(tempNameValuePropertyList);
    		tempNameValuePropertyList.clear();
    }	
	
	/**
	 * Add Map field to internal schema object.
	 * @param mapFields
	 */
	public void addMapFieldsToSchema(Map<String, String> mapFields,Schema schemaData,Component component) 
	{
		BasicSchemaGridRow tempSchemaGridRow = null;
		Schema schema = schemaData;
		List<String> currentFieldsInProppogatedSchemaObject = new LinkedList<>();
		for (GridRow gridRow : schema.getGridRow()) {
			currentFieldsInProppogatedSchemaObject.add(gridRow.getFieldName());
		}

	  for (Map.Entry<String,String> entry: mapFields.entrySet()) 
	  {
			tempSchemaGridRow = (BasicSchemaGridRow) getFieldSchema(entry.getValue(),component);
			BasicSchemaGridRow schemaGridRow=null ;
			if (tempSchemaGridRow != null) {
				schemaGridRow= (BasicSchemaGridRow) tempSchemaGridRow.copy();
				schemaGridRow.setFieldName(entry.getKey());
			}
			else{
				schemaGridRow = SchemaPropagationHelper.INSTANCE.createSchemaGridRow(entry.getKey());
			}
				if (!currentFieldsInProppogatedSchemaObject.contains(entry.getKey()) && !schema.getGridRow().contains(schemaGridRow)) {
							schema.getGridRow().add(schemaGridRow);
				} else {
					for (int index = 0; index < schema.getGridRow().size(); index++) {
						if (schema.getGridRow().get(index).getFieldName().equals(entry.getKey())) {
							schema.getGridRow().set(index, schemaGridRow);
						}
					}
				}
			 
			
		}

	}
	/**
	 * Add Pass through field to schema 
	 * @param passThroughFields
	 */
	public void addPassthroughFieldsToSchema(List<String> passThroughFields,Schema schemaData,Component component) {
		Schema schema = schemaData;
		List<String> currentFieldsInProppogatedSchemaObject = new LinkedList<>();
		for (GridRow gridRow : schema.getGridRow()) {
			currentFieldsInProppogatedSchemaObject.add(gridRow.getFieldName());
		}

		for (String passThroughField : passThroughFields) {
			GridRow schemaGridRow= getFieldSchema(passThroughField,component);
			if(schemaGridRow!=null){
				BasicSchemaGridRow tempSchemaGrid =(BasicSchemaGridRow) schemaGridRow.copy();

				if (!currentFieldsInProppogatedSchemaObject.contains(passThroughField) && !schema.getGridRow().contains(tempSchemaGrid)) {
					schema.getGridRow().add(tempSchemaGrid);
				} else {
					for (int index = 0; index < schema.getGridRow().size(); index++) {
						if (schema.getGridRow().get(index).getFieldName().equals(passThroughField)) {
							schema.getGridRow().set(index, tempSchemaGrid);
						}
					}
				}
			}else{
				schema.getGridRow().add(SchemaPropagationHelper.INSTANCE.createSchemaGridRow(passThroughField));
			}
			}
	}
	/**
	 * 	Add Operation field to internal schema object
	 * 	@param operationFields
	 */
	public void addOperationFieldsToSchema(List<FilterProperties> operationFields,Schema schemaData,Component component) 
	{
		Schema schema = schemaData;
		GridRow schemaGridRow=null;
		List<String> currentFieldsInProppogatedSchemaObject = new LinkedList<>();
		for (GridRow gridRow : schema.getGridRow()) 
		{
			currentFieldsInProppogatedSchemaObject.add(gridRow.getFieldName());
		}

		for (FilterProperties operationField : operationFields) {
			if(!ParameterUtil.isParameter(operationField.getPropertyname())){

				if(getCurrentSchemaField(operationField.getPropertyname(),component)!=null){
					schemaGridRow=getCurrentSchemaField(operationField.getPropertyname(),component);
					schemaGridRow=schemaGridRow.copy();
				}
				else
					schemaGridRow = SchemaPropagationHelper.INSTANCE.createSchemaGridRow(operationField.getPropertyname());




				if (!currentFieldsInProppogatedSchemaObject.contains(operationField.getPropertyname()) && !schema.getGridRow().contains(schemaGridRow)) {
					schema.getGridRow().add(schemaGridRow);
				} else {
					for (int index = 0; index < schema.getGridRow().size(); index++) {
						if (schema.getGridRow().get(index).getFieldName().equals(operationField.getPropertyname())) {
							schema.getGridRow().set(index, schemaGridRow);

						}
					}
				}
			}
		}
	}
	
	
	/**
	 * @param component
	 * @return input schema fixed width row object
	 */
	public List<FixedWidthGridRow> getInputSchema(Component component) 
	{
		
		List<FixedWidthGridRow> fixedWidthSchemaGridRows=new ArrayList<>();
		for(Link link:component.getTargetConnections()){
			Schema previousComponentSchema=SubjobUtility.INSTANCE.getSchemaFromPreviousComponentSchema(component,link);
			
			if(previousComponentSchema!=null &&!previousComponentSchema.getGridRow().isEmpty())
			{
				fixedWidthSchemaGridRows=SchemaSyncUtility.INSTANCE.
						convertGridRowsSchemaToFixedSchemaGridRows(previousComponentSchema.getGridRow());
			}
			break;
		}
		return fixedWidthSchemaGridRows;
	}
	
	/**
	 * @param component
	 * @return list of filterproperties created from component schema object
	 */
	public List<FilterProperties> convertSchemaToFilterProperty(Component component)
	{
		List<FilterProperties> outputFileds = new ArrayList<>();
		Schema schema = (Schema) component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
		    if(schema==null)
		    {	
			 return outputFileds;  
		    }
			 for (GridRow gridRow : schema.getGridRow()) 
			 {
				FilterProperties filterProperty = new FilterProperties();
				filterProperty.setPropertyname(gridRow.getFieldName());
				outputFileds.add(filterProperty);
			 }
		return outputFileds;
	}
	
	/**
	 * @param mapFields
	 * @param passThroughFields
	 * @param component
	 * add pass through,map fields to componentOutputschema
	 */
	public void addPassthroughFieldsAndMappingFieldsToComponentOuputSchema(Map<String, String> mapFields,
			List<String> passThroughFields,Component component) 
    {
		ComponentsOutputSchema componentsOutputSchema = null;
		Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) component
				.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
		if (schemaMap != null && schemaMap.get(Constants.FIXED_OUTSOCKET_ID) != null)
		{	
			componentsOutputSchema = schemaMap.get(Constants.FIXED_OUTSOCKET_ID);
		}
		else {
			componentsOutputSchema = new ComponentsOutputSchema();
			schemaMap = new LinkedHashMap<>();
			schemaMap.put(Constants.FIXED_OUTSOCKET_ID, componentsOutputSchema);
		}
		component.getProperties().put(Constants.SCHEMA_TO_PROPAGATE, schemaMap);

		componentsOutputSchema.getPassthroughFields().clear();
		componentsOutputSchema.getMapFields().clear();
		componentsOutputSchema.getPassthroughFields().addAll(passThroughFields);
		componentsOutputSchema.getMapFields().putAll(mapFields);
	}
	
	/**
	 * @param nameValueProperties
	 * @return map<String,String>
	 */
	public  Map<String,String> getMapFields(List<NameValueProperty> nameValueProperties) 
	{
		Map<String,String> mapField = new LinkedHashMap<>();
		if (!nameValueProperties.isEmpty()) {

			for (NameValueProperty nameValueProperty : nameValueProperties) {
				if (!(nameValueProperty.getPropertyName().equals(
						nameValueProperty.getPropertyValue()))) {
					mapField.put(nameValueProperty.getPropertyValue(),nameValueProperty.getPropertyName());
				}
			}

		}
		return mapField;
	}
    /**
     * @param nameValueProperties
     * @return list of pass through field
     */
    public List<String> getPassThroughFields(
			List<NameValueProperty> nameValueProperties) 
			{
		List<String> passthroughField = new LinkedList<>();
		if (!nameValueProperties.isEmpty()) {

			for (NameValueProperty nameValueProperty : nameValueProperties) {
				if (nameValueProperty.getPropertyName().equals(
						nameValueProperty.getPropertyValue())) {
					passthroughField.add(nameValueProperty.getPropertyValue());
				}
			}

		}
		return passthroughField;
			}
	/**
	 * @param mappingSheetRow
	 * @return list of filter properties
	 */
	public List<FilterProperties> getOpeartionFields(MappingSheetRow mappingSheetRow) {
		List<FilterProperties> operationFields = new LinkedList<>();
		operationFields.addAll(mappingSheetRow.getOutputList());
		return operationFields;
	}
	
	/**
	 * @param transformMapping
	 * @param component 
	 * 
	 * populate input fields of mapping window
	 */
	public void getPropagatedSchema(TransformMapping transformMapping,Component component) {
		InputField inputField = null;
		transformMapping.getInputFields().clear();
		for (Link link : component.getTargetConnections()) {
			String sourceTerminalId=link.getSourceTerminal();
			List<BasicSchemaGridRow> basicSchemaGridRows=SchemaPropagationHelper.INSTANCE.
			getBasicSchemaGridRowList(Constants.INPUT_SOCKET_TYPE+getPortCount(sourceTerminalId)
					, link);
			if (basicSchemaGridRows != null){
				for (BasicSchemaGridRow row :basicSchemaGridRows ) {
					inputField = new InputField(row.getFieldName(), new ErrorObject(false, ""));
						transformMapping.getInputFields().add(inputField);
				}
			}
		}
	}
	/**
	 * @param transformMapping
	 * 
	 * add expression output field to mapping window output fields
	 */
	public void addExpressionOutputFieldToOuterListOfMappingWindow(TransformMapping transformMapping) 
	{
        	for(MappingSheetRow mappingSheetRow:transformMapping.getMappingSheetRows())	
        	{
        		if(transformMapping.isExpression()&&mappingSheetRow.isExpression()&&mappingSheetRow.isActive())
        		{
        			transformMapping.getOutputFieldList().addAll(mappingSheetRow.getOutputList());
        		}	
        	}
	}
	/**
	 * @param transformMapping
	 * add operation output fields to mapping window output fields
	 */
	public void addOperationOutputFieldToOuterListOfMappingWindow(TransformMapping transformMapping) 
	{
    	for(MappingSheetRow mappingSheetRow:transformMapping.getMappingSheetRows())	
    	{
    		if(!transformMapping.isExpression()&&!mappingSheetRow.isExpression()&&mappingSheetRow.isActive())
    		{
    			transformMapping.getOutputFieldList().addAll(mappingSheetRow.getOutputList());
    		}	
    	}
    }
	/**
	 * @param transformMapping
	 * @param component
	 * 
	 * create output fields of mapping window in case of import xml
	 */
	public void populateMappingOutputFieldIfTargetXmlImported(TransformMapping transformMapping,Component component) 
	{
		if(!transformMapping.getMappingSheetRows().isEmpty())
		{
			List<MappingSheetRow> activeMappingSheetRow=TransformMappingFeatureUtility.INSTANCE.
					getActiveMappingSheetRow(transformMapping.getMappingSheetRows());
			if(activeMappingSheetRow.size()==transformMapping.getMappingSheetRows().size())
			{
				for(MappingSheetRow mappingSheetRow:transformMapping.getMappingSheetRows())
			 	{  
					transformMapping.getOutputFieldList().addAll(mappingSheetRow.getOutputList());
			 	}
				if(!transformMapping.getMapAndPassthroughField().isEmpty()&&
			 			transformMapping.getMapAndPassthroughField().get(0).getFilterProperty()==null)
			 	{
			 		backwardJobComapatabilityCode(transformMapping);	
			 	}
				for(NameValueProperty nameValueProperty:transformMapping.getMapAndPassthroughField())
			 	{
			 		transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());
			 	}	
			 	List<FilterProperties> finalSortedList=SchemaSyncUtility.INSTANCE.
			 	sortOutputFieldToMatchSchemaSequence(OutputRecordCountUtility.INSTANCE.convertSchemaToFilterProperty(component), 
			 			transformMapping);
			 	transformMapping.getOutputFieldList().clear();
			 	transformMapping.getOutputFieldList().addAll(finalSortedList);
				
			}	
				
		}
		else if(!transformMapping.getMapAndPassthroughField().isEmpty()&&transformMapping.getOutputFieldList().isEmpty())
		{
			if(transformMapping.getMapAndPassthroughField().get(0).getFilterProperty()==null)
			{	
			backwardJobComapatabilityCode(transformMapping);	
			}
			for(NameValueProperty nameValueProperty:transformMapping.getMapAndPassthroughField())
		 	{
		 		transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());
		 	}	
			List<FilterProperties> finalSortedList=SchemaSyncUtility.INSTANCE.
				 	sortOutputFieldToMatchSchemaSequence(OutputRecordCountUtility.INSTANCE.convertSchemaToFilterProperty(component), 
				 			transformMapping);
				 	transformMapping.getOutputFieldList().clear();
				 	transformMapping.getOutputFieldList().addAll(finalSortedList);
		}
	}
	
	/**
	 * @param transformMapping
	 * 
	 * remove expression output field from mapping output fields
	 */
	public void removeExpressionFieldFromOutputList(TransformMapping transformMapping) 
	{
		for(MappingSheetRow mappingSheetRow:transformMapping.getMappingSheetRows())
		{
			if(!transformMapping.isExpression()&&mappingSheetRow.isExpression()&&mappingSheetRow.isActive())
			transformMapping.getOutputFieldList().removeAll(mappingSheetRow.getOutputList());	
		}
	}
	
	/**
	 * @param transformMapping
	 * 
	 * remove operation output fields from mapping output fields
	 */
	public void removeOperationFieldFromOutputList(TransformMapping transformMapping) 
	{
		for(MappingSheetRow mappingSheetRow:transformMapping.getMappingSheetRows())
		{
			if(transformMapping.isExpression()&&!mappingSheetRow.isExpression()&&mappingSheetRow.isActive())
			transformMapping.getOutputFieldList().removeAll(mappingSheetRow.getOutputList());	
		}
	}
	
	/**
	 * @param transformMapping
	 * @param schema
	 * @param component
	 * @param outputList
	 * @param operationFieldListOfString
	 * 
	 * propagate output fields of mapping window to schema widget
	 */
	public void propagateOuputFieldsToSchemaTabFromTransformWidget(TransformMapping transformMapping,Schema schema,Component component,
			List<FilterProperties> outputList) 
	{
    	if (transformMapping == null || transformMapping.getMappingSheetRows() == null){
			return;
    	}
    	schema.getGridRow().clear();
        List<String> finalPassThroughFields=new LinkedList<String>();
		Map<String, String> finalMapFields=new LinkedHashMap<String, String>();
        List<FilterProperties> operationFieldList=new LinkedList<FilterProperties>();
        for (MappingSheetRow mappingSheetRow : transformMapping.getMappingSheetRows()){
			List<FilterProperties> operationFields = OutputRecordCountUtility.INSTANCE.getOpeartionFields(mappingSheetRow);
         	operationFieldList.addAll(operationFields);
		    addOperationFieldsToSchema(operationFields,schema,component);
		}
		List<String> passThroughFields =getPassThroughFields(transformMapping.getMapAndPassthroughField());
		Map<String, String> mapFields = getMapFields(transformMapping.getMapAndPassthroughField());
		finalMapFields.putAll(mapFields);
		finalPassThroughFields.addAll(passThroughFields);
        addPassthroughFieldsToSchema(passThroughFields,schema,component);
		addMapFieldsToSchema(mapFields,schema,component);
        addPassthroughFieldsAndMappingFieldsToComponentOuputSchema(finalMapFields, finalPassThroughFields,component);
		if(!outputList.isEmpty())
		{
		 List<GridRow> sortedList=new ArrayList<>();
		 for(int i=0;i<outputList.size();i++)
		 {
			 GridRow gridRowTemp = null;
			 for(GridRow gridRow:schema.getGridRow())
			 {
				 if(StringUtils.equals(gridRow.getFieldName(), outputList.get(i).getPropertyname()))
				 {
					 gridRowTemp=gridRow;
					 break;
				 }
				 
			 }
			 if(gridRowTemp!=null)
			 sortedList.add(gridRowTemp);
		 } 
		 schema.getGridRow().clear();
		 schema.getGridRow().addAll(sortedList);
		 sortedList.clear();
		}
		else{
		schema.getGridRow().clear();	
		}
	}
	/**
	 * @param transformMapping
	 * 
	 * add pass through fields to mapping output field
	 */
	public void addPassThroughFieldsToSchema(TransformMapping transformMapping,Component component,Schema schema)
	{
		List<FilterProperties> outputList=new ArrayList<>();
		List<InputField> inputFieldList=transformMapping.getInputFields();	
		for(InputField inputField:inputFieldList)
		{
			NameValueProperty nameValueProperty=new NameValueProperty();
			nameValueProperty.setPropertyName(inputField.getFieldName());
			nameValueProperty.setPropertyValue(inputField.getFieldName());
			nameValueProperty.getFilterProperty().setPropertyname(inputField.getFieldName());
			
			if(!transformMapping.getMapAndPassthroughField().contains(nameValueProperty))
			{
			transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());	
			transformMapping.getMapAndPassthroughField().add(nameValueProperty);
			}
		
	     }
		updatePassThroughFieldAndOutputFields(transformMapping,component, transformMapping.getOutputFieldList());
		SchemaSyncUtility.INSTANCE.unionFilter(transformMapping.getOutputFieldList(),outputList);
		List<String> passthroughFields=getPassThroughFields(transformMapping.getMapAndPassthroughField());
		addPassthroughFieldsToSchema(passthroughFields, schema, component);
		updateComponentOutputSchemaAndSchema(component, schema);
	   
	}
	
	private void updateComponentOutputSchemaAndSchema(Component component, Schema schema) {
		
		Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) component.getProperties()
				.get(Constants.SCHEMA_TO_PROPAGATE);
		if (schemaMap != null && schemaMap.get(Constants.FIXED_OUTSOCKET_ID) != null) {
			ComponentsOutputSchema componentsOutputSchema = schemaMap.get(Constants.FIXED_OUTSOCKET_ID);
			componentsOutputSchema.setFixedWidthGridRowsOutputFields(new ArrayList<>());
			List<GridRow> gridRows = schema.getGridRow();
			for (GridRow gridRow : gridRows) {
				componentsOutputSchema.addSchemaFields(gridRow);
			}
		}
		
		component.getProperties().put(Constants.SCHEMA, schema);
	}
	
	// Removed pass through field according to new input.
	private void updatePassThroughFieldAndOutputFields(TransformMapping transformMapping, Component component, List<FilterProperties> outputLists) {

		List<NameValueProperty> mapAndPassthroughFields = transformMapping.getMapAndPassthroughField();
		Iterator<NameValueProperty> iterator = mapAndPassthroughFields.iterator();
		while (iterator.hasNext()) {
			NameValueProperty nameValueProperty = iterator.next();
			if (isPassThroughField(nameValueProperty) && !isPassThroughFieldPresentInInput(transformMapping, nameValueProperty)) {
				iterator.remove();
				outputLists.remove(nameValueProperty.getFilterProperty());
			}
		}

	}
	
	private boolean isPassThroughFieldPresentInInput(TransformMapping transformMapping,
			NameValueProperty nameValueProperty) {
		List<InputField> inputFields = transformMapping.getInputFields();
		boolean isFound = false;
		for (InputField inputField : inputFields) {
			if (inputField.getFieldName().equals(nameValueProperty.getPropertyName())) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}
	
	private boolean isPassThroughField(NameValueProperty nameValueProperty) {
		if (nameValueProperty.getPropertyName().endsWith(nameValueProperty.getPropertyValue())) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
