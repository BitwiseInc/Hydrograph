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

 
package hydrograph.ui.engine.constants;


/**
 * Name constants used in component config xml files.  
 * @author Bitwise
 */
public enum PropertyNameConstants {
	
		PATH("Path"),
		IS_SAFE("safe"),
		CHAR_SET("charset"),
		SCHEMA ("schema"),
		DELIMITER("delimiter"),
		RUNTIME_PROPERTIES("runtime_properties"),
		HAS_HEADER("hasHeader"),
		STRICT("strict"),
		OPERATION_CLASS("OPERATION_CLASS"),
		RETENTION_LOGIC_KEEP("retention_logic"),
		OPERATION_FILEDS("operation_fields"),
		SECONDARY_COLUMN_KEYS("Secondary_keys"),
		DEDUP_FILEDS("Key_fields"),
		DATABASE_NAME("databaseName"),
		ORACLE_SID("sid"),
		HOST_NAME("hostName"),
		PORT_NO("port"),
		JDBC_DRIVER("jdbcDriver"),
		USER_NAME("userName"),
		PASSWORD("password"),
		BATCH_SIZE("batchSize"),
		TABLE_NAME("tableName"),
		SCHEMA_NAME("schemaName"),
		EXTERNAL_TABLE_PATH("externalTablePath"),
		LOAD_TYPE_CONFIGURATION("load_type_configuration"),
		PARTITION_KEYS("partitionKeys"),
		QUOTE("quote"),
		SELECT_OPTION("selectOption"),
		SELECT_QUERY("selectQuery"),
		COUNT_QUERY("countQuery"), 
		OVER_WRITE("overWrite"),
		SELECT_INTERFACE("selectInterface"),
		PROPERTY_TAG("property"),
		ABSOLUTE_XPATH("absoluteXPath"),
		ROOT_TAG("rootTag"),
		ROW_TAG("rowTag"),
		RUN_SQL_DATABASE_CONNECTION_NAME("databaseConnectionName"),
		RUN_SQL_QUERY("runsqlquery"),
		SELECT_UPDATE_KEYS("update"),
		URL("url"),
		JDBC_DB_DRIVER("jdbcDriverClass"),
		TEMPORARY_DIRECTORY_NAME("temporaryDirectory");
	
		private final String value;


	PropertyNameConstants(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	/**
	 * Provides enum value of constant based on its string value
	 * @param value
	 * @return
	 */
	public static PropertyNameConstants fromValue(String value) {
		for (PropertyNameConstants propertyNameConstant : PropertyNameConstants.values()) {
			if (propertyNameConstant.value.equals(value)) {
				return propertyNameConstant;
			}
		}
		return null;
	}
}
