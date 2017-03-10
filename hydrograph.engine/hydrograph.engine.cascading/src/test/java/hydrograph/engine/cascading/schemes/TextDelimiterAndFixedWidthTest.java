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
import java.util.Date;
import java.util.Properties;

import static data.InputData.*;
import static org.junit.Assert.assertEquals;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TextDelimiterAndFixedWidthTest {

	String inPath;
	String outPath;
	Hadoop2MR1FlowConnector flowConnector;
	Fields fields, fields_new;
	Class[] types;
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
				TextDelimiterAndFixedWidthTest.class);
		flowConnector = new Hadoop2MR1FlowConnector(properties);

		fields = new Fields("f1", "f2", "f3", "f4", "f5");
		fields_new = new Fields("f1", "f2", "f3", "f4", "f5", "newline");
	}

	@Test
	public void itShouldProduceValidResultsForSimpleMixedScheme()
			throws IOException {

		String[] inputLengthsAndDelimiters = { "@,@", "3", "4", "3", "\n" };
		String[] outputLengthsAndDelimiters1 = { ",", "2", "6", "4", "\n" };
		types = new Class[] { String.class, String.class, Integer.class,
				String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				Integer.class, Integer.class, Integer.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForSimpleMixedScheme);
		outTap = new Hfs(outScheme, outPath
				+ "/itShouldProduceValidResultsForSimpleMixedScheme",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
	}

	@Test
	public void itShouldProduceValidResultsForAllRecordsInOneLine()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "!", "4", ";", "@" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "@" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForAllRecordsInOneLine);
		outTap = new Hfs(outScheme, outPath
				+ "/itShouldProduceValidResultsForAllRecordsInOneLine",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(5, tupleRead.size());
		assertEquals("abc	asd	1234	qwe	rty",
				tupleRead.toString().replace("\r", ""));
	}

	@Test
	public void itShouldProduceValidResultsForCedillaDelimitedRecords()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "\\xC7", "4", "\\xC7", "\n" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "\n" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForCedillaDelimitedRecords);
		outTap = new Hfs(outScheme, outPath
				+ "/itShouldProduceValidResultsForCedillaDelimitedRecords",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(5, tupleRead.size());
		assertEquals("abc	sed	1234	qwe	rty",
				tupleRead.toString().replace("\r", ""));
	}

	@Test
	public void itShouldProduceValidResultsForRecordSpanningMultipleLines()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "\n", "4", "\\xC7", "\n" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "@" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForRecordSpanningMultipleLines);
		outTap = new Hfs(outScheme, outPath
				+ "/itShouldProduceValidResultsForRecordSpanningMultipleLines",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(5, tupleRead.size());
		assertEquals("abc	ase	1234	qwe	rty",
				tupleRead.toString().replace("\r", ""));
	}

	@Test
	public void itShouldProduceValidResultsForRecordWithLastFixedWidthField()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "\n", "\\xC7", "\n", "4" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "@" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, String.class, String.class, Integer.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields_new,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields_new,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForRecordWithLastFixedWidthField);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForRecordWithLastFixedWidthField",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(5, tupleRead.size());
		assertEquals("abc	ase	qwe	rty	1234",
				tupleRead.toString().replace("\r", ""));
	}

	@Test
	public void itShouldProduceValidResultsForSimpleMixedSchemeWithFixedNewlineField()
			throws IOException {

		String[] inputLengthsAndDelimiters = { "@", "3", "4", "3", "\n", "1" };
		String[] outputLengthsAndDelimiters1 = { ",", "2", "6", "4", "\n", "1" };
		types = new Class[] { String.class, String.class, Integer.class,
				String.class, String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				Integer.class, Integer.class, Integer.class, String.class,
				Integer.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				Integer.class, Integer.class, Integer.class, String.class,
				Integer.class };
		inScheme = new TextDelimitedAndFixedWidth(fields_new,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields_new,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForSimpleMixedSchemeWithFixedNewlineField);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForSimpleMixedSchemeWithFixedNewlineField",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(6, tupleRead.size());
		assertEquals("abc	ase	1234	qwe	rty	",
				tupleRead.toString().replace("\r", "").replace("\n", ""));
	}

	@Test
	public void itShouldProduceValidResultsForAllRecordsInOneLineWithFixedNewlineField()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "!", "4", ";", "@", "1" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "@", "1" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				Integer.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				Integer.class };
		inScheme = new TextDelimitedAndFixedWidth(fields_new,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields_new,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForAllRecordsInOneLineWithFixedNewlineField);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForAllRecordsInOneLineWithFixedNewlineField",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		assertEquals(6, tupleRead.size());
		assertEquals("abc	asd	1234	qwe	rty	",
				tupleRead.toString().replace("\r", "").replace("\n", ""));
	}

	@Test
	public void itShouldProduceValidResultsForCedillaDelimitedRecordsWithFixedNewlineField()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "\\xC7", "4", "\\xC7", "@",
				"1" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "@", "1" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				Integer.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				Integer.class };
		inScheme = new TextDelimitedAndFixedWidth(fields_new,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields_new,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForCedillaDelimitedRecordsWithFixedNewlineField);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForCedillaDelimitedRecordsWithFixedNewlineField",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(6, tupleRead.size());
		assertEquals("abc	sed	1234	qwe	rty	",
				tupleRead.toString().replace("\r", "").replace("\n", ""));
	}

	@Test
	public void itShouldProduceValidResultsForRecordSpanningMultipleLinesWithFixedNewlineField()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "\n", "4", "\\xC7", "\n",
				"1" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "@", "1" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				Integer.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				Integer.class };
		inScheme = new TextDelimitedAndFixedWidth(fields_new,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields_new,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForRecordSpanningMultipleLinesWithFixedNewlineField);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForRecordSpanningMultipleLinesWithFixedNewlineField",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(6, tupleRead.size());
		assertEquals("abc	ase	1234	qwe	rty	",
				tupleRead.toString().replace("\r", "").replace("\n", ""));
	}

	@Test
	public void itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndFixedNewlineField()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "\n", "\\xC7", "\n", "4",
				"1" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "@", "1" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, String.class, String.class, Integer.class,
				Integer.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				Integer.class };
		inScheme = new TextDelimitedAndFixedWidth(fields_new,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields_new,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndFixedNewlineField);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndFixedNewlineField",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(6, tupleRead.size());
		assertEquals("abc	ase	qwe	rty	1234	",
				tupleRead.toString().replace("\r", "").replace("\n", ""));
	}

	@Test
	public void itShouldProduceValidResultsForSimpleMixedSchemeWithDelimitedNewlineField()
			throws IOException {

		String[] inputLengthsAndDelimiters = { "!", "3", "4", "3", "\n", "\n" };
		String[] outputLengthsAndDelimiters1 = { ",", "2", "6", "4", "\n", "\n" };
		types = new Class[] { String.class, String.class, Integer.class,
				String.class, String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				Integer.class, Integer.class, Integer.class, String.class,
				String.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				Integer.class, Integer.class, Integer.class, String.class,
				String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields_new,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields_new,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForSimpleMixedSchemeWithDelimitedNewlineField);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForSimpleMixedSchemeWithDelimitedNewlineField",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
	}

	@Test
	public void itShouldProduceValidResultsForAllRecordsInOneLineWithDelimitedNewlineField()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "!", "4", ";", "@", "\n" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "@", "\n" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				String.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields_new,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields_new,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForAllRecordsInOneLineWithDelimitedNewlineField);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForAllRecordsInOneLineWithDelimitedNewlineField",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(6, tupleRead.size());
		assertEquals("abc	asd	1234	qwe	rty	",
				tupleRead.toString().replace("\r", "").replace("\n", ""));
	}

	@Test
	public void itShouldProduceValidResultsForCedillaDelimitedRecordsWithDelimitedNewlineField()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "\\xC7", "4", "\\xC7", "@",
				"\n" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "@", "\n" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				String.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields_new,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields_new,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForCedillaDelimitedRecordsWithDelimitedNewlineField);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForCedillaDelimitedRecordsWithDelimitedNewlineField",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(6, tupleRead.size());
		assertEquals("abc	sed	1234	qwe	rty	",
				tupleRead.toString().replace("\r", "").replace("\n", ""));
	}

	@Test
	public void itShouldProduceValidResultsForRecordSpanningMultipleLinesWithDelimitedNewlineField()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "\n", "4", "\\xC7", "\n",
				"\n" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "@", "\n" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				String.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields_new,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields_new,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(
				inScheme,
				itShouldProduceValidResultsForRecordSpanningMultipleLinesWithDelimitedNewlineField);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForRecordSpanningMultipleLinesWithDelimitedNewlineField",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(6, tupleRead.size());
		assertEquals("abc	ase	1234	qwe	rty	",
				tupleRead.toString().replace("\r", "").replace("\n", ""));
	}

	@Test
	public void itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndDelimitedNewlineField()
			throws IOException {

		String[] inputLengthsAndDelimiters = { ":", "\n", "\\xC7", "\n", "4",
				"\n" };
		String[] outputLengthsAndDelimiters1 = { ",", ":", "4", "!", "@", "\n" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, String.class, String.class, Integer.class,
				String.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				String.class, Integer.class, String.class, String.class,
				String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields_new,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields_new,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(
				inScheme,
				itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndDelimitedNewlineField);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForRecordWithLastFixedWidthFieldAndDelimitedNewlineField",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(6, tupleRead.size());
		assertEquals("abc	ase	qwe	rty	1234	",
				tupleRead.toString().replace("\r", "").replace("\n", ""));
	}

	@Test
	public void itShouldProduceValidResultsForFixedWidthRecordsInOneLine()
			throws IOException {

		String[] inputLengthsAndDelimiters = { "3", "3", "4", "3", "3" };
		String[] outputLengthsAndDelimiters1 = { ",", ",", ",", ",", "\n" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { Integer.class,
				Integer.class, Integer.class, Integer.class, Integer.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				String.class, String.class, String.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForFixedWidthRecordsInOneLine);
		outTap = new Hfs(outScheme, outPath
				+ "/itShouldProduceValidResultsForFixedWidthRecordsInOneLine",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();
		Tuple tupleWritten = sinkIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(5, tupleRead.size());
		assertEquals("123	123	1234	123	123",
				tupleRead.toString().replace("\r", ""));
		// itShouldWriteAllRecords
		assertEquals("123	123	1234	123	123",
				tupleWritten.toString().replace("\r", ""));
	}

	@Test
	public void itShouldProduceValidResultsForDelimiterPresentInFixedWidthData()
			throws IOException {

		String[] inputLengthsAndDelimiters = { "2", "1", "2", "1", "\n" };
		String[] outputLengthsAndDelimiters1 = { ",", ",", ",", ",", "\n" };
		types = new Class[] { String.class, String.class, String.class,
				String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { Integer.class,
				Integer.class, Integer.class, Integer.class, String.class };
		Type[] typesOfLengthsAndDelimiters1 = new Class[] { String.class,
				String.class, String.class, String.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(fields,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters1);
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForDelimiterPresentInFixedWidthData);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForDelimiterPresentInFixedWidthData",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
		sourceIterator = flow.openSource();
		sinkIterator = flow.openSink();
		Tuple tupleRead = sourceIterator.next().getTuple();
		Tuple tupleWritten = sinkIterator.next().getTuple();

		// itShouldReadAllRecords
		assertEquals(5, tupleRead.size());
		assertEquals("12	3	12	3	12", tupleRead.toString().replace("\r", ""));
		// itShouldWriteAllRecords
		assertEquals("12	3	12	3	12", tupleWritten.toString().replace("\r", ""));
	}

	@Test
	public void itShouldProduceValidResultsForAllDataTypes() throws IOException {

		String[] inputLengthsAndDelimiters = { "!", "\t", "|", "5", "\n" };
		String[] outputLengthsAndDelimiters1 = { ",", "\t", "|", "3", "\n" };
		types = new Class[] { Integer.class, String.class, Date.class,
				Long.class, Float.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				String.class, String.class, Integer.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(new Fields("id", "name",
				"DOB", "salary", "rating"), inputLengthsAndDelimiters,
				typesOfLengthsAndDelimiters, types, false, false, "UTF-8");
		outScheme = new TextDelimitedAndFixedWidth(new Fields("id", "name",
				"DOB", "salary", "rating"), outputLengthsAndDelimiters1,
				typesOfLengthsAndDelimiters);
		inTap = new Hfs(inScheme, itShouldProduceValidResultsForAllDataTypes);
		outTap = new Hfs(outScheme, outPath
				+ "/itShouldProduceValidResultsForAllDataTypes",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
	}

	@Test
	public void itShouldProduceValidResultsForSimpleMixedSchemeWithQuoteChar()
			throws IOException {

		String[] inputLengthsAndDelimiters = { "@,@", "3", "4", "3", "|" };
		String[] outputLengthsAndDelimiters1 = { ",", "2", "6", "4", "|" };
		types = new Class[] { String.class, String.class, Integer.class,
				String.class, String.class };
		Type[] typesOfLengthsAndDelimiters = new Class[] { String.class,
				Integer.class, Integer.class, Integer.class, String.class };
		inScheme = new TextDelimitedAndFixedWidth(fields,
				inputLengthsAndDelimiters, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8", ".");
		outScheme = new TextDelimitedAndFixedWidth(fields,
				outputLengthsAndDelimiters1, typesOfLengthsAndDelimiters, types,
				false, false, "UTF-8","*");
		inTap = new Hfs(inScheme,
				itShouldProduceValidResultsForSimpleMixedSchemeWithQuoteChar);
		outTap = new Hfs(
				outScheme,
				outPath
						+ "/itShouldProduceValidResultsForSimpleMixedSchemeWithQuoteChar",
				SinkMode.REPLACE);
		pipe = new Pipe("pipe");
		flowDef = FlowDef.flowDef().addSource(pipe, inTap)
				.addTailSink(pipe, outTap);
		flow = flowConnector.connect(flowDef);
		flow.complete();
	}

}
