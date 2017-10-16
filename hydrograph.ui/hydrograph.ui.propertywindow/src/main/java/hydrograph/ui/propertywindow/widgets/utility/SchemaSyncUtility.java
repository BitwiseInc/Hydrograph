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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.JoinMappingGrid;
import hydrograph.ui.datastructure.property.LookupMapProperty;
import hydrograph.ui.datastructure.property.LookupMappingGrid;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;


/**
 * This class holds utility method to perform 
 * Internal and external schema propagation 
 * 
 * @author Bitwise
 */
public class SchemaSyncUtility {

	public static final String OPERATION = "operation";
	public static final String LOOKUP_MAP = "hash_join_map";
	public static final String JOIN_MAP = "join_mapping";

	public static final SchemaSyncUtility INSTANCE= new SchemaSyncUtility();
	
	private SchemaSyncUtility(){
		
	}
	
	/**
	 * Add and remove data from map fields those are not present in outer schema, use to sync outer schema with transform and aggregate internal fields.
	 *
	 * @param outSchema the out schema
	 * @param transformMapping the transform mapping
	 * @return the list
	 */
	public List<NameValueProperty> filterCommonMapFields(List<NameValueProperty> outSchema, TransformMapping transformMapping) {
		List<NameValueProperty> mapNameValueProperties = transformMapping.getMapAndPassthroughField();
		for (NameValueProperty nameValueProperty : outSchema) {
			boolean isPresent=false;
	    	if(!mapNameValueProperties.contains(nameValueProperty))
	    	{
	    		for (MappingSheetRow mappingSheetRow : transformMapping.getMappingSheetRows()) {
    				FilterProperties tempFilterProperties = new FilterProperties();
    				tempFilterProperties.setPropertyname(nameValueProperty.getPropertyValue());
     				if(mappingSheetRow.getOutputList().contains(tempFilterProperties)){
    					isPresent=true;
    					break;    					
    				}
    			}
	    		if(!isPresent)
	    			mapNameValueProperties.add(nameValueProperty);
	    	}
	    	
	    }
		mapNameValueProperties.retainAll(outSchema);
	    return mapNameValueProperties;
	}
	
	/**
	 * Removes the op fields those removed from outer schema.
	 *
	 * @param outSchema the out schema
	 * @param mappingSheetRow the mapping sheet row
	 */
	public void removeOpFields(List<FilterProperties> outSchema, TransformMapping transformMapping,
			List<MappingSheetRow> mappingSheetRow,String componentName){
		for (MappingSheetRow mapSheetRow : mappingSheetRow) {
			if((Constants.NORMALIZE.equalsIgnoreCase(componentName))&&
					((transformMapping.isExpression()&&!mapSheetRow.isExpression())
					||(!transformMapping.isExpression()&&mapSheetRow.isExpression())
					)
			   )
			{
				continue;
			}
			else
			{	
		    mapSheetRow.getOutputList().retainAll(outSchema);
			}
		}
	}
	/**
	 * Union filter.
	 *
	 * @param list1 the list1
	 * @param list2 the list2
	 * @return the list
	 */
	public List<FilterProperties> unionFilter(List<FilterProperties> list1, List<FilterProperties> list2) {
	    for (FilterProperties filterProperties : list1) {
	    	if(!list2.contains(filterProperties) && StringUtils.isNotBlank(filterProperties.getPropertyname()))
	    		list2.add(filterProperties);
	    }
	    return list2;
	}
	
	/**
	 * Returns if schema sync is allowed for the component name passed as parameter.
	 *
	 * @param componentName
	 * @return boolean value if schema sync is allowed
	 */
	public boolean isSchemaSyncAllow(String componentName){
		return StringUtils.equalsIgnoreCase(Constants.TRANSFORM, componentName) || 
			   StringUtils.equalsIgnoreCase(Constants.AGGREGATE, componentName) ||
			   StringUtils.equalsIgnoreCase(Constants.NORMALIZE, componentName) ||
			   StringUtils.equalsIgnoreCase(Constants.GROUP_COMBINE, componentName) ||
			   StringUtils.equalsIgnoreCase(Constants.CUMULATE, componentName) ||
			   StringUtils.equalsIgnoreCase(Constants.LOOKUP, componentName) ||
			   StringUtils.equalsIgnoreCase(Constants.JOIN, componentName);
	}

