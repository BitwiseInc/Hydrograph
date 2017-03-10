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
package hydrograph.engine.cascading.assembly;

import cascading.flow.Flow;
import cascading.flow.FlowDef;
import cascading.flow.hadoop2.Hadoop2MR1FlowConnector;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.DiscardAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.coercetype.StrictDateType;
import hydrograph.engine.core.component.entity.DiscardEntity;
import hydrograph.engine.utilites.AssemblyBuildHelper;
import hydrograph.engine.utilites.CascadingTestCase;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Properties;

import static data.InputData.discardInput;

@SuppressWarnings({ "unused", "rawtypes" })
public class DiscardAssemblyTest {
	@Test
	public void itShouldTestDiscardAssembly() throws IOException {
		Properties runtimeProp = new Properties();
		runtimeProp.setProperty("prop", "propValue");
		StrictDateType strictDateType = new StrictDateType("dd-MM-yyyy");
		Fields fields = new Fields("name", "surname", "city", "val", "date")
				.applyTypes(String.class, String.class, String.class,
						BigDecimal.class, strictDateType);
		FlowDef flowDef = FlowDef.flowDef();

		Pipe pipes = AssemblyBuildHelper.generateInputPipes(fields, flowDef,
				discardInput);

		DiscardEntity entity = new DiscardEntity();
		entity.setComponentId("Trash");

		ComponentParameters parameters = new ComponentParameters();
		parameters.addInputPipe(pipes);
		parameters.setFlowDef(flowDef);
		parameters.addInputFields(fields);

		new DiscardAssembly(entity, parameters);

		Flow flow = new Hadoop2MR1FlowConnector().connect(flowDef);
		flow.complete();

		// assert the actual results with expected results
		CascadingTestCase.validateFileLength(flow.openSink(), 0);
	}
}
