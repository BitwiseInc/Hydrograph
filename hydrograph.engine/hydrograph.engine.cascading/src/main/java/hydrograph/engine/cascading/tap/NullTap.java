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

import cascading.flow.Flow;
import cascading.flow.FlowListener;
import cascading.flow.FlowProcess;
import cascading.scheme.NullScheme;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntry;
import cascading.tuple.TupleEntryCollector;
import cascading.tuple.TupleEntryIterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RecordReader;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("rawtypes")
public class NullTap extends Tap<Configuration, RecordReader, OutputCollector>
		implements FlowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final TupleEntryCollector outCollector = new NullOutputCollector();

	final UUID id;

	public NullTap() {
		super(
				new NullScheme<Configuration, RecordReader, OutputCollector, Object, Object>());
		id = UUID.randomUUID();
	}

	@Override
	public String getIdentifier() {
		return "NullTap-" + id;
	}

	@Override
	public TupleEntryIterator openForRead(
			FlowProcess<? extends Configuration> flowProcess,
			RecordReader recordReader) throws IOException {
		return new NullIterator(new Fields("null"));
	}

	public TupleEntryCollector openForWrite(
			FlowProcess<? extends Configuration> flowProcess,
			OutputCollector output) {
		return outCollector;
	}

	@Override
	public boolean createResource(Configuration conf) throws IOException {
		return true;
	}

	@Override
	public boolean deleteResource(Configuration conf) throws IOException {
		return true;
	}

	@Override
	public boolean resourceExists(Configuration conf) throws IOException {
		return true;
	}

	@Override
	public long getModifiedTime(Configuration conf) throws IOException {
		return 0;
	}

	@Override
	public void onStarting(Flow flow) {
		// do nothing
	}

	@Override
	public void onStopping(Flow flow) {
		// do nothing
	}

	@Override
	public void onCompleted(Flow flow) {
	}

	@Override
	public boolean onThrowable(Flow flow, Throwable throwable) {
		return false;
	}

	private static class NullOutputCollector extends TupleEntryCollector
			implements OutputCollector<Object, Object>, Serializable {
		/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

		@Override
		public void collect(Object o, Object o1) throws IOException {
			// do nothing
		}

		@Override
		protected void collect(TupleEntry tupleEntry) throws IOException {
			// do nothing
		}
	}

	/**
	 * NullIterator has no tuples.
	 */
	private static class NullIterator extends TupleEntryIterator {

		public NullIterator(Fields fields) {
			super(fields);
		}

		@Override
		public void close() throws IOException {

		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public TupleEntry next() {
			return null;
		}

		@Override
		public void remove() {

		}
	}
}