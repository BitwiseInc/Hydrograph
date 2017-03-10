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
import hydrograph.ui.engine.ui.converter.TransformUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.cumulate.TypePrimaryKeyFields;
import hydrograph.engine.jaxb.cumulate.TypeSecondaryKeyFields;
import hydrograph.engine.jaxb.cumulate.TypeSecondayKeyFieldsAttributes;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeFieldName;
import hydrograph.engine.jaxb.operationstypes.Cumulate;

/**
 * The class CumulateUiConverter
 * 
 * @author Paul Pham
 * 
 */

public class CumulateUiConverter extends TransformUiConverter {

	private Cumulate cumulate;
	
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(CumulateUiConverter.class);

	public CumulateUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new hydrograph.ui.graph.model.components.Cumulate();
		this.propertyMap = new LinkedHashMap<>();
	}

	/* 
	 * 
	 */
	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching Cumulate-Properties for -{}", componentName);
		cumulate = (Cumulate) typeBaseComponent;

		
		propertyMap.put(Constants.PROPERTY_COLUMN_NAME, getPrimaryKeys());
		propertyMap.put(Constants.PROPERTY_SECONDARY_COLUMN_KEYS, getSecondaryKeys());
		propertyMap.put(Constants.PARAM_OPERATION, createTransformPropertyGrid());

		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(componentName);
		uiComponent.setProperties(propertyMap);
		uiComponent.setType(UIComponentsConstants.CUMULATE.value());
		
	}

	private Map<String, String> getSecondaryKeys() {
		LOGGER.debug("Fetching Cumulate-Secondary-Key-Properties for -{}", componentName);
		Map<String, String> secondaryKeyMap = null;
		cumulate = (Cumulate) typeBaseComponent;
		TypeSecondaryKeyFields typeSecondaryKeyFields = cumulate.getSecondaryKeys();

		if (typeSecondaryKeyFields != null) {
			secondaryKeyMap = new LinkedHashMap<String, String>();
			for (TypeSecondayKeyFieldsAttributes secondayKeyFieldsAttributes : typeSecondaryKeyFields.getField()) {
				secondaryKeyMap.put(secondayKeyFieldsAttributes.getName(), secondayKeyFieldsAttributes.getOrder()
						.value());

			}
		}

		return secondaryKeyMap;
	}

	private List<String> getPrimaryKeys() {
		LOGGER.debug("Fetching Cumulate-Primary-Key-Properties for -{}", componentName);
		List<String> primaryKeySet = null;
		cumulate = (Cumulate) typeBaseComponent;
		TypePrimaryKeyFields typePrimaryKeyFields = cumulate.getPrimaryKeys();
		if (typePrimaryKeyFields != null) {

			primaryKeySet = new ArrayList<String>();
			for (TypeFieldName fieldName : typePrimaryKeyFields.getField()) {
				primaryKeySet.add(fieldName.getName());
			}
		}
		return primaryKeySet;
	}

}