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

 
package hydrograph.ui.datastructure.property;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.cloneableinterface.IDataStructure;

/**
 * This class stores output schema of each component in FixedWidth format.
 * 
 * @author Bitwise
 * 
 */
public class ComponentsOutputSchema implements IDataStructure {

	private String fromSocketId;
	private List<FixedWidthGridRow> fixedWidthGridRowsOutputFields = new ArrayList<>();
	private List<MixedSchemeGridRow> mixedSchemeGridRowsOutputFields = new ArrayList<>();
	private List<String> passthroughFields = new LinkedList<>();
	private Map<String, String> mapFields = new LinkedHashMap<>();
	private Map<String,String> passThroughFieldsPortInfo = new LinkedHashMap<>();
	private Map<String,String> mapFieldsPortInfo = new LinkedHashMap<>();

	


	/**
	 * This method adds grid row object as fixed width object
	 * 
	 * @param gridRow
	 */
	public void addSchemaFields(GridRow gridRow) {
		if (gridRow instanceof FixedWidthGridRow) {
			this.fixedWidthGridRowsOutputFields.add((FixedWidthGridRow) gridRow);
		} else if(gridRow instanceof MixedSchemeGridRow){
			this.mixedSchemeGridRowsOutputFields.add((MixedSchemeGridRow) gridRow);
		}else if (gridRow instanceof BasicSchemaGridRow) {
			this.fixedWidthGridRowsOutputFields.add(convertSchemaGridRowToFixedWidthSchema((BasicSchemaGridRow) gridRow));
		}else if (gridRow instanceof XPathGridRow){
			this.fixedWidthGridRowsOutputFields.add(convertXPathGridRowToFixedWidthSchema((XPathGridRow)gridRow));
		}

	}


	


	/**
	 * This method converts current fixed width object into schema grid.
	 * 
	 * @param fixedWidthGridRow
	 * @return SchemaGrid
	 */
	public BasicSchemaGridRow convertFixedWidthSchemaToSchemaGridRow(FixedWidthGridRow fixedWidthGridRow) {
		BasicSchemaGridRow schemaGrid = null;
		if (fixedWidthGridRow != null) {
			schemaGrid = new BasicSchemaGridRow();
			schemaGrid.setDataType(fixedWidthGridRow.getDataType());
			schemaGrid.setDataTypeValue(fixedWidthGridRow.getDataTypeValue());
			schemaGrid.setDateFormat(fixedWidthGridRow.getDateFormat());
			schemaGrid.setPrecision(fixedWidthGridRow.getPrecision());
			schemaGrid.setFieldName(fixedWidthGridRow.getFieldName());
			schemaGrid.setScale(fixedWidthGridRow.getScale());
			schemaGrid.setScaleType(fixedWidthGridRow.getScaleType());
			schemaGrid.setScaleTypeValue(fixedWidthGridRow.getScaleTypeValue());
			schemaGrid.setDescription(fixedWidthGridRow.getDescription());
		}
		return schemaGrid;
	}

	/**
	 * This method converts current schema object into fixed width.
	 * 
	 * @param fixedWidthGridRow
	 * @return SchemaGrid
	 */
	private FixedWidthGridRow convertSchemaGridRowToFixedWidthSchema(BasicSchemaGridRow schemaGrid) {
		FixedWidthGridRow fixedWidthGridRow = null;
		if (schemaGrid != null) {
			fixedWidthGridRow = new FixedWidthGridRow();
			fixedWidthGridRow.setDataType(schemaGrid.getDataType());
			fixedWidthGridRow.setDataTypeValue(schemaGrid.getDataTypeValue());
			fixedWidthGridRow.setDateFormat(schemaGrid.getDateFormat());
			fixedWidthGridRow.setPrecision(schemaGrid.getPrecision());
			fixedWidthGridRow.setFieldName(schemaGrid.getFieldName());
			fixedWidthGridRow.setScale(schemaGrid.getScale());
			fixedWidthGridRow.setScaleType(schemaGrid.getScaleType());
			fixedWidthGridRow.setScaleTypeValue(schemaGrid.getScaleTypeValue());
			fixedWidthGridRow.setDescription(schemaGrid.getDescription());
			fixedWidthGridRow.setLength("");
		}
		return fixedWidthGridRow;
	}

