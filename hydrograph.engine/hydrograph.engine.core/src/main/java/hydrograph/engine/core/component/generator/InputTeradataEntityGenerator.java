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
import hydrograph.engine.jaxb.inputtypes.Teradata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class InputTeradataEntityGenerator.
 *
 * @author Bitwise
 *
 */

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
        /**new parameters that has been added after the open source release*/
        inputRDBMSEntity.setNumPartitionsValue(inputTeradataJaxb.getNumPartitions() == null? Integer.MIN_VALUE:inputTeradataJaxb.getNumPartitions().getValue().intValue());
        if(inputTeradataJaxb.getNumPartitions()!=null&&inputTeradataJaxb.getNumPartitions().getValue().intValue()==0){
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
        if(inputTeradataJaxb.getNumPartitions()==null){
            inputRDBMSEntity.setUpperBound(0);
            inputRDBMSEntity.setLowerBound(0);
            inputRDBMSEntity.setColumnName("");
        }
        else {
            if (inputTeradataJaxb.getNumPartitions().getUpperBound() == null) {
                throw new RuntimeException( "Error in Input Teradata Component '"+inputTeradataJaxb.getId()+"' Upper bound cannot be NULL when numPartitions holds an integer value");
            } else inputRDBMSEntity.setUpperBound(inputTeradataJaxb.getNumPartitions().getUpperBound().getValue().intValue());

            if (inputTeradataJaxb.getNumPartitions().getLowerBound() == null) {
                throw new RuntimeException( "Error in Input Teradata Component '"+inputTeradataJaxb.getId()+"' Lower bound cannot be NULL when numPartitions holds an integer value");
            } else inputRDBMSEntity.setLowerBound(inputTeradataJaxb.getNumPartitions().getLowerBound().getValue().intValue());

            if (inputTeradataJaxb.getNumPartitions().getColumnName() == null) {
                throw new RuntimeException("Error in Input Teradata Component '"+inputTeradataJaxb.getId()+"' Column name cannot be NULL when numPartitions holds an integer value");
            } else inputRDBMSEntity.setColumnName(inputTeradataJaxb.getNumPartitions().getColumnName().getValue());

            if(inputTeradataJaxb.getNumPartitions().getLowerBound().getValue()
                    .intValue()==inputTeradataJaxb.getNumPartitions().getUpperBound().getValue().intValue()){
                LOG.warn("'"+inputTeradataJaxb.getId()+"' The upper bound and the lower bound values are same! In this case there will only be\n" +
                        "a single partition");
            }
            if(inputTeradataJaxb.getNumPartitions().getLowerBound().getValue()
                    .intValue()>inputTeradataJaxb.getNumPartitions().getUpperBound().getValue().intValue()){
                LOG.error( "Error in Input Teradata Component '"+inputTeradataJaxb.getId()+"' Can\'t proceed with partitioning");
                throw new RuntimeException("Error in Input Teradata Component '"+inputTeradataJaxb.getId()+"'  The lower bound is greater than upper bound");

            }

        }
        //fetchsize
        inputRDBMSEntity.setFetchSize(inputTeradataJaxb.getFetchSize()==null?null: inputTeradataJaxb.getFetchSize().getValue());
        //extra url parameters
        if (inputTeradataJaxb.getExtraUrlParams() != null) {
            String rawParam = inputTeradataJaxb.getExtraUrlParams().getValue();

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
                String correctedParams = rawParam.replaceAll("(\\s+)","");
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
        inputRDBMSEntity.setDatabaseType("Teradata");
    }

    @Override
    public InputRDBMSEntity getEntity() {
        return inputRDBMSEntity;
    }

}