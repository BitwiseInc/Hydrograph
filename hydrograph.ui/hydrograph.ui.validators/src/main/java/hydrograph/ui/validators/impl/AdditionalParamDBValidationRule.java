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

import java.math.BigInteger;
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
		if(object != null && Map.class.isAssignableFrom(object.getClass())){
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

	private boolean validatePropertyMap(Map<String, String> additionalParam, String propertyName) {
		if(!additionalParam.containsKey(Constants.ADDITIONAL_DB_CHUNK_SIZE) &&
				(additionalParam.containsKey(Constants.NUMBER_OF_PARTITIONS) || 
				additionalParam.containsKey(Constants.ADDITIONAL_DB_FETCH_SIZE) ||
				additionalParam.containsKey(Constants.ADDITIONAL_PARAMETERS_FOR_DB))){//input
			
			if(StringUtils.isNotBlank(additionalParam.get(Constants.NUMBER_OF_PARTITIONS))){
				if((validatePopertyFields(additionalParam, propertyName, propertyName))	
					&& (additionalParam.containsKey(Constants.ADDITIONAL_DB_FETCH_SIZE) &&
							validateFetchSizeField(additionalParam, propertyName, propertyName)) 
					&& validateAdditionalParam(additionalParam, propertyName, propertyName)){
					return true;
				}
			}else if((additionalParam.containsKey(Constants.ADDITIONAL_DB_FETCH_SIZE) &&
					validateFetchSizeField(additionalParam, propertyName, propertyName)) 
			&& validateAdditionalParam(additionalParam, propertyName, propertyName)){
				return true;
			}
			
			
		}else if(additionalParam.containsKey(Constants.ADDITIONAL_DB_CHUNK_SIZE) || 
				additionalParam.containsKey(Constants.ADDITIONAL_PARAMETERS_FOR_DB)){//output
			if(validateChunkSize(additionalParam, propertyName, propertyName) &&
					validateAdditionalParam(additionalParam, propertyName, propertyName)){
				return true;
			}
		}
		return false;
	}
	
	
	private boolean validatePopertyFields(Map<String, String> additionalParam, String errorMessage, String propertyName){
		if(validateNumeric(additionalParam, Constants.NUMBER_OF_PARTITIONS, propertyName) && 
				validateNumeric(additionalParam, Constants.NOP_UPPER_BOUND, propertyName) &&
				validateNumeric(additionalParam, Constants.NOP_LOWER_BOUND, propertyName) &&
				StringUtils.isNotBlank(additionalParam.get(Constants.DB_PARTITION_KEY)) && 
				compareBigIntegerValue(additionalParam.get(Constants.NOP_UPPER_BOUND), additionalParam.get(Constants.NOP_LOWER_BOUND))){
			return true;
		}else{
			errorMessage = propertyName + " is mandatory";
			return false;
		}
	}	
	
	private boolean validateNumeric(Map<String, String> additionalParam, String key, String propertyName){
		if (StringUtils.isNotBlank(additionalParam.get(key))) {
			Matcher matchs = Pattern.compile(Constants.NUMERIC_REGEX).matcher(additionalParam.get(key));
			return validateNumericField(additionalParam.get(key), propertyName, errorMessage, matchs);
		}
		return false;
	}
	
	
	private boolean validateNumericField(String value, String propertyName, String errorMessage, Matcher matchs){
		boolean isValid = false;
		if(matchs.matches()||ParameterUtil.isParameter(value)){
			isValid =  true;
		}else{
			isValid = false;
			errorMessage = propertyName + " is mandatory";
		}
		return isValid;
	}
	
	private boolean validateFetchSizeField(Map<String, String> additionalParam, String errorMessage, String propertyName){
		return validateNumeric(additionalParam, Constants.ADDITIONAL_DB_FETCH_SIZE, propertyName);
	}
	
	private boolean validateChunkSize(Map<String, String> additionalParam, String errorMessage, String propertyName){
		return validateNumeric(additionalParam, Constants.ADDITIONAL_DB_CHUNK_SIZE, propertyName);
	}
	
	private boolean validateAdditionalParam(Map<String, String> additionalParam, String errorMessage, String propertyName){
		boolean isValid = false;
		for(String key : additionalParam.keySet()){
			if(StringUtils.equalsIgnoreCase(key, Constants.ADDITIONAL_PARAMETERS_FOR_DB) && StringUtils.isNotBlank(additionalParam.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB))){
				Matcher matchs=Pattern.compile(Constants.DB_REGEX).matcher(additionalParam.get(key));
				isValid = validateNumericField(additionalParam.get(key), propertyName, errorMessage, matchs);
				if(!isValid){break;}
			}else{
				isValid =  true;
			}
			
		}
		return isValid;
	}
	
	/**
	 * The Function will compare bigInteger values
	 * @param value1
	 * @param value2
	 * @return
	 */
	private boolean compareBigIntegerValue(String value1, String value2){
		if(StringUtils.isNotBlank(value1) && StringUtils.isNotBlank(value2) && validateNumericField(value1) && validateNumericField(value2) ){
			BigInteger int1= BigInteger.valueOf(Long.parseLong(value1));
			BigInteger int2 = BigInteger.valueOf(Long.parseLong(value2));
			if(int1.compareTo(int2) == -1){
				return false;
			}
			
		}
		return true;
	}
	
	
	/**
	 * The Function used to validate the field & field should be positive integer
	 * @param text
	 * @param text1Decorator
	 * @return
	 */
	private boolean validateNumericField(String text){
		if(StringUtils.isNotBlank(text)){
			Matcher matchs = Pattern.compile(Constants.NUMERIC_REGEX).matcher(text);
			if (matchs.matches()) {
				return true;
			}
		}
		return false;
	}
}
