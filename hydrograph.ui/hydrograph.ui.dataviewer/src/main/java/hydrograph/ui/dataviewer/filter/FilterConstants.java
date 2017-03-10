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
package hydrograph.ui.dataviewer.filter;

/**
 * The Class FilterConstants.
 * Provides the constant values for Data Viewer Filter Window 
 * @author Bitwise
 *
 */
public class FilterConstants {
	public static final String DELIM_COMMA = ",";
	public static final String TYPE_BOOLEAN = "java.lang.Boolean";
	public static final String TYPE_DOUBLE = "java.lang.Double";
	public static final String TYPE_FLOAT = "java.lang.Float";
	public static final String TYPE_SHORT = "java.lang.Short";
	public static final String TYPE_LONG = "java.lang.Long";
	public static final String TYPE_BIGDECIMAL = "java.math.BigDecimal";
	public static final String TYPE_INTEGER = "java.lang.Integer";
	public static final String TYPE_DATE = "java.util.Date";
	public static final String TYPE_STRING = "java.lang.String";
	
	public static final String REGEX_DIGIT = "\\d";
	public static final String SINGLE_SPACE = " ";
	public static final String OPEN_BRACKET = "(";
	public static final String CLOSE_BRACKET = ")";
	public static final String SINGLE_QOUTE = "'";
	
	public static final String GREATER_THAN_EQUALS = ">=";
	public static final String FIELD_GREATER_THAN_EQUALS = ">=(Field)";
	public static final String LESS_THAN_EQUALS = "<=";
	public static final String FIELD_LESS_THAN_EQUALS = "<=(Field)";
	public static final String LESS_THAN = "<";
	public static final String FIELD_LESS_THAN = "<(Field)";
	public static final String GREATER_THAN = ">";
	public static final String FIELD_GREATER_THAN = ">(Field)";
	public static final String EQUALS = "=";
	public static final String FIELD_EQUALS = "=(Field)";
	public static final String NOT_EQUALS = "<>";
	public static final String FIELD_NOT_EQUALS = "<>(Field)";
	public static final String NOT_IN = "NOT IN";
	public static final String IN = "IN";
	public static final String NOT_LIKE = "NOT LIKE";
	public static final String LIKE = "LIKE";
	public static final String BETWEEN="BETWEEN";
	public static final String BETWEEN_FIELD="BETWEEN(Field)";
	
	public static final String AND = "AND";
	public static final String OR = "OR";
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final String ROW_INDEX = "rowIndex";
	public static final String CONDITIONAL_EDITOR = "conditional_editor";
	public static final String VALUE2TEXTBOX = "value2TextBox";
	public static final String VALUE1TEXTBOX = "value1TextBox";
}
