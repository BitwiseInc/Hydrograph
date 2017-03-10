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

import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeOutSocketAsInSocket;
import hydrograph.engine.jaxb.commontypes.TypeTransformOperation;
import hydrograph.engine.jaxb.operationstypes.Subjob;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.PathUtility;
import hydrograph.ui.engine.converter.SubjobConverter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

/**
 * Operation type sub graph converter,having both in and out sockets.
 *
 * @author Bitwise
 */
public class OperationSubJobConverter extends SubjobConverter {

	/** The Constant logger. */
	private static final Logger logger = LogFactory.INSTANCE.getLogger(OperationSubJobConverter.class);

	/**
	 * Instantiates a new operation sub job converter.
	 *
	 * @param component the component
	 */
	public OperationSubJobConverter(Component component) {
		super(component);
		this.baseComponent = new Subjob();
		this.component = component;
		this.properties = component.getProperties();
	}

	/**
	 * 
	 * Generating XML for Component.
	 */
	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for {}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		Subjob subjob = (Subjob) baseComponent;
		if (properties.get(Constants.JOB_PATH) != null) {
			Subjob.Path path = new Subjob.Path();
			String subJobFile=((String)properties.get(Constants.JOB_PATH)).replace(Constants.JOB_EXTENSION, Constants.XML_EXTENSION);
			if(PathUtility.INSTANCE.isAbsolute(subJobFile)){
				path.setUri(subJobFile);
			}
			else{
				path.setUri("../"+subJobFile);
			}

			subjob.setPath(path); 
		}
		subjob.setSubjobParameter(getRuntimeProperties());
		 
	}
 
	/**
	 * Adding out socket for operation subgraph component.
	 * @return out socket list
	 */
	@Override
	protected List<TypeOperationsOutSocket> getOutSocket() {
		logger.debug("Generating TypeOperationsOutSocket data for : {}", properties.get(Constants.PARAM_NAME));
		List<TypeOperationsOutSocket> outSocketList = new ArrayList<TypeOperationsOutSocket>();
		if (component.getSourceConnections() != null && !component.getSourceConnections().isEmpty()) {
			for (Link link : component.getSourceConnections()) {
				TypeOperationsOutSocket outSocket = new TypeOperationsOutSocket();
				TypeOutSocketAsInSocket outSocketAsInsocket = new TypeOutSocketAsInSocket();
				outSocket.setId(link.getSourceTerminal());
				outSocketAsInsocket.setInSocketId(link.getTargetTerminal());
				outSocket.setType(link.getSource().getPort(link.getSourceTerminal()).getPortType());
				outSocket.getOtherAttributes();
				outSocketList.add(outSocket);
			}
		}
		return outSocketList;
	}

	@Override
	protected List<TypeTransformOperation> getOperations() {
		return null;
	}

	/**
	 * Adding In socket for operation type subgraph component.
	 * @return in socket list
	 */
	@Override
	public List<TypeBaseInSocket> getInSocket() {
		logger.debug("Generating TypeBaseInSocket data for :{}", component.getProperties().get(Constants.PARAM_NAME));
		List<TypeBaseInSocket> inSocketsList = new ArrayList<>();
		if (component.getTargetConnections() != null || !component.getTargetConnections().isEmpty()) {
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
		} 
		return inSocketsList;
	}	

}
