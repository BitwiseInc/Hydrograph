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
import hydrograph.engine.jaxb.inputtypes.Oracle;
import hydrograph.engine.jaxb.ioracle.TypeInputOracleOutSocket;
import hydrograph.engine.jaxb.ioracle.TypePartitionsChoice;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.DatabaseSelectionConfig;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.InputConverter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;
/**
 * Converter implementation for Input Oracle Component
 * @author Bitwise
 *
 */
public class InputOracleConverter extends InputConverter {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(InputOracleConverter.class);
	private Oracle oracleInput;

	public InputOracleConverter(Component component) {
		super(component);
		this.baseComponent = new Oracle();
		this.component = component;
		this.properties = component.getProperties();
	}

	@Override
	protected List<TypeInputOutSocket> getInOutSocket() {
		logger.debug("Generating TypeInputOutSocket data for {}", properties.get(Constants.PARAM_NAME));
		List<TypeInputOutSocket> outSockets = new ArrayList<>();
		for (Link link : component.getSourceConnections()) {
			TypeInputOracleOutSocket outSocket = new TypeInputOracleOutSocket();
			outSocket.setId(link.getSourceTerminal());
			outSocket.setType(link.getSource().getPort(link.getSourceTerminal()).getPortType());
			outSocket.setSchema(getSchema());
			outSocket.getOtherAttributes();
			outSockets.add(outSocket);
		}
		return outSockets;
	}

	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for {}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		  oracleInput = (Oracle) baseComponent;
		oracleInput.setRuntimeProperties(getRuntimeProperties());

