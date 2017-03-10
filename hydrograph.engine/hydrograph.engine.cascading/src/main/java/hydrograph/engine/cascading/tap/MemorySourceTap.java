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
package hydrograph.engine.cascading.tap;

import cascading.flow.FlowProcess;
import cascading.scheme.Scheme;
import cascading.scheme.SinkCall;
import cascading.scheme.SourceCall;
import cascading.tap.SourceTap;
import cascading.tap.Tap;
import cascading.tap.hadoop.io.HadoopTupleEntrySchemeIterator;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntryIterator;
import hydrograph.engine.cascading.tuplegenerator.ITupleGenerator;
import hydrograph.engine.hadoop.inputformat.TupleMemoryInputFormat;
import hydrograph.engine.hadoop.inputformat.TupleWrapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.RecordReader;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

public class MemorySourceTap extends
		SourceTap<Configuration, RecordReader<TupleWrapper, NullWritable>> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String id;

	public MemorySourceTap(ITupleGenerator tupleGenerator, Fields fields,
			long numTuples) {
		super(new MemorySourceScheme(tupleGenerator, numTuples, fields, "/"
				+ UUID.randomUUID().toString()));
		this.id = ((MemorySourceScheme) this.getScheme()).getId();
	}

	@Override
	public String getIdentifier() {
		return getPath().toString();
	}

	public Path getPath() {
		return new Path(id);
	}

	@Override
	public boolean resourceExists(Configuration conf) throws IOException {
		return true;
	}

	@Override
	public boolean equals(Object object) {
		
		if (object == null) {
			return false;
		} else if (!getClass().equals(object.getClass())) {
			return false;
		}
		MemorySourceTap other = (MemorySourceTap) object;
		return id.equals(other.id);
	}
	
	@Override
	public int hashCode(){
		return super.hashCode(); //overridden for sonarqube
	}

	@Override
	public TupleEntryIterator openForRead(FlowProcess<? extends Configuration> flowProcess,
			RecordReader<TupleWrapper, NullWritable> input) throws IOException {
		// input may be null when this method is called on the client side or
		// cluster side when accumulating
		// for a HashJoin
		return new HadoopTupleEntrySchemeIterator(flowProcess, this, input);
	}

	@Override
	public long getModifiedTime(Configuration conf) throws IOException {
		return System.currentTimeMillis();
	}

	public static class MemorySourceScheme
			extends
			Scheme<Configuration, RecordReader<TupleWrapper, NullWritable>, Void, Object[], Void> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final String id;
		private final ITupleGenerator tupleGenerator;
		private final long numTuples;

		public MemorySourceScheme(ITupleGenerator tupleGenerator,
				long numTuples, Fields fields, String id) {
			super(fields);
			assert tupleGenerator != null;
			this.id = id;
			this.tupleGenerator = tupleGenerator;
			this.numTuples = numTuples;
		}

		public String getId() {
			return this.id;
		}

		@Override
		public void sourceConfInit(
				FlowProcess<? extends Configuration> flowProcess,
				Tap<Configuration, RecordReader<TupleWrapper, NullWritable>, Void> tap,
				Configuration conf) {

			conf.setBoolean("mapred.mapper.new-api", false);
			conf.setClass("mapred.input.format.class",
					TupleMemoryInputFormat.class, InputFormat.class);
			
			TupleMemoryInputFormat.storeTupleGenerator(conf,
					TupleMemoryInputFormat.TUPLES_PROPERTY, tupleGenerator,
					numTuples);
		}

		@Override
		public void sinkConfInit(
				FlowProcess<? extends Configuration> flowProcess,
				Tap<Configuration, RecordReader<TupleWrapper, NullWritable>, Void> tap,
				Configuration conf) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void sourcePrepare(
				FlowProcess<? extends Configuration> flowProcess,
				SourceCall<Object[], RecordReader<TupleWrapper, NullWritable>> sourceCall) {
			sourceCall.setContext(new Object[2]);

			sourceCall.getContext()[0] = sourceCall.getInput().createKey();
			sourceCall.getContext()[1] = sourceCall.getInput().createValue();
		}

		@Override
		public boolean source(
				FlowProcess<? extends Configuration> flowProcess,
				SourceCall<Object[], RecordReader<TupleWrapper, NullWritable>> sourceCall)
				throws IOException {
			TupleWrapper key = (TupleWrapper) sourceCall.getContext()[0];
			NullWritable value = (NullWritable) sourceCall.getContext()[1];

			boolean result = sourceCall.getInput().next(key, value);

			if (!result)
				return false;

			sourceCall.getIncomingEntry().setTuple(key.tuple);
			return true;
		}

		@Override
		public void sourceCleanup(
				FlowProcess<? extends Configuration> flowProcess,
				SourceCall<Object[], RecordReader<TupleWrapper, NullWritable>> sourceCall) {
			sourceCall.setContext(null);
		}

		@Override
		public void sink(FlowProcess<? extends Configuration> flowProcess,
				SinkCall<Void, Void> sinkCall) throws IOException {
			throw new UnsupportedOperationException("Not supported.");
		}

	}
}