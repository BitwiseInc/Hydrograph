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
package hydrograph.ui.propertywindow.widgets.customwidgets.metastore;

import java.util.ArrayList;
import java.util.List;
/**
 * HiveTableSchema used for mapping with json string.
 * @author Bitwise
 *
 */
public class HiveTableSchema {

	private String databaseName = "";
	private String query="";
	private String tableName = "";
	private String owner = "";
	private List<HiveTableSchemaField> schemaFields = new ArrayList<HiveTableSchemaField>();
	private String location = "";
	private String externalTableLocation ="";
	private String fieldDelimiter = "";
	private String partitionKeys = "";
	private String inputOutputFormat = "";
	// private int errorCode;

	/*public HiveTableSchema(String databaseName, String tableName) {
		this.databaseName = databaseName;
		this.tableName = tableName;
	}*/

	public String getDatabaseName() {
		return databaseName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public List<HiveTableSchemaField> getSchemaFields() {
		return schemaFields;
	}

	public void setSchemaFields(List<HiveTableSchemaField> listOfHiveTableSchemaField) {
		this.schemaFields = listOfHiveTableSchemaField;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPartitionKeys() {
		return partitionKeys;
	}

	public void setPartitionKeys(String partitionKeys) {
		this.partitionKeys = partitionKeys;
	}

	public String getInputOutputFormat() {
		return inputOutputFormat;
	}

	public void setInputOutputFormat(String inputOutputFormat) {
		this.inputOutputFormat = inputOutputFormat;
	}

	public String getFieldDelimiter() {
		return fieldDelimiter;
	}

	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}

	public String getExternalTableLocation() {
		return externalTableLocation;
	}

	public void setExternalTableLocation(String externalTableLocation) {
		this.externalTableLocation = externalTableLocation;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	

}
