/*******************************************************************************
 * Copyright 2016, 2017 Capital One Services, LLC and Bitwise, Inc.
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
package hydrograph.server.metadata.strategy;

import hydrograph.server.metadata.entity.TableEntity;
import hydrograph.server.metadata.entity.TableSchemaFieldEntity;
import hydrograph.server.metadata.exception.ParamsCannotBeNullOrEmpty;
import hydrograph.server.metadata.strategy.base.MetadataStrategyTemplate;
import hydrograph.server.utilities.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation for Oracle database and getting the table entity
 * parameters.
 *
 * @author amiyam
 */
public class OracleMetadataStrategy extends MetadataStrategyTemplate {
    private static final Logger LOG = LoggerFactory.getLogger(OracleMetadataStrategy.class);

    Connection connection = null;
    private String query = null, tableName = null;

    /**
     * Used to set the connection for Oracle
     *
     * @param connectionProperties - contain request params details
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setConnection(Map connectionProperties) throws ClassNotFoundException, SQLException {
        String driverType = connectionProperties
                .getOrDefault(Constants.DRIVER_TYPE,
                        new ParamsCannotBeNullOrEmpty(Constants.DRIVER_TYPE + " not found in request parameter"))
                .toString();
        String host = connectionProperties
                .getOrDefault(Constants.HOST_NAME,
                        new ParamsCannotBeNullOrEmpty(Constants.HOST_NAME + " not found in request parameter"))
                .toString();
        String port = connectionProperties
                .getOrDefault(Constants.PORT_NUMBER,
                        new ParamsCannotBeNullOrEmpty(Constants.PORT_NUMBER + " not found in request parameter"))
                .toString();
        String sid = connectionProperties.getOrDefault(Constants.SID,
                new ParamsCannotBeNullOrEmpty(Constants.SID + " not found in request parameter")).toString();
        String userId = connectionProperties
                .getOrDefault(Constants.USERNAME,
                        new ParamsCannotBeNullOrEmpty(Constants.USERNAME + " not found in request parameter"))
                .toString();
        String service_pwd = connectionProperties
                .getOrDefault(Constants.SERVICE_PWD,
                        new ParamsCannotBeNullOrEmpty(Constants.SERVICE_PWD + " not found in request parameter"))
                .toString();
        String jdbcUrl = "jdbc:oracle:" + driverType + "://@" + host + ":" + port + ":" + sid;
        Class.forName(Constants.ORACLE_JDBC_CLASSNAME);
        LOG.info("Connection url for oracle = '" + jdbcUrl + "'");
        LOG.info("Connecting with '" + userId + "' user id.");
        connection = DriverManager.getConnection(jdbcUrl, userId, service_pwd);
    }

    /**
     * @param componentSchemaProperties - Contain request parameter details
     * @return {@link TableEntity}
     */
    @SuppressWarnings("unchecked")
    @Override
    public TableEntity fillComponentSchema(Map componentSchemaProperties)
            throws SQLException, ParamsCannotBeNullOrEmpty {
        if (componentSchemaProperties.get(Constants.TABLENAME) != null)
            tableName = componentSchemaProperties.get(Constants.TABLENAME).toString().trim();
        else
            query = componentSchemaProperties.get(Constants.QUERY).toString().trim();

        LOG.info("Generating schema for oracle using "
                + ((tableName != null) ? "table : " + tableName : "query : " + query));

        ResultSet res = null;
        TableEntity tableEntity = new TableEntity();
        List<TableSchemaFieldEntity> tableSchemaFieldEntities = new ArrayList<TableSchemaFieldEntity>();
        try {
            Statement stmt = connection.createStatement();
            // checks the query or table name . if tableQuery present it will
            // execute the query otherwise selected table name query will be
            // executed.
            if (query != null && !query.isEmpty())
                res = stmt.executeQuery("Select * from (" + query + ") WHERE ROWNUM < 0");
            else if (tableName != null && !tableName.isEmpty())
                res = stmt.executeQuery("Select * from " + tableName + " where 1<0");
            else {
                LOG.error("Table or query cannot be null in requested parameters");
                throw new ParamsCannotBeNullOrEmpty("Table or query cannot be null in requested parameters");
            }

            ResultSetMetaData rsmd = res.getMetaData();
            for (int count = 1; count < rsmd.getColumnCount() + 1; count++) {
                TableSchemaFieldEntity tableSchemaFieldEntity = new TableSchemaFieldEntity();
                tableSchemaFieldEntity.setFieldName(rsmd.getColumnLabel(count));
                // Retrieving schema type for converting oracle type to java
                // specific type
                if (rsmd.getColumnTypeName(count).equalsIgnoreCase("timestamp")) {
                    // setting scale to none for timestamp
                    tableSchemaFieldEntity.setPrecision("");
                    tableSchemaFieldEntity.setScale("");
                    tableSchemaFieldEntity.setFormat("yyyy-MM-dd HH:mm:ss:SSS");
                    tableSchemaFieldEntity.setFieldType("java.util.Date");
                } else if (rsmd.getColumnTypeName(count).equalsIgnoreCase("date")) {
                    // setting scale to none for date
                    tableSchemaFieldEntity.setPrecision("");
                    tableSchemaFieldEntity.setScale("");
                    tableSchemaFieldEntity.setFormat("yyyy-MM-dd");
                    tableSchemaFieldEntity.setFieldType("java.util.Date");
                } else if (rsmd.getScale(count) != 0) {
                    if ((rsmd.getColumnTypeName(count).equalsIgnoreCase("number") && rsmd.getPrecision(count) <= 19)) {
                        tableSchemaFieldEntity.setPrecision("");
                        tableSchemaFieldEntity.setScale("");
                        tableSchemaFieldEntity.setFieldType("java.lang.Double");
                    } else if (rsmd.getColumnTypeName(count).equalsIgnoreCase("number")
                            && rsmd.getPrecision(count) > 19) {
                        tableSchemaFieldEntity.setPrecision(String.valueOf(rsmd.getPrecision(count)));
                        tableSchemaFieldEntity.setScale(String.valueOf(rsmd.getScale(count)));
                        tableSchemaFieldEntity.setScaleType("explicit");
                        tableSchemaFieldEntity.setFieldType("java.math.BigDecimal");
                    }
                } else if ((rsmd.getColumnTypeName(count).equalsIgnoreCase("number") && rsmd.getPrecision(count) <= 10
                        && rsmd.getPrecision(count) > 5)) {
                    tableSchemaFieldEntity.setPrecision("");
                    tableSchemaFieldEntity.setScale("");
                    tableSchemaFieldEntity.setFieldType("java.lang.Integer");
                } else if ((rsmd.getColumnTypeName(count).equalsIgnoreCase("number")
                        && rsmd.getPrecision(count) <= 5)) {
                    tableSchemaFieldEntity.setPrecision("");
                    tableSchemaFieldEntity.setScale("");
                    tableSchemaFieldEntity.setFieldType("java.lang.Short");
                } else if ((rsmd.getColumnTypeName(count).equalsIgnoreCase("number") && rsmd.getPrecision(count) <= 19
                        && rsmd.getPrecision(count) > 10)) {
                    tableSchemaFieldEntity.setPrecision("");
                    tableSchemaFieldEntity.setScale("");
                    tableSchemaFieldEntity.setFieldType("java.lang.Long");
                } else if ((rsmd.getColumnTypeName(count).equalsIgnoreCase("char") && rsmd.getPrecision(count) == 1)) {
                    tableSchemaFieldEntity.setPrecision("");
                    tableSchemaFieldEntity.setScale("");
                    tableSchemaFieldEntity.setFieldType("java.lang.Boolean");
                } else if (rsmd.getColumnTypeName(count).equalsIgnoreCase("number") && rsmd.getPrecision(count) > 19) {
                    tableSchemaFieldEntity.setScaleType("explicit");
                    tableSchemaFieldEntity.setScale("1");
                    tableSchemaFieldEntity.setFieldType("java.math.BigDecimal");
                } else if (rsmd.getColumnTypeName(count).contains("VARCHAR")) {
                    tableSchemaFieldEntity.setScale("");
                    tableSchemaFieldEntity.setPrecision("");
                    tableSchemaFieldEntity.setFieldType("java.lang.String");
                } else {
                    tableSchemaFieldEntity.setPrecision(String.valueOf(rsmd.getPrecision(count)));
                    tableSchemaFieldEntity.setScale(String.valueOf(rsmd.getScale(count)));
                    tableSchemaFieldEntity.setFieldType(rsmd.getColumnClassName(count));
                }
                tableSchemaFieldEntities.add(tableSchemaFieldEntity);
            }
            if (componentSchemaProperties.get(Constants.TABLENAME) == null)
                tableEntity.setQuery(componentSchemaProperties.get(Constants.QUERY).toString());
            else
                tableEntity.setTableName(componentSchemaProperties.get(Constants.TABLENAME).toString());
            tableEntity.setDatabaseName(componentSchemaProperties.get(Constants.dbType).toString());
            tableEntity.setSchemaFields(tableSchemaFieldEntities);
            res.close();
        } finally {
            connection.close();
        }
        return tableEntity;
    }
}
