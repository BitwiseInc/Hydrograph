/*******************************************************************************
 *  Copyright 2016, 2017 Capital One Services, LLC and Bitwise, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *******************************************************************************/
package hydrograph.server.service;

import cascading.lingual.type.SQLTimestampCoercibleType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import hydrograph.server.debug.antlr.parser.QueryParserLexer;
import hydrograph.server.debug.antlr.parser.QueryParserParser;
import hydrograph.server.debug.lingual.LingualFilter;
import hydrograph.server.debug.lingual.json.RemoteFilterJson;
import hydrograph.server.debug.lingual.querygenerator.LingualQueryCreator;
import hydrograph.server.execution.websocket.ExecutionTrackingWebsocketHandler;
import hydrograph.server.metadata.exception.ParamsCannotBeNullOrEmpty;
import hydrograph.server.metadata.exception.TableOrQueryParamNotFound;
import hydrograph.server.metadata.strategy.*;
import hydrograph.server.utilities.Constants;
import hydrograph.server.utilities.ServiceUtilities;
import hydrograph.server.utilities.kerberos.KerberosUtilities;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static spark.Spark.webSocket;

/**
 * @author Bitwise
 */
public class HydrographService {

    private static final Logger LOG = LoggerFactory.getLogger(HydrographService.class);

    public HydrographService() {
    }

    public static void main(String[] args) {
        HydrographService service = new HydrographService();
        service.start();
    }

