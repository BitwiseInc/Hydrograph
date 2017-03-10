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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.OperationClassProperty;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;
import hydrograph.ui.expression.editor.util.FieldDataTypeMap;
import hydrograph.ui.validators.utils.ValidatorUtility;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


public class ClassNameValidatorRule implements IValidator {
	private static final Pattern VALID_JAVA_IDENTIFIER = Pattern.compile(
			"(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)*\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");
	
	private String errorMessage;
	
	public boolean validateJavaIdentifier(String identifier) {
        return VALID_JAVA_IDENTIFIER.matcher(identifier).matches();
    }
	
	@Override
	public boolean validateMap(Object object, String propertyName,Map<String,List<FixedWidthGridRow>> inputSchemaMap) {
		Map<String, Object> propertyMap = (Map<String, Object>) object;
		if(propertyMap != null && !propertyMap.isEmpty()){ 
			return validate(propertyMap.get(propertyName), propertyName,inputSchemaMap,false);
		}
		errorMessage = "Invalid parameter value";
		return false;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public boolean validate(Object object, String propertyName,Map<String,List<FixedWidthGridRow>> inputSchemaMap,boolean isJobImported){
		if(object == null || !OperationClassProperty.class.isAssignableFrom(object.getClass())){
			errorMessage = "Invalid parameter value";
			return false;
		}
		
		OperationClassProperty operationClassProperty = (OperationClassProperty) object; 
		if(operationClassProperty.isExpression())
		{
			String expressionText=operationClassProperty.getExpressionEditorData().getExpression();
			ExpressionEditorData expressionEditorData=operationClassProperty.getExpressionEditorData();
			if(StringUtils.isBlank(expressionText))
			{
				errorMessage = "Expression should not be blank";
				return false;	
			}

			ExpressionEditorUtil.validateExpression(expressionText 
				, FieldDataTypeMap.INSTANCE.createFieldDataTypeMap(expressionEditorData.getfieldsUsedInExpression()
				, inputSchemaMap.get(Constants.FIXED_INSOCKET_ID))
				, expressionEditorData);
			
			if(!expressionEditorData.isValid())
			{
				errorMessage = expressionEditorData.getErrorMessage();
				return false;
			}
			return true;
		}
		else
		{	
		String operationClassPath = operationClassProperty.getOperationClassPath();
		if(StringUtils.isBlank(operationClassPath)){
			errorMessage = "Field should not be empty";
			return false;
		}
		
		else if(operationClassProperty.isParameter()){
			if(!operationClassPath.startsWith("@{") || !operationClassPath.endsWith("}") || 
					operationClassPath.indexOf("}") != 2){
				return true;
			}
			else{
				errorMessage = "Invalid parameter value";
				return false;
			}
		}
		else if(!(ValidatorUtility.INSTANCE.isClassFilePresentOnBuildPath(operationClassProperty.getOperationClassPath())))
		   {
			   errorMessage = "Operation class is not present";
			   return false;
		   }	
		else{
			if(!validateJavaIdentifier(operationClassPath)){
				errorMessage = "Invalid value for property";
				return false;
			}
			else{
				return true;
			}
		}
		}
	}
}
