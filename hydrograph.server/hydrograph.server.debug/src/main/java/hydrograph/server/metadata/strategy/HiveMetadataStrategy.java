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
package hydrograph.server.metadata.strategy;

import hydrograph.server.metadata.entity.TableEntity;
import hydrograph.server.metadata.entity.TableSchemaFieldEntity;
import hydrograph.server.metadata.exception.ParamsCannotBeNullOrEmpty;
import hydrograph.server.metadata.strategy.base.MetadataStrategyTemplate;
import hydrograph.server.utilities.Constants;
import hydrograph.server.utilities.ServiceUtilities;
import hydrograph.server.utilities.kerberos.KerberosUtilities;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.*;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Concrete implementation for to retrieve hive meta store details.
 * </p>
 * <p>
 * This class requires kerberos token for security purpose authentication.
 * </p>
 */
public class HiveMetadataStrategy extends MetadataStrategyTemplate{

    /**
     * Used to set the connection for RedShift
     *
     * @param connectionProperties - contain request params details
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setConnection(Map connectionProperties) {
        String userId = connectionProperties.getOrDefault(Constants.USERNAME, new ParamsCannotBeNullOrEmpty(
                Constants.USERNAME + " not found in request parameter")).toString();
        String service_pwd = connectionProperties.getOrDefault(Constants.SERVICE_PWD, new ParamsCannotBeNullOrEmpty(
                Constants.SERVICE_PWD + " not found in request parameter")).toString();
        String databaseName = connectionProperties.getOrDefault(Constants.DATABASE_NAME, new ParamsCannotBeNullOrEmpty(
                Constants.DATABASE_NAME + " name not found in request parameter")).toString();
        String tableName = connectionProperties.getOrDefault(Constants.TABLENAME, new ParamsCannotBeNullOrEmpty(
                Constants.TABLENAME + " not found in request parameter")).toString();

        Configuration conf = new Configuration();
        KerberosUtilities kerberosUtilities = new KerberosUtilities(userId, service_pwd, conf);


        // load hdfs-site.xml and core-site.xml
        String hdfsConfigPath = ServiceUtilities.getServiceConfigResourceBundle()
                .getString(Constants.HDFS_SITE_CONFIG_PATH);
        String coreSiteConfigPath = ServiceUtilities.getServiceConfigResourceBundle()
                .getString(Constants.CORE_SITE_CONFIG_PATH);
        LOG.debug("Loading hdfs-site.xml:" + hdfsConfigPath);
        conf.addResource(new Path(hdfsConfigPath));
        LOG.debug("Loading hdfs-site.xml:" + coreSiteConfigPath);
        conf.addResource(new Path(coreSiteConfigPath));

        kerberosUtilities.login();

        this.hiveConf = new HiveConf();
        String pathToHiveSiteXml = ServiceUtilities.getServiceConfigResourceBundle()
                .getString(Constants.HIVE_SITE_CONFIG_PATH);

        if (pathToHiveSiteXml.equals(null) || pathToHiveSiteXml.equals("")) {
            LOG.error("Error loading hive-site.xml: Path to hive-site.xml should not be null or empty.");
            throw new RuntimeException(
                    "Error loading hive-site.xml: Path to hive-site.xml should not be null or empty.");
        }
        LOG.debug("Loading hive-site.xml: " + pathToHiveSiteXml);
        hiveConf.addResource(new Path(pathToHiveSiteXml));

        HiveMetaStoreClient client;
        try {
            client = new HiveMetaStoreClient(hiveConf);
            this.table = client
                    .getTable(databaseName, tableName);
            this.storageDescriptor = table.getSd();
        } catch (MetaException e) {
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchObjectException e) {
            throw new RuntimeException(e.getMessage());
        } catch (TException e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            kerberosUtilities.logout();
        }

    }

    /**
     * @param componentSchemaProperties - Contain request parameter details
     * @return {@link TableEntity}
     */
    @SuppressWarnings("unchecked")
    @Override
    public TableEntity fillComponentSchema(Map componentSchemaProperties) {
        // hiveTableEntity = new HiveTableSchema(database, tableName);
        hiveTableEntity = new TableEntity();
        hiveTableEntity
                .setDatabaseName(
                        componentSchemaProperties
                                .getOrDefault(Constants.DATABASE_NAME,
                                        new ParamsCannotBeNullOrEmpty(
                                                Constants.DATABASE_NAME + " not found in request parameter"))
                                .toString());
        hiveTableEntity.setTableName(componentSchemaProperties
                .getOrDefault(Constants.TABLENAME,
                        new ParamsCannotBeNullOrEmpty(Constants.TABLENAME + " not found in request parameter"))
                .toString());
        fillHiveTableSchema();
        hiveTableEntity = getHiveTableSchema();
        return hiveTableEntity;
    }

    private enum InputOutputFormat {
        PARQUET("parquet"), TEXTDELIMITED("textdelimited"), SEQUENCE("sequence");

        private String name;

