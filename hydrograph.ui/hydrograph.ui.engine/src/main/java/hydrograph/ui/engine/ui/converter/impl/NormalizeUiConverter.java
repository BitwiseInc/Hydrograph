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
import hydrograph.ui.engine.ui.converter.TransformUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.LinkedHashMap;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.Normalize;

	/**
	 * The class NormalizeUiConverter
	 * 
	 * @author Solomon Shockley
	 * 
	 */

	public class NormalizeUiConverter extends TransformUiConverter {

		private Normalize normalize;
		
		private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(NormalizeUiConverter.class);

		public NormalizeUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
			this.container = container;
			this.typeBaseComponent = typeBaseComponent;
			this.uiComponent = new hydrograph.ui.graph.model.components.Normalize();
			this.propertyMap = new LinkedHashMap<>();
		}

		/* 
		 * 
		 */
		@Override
		public void prepareUIXML() {

			super.prepareUIXML();
			LOGGER.debug("Fetching Normalize-Properties for -{}", componentName);
			normalize = (Normalize) typeBaseComponent;

			propertyMap.put(Constants.PARAM_OPERATION, createTransformPropertyGrid());

			container.getComponentNextNameSuffixes().put(name_suffix, 0);
			container.getComponentNames().add(componentName);
			uiComponent.setProperties(propertyMap);
			uiComponent.setType(UIComponentsConstants.NORMALIZE.value());
			
			
		}


	}
