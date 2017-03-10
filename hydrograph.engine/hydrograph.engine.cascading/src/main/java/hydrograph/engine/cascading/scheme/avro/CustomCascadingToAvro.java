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
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hydrograph.engine.cascading.scheme.avro;

import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Fixed;
import org.apache.avro.generic.GenericData.Record;
import org.apache.hadoop.hive.serde2.avro.AvroSerDe;
import org.apache.hadoop.io.BytesWritable;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.*;

public class CustomCascadingToAvro {

	@SuppressWarnings("serial")
	private static Map<Class<?>, Type> TYPE_MAP = new HashMap<Class<?>, Type>() {
		{
			put(Integer.class, Type.INT);
			put(Long.class, Type.LONG);
			put(Boolean.class, Type.BOOLEAN);
			put(Double.class, Type.DOUBLE);
			put(Float.class, Type.FLOAT);
			put(String.class, Type.STRING);
			put(Date.class, Type.INT);
			put(BigDecimal.class, Type.BYTES);
			// Note : Cascading field type for Array and Map is really a Tuple
			put(List.class, Type.ARRAY);
			put(Map.class, Type.MAP);

		}
	};

	public static Object[] parseTupleEntry(TupleEntry tupleEntry,
			Schema writerSchema) {
		if (!(writerSchema.getFields().size() == tupleEntry.size())) {
			throw new AvroRuntimeException(
					"Arity mismatch between incoming tuple and schema");
		}

		return parseTuple(tupleEntry.getTuple(), writerSchema);
	}

	public static Object[] parseTuple(Tuple tuple, Schema writerSchema) {
		Object[] result = new Object[writerSchema.getFields().size()];

		List<Field> schemaFields = writerSchema.getFields();
		for (int i = 0; i < schemaFields.size(); i++) {
			Field field = schemaFields.get(i);

			// if (!fields.contains(new Fields(field.name()))) {
			// System.out.println(fields);
			// throw new RuntimeException("Tuple doesn't contain field: "+
			// field.name());
			// }
			Object obj = tuple.getObject(i);
			result[i] = toAvro(obj, field.schema());
		}

		return result;
	}

	protected static Object toAvro(Object obj, Schema schema) {
		switch (schema.getType()) {

		case ARRAY:
			return toAvroArray(obj, schema);

		case STRING:
			return obj.toString();
		case ENUM:
			return toAvroEnum(obj, schema);

		case FIXED:
			return toAvroFixed(obj, schema);

		case RECORD:
			Object[] objs;
			if (obj instanceof Tuple) {
				objs = parseTuple((Tuple) obj, schema);
			} else {
				objs = parseTupleEntry((TupleEntry) obj, schema);
			}

			Record record = new Record(schema);
			for (int i = 0; i < objs.length; i++) {
				record.put(i, objs[i]);
			}

			return record;

		case BYTES:
			if (schema.getJsonProp("logicalType") != null) {
				if (schema.getJsonProp("logicalType").getValueAsText()
						.equalsIgnoreCase("decimal")) {
					return decimalToBinary((BigDecimal) obj, schema);
				}
			} else
				return toAvroBytes(obj);

		case UNION:
			return toAvroUnion(obj, schema);

		case NULL:
		case BOOLEAN:
		case DOUBLE:
		case FLOAT:
		case INT:
		case LONG:
			return obj;

		default:
			throw new AvroRuntimeException("Can't convert from type "
					+ schema.getType().toString());

		}
	}

	public static final int PRECISION_TO_BYTE_COUNT[] = new int[38];
	static {
		for (int prec = 1; prec <= 38; prec++) {
			// Estimated number of bytes needed.
			PRECISION_TO_BYTE_COUNT[prec - 1] = (int) Math.ceil((Math.log(Math
					.pow(10, prec) - 1) / Math.log(2) + 1) / 8);
		}
	}

