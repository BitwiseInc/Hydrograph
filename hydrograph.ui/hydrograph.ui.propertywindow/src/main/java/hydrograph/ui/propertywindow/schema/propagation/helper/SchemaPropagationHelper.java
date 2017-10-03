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

 
package hydrograph.ui.propertywindow.schema.propagation.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ExternalSchemaUtil;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.schema.propagation.SchemaPropagation;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;


public class SchemaPropagationHelper {

	public final static SchemaPropagationHelper INSTANCE = new SchemaPropagationHelper();

	
	private SchemaPropagationHelper(){
		
	}
	
	/**
	 * Returns field-map from propagated schema
	 * 
	 * @param component
	 * @return
	 */
	public Map<String, List<String>> getFieldsForFilterWidget(Component component) {
		Map<String, List<String>> propagatedFiledMap = new HashMap<String, List<String>>();
		List<String> genratedProperty = null;
		ComponentsOutputSchema outputSchema = null;
		for (Link link : component.getTargetConnections()) {
			outputSchema = SchemaPropagation.INSTANCE.getComponentsOutputSchema(link);
			genratedProperty = new ArrayList<String>();
			if (outputSchema != null)
				for (FixedWidthGridRow row : outputSchema.getFixedWidthGridRowsOutputFields())
					genratedProperty.add(row.getFieldName());
			propagatedFiledMap.put(link.getTargetTerminal(), genratedProperty);
		}

		return propagatedFiledMap;
	}

	/**
	 * Sort fields on bases of its source output-port
	 * 
	 * @param component
	 * @return
	 */
	public List<List<FilterProperties>> sortedFiledNamesBySocketId(Component component) {
		int inputPortCount = 2;
		List<List<FilterProperties>> listofFiledNameList = new ArrayList<>();
		if (component.getProperties().get("inPortCount") != null)
			inputPortCount = Integer.parseInt((String) component.getProperties().get("inPortCount"));
		for (int i = 0; i < inputPortCount; i++) {
			listofFiledNameList.add(getFieldNameList(component, Constants.INPUT_SOCKET_TYPE + i));
		}
		return listofFiledNameList;
	}

	private List<FilterProperties> getFieldNameList(Component component, String targetTerminal) {
		FilterProperties filedName = null;
		List<FilterProperties> filedNameList = new ArrayList<>();
		for (Link link : component.getTargetConnections()) {
 
			if (link.getTargetTerminal().equals(targetTerminal)) {
				List<BasicSchemaGridRow> basicSchemaGridRows = getBasicSchemaGridRowList(targetTerminal, link);
				if(basicSchemaGridRows!=null)
				{	
					for (BasicSchemaGridRow row :basicSchemaGridRows  ) {
						filedName = new FilterProperties();
						filedName.setPropertyname(row.getFieldName());
						filedNameList.add(filedName);
					}
				}
			}
		}
		return filedNameList;
	}

	 /**
	  * pull out basicSchemaGridRow object from Schema object.
	  * 
	 * @param targetTerminal  
	 * @param link
	 * @return list of BasicSchemaGridRow
	 */
	public List<BasicSchemaGridRow> getBasicSchemaGridRowList(String targetTerminal, Link link) {
		 List<BasicSchemaGridRow> basicSchemaGridRows=null;
		if(StringUtils.equalsIgnoreCase(Constants.INPUT_SUBJOB_COMPONENT_NAME, link.getSource().getComponentName())
		   ||StringUtils.equalsIgnoreCase(Constants.SUBJOB_COMPONENT, link.getSource().getComponentName()))
		{
			Map<String,Schema> inputSchemaMap=(HashMap<String,Schema>)link.getSource().getProperties().
					get(Constants.SCHEMA_FOR_INPUTSUBJOBCOMPONENT);
			if(inputSchemaMap!=null &&inputSchemaMap.get(targetTerminal)!=null)
			basicSchemaGridRows=SchemaSyncUtility.INSTANCE.
					convertGridRowsSchemaToBasicSchemaGridRows(inputSchemaMap.get(targetTerminal).getGridRow());
		}
		else 
		{	
		Schema previousComponentSchema=SchemaPropagation.INSTANCE.getSchema(link);
		if (previousComponentSchema != null)
		basicSchemaGridRows=SchemaSyncUtility.INSTANCE.
		convertGridRowsSchemaToBasicSchemaGridRows(previousComponentSchema.getGridRow());
		}
		return basicSchemaGridRows;
	}
	

	public FixedWidthGridRow createFixedWidthGridRow(String fieldName) {
		return ExternalSchemaUtil.INSTANCE.createFixedWidthGridRow(fieldName);
	}


	public BasicSchemaGridRow createSchemaGridRow(String fieldName) {
		return ExternalSchemaUtil.INSTANCE.createSchemaGridRow(fieldName);
	}
	
	/**
	 * This method fetches input schema fields from source component.
	 * 
	 * @param sourceComponent
	 * @return
	 */
	public List<String> getInputFieldListForLink(Link link) {
		ComponentsOutputSchema sourceComponentsOutputSchema;
		List<String> availableFields = new ArrayList<>();
		sourceComponentsOutputSchema = SchemaPropagation.INSTANCE.getComponentsOutputSchema(link);
		if (sourceComponentsOutputSchema != null) {
			for (GridRow gridRow : sourceComponentsOutputSchema.getFixedWidthGridRowsOutputFields())
				availableFields.add(StringUtils.lowerCase(gridRow.getFieldName()));
		}
		return availableFields;
	}


	/**
	 * Compares basic properties of grid rows to determine whether they are equal or not.
	 * 
	 * @param sourceGridRow
	 * @param targetGridRow
	 * @return
	 */
	public boolean isGridRowEqual(GridRow sourceGridRow, GridRow targetGridRow) {
		if (!StringUtils.equals(sourceGridRow.getFieldName(), targetGridRow.getFieldName())
				|| !StringUtils.equals(sourceGridRow.getDateFormat(), targetGridRow.getDateFormat())
				|| !StringUtils.equals(sourceGridRow.getScale(), targetGridRow.getScale())
				|| !StringUtils.equals(sourceGridRow.getDataTypeValue(), targetGridRow.getDataTypeValue())
				|| !StringUtils.equals(sourceGridRow.getPrecision(), targetGridRow.getPrecision())
				|| !StringUtils.equals(sourceGridRow.getDescription(), targetGridRow.getDescription()) ) {
		
			return false;
		}
		if (sourceGridRow.getDataType() != null && targetGridRow.getDataType() != null) {
				if (!sourceGridRow.getDataType().equals(targetGridRow.getDataType())) {
					return false;
				}
		}else{
				return false;
			}
		if (sourceGridRow.getScaleType() != null && targetGridRow.getScaleType() != null) {
			if (!sourceGridRow.getScaleType().equals(targetGridRow.getScaleType())) {
				return false;
			}
		} 
		return true;
	}

}
