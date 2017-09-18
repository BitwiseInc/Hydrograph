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
package hydrograph.ui.validators.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.datastructure.property.FTPProtocolDetails;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;

/**
 * The Class FTPProtocolSelectionValidator to validate protocol selection widgets
 * @author Bitwise
 *
 */
public class FTPProtocolSelectionValidator implements IValidator{
	private String errorMessage;

	@Override
	public boolean validateMap(Object object, String propertyName,
			Map<String, List<FixedWidthGridRow>> inputSchemaMap) {
		Map<String, Object> propertyMap = (Map<String, Object>) object;
		if (propertyMap != null && !propertyMap.isEmpty()) {
			return validate(propertyMap.get(propertyName), propertyName, inputSchemaMap,false);
		}
		return false;
	}

	@Override
	public boolean validate(Object object, String propertyName, Map<String, List<FixedWidthGridRow>> inputSchemaMap,
			boolean isJobFileImported) {
		FTPProtocolDetails protocolDetails = null;
		if(object instanceof FTPProtocolDetails){
			if(object != null && !object.equals("")){
				protocolDetails = (FTPProtocolDetails) object;
				if(!StringUtils.equalsIgnoreCase(protocolDetails.getProtocol(), "AWS S3 HTTPS")){
					if(protocolDetails.getHost() != null && protocolDetails.getPort() != null){
						if(StringUtils.isBlank(protocolDetails.getHost())){
							return false;
						}
						if(!validatePort(protocolDetails.getPort(), "Port")){
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
	
	private boolean validatePort(String text, String propertyName){
		if(StringUtils.isNotBlank(text)){
			Matcher matcher=Pattern.compile("[\\d]*").matcher(text);
			if((matcher.matches())|| 
				((StringUtils.startsWith(text, "@{") && StringUtils.endsWith(text, "}")) &&
						!StringUtils.contains(text, "@{}"))){
				return true;
			}
			errorMessage = propertyName + " is mandatory";
		}
		errorMessage = propertyName + " is not integer value or valid parameter";
		return false;
	}

}
