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
import hydrograph.engine.jaxb.commontypes.TypeFieldName;
import hydrograph.engine.jaxb.commontypes.TypeKeyFields;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.ooracle.TypeLoadChoice;
import hydrograph.engine.jaxb.ooracle.TypeOutputOracleInSocket;
import hydrograph.engine.jaxb.ooracle.TypePrimaryKeys;
import hydrograph.engine.jaxb.ooracle.TypeUpdateKeys;
import hydrograph.engine.jaxb.outputtypes.Oracle;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.OutputConverter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * Converter implementation for Output Oracle Component
 * @author Bitwise
 *
 */
public class OutputOracleConverter extends OutputConverter {
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(OutputOracleConverter.class);
	private Oracle oracleOutput;

	public OutputOracleConverter(Component component) {
		super(component);
		this.component = component;
		this.properties = component.getProperties();
		this.baseComponent = new Oracle();
	}
	
	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for {}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		 oracleOutput = (Oracle) baseComponent;
		oracleOutput.setRuntimeProperties(getRuntimeProperties());
		
		ElementValueStringType sid = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.ORACLE_SID.value()))){
			sid.setValue(String.valueOf(properties.get(PropertyNameConstants.ORACLE_SID.value())));
			oracleOutput.setSid(sid);
		}
		
		ElementValueStringType tableName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.TABLE_NAME.value()))){
			tableName.setValue(String.valueOf(properties.get(PropertyNameConstants.TABLE_NAME.value())));
			oracleOutput.setTableName(tableName);
		}
		
		ElementValueStringType hostName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.HOST_NAME.value()))){
			hostName.setValue(String.valueOf(properties.get(PropertyNameConstants.HOST_NAME.value())));
			oracleOutput.setHostName(hostName);
		}
		
		ElementValueIntegerType portNo = new ElementValueIntegerType();
		BigInteger portValue = getPortValue(PropertyNameConstants.PORT_NO.value());
		portNo.setValue(portValue);
		oracleOutput.setPort(portNo);
		
		ElementValueStringType jdbcDriver = new ElementValueStringType();
		jdbcDriver.setValue(String.valueOf(properties.get(PropertyNameConstants.JDBC_DRIVER.value())));
		oracleOutput.setDriverType(jdbcDriver);
		
		ElementValueStringType oracleSchema = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.SCHEMA_NAME.value()))){
			oracleSchema.setValue(String.valueOf(properties.get(PropertyNameConstants.SCHEMA_NAME.value())));
			oracleOutput.setSchemaName(oracleSchema);
		}
		
		ElementValueStringType userName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.USER_NAME.value()))){
			userName.setValue(String.valueOf(properties.get(PropertyNameConstants.USER_NAME.value())));
			oracleOutput.setUserName(userName);
		}
		
		ElementValueStringType password = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.PASSWORD.value()))){
			password.setValue(String.valueOf(properties.get(PropertyNameConstants.PASSWORD.value())));
			oracleOutput.setPassword(password);
		}
		
		TypeLoadChoice loadValue = addTypeLoadChoice();
		oracleOutput.setLoadType(loadValue);
		
		getAdditionalParameterForDBComponent();
	}
	
	private TypeLoadChoice addTypeLoadChoice() {
		TypeLoadChoice loadValue = new TypeLoadChoice();
		Map<String, String> uiValue = (Map<String, String>) properties.get(PropertyNameConstants.LOAD_TYPE_CONFIGURATION.value());
		if(uiValue != null && !uiValue.isEmpty()){
			if (uiValue.containsKey(Constants.LOAD_TYPE_UPDATE_KEY)) {
				loadValue.setUpdate(getUpdateKeys((String) uiValue.get(Constants.LOAD_TYPE_UPDATE_KEY)));
			} else if (uiValue.containsKey(Constants.LOAD_TYPE_NEW_TABLE_KEY)) {
				loadValue.setNewTable(getPrimaryKeyColumnFeilds((String) uiValue.get(Constants.LOAD_TYPE_NEW_TABLE_KEY)));
			} else if (uiValue.containsKey(Constants.LOAD_TYPE_INSERT_KEY)) {
				loadValue.setInsert(uiValue.get(Constants.LOAD_TYPE_INSERT_KEY));
			} else if (uiValue.containsKey(Constants.LOAD_TYPE_REPLACE_KEY)) {
				loadValue.setTruncateLoad(uiValue.get(Constants.LOAD_TYPE_REPLACE_KEY));
			}
		}else{
			loadValue.setNewTable(getPrimaryKeyColumnFeilds(""));
		}
		
		return loadValue;
	}
	
	private void getAdditionalParameterForDBComponent() {
		Object obj = properties.get(PropertyNameConstants.OUTPUT_ADDITIONAL_PARAMETERS_FOR_DB_COMPONENTS.value());
		if(obj != null && Map.class.isAssignableFrom(obj.getClass())){
			Map<String, String> uiValue = (Map<String, String>) properties.get(PropertyNameConstants.OUTPUT_ADDITIONAL_PARAMETERS_FOR_DB_COMPONENTS.value());
			if(uiValue !=null && !uiValue.isEmpty()){
				if(StringUtils.isNotBlank((String)uiValue.get(Constants.ADDITIONAL_DB_CHUNK_SIZE))){
					ElementValueStringType chunkSize = new ElementValueStringType();
					chunkSize.setValue(String.valueOf(uiValue.get(Constants.ADDITIONAL_DB_CHUNK_SIZE)));
					oracleOutput.setChunkSize(chunkSize);
				}
				if(StringUtils.isNotBlank((String)uiValue.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB))){
					ElementValueStringType extraUrlParams = new ElementValueStringType();
					extraUrlParams.setValue(String.valueOf(uiValue.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB)));
					oracleOutput.setExtraUrlParams(extraUrlParams);
				}
			}else{
				ElementValueStringType chunkSize = new ElementValueStringType();
				chunkSize.setValue("1000");
				oracleOutput.setChunkSize(chunkSize);
			}
		}else{
			ElementValueStringType chunkSize = new ElementValueStringType();
			chunkSize.setValue("1000");
			oracleOutput.setChunkSize(chunkSize);
		}
	}

	/**
	 * Creates primary key fields
	 * @param primaryKeyFeilds
	 * @return
	 */
	private TypePrimaryKeys getPrimaryKeyColumnFeilds(String primaryKeyFeilds) {
		TypePrimaryKeys primaryKeys = new TypePrimaryKeys();
		String[] primaryKeyColumnsFeilds = StringUtils.split(primaryKeyFeilds, Constants.LOAD_TYPE_NEW_TABLE_VALUE_SEPERATOR);
		if(primaryKeyColumnsFeilds !=null && primaryKeyColumnsFeilds.length>0){
			TypeKeyFields primaryTypeKeyFields = new TypeKeyFields();
			primaryKeys.setPrimaryKeys(primaryTypeKeyFields);
			for(String fieldValue : primaryKeyColumnsFeilds){
				TypeFieldName primaryTypeFieldName = new TypeFieldName();
				primaryTypeFieldName.setName(fieldValue);
				primaryTypeKeyFields.getField().add(primaryTypeFieldName);
			}
		}
				
		return primaryKeys;
	}
	
	/**
	 * Creates update key fields 
	 * @param fields
	 * @return
	 */
	private TypeUpdateKeys getUpdateKeys(String fields) {
		TypeUpdateKeys updateKeys = null;
		String[] columnFields = StringUtils.split(fields, Constants.LOAD_TYPE_UPDATE_VALUE_SEPERATOR);
		if (columnFields != null && columnFields.length > 0) {
			TypeKeyFields typeKeyFields = new TypeKeyFields();
			updateKeys = new TypeUpdateKeys();
			updateKeys.setUpdateByKeys(typeKeyFields);
			for (String field : columnFields) {
				TypeFieldName typeFieldName = new TypeFieldName();
				typeFieldName.setName(field);
				typeKeyFields.getField().add(typeFieldName);
			}
		}
		
		return updateKeys;
	}

	@Override
	protected List<TypeOutputInSocket> getOutInSocket() {
		logger.debug("Generating TypeOutputInSocket data");
		List<TypeOutputInSocket> outputinSockets = new ArrayList<>();
		for (Link link : component.getTargetConnections()) {
			TypeOutputOracleInSocket outInSocket = new TypeOutputOracleInSocket();
			outInSocket.setId(link.getTargetTerminal());
			outInSocket.setFromSocketId(converterHelper.getFromSocketId(link));
			outInSocket.setFromSocketType(link.getSource().getPorts().get(link.getSourceTerminal()).getPortType());
			outInSocket.setType(link.getTarget().getPort(link.getTargetTerminal()).getPortType());
			outInSocket.setSchema(getSchema());
			outInSocket.getOtherAttributes();
			outInSocket.setFromComponentId(link.getSource().getComponentId());
			outputinSockets.add(outInSocket);
		}
		return outputinSockets;
		
	}

	@Override
	protected List<TypeBaseField> getFieldOrRecord(List<GridRow> list) {
		logger.debug("Generating data for {} for property {}", new Object[] { properties.get(Constants.PARAM_NAME),
				PropertyNameConstants.SCHEMA.value() });

		List<TypeBaseField> typeBaseFields = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (GridRow object : list)
				typeBaseFields.add(converterHelper.getSchemaGridTargetData(object));

		}
		return typeBaseFields;
	}
	

}
