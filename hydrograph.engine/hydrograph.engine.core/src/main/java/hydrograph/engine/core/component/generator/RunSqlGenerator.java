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
package hydrograph.engine.core.component.generator;

import hydrograph.engine.core.component.entity.RunSqlEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.component.generator.base.CommandComponentGeneratorBase;
import hydrograph.engine.jaxb.commandtypes.RunSQL;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
/**
 * The Class RunSqlGenerator.
 *
 * @author Bitwise
 *
 */
public class RunSqlGenerator extends CommandComponentGeneratorBase {


    private RunSQL runSQL;
    private RunSqlEntity runSqlEntity;

    /**
     * The constructor accepts a {@link TypeBaseComponent} object returned by
     * jaxb and calls the methods to create and initialize the entity object
     * using the {@link TypeBaseComponent} object
     *
     * @param typeCommandComponent
     */
    public RunSqlGenerator(TypeBaseComponent typeCommandComponent) {
        super(typeCommandComponent);
    }

    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        runSQL = (RunSQL) baseComponent;
    }

    @Override
    public void createEntity() {
        runSqlEntity = new RunSqlEntity();
    }

    @Override
    public void initializeEntity() {

        runSqlEntity.setComponentId(runSQL.getId());
        runSqlEntity.setBatch(runSQL.getBatch());
        runSqlEntity.setComponentName(runSQL.getName());
        runSqlEntity.setDatabaseConnectionName(runSQL.getDatabaseConnectionName().getDatabaseConnectionName());
        runSqlEntity.setDatabaseName(runSQL.getDatabaseName().getDatabaseName());
        runSqlEntity.setDbPwd(runSQL.getDbPassword().getPassword());
        runSqlEntity.setPortNumber(runSQL.getPortNumber() != null ? runSQL.getPortNumber().getPortNumber() : "");
        runSqlEntity.setDbUserName(runSQL.getDbUserName().getUserName());
        runSqlEntity.setQueryCommand(runSQL.getQueryCommand().getQuery());
        runSqlEntity.setServerName(runSQL.getServerName().getIpAddress());
    }

    @Override
    public AssemblyEntityBase getEntity() {
        return runSqlEntity;
    }
}
