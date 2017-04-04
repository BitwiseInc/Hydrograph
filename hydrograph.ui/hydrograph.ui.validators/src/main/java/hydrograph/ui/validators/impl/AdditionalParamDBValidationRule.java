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

public class AdditionalParamDBValidationRule implements IValidator{
	public static final String ADDITIONAL_DB_PARAM = "Additional DB Parameters";
	public static final String FETCH_SIZE = "Fetch Size";
	public static final String NO_OF_PARAM = "No. Of Partitions";
	public static final String PARTITION_KEY = "Partition Key";
	public static final String CHUNK_SIZE = "Chunk Size";
	
	
	String errorMessage;
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
		if(object != null && StringUtils.isNotBlank(object.toString())){
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

		if(StringUtils.isNotBlank(additionalParam.get(NO_OF_PARAM))){
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
			if(!StringUtils.equalsIgnoreCase(key, ADDITIONAL_DB_PARAM) && !StringUtils.equalsIgnoreCase(key, FETCH_SIZE)){
				if (StringUtils.isNotBlank(additionalParam.get(key))) {
					if(!StringUtils.equalsIgnoreCase(key, PARTITION_KEY)){
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
			if(StringUtils.equalsIgnoreCase(key, FETCH_SIZE) && StringUtils.isNotBlank(additionalParam.get(FETCH_SIZE))
					|| StringUtils.equalsIgnoreCase(key, CHUNK_SIZE) && StringUtils.isNotBlank(additionalParam.get(CHUNK_SIZE))){
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
			if(StringUtils.equalsIgnoreCase(key, ADDITIONAL_DB_PARAM) && StringUtils.isNotBlank(additionalParam.get(ADDITIONAL_DB_PARAM))){
				Matcher matchs=Pattern.compile(Constants.REGEX_ALPHA_NUMERIC).matcher(additionalParam.get(key));
				isValid = validateNumbericField(additionalParam.get(key), propertyName, errorMessage, matchs);
				if(!isValid){break;}
			}else{
				isValid =  true;
			}
			
		}
		return isValid;
	}
}
