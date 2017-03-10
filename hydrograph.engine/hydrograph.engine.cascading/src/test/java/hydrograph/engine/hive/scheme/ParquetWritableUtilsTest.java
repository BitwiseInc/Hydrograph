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
package hydrograph.engine.hive.scheme;

import cascading.tuple.Tuple;
import hydrograph.engine.cascading.scheme.hive.parquet.ParquetWritableUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.BytesWritable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ParquetWritableUtilsTest {

	private ObjectInspector io;
	private List<Object> obj;
	private static Logger LOG = LoggerFactory
			.getLogger(ParquetWritableUtilsTest.class);

	@Before
	public void setup() {
		try {
			Properties properties = new Properties();
			properties.put("columns", "a,b,c,d");
			properties.put("columns.types",
					"int:string:decimal(5,1):array<string>");
			Configuration conf = new Configuration();
			ParquetHiveSerDe serde = new ParquetHiveSerDe();
			serde.initialize(conf, properties);
			io = serde.getObjectInspector();
			Tuple tuple = new Tuple();
			tuple.add(new Integer(1));
			tuple.add(new String("hive"));
			tuple.add(BigDecimal.valueOf(1.2));
			Tuple tuple2 = new Tuple(new String("parquet"), new String(
					"cascading"));
			tuple.add(tuple2);
			obj = new ArrayList<Object>();
			for (int i = 0; i < tuple.size(); i++) {
				obj.add(tuple.getObject(i));
			}
		} catch (SerDeException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void itShouldGetBytesWritable() {
		try {
			ArrayWritable writable = ParquetWritableUtils.createStruct(obj,
					(StructObjectInspector) io);
			BytesWritable bw = (BytesWritable) writable.get()[1];
			Assert.assertTrue(writable.get()[1] instanceof BytesWritable);
			Assert.assertEquals("hive", new String(bw.get()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void itShouldGetDecimalWritable() {
		try {
			ArrayWritable writable = ParquetWritableUtils.createStruct(obj,
					(StructObjectInspector) io);
			Assert.assertTrue(writable.get()[2] instanceof HiveDecimalWritable);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void itShouldGetArrayWritable() {
		try {
			ArrayWritable writable = ParquetWritableUtils.createStruct(obj,
					(StructObjectInspector) io);
			Assert.assertTrue(writable.get()[3] instanceof ArrayWritable);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
