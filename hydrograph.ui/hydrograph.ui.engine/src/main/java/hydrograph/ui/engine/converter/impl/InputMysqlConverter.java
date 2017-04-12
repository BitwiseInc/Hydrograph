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
 * limitations under the License.
 *******************************************************************************/
package hydrograph.ui.engine.converter.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.ElementValueIntegerType;
import hydrograph.engine.jaxb.commontypes.ElementValueStringType;
import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.imysql.TypeInputMysqlOutSocket;
import hydrograph.engine.jaxb.imysql.TypePartitionsChoice;
import hydrograph.engine.jaxb.inputtypes.Mysql;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.DatabaseSelectionConfig;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.InputConverter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class InputMysql Converter implementation for Input Mysql Component
 * @author Bitwise
 *
 */
public class InputMysqlConverter extends InputConverter{

	private static final Logger logger = LogFactory.INSTANCE.getLogger(InputMysqlConverter.class);
	private Mysql mysqlInput;
	
	public InputMysqlConverter(Component component) {
		super(component);
		this.baseComponent = new Mysql();
		this.component = component;
		this.properties = component.getProperties();
	}

	@Override
	protected List<TypeInputOutSocket> getInOutSocket() {
		logger.debug("Generating TypeInputOutSocket data for {}", properties.get(Constants.PARAM_NAME));
		List<TypeInputOutSocket> inputOutSockets = new ArrayList<>();
		for (Link link : component.getSourceConnections()) {
			TypeInputMysqlOutSocket inputMysqlOutSocket = new TypeInputMysqlOutSocket();
			inputMysqlOutSocket.setId(link.getSourceTerminal());
			inputMysqlOutSocket.setType(link.getSource().getPort(link.getSourceTerminal()).getPortType());
			inputMysqlOutSocket.setSchema(getSchema());
			inputMysqlOutSocket.getOtherAttributes();
			inputOutSockets.add(inputMysqlOutSocket);
		}
		return inputOutSockets;
	}

