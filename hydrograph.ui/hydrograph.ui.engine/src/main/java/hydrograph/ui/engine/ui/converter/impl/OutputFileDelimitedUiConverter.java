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

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.outputtypes.TextFileDelimited;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.OutputUiConverter;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.OFDelimited;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

/**
 * The Class OutputFileDelimitedUiConverter.
 * UiConverter for OutputFileDelimited component.
 * @author Bitwise
 */

public class OutputFileDelimitedUiConverter extends OutputUiConverter {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(OutputFileDelimitedUiConverter.class);

	public OutputFileDelimitedUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new OFDelimited();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching OutPut-Delimited-Component for {}", componentName);
		TextFileDelimited fileDelimited = (TextFileDelimited) typeBaseComponent;
		propertyMap.put(PropertyNameConstants.HAS_HEADER.value(),
				convertBooleanValue(fileDelimited.getHasHeader(), PropertyNameConstants.HAS_HEADER.value()));
		if (fileDelimited.getPath() != null){
			propertyMap.put(PropertyNameConstants.PATH.value(), fileDelimited.getPath().getUri());
		}
		propertyMap.put(PropertyNameConstants.STRICT.value(),
				convertBooleanValue(fileDelimited.getStrict(), PropertyNameConstants.STRICT.value()));
		propertyMap.put(PropertyNameConstants.IS_SAFE.value(),
				convertBooleanValue(fileDelimited.getSafe(), PropertyNameConstants.IS_SAFE.value()));
		propertyMap.put(PropertyNameConstants.CHAR_SET.value(), getCharSet());
		
		if (StringUtils.isNotEmpty(getValue(PropertyNameConstants.DELIMITER.value()))){
			propertyMap.put(PropertyNameConstants.DELIMITER.value(), getValue(PropertyNameConstants.DELIMITER.value()));
		}
		else if (fileDelimited.getDelimiter() != null && StringUtils.isNotEmpty(fileDelimited.getDelimiter().getValue())){
				propertyMap.put(PropertyNameConstants.DELIMITER.value(), fileDelimited.getDelimiter().getValue());
		}
		
		if(fileDelimited.getQuote()!=null){
			 propertyMap.put(PropertyNameConstants.QUOTE.value(), getParameterValue(PropertyNameConstants.QUOTE.value(),fileDelimited.getQuote().getValue()));
		}
		propertyMap.put(PropertyNameConstants.OVER_WRITE.value(),
				convertToTrueFalseValue(fileDelimited.getOverWrite(), PropertyNameConstants.OVER_WRITE.value()));
		
		uiComponent.setType(UIComponentsConstants.FILE_DELIMITED.value());
		uiComponent.setCategory(UIComponentsConstants.OUTPUT_CATEGORY.value());
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(fileDelimited.getId());
		uiComponent.setProperties(propertyMap);
		
	}

	private Object getCharSet() {
		TextFileDelimited fileDelimited = (TextFileDelimited) typeBaseComponent;
		Object value = null;
		if (fileDelimited.getCharset() != null) {
			value = fileDelimited.getCharset().getValue();
			if (value != null) {
				return fileDelimited.getCharset().getValue().value();
			} else {
				value = getValue(PropertyNameConstants.CHAR_SET.value());
			}
		}
		return value;

	}

	@Override
	protected Object getSchema(TypeOutputInSocket inSocket) {
		LOGGER.debug("Generating UI-Schema data for OutPut-Delimited-Component - {}", componentName);
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
					gridRow.addAll(converterUiHelper.loadSchemaFromExternalFile(schema.getExternalSchemaPath(), Constants.GENERIC_GRID_ROW));
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
		TypeProperties typeProperties = ((TextFileDelimited) typeBaseComponent).getRuntimeProperties();
		if (typeProperties != null) {
			runtimeMap = new TreeMap<>();
			for (Property runtimeProperty : typeProperties.getProperty()) {
				runtimeMap.put(runtimeProperty.getName(), runtimeProperty.getValue());
			}
		}
		return runtimeMap;
	}

}
