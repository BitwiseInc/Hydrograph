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

import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.io.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum WritableConverter {

	STRING {
		@Override
		public Object getValue(Writable writable) {
			return ((Text) writable).toString();
		}
	},
	BYTES {
		@Override
		public Object getValue(Writable writable) {
			return new String(((BytesWritable) writable).get());
		}
	},
	INTEGER {
		@Override
		public Object getValue(Writable writable) {
			return ((IntWritable) writable).get();
		}
	},
	DOUBLE {
		@Override
		public Object getValue(Writable writable) {
			return ((DoubleWritable) writable).get();
		}
	},
	DATE {
		@Override
		public Object getValue(Writable writable) {
			return ((DateWritable) writable).get().getTime();
		}
	},
	TIMESTAMP {
		@Override
		public Object getValue(Writable writable) {
			return ((TimestampWritable) writable).getTimestamp().getTime();
		}
	},
	DECIMAL {
		@Override
		public Object getValue(Writable writable) {
			// HiveDecimal object does not have method to get BigDecimal value.
			// So creating the BigDecimal object from double and setting the
			// same scale which HiveDecimal object has.
			HiveDecimal hiveDecimal = ((HiveDecimalWritable) writable)
					.getHiveDecimal();
			return new BigDecimal(hiveDecimal.doubleValue()).setScale(
					hiveDecimal.scale(), RoundingMode.HALF_UP);
		}
	},
	LONG {
		@Override
		public Object getValue(Writable writable) {
			return ((LongWritable) writable).get();
		}
	},
	BOOLEAN {
		@Override
		public Object getValue(Writable writable) {
			return ((BooleanWritable) writable).get();
		}
	},
	FLOAT {
		@Override
		public Object getValue(Writable writable) {
			return ((FloatWritable) writable).get();
		}
	};

	public abstract Object getValue(Writable writable);

}
