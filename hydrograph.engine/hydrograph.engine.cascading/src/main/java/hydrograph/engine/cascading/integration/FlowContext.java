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
package hydrograph.engine.cascading.integration;

import cascading.cascade.CascadeDef;
import cascading.flow.FlowDef;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.core.HydrographJob;
import hydrograph.engine.core.helper.JAXBTraversal;
import hydrograph.engine.hadoop.utils.HadoopConfigProvider;
import org.apache.hadoop.mapred.JobConf;

import java.util.HashMap;
import java.util.Properties;

public class FlowContext {

	private HydrographJob hydrographJob;
	private JAXBTraversal traversal;
	private HashMap<String, BaseComponent<AssemblyEntityBase>> assemblies;
	private FlowDef flowDef;
	private CascadeDef cascadeDef;
	private HadoopConfigProvider hadoopConfProvider;

	public FlowContext(HydrographJob hydrographJob, JAXBTraversal traversal,
					   Properties hadoopProps) {
		this.hydrographJob = hydrographJob;
		this.traversal = traversal;
		this.flowDef = FlowDef.flowDef();
		this.assemblies = new HashMap<String, BaseComponent<AssemblyEntityBase>>();
		this.cascadeDef = new CascadeDef();
		this.hadoopConfProvider = new HadoopConfigProvider(hadoopProps);
	}

	public HydrographJob getHydrographJob() {
		return hydrographJob;
	}

	public CascadeDef getCascadeDef() {
		return cascadeDef;
	}

	public JAXBTraversal getTraversal() {
		return traversal;
	}

	public HashMap<String, BaseComponent<AssemblyEntityBase>> getAssemblies() {
		return assemblies;
	}

	public void setAssemblies(HashMap<String, BaseComponent<AssemblyEntityBase>> assemblies) {
		this.assemblies = assemblies;
	}

	public FlowDef getFlowDef() {
		return flowDef;
	}

	public JobConf getJobConf() {
		return hadoopConfProvider.getJobConf();
	}

}
