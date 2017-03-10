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

/**
 * Created by AniruddhaS on 12/27/2016.
 */


import hydrograph.engine.core.component.entity.InputRDBMSEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.core.constants.Constants;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;

import hydrograph.engine.jaxb.inputtypes.Teradata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


public class InputTeradataEntityGenerator extends
        InputComponentGeneratorBase {

    private Teradata inputTeradataJaxb;
    private InputRDBMSEntity inputRDBMSEntity;
    private static Logger LOG = LoggerFactory
            .getLogger(InputTeradataEntityGenerator.class);

    public InputTeradataEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }


    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        inputTeradataJaxb = (Teradata) baseComponent;
    }

    @Override
    public void createEntity() {
        inputRDBMSEntity = new InputRDBMSEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing input file Teradata component: "
                + inputTeradataJaxb.getId());
        inputRDBMSEntity.setComponentId(inputTeradataJaxb.getId());
        inputRDBMSEntity.setBatch(inputTeradataJaxb.getBatch());
        inputRDBMSEntity.setFieldsList(InputEntityUtils.extractInputFields(
                inputTeradataJaxb.getOutSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
        inputRDBMSEntity.setOutSocketList(InputEntityUtils.extractOutSocket(inputTeradataJaxb.getOutSocket()));
        inputRDBMSEntity.setDatabaseName(inputTeradataJaxb.getDatabaseName().getValue());
        inputRDBMSEntity.setHostName(inputTeradataJaxb.getHostName().getValue());
        inputRDBMSEntity.set_interface(inputTeradataJaxb.getExportOptions().getValue());
        if (inputTeradataJaxb.getPort() == null)
            LOG.warn("Input Teradata component '" + inputRDBMSEntity.getComponentId() + "' port is not provided, using default port " + Constants.DEFAULT_TERADATA_PORT);
        inputRDBMSEntity.setPort(inputTeradataJaxb.getPort() == null ? Constants.DEFAULT_TERADATA_PORT : inputTeradataJaxb.getPort().getValue().intValue());
        inputRDBMSEntity.setJdbcDriver(inputTeradataJaxb.getJdbcDriver()==null ?null: inputTeradataJaxb.getJdbcDriver().getValue());
        inputRDBMSEntity.setTableName(inputTeradataJaxb.getTableName() == null ? null : inputTeradataJaxb.getTableName().getValue());
        inputRDBMSEntity.setSelectQuery(inputTeradataJaxb.getSelectQuery() == null ? null : inputTeradataJaxb.getSelectQuery().getValue());
        inputRDBMSEntity.setUsername(inputTeradataJaxb.getUsername().getValue());
        inputRDBMSEntity.setPassword(inputTeradataJaxb.getPassword().getValue());
//		inputRDBMSEntity.setFetchSize(inputTeradataJaxb.getFetchSize()==null?Constants.DEFAULT_DB_FETCHSIZE:inputTeradataJaxb.getFetchSize().getValue().intValue());
        inputRDBMSEntity.setRuntimeProperties(inputTeradataJaxb.getRuntimeProperties() == null ? new Properties():InputEntityUtils
                .extractRuntimeProperties(inputTeradataJaxb.getRuntimeProperties()));

        inputRDBMSEntity.setDatabaseType("Teradata");
    }

    @Override
    public InputRDBMSEntity getEntity() {
        return inputRDBMSEntity;
    }

}