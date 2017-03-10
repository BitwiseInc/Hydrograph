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
package hydrograph.ui.engine.converter.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeBaseOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeOutSocketAsInSocket;
import hydrograph.engine.jaxb.commontypes.TypeUnknownComponent;
import hydrograph.engine.jaxb.commontypes.TypeUnknownComponent.Properties;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.converter.Converter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

public class UnknownConverter extends Converter {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(UnknownConverter.class);
	
	public UnknownConverter(Component component) {
		super(component);
		this.baseComponent = new TypeUnknownComponent();
		this.component = component;
		this.properties = component.getProperties();
	}

	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for :{}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		((TypeUnknownComponent) baseComponent).getInSocket().addAll(getInSocket());
		((TypeUnknownComponent) baseComponent).getOutSocket().addAll(getOutSocket());
		TypeUnknownComponent unknownComponent=(TypeUnknownComponent)baseComponent;
		Properties unknownComponentProperties=new Properties();
		unknownComponentProperties.setValue((String)properties.get("xml_properties_content"));
		unknownComponent.setProperties(unknownComponentProperties);
	}

	protected List<TypeBaseOutSocket> getOutSocket() {
		logger.debug("getOutSocket - Generating TypeStraightPullOutSocket data for :{}",
				properties.get(Constants.PARAM_NAME));
		List<TypeBaseOutSocket> outSockectList = new ArrayList<TypeBaseOutSocket>();
		for (Link link : component.getSourceConnections()) {
			TypeBaseOutSocket outSocket = new TypeBaseOutSocket();
			TypeOutSocketAsInSocket outSocketAsInsocket = new TypeOutSocketAsInSocket();
			outSocketAsInsocket.setInSocketId(Constants.FIXED_INSOCKET_ID);
			outSocketAsInsocket.getOtherAttributes();
			outSocket.setId(link.getSource().getPort(link.getSourceTerminal()).getPortType() + link.getLinkNumber());
			outSocket.setType(link.getSource().getPort(link.getSourceTerminal()).getPortType());
			outSocket.getOtherAttributes();
			outSockectList.add(outSocket);
		}

		return outSockectList;
	}

	public List<TypeBaseInSocket> getInSocket() {
		logger.debug("Generating TypeBaseInSocket data for :{}", component.getProperties().get(Constants.PARAM_NAME));
		List<TypeBaseInSocket> inSocketsList = new ArrayList<>();
		for (Link link : component.getTargetConnections()) {
			TypeBaseInSocket inSocket = new TypeBaseInSocket();
			inSocket.setFromComponentId(link.getSource().getComponentId());
			inSocket.setFromSocketId(converterHelper.getFromSocketId(link));
			inSocket.setFromSocketType(link.getSource().getPorts().get(link.getSourceTerminal()).getPortType());
			inSocket.setId(link.getTargetTerminal());
			inSocket.setType(link.getTarget().getPort(link.getTargetTerminal()).getPortType());
			inSocket.getOtherAttributes();
			inSocketsList.add(inSocket);
		}
		return inSocketsList;
	}

}
