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
import hydrograph.engine.cascading.filters.JoinGetUnmatchedRecordsFilter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class JoinGetUnmatchedRecordsFilterTest {

	@Test
	public void itShouldRemoveAllMatchedRecords() {

		Filter filter = new JoinGetUnmatchedRecordsFilter();

		Plunger plunger = new Plunger();
		Data data = new DataBuilder(new Fields("id", "name", "id1", "name1")).addTuple(1, "abc", null, null)
				.addTuple(1, "pqr", 1, "mum").addTuple(null, null, 3, "walker").build();
		Pipe words = plunger.newNamedPipe("records", data);
		Pipe pipesToTest = new Each(words, filter);
		Bucket bucket = plunger.newBucket(new Fields("id", "name", "id1", "name1"), pipesToTest);
		List<Tuple> actual = bucket.result().asTupleList();

		assertEquals(new Tuple(1, "abc", null, null), actual.get(0));
		assertNotEquals(new Tuple(1, "pqr", 1, "mum"), actual.get(1));
		assertEquals(new Tuple(null, null, 3, "walker"), actual.get(1));
	}

}
