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
package hydrograph.engine.core.flowmanipulation;

import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.core.core.HydrographJob;
import hydrograph.engine.core.schemapropagation.SchemaFieldHandler;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeProperties;

import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * The Class FlowManipulationContext.
 *
 * @author Bitwise
 *
 */
public class FlowManipulationContext {

	private SchemaFieldHandler schemaFieldMap;
	private List<TypeBaseComponent> jaxbMainGraph;
	private String jobId;
	private TypeProperties jaxbJobLevelRuntimeProperties;
	private String graphName;
	private List<String> tmpPath;
	String[] args;

	public FlowManipulationContext(HydrographJob hydrographJob, String[] args,
			SchemaFieldHandler schemaFieldHandler, String jobId) {
		this.jaxbMainGraph = hydrographJob.getJAXBObject().getInputsOrOutputsOrStraightPulls();
		this.jaxbJobLevelRuntimeProperties = hydrographJob.getJAXBObject().getRuntimeProperties();
		this.graphName = hydrographJob.getJAXBObject().getName();
		this.jobId = jobId;
		this.args = args;
		this.schemaFieldMap = schemaFieldHandler;
	}

	public List<String> getTmpPath() {
		return tmpPath;
	}

	public void setTmpPath(List<String> tmpPath) {
		this.tmpPath = tmpPath;
	}

	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	public TypeProperties getJaxbJobLevelRuntimeProperties() {
		return jaxbJobLevelRuntimeProperties;
	}

	public void setJaxbJobLevelRuntimeProperties(TypeProperties jaxbRuntimeProperties) {
		this.jaxbJobLevelRuntimeProperties = jaxbRuntimeProperties;
	}

	public String getJobId() {
		return jobId;
	}

	public Map<String, Set<SchemaField>> getSchemaFieldMap() {
		return schemaFieldMap.getSchemaFieldMap();
	}

	public List<TypeBaseComponent> getJaxbMainGraph() {
		return jaxbMainGraph;
	}

	public SchemaFieldHandler getSchemaFieldHandler() {
		return schemaFieldMap;
	}

	public void setSchemaFieldMap(SchemaFieldHandler schemaFieldHandler) {
		this.schemaFieldMap = schemaFieldHandler;
	}

	public void setJaxbMainGraph(List<TypeBaseComponent> jaxbMainGraph) {
		this.jaxbMainGraph = jaxbMainGraph;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}
}
