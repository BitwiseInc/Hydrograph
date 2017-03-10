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
import hydrograph.engine.cascading.assembly.CloneAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.CloneEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CloneAssemblyTest {

	@Test
	public void itShouldPopulateCorrectParameters() {

		Plunger plunger = new Plunger();

		Data file1 = new DataBuilder(new Fields("col1", "col2", "col3"))
				.addTuple("C1R1", "C2R1", "C3R1")
				.addTuple("C1R1", "C2R2", "C3R2")
				.addTuple("C1R1", "C2R3", "C3R3").build();
		Pipe pipe1 = plunger.newNamedPipe("pipe1", file1);

		ArrayList<Boolean> list = new ArrayList<Boolean>();
		list.add(false);

		ArrayList<String> socketIds = new ArrayList<String>();
		socketIds.add("sdf");
		socketIds.add("sdf1");

		CloneEntity cloneEntity = new CloneEntity();
		cloneEntity.setComponentId("cloneTest");
		cloneEntity.setBatch("1");
		List<OutSocket> outSocketList = new ArrayList<OutSocket>();
		outSocketList.add(new OutSocket("out1"));
		outSocketList.add(new OutSocket("out2"));
		cloneEntity.setOutSocketList(outSocketList);
		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipe1);
		parameters.addInputFields(new Fields("col1", "col2", "col3"));
		parameters.addOutputFields(new Fields("col1", "col2", "col3"));

		CloneAssembly clone = new CloneAssembly(cloneEntity, parameters);

		Bucket bucket1 = plunger.newBucket(new Fields("col1", "col2", "col3"),
				clone.getOutLink("out", "out1", cloneEntity.getComponentId()));

		Bucket bucket2 = plunger.newBucket(new Fields("col1", "col2", "col3"),
				clone.getOutLink("out", "out2", cloneEntity.getComponentId()));

		// test 1st copy
		List<Tuple> actual = bucket1.result().asTupleList();
		assertThat(actual.size(), is(3));
		assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));

		// test 2nd copy
		actual = bucket2.result().asTupleList();
		assertThat(actual.size(), is(3));
		assertThat(actual.get(0), is(new Tuple("C1R1", "C2R1", "C3R1")));

	}
}