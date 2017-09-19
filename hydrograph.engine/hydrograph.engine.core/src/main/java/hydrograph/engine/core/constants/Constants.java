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
package hydrograph.engine.core.constants;

/**
 * The Class Constants.
 *
 * @author Bitwise
 *
 */
public class Constants {

    /**
     * The default value of 'scale' attribute of big decimal fields used on input / output schema
     */
    public static final int DEFAULT_SCALE = -999;
    /**
     * The default value of 'precision' attribute of big decimal fields used on input / output schema
     */
    public static final int DEFAULT_PRECISION = -999;
    /**
     * The default port for mysql
     */
    public static final int DEFAULT_MYSQL_PORT = 3306;
    /*
     * The default port for oracle
     */
    public static final int ORACLE_PORT_NUMBER = 1521;
    /*
    * The default port for Redshift
    */
    public static final int REDSHIFT_PORT_NUMBER = 5439;
    /**
     * The default Teradata port
     */
    public static final int DEFAULT_TERADATA_PORT = 1025;
    /**
     * The default chunk size
     */
    public static final int DEFAULT_CHUNKSIZE = 1000;
    public static final String LENGTHS_AND_DELIMITERS_SEPARATOR = "~!@#@!~";
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd";

    public static final String TERADATA_PWD="PASSWORD";

    public static final int DEFAULT_DB_BATCHSIZE = 1000;
    public static final int DEFAULT_FTP_PORT=21;
    public static final int DEFAULT_SFTP_PORT=22;

    public static final String REDSHIFT_DRIVER_NAME = "com.amazon.redshift.jdbc42.Driver";
    /**
     * The default number of record to be read per roundtrip
     */
    //public static final int DEFAULT_DB_FETCHSIZE=1000;
    private Constants() {
    }
}