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

import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.StraightpullUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.CloneComponent;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.LinkedHashMap;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullComponent;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullOutSocket;
import hydrograph.engine.jaxb.straightpulltypes.Clone;

/**
 * The class CloneUiConverter
 * 
 * @author Bitwise
 * 
 */
public class CloneUiConverter extends StraightpullUiConverter {

	private Clone clone;
	
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(CloneUiConverter.class);

	public CloneUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new CloneComponent();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching Straight-Pull-Properties for -{}", componentName);
		clone = (Clone) typeBaseComponent;

		

		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(clone.getId());
		uiComponent.setProperties(propertyMap);
		uiComponent.setType(UIComponentsConstants.CLONE.value());
	
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
