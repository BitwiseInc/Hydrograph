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

import hydrograph.engine.core.component.entity.InputRDBMSEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.core.constants.Constants;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.inputtypes.Mysql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


public class InputMysqlEntityGenerator extends
        InputComponentGeneratorBase {

    private Mysql inputMysqlJaxb;
    private InputRDBMSEntity inputRDBMSEntity;
    private static Logger LOG = LoggerFactory
            .getLogger(InputMysqlEntityGenerator.class);

    public InputMysqlEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }


    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        inputMysqlJaxb = (Mysql) baseComponent;
    }

    @Override
    public void createEntity() {
        inputRDBMSEntity = new InputRDBMSEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing input file MySql component: "
                + inputMysqlJaxb.getId());

        inputRDBMSEntity.setComponentId(inputMysqlJaxb.getId());
        inputRDBMSEntity.setBatch(inputMysqlJaxb.getBatch());
        inputRDBMSEntity.setFieldsList(InputEntityUtils.extractInputFields(
                inputMysqlJaxb.getOutSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
        inputRDBMSEntity.setOutSocketList(InputEntityUtils.extractOutSocket(inputMysqlJaxb.getOutSocket()));
        inputRDBMSEntity.setDatabaseName(inputMysqlJaxb.getDatabaseName().getValue());
        inputRDBMSEntity.setHostName(inputMysqlJaxb.getHostName().getValue());

        if (inputMysqlJaxb.getPort() == null)
            LOG.warn("Input Mysql component '" + inputRDBMSEntity.getComponentId() + "' port is not provided, using default port " + Constants.DEFAULT_MYSQL_PORT);
        inputRDBMSEntity.setPort(inputMysqlJaxb.getPort() == null ? Constants.DEFAULT_MYSQL_PORT : inputMysqlJaxb.getPort().getValue().intValue());
        inputRDBMSEntity.setJdbcDriver(inputMysqlJaxb.getJdbcDriver()==null ?null: inputMysqlJaxb.getJdbcDriver().getValue());
        inputRDBMSEntity.setTableName(inputMysqlJaxb.getTableName() == null ? null : inputMysqlJaxb.getTableName().getValue());
        inputRDBMSEntity.setSelectQuery(inputMysqlJaxb.getSelectQuery() == null ? null : inputMysqlJaxb.getSelectQuery().getValue());
        inputRDBMSEntity.setCountQuery(inputMysqlJaxb.getCountQuery()==null?("select count(*) from  (" +inputRDBMSEntity.getSelectQuery()+ ") as alias;"):inputMysqlJaxb.getCountQuery().getValue());
        inputRDBMSEntity.setUsername(inputMysqlJaxb.getUsername().getValue());
        inputRDBMSEntity.setPassword(inputMysqlJaxb.getPassword().getValue());
//		inputRDBMSEntity.setFetchSize(inputMysqlJaxb.getFetchSize()==null?Constants.DEFAULT_DB_FETCHSIZE:inputMysqlJaxb.getFetchSize().getValue().intValue());
        inputRDBMSEntity.setRuntimeProperties(inputMysqlJaxb.getRuntimeProperties() == null ? new Properties():InputEntityUtils
                        .extractRuntimeProperties(inputMysqlJaxb.getRuntimeProperties()));

        inputRDBMSEntity.setDatabaseType("Mysql");
    }

    @Override
    public InputRDBMSEntity getEntity() {
        return inputRDBMSEntity;
    }

}