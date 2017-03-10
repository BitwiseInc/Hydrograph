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
import hydrograph.engine.transformation.userfunctions.base.ReusableRow;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author Ganesh
 *
 */
public class ReusableRowHelper {

	@SuppressWarnings("rawtypes")
	public static ReusableRow extractFromTuple(int[] tuplePos,
			Tuple sourceTuple, ReusableRow target) {

		if (tuplePos == null || sourceTuple == null || target == null) {
			return target;
		}

		int counter = -1;
		for (int position : tuplePos) {
			counter = counter + 1;
			target.setField(counter,
					(Comparable) sourceTuple.getObject(position));
		}
		return target;
	}

	@SuppressWarnings("rawtypes")
	public static ReusableRow extractFromTuple(Tuple sourceTuple,
			ReusableRow target) {

		if (sourceTuple == null || target == null) {
			return target;
		}

		int counter = 0;
		while (counter < sourceTuple.size()) {
			target.setField(counter,
					(Comparable) sourceTuple.getObject(counter));
			counter = counter + 1;
		}
		return target;
	}

	public static ArrayList<String> getListFromFields(Fields fields) {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < fields.size(); i++) {
			result.add(fields.get(i).toString());
		}
		return result;
	}

	public static LinkedHashSet<String> getLinkedSetFromFields(Fields fields) {
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for (int i = 0; i < fields.size(); i++) {
			result.add(fields.get(i).toString());
		}
		return result;
	}

	/**
	 * Sets the values in {@link TupleEntry} object {@code target} from the
	 * {@link ReusableRow} object {@code row}
	 * 
	 * @param target
	 *            the {@link TupleEntry} object in which the values need to be
	 *            set
	 * @param row
	 *            the {@link ReusableRow} object from which the values need to
	 *            be copied over
	 * @return the updated {@link TupleEntry} object
	 */
	public static TupleEntry setTupleEntryFromResuableRowAndReset(
			TupleEntry target, ReusableRow row) {

		if (row == null) {
			return target;
		}

		for (String fieldName : row.getFieldNames()) {
			target.setRaw(fieldName, row.getField(fieldName));
		}
		row.reset();
		return target;
	}

	public static TupleEntry setTupleEntryFromResuableRowAndReset(
			TupleEntry target, ReusableRow row, int[] fieldPositions) {

		if (row == null) {
			return target;
		}

		int counter = 0;
		for (Object value : row.getFields()) {
			if (counter <= fieldPositions.length - 1) {
				if (fieldPositions[counter] != -1) {
					target.setRaw(fieldPositions[counter], value);
					counter++;
				}

			}
		}
		row.reset();
		return target;
	}

	/**
	 * Sets the values in {@link TupleEntry} object {@code target} from the
	 * {@link ReusableRow} object {@code row}
	 * 
	 * @param target
	 *            the {@link TupleEntry} object in which the values need to be
	 *            set
	 * @param row
	 *            the {@link ReusableRow} object from which the values need to
	 *            be copied over
	 * @return the updated {@link TupleEntry} object
	 */

	public static TupleEntry setTupleEntryFromResuableRow(TupleEntry target,
			ReusableRow row) {

		if (row == null) {
			return target;
		}

		for (String fieldName : row.getFieldNames()) {
			target.setRaw(fieldName, row.getField(fieldName));
		}
		return target;
	}

	/**
	 * Sets the values in {@link TupleEntry} object {@code target} from the
	 * {@link ReusableRow} object {@code row}. The field names can be changed
	 * from source to target by specifying the {@code fieldMapping} object of
	 * type {@link Map}<{@link String}, {@link String}>. The
	 * {@code fieldMapping} object should specify the field mapping as
	 * Map&lt;SourceFieldName, TargetFieldName&gt;
	 * 
	 * @param target
	 *            the {@link TupleEntry} object in which the values need to be
	 *            set
	 * @param row
	 *            the {@link ReusableRow} object from which the values need to
	 *            be copied over
	 * @param fieldMapping
	 *            the map object specifying the field mapping as
	 *            Map&lt;SourceFieldName, TargetFieldName&gt;
	 * @return the updated {@link TupleEntry} object
	 */
	public static TupleEntry setTupleEntryFromResuableRowWithNewFieldNames(
			TupleEntry target, ReusableRow row, Map<String, String> fieldMapping) {

		if (row == null) {
			return target;
		}

		if (fieldMapping == null) {
			return setTupleEntryFromResuableRowAndReset(target, row);
		}

		// The object row will only contain the values for map fields. Iterate
		// over the map and fetch values from row using the key field, which
		// corresponds to the SourceFieldName. Set the value in target, i.e.
		// TupleEntry using the value field from the map which corresponds to
		// the TargetFieldName
		for (String key : fieldMapping.keySet()) {
			target.setRaw(fieldMapping.get(key), row.getField(key));
		}
		return target;
	}

	/**
	 * Sets the values in {@link TupleEntry} object {@code target} from the
	 * ArrayList of {@link ReusableRow} object {@code row}
	 * 
	 * @param target
	 *            the {@link TupleEntry} object in which the values need to be
	 *            set
	 * @param row
	 *            the ArrayList of {@link ReusableRow} object from which the
	 *            values need to be copied over
	 * @return the updated {@link TupleEntry} object
	 */
	public static TupleEntry setTupleEntryFromResuableRowsAndReset(
			TupleEntry target, ArrayList<ReusableRow> rows) {

		for (ReusableRow row : rows) {
			setTupleEntryFromResuableRowAndReset(target, row);
		}
		return target;
	}

	public static TupleEntry setTupleEntryFromResuableRowsAndReset(
			TupleEntry target, ArrayList<ReusableRow> rows,
			ArrayList<int[]> fieldPositions) {

		int counter = 0;
		for (ReusableRow row : rows) {
			setTupleEntryFromResuableRowAndReset(target, row,
					fieldPositions.get(counter));
			counter++;
		}
		return target;
	}

	/**
	 * Sets the values in Operation Row of type {@link ReusableRow} from Output
	 * Rows of type {@link ArrayList&lt;ReusableRow&gt;} on which operations is
	 * performed.
	 * 
	 * @param allOutputRow
	 * @param operationRow
	 */
	public static void extractOperationRowFromAllOutputRow(
			ArrayList<ReusableRow> allOutputRow, ReusableRow operationRow) {

		for (String operationRowFieldName : operationRow.getFieldNames()) {
			getValueFromFieldName(allOutputRow, operationRowFieldName,
					operationRow);
		}
	}

	private static void getValueFromFieldName(
			ArrayList<ReusableRow> allOutputRow, String operationRowFieldName,
			ReusableRow operationRow) {
		for (ReusableRow eachReusableRow : allOutputRow) {
			if (eachReusableRow.getFieldNames().contains(operationRowFieldName)) {
				operationRow.setField(operationRowFieldName,
						eachReusableRow.getField(operationRowFieldName));
			}
		}
	}

	public static ReusableRow setOperationRowFromPassThroughAndMapRow(
			Map<String, String> mapFields, ReusableRow mapRow,
			ReusableRow passThroughRow, ReusableRow operationRow) {

		if (operationRow == null) {
			return null;
		}

		for (String fieldName : mapRow.getFieldNames()) {
			operationRow.setField(mapFields.get(fieldName),
					mapRow.getField(fieldName));
		}

		for (String fieldName : passThroughRow.getFieldNames()) {
			operationRow
					.setField(fieldName, passThroughRow.getField(fieldName));
		}

		return operationRow;

	}
}
