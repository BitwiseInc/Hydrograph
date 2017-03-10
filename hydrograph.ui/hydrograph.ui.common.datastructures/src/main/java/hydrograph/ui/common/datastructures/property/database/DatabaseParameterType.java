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
package hydrograph.ui.common.datastructures.property.database;

/**
 * The Class DatabaseParameterType
 * @author Bitwise
 *
 */
public class DatabaseParameterType {

	private String hostName = "";
	private String portNo = "";
	private String userName = "";
	private String password = "";
	private String databaseName = "";
	private String jdbcName = "";
	private String schemaName = "";
	private String dataBaseType;
	private String sid;
	
	
	private DatabaseParameterType(DatabaseBuilder builder){
		this.dataBaseType = builder.dataBaseType;
		this.hostName = builder.hostName;
		this.portNo = builder.portNo;
		this.userName = builder.userName;
		this.password = builder.password;
		this.databaseName = builder.databaseName;
		this.jdbcName = builder.jdbcName;
		this.schemaName = builder.schemaName;
		this.sid = builder.sid;
	}
	public static class DatabaseBuilder{
		//required fields
		private String dataBaseType;
		private String hostName = "";
		private String portNo = "";
		private String userName = "";
		private String password = "";
		
		//optional fields
		private String jdbcName = "";
		private String schemaName = "";
		private String databaseName = "";
		private String sid="";
		
		/**
		 * Instantiates a new DatabaseParameterType required fields
		 * @param dataBaseType
		 * @param hostName
		 * @param portNo
		 * @param userName
		 * @param password
		 */
		public DatabaseBuilder(String dataBaseType,String hostName, String portNo, String userName, String password){
			this.dataBaseType = dataBaseType;
			this.hostName = hostName;
			this.portNo = portNo;
			this.userName = userName;
			this.password = password;
			
		}
		
		public DatabaseParameterType build(){
			return new DatabaseParameterType(this);
		}
		
		public DatabaseBuilder jdbcName(String jdbcName){
			this.jdbcName = jdbcName;
			return this;
		}
		
		public DatabaseBuilder schemaName(String schemaName){
			this.schemaName = schemaName;
			return this;
		}
		
		public DatabaseBuilder databaseName(String databaseName){
			this.databaseName = databaseName;
			return this;
		}
		
		public DatabaseBuilder copy(DatabaseParameterType parameterType){
			this.hostName = parameterType.hostName;
			return this;
		}
		
		public DatabaseBuilder sid(String sid){
			this.sid = sid;
			return this;
		}
	}
		
	public String getJdbcName(){
		return jdbcName;
	}
	
	public String getSchemaName(){
		return schemaName;
	}
	
	public String getDataBaseType(){
		return dataBaseType;
	}
	
	public String getHostName(){
		return hostName;
	}
	
	public String getPortNo(){
		return portNo;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public String getDatabaseName(){
		return databaseName;
	}
	
	public String getPassword(){
		return password;
	}
	
	public String getSid(){
		return sid;
	}
}
