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
 * The DatabaseSelectionConfig class 
 * Provides the data structure for database selection 
 * @author Bitwise
 *
 */

public class DatabaseSelectionConfig implements IDataStructure {

	private boolean isTableName = true;
	private String tableName;
	private String sqlQuery;
	private String sqlQueryCounter;

	public String getSqlQueryCounter() {
		return sqlQueryCounter;
	}

	public void setSqlQueryCounter(String sqlQueryCounter) {
		this.sqlQueryCounter = sqlQueryCounter;
	}

	public boolean isTableName() {
		return isTableName;
	}

	public void setTableNameSelection(boolean isTableName) {
		this.isTableName = isTableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	@Override
	public Object clone() {
		DatabaseSelectionConfig oracleSelectionConfig = new DatabaseSelectionConfig();
		oracleSelectionConfig.setTableNameSelection(isTableName());
		oracleSelectionConfig.setTableName(getTableName());
		oracleSelectionConfig.setSqlQuery(getSqlQuery());
		oracleSelectionConfig.setSqlQueryCounter(getSqlQueryCounter());
		return oracleSelectionConfig;
	}

}
