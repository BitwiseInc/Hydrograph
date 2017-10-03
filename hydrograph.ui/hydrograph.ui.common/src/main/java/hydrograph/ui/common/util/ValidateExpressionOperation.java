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


package hydrograph.ui.common.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.datastructure.filter.ExpressionData;
import hydrograph.ui.common.datastructure.filter.OperationClassData;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.external.common.ExpressionOutputFields;
import hydrograph.ui.external.common.InputField;
import hydrograph.ui.external.common.InputFields;
import hydrograph.ui.external.common.OperationOutputFields;
import hydrograph.ui.external.common.Properties;
import hydrograph.ui.external.expression.Expression;
import hydrograph.ui.external.expression.ExternalExpression;
import hydrograph.ui.external.mapping.ExpressionField;
import hydrograph.ui.external.mapping.ExternalMapping;
import hydrograph.ui.external.mapping.MapField;
import hydrograph.ui.external.mapping.OperationField;
import hydrograph.ui.external.operation.ExternalOperation;
import hydrograph.ui.external.operation.Operation;

/**
 * validate expression operation data helper
 * 
 * @author Bitwise
 *
 */
public class ValidateExpressionOperation {

	public static final ValidateExpressionOperation INSTANCE = new ValidateExpressionOperation();

	/**
	 * 
	 * validate ui expression data with external file data
	 * 
	 * @param externalExpression
	 * @param mappingSheetRow
	 * @return
	 * @throws RuntimeException
	 */
	public boolean validateUIExpressionVsJaxb(ExternalExpression externalExpression, MappingSheetRow mappingSheetRow)
			throws RuntimeException {
		Expression expression = externalExpression.getExpression();
		if (expression != null) {
			if (!StringUtils.equals(expression.getId(), mappingSheetRow.getOperationID())) {
				throw new RuntimeException(
						mappingSheetRow.getOperationID() + "- external file is out of sync (Id mismatch)");
			}
			if (isNotEqual(expression.getExpr(), mappingSheetRow.getExpressionEditorData().getExpression())) {
				throw new RuntimeException(
						mappingSheetRow.getOperationID() + "- external file is out of sync (Expression mismatch)");
			}
			if (isNotEqual(expression.getMergeExpr(),
					mappingSheetRow.getMergeExpressionDataForGroupCombine().getExpression())) {
				throw new RuntimeException(mappingSheetRow.getOperationID()
						+ "- external file is out of sync (Merge Expression mismatch)");
			}
			if (isNotEqual(expression.getAccumulatorInitalValue(), mappingSheetRow.getAccumulator())) {
				throw new RuntimeException(mappingSheetRow.getOperationID()
						+ "- external file is out of sync (Accumulator value mismatch)");
			}
			if (!checkInputListsAreEqual(expression.getInputFields(), mappingSheetRow.getInputFields())) {
				throw new RuntimeException(
						mappingSheetRow.getOperationID() + "- external file is out of sync (Input-List mismatch)");
			}
			if (!checkExpressionOutputListsAreEqual(expression.getOutputFields(), mappingSheetRow.getOutputList())) {
				throw new RuntimeException(
						mappingSheetRow.getOperationID() + "- external file is out of sync (Output field mismatch)");
			}
		}
		return true;
	}
	
	/**
	 * 
	 * validate ui expression data with external file data
	 * 
	 * @param externalExpression
	 * @param expressionData
	 * @return
	 * @throws RuntimeException
	 */
	public boolean validateUIExpressionVsJaxb(ExternalExpression externalExpression, ExpressionData expressionData)
			throws RuntimeException {
		Expression expression = externalExpression.getExpression();
		if (expression != null) {
			if (!StringUtils.equals(expression.getId(), expressionData.getId())) {
				throw new RuntimeException(
						expressionData.getId() + "- external file is out of sync (Id mismatch)");
			}
			if (isNotEqual(expression.getExpr(), expressionData.getExpressionEditorData().getExpression())) {
				throw new RuntimeException(
						expressionData.getId() + "- external file is out of sync (Expression mismatch)");
			}
			
			if (!checkFilterInputListsAreEqual(expression.getInputFields(), expressionData.getInputFields())) {
				throw new RuntimeException(
						expressionData.getId() + "- external file is out of sync (Input-List mismatch)");
			}
		}
		return true;
	}

