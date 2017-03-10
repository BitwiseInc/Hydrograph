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
import hydrograph.engine.cascading.assembly.NormalizeAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.NormalizeEntity;
import hydrograph.engine.core.component.entity.elements.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test normalize sub assembly. The tests are written using plunger framework
 * 
 * @author Prabodh
 */
public class NormalizeAssemblyTest {

	@Before
	public void setup() {
		// TODO: add setup related code here
	}

	/**
	 * A simple unit test to test working of normalize component. The unit test
	 * passes denormalized (vector) records to component and checks for
	 * normalized output.
	 * 
	 * @throws IOException
	 */
	@Test
	public void TestNormalizeRecords() throws IOException {
		Plunger plunger = new Plunger();

		Fields fields = new Fields("id", "name", "marks1", "marks2", "marks3");
		Data file1 = new DataBuilder(fields).addTuple(1, "John", 94, 56, 84).addTuple(2, "Mary", 54, 89, 74).build();

		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// normalize
															// component
		ArrayList<Fields>fieldList = new ArrayList<Fields>();
		fieldList.add(fields);
		ComponentParameters parameters = new ComponentParameters();
		parameters.setInputFieldsList(fieldList);
		parameters.addInputPipe(pipe1);

		NormalizeEntity normalizeEnitity = new NormalizeEntity();
		normalizeEnitity.setComponentId("normalizeTest");

		ArrayList<Operation> operationList = new ArrayList<Operation>();

		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationInputFields(new String[] { "marks1", "marks2", "marks3" });
		operation.setOperationOutputFields(new String[] { "marks" });
		Properties props = new Properties();
		props.setProperty("VectorSize", "3");
		operation.setOperationProperties(props);
		operation.setOperationClass("hydrograph.engine.cascading.test.customtransformclasses.NormalizeCustomTransform");
		operationList.add(operation);

		normalizeEnitity.setOperationsList(operationList);

		normalizeEnitity.setNumOperations(1);
		normalizeEnitity.setOperationPresent(true);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("name", "name_new", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("id", "in0"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set Operation Field
		List<OperationField> operationFieldsList = new ArrayList<>();
		OperationField operationField = new OperationField("marks", "operation1");
		operationFieldsList.add(operationField);
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		normalizeEnitity.setOutSocketList(outSocketList);

		NormalizeAssembly normalize = new NormalizeAssembly(normalizeEnitity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("marks", "id", "name_new"), normalize); // create
																								// bucket
																								// for
																								// the
																								// normalize
																								// sub
																								// assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(6));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple(94, "John", 1));
		expectedOutput.add(new Tuple(56, "John", 1));
		expectedOutput.add(new Tuple(84, "John", 1));
		expectedOutput.add(new Tuple(54, "Mary", 2));
		expectedOutput.add(new Tuple(89, "Mary", 2));
		expectedOutput.add(new Tuple(74, "Mary", 2));

		Assert.assertEquals(expectedOutput, output);
	}

	@Test
	public void TestNormalizeForReuseableRowChanges() throws IOException {
		Plunger plunger = new Plunger();

		Fields fields = new Fields("id", "foo", "s1", "s2", "s3");
		Data file1 = new DataBuilder(fields).addTuple(1, 1, "A", "B", "C").addTuple(2, 0, "A", "B", "C")
				.addTuple(3, 0, "A", "B", "C").addTuple(4, 1, "A", "B", "C").addTuple(5, 1, "A", "B", "C")
				.addTuple(6, 0, "A", "B", "C").addTuple(7, 1, "A", "B", "C").build();

		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// normalize
															// component
		ArrayList<Fields>fieldList = new ArrayList<Fields>();
		fieldList.add(fields);
		ComponentParameters parameters = new ComponentParameters();
		parameters.setInputFieldsList(fieldList);
		parameters.addInputPipe(pipe1);

		NormalizeEntity normalizeEnitity = new NormalizeEntity();
		normalizeEnitity.setComponentId("normalizeTest");

		ArrayList<Operation> operationList = new ArrayList<Operation>();

		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationInputFields(new String[] { "foo", "s1", "s2", "s3" });
		operation.setOperationOutputFields(new String[] { "string" });
		Properties props = new Properties();
		props.setProperty("VectorSize", "3");
		operation.setOperationProperties(props);
		operation.setOperationClass("hydrograph.engine.cascading.test.customtransformclasses.NormalizeTransform");
		operationList.add(operation);

		normalizeEnitity.setOperationsList(operationList);

		normalizeEnitity.setNumOperations(1);
		normalizeEnitity.setOperationPresent(true);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("foo", "foo_new", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("id", "in0"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set Operation Field
		List<OperationField> operationFieldsList = new ArrayList<>();
		OperationField operationField = new OperationField("string", "operation1");
		operationFieldsList.add(operationField);
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		normalizeEnitity.setOutSocketList(outSocketList);

		NormalizeAssembly normalize = new NormalizeAssembly(normalizeEnitity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("id", "foo_new", "string"), normalize); // create
																								// bucket
																								// for
																								// the
																								// normalize
																								// sub
																								// assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket
		assertThat(actual.size(), is(12));

		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("A", 1, 1));
		expectedOutput.add(new Tuple("B", 1, 1));
		expectedOutput.add(new Tuple("C", 1, 1));
		expectedOutput.add(new Tuple("A", 1, 4));
		expectedOutput.add(new Tuple("B", 1, 4));
		expectedOutput.add(new Tuple("C", 1, 4));
		expectedOutput.add(new Tuple("A", 1, 5));
		expectedOutput.add(new Tuple("B", 1, 5));
		expectedOutput.add(new Tuple("C", 1, 5));
		expectedOutput.add(new Tuple("A", 1, 7));
		expectedOutput.add(new Tuple("B", 1, 7));
		expectedOutput.add(new Tuple("C", 1, 7));

		Assert.assertEquals(expectedOutput, output);
	}

	@Test
	public void TestNormalizeRecordsWithWildCardPassthroughFields() throws IOException {
		Plunger plunger = new Plunger();

		Fields fields = new Fields("id", "names", "acc_no", "city");
		Data file1 = new DataBuilder(fields).addTuple(1, "John and Smith", 1001, "AAA")
				.addTuple(2, "Mary and Bose", 1154, "BBB").build();

		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// normalize
															// component

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("id", "names", "acc_no", "city"));
		NormalizeEntity normalizeEnitity = new NormalizeEntity();
		normalizeEnitity.setComponentId("normalizeTest");

		ArrayList<Operation> operationList = new ArrayList<Operation>();

		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationInputFields(new String[] { "names" });
		operation.setOperationOutputFields(new String[] { "name" });
		Properties props = new Properties();
		props.setProperty("regex", " and ");
		operation.setOperationProperties(props);
		operation.setOperationClass("hydrograph.engine.transformation.userfunctions.normalize.RegexSplitNormalize");
		operationList.add(operation);
		operation.setOperationOutputFields(new String[] { "name" });

		normalizeEnitity.setOperationsList(operationList);

		normalizeEnitity.setNumOperations(1);
		normalizeEnitity.setOperationPresent(true);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("acc_no", "new_acc", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("*", "in0"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set Operation Field
		List<OperationField> operationFieldsList = new ArrayList<>();
		OperationField operationField = new OperationField("name", "operation1");
		operationFieldsList.add(operationField);
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		normalizeEnitity.setOutSocketList(outSocketList);

		NormalizeAssembly normalize = new NormalizeAssembly(normalizeEnitity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("name", "new_acc", "id", "names", "acc_no", "city"), normalize); // create
		// bucket
		// for the
		// normalize
		// sub
		// assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(4));

		assertTrue(actual.get(0).equals(new Tuple("John", 1001, 1, "John and Smith", 1001, "AAA")));
		assertTrue(actual.get(1).equals(new Tuple("Smith", 1001, 1, "John and Smith", 1001, "AAA")));
		assertTrue(actual.get(2).equals(new Tuple("Mary", 1154, 2, "Mary and Bose", 1154, "BBB")));
		assertTrue(actual.get(3).equals(new Tuple("Bose", 1154, 2, "Mary and Bose", 1154, "BBB")));

	}

	@After
	public void cleanup() {
		// TODO: add cleanup related code here
	}
}
