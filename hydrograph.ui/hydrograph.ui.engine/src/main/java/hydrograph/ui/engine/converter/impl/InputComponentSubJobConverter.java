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
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.engine.converter.InputConverter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.inputtypes.SubjobInput;
/**
 * 
 * @author Bitwise
 * SubJob mapping input component converter that use to map main graph. 
 */
public class InputComponentSubJobConverter extends InputConverter {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(InputComponentSubJobConverter.class);

	public InputComponentSubJobConverter(Component component) {
		super(component);
		this.baseComponent = new SubjobInput();
		this.component = component;
		this.properties = component.getProperties();
	}

	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for {}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();		
	}

	@Override
	protected List<TypeInputOutSocket> getInOutSocket() {
		List<TypeInputOutSocket> outSockets = new ArrayList<>();
		int portCounter=0;
		for (Link link : component.getSourceConnections()) {
			TypeInputOutSocket outSocket = new TypeInputOutSocket();
			outSocket.setId(Constants.INPUT_SOCKET_TYPE+portCounter++);
			outSocket.setType(link.getSource().getPort(link.getSourceTerminal()).getPortType());
			outSocket.setSchema(getSchema());
			outSocket.getOtherAttributes();
			outSockets.add(outSocket);
		}
		return outSockets;
	}

	@Override
	protected List<TypeBaseField> getFieldOrRecord(List<GridRow> gridList) {
	return null;
	}


}
