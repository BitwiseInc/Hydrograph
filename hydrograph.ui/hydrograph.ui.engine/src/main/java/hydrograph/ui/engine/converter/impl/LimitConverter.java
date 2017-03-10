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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.converter.StraightPullConverter;
import hydrograph.ui.engine.helper.ConverterHelper;
import hydrograph.ui.engine.xpath.ComponentXpath;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
import hydrograph.ui.engine.xpath.ComponentsAttributeAndValue;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeOutSocketAsInSocket;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullOutSocket;
import hydrograph.engine.jaxb.straightpulltypes.Limit;

/**
 * Converter to convert Limit component into engine specific limit object
 * 
 * @author BITWISE
 */
public class LimitConverter extends StraightPullConverter {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(LimitConverter.class);
	
	public LimitConverter(Component component) {
		super(component);
		this.baseComponent = new Limit();
		this.component = component;
		this.properties = component.getProperties();
	}

	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for :{}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		Limit limit = (Limit) baseComponent;
		String count =(String)properties.get(Constants.PARAM_COUNT);
		
		if (StringUtils.isNotBlank(count)){
			Limit.MaxRecords value = new Limit.MaxRecords();
			limit.setMaxRecords(value);
			try{
				Long longCount = Long.parseLong(count);
				value.setValue(StringUtils.isBlank(count) ? null : longCount);
			}
			catch(NumberFormatException exception){
				ComponentXpath.INSTANCE.getXpathMap().put(
						(ComponentXpathConstants.COMPONENT_XPATH_COUNT.value().replace(ID, componentName)),
						new ComponentsAttributeAndValue(null, count));
			}
		}		
	}

	@Override
	protected List<TypeStraightPullOutSocket> getOutSocket() {
		logger.debug("getOutSocket - Generating TypeStraightPullOutSocket data for :{}",
				properties.get(Constants.PARAM_NAME));
		List<TypeStraightPullOutSocket> outSockectList = new ArrayList<TypeStraightPullOutSocket>();

		for (Link link : component.getSourceConnections()) {
			TypeStraightPullOutSocket outSocket = new TypeStraightPullOutSocket();
			TypeOutSocketAsInSocket outSocketAsInsocket = new TypeOutSocketAsInSocket();
			outSocketAsInsocket.setInSocketId(Constants.FIXED_INSOCKET_ID);
			outSocketAsInsocket.getOtherAttributes();
			outSocket.setCopyOfInsocket(outSocketAsInsocket);
			outSocket.setId(link.getSource().getPort(link.getSourceTerminal()).getPortType() + link.getLinkNumber());
			outSocket.setType(link.getSource().getPort(link.getSourceTerminal()).getPortType());
			outSocket.getOtherAttributes();
			outSockectList.add(outSocket);
		}
		return outSockectList;
	}

	@Override
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