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
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.InputFileParquetAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.InputFileParquetEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.utilites.AssemblyBuildHelper;
import hydrograph.engine.utilites.CascadingTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static data.InputData.inputFileParquet;

public class InputFileParquetFileTest {
	@Test
	public void itShouldTestParquetInputFileAssembly() throws IOException {

		String outPath = "testData/component/output/parquetIPFile";

		Fields fields = new Fields("name", "surname", "city", "val", "date");

		InputFileParquetEntity entity = new InputFileParquetEntity();
		entity.setPath(inputFileParquet);
		entity.setComponentId("input");

		List<SchemaField> fieldList = new ArrayList<SchemaField>();
		SchemaField sf1 = new SchemaField("name", "java.lang.String");
		SchemaField sf2 = new SchemaField("surname", "java.lang.String");
		SchemaField sf3 = new SchemaField("city", "java.lang.String");
		SchemaField sf4 = new SchemaField("val", "java.lang.String");
		SchemaField sf5 = new SchemaField("date", "java.lang.String");

		fieldList.add(sf1);
		fieldList.add(sf2);
		fieldList.add(sf3);
		fieldList.add(sf4);
		fieldList.add(sf5);

		entity.setFieldsList(fieldList);

		Properties runtimeProp = new Properties();
		runtimeProp.setProperty("prop", "propValue");

		entity.setRuntimeProperties(runtimeProp);
		List<OutSocket> outSockets = new ArrayList<OutSocket>();
		outSockets.add(new OutSocket("outSocket"));
		entity.setOutSocketList(outSockets);

		FlowDef flowDef = FlowDef.flowDef();
		ComponentParameters cpInput = new ComponentParameters();
		cpInput.setFlowDef(flowDef);

		InputFileParquetAssembly inputFileParquetAssembly = new InputFileParquetAssembly(entity, cpInput);

		AssemblyBuildHelper.generateOutputPipes(fields,
				inputFileParquetAssembly.getOutLink("out", "outSocket", entity.getComponentId()), outPath, flowDef);

		Flow<?> flow = new Hadoop2MR1FlowConnector().connect(flowDef);
		flow.complete();
		CascadingTestCase.validateFieldLength(flow.openSink(), 5);

	}
}
