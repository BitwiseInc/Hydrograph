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
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

public class MixedSchemeGridValidationRule implements IValidator {
	private static final Logger logger = LogFactory.INSTANCE
			.getLogger(SchemaGridValidationRule.class);

	private static final String DATA_TYPE_DATE = "java.util.Date";
	private static final String DATA_TYPE_BIG_DECIMAL = "java.math.BigDecimal";
	private static final String SCALE_TYPE_NONE = "none";
	private static final String REGULAR_EXPRESSION_FOR_NUMBER = "\\d+";

	String errorMessage;

	@Override
	public boolean validateMap(Object object, String propertyName,Map<String,List<FixedWidthGridRow>> inputSchemaMap) {
		Map<String, Object> propertyMap = (Map<String, Object>) object;
		if (propertyMap != null && !propertyMap.isEmpty()) {
			return validate(propertyMap.get(propertyName), propertyName,inputSchemaMap,false);
		}
		return false;
	}

	@Override
	public boolean validate(Object object, String propertyName,Map<String,List<FixedWidthGridRow>> inputSchemaMap
			,boolean isJobImported){
		Schema schema = (Schema) object;
		if (schema == null) {
			errorMessage = propertyName + " is mandatory";
			return false;
		} else if (schema.getIsExternal()) {
			errorMessage = propertyName + " is mandatory";
			if (StringUtils.isBlank(schema.getExternalSchemaPath())) {
				return false;
			}
		}
		return validateSchema(schema, propertyName);
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	private boolean validateSchema(Schema schema, String propertyName) {
		List<GridRow> gridRowList = (List<GridRow>) schema.getGridRow();

		/* this list is used for checking duplicate names in the grid */
		List<String> uniqueNamesList = new ArrayList<>();
		boolean mixedSchemeGrid = false;
		if (gridRowList == null || gridRowList.isEmpty()) {
			errorMessage = propertyName + " is mandatory";
			return false;
		}
		GridRow gridRowTest = gridRowList.iterator().next();
		if (MixedSchemeGridRow.class.isAssignableFrom(gridRowTest.getClass())) {
			mixedSchemeGrid = true;
		}
		for (GridRow gridRow : gridRowList) {
			if (StringUtils.isBlank(gridRow.getFieldName())) {
				errorMessage = "Field name can not be blank";
				return false;
			}

			if (DATA_TYPE_BIG_DECIMAL.equalsIgnoreCase(gridRow
							.getDataTypeValue())) {
				if (StringUtils.isBlank(gridRow.getScale()) || StringUtils.equalsIgnoreCase(gridRow.getScale(), "0") 
						|| !(gridRow.getScale().matches(REGULAR_EXPRESSION_FOR_NUMBER))) {
					errorMessage = "Scale can not be blank";
					return false;
				}
				try {
					Integer.parseInt(gridRow.getScale());
				} catch (NumberFormatException exception) {
					logger.debug("Failed to parse the scale", exception);
					errorMessage = "Scale must be integer value";
					return false;
				}
			} else if (DATA_TYPE_DATE.equalsIgnoreCase(gridRow
					.getDataTypeValue())
					&& StringUtils.isBlank(gridRow.getDateFormat())) {
				errorMessage = "Date format is mandatory";
				return false;
			}
			
			if (StringUtils.equalsIgnoreCase(DATA_TYPE_BIG_DECIMAL, gridRow.getDataTypeValue())
					&& (StringUtils.isBlank(gridRow.getScaleTypeValue()) || StringUtils.equalsIgnoreCase(
							SCALE_TYPE_NONE, gridRow.getScaleTypeValue()))){
				errorMessage = "Scale type cannot be blank or none for Big Decimal data type";
				return false;
			}
			
			if (mixedSchemeGrid) {
				MixedSchemeGridRow mixedSchemeGridRow = (MixedSchemeGridRow) gridRow;
				if (mixedSchemeGridRow.getLength().equals("0") || mixedSchemeGridRow.getLength().contains("-")) {
					errorMessage = "Length should be positive integer";
					return false;
				}
				if (StringUtils.isBlank(mixedSchemeGridRow.getLength())
						&& StringUtils.isEmpty(mixedSchemeGridRow
								.getDelimiter())) {
					errorMessage = "Length Or Delimiter is mandatory";
					return false;
				}
				if (StringUtils.isNotBlank(mixedSchemeGridRow.getLength())
						&& StringUtils.isNotEmpty(mixedSchemeGridRow.getDelimiter())) {
					errorMessage = "Either Length Or Delimiter should be given";
					return false;
				}
			}
			
			if (uniqueNamesList.isEmpty()
					|| !uniqueNamesList.contains(gridRow.getFieldName())) {
				uniqueNamesList.add(gridRow.getFieldName());
			} else {
				errorMessage = "Schema grid must have unique names";
				return false;
			}
		}
		return true;
	}
}