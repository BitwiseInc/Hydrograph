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

 
package hydrograph.ui.engine.ui.converter;

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullComponent;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullOutSocket;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;

/**
 * The class StraightfullUiConverter
 * 
 * @author Bitwise
 * 
 */

public abstract class StraightpullUiConverter extends UiConverter {
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(StraightpullUiConverter.class);

	/**
	 * Generate common properties of straight-pull component.
	 * 
	 * @param
	 * 
	 * @return
	 */
	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching common properties for -{}", componentName);
		getInPort((TypeStraightPullComponent) typeBaseComponent);
		getOutPort((TypeStraightPullComponent) typeBaseComponent);
		uiComponent.setCategory(UIComponentsConstants.STRAIGHTPULL_CATEGORY.value());
		propertyMap.put(PropertyNameConstants.RUNTIME_PROPERTIES.value(), getRuntimeProperties());
	}

	/**
	 * Create input ports for straight-pull component.
	 * 
	 * @param TypeStraightPullComponent
	 *            the straightPullComponent
	 * @return
	 */
	protected void getInPort(TypeStraightPullComponent straightPullComponent) {
		LOGGER.debug("Generating InPut Ports for -{}", componentName);
		if (straightPullComponent.getInSocket() != null) {
			for (TypeBaseInSocket inSocket : straightPullComponent.getInSocket()) {
				uiComponent.engageInputPort(inSocket.getId());
				currentRepository.getComponentLinkList().add(
						new LinkingData(inSocket.getFromComponentId(), straightPullComponent.getId(),
								inSocket.getFromSocketId(), inSocket.getId()));
				}
		}
	}

	/**
	 * Create output ports for straight-pull component.
	 * 
	 * @param TypeStraightPullComponent
	 *            the straightPullComponent
	 * @return
	 */
	protected void getOutPort(TypeStraightPullComponent straightPullComponent) {
		LOGGER.debug("Generating OutPut Ports for -{}", componentName);
		if (straightPullComponent.getOutSocket() != null) {
			for (TypeStraightPullOutSocket outSocket : straightPullComponent.getOutSocket()) {
					uiComponent.engageOutputPort(outSocket.getId());
			}

		}
	}

	/**
	 * Generate runtime properties for straight-pull component.
	 * 
	 * @return Map<String,String>
	 */
	@Override
	protected Map<String, String> getRuntimeProperties() {
		LOGGER.debug("Generating Runtime Properties for -{}", componentName);
		TreeMap<String, String> runtimeMap = null;
		TypeProperties typeProperties = ((TypeStraightPullComponent) typeBaseComponent).getRuntimeProperties();
		if (typeProperties != null) {
			runtimeMap = new TreeMap<>();
			for (Property runtimeProperty : typeProperties.getProperty()) {
				runtimeMap.put(runtimeProperty.getName(), runtimeProperty.getValue());
			}
		}
		return runtimeMap;
	}
}
