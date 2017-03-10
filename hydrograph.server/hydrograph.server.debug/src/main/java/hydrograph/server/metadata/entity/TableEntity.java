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

import java.util.ArrayList;
import java.util.List;

/**
 * Simple POJO class to store the schema fields related to respective databases.
 *
 */
public class TableEntity {

	String databaseName = "";
	String query="";
	String tableName = "";
	String owner = "";
	List<TableSchemaFieldEntity> schemaFields = new ArrayList<TableSchemaFieldEntity>();
	String fieldDelimiter = "";
	String partitionKeys = "";
	String location = "";
	String externalTableLocation = "";
	String inputOutputFormat = "";

	/**
	 * Sets the query.
	 *
	 * @param query
	 *            - of type String
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 *
	 * @return query - of type String
	 */
	public String getQuery() {
		return query;
	}


	/**
	 * Sets the database name.
	 *
	 * @param databaseName
	 *            - of type String
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/**
	 * Sets the table name.
	 *
	 * @param tableName
	 *            - of type String
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Returns the location of meta store.
	 *
	 * @return String
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location of meta store.
	 *
	 * @param location
	 *            - of type String
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Returns the external table location.
	 *
	 * @return externalTableLocation - of type String
	 */
	public String getExternalTableLocation() {
		return externalTableLocation;
	}

	/**
	 * Sets the external table location.
	 *
	 * @param externalTableLocation
	 *            - of type String
	 */

	public void setExternalTableLocation(String externalTableLocation) {
		this.externalTableLocation = externalTableLocation;
	}

	/**
	 * Returns the input Output format.
	 *
	 * @return inputOutputFormat - of type String
	 */
	public String getInputOutputFormat() {
		return inputOutputFormat;
	}

	/**
	 * Sets the input output format
	 *
	 * @param inputOutputFormat
	 *            - of type String
	 */
	public void setInputOutputFormat(String inputOutputFormat) {
		this.inputOutputFormat = inputOutputFormat;
	}

	/**
	 *
	 * @return database name - of type String
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 *
	 * @return table name - of type String
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 *
	 * @return owner - of type String
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Sets the owner.
	 *
	 * @param owner
	 *            - of type String
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 *
	 * @return schema Fields - of type List&lt;TableSchemaFieldEntity&gt; object
	 */
	public List<TableSchemaFieldEntity> getSchemaFields() {
		return schemaFields;
	}

	/**
	 *
	 * @param listOfHiveTableSchemaFieldEntity
	 *            -of type List&lt;TableSchemaFieldEntity&gt; object
	 */
	public void setSchemaFields(List<TableSchemaFieldEntity> listOfHiveTableSchemaFieldEntity) {
		this.schemaFields = listOfHiveTableSchemaFieldEntity;
	}

	/**
	 *
	 * @return partitionKeys - of type String
	 */
	public String getPartitionKeys() {
		return partitionKeys;
	}

	/**
	 *
	 * @param partitionKeys
	 *            - of type String
	 */
	public void setPartitionKeys(String partitionKeys) {
		this.partitionKeys = partitionKeys;
	}

	/**
	 *
	 * @return field delimiter - of type String
	 */
	public String getFieldDelimiter() {
		return fieldDelimiter;
	}

	/**
	 *
	 * @param fieldDelimiter
	 *            - of type String
	 */
	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}

}