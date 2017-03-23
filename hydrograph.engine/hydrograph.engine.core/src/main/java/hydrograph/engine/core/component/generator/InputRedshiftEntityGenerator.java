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
import hydrograph.engine.jaxb.inputtypes.Redshift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * The Class InputRedshiftEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class InputRedshiftEntityGenerator extends
        InputComponentGeneratorBase {

    private static Logger LOG = LoggerFactory
            .getLogger(InputRedshiftEntityGenerator.class);
    private Redshift inputRedshiftJaxb;
    private InputRDBMSEntity inputRDBMSEntity;

    public InputRedshiftEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }


    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        inputRedshiftJaxb = (Redshift) baseComponent;
    }

    @Override
    public void createEntity() {
        inputRDBMSEntity = new InputRDBMSEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing input file Redshift component: "
                + inputRedshiftJaxb.getId());
        inputRDBMSEntity.setComponentId(inputRedshiftJaxb.getId());
        inputRDBMSEntity.setFieldsList(InputEntityUtils.extractInputFields(
                inputRedshiftJaxb.getOutSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
        inputRDBMSEntity.setOutSocketList(InputEntityUtils.extractOutSocket(inputRedshiftJaxb.getOutSocket()));
        inputRDBMSEntity.setDatabaseName(inputRedshiftJaxb.getDatabaseName().getValue());
        inputRDBMSEntity.setHostName(inputRedshiftJaxb.getHostName().getValue());
        inputRDBMSEntity.setPort(inputRedshiftJaxb.getPort() == null ? Constants.REDSHIFT_PORT_NUMBER
                : inputRedshiftJaxb.getPort().getValue().intValue());
        inputRDBMSEntity.setRuntimeProperties(inputRedshiftJaxb.getRuntimeProperties() == null ? new Properties() : InputEntityUtils
                .extractRuntimeProperties(inputRedshiftJaxb.getRuntimeProperties()));
        inputRDBMSEntity.setBatch(inputRedshiftJaxb.getBatch());
        inputRDBMSEntity.setJdbcDriver(inputRedshiftJaxb.getJdbcDriver() == null ? null : inputRedshiftJaxb.getJdbcDriver().getValue());
        inputRDBMSEntity.setSelectQuery(inputRedshiftJaxb.getSelectQuery() == null ? null : inputRedshiftJaxb.getSelectQuery().getValue());
        inputRDBMSEntity.setTableName(inputRedshiftJaxb.getTableName() == null ? null : inputRedshiftJaxb.getTableName().getValue());
        inputRDBMSEntity.setCountQuery(inputRedshiftJaxb.getCountQuery() == null
                ? "select count(*) from  (" + inputRDBMSEntity.getSelectQuery() + ")"
                : inputRedshiftJaxb.getCountQuery().getValue());
        inputRDBMSEntity.setUsername(inputRedshiftJaxb.getUserName().getValue());
        inputRDBMSEntity.setPassword(inputRedshiftJaxb.getPassword().getValue());
        if (inputRedshiftJaxb.getSchemaName() != null) {
            inputRDBMSEntity.setSchemaName(inputRedshiftJaxb.getSchemaName().getValue());
            inputRDBMSEntity.setTableName(
                    inputRedshiftJaxb.getSchemaName().getValue() + "." + inputRedshiftJaxb.getTableName().getValue());
        } else {
            inputRDBMSEntity.setSchemaName(null);
        }
    }

    @Override
    public InputRDBMSEntity getEntity() {
        return inputRDBMSEntity;
    }


}