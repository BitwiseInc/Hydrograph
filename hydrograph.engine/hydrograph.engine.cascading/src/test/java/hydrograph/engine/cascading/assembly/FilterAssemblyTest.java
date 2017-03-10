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
import hydrograph.engine.cascading.assembly.FilterAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.FilterEntity;
import hydrograph.engine.core.component.entity.elements.Operation;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FilterAssemblyTest {

	@Test
	public void FilterWithOnlyOutPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R2").build();

		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// filter component

		Properties operationProperties = new Properties();
		operationProperties.put("ABC", "xyz");

		String[] operationInputFields = { "col1" };
		String operationClass = "hydrograph.engine.cascading.test.customtransformclasses.CustomFilterOperation";

		FilterEntity filterEntity = new FilterEntity();
		filterEntity.setComponentId("filterTest");

		Operation operation = new Operation();
		operation.setOperationClass(operationClass);
		operation.setOperationInputFields(operationInputFields);
		operation.setOperationProperties(operationProperties);

		filterEntity.setOperation(operation);
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("out0","out"));
		filterEntity.setOutSocketList(outSocketList);
		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		FilterAssembly filter = new FilterAssembly(filterEntity, parameters);

		// create bucket for filter sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				filter);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));
	}

	@Test
	public void FilterWithOnlyOutPortWithOptionalProperty() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R2").build();

		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// filter component

		String[] operationInputFields = { "col1" };
		String operationClass = "hydrograph.engine.cascading.test.customtransformclasses.CustomFilterOperation";

		FilterEntity filterEntity = new FilterEntity();
		filterEntity.setComponentId("filterTest");

		Operation operation = new Operation();
		operation.setOperationClass(operationClass);
		operation.setOperationInputFields(operationInputFields);
		filterEntity.setOperation(operation);

		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("out0","out"));
		filterEntity.setOutSocketList(outSocketList);
		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		FilterAssembly filter = new FilterAssembly(filterEntity, parameters);

		// create bucket for filter sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				filter);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));
	}

	@Test
	public void FilterWithOnlyOutPortOptionalType() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R2").build();

		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// filter component

		Properties operationProperties = new Properties();
		operationProperties.put("ABC", "xyz");

		String[] operationInputFields = { "col1" };
		String operationClass = "hydrograph.engine.cascading.test.customtransformclasses.CustomFilterOperation";

		FilterEntity filterEntity = new FilterEntity();
		filterEntity.setComponentId("filterTest");

		Operation operation = new Operation();
		operation.setOperationClass(operationClass);
		operation.setOperationInputFields(operationInputFields);
		operation.setOperationProperties(operationProperties);
		filterEntity.setOperation(operation);

		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("out0","out"));
		filterEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		FilterAssembly filter = new FilterAssembly(filterEntity, parameters);

		// create bucket for filter sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				filter);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));
	}

	@Test
	public void TestFilterAssemblyWithOnlyUnusedPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("name", "address", "Fruit"))
				.addTuple("ajay", "Malad", "Apple")
				.addTuple("raja", "Andheri", "Mango").build();

		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// filter component

		Properties operationProperties = new Properties();
		operationProperties.put("ABC", "xyz");

		String[] operationInputFields = { "Fruit" };
		String operationClass = "hydrograph.engine.cascading.test.customtransformclasses.CustomFilterForFilterComponent";

		FilterEntity filterEntity = new FilterEntity();
		filterEntity.setComponentId("filterTest");

		Operation operation = new Operation();
		operation.setOperationClass(operationClass);
		operation.setOperationInputFields(operationInputFields);
		operation.setOperationProperties(operationProperties);
		filterEntity.setOperation(operation);

		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("id1", "unused"));
		filterEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("name", "address", "Fruit"));

		FilterAssembly filter = new FilterAssembly(filterEntity, parameters);

		// create bucket for filter sub assembly
		Bucket bucket = plunger.newBucket(
				new Fields("name", "address", "Fruit"),
				filter.getOutLink("unused", "id1",
						filterEntity.getComponentId()));

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(1));
		assertThat(actual.get(0), is(new Tuple("ajay", "Malad", "Apple")));
	}

	@Test
	public void TestFilterAssemblyWithOutAndUnusedPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("name", "address", "Fruit"))
				.addTuple("ajay", "Malad", "Apple")
				.addTuple("raja", "Andheri", "Mango").build();

		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// filter component
		
		Properties operationProperties = new Properties();
		operationProperties.put("ABC", "xyz");

		String[] operationInputFields = { "Fruit" };
		String operationClass = "hydrograph.engine.cascading.test.customtransformclasses.CustomFilterForFilterComponent";

		FilterEntity filterEntity = new FilterEntity();

		filterEntity.setComponentId("filterTest");

		Operation operation = new Operation();
		operation.setOperationClass(operationClass);
		operation.setOperationInputFields(operationInputFields);
		operation.setOperationProperties(operationProperties);
		filterEntity.setOperation(operation);

		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("id1", "unused"));
		outSocketList.add(new OutSocket("out1","out"));
		filterEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("name", "address", "Fruit"));

		FilterAssembly filter = new FilterAssembly(filterEntity, parameters);

		Bucket bucket_Out = plunger.newBucket(new Fields("name", "address",
				"Fruit"), filter.getOutLink("out", "out1",
				filterEntity.getComponentId()));

		// create bucket for filter sub assembly
		Bucket bucket_unused = plunger.newBucket(new Fields("name", "address",
				"Fruit"), filter.getOutLink("unused", "id1",
				filterEntity.getComponentId()));

		// get results from bucket
		List<Tuple> actualUnused = bucket_unused.result().asTupleList();

		// get results from bucket
		List<Tuple> actualOut = bucket_Out.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actualUnused.size(), is(1));
		assertThat(actualUnused.get(0), is(new Tuple("ajay", "Malad", "Apple")));

		// assert the actual results with expected results
		assertThat(actualOut.size(), is(1));
		assertThat(actualOut.get(0), is(new Tuple("raja", "Andheri", "Mango")));

	}
}
