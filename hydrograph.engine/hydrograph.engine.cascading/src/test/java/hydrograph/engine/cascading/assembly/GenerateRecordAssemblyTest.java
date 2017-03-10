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
import hydrograph.engine.cascading.assembly.GenerateRecordAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.GenerateRecordEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.utilites.AssemblyBuildHelper;
import hydrograph.engine.utilites.CascadingTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerateRecordAssemblyTest {

	@Test
	public void itShouldGenerateRecord() throws IOException {
		String outPath = "testData/component/input/output/generate_data";

		FlowDef flowDef = FlowDef.flowDef();

		ComponentParameters cpInput = new ComponentParameters();
		cpInput.setFlowDef(flowDef);

		GenerateRecordEntity generateRecordEntity = new GenerateRecordEntity();
		generateRecordEntity.setComponentId("generate_data");
		generateRecordEntity.setRecordCount(new Long(10));
		List<SchemaField> fieldList = new ArrayList<SchemaField>();
		SchemaField sf1 = new SchemaField("f1", "java.lang.String");
		sf1.setFieldLength(5);

		SchemaField sf2 = new SchemaField("f2", "java.math.BigDecimal");
		sf2.setFieldScale(3);
		sf2.setFieldScaleType("explicit");

		SchemaField sf3 = new SchemaField("f3", "java.util.Date");
		sf3.setFieldFormat("yyyy-MM-dd");
		sf3.setFieldFromRangeValue("2015-10-31");
		sf3.setFieldToRangeValue("2015-12-31");

		SchemaField sf4 = new SchemaField("f4", "java.lang.Integer");
		sf4.setFieldFromRangeValue("100");
		sf4.setFieldToRangeValue("101");

		fieldList.add(sf1);
		fieldList.add(sf2);
		fieldList.add(sf3);
		fieldList.add(sf4);

		List<OutSocket> outSockets = new ArrayList<OutSocket>();
		outSockets.add(new OutSocket("outSocket"));

		generateRecordEntity.setFieldsList(fieldList);
		generateRecordEntity.setOutSocketList(outSockets);

		GenerateRecordAssembly generateRecord = new GenerateRecordAssembly(generateRecordEntity, cpInput);

		AssemblyBuildHelper.generateOutputPipes(
				generateRecord.getOutLink("out", "outSocket", generateRecordEntity.getComponentId()), outPath, flowDef);

		Flow<?> flow = new Hadoop2MR1FlowConnector().connect(flowDef);
		flow.complete();
		CascadingTestCase.validateFieldLength(flow.openSink(), 4);

	}
}
