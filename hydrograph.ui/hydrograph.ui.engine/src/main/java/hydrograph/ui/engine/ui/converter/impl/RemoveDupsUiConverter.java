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

import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.StraightpullUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.RemovedupsComponent;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeFieldName;
import hydrograph.engine.jaxb.removedups.TypePrimaryKeyFields;
import hydrograph.engine.jaxb.removedups.TypeSecondaryKeyFields;
import hydrograph.engine.jaxb.removedups.TypeSecondayKeyFieldsAttributes;
import hydrograph.engine.jaxb.straightpulltypes.RemoveDups;

public class RemoveDupsUiConverter extends StraightpullUiConverter {
	private RemoveDups removeDups;
	
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(RemoveDupsUiConverter.class);

	public RemoveDupsUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new RemovedupsComponent();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching RemoveDups-Properties for -{}", componentName);
		removeDups = (RemoveDups) typeBaseComponent;

		propertyMap.put(PropertyNameConstants.RETENTION_LOGIC_KEEP.value(), removeDups.getKeep().getValue().value());
		propertyMap.put(PropertyNameConstants.DEDUP_FILEDS.value(), getPrimaryKeys());
		propertyMap.put(PropertyNameConstants.SECONDARY_COLUMN_KEYS.value(), getSecondaryKeys());

		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(componentName);
		uiComponent.setProperties(propertyMap);
		uiComponent.setType(UIComponentsConstants.REMOVE_DUPS.value());
		
	}

	private Map<String, String> getSecondaryKeys() {
		LOGGER.debug("Fetching RemoveDups-Secondary-Keys-Properties for -{}", componentName);
		Map<String, String> secondaryKeyMap = null;
		removeDups = (RemoveDups) typeBaseComponent;
		TypeSecondaryKeyFields typeSecondaryKeyFields = removeDups.getSecondaryKeys();

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
		LOGGER.debug("Fetching RemoveDups-Primary-Keys-Properties for -{}", componentName);
		List<String> primaryKeySet = null;
		removeDups = (RemoveDups) typeBaseComponent;
		TypePrimaryKeyFields typePrimaryKeyFields = removeDups.getPrimaryKeys();
		if (typePrimaryKeyFields != null) {

			primaryKeySet = new ArrayList<String>();
			for (TypeFieldName fieldName : typePrimaryKeyFields.getField()) {
				primaryKeySet.add(fieldName.getName());
			}
		}
		return primaryKeySet;
	}

}
