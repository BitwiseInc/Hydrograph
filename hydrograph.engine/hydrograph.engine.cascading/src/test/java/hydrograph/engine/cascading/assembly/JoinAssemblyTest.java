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
import hydrograph.engine.cascading.assembly.JoinAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.JoinEntity;
import hydrograph.engine.core.component.entity.elements.JoinKeyFields;
import hydrograph.engine.core.component.entity.elements.MapField;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.PassThroughField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test join sub assembly. The tests are written using plunger framework
 * 
 * @author Prabodh
 */
public class JoinAssemblyTest {

	@Before
	public void setup() {
		// TODO: add setup related code here
	}

	/**
	 * Test simple inner join operation using join component with 2 inputs
	 */
	@Test
	public void TestInnerJoin() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R2", "C4R2", "C5R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));
		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in0", new Fields("col1", "col4", "col5"));
		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("col1", "col1", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col2", "in0"));
		passThroughFieldsList1.add(new PassThroughField("col3", "in0"));
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3",
				"col4", "col5"), join);

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1", "C4R1", "C5R1"));
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2", "C4R2", "C5R2"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test simple inner join operation using join component with 2 inputs. One
	 * of the input - output map is kept empty
	 */
	@Test
	public void TestInnerJoinWithEmptyInputOutputMapping() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R2", "C4R2", "C5R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));
		
		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));

		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("*", "in0"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				join); // create bucket for the join sub assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1"));
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test simple inner join operation using join component with 2 inputs using
	 * grouping fields
	 */
	@Test
	public void TestInnerJoinWithGroupFields() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "G1.col2", "G1.col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R2", "C4R2", "C5R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "G1.col2", "G1.col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

		parameters.addCopyOfInSocket("in0", new Fields("col1", "G1.col2", "G1.col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));

		
		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("col1", "col1", "in0"));
		mapFieldsList.add(new MapField("G1.*", "G1.*", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("col1", "G1.col2",
				"G1.col3", "col4", "col5"), join); // create bucket for the join
													// sub assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1", "C4R1", "C5R1"));
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2", "C4R2", "C5R2"));

		Assert.assertEquals(expectedOutput, output);
	}

	
	/**
	 * Test simple inner join operation using join component with 2 inputs. One
	 * of the input has a wildcard mapping.
	 */
	@Test
	public void TestInnerJoinWithWildcardMapping() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", 21, "C3R1").addTuple("C1R2", 22, "C3R2")
				.build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", 41, "C5R1").addTuple("C1R2", 42, "C5R2")
				.build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		
		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "*", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		/*passThroughFieldsList1.add(new PassThroughField("*", "in0"));*/
		passThroughFieldsList1.add(new PassThroughField("col1", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col2",
				"col3", "col1", "col4", "col5"), join);

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple(21, "C3R1", "C1R1", 41, "C5R1"));
		expectedOutput.add(new Tuple(22, "C3R2", "C1R2", 42, "C5R2"));

		Assert.assertEquals(expectedOutput, output);
	}
	
	/**
	 * Test simple inner join operation using join component with 2 inputs. One
	 * of the input has a wildcard as well as one to one mapping
	 */
	@Test
	public void TestInnerJoinWithWildcardAndOneToOneMapping() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", 21, "C3R1").addTuple("C1R2", 22, "C3R2")
				.build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", 41, "C5R1").addTuple("C1R2", 42, "C5R2")
				.build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		
		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "*", "in0"));
		mapFieldsList.add(new MapField("col2", "RenamedColumn2", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col3", "RenamedColumn2", 
				 "col4", "col5"), join);

		// get results from bucket
		List<Tuple> actual = bucket.result().asTupleList();

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C3R1", 21, 41, "C5R1"));
		expectedOutput.add(new Tuple("C1R2", "C3R2", 22, 42, "C5R2"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test simple inner join operation using join component with 2 inputs. One
	 * of the input has a wildcard as well as one to one mapping. The one to one
	 * mapping is prefixed.
	 */
	@Test
	public void TestInnerJoinWithWildcarWithPrefixAndOneToOneMapping() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("in0.col1", "col2", "in0.col3"))
				.addTuple("C1R1", 21, "C3R1").addTuple("C1R2", 22, "C3R2")
				.build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", 41, "C5R1").addTuple("C1R2", 42, "C5R2")
				.build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("in0.col1", "col2", "in0.col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

		parameters.addCopyOfInSocket("in0", new Fields("in0.col1", "col2", "in0.col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		
		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true,
				new String[] { "in0.col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("in0.*", "*", "in0"));
		mapFieldsList.add(new MapField("col2", "RenamedColumn2", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col3",
				"RenamedColumn2", "col4", "col5"), join);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C3R1", 21, 41, "C5R1"));
		expectedOutput.add(new Tuple("C1R2", "C3R2", 22, 42, "C5R2"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test simple inner join operation using join component with 2 inputs using
	 * grouping fields
	 */
	@Test
	public void TestInnerJoinWithWildcardWithPrefixInTargetAndOneToOneMapping() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", 21, "C3R1").addTuple("C1R2", 22, "C3R2")
				.build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col3"))
				.addTuple("C1R1", 41, "C5R1").addTuple("C1R2", 42, "C5R2")
				.build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col3"));
		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col3"));
		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "target_group.*", "in0"));
		mapFieldsList.add(new MapField("col2", "RenamedColumn2", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col3", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly

		Bucket bucket = plunger.newBucket(new Fields("target_group.col1",
				"target_group.col3", "RenamedColumn2", "col4", "col3"), join);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C3R1", 21, "C5R1", 41));
		expectedOutput.add(new Tuple("C1R2", "C3R2", 22, "C5R2", 42));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test the unused port of a simple inner join operation with 2 inputs
	 */
	@Test
	public void TestInnerJoinWithUnusedPort() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R3", "C4R3", "C5R3").build();
		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

		parameters.addinSocketId("in0");
		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		OutSocket outSocket2 = new OutSocket("unused0");
		outSocket2.setSocketType("unused");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "*", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);
		
		
		outSocket2.setCopyOfInSocketId("in0");
		
		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		outSocketList.add(outSocket2);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		// set the unused port explicitly. Default is out
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				join.getOutLink("unused", "unused0", "testJoin"));

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket
		// assert the actual results with expected results
		assertThat(actual.size(), is(1));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test both the unused ports of a simple inner join operation with 2 inputs
	 */
	@Test
	public void TestInnerJoinWithMultipleUnusedPorts() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R3", "C4R3", "C5R3").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");
		
		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		OutSocket outSocket2 = new OutSocket("unused0");
		outSocket2.setSocketType("unused");

		OutSocket outSocket3 = new OutSocket("unused1");
		outSocket3.setSocketType("unused");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "*", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		outSocket2.setCopyOfInSocketId("in0");
		outSocket3.setCopyOfInSocketId("in1");
		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		outSocketList.add(outSocket2);
		outSocketList.add(outSocket3);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the first unused port of join sub assembly
		// set the unused port explicitly. Default is out
		Bucket bucket1 = plunger.newBucket(new Fields("col1", "col2", "col3"),
				join.getOutLink("unused", "unused0", "testJoin"));

		// create bucket for the second unused port of join sub assembly
		// set the unused port explicitly. Default is out
		Bucket bucket2 = plunger.newBucket(new Fields("col1", "col4", "col5"),
				join.getOutLink("unused", "unused1", "testJoin"));
		// buckets need to be created before calling result() on any one of them

		List<Tuple> actual = bucket1.result().asTupleList(); // get results from
																// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(1));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2"));

		Assert.assertEquals(expectedOutput, output);

		// test second unused port
		actual = bucket2.result().asTupleList(); // get results from bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(1));

		// Use HashSet so that order of fields does not matter while comparison
		output = new HashSet<Tuple>(actual);

		expectedOutput.clear();
		expectedOutput.add(new Tuple("C1R3", "C4R3", "C5R3"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test simple inner join operation using join component with 3 inputs
	 */
	@Test
	public void TestInnerJoinWithMoreThanTwoInputs() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R2", "C4R2", "C5R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		Data file3 = new DataBuilder(new Fields("col1", "col6", "col7"))
				.addTuple("C1R1", "C6R1", "C7R1")
				.addTuple("C1R2", "C6R2", "C7R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe3 = plunger.newNamedPipe("pipe3", file3);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component
		parameters.addInputPipe(pipe3); // third input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));
		parameters.addInputFields(new Fields("col1", "col6", "col7"));

		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		parameters.addCopyOfInSocket("in2", new Fields("col1", "col6", "col7"));

		
		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");
		parameters.addinSocketId("in2");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in2", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "*", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col7", "in2"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3",
				"col4", "col5", "col7"), join); // create bucket for the join
												// sub assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1", "C4R1", "C5R1",
				"C7R1"));
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2", "C4R2", "C5R2",
				"C7R2"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test simple left join operation using join component with 2 inputs
	 */
	@Test
	public void TestLeftJoin() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R3", "C4R3", "C5R3").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		
		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", false, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "*", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3",
				"col4", "col5"), join);
		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1", "C4R1", "C5R1"));
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2", null, null));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test simple left join operation using join component with 3 inputs
	 */
	@Test
	public void TestLeftJoinWithMoreThanTwoInputs() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);
		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R3", "C4R2", "C5R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		Data file3 = new DataBuilder(new Fields("col1", "col6", "col7"))
				.addTuple("C1R1", "C6R1", "C7R1")
				.addTuple("C1R3", "C6R2", "C7R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe3 = plunger.newNamedPipe("pipe3", file3);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component
		parameters.addInputPipe(pipe3); // third input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));
		parameters.addInputFields(new Fields("col1", "col6", "col7"));

		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		parameters.addCopyOfInSocket("in2", new Fields("col1", "col6", "col7"));
		
		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");
		parameters.addinSocketId("in2");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", false, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in2", false, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "*", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col7", "in2"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3",
				"col4", "col5", "col7"), join); // create bucket for the join
												// sub assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1", "C4R1", "C5R1",
				"C7R1"));
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2", null, null, null));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test the unused port of a simple left join operation with 2 inputs
	 */
	@Test
	public void TestLeftJoinWithUnusedPort() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R3", "C4R3", "C5R3").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

	/*	parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
	*/	
		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", false, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0", "out");

		OutSocket outSocket2 = new OutSocket("unused0", "unused");
		OutSocket outSocket3 = new OutSocket("unused1", "unused");

		outSocket2.setCopyOfInSocketId("in0");
		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		outSocket3.setCopyOfInSocketId("in1");
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		
		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("col4", "col4", "in1"));
		mapFieldsList.add(new MapField("col5", "col5", "in1"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("*", "in0"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		outSocketList.add(outSocket2);
		outSocketList.add(outSocket3);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		// set the unused port explicitly. Default is out
		Bucket bucket = plunger.newBucket(new Fields("col1", "col4", "col5"),
				join.getOutLink("unused", "unused1", "testJoin"));

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket
		// assert the actual results with expected results
		assertThat(actual.size(), is(1));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R3", "C4R3", "C5R3"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test a mixed join operation using join component with 3 inputs
	 */
	@Test
	public void TestMixedJoinWithMoreThanTwoInputs() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2")
				.addTuple("C1R3", "C2R3", "C3R3").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R2", "C4R2", "C5R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		Data file3 = new DataBuilder(new Fields("col1", "col6", "col7"))
				.addTuple("C1R1", "C6R1", "C7R1")
				.addTuple("C1R2", "C6R2", "C7R2")
				.addTuple("C1R3", "C6R3", "C7R3").build();

		// pipe corresponding to an input of join component
		Pipe pipe3 = plunger.newNamedPipe("pipe3", file3);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component
		parameters.addInputPipe(pipe3); // third input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));
		parameters.addInputFields(new Fields("col1", "col6", "col7"));

		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		parameters.addCopyOfInSocket("in2", new Fields("col1", "col6", "col7"));

		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");
		parameters.addinSocketId("in2");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", false, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", false, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in2", false, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "*", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col7", "in2"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3",
				"col4", "col5", "col7"), join);
		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(3));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1", "C4R1", "C5R1",
				"C7R1"));
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2", "C4R2", "C5R2",
				"C7R2"));
		expectedOutput
				.add(new Tuple("C1R3", "C2R3", "C3R3", null, null, "C7R3"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test a mixed join operation using join component with 2 key fields
	 */
	@Test
	public void TestMixedJoinWithTwoInputs() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2")
				.addTuple("C1R3", "C2R3", "C3R3").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R2", "C4R2", "C5R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));

		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", false, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", false, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "*", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3",
				"col4", "col5"), join);
		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(3));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1", "C4R1", "C5R1"));
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2", "C4R2", "C5R2"));
		expectedOutput
				.add(new Tuple("C1R3", "C2R3", "C3R3", null, null));

		Assert.assertEquals(expectedOutput, output);
	}
	
	/**
	 * Test a mixed join operation using join component with 2 key fields
	 */
	@Test
	public void TestMixedJoinWithMoreThanOneKeyFields() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2")
				.addTuple("C1R3", "C2R3", "C3R3").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R2", "C4R2", "C5R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		
		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", false, new String[] { "col1", "col2" }));
		keyFieldsList.add(new JoinKeyFields("in1", false, new String[] { "col1", "col4" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "*", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col4", "in1"));
		passThroughFieldsList1.add(new PassThroughField("col5", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3",
				"col4", "col5"), join);
		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(5));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1", null, null));
		expectedOutput
		.add(new Tuple(null,	null,	null,	"C4R1",	"C5R1"));
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2", null, null));
		expectedOutput
		.add(new Tuple(null,	null	,null,	"C4R2",	"C5R2"));
		expectedOutput
				.add(new Tuple("C1R3", "C2R3", "C3R3", null, null));
		
		
		Assert.assertEquals(expectedOutput, output);
	}
	

	/**
	 * Test simple right join operation using join component with 2 inputs
	 */
	@Test
	public void TestRightJoin() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R3", "C4R3", "C5R3").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);
		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));

		parameters.addinSocketId("in0");
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", false, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("*", "*", "in1"));
		outSocket1.setMapFieldsList(mapFieldsList);

		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col2", "in0"));
		passThroughFieldsList1.add(new PassThroughField("col3", "in0"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col2", "col3", "col1",
				"col4", "col5"), join);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C2R1", "C3R1", "C1R1", "C4R1", "C5R1"));
		expectedOutput.add(new Tuple(null, null, "C1R3", "C4R3", "C5R3"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test the unused port of a simple right join operation with 2 inputs
	 */
	@Test
	public void TestRightJoinWithUnusedPort() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R3", "C4R3", "C5R3").build();

		// pipe corresponding to an input of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters = new ComponentParameters();

		parameters.addInputPipe(pipe1); // first input to join component
		parameters.addInputPipe(pipe2); // second input to join component

		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addInputFields(new Fields("col1", "col4", "col5"));

		parameters.addinSocketId("in0");
		parameters.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters.addCopyOfInSocket("in1", new Fields("col1", "col4", "col5"));
		parameters.addinSocketId("in1");

		JoinEntity joinEntity = new JoinEntity();

		// set the name of the component
		joinEntity.setComponentId("testJoin");

		// set key fields
		ArrayList<JoinKeyFields> keyFieldsList = new ArrayList<JoinKeyFields>();
		keyFieldsList.add(new JoinKeyFields("in0", false, new String[] { "col1" }));
		keyFieldsList.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		joinEntity.setKeyFields(keyFieldsList);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");

		OutSocket outSocket2 = new OutSocket("unused0");
		outSocket2.setSocketType("unused");

		// set map fields
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("col2", "col2", "in0"));
		mapFieldsList.add(new MapField("col3", "col3", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);

		outSocket2.setCopyOfInSocketId("in0");
		// set pass through fields
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("*", "in1"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);

		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		outSocketList.add(outSocket2);
		joinEntity.setOutSocketList(outSocketList);

		// create a dummy component to be tested
		JoinAssembly join = new JoinAssembly(joinEntity, parameters);

		// create bucket for the join sub assembly
		// set the unused port explicitly. Default is out
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				join.getOutLink("unused", "unused0", "testJoin"));

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(1));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R2", "C2R2", "C3R2"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Integration test of two join assemblies with copyOfInSocket in the outSocket
	 */
	@Test
	public void testCopyOfInsocketOfTwoJoinWithThreeInputsHavingSameSchema(){
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input 1 of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R3", "C4R3", "C5R3").build();

		// pipe corresponding to an input 2 of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters_1 = new ComponentParameters();

		parameters_1.addInputPipe(pipe1); // first input to join component
		parameters_1.addInputPipe(pipe2); // second input to join component
		parameters_1.addInputFields(new Fields("col1", "col2", "col3"));
		parameters_1.addInputFields(new Fields("col1", "col2", "col3"));

		parameters_1.addinSocketId("in0");
		parameters_1.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters_1.addCopyOfInSocket("in1", new Fields("col1", "col2", "col3"));
		parameters_1.addinSocketId("in1");
		
		JoinEntity join_1_Entity = new JoinEntity();

		// set the name of the component
		join_1_Entity.setComponentId("testJoin_1");

		// set key fields
		ArrayList<JoinKeyFields> keyFields_1_List = new ArrayList<JoinKeyFields>();
		keyFields_1_List.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFields_1_List.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		join_1_Entity.setKeyFields(keyFields_1_List);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");
		outSocket1.setCopyOfInSocketId("in0");
		
		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		join_1_Entity.setOutSocketList(outSocketList);
		
		List<MapField> mapFieldsList = new ArrayList<>();
		outSocket1.setMapFieldsList(mapFieldsList);
		
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);
		
		JoinAssembly join = new JoinAssembly(join_1_Entity, parameters_1);

		Bucket bucket_1 = plunger.newBucket(new Fields("col1", "col2", "col3"),
				join.getOutLink("out", "out0", "testJoin_1"));

		List<Tuple> actual_1 = bucket_1.result().asTupleList(); // get results from
		// bucket

		// assert the actual results with expected results
		assertThat(actual_1.size(), is(1));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output_1 = new HashSet<Tuple>(actual_1);

		Set<Tuple> expectedOutput_1 = new HashSet<Tuple>();
		expectedOutput_1.add(new Tuple("C1R1", "C2R1", "C3R1"));

		Assert.assertEquals(expectedOutput_1, output_1);
		
		Plunger plunger2 = new Plunger();
		ComponentParameters parameters_2 = new ComponentParameters();
		Data file3 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R4", "C3R4")
				.addTuple("C1R4", "C2R4", "C3R4").build();

		// pipe corresponding to an input 3 of join component
		Pipe pipe3 = plunger2.newNamedPipe("pipe3", file3);

		parameters_2.addInputPipe(pipe3); // first input to join component
		parameters_2.addInputPipe(plunger2.newNamedPipe("join_1_pipe", bucket_1.result())); // second input to join component
		parameters_2.addInputFields(new Fields("col1", "col2", "col3"));
		parameters_2.addInputFields(new Fields("col1", "col2", "col3"));

		parameters_2.addinSocketId("in0");
		parameters_2.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters_2.addCopyOfInSocket("in1", new Fields("col1", "col2", "col3"));
		parameters_2.addinSocketId("in1");
		
		JoinEntity join_2_Entity = new JoinEntity();

		// set the name of the component
		join_2_Entity.setComponentId("testJoin_2");

		// set key fields
		ArrayList<JoinKeyFields> keyFields_2_List = new ArrayList<JoinKeyFields>();
		keyFields_2_List.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFields_2_List.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		join_2_Entity.setKeyFields(keyFields_2_List);

		// create outSocket
		OutSocket outSocket2 = new OutSocket("out0");
		outSocket2.setSocketType("out");
		outSocket2.setCopyOfInSocketId("in0");
		
		// add outSocket in list
		List<OutSocket> outSocketList_2 = new ArrayList<>();
		outSocketList_2.add(outSocket2);
		join_2_Entity.setOutSocketList(outSocketList_2);
		
		List<MapField> mapFieldsList_2 = new ArrayList<>();
		outSocket2.setMapFieldsList(mapFieldsList_2);
		
		List<PassThroughField> passThroughFieldsList2 = new ArrayList<>();
		outSocket2.setPassThroughFieldsList(passThroughFieldsList2);
		
		JoinAssembly join_2 = new JoinAssembly(join_2_Entity, parameters_2);
		
		Bucket bucket_2 = plunger2.newBucket(new Fields("col1", "col2", "col3"),
				join_2.getOutLink("out", "out0", "testJoin_2"));

		List<Tuple> actual_2 = bucket_2.result().asTupleList(); // get results from
		// bucket

		// assert the actual results with expected results
		assertThat(actual_2.size(), is(1));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output_2 = new HashSet<Tuple>(actual_2);

		Set<Tuple> expectedOutput_2 = new HashSet<Tuple>();
		expectedOutput_2.add(new Tuple("C1R1", "C2R4", "C3R4"));

		Set<Tuple> unexpectedOutput_2 = new HashSet<Tuple>();
		unexpectedOutput_2.add(new Tuple("C1R1", "C2R1", "C3R1"));

		Assert.assertEquals(expectedOutput_2, output_2);
		Assert.assertNotEquals(unexpectedOutput_2, output_2);
	}
	
	/**
	 * Integration test of two join assemblies with mapFields and passThroughFields in the outSocket 
	 */
	@Test
	public void testMapAndPassthroughFieldsOfTwoJoinWithThreeInputsHavingSameSchema(){
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R2", "C2R2", "C3R2").build();

		// pipe corresponding to an input 1 of join component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		Data file2 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C4R1", "C5R1")
				.addTuple("C1R3", "C4R3", "C5R3").build();

		// pipe corresponding to an input 2 of join component
		Pipe pipe2 = plunger.newNamedPipe("pipe2", file2);

		ComponentParameters parameters_1 = new ComponentParameters();

		parameters_1.addInputPipe(pipe1); // first input to join component
		parameters_1.addInputPipe(pipe2); // second input to join component
		parameters_1.addInputFields(new Fields("col1", "col2", "col3"));
		parameters_1.addInputFields(new Fields("col1", "col2", "col3"));

		parameters_1.addinSocketId("in0");
		parameters_1.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters_1.addCopyOfInSocket("in1", new Fields("col1", "col2", "col3"));
		parameters_1.addinSocketId("in1");
		
		JoinEntity join_1_Entity = new JoinEntity();

		// set the name of the component
		join_1_Entity.setComponentId("testJoin_1");

		// set key fields
		ArrayList<JoinKeyFields> keyFields_1_List = new ArrayList<JoinKeyFields>();
		keyFields_1_List.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFields_1_List.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		join_1_Entity.setKeyFields(keyFields_1_List);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		outSocket1.setSocketType("out");
		
		// add outSocket in list
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		join_1_Entity.setOutSocketList(outSocketList);
		
		List<MapField> mapFieldsList = new ArrayList<>();
		mapFieldsList.add(new MapField("col2", "col2_in1", "in1"));
		mapFieldsList.add(new MapField("col3", "col3_in0", "in0"));
		outSocket1.setMapFieldsList(mapFieldsList);
		
		List<PassThroughField> passThroughFieldsList1 = new ArrayList<>();
		passThroughFieldsList1.add(new PassThroughField("col1", "in0"));
		outSocket1.setPassThroughFieldsList(passThroughFieldsList1);
		
		JoinAssembly join = new JoinAssembly(join_1_Entity, parameters_1);

		Bucket bucket_1 = plunger.newBucket(new Fields("col3_in0", "col1", "col2_in1"),
				join.getOutLink("out", "out0", "testJoin_1"));

		List<Tuple> actual_1 = bucket_1.result().asTupleList(); // get results from
		// bucket

		// assert the actual results with expected results
		assertThat(actual_1.size(), is(1));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output_1 = new HashSet<Tuple>(actual_1);

		Set<Tuple> expectedOutput_1 = new HashSet<Tuple>();
		expectedOutput_1.add(new Tuple("C3R1", "C1R1", "C4R1"));

		Assert.assertEquals(expectedOutput_1, output_1);
		
		Plunger plunger2 = new Plunger();
		ComponentParameters parameters_2 = new ComponentParameters();
		Data file3 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R4", "C3R4")
				.addTuple("C1R4", "C2R4", "C3R4").build();

		// pipe corresponding to an input 3 of join component
		Pipe pipe3 = plunger2.newNamedPipe("pipe3", file3);

		parameters_2.addInputPipe(pipe3); // first input to join component
		parameters_2.addInputPipe(plunger2.newNamedPipe("join_1_pipe", bucket_1.result())); // second input to join component
		parameters_2.addInputFields(new Fields("col1", "col2", "col3"));
		parameters_2.addInputFields(new Fields("col1", "col2_in1", "col3_in0"));

		parameters_2.addinSocketId("in0");
		parameters_2.addCopyOfInSocket("in0", new Fields("col1", "col2", "col3"));
		parameters_2.addCopyOfInSocket("in1", new Fields("col1", "col2_in1", "col3_in0"));
		parameters_2.addinSocketId("in1");
		
		JoinEntity join_2_Entity = new JoinEntity();

		// set the name of the component
		join_2_Entity.setComponentId("testJoin_2");

		// set key fields
		ArrayList<JoinKeyFields> keyFields_2_List = new ArrayList<JoinKeyFields>();
		keyFields_2_List.add(new JoinKeyFields("in0", true, new String[] { "col1" }));
		keyFields_2_List.add(new JoinKeyFields("in1", true, new String[] { "col1" }));
		join_2_Entity.setKeyFields(keyFields_2_List);

		// create outSocket
		OutSocket outSocket2 = new OutSocket("out0");
		outSocket2.setSocketType("out");
//		outSocket2.setCopyOfInSocketId("in0");
		
		// add outSocket in list
		List<OutSocket> outSocketList_2 = new ArrayList<>();
		outSocketList_2.add(outSocket2);
		join_2_Entity.setOutSocketList(outSocketList_2);
		
		List<MapField> mapFieldsList_2 = new ArrayList<>();
		
		outSocket2.setMapFieldsList(mapFieldsList_2);
		
		List<PassThroughField> passThroughFieldsList2 = new ArrayList<>();
		passThroughFieldsList2.add(new PassThroughField("col1", "in0"));
		passThroughFieldsList2.add(new PassThroughField("col2_in1", "in1"));
		passThroughFieldsList2.add(new PassThroughField("col3", "in0"));
		outSocket2.setPassThroughFieldsList(passThroughFieldsList2);
		
		JoinAssembly join_2 = new JoinAssembly(join_2_Entity, parameters_2);
		
		Bucket bucket_2 = plunger2.newBucket(new Fields("col1", "col2_in1", "col3"),
				join_2.getOutLink("out", "out0", "testJoin_2"));

		List<Tuple> actual_2 = bucket_2.result().asTupleList(); // get results from
		// bucket

		// assert the actual results with expected results
		assertThat(actual_2.size(), is(1));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output_2 = new HashSet<Tuple>(actual_2);

		Set<Tuple> expectedOutput_2 = new HashSet<Tuple>();
		expectedOutput_2.add(new Tuple("C1R1", "C3R4", "C4R1"));

		Set<Tuple> unexpectedOutput_2 = new HashSet<Tuple>();
		unexpectedOutput_2.add(new Tuple("C1R1", "C2R1", "C3R1"));

		Assert.assertEquals(expectedOutput_2, output_2);
		Assert.assertNotEquals(unexpectedOutput_2, output_2);
	}
	/*
	 * Negative test cases start
	 *//**
	 * Test validation of join component for empty key fields for one of the
	 * input. The component should throw exception as it expects key fields for
	 * each input
	 */
	/*
	 * @Test public void TestJoinWithEmptyKeyFields() {
	 * 
	 * Plunger plunger = new Plunger();
	 * 
	 * Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
	 * .addTuple("C1R1", "C2R1", "C3R1") .addTuple("C1R2", "C2R2",
	 * "C3R2").build(); Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); //
	 * pipe corresponding // to an input of // join component
	 * 
	 * Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
	 * .addTuple("C1R1", "C4R1", "C5R1") .addTuple("C1R3", "C4R2",
	 * "C5R2").build(); Pipe pipe2 = plunger.newNamedPipe("pipe2", file2); //
	 * pipe corresponding // to an input of // join component
	 * 
	 * ComponentParameters parameters = new ComponentParameters();
	 * 
	 * parameters.addInputPipe(pipe1); // first input to join component
	 * parameters.addInputPipe(pipe2); // second input to join component
	 * 
	 * parameters.addInputFields(new Fields("col1", "col2", "col3"));
	 * parameters.addInputFields(new Fields("col1", "col4", "col5"));
	 * 
	 * parameters.addSourceTargetMap(0, "*", "*");
	 * parameters.addSourceTargetMap(1, "col4", "col4");
	 * parameters.addSourceTargetMap(1, "col5", "col5");
	 * 
	 * parameters.addOutputFields(new Fields("col1", "col2", "col3", "col4",
	 * "col5"));
	 * 
	 * parameters.setJoinTypes(new boolean[] { true, true });
	 * 
	 * parameters.setUnusedPorts(new ArrayList<Boolean>(Arrays.asList(false,
	 * false)));
	 * 
	 * parameters.setComponentName("testJoin"); // set the name of the //
	 * component
	 * 
	 * JoinSubAssembly join;
	 * 
	 * try { // The component should throw an exception on validation join = new
	 * JoinSubAssembly(parameters); // create a dummy component // to be tested
	 * assertThat("Validation is not working", is("Validation is working")); }
	 * catch (ParametersValidationException e) { assertThat( e.getMessage(),
	 * is("'Key Fields' parameter cannot be null for component 'testJoin'")); }
	 * }
	 *//**
	 * Test validation of join component for empty key fields for one of the
	 * input. The component should throw exception as it expects key fields for
	 * each input
	 */
	/*
	 * @Test public void TestJoinWithEmptyKeyFieldsInOneInput() {
	 * 
	 * Plunger plunger = new Plunger();
	 * 
	 * Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
	 * .addTuple("C1R1", "C2R1", "C3R1") .addTuple("C1R2", "C2R2",
	 * "C3R2").build(); Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); //
	 * pipe corresponding // to an input of // join component
	 * 
	 * Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
	 * .addTuple("C1R1", "C4R1", "C5R1") .addTuple("C1R3", "C4R2",
	 * "C5R2").build(); Pipe pipe2 = plunger.newNamedPipe("pipe2", file2); //
	 * pipe corresponding // to an input of // join component
	 * 
	 * ComponentParameters parameters = new ComponentParameters();
	 * 
	 * parameters.addInputPipe(pipe1); // first input to join component
	 * parameters.addInputPipe(pipe2); // second input to join component
	 * 
	 * parameters.addInputFields(new Fields("col1", "col2", "col3"));
	 * parameters.addInputFields(new Fields("col1", "col4", "col5"));
	 * 
	 * parameters.addSourceTargetMap(0, "*", "*");
	 * parameters.addSourceTargetMap(1, "col4", "col4");
	 * parameters.addSourceTargetMap(1, "col5", "col5");
	 * 
	 * parameters.addOutputFields(new Fields("col1", "col2", "col3", "col4",
	 * "col5"));
	 * 
	 * parameters.setJoinTypes(new boolean[] { true, true });
	 * 
	 * parameters.addKeyFields(new Fields("col1"));
	 * 
	 * parameters.setUnusedPorts(new ArrayList<Boolean>(Arrays.asList(false,
	 * false)));
	 * 
	 * parameters.setComponentName("testJoin"); // set the name of the //
	 * component
	 * 
	 * JoinSubAssembly join;
	 * 
	 * try { // The component should throw an exception on validation join = new
	 * JoinSubAssembly(parameters); // create a dummy component // to be tested
	 * assertThat("Validation is not working", is("Validation is working")); }
	 * catch (ParametersValidationException e) { assertThat( e.getMessage(), is(
	 * "Number of input links (2) does not match number of key field instances (1) for component 'testJoin'"
	 * )); } }
	 *//**
	 * Test validation of join component for empty input fields for one of
	 * the input. The component should throw exception as it expects input
	 * fields for each input
	 */
	/*
	 * @Test public void TestJoinWithEmptyInputFields() {
	 * 
	 * Plunger plunger = new Plunger();
	 * 
	 * Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
	 * .addTuple("C1R1", "C2R1", "C3R1") .addTuple("C1R2", "C2R2",
	 * "C3R2").build(); Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); //
	 * pipe corresponding // to an input of // join component
	 * 
	 * Data file2 = new DataBuilder(new Fields("col1", "col4", "col5"))
	 * .addTuple("C1R1", "C4R1", "C5R1") .addTuple("C1R3", "C4R2",
	 * "C5R2").build(); Pipe pipe2 = plunger.newNamedPipe("pipe2", file2); //
	 * pipe corresponding // to an input of // join component
	 * 
	 * ComponentParameters parameters = new ComponentParameters();
	 * 
	 * parameters.addInputPipe(pipe1); // first input to join component
	 * parameters.addInputPipe(pipe2); // second input to join component
	 * 
	 * parameters.addInputFields(new Fields("col1", "col2", "col3")); //
	 * parameters.addInputFields(new Fields("col1", "col4", "col5"));
	 * 
	 * parameters.addSourceTargetMap(0, "*", "*");
	 * parameters.addSourceTargetMap(1, "col4", "col4");
	 * parameters.addSourceTargetMap(1, "col5", "col5");
	 * 
	 * parameters.addOutputFields(new Fields("col1", "col2", "col3", "col4",
	 * "col5"));
	 * 
	 * parameters.setJoinTypes(new boolean[] { true, true });
	 * 
	 * parameters.addKeyFields(new Fields("col1")); parameters.addKeyFields(new
	 * Fields("col1"));
	 * 
	 * parameters.setUnusedPorts(new ArrayList<Boolean>(Arrays.asList(false,
	 * false)));
	 * 
	 * parameters.setComponentName("testJoin"); // set the name of the //
	 * component
	 * 
	 * JoinSubAssembly join;
	 * 
	 * try { // The component should throw an exception on validation join = new
	 * JoinSubAssembly(parameters); // create a dummy component // to be tested
	 * assertThat("Validation is not working", is("Validation is working")); }
	 * catch (ParametersValidationException e) { assertThat( e.getMessage(), is(
	 * "Number of input links (2) does not match number of input field instances (1) for component 'testJoin'"
	 * )); } }
	 *//**
	 * Test validation of join component for just one input pipe. The
	 * component should throw a validation exception as it expects two input
	 * pipes
	 */
	/*
	 * @Test public void TestJoinWithOneInputPipe() {
	 * 
	 * Plunger plunger = new Plunger();
	 * 
	 * Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
	 * .addTuple("C1R1", "C2R1", "C3R1") .addTuple("C1R2", "C2R2",
	 * "C3R2").build(); Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); //
	 * pipe corresponding // to an input of // join component
	 * 
	 * ComponentParameters parameters = new ComponentParameters();
	 * 
	 * parameters.addInputPipe(pipe1); // first input to join component
	 * 
	 * parameters.addInputFields(new Fields("col1", "col2", "col3")); //
	 * parameters.addInputFields(new Fields("col1", "col4", "col5"));
	 * 
	 * parameters.addSourceTargetMap(0, "*", "*");
	 * parameters.addSourceTargetMap(1, "col4", "col4");
	 * parameters.addSourceTargetMap(1, "col5", "col5");
	 * 
	 * parameters.addOutputFields(new Fields("col1", "col2", "col3", "col4",
	 * "col5"));
	 * 
	 * parameters.setJoinTypes(new boolean[] { true, true });
	 * 
	 * parameters.addKeyFields(new Fields("col1")); parameters.addKeyFields(new
	 * Fields("col1"));
	 * 
	 * parameters.setUnusedPorts(new ArrayList<Boolean>(Arrays.asList(false,
	 * false)));
	 * 
	 * parameters.setComponentName("testJoin"); // set the name of the //
	 * component
	 * 
	 * JoinSubAssembly join;
	 * 
	 * try { // The component should throw an exception on validation join = new
	 * JoinSubAssembly(parameters); // create a dummy component // to be tested
	 * assertThat("Validation is not working", is("Validation is working")); }
	 * catch (ParametersValidationException e) { assertThat( e.getMessage(), is(
	 * "Atleast two input links should be provided in input pipes parameter for component 'testJoin'"
	 * )); } } Negative test cases end
	 */
}