    private void start() {
        int portNumber = Constants.DEFAULT_PORT_NUMBER;
        try {
            portNumber = Integer
                    .parseInt(ServiceUtilities.getServiceConfigResourceBundle().getString(Constants.PORT_ID));
            LOG.debug("Port number '" + portNumber + "' fetched from properties file");
        } catch (Exception e) {
            LOG.error("Error fetching port number. Defaulting to " + Constants.DEFAULT_PORT_NUMBER, e);
        }

        /**
         * Setting Port number to the server
         */
        Spark.port(portNumber);

        /**
         * Creating Websocket on Server for Execution tracking service.
         */
        webSocket("/executionTracking", ExecutionTrackingWebsocketHandler.class);

        Spark.post("/getConnectionStatus", new Route() {
            @SuppressWarnings({"unchecked", "rawtypes", "unused"})
            @Override
            public Object handle(Request request, Response response)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException, JSONException {
                LOG.info("************************getConnectionStatus endpoint - started************************");
                LOG.info("+++ Start: " + new Timestamp((new Date()).getTime()));
                ObjectMapper objectMapper = new ObjectMapper();
                String requestParameters = request.queryParams(Constants.REQUEST_PARAMETERS), dbClassName = null,
                        objectAsString = null;
                JSONObject requestParameterValues = new JSONObject(requestParameters);
                Map metadataProperties = extractingJsonObjects(requestParameterValues);
                String dbTypeToTest = metadataProperties
                        .getOrDefault(Constants.dbType,
                                new ParamsCannotBeNullOrEmpty(Constants.dbType + " Cannot be null or empty"))
                        .toString();
                switch (dbTypeToTest.toLowerCase()) {
                    case Constants.ORACLE:
                        try {
                            if (ServiceUtilities.getConnectionStatus(metadataProperties, Constants.ORACLE_JDBC_CLASSNAME,
                                    Constants.QUERY_TO_TEST)) {
                                LOG.trace("Connection Successful");
                                objectAsString = objectMapper
                                        .writeValueAsString("Connection To Oracle database is Successful");
                            } else {
                                LOG.trace("Connection UnSuccessful");
                                objectAsString = objectMapper
                                        .writeValueAsString("Connection To Oracle database UnSuccessful");
                            }
                        } catch (Exception e) {
                            LOG.error("Connection fails with exception : " + e);
                            objectAsString = e.getLocalizedMessage();
                        }
                        break;
                    case Constants.MYSQL:
                        try {
                            if (ServiceUtilities.getConnectionStatus(metadataProperties, Constants.MYSQL_JDBC_CLASSNAME,
                                    Constants.QUERY_TO_TEST)) {
                                LOG.trace("Connection Successful");
                                objectAsString = objectMapper
                                        .writeValueAsString("Connection To MySQL database is Successful");
                            } else {
                                LOG.trace("Connection UnSuccessful");
                                objectAsString = objectMapper
                                        .writeValueAsString("Connection To MySQL database UnSuccessful");
                            }
                        } catch (Exception e) {
                            LOG.error("Connection fails with exception : " + e);
                            objectAsString = e.getLocalizedMessage();
                        }
                        break;

                    case Constants.REDSHIFT:
                        try {
                            if (ServiceUtilities.getConnectionStatus(metadataProperties, Constants.REDSHIFT_JDBC_CLASSNAME,
                                    Constants.DEFAULT_REDRESHIFT_QUERY_TO_TEST)) {
                                LOG.trace("Connection Successful");
                                objectAsString = objectMapper
                                        .writeValueAsString("Connection To Redshift database is Successful");
                            } else {
                                LOG.trace("Connection UnSuccessful");
                                objectAsString = objectMapper
                                        .writeValueAsString("Connection To Redshift database UnSuccessful");
                            }
                        } catch (Exception e) {
                            LOG.error("Connection fails with exception : " + e);
                            objectAsString = e.getLocalizedMessage();
                        }
                        break;
                    case Constants.TERADATA:
                        try {
                            if (ServiceUtilities.getConnectionStatus(metadataProperties, Constants.TERADATA_JDBC_CLASSNAME,
                                    Constants.QUERY_TO_TEST_TERADATA)) {
                                LOG.trace("Connection Successful");
                                objectAsString = objectMapper
                                        .writeValueAsString("Connection To Teradata database is Successful");
                            } else {
                                LOG.trace("Connection UnSuccessful");
                                objectAsString = objectMapper
                                        .writeValueAsString("Connection To Teradata database UnSuccessful");
                            }
                        } catch (Exception e) {
                            LOG.error("Connection fails with exception : " + e);
                            objectAsString = e.getLocalizedMessage();
                        }
                        break;
                }
                return objectAsString;
            }

            @SuppressWarnings({"unchecked", "rawtypes"})
            private Map extractingJsonObjects(JSONObject requestParameterValues) throws JSONException {

                String dbType = null, userId = null, password = null, host = null, port = null, sid = null,
                        driverType = null, query = null, tableName = null, database = null;
                Map metadataProperties = new HashMap();
                if (!requestParameterValues.isNull(Constants.dbType)) {
                    dbType = requestParameterValues.getString(Constants.dbType);
                    metadataProperties.put(Constants.dbType, dbType);
                }
                if (!requestParameterValues.isNull(Constants.USERNAME)) {
                    userId = requestParameterValues.getString(Constants.USERNAME);
                    metadataProperties.put(Constants.USERNAME, userId);
                }
                if (!requestParameterValues.isNull(Constants.SERVICE_PWD)) {
                    password = requestParameterValues.getString(Constants.SERVICE_PWD);
                    metadataProperties.put(Constants.SERVICE_PWD, password);
                }
                if (!requestParameterValues.isNull(Constants.HOST_NAME)) {
                    host = requestParameterValues.getString(Constants.HOST_NAME);
                    metadataProperties.put(Constants.HOST_NAME, host);
                }
                if (!requestParameterValues.isNull(Constants.PORT_NUMBER)) {
                    port = requestParameterValues.getString(Constants.PORT_NUMBER);
                    metadataProperties.put(Constants.PORT_NUMBER, port);
                } else {
                    if (metadataProperties.get(Constants.dbType).toString().equalsIgnoreCase("mysql")) {
                        port = Constants.MYSQL_DEFAULT_PORT;
                        metadataProperties.put(Constants.PORT_NUMBER, port);

                    } else if (metadataProperties.get(Constants.dbType).toString().equalsIgnoreCase("oracle")) {
                        port = Constants.ORACLE_DEFAULT_PORT;
                        metadataProperties.put(Constants.PORT_NUMBER, port);
                    }
                    LOG.info("Connecting " + dbType + " port is not provided using default port : " + port);
                }
                if (!requestParameterValues.isNull(Constants.SID)) {
                    sid = requestParameterValues.getString(Constants.SID);
                    metadataProperties.put(Constants.SID, sid);
                }
                if (!requestParameterValues.isNull(Constants.DRIVER_TYPE)) {
                    driverType = requestParameterValues.getString(Constants.DRIVER_TYPE);
                    metadataProperties.put(Constants.DRIVER_TYPE, driverType);
                }
                if (!requestParameterValues.isNull(Constants.QUERY)) {
                    query = requestParameterValues.getString(Constants.QUERY);
                    metadataProperties.put(Constants.QUERY, query);
                }
                if (!requestParameterValues.isNull(Constants.TABLENAME)) {
                    tableName = requestParameterValues.getString(Constants.TABLENAME);
                    metadataProperties.put(Constants.TABLENAME, tableName);
                }
                if (!requestParameterValues.isNull(Constants.DATABASE_NAME)) {
                    database = requestParameterValues.getString(Constants.DATABASE_NAME);
                    metadataProperties.put(Constants.DATABASE_NAME, database);
                }

                LOG.info("Fetched request parameters are: " + Constants.dbType + " => " + dbType + " "
                        + Constants.USERNAME + " => " + userId + " " + Constants.HOST_NAME + " => " + host + " "
                        + Constants.PORT_NUMBER + " => " + port + " " + Constants.SID + " => " + sid + " "
                        + Constants.DRIVER_TYPE + " => " + driverType + " " + Constants.QUERY + " => " + query + " "
                        + Constants.TABLENAME + " => " + tableName + " " + Constants.DATABASE_NAME + " => " + database
                        + " ");
                return metadataProperties;
            }
        });

        Spark.post("readFromMetastore", new Route() {

            @Override
            public Object handle(Request request, Response response)
                    throws ParamsCannotBeNullOrEmpty, ClassNotFoundException, IllegalAccessException, JSONException,
                    JsonProcessingException, TableOrQueryParamNotFound, SQLException, InstantiationException {
                LOG.info("************************readFromMetastore endpoint - started************************");
                LOG.info("+++ Start: " + new Timestamp((new Date()).getTime()));
                ObjectMapper objectMapper = new ObjectMapper();
                String requestParameters = request.queryParams(Constants.REQUEST_PARAMETERS), objectAsString = null,
                        dbClassName = null;
                JSONObject requestParameterValues = new JSONObject(requestParameters);
                // Method to extracting request parameter details from input
                // json.
                Map metadataProperties = extractingJsonObjects(requestParameterValues);

                String dbType = metadataProperties
                        .getOrDefault(Constants.dbType,
                                new ParamsCannotBeNullOrEmpty(Constants.dbType + " Cannot be null or empty"))
                        .toString();
                LOG.info("Retrieving schema for " + dbType + " Database.");
                try {
                    switch (dbType.toLowerCase()) {
                        case Constants.ORACLE:
                            dbClassName = Constants.oracle;
                            OracleMetadataStrategy oracleMetadataHelper = (OracleMetadataStrategy) Class
                                    .forName(dbClassName).newInstance();
                            oracleMetadataHelper.setConnection(metadataProperties);
                            objectAsString = objectMapper
                                    .writeValueAsString(oracleMetadataHelper.fillComponentSchema(metadataProperties));
                            LOG.trace("Schema json for oracle : " + objectAsString);
                            LOG.info("+++ Stop: " + new Timestamp((new Date()).getTime()));
                            break;
                        case Constants.HIVE:
                            dbClassName = Constants.hive;
                            HiveMetadataStrategy hiveMetadataHelper = (HiveMetadataStrategy) Class.forName(dbClassName)
                                    .newInstance();
                            hiveMetadataHelper.setConnection(metadataProperties);
                            objectAsString = objectMapper
                                    .writeValueAsString(hiveMetadataHelper.fillComponentSchema(metadataProperties));
                            LOG.trace("Schema json for hive : " + objectAsString);
                            LOG.info("+++ Stop: " + new Timestamp((new Date()).getTime()));
                            break;
                        case Constants.REDSHIFT:
                            dbClassName = Constants.redshift;
                            RedshiftMetadataStrategy redShiftMetadataHelper = (RedshiftMetadataStrategy) Class
                                    .forName(dbClassName).newInstance();
                            redShiftMetadataHelper.setConnection(metadataProperties);
                            objectAsString = objectMapper
                                    .writeValueAsString(redShiftMetadataHelper.fillComponentSchema(metadataProperties));
                            LOG.trace("Schema json for redshift : " + objectAsString);
                            LOG.info("+++ Stop: " + new Timestamp((new Date()).getTime()));
                            break;
                        case Constants.MYSQL:
                            dbClassName = Constants.mysql;
                            MysqlMetadataStrategy mysqlMetadataHelper = (MysqlMetadataStrategy) Class.forName(dbClassName)
                                    .newInstance();
                            mysqlMetadataHelper.setConnection(metadataProperties);
                            objectAsString = objectMapper
                                    .writeValueAsString(mysqlMetadataHelper.fillComponentSchema(metadataProperties));
                            LOG.trace("Schema json for mysql : " + objectAsString);
                            LOG.info("+++ Stop: " + new Timestamp((new Date()).getTime()));
                            break;
                        case Constants.TERADATA:
                            dbClassName = Constants.teradata;
                            TeradataMetadataStrategy teradataMetadataHelper = (TeradataMetadataStrategy) Class.forName(dbClassName)
                                    .newInstance();
                            teradataMetadataHelper.setConnection(metadataProperties);
                            objectAsString = objectMapper
                                    .writeValueAsString(teradataMetadataHelper.fillComponentSchema(metadataProperties));
                            LOG.trace("Schema json for teradata : " + objectAsString);
                            LOG.info("+++ Stop: " + new Timestamp((new Date()).getTime()));
                            break;
                    }
                } catch (Exception e) {
                    LOG.error("Metadata read for database  '" + dbType + "' not completed.");
                    LOG.error("Exception : " + e);
                    response.status(400);
                    return "Metadata read for database '" + dbType + "' not completed.";
                }
                LOG.info("Class Name used for " + dbType + " Is : " + dbClassName);
                LOG.debug("Json for " + dbType + " : " + objectAsString);
                return objectAsString;
            }

            @SuppressWarnings({"unchecked", "rawtypes"})
            private Map extractingJsonObjects(JSONObject requestParameterValues) throws JSONException {

                String dbType = null, userId = null, password = null, host = null, port = null, sid = null,
                        driverType = null, query = null, tableName = null, database = null;
                Map metadataProperties = new HashMap();
                if (!requestParameterValues.isNull(Constants.dbType)) {
                    dbType = requestParameterValues.getString(Constants.dbType);
                    metadataProperties.put(Constants.dbType, dbType);
                }
                if (!requestParameterValues.isNull(Constants.USERNAME)) {
                    userId = requestParameterValues.getString(Constants.USERNAME);
                    metadataProperties.put(Constants.USERNAME, userId);
                }
                if (!requestParameterValues.isNull(Constants.SERVICE_PWD)) {
                    password = requestParameterValues.getString(Constants.SERVICE_PWD);
                    metadataProperties.put(Constants.SERVICE_PWD, password);
                }
                if (!requestParameterValues.isNull(Constants.HOST_NAME)) {
                    host = requestParameterValues.getString(Constants.HOST_NAME);
                    metadataProperties.put(Constants.HOST_NAME, host);
                }
                if (!requestParameterValues.isNull(Constants.PORT_NUMBER)) {
                    port = requestParameterValues.getString(Constants.PORT_NUMBER);
                    metadataProperties.put(Constants.PORT_NUMBER, port);
                } else {
                    if (metadataProperties.get(Constants.dbType).toString().equalsIgnoreCase("mysql")) {
                        port = Constants.MYSQL_DEFAULT_PORT;
                        metadataProperties.put(Constants.PORT_NUMBER, port);

                    } else if (metadataProperties.get(Constants.dbType).toString().equalsIgnoreCase("oracle")) {
                        port = Constants.ORACLE_DEFAULT_PORT;
                        metadataProperties.put(Constants.PORT_NUMBER, port);
                    }
                    LOG.info("Connecting " + dbType + " port is not provided using default port : " + port);
                }
                if (!requestParameterValues.isNull(Constants.SID)) {
                    sid = requestParameterValues.getString(Constants.SID);
                    metadataProperties.put(Constants.SID, sid);
                }
                if (!requestParameterValues.isNull(Constants.DRIVER_TYPE)) {
                    driverType = requestParameterValues.getString(Constants.DRIVER_TYPE);
                    metadataProperties.put(Constants.DRIVER_TYPE, driverType);
                }
                if (!requestParameterValues.isNull(Constants.QUERY)) {
                    query = requestParameterValues.getString(Constants.QUERY);
                    metadataProperties.put(Constants.QUERY, query);
                }
                if (!requestParameterValues.isNull(Constants.TABLENAME)) {
                    tableName = requestParameterValues.getString(Constants.TABLENAME);
                    metadataProperties.put(Constants.TABLENAME, tableName);
                }
                if (!requestParameterValues.isNull(Constants.DATABASE_NAME)) {
                    database = requestParameterValues.getString(Constants.DATABASE_NAME);
                    metadataProperties.put(Constants.DATABASE_NAME, database);
                }

                LOG.info("Fetched request parameters are: " + Constants.dbType + " => " + dbType + " "
                        + Constants.USERNAME + " => " + userId + " " + Constants.HOST_NAME + " => " + host + " "
                        + Constants.PORT_NUMBER + " => " + port + " " + Constants.SID + " => " + sid + " "
                        + Constants.DRIVER_TYPE + " => " + driverType + " " + Constants.QUERY + " => " + query + " "
                        + Constants.TABLENAME + " => " + tableName + " " + Constants.DATABASE_NAME + " => " + database
                        + " ");
                return metadataProperties;
            }
        });

        Spark.post("/read", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                LOG.info("************************read endpoint - started************************");
                LOG.info("+++ Start: " + new Timestamp((new Date()).getTime()));
                String jobId = request.queryParams(Constants.JOB_ID);
                String componentId = request.queryParams(Constants.COMPONENT_ID);
                String socketId = request.queryParams(Constants.SOCKET_ID);
                String basePath = request.queryParams(Constants.BASE_PATH);

                // String host = request.queryParams(Constants.HOST);
                String userID = request.queryParams(Constants.USER_ID);
                String password = request.queryParams(Constants.SERVICE_PWD);

                double sizeOfData = Double.parseDouble(request.queryParams(Constants.FILE_SIZE)) * 1024 * 1024;
                LOG.info("Base Path: {}, Job Id: {}, Component Id: {}, Socket ID: {}, User ID:{}, DataSize:{}",
                        basePath, jobId, componentId, socketId, userID, sizeOfData);

                String batchID = jobId + "_" + componentId + "_" + socketId;
                String tempLocationPath = ServiceUtilities.getServiceConfigResourceBundle()
                        .getString(Constants.TEMP_LOCATION_PATH);
                String filePath = tempLocationPath + "/" + batchID + ".csv";
                try {
                    readFileFromHDFS(basePath + "/debug/" + jobId + "/" + componentId + "_" + socketId, sizeOfData,
                            filePath, userID, password);
                    LOG.info("+++ Stop: " + new Timestamp((new Date()).getTime()));
                } catch (Exception e) {
                    LOG.error("Error in reading debug files", e);
                    filePath = "error";
                }
                return filePath;
            }

            /**
             * This method will read the HDFS file, fetch the records from it
             * and write its records to a local file on edge node with size <=
             * {@code sizeOfData} passed in parameter.
             *
             * @param hdfsFilePath   path of HDFS file from where records to be read
             * @param sizeOfData     defines the size of data (in bytes) to be read from
             *                       HDFS file
             * @param remoteFileName after reading the data of {@code sizeOfData} bytes
             *                       from HDFS file, it will be written to local file on
             *                       edge node with file name {@code remoteFileName}
             * @param userId
             * @param password
             */
            private void readFileFromHDFS(String hdfsFilePath, double sizeOfData, String remoteFileName, String userId,
                                          String password) {
                try {
                    Path path = new Path(hdfsFilePath);
                    LOG.debug("Reading Debug file:" + hdfsFilePath);
                    Configuration conf = new Configuration();
                    KerberosUtilities kerberosUtilities = new KerberosUtilities(userId, password, conf);
                    // load hdfs-site.xml and core-site.xml
                    String hdfsConfigPath = ServiceUtilities.getServiceConfigResourceBundle()
                            .getString(Constants.HDFS_SITE_CONFIG_PATH);
                    String coreSiteConfigPath = ServiceUtilities.getServiceConfigResourceBundle()
                            .getString(Constants.CORE_SITE_CONFIG_PATH);
                    LOG.debug("Loading hdfs-site.xml:" + hdfsConfigPath);
                    conf.addResource(new Path(hdfsConfigPath));
                    LOG.debug("Loading hdfs-site.xml:" + coreSiteConfigPath);
                    conf.addResource(new Path(coreSiteConfigPath));
                    // apply kerberos token
                    kerberosUtilities.login();

                    listAndWriteFiles(remoteFileName, path, conf, sizeOfData);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            /**
             * This method will list all files for {@code path}, read all files
             * and writes its data to a local file on edge node with size <=
             * {@code sizeOfData} passed in parameter.
             *
             * @param remoteFileName
             * @param path
             * @param conf
             * @param sizeOfData
             * @throws IOException
             */
            private void listAndWriteFiles(String remoteFileName, Path path, Configuration conf, double sizeOfData)
                    throws IOException {
                FileSystem fs = FileSystem.get(conf);
                FileStatus[] status = fs.listStatus(path);
                File remoteFile = new File(remoteFileName);

                OutputStream os = new FileOutputStream(remoteFileName);
                try {

                    int numOfBytes = 0;
                    for (int i = 0; i < status.length; i++) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(status[i].getPath())));
                        String line = "";
                        line = br.readLine();
                        if (line != null) {
                            // header will only get fetch from first part file
                            // and it
                            // will skip header from remaining files
                            if (numOfBytes == 0) {
                                os.write((line + "\n").toString().getBytes());
                                numOfBytes += line.toString().length();
                            }
                            while ((line = br.readLine()) != null) {
                                numOfBytes += line.toString().length();
                                // line = br.readLine();
                                if (numOfBytes <= sizeOfData) {
                                    os.write((line + "\n").toString().getBytes());
                                } else {
                                    break;
                                }
                            }
                        }
                        br.close();
                        remoteFile.setReadable(true, false);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    os.close();
                    fs.close();
                }
            }

        });

        Spark.post("/delete", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                LOG.info("************************delete endpoint - started************************");
                LOG.info("+++ Start: " + new Timestamp((new Date()).getTime()));
                response.type("text/json");
                String jobId = request.queryParams(Constants.JOB_ID);
                String basePath = request.queryParams(Constants.BASE_PATH);
                String componentId = request.queryParams(Constants.COMPONENT_ID);
                String socketId = request.queryParams(Constants.SOCKET_ID);
                String userID = request.queryParams(Constants.USER_ID);
                String password = request.queryParams(Constants.SERVICE_PWD);

                LOG.info("Base Path: {}, Job Id: {}, Component Id: {}, Socket ID: {}, User ID:{}",
                        basePath, jobId, componentId, socketId, userID);

                try {
                    removeDebugFiles(basePath, jobId, componentId, socketId, userID, password);
                    LOG.info("+++ Stop: " + new Timestamp((new Date()).getTime()));
                } catch (Exception e) {
                    LOG.error("Error in deleting debug files", e);
                }
                return "error";
            }

            private void removeDebugFiles(String basePath, String jobId, String componentId, String socketId,
                                          String userID, String password) {
                try {
                    // DebugFilesReader debugFilesReader = new
                    // DebugFilesReader(basePath, jobId, componentId, socketId,
                    // userID,
                    // password);
                    delete(basePath, jobId, componentId, socketId, userID, password);
                } catch (Exception e) {
                    LOG.error("Error while deleting the debug file", e);
                    throw new RuntimeException(e);
                }
            }

            /**
             * Deletes the jobId directory
             *
             * @param password
             * @param userID
             * @param socketId
             * @param componentId
             * @param jobId
             * @param basePath
             * @throws IOException
             */
            public void delete(String basePath, String jobId, String componentId, String socketId, String userID,
                               String password) throws IOException {
                LOG.trace("Entering method delete()");
                String deletePath = basePath + "/debug/" + jobId;
                Configuration configuration = new Configuration();
                FileSystem fileSystem = FileSystem.get(configuration);
                Path deletingFilePath = new Path(deletePath);
                if (!fileSystem.exists(deletingFilePath)) {
                    throw new PathNotFoundException(deletingFilePath.toString());
                } else {
                    // Delete file
                    fileSystem.delete(deletingFilePath, true);
                    LOG.info("Deleted path : " + deletePath);
                }
                fileSystem.close();
            }
        });

        Spark.post("/deleteLocalDebugFile", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                String error = "";
                LOG.info("+++ Start: " + new Timestamp((new Date()).getTime()));
                LOG.info("************************deleteLocalDebugFile endpoint - started************************");
                try {
                    String jobId = request.queryParams(Constants.JOB_ID);
                    String componentId = request.queryParams(Constants.COMPONENT_ID);
                    String socketId = request.queryParams(Constants.SOCKET_ID);
                    String batchID = jobId + "_" + componentId + "_" + socketId;
                    String tempLocationPath = ServiceUtilities.getServiceConfigResourceBundle()
                            .getString(Constants.TEMP_LOCATION_PATH);

                    LOG.info("Job Id: {}, Component Id: {}, Socket ID: {}, TemporaryPath: {}",
                            jobId, componentId, socketId, tempLocationPath);
                    LOG.debug("File to be deleted: " + tempLocationPath + "/" + batchID + ".csv");
                    File file = new File(tempLocationPath + "/" + batchID + ".csv");
                    file.delete();
                    LOG.trace("Local debug file deleted successfully.");
                    return "Success";
                } catch (Exception e) {
                    LOG.error("Error in deleting local debug file.", e);
                    error = e.getMessage();
                }
                LOG.info("+++ Stop: " + new Timestamp((new Date()).getTime()));
                return "Local file delete failed. Error: " + error;
            }
        });

        // TODO : Keep this for test
        Spark.post("/post", new Route() {

            @Override
            public Object handle(Request request, Response response) {
                LOG.info("****TEST SPARK POST STARTED**********");
                response.type("text/json");
                return "calling post...";
            }
        });

        // TODO : Keep this for test
        Spark.get("/test", new Route() {

            @Override
            public Object handle(Request request, Response response) {
                LOG.info("****TEST SPARK GET STARTED**********");
                response.type("text/json");
                response.status(200);
                response.body("Test successful!");
                return "Test successful!";
            }
        });

        Spark.post("/filter", new Route() {
            @Override
            public Object handle(Request request, Response response) {

                LOG.info("************************filter - started************************");
                LOG.info("+++ Start: " + new Timestamp((new Date()).getTime()));

                Gson gson = new Gson();
                String json = request.queryParams(Constants.REQUEST_PARAMETERS);
                RemoteFilterJson remoteFilterJson = gson.fromJson(json, RemoteFilterJson.class);

                String jobId = remoteFilterJson.getJobDetails().getUniqueJobID();
                String componentId = remoteFilterJson.getJobDetails().getComponentID();
                String socketId = remoteFilterJson.getJobDetails().getComponentSocketID();
                String basePath = remoteFilterJson.getJobDetails().getBasepath();
                String username = remoteFilterJson.getJobDetails().getUsername();
                String password = remoteFilterJson.getJobDetails().getService_pwd();
                double outputFileSizeInMB = remoteFilterJson.getFileSize();
                double sizeOfDataInByte = outputFileSizeInMB * 1024 * 1024;

                String condition = parseSQLQueryToLingualQuery(remoteFilterJson);

                LOG.info("Base Path: {}, Job Id: {}, Component Id: {}, Socket ID: {}, User ID:{}, DataSize:{}",
                        basePath, jobId, componentId, socketId, username, sizeOfDataInByte);

                String batchID = jobId + "_" + componentId + "_" + socketId;

                String tempLocationPath = ServiceUtilities.getServiceConfigResourceBundle()
                        .getString(Constants.TEMP_LOCATION_PATH);

                String filePath = tempLocationPath + "/" + batchID + ".csv";
                String UUID = generateUUID();
                String uniqueId = batchID + "_" + UUID;
                String linugalMetaDataPath = basePath + "/filter/" + UUID;
                Configuration conf = getConfiguration();
                KerberosUtilities kerberosUtilities = new KerberosUtilities(username, password, conf);

                String fieldNames[] = getHeader(basePath + "/debug/" + jobId + "/" + componentId + "_" + socketId,
                        username, password);
                try {
                    HashMap<String, Type> fieldNameAndDatatype = getFieldNameAndType(remoteFilterJson);
                    Type[] fieldTypes = getFieldTypeFromMap(fieldNames, fieldNameAndDatatype);


                    // apply kerberos token
                    kerberosUtilities.login();

                    new LingualFilter().filterData(linugalMetaDataPath, uniqueId,
                            basePath + "/debug/" + jobId + "/" + componentId + "_" + socketId, sizeOfDataInByte,
                            filePath, condition, fieldNames, fieldTypes, conf);

                    LOG.info("debug output path : " + filePath);
                    LOG.info("+++ Stop: " + new Timestamp((new Date()).getTime()));
                } catch (Exception e) {
                    LOG.error("Error in reading debug files", e);
                    return "error";
                } finally {
                    try {
                        System.gc();
                        deleteLingualResult(linugalMetaDataPath);
                    } catch (Exception e) {
                        LOG.error("Error in deleting lingual result", e);
                        return "Error in deleting lingual result: " + e.getMessage();
                    }
                    kerberosUtilities.logout();
                }

                return filePath;
            }

            private Type[] getFieldTypeFromMap(String[] fieldNames, HashMap<String, Type> fieldNameAndDatatype) {
                Type[] type = new Type[fieldNameAndDatatype.size()];
                int i = 0;
                for (String eachFieldName : fieldNames) {
                    type[i++] = fieldNameAndDatatype.get(eachFieldName);
                }
                return type;
            }

            private String[] getHeader(String path, String username, String password) {
                String[] header = readFile(path, username, password);
                return header;
            }

            private String[] readFile(String hdfsFilePath, String username, String password) {
                String[] header = null;
                Configuration conf = getConfiguration();
                KerberosUtilities kerberosUtilities=new KerberosUtilities(username,password,conf);
                try {
                    Path path = new Path(hdfsFilePath);
                    LOG.debug("Reading Debug file:" + hdfsFilePath);


                    kerberosUtilities.login();

                    header = getHeaderArray(path, conf);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally {
                    kerberosUtilities.logout();
                }
                return header;
            }

            private Path filterOutSuccessFile(FileStatus[] fileStatus) {
                for (FileStatus status : fileStatus) {
                    if (status.getPath().getName().toUpperCase().contains("_SUCCESS"))
                        continue;
                    else
                        return status.getPath();
                }
                return null;
            }

            private String[] getHeaderArray(Path path, Configuration conf) throws IOException {
                FileSystem fs = FileSystem.get(conf);
                FileStatus[] status = fs.listStatus(path);
                String line = "";
                try {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(fs.open(filterOutSuccessFile(status))));

                    line = br.readLine();
                    br.close();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    fs.close();
                }
                return line.split(",");
            }

            private Configuration getConfiguration()  {


                    Configuration conf = new Configuration();

                    // load hdfs-site.xml and core-site.xml
                    String hdfsConfigPath = ServiceUtilities.getServiceConfigResourceBundle()
                            .getString(Constants.HDFS_SITE_CONFIG_PATH);
                    String coreSiteConfigPath = ServiceUtilities.getServiceConfigResourceBundle()
                            .getString(Constants.CORE_SITE_CONFIG_PATH);
                    LOG.debug("Loading hdfs-site.xml:" + hdfsConfigPath);
                    conf.addResource(new Path(hdfsConfigPath));
                    LOG.debug("Loading hdfs-site.xml:" + coreSiteConfigPath);
                    conf.addResource(new Path(coreSiteConfigPath));



                return conf;
            }





            private void deleteLingualResult(String deletePath) throws IOException {
                Configuration configuration = new Configuration();
                FileSystem fileSystem = FileSystem.get(configuration);
                Path deletingFilePath = new Path(deletePath);

                if (!fileSystem.exists(deletingFilePath)) {
                    throw new PathNotFoundException(deletingFilePath.toString());
                } else {
                    boolean isDeleted = fileSystem.delete(deletingFilePath, true);
                    if (isDeleted) {
                        fileSystem.deleteOnExit(deletingFilePath);
                    }
                    LOG.info("Deleted path : " + deletePath);
                }

                fileSystem.close();
            }

            private String generateUUID() {
                return String.valueOf(UUID.randomUUID());
            }

            private String parseSQLQueryToLingualQuery(RemoteFilterJson remoteFilterJson) {
                ANTLRInputStream stream = new ANTLRInputStream(remoteFilterJson.getCondition());
                QueryParserLexer lexer = new QueryParserLexer(stream);
                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                QueryParserParser parser = new QueryParserParser(tokenStream);
                parser.removeErrorListeners();
                LingualQueryCreator customVisitor = new LingualQueryCreator(remoteFilterJson.getSchema());
                String condition = customVisitor.visit(parser.eval());
                return condition;
            }

            private HashMap<String, Type> getFieldNameAndType(RemoteFilterJson remoteFilterJson)
                    throws ClassNotFoundException {
                HashMap<String, Type> fieldDataTypeMap = new HashMap<>();
                Type type;
                for (int i = 0; i < remoteFilterJson.getSchema().size(); i++) {
                    Class clazz = Class.forName(remoteFilterJson.getSchema().get(i).getDataTypeValue());
                    if (clazz.getSimpleName().toString().equalsIgnoreCase("Date")) {
                        type = new SQLTimestampCoercibleType();
                    } else {
                        type = clazz;
                    }
                    fieldDataTypeMap.put(remoteFilterJson.getSchema().get(i).getFieldName(), type);
                }
                return fieldDataTypeMap;
            }

        });
    }

}