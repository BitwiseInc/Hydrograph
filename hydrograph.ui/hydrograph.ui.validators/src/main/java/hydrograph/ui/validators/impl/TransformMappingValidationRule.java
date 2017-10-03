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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Display;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ExternalOperationExpressionUtil;
import hydrograph.ui.common.util.PathUtility;
import hydrograph.ui.common.util.TransformMappingFeatureUtility;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.mapping.ErrorObject;
import hydrograph.ui.datastructure.property.mapping.InputField;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;
import hydrograph.ui.expression.editor.util.FieldDataTypeMap;
import hydrograph.ui.validators.utils.ValidatorUtility;

public class TransformMappingValidationRule implements IValidator{
	private String errorMessage;
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
		errorMessage="";
		TransformMapping transformMapping=(TransformMapping) object;
		
		if(transformMapping==null)
		{
			errorMessage = propertyName + " is mandatory";
			return false;
		}	
		List<MappingSheetRow> mappingSheetRows=TransformMappingFeatureUtility.INSTANCE.getActiveMappingSheetRow
				(transformMapping.getMappingSheetRows());
		List<NameValueProperty>  mapOrPassthroughfields = transformMapping.getMapAndPassthroughField();
		if(isJobImported)
		{	
		List<InputField> inputFieldsList = new ArrayList<InputField>();
		for(Entry< String,List<FixedWidthGridRow>> inputList :inputSchemaMap.entrySet()){
			for(FixedWidthGridRow row : inputList.getValue()){
				inputFieldsList.add(new InputField(row.getFieldName(), new ErrorObject(false, "")));
			}
			
		}
		transformMapping.setInputFields(inputFieldsList);
		ValidatorUtility.INSTANCE.putOutputFieldsInTransformMapping(transformMapping);
		isJobImported=false;
		}
		if((mappingSheetRows==null || mappingSheetRows.isEmpty()) && (mapOrPassthroughfields==null || mapOrPassthroughfields.isEmpty() ) )
		{
	    errorMessage = propertyName + "Output field(s) is mandatory";		 	
		return false;
		}
		
		Set<FilterProperties>set=null;
		if(mappingSheetRows!=null && !mappingSheetRows.isEmpty())
		{
			validateAllExpressions(mappingSheetRows, inputSchemaMap);
			
			for(MappingSheetRow mappingSheetRow:mappingSheetRows)
			{
				if(!mappingSheetRow.isExpression())
				{	
					if(StringUtils.isBlank(mappingSheetRow.getOperationClassPath()))
					{
					 errorMessage = propertyName + "Operation class is blank in"+" "+mappingSheetRow.getOperationID();		
					 return false;
					}
					else if(
						!mappingSheetRow.isClassParameter()
						&&!mappingSheetRow.isWholeOperationParameter()
						//&&StringUtils.equalsIgnoreCase(mappingSheetRow.getComboBoxValue(),"Custom")
						&&!(ValidatorUtility.INSTANCE.isClassFilePresentOnBuildPath(mappingSheetRow.getOperationClassPath()))
						)
					{
					   errorMessage = "Operation class is not present for"+" "+mappingSheetRow.getOperationID();
					   return false;
					}else if(mappingSheetRow.getExternalOperation()!=null && mappingSheetRow.getExternalOperation().isExternal())
					{
						if(StringUtils.isBlank(mappingSheetRow.getExternalOperation().getFilePath())){
							errorMessage = "External path is blank for"+" "+mappingSheetRow.getOperationID();
							return false;
						}else {
							checkIfUIDataAndFileIsOutOfSyncForOperation(mappingSheetRow);
							if(StringUtils.isNotBlank(errorMessage)){
								return false;
							}
						}
					}
				}
				else if(mappingSheetRow.isExpression())
				{
					String expressionText=mappingSheetRow.getExpressionEditorData().getExpression();
					ExpressionEditorData expressionEditorData=mappingSheetRow.getExpressionEditorData();

					if(StringUtils.isBlank(expressionText))
					{
						 errorMessage = propertyName + "Expression is blank in"+" "+mappingSheetRow.getOperationID();		
						 return false;
					}
					if(!expressionEditorData.isValid())
					{
						errorMessage = expressionEditorData.getErrorMessage();
						return false;
					}
					if(mappingSheetRow.getExternalExpresion()!=null && mappingSheetRow.getExternalExpresion().isExternal())
					{
						if(StringUtils.isBlank(mappingSheetRow.getExternalExpresion().getFilePath())){
							errorMessage = "External path is blank for"+" "+mappingSheetRow.getOperationID();
							return false;
						}else {
							checkIfUIDataAndFileIsOutOfSyncForExpression(mappingSheetRow);
							if(StringUtils.isNotBlank(errorMessage)){
								return false;
							}
						}
					}
					
				}	
				if(mappingSheetRow.getOutputList().isEmpty())
				{
					 errorMessage = propertyName + "Operation field(s) are empty";		
					 return false;
				}
			    for(NameValueProperty nameValueProperty :mappingSheetRow.getNameValueProperty())
			    {
			    	if(StringUtils.isBlank(nameValueProperty.getPropertyValue()))
			    	{
			    		 errorMessage = propertyName + "Property value is Blank";		
						 return false;
			    	}	
			    }
			   
			}
			Set<String> duplicateOperationIdSet=new HashSet<String>();
			for(MappingSheetRow mappingSheetRow:mappingSheetRows)
			{
				   set = new HashSet<FilterProperties>(mappingSheetRow.getInputFields());
				   if(set.size() < mappingSheetRow.getInputFields().size())
				   {
					 errorMessage = propertyName + "Duplicate field(s) exists in" +" "+mappingSheetRow.getOperationID();
					 return false;
				   }
				   
				   if(!(duplicateOperationIdSet.add(mappingSheetRow.getOperationID())))
				   {
					   errorMessage = propertyName + "Duplicate operation Id"+" "+mappingSheetRow.getOperationID();
					   return false;
				   }  
			}
			
		}
		
