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
package hydrograph.engine.cascading.assembly.infra;

import cascading.flow.FlowDef;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import org.apache.hadoop.mapred.JobConf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

//import hydrograph.engine.assembly.entity.elements.CopyOfInSocket;

public class ComponentParameters implements Cloneable {

	private static final String OUTPUT_FIELDS_KEY = "output_fields";
	private static final String INPUT_FIELDS_KEY = "input_fields";
	private static final String SCHEMA_FIELDS_KEY = "schema_fields";
	private static final String INPUT_PIPES_KEY = "input_pipes";
	private static final String FLOW_DEF_KEY = "flow_def";
	private static final String INSOCKET_ID = "inSocket_id";
	private static final String INSOCKET_TYPE = "inSocket_type";
	private static final String PATH_URI_KEY = "path_uri";
	private static final String TEMP_PATH_KEY = "temp_path_uri";
	private static final String JOB_ID = "job_id";
	private static final String BASE_PATH = "base_path";
	private static final String UDF_PATH = "udf_path";
	/**
	 *
	 */

	private HashMap<String, Object> parameters;
	private HashMap<String, Fields> copyOfInSocket;

	public ComponentParameters() {
		this.parameters = new HashMap<String, Object>();
		this.copyOfInSocket = new HashMap<String, Fields>();
	}

	public ComponentParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

	@SuppressWarnings("unchecked")
	public ComponentParameters clone() {
		return new ComponentParameters((HashMap<String, Object>) parameters.clone());
	}

	public void setOutputFieldsList(ArrayList<Fields> outputList) {
		setParameterList(outputList, OUTPUT_FIELDS_KEY);
	}

	public void addOutputFields(Fields output) {
		addParameterToList(output, OUTPUT_FIELDS_KEY);

	}

	public Fields getOutputFields() {
		return getParameterfromList(OUTPUT_FIELDS_KEY);
	}

	public ArrayList<Fields> getOutputFieldsList() {
		return getEntireList(OUTPUT_FIELDS_KEY);
	}

	public void addInputFields(Fields input) {
		addParameterToList(input, INPUT_FIELDS_KEY);

	}

	public void addSchemaFields(Set<SchemaField> input) {
		addParameterToList(input, SCHEMA_FIELDS_KEY);
	}

	public void addTempPath(JobConf input) {
		addParameterToList(input, TEMP_PATH_KEY);
	}

	public void addinSocketId(String id) {
		addParameterToList(id, INSOCKET_ID);
	}

	public ArrayList<String> getinSocketId() {
		return getEntireList(INSOCKET_ID);
	}

	public void addinSocketType(String type) {
		addParameterToList(type, INSOCKET_TYPE);
	}

	public ArrayList<String> getinSocketType() {
		return getEntireList(INSOCKET_TYPE);
	}

	public void setInputFieldsList(ArrayList<Fields> fieldsList) {
		setParameterList(fieldsList, INPUT_FIELDS_KEY);
	}

	public Fields getInputFields() {
		return getParameterfromList(INPUT_FIELDS_KEY);
	}

	public Set<SchemaField> getSchemaFields() {
		return getParameterfromList(SCHEMA_FIELDS_KEY);
	}

	public ArrayList<Set<SchemaField>> getSchemaFieldList() {
		return getEntireList(SCHEMA_FIELDS_KEY);
	}

	public JobConf getJobConf() {
		return getParameterfromList(TEMP_PATH_KEY);
	}

	public ArrayList<Fields> getInputFieldsList() {
		return getEntireList(INPUT_FIELDS_KEY);
	}

	public void setInputPipes(ArrayList<Pipe> inputPipes) {
		setParameterList(inputPipes, INPUT_PIPES_KEY);
	}

	public ArrayList<Pipe> getInputPipes() {
		return getEntireList(INPUT_PIPES_KEY);
	}

	public void addInputPipe(Pipe inputPipe) {
		addParameterToList(inputPipe, INPUT_PIPES_KEY);
	}

	// public Pipe[] getInputPipe(int index) {
	// return getParameterfromList(INPUT_PIPES_KEY, index);
	// }

	public Pipe getInputPipe() {
		return getParameterfromList(INPUT_PIPES_KEY);
	}

	private <T> void setParameterList(ArrayList<T> parameterList, String key) {
		parameters.put(key, parameterList);
	}

	private <T> void addParameterToList(T parameter, String key) {
		@SuppressWarnings("unchecked")
		ArrayList<T> parameterList = (ArrayList<T>) parameters.get(key);
		if (parameterList == null) {
			parameterList = new ArrayList<T>();
			parameterList.add(parameter);
			parameters.put(key, parameterList);
			return;
		}
		parameterList.add(parameter);
	}

	private <T> T getParameterfromList(String key, int index) {
		@SuppressWarnings("unchecked")
		ArrayList<T> parameterList = (ArrayList<T>) parameters.get(key);
		if (parameterList == null) {
			return null;
		}

		return parameterList.get(index);

	}

	private <T> T getParameterfromList(String key) {
		return getParameterfromList(key, 0);

	}

	private <T> ArrayList<T> getEntireList(String key) {
		@SuppressWarnings("unchecked")
		ArrayList<T> parameterList = (ArrayList<T>) parameters.get(key);

		return parameterList;
	}

	public void setFlowDef(FlowDef flowDef) {
		parameters.put(FLOW_DEF_KEY, flowDef);
	}

	public FlowDef getFlowDef() {
		return (FlowDef) parameters.get(FLOW_DEF_KEY);
	}

	public void setPathUri(String uri) {
		addParameterToList(uri, PATH_URI_KEY);
	}

	public String getPathUri() {
		return getParameterfromList(PATH_URI_KEY);
	}

	public void addCopyOfInSocket(String fromSocketId, Fields inputFields) {
		this.copyOfInSocket.put(fromSocketId, inputFields);
	}

	public Fields getCopyOfInSocket(String fromSocketId) {
		return this.copyOfInSocket.get(fromSocketId);
	}

	public void setJobId(String jobId) {
		addParameterToList(jobId, JOB_ID);
	}

	public String getJobId() {
		return getParameterfromList(JOB_ID);
	}

	public void setBasePath(String basePath) {
		addParameterToList(basePath, BASE_PATH);
	}

	public String getBasePath() {
		return getParameterfromList(BASE_PATH);
	}

	public void setUDFPath(String UDFPath) {
		addParameterToList(UDFPath, UDF_PATH);
	}

	public String getUDFPath() {
		return getParameterfromList(UDF_PATH);
	}

}