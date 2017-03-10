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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.InputUiConverter;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.IFileMixedScheme;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.inputtypes.TextFileMixedScheme;

/**
 * The Class InputMixedSchemeUiConverter.
 * UiConverter for InputMixedScheme component.
 * @author Bitwise
 */

public class InputMixedSchemeUiConverter extends InputUiConverter {
	
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(InputMixedSchemeUiConverter.class);

	public InputMixedSchemeUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new IFileMixedScheme();
		this.propertyMap = new LinkedHashMap<>();
	}
	
	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching Input-File-Mixed-Schema-Properties for {}", componentName);
		TextFileMixedScheme fileMixedSchema = (TextFileMixedScheme) typeBaseComponent;
		if (fileMixedSchema.getPath() != null)
			propertyMap.put(PropertyNameConstants.PATH.value(), fileMixedSchema.getPath().getUri());
		propertyMap.put(PropertyNameConstants.CHAR_SET.value(), getCharSet());
		propertyMap.put(PropertyNameConstants.STRICT.value(),
				convertBooleanValue(fileMixedSchema.getStrict(), PropertyNameConstants.STRICT.value()));
		propertyMap.put(PropertyNameConstants.IS_SAFE.value(),
				convertBooleanValue(fileMixedSchema.getSafe(), PropertyNameConstants.IS_SAFE.value()));

		if(fileMixedSchema.getQuote()!=null){
			 propertyMap.put(PropertyNameConstants.QUOTE.value(), getParameterValue(PropertyNameConstants.QUOTE.value(),fileMixedSchema.getQuote().getValue()));
		}
		uiComponent.setType(UIComponentsConstants.FILE_MIXEDSCHEMA.value());
		uiComponent.setCategory(UIComponentsConstants.INPUT_CATEGORY.value());
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(fileMixedSchema.getId());
		uiComponent.setProperties(propertyMap);
	}
	
	private Object getCharSet() {
		TextFileMixedScheme fileMixedSchema = (TextFileMixedScheme) typeBaseComponent;
		Object value = null;
		if (fileMixedSchema.getCharset() != null) {
			value = fileMixedSchema.getCharset().getValue();
			if (value != null) {
				return fileMixedSchema.getCharset().getValue().value();
			} else {
				value = getValue(PropertyNameConstants.CHAR_SET.value());
			}
		}
		return value;
	}	
	
	@Override
	protected Object getSchema(TypeInputOutSocket outSocket) {
		LOGGER.debug("Generating UI-Schema data for {}", componentName);
		Schema schema =null;
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
					gridRowList.addAll(converterUiHelper.loadSchemaFromExternalFile(schema.getExternalSchemaPath(), Constants.MIXEDSCHEMA_GRID_ROW));
					schema.setGridRow(gridRowList);
				} else {
					gridRowList.add(converterUiHelper.getMixedScheme(record));
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
		TypeProperties typeProperties = ((TextFileMixedScheme) typeBaseComponent).getRuntimeProperties();
		if (typeProperties != null) {
			runtimeMap = new TreeMap<>();
			for (Property runtimeProperty : typeProperties.getProperty()) {
				runtimeMap.put(runtimeProperty.getName(), runtimeProperty.getValue());
			}
		}
		return runtimeMap;
	}

}
