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

package hydrograph.ui.expression.editor.enums;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.evaluate.InvalidDataTypeValueException;


public enum DataTypes {
	
	
	Integer("I","integer") {
		@Override
		protected String getDefaultValue(FixedWidthGridRow inputFieldSchema) {
			return "0";
		}

		@Override
		protected String getDataTypeName() {
			return "int";
		}

		@Override
		protected Class<?> getDataTypeClass() {
			return java.lang.Integer.class;
		}

		@Override
		protected Object validateValue(String inputValue,String filedName,FixedWidthGridRow inputFieldSchema) throws InvalidDataTypeValueException {
			try {
				return java.lang.Integer.parseInt(inputValue);
			}
			catch (Exception exception) {
				throw new InvalidDataTypeValueException(exception,"Invalid value for "+filedName+". Requires integer value.");
			}
		}
	},
	
	BigDecimal("B","bigdecimal") {
		@Override
		protected String getDefaultValue(FixedWidthGridRow inputFieldSchema) {
			return "0";
		}

		@Override
		protected String getDataTypeName() {
			return java.math.BigDecimal.class.getSimpleName();
		}

		@Override
		protected Class<?> getDataTypeClass() {
			return java.math.BigDecimal.class;
		}

		@Override
		protected Object validateValue(String inputValue,String filedName,FixedWidthGridRow inputFieldSchema) throws InvalidDataTypeValueException {
			try {
				
				java.math.BigDecimal bigDecimal=new java.math.BigDecimal(inputValue);
				return bigDecimal;
			}
			catch (Exception exception) {
				throw new InvalidDataTypeValueException(exception,"Invalid value for "+filedName+". Requires numeric value.");
			}
		}
	},
	
	
	
	Float("F","float") {
		@Override
		protected String getDefaultValue(FixedWidthGridRow inputFieldSchema) {
			return "1.0";
		}

		@Override
		protected String getDataTypeName() {
			return "float";
		}

		@Override
		protected Class<?> getDataTypeClass() {
			return java.lang.Float.class;
		}

		@Override
		protected Object validateValue(String inputValue,String filedName,FixedWidthGridRow inputFieldSchema) throws InvalidDataTypeValueException {
			try {
				return java.lang.Float.parseFloat(inputValue);
			}
			catch (Exception exception) {
				throw new InvalidDataTypeValueException(exception,"Invalid value for "+filedName+". Requires float value.");
			}
		}
	},
	Double("D","double") {
		@Override
		protected String getDefaultValue(FixedWidthGridRow inputFieldSchema) {
			return "1.0";
		}

		@Override
		protected String getDataTypeName() {
			return "double";
		}

		@Override
		protected Class<?> getDataTypeClass() {
			return java.lang.Double.class;
		}

		@Override
		protected Object validateValue(String inputValue,String filedName, FixedWidthGridRow inputFieldSchema) throws InvalidDataTypeValueException {
			try {
				return java.lang.Double.parseDouble(inputValue);
			}
			catch (Exception exception) {
				throw new InvalidDataTypeValueException(exception,"Invalid value for "+filedName+". Requires double value.");
			}
		}
	},
	Long("J","long") {
		@Override
		protected String getDefaultValue(FixedWidthGridRow inputFieldSchema) {
			return "1";
		}

		@Override
		protected String getDataTypeName() {
			return "long";
		}

		@Override
		protected Class<?> getDataTypeClass() {
			return java.lang.Long.class;
		}

		@Override
		protected Object validateValue(String inputValue,String filedName, FixedWidthGridRow inputFieldSchema) throws InvalidDataTypeValueException {
			try {
				return java.lang.Long.parseLong(inputValue);
			}
			catch (Exception exception) {
				throw new InvalidDataTypeValueException(exception,"Invalid value for "+filedName+". Requires long value.");
			}
		}
	},
	Short("S","short") {
		@Override
		protected String getDefaultValue(FixedWidthGridRow inputFieldSchema) {
			return "1";
		}

		@Override
		protected String getDataTypeName() {
			return "short";
		}

		@Override
		protected Class<?> getDataTypeClass() {
			return java.lang.Short.class;
		}

		@Override
		protected Object validateValue(String inputValue,String filedName, FixedWidthGridRow inputFieldSchema) throws InvalidDataTypeValueException {
			try {
				return java.lang.Long.parseLong(inputValue);
			}
			catch (Exception exception) {
				throw new InvalidDataTypeValueException(exception,"Invalid value for "+filedName+". Requires short value.");
			}
		}
	},
	Boolean("Z","boolean") {
		@Override
		public String getDefaultValue(FixedWidthGridRow inputFieldSchema) {
			return "false";
		}

		@Override
		protected String getDataTypeName() {
			return "boolean";
		}

		@Override
		protected Class<?> getDataTypeClass() {
			return java.lang.Boolean.class;
		}

		@Override
		protected Object validateValue(String inputValue,String filedName, FixedWidthGridRow inputFieldSchema) throws InvalidDataTypeValueException {
			try {
				return java.lang.Boolean.parseBoolean(inputValue);
			}
			catch (Exception exception) {
				throw new InvalidDataTypeValueException(exception,"Invalid value for "+filedName+". Requires boolean value.");
			}
		}
	},
	
