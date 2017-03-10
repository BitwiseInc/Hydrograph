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
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.StraightpullUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.CloneComponent;
import hydrograph.ui.graph.model.components.LimitComponent;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.LinkedHashMap;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullComponent;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullOutSocket;
import hydrograph.engine.jaxb.straightpulltypes.Clone;
import hydrograph.engine.jaxb.straightpulltypes.Limit;

/**
 * Converter to convert jaxb limit object into limit component
 */
public class LimitUiConverter extends StraightpullUiConverter {

	private static final String MAX_RECORDS = "maxRecords";

	private Limit limit;
	
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(LimitUiConverter.class);

	public LimitUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new LimitComponent();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching Straight-Pull-Properties for -{}", componentName);
		limit = (Limit) typeBaseComponent;

		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(limit.getId());
		if (limit.getMaxRecords() != null) {
			Object value = getValue(MAX_RECORDS);
			if (value != null) {
				propertyMap.put(Constants.PARAM_COUNT, value);
			} else {
				propertyMap.put(Constants.PARAM_COUNT, String.valueOf(limit.getMaxRecords().getValue()));
			}
		} else
			propertyMap.put(Constants.PARAM_COUNT, "0");
		uiComponent.setProperties(propertyMap);
		uiComponent.setType(UIComponentsConstants.LIMIT.value());
	
		
	}

	protected void getOutPort(TypeStraightPullComponent straightPullComponent) {
		LOGGER.debug("Fetching Straight-Pull Output port for -{}", componentName);
		int portCounter = 0;
		if (straightPullComponent.getOutSocket() != null) {
			for (TypeStraightPullOutSocket outSocket : straightPullComponent.getOutSocket()) {
				uiComponent.engageOutputPort(getOutputSocketType(outSocket) + portCounter);
			}
		}
	}

}