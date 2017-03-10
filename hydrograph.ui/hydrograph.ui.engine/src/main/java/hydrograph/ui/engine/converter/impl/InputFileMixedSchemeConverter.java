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
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.ifmixedscheme.TypeInputMixedOutSocket;
import hydrograph.engine.jaxb.inputtypes.TextFileMixedScheme;
import hydrograph.engine.jaxb.inputtypes.TextFileMixedScheme.Quote;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.InputConverter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class InputFileMixedSchemeConverter.
 * Converter for InputFileMixedScheme component.
 * @author Bitwise
 */
public class InputFileMixedSchemeConverter extends InputConverter {

	private static final Logger logger = LogFactory.INSTANCE
			.getLogger(InputFileMixedSchemeConverter.class);

	public InputFileMixedSchemeConverter(Component component) {
		super(component);
		this.baseComponent = new TextFileMixedScheme();
		this.component = component;
		this.properties = component.getProperties();
	}

	@Override
	public void prepareForXML() {
		logger.debug("prepareForXML - Generating XML Data for " + component);
		super.prepareForXML();
		TextFileMixedScheme textFileMixedScheme = (TextFileMixedScheme) baseComponent;
		TextFileMixedScheme.Path path = new TextFileMixedScheme.Path();
		path.setUri((String) properties.get(PropertyNameConstants.PATH.value()));
		TextFileMixedScheme.Charset charset = new TextFileMixedScheme.Charset();
		charset.setValue(getCharset());

		textFileMixedScheme.setPath(path);
		textFileMixedScheme.setStrict(getBoolean(PropertyNameConstants.STRICT
				.value()));
		textFileMixedScheme.setSafe(getBoolean(PropertyNameConstants.IS_SAFE
				.value()));
		textFileMixedScheme.setCharset(charset);
		textFileMixedScheme.setRuntimeProperties(getRuntimeProperties());	
		if (StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.QUOTE.value()))) {
			Quote quote = new Quote();
			quote.setValue((String) properties.get(PropertyNameConstants.QUOTE.value()));
			textFileMixedScheme.setQuote(quote);
		}	

	}

	@Override
	protected List<TypeInputOutSocket> getInOutSocket() {
		logger.debug("getInOutSocket - Generating TypeInputOutSocket data for "
				+ component);
		List<TypeInputOutSocket> outSockets = new ArrayList<>();
		for (Link link : component.getSourceConnections()) {
			TypeInputMixedOutSocket outSocket = new TypeInputMixedOutSocket();
			outSocket.setId(link.getSourceTerminal());
			outSocket.setType(link.getSource()
					.getPort(link.getSourceTerminal()).getPortType());
			outSocket.setSchema(getSchema());
			outSocket.getOtherAttributes();
			outSockets.add(outSocket);
		}
		return outSockets;
	}

	@Override
	protected List<TypeBaseField> getFieldOrRecord(List<GridRow> gridList) {
		logger.debug("Generating data for {} for property {}", new Object[] {
				properties.get(Constants.PARAM_NAME),
				PropertyNameConstants.SCHEMA.value() });

		List<TypeBaseField> typeBaseFields = new ArrayList<>();

		if (gridList != null && gridList.size() != 0) {
			for (GridRow object : gridList)
				typeBaseFields.add(converterHelper
						.getFileMixedSchemeTargetData((MixedSchemeGridRow) object));
		}
		return typeBaseFields;
	}
}