	Void("V","void") {
		@Override
		public String getDefaultValue(FixedWidthGridRow inputFieldSchema) {
			return "";
		}

		@Override
		protected String getDataTypeName() {
			return "void";
		}

		@Override
		protected Class<?> getDataTypeClass() {
			return java.lang.Void.class;
		}

		@Override
		protected Object validateValue(String inputValue,String filedName, FixedWidthGridRow inputFieldSchema) throws InvalidDataTypeValueException {
			return null;
		}
	},
	
	String("S","String") {
		@Override
		public String getDefaultValue(FixedWidthGridRow inputFieldSchema) {
			return "Hydrograph";
		}

		@Override
		protected String getDataTypeName() {
			return "String";
		}

		@Override
		protected Class<?> getDataTypeClass() {
			return java.lang.String.class;
		}

		@Override
		protected Object validateValue(String inputValue,String filedName, FixedWidthGridRow inputFieldSchema) throws InvalidDataTypeValueException {
			return inputValue;
		}
	},
	
	Date("Date", "Date") {
		@Override
		public String getDefaultValue(FixedWidthGridRow inputFieldSchema) {
			String defaultDate = null;
			if (inputFieldSchema != null && StringUtils.isNotBlank(inputFieldSchema.getDateFormat())) {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat(inputFieldSchema.getDateFormat());
					defaultDate = dateFormat.format(new java.util.Date());
				} catch (Exception e) {
					defaultDate = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT).format(new java.util.Date());
				}
			} else {
				defaultDate = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT).format(new java.util.Date());
			}
			return defaultDate;
		}

		@Override
		protected String getDataTypeName() {
			return java.util.Date.class.getSimpleName();
		}

		@Override
		protected Class<?> getDataTypeClass() {
			return java.util.Date.class;
		}

		@Override
		protected Object validateValue(String inputValue, String filedName, FixedWidthGridRow inputFieldSchema)	throws InvalidDataTypeValueException {
			String dateFormat = null;
			if (inputFieldSchema != null && StringUtils.isNotBlank(inputFieldSchema.getDateFormat())) {
				dateFormat = validateDateFormat(inputFieldSchema.getDateFormat());
			}
			try {
				return getParsedDate(inputValue, dateFormat);
			} catch (Exception exception) {
				throw new InvalidDataTypeValueException(exception, "Invalid value for " + filedName
						+ ". Required date format is " + dateFormat);
			}
		}

		private java.lang.String validateDateFormat(java.lang.String dateFormat) {
			try {
				DateFormat format = new SimpleDateFormat(dateFormat);
				format.setLenient(false);
			} catch (IllegalArgumentException illegalArgumentException) {
				return Constants.DEFAULT_DATE_FORMAT;
			}
			return dateFormat;
		}

		private Object getParsedDate(String inputValue, String dateFormat) throws ParseException  {
			DateFormat format;
			format = new SimpleDateFormat(dateFormat);
			format.setLenient(false);
			return format.parse(inputValue);
		}
	};
	

	private final String reflectionValue;
	private String dataType;

	protected abstract String getDefaultValue(FixedWidthGridRow inputFieldSchema);
	protected abstract String getDataTypeName();
	protected abstract Class<?> getDataTypeClass();
	protected abstract Object validateValue(String inputValue,String fieldName, FixedWidthGridRow inputFieldSchema)throws InvalidDataTypeValueException;
	
	DataTypes(String value,String dataType) {
		this.reflectionValue = value;
		this.dataType=dataType;
	}

	public String value() {
		return reflectionValue;
	}

	public static String getDefaulltValuefromDataTypesSimpleName(String value,FixedWidthGridRow inputFieldSchema) {
		for (DataTypes dataType : DataTypes.values()) {
			if (StringUtils.equalsIgnoreCase(dataType.dataType, value)) {
				return dataType.getDefaultValue(inputFieldSchema);
			}
		}
		return value;
	}
	
	public static String getDataTypefromString(String value) {
		for (DataTypes dataType : DataTypes.values()) {
			if (StringUtils.equalsIgnoreCase(dataType.reflectionValue, value)) {
				return dataType.getDataTypeName();
			}else if (StringUtils.containsIgnoreCase(value,dataType.toString())) {
				return dataType.getDataTypeName();
			}
		}
		return StringUtils.removeStart(value, Constants.DATA_TYPE_PREFIX_FOR_SOUCE_CODE);
	}
	
	public static Class<?> getDataTypeClassfromString(String dataTypeName) {
		for (DataTypes dataType : DataTypes.values()) {
			if (StringUtils.equalsIgnoreCase(dataType.dataType, dataTypeName)) {
				return dataType.getDataTypeClass();
			}
		}
		return null;
	}
	
	public static Object validateInputeAndGetEquivalentObject(String inputValue,String fieldName,String dataTypeSimpleName, FixedWidthGridRow inputFieldSchema) throws InvalidDataTypeValueException{
		if(StringUtils.equals(Constants.NULL_STRING, inputValue) || StringUtils.isBlank(inputValue))
			return null;
		for (DataTypes dataType : DataTypes.values()) {
			if (StringUtils.equalsIgnoreCase(dataType.dataType, dataTypeSimpleName)) {
				return dataType.validateValue(inputValue, fieldName,inputFieldSchema);
			}
		}
		return null;
	}
	
}