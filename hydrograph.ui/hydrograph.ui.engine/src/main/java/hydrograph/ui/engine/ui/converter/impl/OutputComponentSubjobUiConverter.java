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
import hydrograph.engine.jaxb.commontypes.TypeOutputComponent;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.outputtypes.SubjobOutput;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.ui.converter.LinkingData;
import hydrograph.ui.engine.ui.converter.UiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.OutputSubjobComponent;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

/**
 * Converter to convert jaxb SubjobOutput object into output subjob component
 *
 *@author BITWISE
 */
public class OutputComponentSubjobUiConverter extends UiConverter {

	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(OutputComponentSubjobUiConverter.class);
	private SubjobOutput subjobOutput;
	
	public OutputComponentSubjobUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new OutputSubjobComponent();
		this.propertyMap = new LinkedHashMap<>();
		subjobOutput = (SubjobOutput) typeBaseComponent;
	}
	
	@Override
	public void prepareUIXML() {
		logger.debug("Fetching Input-Delimited-Properties for Name:{} and Id:{}", componentName,componentId);
		super.prepareUIXML();
		getInPort((TypeOutputComponent) typeBaseComponent);
		uiComponent.setType(Constants.OUTPUT_SOCKET_FOR_SUBJOB);
		uiComponent.setCategory(Constants.SUBJOB_COMPONENT_CATEGORY);
		uiComponent.setComponentLabel(Constants.SUBJOB_OUTPUT_COMPONENT_NAME);
		uiComponent.setParent(container);
		propertyMap.put(NAME,Constants.SUBJOB_OUTPUT_COMPONENT_NAME);
		propertyMap.put(Constants.INPUT_PORT_COUNT_PROPERTY,subjobOutput.getInSocket().size());
		uiComponent.setProperties(propertyMap);
	}

	
	private void getInPort(TypeOutputComponent typeBaseComponent) {
		logger.debug("Generating InPut Ports for -{}", componentName);
		int count=0;
		if (typeBaseComponent.getInSocket() != null) {
			for (TypeOutputInSocket inSocket : typeBaseComponent.getInSocket()) {
				uiComponent.engageInputPort(Constants.INPUT_SOCKET_TYPE+count);
				currentRepository.getComponentLinkList().add(
						new LinkingData(inSocket.getFromComponentId(),
								typeBaseComponent.getId(), inSocket.getFromSocketId(), Constants.INPUT_SOCKET_TYPE+count));
				count++;
			}
			uiComponent.completeInputPortSettings(count);
		}
	}
	
	@Override
	protected Map<String, String> getRuntimeProperties() {
		return null;
	}

}
