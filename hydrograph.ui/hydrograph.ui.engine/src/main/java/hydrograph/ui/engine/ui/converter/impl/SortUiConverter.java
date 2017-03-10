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
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.StraightpullUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.SortComponent;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.sort.TypePrimaryKeyFields;
import hydrograph.engine.jaxb.sort.TypePrimaryKeyFieldsAttributes;
import hydrograph.engine.jaxb.sort.TypeSecondaryKeyFields;
import hydrograph.engine.jaxb.sort.TypeSecondayKeyFieldsAttributes;
import hydrograph.engine.jaxb.straightpulltypes.Sort;

/**
 * Converter to convert jaxb sort object into sort component
 *
 *@author BITWISE
 */
public class SortUiConverter extends StraightpullUiConverter {
	private Sort sort;
	
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(SortUiConverter.class);

	public SortUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new SortComponent();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching Sort-Properties for -{}", componentName);
		sort = (Sort) typeBaseComponent;

		propertyMap.put(Constants.PARAM_PRIMARY_COLUMN_KEYS, getPrimaryKeys());

		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(componentName);
		uiComponent.setProperties(propertyMap);
		uiComponent.setType(UIComponentsConstants.SORT.value());
		
	}


	private Map<String, String> getPrimaryKeys() {
		LOGGER.debug("Fetching RemoveDups-Primary-Keys-Properties for -{}", componentName);
		Map<String, String> primaryKeyMap = null;
		sort = (Sort) typeBaseComponent;
		TypePrimaryKeyFields typePrimaryKeyFields = sort.getPrimaryKeys();

		if (typePrimaryKeyFields != null) {
			primaryKeyMap = new LinkedHashMap<String, String>();
			for (TypePrimaryKeyFieldsAttributes primaryKeyFieldsAttributes : typePrimaryKeyFields.getField()) {
				primaryKeyMap.put(primaryKeyFieldsAttributes.getName(), primaryKeyFieldsAttributes.getOrder().value());
			}
		}
		return primaryKeyMap;
	}

}