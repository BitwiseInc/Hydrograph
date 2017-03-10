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
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import hydrograph.engine.cascading.assembly.context.UniqueSequenceNumberContext;

public class UniqueSequenceNumberOperation extends
		BaseOperation<UniqueSequenceNumberContext> implements
		Function<UniqueSequenceNumberContext> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5227523422113200964L;

	public UniqueSequenceNumberOperation(Fields newFields) {

		super(0, newFields);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(FlowProcess flowProcess,
			OperationCall<UniqueSequenceNumberContext> operationCall) {
		long counter = 0;
		UniqueSequenceNumberContext context = new UniqueSequenceNumberContext();
		
		context.setCounter(counter);
		context.setNumberOfSlices(flowProcess.getNumProcessSlices());
		context.setCurrentSliceNo(flowProcess.getCurrentSliceNum()+1);
		
		operationCall.setContext(context);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void operate(FlowProcess flowProcess,
			FunctionCall<UniqueSequenceNumberContext> functionCall) {
		// get the arguments TupleEntry
		TupleEntry arguments = functionCall.getArguments();
		UniqueSequenceNumberContext context = functionCall.getContext();

		// create a Tuple to hold our result values
		Tuple result = new Tuple();
		long counter = context.getCounter();
		int noOfSlices = context.getNoOfSlices();
		int currentSliceNo = context.getCurrentSliceNo();

		result.add(counter * noOfSlices + currentSliceNo);

		counter = counter + 1;
		context.setCounter(counter);
		Tuple outputTuple = arguments.getTuple().append(result);

		// return the result Tuple
		functionCall.getOutputCollector().add(outputTuple);
	}

}
