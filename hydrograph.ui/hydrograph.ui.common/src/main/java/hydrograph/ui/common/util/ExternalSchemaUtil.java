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

import java.math.BigInteger;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.message.Messages;
import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.schema.Field;
import hydrograph.ui.common.schema.FieldDataTypes;
import hydrograph.ui.common.schema.ScaleTypes;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GenerateRecordSchemaGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.datastructure.property.XPathGridRow;

/**
 * Utility class for schema fields
 *	
 * Bitwise
 * 
 */
public class ExternalSchemaUtil {

	public static final ExternalSchemaUtil INSTANCE=new ExternalSchemaUtil();
	
	
	/**
	 * Converts UI grid-row object to its equivalent Jaxb Object
	 * 
	 * @param gridRow
	 * @return
	 */
	public Field convertGridRowToJaxbSchemaField(GridRow gridRow) {
		Field field = new Field();
		if (StringUtils.indexOf(gridRow.getFieldName(), "}") - StringUtils.indexOf(gridRow.getFieldName(), "@{") >= 3) {
			Utils.INSTANCE.loadProperties();
			String paramValue = Utils.INSTANCE.getParamValueForRunSql(gridRow.getFieldName());
			field.setName(paramValue);
		} else {
			field.setName(gridRow.getFieldName());
		}
		field.setType(FieldDataTypes.fromValue(gridRow.getDataTypeValue()));
		
		if(gridRow instanceof XPathGridRow){
			if(StringUtils.isNotBlank(((XPathGridRow)gridRow).getXPath())){
				field.setAbsoluteOrRelativeXpath((((XPathGridRow)gridRow).getXPath()));
			}
		}
		
		if(StringUtils.isNotBlank(gridRow.getDateFormat())){
			field.setFormat(gridRow.getDateFormat());
		}
		if(StringUtils.isNotBlank(gridRow.getPrecision())){
			field.setPrecision(Integer.parseInt(gridRow.getPrecision()));
		}
		if(StringUtils.isNotBlank(gridRow.getScale())){
			field.setScale(Integer.parseInt(gridRow.getScale()));
		}
		if(gridRow.getScaleTypeValue()!=null){
			if(!gridRow.getScaleTypeValue().equals("") && !gridRow.getScaleTypeValue().equals(Messages.SCALE_TYPE_NONE)){
				field.setScaleType(ScaleTypes.fromValue(gridRow.getScaleTypeValue()));
			}
		}
		if(StringUtils.isNotBlank(gridRow.getDescription())){
			field.setDescription(gridRow.getDescription());
		}

		if(gridRow instanceof FixedWidthGridRow){
			if(StringUtils.isNotBlank(((FixedWidthGridRow)gridRow).getLength())){
				field.setLength(new BigInteger(((FixedWidthGridRow)gridRow).getLength()));
			}
		}
		
		if(gridRow instanceof MixedSchemeGridRow){
			if(StringUtils.isNotBlank(((MixedSchemeGridRow)gridRow).getLength())){
				field.setLength(new BigInteger(((MixedSchemeGridRow)gridRow).getLength()));
			}
			
			if(StringUtils.isNotBlank(((MixedSchemeGridRow)gridRow).getDelimiter())){
				field.setDelimiter((((MixedSchemeGridRow)gridRow).getDelimiter()));
			}

		}

		if(gridRow instanceof GenerateRecordSchemaGridRow){
			if(StringUtils.isNotBlank(((GenerateRecordSchemaGridRow)gridRow).getLength())){
				field.setLength(new BigInteger(((GenerateRecordSchemaGridRow)gridRow).getLength()));
			}
			if(StringUtils.isNotBlank(((GenerateRecordSchemaGridRow) gridRow).getRangeFrom())){
				field.setRangeFrom((((GenerateRecordSchemaGridRow) gridRow).getRangeFrom()));
			}
			if(StringUtils.isNotBlank(((GenerateRecordSchemaGridRow) gridRow).getRangeTo())){
				field.setRangeTo(((GenerateRecordSchemaGridRow) gridRow).getRangeTo());
			}
			if(StringUtils.isNotBlank(((GenerateRecordSchemaGridRow) gridRow).getDefaultValue())){
				field.setDefault(((GenerateRecordSchemaGridRow) gridRow).getDefaultValue());
			}
		}
		return field;
	}

	
	/**
	 * Creates an object of FixedWidthGridRow from field-name
	 * 
	 * @param fieldName
	 * @return
	 */
	public FixedWidthGridRow createFixedWidthGridRow(String fieldName) {

		FixedWidthGridRow fixedWidthGridRow = null;
		if (fieldName != null) {
			fixedWidthGridRow = new FixedWidthGridRow();
			fixedWidthGridRow.setFieldName(fieldName);
			fixedWidthGridRow.setDataType(Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX));
			fixedWidthGridRow.setDataTypeValue(String.class.getCanonicalName());
			fixedWidthGridRow.setScale("");
			fixedWidthGridRow.setLength("");
			fixedWidthGridRow.setPrecision("");
			fixedWidthGridRow.setDateFormat("");
			fixedWidthGridRow.setScaleType(Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX));
			fixedWidthGridRow.setScaleTypeValue(Messages.SCALE_TYPE_NONE);
			
		}
		return fixedWidthGridRow;
	}


	/**
	 * Creates an object of BasicSchemaGridRow from field-name
	 * 
	 * @param fieldName
	 * @return
	 */
	public BasicSchemaGridRow createSchemaGridRow(String fieldName) {

		BasicSchemaGridRow	 schemaGrid = null;
		if (fieldName != null) {
			schemaGrid = new BasicSchemaGridRow();
			schemaGrid.setFieldName(fieldName);
			schemaGrid.setDataType(Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX));
			schemaGrid.setDataTypeValue(String.class.getCanonicalName());
			schemaGrid.setScale("");
			schemaGrid.setPrecision("");
			schemaGrid.setDateFormat("");
			
		}
		return schemaGrid;
	}
	
}
