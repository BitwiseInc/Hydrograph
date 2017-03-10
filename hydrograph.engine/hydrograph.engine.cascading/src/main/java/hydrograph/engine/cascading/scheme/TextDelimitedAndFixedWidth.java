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
import cascading.scheme.Scheme;
import cascading.scheme.SinkCall;
import cascading.scheme.SourceCall;
import cascading.scheme.util.DelimitedParser;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import hydrograph.engine.hadoop.inputformat.DelimitedAndFixedWidthInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;

@SuppressWarnings("rawtypes")
public class TextDelimitedAndFixedWidth
		extends
		Scheme<Configuration, RecordReader, OutputCollector, Object[], Object[]> {

	public enum Compress {
		DEFAULT, ENABLE, DISABLE
	}

	protected char filler;
	protected boolean strict;
	protected boolean safe;
	protected int[] lengths;
	protected Type[] types;
	protected String quote;

	public static final String DEFAULT_CHARSET = "UTF-8";
	private static final long serialVersionUID = 1L;
	public static final Fields DEFAULT_SOURCE_FIELDS = new Fields("offset",
			"line");
	public static final char DEFAULT_FILLER = ' ';
	public static final boolean DEFAULT_STRICT = false;
	public static final boolean DEFAULT_SAFE = false;
	private static final Type[] DEFAULT_TYPES = null;
	private static final String DEFAULT_QUOTE = "";

	Compress sinkCompression = Compress.DISABLE;
	String charsetName = DEFAULT_CHARSET;
	String[] lengthsAndDelimiters, lengthsAndDelimitersType;
	Type[] typesOfLengthsAndDelimiters;
	Calendar calender;
	Text text;
	Charset charset;
	String line;
	DelimitedParser delimitedParser;
	StringBuilder sb = new StringBuilder();
	String recordToBeSpilled = "";
	boolean hasaNewLineField = true;
	boolean isLastFieldNewLine = true;

	@ConstructorProperties({ "sourceFields" })
	public TextDelimitedAndFixedWidth(Fields sourceFields,
			String[] lengthsAndDelimiters, Type[] typesOfLengthsAndDelimiters) {
		this(sourceFields, lengthsAndDelimiters, typesOfLengthsAndDelimiters,
				DEFAULT_TYPES, DEFAULT_STRICT, DEFAULT_SAFE, DEFAULT_FILLER,
				DEFAULT_CHARSET, DEFAULT_QUOTE);
	}

	public TextDelimitedAndFixedWidth(Fields sourceFields,
			String[] lengthsAndDelimiters, Type[] typesOfLengthsAndDelimiters,
			Type[] types) {
		this(sourceFields, lengthsAndDelimiters, typesOfLengthsAndDelimiters,
				types, DEFAULT_STRICT, DEFAULT_SAFE, DEFAULT_FILLER,
				DEFAULT_CHARSET, DEFAULT_QUOTE);
	}

	public TextDelimitedAndFixedWidth(Fields sourceFields,
			String[] lengthsAndDelimiters, Type[] typesOfLengthsAndDelimiters,
			Type[] types, boolean strict) {
		this(sourceFields, lengthsAndDelimiters, typesOfLengthsAndDelimiters,
				types, strict, DEFAULT_SAFE, DEFAULT_FILLER, DEFAULT_CHARSET,
				DEFAULT_QUOTE);
	}

	public TextDelimitedAndFixedWidth(Fields sourceFields,
			String[] lengthsAndDelimiters, Type[] typesOfLengthsAndDelimiters,
			Type[] types, boolean strict, boolean safe) {
		this(sourceFields, lengthsAndDelimiters, typesOfLengthsAndDelimiters,
				types, strict, safe, DEFAULT_FILLER, DEFAULT_CHARSET,
				DEFAULT_QUOTE);
	}

	public TextDelimitedAndFixedWidth(Fields sourceFields,
			String[] lengthsAndDelimiters, Type[] typesOfLengthsAndDelimiters,
			Type[] types, boolean strict, boolean safe, String charsetName) {
		this(sourceFields, lengthsAndDelimiters, typesOfLengthsAndDelimiters,
				types, strict, safe, DEFAULT_FILLER, charsetName, DEFAULT_QUOTE);
	}

	public TextDelimitedAndFixedWidth(Fields sourceFields,
			String[] lengthsAndDelimiters, Type[] typesOfLengthsAndDelimiters,
			Type[] types, boolean strict, boolean safe, String charsetName,
			String quote) {
		this(sourceFields, lengthsAndDelimiters, typesOfLengthsAndDelimiters,
				types, strict, safe, DEFAULT_FILLER, charsetName, quote);
	}

	public TextDelimitedAndFixedWidth(Fields sourceFields,
			String[] lengthsAndDelimiters, Type[] typesOfLengthsAndDelimiters,
			String quote) {
		this(sourceFields, lengthsAndDelimiters, typesOfLengthsAndDelimiters,
				DEFAULT_TYPES, DEFAULT_STRICT, DEFAULT_SAFE, DEFAULT_FILLER,
				DEFAULT_CHARSET, quote);
	}

	public TextDelimitedAndFixedWidth(Fields fields,
			String[] lengthsAndDelimiters, Type[] typesOfLengthsAndDelimiters,
			Type[] types, boolean strict, boolean safe, char filler,
			String charsetName, String quote) {
		super(fields, fields);
		setCharsetName(charsetName);

		// SonarQube: Constructors and methods receiving arrays should clone
		// objects and store the copy. This prevents that future changes from
		// the user affect the internal functionality
		this.lengthsAndDelimiters = lengthsAndDelimiters.clone();
		this.typesOfLengthsAndDelimiters = typesOfLengthsAndDelimiters.clone();
		this.hasaNewLineField = DelimitedAndFixedWidthHelper
				.hasaNewLineField(lengthsAndDelimiters);
		this.isLastFieldNewLine = DelimitedAndFixedWidthHelper
				.isLastFieldNewLine(lengthsAndDelimiters);
		this.types = types == null ? null : types.clone();
		this.strict = strict;
		this.safe = safe;
		this.lengthsAndDelimitersType = Arrays.toString(
				typesOfLengthsAndDelimiters).split(",");
		this.filler = filler;
		this.quote = quote == null ? "" : quote;
	}

	public Compress getSinkCompression() {
		return sinkCompression;
	}

	protected void setCharsetName(String charsetName) {
		if (charsetName != null)
			this.charsetName = charsetName;

		Charset.forName(this.charsetName);
	}

	public void setSinkCompression(Compress sinkCompression) {
		if (sinkCompression != null)
			this.sinkCompression = sinkCompression;
	}

	private boolean hasZippedFiles(Path[] paths) {
		if (paths == null || paths.length == 0)
			return false;
		boolean isZipped = paths[0].getName().endsWith(".zip");
		for (int i = 1; i < paths.length; i++) {
			if (isZipped != paths[i].getName().endsWith(".zip"))
				throw new IllegalStateException(
						"cannot mix zipped and upzipped files");
		}
		return isZipped;
	}

	@Override
	public void sourceCleanup(FlowProcess<? extends Configuration> flowProcess,
			SourceCall<Object[], RecordReader> sourceCall) {
		sourceCall.setContext(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean source(FlowProcess<? extends Configuration> flowProcess,
			SourceCall<Object[], RecordReader> sourceCall) throws IOException {
		if (!sourceCall.getInput().next(sourceCall.getContext()[0],
				sourceCall.getContext()[1]))
			return false;
		Tuple tuple = sourceCall.getIncomingEntry().getTuple();
		tuple.clear();
		tuple.addAll(new Tuple(DelimitedAndFixedWidthHelper.getFields(
				getSourceFields(), makeEncodedString(sourceCall.getContext()),
				lengthsAndDelimiters, lengthsAndDelimitersType, types, safe, quote)));
		return true;
	}

	@Override
	public void sourcePrepare(FlowProcess<? extends Configuration> flowProcess,
			SourceCall<Object[], RecordReader> sourceCall) {
		if (sourceCall.getContext() == null)
			sourceCall.setContext(new Object[3]);

		sourceCall.getContext()[0] = sourceCall.getInput().createKey();
		sourceCall.getContext()[1] = sourceCall.getInput().createValue();
		sourceCall.getContext()[2] = Charset.forName(charsetName);
	}

	protected String makeEncodedString(Object[] context) {
		Text temporary = (Text) context[1];
		return temporary.toString();
	}

	@Override
	public void sourceConfInit(
			FlowProcess<? extends Configuration> flowProcess,
			Tap<Configuration, RecordReader, OutputCollector> tap,
			Configuration conf) {
		if (hasZippedFiles(FileInputFormat
				.getInputPaths(asJobConfInstance(conf))))
			throw new IllegalStateException("cannot read zip files: "
					+ Arrays.toString(FileInputFormat
							.getInputPaths(asJobConfInstance(conf))));

		conf.setBoolean("mapred.mapper.new-api", false);
		conf.setClass("mapred.input.format.class",
				DelimitedAndFixedWidthInputFormat.class, InputFormat.class);
		conf.set("charsetName", charsetName);
		conf.set("quote", quote);
		conf.set("lengthsAndDelimiters", DelimitedAndFixedWidthHelper
				.arrayToString(lengthsAndDelimiters));
		conf.setStrings("lengthsAndDelimitersType", lengthsAndDelimitersType);
	}

	public static JobConf asJobConfInstance(Configuration configuration) {
		if (configuration instanceof JobConf)
			return (JobConf) configuration;

		return new JobConf(configuration);
	}

	@Override
	public void sinkConfInit(FlowProcess<? extends Configuration> flowProcess,
			Tap<Configuration, RecordReader, OutputCollector> tap,
			Configuration conf) {
		if (tap.getFullIdentifier(conf).endsWith(".zip"))
			throw new IllegalStateException("cannot write zip files: "
					+ getOutputPath(conf));
		conf.setBoolean("mapred.mapper.new-api", false);
		if (getSinkCompression() == Compress.DISABLE)
			conf.setBoolean("mapred.output.compress", false);
		else if (getSinkCompression() == Compress.ENABLE)
			conf.setBoolean("mapred.output.compress", true);
		conf.setClass("mapred.output.key.class", Text.class, Object.class);
		conf.setClass("mapred.output.value.class", Text.class, Object.class);
		conf.setClass("mapred.output.format.class", TextOutputFormat.class,
				OutputFormat.class);
	}

	public static Path getOutputPath(Configuration conf) {
		String name = conf.get("mapred.output.dir");
		return name == null ? null : new Path(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sink(FlowProcess<? extends Configuration> flowProcess,
			SinkCall<Object[], OutputCollector> sinkCall) throws IOException {
		text = (Text) sinkCall.getContext()[0];
		charset = (Charset) sinkCall.getContext()[1];
		sb.append(DelimitedAndFixedWidthHelper.createLine(sinkCall
				.getOutgoingEntry().getTuple(), lengthsAndDelimiters,
				lengthsAndDelimitersType, strict, filler, types, quote));
		if (hasaNewLineField) {
			recordToBeSpilled = DelimitedAndFixedWidthHelper
					.spillOneLineToOutput(sb, lengthsAndDelimiters);
			sinkCall.getOutput().collect(null, new Text(recordToBeSpilled));
			sb = new StringBuilder(sb.toString().replace(recordToBeSpilled, "")
					.trim());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sinkCleanup(FlowProcess<? extends Configuration> flowProcess,
			SinkCall<Object[], OutputCollector> sinkCall) throws IOException {
		sinkCall.getOutput().collect(null, new Text(sb.toString()));
		sb.setLength(0);
	}

	@Override
	public void sinkPrepare(FlowProcess<? extends Configuration> flowProcess,
			SinkCall<Object[], OutputCollector> sinkCall) throws IOException {
		sinkCall.setContext(new Object[3]);
		sinkCall.getContext()[0] = new Text();
		sinkCall.getContext()[1] = Charset.forName(charsetName);
	}
}
