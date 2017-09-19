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

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;

/**
 * The Class FTPAuthenticationValidator to validate authentication fields
 * @author Bitwise
 *
 */
public class FTPAuthenticationValidator implements IValidator{
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
		Map<String, FTPAuthOperationDetails> additionalParam = null;
		if(object != null && Map.class.isAssignableFrom(object.getClass())){
				additionalParam = (Map<String, FTPAuthOperationDetails>) object;
				if (!additionalParam.isEmpty()) {
					for(Map.Entry<String, FTPAuthOperationDetails> map : additionalParam.entrySet()){
						FTPAuthOperationDetails details = map.getValue();
						if(map.getKey().contains("S3")){
							if(StringUtils.equalsIgnoreCase("AWS S3 Property File", map.getKey())){
									if(details.getField2() == null || details.getField2().isEmpty()){
										errorMessage = propertyName + " is mandatory";
										return false;
									}
							}else {
								if(details.getField1() == null || details.getField1().isEmpty()){
									errorMessage = propertyName + " is mandatory";
									return false;
								}
								if(details.getField2() == null || details.getField2().isEmpty()){
									errorMessage = propertyName + " is mandatory";
									return false;
								}
							}
						}else{
								if(details.getField1() == null || details.getField1().isEmpty()){
									errorMessage = propertyName + " is mandatory";
									return false;
								}
								if(details.getField2() == null || details.getField2().isEmpty()){
									errorMessage = propertyName + " is mandatory";
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

}
