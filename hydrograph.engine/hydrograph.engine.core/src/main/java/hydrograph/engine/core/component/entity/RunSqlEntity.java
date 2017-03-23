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
package hydrograph.engine.core.component.entity;

import hydrograph.engine.core.component.entity.base.OperationEntityBase;
/**
 * The Class RunSqlEntity.
 *
 * @author Bitwise
 *
 */
public class RunSqlEntity extends OperationEntityBase {

    private String databaseConnectionName;
    private String serverName;
    private String portNumber;
    private String databaseName;
    private String dbUserName;
    private String db_pwd;
    private String queryCommand;

    public String getDatabaseConnectionName() {
        return databaseConnectionName;
    }

    public void setDatabaseConnectionName(String databaseConnectionName) {
        this.databaseConnectionName = databaseConnectionName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPwd() {
        return db_pwd;
    }

    public void setDbPwd(String db_pwd) {
        this.db_pwd = db_pwd;
    }

    public String getQueryCommand() {
        return queryCommand;
    }

    public void setQueryCommand(String queryCommand) {
        this.queryCommand = queryCommand;
    }
}
