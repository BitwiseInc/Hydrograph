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

import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.DatabaseSelectionConfig;

/**
 * ToggleSelectionValidationRule validates the selected option for database components
 * @author Bitwise
 *
 */
public class ToggleSelectionValidationRule implements IValidator {
	private String errorMessage;

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return errorMessage;
	}

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
		
		DatabaseSelectionConfig databaseSelectionConfig = (DatabaseSelectionConfig) object;

		if (databaseSelectionConfig != null) {
			if (databaseSelectionConfig.isTableName()) {
				if (StringUtils.isNotBlank(databaseSelectionConfig.getTableName())) {
					return true;
				} else {
					errorMessage = propertyName + " can not be blank";
					return false;
				}
			} else {
				if ((StringUtils.isNotBlank(databaseSelectionConfig.getSqlQuery()))) {
					return true;
				}
				else {
					errorMessage = propertyName + " can not be blank";
					return false;
				}
			}
		}

		return false;
	}

}