	private static Object decimalToBinary(final BigDecimal bigDecimal,
			Schema schema) {

		// int prec = schema.getJsonProp("precision").asInt();
		int prec = bigDecimal.precision();
		int scale = schema.getJsonProp("scale").asInt();
		byte[] decimalBytes = bigDecimal.setScale(scale).unscaledValue()
				.toByteArray();

		// Estimated number of bytes needed.
		int precToBytes = PRECISION_TO_BYTE_COUNT[prec - 1];
		if (precToBytes == decimalBytes.length) {
			// No padding needed.
			return ByteBuffer.wrap(decimalBytes);
		}

		byte[] tgt = new byte[precToBytes];
		if (bigDecimal.signum() == -1) {
			// For negative number, initializing bits to 1
			for (int i = 0; i < precToBytes; i++) {
				tgt[i] |= 0xFF;
			}
		}

		System.arraycopy(decimalBytes, 0, tgt, precToBytes
				- decimalBytes.length, decimalBytes.length); // Padding leading
																// zeroes/ones.
		return ByteBuffer.wrap(tgt);

	}

	protected static Object toAvroEnum(Object obj, Schema schema) {
		return new GenericData.EnumSymbol(schema, obj.toString());
	}

	protected static Object toAvroFixed(Object obj, Schema schema) {
		BytesWritable bytes = (BytesWritable) obj;
		return new Fixed(schema, Arrays.copyOfRange(bytes.getBytes(), 0,
				bytes.getLength()));
	}

	protected static Object toAvroBytes(Object obj) {
		BytesWritable inBytes = (BytesWritable) obj;
		return ByteBuffer.wrap(Arrays.copyOfRange(inBytes.getBytes(), 0,
				inBytes.getLength()));
	}

	protected static Object toAvroArray(Object obj, Schema schema) {
		if (obj instanceof Iterable) {
			Schema elementSchema = schema.getElementType();
			List<Object> array = new ArrayList<Object>();
			for (Object element : (Iterable<Object>) obj) {
				array.add(toAvro(element, elementSchema));
			}

			return new GenericData.Array(schema, array);
		} else
			throw new AvroRuntimeException(
					"Can't convert from non-iterable to array");
	}

	protected static Object toAvroUnion(Object obj, Schema schema) {
		if (obj == null) {
			return null;
		}

		List<Schema> types = schema.getTypes();
		if (types.size() < 1) {
			throw new AvroRuntimeException(
					"Union in writer schema has no types");
		} else if (types.size() == 1) {
			return toAvro(obj, types.get(0));
		} else if (types.size() > 2) {
			throw new AvroRuntimeException(
					"Unions may only consist of a concrete type and null in cascading.avro");
		} else if (!types.get(0).getType().equals(Type.NULL)
				&& !types.get(1).getType().equals(Type.NULL)) {
			throw new AvroRuntimeException(
					"Unions may only consist of a concrete type and null in cascading.avro");
		} else {
			Integer concreteIndex = (types.get(0).getType() == Type.NULL) ? 1
					: 0;
			return toAvro(obj, types.get(concreteIndex));
		}
	}

