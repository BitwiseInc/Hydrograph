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
package hydrograph.engine.cascading.assembly.context;

import cascading.tuple.Fields;
import cascading.tuple.TupleEntry;
import hydrograph.engine.cascading.assembly.handlers.FieldManupulatingHandler;
import hydrograph.engine.cascading.utilities.ReusableRowHelper;
import hydrograph.engine.cascading.utilities.TupleHelper;
import hydrograph.engine.expression.api.ValidationAPI;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;
import hydrograph.engine.utilities.UserClassLoader;

import java.util.ArrayList;

public class CustomHandlerContext<T> {

	private ArrayList<T> transformInstances;
	private ArrayList<ValidationAPI> exprValidationAPIList;
	private String[] initialValues;
	private Object[] accumulatorList;
	private String exprCount;
	private ArrayList<ReusableRow> inputRows;
	private ArrayList<ReusableRow> outputRows;
	private ReusableRow passthroughRow;
	private ReusableRow mapRow;
	private ReusableRow operationRow;
	private Object userObject;
	private TupleEntry outputTupleEntry;

	/**
	 * Creates the {@code CustomHandlerContext} object. Initialized input fields
	 * row, output fields row, pass through fields row and map fields row. Sets
	 * the transform class names
	 * 
	 * @param fieldManupulatingHandler
	 * @param transformClassNames
	 */
	public CustomHandlerContext(FieldManupulatingHandler fieldManupulatingHandler,
			ArrayList<String> transformClassNames) {

		this.outputTupleEntry = (TupleHelper.initializeTupleEntry(fieldManupulatingHandler.getOutputFields()));

		initialize(fieldManupulatingHandler.getOperationInputFields(),
				fieldManupulatingHandler.getOperationOutputFields(), fieldManupulatingHandler.getPassThroughFields(),
				fieldManupulatingHandler.getMapSourceFields(), fieldManupulatingHandler.getOutputFields(),
				transformClassNames, null);
	}

	public CustomHandlerContext(FieldManupulatingHandler fieldManupulatingHandler,
			ArrayList<String> transformClassNames, ArrayList<ValidationAPI> exprValidationObject, String[] initialValues2, Object[] objects) {

		this.outputTupleEntry = (TupleHelper.initializeTupleEntry(fieldManupulatingHandler.getOutputFields()));
		this.accumulatorList = objects;
		this.initialValues = initialValues2;

		initialize(fieldManupulatingHandler.getOperationInputFields(),
				fieldManupulatingHandler.getOperationOutputFields(), fieldManupulatingHandler.getPassThroughFields(),
				fieldManupulatingHandler.getMapSourceFields(), fieldManupulatingHandler.getOutputFields(),
				transformClassNames, exprValidationObject);
	}
	
	public CustomHandlerContext(FieldManupulatingHandler fieldManupulatingHandler,
			ArrayList<String> transformClassNames, ArrayList<ValidationAPI> exprValidationObject, String exprCount) {

		this.outputTupleEntry = (TupleHelper.initializeTupleEntry(fieldManupulatingHandler.getOutputFields()));
		this.exprCount = exprCount;

		initialize(fieldManupulatingHandler.getOperationInputFields(),
				fieldManupulatingHandler.getOperationOutputFields(), fieldManupulatingHandler.getPassThroughFields(),
				fieldManupulatingHandler.getMapSourceFields(), fieldManupulatingHandler.getOutputFields(),
				transformClassNames, exprValidationObject);
	}
	
	public CustomHandlerContext(FieldManupulatingHandler fieldManupulatingHandler,
			ArrayList<String> transformClassNames, ArrayList<ValidationAPI> exprValidationObject) {

		this.outputTupleEntry = (TupleHelper.initializeTupleEntry(fieldManupulatingHandler.getOutputFields()));

		initialize(fieldManupulatingHandler.getOperationInputFields(),
				fieldManupulatingHandler.getOperationOutputFields(), fieldManupulatingHandler.getPassThroughFields(),
				fieldManupulatingHandler.getMapSourceFields(), fieldManupulatingHandler.getOutputFields(),
				transformClassNames, exprValidationObject);
	}

	public CustomHandlerContext(Fields inputFields, String transformClassName, ValidationAPI exprValidationAPI) {

		ArrayList<Fields> inputs = null;
		ArrayList<String> classNames = null;
		ArrayList<ValidationAPI> exprValidationObjectList = null;

		if (inputFields != null) {
			inputs = new ArrayList<Fields>();
			inputs.add(inputFields);
		}

		if (transformClassName != null) {
			classNames = new ArrayList<String>();
			classNames.add(transformClassName);
		}
		if (exprValidationAPI != null) {
			exprValidationObjectList = new ArrayList<ValidationAPI>();
			exprValidationObjectList.add(exprValidationAPI);
		}

		initialize(inputs, null, null, null, classNames, exprValidationObjectList);
	}

	public CustomHandlerContext(FieldManupulatingHandler fieldManupulatingHandler, String transformClassName) {

		this.outputTupleEntry = (TupleHelper.initializeTupleEntry(fieldManupulatingHandler.getOutputFields()));

		ArrayList<String> classNames = null;
		if (transformClassName != null) {
			classNames = new ArrayList<String>();
			classNames.add(transformClassName);
		}

		initialize(fieldManupulatingHandler.getOperationInputFields(),
				fieldManupulatingHandler.getOperationOutputFields(), fieldManupulatingHandler.getPassThroughFields(),
				fieldManupulatingHandler.getMapSourceFields(), fieldManupulatingHandler.getOutputFields(), classNames,
				null);
	}

