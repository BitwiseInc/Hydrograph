
/*
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
 */
package hydrograph.engine.jaxb.commandtypes;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.commandtypes package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.commandtypes
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Hplsql }
     * 
     */
    public Hplsql createHplsql() {
        return new Hplsql();
    }

    /**
     * Create an instance of {@link Hplsql.Execute }
     * 
     */
    public Hplsql.Execute createHplsqlExecute() {
        return new Hplsql.Execute();
    }

    /**
     * Create an instance of {@link RunSQL }
     * 
     */
    public RunSQL createRunSQL() {
        return new RunSQL();
    }

    /**
     * Create an instance of {@link RunProgram }
     * 
     */
    public RunProgram createRunProgram() {
        return new RunProgram();
    }

    /**
     * Create an instance of {@link S3FileTransfer }
     * 
     */
    public S3FileTransfer createS3FileTransfer() {
        return new S3FileTransfer();
    }

    /**
     * Create an instance of {@link FtpIn }
     * 
     */
    public FtpIn createFtpIn() {
        return new FtpIn();
    }

    /**
     * Create an instance of {@link Subjob }
     * 
     */
    public Subjob createSubjob() {
        return new Subjob();
    }

    /**
     * Create an instance of {@link FileOperationChoice }
     * 
     */
    public FileOperationChoice createFileOperationChoice() {
        return new FileOperationChoice();
    }

    /**
     * Create an instance of {@link FTP }
     * 
     */
    public FTP createFTP() {
        return new FTP();
    }

    /**
     * Create an instance of {@link SFTP }
     * 
     */
    public SFTP createSFTP() {
        return new SFTP();
    }

    /**
     * Create an instance of {@link Hplsql.Command }
     * 
     */
    public Hplsql.Command createHplsqlCommand() {
        return new Hplsql.Command();
    }

    /**
     * Create an instance of {@link Hplsql.Execute.Query }
     * 
     */
    public Hplsql.Execute.Query createHplsqlExecuteQuery() {
        return new Hplsql.Execute.Query();
    }

    /**
     * Create an instance of {@link Hplsql.Execute.Uri }
     * 
     */
    public Hplsql.Execute.Uri createHplsqlExecuteUri() {
        return new Hplsql.Execute.Uri();
    }

    /**
     * Create an instance of {@link RunSQL.DatabaseConnectionName }
     * 
     */
    public RunSQL.DatabaseConnectionName createRunSQLDatabaseConnectionName() {
        return new RunSQL.DatabaseConnectionName();
    }

    /**
     * Create an instance of {@link RunSQL.ServerName }
     * 
     */
    public RunSQL.ServerName createRunSQLServerName() {
        return new RunSQL.ServerName();
    }

    /**
     * Create an instance of {@link RunSQL.PortNumber }
     * 
     */
    public RunSQL.PortNumber createRunSQLPortNumber() {
        return new RunSQL.PortNumber();
    }

    /**
     * Create an instance of {@link RunSQL.DatabaseName }
     * 
     */
    public RunSQL.DatabaseName createRunSQLDatabaseName() {
        return new RunSQL.DatabaseName();
    }

    /**
     * Create an instance of {@link RunSQL.DbUserName }
     * 
     */
    public RunSQL.DbUserName createRunSQLDbUserName() {
        return new RunSQL.DbUserName();
    }

    /**
     * Create an instance of {@link RunSQL.DbPassword }
     * 
     */
    public RunSQL.DbPassword createRunSQLDbPassword() {
        return new RunSQL.DbPassword();
    }

    /**
     * Create an instance of {@link RunSQL.QueryCommand }
     * 
     */
    public RunSQL.QueryCommand createRunSQLQueryCommand() {
        return new RunSQL.QueryCommand();
    }

    /**
     * Create an instance of {@link RunProgram.Command }
     * 
     */
    public RunProgram.Command createRunProgramCommand() {
        return new RunProgram.Command();
    }

    /**
     * Create an instance of {@link hydrograph.engine.jaxb.commandtypes.FileTransferBase.Encoding }
     * 
     */
    public hydrograph.engine.jaxb.commandtypes.FileTransferBase.Encoding createFileTransferBaseEncoding() {
        return new hydrograph.engine.jaxb.commandtypes.FileTransferBase.Encoding();
    }

    /**
     * Create an instance of {@link S3FileTransfer.Encoding }
     * 
     */
    public S3FileTransfer.Encoding createS3FileTransferEncoding() {
        return new S3FileTransfer.Encoding();
    }

    /**
     * Create an instance of {@link FtpIn.Host }
     * 
     */
    public FtpIn.Host createFtpInHost() {
        return new FtpIn.Host();
    }

    /**
     * Create an instance of {@link Subjob.Path }
     * 
     */
    public Subjob.Path createSubjobPath() {
        return new Subjob.Path();
    }

}
