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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class InputMysqlEntityGenerator.
 *
 * @author Bitwise
 */
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
        inputRDBMSEntity.setJdbcDriver(inputMysqlJaxb.getJdbcDriver() == null ? null : inputMysqlJaxb.getJdbcDriver().getValue());
        inputRDBMSEntity.setTableName(inputMysqlJaxb.getTableName() == null ? null : inputMysqlJaxb.getTableName().getValue());
        inputRDBMSEntity.setSelectQuery(inputMysqlJaxb.getSelectQuery() == null ? null : inputMysqlJaxb.getSelectQuery().getValue());
        inputRDBMSEntity.setCountQuery(inputMysqlJaxb.getCountQuery() == null ? ("select count(*) from  (" + inputRDBMSEntity.getSelectQuery() + ") as alias;") : inputMysqlJaxb.getCountQuery().getValue());
        inputRDBMSEntity.setUsername(inputMysqlJaxb.getUsername().getValue());
        inputRDBMSEntity.setPassword(inputMysqlJaxb.getPassword().getValue());
//		inputRDBMSEntity.setFetchSize(inputMysqlJaxb.getFetchSize()==null?Constants.DEFAULT_DB_FETCHSIZE:inputMysqlJaxb.getFetchSize().getValue().intValue());
        inputRDBMSEntity.setRuntimeProperties(inputMysqlJaxb.getRuntimeProperties() == null ? new Properties() : InputEntityUtils
                .extractRuntimeProperties(inputMysqlJaxb.getRuntimeProperties()));

        /**new parameters that has been added after the open source release*/
        inputRDBMSEntity.setNumPartitionsValue(inputMysqlJaxb.getNumPartitions() == null ? Integer.MIN_VALUE : inputMysqlJaxb.getNumPartitions().getValue().intValue());
        if (inputMysqlJaxb.getNumPartitions() != null && inputMysqlJaxb.getNumPartitions().getValue().intValue() == 0) {
            LOG.warn("The number of partitions has been entered as ZERO," +
                    "\nThe Execution shall still continue but will work on a single" +
                    "\npartition hence impacting performance");
        }

        /**
         * @note : At first the check is made for the nullability of the partiton value; in case the partition
         *          value goes missing, the upper bound, lower bound and column name must not hold a value as the
         *          JDBCRDD has only two JDBC functions as the one which doesn't do any partitioning and does not accept
         *          the values from the below parameters listed and the second one which does partitoning accepts the
         *          aforementioned parameters. Absence of any one parameter may lead to undesirable outcomes
         * */
        if (inputMysqlJaxb.getNumPartitions() == null) {
            inputRDBMSEntity.setUpperBound(0);
            inputRDBMSEntity.setLowerBound(0);
            inputRDBMSEntity.setColumnName("");
        } else {
            if (inputMysqlJaxb.getNumPartitions().getUpperBound() == null) {
                throw new RuntimeException("Error in Input Mysql Component '" + inputMysqlJaxb.getId() + "' Upper bound cannot be NULL when numPartitions holds an integer value");
            } else
                inputRDBMSEntity.setUpperBound(inputMysqlJaxb.getNumPartitions().getUpperBound().getValue().intValue());

            if (inputMysqlJaxb.getNumPartitions().getLowerBound() == null) {
                throw new RuntimeException("Error in Input Mysql Component '" + inputMysqlJaxb.getId() + "' Lower bound cannot be NULL when numPartitions holds an integer value");
            } else
                inputRDBMSEntity.setLowerBound(inputMysqlJaxb.getNumPartitions().getLowerBound().getValue().intValue());

            if (inputMysqlJaxb.getNumPartitions().getColumnName() == null) {
                throw new RuntimeException("Error in Input Oracle Component '" + inputMysqlJaxb.getId() + "' Column Name cannot be NULL when numPartitions holds an integer value");
            } else inputRDBMSEntity.setColumnName(inputMysqlJaxb.getNumPartitions().getColumnName().getValue());

            if (inputMysqlJaxb.getNumPartitions().getLowerBound().getValue()
                    .intValue() == inputMysqlJaxb.getNumPartitions().getUpperBound().getValue().intValue()) {
                LOG.warn("'" + inputMysqlJaxb.getId() + "'The upper bound and the lower bound values are same! In this case there will only be\n" +
                        "a single partition");
            }
            if (inputMysqlJaxb.getNumPartitions().getLowerBound().getValue()
                    .intValue() > inputMysqlJaxb.getNumPartitions().getUpperBound().getValue().intValue()) {
                LOG.error("Error in Input Mysql Component '" + inputMysqlJaxb.getId() + "' Error! Can\'t proceed with partitioning");
                throw new RuntimeException("Error in Input Mysql Component '" + inputMysqlJaxb.getId() + "' The lower bound is greater than upper bound");

            }

        }
        //fetchsize
        inputRDBMSEntity.setFetchSize(inputMysqlJaxb.getFetchSize() == null ? null : inputMysqlJaxb.getFetchSize().getValue());
        //extra url parameters

        if (inputMysqlJaxb.getExtraUrlParams() != null) {
            String rawParam = inputMysqlJaxb.getExtraUrlParams().getValue();

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
                inputRDBMSEntity.setExtraUrlParameters(correctedParams);
            }
            else {
                String correctedParams = rawParam.replaceAll("(\\s+)","&");
                LOG.info("The extraUrlParams being used as "+ correctedParams);
                inputRDBMSEntity.setExtraUrlParameters(correctedParams);
            }

        } else {
            LOG.info("extraUrlParameters initialized with null");
            inputRDBMSEntity.setExtraUrlParameters(null);
        }

        /**END*/
        inputRDBMSEntity.setDatabaseType("Mysql");
    }

    @Override
    public InputRDBMSEntity getEntity() {
        return inputRDBMSEntity;
    }

}