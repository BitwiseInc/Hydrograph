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

 
package hydrograph.ui.engine.converter;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.BooleanValueType;
import hydrograph.engine.jaxb.commontypes.StandardCharsets;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.exceptions.BatchException;
import hydrograph.ui.engine.exceptions.SchemaException;
import hydrograph.ui.engine.helper.ConverterHelper;
import hydrograph.ui.engine.xpath.ComponentXpath;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
import hydrograph.ui.engine.xpath.ComponentsAttributeAndValue;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * Base class for converter implementation. Consists of common methods used by
 * all components. Functionalities specific to some of the converters can be
 * found in {@link ConverterHelper}
 * 
 */
public abstract class Converter {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(Converter.class);
	protected ConverterHelper converterHelper;
	protected static final String ID = "$id";
	protected Map<String, Object> properties = new LinkedHashMap<String, Object>();
	protected Component component;
	protected TypeBaseComponent baseComponent = null;
	protected String componentName = null;
	private String componentId;

	public Converter(Component comp){
		converterHelper = new ConverterHelper(comp);
	}
	
	/**
	 * Prepares the class of type {@link TypeBaseComponent} for xml conversion
	 * 
	 * @throws BatchException
	 * @throws SchemaException
	 */
	public void prepareForXML() {
		componentName = (String) properties.get(Constants.PARAM_NAME);
		componentId=component.getComponentId();
		if (StringUtils.isNotBlank(componentId))
			baseComponent.setId(componentId);
		else
			baseComponent.setId(componentName);
		baseComponent.setName(componentName);
		try {
			baseComponent.setBatch((String) properties
					.get(Constants.PARAM_BATCH));
		} catch (NullPointerException | NumberFormatException nfe) {
			logger.error("Batch id Empty or Invalid for : {}, {}",
					new Object[]{baseComponent.getId(), nfe});
		}
	}

	/**
	 * Converts the String to {@link BooleanValueType}
	 * 
	 * @param propertyName
	 * @return {@link BooleanValueType}
	 */
	protected BooleanValueType getBoolean(String propertyName) {
		logger.debug("Getting boolean Value for {}={}", new Object[] {
				propertyName, properties.get(propertyName) });
		if (properties.get(propertyName) != null) {
			BooleanValueType booleanValue = new BooleanValueType();
			booleanValue.setValue(Boolean.valueOf((String) properties
					.get(propertyName)));

			if (!booleanValue.isValue().toString()
					.equalsIgnoreCase((String) properties.get(propertyName))) {
				ComponentXpath.INSTANCE
						.getXpathMap()
						.put((ComponentXpathConstants.COMPONENT_XPATH_BOOLEAN.value().replace(
								ID, componentName)).replace(Constants.PARAM_PROPERTY_NAME,
								propertyName),
								new ComponentsAttributeAndValue(null, properties.get(propertyName).toString()));
				return booleanValue;
			} else {
				return booleanValue;
			}
		}
		return null;
	}

	/**
	 * Converts the String to {@link BigInteger}
	 * 
	 * @param propertyName
	 * @return {@link BigInteger}
	 */
	protected BigInteger getBigInteger(String propertyName) {
		logger.debug("Getting boolean Value for {}={}", new Object[] { propertyName, properties.get(propertyName) });
		BigInteger bigInteger = null;
		String propertyValue = (String) properties.get(propertyName);
		if (StringUtils.isNotBlank(propertyValue) && StringUtils.isNumeric(propertyValue)) {
			bigInteger = new BigInteger(String.valueOf(propertyValue));
		} else if (ParameterUtil.isParameter(propertyValue)) {
			ComponentXpath.INSTANCE.getXpathMap()
					.put((ComponentXpathConstants.COMPONENT_XPATH_BOOLEAN.value().replace(ID, componentId))
							.replace(Constants.PARAM_PROPERTY_NAME, propertyName),
							new ComponentsAttributeAndValue(null, properties.get(propertyName).toString()));
			bigInteger = new BigInteger(String.valueOf(0));
		}
		return bigInteger;

	}
	
	/**
	 * Converts the String to {@link BigInteger} for port
	 * 
	 * @param propertyName
	 * @return {@link BigInteger}
	 */
	protected BigInteger getPortValue(String propertyName) {
		logger.debug("Getting boolean Value for {}={}", new Object[] { propertyName, properties.get(propertyName) });
		BigInteger bigInteger = null;
		String propertyValue = (String) properties.get(propertyName);
		if (StringUtils.isNotBlank(propertyValue) && StringUtils.isNumeric(propertyValue)) {
			bigInteger = new BigInteger(String.valueOf(propertyValue));
		} else if (ParameterUtil.isParameter(propertyValue)) {
			ComponentXpath.INSTANCE.getXpathMap()
					.put((ComponentXpathConstants.COMPONENT_XPATH_BOOLEAN.value().replace(ID, componentId))
							.replace(Constants.PARAM_PROPERTY_NAME, propertyName),
							new ComponentsAttributeAndValue(null, properties.get(propertyName).toString()));
			return null;
		}
		return bigInteger;

	}
	
