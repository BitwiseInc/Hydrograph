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
package hydrograph.ui.datastructure.expression;

import hydrograph.ui.common.cloneableinterface.IDataStructure;
import hydrograph.ui.common.datastructures.messages.Messages;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bitwise Expression editor data values are stored here
 *
 */
public class ExpressionEditorData implements IDataStructure {
	private boolean isValid;
	private String errorMessage=Messages.INVALID_EXPRESSION;
	private String expression;
	private List<String> fieldsUsedInExpression;
	private Map<String,Class<?>> selectedInputFieldsForExpression;
	private Map<String,Class<?>> extraFieldDatatypeMap;
 	private String componentName;
	public ExpressionEditorData(String expression,String componentName) {
		this.expression = expression;
		fieldsUsedInExpression = new ArrayList<>();
		selectedInputFieldsForExpression = new LinkedHashMap<String,Class<?>>();
		extraFieldDatatypeMap=new LinkedHashMap<String,Class<?>>();
		this.componentName=componentName;
	}

	/**
	 * @return get extra fields value
	 */
	public Map<String, Class<?>> getExtraFieldDatatypeMap() {
		return extraFieldDatatypeMap;
	}

    public ExpressionEditorData(String expression, List<String> clonedListUsedFieldsInExpression,
			Map<String,Class<?>> clonedSelectedFieldsForExpression,
			Map<String,Class<?>> clonedExtraInputFieldsDatatypeMap) {
		this.expression=expression;
		this.fieldsUsedInExpression=clonedListUsedFieldsInExpression;
		this.selectedInputFieldsForExpression=clonedSelectedFieldsForExpression;
		this.extraFieldDatatypeMap=clonedExtraInputFieldsDatatypeMap;
		
	}

	/**
	 * @return expression value
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * @param expression set expression value
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * @return
	 */
	public Map<String,Class<?>> getSelectedInputFieldsForExpression() {
		return selectedInputFieldsForExpression;
	}

	public List<String> getfieldsUsedInExpression() {
		return fieldsUsedInExpression;
	}
   @Override
   public boolean equals(Object obj) {
	   if (this == obj)
			return true;
	   if (obj == null||getClass() != obj.getClass())
			return false;
	   ExpressionEditorData other=(ExpressionEditorData)obj;
	   if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
	   if (fieldsUsedInExpression == null) {
			if (other.fieldsUsedInExpression != null)
				return false;
		} else if (!fieldsUsedInExpression.equals(other.fieldsUsedInExpression))
			return false;
	   return true;
    }
   @Override
   public int hashCode() {
	   final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((fieldsUsedInExpression == null) ? 0 : fieldsUsedInExpression.hashCode());
		return result;
    }

	/**
	 * @return error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
	/**
	 * @param errorMessage set error message
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/**
	 * @return
	 */
	public boolean isValid() {
		return isValid;
	}
	
	/**
	 * @param isValid
	 */
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ExpressionEditorData clone() {
		String clonedExpression=this.expression;
		boolean isValid=this.isValid;
		if(extraFieldDatatypeMap==null){
			extraFieldDatatypeMap=new LinkedHashMap<String,Class<?>>();
		}
		List<String> clonedFieldsUsedInExpression = new ArrayList<>();
		clonedFieldsUsedInExpression.addAll(this.fieldsUsedInExpression);
		Map<String,Class<?>> clonedSelectedInputFieldsForExpression = new LinkedHashMap<String,Class<?>>();
		Map<String,Class<?>> clonedExtraInputFieldsDatatypeMap = new LinkedHashMap<String,Class<?>>();
		for(Map.Entry<String, Class<?>> entry:selectedInputFieldsForExpression.entrySet())
		{
			clonedSelectedInputFieldsForExpression.put(entry.getKey(), entry.getValue());
		}
		for(Map.Entry<String, Class<?>> entry:extraFieldDatatypeMap.entrySet())
		{
			clonedExtraInputFieldsDatatypeMap.put(entry.getKey(), entry.getValue());
		}
		ExpressionEditorData expressionEditorData=
				new ExpressionEditorData(clonedExpression, clonedFieldsUsedInExpression, clonedSelectedInputFieldsForExpression
						,clonedExtraInputFieldsDatatypeMap);
		expressionEditorData.setValid(isValid);
		expressionEditorData.setComponentName(componentName);
		return expressionEditorData;
	}

	/**
	 * @return component name
	 */
	public String getComponentName() {
		return componentName;
	}
    
	/**
	 * 
	 * merge the two maps into  single map
	 * @return map 
	 */
	public Map<String,Class<?>> getCombinedFieldDatatypeMap()
	{
        Map<String,Class<?>> fieldDatatypeMap=new LinkedHashMap<>();
        fieldDatatypeMap.putAll(extraFieldDatatypeMap);
        fieldDatatypeMap.putAll(selectedInputFieldsForExpression);
		return fieldDatatypeMap;
	}
	
	/**
	 * @param componentName set component name
	 */
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public void clear() {
		isValid = false;
		errorMessage = Messages.INVALID_EXPRESSION;
		expression = "";
		fieldsUsedInExpression.clear();
		selectedInputFieldsForExpression.clear();
		extraFieldDatatypeMap.clear();
		componentName = "";
	}

}
