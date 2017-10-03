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

 
package hydrograph.ui.datastructure.property.mapping;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.cloneableinterface.IDataStructure;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.OperationClassProperty;


/**
 * 
 * This class stores rows in mapping sheet 
 * 
 * @author Bitwise
 *
 */
public class MappingSheetRow implements IDataStructure {

	private List<FilterProperties> inputFieldList;
	private String comboBoxValue;
	private String operationClassPath;
	private boolean isWholeOperationParameter;
	private List<FilterProperties> outputList;
	private boolean isClassParameter;
	private String operationId;
	private List<NameValueProperty> nameValuePropertyList;
    private String wholeOperationParameterValue;
    private String operationClassFullPath;
	private boolean isExpression;
	private ExpressionEditorData expressionEditorData;
    private boolean isActive;
    private String comboDataType = "Integer";
    private String accumulator;
    private boolean isAccumulatorParameter;
    private ExpressionEditorData mergeExpressionDataForGroupCombine;
    private ExternalWidgetData externalOperation;
    private ExternalWidgetData externalExpresion;
    private List<GridRow> gridRows=new ArrayList<>();
    
    public List<GridRow> getGridRows() {
		return gridRows;
	}
    
	public ExternalWidgetData getExternalOperation() {
		return externalOperation;
	}

	public void setExternalOperation(ExternalWidgetData externalOperation) {
		this.externalOperation = externalOperation;
	}

	public ExternalWidgetData getExternalExpresion() {
		return externalExpresion;
	}

	public void setExternalExpresion(ExternalWidgetData externalExpresion) {
		this.externalExpresion = externalExpresion;
	}

	/**
	 * @return If accumulator is a parameter
	 */
	public boolean isAccumulatorParameter() {
		return isAccumulatorParameter;
	}
	
	/**
	 * @param isAccumulatorParameter set if accumulator will be a parameter
	 */
	public void setAccumulatorParameter(boolean isAccumulatorParameter) {
		this.isAccumulatorParameter = isAccumulatorParameter;
	}
	
	/**
	 * @return Get accumulator data type
	 */
	public String getComboDataType() {
		return comboDataType;
	}
	
	/**
	 * @param comboDataType set accmulator datatype
	 */
	
	public void setComboDataType(String comboDataType) {
		this.comboDataType = comboDataType;
	}
	
	 /**
     * @return 
     */
	 public boolean isExpression() {
		return isExpression;
	}
	/**
	 * Gets the operation class full path.
	 * 
	 * @return the operation class full path
	 */
    
	public String getOperationClassFullPath() {
		return operationClassFullPath;
	}

	/**
	 * Sets the operation class full path.
	 * 
	 * @param operationClassFullPath
	 *            the new operation class full path
	 */
	public void setOperationClassFullPath(String operationClassFullPath) {
		this.operationClassFullPath = operationClassFullPath;
	}

	/**
	 * Instantiates a new mapping sheet row.
	 * 
	 * @param input
	 *            the input
	 * @param operationClass
	 *            the operation class
	 * @param outputList
	 *            the output list
	 */
	public MappingSheetRow(List<FilterProperties> input,
			OperationClassProperty   operationClass,
			List<FilterProperties> outputList) {
		this.inputFieldList = input;
		this.externalExpresion=new ExternalWidgetData(false, null);
		this.externalOperation=new ExternalWidgetData(false, null);
		this.outputList = outputList;

	}

