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
package hydrograph.engine.cascading.filters;

import cascading.operation.Filter;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import com.hotels.plunger.Bucket;
import com.hotels.plunger.Data;
import com.hotels.plunger.DataBuilder;
import com.hotels.plunger.Plunger;
import hydrograph.engine.cascading.assembly.handlers.FilterCustomHandler;
import hydrograph.engine.cascading.assembly.handlers.RecordFilterHandlerBase;
import hydrograph.engine.cascading.filters.RecordFilter;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CustomFilterTest {

	@Test
	public void itShouldRemoveRecordsSpecifiedByCustomFilter() {

		Properties properties = new Properties();
		properties.setProperty("value", "100");
		Fields inputFields = new Fields("id", "name", "surname");
		RecordFilterHandlerBase filterHandler = new FilterCustomHandler(inputFields,
				"hydrograph.engine.cascading.test.customtransformclasses.CustomFilter", properties);

		Filter filter = new RecordFilter(filterHandler,null);

		Plunger plunger = new Plunger();
		Data data = new DataBuilder(new Fields("id", "name", "surname")).addTuple(101, "abc", "pqr")
				.addTuple(99, "def", "stu").addTuple(100, "pqr", "xyz").build();
		Pipe words = plunger.newNamedPipe("records", data);
		Pipe pipesToTest = new Each(words, filter);
		Bucket bucket = plunger.newBucket(new Fields("id", "name", "surname"), pipesToTest);
		List<Tuple> actual = bucket.result().asTupleList();
		assertNotEquals(new Tuple(101, "abc", "pqr"), actual.get(0));
		assertEquals(new Tuple(99, "def", "stu"), actual.get(0));
		assertEquals(new Tuple(100, "pqr", "xyz"), actual.get(1));
	}

}
