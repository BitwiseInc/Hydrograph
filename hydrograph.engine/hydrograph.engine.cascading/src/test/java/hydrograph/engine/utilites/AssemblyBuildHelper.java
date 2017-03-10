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
package hydrograph.engine.utilites;

import cascading.flow.FlowDef;
import cascading.pipe.Pipe;
import cascading.scheme.Scheme;
import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.SinkMode;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AssemblyBuildHelper {

	static Tap taps;
	static Pipe pipes;
	static FlowDef flow;
	static int dummy = 0;

	public static Pipe generateInputPipes(Fields fields, FlowDef flowDef, String filePath) {
		Scheme scheme = new TextDelimited(fields, true, ",");
		flow = flowDef;
		taps = new Hfs(scheme, filePath);
		pipes = new Pipe(String.valueOf(Math.random()));
		flow.addSource(pipes, taps);

		return pipes;
	}

	public static Pipe generateInputPipes(Fields fields, FlowDef flowDef, String filePath, boolean hasHeader) {
		Scheme scheme = new TextDelimited(fields, false, ",");
		flow = flowDef;
		taps = new Hfs(scheme, filePath);
		pipes = new Pipe(String.valueOf(Math.random()));
		flow.addSource(pipes, taps);

		return pipes;
	}

	public static Pipe generateInputPipes(Fields fields, String Delimiter, Boolean Header, FlowDef flowDef,
			String filePath) {
		Scheme scheme = new TextDelimited(fields, Header, Delimiter);
		flow = flowDef;
		taps = new Hfs(scheme, filePath);
		pipes = new Pipe("InputPipe");
		flow.addSource(pipes, taps);

		return pipes;
	}

	public static void generateOutputPipes(Fields fields, Pipe pipes, String filePath, FlowDef flowDef) {
		Scheme scheme = new TextDelimited(fields, true, ",");
		flow = flowDef;
		taps = new Hfs(scheme, filePath + pipes.getName(), SinkMode.REPLACE);
		flow.addTailSink(pipes, taps);

	}

	public static void generateOutputPipes(Pipe pipes, String filePath, FlowDef flowDef) {
		Scheme scheme = new TextDelimited(true, ",");
		flow = flowDef;

		taps = new Hfs(scheme, filePath + "/" + pipes.getName(), SinkMode.REPLACE);
		flow.addTailSink(pipes, taps);
	}

	public static FlowDef getFlow() {
		return flow;
	}

	public static Tap generateOutTaps(String filePath) {
		Scheme scheme = new TextDelimited(true, ",");
		taps = new Hfs(scheme, filePath, SinkMode.REPLACE);
		return taps;
	}

	/*
	 * public static Properties setProperty(ComponentParameters params) {
	 * Properties props = new Properties();
	 * props.put("cascading.compatibility.retain.collector", "true");
	 * params.addHadoopLocalProperties(props); return props;
	 * 
	 * }
	 */

}