	@SuppressWarnings("unchecked")
	private void initialize(ArrayList<Fields> operationInputFields, ArrayList<Fields> operationOutputFields,
			Fields passThrough, Fields mapSourceFields, ArrayList<String> transformClassNames,
			ArrayList<ValidationAPI> exprValidationObjectList) {

		transformInstances = new ArrayList<T>();
		inputRows = new ArrayList<ReusableRow>();
		outputRows = new ArrayList<ReusableRow>();
		exprValidationAPIList = new ArrayList<ValidationAPI>();
		
		if (exprValidationObjectList != null) {
			for (ValidationAPI validationAPI : exprValidationObjectList)
				exprValidationAPIList.add(validationAPI);
		}
		
		if (transformClassNames != null) {
			for (String transformClassName : transformClassNames) {
				transformInstances.add((T) UserClassLoader
						.loadAndInitClass(transformClassName));
			}
		}
		
		if (operationInputFields != null) {
			for (Fields fields : operationInputFields) {
				inputRows.add(new CascadingReusableRow(ReusableRowHelper.getLinkedSetFromFields(fields)));
			}
		}

		if (operationOutputFields != null) {
			for (Fields fields : operationOutputFields) {
				outputRows.add(new CascadingReusableRow(ReusableRowHelper.getLinkedSetFromFields(fields)));
			}
		}

		if (passThrough == null) {
			passthroughRow = null;
		} else {
			passthroughRow = new CascadingReusableRow(ReusableRowHelper.getLinkedSetFromFields(passThrough));
		}

		if (mapSourceFields == null) {
			mapRow = null;
		} else {
			mapRow = new CascadingReusableRow(ReusableRowHelper.getLinkedSetFromFields(mapSourceFields));
		}
	}

	private void initialize(ArrayList<Fields> operationInputFields, ArrayList<Fields> operationOutputFields,
			Fields passThrough, Fields mapSourceFields, Fields operationFields, ArrayList<String> transformClassNames,
			ArrayList<ValidationAPI> exprValidationObjectList) {

		initialize(operationInputFields, operationOutputFields, passThrough, mapSourceFields, transformClassNames,
				exprValidationObjectList);
		if (operationFields == null) {
			operationRow = null;
		} else {
			operationRow = new CascadingReusableRow(ReusableRowHelper.getLinkedSetFromFields(operationFields));
		}
	}

	public ArrayList<ValidationAPI> getExpressionInstancesList() {
		return exprValidationAPIList;
	}

	public ValidationAPI getSingleExpressionInstances() {
		if (exprValidationAPIList.size() > 0)
			return exprValidationAPIList.get(0);
		return null;
	}

	public T getTransformInstance(int index) {
		return transformInstances.get(index);
	}

	public ArrayList<T> getTransformInstances() {
		return transformInstances;
	}

	public ReusableRow getInputRow(int index) {
		return this.inputRows.get(index);
	}

	public ReusableRow getOutputRow(int index) {
		return this.outputRows.get(index);
	}
	
	public void setOutputRow(ReusableRow row) {
		this.outputRows.add(0, row);
	}

	public ArrayList<ReusableRow> getAllOutputRow() {
		return this.outputRows;
	}
	
	public ArrayList<ReusableRow> getAllinputRow() {
		return this.inputRows;
	}

	public ReusableRow getPassThroughRow() {
		return this.passthroughRow;
	}

	public ReusableRow getOperationRow() {
		return this.operationRow;
	}
	
	public ReusableRow getSingleOutputRow() {
		return this.getOutputRow(0);
	}

	public ReusableRow getSingleInputRow() {
		return this.getInputRow(0);
	}

	public T getSingleTransformInstance() {
		return this.getTransformInstance(0);
	}

	public void setUserObject(Object obj) {
		this.userObject = obj;
	}

	public Object getUserObject() {
		return userObject;
	}

	public TupleEntry getOutputTupleEntry() {
		return outputTupleEntry;
	}

	public Object[] getAccumulatorList() {
		return accumulatorList;
	}

	public void setAccumulatorList(Object[] accumulatorList) {
		this.accumulatorList = accumulatorList;
	}
	
	public Object getAccumulatorValue(int index) {
		Object obj = accumulatorList[index];
		return obj;
	}

	public void setAccumulatorValue(Object accumulatorValue, int index) {
		this.accumulatorList[index] = accumulatorValue;
	}

	public String[] getInitialValues() {
		return initialValues;
	}
	
	public String getInitialValue(int index) {
		String str = initialValues[index]; 
		return str;
	}
	
	public String getExprCount() {
		return exprCount;
	}

	/**
	 * @param transformInstances
	 *            the transformInstances to set
	 */
	public void setTransformInstances(ArrayList<T> transformInstances) {
		this.transformInstances = transformInstances;
	}

	/**
	 * @return the object of type {@link ReusableRow} containing only the source
	 *         field names of the map fields
	 */
	public ReusableRow getMapRow() {
		return mapRow;
	}
}