	private FixedWidthGridRow convertXPathGridRowToFixedWidthSchema(XPathGridRow gridRow) {
		FixedWidthGridRow fixedWidthGridRow = null;
		if (gridRow != null) {
			fixedWidthGridRow = new FixedWidthGridRow();
			fixedWidthGridRow.setDataType(gridRow.getDataType());
			fixedWidthGridRow.setDataTypeValue(gridRow.getDataTypeValue());
			fixedWidthGridRow.setDateFormat(gridRow.getDateFormat());
			fixedWidthGridRow.setPrecision(gridRow.getPrecision());
			fixedWidthGridRow.setFieldName(gridRow.getFieldName());
			fixedWidthGridRow.setScale(gridRow.getScale());
			fixedWidthGridRow.setScaleType(gridRow.getScaleType());
			fixedWidthGridRow.setScaleTypeValue(gridRow.getScaleTypeValue());
			fixedWidthGridRow.setDescription(gridRow.getDescription());
			fixedWidthGridRow.setLength("");
		}
		return fixedWidthGridRow;
	}
	/**
	 * It updates the current schema as per its pass-through fields mapping
	 * 
	 * @param sourceOutputSchema
	 * @param string 
	 */
	public void updatePassthroughFieldsSchema(ComponentsOutputSchema sourceOutputSchema) {
		if(sourceOutputSchema==null)
			return ;
		for (String fieldName : passthroughFields) {
			FixedWidthGridRow targetFixedWidthGridRow = getFixedWidthGridRowForFieldName(fieldName);
			FixedWidthGridRow sourceFixedWidthGridRow = sourceOutputSchema.getFixedWidthGridRowForFieldName(fieldName);
			if (targetFixedWidthGridRow != null && sourceFixedWidthGridRow!=null) {
					targetFixedWidthGridRow.setDataType(sourceFixedWidthGridRow.getDataType());
					targetFixedWidthGridRow.setDataTypeValue(sourceFixedWidthGridRow.getDataTypeValue());
					targetFixedWidthGridRow.setLength(sourceFixedWidthGridRow.getLength());
					targetFixedWidthGridRow.setPrecision(sourceFixedWidthGridRow.getPrecision());
					targetFixedWidthGridRow.setScale(sourceFixedWidthGridRow.getScale());
					targetFixedWidthGridRow.setDateFormat(sourceFixedWidthGridRow.getDateFormat());
					targetFixedWidthGridRow.setScaleType(sourceFixedWidthGridRow.getScaleType());
					targetFixedWidthGridRow.setScaleTypeValue(sourceFixedWidthGridRow.getScaleTypeValue());
					targetFixedWidthGridRow.setDescription(sourceFixedWidthGridRow.getDescription());
			}else
				if(sourceFixedWidthGridRow!=null){
					this.addSchemaFields(sourceFixedWidthGridRow.copy());
				}
			}
	}

	private FixedWidthGridRow getFixedWidthGridRowForFieldName(String fieldName) {
		for (FixedWidthGridRow fixedWidthGridRow : fixedWidthGridRowsOutputFields) {
			if (fixedWidthGridRow.getFieldName().equals(fieldName))
				return fixedWidthGridRow;
		}
		return null;
	}

	/**
	 * It updates the current schema as per its map-fields mapping
	 * 
	 * @param sourceOutputSchema
	 * @param port 
	 */
	public void updateMapFieldsSchema(ComponentsOutputSchema sourceOutputSchema) {
		if(sourceOutputSchema==null)
			return ;
		for (Entry<String, String> entry : mapFields.entrySet()) {
			FixedWidthGridRow targetFixedWidthGridRow = getFixedWidthGridRowForFieldName(entry.getKey());
			FixedWidthGridRow sourceFixedWidthGridRow = sourceOutputSchema.getFixedWidthGridRowForFieldName(entry.getValue());
			if (targetFixedWidthGridRow != null && sourceFixedWidthGridRow!=null) {
					targetFixedWidthGridRow.setDataType(sourceFixedWidthGridRow.getDataType());
					targetFixedWidthGridRow.setDataTypeValue(sourceFixedWidthGridRow.getDataTypeValue());
					targetFixedWidthGridRow.setLength(sourceFixedWidthGridRow.getLength());
					targetFixedWidthGridRow.setPrecision(sourceFixedWidthGridRow.getPrecision());
					targetFixedWidthGridRow.setScale(sourceFixedWidthGridRow.getScale());
					targetFixedWidthGridRow.setDateFormat(sourceFixedWidthGridRow.getDateFormat());
					targetFixedWidthGridRow.setScaleType(sourceFixedWidthGridRow.getScaleType());
					targetFixedWidthGridRow.setScaleTypeValue(sourceFixedWidthGridRow.getScaleTypeValue());
					targetFixedWidthGridRow.setDescription(sourceFixedWidthGridRow.getDescription());
			} else if ( sourceFixedWidthGridRow != null) {
					GridRow newGridRow=sourceFixedWidthGridRow.copy();
					newGridRow.setFieldName(entry.getKey());
					this.addSchemaFields(newGridRow);
				}
		}
	}

