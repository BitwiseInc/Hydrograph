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

import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.outputtypes.Subjob;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.PathUtility;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.OutputConverter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

/**
 * Output type subjob converter.
 * @author Bitwise
 *
 */
public class OutputSubJobConverter extends OutputConverter {

	/** The Constant logger. */
	private static final Logger logger = LogFactory.INSTANCE.getLogger(OutputSubJobConverter.class);

	/**
	 * Instantiates a new output sub job converter.
	 *
	 * @param component the component
	 */
	public OutputSubJobConverter(Component component) {
		super(component);
		this.baseComponent = new Subjob();
		this.component = component;
		this.properties = component.getProperties();
	}

	/**
	 * Generating XML for component
	 */
	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for {}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		Subjob subjob = (Subjob) baseComponent;
		if(properties.get(Constants.JOB_PATH)!=null){
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
	 * Update OutputInSocket for component.
	 * return outputInSocket
	 */
	@Override
	protected List<TypeOutputInSocket> getOutInSocket() {
		logger.debug("getInOutSocket - Generating TypeOutputInSocket data");
		List<TypeOutputInSocket> outputinSockets = new ArrayList<>();
		for (Link link : component.getTargetConnections()) {
			TypeOutputInSocket outInSocket = new TypeOutputInSocket();
			outInSocket.setId(link.getTarget().getPort(link.getTargetTerminal()).getTerminal());
			outInSocket.setFromSocketId(converterHelper.getFromSocketId(link));
			outInSocket.setType(link.getTarget().getPort(link.getTargetTerminal()).getPortType());
			outInSocket.setSchema(getSchema());
			outInSocket.getOtherAttributes();
			outInSocket.setFromComponentId(link.getSource().getComponentId());
			outputinSockets.add(outInSocket);
		}
		return outputinSockets;

	}

	/**
	 * Generate Base Field 
	 */
	@Override
	protected List<TypeBaseField> getFieldOrRecord(List<GridRow> gridRowList) {
		logger.debug("Generating data for {} for property {}", new Object[] { properties.get(Constants.PARAM_NAME),
				PropertyNameConstants.SCHEMA.value() });
		List<TypeBaseField> typeBaseFields = new ArrayList<>();
		if (gridRowList != null && gridRowList.size() != 0) {
			for (GridRow object : gridRowList)
				typeBaseFields.add(converterHelper.getFixedWidthTargetData((FixedWidthGridRow) object));
		}
		return typeBaseFields;
	}
	

}
