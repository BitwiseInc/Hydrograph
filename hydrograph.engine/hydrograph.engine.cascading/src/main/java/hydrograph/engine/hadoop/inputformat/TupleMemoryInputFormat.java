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
package hydrograph.engine.hadoop.inputformat;

import hydrograph.engine.cascading.tuplegenerator.ITupleGenerator;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class TupleMemoryInputFormat implements
		InputFormat<TupleWrapper, NullWritable> {
	private static final Logger LOG = LoggerFactory
			.getLogger(TupleMemoryInputFormat.class);

	public static final String ENCODING = "US-ASCII";
	public static final String TUPLES_PROPERTY = "memory.format.tuples";

	public static class TupleInputSplit implements InputSplit {
		public long numTuples;

		public TupleInputSplit() {
		}

		public TupleInputSplit(long numTuples) {
			this.numTuples = numTuples;
		}

		public long getLength() throws IOException {
			return numTuples;
		}

		public String[] getLocations() throws IOException {
			return new String[] {};
		}

		public void write(DataOutput d) throws IOException {
			d.writeLong(numTuples);
		}

		public void readFields(DataInput di) throws IOException {
			numTuples = di.readLong();
		}
	}

	public static class TupleRecordReader implements
			RecordReader<TupleWrapper, NullWritable> {

		ITupleGenerator tupleGenerator;
		long numTuples;

		long pos = 0;

		public TupleRecordReader(ITupleGenerator tupleGenerator, long numTuples) {
			this.tupleGenerator = tupleGenerator;
			this.numTuples = numTuples;
		}

		public boolean next(TupleWrapper k, NullWritable v) throws IOException {
			if (pos >= numTuples)
				return false;
			k.tuple = tupleGenerator.getTuple();
			pos++;
			return true;
		}

		public TupleWrapper createKey() {
			return new TupleWrapper();
		}

		public NullWritable createValue() {
			return NullWritable.get();
		}

		public long getPos() throws IOException {
			return pos;
		}

		public void close() throws IOException {
		}

		public float getProgress() throws IOException {
			if (numTuples == 0) {
				return 1;
			}
			return (float) (pos * 1.0 / numTuples);
		}

	}

	public InputSplit[] getSplits(JobConf jc, int i) throws IOException {
		long numTuples = retrieveNumTuples(jc, TUPLES_PROPERTY);
		return new InputSplit[] { new TupleInputSplit(numTuples) };
	}

	public RecordReader<TupleWrapper, NullWritable> getRecordReader(
			InputSplit is, JobConf jc, Reporter rprtr) throws IOException {
		return new TupleRecordReader(
				retrieveTupleGenerator(jc, TUPLES_PROPERTY), retrieveNumTuples(
						jc, TUPLES_PROPERTY));
	}

	public static String encodeBytes(byte[] bytes) {
		try {
			return new String(Base64.encodeBase64(bytes), ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] decodeBytes(String str) {
		try {
			byte[] bytes = str.getBytes(ENCODING);
			return Base64.decodeBase64(bytes);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void storeTupleGenerator(Configuration conf, String key,
			ITupleGenerator tupleGenerator, long numTuples) {
		LOG.debug("Storing ITupleGenerator: {}", tupleGenerator);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ObjectOutputStream out;

		try {
			out = new ObjectOutputStream(stream);
			out.writeObject(tupleGenerator);
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String confVal = numTuples + ":" + encodeBytes(stream.toByteArray());
		conf.set(key, confVal);

	}

	public static long retrieveNumTuples(JobConf conf, String key) {
		String s = conf.get(key);
		if (s == null)

			return 0;

		String[] pieces = s.split(":");
		return Long.valueOf(pieces[0]);

	}

	public static ITupleGenerator retrieveTupleGenerator(JobConf conf,
			String key) {
		String s = conf.get(key);
		if (s == null)

			return null;

		String[] pieces = s.split(":");

		byte[] val;

		if (pieces.length > 1) {
			val = decodeBytes(pieces[1]);
		} else {
			val = new byte[0];
		}

		ByteArrayInputStream stream = new ByteArrayInputStream(val);
		ObjectInputStream in;

		ITupleGenerator tupleGenerator;
		try {
			in = new ObjectInputStream(stream);
			tupleGenerator = (ITupleGenerator) in.readObject();
			in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		return tupleGenerator;
	}
}