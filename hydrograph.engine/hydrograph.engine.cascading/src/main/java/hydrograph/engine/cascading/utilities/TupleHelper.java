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
package hydrograph.engine.cascading.utilities;

import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;

public class TupleHelper {

	public static Tuple setTuplePart(int[] fieldsPosition, Tuple source,
			Tuple target) {

		int i = 0;
		for (int position : fieldsPosition) {
			target.set(i, source.getObject(position));
			i++;
		}
		return target;
	}

	public static TupleEntry initializeTupleEntry(Fields fields) {

		Tuple t = new Tuple(new Object[fields.size()]);

		return new TupleEntry(fields, t);

	}

	public static Tuple setTupleOnPositions(int[] sourcePositions,
			Tuple source, int[] targetPositions, Tuple target) {

		int i = -1;
		for (int sourcePosition : sourcePositions) {
			i = i + 1;
			target.set(targetPositions[i], source.getObject(sourcePosition));
		}
		return target;
	}

}
