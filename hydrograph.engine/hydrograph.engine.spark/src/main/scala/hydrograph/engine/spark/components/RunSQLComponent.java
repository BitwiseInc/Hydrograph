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
package hydrograph.engine.spark.components;

import com.ibatis.common.jdbc.ScriptRunner;
import hydrograph.engine.core.component.entity.RunSqlEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.component.utils.SafeResourceClose;
import hydrograph.engine.core.constants.Constants;
import hydrograph.engine.spark.components.base.CommandComponentSparkFlow;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The Class RunSQLComponent.
 *
 * @author Bitwise
 *
 */
public class RunSQLComponent extends CommandComponentSparkFlow implements Serializable {

    static final Logger log = Logger.getLogger(RunSQLComponent.class.getName());
    File tempFile = File.createTempFile("query", ".txt", new File("."));
    private AssemblyEntityBase assemblyEntityBase;
    private RunSqlEntity runSqlEntity;

    public RunSQLComponent(AssemblyEntityBase assemblyEntityBase) throws IOException {
        this.assemblyEntityBase = assemblyEntityBase;
        runSqlEntity = (RunSqlEntity) assemblyEntityBase;
    }

    @Override
    public void execute() {
        Properties properties = new Properties();
        Connection conn = null;
        Reader reader = null;

        if (((null != runSqlEntity.getDatabaseConnectionName()) && !(runSqlEntity.getDatabaseConnectionName().trim().isEmpty()))
                && ((null != runSqlEntity.getQueryCommand()) && !(runSqlEntity.getQueryCommand().trim().isEmpty()))) {
            if (runSqlEntity.getDatabaseConnectionName().equalsIgnoreCase("MYSQL")) {
                log.debug("Request received test connection " + runSqlEntity.getDatabaseConnectionName());
                try {
                    properties.setProperty("className", "com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection("jdbc:mysql://" + runSqlEntity.getServerName() + ":" + runSqlEntity.getPortNumber() + "/" + runSqlEntity.getDatabaseName() + "?allowMultiQueries=true", runSqlEntity.getDbUserName(), runSqlEntity.getDbPwd());
                    conn.setAutoCommit(false);
                    scriptBuilder(runSqlEntity.getQueryCommand());
                    ScriptRunner sr = new ScriptRunner(conn, false, true);
                    reader = new BufferedReader(new FileReader(tempFile));
                    sr.runScript(reader);
                } catch (SQLException | IOException | ClassNotFoundException e) {
                    log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                    throw new RuntimeException(e);
                } finally {
                    Exception ex = null;
                    try {
                        SafeResourceClose.safeReaderClose(reader);
                    } catch (IOException e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                        ex = e;
                    }
                    try {
                        tempFile.deleteOnExit();
                    } catch (Exception e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                        ex = e;
                    }
                    try {
                        SafeResourceClose.safeConnectionClose(conn);
                    } catch (SQLException e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                        ex = e;
                    }

                    if(ex != null) {
                        throw new RuntimeException(ex);
                    }
                }
            } else if (runSqlEntity.getDatabaseConnectionName().equalsIgnoreCase("Oracle")) {
                log.debug("Request received test connection " + runSqlEntity.getDatabaseConnectionName());
                try {
                    properties.setProperty("className", "oracle.jdbc.driver.OracleDriver");
                    conn = DriverManager.getConnection("jdbc:oracle:thin://@" + runSqlEntity.getServerName() + ":" + runSqlEntity.getPortNumber() + "/" + runSqlEntity.getDatabaseName(), runSqlEntity.getDbUserName(), runSqlEntity.getDbPwd());
                    conn.setAutoCommit(false);
                    scriptBuilder(runSqlEntity.getQueryCommand());
                    ScriptRunner sr = new ScriptRunner(conn, false, true);
                    reader = new BufferedReader(new FileReader(tempFile));
                    sr.runScript(reader);
                } catch (SQLException | IOException | ClassNotFoundException e) {
                    log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                    throw new RuntimeException(e);
                } finally {
                    Exception ex = null;
                    try {
                        SafeResourceClose.safeReaderClose(reader);
                    } catch (IOException e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                        ex = e;
                    }
                    try {
                        tempFile.deleteOnExit();
                    } catch (Exception e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                        ex = e;
                    }
                    try {
                        SafeResourceClose.safeConnectionClose(conn);
                    } catch (SQLException e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                        ex = e;
                    }

                    if(ex != null) {
                        throw new RuntimeException(ex);
                    }
                }
            } else if (runSqlEntity.getDatabaseConnectionName().equalsIgnoreCase("Teradata")) {
                log.debug("Request received test connection " + runSqlEntity.getDatabaseConnectionName());
                try {
                    properties.setProperty("className", "com.teradata.jdbc.TeraDriver");
                    conn = DriverManager.getConnection("jdbc:teradata://" + runSqlEntity.getServerName() + "/DATABASE=" + runSqlEntity.getDatabaseName() + ",USER=" + runSqlEntity.getDbUserName() + ", " + Constants.TERADATA_PWD + "=" + runSqlEntity.getDbPwd() + ",TMODE=ANSI,CHARSET=UTF8");
                    conn.setAutoCommit(false);
                    scriptBuilder(runSqlEntity.getQueryCommand());
                    ScriptRunner sr = new ScriptRunner(conn, false, true);
                    reader = new BufferedReader(new FileReader(tempFile));
                    sr.runScript(reader);
                } catch (SQLException | IOException | ClassNotFoundException e) {
                    log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                    throw new RuntimeException(e);
                } finally {
                    Exception ex = null;
                    try {
                        SafeResourceClose.safeReaderClose(reader);
                    } catch (IOException e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                        ex = e;
                    }
                    try {
                        tempFile.deleteOnExit();
                    } catch (Exception e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                        ex = e;
                    }
                    try {
                        SafeResourceClose.safeConnectionClose(conn);
                    } catch (SQLException e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                        ex = e;
                    }

                    if(ex != null) {
                        throw new RuntimeException(ex);
                    }
                }
            } else if (runSqlEntity.getDatabaseConnectionName().equalsIgnoreCase("Redshift")) {
                log.debug("Request received test connection " + runSqlEntity.getDatabaseConnectionName());
                try {
                    properties.setProperty("className", "com.amazon.redshift.jdbc42.Driver");
                    conn = DriverManager.getConnection("jdbc:redshift://" + runSqlEntity.getServerName() + ":" + runSqlEntity.getPortNumber() + "/" + runSqlEntity.getDatabaseName(), runSqlEntity.getDbUserName(), runSqlEntity.getDbPwd());
                    conn.setAutoCommit(false);
                    scriptBuilder(runSqlEntity.getQueryCommand());
                    ScriptRunner sr = new ScriptRunner(conn, false, true);
                    reader = new BufferedReader(new FileReader(tempFile));
                    sr.runScript(reader);
                } catch (SQLException | IOException | ClassNotFoundException e) {
                    log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                    throw new RuntimeException(e);
                } finally {
                    try {
                        SafeResourceClose.safeReaderClose(reader);
                    } catch (IOException e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                    }
                    try {
                        tempFile.deleteOnExit();
                    } catch (Exception e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                    }
                    try {
                        SafeResourceClose.safeConnectionClose(conn);
                    } catch (SQLException e) {
                        log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
                    }
                }
            }
        } else {
            log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + "Required field is empty.");
            throw new DatabaseConnectionException("Required field is empty.",null);
        }
    }

