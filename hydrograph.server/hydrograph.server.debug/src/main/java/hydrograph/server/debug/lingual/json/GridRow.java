/*******************************************************************************
 *  Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *******************************************************************************/

package hydrograph.server.debug.lingual.json;

/**
 * The Class GridRow.
 * 
 * @author Bitwise
 */
public class GridRow  {

	private String fieldName;
	private String dateFormat;
	private Integer dataType;
	private String scale;
	private String dataTypeValue;
	private Integer scaleType;
	private String scaleTypeValue;
	private String precision;
	private String description = "";

	public String getDataTypeValue() {
		return dataTypeValue;
	}

	public void setDataTypeValue(String dataTypeValue) {
		this.dataTypeValue = dataTypeValue;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getScaleTypeValue() {
		return scaleTypeValue;
	}

	public void setScaleTypeValue(String scaleTypeValue) {
		this.scaleTypeValue = scaleTypeValue;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public Integer getScaleType() {
		return scaleType;
	}

	public void setScaleType(Integer scaleType) {
		this.scaleType = scaleType;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public GridRow clone() {
		GridRow gridRow = new GridRow();
		gridRow.setFieldName(fieldName);
		gridRow.setDataType(dataType);
		gridRow.setDataTypeValue(dataTypeValue);
		gridRow.setDateFormat(dateFormat);
		gridRow.setScaleType(scaleType);
		gridRow.setScaleTypeValue(scaleTypeValue);
		gridRow.setPrecision(precision);
		gridRow.setDescription(description);
		return gridRow;
	}

	// NOTE: DO NOT CHANGE THIS METHOD UNLESS YOU KNOW WHAT YOU ARE DOING
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
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
		GridRow other = (GridRow) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GridRow [fieldName=");
		builder.append(fieldName);
		builder.append("\n dataType=");
		builder.append(dataType);
		builder.append("\n dataTypeValue=");
		builder.append(dataTypeValue);
		builder.append("\n dateFormat=");
		builder.append(dateFormat);
		builder.append("\n precision=");
		builder.append(precision);
		builder.append("\n scaleType=");
		builder.append(scaleType);
		builder.append("\n scaleTypeValue=");
		builder.append(scaleTypeValue);
		builder.append("\n description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Copy.
	 * 
	 * @return the grid row
	 */
	public GridRow copy() {
		GridRow tempschemaGrid = new GridRow();

		tempschemaGrid.setFieldName(fieldName);
		tempschemaGrid.setDataType(dataType);
		tempschemaGrid.setDataTypeValue(dataTypeValue);
		tempschemaGrid.setDateFormat(dateFormat);
		tempschemaGrid.setScale(scale);
		tempschemaGrid.setScaleType(scaleType);
		tempschemaGrid.setScaleTypeValue(scaleTypeValue);
		tempschemaGrid.setPrecision(precision);
		tempschemaGrid.setDescription(description);

		return tempschemaGrid;
	}

	/**
	 * Updates current grid row properties.
	 * 
	 * @param sourceGridRow
	 */
	public void updateBasicGridRow(GridRow sourceGridRow) {
		if (sourceGridRow != null) {
			this.setFieldName(sourceGridRow.getFieldName());
			this.setDataType(sourceGridRow.getDataType());
			this.setDataTypeValue(sourceGridRow.getDataTypeValue());
			this.setDateFormat(sourceGridRow.getDateFormat());
			this.setScale(sourceGridRow.getScale());
			this.setScaleType(sourceGridRow.getScaleType());
			this.setScaleTypeValue(sourceGridRow.getScaleTypeValue());
			this.setPrecision(sourceGridRow.getPrecision());
			this.setDescription(sourceGridRow.getDescription());
		}
	}
}