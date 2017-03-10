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
import hydrograph.engine.jaxb.commontypes.TypeInputComponent;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.inputtypes.SubjobInput;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.ui.converter.UiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.InputSubjobComponent;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

/**
 * Converter to convert jaxb SubjobInput object into input subjob component
 *
 *@author BITWISE
 */
public class InputComponentSubjobUiConverter extends UiConverter {
	
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(InputComponentSubjobUiConverter.class);
	private SubjobInput subjobInput;
	
	public InputComponentSubjobUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new InputSubjobComponent();
		this.propertyMap = new LinkedHashMap<>();
		subjobInput = (SubjobInput) typeBaseComponent;
	}
	
	@Override
	public void prepareUIXML() {
		logger.debug("Fetching Input-Delimited-Properties for {}", componentName);
		super.prepareUIXML();
		
		componentName=typeBaseComponent.getId();
		getOutPort((TypeInputComponent) typeBaseComponent);
		uiComponent.setType(Constants.INPUT_SOCKET_FOR_SUBJOB);
		uiComponent.setCategory(Constants.SUBJOB_COMPONENT_CATEGORY);
		uiComponent.setComponentLabel(Constants.SUBJOB_INPUT_COMPONENT_NAME);
		uiComponent.setParent(container);
		currentRepository.getComponentUiFactory().put(componentName, uiComponent);
		propertyMap.put(NAME,Constants.SUBJOB_INPUT_COMPONENT_NAME);
		propertyMap.put(Constants.OUTPUT_PORT_COUNT_PROPERTY,subjobInput.getOutSocket().size());
		uiComponent.setProperties(propertyMap);
	}

	
	protected void getOutPort(TypeInputComponent inputComponent) {
		logger.debug("Generating OutPut Ports for -{}", componentName);
		int count=0;
		if (inputComponent.getOutSocket() != null) {
			for (TypeInputOutSocket outSocket : inputComponent.getOutSocket()) {
				uiComponent.engageOutputPort(Constants.OUTPUT_SOCKET_TYPE+count);
				count++;
				}
			uiComponent.completeOutputPortSettings(count);
		}
	}

	@Override
	protected Map<String, String> getRuntimeProperties() {
		return null;
	}

}
