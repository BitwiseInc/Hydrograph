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
import hydrograph.engine.cascading.assembly.TransformAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.TransformEntity;
import hydrograph.engine.core.component.entity.elements.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit test class for transform sub assembly. The tests are written using
 * plunger framework
 * 
 * @author Prabodh
 *
 */
public class TransformAssemblyTest {

	/*
	 * Tests to write: 2. Wild card mapping 3. Multiple operations 4. Override
	 * field assignment, field in operation output as well as pass through 6.
	 * Map fields with similar field names in operation output fields 7. Map
	 * fields with similar field names in pass through fields
	 */

	/**
	 * Unit test with simple transform operation
	 */
	@Test
	public void simpleTransformWithOneOperationTest() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city")).addTuple(1, "  John  ", "Chicago")
				.addTuple(2, "Mary    ", "Richmond").build();

		// pipe corresponding to an input of transform component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();
		Properties userProps = new Properties();

		parameters.addInputPipe(pipe1); // first input to transform component

		parameters.addInputFields(new Fields("id", "name", "city"));

		TransformEntity transformEntity = new TransformEntity();

		// create the operation object and set the properties of operation
		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationClass("hydrograph.engine.cascading.test.customtransformclasses.SimpleTransformTest");
		operation.setOperationInputFields(new String[] { "name" });
		operation.setOperationOutputFields(new String[] { "name_trimmed" });
		operation.setOperationProperties(userProps);

		List<Operation> operationList = new ArrayList<>();
		operationList.add(operation);
		transformEntity.setOperationsList(operationList);

