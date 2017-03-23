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

import hydrograph.engine.core.component.entity.OutputRDBMSEntity;
import hydrograph.engine.core.component.entity.utils.OutputEntityUtils;
import hydrograph.engine.core.component.generator.base.OutputComponentGeneratorBase;
import hydrograph.engine.core.constants.Constants;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.outputtypes.Redshift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
/**
 * The Class OutputRedshiftEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputRedshiftEntityGenerator extends OutputComponentGeneratorBase {

    private static Logger LOG = LoggerFactory.getLogger(OutputRedshiftEntityGenerator.class);
    private final String DATABASE_TYPE = "Redshift";
    private Redshift jaxbOutputRedshift;
    private OutputRDBMSEntity outputRDBMSEntity;

    public OutputRedshiftEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }

    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        jaxbOutputRedshift = (Redshift) baseComponent;
    }

    @Override
    public void createEntity() {
        outputRDBMSEntity = new OutputRDBMSEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing input file RDBMS component: " + jaxbOutputRedshift.getId());

        outputRDBMSEntity.setComponentId(jaxbOutputRedshift.getId());
        outputRDBMSEntity.setFieldsList(OutputEntityUtils.extractOutputFields(
                jaxbOutputRedshift.getInSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
        outputRDBMSEntity.setPort(jaxbOutputRedshift.getPort() == null ? Constants.REDSHIFT_PORT_NUMBER
                : jaxbOutputRedshift.getPort().getValue().intValue());
        outputRDBMSEntity.setDatabaseName(jaxbOutputRedshift.getDatabaseName().getValue());
        outputRDBMSEntity.setHostName(jaxbOutputRedshift.getHostName().getValue());
        outputRDBMSEntity.setTableName(jaxbOutputRedshift.getTableName().getValue());
        outputRDBMSEntity.setDatabaseType(DATABASE_TYPE);
        outputRDBMSEntity.setJdbcDriver(jaxbOutputRedshift.getJdbcDriver() == null ? null : jaxbOutputRedshift.getJdbcDriver().getValue());
        outputRDBMSEntity.setRuntimeProperties(jaxbOutputRedshift.getRuntimeProperties() == null ? new Properties() :
                OutputEntityUtils.extractRuntimeProperties(jaxbOutputRedshift.getRuntimeProperties()));
        outputRDBMSEntity.setBatch(jaxbOutputRedshift.getBatch());
        outputRDBMSEntity.setUsername(jaxbOutputRedshift.getUserName().getValue());
        outputRDBMSEntity.setPassword(jaxbOutputRedshift.getPassword().getValue());
        if (jaxbOutputRedshift.getLoadType().getNewTable() != null)
            outputRDBMSEntity.setLoadType("newTable");
        else if (jaxbOutputRedshift.getLoadType().getTruncateLoad() != null)
            outputRDBMSEntity.setLoadType("truncateLoad");
        else if (jaxbOutputRedshift.getLoadType().getInsert() != null)
            outputRDBMSEntity.setLoadType("insert");
        else
            outputRDBMSEntity.setLoadType("update");

        if ("newTable".equals(outputRDBMSEntity.getLoadType()))
            outputRDBMSEntity.setPrimaryKeys(jaxbOutputRedshift.getLoadType().getNewTable().getPrimaryKeys() == null
                    ? null : jaxbOutputRedshift.getLoadType().getNewTable().getPrimaryKeys().getField());
        if (outputRDBMSEntity.getLoadType().equals("update"))
            outputRDBMSEntity.setUpdateByKeys(jaxbOutputRedshift.getLoadType().getUpdate().getUpdateByKeys().getField());
        if (jaxbOutputRedshift.getSchemaName() != null) {
            outputRDBMSEntity.setSchemaName(jaxbOutputRedshift.getSchemaName().getValue());
            outputRDBMSEntity.setTableName(
                    jaxbOutputRedshift.getSchemaName().getValue() + "." + jaxbOutputRedshift.getTableName().getValue());
        } else {
            outputRDBMSEntity.setSchemaName(null);
        }
    }

    @Override
    public OutputRDBMSEntity getEntity() {
        return outputRDBMSEntity;
    }
}