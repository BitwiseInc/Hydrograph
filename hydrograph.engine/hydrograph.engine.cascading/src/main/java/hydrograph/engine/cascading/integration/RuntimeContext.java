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

import cascading.cascade.Cascade;
import cascading.flow.Flow;
import hydrograph.engine.adapters.base.BaseAdapter;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.component.mapping.ComponentAdapterFactory;
import hydrograph.engine.core.core.HydrographJob;
import hydrograph.engine.core.helper.JAXBTraversal;
import hydrograph.engine.core.schemapropagation.SchemaFieldHandler;
import hydrograph.engine.hadoop.utils.HadoopConfigProvider;
import org.apache.hadoop.mapred.JobConf;

import java.util.*;

@SuppressWarnings("rawtypes")
public class RuntimeContext {

	private Flow[] cascadingFlows;
	private Cascade[] cascades;
	private HydrographJob hydrographJob;
	private JAXBTraversal traversal;
	private HadoopConfigProvider hadoopConfProvider;
	private LinkedHashMap<String, ComponentParameters> tempPathParameters;
	private Properties hadoopProperties;
	private ComponentAdapterFactory componentAdapterFactory;
	private Map<String, FlowContext> flowContext;
	private SchemaFieldHandler schemaFieldHandler;
	private String UDFPath;

	public RuntimeContext(HydrographJob hydrographJob, JAXBTraversal traversal, Properties hadoopProps,
						  ComponentAdapterFactory componentAdapterFactory, SchemaFieldHandler schemaFieldHandler, String UDFPath) {
		this.hydrographJob = hydrographJob;
		this.traversal = traversal;
		this.hadoopProperties = hadoopProps;
		this.componentAdapterFactory = componentAdapterFactory;
		this.hadoopConfProvider = new HadoopConfigProvider(hadoopProps);
		this.schemaFieldHandler = schemaFieldHandler;
		this.UDFPath=UDFPath;
		tempPathParameters = new LinkedHashMap<String, ComponentParameters>();
		this.flowContext = new HashMap<String, FlowContext>();
	}

	public String getUDFPath() {
		return UDFPath;
	}

	public void setSchemaFieldHandler(SchemaFieldHandler schemaFieldHandler){
		this.schemaFieldHandler=schemaFieldHandler;
	}

	public SchemaFieldHandler getSchemaFieldHandler() {
		return schemaFieldHandler;
	}

	public JobConf getJobConf() {
		return hadoopConfProvider.getJobConf();
	}

	public HydrographJob getHydrographJob() {
		return hydrographJob;
	}

	public JAXBTraversal getTraversal() {
		return traversal;
	}

	public Cascade[] getCascadingFlows() {
		// Exposing internal arrays directly allows the user to modify some code
		// that could be critical. It is safer to return a copy of the array
		return cascades.clone();
	}

	public void setCascadingFlows(Flow[] flows) {

		// SonarQube: Constructors and methods receiving arrays should clone
		// objects and
		// store the copy. This prevents that future changes from the user
		// affect the internal functionality
		this.cascadingFlows = flows.clone();
	}

	public void setCascade(Cascade[] cascades) {
		this.cascades = cascades != null ? cascades.clone() : null;
	}

	public Cascade[] getCascade() {
		return cascades != null ? cascades.clone() : null;
	}

	public Map<String, BaseAdapter> getAssemblyGeneratorMap() {
		return componentAdapterFactory.getAssemblyGeneratorMap();
	}

	/*
	 * public void addTempPathParameter(String key, ComponentParameters cp) {
	 * tempPathParameters.put(key, cp); //System.out.println(
	 * "****Added temp path " + cp.getPathUri()); }
	 * 
	 * 
	 * 
	 * public ComponentParameters getTempPathParameter(String key) { return
	 * tempPathParameters.get(key); }
	 */
	public Collection<ComponentParameters> getAllTempPathParameters() {
		return tempPathParameters.values();
	}

	public Properties getHadoopProperties() {
		return hadoopProperties;
	}

	public void addTempPathParameter(String string, ComponentParameters cp) {
		tempPathParameters.put(string, cp);
	}

	public ComponentParameters getTempPathParameter(String string) {
		return tempPathParameters.get(string);
	}

	public Map<String, FlowContext> getFlowContext() {
		return flowContext;
	}

	public void setFlowContext(String batch, FlowContext flowContext) {
		this.flowContext.put(batch, flowContext);
	}

}