		transformEntity.setOperation(operation);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("id", "in"));
		passThroughFieldsList1.add(new PassThroughField("name", "in"));
		passThroughFieldsList1.add(new PassThroughField("city", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		operationFieldsList.add(new OperationField("name_trimmed", "operation1"));
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		transformEntity.setComponentId("testTransform"); // set the name of the
															// component

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(1);

		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);

		// create bucket for the transform sub assembly
		Bucket bucket = plunger.newBucket(new Fields("name_trimmed", "id", "name", "city"), transform);

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("John", 1, "  John  ", "Chicago"));
		expectedOutput.add(new Tuple("Mary", 2, "Mary    ", "Richmond"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Unit test without any transform operation, to mimic drop fields
	 * functionality
	 */

	@Test
	public void noOperationWithMapFieldsTest() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city")).addTuple(1, "John", "Chicago")
				.addTuple(2, "Mary", "Richmond").build();

		// pipe corresponding to an input of transform component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to transform component
		parameters.addInputFields(new Fields("id", "name", "city"));

		TransformEntity transformEntity = new TransformEntity();

		OutSocket outSocket1 = new OutSocket("out0");
		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("city", "new_city", "in"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("id", "in"));
		passThroughFieldsList1.add(new PassThroughField("name", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		transformEntity.setComponentId("testTransform"); // set the name of the
															// component

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(false);
		transformEntity.setNumOperations(0);

		transformEntity.setComponentId("testTransform"); // set the name of the
															// component

		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);
		// create a dummy component to be tested

		Bucket bucket = plunger.newBucket(new Fields("new_city", "id", "name"), transform);
		// create bucket for the transform sub assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("Chicago", 1, "John"));
		expectedOutput.add(new Tuple("Richmond", 2, "Mary"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Unit test for testing renaming of fields in transform component
	 */

	@Test
	public void transformComponentWithWildCardPassthroughFields() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city")).addTuple(1, "  John  ", "Chicago")
				.addTuple(2, "Mary    ", "Richmond").build();

		// pipe corresponding to an input of transform component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();
		Properties userProps = new Properties();

		parameters.addInputPipe(pipe1); // first input to transform component

		parameters.addInputFields(new Fields("id", "name", "city"));

		TransformEntity transformEntity = new TransformEntity();

		// create the operation object and set the properties of operation
		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationClass("hydrograph.engine.cascading.test.customtransformclasses.SimpleTransformTest");
		operation.setOperationInputFields(new String[] { "name" });
		operation.setOperationOutputFields(new String[] { "name_trimmed" });
		operation.setOperationProperties(userProps);

		List<Operation> operationList = new ArrayList<>();
		operationList.add(operation);
		transformEntity.setOperationsList(operationList);

		transformEntity.setOperation(operation);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("*", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		operationFieldsList.add(new OperationField("name_trimmed", "operation1"));
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		transformEntity.setComponentId("testTransform"); // set the name of the
															// component

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(1);

		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);

		// create bucket for the transform sub assembly
		Bucket bucket = plunger.newBucket(new Fields("name_trimmed", "id", "name", "city"), transform);

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("John", 1, "  John  ", "Chicago"));
		expectedOutput.add(new Tuple("Mary", 2, "Mary    ", "Richmond"));

		Assert.assertEquals(expectedOutput, output);
	}

	@Test
	public void renameFieldsTest() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city")).addTuple(1, "John", "Chicago")
				.addTuple(2, "Mary", "Richmond").build();

		// pipe corresponding to an input of transform component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();
		Properties userProps = new Properties();

		parameters.addInputPipe(pipe1); // first input to transform component

		parameters.addInputFields(new Fields("id", "name", "city"));

		TransformEntity transformEntity = new TransformEntity();

		// create the operation object and set the properties of operation
		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationClass(
				"hydrograph.engine.cascading.test.customtransformclasses.TransformTest_RenameFields");
		operation.setOperationInputFields(new String[] { "id", "name", "city" });
		operation.setOperationOutputFields(new String[] { "new.id", "new.name", "new.city" });
		operation.setOperationProperties(userProps);

		List<Operation> operationList = new ArrayList<>();
		operationList.add(operation);
		transformEntity.setOperationsList(operationList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		operationFieldsList.add(new OperationField("new.id", "operation1"));
		operationFieldsList.add(new OperationField("new.name", "operation1"));
		operationFieldsList.add(new OperationField("new.city", "operation1"));
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		transformEntity.setComponentId("testTransform"); // set the name of the
															// component

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(1);

		// set the name of the component
		transformEntity.setComponentId("testTransform");

		// create a dummy component to be tested
		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("new.id", "new.name", "new.city"), transform); // create
																									// bucket
																									// for
																									// the
																									// transform
																									// sub
																									// assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple(1, "John", "Chicago"));
		expectedOutput.add(new Tuple(2, "Mary", "Richmond"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Unit test for testing user properties in transform component
	 */

	@Test
	public void userPropertiesTest() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city")).addTuple(1, "John", "Chicago")
				.addTuple(2, "Mary", "Richmond").build();

		// pipe corresponding to an input of transform component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to transform component

		parameters.addInputFields(new Fields("id", "name", "city"));

		TransformEntity transformEntity = new TransformEntity();

		// create the operation object and set the properties of operation
		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationClass(
				"hydrograph.engine.cascading.test.customtransformclasses.TransformTest_UserProperties");
		operation.setOperationInputFields(new String[] { "name" });
		operation.setOperationOutputFields(new String[] { "name", "load_id" });

		Properties userProps = new Properties();
		userProps.put("LOAD_ID", 1);
		operation.setOperationProperties(userProps);

		List<Operation> operationList = new ArrayList<>();
		operationList.add(operation);
		transformEntity.setOperationsList(operationList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("id", "in"));
		passThroughFieldsList1.add(new PassThroughField("city", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		operationFieldsList.add(new OperationField("name", "operation1"));
		operationFieldsList.add(new OperationField("load_id", "operation1"));
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		// set the name of the component
		transformEntity.setComponentId("testTransform");

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(1);

		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("name", "load_id", "id", "city"), transform);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("John", 1, 1, "Chicago"));
		expectedOutput.add(new Tuple("Mary", 1, 2, "Richmond"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Unit test with multiple transform operations
	 */

	@Test
	public void simpleTransformWithMultipleOperationTest() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city")).addTuple(1, "  John  ", "Chicago")
				.addTuple(2, "Mary    ", "Richmond").build();

		// pipe corresponding to an input of transform component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();
		Properties userProps = new Properties();

		parameters.addInputPipe(pipe1); // first input to transform component
		parameters.addInputFields(new Fields("id", "name", "city"));

		TransformEntity transformEntity = new TransformEntity();

		// create the operation object and set the properties of operation
		Operation operation1 = new Operation();
		operation1.setOperationId("operation1");
		operation1.setOperationClass("hydrograph.engine.cascading.test.customtransformclasses.SimpleTransformTest");
		operation1.setOperationInputFields(new String[] { "name" });
		operation1.setOperationOutputFields(new String[] { "name_trimmed" });
		operation1.setOperationProperties(userProps);

		// create another operation object and set the properties of operation
		Operation operation2 = new Operation();
		operation2.setOperationId("operation2");
		operation2.setOperationClass(
				"hydrograph.engine.cascading.test.customtransformclasses.TransformTest_RenameFields");
		operation2.setOperationInputFields(new String[] { "id", "name", "city" });
		operation2.setOperationOutputFields(new String[] { "new.id", "new.name", "new.city" });
		userProps.put("LOAD_ID", 1);
		operation2.setOperationProperties(userProps);

		List<Operation> operationList = new ArrayList<>();
		operationList.add(operation1);
		operationList.add(operation2);
		transformEntity.setOperationsList(operationList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("id", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		operationFieldsList.add(new OperationField("name_trimmed", "operation1"));
		operationFieldsList.add(new OperationField("new.id", "operation2"));
		operationFieldsList.add(new OperationField("new.name", "operation2"));
		operationFieldsList.add(new OperationField("new.city", "operation2"));
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		// set the name of the component
		transformEntity.setComponentId("testTransform");

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(2);

		// set the name of the component
		transformEntity.setComponentId("testTransform");

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(2);

		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);

		// create bucket for the transform sub assembly
		Bucket bucket = plunger.newBucket(new Fields("name_trimmed", "new.id", "new.name", "new.city", "id"),
				transform);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("John", 1, "  John  ", "Chicago", 1));
		expectedOutput.add(new Tuple("Mary", 2, "Mary    ", "Richmond", 2));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Unit test for testing overriding fields in custom transform operation
	 * when the field is also specified in pass through
	 */

	@Test
	public void overrideFieldsInCustomOperationtest() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city")).addTuple(1, "  John  ", "Chicago")
				.addTuple(2, "Mary    ", "Richmond").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);
		// pipe corresponding to an input of transform component

		ComponentParameters parameters = new ComponentParameters();
		Properties userProps = new Properties();

		parameters.addInputPipe(pipe1); // first input to transform component

		parameters.addInputFields(new Fields("id", "name", "city"));

		TransformEntity transformEntity = new TransformEntity();

		// create the operation object and set the properties of operation
		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationClass(
				"hydrograph.engine.cascading.test.customtransformclasses.TransformTest_UserProperties");
		operation.setOperationInputFields(new String[] { "name" });
		operation.setOperationOutputFields(new String[] { "name", "load_id" });
		userProps.put("LOAD_ID", 1);
		operation.setOperationProperties(userProps);

		List<Operation> operationList = new ArrayList<>();
		operationList.add(operation);
		transformEntity.setOperationsList(operationList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("id", "in"));
		passThroughFieldsList1.add(new PassThroughField("name", "in"));
		passThroughFieldsList1.add(new PassThroughField("city", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		operationFieldsList.add(new OperationField("name", "operation1"));
		operationFieldsList.add(new OperationField("load_id", "operation1"));
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		// set the name of the component
		transformEntity.setComponentId("testTransform");

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(1);

		// create a dummy component to be tested
		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);

		// create bucket for the transform sub assembly
		Bucket bucket = plunger.newBucket(new Fields("name", "load_id", "id", "city"), transform);

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("John", 1, 1, "Chicago"));
		expectedOutput.add(new Tuple("Mary", 1, 2, "Richmond"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Unit test with simple transform operation and a map field
	 */
	@Test
	public void simpleTransformWithOneOperationAndMapFieldsTest() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city")).addTuple(1, "  John  ", "Chicago")
				.addTuple(2, "Mary    ", "Richmond").build();

		// pipe corresponding to an input of transform component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();
		Properties userProps = new Properties();

		parameters.addInputPipe(pipe1); // first input to transform component

		parameters.addInputFields(new Fields("id", "name", "city"));

		TransformEntity transformEntity = new TransformEntity();

		// create the operation object and set the properties of operation
		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationClass("hydrograph.engine.cascading.test.customtransformclasses.SimpleTransformTest");
		operation.setOperationInputFields(new String[] { "name" });
		operation.setOperationOutputFields(new String[] { "name_trimmed" });
		operation.setOperationProperties(userProps);

		List<Operation> operationList = new ArrayList<>();
		operationList.add(operation);
		transformEntity.setOperationsList(operationList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("city", "new_city", "in"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("id", "in"));
		passThroughFieldsList1.add(new PassThroughField("name", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		operationFieldsList.add(new OperationField("name_trimmed", "operation1"));
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		transformEntity.setComponentId("testTransform"); // set the name of the
															// component

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(1);

		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);
		// create bucket for the transform sub assembly
		Bucket bucket = plunger.newBucket(new Fields("name_trimmed", "new_city", "id", "name"), transform);

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("John", "Chicago", 1, "  John  "));
		expectedOutput.add(new Tuple("Mary", "Richmond", 2, "Mary    "));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Unit test with simple transform operation and multiple map fields
	 */
	@Test
	public void simpleTransformWithOneOperationAndMulitpleMapFieldsTest() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city")).addTuple(1, "  John  ", "Chicago")
				.addTuple(2, "Mary    ", "Richmond").build();

		// pipe corresponding to an input of transform component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();
		Properties userProps = new Properties();

		parameters.addInputPipe(pipe1); // first input to transform component
		parameters.addInputFields(new Fields("id", "name", "city"));

		TransformEntity transformEntity = new TransformEntity();

		// create the operation object and set the properties of operation
		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationClass("hydrograph.engine.cascading.test.customtransformclasses.SimpleTransformTest");
		operation.setOperationInputFields(new String[] { "name" });
		operation.setOperationOutputFields(new String[] { "name_trimmed" });
		operation.setOperationProperties(userProps);

		List<Operation> operationList = new ArrayList<>();
		operationList.add(operation);
		transformEntity.setOperationsList(operationList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("id", "new_id", "in"));
		mapFieldsList.add(new MapField("city", "new_city", "in"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("name", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		operationFieldsList.add(new OperationField("name_trimmed", "operation1"));
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		transformEntity.setComponentId("testTransform"); // set the name of the
															// component

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(1);

		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);
		// create bucket for the transform sub assembly
		Bucket bucket = plunger.newBucket(new Fields("name_trimmed", "new_id", "new_city", "name"), transform);

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("John", 1, "Chicago", "  John  "));
		expectedOutput.add(new Tuple("Mary", 2, "Richmond", "Mary    "));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Unit test with same field name in map fields and pass through fields
	 */
	@Test
	public void sameFieldNameInMapFieldsAndPassThroughFieldsTest() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city")).addTuple(1, "  John  ", "Chicago")
				.addTuple(2, "Mary    ", "Richmond").build();

		// pipe corresponding to an input of transform component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();
		Properties userProps = new Properties();

		parameters.addInputPipe(pipe1); // first input to transform component
		parameters.addInputFields(new Fields("id", "name", "city"));

		TransformEntity transformEntity = new TransformEntity();

		// create the operation object and set the properties of operation
		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationClass("hydrograph.engine.cascading.test.customtransformclasses.SimpleTransformTest");
		operation.setOperationInputFields(new String[] { "name" });
		operation.setOperationOutputFields(new String[] { "name_trimmed" });
		operation.setOperationProperties(userProps);

		List<Operation> operationList = new ArrayList<>();
		operationList.add(operation);
		transformEntity.setOperationsList(operationList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("city", "new_city", "in"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("id", "in"));
		passThroughFieldsList1.add(new PassThroughField("city", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		operationFieldsList.add(new OperationField("name_trimmed", "operation1"));
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		transformEntity.setComponentId("testTransform"); // set the name of the
															// component

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(1);

		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);
		// create bucket for the transform sub assembly
		Bucket bucket = plunger.newBucket(new Fields("name_trimmed", "new_city", "id", "city"), transform);

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("John", "Chicago", 1, "Chicago"));
		expectedOutput.add(new Tuple("Mary", "Richmond", 2, "Richmond"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Unit test with same field name in map fields and pass through fields
	 */
	@Test
	public void sameFieldNameInMapFieldTargetAndOperationInputFieldsTest() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city", "old_city"))
				.addTuple(1, "John", "Chicago", "Wheeling").addTuple(2, "Mary", "Richmond", "Henrico").build();

		// pipe corresponding to an input of transform component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();
		Properties userProps = new Properties();

		parameters.addInputPipe(pipe1); // first input to transform component

		parameters.addInputFields(new Fields("id", "name", "city", "old_city"));

		TransformEntity transformEntity = new TransformEntity();

		// create the operation object and set the properties of operation
		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationClass("hydrograph.engine.cascading.test.customtransformclasses.TransformTest_RenameCity");
		operation.setOperationInputFields(new String[] { "city" });
		operation.setOperationOutputFields(new String[] { "other_city" });
		operation.setOperationProperties(userProps);

		List<Operation> operationList = new ArrayList<>();
		operationList.add(operation);
		transformEntity.setOperationsList(operationList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("old_city", "city", "in"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("id", "in"));
		passThroughFieldsList1.add(new PassThroughField("name", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		operationFieldsList.add(new OperationField("other_city", "operation1"));
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		transformEntity.setComponentId("testTransform"); // set the name of the
															// component

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(1);

		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);
		// create bucket for the transform sub assembly
		Bucket bucket = plunger.newBucket(new Fields("other_city", "city", "id", "name"), transform);

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("Chicago", "Wheeling", 1, "John"));
		expectedOutput.add(new Tuple("Richmond", "Henrico", 2, "Mary"));

		Assert.assertEquals(expectedOutput, output);
	}

	@Test
	public void transformComponentWithWildCardPassthroughFieldsWithPriority() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("id", "name", "city")).addTuple(1, "  John  ", "Chicago")
				.addTuple(2, "Mary    ", "Richmond").build();

		// pipe corresponding to an input of transform component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();
		Properties userProps = new Properties();

		parameters.addInputPipe(pipe1); // first input to transform component

		parameters.addInputFields(new Fields("id", "name", "city"));

		TransformEntity transformEntity = new TransformEntity();

		// create the operation object and set the properties of operation
		Operation operation = new Operation();
		operation.setOperationId("operation1");
		operation.setOperationClass(
				"hydrograph.engine.cascading.test.customtransformclasses.TransformWithSameInputOutputField");
		operation.setOperationInputFields(new String[] { "name" });
		operation.setOperationOutputFields(new String[] { "name" });
		operation.setOperationProperties(userProps);

		List<Operation> operationList = new ArrayList<>();
		operationList.add(operation);
		transformEntity.setOperationsList(operationList);

		transformEntity.setOperation(operation);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("*", "in"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// set operation field
		List<OperationField> operationFieldsList = new ArrayList<>();
		operationFieldsList.add(new OperationField("name", "operation1"));
		outSocket1.setOperationFieldList(operationFieldsList);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		transformEntity.setOutSocketList(outSocketList);

		transformEntity.setComponentId("testTransform"); // set the name of the
															// component

		// set operation present and number of operations in the entity
		transformEntity.setOperationPresent(true);
		transformEntity.setNumOperations(1);

		TransformAssembly transform = new TransformAssembly(transformEntity, parameters);

		// create bucket for the transform sub assembly
		Bucket bucket = plunger.newBucket(new Fields("name", "id", "city"), transform);

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("John", 1, "Chicago"));
		expectedOutput.add(new Tuple("Mary", 2, "Richmond"));

		Assert.assertEquals(expectedOutput, output);
	}

}