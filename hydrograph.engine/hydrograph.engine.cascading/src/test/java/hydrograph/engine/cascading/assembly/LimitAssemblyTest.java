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
import hydrograph.engine.cascading.assembly.LimitAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.LimitEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test limit sub assembly. The tests are written using plunger framework
 * 
 * @author Prabodh
 */
public class LimitAssemblyTest {

	@Before
	public void setup() {
		// TODO: add setup related code here
	}

	/**
	 * Test simple limit operation
	 */
	@Test
	public void TestSimpleLimitOperation() {
		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", 100).addTuple("C1R1", "C2R2", 100)
				.addTuple("C1R1", "C2R3", 100).addTuple("C1R1", "C2R4", 100)
				.addTuple("C1R1", "C2R5", 100).addTuple("C1R1", "C2R6", 100)
				.addTuple("C1R1", "C2R7", 100).addTuple("C1R1", "C2R8", 100)
				.addTuple("C1R1", "C2R9", 100).addTuple("C1R1", "C2R10", 100)
				.addTuple("C1R1", "C2R11", 100).addTuple("C1R1", "C2R12", 100)
				.addTuple("C1R1", "C2R13", 100).build();

		// pipe corresponding to an input of limit component
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));

		LimitEntity limitEntity = new LimitEntity();
		limitEntity.setComponentId("limitTest");
		limitEntity.setMaxRecord((long) 10);

		// create outSocket
		OutSocket outSocket1 = new OutSocket("out0");
		List<OutSocket> outSocketList = new ArrayList<>();
		outSocketList.add(outSocket1);
		limitEntity.setOutSocketList(outSocketList);

		LimitAssembly limit = new LimitAssembly(limitEntity, parameters);

		// create bucket for the limit sub assembly
		Bucket bucket = plunger.newBucket(new Fields("col1", "col2", "col3"),
				limit);
		List<Tuple> actual = bucket.result().asTupleList(); // get results from
															// bucket

		// assert the actual results with expected results
		assertThat(actual.size(), is(10));
	}

	@After
	public void cleanup() {
		// TODO: add cleanup related code here
	}
}
