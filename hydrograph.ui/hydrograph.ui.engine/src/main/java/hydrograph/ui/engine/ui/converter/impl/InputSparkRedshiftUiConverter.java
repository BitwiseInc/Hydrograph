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
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.inputtypes.Sparkredshift;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.DatabaseSelectionConfig;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.InputUiConverter;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.ISRedshift;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * Converter to convert jaxb RedShift object into input RedShift component
 * 
 * @author Bitwise
 *
 */
public class InputSparkRedshiftUiConverter extends InputUiConverter {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(InputSparkRedshiftUiConverter.class);

	public InputSparkRedshiftUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new ISRedshift();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching Input-Redshift-Properties for {}", componentName);
		Sparkredshift redshift = (Sparkredshift) typeBaseComponent;
		DatabaseSelectionConfig databaseSelectionConfig = new DatabaseSelectionConfig();

		setValueInPropertyMap(PropertyNameConstants.DATABASE_NAME.value(),
				redshift.getDatabaseName() == null ? "" : redshift.getDatabaseName().getValue());

		setValueInPropertyMap(PropertyNameConstants.HOST_NAME.value(),
				redshift.getHostName() == null ? "" : redshift.getHostName().getValue());

		setValueInPropertyMap(PropertyNameConstants.TEMPORARY_DIRECTORY_NAME.value(),
				redshift.getTemps3Dir() == null ? "" : redshift.getTemps3Dir().getValue());

		try {
			BigInteger bigInteger = redshift.getPort().getValue();
			setValueInPropertyMap(PropertyNameConstants.PORT_NO.value(), redshift.getPort() == null ? "" : bigInteger);
		} catch (Exception e) {
			LOGGER.error("Exception" + e);
		}

		setValueInPropertyMap(PropertyNameConstants.USER_NAME.value(),
				redshift.getUserName() == null ? "" : redshift.getUserName().getValue());

		setValueInPropertyMap(PropertyNameConstants.PASSWORD.value(),
				redshift.getPassword() == null ? "" : redshift.getPassword().getValue());

		if (redshift.getTableName() != null && StringUtils.isNotBlank(redshift.getTableName().getValue())) {
			databaseSelectionConfig.setTableName(redshift.getTableName().getValue());
			databaseSelectionConfig.setTableNameSelection(true);
		} else {
			if (redshift.getTableName() != null) {
				setValueInPropertyMap(PropertyNameConstants.TABLE_NAME.value(), redshift.getTableName().getValue());
				databaseSelectionConfig.setTableName(getParameterValue(PropertyNameConstants.TABLE_NAME.value(), null));
				databaseSelectionConfig.setTableNameSelection(true);
			}
		}

		if (redshift.getSelectQuery() != null && StringUtils.isNotBlank(redshift.getSelectQuery().getValue())) {
			databaseSelectionConfig.setSqlQuery(redshift.getSelectQuery().getValue());
			databaseSelectionConfig.setTableNameSelection(false);
		} else {
			if (redshift.getSelectQuery() != null) {
				setValueInPropertyMap(PropertyNameConstants.SELECT_QUERY.value(), redshift.getSelectQuery().getValue());
				databaseSelectionConfig
						.setSqlQuery(getParameterValue(PropertyNameConstants.SELECT_QUERY.value(), null));
				databaseSelectionConfig.setTableNameSelection(false);
			}
		}

		propertyMap.put(PropertyNameConstants.SELECT_OPTION.value(), databaseSelectionConfig);
		uiComponent.setType(UIComponentsConstants.REDSHIFT.value());
		uiComponent.setCategory(UIComponentsConstants.INPUT_CATEGORY.value());
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
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
					gridRowList.addAll(converterUiHelper.loadSchemaFromExternalFile(schema.getExternalSchemaPath(),
							Constants.FIXEDWIDTH_GRID_ROW));
					schema.setGridRow(gridRowList);
				} else {
					gridRowList.add(converterUiHelper.getSchema(record));
					schema.setGridRow(gridRowList);
					schema.setIsExternal(false);
				}
				saveComponentOutputSchema(outSocket.getId(), gridRowList);
			}
		}
		return schema;
	}

	@Override
	protected Map<String, String> getRuntimeProperties() {
		LOGGER.debug("Generating Runtime Properties for -{}", componentName);
		TreeMap<String, String> runtimeMap = null;
		TypeProperties typeProperties = ((Sparkredshift) typeBaseComponent).getRuntimeProperties();
		if (typeProperties != null) {
			runtimeMap = new TreeMap<>();
			for (Property runtimeProperty : typeProperties.getProperty()) {
				runtimeMap.put(runtimeProperty.getName(), runtimeProperty.getValue());
			}
		}
		return runtimeMap;
	}

	private void setValueInPropertyMap(String propertyName, Object value) {
		propertyMap.put(propertyName, getParameterValue(propertyName, value));
	}
}
