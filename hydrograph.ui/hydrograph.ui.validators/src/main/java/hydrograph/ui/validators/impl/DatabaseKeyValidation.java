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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;

/***
 * DatabaseKeyValidation class validates the incorrect key fields present for Database Components
 * @author Bitwise
 *
 */
public class DatabaseKeyValidation implements IValidator{
	
	private String errorMessage;

	@Override
	public boolean validateMap(Object object, String propertyName,
			Map<String, List<FixedWidthGridRow>> inputSchemaMap) {
		Map<String, Object> propertyMap = (Map<String, Object>) object;
		if (propertyMap != null && !propertyMap.isEmpty()) {
			return validate(propertyMap.get(propertyName), propertyName,inputSchemaMap,false);
		}
		return false;
	}

	@Override
	public boolean validate(Object object, String propertyName, Map<String, List<FixedWidthGridRow>> inputSchemaMap,
			boolean isJobFileImported) {
		if (object != null) {
			List<String> tmpList = new LinkedList<>();
			Map<String, Object> keyFieldsList = (LinkedHashMap<String, Object>) object;
			
			if (keyFieldsList != null && !keyFieldsList.isEmpty()&& keyFieldsList.containsKey(Constants.LOAD_TYPE_NEW_TABLE_KEY)) {
				if(inputSchemaMap != null){
					for(java.util.Map.Entry<String, List<FixedWidthGridRow>> entry : inputSchemaMap.entrySet()){
						List<FixedWidthGridRow> gridRowList = entry.getValue();
						gridRowList.forEach(gridRow -> tmpList.add(gridRow.getFieldName()));
					}
				}
				for(Entry<String, Object> grid: keyFieldsList.entrySet()){
				    String[] keyValues = StringUtils.split((String) grid.getValue(), ",");
				    for(String values : keyValues){
				    	if(!tmpList.contains(values)){
				    		errorMessage = "Target Fields Should be present in Available Fields";
							return false;
				    	}
				    }
				}
				
				return true;
			}else if(keyFieldsList != null && !keyFieldsList.isEmpty() && keyFieldsList.containsKey(Constants.LOAD_TYPE_INSERT_KEY)){
				return true;
			}else if(keyFieldsList != null && !keyFieldsList.isEmpty() && keyFieldsList.containsKey(Constants.LOAD_TYPE_REPLACE_KEY)){
				return true;
			}
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

}
