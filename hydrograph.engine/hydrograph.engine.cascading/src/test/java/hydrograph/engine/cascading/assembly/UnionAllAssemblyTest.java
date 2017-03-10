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
import hydrograph.engine.cascading.assembly.UnionAllAssembly;
import hydrograph.engine.cascading.assembly.UnionAllAssembly.SchemaMismatchException;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.UnionAllEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test unionAll sub assembly. The tests are written using plunger framework
 * 
 * @author Prabodh
 */
public class UnionAllAssemblyTest {

	@Before
	public void setup() {
		// TODO: Add setup related code here

	}

	/**
	 * Test the unionAll component working
	 */
	@Test
	public void TestSimpleUnionAllComponentWorking() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(
				new Fields("col1", "col2", "col3"))
						.addTuple(1, "C2R1", "C3R1").addTuple(2, "C2R2", "C3R2").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// unionAll
															// component

		Data file2 = new DataBuilder(
				new Fields("col1", "col2", "col3"))
						.addTuple(3, "C2R3", "C3R3").addTuple(4, "C2R4", "C3R4").build();
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2); // pipe corresponding
															// to an input of
															// unionAll
															// component
		UnionAllEntity unionAllEntity = new UnionAllEntity();
		unionAllEntity.setComponentId("unionAll");
		unionAllEntity.setOutSocket(new OutSocket("out1"));

		ComponentParameters parameters = new ComponentParameters();

		Fields in1Fields=new Fields("col1", "col2", "col3").applyTypes(Integer.class, String.class, String.class);
		Fields in2Fields=new Fields("col1", "col2", "col3").applyTypes(Integer.class, String.class, String.class);
		
		addSchemaFields(parameters, in1Fields);
		addSchemaFields(parameters, in2Fields);
		
		parameters.addInputPipe(pipe1); // first input to unionAll component
		parameters.addInputPipe(pipe2); // second input to unionAll component
		parameters.addInputFields(in1Fields); // list
		parameters.addInputFields(in2Fields); // fields
																	// on
																		// input
																		// of
																		// unionAll
																		// component
		parameters.addOutputFields(new Fields("col1", "col2", "col3")); // list
																		// of
																		// fields
																		// on
																		// output
																		// of
																		// unionAll
																		// component
		

		// parameters.s("testunionAll"); //set the name of the component

		UnionAllAssembly unionAll = new UnionAllAssembly(unionAllEntity, parameters); // create
																						// a
																						// dummy
																						// component
																						// to
																						// be
																						// tested

		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"), unionAll); // create
																							// bucket
																							// for
																							// the
																							// unionAll
																							// sub
																							// assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(4));
		// assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple(1, "C2R1", "C3R1"));
		expectedOutput.add(new Tuple(2, "C2R2", "C3R2"));
		expectedOutput.add(new Tuple(3, "C2R3", "C3R3"));
		expectedOutput.add(new Tuple(4, "C2R4", "C3R4"));

		Assert.assertEquals(expectedOutput, output);
	}

	private void addSchemaFields(ComponentParameters parameters, Fields fields) {

		Type[] types = fields.getTypes();
		Set<SchemaField> schemaFields = new LinkedHashSet<>();
		for (int i = 0; i < fields.size(); i++) {

			SchemaField sc = new SchemaField(fields.get(i).toString(), types[i].toString());
			schemaFields.add(sc);
		}
		parameters.addSchemaFields(schemaFields);

	}
	
	/**
	 * Test unionAll component working with four inputs
	 */
	@Test
	public void TestUnionAllComponentWithFourInputs() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple(1, "C2R1", "C3R1")
				.addTuple(2, "C2R2", "C3R2").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// unionAll
															// component

		Data file2 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple(3, "C2R3", "C3R3")
				.addTuple(4, "C2R4", "C3R4").build();
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2); // pipe corresponding
															// to an input of
															// unionAll
															// component

		Data file3 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple(5, "C2R5", "C3R5")
				.addTuple(6, "C2R6", "C3R6").build();
		Pipe pipe3 = plunger.newNamedPipe("pipe3", file3); // pipe corresponding
															// to an input of
															// unionAll
															// component

		Data file4 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple(7, "C2R7", "C3R7")
				.addTuple(8, "C2R8", "C3R8").build();
		Pipe pipe4 = plunger.newNamedPipe("pipe4", file4); // pipe corresponding
															// to an input of
															// unionAll
															// component

		// Map<String, String> test = new HashMap<String, String>();
		// test.put("col1", "out");
		UnionAllEntity unionAllEntity = new UnionAllEntity();
		unionAllEntity.setComponentId("1");
		unionAllEntity.setOutSocket(new OutSocket("abcc"));
		
		Fields in1Fields=new Fields("col1", "col2", "col3").applyTypes(Integer.class, String.class, String.class);
		Fields in2Fields=new Fields("col1", "col2", "col3").applyTypes(Integer.class, String.class, String.class);
		Fields in3Fields=new Fields("col1", "col2", "col3").applyTypes(Integer.class, String.class, String.class);
		Fields in4Fields=new Fields("col1", "col2", "col3").applyTypes(Integer.class, String.class, String.class);
		
		ComponentParameters parameters = new ComponentParameters();

		addSchemaFields(parameters, in1Fields);
		addSchemaFields(parameters, in2Fields);
		addSchemaFields(parameters, in3Fields);
		addSchemaFields(parameters, in4Fields);
		
		parameters.addInputPipe(pipe1); // first input to unionAll component
		parameters.addInputPipe(pipe2); // second input to unionAll component
		parameters.addInputPipe(pipe3); // third input to unionAll component
		parameters.addInputPipe(pipe4); // fourth input to unionAll component
		parameters.addInputFields(in1Fields); // list
		parameters.addInputFields(in2Fields); // of
		parameters.addInputFields(in3Fields); // fields
		parameters.addInputFields(in4Fields); // on
												// input
												// of
												// unionAll
												// component
		parameters.addOutputFields(new Fields("col1", "col2", "col3")); // list
																		// of
																		// fields
																		// on
																		// output
																		// of
																		// unionAll
																		// component

		// parameters.setComponentName("testunionAll"); //set the name of the
		// component

		UnionAllAssembly unionAllAssembly = new UnionAllAssembly(unionAllEntity, parameters); // create
																								// a
																								// dummy
																								// component
																								// to
																								// be
																								// tested

		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"), unionAllAssembly); // create
																									// bucket
																									// for
																									// the
																									// unionAll
																									// sub
																									// assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(8));
		// assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple(1, "C2R1", "C3R1"));
		expectedOutput.add(new Tuple(2, "C2R2", "C3R2"));
		expectedOutput.add(new Tuple(3, "C2R3", "C3R3"));
		expectedOutput.add(new Tuple(4, "C2R4", "C3R4"));
		expectedOutput.add(new Tuple(5, "C2R5", "C3R5"));
		expectedOutput.add(new Tuple(6, "C2R6", "C3R6"));
		expectedOutput.add(new Tuple(7, "C2R7", "C3R7"));
		expectedOutput.add(new Tuple(8, "C2R8", "C3R8"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test unionAll component with different order of fields in input. The
	 * unionAll component should re-align the fields on all the inputs to match
	 * the first input. The first input is on port 0
	 */
	@Test
	public void TestReAligningInputFieldsInUnionAllComponent() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col3", "col2")).addTuple("C1R1", "C3R1", "C2R1")
				.addTuple("C1R2", "C3R2", "C2R2").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// unionAll
															// component

		Data file2 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R3", "C2R3", "C3R3")
				.addTuple("C1R4", "C2R4", "C3R4").build();
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2); // pipe corresponding
															// to an input of
															// unionAll
															// component

		UnionAllEntity unionAllEntity = new UnionAllEntity();
		unionAllEntity.setComponentId("unionAll");
		unionAllEntity.setOutSocket(new OutSocket("out1"));
		ComponentParameters parameters = new ComponentParameters();
		
		Fields in1Fields=new Fields("col1", "col3", "col2").applyTypes(Integer.class, String.class, String.class);
		Fields in2Fields=new Fields("col1", "col2", "col3").applyTypes(Integer.class, String.class, String.class);
		
		addSchemaFields(parameters, in1Fields);
		addSchemaFields(parameters, in2Fields);
		

		parameters.addInputPipe(pipe1); // first input to unionAll component
		parameters.addInputPipe(pipe2); // second input to unionAll component

		parameters.addInputFields(new Fields("col1", "col3", "col2")); // list
		parameters.addInputFields(new Fields("col1", "col2", "col3")); // of
																		// fields
																		// on
																		// input
																		// of
																		// unionAll
																		// component

		parameters.addOutputFields(new Fields("col1", "col2", "col3")); // list
																		// of
																		// fields
																		// on
																		// output
																		// of
																		// unionAll
																		// component

		// parameters.setComponentName("testunionAll"); //set the name of the
		// component

		UnionAllAssembly unionAllAssembly = new UnionAllAssembly(unionAllEntity, parameters); // create
																								// a
																								// dummy
																								// component
																								// to
																								// be
																								// tested

		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"), unionAllAssembly); // create
																									// bucket
																									// for
																									// the
																									// unionAll
																									// sub
																									// assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(4));
		
		
		
	}
	
	@Test(expected=SchemaMismatchException.class)
	public void itShouldThrowExceptionIfInSchemasAreMismatch() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(
				new Fields("col1", "col2", "col3","col4"))
						.addTuple(1, "C2R1", "C3R1","C4R1").addTuple(2, "C2R2", "C3R2","C4R2").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// unionAll
															// component

		Data file2 = new DataBuilder(
				new Fields("col1", "col2", "col3"))
						.addTuple(3, "C2R3", "C3R3").addTuple(4, "C2R4", "C3R4").build();
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2); // pipe corresponding
															// to an input of
															// unionAll
															// component
		UnionAllEntity unionAllEntity = new UnionAllEntity();
		unionAllEntity.setComponentId("unionAll");
		unionAllEntity.setOutSocket(new OutSocket("out1"));

		ComponentParameters parameters = new ComponentParameters();

		//create fields of inSockets with different no of fields
		Fields in1Fields=new Fields("col1", "col2", "col3","col4").applyTypes(Integer.class, String.class, String.class,String.class);
		Fields in2Fields=new Fields("col1", "col2", "col3").applyTypes(Integer.class, String.class, String.class);
		
		addSchemaFields(parameters, in2Fields);
		addSchemaFields(parameters, in1Fields);
		
		parameters.addInputPipe(pipe1); // first input to unionAll component
		parameters.addInputPipe(pipe2); // second input to unionAll component
		parameters.addInputFields(in1Fields); // list
		parameters.addInputFields(in2Fields); // fields
												// on
												// input
												// of
												// unionAll
												// component
		parameters.addOutputFields(new Fields("col1", "col2", "col3")); // list
																		// of
																		// fields
																		// on
																		// output
																		// of
																		// unionAll
																		// component
		

		// parameters.s("testunionAll"); //set the name of the component

		
		UnionAllAssembly unionAll = new UnionAllAssembly(unionAllEntity, parameters);// create
									// a
									// dummy
									// component
									// to
									// be
									// tested


		
	

	

	}
	
	@Test(expected=SchemaMismatchException.class)
	public void itShouldThrowExceptionIfInSchemasFieldsHaveDifferentDataTypes() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(
				new Fields("col1", "col2", "col3"))
						.addTuple(1, "C2R1", "C3R1").addTuple(2, "C2R2", "C3R2").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// unionAll
															// component

		Data file2 = new DataBuilder(
				new Fields("col1", "col2", "col3"))
						.addTuple(3, "C2R3", "C3R3").addTuple(4, "C2R4", "C3R4").build();
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2); // pipe corresponding
															// to an input of
															// unionAll
															// component
		UnionAllEntity unionAllEntity = new UnionAllEntity();
		unionAllEntity.setComponentId("unionAll");
		unionAllEntity.setOutSocket(new OutSocket("out1"));

		ComponentParameters parameters = new ComponentParameters();

		//create fields for inSockets that have different data types
		Fields in1Fields=new Fields("col1", "col2", "col3").applyTypes(Integer.class, String.class, String.class);
		Fields in2Fields=new Fields("col1", "col2", "col3").applyTypes(String.class, String.class, String.class);
		
		addSchemaFields(parameters, in2Fields);
		addSchemaFields(parameters, in1Fields);
		
		parameters.addInputPipe(pipe1); // first input to unionAll component
		parameters.addInputPipe(pipe2); // second input to unionAll component
		parameters.addInputFields(in1Fields); // list
		parameters.addInputFields(in2Fields); // fields
																	// on
																		// input
																		// of
																		// unionAll
																		// component
		parameters.addOutputFields(new Fields("col1", "col2", "col3")); // list
																		// of
																		// fields
																		// on
																		// output
																		// of
																		// unionAll
																		// component
		

		// parameters.s("testunionAll"); //set the name of the component

		
		UnionAllAssembly unionAll = new UnionAllAssembly(unionAllEntity, parameters);// create
									// a
									// dummy
									// component
									// to
									// be
									// tested


		
	

	

	}
	
	@Test(expected=SchemaMismatchException.class)
	public void itShouldThrowExceptionIfInSchemasFieldsAreNotIdentical() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(
				new Fields("col11", "col2", "col3"))
						.addTuple(1, "C2R1", "C3R1").addTuple(2, "C2R2", "C3R2").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// unionAll
															// component

		Data file2 = new DataBuilder(
				new Fields("col1", "col2", "col3"))
						.addTuple(3, "C2R3", "C3R3").addTuple(4, "C2R4", "C3R4").build();
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2); // pipe corresponding
															// to an input of
															// unionAll
															// component
		UnionAllEntity unionAllEntity = new UnionAllEntity();
		unionAllEntity.setComponentId("unionAll");
		unionAllEntity.setOutSocket(new OutSocket("out1"));

		ComponentParameters parameters = new ComponentParameters();

		//create fields for inSockets that are not identical
		Fields in1Fields=new Fields("col11", "col2", "col3").applyTypes(Integer.class, String.class, String.class);
		Fields in2Fields=new Fields("col1", "col2", "col3").applyTypes(String.class, String.class, String.class);
		
		addSchemaFields(parameters, in2Fields);
		addSchemaFields(parameters, in1Fields);
		
		parameters.addInputPipe(pipe1); // first input to unionAll component
		parameters.addInputPipe(pipe2); // second input to unionAll component
		parameters.addInputFields(in1Fields); // list
		parameters.addInputFields(in2Fields); // fields
																	// on
																		// input
																		// of
																		// unionAll
																		// component
		parameters.addOutputFields(new Fields("col11", "col2", "col3")); // list
																		// of
																		// fields
																		// on
																		// output
																		// of
																		// unionAll
																		// component
		

		// parameters.s("testunionAll"); //set the name of the component

		
		UnionAllAssembly unionAll = new UnionAllAssembly(unionAllEntity, parameters);// create
									// a
									// dummy
									// component
									// to
									// be
									// tested


		
	

	

	}


}
