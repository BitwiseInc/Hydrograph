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
import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
import hydrograph.engine.jaxb.commontypes.TypeStraightPullComponent;
import hydrograph.engine.jaxb.straightpulltypes.UnionAll;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.LinkingData;
import hydrograph.ui.engine.ui.converter.StraightpullUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.UnionallComponent;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.LinkedHashMap;

import org.slf4j.Logger;

public class UnionAllUiConverter extends StraightpullUiConverter {

	private UnionAll unionAll;
	
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(UnionAllUiConverter.class);

	public UnionAllUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new UnionallComponent();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching Union-All-Properties for -{}", componentName);
		unionAll = (UnionAll) typeBaseComponent;
		uiComponent.setCategory(UIComponentsConstants.STRAIGHTPULL_CATEGORY.value());
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(unionAll.getId());
		uiComponent.setProperties(propertyMap);
		uiComponent.setType(UIComponentsConstants.UNION_ALL.value());
		
	}

	protected void getInPort(TypeStraightPullComponent straightPullComponent) {
		LOGGER.debug("Generating Union-All-Input-Port for -{}", componentName);
		String fixedInsocket = "in0";
		if (straightPullComponent.getInSocket() != null) {
			for (TypeBaseInSocket inSocket : straightPullComponent.getInSocket()) {
				uiComponent.engageInputPort(fixedInsocket);
				currentRepository.getComponentLinkList().add(
						new LinkingData(inSocket.getFromComponentId(), straightPullComponent.getId(), inSocket
								.getFromSocketId(), fixedInsocket
						));

			}
		}
	}

}
