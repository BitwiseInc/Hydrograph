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

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.message.Messages;
import hydrograph.ui.common.schema.Field;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;

/**
 * This is the helper class for schema field.
 * 
 * @author Bitwise
 *
 */
public class SchemaFieldUtil {

	public static final SchemaFieldUtil INSTANCE = new SchemaFieldUtil();

	/**
	 * gets the data type for integer value 
	 * 
	 * @param value
	 * @return Integer wrapper class object.
	 */
	public Integer getDataTypeByValue(String value) {
		int i;
		String schemaList = Messages.DATATYPELIST;
		String[] dataTypeList = schemaList.split(",");
		for (i = 0; i < dataTypeList.length; i++) {
			String[] data = dataTypeList[i].split("#");
			if (value.equalsIgnoreCase(data[1]))
				return i;
		}
		return 0;
	}

	/**
	 * gets the data type for scale type  value 
	 * 
	 * @param value
	 * @return Integer wrapper class object
	 */
	public Integer getScaleTypeByValue(String value) {
		int i;
		String schemaList = Messages.SCALETYPELIST;
		String[] scaleTypeList = schemaList.split(",");
		for (i = 0; i < scaleTypeList.length; i++) {
			String[] data = scaleTypeList[i].split("#");
			if (value.equalsIgnoreCase(data[1]))
				return i;
		}
		return 0;
	}

	/**
	 * convert the jaxbField into basicSchemaGridRow object.
	 * 
	 * @param jaxbField
	 * @return basicSchemaGridRow object
	 */
	public BasicSchemaGridRow getBasicSchemaGridRow(Field jaxbField) {
		BasicSchemaGridRow gridRow = null;
		if (jaxbField != null) {
			gridRow = new BasicSchemaGridRow();
			gridRow.setFieldName(jaxbField.getName());
			if (jaxbField.getType() != null) {
				gridRow.setDataTypeValue(getStringValue(jaxbField.getType().value()));
				gridRow.setDataType(getDataTypeByValue(jaxbField.getType().value()));
			}
			gridRow.setDateFormat(getStringValue(jaxbField.getFormat()));
			gridRow.setFieldName(getStringValue(jaxbField.getName()));
			gridRow.setScale(getStringValue(String.valueOf(jaxbField.getScale())));
			gridRow.setPrecision(getStringValue(String.valueOf(jaxbField.getPrecision())));
			gridRow.setDescription(getStringValue(jaxbField.getDescription()));
			if (jaxbField.getScaleType() != null) {
				gridRow.setScaleType(getScaleTypeByValue(jaxbField.getScaleType().value()));
				gridRow.setScaleTypeValue(jaxbField.getScaleType().value());
			} else if (StringUtils.equals(gridRow.getDataTypeValue(), BigDecimal.class.getName())) {
				gridRow.setScaleType(getScaleTypeByValue(Constants.EXPLICIT_SCALE_TYPE_VALUE));
				gridRow.setScaleTypeValue(Constants.EXPLICIT_SCALE_TYPE_VALUE);
			}
		}
		return gridRow;
	}
	
	/**
	 * Create empty string for null values.
	 * 
	 * @param value
	 * @return
	 */
	public String getStringValue(String value) {
		String emptyString = "";
		if (value == null || value.equals("null"))
			return emptyString;
		else
			return value;
	}
}
