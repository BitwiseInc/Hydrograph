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
import hydrograph.engine.jaxb.outputtypes.Sparkredshift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
/**
 * The Class OutputSparkRedshiftEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputSparkRedshiftEntityGenerator extends OutputComponentGeneratorBase {

    private static Logger LOG = LoggerFactory.getLogger(OutputSparkRedshiftEntityGenerator.class);
    private final String DATABASE_TYPE = "Redshift";
    private Sparkredshift jaxbOutputSparkRedshift;
    private OutputRDBMSEntity outputRDBMSEntity;

    public OutputSparkRedshiftEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }

    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        jaxbOutputSparkRedshift = (Sparkredshift) baseComponent;
    }

    @Override
    public void createEntity() {
        outputRDBMSEntity = new OutputRDBMSEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing input file RDBMS component: " + jaxbOutputSparkRedshift.getId());

        outputRDBMSEntity.setComponentId(jaxbOutputSparkRedshift.getId());
        outputRDBMSEntity.setFieldsList(OutputEntityUtils.extractOutputFields(
                jaxbOutputSparkRedshift.getInSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
        outputRDBMSEntity.setPort(jaxbOutputSparkRedshift.getPort() == null ? Constants.REDSHIFT_PORT_NUMBER
                : jaxbOutputSparkRedshift.getPort().getValue().intValue());
        outputRDBMSEntity.setDatabaseName(jaxbOutputSparkRedshift.getDatabaseName().getValue());
        outputRDBMSEntity.setHostName(jaxbOutputSparkRedshift.getHostName().getValue());
        outputRDBMSEntity.setTableName(jaxbOutputSparkRedshift.getTableName().getValue());
        outputRDBMSEntity.setDatabaseType(DATABASE_TYPE);
        outputRDBMSEntity.setRuntimeProperties(jaxbOutputSparkRedshift.getRuntimeProperties() == null ? new Properties() :
                OutputEntityUtils.extractRuntimeProperties(jaxbOutputSparkRedshift.getRuntimeProperties()));
        outputRDBMSEntity.setBatch(jaxbOutputSparkRedshift.getBatch());
        outputRDBMSEntity.setUsername(jaxbOutputSparkRedshift.getUserName().getValue());
        outputRDBMSEntity.setPassword(jaxbOutputSparkRedshift.getPassword().getValue());
        outputRDBMSEntity.setTemps3dir(jaxbOutputSparkRedshift.getTemps3Dir().getValue());
        if (jaxbOutputSparkRedshift.getLoadType().getNewTable() != null)
            outputRDBMSEntity.setLoadType("newTable");
        else if (jaxbOutputSparkRedshift.getLoadType().getTruncateLoad() != null)
            outputRDBMSEntity.setLoadType("truncateLoad");
        else if (jaxbOutputSparkRedshift.getLoadType().getInsert() != null)
            outputRDBMSEntity.setLoadType("insert");
        else
            outputRDBMSEntity.setLoadType("update");

        if ("newTable".equals(outputRDBMSEntity.getLoadType()))
            outputRDBMSEntity.setPrimaryKeys(jaxbOutputSparkRedshift.getLoadType().getNewTable().getPrimaryKeys() == null
                    ? null : jaxbOutputSparkRedshift.getLoadType().getNewTable().getPrimaryKeys().getField());
        if (outputRDBMSEntity.getLoadType().equals("update"))
            outputRDBMSEntity.setUpdateByKeys(jaxbOutputSparkRedshift.getLoadType().getUpdate().getUpdateByKeys().getField());
        if (jaxbOutputSparkRedshift.getSchemaName() != null) {
            outputRDBMSEntity.setSchemaName(jaxbOutputSparkRedshift.getSchemaName().getValue());
            outputRDBMSEntity.setTableName(
                    jaxbOutputSparkRedshift.getSchemaName().getValue() + "." + jaxbOutputSparkRedshift.getTableName().getValue());
        } else {
            outputRDBMSEntity.setSchemaName(null);
        }
    }

    @Override
    public OutputRDBMSEntity getEntity() {
        return outputRDBMSEntity;
    }
}