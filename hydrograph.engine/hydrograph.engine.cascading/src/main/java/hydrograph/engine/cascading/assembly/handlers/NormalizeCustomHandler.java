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

import hydrograph.engine.cascading.assembly.context.CascadingReusableRow;
import hydrograph.engine.cascading.assembly.context.CustomHandlerContext;
import hydrograph.engine.cascading.utilities.ReusableRowHelper;
import hydrograph.engine.cascading.utilities.TupleHelper;
import hydrograph.engine.expression.api.ValidationAPI;
import hydrograph.engine.expression.userfunctions.NormalizeForExpression;
import hydrograph.engine.transformation.userfunctions.base.NormalizeTransformBase;
import hydrograph.engine.transformation.userfunctions.base.OutputDispatcher;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.FlowProcess;
import cascading.management.annotation.Property;
import cascading.management.annotation.PropertyDescription;
import cascading.management.annotation.Visibility;
import cascading.operation.BaseOperation;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.operation.OperationCall;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntry;

public class NormalizeCustomHandler extends
		BaseOperation<CustomHandlerContext<NormalizeTransformBase>> implements
		Function<CustomHandlerContext<NormalizeTransformBase>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 155032640399766097L;
	private ArrayList<Properties> userProperties;
	private ArrayList<String> transformClassName;
	private ArrayList<ValidationAPI> expressionList;
	private FieldManupulatingHandler fieldManupulatingHandler;
	private String exprCount;
	private static Logger LOG = LoggerFactory
			.getLogger(NormalizeCustomHandler.class);

	public NormalizeCustomHandler(
			FieldManupulatingHandler fieldManupulatingHandler,
			Properties userProperties, String transformClassName) {

		super(fieldManupulatingHandler.getInputFields().size(),
				fieldManupulatingHandler.getOutputFields());

		this.userProperties = new ArrayList<>();
		this.userProperties.add(userProperties);

		this.transformClassName = new ArrayList<>();
		this.transformClassName.add(transformClassName);

		this.fieldManupulatingHandler = fieldManupulatingHandler;
		LOG.trace("NormalizeCustomHandler object created for: "
				+ transformClassName);
	}

	public NormalizeCustomHandler(
			FieldManupulatingHandler fieldManupulatingHandler,
			ArrayList<Properties> props, ArrayList<String> transformClassNames,
			ArrayList<ValidationAPI> expressionObjectList, String exprCount) {
		super(fieldManupulatingHandler.getInputFields().size(),
				fieldManupulatingHandler.getOutputFields());
		this.userProperties = props;
		this.transformClassName = transformClassNames;
		this.expressionList = expressionObjectList;
		this.fieldManupulatingHandler = fieldManupulatingHandler;
		this.exprCount = exprCount;
		LOG.trace("AggregateCustomHandler object created for: "
				+ Arrays.toString(transformClassNames.toArray()));
	}

	public Fields getInputFields() {
		return fieldManupulatingHandler.getInputFields();
	}

	public Fields getOutputFields() {
		return fieldManupulatingHandler.getOutputFields();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(
			FlowProcess flowProcess,
			OperationCall<CustomHandlerContext<NormalizeTransformBase>> operationCall) {

		CustomHandlerContext<NormalizeTransformBase> context = new CustomHandlerContext<NormalizeTransformBase>(
			fieldManupulatingHandler, transformClassName,expressionList, exprCount);
		
		if (fieldManupulatingHandler.getOperationOutputFields() != null) {
			Fields outputFields = new Fields();
			for (Fields fields : fieldManupulatingHandler.getOperationOutputFields()) {
				outputFields = outputFields.append(fields);
			}
			context.setOutputRow(new CascadingReusableRow(ReusableRowHelper.getLinkedSetFromFields(outputFields)));
		}

		context.setUserObject(new NormalizeOutputDispatcher(operationCall));

		if (context.getSingleTransformInstance() != null) {
			LOG.trace("calling prepare method of: "
					+ context.getSingleTransformInstance().getClass().getName());
			try {
				context.getSingleTransformInstance().prepare(
						userProperties.get(0));
			} catch (Exception e) {
				LOG.error(
						"Exception in prepare method of: "
								+ context.getSingleTransformInstance()
										.getClass().getName()
								+ ".\nArguments passed to prepare() method are: \nProperties: "
								+ userProperties, e);
				throw new RuntimeException(
						"Exception in prepare method of: "
								+ context.getSingleTransformInstance()
										.getClass().getName()
								+ ".\nArguments passed to prepare() method are: \nProperties: "
								+ userProperties, e);
			}
		} 
		if (context.getTransformInstance(0) instanceof NormalizeForExpression)
			fieldManupulatingHandler
					.setAllOutputPositions(getAllOutputPositions(fieldManupulatingHandler
							.getAllOutputPositions()));
		operationCall.setContext(context);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void operate(FlowProcess arg0,
			FunctionCall<CustomHandlerContext<NormalizeTransformBase>> call) {
		CustomHandlerContext<NormalizeTransformBase> context = call
				.getContext();
		TupleEntry inTupleEntry = call.getArguments();

		NormalizeTransformBase transformInstance = context
				.getTransformInstance(0);
		if (context.getTransformInstances().get(0) != null) {
			LOG.trace("calling normalize method of: "
					+ transformInstance.getClass().getName());
			int[] inputPositions = fieldManupulatingHandler.getInputPositions();
			ReusableRow inputReusableRow = context.getSingleInputRow();
			if (transformInstance instanceof NormalizeForExpression) {
				int i = 0;
				String fieldNames[];
				Object tuples[];

				fieldNames = new String[fieldManupulatingHandler
						.getInputFields().size()];
				tuples = new Object[fieldManupulatingHandler.getInputFields()
						.size()];
				for (i = 0; i < fieldManupulatingHandler.getInputFields()
						.size(); i++) {
					fieldNames[i] = String.valueOf(fieldManupulatingHandler
							.getInputFields().get(i));
					tuples[i] = inTupleEntry.getObject(fieldManupulatingHandler
							.getInputFields().get(i));
				}
				inputPositions = extractInputPositions(fieldManupulatingHandler
						.getAllInputPositions());
				inputReusableRow = extractInputRows(context.getAllinputRow());
				
				((NormalizeForExpression) transformInstance)
				.setValidationAPI(context
						.getSingleExpressionInstances());
				((NormalizeForExpression) transformInstance)
				.setTransformInstancesSize(context.getTransformInstances().size());
				((NormalizeForExpression) transformInstance)
				.setListOfExpressions(populateListOfExpressions(context
						.getExpressionInstancesList()));
				((NormalizeForExpression) transformInstance)
				.setCountExpression(context.getExprCount());
				((NormalizeForExpression) transformInstance)
				.setOperationOutputFields(flattenSchema(populateOperationOutputFieldsForExpression()));
				
				((NormalizeForExpression) transformInstance)
						.setFieldNames(fieldNames);
				((NormalizeForExpression) transformInstance).setTuples(tuples);
			}
			try {
				transformInstance.Normalize(ReusableRowHelper.extractFromTuple(
						inputPositions, call.getArguments().getTuple(),
						inputReusableRow), context.getSingleOutputRow(),
						(NormalizeOutputDispatcher) context.getUserObject());

			} catch (Exception e) {
				LOG.error("Exception in normalize method of: "
						+ transformInstance.getClass().getName()
						+ ".\nRow being processed: " + call.getArguments(), e);
				throw new RuntimeException("Exception in normalize method of: "
						+ transformInstance.getClass().getName()
						+ ".\nRow being processed: " + call.getArguments(), e);
			}
		}
	}
	
	private ArrayList<String> flattenSchema(ArrayList<ArrayList<String>> arrayList) {
		ArrayList<String> strings = new ArrayList<String>();
		for (int i = 0; i < fieldManupulatingHandler.getOperationOutputFields().size(); i++) {
			for (int j = 0; j < fieldManupulatingHandler.getOperationOutputFields().get(i).size(); j++) {
				strings.add((String) fieldManupulatingHandler.getOperationOutputFields().get(i).get(j));
			}
		}
		return strings;
	}
	private ArrayList<int[]> getAllOutputPositions(
			ArrayList<int[]> allOutputPositions) {
		int[] outputPositions = new int[fieldManupulatingHandler
				.getAllOutputPositions().size()];
		for (int count = 0; count < fieldManupulatingHandler
				.getAllOutputPositions().size(); count++) {
			outputPositions[count] = (fieldManupulatingHandler
					.getAllOutputPositions().get(count))[0];
		}
		ArrayList<int[]> arrayList = new ArrayList<int[]>();
		arrayList.add(outputPositions);
		return arrayList;
	}

	private ReusableRow extractInputRows(ArrayList<ReusableRow> allInputRow) {
		LinkedHashSet<String> fieldNames = new LinkedHashSet<String>();
		for (ReusableRow reusableRow : allInputRow) {
			for (int i = 0; i < reusableRow.getFieldNames().size(); i++) {
				if (!fieldNames.contains(reusableRow.getFieldName(i)))
					fieldNames.add(reusableRow.getFieldName(i));
			}
		}
		return new CascadingReusableRow(fieldNames);
	}

	private int[] extractInputPositions(ArrayList<int[]> allInputPositions) {
		Set<Integer> inputPositions = new HashSet<Integer>();
		for (int[] array : allInputPositions) {
			for (int i = 0; i < array.length; i++) {
				inputPositions.add(array[i]);
			}
		}
		Integer[] ints = inputPositions.toArray(new Integer[inputPositions
				.size()]);
		int[] result = new int[ints.length];
		for (int i = 0; i < ints.length; i++) {
			result[i] = ints[i];
		}
		return result;
	}

	private ArrayList<String> populateListOfExpressions(
			ArrayList<ValidationAPI> expressionInstancesList) {
		ArrayList<String> listOfExpressions = new ArrayList<String>();
		for (ValidationAPI validationAPI : expressionInstancesList) {
			listOfExpressions.add(validationAPI.getExpr());
		}
		return listOfExpressions;
	}

	private ArrayList<ArrayList<String>> populateOperationOutputFieldsForExpression() {
		ArrayList<ArrayList<String>> listOfString = new ArrayList<ArrayList<String>>();
		ArrayList<String> strings = new ArrayList<String>();
		for(int i=0;i<fieldManupulatingHandler.getOperationOutputFields().size();i++){
			for(int j=0;j<fieldManupulatingHandler.getOperationOutputFields().get(i).size();j++){
				strings.add((String)fieldManupulatingHandler.getOperationOutputFields().get(i).get(j));
			}
			listOfString.add(strings);
		}
		return listOfString;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void cleanup(FlowProcess flowProcess,
			OperationCall<CustomHandlerContext<NormalizeTransformBase>> call) {
		CustomHandlerContext<NormalizeTransformBase> context = call
				.getContext();

		if (context.getTransformInstances().get(0) != null) {
		LOG.trace("calling cleanup method of: "
				+ context.getSingleTransformInstance().getClass().getName());
		context.getSingleTransformInstance().cleanup();
		}

	}

	@Property(name = "Operation Class", visibility = Visibility.PUBLIC)
	@PropertyDescription(value = "Normalize Operation executed by this Component")
	public ArrayList<String> getOperationClass() {

		return transformClassName;
	}

	private class NormalizeOutputDispatcher implements OutputDispatcher {

		FunctionCall<CustomHandlerContext<NormalizeTransformBase>> call;

		public NormalizeOutputDispatcher(
				OperationCall<CustomHandlerContext<NormalizeTransformBase>> call) {
			this.call = (FunctionCall<CustomHandlerContext<NormalizeTransformBase>>) call;
		}

		@Override
		public void sendOutput() {
			LOG.trace("entering sendOutput method");
			CustomHandlerContext<NormalizeTransformBase> context = call
					.getContext();

			// set output tuple entry with map field values
			TupleHelper.setTupleOnPositions(fieldManupulatingHandler
					.getMapSourceFieldPositions(), call.getArguments()
					.getTuple(), fieldManupulatingHandler
					.getMapTargetFieldPositions(), call.getContext()
					.getOutputTupleEntry().getTuple());

			// set output tuple entry with passthrough field values
			TupleHelper.setTupleOnPositions(
					fieldManupulatingHandler.getInputPassThroughPositions(),
					call.getArguments().getTuple(),
					fieldManupulatingHandler.getOutputPassThroughPositions(),
					call.getContext().getOutputTupleEntry().getTuple());

			// Set all output fields in order
			ReusableRowHelper.setTupleEntryFromResuableRowAndReset(context
					.getOutputTupleEntry(), context.getSingleOutputRow(),
					fieldManupulatingHandler.getAllOutputPositions().get(0));

			call.getOutputCollector().add(context.getOutputTupleEntry());
		}
	}
}