        InputOutputFormat(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(HiveMetadataStrategy.class);
    HiveConf hiveConf = null;
    TableEntity hiveTableEntity = null;
    StorageDescriptor storageDescriptor = null;
    Table table;
    boolean isTableExternal;

    private void fillHiveTableSchema() {
        setTableLocation();
        setExternalTable();
        setInputOutputFormat();
        setPartitionKeys();
        setFieldDelimiter();
        setOwner();
        fillFieldSchema();
    }

    private void setExternalTable() {

        if (checkIfhiveTableIsExternal())
            this.hiveTableEntity.setExternalTableLocation(storageDescriptor.getLocation());

    }

    private boolean checkIfhiveTableIsExternal() {

        String hiveWarehouseDir = hiveConf.get("hive.metastore.warehouse.dir");
        if (!storageDescriptor.getLocation().contains(hiveWarehouseDir))
            isTableExternal = true;

        return isTableExternal;

    }

    private void fillFieldSchema() {

        List<FieldSchema> columns = storageDescriptor.getCols();
        List<FieldSchema> partitionKeys = table.getPartitionKeys();
        List<TableSchemaFieldEntity> listOfHiveTableSchemaFieldEntity = new ArrayList<TableSchemaFieldEntity>();
        fillHiveTableSchemaFields(columns, listOfHiveTableSchemaFieldEntity);
        fillHiveTableSchemaFields(partitionKeys, listOfHiveTableSchemaFieldEntity);
        this.hiveTableEntity.setSchemaFields(listOfHiveTableSchemaFieldEntity);
    }

    private void fillHiveTableSchemaFields(List<FieldSchema> columns,
                                           List<TableSchemaFieldEntity> listOfHiveTableSchemaFieldEntity) {
        for (FieldSchema fieldSchema : columns) {
            TableSchemaFieldEntity hiveSchemaField = new TableSchemaFieldEntity();
            hiveSchemaField = fillHiveTableSchemaField(fieldSchema);
            listOfHiveTableSchemaFieldEntity.add(hiveSchemaField);
        }
    }

    private TableSchemaFieldEntity fillHiveTableSchemaField(FieldSchema fieldSchema) {

        TableSchemaFieldEntity hiveSchemaField = new TableSchemaFieldEntity();
        hiveSchemaField.setFieldName(fieldSchema.getName());
        if (fieldSchema.getType().equals("string")) {
            hiveSchemaField.setFieldType("java.lang.String");
        } else if (fieldSchema.getType().equals("int")) {
            hiveSchemaField.setFieldType("java.lang.Integer");
        } else if (fieldSchema.getType().equals("bigint")) {
            hiveSchemaField.setFieldType("java.lang.Long");
        } else if (fieldSchema.getType().equals("smallint")) {
            hiveSchemaField.setFieldType("java.lang.Short");
        } else if (fieldSchema.getType().equals("date")) {
            hiveSchemaField.setFieldType("java.util.Date");
        } else if (fieldSchema.getType().equals("timestamp")) {
            hiveSchemaField.setFieldType("java.util.Date");
        } else if (fieldSchema.getType().equals("double")) {
            hiveSchemaField.setFieldType("java.lang.Double");
        } else if (fieldSchema.getType().equals("boolean")) {
            hiveSchemaField.setFieldType("java.lang.Boolean");
        } else if (fieldSchema.getType().equals("float")) {
            hiveSchemaField.setFieldType("java.lang.Float");
        } else if (fieldSchema.getType().contains("decimal")) {
            hiveSchemaField.setFieldType("java.math.BigDecimal");
            hiveSchemaField.setScale(getScale(fieldSchema.getType()));
            hiveSchemaField.setPrecision(getPrecision(fieldSchema.getType()));
        }
        return hiveSchemaField;
    }

    private String getScalePrecision(String typeWithScale, int index) {

        String pattern = "decimal\\((\\d+),(\\d+)\\)";
        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(typeWithScale);
        if (m.find()) {
            return m.group(index);
        } else {
            return "-999";
        }
    }

    private String getScale(String typeWithScale) {
        return getScalePrecision(typeWithScale, 2);
    }

    private String getPrecision(String typeWithPrecision) {
        return getScalePrecision(typeWithPrecision, 1);
    }

    private void setPartitionKeys() {

        List<String> listOfPartitionKeys = new ArrayList<String>();
        for (FieldSchema fieldSchema : table.getPartitionKeys()) {
            listOfPartitionKeys.add(fieldSchema.getName());
        }
        this.hiveTableEntity.setPartitionKeys(listOfPartitionKeys.toString().replace("[", "").replace("]", ""));
    }

    private void setInputOutputFormat() {

        if (storageDescriptor.getInputFormat().contains("parquet")) {
            this.hiveTableEntity.setInputOutputFormat(InputOutputFormat.PARQUET.getName());
        } else if (storageDescriptor.getInputFormat().contains("sequence")) {
            this.hiveTableEntity.setInputOutputFormat(InputOutputFormat.SEQUENCE.getName());
        } else {
            this.hiveTableEntity.setInputOutputFormat(InputOutputFormat.TEXTDELIMITED.getName());
        }
    }

    private void setTableLocation() {

        this.hiveTableEntity.setLocation(storageDescriptor.getLocation());
    }

    private void setFieldDelimiter() {
        SerDeInfo serDeInfo = storageDescriptor.getSerdeInfo();
        this.hiveTableEntity.setFieldDelimiter(serDeInfo.getParameters().get("field.delim"));

    }

    private void setOwner() {

        this.hiveTableEntity.setOwner(table.getOwner());
    }

    public TableEntity getHiveTableSchema() {
        return hiveTableEntity;
    }

}