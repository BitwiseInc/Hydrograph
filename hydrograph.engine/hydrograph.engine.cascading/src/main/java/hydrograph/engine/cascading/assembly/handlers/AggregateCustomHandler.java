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
import cascading.operation.Aggregator;
import cascading.operation.AggregatorCall;
import cascading.operation.BaseOperation;
import cascading.operation.OperationCall;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import hydrograph.engine.cascading.assembly.context.CustomHandlerContext;
import hydrograph.engine.cascading.utilities.ReusableRowHelper;
import hydrograph.engine.cascading.utilities.TupleHelper;
import hydrograph.engine.expression.api.ValidationAPI;
import hydrograph.engine.expression.userfunctions.AggregateForExpression;
import hydrograph.engine.expression.utils.ExpressionWrapper;
import hydrograph.engine.transformation.userfunctions.base.AggregateTransformBase;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class AggregateCustomHandler extends BaseOperation<CustomHandlerContext<AggregateTransformBase>>
		implements Aggregator<CustomHandlerContext<AggregateTransformBase>> {

	Logger LOG = LoggerFactory.getLogger(AggregateCustomHandler.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -5599319878964805478L;
	/**
	 * 
	 */

	private ArrayList<Properties> props;
	private ArrayList<String> transformClassNames;
	private FieldManupulatingHandler fieldManupulatingHandler;
	private ArrayList<ValidationAPI> expressionList;
	private String[] initialValues;

	public AggregateCustomHandler(
			FieldManupulatingHandler fieldManupulatingHandler,
			ArrayList<Properties> props, ArrayList<String> transformClassNames,
			ArrayList<ValidationAPI> expressionObjectList, String[] initialValues) {
		super(fieldManupulatingHandler.getInputFields().size(), fieldManupulatingHandler.getOutputFields());
		this.props = props;
		this.transformClassNames = transformClassNames;
		this.fieldManupulatingHandler = fieldManupulatingHandler;
		this.expressionList = expressionObjectList;
		this.initialValues = initialValues;
		if (transformClassNames != null) {
			LOG.trace("AggregateCustomHandler object created for: " + Arrays.toString(transformClassNames.toArray()));
		} else {
			LOG.trace("AggregateCustomHandler object created for:" + Arrays.toString(expressionObjectList.toArray()));
		}
	}

	public Fields getOutputFields() {
		return fieldManupulatingHandler.getOutputFields();
	}

	public Fields getInputFields() {
		return fieldManupulatingHandler.getInputFields();
	}

	public Fields getKeyFields() {
		return fieldManupulatingHandler.keyFields;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(FlowProcess flowProcess, OperationCall<CustomHandlerContext<AggregateTransformBase>> call) {

		Object[] accumulatorValues = new Object[initialValues.length];
		CustomHandlerContext<AggregateTransformBase> context = new CustomHandlerContext<AggregateTransformBase>(
				fieldManupulatingHandler, transformClassNames, expressionList,
				initialValues, accumulatorValues);
		
		int counter = -1;
		for (AggregateTransformBase transformInstance : context
				.getTransformInstances()) {
			counter = counter + 1;
			if (transformInstance != null) {
				LOG.trace("calling prepare method of: "
						+ transformInstance.getClass().getName());
				
				if (transformInstance instanceof AggregateForExpression) {
					ExpressionWrapper expressionWrapper=new ExpressionWrapper(context.getSingleExpressionInstances(), "");
					((AggregateForExpression) transformInstance)
							.setValidationAPI(expressionWrapper);
					((AggregateForExpression) transformInstance).callPrepare();
				}
				
				try {
					transformInstance
							.prepare(
									props.get(counter),
									context.getInputRow(counter)
											.getFieldNames(),
									context.getOutputRow(counter)
											.getFieldNames(),
									ReusableRowHelper
											.getListFromFields(fieldManupulatingHandler.keyFields));

				} catch (Exception e) {
					LOG.error(
							"Exception in prepare method of: "
									+ transformInstance.getClass().getName()
									+ ".\nArguments passed to prepare() method are: \nProperties: "
									+ props
									+ "\nInput Fields: "
									+ Arrays.toString(context
											.getInputRow(counter)
											.getFieldNames().toArray())
									+ "\nOutput Fields: "
									+ Arrays.toString(context
											.getOutputRow(counter)
											.getFieldNames().toArray())
									+ "\nKey Fields: "
									+ Arrays.toString(ReusableRowHelper
											.getListFromFields(
													fieldManupulatingHandler.keyFields)
											.toArray()), e);
					throw new RuntimeException(
							"Exception in prepare method of: "
									+ transformInstance.getClass().getName()
									+ ".\nArguments passed to prepare() method are: \nProperties: "
									+ props
									+ Arrays.toString(context
											.getInputRow(counter)
											.getFieldNames().toArray())
									+ "\nOutput Fields: "
									+ Arrays.toString(context
											.getOutputRow(counter)
											.getFieldNames().toArray())
									+ "\nKey Fields: "
									+ Arrays.toString(ReusableRowHelper
											.getListFromFields(
													fieldManupulatingHandler.keyFields)
											.toArray()), e);
				}
			} 
		}

		call.setContext(context);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void cleanup(FlowProcess flowProcess, OperationCall<CustomHandlerContext<AggregateTransformBase>> call) {

		CustomHandlerContext<AggregateTransformBase> context = call.getContext();

		for (AggregateTransformBase transformInstance : context.getTransformInstances()) {
			if (transformInstance != null) {
				LOG.trace("calling cleanup method of: "
						+ transformInstance.getClass().getName());
				transformInstance.cleanup();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void aggregate(FlowProcess flowProcess, AggregatorCall<CustomHandlerContext<AggregateTransformBase>> call) {
		CustomHandlerContext<AggregateTransformBase> context = call.getContext();
		int counter = -1;

		for (AggregateTransformBase transformInstance : context.getTransformInstances()) {
			counter = counter + 1;
			if (transformInstance != null) {
				LOG.trace("calling aggregate method of: "
						+ transformInstance.getClass().getName());
				ReusableRow reusableRow = ReusableRowHelper.extractFromTuple(
						fieldManupulatingHandler.getInputPositions(counter),
						call.getArguments().getTuple(),
						context.getInputRow(counter));
//				if (transformInstance instanceof AggregateForExpression) {
//					((AggregateForExpression) transformInstance)
//							.setCounter(counter);
//				}
				try {
					transformInstance.aggregate(reusableRow);
				} catch (Exception e) {
					LOG.error("Exception in aggregate method of: "
							+ transformInstance.getClass().getName()
							+ ".\nRow being processed: " + call.getArguments(),
							e);
					throw new RuntimeException(
							"Exception in aggregate method of: "
									+ transformInstance.getClass().getName()
									+ ".\nRow being processed: "
									+ call.getArguments(), e);
				}
			} 
		}

		call.getContext().setUserObject(call.getArguments().getTuple());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void complete(FlowProcess flowProcess, AggregatorCall<CustomHandlerContext<AggregateTransformBase>> call) {
		CustomHandlerContext<AggregateTransformBase> context = call.getContext();

		// call on group complete to gather results for earlier group
		int counter = -1;
		for (AggregateTransformBase transformInstance : context.getTransformInstances()) {
			counter = counter + 1;
			if (transformInstance != null) {
				LOG.trace("calling onCompleteGroup method of: "
						+ transformInstance.getClass().getName());
				transformInstance
						.onCompleteGroup(context.getOutputRow(counter));
			}	 
		}

		// set output tuple entry with map field values
		TupleHelper.setTupleOnPositions(fieldManupulatingHandler.getMapSourceFieldPositions(),
				(Tuple) call.getContext().getUserObject(), fieldManupulatingHandler.getMapTargetFieldPositions(),
				call.getContext().getOutputTupleEntry().getTuple());

		// set output tuple entry with passthrough field values
		TupleHelper.setTupleOnPositions(fieldManupulatingHandler.getInputPassThroughPositions(),
				(Tuple) call.getContext().getUserObject(), fieldManupulatingHandler.getOutputPassThroughPositions(),
				call.getContext().getOutputTupleEntry().getTuple());

		// set output tuple entry with operation output Fields
		ReusableRowHelper.setTupleEntryFromResuableRowsAndReset(call.getContext().getOutputTupleEntry(),
				context.getAllOutputRow(), fieldManupulatingHandler.getAllOutputPositions());

		// add output to collector
		call.getOutputCollector().add(call.getContext().getOutputTupleEntry());

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void start(FlowProcess flowProcess, AggregatorCall<CustomHandlerContext<AggregateTransformBase>> call) {

		// not required

	}

	@Property(name = "Operation Classes", visibility = Visibility.PUBLIC)
	@PropertyDescription(value = "Aggregate Operations executed by this Component")
	public String[] getOperationClasses() {

		String[] classes = new String[transformClassNames.size()];
		classes = transformClassNames.toArray(classes);
		return classes;
	}

}
