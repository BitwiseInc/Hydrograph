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
package hydrograph.engine.cascading.assembly.base;


import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.constants.ComponentLinkType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.pipe.Pipe;
import cascading.pipe.SubAssembly;
import cascading.property.ConfigDef;
import cascading.tuple.Fields;

/**
 * @author gurdits
 *
 */
public abstract class BaseComponent<T extends AssemblyEntityBase> extends
		SubAssembly {

	private static final long serialVersionUID = -804998221607089402L;
	private HashMap<String, HashMap<String, Pipe>> outLinksMap;
	private HashMap<String, HashMap<String, Fields>> outLinksFields;
	private AssemblyEntityBase baseComponentEntity;
	protected ComponentParameters componentParameters;
	private Map<String, Fields> outFieldsMap;
	private static Logger LOG = LoggerFactory.getLogger(BaseComponent.class);

	public BaseComponent(T baseComponentEntity,
			ComponentParameters componentParameters) {
		this.outLinksMap = new HashMap<String, HashMap<String, Pipe>>();
		this.outLinksFields = new HashMap<String, HashMap<String, Fields>>();
		this.outFieldsMap = new HashMap<String, Fields>();
		this.baseComponentEntity = baseComponentEntity;
		this.componentParameters = componentParameters;

		if (LOG.isTraceEnabled()) {
			logComponentParameterInfo();
		}
		initializeEntity(baseComponentEntity);
		createAssembly();
		setOutLinksToTails();
	}

	private void logComponentParameterInfo() {
		StringBuilder str = new StringBuilder(
				"Component parameter information for component: "
						+ baseComponentEntity.getComponentId());

		str.append("\nIn socket: ");
		if (componentParameters.getinSocketId() != null) {
			str.append(Arrays.toString(componentParameters.getinSocketId()
					.toArray()));
		}
		str.append("\nInput fields: ");
		if (componentParameters.getInputFields() != null) {
			str.append(Arrays.toString(componentParameters.getInputFieldsList()
					.toArray()));
		}
		str.append("\nOutput fields: ");
		if (componentParameters.getOutputFieldsList() != null) {
			str.append(Arrays.toString(componentParameters
					.getOutputFieldsList().toArray()));
		}
		LOG.trace(str.toString());
	}

	/**
	 * Casts the {@link AssemblyEntityBase} object to the object of entity class
	 * specific to the assembly
	 * 
	 * @param assemblyEntityBase
	 *            the {@link AssemblyEntityBase} object to cast to specific
	 *            entity object
	 */
	public abstract void initializeEntity(T assemblyEntityBase);

	protected abstract void createAssembly();

	protected void setOutLink(String socketType, String socketId,
			String componentId, Pipe pipeLink, Fields outputFields) {

		// add pipe link
		HashMap<String, Pipe> linkMap = outLinksMap.get(socketType);

		// If we have not dealt with particular type of link so far then add it
		if (linkMap == null) {
			linkMap = new HashMap<String, Pipe>();
			outLinksMap.put(socketId, linkMap);
		}

		linkMap.put(componentId + "_" + socketId, pipeLink);

		// add link output schema
		HashMap<String, Fields> fieldsMap = outLinksFields.get(socketType);

		// If we have not dealt with particular type of link so far then add it
		if (fieldsMap == null) {
			fieldsMap = new HashMap<String, Fields>();
			outLinksFields.put(socketId, fieldsMap);
		}

		fieldsMap.put(componentId + "_" + socketId, outputFields);
		outFieldsMap.put(componentId + "_" + socketId, outputFields);
	}

	public Pipe getOutLink(String socketType, String socketId,
			String componentId) {
		HashMap<String, Pipe> linkMap = outLinksMap.get(socketId);

		// If we have not dealt with particular type of link so far then add it
		if (linkMap == null) {
			return null;
		}

		return linkMap.get(componentId + "_" + socketId);
	}

	public Fields getOutFields(String socketType, String socketId,
			String componentId) {
		HashMap<String, Fields> fieldsMap = outLinksFields.get(socketId);

		if (fieldsMap == null) {
			return null;
		}

		return fieldsMap.get(componentId + "_" + socketId);
	}

	protected HashMap<String, Pipe> getAllOutLink(ComponentLinkType linkType) {
		return outLinksMap.get(linkType.toString());
	}

	public Map<String, Fields> getAllOutFields() {
		return outFieldsMap;
	}
	
	public Collection<HashMap<String, Pipe>> getAllOutLinkForAssembly() {
		return  outLinksMap.values();
	}

	private void setOutLinksToTails() {
		ArrayList<Pipe> allOutPipes = new ArrayList<Pipe>();

		// traverse entire data structure to extract all damn pipes
		for (HashMap<String, Pipe> linkMap : outLinksMap.values()) {

			for (Pipe outPipes : linkMap.values()) {
				allOutPipes.add(outPipes);
			}
		}

		// set tails only if there is something to set
		if (allOutPipes.size() > 0) {
			setTails(allOutPipes.toArray(new Pipe[allOutPipes.size()]));
		}
	}

	protected void setHadoopProperties(ConfigDef conf) {
		Properties hadoopProps = baseComponentEntity.getRuntimeProperties();
		if (hadoopProps != null) {
			// Iterate over all hadoop props and set it for current component
			for (Map.Entry<Object, Object> entry : hadoopProps.entrySet()) {
				conf.setProperty(ConfigDef.Mode.REPLACE, (String) entry.getKey(),
						(String) entry.getValue());
			}
		}
	}
}
