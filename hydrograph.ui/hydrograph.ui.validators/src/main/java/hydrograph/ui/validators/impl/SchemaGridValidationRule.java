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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GenerateRecordSchemaGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.logging.factory.LogFactory;


public class SchemaGridValidationRule implements IValidator {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(SchemaGridValidationRule.class); 

	private static final String DATA_TYPE_DATE = "java.util.Date";
	private static final String DATA_TYPE_BIG_DECIMAL = "java.math.BigDecimal";
	private static final String SCALE_TYPE_NONE = "none";
	private static final String REGULAR_EXPRESSION_FOR_NUMBER = "\\d+";
	
	String errorMessage;
	
	@Override
	public boolean validateMap(Object object, String propertyName,Map<String,List<FixedWidthGridRow>> inputSchemaMap) {
		Map<String, Object> propertyMap = (Map<String, Object>) object;
		if(propertyMap != null && !propertyMap.isEmpty()){ 
			return validate(propertyMap.get(propertyName), propertyName,inputSchemaMap,false);
		}
		return false;
	}

	@Override
	public boolean validate(Object object, String propertyName,Map<String,List<FixedWidthGridRow>> inputSchemaMap
			,boolean isJobImported){
		Schema schema = (Schema) object;
		if(schema == null){
			errorMessage = propertyName + " is mandatory";
			return false;
		}
		else if(schema.getIsExternal()){
			errorMessage = propertyName + " is mandatory";
			if(StringUtils.isBlank(schema.getExternalSchemaPath())){
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
		int precision = 0 , scale = 0;
		
		/*this list is used for checking duplicate names in the grid*/
		List<String> uniqueNamesList = new ArrayList<>();
		boolean fixedWidthGrid = false;
		boolean generateRecordSchemaGrid = false;
		
		if(gridRowList == null || gridRowList.isEmpty()){
			errorMessage = propertyName + " is mandatory";
			return false;
		}
		
		GridRow gridRowTest = gridRowList.iterator().next();
		
		if (gridRowTest instanceof GenerateRecordSchemaGridRow) {
			generateRecordSchemaGrid = true;
		}
		if (gridRowTest instanceof FixedWidthGridRow) {
			fixedWidthGrid = true;
		}
		
		for (GridRow gridRow : gridRowList) {
			if(StringUtils.isBlank(gridRow.getFieldName())){
				errorMessage = "Field name can not be blank";
				return false;
			}
			
			if(!gridRow.getFieldName().matches(Constants.REGEX)){
				errorMessage="Only Underscore and Alphanumeric characters are allowed Ex.jKh_78.";
				return false;
			}
			if(StringUtils.isNotBlank(gridRow.getScale())){
				try{
					if(StringUtils.isNotEmpty(gridRow.getPrecision())){
						precision = Integer.parseInt(gridRow.getPrecision());
					}
				}
				catch(NumberFormatException exception){
					logger.debug("Failed to parse the precision ", exception);
					return false;
				}
				
				try{
					scale = Integer.parseInt(gridRow.getScale());
				}
				catch(NumberFormatException exception){
					logger.debug("Failed to parse the scale ", exception);
					return false;
				}
			}
			
			if(DATA_TYPE_BIG_DECIMAL.equalsIgnoreCase(gridRow.getDataTypeValue())){
				if(StringUtils.isBlank(gridRow.getScale()) || scale<0 || !(gridRow.getScale().matches(REGULAR_EXPRESSION_FOR_NUMBER))){
					errorMessage = "Scale should be positive integer.";
					return false;
				}
				try{
					Integer.parseInt(gridRow.getScale());
				}catch(NumberFormatException exception){
					logger.debug("Failed to parse the scale", exception);
					errorMessage = "Scale must be integer value";
					return false;
				}
			}
			else if(DATA_TYPE_DATE.equalsIgnoreCase(gridRow.getDataTypeValue()) && 
					StringUtils.isBlank(gridRow.getDateFormat())){
				errorMessage = "Date format is mandatory";
				return false;
			}
			
			if (StringUtils.equalsIgnoreCase(DATA_TYPE_BIG_DECIMAL, gridRow.getDataTypeValue())
					&& ((gridRow.getScaleType() ==null) || StringUtils.equalsIgnoreCase(
							gridRow.getScaleTypeValue(),SCALE_TYPE_NONE))){
				errorMessage = "Scale type cannot be blank or none for Big Decimal data type";
				return false;
			}
			
			if(fixedWidthGrid && (!generateRecordSchemaGrid)){
				FixedWidthGridRow fixedWidthGridRow = (FixedWidthGridRow) gridRow;
				if(StringUtils.isBlank(fixedWidthGridRow.getLength())){
					logger.debug("Length is mandatory for FixedWidthComponent");
				return false;
				} else if(!(fixedWidthGridRow.getLength().matches(REGULAR_EXPRESSION_FOR_NUMBER))|| (fixedWidthGridRow.getLength().equals("0"))){
					logger.debug("Length should be a positive Integer greater than 0");
                return false;
          }
			}
			if(uniqueNamesList.isEmpty() || !uniqueNamesList.contains(gridRow.getFieldName())){
				uniqueNamesList.add(gridRow.getFieldName());
			}
			else{
				errorMessage = "Schema grid must have unique names";
				return false;
			}
		}
		return true;
	}
}
