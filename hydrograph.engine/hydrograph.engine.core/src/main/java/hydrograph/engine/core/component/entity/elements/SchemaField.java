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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.core.component.entity.elements;


import hydrograph.engine.core.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Type;
/**
 * The Class SchemaField.
 *
 * @author Bitwise
 *
 */
public class SchemaField implements Cloneable,Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6772494118851529659L;
	private String fieldName;
	private String fieldDataType;
	private int fieldLength;
	private String fieldFormat=Constants.DEFAULT_FORMAT;
	private int fieldScale = Constants.DEFAULT_SCALE;
	private String fieldScaleType;
	private int fieldPrecision = Constants.DEFAULT_PRECISION;
	private String fieldLengthDelimiter;
	private Type typeFieldLengthDelimiter;
	private String fieldDefaultValue;
	private String fieldToRangeValue;
	private String fieldFromRangeValue;
	private String absoluteOrRelativeXPath;
	private String colDef;
	private static Logger LOG = LoggerFactory.getLogger(SchemaField.class);

	public SchemaField(String fieldName, String fieldDataType) {
		this.fieldName = fieldName;
		this.fieldDataType = fieldDataType;
	}

	/**
	 * @param absoluteOrRelativeXPath
	 */
	public void setAbsoluteOrRelativeXPath(String absoluteOrRelativeXPath) {
		this.absoluteOrRelativeXPath = absoluteOrRelativeXPath;
	}

	/**
	 * @return the absoluteOrRelativeXPath
	 */
	public String getAbsoluteOrRelativeXPath() {
		return absoluteOrRelativeXPath;
	}

	/**
	 * @param fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @return the fieldDataType
	 */
	public String getFieldDataType() {
		return fieldDataType;
	}

	/**
	 * @return the fieldLength
	 */
	public int getFieldLength() {
		return fieldLength;
	}

	/**
	 * @param fieldLength
	 *            the fieldLength to set
	 */
	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}

	/**
	 * @return the fieldFormat
	 */
	public String getFieldFormat() {
		return fieldFormat;
	}

	/**
	 * @param fieldFormat
	 *            the fieldFormat to set
	 */
	public void setFieldFormat(String fieldFormat) {
		this.fieldFormat = fieldFormat;
	}

	/**
	 * @return the fieldScale
	 */
	public int getFieldScale() {
		return fieldScale;
	}

	/**
	 * @param fieldScale
	 *            the fieldScale to set
	 */
	public void setFieldScale(int fieldScale) {
		this.fieldScale = fieldScale;
	}

	/**
	 * @return the fieldScaleType
	 */
	public String getFieldScaleType() {
		return fieldScaleType;
	}

	/**
	 * @param fieldScaleType
	 *            the fieldScaleType to set
	 */
	public void setFieldScaleType(String fieldScaleType) {
		this.fieldScaleType = fieldScaleType;
	}

	/**
	 * @param precision
	 *            the precision to set
	 */
	public void setFieldPrecision(Integer precision) {
		this.fieldPrecision = precision;
	}

	/**
	 * @return the fieldPrecision
	 */
	public int getFieldPrecision() {
		return fieldPrecision;
	}

	/**
	 * @return the fieldDefaultValue
	 */
	public String getFieldDefaultValue() {
		return fieldDefaultValue;
	}

	/**
	 * @param fieldDefaultValue
	 *            the fieldDefaultValue to set
	 */
	public void setFieldDefaultValue(String fieldDefaultValue) {
		this.fieldDefaultValue = fieldDefaultValue;
	}

	/**
	 * @return the fieldToRangeValue
	 */
	public String getFieldToRangeValue() {
		return fieldToRangeValue;
	}

	/**
	 * @param fieldToRangeValue
	 *            the fieldToRangeValue to set
	 */
	public void setFieldToRangeValue(String fieldToRangeValue) {
		this.fieldToRangeValue = fieldToRangeValue;
	}

	/**
	 * @return the fieldFromRangeValue
	 */
	public String getFieldFromRangeValue() {
		return fieldFromRangeValue;
	}

	/**
	 * @param fieldFromRangeValue
	 *            the fieldFromRangeValue to set
	 */
	public void setFieldFromRangeValue(String fieldFromRangeValue) {
		this.fieldFromRangeValue = fieldFromRangeValue;
	}

	/**
	 * @return the fieldLengthDelimiter
	 */
	public String getFieldLengthDelimiter() {
		return fieldLengthDelimiter;
	}

	/**
	 * @param fieldLengthDelimiter
	 *            the fieldLengthDelimiter to set
	 */
	public void setFieldLengthDelimiter(String fieldLengthDelimiter) {
		this.fieldLengthDelimiter = fieldLengthDelimiter;
	}

	/**
	 * @return the typeFieldLengthDelimiter
	 */
	public Type getTypeFieldLengthDelimiter() {
		return typeFieldLengthDelimiter;
	}

	/**
	 * @param typeFieldLengthDelimiter
	 *            the typeFieldLengthDelimiter to set
	 */
	public void setTypeFieldLengthDelimiter(Type typeFieldLengthDelimiter) {
		this.typeFieldLengthDelimiter = typeFieldLengthDelimiter;
	}

	/**
	 * @return the colDef
	 */
	public String getColDef() {
		return colDef;
	}

	/**
	 * @param colDef
	 *            the colDef to set
	 */
	public void setColDef(String colDef) {
		this.colDef = colDef;
	}
	public String toString() {
		StringBuilder str = new StringBuilder("schema field: ");
		str.append("name: " + fieldName);
		str.append(" | data type: " + fieldDataType);
		str.append(" | length: " + fieldLength);
		str.append(" | format: " + fieldFormat);
		str.append(" | scale: " + fieldScale);
		str.append(" | scale type: " + fieldScaleType);
		str.append(" | precision: " + fieldPrecision);
		str.append(" | mixed scheme length / delimiter: "
				+ fieldLengthDelimiter);
		str.append(" | mixed scheme length / delimiter type: "
				+ typeFieldLengthDelimiter);
		str.append(" | default value: " + fieldDefaultValue);
		str.append(" | range from: " + fieldFromRangeValue);
		str.append(" | range to: " + fieldToRangeValue);

		return str.toString();
	}

	public SchemaField clone() {
		try {
			return (SchemaField) super.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Error cloning SchemaField object", e);
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldDataType == null) ? 0 : fieldDataType.hashCode());
		result = prime * result + ((fieldFormat == null) ? 0 : fieldFormat.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + fieldPrecision;
		result = prime * result + fieldScale;
		result = prime * result + ((fieldScaleType == null) ? 0 : fieldScaleType.hashCode());
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
		SchemaField other = (SchemaField) obj;
		if (fieldDataType == null) {
			if (other.fieldDataType != null)
				return false;
		} else if (!fieldDataType.equals(other.fieldDataType))
			return false;
		if (fieldFormat == null) {
			if (other.fieldFormat != null)
				return false;
		} else if (!fieldFormat.equals(other.fieldFormat))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (fieldPrecision != other.fieldPrecision)
			return false;
		if (fieldScale != other.fieldScale)
			return false;
		if (fieldScaleType == null) {
			if (other.fieldScaleType != null)
				return false;
		} else if (!fieldScaleType.equals(other.fieldScaleType))
			return false;
		return true;
	}
}