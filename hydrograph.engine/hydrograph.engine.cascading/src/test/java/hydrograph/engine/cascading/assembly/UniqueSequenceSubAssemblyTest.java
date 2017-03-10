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
package hydrograph.engine.cascading.assembly;

import cascading.flow.Flow;
import cascading.flow.FlowDef;
import cascading.flow.hadoop2.Hadoop2MR1FlowConnector;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.UniqueSequenceEntity;
import hydrograph.engine.core.component.entity.elements.Operation;
import hydrograph.engine.core.component.entity.elements.OperationField;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.PassThroughField;
import hydrograph.engine.utilites.AssemblyBuildHelper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static data.InputData.uniqueSequenceInput;

@SuppressWarnings("rawtypes")
public class UniqueSequenceSubAssemblyTest {
	@Test
	public void itShouldCheckComponentExecution() {

		String outPath = "testData/component/uniqSeq/output";

		FlowDef flowDef = FlowDef.flowDef();
		Fields fields = new Fields("f1", "count");

		Pipe pipes = AssemblyBuildHelper.generateInputPipes(fields, flowDef,
				uniqueSequenceInput);

		ComponentParameters params = new ComponentParameters();

		params.addInputPipe(pipes);
		params.addInputFields(fields);

		UniqueSequenceEntity generateEntity = new UniqueSequenceEntity();
		generateEntity.setComponentId("uniqueSequence");
		generateEntity.setOperationPresent(true);

		ArrayList<Operation> operationList = new ArrayList<Operation>();

		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationOutputFields(new String[] { "uniqSeq" });
		operationList.add(operation);
		generateEntity.setOperationsList(operationList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		List<OperationField> operationFieldsList = new ArrayList<>();
		OperationField operationField = new OperationField("uniqSeq",
				"operation1");
		operationFieldsList.add(operationField);
		outSocket1.setOperationFieldList(operationFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("*", "in0"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		generateEntity.setOutSocketList(outSocketList);

		UniqueSequenceAssembly uniSeqSubAssembly = new UniqueSequenceAssembly(
				generateEntity, params);

		AssemblyBuildHelper.generateOutputPipes(
				uniSeqSubAssembly.getOutLink("out", "out0",
						generateEntity.getComponentId()), outPath, flowDef);

		Flow flow = new Hadoop2MR1FlowConnector().connect(flowDef);
		flow.complete();

	}
}
