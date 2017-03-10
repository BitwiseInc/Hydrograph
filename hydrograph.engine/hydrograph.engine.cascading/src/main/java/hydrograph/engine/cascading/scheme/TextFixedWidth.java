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
package hydrograph.engine.cascading.scheme;

import cascading.flow.FlowProcess;
import cascading.scheme.SinkCall;
import cascading.scheme.SourceCall;
import cascading.scheme.hadoop.TextLine;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntry;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RecordReader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

@SuppressWarnings("serial")
public class TextFixedWidth extends TextLine {

	public static final char DEFAULT_FILLER = ' ';
	public static final boolean DEFAULT_STRICT = false;
	public static final boolean DEFAULT_SAFE = false;
	public static final String DEFAULT_CHARSETNAME = "UTF-8";
	public static final Type[] DEFAULT_TYPES = null;
	public static final Compress DEFAULT_SINKCOMPRESSION = null;

	protected char filler;
	protected boolean strict;
	protected boolean safe;
	protected int[] lengths;
	protected Type[] types;
	protected String charsetName;

	public TextFixedWidth(Fields fields, int[] lengths) {
		this(fields, lengths, DEFAULT_TYPES, DEFAULT_SINKCOMPRESSION,
				DEFAULT_STRICT, DEFAULT_SAFE, DEFAULT_FILLER,
				DEFAULT_CHARSETNAME);
	}

	public TextFixedWidth(Fields fields, int[] lengths, Type[] types) {
		this(fields, lengths, types, DEFAULT_SINKCOMPRESSION, DEFAULT_STRICT,
				DEFAULT_SAFE, DEFAULT_FILLER, DEFAULT_CHARSETNAME);
	}

	public TextFixedWidth(Fields fields, int[] lengths, Type[] types,
			boolean strict, boolean safe) {
		this(fields, lengths, types, DEFAULT_SINKCOMPRESSION, strict, safe,
				DEFAULT_FILLER, DEFAULT_CHARSETNAME);
	}

	public TextFixedWidth(Fields fields, int[] lengths, Type[] types,
			boolean strict, boolean safe, String charsetName) {
		this(fields, lengths, types, DEFAULT_SINKCOMPRESSION, strict, safe,
				DEFAULT_FILLER, charsetName);
	}

	public TextFixedWidth(Fields fields, int[] lengths, Type[] types,
			Compress sinkCompression, boolean strict, boolean safe,
			char filler, String charsetName) {
		super(sinkCompression);

		// normalizes ALL and UNKNOWN
		setSinkFields(fields);
		setSourceFields(fields);

		// throws an exception if not found
		setCharsetName(charsetName);

		this.filler = filler;
		this.strict = strict;

		// SonarQube: Constructors and methods receiving arrays should clone
		// objects and
		// store the copy. This prevents that future changes from the user
		// affect the internal functionality
		this.lengths = lengths == null ? null : lengths.clone();
		this.types = types == null ? null : types.clone();
		this.safe = safe;
		this.charsetName = charsetName;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean source(FlowProcess<? extends Configuration> flowProcess,
			SourceCall<Object[], RecordReader> sourceCall) throws IOException {
		Object[] context = sourceCall.getContext();

		if (!sourceCall.getInput().next(context[0], context[1]))
			return false;

		Object[] split = FixedWidthHelper.splitLine(getSourceFields(),
				makeEncodedString(context), lengths, types, safe, strict);
		Tuple tuple = sourceCall.getIncomingEntry().getTuple();

		tuple.clear();

		tuple.addAll(split);

		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void sinkPrepare(FlowProcess<? extends Configuration> flowProcess,
			SinkCall<Object[], OutputCollector> sinkCall) throws IOException {
		sinkCall.setContext(new Object[3]);

		sinkCall.getContext()[0] = new Text();
		sinkCall.getContext()[1] = new StringBuilder(4 * 1024);
		sinkCall.getContext()[2] = Charset.forName(charsetName);

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void sink(FlowProcess<? extends Configuration> flowProcess,
			SinkCall<Object[], OutputCollector> sinkCall) throws IOException {
		Tuple tuple = sinkCall.getOutgoingEntry().getTuple();

		write(sinkCall, tuple);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void write(SinkCall<Object[], OutputCollector> sinkCall,
			Iterable tuple) throws IOException {
		Text text = (Text) sinkCall.getContext()[0];
		StringBuilder line = (StringBuilder) sinkCall.getContext()[1];
		Charset charset = (Charset) sinkCall.getContext()[2];

		TupleEntry tupleEntry = sinkCall.getOutgoingEntry();

		line = (StringBuilder) FixedWidthHelper.createLine(
				tupleEntry.getTuple(), line, filler, lengths, strict,
				getSinkFields().getTypes(), getSinkFields());

		text.set(line.toString().getBytes(charset));
		sinkCall.getOutput().collect(null, text);
		line.setLength(0);
	}

}
