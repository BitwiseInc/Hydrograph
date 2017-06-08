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
 * limitations under the License
 *******************************************************************************/
/**
 *
 */
package hydrograph.engine.core.component.entity;

import hydrograph.engine.core.component.entity.base.InputOutputEntityBase;
import hydrograph.engine.core.component.entity.elements.InSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.jaxb.commontypes.TypeFieldName;

import java.util.List;

/**
 * The Class OutputRDBMSEntity.
 *
 * @author Bitwise
 *
 */
public class OutputRDBMSEntity extends InputOutputEntityBase {

    private List<InSocket> inSocketList;
    private List<SchemaField> schemaFieldsList;
    private List<TypeFieldName> primaryKeys;
    private List<TypeFieldName> updateByKeys;

    private String databaseName;
    private String tableName;
    private String username;
    private String password;

    private String databaseType;
    private String loadType;

    private String hostName;
    private Integer port;
    private String jdbcDriver;
    private String schemaName;
    private String sid;
    private String driverType;
    private String _interface;
    private String temps3dir;

    private String chunkSize;
    private String extraUrlParamters;
    /**
     * @return temps3dir - of type String
     */
    public String getTemps3dir() {
        return temps3dir;
    }

    /**
     * @param temps3dir - of type String
     */
    public void setTemps3dir(String temps3dir) {
        this.temps3dir = temps3dir;
    }

    /**
     * @return _inteface - of type String
     */
    public String get_interface() {
        return _interface;
    }

    public void set_interface(String _interface) {
        this._interface = _interface;
    }
    /**
     * @return String - which required driver type name
     */
    public String getDriverType() {
        return driverType;
    }

    /**
     * @param driverType
     */
    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    /**
     * @return String of type sid
     */
    public String getSid() {
        return sid;
    }

    /**
     *
     * @param sid - oracle specific sid
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     *
     * @return schemaName - of type String
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     *
     * @param schemaName
     *            - of type String
     */

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * @return the inSocketList
     */
    public List<InSocket> getInSocketList() {
        return inSocketList;
    }

    /**
     * @param inSocketList the inSocketList to set
     */
    public void setInSocketList(List<InSocket> inSocketList) {
        this.inSocketList = inSocketList;
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
     * @return the primaryKeys
     */
    public List<TypeFieldName> getPrimaryKeys() {
        return primaryKeys;
    }

    /**
     * @param primaryKeys the primaryKeys to set
     */
    public void setPrimaryKeys(List<TypeFieldName> primaryKeys) {
        this.primaryKeys = primaryKeys;
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
     * @return the loadType
     */
    public String getLoadType() {
        return loadType;
    }

    /**
     * @param loadType the loadType to set
     */
    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    /**
     * @return the updateByKeys
     */
    public List<TypeFieldName> getUpdateByKeys() {
        return updateByKeys;
    }

    /**
     * @param updateByKeys the updateByKeys to set
     */
    public void setUpdateByKeys(List<TypeFieldName> updateByKeys) {
        this.updateByKeys = updateByKeys;
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

    /**
     * @return the chunkSize
     */
    public String getChunkSize() {
        return chunkSize;
    }

    /**
     * @param chunkSize the chunkSize to set
     */
    public void setChunkSize(String chunkSize) {
        this.chunkSize = chunkSize;
    }
    /**
     * @param 'extraUrlParameters'
     * @return String
     * a parameters that is optional and can be set as per users preference
     * */
    public String getExtraUrlParamters() {
        return extraUrlParamters;
    }

    public void setExtraUrlParamters(String extraUrlParamters) {
        this.extraUrlParamters = extraUrlParamters;
    }
}
