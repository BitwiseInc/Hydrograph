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
package hydrograph.engine.core.component.entity.elements;

import hydrograph.engine.core.component.utils.OperationOutputField;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Properties;

/**
 * The Class Operation.
 *
 * @author Bitwise
 *
 */

public class Operation implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -9115691570772361681L;
	private String operationId;
	private String operationClass;
	private String[] operationInputFields;
	private String[] operationOutputFields;
	private Properties operationProperties;
	private String expression;
	private String accumulatorInitialValue;
	private String mergeExpression;
	private boolean isExpressionPresent;
	private OperationOutputField[] operationFields;

	public Operation() {

	}

	public OperationOutputField[] getOperationFields() {
		return operationFields;
	}

	public void setOperationFields(OperationOutputField[] operationFields) {
		this.operationFields = operationFields;
	}


	public boolean isExpressionPresent() {
		return isExpressionPresent;
	}

	public void setExpressionPresent(boolean expressionPresent) {
		isExpressionPresent = expressionPresent;
	}

	/**
	 * @return the Expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * @param expression
	 *            the Expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * @return the mergeExpression
	 */
	public String getMergeExpression() {
		return mergeExpression;
	}

	/**
	 * @param mergeExpression
	 *            the mergeExpression to set
	 */
	public void setMergeExpression(String mergeExpression) {
		this.mergeExpression = mergeExpression;
	}

	/**
	 * @return the operationClass
	 */
	public String getOperationClass() {
		return operationClass;
	}

	/**
	 * @param operationClass
	 *            the operationClass to set
	 */
	public void setOperationClass(String operationClass) {
		this.operationClass = operationClass;
	}

	/**
	 * @return the operationInputFields
	 */
	public String[] getOperationInputFields() {
		return operationInputFields != null ? operationInputFields.clone() : new String[]{};
	}

	/**
	 * @param operationInputFields
	 *            the operationInputFields to set
	 */
	public void setOperationInputFields(String[] operationInputFields) {
		this.operationInputFields = operationInputFields != null ? operationInputFields.clone() : null;
	}

	/**
	 * @return the operationOutputFields
	 */
	public String[] getOperationOutputFields() {
		return operationOutputFields != null ? operationOutputFields.clone() : null;
	}

	/**
	 * @param operationOutputFields
	 *            the operationOutputFields to set
	 */
	public void setOperationOutputFields(String[] operationOutputFields) {
		this.operationOutputFields = operationOutputFields != null ? operationOutputFields.clone() : null;
	}

	/**
	 * @return the operationProperties
	 */
	public Properties getOperationProperties() {
		return operationProperties;
	}

	/**
	 * @param operationProperties
	 *            the operationProperties to set
	 */
	public void setOperationProperties(Properties operationProperties) {
		this.operationProperties = operationProperties;
	}

	/**
	 * @return the operationId
	 */
	public String getOperationId() {
		return operationId;
	}

	/**
	 * @param operationId
	 *            the operationId to set
	 */
	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
	
	/**
	 * @return the accumulatorInitialValue
	 */	
	public String getAccumulatorInitialValue() {
		return accumulatorInitialValue;
	}

	/**
	 * @param accumulatorInitialValue
	 *            the accumulatorInitialValue to set
	 */
	public void setAccumulatorInitialValue(String accumulatorInitialValue) {
		this.accumulatorInitialValue = accumulatorInitialValue;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\nOperationID: " + operationId + " | operation class: "
				+ operationClass + " | ");

		str.append("operation input fields: ");
		if (operationInputFields != null) {
			str.append(Arrays.toString(operationInputFields));
		}

		str.append(" | operation output fields: ");
		if (operationOutputFields != null) {
			str.append(Arrays.toString(operationOutputFields));
		}
		str.append(" | operation properties: " + operationProperties);

		return str.toString();
	}
}
