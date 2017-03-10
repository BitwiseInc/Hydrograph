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

import cascading.flow.FlowProcess;
import cascading.scheme.Scheme;
import cascading.scheme.SinkCall;
import cascading.scheme.SourceCall;
import cascading.tap.Tap;
import cascading.tap.hive.HiveTableDescriptor;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.io.parquet.read.DataWritableReadSupport;
import org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe;
import org.apache.hadoop.hive.ql.io.parquet.write.DataWritableWriteSupport;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RecordReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parquet.hadoop.ParquetInputFormat;
import parquet.hadoop.ParquetOutputFormat;
import parquet.hadoop.mapred.Container;
import parquet.hadoop.mapred.DeprecatedParquetInputFormat;
import parquet.hadoop.mapred.DeprecatedParquetOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class HiveParquetScheme extends Scheme<JobConf, RecordReader, OutputCollector, Object[], Object[]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9178222334142050386L;
	private HiveTableDescriptor hiveTableDescriptor;
	private static Logger LOG = LoggerFactory.getLogger(HiveParquetScheme.class);

	public HiveParquetScheme(HiveTableDescriptor hiveTableDescriptor) {
		// super(new Fields(hiveTableDescriptor.getColumnNames()), new Fields(
		// hiveTableDescriptor.getColumnNames()));
		this.hiveTableDescriptor = hiveTableDescriptor;

	}

	@Override
	public void sourcePrepare(FlowProcess<? extends JobConf> flowProcess, SourceCall<Object[], RecordReader> sourceCall)
			throws IOException {
		sourceCall.setContext(new Object[3]);

		sourceCall.getContext()[0] = sourceCall.getInput().createKey();
		sourceCall.getContext()[1] = sourceCall.getInput().createValue();
	}

	@Override
	public void sourceConfInit(FlowProcess<? extends JobConf> fp, Tap<JobConf, RecordReader, OutputCollector> tap,
			JobConf jobConf) {

		jobConf.setInputFormat(DeprecatedParquetInputFormat.class);
		jobConf.set("hive.parquet.timestamp.skip.conversion", "false");
		ParquetInputFormat.setReadSupportClass(jobConf, DataWritableReadSupport.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean source(FlowProcess<? extends JobConf> fp, SourceCall<Object[], RecordReader> sc) throws IOException {
		Container<ArrayWritable> value = (Container<ArrayWritable>) sc.getInput().createValue();
		boolean hasNext = sc.getInput().next(null, value);
		if (!hasNext) {
			return false;
		}

		// Skip nulls
		if (value == null) {
			return true;
		}
		Tuple tuple = WritableFactory.getTuple(value.get());
		sc.getIncomingEntry().setTuple(tuple);
		return true;
	}

	@Override
	public void sinkConfInit(FlowProcess<? extends JobConf> fp, Tap<JobConf, RecordReader, OutputCollector> tap,
			JobConf jobConf) {
		jobConf.setOutputFormat(DeprecatedParquetOutputFormat.class);

		jobConf.set(DataWritableWriteSupport.PARQUET_HIVE_SCHEMA,
				HiveParquetSchemeHelper.getParquetSchemeMessage(hiveTableDescriptor));

		ParquetOutputFormat.setWriteSupportClass(jobConf, DataWritableWriteSupport.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sink(FlowProcess<? extends JobConf> fp, SinkCall<Object[], OutputCollector> sink) throws IOException {
		TupleEntry entry = sink.getOutgoingEntry();
		OutputCollector outputCollector = sink.getOutput();

		Tuple tuple = entry.getTuple();
		List<Object> obj = new ArrayList<Object>();
		for (int i = 0; i < tuple.size(); i++) {
			obj.add(tuple.getObject(i));
		}
		ArrayWritable writable;
		try {
			ParquetHiveSerDe serde = new ParquetHiveSerDe();
			Configuration conf = new Configuration();

			serde.initialize(conf, HiveParquetSchemeHelper.getTableProperties(hiveTableDescriptor));
			ObjectInspector io = serde.getObjectInspector();
			writable = ParquetWritableUtils.createStruct(obj, (StructObjectInspector) io);
			Writable parRow = serde.serialize(writable, io);
			outputCollector.collect(null, parRow);
		} catch (SerDeException e) {
			LOG.error("", e);
			throw new RuntimeException(e);
		}

	}

}