	/**
	 * Instantiates a new mapping sheet row.
	 * 
	 * @param input
	 *            the input
	 * @param outputList
	 *            the output list
	 * @param operationId
	 *            the operation id
	 * @param comBoxValue
	 *            the com box value
	 * @param operationClassPath
	 *            the operation class path
	 * @param nameValueProperty
	 *            the name value property
	 * @param isClassParameter
	 *            the is class parameter
	 * @param wholeOperationParameterValue
	 *            the whole operation parameter value
	 * @param isWholeOperationParameter
	 *            the is whole operation parameter
	 * @param operationClassFullPath
	 *            the operation class full path
	 * @param mergeExpressionEditorDataForGroupCombine 
	 */
	public MappingSheetRow(List<FilterProperties> input,
			List<FilterProperties> outputList,
			String operationId,
			String comBoxValue,
			String operationClassPath,
			List<NameValueProperty> nameValueProperty,
			boolean isClassParameter,
			String wholeOperationParameterValue,
			boolean isWholeOperationParameter,
			String operationClassFullPath,
			boolean isExpression,
			ExpressionEditorData expressionEditorData,
			ExpressionEditorData mergeExpressionEditorDataForGroupCombine, boolean isActive
			) {
		this.inputFieldList = input;
		this.outputList = outputList;
		this.comboBoxValue = comBoxValue;
		this.operationClassPath = operationClassPath;
		this.operationId=operationId;
		this.setClassParameter(isClassParameter);
		this.nameValuePropertyList=nameValueProperty;
		this.wholeOperationParameterValue=wholeOperationParameterValue;
		this.isWholeOperationParameter=isWholeOperationParameter;
		this.operationClassFullPath=operationClassFullPath;
		this.isExpression=isExpression;
		this.expressionEditorData=expressionEditorData;
		this.mergeExpressionDataForGroupCombine = mergeExpressionEditorDataForGroupCombine;
		this.isActive=isActive;
		this.externalExpresion=new ExternalWidgetData(false, null);
		this.externalOperation=new ExternalWidgetData(false, null);
    }
	
	/**
	 * Instantiates a new mapping sheet row.
	 * 
	 * @param input
	 *            the input
	 * @param outputList
	 *            the output list
	 * @param comBoxValue
	 *            the com box value
	 * @param operationClassPath
	 *            the operation class path
	 * @param isClassParameter
	 *            the is class parameter
	 * @param operationId
	 *            the operation id
	 * @param nameValueProperty
	 *            the name value property
	 */
	public MappingSheetRow(List<FilterProperties> input, List<FilterProperties> outputList, String comBoxValue,String operationClassPath,boolean isClassParameter,String operationId,
			 List<NameValueProperty> nameValueProperty,boolean isExpression,ExpressionEditorData expressionEditorData,ExpressionEditorData mergeExpressionEditorDataForGroupCombine,boolean isActive) 
	{
		this.inputFieldList = input;
		this.outputList = outputList;
		this.comboBoxValue = comBoxValue;
		this.operationClassPath = operationClassPath;
		this.operationId=operationId;
		this.nameValuePropertyList=nameValueProperty;
		this.setClassParameter(isClassParameter);
		this.isExpression=isExpression;
		this.expressionEditorData=expressionEditorData;
		this.mergeExpressionDataForGroupCombine=mergeExpressionEditorDataForGroupCombine;
		this.isActive=isActive;
		this.externalExpresion=new ExternalWidgetData(false, null);
		this.externalOperation=new ExternalWidgetData(false, null);
	}
	
	

   /** Checks if is whole operation parameter.
	 * 
	 * @return true, if is whole operation parameter
	 */
   public boolean isWholeOperationParameter() {
		return isWholeOperationParameter;
	}

	/**
	 * Sets the whole operation parameter.
	 * 
	 * @param isWholeOperationParameter
	 *            the new whole operation parameter
	 */
	public void setWholeOperationParameter(boolean isWholeOperationParameter) {
		this.isWholeOperationParameter = isWholeOperationParameter;
	}

	/**
	 * Gets the whole operation parameter value.
	 * 
	 * @return the whole operation parameter value
	 */
	public String getWholeOperationParameterValue() {
		return wholeOperationParameterValue;
	}

	/**
	 * Sets the whole operation parameter value.
	 * 
	 * @param wholeOperationParameterValue
	 *            the new whole operation parameter value
	 */
	public void setWholeOperationParameterValue(String wholeOperationParameterValue) {
		this.wholeOperationParameterValue = wholeOperationParameterValue;
	}

