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


/**
 * GenerateRecordSchemaGridRow is the data-structure used for storing schema of Generate-Record component
 * 
 * @author Bitwise
 * 
 */
public class GenerateRecordSchemaGridRow extends FixedWidthGridRow {

	private String rangeFrom;
	private String rangeTo;
	private String defaultValue;

	/**
	 * Gets the range from.
	 * 
	 * @return the range from
	 */
	public String getRangeFrom() {
		return rangeFrom;
	}

	/**
	 * Sets the range from.
	 * 
	 * @param rangeFrom
	 *            the new range from
	 */
	public void setRangeFrom(String rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	/**
	 * Gets the range to.
	 * 
	 * @return the range to
	 */
	public String getRangeTo() {
		return rangeTo;
	}

	/**
	 * Sets the range to.
	 * 
	 * @param rangeTo
	 *            the new range to
	 */
	public void setRangeTo(String rangeTo) {
		this.rangeTo = rangeTo;
	}

	/**
	 * Gets the default value.
	 * 
	 * @return the default value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value.
	 * 
	 * @param defaultValue
	 *            the new default value
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GenerateRecordSchemaGridRow [rangeFrom=" + getRangeFrom() + ", rangeTo=" + getRangeTo()
				+ ", defaultValue=" + getDefaultValue() + ", length=" + getLength() + ", fieldName=" + getFieldName()
				+ ", dateFormat=" + getDateFormat() + ", dataType=" + getDataType() + ", scale=" + getScale()
				+ ", dataTypeValue=" + getDataTypeValue());
		builder.append("]");
		return builder.toString();
	}

	public GridRow copy() {
		GenerateRecordSchemaGridRow tempschemaGrid = new GenerateRecordSchemaGridRow();
		tempschemaGrid.setDataType(getDataType());
		tempschemaGrid.setDateFormat(getDateFormat());
		tempschemaGrid.setPrecision(getPrecision());
		tempschemaGrid.setFieldName(getFieldName());
		tempschemaGrid.setScale(getScale());
		tempschemaGrid.setScaleType(getScaleType());
		tempschemaGrid.setScaleTypeValue(getScaleTypeValue());
		tempschemaGrid.setDataTypeValue(getDataTypeValue());
		tempschemaGrid.setDescription(getDescription());
		tempschemaGrid.setLength(getLength());
		tempschemaGrid.setRangeFrom(rangeFrom);
		tempschemaGrid.setRangeTo(rangeTo);
		tempschemaGrid.setDefaultValue(defaultValue);
		return tempschemaGrid;
	}
	
}
