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

import java.lang.reflect.Type;

public class FieldEntity {
	private Type type;
	private String defaultValue;
	private String rangeFromValue;
	private String rangeToValue;
	private int fieldLength;
	private String fieldFormat;
	private int fieldScale;

	public FieldEntity() {		
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getRangeFromValue() {
		return rangeFromValue;
	}

	public void setRangeFromValue(String rangeFromValue) {
		this.rangeFromValue = rangeFromValue;
	}

	public String getRangeToValue() {
		return rangeToValue;
	}

	public void setRangeToValue(String rangeToValue) {
		this.rangeToValue = rangeToValue;
	}

	public int getFieldLength() {
		return fieldLength;
	}

	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}

	public String getFieldFormat() {
		return fieldFormat;
	}

	public void setFieldFormat(String fieldFormat) {
		this.fieldFormat = fieldFormat;
	}

	public int getFieldScale() {
		return fieldScale;
	}

	public void setFieldScale(int fieldScale) {
		this.fieldScale = fieldScale;
	}

}
