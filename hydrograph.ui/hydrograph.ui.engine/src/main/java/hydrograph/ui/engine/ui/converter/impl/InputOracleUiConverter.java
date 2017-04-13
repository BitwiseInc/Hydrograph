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
package hydrograph.ui.engine.ui.converter.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.inputtypes.Oracle;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.DatabaseSelectionConfig;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.InputUiConverter;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.IOracle;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * Converter to convert jaxb oracle object into input Oracle component
 * @author Bitwise
 *
 */
public class InputOracleUiConverter extends InputUiConverter {
	
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(InputOracleUiConverter.class);
	
	public InputOracleUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new IOracle();
		this.propertyMap = new LinkedHashMap<>();
		
	}
	
	
	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching Input-Oracle-Properties for {}", componentName);
		Oracle inputOracle = (Oracle) typeBaseComponent;
		DatabaseSelectionConfig databaseSelectionConfig = new DatabaseSelectionConfig();
		Map<String, Object> additionalParameterDetails = new HashMap<String, Object>();

		setValueInPropertyMap(PropertyNameConstants.JDBC_DRIVER.value(),
				inputOracle.getDriverType() == null ? "" : inputOracle.getDriverType().getValue());

		setValueInPropertyMap(PropertyNameConstants.HOST_NAME.value(),
				inputOracle.getHostName() == null ? "" : inputOracle.getHostName().getValue());

		try {
			BigInteger bigInteger = inputOracle.getPort().getValue();
			setValueInPropertyMap(PropertyNameConstants.PORT_NO.value(),
					inputOracle.getPort() == null ? "" : String.valueOf(bigInteger));
		} catch (Exception e) {
			LOGGER.error("Exception" + e);
		}

		setValueInPropertyMap(PropertyNameConstants.ORACLE_SID.value(),
				inputOracle.getSid() == null ? "" : inputOracle.getSid().getValue());

		setValueInPropertyMap(PropertyNameConstants.SCHEMA_NAME.value(),
				inputOracle.getSchemaName() == null ? "" : inputOracle.getSchemaName().getValue());

		setValueInPropertyMap(PropertyNameConstants.USER_NAME.value(),
				inputOracle.getUserName() == null ? "" : inputOracle.getUserName().getValue());

		setValueInPropertyMap(PropertyNameConstants.PASSWORD.value(),
				inputOracle.getPassword() == null ? "" : inputOracle.getPassword().getValue());

		if (inputOracle.getTableName() != null && StringUtils.isNotBlank(inputOracle.getTableName().getValue())) {
			databaseSelectionConfig.setTableName(inputOracle.getTableName().getValue());
			databaseSelectionConfig.setTableNameSelection(true);
		} else {
			if (inputOracle.getTableName() != null) {
				setValueInPropertyMap(PropertyNameConstants.TABLE_NAME.value(), inputOracle.getTableName().getValue());
				databaseSelectionConfig.setTableName(getParameterValue(PropertyNameConstants.TABLE_NAME.value(), null));
				databaseSelectionConfig.setTableNameSelection(true);
			}
		}

		if (inputOracle.getSelectQuery() != null && StringUtils.isNotBlank(inputOracle.getSelectQuery().getValue())) {
			databaseSelectionConfig.setSqlQuery(inputOracle.getSelectQuery().getValue());
			databaseSelectionConfig.setTableNameSelection(false);
		} else {
			if (inputOracle.getSelectQuery() != null) {
				setValueInPropertyMap(PropertyNameConstants.SELECT_QUERY.value(),
						inputOracle.getSelectQuery().getValue());
				databaseSelectionConfig
						.setSqlQuery(getParameterValue(PropertyNameConstants.SELECT_QUERY.value(), null));
				databaseSelectionConfig.setTableNameSelection(false);
			}
		}

		propertyMap.put(PropertyNameConstants.SELECT_OPTION.value(), databaseSelectionConfig);
		
		if(inputOracle.getNumPartitions() !=null ){
			additionalParameterDetails.put(Constants.NUMBER_OF_PARTITIONS, getParameterValue(PropertyNameConstants.NUMBER_OF_PARTITIONS.value(),
					inputOracle.getNumPartitions() == null ? "" : inputOracle.getNumPartitions().getValue()));
				
			if(inputOracle.getNumPartitions().getColumnName() !=null && StringUtils.isNotBlank(inputOracle.getNumPartitions().getColumnName().getValue())){
				additionalParameterDetails.put(Constants.DB_PARTITION_KEY, inputOracle.getNumPartitions().getColumnName().getValue());
			}
			if(inputOracle.getNumPartitions().getUpperBound() !=null ){
				additionalParameterDetails.put(Constants.NOP_UPPER_BOUND, getParameterValue(PropertyNameConstants.UPPER_BOUND.value(),						
						inputOracle.getNumPartitions().getUpperBound().getValue()));
			}
			if(inputOracle.getNumPartitions().getLowerBound() !=null ){
				additionalParameterDetails.put(Constants.NOP_LOWER_BOUND,getParameterValue(PropertyNameConstants.LOWER_BOUND.value(), 
						inputOracle.getNumPartitions().getLowerBound().getValue()));
			}
		}
		
		if(inputOracle.getFetchSize() != null){
			additionalParameterDetails.put(Constants.ADDITIONAL_DB_FETCH_SIZE,getParameterValue(PropertyNameConstants.FETCH_SIZE.value(), 
					inputOracle.getFetchSize().getValue()));
		}
		
		if(inputOracle.getExtraUrlParams() !=null){
			additionalParameterDetails.put(Constants.ADDITIONAL_PARAMETERS_FOR_DB,getParameterValue(PropertyNameConstants.ADDITIONAL_DB_PARAM.value(), 
					inputOracle.getExtraUrlParams().getValue()));
		}
		
		propertyMap.put(PropertyNameConstants.INPUT_ADDITIONAL_PARAMETERS_FOR_DB_COMPONENTS.value(), additionalParameterDetails);

		System.out.println(propertyMap.toString());
		uiComponent.setType(UIComponentsConstants.ORACLE.value());
		uiComponent.setCategory(UIComponentsConstants.INPUT_CATEGORY.value());

		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(inputOracle.getId());
		uiComponent.setProperties(propertyMap);
	}
	
	
	
	@Override
	protected Object getSchema(TypeInputOutSocket outSocket) {
		LOGGER.debug("Generating UI-Schema data for {}", componentName);
		Schema schema = null;
		List<GridRow> gridRowList = new ArrayList<>();
		ConverterUiHelper converterUiHelper = new ConverterUiHelper(uiComponent);
		if (outSocket.getSchema() != null
				&& outSocket.getSchema().getFieldOrRecordOrIncludeExternalSchema().size() != 0) {
			schema = new Schema();
			for (Object record : outSocket.getSchema().getFieldOrRecordOrIncludeExternalSchema()) {
				if ((TypeExternalSchema.class).isAssignableFrom(record.getClass())) {
					schema.setIsExternal(true);
					if (((TypeExternalSchema) record).getUri() != null)
						schema.setExternalSchemaPath(((TypeExternalSchema) record).getUri());
				} else {
					gridRowList.add(converterUiHelper.getSchema(record));
					schema.setGridRow(gridRowList);
					schema.setIsExternal(false);
				}
				saveComponentOutputSchema(outSocket.getId(),gridRowList);
			}
		} 
		return schema;

	
	}

	@Override
	protected Map<String, String> getRuntimeProperties() {
		LOGGER.debug("Generating Runtime Properties for -{}", componentName);
		TreeMap<String, String> runtimeMap = null;
		TypeProperties typeProperties = ((Oracle) typeBaseComponent).getRuntimeProperties();
		if (typeProperties != null) {
			runtimeMap = new TreeMap<>();
			for (Property runtimeProperty : typeProperties.getProperty()) {
				runtimeMap.put(runtimeProperty.getName(), runtimeProperty.getValue());
			}
		}
		return runtimeMap;
	}
	
	private void setValueInPropertyMap(String propertyName,Object value){
		propertyMap.put(propertyName, getParameterValue(propertyName,value));
	}
	
}
