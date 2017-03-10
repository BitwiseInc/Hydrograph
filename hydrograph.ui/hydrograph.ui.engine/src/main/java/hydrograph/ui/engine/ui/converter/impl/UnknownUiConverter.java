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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.LinkingData;
import hydrograph.ui.engine.ui.converter.UiConverter;
import hydrograph.ui.engine.ui.repository.InSocketDetail;
import hydrograph.ui.engine.ui.repository.UIComponentRepo;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.UnknownComponent;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.main.Graph;
import com.thoughtworks.xstream.XStream;

public class UnknownUiConverter extends UiConverter {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(UnknownUiConverter.class);

	public UnknownUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new UnknownComponent();
		this.propertyMap = new LinkedHashMap<>();

	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching Straight-Pull-Properties for -{}", componentName);

		LOGGER.info("Converting TYPEBASE TO XML STRING");
		propertyMap.put("xml_properties_content", marshall());
		if (getInPort() && getOutPort()) {
			container.getComponentNextNameSuffixes().put(name_suffix, 0);
			container.getComponentNames().add(componentName);
			uiComponent.setProperties(propertyMap);
			uiComponent.setCategory(Constants.UNKNOWN_COMPONENT_TYPE);
			uiComponent.setType(typeBaseComponent.getClass().getSimpleName());

		}
		propertyMap.put(UIComponentsConstants.VALIDITY_STATUS.value(), "ERROR");

	}

	private boolean getOutPort() {
		LOGGER.debug("Fetching default-component Output port for -{}", componentName);
		uiComponent.engageOutputPort("out0");
		uiComponent.engageOutputPort("unused0");
		return true;

	}

	private boolean getInPort() {
		LOGGER.debug("Generating default-component inputport for -{}", componentName);

		String fixedInsocket = "in0";
		if (currentRepository.getInsocketMap().get(componentName) != null) {
			for (InSocketDetail inSocketDetail : currentRepository.getInsocketMap().get(componentName)) {
					uiComponent.engageInputPort(fixedInsocket);
				currentRepository.getComponentLinkList().add(
						new LinkingData(inSocketDetail.getFromComponentId(), componentName, inSocketDetail
								.getFromSocketId(), fixedInsocket));

			}

		}
		return true;
	}

	@Override
	protected Map<String, String> getRuntimeProperties() {

		return null;
	}

	public String marshall() {
		String properties = null;
		ByteArrayOutputStream out = null;
		Graph graph = new Graph();
		graph.getInputsOrOutputsOrStraightPulls().add(typeBaseComponent);
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(graph.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			out = new ByteArrayOutputStream();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(graph, out);
			properties = out.toString();
		} catch (JAXBException e) {

			LOGGER.error("ERROR OCCURED", e);
		}

		return properties;
	}
}
