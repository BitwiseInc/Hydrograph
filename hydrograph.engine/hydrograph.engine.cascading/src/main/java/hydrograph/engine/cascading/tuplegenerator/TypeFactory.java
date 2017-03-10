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

import cascading.tuple.coerce.*;
import cascading.tuple.type.DateType;
import hydrograph.engine.cascading.coercetype.ImplicitBigDecimalType;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TypeFactory {

	private final static Map<Type, FieldTypeEnum> FIELD_TYPE_DICTIONARY;

	static {
		FIELD_TYPE_DICTIONARY = new HashMap<Type, FieldTypeEnum>();
		FIELD_TYPE_DICTIONARY.put(String.class, FieldTypeEnum.STRING);
		FIELD_TYPE_DICTIONARY.put(Integer.class, FieldTypeEnum.INTEGER);
		FIELD_TYPE_DICTIONARY.put(BigDecimal.class, FieldTypeEnum.BIGDECIMAL);
		FIELD_TYPE_DICTIONARY.put(Date.class, FieldTypeEnum.DATE);
		FIELD_TYPE_DICTIONARY.put(Double.class, FieldTypeEnum.DOUBLE);
		FIELD_TYPE_DICTIONARY.put(Float.class, FieldTypeEnum.FLOAT);
		FIELD_TYPE_DICTIONARY.put(Short.class, FieldTypeEnum.SHORT);
		FIELD_TYPE_DICTIONARY.put(Long.class, FieldTypeEnum.LONG);
		FIELD_TYPE_DICTIONARY.put(Boolean.class, FieldTypeEnum.BOOLEAN);
	}

	public static FieldTypeEnum getFieldType(Type type) {
		if (type instanceof DateType || type == Date.class) {
			return FIELD_TYPE_DICTIONARY.get(Date.class);
		} else if (type instanceof StringCoerce || type == String.class) {
			return FIELD_TYPE_DICTIONARY.get(String.class);
		} else if (type instanceof IntegerObjectCoerce || type == Integer.class) {
			return FIELD_TYPE_DICTIONARY.get(Integer.class);
		} else if (type instanceof ImplicitBigDecimalType || type == BigDecimal.class) {
			return FIELD_TYPE_DICTIONARY.get(BigDecimal.class);
		} else if (type instanceof DoubleObjectCoerce || type == Double.class) {
			return FIELD_TYPE_DICTIONARY.get(Double.class);
		} else if (type instanceof FloatObjectCoerce || type == Float.class) {
			return FIELD_TYPE_DICTIONARY.get(Float.class);
		} else if (type instanceof ShortObjectCoerce || type == Short.class) {
			return FIELD_TYPE_DICTIONARY.get(Short.class);
		} else if (type instanceof LongObjectCoerce || type == Long.class) {
			return FIELD_TYPE_DICTIONARY.get(Long.class);
		} else if (type instanceof BooleanObjectCoerce || type == Boolean.class) {
			return FIELD_TYPE_DICTIONARY.get(Boolean.class);
		}
		return FIELD_TYPE_DICTIONARY.get(String.class);
	}

}