    private void scriptBuilder(String statement) throws ClassNotFoundException, SQLException {
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            statement.trim();
            if (statement.toLowerCase().contains("procedure") || statement.toLowerCase().contains("function")) {
                statement = statement.replaceAll("\r", " ");
                statement = statement.replaceAll("\n", " ");
                if (statement.endsWith(";") && !statement.endsWith(";;")) {
                    bufferedWriter.write(statement + ";");
                } else if (!statement.endsWith(";")) {
                    bufferedWriter.write(statement + ";;");
                }
            } else {
                String[] statementArray = statement.split(";");
                if (statementArray.length >= 1) {
                    for (String tempStatement : statementArray)
                        if (tempStatement.trim().length() > 0)
                            bufferedWriter.write(tempStatement + ";\n");
                }
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + e.getMessage());
        } finally {
            try {
                SafeResourceClose.safeWriterClose(bufferedWriter);
            }catch (IOException ex) {
                log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + ex.getMessage());
            }
            try {
                SafeResourceClose.safeWriterClose(fileWriter);
            }catch (IOException ex) {
                log.debug("Failed to Execute" + runSqlEntity.getQueryCommand() + " The error is " + ex.getMessage());
            }
        }
    }

}

class DatabaseConnectionException extends RuntimeException {
    DatabaseConnectionException(String message, Throwable cause) {
        super(message,cause);
    }
}



