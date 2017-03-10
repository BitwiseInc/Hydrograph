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
import hydrograph.engine.jaxb.oredshift.TypePrimaryKeys;
import hydrograph.engine.jaxb.oredshift.TypeUpdateKeys;
import hydrograph.engine.jaxb.outputtypes.Redshift;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.OutputUiConverter;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.ORedshift;
import hydrograph.ui.logging.factory.LogFactory;

/**
 *The Class OutputRedshiftUiConverter to convert jaxb Redshift object into Redshift component
 * @author Bitwise
 *
 */
public class OutputRedshiftUiConverter extends OutputUiConverter {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(OutputRedshiftUiConverter.class);
	Redshift redshift;
	private LinkedHashMap<String, String> loadSelectedDetails;

	public OutputRedshiftUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new ORedshift();
		this.propertyMap = new LinkedHashMap<>();
		redshift = (Redshift) typeBaseComponent;
	}

	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		loadSelectedDetails = new LinkedHashMap<String, String>();
		
		LOGGER.debug("Fetching OutPut-Redshift-Component for {}", componentName);
		redshift = (Redshift) typeBaseComponent;
		
				
		setValueInPropertyMap(PropertyNameConstants.DATABASE_NAME.value(), redshift.getDatabaseName()==null?"":redshift.getDatabaseName().getValue());
				
		setValueInPropertyMap(PropertyNameConstants.JDBC_DRIVER.value(), redshift.getJdbcDriver()==null?"":redshift.getJdbcDriver().getValue());
		
		setValueInPropertyMap(PropertyNameConstants.HOST_NAME.value(), redshift.getHostName()==null?"":redshift.getHostName().getValue());
				
		try {
			BigInteger bigInteger = redshift.getPort().getValue();
			setValueInPropertyMap(PropertyNameConstants.PORT_NO.value(),
					redshift.getPort() == null ? "" : bigInteger);
		} catch (Exception e) {
			LOGGER.error("Exception" + e);
		}
		
		setValueInPropertyMap(PropertyNameConstants.USER_NAME.value(), redshift.getUserName()==null?"":redshift.getUserName().getValue());

		setValueInPropertyMap(PropertyNameConstants.PASSWORD.value(), redshift.getPassword()==null?"":redshift.getPassword().getValue());
				
		setValueInPropertyMap(PropertyNameConstants.TABLE_NAME.value(), redshift.getTableName()==null?"":redshift.getTableName().getValue());
		
		
		if(redshift.getLoadType() !=null){
			if(redshift.getLoadType().getInsert() !=null){
				loadSelectedDetails.put(Constants.LOAD_TYPE_INSERT_KEY, redshift.getLoadType().getInsert().toString());
			}else if(redshift.getLoadType().getTruncateLoad() !=null){
				loadSelectedDetails.put(Constants.LOAD_TYPE_REPLACE_KEY,redshift.getLoadType().getTruncateLoad().toString());
			} 
			//TODO Currently, below widget is not use for spark support
			/*else if(redshift.getLoadType().getUpdate() !=null){
				loadSelectedDetails.put(Constants.LOAD_TYPE_UPDATE_KEY,getLoadTypeUpdateKeyUIValue(redshift.getLoadType().getUpdate()));
			}*/else if(redshift.getLoadType().getNewTable() !=null){
				loadSelectedDetails.put(Constants.LOAD_TYPE_NEW_TABLE_KEY,getLoadTypePrimaryKeyUIValue(redshift.getLoadType().getNewTable()));
			}
				
		}
		
		propertyMap.put(PropertyNameConstants.LOAD_TYPE_CONFIGURATION.value(), loadSelectedDetails);
		
		uiComponent.setType(UIComponentsConstants.REDSHIFT.value());
		uiComponent.setCategory(UIComponentsConstants.OUTPUT_CATEGORY.value());
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(redshift.getId());
		uiComponent.setProperties(propertyMap);
	}
	
	/**
	 *  Appends primary keys using a comma
	 * @param newTable
	 * @return
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
	 * @return
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
		LOGGER.debug("Generating UI-Schema data for OutPut-Redshift-Component - {}", componentName);
		Schema schema = null;
		List<GridRow> gridRow = new ArrayList<>();
		ConverterUiHelper converterUiHelper = new ConverterUiHelper(uiComponent);
		if (inSocket.getSchema() != null
				&& inSocket.getSchema().getFieldOrRecordOrIncludeExternalSchema().size() != 0) {
			schema = new Schema();
			for (Object record : inSocket.getSchema().getFieldOrRecordOrIncludeExternalSchema()) {
				if ((TypeExternalSchema.class).isAssignableFrom(record.getClass())) {
					schema.setIsExternal(true);
					if (((TypeExternalSchema) record).getUri() != null)
						schema.setExternalSchemaPath(((TypeExternalSchema) record).getUri());
					gridRow.addAll(converterUiHelper.loadSchemaFromExternalFile(schema.getExternalSchemaPath(),
							Constants.GENERIC_GRID_ROW));
					schema.setGridRow(gridRow);
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
		LOGGER.debug("Fetching runtime properties for -", componentName);
		TreeMap<String, String> runtimeMap = null;
		TypeProperties typeProperties = ((Redshift) typeBaseComponent).getRuntimeProperties();
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