		ElementValueStringType sid = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.ORACLE_SID.value()))){
			sid.setValue(String.valueOf(properties.get(PropertyNameConstants.ORACLE_SID.value())));
			oracleInput.setSid(sid);
		}
		
		ElementValueStringType hostName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.HOST_NAME.value()))){
			hostName.setValue(String.valueOf(properties.get(PropertyNameConstants.HOST_NAME.value())));
			oracleInput.setHostName(hostName);
		}
		
		ElementValueIntegerType portNo = new ElementValueIntegerType();
		BigInteger portValue = getPortValue(PropertyNameConstants.PORT_NO.value());
		portNo.setValue(portValue);
		oracleInput.setPort(portNo);

		ElementValueStringType jdbcDriver = new ElementValueStringType();
		jdbcDriver.setValue(String.valueOf(properties.get(PropertyNameConstants.JDBC_DRIVER.value())));
		oracleInput.setDriverType(jdbcDriver);
		
		ElementValueStringType oracleSchema = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.SCHEMA_NAME.value()))){
			oracleSchema.setValue(String.valueOf(properties.get(PropertyNameConstants.SCHEMA_NAME.value())));
			oracleInput.setSchemaName(oracleSchema);
		}
		
		ElementValueStringType userName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.USER_NAME.value()))){
			userName.setValue(String.valueOf(properties.get(PropertyNameConstants.USER_NAME.value())));
			oracleInput.setUserName(userName);
		}
		
		ElementValueStringType password = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.PASSWORD.value()))){
			password.setValue(String.valueOf(properties.get(PropertyNameConstants.PASSWORD.value())));
			oracleInput.setPassword(password);
		}

		DatabaseSelectionConfig databaseSelectionConfig = (DatabaseSelectionConfig) properties
				.get(PropertyNameConstants.SELECT_OPTION.value());

		if (databaseSelectionConfig != null) {

			if (databaseSelectionConfig.isTableName()) {
				if(StringUtils.isNotBlank(databaseSelectionConfig.getTableName())){
					ElementValueStringType tableName = new ElementValueStringType();
					tableName.setValue(databaseSelectionConfig.getTableName());
					oracleInput.setTableName(tableName);
				}
				
			} else {
				if(StringUtils.isNotBlank(databaseSelectionConfig.getSqlQuery())){
					ElementValueStringType sqlQuery = new ElementValueStringType();
					sqlQuery.setValue(databaseSelectionConfig.getSqlQuery());
					oracleInput.setSelectQuery(sqlQuery);
				}
			}
		}
		
		addAdditionalParameters();

	}

	private void addAdditionalParameters() {
		Object obj = properties.get(PropertyNameConstants.INPUT_ADDITIONAL_PARAMETERS_FOR_DB_COMPONENTS.value());
		if(obj != null && Map.class.isAssignableFrom(obj.getClass())){
			Map<String, String> uiValue = (Map<String, String>) properties
					.get(PropertyNameConstants.INPUT_ADDITIONAL_PARAMETERS_FOR_DB_COMPONENTS.value());
			
			if (uiValue != null) {
				
				if (StringUtils.isNotBlank((String) uiValue.get(Constants.ADDITIONAL_DB_FETCH_SIZE))) {
					ElementValueStringType fetchSize = new ElementValueStringType();
					fetchSize.setValue(String.valueOf(uiValue.get(Constants.ADDITIONAL_DB_FETCH_SIZE)));
					oracleInput.setFetchSize(fetchSize);
				}else{
					ElementValueStringType fetchSize = new ElementValueStringType();
					fetchSize.setValue("1000");
					oracleInput.setFetchSize(fetchSize);
				}
				
				if (StringUtils.isNotBlank((String) uiValue.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB))) {
					ElementValueStringType extraUrlParams = new ElementValueStringType();
					extraUrlParams.setValue(String.valueOf(uiValue.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB)));
					oracleInput.setExtraUrlParams(extraUrlParams);
				}
				
				if (StringUtils.isNotBlank(uiValue.get(Constants.NUMBER_OF_PARTITIONS))) {
					TypePartitionsChoice typePartitionsChoice = new TypePartitionsChoice();
					
						ElementValueIntegerType partitionKey = new ElementValueIntegerType();
						BigInteger no_of_partitions = getDBAdditionalParamValue(PropertyNameConstants.INPUT_ADDITIONAL_PARAMETERS_FOR_DB_COMPONENTS.value(),
								Constants.NUMBER_OF_PARTITIONS, PropertyNameConstants.NUMBER_OF_PARTITIONS.value());
						partitionKey.setValue(no_of_partitions);
						typePartitionsChoice.setValue(no_of_partitions);
						
					if (StringUtils.isNotBlank((String) uiValue.get(Constants.DB_PARTITION_KEY))) {
						ElementValueStringType partitionKeyColumn = new ElementValueStringType();
						partitionKeyColumn.setValue(String.valueOf(uiValue.get(Constants.DB_PARTITION_KEY)));
						typePartitionsChoice.setColumnName(partitionKeyColumn);
					}
					if (uiValue.get(Constants.NOP_LOWER_BOUND) !=null) {
						ElementValueIntegerType partition_key_lower_bound = new ElementValueIntegerType();
						BigInteger partition_lower_bound = getDBAdditionalParamValue(PropertyNameConstants.INPUT_ADDITIONAL_PARAMETERS_FOR_DB_COMPONENTS.value(),
								Constants.NOP_LOWER_BOUND, PropertyNameConstants.NOP_LOWER_BOUND.value());
						partition_key_lower_bound.setValue(partition_lower_bound);
						typePartitionsChoice.setLowerBound(partition_key_lower_bound);
					}
					if (uiValue.get(Constants.NOP_UPPER_BOUND) !=null) {
						ElementValueIntegerType partition_key_upper_bound = new ElementValueIntegerType();
						BigInteger partition_upper_bound = getDBAdditionalParamValue(PropertyNameConstants.INPUT_ADDITIONAL_PARAMETERS_FOR_DB_COMPONENTS.value(),
								Constants.NOP_UPPER_BOUND, PropertyNameConstants.NOP_UPPER_BOUND.value());
						partition_key_upper_bound.setValue(partition_upper_bound);
						typePartitionsChoice.setUpperBound(partition_key_upper_bound);
					}
					oracleInput.setNumPartitions(typePartitionsChoice);
				}
			}
		}else{
			ElementValueStringType fetchSize = new ElementValueStringType();
			fetchSize.setValue("1000");
			oracleInput.setFetchSize(fetchSize);
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
