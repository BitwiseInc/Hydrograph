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
package hydrograph.engine.cascading.schemes;

import cascading.flow.Flow;
import cascading.flow.FlowDef;
import cascading.flow.hadoop2.Hadoop2MR1FlowConnector;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.scheme.Scheme;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;
import cascading.tuple.TupleEntryIterator;
import hydrograph.engine.cascading.scheme.TextDelimitedAndFixedWidth;
import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import static data.InputData.*;
import static org.junit.Assert.assertEquals;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MixedSchemeTestsWithEncoding {

	String inPath;
	String outPath;
	Hadoop2MR1FlowConnector flowConnector;
	Fields fields, fields_new;
	Class[] types;
	Type[] typesOfLengthsAndDelimiters;
	Scheme inScheme, outScheme;
	Pipe pipe, pipe2, pipe3;
	Tap inTap;
	Tap outTap;
	Flow flow;
	TupleEntryIterator sourceIterator;
	TupleEntryIterator sinkIterator;
	FlowDef flowDef;

	@Before
	public void prepare() {
		outPath = "testData/schemes/TextMixed/output";

		Configuration conf = new Configuration();
		Properties properties = new Properties();
		properties.putAll(conf.getValByRegex(".*"));
		AppProps.setApplicationJarClass(properties,
				MixedSchemeTestsWithEncoding.class);
		flowConnector = new Hadoop2MR1FlowConnector(properties);

		fields = new Fields("f1", "f2", "f3", "f4", "f5");
		fields_new = new Fields("f1", "f2", "f3", "f4", "f5");
	}

	@Test
	public void utf8InputFileWindowsEOLTest() throws IOException {

		String[] inputLengthsAndDelimiters = { "!", "\\xC7", "6", "\\xC6", "\n" };
		String[] outputLengthsAndDelimiters1 = { "!", "\\xC7", "6", "\\xC6",
				"\n" };
		types = new Class[] { Integer.class, Date.class, String.class,
				BigDecimal.class, Long.class };
		typesOfLengthsAndDelimiters = new Class[] { String.class, String.class,
				Integer.class, String.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				true, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters,
				types, true, false, "UTF-8");
		inTap = new Hfs(inScheme, utf8inputfilewindowsEOL);
		outTap = new Hfs(outScheme, outPath + "/utf8outputfilewindowsEOL",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllFields
		assertEquals(5, tupleRead.size());
		assertEquals("111\t21092013\taaaaaa\t1234.567\t11111111", tupleRead
				.toString().replace("\r", ""));
	}

	@Test
	public void isoInputFileWindowsEOLTest() throws IOException {

		String[] inputLengthsAndDelimiters = { "!", "\\xC7", "6", "\\xC6", "\n" };
		String[] outputLengthsAndDelimiters1 = { "!", "\\xC7", "6", "\\xC6",
				"\n" };
		types = new Class[] { Integer.class, Date.class, String.class,
				BigDecimal.class, Long.class };
		typesOfLengthsAndDelimiters = new Class[] { String.class, String.class,
				Integer.class, String.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				true, false, "ISO-8859-1");
		outScheme = new TextDelimitedAndFixedWidth(fields,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters,
				types, true, false, "ISO-8859-1");
		inTap = new Hfs(inScheme, isoinputfilewindowsEOL);
		outTap = new Hfs(outScheme, outPath + "/isooutputfilewindowsEOL",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
	}

	@Test
	public void utf8InputFileLinuxEOLTest() throws IOException {

		String[] inputLengthsAndDelimiters = { "!", "\\xC7", "6", "\\xC6", "\n" };
		String[] outputLengthsAndDelimiters1 = { "!", "\\xC7", "6", "\\xC6",
				"\n" };
		types = new Class[] { Integer.class, Date.class, String.class,
				BigDecimal.class, Long.class };
		typesOfLengthsAndDelimiters = new Class[] { String.class, String.class,
				Integer.class, String.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				true, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters,
				types, true, false, "UTF-8");
		inTap = new Hfs(inScheme, utf8inputfilelinuxEOL);
		outTap = new Hfs(outScheme, outPath + "/utf8outputfilelinuxEOL",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllFields
		assertEquals(5, tupleRead.size());
		assertEquals("111\t21092013\taaaaaa\t1234.567\t11111111", tupleRead
				.toString().replace("\r", ""));
	}

	@Test
	public void isoInputFileLinuxEOLTest() throws IOException {

		String[] inputLengthsAndDelimiters = { "!", "\\xC7", "6", "\\xC6", "\n" };
		String[] outputLengthsAndDelimiters1 = { "!", "\\xC7", "6", "\\xC6",
				"\n" };
		types = new Class[] { Integer.class, Date.class, String.class,
				BigDecimal.class, Long.class };
		typesOfLengthsAndDelimiters = new Class[] { String.class, String.class,
				Integer.class, String.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				true, false, "ISO-8859-1");
		outScheme = new TextDelimitedAndFixedWidth(fields,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters,
				types, true, false, "ISO-8859-1");
		inTap = new Hfs(inScheme, isoinputfilelinuxEOL);
		outTap = new Hfs(outScheme, outPath + "/isooutputfilelinuxEOL",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
	}
}
