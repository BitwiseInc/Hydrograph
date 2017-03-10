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
package hydrograph.server.utilities;

/**
 * This class holds all the constant values used in debug service project. The
 * constructor of this class has been made private to discourage instantiation
 * of this class
 *
 * @author Prabodh
 */
public class Constants {

    /**
     * The config key for port id set in ServiceConfig.properties file
     */
    public static final String PORT_ID = "portId";
    /**
     * The config key for hdfs-site.xml path set in ServiceConfig.properties
     * file
     */
    public static final String HDFS_SITE_CONFIG_PATH = "hdfsSiteConfigPath";
    /**
     * The config key for hive-site.xml path set in ServiceConfig.properties
     * file
     */
    public static final String HIVE_SITE_CONFIG_PATH = "hiveSiteConfigPath";
    /**
     * The config key for core-site.xml path set in ServiceConfig.properties
     * file
     */
    public static final String CORE_SITE_CONFIG_PATH = "coreSiteConfigPath";
    /**
     * The config key for creating temporary debug file in
     * ServiceConfig.properties file
     */
    public static final String TEMP_LOCATION_PATH = "tempLocationPath";
    public static final String LOCAL_LOCATION_PATH = "localLocationPath";
    /**
     * The config key for enable kerberos property set in
     * ServiceConfig.properties file
     */
    public static final String ENABLE_KERBEROS = "enableKerberos";
    /**
     * The config key for kerberos domain name property set in
     * ServiceConfig.properties file
     */
    public static final String KERBEROS_DOMAIN_NAME = "kerberosDomainName";
    /**
     * The URL parameter for base path sent in the request object to the service
     */
    public static final String BASE_PATH = "basePath";
    /**
     * The URL parameter for job id sent in the request object to the service
     */
    public static final String JOB_ID = "jobId";
    /**
     * The URL parameter for socket id sent in the request object to the service
     */
    public static final String SOCKET_ID = "socketId";
    /**
     * The URL parameter for component id sent in the request object to the
     * service
     */
    public static final String COMPONENT_ID = "componentId";
    /**
     * The URL parameter for user id sent in the request object to the service
     */
    public static final String USER_ID = "userId";
    /**
     * The json key for schema and condition set in ServiceConfig.properties
     * file
     */
    public static final String REQUEST_PARAMETERS = "request_parameters";
    /**
     * The URL parameter for password sent in the request object to the service
     */
    public static final String SERVICE_PWD = "password";
    public static final String FILE_SIZE = "file_size";
    public static final String FIELD_DELIMITER = "file_size";
    public static final String ESCAPE_CHAR = "file_size";
    public static final String QUOTE_CHAR = "file_size";
    /**
     * The default port number to be used for the service
     */
    public static final int DEFAULT_PORT_NUMBER = 8006;
    /**
     * The default record limit to be used to fetch data from avro file
     */
    public static final int DEFAULT_RECORD_LIMIT = 100;
    /**
     * The default record limit to be used to fetch data from avro file
     */
    public static final long DEFAULT_FILE_SIZE = 10485760;
    /**
     * The default record limit to be used to fetch data from avro file
     */
    public static final char DEFAULT_FIELD_DELIMITER = ',';
    public static final char DEFAULT_ESCAPE_CHARACTER = '\\';
    public static final char DEFAULT_QUOTE_CHARACTER = '"';
    public static final String HOST = "host_name";
    /**
     * Constants to define the database name.
     */
    public static final String DATABASE_NAME = "database";
    /**
     * Constants to define the table name.
     */
    public static final String TABLENAME = "table";
    /**
     * Constants used to store the value user while connecting to databases.
     */
    public static final String USERNAME = "username";
    /**
     * HOSTNAME used to connect to database
     */
    public static final String HOST_NAME = "hostname";
    /**
     * Constants used to store the portNo on which database service is running.
     */
    public static final String PORT_NUMBER = "port";
    /**
     * Store the query value for retrieving data from database.
     */
    public static final String QUERY = "query";
    /**
     * dbtype determines at runtime which database type to use. ex:
     * oracle,MySql, etc
     */
    public static final String dbType = "dbtype";
    /**
     * Used to store the database sid name of oracle.
     */
    public static final String SID = "sid";
    /**
     * Used to store the database driverType name.
     */
    public static final String DRIVER_TYPE = "drivertype";
    /**
     * Used to get the class name for oracle
     */
    public static final String oracle = "hydrograph.server.metadata.strategy.OracleMetadataStrategy";
    /**
     * Used to get the class name for hive
     */
    public static final String hive = "hydrograph.server.metadata.strategy.HiveMetadataStrategy";
    /**
     * Used to get the class name for redshift
     */
    public static final String redshift = "hydrograph.server.metadata.strategy.RedshiftMetadataStrategy";
    /**
     * Used to get the class name for mysql
     */
    public static final String mysql = "hydrograph.server.metadata.strategy.MysqlMetadataStrategy";
    /**
     * Used to get the class name for teradata
     */
    public static final String teradata = "hydrograph.server.metadata.strategy.TeradataMetadataStrategy";
    /**
     * checking the ORACLE type
     */
    public static final String ORACLE = "oracle";
    /**
     * checking the HIVE type
     */
    public static final String HIVE = "hive";
    /**
     * checking the REDSHIFT type
     */
    public static final String REDSHIFT = "redshift";
    /**
     * checking the MYSQL type
     */
    public static final String MYSQL = "mysql";
    /**
     * checking the TERADATA type
     */
    public static final String TERADATA = "teradata";
    /**
     * checking the TERADATA type
     */
    public static final String TERADATA_DEFAULT_PORT = "1025";
    /**
     * checking the MYSQL type
     */

    public static final String MYSQL_DEFAULT_PORT = "3306";
    /**
     * checking the ORACLE type
     */
    public static final String ORACLE_DEFAULT_PORT = "1521";
    /**
     * Oracle JDBC driver class name.
     */
    public final static String ORACLE_JDBC_CLASSNAME = "oracle.jdbc.OracleDriver";
    /**
     * MYSQL JDBC driver class name.
     */
    public final static String MYSQL_JDBC_CLASSNAME = "com.mysql.jdbc.Driver";
    /**
     * Mysql query to check for connection status.
     */
    public static final String QUERY_TO_TEST = "SELECT 1 FROM DUAL";
    /**
     * Default redshift query to check for connection status.
     */
    public static final String DEFAULT_REDRESHIFT_QUERY_TO_TEST = "SELECT 1";
    /**
     * Teradata query to check for connection status.
     */
    public static final String QUERY_TO_TEST_TERADATA = "SELECT 1";
    /**
     * TERADATA JDBC driver class name.
     */
    public final static String TERADATA_JDBC_CLASSNAME = "com.teradata.jdbc.TeraDriver";
    /**
     * Redshift JDBC driver class name.
     */
    public static final String REDSHIFT_JDBC_CLASSNAME = "com.amazon.redshift.jdbc42.Driver";

    private Constants() {

    }
}