	@SuppressWarnings("rawtypes")
	protected static Schema generateAvroSchemaFromTupleEntry(
			TupleEntry tupleEntry, String recordName, boolean isNullable) {
		Fields tupleFields = tupleEntry.getFields();
		List<Field> avroFields = new ArrayList<Field>();
		for (Comparable fieldName : tupleFields) {
			if (!(fieldName instanceof String)) {
				throw new AvroRuntimeException(
						"Can't generate schema from non-string named fields");
			}
			Schema fieldSchema = generateAvroSchemaFromElement(
					tupleEntry.getObject(fieldName), (String) fieldName,
					isNullable);
			avroFields.add(new Field((String) fieldName, fieldSchema, null,
					null));
		}

		Schema outputSchema = Schema.createRecord(recordName,
				"auto-generated by cascading.avro", null, false);
		outputSchema.setFields(avroFields);
		return outputSchema;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static Schema generateAvroSchemaFromElement(Object element,
			String name, boolean isNullable) {
		if (element == null) {
			throw new AvroRuntimeException(
					"Can't infer schema from null valued element");
		} else if (isNullable)
			return generateUnionSchema(element, name);
		else if (element instanceof TupleEntry)
			return generateAvroSchemaFromTupleEntry((TupleEntry) element, name,
					isNullable);
		else if (element instanceof Map)
			return generateAvroSchemaFromMap((Map<String, Object>) element,
					name);
		else if (element instanceof Iterable)
			return generateAvroSchemaFromIterable((Iterable) element, name);
		else if (element instanceof BytesWritable)
			return Schema.create(Type.BYTES);
		else if (element instanceof String)
			return Schema.create(Type.STRING);
		else if (element instanceof Double)
			return Schema.create(Type.DOUBLE);
		else if (element instanceof Float)
			return Schema.create(Type.FLOAT);
		else if (element instanceof Integer)
			return Schema.create(Type.INT);
		else if (element instanceof Long)
			return Schema.create(Type.LONG);
		else if (element instanceof Boolean)
			return Schema.create(Type.BOOLEAN);
		else
			throw new AvroRuntimeException("Can't create schema from type "
					+ element.getClass());

	}

	private static Schema generateAvroSchemaFromIterable(Iterable element,
			String name) {
		Iterator<Object> iterator = element.iterator();
		if (!iterator.hasNext()) {
			throw new AvroRuntimeException(
					"Can't infer list schema from empty iterable");
		} else {
			Schema itemSchema = generateAvroSchemaFromElement(iterator.next(),
					name + "ArrayElement", false);
			return Schema.createArray(itemSchema);
		}

	}

	private static Schema generateAvroSchemaFromMap(
			Map<String, Object> element, String name) {
		if (element.isEmpty()) {
			throw new AvroRuntimeException(
					"Can't infer map schema from empty map");
		} else {
			Iterator<Object> iterator = element.values().iterator();
			Schema valueSchema = generateAvroSchemaFromElement(iterator.next(),
					name + "MapValue", false);
			return Schema.createMap(valueSchema);
		}

	}

	private static Schema generateUnionSchema(Object element, String name) {
		List<Schema> types = new ArrayList<Schema>();
		types.add(Schema.create(Type.NULL));
		types.add(generateAvroSchemaFromElement(element, name, false));
		return Schema.createUnion(types);
	}

	public static Schema generateAvroSchemaFromFieldsAndTypes(
			String recordName, Fields schemeFields, Class<?>[] schemeTypes,
			int[] fieldPrecision, int[] fieldScale) {
		if (schemeFields.size() == 0) {
			throw new IllegalArgumentException(
					"There must be at least one field");
		}

		int schemeTypesSize = 0;
		for (int i = 0; i < schemeTypes.length; i++, schemeTypesSize++) {
			if ((schemeTypes[i] == List.class) || (schemeTypes[i] == Map.class)) {
				i++;
			}
		}

		if (schemeTypesSize != schemeFields.size()) {
			throw new IllegalArgumentException(
					"You must have a schemeType for every field");
		}

		for (int i = 0; i < schemeTypes.length; i++) {
			if ((schemeTypes[i] == List.class) || (schemeTypes[i] == Map.class)) {
				++i;
				if (!isPrimitiveType(schemeTypes[i])) {
					throw new IllegalArgumentException(
							"Only primitive types are allowed for an Array or Map");
				}
			}
		}

		return generateSchema(recordName, schemeFields, schemeTypes, 0,
				fieldPrecision, fieldScale);
	}

	public static void addToTuple(Tuple t, byte[] bytes) {
		t.add(new BytesWritable(bytes));
	}

	@SuppressWarnings("rawtypes")
	public static void addToTuple(Tuple t, Enum e) {
		t.add(e.toString());
	}

	public static void addToTuple(Tuple t, List<?> list) {
		Tuple listTuple = new Tuple();
		for (Object item : list) {
			listTuple.add(item);
		}

		t.add(listTuple);
	}

	public static void addToTuple(Tuple t, Map<String, ?> map) {
		Tuple mapTuple = new Tuple();
		for (String key : map.keySet()) {
			mapTuple.add(key);
			mapTuple.add(map.get(key));
		}

		t.add(mapTuple);
	}

	private static boolean isPrimitiveType(Class<?> arrayType) {
		// only primitive types are allowed for arrays

		return (arrayType == Boolean.class || arrayType == Integer.class
				|| arrayType == Long.class || arrayType == Float.class
				|| arrayType == Double.class || arrayType == String.class || arrayType == BytesWritable.class);
	}

	private static Schema generateSchema(String recordName,
			Fields schemeFields, Class<?>[] schemeTypes, int depth,
			int[] fieldPrecision, int[] fieldScale) {
		// Create a 'record' that is made up of fields.
		// Since we support arrays and maps that means we can have nested
		// records

		List<Field> fields = new ArrayList<Field>();
		for (int typeIndex = 0, fieldIndex = 0; typeIndex < schemeTypes.length; typeIndex++, fieldIndex++) {
			String fieldName = schemeFields.get(fieldIndex).toString();
			Class<?>[] subSchemeTypes = new Class[2]; // at most 2, since we
														// only allow primitive
														// types for arrays and
														// maps
			subSchemeTypes[0] = schemeTypes[typeIndex];
			if ((schemeTypes[typeIndex] == List.class)
					|| (schemeTypes[typeIndex] == Map.class)) {
				typeIndex++;
				subSchemeTypes[1] = schemeTypes[typeIndex];
			}

			final Schema schema = createAvroSchema(recordName, schemeFields,
					subSchemeTypes, depth + 1, fieldPrecision[typeIndex],
					fieldScale[typeIndex]);
			final Schema nullSchema = Schema.create(Type.NULL);
			List<Schema> schemas = new LinkedList<Schema>() {
				{
					add(nullSchema);
					add(schema);
				}
			};

			fields.add(new Field(fieldName, Schema.createUnion(schemas),
					"", null));
		}

		// Avro doesn't like anonymous records - so create a named one.
		if (depth > 0) {
			recordName = recordName + depth;
		}

		Schema schema = Schema.createRecord(recordName, "auto generated", "",
				false);
		schema.setFields(fields);
		return schema;
	}

	private static Schema createAvroSchema(String recordName,
			Fields schemeFields, Class<?>[] fieldTypes, int depth,
			int fieldPrecision, int fieldScale) {
		Map<Class<?>, Type> avroType = toAvroSchemaType(fieldTypes[0]);

		int remainingFields = schemeFields.size() - 1;
		if (avroType.get(fieldTypes[0]) == Type.ARRAY) {
			Schema schema;
			if (remainingFields == 0) {
				schema = Schema.createArray(Schema.create(toAvroSchemaType(
						fieldTypes[1]).get(avroType.get(fieldTypes[0]))));
			} else {
				Class<?> arrayTypes[] = { fieldTypes[1] };
				schema = Schema.createArray(createAvroSchema(recordName,
						Fields.offsetSelector(schemeFields.size() - 1, 1),
						arrayTypes, depth + 1, fieldPrecision, fieldScale));
			}
			return schema;
		} else if (avroType.get(fieldTypes[0]) == Type.MAP) {
			Schema schema;
			if (remainingFields == 0) {
				schema = Schema.createMap(Schema.create(toAvroSchemaType(
						fieldTypes[1]).get(avroType.get(fieldTypes[0]))));
			} else {
				Class<?> mapTypes[] = { fieldTypes[1] };
				schema = Schema.createMap(createAvroSchema(recordName,
						Fields.offsetSelector(schemeFields.size() - 1, 1),
						mapTypes, depth + 1, fieldPrecision, fieldScale));
			}
			return schema;
		} else if (avroType.get(fieldTypes[0]) == Type.ENUM) {
			Class<?> clazz = fieldTypes[0];
			Object[] names = clazz.getEnumConstants();
			List<String> enumNames = new ArrayList<String>(names.length);
			for (Object name : names) {
				enumNames.add(name.toString());
			}

			return Schema.createEnum(fieldTypes[0].getName(), null, null,
					enumNames);
		} else if (fieldTypes[0] == Date.class) {
			return AvroSchemaUtils.getSchemaFor("{" + "\"type\":\""
					+ AvroSerDe.AVRO_LONG_TYPE_NAME + "\","
					+ "\"logicalType\":\"" + AvroSerDe.DATE_TYPE_NAME + "\"}");

		} else if (fieldTypes[0] == BigDecimal.class) {
			String precision;
			if (String.valueOf(fieldPrecision).equals("-999"))
				precision = "-999";
			else
				precision = String.valueOf(fieldScale);
			String scale = String.valueOf(fieldScale);
			return AvroSchemaUtils.getSchemaFor("{" + "\"type\":\"bytes\","
					+ "\"logicalType\":\"decimal\"," + "\"precision\":"
					+ precision + "," + "\"scale\":" + scale + "}");
		} else {
			return Schema.create(avroType.get(fieldTypes[0]));
		}
	}

	private static Map<Class<?>, Type> toAvroSchemaType(Class<?> clazz) {
		if (TYPE_MAP.containsKey(clazz)) {
			return TYPE_MAP;
		} else {
			throw new UnsupportedOperationException("The class type " + clazz
					+ " is currently unsupported");
		}
	}
}