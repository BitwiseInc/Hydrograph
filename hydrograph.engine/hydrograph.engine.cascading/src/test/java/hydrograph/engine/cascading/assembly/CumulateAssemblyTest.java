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
import hydrograph.engine.cascading.assembly.CumulateAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.CumulateEntity;
import hydrograph.engine.core.component.entity.elements.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CumulateAssemblyTest {
	@Before
	public void setup() {
		// TODO: setup related things go here
	}

	/**
	 * Test aggregate component's with simple count operation
	 */
	@Test
	public void CumulateCountOfResults() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3", "col4"))
				.addTuple("C1R1", "C2R1", "C3Rx", "C4R1").addTuple("C1R1", "C2R2", "C3Rx", "C4R2")
				.addTuple("C1R1", "C2R3", "C3Rx", "C4R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		CumulateEntity cumulateEntity = new CumulateEntity();
		cumulateEntity.setComponentId("CumulateTest");
		KeyField keyField = new KeyField();
		keyField.setName("col1");
		keyField.setSortOrder("asc");
		cumulateEntity.setKeyFields(new KeyField[] { keyField });

		ArrayList<Operation> operationList = new ArrayList<Operation>();

		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationInputFields(new String[] { "col2" });
		operation.setOperationOutputFields(new String[] { "count" });
		operation.setOperationClass("hydrograph.engine.transformation.userfunctions.cumulate.Count");
		operation.setOperationProperties(new Properties());
		operationList.add(operation);

		cumulateEntity.setOperationsList(operationList);

		cumulateEntity.setNumOperations(1);
		cumulateEntity.setOperationPresent(true);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("col4", "col4_new", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col3", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set Operation Field
		List<OperationField> operationFieldsList = new ArrayList<>();
		OperationField operationField = new OperationField("count", "operation1");
		operationFieldsList.add(operationField);
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		cumulateEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		CumulateAssembly aggr = new CumulateAssembly(cumulateEntity, parameters);

		// create bucket for the aggr sub assembly
		Bucket bucket = plunger.newBucket(new Fields("count", "col4_new", "col3"), aggr);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(3));
		assertThat(actual.get(0), is(new Tuple(Long.valueOf("1"), "C4R1", "C3Rx")));
		assertThat(actual.get(1), is(new Tuple(Long.valueOf("2"), "C4R2", "C3Rx")));
		assertThat(actual.get(2), is(new Tuple(Long.valueOf("3"), "C4R3", "C3Rx")));
	}

	/**
	 * Test aggregate component's with simple count operation and map fields
	 */
	@Test
	public void itShouldCumulateAndDoCountAndMapFields() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3Rx")
				.addTuple("C1R1", "C2R2", "C3Rx").addTuple("C1R1", "C2R3", "C3Rx").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		CumulateEntity cumulateEntity = new CumulateEntity();
		cumulateEntity.setComponentId("CumulateTest");
		KeyField keyField = new KeyField();
		keyField.setName("col1");
		keyField.setSortOrder("asc");
		cumulateEntity.setKeyFields(new KeyField[] { keyField });

		ArrayList<Operation> operationList = new ArrayList<Operation>();

		Operation operation = new Operation();
		operation.setOperationId("operationName1");
		operation.setOperationInputFields(new String[] { "col2" });
		operation.setOperationOutputFields(new String[] { "count" });
		operation.setOperationClass("hydrograph.engine.transformation.userfunctions.cumulate.Count");
		operation.setOperationProperties(new Properties());
		operationList.add(operation);

		cumulateEntity.setOperationsList(operationList);

		cumulateEntity.setNumOperations(1);
		cumulateEntity.setOperationPresent(true);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("col3", "col3_new", "in"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set Operation Field
		List<OperationField> operationFieldList = new ArrayList<>();
		OperationField operationField = new OperationField("count", "operationName1");
		operationFieldList.add(operationField);
		outSocket1.setOperationFieldList(operationFieldList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		cumulateEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		CumulateAssembly aggr = new CumulateAssembly(cumulateEntity, parameters);

		// create bucket for the aggr sub assembly
		Bucket bucket = plunger.newBucket(new Fields("count", "col3_new"), aggr);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(3));
		assertThat(actual.get(0), is(new Tuple(Long.valueOf("1"), "C3Rx")));
		assertThat(actual.get(1), is(new Tuple(Long.valueOf("2"), "C3Rx")));
		assertThat(actual.get(2), is(new Tuple(Long.valueOf("3"), "C3Rx")));
	}

	// @Test(expected=cascading.tuple.TupleException.class)
	@Test
	public void itShouldCumulateAndDoCountAndMapFieldsWithWildCardPassthroughFields() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3Rx")
				.addTuple("C1R1", "C2R2", "C3Rx").addTuple("C1R1", "C2R3", "C3Rx").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		CumulateEntity cumulateEntity = new CumulateEntity();
		cumulateEntity.setComponentId("CumulateTest");
		KeyField keyField = new KeyField();
		keyField.setName("col1");
		keyField.setSortOrder("asc");
		cumulateEntity.setKeyFields(new KeyField[] { keyField });

		ArrayList<Operation> operationList = new ArrayList<Operation>();

		Operation operation = new Operation();
		operation.setOperationId("operationName1");
		operation.setOperationInputFields(new String[] { "col2" });
		operation.setOperationOutputFields(new String[] { "count" });
		operation.setOperationClass("hydrograph.engine.transformation.userfunctions.cumulate.Count");
		operation.setOperationProperties(new Properties());
		operationList.add(operation);

		cumulateEntity.setOperationsList(operationList);

		cumulateEntity.setNumOperations(1);
		cumulateEntity.setOperationPresent(true);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("col3", "col3_new", "in"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("*", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set Operation Field
		List<OperationField> operationFieldList = new ArrayList<>();
		OperationField operationField = new OperationField("count", "operationName1");
		operationFieldList.add(operationField);
		outSocket1.setOperationFieldList(operationFieldList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		cumulateEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		CumulateAssembly aggr = new CumulateAssembly(cumulateEntity, parameters);

		// create bucket for the aggr sub assembly
		Bucket bucket = plunger.newBucket(new Fields("count", "col3_new", "col1", "col2", "col3"), aggr);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(3));

		assertThat(actual.get(0), is(new Tuple(Long.valueOf("1"), "C3Rx", "C1R1", "C2R1", "C3Rx")));

	}
	
	@Test
	public void itShouldCumulateAndCountWithWildCardPassthroughFieldsWithPriority() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3","count")).addTuple("C1R1", "C2R1", "C3Rx",1)
				.addTuple("C1R1", "C2R2", "C3Rx",1).addTuple("C1R1", "C2R3", "C3Rx",1).build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		CumulateEntity cumulateEntity = new CumulateEntity();
		cumulateEntity.setComponentId("CumulateTest");
		KeyField keyField = new KeyField();
		keyField.setName("col1");
		keyField.setSortOrder("asc");
		cumulateEntity.setKeyFields(new KeyField[] { keyField });

		ArrayList<Operation> operationList = new ArrayList<Operation>();

		Operation operation = new Operation();
		operation.setOperationId("operationName1");
		operation.setOperationInputFields(new String[] { "col2" });
		operation.setOperationOutputFields(new String[] { "count" });
		operation.setOperationClass("hydrograph.engine.transformation.userfunctions.cumulate.Count");
		operation.setOperationProperties(new Properties());
		operationList.add(operation);

		cumulateEntity.setOperationsList(operationList);

		cumulateEntity.setNumOperations(1);
		cumulateEntity.setOperationPresent(true);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("col3", "col3_new", "in"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("*", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set Operation Field
		List<OperationField> operationFieldList = new ArrayList<>();
		OperationField operationField = new OperationField("count", "operationName1");
		operationFieldList.add(operationField);
		outSocket1.setOperationFieldList(operationFieldList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		cumulateEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3","count"));

		CumulateAssembly aggr = new CumulateAssembly(cumulateEntity, parameters);

		// create bucket for the aggr sub assembly
		Bucket bucket = plunger.newBucket(new Fields("count", "col3_new", "col1", "col2", "col3"), aggr);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(3));

		assertThat(actual.get(0), is(new Tuple(Long.valueOf("1"), "C3Rx", "C1R1", "C2R1", "C3Rx")));
		assertThat(actual.get(2), is(new Tuple(Long.valueOf("3"), "C3Rx", "C1R1", "C2R3", "C3Rx")));

	}

}
