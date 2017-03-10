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
package hydrograph.engine.cascading.functions;

import cascading.flow.FlowProcess;
import cascading.operation.BaseOperation;
import cascading.operation.Function;
import cascading.operation.FunctionCall;
import cascading.operation.OperationCall;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.context.CustomFunctionContext;
import hydrograph.engine.cascading.utilities.TupleHelper;

public class CopyFields extends BaseOperation<CustomFunctionContext> implements
		Function<CustomFunctionContext> {

	private static final long serialVersionUID = -5068171535610384444L;

	private Fields outputFields;
	private int[] inputPos;

	public CopyFields(Fields requiredFields) {
		super(requiredFields.size(), requiredFields);
		outputFields = requiredFields;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(FlowProcess flowProcess,
			OperationCall<CustomFunctionContext> operationCall) {

		CustomFunctionContext context = new CustomFunctionContext(outputFields);
		operationCall.setContext(context);

		// get input fields and their positions
		Fields inputFields = operationCall.getArgumentFields();
		inputPos = inputFields.getPos(outputFields);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void operate(FlowProcess arg0,
			FunctionCall<CustomFunctionContext> call) {

		// call.getContext().getOutputTupleEntry().set(call.getArguments());

		TupleHelper.setTuplePart(inputPos, call.getArguments().getTuple(), call
				.getContext().getOutputTupleEntry().getTuple());

		call.getOutputCollector().add(
				call.getContext().getOutputTupleEntry().getTuple());

	}

}
