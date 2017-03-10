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
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.io.*;

import java.math.BigDecimal;

public class WritableFactory {

	public static Tuple getTuple(ArrayWritable arrayWritable) {
		Tuple tuple = new Tuple();
		for (Writable writable : arrayWritable.get()) {
			tuple.append(getFieldType(writable, tuple));
		}
		return tuple;
	}

	private static Tuple getFieldType(Writable writable, Tuple tuple) {
		if (writable instanceof DateWritable) {
			tuple.addLong((long) WritableConverter.DATE.getValue(writable));
			return tuple;
		} else if (writable instanceof LongWritable) {
			tuple.addLong((long) WritableConverter.LONG.getValue(writable));
			return tuple;
		}else if (writable instanceof TimestampWritable) {
			tuple.addLong((long) WritableConverter.TIMESTAMP.getValue(writable));
			return tuple;
		}
		else if (writable instanceof Text) {
			tuple.addString((String) WritableConverter.STRING.getValue(writable));
			return tuple;
		} else if (writable instanceof BytesWritable) {
			tuple.addString((String) WritableConverter.BYTES.getValue(writable));
			return tuple;
		} else if (writable instanceof IntWritable) {
			tuple.addInteger((int) WritableConverter.INTEGER.getValue(writable));
			return tuple;
		} else if (writable instanceof DoubleWritable) {
			tuple.addDouble((double) WritableConverter.DOUBLE.getValue(writable));
			return tuple;
		} else if (writable instanceof HiveDecimalWritable) {
			tuple.add((BigDecimal) WritableConverter.DECIMAL.getValue(writable));
			return tuple;
		} else if (writable instanceof BooleanWritable) {
			tuple.addBoolean((boolean) WritableConverter.BOOLEAN.getValue(writable));
			return tuple;
		} else if (writable instanceof FloatWritable) {
			tuple.addFloat((float) WritableConverter.FLOAT.getValue(writable));
			return tuple;
		} else if (writable instanceof ArrayWritable) {
			Tuple tuples = getTuple((ArrayWritable) writable);
			tuple.add(tuples);
			return tuple;
		} else if (writable instanceof NullWritable) {
			tuple.add(null);
			return tuple;
		} else if (writable == null) {
			tuple.add(null);
			return tuple;
		}
		return null;
	}
}
