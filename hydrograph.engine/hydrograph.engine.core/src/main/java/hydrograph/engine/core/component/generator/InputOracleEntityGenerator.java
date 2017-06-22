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
import hydrograph.engine.jaxb.inputtypes.Oracle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class InputOracleEntityGenerator.
 *
 * @author Bitwise
 */
public class InputOracleEntityGenerator extends
        InputComponentGeneratorBase {

    private static Logger LOG = LoggerFactory
            .getLogger(InputOracleEntityGenerator.class);
    private Oracle inputOracleJaxb;
    private InputRDBMSEntity inputRDBMSEntity;

    public InputOracleEntityGenerator(TypeBaseComponent baseComponent) {
        super(baseComponent);
    }


    @Override
    public void castComponentFromBase(TypeBaseComponent baseComponent) {
        inputOracleJaxb = (Oracle) baseComponent;
    }

    @Override
    public void createEntity() {
        inputRDBMSEntity = new InputRDBMSEntity();
    }

    @Override
    public void initializeEntity() {

        LOG.trace("Initializing input file Oracle component: " + inputOracleJaxb.getId());

        inputRDBMSEntity.setComponentId(inputOracleJaxb.getId());
        inputRDBMSEntity.setFieldsList(InputEntityUtils.extractInputFields(
                inputOracleJaxb.getOutSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
        inputRDBMSEntity.setOutSocketList(InputEntityUtils.extractOutSocket(inputOracleJaxb.getOutSocket()));
        inputRDBMSEntity.setSid(inputOracleJaxb.getSid().getValue());
        inputRDBMSEntity.setHostName(inputOracleJaxb.getHostName().getValue());
        inputRDBMSEntity.setPort(inputOracleJaxb.getPort() == null ? Constants.ORACLE_PORT_NUMBER
                : inputOracleJaxb.getPort().getValue().intValue());
        inputRDBMSEntity.setRuntimeProperties(inputOracleJaxb.getRuntimeProperties() == null ? new Properties() : InputEntityUtils
                .extractRuntimeProperties(inputOracleJaxb.getRuntimeProperties()));
        inputRDBMSEntity.setBatch(inputOracleJaxb.getBatch());
        if (inputOracleJaxb.getSelectQuery() != null) {
            inputRDBMSEntity.setTableName("Dual");
            inputRDBMSEntity.setSelectQuery(inputOracleJaxb.getSelectQuery().getValue());
        } else {
            inputRDBMSEntity.setSelectQuery(null);
            inputRDBMSEntity.setTableName(inputOracleJaxb.getTableName().getValue());
        }
        inputRDBMSEntity.setCountQuery(inputOracleJaxb.getCountQuery() == null
                ? "select count(*) from  (" + inputRDBMSEntity.getSelectQuery() + ")"
                : inputOracleJaxb.getCountQuery().getValue());
        inputRDBMSEntity.setUsername(inputOracleJaxb.getUserName().getValue());
        inputRDBMSEntity.setPassword(inputOracleJaxb.getPassword().getValue());
        inputRDBMSEntity.setDriverType(inputOracleJaxb.getDriverType().getValue());
        if (inputOracleJaxb.getSchemaName() != null) {
            inputRDBMSEntity.setSchemaName(inputOracleJaxb.getSchemaName().getValue());
            if(inputOracleJaxb.getSelectQuery() == null) {
                inputRDBMSEntity.setTableName(
                        inputOracleJaxb.getSchemaName().getValue() + "." + inputOracleJaxb.getTableName().getValue());
            }
        } else {
            inputRDBMSEntity.setSchemaName(null);
        }
        /**new parameters that has been added after the open source release*/
        inputRDBMSEntity.setNumPartitionsValue(inputOracleJaxb.getNumPartitions() == null ? Integer.MIN_VALUE : inputOracleJaxb.getNumPartitions().getValue().intValue());
        if (inputOracleJaxb.getNumPartitions() != null && inputOracleJaxb.getNumPartitions().getValue().intValue() == 0) {
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
        if (inputOracleJaxb.getNumPartitions() == null) {
            inputRDBMSEntity.setUpperBound(0);
            inputRDBMSEntity.setLowerBound(0);
            inputRDBMSEntity.setColumnName("");
        } else {
            if (inputOracleJaxb.getNumPartitions().getUpperBound() == null) {
                throw new RuntimeException("Error in Input Oracle Component '" + inputOracleJaxb.getId() + "'  Upper bound cannot be NULL when numPartitions holds an integer value");
            } else
                inputRDBMSEntity.setUpperBound(inputOracleJaxb.getNumPartitions().getUpperBound().getValue().intValue());

            if (inputOracleJaxb.getNumPartitions().getLowerBound() == null) {
                throw new RuntimeException("Error in Input Oracle Component '" + inputOracleJaxb.getId() + "'  Lower bound cannot be NULL when numPartitions holds an integer value");
            } else
                inputRDBMSEntity.setLowerBound(inputOracleJaxb.getNumPartitions().getLowerBound().getValue().intValue());

            if (inputOracleJaxb.getNumPartitions().getColumnName() == null) {
                throw new RuntimeException("Error in Input Oracle Component '" + inputOracleJaxb.getId() + "' Column Name cannot be NULL when numPartitions holds an integer value");
            } else inputRDBMSEntity.setColumnName(inputOracleJaxb.getNumPartitions().getColumnName().getValue());

            if (inputOracleJaxb.getNumPartitions().getLowerBound().getValue()
                    .intValue() == inputOracleJaxb.getNumPartitions().getUpperBound().getValue().intValue()) {
                LOG.warn("'" + inputOracleJaxb.getId() + "'The upper bound and the lower bound values are same! In this case there will only be\n" +
                        "a single partition");
            }
            if (inputOracleJaxb.getNumPartitions().getLowerBound().getValue()
                    .intValue() > inputOracleJaxb.getNumPartitions().getUpperBound().getValue().intValue()) {
                LOG.error("Error in Input Oracle Component '" + inputOracleJaxb.getId() + "'  Can\'t proceed with partitioning");
                throw new RuntimeException("Error in Input Oracle Component '" + inputOracleJaxb.getId() + "'  The lower bound is greater than upper bound");

            }

        }
        //fetchsize
        inputRDBMSEntity.setFetchSize(inputOracleJaxb.getFetchSize() == null ? null : inputOracleJaxb.getFetchSize().getValue());
        //extra url parameters

        if (inputOracleJaxb.getExtraUrlParams() != null) {
            String rawParam = inputOracleJaxb.getExtraUrlParams().getValue();

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
    }


    @Override
    public InputRDBMSEntity getEntity() {
        return inputRDBMSEntity;
    }
}