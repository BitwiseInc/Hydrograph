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

 
package hydrograph.ui.datastructure.property;



import java.util.ArrayList;
import java.util.List;

import hydrograph.ui.common.cloneableinterface.IDataStructure;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;


/**
 * The Class OperationClassProperty.
 * Holds details of the operation class for the transform components.
 * 
 * @author Bitwise
 */
public class OperationClassProperty implements IDataStructure{


	private String comboBoxValue;
	private String operationClassPath;
	private boolean isParameter;
	private String operationClassFullPath;
	private List<NameValueProperty> nameValuePropertyList;
	private boolean isExpression;
	private ExpressionEditorData expressionEditor ;
	
	/**
	 * Instantiates a new operation class property.
	 * 
	 * @param operationClassPath
	 *            the operation class path
	 * @param isParameter
	 *            the is parameter
	 */
	public OperationClassProperty(String comboBoxValue, String operationClassPath, boolean isParameter,boolean isExpression,ExpressionEditorData expressionEditorData) {
		this.operationClassPath = operationClassPath;
		this.isParameter = isParameter;
		this.comboBoxValue = comboBoxValue;
		this.isExpression=isExpression;
		this.expressionEditor=expressionEditorData;
		
	}
	
	

	/**
	 * Instantiates a new operation class property.
	 * 
	 * @param comboBoxValue
	 * @param operationClassPath
	 * @param isParameter
	 * @param operationClassFullPath
	 */
	public OperationClassProperty(String comboBoxValue,String operationClassPath, boolean isParameter,String operationClassFullPath,List<NameValueProperty> nameValuePropertyList ,ExpressionEditorData expressionEditorData,String componentName) {
		this.comboBoxValue=comboBoxValue;
		this.operationClassPath = operationClassPath;
		this.isParameter = isParameter;
		this.operationClassFullPath=operationClassFullPath;
		this.nameValuePropertyList=nameValuePropertyList;
		if(expressionEditorData==null)
		this.expressionEditor=new ExpressionEditorData("",componentName);
		else
		this.expressionEditor=expressionEditorData;
	}
	
	
	public OperationClassProperty(String operationClassPath, List<NameValueProperty> nameValuePropertyList, String simpleClassName) {
		this.operationClassPath = operationClassPath;
		this.nameValuePropertyList=nameValuePropertyList;
		this.comboBoxValue=simpleClassName;
	}
	
	/**
	 * 
	 * returns combobox value from Opeartion class widget
	 * 
	 * @return
	 */
	public String getComboBoxValue() {
		return comboBoxValue;
	}
	
	public void setComboBoxValue(String comboBoxValue) {
		this.comboBoxValue = comboBoxValue;
	}
	
	
	public List<NameValueProperty> getNameValuePropertyList() {
		return nameValuePropertyList;
	}

	
	public void setNameValuePropertyList(
			List<NameValueProperty> nameValuePropertyList) {
		this.nameValuePropertyList = nameValuePropertyList;
	}

	public void setParameter(boolean isParameter) {
		this.isParameter = isParameter;
	}
	
	public boolean isExpression() {
		return isExpression;
	}



	public void setExpression(boolean isExpression) {
		this.isExpression = isExpression;
	}



	public ExpressionEditorData getExpressionEditorData() {
		return expressionEditor;
	}



	public void setExpressionEditor(ExpressionEditorData expressionEditor) {
		this.expressionEditor = expressionEditor;
	}
	
	/**
	 * 
	 * Returns operation class path
	 * 
	 * @return operation class path
	 */
	public String getOperationClassPath() {
		return operationClassPath;
	}
	
	/**
	 * 
	 * returns true if user added parameter instead of operation class
	 * 
	 * @return boolean
	 */
	public boolean isParameter() {
		return isParameter;
	}
	
	/**
	 * 
	 * returns full operation class path
	 * 	
	 * @return
	 */
	public String getOperationClassFullPath() {
		return operationClassFullPath;
	}
	
	/**
	 * 
	 * set full operation class path
	 * 
	 * @param operationClassFullPath
	 */
	public void setOperationClassFullPath(String operationClassFullPath) {
		this.operationClassFullPath = operationClassFullPath;
	}
	
	@Override
	public OperationClassProperty clone() {
		List<NameValueProperty> nameValueProperties=new ArrayList<>();
		if(this.nameValuePropertyList!=null)
		{	
		for(NameValueProperty nameValueProperty:this.nameValuePropertyList)
		{
		  nameValueProperties.add(nameValueProperty.clone());
		}
		}
		OperationClassProperty clonedObject;
		if(expressionEditor!=null)
		{	
		ExpressionEditorData cloneEditorData=expressionEditor.clone();
		clonedObject=new OperationClassProperty
        (comboBoxValue,operationClassPath,isParameter,operationClassFullPath,nameValueProperties,cloneEditorData,
        		cloneEditorData.getComponentName());
		}
		else
		{	
		 clonedObject=new OperationClassProperty
				 (comboBoxValue,operationClassPath,isParameter,operationClassFullPath,nameValueProperties,null,null);
		}
		clonedObject.setExpression(isExpression);
		return clonedObject;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comboBoxValue == null) ? 0 : comboBoxValue.hashCode());
		result = prime * result + (isParameter ? 1231 : 1237);
		result = prime * result + ((operationClassFullPath == null) ? 0 : operationClassFullPath.hashCode());
		result = prime * result + ((operationClassPath == null) ? 0 : operationClassPath.hashCode());
		result = prime * result + ((nameValuePropertyList == null) ? 0 : nameValuePropertyList.hashCode());
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
		OperationClassProperty other = (OperationClassProperty) obj;
		if (comboBoxValue == null) {
			if (other.comboBoxValue != null)
				return false;
		} else if (!comboBoxValue.equals(other.comboBoxValue))
			return false;
		if (isParameter != other.isParameter)
			return false;
		if (operationClassFullPath == null) {
			if (other.operationClassFullPath != null)
				return false;
		} else if (!operationClassFullPath.equals(other.operationClassFullPath))
			return false;
		if (operationClassPath == null) {
			if (other.operationClassPath != null)
				return false;
		} else if (!operationClassPath.equals(other.operationClassPath))
			return false;
		if (nameValuePropertyList == null) {
			if (other.nameValuePropertyList != null)
				return false;
		} else if (!nameValuePropertyList.equals(other.nameValuePropertyList))
			return false;
		return true;
	}
	
	public void setOperationClassPath(String operationClassPath) {
		this.operationClassPath = operationClassPath;
	}

	@Override
	public String toString() {
		return operationClassPath;
	}
}
