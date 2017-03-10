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
import cascading.pipe.Pipe;
import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tap.hive.HivePartitionTap;
import cascading.tap.hive.HiveTableDescriptor;
import cascading.tap.hive.HiveTap;
import hydrograph.engine.cascading.scheme.hive.parquet.HiveParquetScheme;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.Properties;

public class HivePartRead {

	public static void main(String args[]) throws IOException {

		Configuration conf = new Configuration();

		String[] otherArgs;

		otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

		// print other args
		String argsString = "";
		for (String arg : otherArgs) {
			argsString = argsString + " " + arg;
		}
		System.out.println("After processing arguments are:" + argsString);

		Properties properties = new Properties();
		properties.putAll(conf.getValByRegex(".*"));

		Tap sink = new Hfs(new TextDelimited(false, ","), "/data/file_out_2",
				SinkMode.REPLACE);

		HiveTableDescriptor hiveTableDescriptor = new HiveTableDescriptor(
				"testp14", new String[] { "a", "b", "c" }, new String[] {
						"string", "string", "string" }, new String[] { "a" });
		HiveTap hivetap = new HiveTap(hiveTableDescriptor,
				new HiveParquetScheme(hiveTableDescriptor));
		Tap source = new HivePartitionTap(hivetap);
		Pipe pipe = new Pipe("pipe");

		properties.put("hive.metastore.uris",
				"thrift://UbuntuD5.bitwiseglobal.net:9083");
		FlowDef def = FlowDef.flowDef().addSource(pipe, source)
				.addTailSink(pipe, sink);

		new Hadoop2MR1FlowConnector(properties).connect(def).complete();
	}

}
