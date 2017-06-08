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
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.outputtypes.Oracle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class OutputOracleEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputOracleEntityGenerator extends OutputComponentGeneratorBase {

    private static Logger LOG = LoggerFactory.getLogger(OutputOracleEntityGenerator.class);
    private final String DATABASE_TYPE = "Oracle";
    private Oracle jaxbOutputOracle;
    private OutputRDBMSEntity outputRDBMSEntity;

    public OutputOracleEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }

    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        jaxbOutputOracle = (Oracle) baseComponent;
    }

    @Override
    public void createEntity() {
        outputRDBMSEntity = new OutputRDBMSEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing input file RDBMS component: " + jaxbOutputOracle.getId());

        outputRDBMSEntity.setComponentId(jaxbOutputOracle.getId());
        outputRDBMSEntity.setDriverType(jaxbOutputOracle.getDriverType().getValue());
        outputRDBMSEntity.setFieldsList(OutputEntityUtils.extractOutputFields(
                jaxbOutputOracle.getInSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
        outputRDBMSEntity.setPort(jaxbOutputOracle.getPort().getValue().intValue());
        outputRDBMSEntity.setSid(jaxbOutputOracle.getSid().getValue());
        outputRDBMSEntity.setHostName(jaxbOutputOracle.getHostName().getValue());
        outputRDBMSEntity.setTableName(jaxbOutputOracle.getTableName().getValue());
        outputRDBMSEntity.setDatabaseType(DATABASE_TYPE);
        outputRDBMSEntity.setRuntimeProperties(jaxbOutputOracle.getRuntimeProperties() == null? new Properties():
                OutputEntityUtils.extractRuntimeProperties(jaxbOutputOracle.getRuntimeProperties()));
        outputRDBMSEntity.setBatch(jaxbOutputOracle.getBatch());
        outputRDBMSEntity.setUsername(jaxbOutputOracle.getUserName().getValue());
        outputRDBMSEntity.setPassword(jaxbOutputOracle.getPassword().getValue());
        if (jaxbOutputOracle.getLoadType().getNewTable() != null)
            outputRDBMSEntity.setLoadType("newTable");
        else if (jaxbOutputOracle.getLoadType().getTruncateLoad() != null)
            outputRDBMSEntity.setLoadType("truncateLoad");
        else if (jaxbOutputOracle.getLoadType().getInsert() != null)
            outputRDBMSEntity.setLoadType("insert");
        else
            outputRDBMSEntity.setLoadType("update");

        if ("newTable".equals(outputRDBMSEntity.getLoadType()))
            outputRDBMSEntity.setPrimaryKeys(jaxbOutputOracle.getLoadType().getNewTable().getPrimaryKeys() == null
                    ? null : jaxbOutputOracle.getLoadType().getNewTable().getPrimaryKeys().getField());
        if (outputRDBMSEntity.getLoadType().equals("update"))
            outputRDBMSEntity.setUpdateByKeys(jaxbOutputOracle.getLoadType().getUpdate().getUpdateByKeys().getField());
        if (jaxbOutputOracle.getSchemaName() != null) {
            outputRDBMSEntity.setSchemaName(jaxbOutputOracle.getSchemaName().getValue());
            outputRDBMSEntity.setTableName(
                    jaxbOutputOracle.getSchemaName().getValue() + "." + jaxbOutputOracle.getTableName().getValue());
        } else {
            outputRDBMSEntity.setSchemaName(null);
        }
        /**New fields added since the project was open-sourced*/
        //for batchsize which was named to chunksize since there is batch in the ETL tool as well
        outputRDBMSEntity.setChunkSize(jaxbOutputOracle.getChunkSize()==null?null:jaxbOutputOracle.getChunkSize().getValue());
        //extra url parameters has been
        if (jaxbOutputOracle.getExtraUrlParams() != null) {
            String rawParam = jaxbOutputOracle.getExtraUrlParams().getValue();

            Pattern regexForComma = Pattern.compile("([,]{0,1})");
            Pattern regexForOthers = Pattern.compile("[$&:;?@#|'<>.^*()%+!]");
            Pattern regexForRepitititons = Pattern.compile("([,]{2,})");

            Matcher commaMatcher = regexForComma.matcher(rawParam);
            Matcher otherCharMatcher = regexForOthers.matcher(rawParam);
            Matcher doubleCharMatcher = regexForRepitititons.matcher(rawParam);



            if(doubleCharMatcher.find()) {
                throw new RuntimeException("Repeated comma found");
            }
            else if (otherCharMatcher.find()) {
                throw new RuntimeException("Other delimiter found");
            } else if (commaMatcher.find()) {
               /*
               * If the string happens to have a , then all the commas shall be replaced by &
               * */
                String correctedParams = rawParam.replaceAll(",", "&").replaceAll("(\\s+)","");
                LOG.info("The extraUrlParams being used as"+ correctedParams );
                outputRDBMSEntity.setExtraUrlParamters(correctedParams);
            }
            else {
                String correctedParams = rawParam.replaceAll("(\\s+)","&");
                LOG.info("The extraUrlParams being used as "+ correctedParams);
                outputRDBMSEntity.setExtraUrlParamters(correctedParams);
            }

        } else {
            LOG.info("extraUrlParameters initialized with null");
            outputRDBMSEntity.setExtraUrlParamters(null);
        }
        /**end**/
    }

    @Override
    public OutputRDBMSEntity getEntity() {
        return outputRDBMSEntity;
    }
}