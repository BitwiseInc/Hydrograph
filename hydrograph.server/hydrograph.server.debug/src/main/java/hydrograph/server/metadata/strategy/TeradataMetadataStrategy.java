/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
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
 * Created by manojp on 01/12/2017.
 */
public class TeradataMetadataStrategy extends MetadataStrategyTemplate {

    Logger LOG = LoggerFactory.getLogger(TeradataMetadataStrategy.class);
    Connection connection = null;
    private String query = null, tableName = null;

    /**
     * Used to set the connection for Teradata
     *
     * @param connectionProperties - contain request params details
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setConnection(Map connectionProperties) throws ClassNotFoundException, SQLException {
        String host = connectionProperties
                .getOrDefault(Constants.HOST_NAME,
                        new ParamsCannotBeNullOrEmpty(Constants.HOST_NAME + " not found in request parameter"))
                .toString();
        String port = connectionProperties
                .getOrDefault(Constants.PORT_NUMBER,
                        new ParamsCannotBeNullOrEmpty(Constants.PORT_NUMBER + " not found in request parameter"))
                .toString();
        String userId = connectionProperties
                .getOrDefault(Constants.USERNAME,
                        new ParamsCannotBeNullOrEmpty(Constants.USERNAME + " not found in request parameter"))
                .toString();
        String service_pwd = connectionProperties
                .getOrDefault(Constants.SERVICE_PWD,
                        new ParamsCannotBeNullOrEmpty(Constants.SERVICE_PWD + " not found in request parameter"))
                .toString();
        String database = connectionProperties
                .getOrDefault(Constants.DATABASE_NAME,
                        new ParamsCannotBeNullOrEmpty(Constants.DATABASE_NAME + " not found in request parameter"))
                .toString();
        //String jdbcurl = "jdbc:teradata://" + host + ":" + port + "/" + database;

        //String jdbcurl = "jdbc:teradata://" + host + "/" + "DBS_PORT =" + port + ",DATABASE=" + database +";
        String jdbcurl = "jdbc:teradata://" + host + "/" + "DBS_PORT" + "=" + port + "," + "DATABASE"+ "=" + database;


        LOG.info("Connection url for teradata = '" + jdbcurl + "'");
        LOG.info("Connecting with '" + userId + "' user id.");
        Class.forName(Constants.TERADATA_JDBC_CLASSNAME);
        connection = DriverManager.getConnection(jdbcurl, userId, service_pwd);
    }

    /**
     * @param componentSchemaProperties - Contains request parameter details
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

        LOG.info("Generating schema for teradata using "
                + ((tableName != null) ? "table : " + tableName : "query : " + query));

        ResultSet res = null;
        TableEntity tableEntity = new TableEntity();
        List<TableSchemaFieldEntity> tableSchemaFieldEntities = new ArrayList<TableSchemaFieldEntity>();
        try {
            Statement stmt = connection.createStatement();
            if (query != null && !query.isEmpty())
                res = stmt.executeQuery("Select * from (" + query + ") as alias WHERE 1<0");
            else if (tableName != null && !tableName.isEmpty())
                res = stmt.executeQuery("Select * from " + tableName + " where 1<0");
            else {
                LOG.error("Table or query cannot be null in requested parameters");
                throw new ParamsCannotBeNullOrEmpty("Table or query cannot be null in requested parameters");
            }
            ResultSetMetaData rsmd = res.getMetaData();
            for (int count = 1; count <= rsmd.getColumnCount(); count++) {
                TableSchemaFieldEntity tableSchemaFieldEntity = new TableSchemaFieldEntity();
                tableSchemaFieldEntity.setFieldName(rsmd.getColumnLabel(count));
                if (rsmd.getColumnTypeName(count).equalsIgnoreCase("TIMESTAMP")) {
                    tableSchemaFieldEntity.setFormat("yyyy-MM-dd HH:mm:ss");
                    tableSchemaFieldEntity.setFieldType("java.util.Date");
                } else if (rsmd.getColumnTypeName(count).equalsIgnoreCase("DATE")) {
                    tableSchemaFieldEntity.setFormat("yyyy-MM-dd");
                    tableSchemaFieldEntity.setFieldType("java.util.Date");
                } else if (rsmd.getColumnTypeName(count).equalsIgnoreCase("BYTEINT")) {
                    tableSchemaFieldEntity.setFieldType("java.lang.Boolean");
                } else if (rsmd.getColumnTypeName(count).equalsIgnoreCase("SMALLINT")) {
                    tableSchemaFieldEntity.setFieldType("java.lang.Short");
                } else if (rsmd.getColumnTypeName(count).equalsIgnoreCase("DECIMAL")) {
                    tableSchemaFieldEntity.setFieldType("java.math.BigDecimal");
                    tableSchemaFieldEntity.setScaleType("explicit");
                    tableSchemaFieldEntity.setPrecision(String.valueOf(rsmd.getPrecision(count)));
                    tableSchemaFieldEntity.setScale(String.valueOf(rsmd.getScale(count)));
                } else {
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