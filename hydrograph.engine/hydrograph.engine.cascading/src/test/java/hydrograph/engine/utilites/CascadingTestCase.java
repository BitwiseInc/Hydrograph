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
package hydrograph.engine.utilites;

import cascading.tuple.Fields;
import cascading.tuple.TupleEntryIterator;
import junit.framework.TestCase;

import java.util.ArrayList;

public class CascadingTestCase extends TestCase {

	public static void validateFileLength(TupleEntryIterator iterator, int size) {

		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		assertEquals("File Length is Not Equal", size, count);
	}

	public static void validateFieldLength(TupleEntryIterator iterator, int size) {

		Fields fields = iterator.getTupleEntry().getFields();
		assertEquals("Field Length is Not Equal", size, fields.size());
	}

	public static void validateFieldValue(TupleEntryIterator iterator, String field, String[] expected) {

		boolean isMatch = false;
		ArrayList<String> dummyList = convertIteratorToList(iterator, field);

		if (expected.length == dummyList.size()) {

			for (int i = 0; i < expected.length; i++) {
				if (expected[i].equals(dummyList.get(i)))
					isMatch = true;
				else {
					isMatch = false;
					break;
				}
			}
		} else {
			isMatch = false;
		}
		assertTrue("\"" + field.toUpperCase() + "\"" + " Field Not Match", isMatch);

	}

	private static ArrayList<String> convertIteratorToList(TupleEntryIterator iterator, String field) {
		ArrayList<String> list = new ArrayList<String>();
		while (iterator.hasNext()) {
			iterator.next();
			list.add(iterator.getTupleEntry().getString(field));
		}
		return list;
	}
}