	/**
	 * Converts String value to {@link StandardCharsets}
	 * 
	 * @return {@link StandardCharsets}
	 */
	protected StandardCharsets getCharset() {
		logger.debug("Getting StandardCharsets for {}", properties.get(Constants.PARAM_NAME));
		String charset = (String) properties.get(PropertyNameConstants.CHAR_SET
				.value());
		StandardCharsets targetCharset = null;
		for (StandardCharsets standardCharsets : StandardCharsets.values()) {
			if (standardCharsets.value().equalsIgnoreCase(charset)) {
				targetCharset = standardCharsets;
				break;
			}
		}
		if (targetCharset == null)
			ComponentXpath.INSTANCE.getXpathMap().put(
					ComponentXpathConstants.COMPONENT_CHARSET_XPATH.value()
							.replace(ID, componentName), new ComponentsAttributeAndValue(null, charset));
		return targetCharset;
	}

	/**
	 * Converts String value to {@link TypeDependsOn}
	 * 
	 * @return {@link TypeDependsOn}
	 */
	/*protected TypeDependsOn getDependsOn() {
		logger.debug("Getting DependsOn for {}", properties.get(Constants.PARAM_NAME));
		if (properties.get(Constants.PARAM_DEPENDS_ON) != null) {
			TypeDependsOn dependsOn = new TypeDependsOn();
			dependsOn.setComponentId((String) properties.get(Constants.PARAM_DEPENDS_ON));
			return dependsOn;
		}
		return null;
	}*/

	protected TypeProperties getRuntimeProperties() {
		TypeProperties typeProperties = null;
		Map<String, String> runtimeProps = (Map<String, String>) properties.get(PropertyNameConstants.RUNTIME_PROPERTIES.value());
		if (runtimeProps != null && !runtimeProps.isEmpty()) {
			typeProperties = new TypeProperties();
			List<TypeProperties.Property> runtimePropertyList = typeProperties.getProperty();
			if (!converterHelper.hasAllKeysAsParams(runtimeProps)) {

				for (Map.Entry<String, String> entry : runtimeProps.entrySet()) {
					if(!ParameterUtil.isParameter(entry.getKey())){
						Property runtimeProperty = new Property();
						runtimeProperty.setName(entry.getKey());
						runtimeProperty.setValue(entry.getValue());
						runtimePropertyList.add(runtimeProperty);
					}else{
						converterHelper.addParamTag(this.ID, entry.getKey(), 
								ComponentXpathConstants.RUNTIME_PROPERTIES.value(), false);
					}
				}
			}else{
				StringBuffer parameterFieldNames = new StringBuffer();
				Property runtimeProperty = new Property();
				runtimeProperty.setName("");
				runtimeProperty.setValue("");
				runtimePropertyList.add(runtimeProperty);
				for (Entry<String, String> propertyEntry : runtimeProps.entrySet())
					parameterFieldNames.append(propertyEntry.getKey() + " ");
				converterHelper.addParamTag(this.ID, parameterFieldNames.toString(),
						ComponentXpathConstants.RUNTIME_PROPERTIES.value(), true);
			}
		}
		return typeProperties;
	}
	/**
	 * Returns the base type of the component
	 * 
	 * @return {@link TypeBaseComponent}
	 */
	public TypeBaseComponent getComponent() {
		return baseComponent;
	}
	
	/**
	 * This method returns absolute path of subjob xml.
	 * 
	 * @param subJobPath
	 * @return
	 */
	protected String getSubJobAbsolutePath(String subJobPath) {
		String absolutePath = subJobPath;
		IPath ipath=new Path(subJobPath);
		try{
			if (ResourcesPlugin.getWorkspace().getRoot().getFile(ipath).exists())
				absolutePath= ResourcesPlugin.getWorkspace().getRoot().getFile(ipath).getLocation().toString();
			else if (ipath.toFile().exists())
				absolutePath= ipath.toFile().getAbsolutePath();
		}catch(Exception exception){
			logger.warn("Exception occurred while getting absolute path for "+subJobPath,exception);
		}
		return absolutePath;
	}
	
	
	/**
	 * Converts the String to {@link BigInteger}
	 * @param propertyName
	 * @param nameOfFeild
	 * @param fieldKey
	 * @return
	 */
	public BigInteger getDBAdditionalParamValue(String propertyName , String nameOfField, String fieldKey){
		Map<String,String> propertyValue = (Map<String, String>) properties.get(propertyName);
		if (StringUtils.isNotBlank(propertyValue.get(nameOfField)) && StringUtils.isNumeric(propertyValue.get(nameOfField))) {
			BigInteger bigInteger = new BigInteger(String.valueOf(propertyValue.get(nameOfField)));
			return bigInteger;
		} else if (ParameterUtil.isParameter(propertyValue.get(nameOfField))) {
			ComponentXpath.INSTANCE.getXpathMap()
					.put((ComponentXpathConstants.COMPONENT_XPATH_BOOLEAN.value().replace(ID, component.getComponentId()))
							.replace(Constants.PARAM_PROPERTY_NAME, fieldKey),
							new ComponentsAttributeAndValue(null, propertyValue.get(nameOfField)));
			return null;
		}
		return null;
	}
	
}