	/**
	 * Gets the name value property.
	 * 
	 * @return the name value property
	 */
	public List<NameValueProperty> getNameValueProperty() {
		return nameValuePropertyList;
	}

	/**
	 * Sets the name value property.
	 * 
	 * @param nameValueProperty
	 *            the new name value property
	 */
	public void setNameValueProperty(List<NameValueProperty> nameValueProperty) {
		this.nameValuePropertyList = nameValueProperty;
	}

	/**
	 * Gets the operation ID.
	 * 
	 * @return the operation ID
	 */
	public String getOperationID() {
		return operationId;
	}

	/**
	 * Sets the operation ID.
	 * 
	 * @param operaionId
	 *            the new operation ID
	 */
	public void setOperationID(String operaionId) {
		this.operationId = operaionId;
	}

	
	
	/**
	 * Gets the combo box value.
	 * 
	 * @return the combo box value
	 */
	public String getComboBoxValue() {
		return comboBoxValue;
	}
	
	/**
	 * Sets the combo box value.
	 * 
	 * @param comboBoxValue
	 *            the new combo box value
	 */
	public void setComboBoxValue(String comboBoxValue) {
		this.comboBoxValue = comboBoxValue;
	}
	
	/**
	 * Gets the operation class path.
	 * 
	 * @return the operation class path
	 */
	public String getOperationClassPath() {
		return operationClassPath;
	}
	
	/**
	 * Sets the operation class path.
	 * 
	 * @param operationClassPath
	 *            the new operation class path
	 */
	public void setOperationClassPath(String operationClassPath) {
		this.operationClassPath = operationClassPath;
	}
	
	/**
	 * Checks if is parameter.
	 * 
	 * @return true, if is parameter
	 */
	public boolean isParameter() {
		return isWholeOperationParameter;
	}
	
	
	/**
	 * @param isParameter
	 */
	public void setParameter(boolean isParameter) {
		this.isWholeOperationParameter = isParameter;
	}
    
	/** 
	 * @return Input Fields
	 */
	public List<FilterProperties> getInputFields() {
		if(this.inputFieldList==null)
			return new ArrayList<FilterProperties>();
		return inputFieldList;
	}
    
	/**
	 * @param inputFields se Input Fields
	 */
	public void setInputFields(List<FilterProperties> inputFields) {
		this.inputFieldList = inputFields;
	}
	
	/**
	 * @return output list
	 */
	public List<FilterProperties> getOutputList() {
		return outputList;
	}

	/**
	 * @param outputList set output list
	 */
	public void setOutputList(List<FilterProperties> outputList) {
		this.outputList = outputList;
	}
	
	/**
	 * @return 
	 */
	public boolean isClassParameter() {
		return isClassParameter;
	}

	/**
	 * @return get expression editor data
	 */
	public ExpressionEditorData getExpressionEditorData() {
		return expressionEditorData;
	}

	/**
	 * @param expressionEditorData set expression editor data
	 */
	public void setExpressionEditorData(ExpressionEditorData expressionEditorData) {
		this.expressionEditorData = expressionEditorData;
	}
	
	/**
	 * @param isClassParameter
	 */
	public void setClassParameter(boolean isClassParameter) {
		this.isClassParameter = isClassParameter;
	}
	
	/**
	 * @return 
	 */
	public boolean isActive() {
		return isActive;
	}
	/**
	 * @param isActive
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * @return get accmulator value
	 */
	public String getAccumulator() {
		return accumulator;
	}
	
	/**
	 * @param accumulator value
	 */
	public void setAccumulator(String accumulator) {
		this.accumulator = accumulator;
	}
	
	public ExpressionEditorData getMergeExpressionDataForGroupCombine() {
		return mergeExpressionDataForGroupCombine;
	}

