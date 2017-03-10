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
import hydrograph.engine.cascading.assembly.InputFileHiveTextAssembly;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.InputFileHiveTextEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.utilites.AssemblyBuildHelper;
import hydrograph.engine.utilites.CascadingTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class InputFileHiveTextFileTest {
	@Test
	public void itShouldTestHiveTextInputFileAssembly() throws IOException {

		String databaseName = "devl_fraud";
		String tableName = "tsimple";
		String outPath = "../elt-command-line/testData/Output/outputFromHiveParquet";

		InputFileHiveTextEntity entity = new InputFileHiveTextEntity();
		entity.setDatabaseName(databaseName);
		entity.setTableName(tableName);
		entity.setComponentId("HiveTextInput");

		List<SchemaField> fieldList = new ArrayList<SchemaField>();
		SchemaField sf1 = new SchemaField("id", "java.lang.Integer");
		SchemaField sf2 = new SchemaField("fname", "java.lang.String");
		SchemaField sf3 = new SchemaField("date", "java.util.Date");
		SchemaField sf4 = new SchemaField("salary", "java.math.BigDecimal");
		sf4.setFieldScale(2);
		sf4.setFieldPrecision(6);
		sf3.setFieldFormat("yyyy-MM-dd");
		fieldList.add(sf1);
		fieldList.add(sf2);
		fieldList.add(sf3);
		fieldList.add(sf4);

		entity.setFieldsList(fieldList);
		entity.setDelimiter(",");
		/*entity.setPartitionKeys(new String[]{"salary"});*/

		Properties runtimeProp = new Properties();
		runtimeProp.setProperty("prop", "propValue");

		entity.setRuntimeProperties(runtimeProp);
		List<OutSocket> outSockets = new ArrayList<OutSocket>();
		outSockets.add(new OutSocket("outSocket"));
		entity.setOutSocketList(outSockets);

		FlowDef flowDef = FlowDef.flowDef();
		ComponentParameters cpInput = new ComponentParameters();
		cpInput.setFlowDef(flowDef);

		InputFileHiveTextAssembly inputFileHiveTextAssembly = new InputFileHiveTextAssembly(entity, cpInput);

		AssemblyBuildHelper.generateOutputPipes(
				inputFileHiveTextAssembly.getOutLink("out", "outSocket", entity.getComponentId()), outPath, flowDef);

		Flow<?> flow = new Hadoop2MR1FlowConnector().connect(flowDef);
		flow.complete();
		CascadingTestCase.validateFieldLength(flow.openSink(), 4);
		CascadingTestCase.validateFileLength(flow.openSink(), 6);

	}

	@Test
	public void itShouldTestHiveTextInputFileAssemblyWithPartition() throws IOException {

		String databaseName = "devl_fraud";
		String tableName = "tPartition";
		String outPath = "../elt-command-line/testData/Output/outputFromHiveParquetPartition";

		InputFileHiveTextEntity entity = new InputFileHiveTextEntity();
		entity.setDatabaseName(databaseName);
		entity.setTableName(tableName);
		entity.setComponentId("HiveTextInput");

		List<SchemaField> fieldList = new ArrayList<SchemaField>();
		// Id field should not be present in file as it is partitioned column.
		// This is the bug in cascading. This is field is kept as work around.
		SchemaField sf1 = new SchemaField("id", "java.lang.Integer");
		SchemaField sf2 = new SchemaField("fname", "java.lang.String");
		SchemaField sf3 = new SchemaField("lname", "java.util.Date");
		SchemaField sf4 = new SchemaField("salary", "java.math.BigDecimal");
		sf4.setFieldScale(2);
		sf4.setFieldPrecision(6);
		sf3.setFieldFormat("yyyy-MM-dd");
		fieldList.add(sf1);
		fieldList.add(sf2);
		fieldList.add(sf3);
		fieldList.add(sf4);

		String[] partitionField = new String[1];
		partitionField[0] = "salary";

		entity.setFieldsList(fieldList);
		entity.setPartitionKeys(partitionField);
		entity.setDelimiter(",");

		Properties runtimeProp = new Properties();
		runtimeProp.setProperty("prop", "propValue");

		entity.setRuntimeProperties(runtimeProp);
		List<OutSocket> outSockets = new ArrayList<OutSocket>();
		outSockets.add(new OutSocket("outSocket"));
		entity.setOutSocketList(outSockets);

		FlowDef flowDef = FlowDef.flowDef();
		ComponentParameters cpInput = new ComponentParameters();
		cpInput.setFlowDef(flowDef);

		InputFileHiveTextAssembly inputFileHiveTextAssembly = new InputFileHiveTextAssembly(entity, cpInput);

		AssemblyBuildHelper.generateOutputPipes(
				inputFileHiveTextAssembly.getOutLink("out", "outSocket", entity.getComponentId()), outPath, flowDef);

		Flow<?> flow = new Hadoop2MR1FlowConnector().connect(flowDef);
		flow.complete();
		CascadingTestCase.validateFieldLength(flow.openSink(), 4);

	}

	@Test
	public void itShouldTestHiveTextInputFileAssemblyWithExternalTable() throws IOException {

		String databaseName = "devl_fraud";
		String tableName = "t9";
		String externalTablePathUri = "../elt-command-line/testData/Output/HiveTextOutputExternalTable";
		String outPath = "../elt-command-line/testData/Output/outputFromHiveParquetExternalTable";

		InputFileHiveTextEntity entity = new InputFileHiveTextEntity();
		entity.setDatabaseName(databaseName);
		entity.setTableName(tableName);
		entity.setComponentId("HiveTextInput");
		entity.setDelimiter(",");

		List<SchemaField> fieldList = new ArrayList<SchemaField>();
		SchemaField sf1 = new SchemaField("id", "java.lang.Integer");
		SchemaField sf2 = new SchemaField("fname", "java.lang.String");
		SchemaField sf3 = new SchemaField("lname", "java.util.Date");
		SchemaField sf4 = new SchemaField("salary", "java.math.BigDecimal");
		sf4.setFieldScale(2);
		sf4.setFieldPrecision(6);
		sf3.setFieldFormat("yyyy-MM-dd");
		fieldList.add(sf1);
		fieldList.add(sf2);
		fieldList.add(sf3);
		fieldList.add(sf4);
		
		/*String[] partitionField = new String[1];
		partitionField[0] = "salary";*/
		
		//entity.setPartitionKeys(partitionField);
		entity.setFieldsList(fieldList);
		entity.setExternalTablePathUri(externalTablePathUri);

		Properties runtimeProp = new Properties();
		runtimeProp.setProperty("prop", "propValue");

		entity.setRuntimeProperties(runtimeProp);
		List<OutSocket> outSockets = new ArrayList<OutSocket>();
		outSockets.add(new OutSocket("outSocket"));
		entity.setOutSocketList(outSockets);

		FlowDef flowDef = FlowDef.flowDef();
		ComponentParameters cpInput = new ComponentParameters();
		cpInput.setFlowDef(flowDef);

		InputFileHiveTextAssembly inputFileHiveTextAssembly = new InputFileHiveTextAssembly(entity, cpInput);

		AssemblyBuildHelper.generateOutputPipes(
				inputFileHiveTextAssembly.getOutLink("out", "outSocket", entity.getComponentId()), outPath, flowDef);

		Flow<?> flow = new Hadoop2MR1FlowConnector().connect(flowDef);
		flow.complete();
		CascadingTestCase.validateFieldLength(flow.openSink(), 4);

	}

	@Test
	public void itShouldTestHiveTextInputFileAssemblyWithExternalShemaAndPartition() throws IOException {

		String databaseName = "devl_fraud";
		String tableName = "tPartitionExternal";
		String externalTablePathUri = "../elt-command-line/testData/Output/HiveParquetOutputPartitionExternalTable";
		String outPath = "../elt-command-line/testData/Output/romHiveParquetOutputPartitionExternalTable";

		InputFileHiveTextEntity entity = new InputFileHiveTextEntity();
		entity.setDatabaseName(databaseName);
		entity.setTableName(tableName);
		entity.setComponentId("HiveTextInput");
		entity.setDelimiter(",");

		List<SchemaField> fieldList = new ArrayList<SchemaField>();
		SchemaField sf1 = new SchemaField("id", "java.lang.Integer");
		SchemaField sf2 = new SchemaField("fname", "java.lang.String");
		SchemaField sf3 = new SchemaField("lname", "java.util.Date");
		SchemaField sf4 = new SchemaField("salary", "java.math.BigDecimal");
		sf4.setFieldScale(2);
		sf4.setFieldPrecision(6);
		sf3.setFieldFormat("yyyy-MM-dd");
		fieldList.add(sf1);
		fieldList.add(sf2);
		fieldList.add(sf3);
		fieldList.add(sf4);

		String[] partitionField = new String[1];
		partitionField[0] = "salary";

		entity.setFieldsList(fieldList);
		entity.setPartitionKeys(partitionField);
		entity.setExternalTablePathUri(externalTablePathUri);

		Properties runtimeProp = new Properties();
		runtimeProp.setProperty("prop", "propValue");

		entity.setRuntimeProperties(runtimeProp);
		List<OutSocket> outSockets = new ArrayList<OutSocket>();
		outSockets.add(new OutSocket("outSocket"));
		entity.setOutSocketList(outSockets);

		FlowDef flowDef = FlowDef.flowDef();
		ComponentParameters cpInput = new ComponentParameters();
		cpInput.setFlowDef(flowDef);

		InputFileHiveTextAssembly inputFileHiveTextAssembly = new InputFileHiveTextAssembly(entity, cpInput);

		AssemblyBuildHelper.generateOutputPipes(
				inputFileHiveTextAssembly.getOutLink("out", "outSocket", entity.getComponentId()), outPath, flowDef);

		Flow<?> flow = new Hadoop2MR1FlowConnector().connect(flowDef);
		flow.complete();
		CascadingTestCase.validateFieldLength(flow.openSink(), 4);

	}
}