	/**
	 * Copy output-schema from source component. 
	 * 
	 * @param sourceComponentsOutputSchema
	 */
	public void copySchemaFromOther(ComponentsOutputSchema sourceComponentsOutputSchema) {
	
		this.flushCurrentData();
		if (sourceComponentsOutputSchema != null) {
			this.getFixedWidthGridRowsOutputFields().addAll(
					sourceComponentsOutputSchema.getFixedWidthGridRowsOutputFields());
			this.getPassthroughFields().addAll(sourceComponentsOutputSchema.getPassthroughFields());
			this.getMapFields().putAll(sourceComponentsOutputSchema.getMapFields());
			this.getPassthroughFieldsPortInfo().putAll(
					sourceComponentsOutputSchema.getPassthroughFieldsPortInfo());
		}
	}

	private void flushCurrentData() {
		this.getFixedWidthGridRowsOutputFields().clear();
		this.getPassthroughFields().clear();
		this.getMapFields().clear();
		this.getPassthroughFieldsPortInfo().clear();
	}
	
	public List<GridRow> getSchemaGridOutputFields(GridRow gridRow) {
		List<GridRow> schemaGrid = new ArrayList<>();
		
		if (gridRow instanceof MixedSchemeGridRow) {
			for (FixedWidthGridRow fixedWidthGridRow : fixedWidthGridRowsOutputFields) {
				schemaGrid.add(convertFixedWidthSchemaToMixedSchemaGridRow(fixedWidthGridRow));
			}
		} else if(gridRow instanceof FixedWidthGridRow){
			for (FixedWidthGridRow fixedWidthGridRow : fixedWidthGridRowsOutputFields) {
				schemaGrid.add(fixedWidthGridRow);
			}
		}else {
			for (FixedWidthGridRow fixedWidthGridRow : fixedWidthGridRowsOutputFields) {
				schemaGrid.add(convertFixedWidthSchemaToSchemaGridRow(fixedWidthGridRow));
			}
		}
			
		
		return schemaGrid;
	}

	@Override
	public ComponentsOutputSchema clone() {
		return new ComponentsOutputSchema();

	}
	
	public List<String> getPassthroughFields() {

		return passthroughFields;
	}

	public Map<String, String> getMapFields() {

		return mapFields;
	}
	
	public Map<String, String> getPassthroughFieldsPortInfo() {
		return passThroughFieldsPortInfo;
	}

	public Map<String, String> getMapFieldsPortInfo() {
		return mapFieldsPortInfo;
	}

	public String getFromSocketId() {
		return fromSocketId;
	}

	public void setFromSocketId(String fromSocketId) {
		this.fromSocketId = fromSocketId;
	}

	public List<FixedWidthGridRow> getFixedWidthGridRowsOutputFields() {
		return fixedWidthGridRowsOutputFields;
	}
	
	/**
	 * This method returns list of basic-schema from current propagated schema. 
	 * 
	 * @return
	 */
	public List<BasicSchemaGridRow> getBasicGridRowsOutputFields() {
		List<BasicSchemaGridRow> basicSchemaGridRows = null;
		if (fixedWidthGridRowsOutputFields != null) {
			basicSchemaGridRows = new ArrayList<>();
			for (FixedWidthGridRow fixedWidthGridRow : fixedWidthGridRowsOutputFields) {
				basicSchemaGridRows.add(convertFixedWidthSchemaToSchemaGridRow(fixedWidthGridRow));
			}
		}
		return basicSchemaGridRows;
	}

