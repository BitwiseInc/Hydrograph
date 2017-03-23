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
import hydrograph.engine.jaxb.inputtypes.Sparkredshift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * The Class InputSparkRedshiftEntityGenerator.
 *
 * @author Bitwise
 *
 */

public class InputSparkRedshiftEntityGenerator extends
        InputComponentGeneratorBase {

    private static Logger LOG = LoggerFactory
            .getLogger(InputSparkRedshiftEntityGenerator.class);
    private Sparkredshift inputSparkRedshiftJaxb;
    private InputRDBMSEntity inputRDBMSEntity;

    public InputSparkRedshiftEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }


    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        inputSparkRedshiftJaxb = (Sparkredshift) baseComponent;
    }

    @Override
    public void createEntity() {
        inputRDBMSEntity = new InputRDBMSEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing input file Redshift component: "
                + inputSparkRedshiftJaxb.getId());
        inputRDBMSEntity.setComponentId(inputSparkRedshiftJaxb.getId());
        inputRDBMSEntity.setFieldsList(InputEntityUtils.extractInputFields(
                inputSparkRedshiftJaxb.getOutSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
        inputRDBMSEntity.setOutSocketList(InputEntityUtils.extractOutSocket(inputSparkRedshiftJaxb.getOutSocket()));
        inputRDBMSEntity.setDatabaseName(inputSparkRedshiftJaxb.getDatabaseName().getValue());
        inputRDBMSEntity.setHostName(inputSparkRedshiftJaxb.getHostName().getValue());
        inputRDBMSEntity.setPort(inputSparkRedshiftJaxb.getPort() == null ? Constants.REDSHIFT_PORT_NUMBER
                : inputSparkRedshiftJaxb.getPort().getValue().intValue());
        inputRDBMSEntity.setRuntimeProperties(inputSparkRedshiftJaxb.getRuntimeProperties() == null ? new Properties() : InputEntityUtils
                .extractRuntimeProperties(inputSparkRedshiftJaxb.getRuntimeProperties()));
        inputRDBMSEntity.setBatch(inputSparkRedshiftJaxb.getBatch());
        inputRDBMSEntity.setSelectQuery(inputSparkRedshiftJaxb.getSelectQuery() == null ? null : inputSparkRedshiftJaxb.getSelectQuery().getValue());
        inputRDBMSEntity.setTableName(inputSparkRedshiftJaxb.getTableName() == null ? null : inputSparkRedshiftJaxb.getTableName().getValue());
        inputRDBMSEntity.setCountQuery(inputSparkRedshiftJaxb.getCountQuery() == null
                ? "select count(*) from  (" + inputRDBMSEntity.getSelectQuery() + ")"
                : inputSparkRedshiftJaxb.getCountQuery().getValue());
        inputRDBMSEntity.setUsername(inputSparkRedshiftJaxb.getUserName().getValue());
        inputRDBMSEntity.setPassword(inputSparkRedshiftJaxb.getPassword().getValue());
        inputRDBMSEntity.setTemps3dir(inputSparkRedshiftJaxb.getTemps3Dir().getValue());
        if (inputSparkRedshiftJaxb.getSchemaName() != null) {
            inputRDBMSEntity.setSchemaName(inputSparkRedshiftJaxb.getSchemaName().getValue());
            inputRDBMSEntity.setTableName(
                    inputSparkRedshiftJaxb.getSchemaName().getValue() + "." + inputSparkRedshiftJaxb.getTableName().getValue());
        } else {
            inputRDBMSEntity.setSchemaName(null);
        }
    }

    @Override
    public InputRDBMSEntity getEntity() {
        return inputRDBMSEntity;
    }


}