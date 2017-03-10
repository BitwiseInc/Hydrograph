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

import java.util.ArrayList;
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
import hydrograph.engine.jaxb.ojdbcupdate.TypeUpdateKeys;
import hydrograph.engine.jaxb.outputtypes.JdbcUpdate;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.OutputUiConverter;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.ODBUpdate;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class OutputDBUpdateUiConverter.
 * UiConverter for OutputDBUpdate component.
 * @author Bitwise
 */

public class OutputDBUpdateUiConverter extends OutputUiConverter{
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(OutputDBUpdateUiConverter.class);
	
	public OutputDBUpdateUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new ODBUpdate();
		this.propertyMap = new LinkedHashMap<>();
	}
	
	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching Output-Oracle-Properties for {}", componentName);
		JdbcUpdate jdbcUpdate = (JdbcUpdate) typeBaseComponent;
		
		setValueInPropertyMap(PropertyNameConstants.URL.value(), 
				jdbcUpdate.getUrl() == null ? "" : jdbcUpdate.getUrl().getValue()); 
		
		setValueInPropertyMap(PropertyNameConstants.TABLE_NAME.value(), 
				jdbcUpdate.getTableName() == null ? "" : jdbcUpdate.getTableName().getValue());
		
		setValueInPropertyMap(PropertyNameConstants.BATCH_SIZE.value(), 
				jdbcUpdate.getBatchSize() == null ? "" : jdbcUpdate.getBatchSize().getValue());
		
		setValueInPropertyMap(PropertyNameConstants.USER_NAME.value(), 
				jdbcUpdate.getUserName() == null ? "" : jdbcUpdate.getUserName().getValue());
		
		setValueInPropertyMap(PropertyNameConstants.PASSWORD.value(), 
				jdbcUpdate.getPassword()== null ? "" : jdbcUpdate.getPassword().getValue());
		
		if(jdbcUpdate.getUpdate() !=null){
			propertyMap.put(PropertyNameConstants.SELECT_UPDATE_KEYS.value(),getUpdateKeyUIValue(jdbcUpdate.getUpdate()));
		}
		
		if(StringUtils.isNotBlank(jdbcUpdate.getJdbcDriverClass().getValue())){
			propertyMap.put(PropertyNameConstants.JDBC_DB_DRIVER.value(), jdbcUpdate.getJdbcDriverClass().getValue());
		}
		uiComponent.setType(UIComponentsConstants.DB_UPDATE.value());
		uiComponent.setCategory(UIComponentsConstants.OUTPUT_CATEGORY.value());
		
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(jdbcUpdate.getId());
		uiComponent.setProperties(propertyMap);
	}

	/**
	 *  Appends update keys using a comma
	 * @param typeUpdateKeys
	 */
	private String getUpdateKeyUIValue(TypeUpdateKeys typeUpdateKeys) {
		StringBuffer buffer=new StringBuffer();
			if(typeUpdateKeys!=null && typeUpdateKeys.getUpdateByKeys()!=null){
				TypeKeyFields keyFields=typeUpdateKeys.getUpdateByKeys();
				for(TypeFieldName fieldName:keyFields.getField()){
					buffer.append(fieldName.getName());
					buffer.append(",");
					}
			}
		
		return StringUtils.removeEnd(buffer.toString(), ",");
	}
	
	@Override
	protected Object getSchema(TypeOutputInSocket inSocket) {
		LOGGER.debug("Generating UI-Schema data for OutPut-DBUpdate-Component - {}", componentName);
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
		TypeProperties typeProperties = ((JdbcUpdate) typeBaseComponent).getRuntimeProperties();
		if (typeProperties != null) {
			runtimeMap = new TreeMap<>();
			for (Property runtimeProperty : typeProperties.getProperty()) {
				runtimeMap.put(runtimeProperty.getName(), runtimeProperty.getValue());
			}
		}
		return runtimeMap;
	}
	
	private void setValueInPropertyMap(String propertyName,Object value){
		propertyMap.put(propertyName, getParameterValue(propertyName,value));;
	}

}
