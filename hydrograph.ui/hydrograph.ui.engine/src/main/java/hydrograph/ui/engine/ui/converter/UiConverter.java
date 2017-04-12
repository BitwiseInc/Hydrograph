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

	package hydrograph.ui.engine.ui.converter;

	import hydrograph.engine.jaxb.commontypes.BooleanValueType;
	import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
	import hydrograph.engine.jaxb.commontypes.TypeBaseInSocket;
	import hydrograph.engine.jaxb.commontypes.TypeBaseOutSocket;
	import hydrograph.ui.common.util.Constants;
	import hydrograph.ui.engine.ui.repository.ParameterData;
	import hydrograph.ui.engine.ui.repository.UIComponentRepo;
	import hydrograph.ui.graph.model.Component;
	import hydrograph.ui.graph.model.Container;
	import hydrograph.ui.logging.factory.LogFactory;

	import java.io.File;
	import java.util.LinkedHashMap;
	import java.util.List;
	import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
	import org.slf4j.Logger;

	/**
	 * The class UiConverter
	 * 
	 * @author Bitwise
	 * 
	 */

	public abstract class UiConverter {

		private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(UiConverter.class);
		protected Container container;
		protected Component uiComponent = null;
		protected TypeBaseComponent typeBaseComponent = null;
		protected LinkedHashMap<String, Object> propertyMap = null;
		protected static final String BATCH = "batch";
		protected static final String NAME = "name";
		protected String componentName;
		protected String name_suffix;
		protected UIComponentRepo currentRepository;
		protected File sourceXmlPath;
		protected IFile parameterFile;
		protected String componentId;

	
		/**
		 * Generate basic properties that are common in all components.
		 */
		public void prepareUIXML() {
			componentId = StringUtils.trim(typeBaseComponent.getId());
			componentName=StringUtils.trim(typeBaseComponent.getName());
			if(StringUtils.isBlank(componentName)){
				componentName=componentId;
			}
			name_suffix = uiComponent.getComponentName() + "_";
			LOGGER.debug("Preparing basic properties for component Name:{} and Id{}", componentName,componentId);
			propertyMap.put(NAME, componentName);
			propertyMap.put(BATCH, typeBaseComponent.getBatch().toString());
			uiComponent.setComponentLabel(componentName);
			uiComponent.setParent(container);
			uiComponent.setComponentId(componentId);
			currentRepository.getComponentUiFactory().put(componentId, uiComponent);
		}

		/**
		 * @return Component's object
		 */
		public Component getComponent() {
			return uiComponent;
		}

		/**
		 * @param value
		 *            , BooleanValueType value.
		 * 
		 * @param propertyName
		 *            , component property-name having BooleanValueType value.
		 * 
		 * @return String, string value of BooleanValueType value.
		 */
		public String convertBooleanValue(BooleanValueType value, String propertyName) {
			LOGGER.debug("Converting Boolean to String - {}", propertyName);
			Object stringValue = null;
			if (value != null && value.isValue() != null && value.isValue().toString().equalsIgnoreCase("true")) {
				return "True";
			} else {
				stringValue = getValue(propertyName);
				if (stringValue != null) {
					return (String) stringValue;
				}

			}
			return "False";
		}

		/**
		 * Replace the default values by parameters that were generated while parsing the engine xml.
		 * 
		 * @param propertyName
		 * @return String
		 */
		protected String getValue(String propertyName) {
			LOGGER.debug("Getting Parameter for - {}", propertyName);
			List<ParameterData> parameterList = currentRepository.getParammeterFactory().get(componentId);
			if (parameterList != null) {
				for (ParameterData param : parameterList) {
					if(StringUtils.equalsIgnoreCase(param.getPropertyName(), propertyName)){
						return param.getParameterName();
					}
				}
			}

			return null;
		}

		/**
		 * Returns parameter name of property tag
		 * 
		 * @param propertyNameAttribute
		 * @return
		 */
		protected String getValueForPropertyTag(String propertyNameAttribute) {
			LOGGER.debug("Getting Parameter for - {}", propertyNameAttribute);
			List<ParameterData> parameterList = currentRepository.getParammeterFactory().get(componentName);
			try {
				if (parameterList != null) {
					for (ParameterData param : parameterList) {
						String[] arr = StringUtils.split(param.getParameterName(), "=");
						if (StringUtils.equals(propertyNameAttribute, arr[0])) {
							return arr[1];
						}
					}
				}
			} catch (Exception e) {
				LOGGER.warn("Unable to generate ui property for property tag :" + propertyNameAttribute);
			}
			return "";
		}
			
		/**
		 * Returns parameter name of property.
		 * 
		 * @param propertyName
		 * @parm currentValue
		 * @return String
		 */
		protected String getParameterValue(String propertyName,Object currentValue) {
			
			if(currentValue ==null){
				return getValue(propertyName);
			}
			return currentValue.toString();
		}
		
		/**
		 * Generate runtime properties for component.
		 * 
		 * @return Map<String,String>
		 */
		protected abstract Map<String, String> getRuntimeProperties();

		protected String getInputSocketType(TypeBaseInSocket inSocket) {
			if (inSocket.getType() != null)
				return inSocket.getType();

			return Constants.INPUT_SOCKET_TYPE;
		}

		protected String getOutputSocketType(TypeBaseOutSocket outSocket) {
			if (outSocket.getType() != null)
				return outSocket.getType();

			return Constants.OUTPUT_SOCKET_TYPE;
		}

		public void setCurrentRepository(UIComponentRepo componentRepo) {
			this.currentRepository=componentRepo;
		}

		public void setSourceXMLPath(File sourceXmlPath) {
			this.sourceXmlPath=sourceXmlPath;
		}

		public void setParameterFile(IFile parameterFile) {
			this.parameterFile=parameterFile;
		}
	}