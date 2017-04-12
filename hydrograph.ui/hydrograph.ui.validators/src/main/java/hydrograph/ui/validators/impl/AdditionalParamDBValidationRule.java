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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;

/**
 * AdditionalParamDBValidationRule validates the additional parameter fields present for Database Components
 * @author Bitwise
 *
 */
public class AdditionalParamDBValidationRule implements IValidator{
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
		
		Map<String, String> additionalParam = null;
		if(object != null && object instanceof Map){
				additionalParam = (Map<String, String>) object;
				if (!additionalParam.isEmpty()) {
					return validatePropertyMap(additionalParam, propertyName);
				}
			}
			errorMessage = propertyName + " can not be blank";
		
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	private boolean validatePropertyMap(Map<String, String> additionalParam,
			String propertyName) {
		boolean retValue = false;

		if(StringUtils.isNotBlank(additionalParam.get(Constants.NO_OF_PARAM))){
			retValue = validatePopertyFields(additionalParam, propertyName, propertyName);
		}else{
			retValue = true;
		}
		if(retValue){
			retValue = validateAdditionField(additionalParam, propertyName, propertyName);
		}
		
		if(retValue){
			retValue = validateAdditionalParam(additionalParam, propertyName, propertyName);
		}
		
		return retValue;
	}
	
	private boolean validatePopertyFields(Map<String, String> additionalParam, String errorMessage, String propertyName){
		boolean isValid = false;
		for (String key : additionalParam.keySet()) {
			if(!StringUtils.equalsIgnoreCase(key, Constants.ADDITIONAL_DB_PARAM) && !StringUtils.equalsIgnoreCase(key, Constants.FETCH_SIZE)){
				if (StringUtils.isNotBlank(additionalParam.get(key))) {
					if(!StringUtils.equalsIgnoreCase(key, Constants.PARTITION_KEY)){
						Matcher matchs = Pattern.compile(Constants.NUMERIC_REGEX).matcher(additionalParam.get(key));
						isValid = validateNumbericField(additionalParam.get(key), propertyName, errorMessage, matchs);
						if(!isValid){break;}
					}
				}else{
					isValid = false;
					errorMessage = propertyName + " is mandatory";
					break;
				}
			}
		}
		return isValid;
	}
	
	private boolean validateAdditionField(Map<String, String> additionalParam, String errorMessage, String propertyName){
		boolean isValid = false;
		for(String key : additionalParam.keySet()){
			if(StringUtils.equalsIgnoreCase(key, Constants.FETCH_SIZE) && StringUtils.isNotBlank(additionalParam.get(Constants.FETCH_SIZE))
					|| StringUtils.equalsIgnoreCase(key, Constants.CHUNK_SIZE) && StringUtils.isNotBlank(additionalParam.get(Constants.CHUNK_SIZE))){
				Matcher matchs = Pattern.compile(Constants.NUMERIC_REGEX).matcher(additionalParam.get(key));
				isValid = validateNumbericField(additionalParam.get(key), propertyName, errorMessage, matchs);
				if(!isValid){break;}
			}
		}
		return isValid;
	}
	
	private boolean validateNumbericField(String value, String propertyName, String errorMessage, Matcher matchs){
		boolean isValid = false;
		if(matchs.matches()||ParameterUtil.isParameter(value)){
			isValid =  true;
		}else{
			isValid = false;
			errorMessage = propertyName + " is mandatory";
		}
		return isValid;
	}
	
	private boolean validateAdditionalParam(Map<String, String> additionalParam, String errorMessage, String propertyName){
		boolean isValid = false;
		for(String key : additionalParam.keySet()){
			if(StringUtils.equalsIgnoreCase(key, Constants.ADDITIONAL_DB_PARAM) && StringUtils.isNotBlank(additionalParam.get(Constants.ADDITIONAL_DB_PARAM))){
				Matcher matchs=Pattern.compile(Constants.DB_REGEX).matcher(additionalParam.get(key));
				isValid = validateNumbericField(additionalParam.get(key), propertyName, errorMessage, matchs);
				if(!isValid){break;}
			}else{
				isValid =  true;
			}
			
		}
		return isValid;
	}
}