	/**
	 * Returns if auto schema sync is allowed for the component name passed as parameter.
	 *
	 * @param componentName
	 * @return boolean value if schema sync is allowed
	 */
	public boolean isAutoSchemaSyncAllow(String componentName){
		return isSchemaSyncAllow(componentName);
	}
	
	
	public void autoSyncSchema(Schema SchemaForInternalPropagation,Component component,List<AbstractWidget> widgets) {
		if (SchemaSyncUtility.INSTANCE.isAutoSyncRequiredInSchemaTab(
				SchemaForInternalPropagation.getGridRow(),
				(Schema) component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME))) {
			MessageDialog dialog = new MessageDialog(new Shell(),
					Constants.SYNC_WARNING, null,
					Constants.SCHEMA_NOT_SYNC_MESSAGE, MessageDialog.CONFIRM,
					new String[] { Messages.SYNC_NOW, Messages.MANUAL_SYNC }, 0);
			if (dialog.open() == 0) {
				getSchemaGridWidget(widgets).updateSchemaWithPropogatedSchema(false);
			}
		}
	}
	
	private boolean isAutoSyncRequiredInSchemaTab(List<GridRow> outputFieldSchema,Schema schema) {
		
		List<String> outputFieldList = getSchemaFieldList(outputFieldSchema);
		if(schema==null && outputFieldList.size()!=0){
			return true;
		}
		
		if(schema==null && outputFieldList.size()==0){
			return false;
		}
		
		List<String> schemaFieldList = getSchemaFieldList(schema.getGridRow());
		
		
		if(schemaFieldList == null && outputFieldList == null){
			return false;
		}
		
		if(schemaFieldList.size()!=outputFieldList.size()){
			return true;
		}
		
		for(int index=0;index<schemaFieldList.size();index++){
			if(!StringUtils.equals(schemaFieldList.get(index), outputFieldList.get(index))){
				return true;
			}
		}
		return false;
	}
	
	private ELTSchemaGridWidget getSchemaGridWidget(List<AbstractWidget> widgets){
		for(AbstractWidget widget: widgets){
			if(widget instanceof ELTSchemaGridWidget){
				return (ELTSchemaGridWidget) widget;
			}
		}
		return null;
	}
	
	/**
	 * Push the schema from schema tab to Mapping in General tab
	 *
	 * @param component
	 * @param schemaGridRowList
	 */
	public void pushSchemaToMapping( Component component, List<GridRow> schemaGridRowList) {
		if(StringUtils.equalsIgnoreCase(Constants.TRANSFORM, component.getComponentName()) ||
				   StringUtils.equalsIgnoreCase(Constants.AGGREGATE, component.getComponentName()) ||
				   StringUtils.equalsIgnoreCase(Constants.NORMALIZE, component.getComponentName())||
				   StringUtils.equalsIgnoreCase(Constants.GROUP_COMBINE, component.getComponentName())||
				   StringUtils.equalsIgnoreCase(Constants.CUMULATE, component.getComponentName())){
			pushSchemaToTransformMapping(component, schemaGridRowList);
		}
		else if(StringUtils.equalsIgnoreCase(Constants.LOOKUP, component.getComponentName())){
			pushSchemaToLookupMapping( component, schemaGridRowList);
		}else if(StringUtils.equalsIgnoreCase(Constants.JOIN, component.getComponentName())){
			pushSchemaToJoinMapping( component, schemaGridRowList);
		}
	}
   
	public boolean isAutoSyncRequiredInMappingWidget(Component component, List<GridRow> schemaGridRowList) {
		List<String> outputFieldList = getOutputFieldList(component);
		
		List<String> schemaFieldList = getSchemaFieldList(schemaGridRowList);		
		if(schemaFieldList == null && outputFieldList == null){
			return false;
		}
		
		if(schemaFieldList.size()!=outputFieldList.size()){
			return true;
		}
		
		for(int index=0;index<schemaFieldList.size();index++){
			if(!StringUtils.equals(schemaFieldList.get(index), outputFieldList.get(index))){
				return true;
			}
		}
		return false;
	}

	private List<String> getOutputFieldList(Component component) {
		List<String> outputFieldList=null;
		if(StringUtils.equalsIgnoreCase(component.getComponentName(),Constants.JOIN)){
			JoinMappingGrid joinMappingGrid = (JoinMappingGrid) component.getProperties().get(JOIN_MAP);
			outputFieldList = getOutputFieldsFromJoinMapping(joinMappingGrid);
		}else if(StringUtils.equalsIgnoreCase(component.getComponentName(),Constants.LOOKUP)){
			LookupMappingGrid lookupMappingGrid = (LookupMappingGrid) component.getProperties().get(LOOKUP_MAP);
			outputFieldList = getOutputFieldsFromLookupMapping(lookupMappingGrid);
		}else if(StringUtils.equalsIgnoreCase(Constants.TRANSFORM, component.getComponentName()) ||
				   StringUtils.equalsIgnoreCase(Constants.AGGREGATE, component.getComponentName()) ||
				   StringUtils.equalsIgnoreCase(Constants.NORMALIZE, component.getComponentName()) ||
				   StringUtils.equalsIgnoreCase(Constants.GROUP_COMBINE, component.getComponentName()) ||
				   StringUtils.equalsIgnoreCase(Constants.CUMULATE, component.getComponentName())){
			TransformMapping transformMapping = (TransformMapping) component.getProperties().get(OPERATION);
			outputFieldList = getOutputFieldsFromTransformMapping(transformMapping.getOutputFieldList());
		}
		return outputFieldList;
	}
	
	private List<String> getOutputFieldsFromTransformMapping(List<FilterProperties> outputFieldList) 
	{
		 List<String> outputFields = new ArrayList<>();
		 for (FilterProperties fileFilterProperty : outputFieldList) 
		 {
			if(StringUtils.isNotBlank(fileFilterProperty.getPropertyname())
					&&!(ParameterUtil.isParameter(fileFilterProperty.getPropertyname())))
			{
				if(!outputFields.contains(fileFilterProperty.getPropertyname()))
				outputFields.add(fileFilterProperty.getPropertyname());
			}	
		 }
		return outputFields;
	}

	/**
	 * Push the schema from schema tab to Mapping in General tab for Lookup component
	 *
	 * @param component
	 * @param schemaGridRowList
	 */
	public void pushSchemaToJoinMapping( Component component,
			List<GridRow> schemaGridRowList) {		
		JoinMappingGrid joinMappingGrid = (JoinMappingGrid) component.getProperties().get(JOIN_MAP);
		
		List<LookupMapProperty> mappingTableItemListCopy=new LinkedList<>();
		mappingTableItemListCopy.addAll(joinMappingGrid.getLookupMapProperties());
		joinMappingGrid.getLookupMapProperties().clear();
				
		List<String> schemaFieldList = getSchemaFieldList(schemaGridRowList);
		if(schemaFieldList.size() == 0){
			return;
		}
		
		for(String fieldName:schemaFieldList){
			LookupMapProperty row = getMappingTableItem(mappingTableItemListCopy,fieldName);
			if(row!=null){
				joinMappingGrid.getLookupMapProperties().add(row);
			}else{
				row=new LookupMapProperty();
				row.setSource_Field("");
				row.setOutput_Field(fieldName);
				joinMappingGrid.getLookupMapProperties().add(row);
			}
		}
	}
	
	private LookupMapProperty getMappingTableItem(List<LookupMapProperty> mappingTableItemListClone, String fieldName) {
		for(LookupMapProperty row:mappingTableItemListClone){
			if(StringUtils.equals(fieldName, row.getOutput_Field())){
				return row;
			}
		}
		return null;
	}

	public List<String> getSchemaFieldList(List<GridRow> schemaGridRowList) {
		List<String> schemaFieldList = new LinkedList<>();
		
		for(GridRow gridRow: schemaGridRowList){
			schemaFieldList.add(gridRow.getFieldName());
		}
		return schemaFieldList;
	}
	

	/**
	 * Push the schema from schema tab to Mapping in General tab for Lookup component
	 *
	 * @param component
	 * @param schemaGridRowList
	 */
	public void pushSchemaToLookupMapping( Component component,
			List<GridRow> schemaGridRowList) {
		
		LookupMappingGrid lookupMappingGrid = (LookupMappingGrid) component.getProperties().get(LOOKUP_MAP);
		
		List<LookupMapProperty> mappingTableItemListCopy=new LinkedList<>();
		mappingTableItemListCopy.addAll(lookupMappingGrid.getLookupMapProperties());
		lookupMappingGrid.getLookupMapProperties().clear();
		
		List<String> schemaFieldList = getSchemaFieldList(schemaGridRowList);
		if(schemaFieldList.size() == 0){
			return;
		}
		
		for(String fieldName:schemaFieldList){
			LookupMapProperty row = getMappingTableItem(mappingTableItemListCopy,fieldName);
			if(row!=null){
				lookupMappingGrid.getLookupMapProperties().add(row);
			}else{
				row=new LookupMapProperty();
				row.setSource_Field("");
				row.setOutput_Field(fieldName);
				lookupMappingGrid.getLookupMapProperties().add(row);
			}
		}
	}
	
	/**
	 * Pull the schema from schema tab to Mapping in General tab for Join component
	 *
	 * @param schema
	 * @param component
	 * @return The list of schema grid rows to be shown in mapping.
	 */
	public List<LookupMapProperty> pullJoinSchemaInMapping(Schema schema, Component component) {
		JoinMappingGrid joinMappingGrid = (JoinMappingGrid) component.getProperties().get(JOIN_MAP);
		List<String> lookupMapOutputs = getOutputFieldsFromJoinMapping(joinMappingGrid);
		
		List<LookupMapProperty> outputFieldsFromSchema = getComponentSchemaAsLookupMapProperty(schema.getGridRow());
		
		List<LookupMapProperty> outputFieldsFromSchemaToRetain = getOutputFieldsFromSchemaToRetain(schema.getGridRow(), joinMappingGrid.getLookupMapProperties());
		
		joinMappingGrid.getLookupMapProperties().retainAll(outputFieldsFromSchemaToRetain);
		
		for (LookupMapProperty l : outputFieldsFromSchema){
			if(!lookupMapOutputs.contains(l.getOutput_Field())){
				joinMappingGrid.getLookupMapProperties().add(l);
			}
		}
		return joinMappingGrid.getLookupMapProperties();
	}
	
	
	/**
	 * Pull the schema from schema tab to Mapping in General tab for Lookup component
	 *
	 * @param schema
	 * @param component
	 * @return The list of schema grid rows to be shown in mapping.
	 */
	public List<LookupMapProperty> pullLookupSchemaInMapping(Schema schema, Component component) {
		LookupMappingGrid lookupMappingGrid = (LookupMappingGrid) component.getProperties().get(LOOKUP_MAP);
		List<String> lookupMapOutputs = getOutputFieldsFromLookupMapping(lookupMappingGrid);
		
		List<LookupMapProperty> outputFieldsFromSchema = getComponentSchemaAsLookupMapProperty(schema.getGridRow());
		
		List<LookupMapProperty> outputFieldsFromSchemaToRetain = getOutputFieldsFromSchemaToRetain(schema.getGridRow(), lookupMappingGrid.getLookupMapProperties());
		
		lookupMappingGrid.getLookupMapProperties().retainAll(outputFieldsFromSchemaToRetain);
		
		for (LookupMapProperty l : outputFieldsFromSchema){
			if(!lookupMapOutputs.contains(l.getOutput_Field())){
				lookupMappingGrid.getLookupMapProperties().add(l);
			}
		}
		return lookupMappingGrid.getLookupMapProperties();
	}

	private List<String> getOutputFieldsFromLookupMapping(
			LookupMappingGrid lookupMappingGrid) {
		List<String> lookupMapOutputs = new ArrayList<>();
		for (LookupMapProperty l : lookupMappingGrid.getLookupMapProperties()) {
			lookupMapOutputs.add(l.getOutput_Field());
		}
		return lookupMapOutputs;
	}
	
	private List<String> getOutputFieldsFromJoinMapping(
			JoinMappingGrid joinMappingGrid) {
		List<String> lookupMapOutputs = new ArrayList<>();
		for (LookupMapProperty l : joinMappingGrid.getLookupMapProperties()) {
			lookupMapOutputs.add(l.getOutput_Field());
		}
		return lookupMapOutputs;
	}
    
  
	private void pushSchemaToTransformMapping(
			Component component, List<GridRow> schemaGridRowList) {
		TransformMapping transformMapping = (TransformMapping) component.getProperties().get(OPERATION);
		List<FilterProperties> filterProperties=convertSchemaToFilterProperty(schemaGridRowList);
		removeOpFields(filterProperties,transformMapping,transformMapping.getMappingSheetRows(),component.getComponentName());
		List<NameValueProperty> outputFileds= getComponentSchemaAsProperty(schemaGridRowList);
		filterCommonMapFields(outputFileds, transformMapping);
		Map<Integer,FilterProperties> indexValueParameterMap=retainIndexAndValueOfParameterFields
				(transformMapping.getOutputFieldList());
		transformMapping.getOutputFieldList().clear();
		addOperationFieldAndMapPassthroughfieldToOutputField(transformMapping);
		List<FilterProperties> sortedList=sortOutputFieldToMatchSchemaSequence(filterProperties,transformMapping);
		transformMapping.getOutputFieldList().clear();
		transformMapping.getOutputFieldList().addAll(sortedList);
		addParamtereFieldsToSameIndexAsBeforePull(indexValueParameterMap,transformMapping);
	
	}
	
	public void addOperationFieldAndMapPassthroughfieldToOutputField(TransformMapping transformMapping) {
		for(MappingSheetRow row:transformMapping.getMappingSheetRows())
		{
			transformMapping.getOutputFieldList().addAll(row.getOutputList());
		}	
		for(NameValueProperty nameValueProperty:transformMapping.getMapAndPassthroughField())
		{
			transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());
		}
	}
	
	public List<FilterProperties> sortOutputFieldToMatchSchemaSequence(
			List<FilterProperties> filterProperties,
			TransformMapping transformMapping) {
		List<FilterProperties> finalSortedList=new ArrayList<>();
		for(int i=0;i<filterProperties.size();i++)
		{
			for(FilterProperties filterProperty:transformMapping.getOutputFieldList())
			{
				if(filterProperty.equals(filterProperties.get(i)))
				{
					finalSortedList.add(filterProperty);
					break;
				}
			}
		}
		return finalSortedList;
	}
	
	public void addParamtereFieldsToSameIndexAsBeforePull(
			Map<Integer, FilterProperties> indexValueParameterMap,TransformMapping transformMapping) {
       for(Map.Entry<Integer, FilterProperties> entry:indexValueParameterMap.entrySet())
       {
    	   if(transformMapping.getOutputFieldList().size()>=entry.getKey())
    	   {
    	   transformMapping.getOutputFieldList().add(entry.getKey(),entry.getValue());
    	   }
    	   else
    	   {
    		   transformMapping.getOutputFieldList().add(transformMapping.getOutputFieldList().size(),entry.getValue());
    	   }	   
    		   
       }  
	}
	public List<FilterProperties> convertSchemaToFilterProperty(List<GridRow> schemaGridRowList){
		List<FilterProperties> outputFileds = new ArrayList<>();
			for (GridRow gridRow : schemaGridRowList) {
				FilterProperties filterProperty = new FilterProperties();
				filterProperty.setPropertyname(gridRow.getFieldName());
				outputFileds.add(filterProperty);
			}
		return outputFileds;
	}
	
	public List<LookupMapProperty> getComponentSchemaAsLookupMapProperty(List<GridRow> schemaGridRowList){
		List<LookupMapProperty> outputFields = new ArrayList<>();
			for (GridRow gridRow : schemaGridRowList) {
				LookupMapProperty lookupMapProperty = new LookupMapProperty();
				lookupMapProperty.setSource_Field("");
				lookupMapProperty.setOutput_Field(gridRow.getFieldName());
				outputFields.add(lookupMapProperty);
			}
		return outputFields;
	}
	
	public List<LookupMapProperty> getOutputFieldsFromSchemaToRetain(List<GridRow> schemaGridRowList, List<LookupMapProperty> list){
		List<LookupMapProperty> outputFieldsToRetain = new ArrayList<>();
		for (LookupMapProperty l : list) {
			for(GridRow gridRow : schemaGridRowList){
				if(l.getOutput_Field().equals(gridRow.getFieldName())){
					outputFieldsToRetain.add(l);
					break;
				}
			}

		}
		return outputFieldsToRetain;
	}
	
	public List<NameValueProperty> getComponentSchemaAsProperty(List<GridRow> schemaGridRowList){
		List<NameValueProperty> outputFileds = new ArrayList<>();
			for (GridRow gridRow : schemaGridRowList) {
				NameValueProperty nameValueProperty = new NameValueProperty();
				nameValueProperty.setPropertyName(gridRow.getFieldName());
				nameValueProperty.setPropertyValue(gridRow.getFieldName());
				nameValueProperty.getFilterProperty().setPropertyname(gridRow.getFieldName());
				outputFileds.add(nameValueProperty);
			}
		return outputFileds;
	}
	
	public Map<Integer,FilterProperties> retainIndexAndValueOfParameterFields(List<FilterProperties> filterProperties)
	{
		Map<Integer,FilterProperties> indexAndValueOfParameter=new HashMap<Integer,FilterProperties>();
		for(FilterProperties filterProperty:filterProperties)
		{
			if(ParameterUtil.isParameter(filterProperty.getPropertyname()))
				{
				indexAndValueOfParameter.put(filterProperties.indexOf(filterProperty), filterProperty);
				}		
		}
		return indexAndValueOfParameter;
	}
	/**
	 * This method converts list of filter properties to list of string.
	 * 
	 * @param List of filterProperties
	 * @return List of String
	 */
	public List<String> converterFilterPropertyListToStringList(List<FilterProperties> filterProperties)
	{
		List<String> stringList=new ArrayList<>();
		for(FilterProperties filterProperty:filterProperties)
		{
			stringList.add(filterProperty.getPropertyname());
			
		}	
     return stringList;	
	}
	
	/**
	 * 
	 * Convert GridRow object to BasicSchemaGridRow object.
	 * 
	 * @param list of GridRow object.
	 * @return list of BasicSchemaGridRow object.
	 */
	public List<BasicSchemaGridRow> convertGridRowsSchemaToBasicSchemaGridRows(List<GridRow> gridRows) {
		List<BasicSchemaGridRow> basicSchemaGridRows = null;
		if (gridRows != null) {
			basicSchemaGridRows = new ArrayList<>();
			for (GridRow gridRow1 : gridRows) {
				basicSchemaGridRows.add(convertGridRowSchemaToBasicSchemaGridRow(gridRow1));
			}
		}
		return basicSchemaGridRows;
	}

	private BasicSchemaGridRow convertGridRowSchemaToBasicSchemaGridRow(GridRow gridRow) {
		BasicSchemaGridRow schemaGrid = null;
		if (gridRow != null) {
			schemaGrid = new BasicSchemaGridRow();
			schemaGrid.setDataType(gridRow.getDataType());
			schemaGrid.setDataTypeValue(gridRow.getDataTypeValue());
			schemaGrid.setDateFormat(gridRow.getDateFormat());
			schemaGrid.setPrecision(gridRow.getPrecision());
			schemaGrid.setFieldName(gridRow.getFieldName());
			schemaGrid.setScale(gridRow.getScale());
			schemaGrid.setScaleType(gridRow.getScaleType());
			schemaGrid.setScaleTypeValue(gridRow.getScaleTypeValue());
			schemaGrid.setDescription(gridRow.getDescription());
		}
		return schemaGrid;
	}
	
	/**
	 * 
	 * Convert GridRow object to BasicSchemaGridRow object.
	 * 
	 * @param list of GridRow object.
	 * @return list of BasicSchemaGridRow object.
	 */
	public List<FixedWidthGridRow> convertGridRowsSchemaToFixedSchemaGridRows(List<GridRow> gridRows) {
		List<FixedWidthGridRow> basicSchemaGridRows = null;
		if (gridRows != null) {
			basicSchemaGridRows = new ArrayList<>();
			for (GridRow gridRow1 : gridRows) {
				basicSchemaGridRows.add(convertGridRowSchemaToFixedSchemaGridRow(gridRow1));
			}
		}
		return basicSchemaGridRows;
	}

	private FixedWidthGridRow convertGridRowSchemaToFixedSchemaGridRow(GridRow gridRow) {
		FixedWidthGridRow schemaGrid = null;
		if (gridRow != null) {
			schemaGrid = new FixedWidthGridRow();
			schemaGrid.setDataType(gridRow.getDataType());
			schemaGrid.setDataTypeValue(gridRow.getDataTypeValue());
			schemaGrid.setDateFormat(gridRow.getDateFormat());
			schemaGrid.setPrecision(gridRow.getPrecision());
			schemaGrid.setFieldName(gridRow.getFieldName());
			schemaGrid.setScale(gridRow.getScale());
			schemaGrid.setScaleType(gridRow.getScaleType());
			schemaGrid.setScaleTypeValue(gridRow.getScaleTypeValue());
			schemaGrid.setDescription(gridRow.getDescription());
		}
		return schemaGrid;
	}
	
	
	
}
