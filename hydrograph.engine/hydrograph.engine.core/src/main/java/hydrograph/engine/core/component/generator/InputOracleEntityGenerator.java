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
        inputRDBMSEntity.setRuntimeProperties(inputOracleJaxb.getRuntimeProperties() == null ? new Properties():InputEntityUtils
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
            inputRDBMSEntity.setTableName(
                    inputOracleJaxb.getSchemaName().getValue() + "." + inputOracleJaxb.getTableName().getValue());
        } else {
            inputRDBMSEntity.setSchemaName(null);
        }
    }


	
	

	@Override
	public InputRDBMSEntity getEntity() {
		return inputRDBMSEntity;
	}
}