	/**
	 * 
	 *  validate ui operation data with external file data
	 * @param externalOperations
	 * @param mappingSheetRow
	 * @return
	 */
	public boolean validateUIOperationVsJaxb(ExternalOperation externalOperations, MappingSheetRow mappingSheetRow) {
		Operation operation = externalOperations.getOperation();
		if (operation != null) {
			if (!StringUtils.equals(operation.getId(), mappingSheetRow.getOperationID())) {
				throw new RuntimeException(
						mappingSheetRow.getOperationID() + "- external file is out of sync (Id mismatch)");
			}
			if (isNotEqual(operation.getClazz(), mappingSheetRow.getOperationClassPath())) {
				throw new RuntimeException(
						mappingSheetRow.getOperationID() + "- external file is out of sync (Class mismatch)");
			}
			if (!checkInputListsAreEqual(operation.getInputFields(), mappingSheetRow.getInputFields())) {
				throw new RuntimeException(
						mappingSheetRow.getOperationID() + "- external file is out of sync (Input field mismatch)");
			}
			if (!checkOperationOutputListsAreEqual(operation.getOutputFields(), mappingSheetRow.getOutputList(),
					mappingSheetRow)) {
				throw new RuntimeException(
						mappingSheetRow.getOperationID() + "- external file is out of sync (Output field mismatch)");
			}
			if (!checkOperationPropertyList(operation.getProperties(), mappingSheetRow.getNameValueProperty())) {
				throw new RuntimeException(mappingSheetRow.getOperationID()
						+ "- external file is out of sync (Operation properties mismatch)");
			}

		}
		return true;
	}
	
	/**
	 * 
	 * validate ui operation data with external file data
	 * 
	 * @param externalOperations
	 * @param operationClassData
	 * @return
	 */
	public boolean validateUIOperationVsJaxb(ExternalOperation externalOperations, OperationClassData operationClassData) {
		Operation operation = externalOperations.getOperation();
		if (operation != null) {
			if (!StringUtils.equals(operation.getId(), operationClassData.getId())) {
				throw new RuntimeException(
						operationClassData.getId() + "- external file is out of sync (Id mismatch)");
			}
			if (isNotEqual(operation.getClazz(), operationClassData.getQualifiedOperationClassName())) {
				throw new RuntimeException(
						operationClassData.getId() + "- external file is out of sync (Class mismatch)");
			}
			if (!checkFilterInputListsAreEqual(operation.getInputFields(), operationClassData.getInputFields())) {
				throw new RuntimeException(
						operationClassData.getId() + "- external file is out of sync (Input field mismatch)");
			}
			if (!checkOperationPropertyList(operation.getProperties(), operationClassData.getClassProperties())) {
				throw new RuntimeException(operationClassData.getId()
						+ "- external file is out of sync (Operation properties mismatch)");
			}

		}
		return true;
	}
	

	private boolean checkOperationPropertyList(Properties properties, List<NameValueProperty> uiPropertryList) {
		
		if(properties == null && uiPropertryList != null && uiPropertryList.isEmpty()) {
			return true;
		}
		
		if ((properties == null && uiPropertryList != null) || (properties == null && uiPropertryList != null)
				|| (uiPropertryList == null && properties != null)
				|| (uiPropertryList == null && properties.getProperty() != null)
				|| (properties.getProperty().size() != uiPropertryList.size())) {
			return false;
		}

		for (int index = 0; index < properties.getProperty().size(); index++) {
			if (isNotEqual(properties.getProperty().get(index).getName(),
					uiPropertryList.get(index).getPropertyName())) {
				return false;
			}
			if (isNotEqual(properties.getProperty().get(index).getValue(),
					uiPropertryList.get(index).getPropertyValue())) {
				return false;
			}
		}
		return true;
	}

