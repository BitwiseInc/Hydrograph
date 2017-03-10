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
import hydrograph.engine.cascading.assembly.InputFileDelimitedAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.InputFileDelimitedEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.utilites.AssemblyBuildHelper;
import hydrograph.engine.utilites.CascadingTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static data.InputData.inputFileDelimited;

public class InputFileDelimitedTest {

	@Test
	public void itShouldCheckFieldLengthOfDelimitedInputFile()
			throws IOException {

		String outPath = "testData/component/input/output/file1_input";

		FlowDef flowDef = FlowDef.flowDef();

		ComponentParameters cpInput = new ComponentParameters();
		cpInput.setFlowDef(flowDef);

		InputFileDelimitedEntity inputFileDelimitedEntity = new InputFileDelimitedEntity();
		inputFileDelimitedEntity.setComponentId("input");
		inputFileDelimitedEntity.setPath(inputFileDelimited);
		inputFileDelimitedEntity.setDelimiter(",");
		List<SchemaField> fieldList = new ArrayList<SchemaField>();
		SchemaField sf1 = new SchemaField("name", "java.lang.String");
		SchemaField sf2 = new SchemaField("surname", "java.lang.String");
		SchemaField sf3 = new SchemaField("city", "java.lang.String");
		SchemaField sf4 = new SchemaField("val", "java.math.BigDecimal");
		sf4.setFieldScale(3);
		sf4.setFieldScaleType("explicit");
		SchemaField sf5 = new SchemaField("date", "java.util.Date");
		sf5.setFieldFormat("yyyy-MM-dd");

		fieldList.add(sf1);
		fieldList.add(sf2);
		fieldList.add(sf3);
		fieldList.add(sf4);
		fieldList.add(sf5);

		List<OutSocket> outSockets = new ArrayList<OutSocket>();
		outSockets.add(new OutSocket("outSocket"));

		inputFileDelimitedEntity.setFieldsList(fieldList);

		inputFileDelimitedEntity.setOutSocketList(outSockets);
		InputFileDelimitedAssembly inputFile = new InputFileDelimitedAssembly(
				inputFileDelimitedEntity, cpInput);

		AssemblyBuildHelper.generateOutputPipes(inputFile.getOutLink("out",
				"outSocket", inputFileDelimitedEntity.getComponentId()),
				outPath, flowDef);

		Flow<?> flow = new Hadoop2MR1FlowConnector().connect(flowDef);
		flow.complete();
		CascadingTestCase.validateFieldLength(flow.openSink(), 5);

	}

}
