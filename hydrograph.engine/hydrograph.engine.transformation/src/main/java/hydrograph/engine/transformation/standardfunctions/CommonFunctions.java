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
package hydrograph.engine.transformation.standardfunctions;

import hydrograph.engine.transformation.userfunctions.base.ReusableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;

/**
 * The Class CommonFunctions.
 *
 * @author Bitwise
 *
 */
public class CommonFunctions {

	private static Logger LOG = LoggerFactory.getLogger(CommonFunctions.class);

	/**
	 * This method can be used to simulate the vector comparison like old ==
	 * new, where 'old' and 'new' are vectors with equal number of sub-fields in
	 * them. The field names and data types should match for a correct
	 * comparison
	 * 
	 * @param inputRow
	 *            the {@link ReusableRow} object whose fields are to be compared
	 * @param prefix1
	 *            the prefix of the vector 1 to be compared
	 * @param prefix2
	 *            the prefix of the vector 2 to be compared
	 * @return true if all the field values match <br>
	 *         false is any field value does not match
	 */
	@Deprecated
	public static boolean vectorCompare(ReusableRow inputRow, String prefix1,
			String prefix2) {

		// TreeSet creates a sorted and unique set of fields. Duplicates are not
		// added
		TreeSet<String> prefix1FieldNames = new TreeSet<String>();
		TreeSet<String> prefix2FieldNames = new TreeSet<String>();

		boolean namesMatch = true;

		for (String fieldName : inputRow.getFieldNames()) {
			if (fieldName.startsWith(prefix1)) {
				prefix1FieldNames.add(fieldName);
				// check of the same field name exists for prefix 2
				if (!inputRow.getFieldNames().contains(
						prefix2 + fieldName.substring(prefix1.length()))) {
					namesMatch = false;
				}
			} else if (fieldName.startsWith(prefix2)) {
				prefix2FieldNames.add(fieldName);

				// check of the same field name exists for prefix 1
				if (!inputRow.getFieldNames().contains(
						prefix1 + fieldName.substring(prefix2.length()))) {
					namesMatch = false;
				}
			}
		}

		// check the size of both the vectors
		if (prefix1FieldNames.size() != prefix2FieldNames.size()) {
			throw new IllegalArgumentException("Number of fields in '"
					+ prefix1 + "' (" + prefix1FieldNames.size()
					+ ") does not match number of fields in '" + prefix2
					+ "' (" + prefix2FieldNames.size() + ")");
		}

		// check the field names in both the vectors
		if (!namesMatch) {
			throw new IllegalArgumentException("Fields names in '" + prefix1
					+ "' (" + prefix1FieldNames
					+ ") does not match field names in '" + prefix2 + "' ("
					+ prefix2FieldNames + ")");
		}

		boolean returnValue = true;
		for (String prefix1FieldName : prefix1FieldNames) {
			// generate the field name for prefix2
			String prefix2FieldName = prefix2
					+ prefix1FieldName.substring(prefix1.length());

			// test for nulls
			if (inputRow.getField(prefix1FieldName) == null
					&& inputRow.getField(prefix2FieldName) != null) {

				LOG.debug("vectorCompare: " + prefix1FieldName + ": null"
						+ " | " + prefix2FieldName + ": "
						+ inputRow.getField(prefix2FieldName).toString());

				returnValue = false;
				break; // break out of the for loop at first non matching
						// occurrence
			}

			if (inputRow.getField(prefix2FieldName) == null
					&& inputRow.getField(prefix1FieldName) != null) {

				LOG.debug("vectorCompare: " + prefix1FieldName + ": "
						+ inputRow.getField(prefix1FieldName).toString()
						+ " | " + prefix2FieldName + ": null");

				returnValue = false;
				break; // break out of the for loop at first non matching
						// occurrence
			}

			// both values are null, continue comparison
			if (inputRow.getField(prefix1FieldName) == null
					&& inputRow.getField(prefix2FieldName) == null) {
				continue;
			}
			if (!inputRow.getField(prefix1FieldName).equals(
					inputRow.getField(prefix2FieldName))) {

				LOG.debug("vectorCompare: " + prefix1FieldName + ": "
						+ inputRow.getField(prefix1FieldName).toString()
						+ " | " + prefix2FieldName + ": "
						+ inputRow.getField(prefix2FieldName).toString());

				returnValue = false;
				break; // break out of the for loop at first non matching
						// occurrence
			}
		}
		return returnValue;
	}
}
