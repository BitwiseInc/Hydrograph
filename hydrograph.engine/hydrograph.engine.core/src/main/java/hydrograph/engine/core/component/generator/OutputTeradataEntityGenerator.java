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
import hydrograph.engine.jaxb.outputtypes.Teradata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
/**
 * The Class OutputTeradataEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputTeradataEntityGenerator extends OutputComponentGeneratorBase {

    private Teradata jaxbOutputTeradata;
    private OutputRDBMSEntity outputRDBMSEntity;
    private static Logger LOG = LoggerFactory
            .getLogger(OutputTeradataEntityGenerator.class);

    public OutputTeradataEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }


    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        jaxbOutputTeradata = (Teradata) baseComponent;
    }

    @Override
    public void createEntity() {
        outputRDBMSEntity = new OutputRDBMSEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing input file RDBMS component: "
                + jaxbOutputTeradata.getId());

        outputRDBMSEntity.setComponentId(jaxbOutputTeradata.getId());
        outputRDBMSEntity.setBatch(jaxbOutputTeradata.getBatch());

        outputRDBMSEntity
                .setFieldsList(OutputEntityUtils.extractOutputFields(jaxbOutputTeradata
                        .getInSocket().get(0).getSchema()
                        .getFieldOrRecordOrIncludeExternalSchema()));
        outputRDBMSEntity.setDatabaseName(jaxbOutputTeradata.getDatabaseName().getValue());
        outputRDBMSEntity.setTableName(jaxbOutputTeradata.getTableName().getValue());

        outputRDBMSEntity.setUsername(jaxbOutputTeradata.getUsername().getValue());
        outputRDBMSEntity.setPassword(jaxbOutputTeradata.getPassword().getValue());

        outputRDBMSEntity.setHostName(jaxbOutputTeradata.getHostName().getValue());
        outputRDBMSEntity.set_interface(jaxbOutputTeradata.getLoadUtilityType().getValue());
        if (jaxbOutputTeradata.getPort() == null)
            LOG.warn("Output Teradata component '" + outputRDBMSEntity.getComponentId() + "' "
                    + " port is not provided, using default port " + Constants.DEFAULT_TERADATA_PORT);

        outputRDBMSEntity.setPort(jaxbOutputTeradata.getPort() == null ? Constants.DEFAULT_TERADATA_PORT : jaxbOutputTeradata.getPort().getValue().intValue());
        outputRDBMSEntity.setJdbcDriver(jaxbOutputTeradata.getJdbcDriver() == null ? null : jaxbOutputTeradata.getJdbcDriver().getValue());
        // outputRDBMSEntity.setChunkSize(jaxbOutputTeradata.getChunkSize() == null ? Constants.DEFAULT_CHUNKSIZE : jaxbOutputTeradata.getChunkSize().getValue().intValue());
        outputRDBMSEntity.setDatabaseType("Teradata");

        if (jaxbOutputTeradata.getLoadType().getNewTable() != null)
            outputRDBMSEntity.setLoadType("newTable");
        else if (jaxbOutputTeradata.getLoadType().getTruncateLoad() != null)
            outputRDBMSEntity.setLoadType("truncateLoad");
        else if (jaxbOutputTeradata.getLoadType().getInsert() != null)
            outputRDBMSEntity.setLoadType("insert");
        else
            outputRDBMSEntity.setLoadType("update");


        if ("newTable".equals(outputRDBMSEntity.getLoadType()))
            outputRDBMSEntity
                    .setPrimaryKeys(jaxbOutputTeradata.getLoadType().getNewTable().getPrimaryKeys() == null ? null
                            : jaxbOutputTeradata.getLoadType().getNewTable().getPrimaryKeys().getField());
        if (outputRDBMSEntity.getLoadType().equals("update"))
            outputRDBMSEntity
                    .setUpdateByKeys(jaxbOutputTeradata.getLoadType().getUpdate().getUpdateByKeys().getField());

        outputRDBMSEntity.setRuntimeProperties(jaxbOutputTeradata.getRuntimeProperties() == null ? new Properties() : OutputEntityUtils
                .extractRuntimeProperties(jaxbOutputTeradata.getRuntimeProperties()));
/*		outputRDBMSEntity.setRuntimeProperties(OutputEntityUtils
                .extractRuntimeProperties(jaxbOutputMysql.getRuntimeProperties()));*/
    }

    @Override
    public OutputRDBMSEntity getEntity() {
        return outputRDBMSEntity;
    }
}