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
package hydrograph.engine.cascading.scheme.hive.parquet;

import cascading.tuple.Tuple;

import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.io.*;
import org.apache.hadoop.hive.serde2.objectinspector.*;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.ShortWritable;

import parquet.io.api.Binary;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * A ParquetHiveSerDe for Hive (with the deprecated package mapred)
 *
 */
public class ParquetWritableUtils {
	public static final Text MAP_KEY = new Text("key");
	public static final Text MAP_VALUE = new Text("value");
	public static final Text MAP = new Text("map");
	public static final Text ARRAY = new Text("bag");

	public static ArrayWritable createStruct(final Object obj,
			final StructObjectInspector inspector) throws SerDeException {
		final List<? extends StructField> fields = inspector
				.getAllStructFieldRefs();
		final Writable[] arr = new Writable[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			final StructField field = fields.get(i);
			final Object subObj = inspector.getStructFieldData(obj, field);
			final ObjectInspector subInspector = field
					.getFieldObjectInspector();
			arr[i] = createObject(subObj, subInspector);
		}
		return new ArrayWritable(Writable.class, arr);
	}

	private static Writable createMap(final Object obj,
			final MapObjectInspector inspector) throws SerDeException {
		final Map<?, ?> sourceMap = inspector.getMap(obj);
		final ObjectInspector keyInspector = inspector
				.getMapKeyObjectInspector();
		final ObjectInspector valueInspector = inspector
				.getMapValueObjectInspector();
		final List<ArrayWritable> array = new ArrayList<ArrayWritable>();

		if (sourceMap != null) {
			for (final Entry<?, ?> keyValue : sourceMap.entrySet()) {
				final Writable key = createObject(keyValue.getKey(),
						keyInspector);
				final Writable value = createObject(keyValue.getValue(),
						valueInspector);
				if (key != null) {
					Writable[] arr = new Writable[2];
					arr[0] = key;
					arr[1] = value;
					array.add(new ArrayWritable(Writable.class, arr));
				}
			}
		}
		if (array.size() > 0) {
			final ArrayWritable subArray = new ArrayWritable(
					ArrayWritable.class, array.toArray(new ArrayWritable[array
							.size()]));
			return new ArrayWritable(Writable.class,
					new Writable[] { subArray });
		} else {
			return null;
		}
	}

	private static ArrayWritable createArray(final Object obj,
			final ListObjectInspector inspector) throws SerDeException {
		final ObjectInspector subInspector = inspector
				.getListElementObjectInspector();
		Tuple tuple = (Tuple) obj;
		final List<Writable> array = new ArrayList<Writable>();
		for (int i = 0; i < tuple.size(); i++) {
			array.add(createObject(tuple.getObject(i), subInspector));
		}

		if (array.size() > 0) {
			final ArrayWritable subArray = new ArrayWritable(array.get(0)
					.getClass(), array.toArray(new Writable[array.size()]));
			return new ArrayWritable(Writable.class,
					new Writable[] { subArray });
		} else {
			return null;
		}

	}

	private static Writable createPrimitive(final Object obj,
			final PrimitiveObjectInspector inspector) throws SerDeException {
		if (obj == null) {
			return null;
		}

		switch (inspector.getPrimitiveCategory()) {
		case VOID:
			return null;
		case BOOLEAN:
			return new BooleanWritable(
					((BooleanObjectInspector) inspector)
							.get(new BooleanWritable((boolean) obj)));
		case BYTE:
			return new ByteWritable(
					((ByteObjectInspector) inspector).get(new ByteWritable(
							(byte) obj)));
		case DOUBLE:
			return new DoubleWritable(
					((DoubleObjectInspector) inspector).get(new DoubleWritable(
							(double) obj)));
		case FLOAT:
			return new FloatWritable(
					((FloatObjectInspector) inspector).get(new FloatWritable(
							(float) obj)));
		case INT:
			return new IntWritable(
					((IntObjectInspector) inspector).get(new IntWritable(
							(int) obj)));
		case LONG:
			return new LongWritable(
					((LongObjectInspector) inspector).get(new LongWritable(
							(long) obj)));
		case SHORT:
			return new ShortWritable(
					((ShortObjectInspector) inspector).get(new ShortWritable(
							(short) obj)));
		case STRING:
			String v;
			if (obj instanceof Long) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date((long) obj);
				v = df.format(date);
			} else if (obj instanceof BigDecimal) {
				BigDecimal bigDecimalObj = (BigDecimal) obj;
				v = bigDecimalObj.toString();
			} else {
				v = ((StringObjectInspector) inspector)
						.getPrimitiveJavaObject(obj);
			}
			try {
				return new BytesWritable(v.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new SerDeException("Failed to encode string in UTF-8", e);
			}
		case DECIMAL:
			HiveDecimal hd;
			if (obj instanceof Double) {
				hd = HiveDecimal.create(new BigDecimal((Double) obj));
			} else if (obj instanceof BigDecimal) {
				hd = HiveDecimal.create((BigDecimal) obj);
			} else {
				// if "obj" is other than Double or BigDecimal and a vaild
				// number, .toString, will get its correct number representation
				// and a BigDecimal object will be created
				hd = HiveDecimal.create(new BigDecimal(obj.toString()));
			}
			return new HiveDecimalWritable(hd);
		case TIMESTAMP:
			return new TimestampWritable(
					((TimestampObjectInspector) inspector)
							.getPrimitiveJavaObject(new TimestampWritable(
									new Timestamp((long) obj))));
		case DATE:
			return new DateWritable(
					((DateObjectInspector) inspector)
							.getPrimitiveJavaObject(new DateWritable(new Date(
									(long) obj))));
		case CHAR:
			String strippedValue = ((HiveCharObjectInspector) inspector)
					.getPrimitiveJavaObject(obj).getStrippedValue();
			return new BytesWritable(Binary.fromString(strippedValue)
					.getBytes());
		case VARCHAR:
			String value = ((HiveVarcharObjectInspector) inspector)
					.getPrimitiveJavaObject(obj).getValue();
			return new BytesWritable(Binary.fromString(value).getBytes());
		default:
			throw new SerDeException("Unknown primitive : "
					+ inspector.getPrimitiveCategory());
		}
	}

	private static Writable createObject(final Object obj,
			final ObjectInspector inspector) throws SerDeException {
		switch (inspector.getCategory()) {
		case STRUCT:
			return createStruct(obj, (StructObjectInspector) inspector);
		case LIST:
			return createArray(obj, (ListObjectInspector) inspector);
		case MAP:
			return createMap(obj, (MapObjectInspector) inspector);
		case PRIMITIVE:
			return createPrimitive(obj, (PrimitiveObjectInspector) inspector);
		default:
			throw new SerDeException("Unknown data type"
					+ inspector.getCategory());
		}
	}

}