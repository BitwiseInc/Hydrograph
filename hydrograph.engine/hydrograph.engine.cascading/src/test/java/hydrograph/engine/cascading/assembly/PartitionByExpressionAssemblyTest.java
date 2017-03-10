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

import cascading.pipe.Pipe;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import com.hotels.plunger.Bucket;
import com.hotels.plunger.Data;
import com.hotels.plunger.DataBuilder;
import com.hotels.plunger.Plunger;
import hydrograph.engine.cascading.assembly.PartitionByExpressionAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.PartitionByExpressionEntity;
import hydrograph.engine.core.component.entity.elements.Operation;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PartitionByExpressionAssemblyTest {

	@Test
	public void PartitionByExpressionTestOutPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("name", "accountType", "address")).addTuple("AAA", "debit", "Malad")
				.addTuple("BBB", "credit", "Kandivali").addTuple("CCC", "mix", "Borivali").build();

		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// partitionByExpression
															// component

		Properties operationProperties = new Properties();
		operationProperties.put("ABC", "xyz");

		String[] operationInputFields = { "accountType" };
		String operationClass = "hydrograph.engine.cascading.test.customtransformclasses.PartitionExpressionTransaction";

		PartitionByExpressionEntity partitionByExpressionEntity = new PartitionByExpressionEntity();
		partitionByExpressionEntity.setComponentId("partitionByExpressionTest");

		Operation operation = new Operation();
		operation.setOperationClass(operationClass);
		operation.setOperationInputFields(operationInputFields);
		operation.setOperationProperties(operationProperties);

		partitionByExpressionEntity.setOperation(operation);
		partitionByExpressionEntity.setNumPartitions(3);

		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("out_credit", "out"));
		outSocketList.add(new OutSocket("out_debit", "out"));
		outSocketList.add(new OutSocket("out_mix", "out"));

		partitionByExpressionEntity.setOutSocketList(outSocketList);
		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("name", "accountType", "address"));

		PartitionByExpressionAssembly partitionByExpressionAssembly = new PartitionByExpressionAssembly(
				partitionByExpressionEntity, parameters);

		// create bucket for filter sub assembly
		Bucket bucket1 = plunger.newBucket(new Fields("name", "accountType", "address"), partitionByExpressionAssembly
				.getOutLink("out", "out_credit", partitionByExpressionEntity.getComponentId()));

		// create bucket for filter sub assembly
		Bucket bucket2 = plunger.newBucket(new Fields("name", "accountType", "address"), partitionByExpressionAssembly
				.getOutLink("out", "out_debit", partitionByExpressionEntity.getComponentId()));

		// create bucket for filter sub assembly
		Bucket bucket3 = plunger.newBucket(new Fields("name", "accountType", "address"), partitionByExpressionAssembly
				.getOutLink("out", "out_mix", partitionByExpressionEntity.getComponentId()));

		List<Tuple> actual1 = bucket1.result().asTupleList(); // get results
																// from

		// assert the actual results with expected results
		assertThat(actual1.size(), is(1));
		assertThat(actual1.get(0), is(new Tuple("BBB", "credit", "Kandivali")));

		List<Tuple> actual2 = bucket2.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual2.size(), is(1));
		assertThat(actual2.get(0), is(new Tuple("AAA", "debit", "Malad")));

		List<Tuple> actual3 = bucket3.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual3.size(), is(1));
		assertThat(actual3.get(0), is(new Tuple("CCC", "mix", "Borivali")));
	}

}