	@Override
	public void prepareForXML() {
		super.prepareForXML();
		 mysqlInput = (Mysql) baseComponent;
		mysqlInput.setRuntimeProperties(getRuntimeProperties());

		ElementValueStringType dataBaseName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.DATABASE_NAME.value()))){
			dataBaseName.setValue(String.valueOf(properties.get(PropertyNameConstants.DATABASE_NAME.value())));
			mysqlInput.setDatabaseName(dataBaseName);
		}
		
		ElementValueStringType hostName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.HOST_NAME.value()))){
			hostName.setValue(String.valueOf(properties.get(PropertyNameConstants.HOST_NAME.value())));
			mysqlInput.setHostName(hostName);
		}
		
		ElementValueStringType driverName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.JDBC_DRIVER.value()))){
			driverName.setValue(String.valueOf(properties.get(PropertyNameConstants.JDBC_DRIVER.value())));
			mysqlInput.setJdbcDriver(driverName);
		}
		
		ElementValueIntegerType portNo = new ElementValueIntegerType();
		BigInteger portValue = getPortValue(PropertyNameConstants.PORT_NO.value());
		portNo.setValue(portValue);
		mysqlInput.setPort(portNo);
		
		ElementValueStringType userName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.USER_NAME.value()))){
			userName.setValue(String.valueOf(properties.get(PropertyNameConstants.USER_NAME.value())));
			mysqlInput.setUsername(userName);
		}
		
		ElementValueStringType password = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.PASSWORD.value()))){
			password.setValue(String.valueOf(properties.get(PropertyNameConstants.PASSWORD.value())));
			mysqlInput.setPassword(password);
		}
		
		DatabaseSelectionConfig databaseSelectionConfig = (DatabaseSelectionConfig) properties
				.get(PropertyNameConstants.SELECT_OPTION.value());
		
		if (databaseSelectionConfig != null) {

			if (databaseSelectionConfig.isTableName()) {
				ElementValueStringType tableName = new ElementValueStringType();
				tableName.setValue(databaseSelectionConfig.getTableName());
				mysqlInput.setTableName(tableName);
				
			} else {
				if(StringUtils.isNotBlank(databaseSelectionConfig.getSqlQuery())){
					ElementValueStringType sqlQuery = new ElementValueStringType();
					sqlQuery.setValue(databaseSelectionConfig.getSqlQuery());
					mysqlInput.setSelectQuery(sqlQuery);
				}

				//TODO Below code will use in future
				/*ElementValueStringType sqlQueryCounter = new ElementValueStringType();
				if(databaseSelectionConfig.getSqlQueryCounter() !=null && StringUtils.isNotBlank(databaseSelectionConfig.getSqlQueryCounter())){
				sqlQueryCounter.setValue(databaseSelectionConfig.getSqlQueryCounter());
				mysqlInput.setCountQuery(sqlQueryCounter);
				}*/
			}
		}
		
		addAdditionalParameters();
		
	}
	
	private void addAdditionalParameters() {
		
		Map<String, String> uiValue = (Map<String, String>) properties
				.get(PropertyNameConstants.INPUT_ADDITIONAL_PARAMETERS_FOR_DB_COMPONENTS.value());
		
		if (uiValue != null) {
			if (StringUtils.isNotBlank((String) uiValue.get(Constants.FECTH_SIZE))) {
				ElementValueStringType fetchSize = new ElementValueStringType();
				fetchSize.setValue(String.valueOf(uiValue.get(Constants.FECTH_SIZE)));
				mysqlInput.setFetchSize(fetchSize);
			}
			if (StringUtils.isNotBlank((String) uiValue.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB))) {
				ElementValueStringType extraUrlParams = new ElementValueStringType();
				extraUrlParams.setValue(String.valueOf(uiValue.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB)));
				mysqlInput.setExtraUrlParams(extraUrlParams);
			}

			if (uiValue.get(Constants.NO_OF_PARTITION) !=null) {
				TypePartitionsChoice typePartitionsChoice = new TypePartitionsChoice();

				if (uiValue.get(Constants.NO_OF_PARTITION) !=null) {
					BigInteger no_of_partitions = new BigInteger(
							String.valueOf(uiValue.get(Constants.NO_OF_PARTITION)));
					typePartitionsChoice.setValue(no_of_partitions);
				}
				if (StringUtils.isNotBlank((String) uiValue.get(Constants.DB_PARTITION_KEY))) {
					ElementValueStringType partitionKey = new ElementValueStringType();
					partitionKey.setValue(String.valueOf(uiValue.get(Constants.DB_PARTITION_KEY)));
					typePartitionsChoice.setColumnName(partitionKey);
				}
				if (uiValue.get(Constants.PARTITION_KEY_LOWER_BOUND) !=null) {
					ElementValueIntegerType partition_key_lower_bound = new ElementValueIntegerType();
					BigInteger partition_lower_bound = new BigInteger(
							String.valueOf(uiValue.get(Constants.PARTITION_KEY_LOWER_BOUND)));
					partition_key_lower_bound.setValue(partition_lower_bound);
					typePartitionsChoice.setLowerBound(partition_key_lower_bound);
				}
				if (uiValue.get(Constants.PARTITION_KEY_UPPER_BOUND) !=null) {
					ElementValueIntegerType partition_key_upper_bound = new ElementValueIntegerType();
					BigInteger partition_upper_bound = new BigInteger(
							String.valueOf(uiValue.get(Constants.PARTITION_KEY_UPPER_BOUND)));
					partition_key_upper_bound.setValue(partition_upper_bound);
					typePartitionsChoice.setUpperBound(partition_key_upper_bound);
				}
				mysqlInput.setNumPartitions(typePartitionsChoice);
			}

		}
		
	}

	@Override
	protected List<TypeBaseField> getFieldOrRecord(List<GridRow> list) {
		logger.debug("Generating data for {} for property {}",
				new Object[] { properties.get(Constants.PARAM_NAME), PropertyNameConstants.SCHEMA.value() });

		List<TypeBaseField> typeBaseFields = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (GridRow object : list) {
				typeBaseFields.add(converterHelper.getSchemaGridTargetData(object));
			}
		}
		return typeBaseFields;
	}

}
