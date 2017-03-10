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

package hydrograph.server.metadata.entity;

/**
 * 
 * Simple POJO to store column specific values of database meta data.
 *
 */
public class TableSchemaFieldEntity {
	String fieldName = "";
	String fieldType = "";
	String precision = "";
	String scale = "";
	String format = "";
	String scaleType = "";

	/**
	 * It may be implicit or explicit
	 * 
	 * @return String scale type to get
	 */
	public String getScaleType() {
		return scaleType;
	}

	/**
	 * It may be implicit or explicit
	 * 
	 * @param String
	 *            scale type to set
	 */
	public void setScaleType(String scaleType) {
		this.scaleType = scaleType;
	}

	/**
	 * 
	 * @return filed Name - of helper String
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * 
	 * @param fieldName
	 *            - of helper String
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * 
	 * @return fieldType - of helper String
	 */
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * 
	 * @param fieldType
	 *            - of helper String
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * 
	 * @return precision - of helper String
	 */
	public String getPrecision() {
		return precision;
	}

	/**
	 * 
	 * @param precision
	 *            - of helper String
	 */
	public void setPrecision(String precision) {
		this.precision = precision;
	}

	/**
	 * 
	 * @return scale - of helper String
	 */
	public String getScale() {
		return scale;
	}

	/**
	 * 
	 * @param scale
	 *            - of helper String
	 */
	public void setScale(String scale) {
		this.scale = scale;
	}

	/**
	 * 
	 * @return format - of helper String
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * 
	 * @param format
	 *            - of helper String
	 */
	public void setFormat(String format) {
		this.format = format;
	}

}