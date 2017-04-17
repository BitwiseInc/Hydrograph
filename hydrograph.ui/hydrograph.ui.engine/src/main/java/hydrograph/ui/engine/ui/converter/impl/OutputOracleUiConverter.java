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
import hydrograph.engine.jaxb.commontypes.TypeFieldName;
import hydrograph.engine.jaxb.commontypes.TypeKeyFields;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.ooracle.TypePrimaryKeys;
import hydrograph.engine.jaxb.ooracle.TypeUpdateKeys;
import hydrograph.engine.jaxb.outputtypes.Oracle;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.OutputUiConverter;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.OOracle;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class OutputOracleUiConverter to convert jaxb Oracle object into Oracle component
 * @author Bitwise
 *
 */
public class OutputOracleUiConverter extends OutputUiConverter {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(OutputOracleUiConverter.class);
	
	public OutputOracleUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new OOracle();
		this.propertyMap = new LinkedHashMap<>();
	}
	
	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching Output-Oracle-Properties for {}", componentName);
		Oracle outputOracle = (Oracle) typeBaseComponent;
		LinkedHashMap<String, String> loadSelectedDetails = new LinkedHashMap<String, String>();
		Map<String, Object> additionalParameterDetails = new HashMap<String, Object>();
		
		setValueInPropertyMap(PropertyNameConstants.JDBC_DRIVER.value(), 
				outputOracle.getDriverType() == null ? "" : outputOracle.getDriverType().getValue());
		
		setValueInPropertyMap(PropertyNameConstants.HOST_NAME.value(), 
				outputOracle.getHostName() == null ? "" : outputOracle.getHostName().getValue());
		
		try {
			BigInteger bigInteger = outputOracle.getPort().getValue();
			setValueInPropertyMap(PropertyNameConstants.PORT_NO.value(),
					outputOracle.getPort() == null ? "" : String.valueOf(bigInteger));
		} catch (Exception e) {
			LOGGER.error("Exception" + e);
		}
		
		setValueInPropertyMap(PropertyNameConstants.ORACLE_SID.value(), 
				outputOracle.getSid() == null ? "" : outputOracle.getSid().getValue());
		
		setValueInPropertyMap(PropertyNameConstants.SCHEMA_NAME.value(), 
				outputOracle.getSchemaName() == null ? "" : outputOracle.getSchemaName().getValue());
		
		setValueInPropertyMap(PropertyNameConstants.USER_NAME.value(), 
				outputOracle.getUserName()==null ? "" : outputOracle.getUserName().getValue());
		
		setValueInPropertyMap(PropertyNameConstants.PASSWORD.value(), 
				outputOracle.getPassword()==null ? "" : outputOracle.getPassword().getValue());
		
		setValueInPropertyMap(PropertyNameConstants.TABLE_NAME.value(), 
				outputOracle.getTableName()==null ? "" : outputOracle.getTableName().getValue());
		
		if(outputOracle.getLoadType() !=null){
			if(outputOracle.getLoadType().getInsert() !=null){
				loadSelectedDetails.put(Constants.LOAD_TYPE_INSERT_KEY, outputOracle.getLoadType().getInsert().toString());
			}else if(outputOracle.getLoadType().getTruncateLoad() !=null){
				loadSelectedDetails.put(Constants.LOAD_TYPE_REPLACE_KEY,outputOracle.getLoadType().getTruncateLoad().toString());
			} else if(outputOracle.getLoadType().getUpdate() !=null){
				loadSelectedDetails.put(Constants.LOAD_TYPE_UPDATE_KEY,getLoadTypeUpdateKeyUIValue(outputOracle.getLoadType().getUpdate()));
			}else if(outputOracle.getLoadType().getNewTable() !=null){
				loadSelectedDetails.put(Constants.LOAD_TYPE_NEW_TABLE_KEY,getLoadTypePrimaryKeyUIValue(outputOracle.getLoadType().getNewTable()));
			}
				
		}
		propertyMap.put(PropertyNameConstants.LOAD_TYPE_CONFIGURATION.value(), loadSelectedDetails);
		
		if(outputOracle.getChunkSize() !=null){
			additionalParameterDetails.put(Constants.ADDITIONAL_DB_CHUNK_SIZE, getParameterValue(PropertyNameConstants.CHUNK_SIZE.value(),
					outputOracle.getChunkSize() == null ? "" : outputOracle.getChunkSize().getValue()));
		}
		
		if(outputOracle.getExtraUrlParams() !=null){
			additionalParameterDetails.put(Constants.ADDITIONAL_PARAMETERS_FOR_DB, getParameterValue(PropertyNameConstants.ADDITIONAL_DB_PARAM.value(),
					outputOracle.getExtraUrlParams() == null ? "" : outputOracle.getExtraUrlParams().getValue()));
		}
		
		propertyMap.put(PropertyNameConstants.OUTPUT_ADDITIONAL_PARAMETERS_FOR_DB_COMPONENTS.value(), additionalParameterDetails);
		
		uiComponent.setType(UIComponentsConstants.ORACLE.value());
		uiComponent.setCategory(UIComponentsConstants.OUTPUT_CATEGORY.value());
		
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(outputOracle.getId());
		uiComponent.setProperties(propertyMap);
	}
	
	/**
	 * Appends primary keys using a comma
	 * @param newTable
	 */
	private String getLoadTypePrimaryKeyUIValue(TypePrimaryKeys newTable) {
		StringBuffer stringBuffer = new StringBuffer();
		if(newTable !=null && newTable.getPrimaryKeys() !=null){
			TypeKeyFields typeKeyFields = newTable.getPrimaryKeys();
			for(TypeFieldName typeFieldName : typeKeyFields.getField()){
				stringBuffer.append(typeFieldName.getName());
				stringBuffer.append(",");
			}
		}
		return StringUtils.removeEnd(stringBuffer.toString(), ",");
	}
	
	/**
	 *  Appends update keys using a comma
	 * @param update
	 */
	private String getLoadTypeUpdateKeyUIValue(TypeUpdateKeys update) {
		StringBuffer buffer=new StringBuffer();
			if(update!=null && update.getUpdateByKeys()!=null){
				TypeKeyFields keyFields=update.getUpdateByKeys();
				for(TypeFieldName fieldName:keyFields.getField()){
					buffer.append(fieldName.getName());
					buffer.append(",");
					}
			}
		
		return StringUtils.removeEnd(buffer.toString(), ",");
	}

	@Override
	protected Object getSchema(TypeOutputInSocket inSocket) {
		LOGGER.debug("Generating UI-Schema data for OutPut-Oracle-Component - {}", componentName);
		Schema schema = null;
		List<GridRow> gridRow = new ArrayList<>();
		ConverterUiHelper converterUiHelper = new ConverterUiHelper(uiComponent);
		if (inSocket.getSchema() != null && inSocket.getSchema().getFieldOrRecordOrIncludeExternalSchema().size() != 0) {
			schema=new Schema();
			for (Object record : inSocket.getSchema().getFieldOrRecordOrIncludeExternalSchema()) {
				if ((TypeExternalSchema.class).isAssignableFrom(record.getClass())) {
					schema.setIsExternal(true);
					if (((TypeExternalSchema) record).getUri() != null)
						schema.setExternalSchemaPath(((TypeExternalSchema) record).getUri());
				} else {
					gridRow.add(converterUiHelper.getSchema(record));
					schema.setGridRow(gridRow);
					schema.setIsExternal(false);
				}
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
