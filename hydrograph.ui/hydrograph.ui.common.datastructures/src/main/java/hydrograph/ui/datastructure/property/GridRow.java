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
import hydrograph.ui.common.cloneableinterface.IDataStructure;



/**
 * The Class GridRow.
 * Represents a row in the Schema grid of the component properties window.
 * 
 * @author Bitwise
 */
public class GridRow implements IDataStructure {
	
		private String fieldName;
		private String dateFormat;
		private Integer dataType;
		private String scale;
		private String dataTypeValue;
		private Integer scaleType;
		private String scaleTypeValue;
		private String precision;
		private String description = "";
		
		/**
		 * Gets the data type value.
		 * 
		 * @return the data type value
		 */
		public String getDataTypeValue() {
			return dataTypeValue;
		}

		/**
		 * Sets the data type value.
		 * 
		 * @param dataTypeValue
		 *            the new data type value
		 */
		public void setDataTypeValue(String dataTypeValue) {
			this.dataTypeValue = dataTypeValue;
		}

		/**
		 * Gets the scale.
		 * 
		 * @return the scale
		 */
		public String getScale() {
			return scale;
		}

		/**
		 * Sets the scale.
		 * 
		 * @param scale
		 *            the new scale
		 */
		public void setScale(String scale) {
			this.scale = scale;
		}
		
		/**
		 * Gets the scale type value.
		 * 
		 * @return the scale type value
		 */
		public String getScaleTypeValue() {
			return scaleTypeValue;
		}

		/**
		 * Sets the scale type value.
		 * 
		 * @param scaleTypeValue
		 *            the new scale type value
		 */
		public void setScaleTypeValue(String scaleTypeValue) {
			this.scaleTypeValue = scaleTypeValue;
		}
		
		/**
		 * Gets the field name.
		 * 
		 * @return the field name
		 */
		public String getFieldName() {
			return fieldName;
		}

		/**
		 * Sets the field name.
		 * 
		 * @param fieldName
		 *            the new field name
		 */
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		/**
		 * Gets the date format.
		 * 
		 * @return the date format
		 */
		public String getDateFormat() {
			return dateFormat;
		}

		/**
		 * Sets the date format.
		 * 
		 * @param dateFormat
		 *            the new date format
		 */
		public void setDateFormat(String dateFormat) {
			this.dateFormat = dateFormat;
		}

		/**
		 * Gets the data type.
		 * 
		 * @return the data type
		 */
		public Integer getDataType() {
			return dataType;
		}

		/**
		 * Sets the data type.
		 * 
		 * @param dataType
		 *            the new data type
		 */
		public void setDataType(Integer dataType) {
			this.dataType = dataType;
		}
		
		/**
		 * Gets the scale type.
		 * 
		 * @return the scale type
		 */
		public Integer getScaleType() {
			return scaleType;
		}

		/**
		 * Sets the scale type.
		 * 
		 * @param scaleType
		 *            the new scale type
		 */
		public void setScaleType(Integer scaleType) {
			this.scaleType = scaleType;
		}

		/**
		 * Gets the precision.
		 * 
		 * @return the precision
		 */
		public String getPrecision() {
			return precision;
		}

		/**
		 * Sets the precision.
		 * 
		 * @param precision
		 *            the new precision
		 */
		public void setPrecision(String precision) {
			this.precision = precision;
		}
        
		/**
		 * compare two object on the basis of field name and data type value
		 * @param otherGridRow object
		 * @return true if objects are same otherwise false
		 */
		public boolean checkGridRowEqauality(GridRow otherGridRow)
		{
			if (this == otherGridRow)
				return true;
			if (otherGridRow == null)
				return false;
			if (fieldName == null) {
				if (otherGridRow.fieldName != null)
					return false;
			} else if (!fieldName.equalsIgnoreCase(otherGridRow.fieldName))
				return false;
			if (dataTypeValue == null) {
				if (otherGridRow.dataTypeValue != null)
					return false;
			} else if (!dataTypeValue.equals(otherGridRow.dataTypeValue))
				return false;
			return true;
		}	
		
		/**
		 * Gets the description.
		 * 
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Sets the description.
		 * 
		 * @param description
		 *            the new description
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public GridRow clone() 
		{  
		    GridRow gridRow =new GridRow();
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
		//NOTE: DO NOT CHANGE THIS METHOD UNLESS YOU KNOW WHAT YOU ARE DOING
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((fieldName == null) ? 0 : fieldName.hashCode());
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