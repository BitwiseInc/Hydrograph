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

 
package hydrograph.ui.datastructure.property.mapping;

import hydrograph.ui.common.cloneableinterface.IDataStructure;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.NameValueProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * The class is a data structure to to save aggregate and transform mapping sheet 
 * 
 * @author Bitwise
 *
 */
public class TransformMapping implements IDataStructure{
	
	private List<InputField> inputFields;
	private List<MappingSheetRow> mappingSheetRows;
	private List<NameValueProperty> mapAndPassthroughField;
	private List<FilterProperties> outputFieldList;
	private boolean addPassThroughFields;
	private boolean isExpression;
	private ExpressionEditorData expressionEditorData;
	
	public boolean isExpression() {
		return isExpression;
	}

	public void setExpression(boolean isExpression) {
		this.isExpression = isExpression;
	}

	/**
	 * Instantiates a new transform mapping.
	 */
	public TransformMapping() {
		inputFields = new LinkedList<>();
		mappingSheetRows = new LinkedList<>();
		mapAndPassthroughField=new ArrayList<>();
		outputFieldList=new ArrayList<>();
	}

	/**
	 * Gets the map and passthrough field.
	 * 
	 * @return the map and passthrough field
	 */
	public List<NameValueProperty> getMapAndPassthroughField() {
		return mapAndPassthroughField;
	}

	/**
	 * Sets the map and passthrough field.
	 * 
	 * @param mapAndPassthroughField
	 *            the new map and passthrough field
	 */
	public void setMapAndPassthroughField(List<NameValueProperty> mapAndPassthroughField) {
		this.mapAndPassthroughField = mapAndPassthroughField;
	}
    
	/**
	 * Gets the output field list.
	 * 
	 * @return the output field list
	 */
	public List<FilterProperties> getOutputFieldList() {
		return outputFieldList;
	}

    /** Sets the output field list.
	 * 
	 * @param outputFieldList
	 *            the new output field list
	 */
    public void setOutputFieldList(List<FilterProperties> outputFieldList) {
		this.outputFieldList = outputFieldList;
	}


	public boolean isAddPassThroughFields() {
		return addPassThroughFields;
	}

	public void setAddPassThroughFields(boolean addPassThroughFields) {
		this.addPassThroughFields = addPassThroughFields;
	}

	/**
	 * Instantiates a new transform mapping.
	 * 
	 * @param inputFields
	 *            the input fields
	 * @param mappingSheetRows
	 *            the mapping sheet rows
	 * @param nameValueProperties
	 *            the name value properties
	 * @param outputFieldList
	 *            the output field list
	 */
	public TransformMapping(List<InputField> inputFields,
			List<MappingSheetRow> mappingSheetRows,List<NameValueProperty> nameValueProperties,List<FilterProperties> outputFieldList ) {
		this.inputFields = inputFields;
		this.mappingSheetRows = mappingSheetRows;
		this.mapAndPassthroughField=nameValueProperties;
		this.outputFieldList=outputFieldList;
		
	}
	
	/**
	 * returns list of input fields
	 * 
	 * @return input fields
	 */
	public List<InputField> getInputFields() {
		return inputFields;
	}

	/**
	 * set list of input fields
	 * 
	 * @param inputFields
	 */
	public void setInputFields(List<InputField> inputFields) {
		this.inputFields = inputFields;
	}

	/**
	 * returns list of mapping sheet rows
	 * 
	 * @return mappingSheetRows
	 */
	public List<MappingSheetRow> getMappingSheetRows() {
		return mappingSheetRows;
	}

	/**
	 * set the list of mapping sheet rows.
	 * @param mappingSheetRows
	 */
	public void setMappingSheetRows(List<MappingSheetRow> mappingSheetRows) {
		this.mappingSheetRows = mappingSheetRows;
	}

	public ExpressionEditorData getExpressionEditorData() {
		return expressionEditorData;
	}

	public void setExpressionEditorData(ExpressionEditorData expressionEditorData) {
		this.expressionEditorData = expressionEditorData;
	}

	@Override
	public Object clone() {
		TransformMapping atMapping = new TransformMapping();
		atMapping.getInputFields().addAll(this.inputFields);
		atMapping.getMapAndPassthroughField().addAll(this.mapAndPassthroughField);
		atMapping.getOutputFieldList().addAll(this.outputFieldList);
		atMapping.setExpression(this.isExpression);
		if(this.expressionEditorData!=null)
		atMapping.setExpressionEditorData(this.expressionEditorData.clone());
		for (MappingSheetRow mappingSheetRow : this.mappingSheetRows) {
			if (this.mappingSheetRows != null)
				atMapping.getMappingSheetRows().add((MappingSheetRow) mappingSheetRow.clone());
		}

		return atMapping;
	}
	
	

	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inputFields == null) ? 0 : inputFields.hashCode());
		result = prime * result + ((mapAndPassthroughField == null) ? 0 : mapAndPassthroughField.hashCode());
		result = prime * result + ((mappingSheetRows == null) ? 0 : mappingSheetRows.hashCode());
		result = prime * result + ((outputFieldList == null) ? 0 : outputFieldList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransformMapping other = (TransformMapping) obj;
		if (inputFields == null) {
			if (other.inputFields != null)
				return false;
		} else if (!inputFields.equals(other.inputFields))
			return false;
		if (mapAndPassthroughField == null) {
			if (other.mapAndPassthroughField != null)
				return false;
		} else if (!mapAndPassthroughField.equals(other.mapAndPassthroughField))
			return false;
		if (mappingSheetRows == null) {
			if (other.mappingSheetRows != null)
				return false;
		} else if (!mappingSheetRows.equals(other.mappingSheetRows))
			return false;
		if (outputFieldList == null) {
			if (other.outputFieldList != null)
				return false;
		} else if (!outputFieldList.equals(other.outputFieldList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ATMapping [inputFields=" + inputFields + ", mappingSheetRows="
				+ mappingSheetRows + "]";
	}
}
