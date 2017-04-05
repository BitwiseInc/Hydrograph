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
package hydrograph.engine.plugin.debug;

import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * The Class DebugContext.
 *
 * @author Bitwise
 *
 */
public class DebugContext {

	private String previousComponentId;
	private String fromComponentId;
	private String fromOutSocketId;
	private String fromOutSocketType;
	private String batch;
	private List<TypeBaseComponent> typeBaseComponents;
	private Map<String, Set<SchemaField>> schemaFieldsMap;
	private Map<String, Object> params;
	private String jobId;
	private String basePath;

	public DebugContext() {
		params = new HashMap<String, Object>();
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getPreviousComponentId() {
		return previousComponentId;
	}

	public void setPreviousComponentId(String previousComponentId) {
		this.previousComponentId = previousComponentId;
	}

	public String getFromComponentId() {
		return fromComponentId;
	}

	public void setFromComponentId(String fromComponentId) {
		this.fromComponentId = fromComponentId;
	}

	public String getFromOutSocketId() {
		return fromOutSocketId;
	}

	public void setFromOutSocketId(String fromOutSocketId) {
		this.fromOutSocketId = fromOutSocketId;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public List<TypeBaseComponent> getTypeBaseComponents() {
		return typeBaseComponents;
	}

	public void setTypeBaseComponents(List<TypeBaseComponent> typeBaseComponents) {
		this.typeBaseComponents = typeBaseComponents;
	}

	public String getFromOutSocketType() {
		return fromOutSocketType;
	}

	public void setFromOutSocketType(String fromOutSocketType) {
		this.fromOutSocketType = fromOutSocketType;
	}

	public Map<String, Set<SchemaField>> getSchemaFieldsMap() {
		return schemaFieldsMap;
	}

	public void setSchemaFieldsMap(Map<String, Set<SchemaField>> schemaFieldsMap) {
		this.schemaFieldsMap = schemaFieldsMap;
	}

}