		for (NameValueProperty nameValueProperty : mapOrPassthroughfields) {
			if(!transformMapping.getInputFields().contains(new InputField(nameValueProperty.getPropertyName(), new ErrorObject(false, "")))){
				 errorMessage = propertyName + " Input field not present.";		
				 return false;
			}
		}
		
		List<FilterProperties> filterProperties = new ArrayList<>();

		for (NameValueProperty nameValue : transformMapping.getMapAndPassthroughField()) {
			FilterProperties filterProperty = new FilterProperties();
			filterProperty.setPropertyname(nameValue.getPropertyValue());
			filterProperties.add(filterProperty);
		}
		
		List<FilterProperties> operationOutputFieldList=new ArrayList<>();
		for( MappingSheetRow mappingSheetRow : mappingSheetRows)
		{
			operationOutputFieldList.addAll(mappingSheetRow.getOutputList());
		}
		 set = new HashSet<FilterProperties>(filterProperties);
		 set.addAll(operationOutputFieldList);
		
		if((set.size()<(operationOutputFieldList.size()+filterProperties.size()))) 
		{
			 errorMessage = propertyName + "Duplicate field(s) exists in OutputFields";		
			 return false;
			
		}
		return checkIfExternalOutputPathIsBlank(transformMapping);
	}
	
	private void checkIfUIDataAndFileIsOutOfSyncForExpression(MappingSheetRow mappingSheetRow) {
		Display.getCurrent().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try{
					ExternalOperationExpressionUtil.INSTANCE.validateUIExpressionWithExternalFile(mappingSheetRow, 
							PathUtility.INSTANCE.getPath(mappingSheetRow.getExternalExpresion().getFilePath(),
									Constants.XML_EXTENSION, false, Constants.XML_EXTENSION));	
					
				}catch(RuntimeException exception){
					errorMessage=exception.getMessage();
				}
			}
		});
	}

	private void checkIfUIDataAndFileIsOutOfSyncForOperation(MappingSheetRow mappingSheetRow) {
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				try{
					ExternalOperationExpressionUtil.INSTANCE.validateUIOperationWithExternalFile(mappingSheetRow, 
							PathUtility.INSTANCE.getPath(mappingSheetRow.getExternalOperation().getFilePath(),
									Constants.XML_EXTENSION, false, Constants.XML_EXTENSION));	
					
				}catch(RuntimeException exception){
					errorMessage=exception.getMessage();
				}
			}
		});
	}
	
	
	
	private boolean checkIfExternalOutputPathIsBlank(TransformMapping transformMapping) {
		if(transformMapping.getExternalOutputFieldsData()!=null && transformMapping.getExternalOutputFieldsData().isExternal()){
			if(StringUtils.isBlank(transformMapping.getExternalOutputFieldsData().getFilePath())){
				errorMessage = "External path is blank for output fields";
				return false;
			}else{
				checkIfUIDataAndFileIsOutOfSyncForOutputFields(transformMapping);
				if(StringUtils.isNotBlank(errorMessage)){
					return false;
				}
			}
		}
		return true;
	}


	private void checkIfUIDataAndFileIsOutOfSyncForOutputFields(TransformMapping transformMapping) {
		try{
			ExternalOperationExpressionUtil.INSTANCE.validateUIMappingFieldsWithExternalFile(transformMapping, 
					PathUtility.INSTANCE.getPath(transformMapping.getExternalOutputFieldsData().getFilePath(),
							Constants.XML_EXTENSION, false, Constants.XML_EXTENSION));	
			
		}catch(RuntimeException exception){
			errorMessage=exception.getMessage();
		}
	}

	private void validateAllExpressions(List<MappingSheetRow> mappingSheetRows,
			Map<String, List<FixedWidthGridRow>> inputSchemaMap) {
		for (MappingSheetRow mappingSheetRow : mappingSheetRows) {
			if (mappingSheetRow.isExpression()) {
				String expressionText = mappingSheetRow.getExpressionEditorData().getExpression();
				ExpressionEditorData expressionEditorData = mappingSheetRow.getExpressionEditorData();

				ExpressionEditorUtil.validateExpression(
						expressionText,
						FieldDataTypeMap.INSTANCE.createFieldDataTypeMap(
								expressionEditorData.getfieldsUsedInExpression(),
								inputSchemaMap.get(Constants.FIXED_INSOCKET_ID)), expressionEditorData);
				ExpressionEditorData mergeExpressionDataForGroupCombine = mappingSheetRow
						.getMergeExpressionDataForGroupCombine();
				if (mergeExpressionDataForGroupCombine != null
						&& StringUtils.isNotBlank(mergeExpressionDataForGroupCombine.getExpression())) {
					String mergeExpressionText = mappingSheetRow.getMergeExpressionDataForGroupCombine().getExpression();
					
					ExpressionEditorUtil.validateExpression(
							mergeExpressionText,
							FieldDataTypeMap.INSTANCE.createFieldDataTypeMap(
									mergeExpressionDataForGroupCombine.getfieldsUsedInExpression(),
									inputSchemaMap.get(Constants.FIXED_INSOCKET_ID)), mergeExpressionDataForGroupCombine);
				}
			}
		}
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
}