	/**
	 * This methods returns fixed-width-schema row of given field-name.
	 * 
	 * @param fieldName
	 * @return
	 */
	public FixedWidthGridRow getFixedWidthSchemaRow(String fieldName) {
		FixedWidthGridRow fixedWidthGridRow = null;
		if (StringUtils.isNotEmpty(fieldName)) {
			for (FixedWidthGridRow row : this.getFixedWidthGridRowsOutputFields())
				if (StringUtils.equals(fieldName, row.getFieldName()))
					fixedWidthGridRow = row;
		}
		return fixedWidthGridRow;
	}
	
	/**
	 * This methods returns schema-grid row of given field-name.
	 * 
	 * @param fieldName
	 * @return
	 */
	public GridRow getSchemaGridRow(GridRow gridRow) {
		GridRow schemaGridRow = null;
		if (StringUtils.isNotEmpty(gridRow.getFieldName())) {
		for (GridRow row : this.getSchemaGridOutputFields(gridRow))
			if (StringUtils.equals(gridRow.getFieldName(), row.getFieldName()))
				schemaGridRow = row;
		}
		return schemaGridRow;
	}


	public MixedSchemeGridRow convertFixedWidthSchemaToMixedSchemaGridRow(
			FixedWidthGridRow fixedWidthGridRow) {
		MixedSchemeGridRow mixedSchemeGridRow=new MixedSchemeGridRow();
		mixedSchemeGridRow.setDataType(fixedWidthGridRow.getDataType());
		mixedSchemeGridRow.setDataTypeValue(fixedWidthGridRow.getDataTypeValue());
		mixedSchemeGridRow.setDateFormat(fixedWidthGridRow.getDateFormat());
		mixedSchemeGridRow.setDescription(fixedWidthGridRow.getDescription());
		mixedSchemeGridRow.setFieldName(fixedWidthGridRow.getFieldName());
		mixedSchemeGridRow.setLength(fixedWidthGridRow.getLength());
		mixedSchemeGridRow.setPrecision(fixedWidthGridRow.getPrecision());
		mixedSchemeGridRow.setScale(fixedWidthGridRow.getScale());
		mixedSchemeGridRow.setScaleType(fixedWidthGridRow.getScaleType());
		mixedSchemeGridRow.setScaleTypeValue(fixedWidthGridRow.getScaleTypeValue());
		mixedSchemeGridRow.setDelimiter("");
	return mixedSchemeGridRow;
	}

	public ComponentsOutputSchema copy() {
		ComponentsOutputSchema newComponentsOutputSchema = new ComponentsOutputSchema();
			for (FixedWidthGridRow fixedWidthGridRow : this.getFixedWidthGridRowsOutputFields())
				newComponentsOutputSchema.getFixedWidthGridRowsOutputFields().add(
						(FixedWidthGridRow) fixedWidthGridRow.copy());
			for (MixedSchemeGridRow mixedSchemeGridRow : this.mixedSchemeGridRowsOutputFields)
				newComponentsOutputSchema.mixedSchemeGridRowsOutputFields.add((MixedSchemeGridRow) mixedSchemeGridRow.copy());
			newComponentsOutputSchema.passthroughFields.addAll(this.passthroughFields);
			newComponentsOutputSchema.mapFields.putAll(this.mapFields);
			newComponentsOutputSchema.passThroughFieldsPortInfo.putAll(this.passThroughFieldsPortInfo);
			newComponentsOutputSchema.mapFieldsPortInfo.putAll(this.mapFieldsPortInfo);
		return newComponentsOutputSchema;
	}

	/**
	 * This methods returns schema-grid row list
	 * 
	 * @param fieldName
	 * @return
	 */
	public List<GridRow> getGridRowList(){
		List<GridRow> gridRows=new ArrayList<>();
		for(GridRow gridRow : fixedWidthGridRowsOutputFields)
		{
			gridRows.add(gridRow.copy());
		}
		return gridRows;
	}
	public void setFixedWidthGridRowsOutputFields(List<FixedWidthGridRow> fixedWidthGridRowsOutputFields) {
		this.fixedWidthGridRowsOutputFields = fixedWidthGridRowsOutputFields;
	}
	
}
