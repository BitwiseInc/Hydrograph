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
package hydrograph.ui.dataviewer.filter;

import hydrograph.ui.common.schema.Field;
import hydrograph.ui.common.schema.Fields;
import hydrograph.ui.dataviewer.constants.AdapterConstants;
import hydrograph.ui.dataviewer.utilities.ViewDataSchemaHelper;
import hydrograph.ui.dataviewer.window.DebugDataViewer;
import hydrograph.ui.logging.factory.LogFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

/**
 * The Class FilterValidator.
 * Provides validations for Filter Window
 * @author Bitwise
 *
 */
public class FilterValidator {

	public static FilterValidator INSTANCE = new FilterValidator();
	public static final String FIELD= "Field";
	private static final Logger logger = LogFactory.INSTANCE.getLogger(FilterValidator.class);
	private List<String> relationalList = Arrays.asList(new String[]{FilterConstants.AND, FilterConstants.OR});
	
	
	/**
	 * Checks if all filter conditions are valid.
	 * 
	 * @param conditionList
	 *            the condition list
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param fieldNames
	 *            the field names
	 * @param debugDataViewer
	 *            the debug data viewer
	 * @return true, if is all filter conditions valid
	 */
	public boolean isAllFilterConditionsValid(List<Condition> conditionList, Map<String, String> fieldsAndTypes, String[] fieldNames, DebugDataViewer debugDataViewer){
		Map<String, String[]> conditionalOperatorsMap = FilterHelper.INSTANCE.getTypeBasedOperatorMap();
		
		for (int index = 0; index < conditionList.size(); index++) {
			Condition condition = conditionList.get(index);
			String relationalOperator = condition.getRelationalOperator();
			String fieldName = condition.getFieldName();
			String conditional = condition.getConditionalOperator();
			String value1 = condition.getValue1();
			String value2 = condition.getValue2();
			if(index != 0 && StringUtils.isBlank(relationalOperator)){
				logger.trace("Relational Operator at {} is blank" + index);
				return false;
			}
			if(StringUtils.isBlank(fieldName) 
					|| StringUtils.isBlank(conditional) || StringUtils.isBlank(value1)){
				logger.trace("Field name at {} is blank" + index);
				return false;
			}
			if (FilterConstants.BETWEEN.equalsIgnoreCase(conditional)) {
				if (StringUtils.isBlank(value2)) {
					logger.trace("Value 2 at {} is blank" + index);
					return false;
				}
			}
			
			if (StringUtils.equalsIgnoreCase(FilterConstants.BETWEEN_FIELD,conditional)) {
				if (StringUtils.isBlank(value2)) {
					logger.trace("Value 2 at {} is blank" + index);
					return false;
				}
			}
			
			if(index != 0 && !relationalList.contains(relationalOperator)){
				logger.trace("Relational Operator at {} is incorrect", index);
				return false;
			}
			if(!Arrays.asList(fieldNames).contains(fieldName)){
				logger.trace("Field Name at {} is incorrect {}", index);
				return false;
			}
			String type = getType(fieldName, fieldsAndTypes);
			List<String> operators = Arrays.asList(conditionalOperatorsMap.get(type));
			if(!operators.contains(condition.getConditionalOperator())){
				logger.trace("operator at {} is incorrect", condition.getConditionalOperator());
				return false;
			}
			else {
				if (condition.getConditionalOperator().contains(FIELD)) {
					if (StringUtils.equalsIgnoreCase(condition.getConditionalOperator(), FilterConstants.BETWEEN_FIELD)) {
						if (validateField(fieldsAndTypes, value1, fieldName)
								&& validateField(fieldsAndTypes, value2, fieldName)) {
							return true;
						} else {
							return false;
						}
					}
					else if (validateField(fieldsAndTypes, value1 ,fieldName)) {
						return true;
					}
					else {
						return false;
					}
				}
				else if(StringUtils.isNotBlank(value1)){
				if(!validateDataBasedOnTypes(type, value1, condition.getConditionalOperator(),debugDataViewer,fieldName)){
					return false;
				}
			}
			if (condition.getConditionalOperator().equalsIgnoreCase(FilterConstants.BETWEEN)) {
				if (StringUtils.isNotBlank(value2)) {
					if (!validateDataBasedOnTypes(type, value2, condition.getConditionalOperator(),debugDataViewer,fieldName)) {
						return false;
					}
				}
			}
		}
		}
		return true;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @param fieldName
	 *            the field name
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @return the type
	 */
	public String getType(String fieldName, Map<String, String> fieldsAndTypes){
		String type = fieldsAndTypes.get(fieldName);
		return type;
	}
	
	/**
	 * Validate field.
	 * 
	 * @param fieldsAndTypes
	 *            the fields and types
	 * @param value
	 *            the value
	 * @param field
	 *            the field
	 * @return true, if successful
	 */
	public boolean validateField(Map<String,String> fieldsAndTypes, String value ,String field){
		List<String> columnList = new ArrayList<String>(fieldsAndTypes.keySet());
		if(columnList.contains(value) && fieldsAndTypes.get(value).equals(fieldsAndTypes.get(field))){
			return true;
		}
	return false;
} 
	
	/**
	 * Validate data based on types.
	 * 
	 * @param type
	 *            the type
	 * @param value
	 *            the value
	 * @param conditionalOperator
	 *            the conditional operator
	 * @param debugDataViewer
	 *            the debug data viewer
	 * @param fieldName
	 *            the field name
	 * @return true, if successful
	 */
	public boolean validateDataBasedOnTypes(String type, String value, String conditionalOperator, DebugDataViewer debugDataViewer, String fieldName){
		try{
			if(FilterConstants.IN.equalsIgnoreCase(conditionalOperator) ||
					FilterConstants.NOT_IN.equalsIgnoreCase(conditionalOperator)){
				if(value.contains(FilterConstants.DELIM_COMMA)){
					StringTokenizer tokenizer = new StringTokenizer(value, FilterConstants.DELIM_COMMA);
					int numberOfTokens = tokenizer.countTokens();
					for (int index = 0; index < numberOfTokens; index++) {
						validate(type, tokenizer.nextToken(),debugDataViewer,fieldName);
					}
				}
				else {
					validate(type, value, debugDataViewer, fieldName);
				}
			}
			else if (FilterConstants.BETWEEN.equalsIgnoreCase(conditionalOperator)) {
				validate(type, value,debugDataViewer,fieldName);
			}
			else{
				validate(type, value,debugDataViewer,fieldName);
			}
		}
		catch(Exception exception){
			logger.trace("value can not be converted to {}", new Object[]{type});
			return false;
		}
		return true;
	}

	private boolean validate(String type, String value, DebugDataViewer debugDataViewer, String fieldName) throws ParseException {
		if(FilterConstants.TYPE_BOOLEAN.equals(type)){
			Boolean convertedBoolean = Boolean.valueOf(value);
			if(!StringUtils.equalsIgnoreCase(convertedBoolean.toString(), value)){
				return false;
			}
		}
		else if(FilterConstants.TYPE_DOUBLE.equals(type)){
			Double.valueOf(value);
		}
		else if(FilterConstants.TYPE_FLOAT.equals(type)){
			Float.valueOf(value);
		}
		else if(FilterConstants.TYPE_INTEGER.equals(type)){
			Integer.valueOf(value);
		}
		else if(FilterConstants.TYPE_LONG.equals(type)){
			Long.valueOf(value);
		}
		else if(FilterConstants.TYPE_SHORT.equals(type)){
			Short.valueOf(value);
		}
		else if(FilterConstants.TYPE_STRING.equals(type)){
			String.valueOf(value);
		}
		else if(FilterConstants.TYPE_BIGDECIMAL.equals(type)){
			new BigDecimal(value);
		}
		else if(FilterConstants.TYPE_DATE.equals(type)){
			String debugFileName = debugDataViewer.getDebugFileName();
			String debugFileLocation = debugDataViewer.getDebugFileLocation();
			Fields dataViewerFileSchema = ViewDataSchemaHelper.INSTANCE.getFieldsFromSchema(debugFileLocation + 
					debugFileName + AdapterConstants.SCHEMA_FILE_EXTENTION);
			for (Field field : dataViewerFileSchema.getField()) {
					if(field.getName().equalsIgnoreCase(fieldName))
					{
						SimpleDateFormat sdf = new SimpleDateFormat(field.getFormat()); 
						sdf.parse(value);
					}
			}
			
		}
		return true;
		
	}
}
