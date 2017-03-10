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
package hydrograph.engine.utilities;

import cascading.flow.FlowConnector;
import cascading.flow.FlowDef;
import cascading.flow.hadoop2.Hadoop2MR1FlowConnector;
import cascading.flow.tez.Hadoop2TezFlowConnector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

public class PlatformHelper {

	private static ArrayList<String> listOflibjars = new ArrayList<String>();

	private PlatformHelper() {
	}

	public static FlowDef addJarsToClassPathInCaseOfTezExecution(FlowDef flowDef) {
		for (String eachJar : listOflibjars) {
			flowDef.addToClassPath(eachJar);
		}
		return flowDef;
	}

	public static FlowConnector getHydrographEngineFlowConnector(Properties hadoopProps) {
		FlowConnector flowConnector;
		String hydrographExecutionEngine = hadoopProps
				.getProperty("hydrograph.execution.engine");
		if (hydrographExecutionEngine != null) {
			if (hydrographExecutionEngine.equalsIgnoreCase(Constants.HYDROGRAPH_EXECUTION_ENGINE_TEZ)) {
				flowConnector = new Hadoop2TezFlowConnector(hadoopProps);
				String libjars = (String) hadoopProps.get("tmpjars");
				if (libjars != null) {
					Collections.addAll(listOflibjars,
							libjars.replace("file:", "").split(","));
				}
			} else if (hydrographExecutionEngine.equalsIgnoreCase(Constants.HYDROGRAPH_EXECUTION_ENGINE_MR)) {
				flowConnector = new Hadoop2MR1FlowConnector(hadoopProps);
			} else {
				flowConnector = new Hadoop2MR1FlowConnector(hadoopProps);
			}
		} else {
			flowConnector = new Hadoop2MR1FlowConnector(hadoopProps);
		}
		return flowConnector;
	}
}