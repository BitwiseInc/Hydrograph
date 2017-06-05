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

import cascading.flow.FlowDef;
import cascading.flow.hadoop2.Hadoop2MR1FlowConnector;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tap.hive.HiveTableDescriptor;
import cascading.tap.hive.HiveTap;
import cascading.tuple.Fields;
import cascading.tuple.type.DateType;
import hydrograph.engine.cascading.scheme.hive.parquet.HiveParquetScheme;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

public class ParquetHiveTest {

	public static void main(String args[]) throws IOException {

		Configuration conf = new Configuration();
		String[] otherArgs;
		otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		String argsString = "";
		for (String arg : otherArgs) {
			argsString = argsString + " " + arg;
		}
		System.out.println("After processing arguments are:      " + argsString);
		Properties properties = new Properties();
		properties.putAll(conf.getValByRegex(".*"));
		DateType dt = new DateType("yyyy-MM-dd");
		Fields fields = new Fields("a", "b", "c", "d", "e", "f").applyTypes(
				String.class, Integer.class, Long.class, BigDecimal.class, dt,
				String.class);
		Tap source = new Hfs(new TextDelimited(fields, true, ","),
				"data/output_testalltype");

		HiveTableDescriptor hiveTableDescriptor = new HiveTableDescriptor(
				"testing23", new String[] { "a", "b", "c", "d", "e", "new" },
				new String[] { "string", "int", "bigint", "decimal(10,2)",
						"date", "array<int>" });

		Tap sink = new HiveTap(hiveTableDescriptor, new HiveParquetScheme(
				hiveTableDescriptor), SinkMode.REPLACE, false);

		Pipe pipe = new Pipe("pipe");

		pipe = new Each(pipe, fields, new Custome1(new Fields("new")),
				new Fields("a", "b", "c", "d", "e", "new"));

		FlowDef def = FlowDef.flowDef().addSource(pipe, source)
				.addTailSink(pipe, sink);

		new Hadoop2MR1FlowConnector(properties).connect(def).complete();

	}
}
