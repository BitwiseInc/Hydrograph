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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.expression.utils;

import hydrograph.engine.expression.api.ValidationAPI;

import java.io.Serializable;
import java.util.List;

/**
 * The Class ExpressionWrapper.
 *
 * @author Bitwise
 */
public class ExpressionWrapper implements Serializable {

	private ValidationAPI validationAPI;
	private String intialValueExpression;
	private String[] fieldNames;
	private Object[] tuples;
	private String countExpression;
	private int transformInstancesSize;
	private List<String> operationOutputFields;
	private List<String> listOfExpressions;
	private List<ValidationAPI> validationAPIList;

	public ExpressionWrapper(ValidationAPI validationAPI) {
		this.validationAPI = validationAPI;
	}

	public ExpressionWrapper(ValidationAPI validationAPI, String intialValue) {
		this.validationAPI = validationAPI;
		this.intialValueExpression = intialValue;
	}

	public ExpressionWrapper(List<ValidationAPI> validationAPIList, String countExpression) {
		this.validationAPIList = validationAPIList;
		this.countExpression = countExpression;
	}

	public ExpressionWrapper(ValidationAPI validationAPI, String[] fieldNames, Object[] tuples, String countExpression,
			int transformInstancesSize, List<String> operationOutputFields, List<String> listOfExpressions) {
		this.validationAPI = validationAPI;
		this.fieldNames = fieldNames;
		this.tuples = tuples;
		this.countExpression = countExpression;
		this.transformInstancesSize = transformInstancesSize;
		this.operationOutputFields = operationOutputFields;
		this.listOfExpressions = listOfExpressions;
	}

	public ValidationAPI getValidationAPI() {
		return validationAPI;
	}

	public List<ValidationAPI> getValidationAPIList() {
		return validationAPIList;
	}

	public String getIntialValueExpression() {
		return intialValueExpression;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public Object[] getTuples() {
		return tuples;
	}

	public String getCountExpression() {
		return countExpression;
	}

	public int getTransformInstancesSize() {
		return transformInstancesSize;
	}

	public List<String> getOperationOutputFields() {
		return operationOutputFields;
	}

	public List<String> getListOfExpressions() {
		return listOfExpressions;
	}
}
