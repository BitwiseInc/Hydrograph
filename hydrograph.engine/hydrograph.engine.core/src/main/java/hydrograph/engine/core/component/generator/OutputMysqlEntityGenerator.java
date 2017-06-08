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
import hydrograph.engine.jaxb.outputtypes.Mysql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class OutputMysqlEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputMysqlEntityGenerator extends OutputComponentGeneratorBase {

    private Mysql jaxbOutputMysql;
    private OutputRDBMSEntity outputRDBMSEntity;
    private static Logger LOG = LoggerFactory
            .getLogger(OutputMysqlEntityGenerator.class);

    public OutputMysqlEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }


    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        jaxbOutputMysql = (Mysql) baseComponent;
    }

    @Override
    public void createEntity() {
        outputRDBMSEntity = new OutputRDBMSEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing input file RDBMS component: "
                + jaxbOutputMysql.getId());

        outputRDBMSEntity.setComponentId(jaxbOutputMysql.getId());
        outputRDBMSEntity.setBatch(jaxbOutputMysql.getBatch());

        outputRDBMSEntity
                .setFieldsList(OutputEntityUtils.extractOutputFields(jaxbOutputMysql
                        .getInSocket().get(0).getSchema()
                        .getFieldOrRecordOrIncludeExternalSchema()));
        outputRDBMSEntity.setDatabaseName(jaxbOutputMysql.getDatabaseName().getValue());
        outputRDBMSEntity.setTableName(jaxbOutputMysql.getTableName().getValue());

        outputRDBMSEntity.setUsername(jaxbOutputMysql.getUsername().getValue());
        outputRDBMSEntity.setPassword(jaxbOutputMysql.getPassword().getValue());

        outputRDBMSEntity.setHostName(jaxbOutputMysql.getHostName().getValue());

        if (jaxbOutputMysql.getPort() == null)
            LOG.warn("Output Mysql component '" + outputRDBMSEntity.getComponentId() + "' "
                    + " port is not provided, using default port " + Constants.DEFAULT_MYSQL_PORT);

        outputRDBMSEntity.setPort(jaxbOutputMysql.getPort() == null ? Constants.DEFAULT_MYSQL_PORT : jaxbOutputMysql.getPort().getValue().intValue());
        outputRDBMSEntity.setJdbcDriver(jaxbOutputMysql.getJdbcDriver() == null ? null : jaxbOutputMysql.getJdbcDriver().getValue());
        outputRDBMSEntity.setDatabaseType("Mysql");

        if (jaxbOutputMysql.getLoadType().getNewTable() != null)
            outputRDBMSEntity.setLoadType("newTable");
        else if (jaxbOutputMysql.getLoadType().getTruncateLoad() != null)
            outputRDBMSEntity.setLoadType("truncateLoad");
        else if (jaxbOutputMysql.getLoadType().getInsert() != null)
            outputRDBMSEntity.setLoadType("insert");
        else
            outputRDBMSEntity.setLoadType("update");


        if ("newTable".equals(outputRDBMSEntity.getLoadType()))
            outputRDBMSEntity
                    .setPrimaryKeys(jaxbOutputMysql.getLoadType().getNewTable().getPrimaryKeys() == null ? null
                            : jaxbOutputMysql.getLoadType().getNewTable().getPrimaryKeys().getField());
        if (outputRDBMSEntity.getLoadType().equals("update"))
            outputRDBMSEntity
                    .setUpdateByKeys(jaxbOutputMysql.getLoadType().getUpdate().getUpdateByKeys().getField());

        outputRDBMSEntity.setRuntimeProperties(jaxbOutputMysql.getRuntimeProperties() == null ? new Properties() : OutputEntityUtils
                .extractRuntimeProperties(jaxbOutputMysql.getRuntimeProperties()));
/*		outputRDBMSEntity.setRuntimeProperties(OutputEntityUtils
                .extractRuntimeProperties(jaxbOutputMysql.getRuntimeProperties()));*/
/**New fields added since the project was open-sourced*/
        //for batchsize which was named to chunksize since there is batch in the ETL tool as well
        outputRDBMSEntity.setChunkSize(jaxbOutputMysql.getChunkSize()==null?null:jaxbOutputMysql.getChunkSize().getValue());
        //extra url parameters has been

        if (jaxbOutputMysql.getExtraUrlParams() != null) {
            String rawParam = jaxbOutputMysql.getExtraUrlParams().getValue();

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