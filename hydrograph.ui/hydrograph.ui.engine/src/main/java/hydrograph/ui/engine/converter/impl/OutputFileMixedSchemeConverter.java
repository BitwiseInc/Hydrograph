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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.ofmixedscheme.TypeOutputMixedInSocket;
import hydrograph.engine.jaxb.outputtypes.TextFileMixedScheme;
import hydrograph.engine.jaxb.outputtypes.TextFileMixedScheme.Quote;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.OutputConverter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class OutputFileMixedSchemeConverter.
 * Converter for OutputFileMixedScheme component.
 * @author Bitwise
 */
public class OutputFileMixedSchemeConverter extends OutputConverter {
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(OutputFileMixedSchemeConverter.class);	

	public OutputFileMixedSchemeConverter(Component component) {
		super(component);
		this.component = component;
		this.properties = component.getProperties();
		this.baseComponent = new TextFileMixedScheme();		
	}
	
	@Override
	public void prepareForXML() {
		logger.debug("prepareForXML - Generating XML data for " + component);
		super.prepareForXML();
		TextFileMixedScheme fileMixedScheme = (TextFileMixedScheme) baseComponent;
		TextFileMixedScheme.Path path = new TextFileMixedScheme.Path();
		path.setUri((String) properties.get(PropertyNameConstants.PATH.value()));
		TextFileMixedScheme.Charset charset = new TextFileMixedScheme.Charset();
		charset.setValue(getCharset());
		fileMixedScheme.setSafe(getBoolean(PropertyNameConstants.IS_SAFE.value()));
		fileMixedScheme.setPath(path);
		fileMixedScheme.setStrict(getBoolean(PropertyNameConstants.STRICT.value()));
		fileMixedScheme.setCharset(charset);
		fileMixedScheme.setRuntimeProperties(getRuntimeProperties());
		if (StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.QUOTE.value()))) {
			Quote quote = new Quote();
			quote.setValue((String) properties.get(PropertyNameConstants.QUOTE.value()));
			fileMixedScheme.setQuote(quote);
		}
		fileMixedScheme.setOverWrite(getTrueFalse(PropertyNameConstants.OVER_WRITE.value()));
	}
	

	@Override
	protected List<TypeOutputInSocket> getOutInSocket() {
		logger.debug("getInOutSocket - Generating TypeOutputInSocket data");
		List<TypeOutputInSocket> outputinSockets = new ArrayList<>();
		for (Link link : component.getTargetConnections()) {
			TypeOutputMixedInSocket outInSocket = new TypeOutputMixedInSocket();
			outInSocket.setId(link.getTarget().getPort(link.getTargetTerminal()).getTerminal());
			outInSocket.setFromSocketId(converterHelper.getFromSocketId(link));
			outInSocket.setFromSocketType(link.getSource().getPorts().get(link.getSourceTerminal()).getPortType());
			outInSocket.setType(link.getTarget().getPort(link.getTargetTerminal()).getPortType());
			outInSocket.setSchema(getSchema());
			outInSocket.getOtherAttributes();
			outInSocket.setFromComponentId(link.getSource().getComponentId());
			outputinSockets.add(outInSocket);
		}
		return outputinSockets;
	}

	@Override
	protected List<TypeBaseField>getFieldOrRecord(List<GridRow> gridRowList) {
		logger.debug("Generating data for {} for property {}", new Object[] { properties.get(Constants.PARAM_NAME),
				PropertyNameConstants.SCHEMA.value() });
		List<TypeBaseField> typeBaseFields = new ArrayList<>();
		if (gridRowList != null && gridRowList.size() != 0) {
			for (GridRow object : gridRowList)
				typeBaseFields.add(converterHelper.getFileMixedSchemeTargetData((FixedWidthGridRow) object));
		}
		return typeBaseFields;
	}

}