	public void setMergeExpressionDataForGroupCombine(ExpressionEditorData mergeExpressionDataForGroupCombine) {
		this.mergeExpressionDataForGroupCombine = mergeExpressionDataForGroupCombine;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone(){
		List<FilterProperties> inputFieldList = new LinkedList<>();
		List<FilterProperties> outputList = new LinkedList<>();		
		List<NameValueProperty> nameValuePropertyList=new ArrayList<>();
		
		boolean isWholeOperationParameter=this.isWholeOperationParameter;
		boolean isActive=this.isActive;
		String wholeOperationParameterValue=this.wholeOperationParameterValue;
		String comboBoxvalue=this.comboBoxValue;
		String operationClasspath=this.operationClassPath;
		boolean isClassParamter=this.isClassParameter;
		boolean isOperationClass=this.isExpression;
		String operationId=this.operationId;
		String operationClassFullPath=this.operationClassFullPath;
		cloneInputOutputList(inputFieldList,this.inputFieldList);
		cloneInputOutputList(outputList,this.outputList);
		
		String accumulator=this.accumulator;
		String comboDataType = this.comboDataType;
		if(this.nameValuePropertyList!=null)
		{
		for(NameValueProperty nameValueProperty2:this.nameValuePropertyList)
		{
			NameValueProperty clonedNameValueProperty=new NameValueProperty();
			clonedNameValueProperty=nameValueProperty2.clone();
			nameValuePropertyList.add(clonedNameValueProperty);
		}
		}
		MappingSheetRow mappingSheetRow;
		if (isExpression) {
			if(mergeExpressionDataForGroupCombine==null){
				mergeExpressionDataForGroupCombine=new ExpressionEditorData("", "");
			}
			mappingSheetRow = new MappingSheetRow(inputFieldList, outputList, operationId, comboBoxvalue,
					operationClasspath, nameValuePropertyList, isClassParamter, wholeOperationParameterValue,
					isWholeOperationParameter, operationClassFullPath, isOperationClass, expressionEditorData.clone(),mergeExpressionDataForGroupCombine.clone(),
					isActive);
			if (StringUtils.isNotBlank(accumulator)) {
				mappingSheetRow.setAccumulator(accumulator);
			}
			mappingSheetRow.setAccumulatorParameter(isAccumulatorParameter);
			if (StringUtils.isNotBlank(comboDataType)) {
				mappingSheetRow.setComboDataType(comboDataType);
			}
		}
		else
		mappingSheetRow= new MappingSheetRow(inputFieldList, outputList,operationId,comboBoxvalue,operationClasspath,
				nameValuePropertyList,isClassParamter,wholeOperationParameterValue,isWholeOperationParameter,operationClassFullPath,
				isOperationClass,null,null,isActive);	
		
		cloneExternalExpressionOperation(mappingSheetRow);
		return mappingSheetRow;
	}

	private void cloneExternalExpressionOperation(MappingSheetRow mappingSheetRow) {
		if (externalExpresion == null) {
			mappingSheetRow.externalExpresion = new ExternalWidgetData(false, null);
		} else {
			mappingSheetRow.externalExpresion = (ExternalWidgetData) externalExpresion.clone();
		}

		if (externalOperation == null) {
			mappingSheetRow.externalOperation = new ExternalWidgetData(false, null);
		} else {
			mappingSheetRow.externalOperation = (ExternalWidgetData) externalOperation.clone();
		}
	}
    
	private void cloneInputOutputList(List<FilterProperties> cloneInputFieldList,List<FilterProperties> oldInputFieldList) {
		for(FilterProperties filterProperties:oldInputFieldList){
			cloneInputFieldList.add(filterProperties.clone());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MappingSheetRow [inputFields=" + inputFieldList + ", comboBoxValue=" + comboBoxValue
				+ ", operationClassPath=" + operationClassPath + ", isWholeOperationParameter="
				+ isWholeOperationParameter + ", outputList=" + outputList + ", isClassParameter=" + isClassParameter
				+ ", operationId=" + operationId + ", nameValueProperty=" + nameValuePropertyList
				+ ", wholeOperationParameterValue=" + wholeOperationParameterValue + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comboBoxValue == null) ? 0 : comboBoxValue.hashCode());
		result = prime * result + ((inputFieldList == null) ? 0 : inputFieldList.hashCode());
		result = prime * result + (isClassParameter ? 1231 : 1237);
		result = prime * result + (isWholeOperationParameter ? 1231 : 1237);
		result = prime * result + ((nameValuePropertyList == null) ? 0 : nameValuePropertyList.hashCode());
		result = prime * result + ((operationClassPath == null) ? 0 : operationClassPath.hashCode());
		result = prime * result + ((operationId == null) ? 0 : operationId.hashCode());
		result = prime * result + ((outputList == null) ? 0 : outputList.hashCode());
		result = prime * result + ((expressionEditorData == null) ? 0 : expressionEditorData.hashCode());
		result = prime * result + ((mergeExpressionDataForGroupCombine == null) ? 0 : mergeExpressionDataForGroupCombine.hashCode());
		result = prime * result
				+ ((wholeOperationParameterValue == null) ? 0 : wholeOperationParameterValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MappingSheetRow other = (MappingSheetRow) obj;
		if (comboBoxValue == null) {
			if (other.comboBoxValue != null)
				return false;
		} else if (!comboBoxValue.equals(other.comboBoxValue))
			return false;
		if (inputFieldList == null) {
			if (other.inputFieldList != null)
				return false;
		} else if (!inputFieldList.equals(other.inputFieldList))
			return false;
		if (isClassParameter != other.isClassParameter)
			return false;
		if (isWholeOperationParameter != other.isWholeOperationParameter)
			return false;
		if (nameValuePropertyList == null) {
			if (other.nameValuePropertyList != null)
				return false;
		} else if (!nameValuePropertyList.equals(other.nameValuePropertyList))
			return false;
		if (operationClassPath == null) {
			if (other.operationClassPath != null)
				return false;
		} else if (!operationClassPath.equals(other.operationClassPath))
			return false;
		if (operationId == null) {
			if (other.operationId != null)
				return false;
		} else if (!operationId.equals(other.operationId))
			return false;
		if (outputList == null) {
			if (other.outputList != null)
				return false;
		} else if (!outputList.equals(other.outputList))
			return false;
		if (wholeOperationParameterValue == null) {
			if (other.wholeOperationParameterValue != null)
				return false;
		} else if (!wholeOperationParameterValue.equals(other.wholeOperationParameterValue))
			return false;
		if(expressionEditorData==null){
			if(other.expressionEditorData!=null)
			return false;	
		}else if(!expressionEditorData.equals(other.expressionEditorData))
			return false;
		if(mergeExpressionDataForGroupCombine==null){
			if(other.mergeExpressionDataForGroupCombine!=null)
			return false;	
		}else if(!mergeExpressionDataForGroupCombine.equals(other.mergeExpressionDataForGroupCombine))
			return false;
		if(isActive!=other.isActive)
			return false;
		
		if (this.externalExpresion == null) {
			if (other.externalExpresion != null)
				return false;
		} else if (!this.externalExpresion.equals(other.externalExpresion)) {
			return false;
		}
		
		if (this.externalOperation == null) {
			if (other.externalOperation != null)
				return false;
		} else if (!this.externalOperation.equals(other.externalOperation)) {
			return false;
		}

		return true;
	}
	
	public void clear() {
		if(inputFieldList!=null)inputFieldList.clear();
		comboBoxValue = "";
		operationClassPath = "";
		isWholeOperationParameter = false;
		outputList.clear();
		isClassParameter = false;
		operationId = "";
		if(nameValuePropertyList!=null)nameValuePropertyList.clear();
		wholeOperationParameterValue = "";
		operationClassFullPath = "";
		isExpression = false;
		if(expressionEditorData!=null)expressionEditorData.clear();
		isActive = false;
		comboDataType = "Integer";
		accumulator = "";
		isAccumulatorParameter = false;
		if(mergeExpressionDataForGroupCombine!=null)mergeExpressionDataForGroupCombine.clear();
		if(externalOperation!=null)externalOperation.clear();
		if(externalExpresion!=null)externalExpresion.clear();
	}
	
}