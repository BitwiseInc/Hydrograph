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
package hydrograph.ui.datastructures.metadata;

import hydrograph.ui.common.cloneableinterface.IDataStructure;
/**
 * This class used for holding data for metadata/debug service communication
 * @author Bitwise
 *
 */
public class MetaDataDetails implements IDataStructure {

	private String dbtype;
	private String username;
	private String password;
	private String hostname;
	private String port;
	private String sid;
	private String drivertype;
	private String query;
	private String table;
	private String database;

	public String getDbType() {
		return dbtype;
	}

	public void setDbType(String dbType) {
		this.dbtype = dbType;
	}

	public String getUserId() {
		return username;
	}

	public void setUserId(String userId) {
		this.username = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return hostname;
	}

	public void setHost(String host) {
		this.hostname = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getDriverType() {
		return drivertype;
	}

	public void setDriverType(String driverType) {
		this.drivertype = driverType;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getTableName() {
		return table;
	}

	public void setTableName(String tableName) {
		this.table = tableName;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
    @Override
	public Object clone() {
		MetaDataDetails dataDetails = new MetaDataDetails();
		dataDetails.setDatabase(this.database);
		dataDetails.setDbType(this.dbtype);
		dataDetails.setDriverType(this.drivertype);
		dataDetails.setHost(this.hostname);
		dataDetails.setPassword(this.password);
		dataDetails.setPort(this.port);
		dataDetails.setQuery(this.query);
		dataDetails.setSid(this.sid);
		dataDetails.setTableName(this.table);
		dataDetails.setUserId(this.username);
		
		return dataDetails;
	}

}
