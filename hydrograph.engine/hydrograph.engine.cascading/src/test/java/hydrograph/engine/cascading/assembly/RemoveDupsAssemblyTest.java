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
import hydrograph.engine.cascading.assembly.RemoveDupsAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.RemoveDupsEntity;
import hydrograph.engine.core.component.entity.elements.KeyField;
import hydrograph.engine.core.component.entity.elements.OutSocket;
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
 * Test dedup sub assembly. The tests are written using plunger framework
 * 
 * @author Prabodh
 */
public class RemoveDupsAssemblyTest {

	@Before
	public void setup() {
		// TODO: setup related things go here
	}

	/**
	 * Test dedup component's out port with 'keep first' option
	 */
	@Test
	public void RemoveDupsKeepFirstWithOutPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R2").addTuple("C1R1", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// dedup component

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(false);

		RemoveDupsEntity removeDupsEntity = new RemoveDupsEntity();
		removeDupsEntity.setComponentId("dedupTest");

		KeyField keyField = new KeyField();
		keyField.setName("col1");
		removeDupsEntity.setKeyFields(new KeyField[] { keyField });
		// keep just the first record. Discard remaining duplicate records
		removeDupsEntity.setKeep("first");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("out0", "out"));
		removeDupsEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		RemoveDupsAssembly dedup = new RemoveDupsAssembly(removeDupsEntity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"), dedup);

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(1));
		assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));
	}

	/**
	 * Test dedup component's out port with 'keep last' option
	 */
	@Test
	public void RemoveDupsKeepLastWithOutPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R2").addTuple("C1R1", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// dedup component

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(false);

		RemoveDupsEntity removeDupsEntity = new RemoveDupsEntity();
		removeDupsEntity.setComponentId("dedupTest");

		KeyField keyField = new KeyField();
		keyField.setName("col1");
		removeDupsEntity.setKeyFields(new KeyField[] { keyField });
		// keep just the first record. Discard remaining duplicate records
		removeDupsEntity.setKeep("last");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("out0", "out"));
		removeDupsEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		RemoveDupsAssembly dedup = new RemoveDupsAssembly(removeDupsEntity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"), dedup); // create
																						// bucket
																						// for
																						// the
																						// dedup
																						// sub
																						// assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(1));
		assertThat(actual.get(0), is(new Tuple("C1R1", "C2R3", "C3R3")));
	}

	/**
	 * Test dedup component's out port with 'keep unique' option
	 */
	@Test
	public void RemoveDupsKeepUniqueOnlyWithOutPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R2").addTuple("C1R3", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// dedup component

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(false);

		RemoveDupsEntity removeDupsEntity = new RemoveDupsEntity();
		removeDupsEntity.setComponentId("dedupTest");

		KeyField keyField = new KeyField();
		keyField.setName("col1");
		removeDupsEntity.setKeyFields(new KeyField[] { keyField });
		// keep just the first record. Discard remaining duplicate records
		removeDupsEntity.setKeep("unique");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("out0", "out"));
		removeDupsEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		RemoveDupsAssembly dedup = new RemoveDupsAssembly(removeDupsEntity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"), dedup); // create
																						// bucket
																						// for
																						// the
																						// dedup
																						// sub
																						// assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(1));
		assertThat(actual.get(0), is(new Tuple("C1R3", "C2R3", "C3R3")));
	}

	/**
	 * Test dedup component's out port with 'keep first' option with more than
	 * one key
	 */
	@Test
	public void RemoveDupsKeepFirstWithMultipleKeysWithOutPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R1").addTuple("C1R1", "C2R2", "C3R2").addTuple("C1R1", "C2R1", "C3R2")
				.addTuple("C1R1", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// dedup component

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(false);

		RemoveDupsEntity removeDupsEntity = new RemoveDupsEntity();
		removeDupsEntity.setComponentId("dedupTest");

		KeyField keyField1 = new KeyField();
		keyField1.setName("col1");
		KeyField keyField2 = new KeyField();
		keyField2.setName("col2");
		removeDupsEntity.setKeyFields(new KeyField[] { keyField1, keyField2 });
		// keep just the first record. Discard remaining duplicate records
		removeDupsEntity.setKeep("first");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("out0", "out"));
		removeDupsEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		RemoveDupsAssembly dedup = new RemoveDupsAssembly(removeDupsEntity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"), dedup); // create
																						// bucket
																						// for
																						// the
																						// dedup
																						// sub
																						// assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(3));
		// assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1"));
		expectedOutput.add(new Tuple("C1R1", "C2R2", "C3R1"));
		expectedOutput.add(new Tuple("C1R1", "C2R3", "C3R3"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test dedup component's out port with 'keep last' option with more than
	 * one key
	 */
	@Test
	public void RemoveDupsKeepLastWithMultipleKeysWithOutPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R1").addTuple("C1R1", "C2R2", "C3R2").addTuple("C1R1", "C2R1", "C3R2")
				.addTuple("C1R1", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// dedup component

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(false);

		RemoveDupsEntity removeDupsEntity = new RemoveDupsEntity();
		removeDupsEntity.setComponentId("dedupTest");

		KeyField keyField1 = new KeyField();
		keyField1.setName("col1");
		KeyField keyField2 = new KeyField();
		keyField2.setName("col2");
		removeDupsEntity.setKeyFields(new KeyField[] { keyField1, keyField2 });
		// keep just the first record. Discard remaining duplicate records
		removeDupsEntity.setKeep("last");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("out0", "out"));
		removeDupsEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		RemoveDupsAssembly dedup = new RemoveDupsAssembly(removeDupsEntity, parameters);

		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"), dedup); // create
																						// bucket
																						// for
																						// the
																						// dedup
																						// sub
																						// assembly

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(3));
		// assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R2"));
		expectedOutput.add(new Tuple("C1R1", "C2R2", "C3R2"));
		expectedOutput.add(new Tuple("C1R1", "C2R3", "C3R3"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test dedup component's unused port with 'keep first' option
	 */
	@Test
	public void RemoveDupsKeepFirstWithUnusedPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R2").addTuple("C1R1", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// dedup component

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(true);

		RemoveDupsEntity removeDupsEntity = new RemoveDupsEntity();
		removeDupsEntity.setComponentId("dedupTest");

		KeyField keyField1 = new KeyField();
		keyField1.setName("col1");
		removeDupsEntity.setKeyFields(new KeyField[] { keyField1 });
		// keep just the first record. Discard remaining duplicate records
		removeDupsEntity.setKeep("first");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("unused0", "unused"));
		outSocketList.add(new OutSocket("out0", "out"));
		removeDupsEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		RemoveDupsAssembly dedup = new RemoveDupsAssembly(removeDupsEntity, parameters);

		// create bucket for the dedup sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				dedup.getOutLink("unused", "unused0", removeDupsEntity.getComponentId())); // explicitly
																						// set
																						// the
																						// unused
																						// port.
																						// Default
																						// is
																						// out
																						// port

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));
		// assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R2", "C3R2"));
		expectedOutput.add(new Tuple("C1R1", "C2R3", "C3R3"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test dedup component's unused port with 'keep last' option
	 */
	@Test
	public void RemoveDupsKeepLastWithUnusedPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R2").addTuple("C1R1", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// dedup component

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(true);

		RemoveDupsEntity removeDupsEntity = new RemoveDupsEntity();
		removeDupsEntity.setComponentId("dedupTest");

		KeyField keyField1 = new KeyField();
		keyField1.setName("col1");
		removeDupsEntity.setKeyFields(new KeyField[] { keyField1 });
		// keep just the first record. Discard remaining duplicate records
		removeDupsEntity.setKeep("last");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("out0", "out"));
		outSocketList.add(new OutSocket("unused0", "unused"));
		removeDupsEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		RemoveDupsAssembly dedup = new RemoveDupsAssembly(removeDupsEntity, parameters);

		// create bucket for the dedup sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				dedup.getOutLink("unused", "unused0", removeDupsEntity.getComponentId())); // explicitly
																						// set
																						// the
																						// unused
																						// port.
																						// Default
																						// is
																						// out
																						// port

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1"));
		expectedOutput.add(new Tuple("C1R1", "C2R2", "C3R2"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test dedup component's unused port with 'keep unique' option
	 */
	@Test
	public void RemoveDupsKeepUniqueOnlyWithUnusedPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R2").addTuple("C1R3", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// dedup component

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(true);

		RemoveDupsEntity removeDupsEntity = new RemoveDupsEntity();
		removeDupsEntity.setComponentId("dedupTest");

		KeyField keyField1 = new KeyField();
		keyField1.setName("col1");
		removeDupsEntity.setKeyFields(new KeyField[] { keyField1 });
		// keep just the first record. Discard remaining duplicate records
		removeDupsEntity.setKeep("unique");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("unused0", "unused"));
		outSocketList.add(new OutSocket("out0", "out"));
		removeDupsEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		RemoveDupsAssembly dedup = new RemoveDupsAssembly(removeDupsEntity, parameters);

		// create bucket for the dedup sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				dedup.getOutLink("unused", "unused0", removeDupsEntity.getComponentId())); // explicitly
																						// set
																						// the
																						// unused
																						// port.
																						// Default
																						// is
																						// out
																						// port

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1"));
		expectedOutput.add(new Tuple("C1R1", "C2R2", "C3R2"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test dedup component's unused port with 'keep first' option with more
	 * than one key
	 */
	@Test
	public void RemoveDupsKeepFirstWithMultipleKeysWithUnusedPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R1").addTuple("C1R1", "C2R2", "C3R2").addTuple("C1R1", "C2R1", "C3R2")
				.addTuple("C1R1", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// dedup component

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(true);

		RemoveDupsEntity removeDupsEntity = new RemoveDupsEntity();
		removeDupsEntity.setComponentId("dedupTest");

		KeyField keyField1 = new KeyField();
		keyField1.setName("col1");
		KeyField keyField2 = new KeyField();
		keyField2.setName("col2");
		removeDupsEntity.setKeyFields(new KeyField[] { keyField1, keyField2 });
		// keep just the first record. Discard remaining duplicate records
		removeDupsEntity.setKeep("first");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("unused0", "unused"));
		outSocketList.add(new OutSocket("out0", "out"));
		removeDupsEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		RemoveDupsAssembly dedup = new RemoveDupsAssembly(removeDupsEntity, parameters);

		// create bucket for the dedup sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				dedup.getOutLink("unused", "unused0", removeDupsEntity.getComponentId())); // explicitly
																						// set
																						// the
																						// unused
																						// port.
																						// Default
																						// is
																						// out
																						// port

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));
		// assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R2"));
		expectedOutput.add(new Tuple("C1R1", "C2R2", "C3R2"));

		Assert.assertEquals(expectedOutput, output);
	}

	/**
	 * Test dedup component's unused port with 'keep last' option with more than
	 * one key
	 */
	@Test
	public void RemoveDupsKeepLastWithMultipleKeysWithUnusedPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R1").addTuple("C1R1", "C2R2", "C3R2").addTuple("C1R1", "C2R1", "C3R2")
				.addTuple("C1R1", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// dedup component

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(true);

		RemoveDupsEntity removeDupsEntity = new RemoveDupsEntity();
		removeDupsEntity.setComponentId("dedupTest");

		KeyField keyField1 = new KeyField();
		keyField1.setName("col1");
		KeyField keyField2 = new KeyField();
		keyField2.setName("col2");
		removeDupsEntity.setKeyFields(new KeyField[] { keyField1, keyField2 });
		// keep just the first record. Discard remaining duplicate records
		removeDupsEntity.setKeep("last");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("unused0", "unused"));
		outSocketList.add(new OutSocket("out0", "out"));
		removeDupsEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		RemoveDupsAssembly dedup = new RemoveDupsAssembly(removeDupsEntity, parameters);

		// create bucket for the dedup sub assembly
		// explicitly set the unused port. Default is out port
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				dedup.getOutLink("unused", "unused0", removeDupsEntity.getComponentId()));

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(2));
		// assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R1", "C3R1"));
		expectedOutput.add(new Tuple("C1R1", "C2R2", "C3R1"));

		Assert.assertEquals(expectedOutput, output);
	}
	
	@Test
	public void RemoveDupsKeepLastWithNullKeyWithOutPort() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3")).addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R1").addTuple("C1R1", "C2R2", "C3R2").addTuple("C1R1", "C2R1", "C3R2")
				.addTuple("C1R1", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1); // pipe corresponding
															// to an input of
															// dedup component

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(true);

		RemoveDupsEntity removeDupsEntity = new RemoveDupsEntity();
		removeDupsEntity.setComponentId("dedupTest");

		removeDupsEntity.setKeyFields(null);
		// keep just the first record. Discard remaining duplicate records
		removeDupsEntity.setKeep("last");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("unused0", "unused"));
		outSocketList.add(new OutSocket("out0", "out"));
		removeDupsEntity.setOutSocketList(outSocketList);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		RemoveDupsAssembly dedup = new RemoveDupsAssembly(removeDupsEntity, parameters);

		// create bucket for the dedup sub assembly
		// explicitly set the unused port. Default is out port
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				dedup.getOutLink("out", "out0", removeDupsEntity.getComponentId()));

		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(1));

		// Use HashSet so that order of fields does not matter while comparison
		Set<Tuple> output = new HashSet<Tuple>(actual);

		Set<Tuple> expectedOutput = new HashSet<Tuple>();
		expectedOutput.add(new Tuple("C1R1", "C2R3", "C3R3"));

		Assert.assertEquals(expectedOutput, output);
	}
}