	private boolean isNotEqual(String jaxbValue, String uiValue) {
		if (!(StringUtils.isBlank(jaxbValue) && StringUtils.isBlank(uiValue))) {
			if (!StringUtils.equals(jaxbValue, uiValue)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkOperationOutputListsAreEqual(OperationOutputFields outputFields,
			List<FilterProperties> uiOutputList, MappingSheetRow mappingSheetRow) {
		
		if(outputFields == null && uiOutputList != null && uiOutputList.isEmpty()) {
			return true;
		}
		
		if ((outputFields == null && uiOutputList != null) || (outputFields == null && uiOutputList != null)
				|| (uiOutputList == null && outputFields != null)
				|| (uiOutputList == null && outputFields.getField() != null)
				|| (outputFields.getField().size() != uiOutputList.size())) {
			return false;
		}
		if (outputFields.getField() != null && !outputFields.getField().isEmpty() && uiOutputList != null
				&& !uiOutputList.isEmpty()) {
			for (int index = 0; index < outputFields.getField().size(); index++) {
				if (isNotEqual(outputFields.getField().get(index).getName(),
						uiOutputList.get(index).getPropertyname())) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkExpressionOutputListsAreEqual(ExpressionOutputFields outputFields,
			List<FilterProperties> uiOutputList) {
		
		if(outputFields == null && uiOutputList != null && uiOutputList.isEmpty()) {
			return true;
		}
		
		if ((outputFields == null && uiOutputList != null) || (outputFields == null && uiOutputList != null)
				|| (uiOutputList == null && outputFields != null)
				|| (uiOutputList == null && outputFields.getField() != null)) {
			return false;
		}
		if (outputFields !=null && StringUtils.isNotBlank(outputFields.getField().getName()) && !uiOutputList.isEmpty()) {
			if (!StringUtils.equals(outputFields.getField().getName(), uiOutputList.get(0).getPropertyname())) {
				return false;
			}
		}
		return true;
	}

	private boolean checkInputListsAreEqual(InputFields inputFields, List<FilterProperties> uiInputFields) {
		
		if(inputFields == null && uiInputFields != null && uiInputFields.isEmpty()) {
			return true;
		}
		
		if ((inputFields == null && uiInputFields != null) || (inputFields.getField() == null && uiInputFields != null)
				|| (uiInputFields == null && inputFields != null)
				|| (uiInputFields == null && inputFields.getField() != null)
				|| (inputFields.getField().size() != uiInputFields.size())) {
			return false;
		}
		if (inputFields.getField() != null && !inputFields.getField().isEmpty() && uiInputFields != null
				&& !uiInputFields.isEmpty()) {
			for (int index = 0; index < inputFields.getField().size(); index++) {
				if (isNotEqual(inputFields.getField().get(index).getName(),
						uiInputFields.get(index).getPropertyname())) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkFilterInputListsAreEqual(InputFields inputFields, List<String> uiInputFields) {
		
		if(inputFields == null && uiInputFields != null && uiInputFields.isEmpty()) {
			return true;
		}
		
		if ((inputFields == null && uiInputFields != null) || (inputFields.getField() == null && uiInputFields != null)
				|| (uiInputFields == null && inputFields != null)
				|| (uiInputFields == null && inputFields.getField() != null)
				|| (inputFields.getField().size() != uiInputFields.size())) {
			return false;
		}
		if (inputFields.getField() != null && !inputFields.getField().isEmpty() && uiInputFields != null
				&& !uiInputFields.isEmpty()) {
			for (int index = 0; index < inputFields.getField().size(); index++) {
				if (isNotEqual(inputFields.getField().get(index).getName(),
						uiInputFields.get(index))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * validate transform output fields with external file data.
	 * 
	 * @param externalMappings
	 * @param transformMapping
	 * @return
	 */
	public boolean validateUIMappingFieldsVsJaxb(ExternalMapping externalMappings, TransformMapping transformMapping) {

		if (externalMappings.getPassThroughFieldOrOperationFieldOrExpressionField() != null
				&& transformMapping.getOutputFieldList() != null) {
			if (getSizeOfExternalMappingsFields(externalMappings, transformMapping) != transformMapping
					.getOutputFieldList().size()) {
				throw new RuntimeException("Output field's external file is out of sync");
			}
		}

		boolean isPassThroughField = isPassThroughFields(externalMappings);
		if (externalMappings.getPassThroughFieldOrOperationFieldOrExpressionField() != null
				&& !externalMappings.getPassThroughFieldOrOperationFieldOrExpressionField().isEmpty()) {
			List<String> listofFields = new ArrayList<>();
			transformMapping.getOutputFieldList().forEach(list -> {
				listofFields.add(list.getPropertyname());
			});
			for (int index = 0; index < externalMappings.getPassThroughFieldOrOperationFieldOrExpressionField()
					.size(); index++) {
				Object object = externalMappings.getPassThroughFieldOrOperationFieldOrExpressionField().get(index);
				// In the case of *.
				if (isPassThroughField && object.getClass() == InputField.class) {
					continue;
				}
				String fieldName = getFieldName(object);

				if (!listofFields.contains(fieldName)) {
					throw new RuntimeException("Output field's external file is out of sync");
				}
			}
		}
		return true;
	}

	private int getSizeOfExternalMappingsFields(ExternalMapping externalMappings, TransformMapping transformMapping) {
		if (isPassThroughFields(externalMappings)) {
			return externalMappings.getPassThroughFieldOrOperationFieldOrExpressionField().size() - 1
					+ transformMapping.getInputFields().size();
		} else {
			return externalMappings.getPassThroughFieldOrOperationFieldOrExpressionField().size();
		}
	}

	private boolean isPassThroughFields(ExternalMapping externalMappings) {
		boolean isPassThroughField = false;
		if (externalMappings != null
				&& externalMappings.getPassThroughFieldOrOperationFieldOrExpressionField() != null) {
			List<Object> fields = externalMappings.getPassThroughFieldOrOperationFieldOrExpressionField();
			for (Object field : fields) {
				if ("*".equals(getFieldName(field))) {
					isPassThroughField = true;
					break;
				}
			}
		}
		return isPassThroughField;
	}

	private String getFieldName(Object object) {
		if (object instanceof MapField) {
			return ((MapField) object).getName();
		} else if (object instanceof InputField) {
			return ((InputField) object).getName();
		} else if (object instanceof OperationField) {
			return ((OperationField) object).getName();
		} else if (object instanceof ExpressionField) {
			return ((ExpressionField) object).getName();
		}
		return null;
	}
	

}
