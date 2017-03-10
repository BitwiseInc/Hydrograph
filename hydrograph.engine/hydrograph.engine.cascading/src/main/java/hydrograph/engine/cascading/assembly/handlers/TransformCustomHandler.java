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
package hydrograph.engine.cascading.assembly.handlers;

import cascading.flow.FlowProcess;
import cascading.management.annotation.Property;
import cascading.management.annotation.PropertyDescription;
import cascading.management.annotation.Visibility;
import cascading.operation.BaseOperation;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.operation.OperationCall;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import hydrograph.engine.cascading.assembly.context.CustomHandlerContext;
import hydrograph.engine.cascading.utilities.ReusableRowHelper;
import hydrograph.engine.cascading.utilities.TupleHelper;
import hydrograph.engine.expression.api.ValidationAPI;
import hydrograph.engine.expression.userfunctions.TransformForExpression;
import hydrograph.engine.transformation.userfunctions.base.TransformBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class TransformCustomHandler extends BaseOperation<CustomHandlerContext<TransformBase>>
		implements Function<CustomHandlerContext<TransformBase>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 155032640399766097L;
	private ArrayList<Properties> props;
	private ArrayList<String> transformClassNames;
	private ArrayList<ValidationAPI> expressionList;
	private FieldManupulatingHandler fieldManupulatingHandler;
	private static Logger LOG = LoggerFactory.getLogger(TransformCustomHandler.class);

	public TransformCustomHandler(FieldManupulatingHandler fieldManupulatingHandler,
			ArrayList<Properties> userProperties, ArrayList<String> transformClassNames,
			ArrayList<ValidationAPI> expressionObjectList) {

		super(fieldManupulatingHandler.getInputFields().size(), fieldManupulatingHandler.getOutputFields());

		this.props = userProperties;
		this.transformClassNames = transformClassNames;
		this.fieldManupulatingHandler = fieldManupulatingHandler;
		this.expressionList = expressionObjectList;
		if (transformClassNames != null) {
			LOG.trace("TransformCustomHandler object created for: " + Arrays.toString(transformClassNames.toArray()));

		} else {
			LOG.trace("TransformCustomHandler object created for:" + Arrays.toString(expressionObjectList.toArray()));
		}
	}

	public Fields getInputFields() {
		return fieldManupulatingHandler.getInputFields();
	}

	public Fields getOutputFields() {
		return fieldManupulatingHandler.getOutputFields();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(FlowProcess flowProcess, OperationCall<CustomHandlerContext<TransformBase>> operationCall) {

		CustomHandlerContext<TransformBase> context = new CustomHandlerContext<TransformBase>(fieldManupulatingHandler,
				transformClassNames, expressionList);

		operationCall.setContext(context);
		int counter = -1;
		for (TransformBase transformInstance : context.getTransformInstances()) {
			if (transformInstance != null) {
				counter = counter + 1;
				LOG.trace("calling prepare method for: " + transformInstance.getClass().getName());
				try {
					transformInstance.prepare(props.get(counter), context.getInputRow(counter).getFieldNames(),
							context.getOutputRow(counter).getFieldNames());
				} catch (Exception e) {
					LOG.error("Exception in prepare method of: " + transformInstance.getClass().getName()
							+ ".\nArguments passed to prepare() method are: \nProperties: " + props + "\nInput Fields: "
							+ Arrays.toString(context.getInputRow(counter).getFieldNames().toArray())
							+ "\nOutput Fields: "
							+ Arrays.toString(context.getOutputRow(counter).getFieldNames().toArray()), e);
					throw new RuntimeException("Exception in prepare method of: "
							+ transformInstance.getClass().getName()
							+ ".\nArguments passed to prepare() method are: \nProperties: " + props + "\nInput Fields: "
							+ Arrays.toString(context.getInputRow(counter).getFieldNames().toArray())
							+ "\nOutput Fields: "
							+ Arrays.toString(context.getOutputRow(counter).getFieldNames().toArray()), e);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void operate(FlowProcess arg0, FunctionCall<CustomHandlerContext<TransformBase>> call) {
		CustomHandlerContext<TransformBase> context = call.getContext();

		Tuple inputTuple = new Tuple(call.getArguments().getTuple());

		// copy the field values of map fields into a temporary object
		// ReusableRowHelper.extractFromTuple(
		// fieldManupulatingHandler.getMapSourceFieldPositions(),
		// inputTuple, context.getMapRow());

		// copy the field values of map fields outputTupleEntry
		TupleHelper.setTupleOnPositions(fieldManupulatingHandler.getMapSourceFieldPositions(),
				call.getArguments().getTuple(), fieldManupulatingHandler.getMapTargetFieldPositions(),
				call.getContext().getOutputTupleEntry().getTuple());

		// // copy the field values of pass through fields into a temporary
		// object
		// ReusableRowHelper.extractFromTuple(
		// fieldManupulatingHandler.getInputPassThroughPositions(),
		// inputTuple, context.getPassThroughRow());

		// copy the field values of pass through fields into a temporary object
		TupleHelper.setTupleOnPositions(fieldManupulatingHandler.getInputPassThroughPositions(),
				call.getArguments().getTuple(), fieldManupulatingHandler.getOutputPassThroughPositions(),
				call.getContext().getOutputTupleEntry().getTuple());

		// // set operation row from pass through row and map row
		// ReusableRowHelper.setOperationRowFromPassThroughAndMapRow(
		// fieldManupulatingHandler.getMapFields(), context.getMapRow(),
		// context.getPassThroughRow(), context.getOperationRow());

		int counter = -1;
		for (TransformBase transformInstance : context.getTransformInstances()) {
			// for (TransformBase transformInstance :
			// context.getTransformInstances()) {

			counter = counter + 1;
			// LOG.trace("calling transform method of: " +
			// transformInstance.getClass().getName());
			if (transformInstance != null) {
				if(transformInstance instanceof TransformForExpression)
					((TransformForExpression) transformInstance)
							.setValidationAPI(expressionList.get(counter));
				try {
					transformInstance.transform(
							ReusableRowHelper.extractFromTuple(fieldManupulatingHandler.getInputPositions(counter),
									inputTuple, context.getInputRow(counter)),
							context.getOutputRow(counter));
				} catch (Exception e) {
					LOG.error("Exception in tranform method of: " + transformInstance.getClass().getName()
							+ ".\nRow being processed: " + call.getArguments(), e);
					throw new RuntimeException("Exception in tranform method of: "
							+ transformInstance.getClass().getName() + ".\nRow being processed: " + call.getArguments(),
							e);
				}
			} 
		}
		// // set operation row, copy operation fields
		// ReusableRowHelper.extractOperationRowFromAllOutputRow(
		// context.getAllOutputRow(), context.getOperationRow());
		//
		// ReusableRowHelper.setTupleEntryFromResuableRowAndReset(call
		// .getContext().getOutputTupleEntry(), context.getOperationRow());

		// Set all output fields in order
		ReusableRowHelper.setTupleEntryFromResuableRowsAndReset(call.getContext().getOutputTupleEntry(),
				context.getAllOutputRow(), fieldManupulatingHandler.getAllOutputPositions());

		// add output to collector
		call.getOutputCollector().add(call.getContext().getOutputTupleEntry());

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void cleanup(FlowProcess flowProcess, OperationCall<CustomHandlerContext<TransformBase>> call) {
		CustomHandlerContext<TransformBase> context = call.getContext();

		for (TransformBase transformInstance : context.getTransformInstances()) {
			if (transformInstance != null) {
				LOG.trace("calling cleanup method of: " + transformInstance.getClass().getName());
				transformInstance.cleanup();
			}
		}
	}

	@Property(name = "Operation Classes", visibility = Visibility.PUBLIC)
	@PropertyDescription(value = "Transform Operations executed by this Component")
	public String[] getOperationClasses() {

		String[] classes = new String[transformClassNames.size()];
		classes = transformClassNames.toArray(classes);
		return classes;
	}

}