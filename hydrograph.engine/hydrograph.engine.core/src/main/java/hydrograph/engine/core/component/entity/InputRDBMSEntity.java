/**
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
 */
/**
 *
 */
package hydrograph.engine.core.component.entity;

import hydrograph.engine.core.component.entity.base.InputOutputEntityBase;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;

import java.util.List;
/**
 * The Class InputRDBMSEntity.
 *
 * @author Bitwise
 *
 */
public class InputRDBMSEntity extends InputOutputEntityBase {
    private List<OutSocket> outSocketList;
    private List<SchemaField> schemaFieldsList;

    private String databaseType;

    private String databaseName;
    private String tableName;
    private String username;
    private String password;
    private String hostName;
    private Integer port;
    private String jdbcDriver;
    private String selectQuery;
    private String countQuery;
    private String sid;
    private String driverType;
    private String schemaName;
    private String _interface;
    private String temps3dir;

    private int numPartitionsValue=Integer.MIN_VALUE;
    private int upperBound=0;
    private int lowerBound=0;
    private String columnName="";
    private String fetchSize;
    private String extraUrlParameters;


    /**
     * @param  'numPartitionsValue'
     * @return Integer
     * number of partitions in order to get optimized performance
     * */
    public Integer getNumPartitionsValue() {
        return numPartitionsValue;
    }

    public void setNumPartitionsValue(Integer numPartitionsValue) {
        this.numPartitionsValue = numPartitionsValue;
    }

    /**
     * @param 'upperBound'
     * @return Integer
     * upper limit for the chosen column of integral type
     * */
    public Integer getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
    }

    /**
     * @param 'lowerBound'
     * @return Integer
     * lower limit for the chosen column of integral type
     * */
    public Integer getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
    }

    /**
     * @param 'columnName'
     * @return String
     * name of the column of integral type where partitioning needs to be done
     */
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @param 'fetchSize'
     * @return Integer
     * a JDBC parameter in order to get more set of rows from the database per network trips
     */
    public String getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(String fetchSize) {
        this.fetchSize = fetchSize;
    }

    /**
     * @param 'extraUrlParameters'
     * @return String
     * a parameters that is optional and can be set as per users preference
     * */
    public String getExtraUrlParameters() {
        return extraUrlParameters;
    }

    public void setExtraUrlParameters(String extraUrlParameters) {
        this.extraUrlParameters = extraUrlParameters;
    }


    /**
     * @return temps3dir - ot type String
     */
    public String getTemps3dir() { return temps3dir;   }

    /**
     * @param temps3dir - of type String
     */
    public void setTemps3dir(String temps3dir) { this.temps3dir = temps3dir;  }

    /**
     * @return _inteface - of type String
     */
    public String get_interface() {
        return _interface;
    }

    /**
     * @param _interface - of type String
     */
    public void set_interface(String _interface) {
        this._interface = _interface;
    }

    /**
     * @return schemaName - of type String
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * @param schemaName - of type String
     */

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * @return drivertype - of type String
     */
    public String getDriverType() {
        return driverType;
    }

    /**
     * @param driverType - of type String
     */
    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    /**
     * @return sid - of type String
     */

    public String getSid() {
        return sid;
    }

    /**
     * @param sid - of type String
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * @return the countQuery
     */
    public String getCountQuery() {
        return countQuery;
    }

    /**
     * @param countQuery the countQuery to set
     */
    public void setCountQuery(String countQuery) {
        this.countQuery = countQuery;
    }

    /**
     * @return the outSocketList
     */
    public List<OutSocket> getOutSocketList() {
        return outSocketList;
    }

    /**
     * @param outSocketList the outSocketList to set
     */
    public void setOutSocketList(List<OutSocket> outSocketList) {
        this.outSocketList = outSocketList;
    }

    /**
     * @return the schemaFieldsList
     */
    public List<SchemaField> getFieldsList() {
        return schemaFieldsList;
    }

    /**
     * @param schemaFieldsList the schemaFieldsList to set
     */
    public void setFieldsList(List<SchemaField> schemaFieldsList) {
        this.schemaFieldsList = schemaFieldsList;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * @param databaseName the databaseName to set
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the selectQuery
     */
    public String getSelectQuery() {
        return selectQuery;
    }

    /**
     * @param selectQuery the selectQuery to set
     */
    public void setSelectQuery(String selectQuery) {
        this.selectQuery = selectQuery;
    }

    /**
     * @return the databaseType
     */
    public String getDatabaseType() {
        return databaseType;
    }

    /**
     * @param databaseType the databaseType to set
     */
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    /**
     * @return the hostName
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return the jdbcDriver
     */
    public String getJdbcDriver() {
        return jdbcDriver;
    }

    /**
     * @param jdbcDriver the jdbcDriver to set
     */
    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }
}
