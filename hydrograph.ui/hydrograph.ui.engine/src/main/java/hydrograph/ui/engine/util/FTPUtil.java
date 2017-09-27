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
package hydrograph.ui.engine.util;

import java.math.BigInteger;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.BooleanValueType;
import hydrograph.engine.jaxb.commontypes.StandardCharsets;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.FTPProtocolDetails;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.xpath.ComponentXpath;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
import hydrograph.ui.engine.xpath.ComponentsAttributeAndValue;
import hydrograph.ui.logging.factory.LogFactory;

/**
 *  The Class FTPUtil.
 * @author Bitwise
 *
 */
public class FTPUtil {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(ConverterUtil.class);
	
	/** The Constant INSTANCE. */
	public static final FTPUtil INSTANCE = new FTPUtil();
	
	public FTPUtil() {
	}

	
	/**
	 * Converts the String to {@link BigInteger} for widgets
	 * 
	 * @param propertyName
	 * @return {@link BigInteger}
	 */
	public BigInteger getPortValue(String propertyName, String componentId, Map<String, Object> properties) {
		LOGGER.debug("Getting boolean Value for {}={}", new Object[] { propertyName, properties.get(propertyName) });
		BigInteger bigInteger = null;
		String propertyValue = (String) properties.get(propertyName);
		if (StringUtils.isNotBlank(propertyValue) && StringUtils.isNumeric(propertyValue)) {
			bigInteger = new BigInteger(String.valueOf(propertyValue));
		} else if (ParameterUtil.isParameter(propertyValue)) {
			ComponentXpath.INSTANCE.getXpathMap()
					.put((ComponentXpathConstants.COMPONENT_XPATH_BOOLEAN.value().replace("$id", componentId))
							.replace(Constants.PARAM_PROPERTY_NAME, propertyName),
							new ComponentsAttributeAndValue(null, properties.get(propertyName).toString()));
			return null;
		}
		return bigInteger;
	}
	
	/**
	 * Converts the String to {@link BigInteger} for port
	 * @param propertyName
	 * @param componentId
	 * @param propertyValue
	 * @return
	 */
	public BigInteger getPortParam(String propertyName, String componentId, Map<String, Object> properties) {
		LOGGER.debug("Getting boolean Value for {}={}", new Object[] { propertyName, properties });
		BigInteger bigInteger = null;
		FTPProtocolDetails protocolDetails = (FTPProtocolDetails) properties.get(PropertyNameConstants.PROTOCOL_SELECTION.value());
		if (StringUtils.isNotBlank(protocolDetails.getPort()) && StringUtils.isNumeric(protocolDetails.getPort())) {
			bigInteger = new BigInteger(String.valueOf(protocolDetails.getPort()));
		} else if (ParameterUtil.isParameter(protocolDetails.getPort())) {
			ComponentXpath.INSTANCE.getXpathMap()
					.put((ComponentXpathConstants.COMPONENT_XPATH_BOOLEAN.value().replace("$id", componentId))
							.replace(Constants.PARAM_PROPERTY_NAME, propertyName),
							new ComponentsAttributeAndValue(null, protocolDetails.getPort()));
			return null;
		}
		return bigInteger;
	}
	
	/**
	 * Converts the String to {@link BooleanValueType}
	 * 
	 * @param propertyName
	 * @return {@link BooleanValueType}
	 */
	public BooleanValueType getBoolean(String propertyName, String componentName, Map<String, Object> properties) {
		LOGGER.debug("Getting boolean Value for {}={}", new Object[] {
				propertyName, properties.get(propertyName) });
		if (properties.get(propertyName) != null) {
			BooleanValueType booleanValue = new BooleanValueType();
			
			if((properties.get(propertyName)) instanceof String){
				booleanValue.setValue(Boolean.valueOf((String) properties
						.get(propertyName)));
				return getPropertyBooleanValue(propertyName, booleanValue, properties, componentName);
			}else{
				booleanValue.setValue((boolean) properties.get(propertyName));
				return booleanValue;
			}

		}
		return null;
	}
	
	private BooleanValueType getPropertyBooleanValue(String propertyName, BooleanValueType booleanValue, 
			Map<String, Object> properties, String componentName){
		if (!booleanValue.isValue().toString()
				.equalsIgnoreCase((String) properties.get(propertyName))) {
			ComponentXpath.INSTANCE
					.getXpathMap()
					.put((ComponentXpathConstants.COMPONENT_XPATH_BOOLEAN.value().replace(
							"$id", componentName)).replace(Constants.PARAM_PROPERTY_NAME,
							propertyName),
							new ComponentsAttributeAndValue(null, properties.get(propertyName).toString()));
			return booleanValue;
		} else {
			return booleanValue;
		}
	}
	
	
	/**
	 * Converts String value to {@link StandardCharsets}
	 * 
	 * @return {@link StandardCharsets}
	 */
	public StandardCharsets getCharset(String propertyName, String componentName, Map<String, Object> properties) {
		LOGGER.debug("Getting StandardCharsets for {}", properties.get(Constants.PARAM_NAME));
		String charset = (String) properties.get("encoding");
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
							.replace("$id", componentName), new ComponentsAttributeAndValue(null, charset));
		return targetCharset;
	}
}
