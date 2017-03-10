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
package hydrograph.engine.cascading.tuplegenerator;

import cascading.tuple.Fields;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;

public class GenerateDataEntity implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8701171489412200765L;

	private HashMap<String, Object> parameters;

	private static final String RECORD_COUNT = "record_count";
	private static final String IS_FILE_DELIMITED = "is_file_delimited";
	private static final String FIELD_DEFAULT_VALUE = "field_default_value";
	private static final String FIELD_TO_RANGE_VALUE = "field_to_value";
	private static final String FIELD_FROM_RANGE_VALUE = "field_from_value";
	private static final String FIELD_LENGTHS_KEY = "field_length";
	private static final String FIELD_SCALE_KEY = "field_scale";
	private static final String FIELD_FORMAT = "field_format";
	private static final String FIELD_DATA_TYPE_KEY = "data_type";
	private static final String INPUT_FIELDS_KEY = "input_fields";

	public GenerateDataEntity() {
		this.parameters = new HashMap<String, Object>();
	}

	public GenerateDataEntity(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

	@SuppressWarnings("unchecked")
	public GenerateDataEntity clone() {
		return new GenerateDataEntity((HashMap<String, Object>) parameters.clone());
	}

	public long getRecordCount() {
		return (Long) parameters.get(RECORD_COUNT);
	}

	public void setRecordCount(long recCount) {
		parameters.put(RECORD_COUNT, recCount);
	}

	public boolean isDelimitedFile() {
		return (Boolean) parameters.get(IS_FILE_DELIMITED);
	}

	public String[] getFieldDefaultValue() {
		return (String[]) parameters.get(FIELD_DEFAULT_VALUE);
	}

	public void setFieldDefaultValue(String[] defaultValue) {
		parameters.put(FIELD_DEFAULT_VALUE, defaultValue);
	}

	public String[] getFieldToRangeValue() {
		return (String[]) parameters.get(FIELD_TO_RANGE_VALUE);
	}

	public void setFieldToRangeValue(String[] toValues) {
		parameters.put(FIELD_TO_RANGE_VALUE, toValues);
	}

	public String[] getFieldFromRangeValue() {
		return (String[]) parameters.get(FIELD_FROM_RANGE_VALUE);
	}

	public void setFieldFromRangeValue(String[] fromValue) {
		parameters.put(FIELD_FROM_RANGE_VALUE, fromValue);
	}

	public String[] getFieldFormat() {
		return (String[]) parameters.get(FIELD_FORMAT);
	}

	public void setFieldFormat(String[] fieldFormat) {
		parameters.put(FIELD_FORMAT, fieldFormat);
	}

	public void setFieldLength(int[] fieldLength) {
		parameters.put(FIELD_LENGTHS_KEY, fieldLength);
	}

	public int[] getFieldLength() {
		return parameters.get(FIELD_LENGTHS_KEY) == null ? null : (int[]) parameters.get(FIELD_LENGTHS_KEY);
	}

	public void setFieldScale(int[] fieldScale) {
		parameters.put(FIELD_SCALE_KEY, fieldScale);
	}

	public int[] getFieldScale() {
		return (int[]) parameters.get(FIELD_SCALE_KEY);
	}

	public void setInputFields(Fields fieldsList) {
		parameters.put(INPUT_FIELDS_KEY, fieldsList);
	}

	public Fields getInputFields() {
		return (Fields) parameters.get(INPUT_FIELDS_KEY);
	}

	public void setDataTypes(Type[] datatype) {
		parameters.put(FIELD_DATA_TYPE_KEY, datatype);
	}

	public Type[] getDataTypes() {

		return parameters.get(FIELD_DATA_TYPE_KEY) == null ? null : (Type[]) parameters.get(FIELD_DATA_TYPE_KEY);
	}
}
