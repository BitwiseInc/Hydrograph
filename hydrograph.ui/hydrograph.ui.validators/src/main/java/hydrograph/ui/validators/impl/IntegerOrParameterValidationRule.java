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

import hydrograph.ui.datastructure.property.FixedWidthGridRow;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Validation rule to check that value is either integer or parameter <br>
 * ex. @{some_parameter_name}.
 */
public class IntegerOrParameterValidationRule implements IValidator {
	private String errorMessage;
	
	@Override
	public boolean validateMap(Object object, String propertyName,Map<String,List<FixedWidthGridRow>> inputSchemaMap) {
		Map<String, Object> propertyMap = (Map<String, Object>) object;
		if(propertyMap != null && !propertyMap.isEmpty()){ 
			return validate(propertyMap.get(propertyName), propertyName,inputSchemaMap
					,false);
		}
		return false;
	}

	@Override
	public boolean validate(Object object, String propertyName,Map<String,List<FixedWidthGridRow>> inputSchemaMap,
			boolean isJobImported){
		String value = (String) object;
		if(StringUtils.isNotBlank(value)){
			Matcher matcher=Pattern.compile("[\\d]*").matcher(value);
			if((matcher.matches())|| 
				((StringUtils.startsWith(value, "@{") && StringUtils.endsWith(value, "}")) &&
						!StringUtils.contains(value, "@{}"))){
				return true;
			}
			errorMessage = propertyName + " is mandatory";
		}
		errorMessage = propertyName + " is not integer value or valid parameter";
		return false;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
}