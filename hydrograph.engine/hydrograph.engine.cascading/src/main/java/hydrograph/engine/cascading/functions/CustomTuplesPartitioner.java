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
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.context.CascadingReusableRow;
import hydrograph.engine.cascading.assembly.context.CustomPartitionContext;
import hydrograph.engine.cascading.utilities.ReusableRowHelper;
import hydrograph.engine.transformation.userfunctions.base.CustomPartitionExpression;
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.util.LinkedHashSet;
import java.util.Properties;

public class CustomTuplesPartitioner extends
		BaseOperation<CustomPartitionContext> implements
		Function<CustomPartitionContext> {
	private static final long serialVersionUID = 2028844531439771485L;
	private Fields fieldDeclaration;
	private Fields keyFields;
	private String currentPartitionId;


	private CustomPartitionExpression partition;
	private int numOfPartitions;
	private int[] keyFieldPos;
	private ReusableRow rrow;
	private Properties props;

	public CustomTuplesPartitioner(Fields fieldDeclaration, Fields keyFields,
			CustomPartitionExpression partition, int numOfPartitions,
			String currentPartitionId,Properties props) {
		super(fieldDeclaration.size(), fieldDeclaration);
		this.fieldDeclaration = fieldDeclaration;
		this.keyFields = keyFields;
		this.partition = partition;
		this.numOfPartitions = numOfPartitions;
		this.currentPartitionId = currentPartitionId;
		keyFieldPos = fieldDeclaration.getPos(keyFields);
		this.props=props;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(
			FlowProcess flowProcess,
			cascading.operation.OperationCall<CustomPartitionContext> operationCall) {
		operationCall
				.setContext(new CustomPartitionContext(this.getPartition()));
		operationCall.getContext().setKeyFields(this.keyFields);
		operationCall.getContext()
				.setNumOfPartitions(this.getNumOfPartitions());
		operationCall.getContext().setCurrentPartitionId(currentPartitionId);
		rrow = new CascadingReusableRow(new LinkedHashSet(operationCall.getContext().getKeyFields()));
		operationCall.getContext().getPartitionLogic().prepare(props);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void operate(FlowProcess flowProcess,
			FunctionCall<CustomPartitionContext> functionCall) {
		rrow = ReusableRowHelper.extractFromTuple(functionCall.getArguments()
				.getTuple().get(keyFieldPos), rrow);
		functionCall.getContext().setCurrentKeys(rrow);
		if (functionCall.getContext().isCurrentPipe())
			functionCall.getOutputCollector().add(functionCall.getArguments());

	}

	public String getCurrentPartitionId() {
		return currentPartitionId;
	}

	public void setCurrentPartitionId(String currentPartitionId) {
		this.currentPartitionId = currentPartitionId;
	}

	public CustomPartitionExpression getPartition() {
		return partition;
	}

	public void setPartition(CustomPartitionExpression partition) {
		this.partition = partition;
	}

	public int getNumOfPartitions() {
		return numOfPartitions;
	}

	public void setNumOfPartitions(int numOfPartitions) {
		this.numOfPartitions = numOfPartitions;
	}

}
