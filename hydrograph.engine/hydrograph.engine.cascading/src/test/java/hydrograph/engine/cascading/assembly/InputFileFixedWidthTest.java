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
import hydrograph.engine.cascading.assembly.InputFileFixedWidthAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.InputFileFixedWidthEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.utilites.AssemblyBuildHelper;
import hydrograph.engine.utilites.CascadingTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data.InputData.inputFileFixedWidth;

public class InputFileFixedWidthTest {

	@Test
	public void itShouldCheckFieldLengthOfFixedWidthInputFile()
			throws IOException {

		String outPath = "testData/component/input/output/FixedWidthOutput";

		FlowDef flowDef = FlowDef.flowDef();

		ComponentParameters cpInput = new ComponentParameters();
		cpInput.setFlowDef(flowDef);
		Map<String, String> socketTypeId = new HashMap<String, String>();
		socketTypeId.put("input", "out");

		InputFileFixedWidthEntity inputFileFixedWidthEntity = new InputFileFixedWidthEntity();
		inputFileFixedWidthEntity.setComponentId("input");
		inputFileFixedWidthEntity.setPath(inputFileFixedWidth);

		List<SchemaField> fields = new ArrayList<SchemaField>();
		SchemaField sf1 = new SchemaField("id", "java.lang.String");
		SchemaField sf2 = new SchemaField("name", "java.lang.String");
		SchemaField sf3 = new SchemaField("income", "java.lang.String");
		SchemaField sf4 = new SchemaField("val", "java.math.BigDecimal");
		sf4.setFieldScale(3);
		sf4.setFieldScaleType("implicit");
		SchemaField sf5 = new SchemaField("date", "java.util.Date");
		sf5.setFieldFormat("yyyy-MM-dd");

		sf1.setFieldLength(1);
		sf2.setFieldLength(4);
		sf3.setFieldLength(5);
		sf4.setFieldLength(6);
		sf5.setFieldLength(10);

		fields.add(sf1);
		fields.add(sf2);
		fields.add(sf3);
		fields.add(sf4);
		fields.add(sf5);

		List<OutSocket> outSockets = new ArrayList<OutSocket>();
		outSockets.add(new OutSocket("input"));

		inputFileFixedWidthEntity.setFieldsList(fields);

		inputFileFixedWidthEntity.setOutSocketList(outSockets);
		InputFileFixedWidthAssembly inputFile = new InputFileFixedWidthAssembly(
				inputFileFixedWidthEntity, cpInput);

		AssemblyBuildHelper.generateOutputPipes(
				inputFile.getOutLink("out", "input",
						inputFileFixedWidthEntity.getComponentId()), outPath,
				flowDef);

		Flow<?> flow = new Hadoop2MR1FlowConnector().connect(flowDef);
		flow.complete();
		CascadingTestCase.validateFieldLength(flow.openSink(), 5);

	}

}
