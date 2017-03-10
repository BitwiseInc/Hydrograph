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

import cascading.flow.FlowDef;
import cascading.pipe.Pipe;
import cascading.scheme.Scheme;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.scheme.avro.AvroDescriptor;
import hydrograph.engine.cascading.scheme.avro.CustomAvroScheme;
import hydrograph.engine.core.component.entity.InputFileAvroEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class InputFileAvroAssembly extends BaseComponent<InputFileAvroEntity> {

	private static final long serialVersionUID = -2946197683137950707L;
	private InputFileAvroEntity inputFileAvroEntity;
	@SuppressWarnings("rawtypes")
	private Scheme scheme;
	private Pipe pipe;
	@SuppressWarnings("rawtypes")
	private Tap tap;
	private FlowDef flowDef;
	private Fields inputFields;
	private String[] fieldDataTypes;
	private int[] fieldScale;
	private int[] fieldPrecision;
	private String[] fieldFormat;
	private String[] fieldScaleType;
	private static final Logger LOG = LoggerFactory
			.getLogger(InputFileAvroAssembly.class);

	public InputFileAvroAssembly(InputFileAvroEntity baseComponentEntity,
			ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try {
			getSchemaFieldType();
			generateTapsAndPipes();
			flowDef = flowDef.addSource(pipe, tap);

			if (LOG.isTraceEnabled()) {
				LOG.trace(inputFileAvroEntity.toString());
			}
			for (OutSocket outSocket : inputFileAvroEntity.getOutSocketList()) {

				LOG.trace("Creating input file Avro assembly for '"
						+ inputFileAvroEntity.getComponentId()
						+ "' for socket: '" + outSocket.getSocketId()
						+ "' of type: '" + outSocket.getSocketType() + "'");
				setOutLink(outSocket.getSocketType(), outSocket.getSocketId(),
						inputFileAvroEntity.getComponentId(), pipe,
						scheme.getSourceFields());
			}
		} catch (Exception e) {
			LOG.error(
					"Error in creating assembly for component '"
							+ inputFileAvroEntity.getComponentId() + "', Error: "
							+ e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void generateTapsAndPipes() throws IOException {
		prepareScheme();
		flowDef = componentParameters.getFlowDef();
		tap = new Hfs(scheme, inputFileAvroEntity.getPath());
		pipe = new Pipe(inputFileAvroEntity.getComponentId()+inputFileAvroEntity.getOutSocketList().get(0).getSocketId());

		setHadoopProperties(tap.getStepConfigDef());
		setHadoopProperties(pipe.getStepConfigDef());
	}

	public void getSchemaFieldType() {
		inputFields = new Fields();
		fieldDataTypes = new String[inputFileAvroEntity.getFieldsList()
				.size()];
		fieldScale = new int[inputFileAvroEntity.getFieldsList().size()];
		fieldPrecision = new int[inputFileAvroEntity.getFieldsList()
				.size()];
		fieldFormat = new String[inputFileAvroEntity.getFieldsList()
				.size()];
		fieldScaleType = new String[inputFileAvroEntity.getFieldsList()
				.size()];
		for (int i = 0; i < inputFileAvroEntity.getFieldsList().size(); i++) {
			inputFields = inputFields.append(new Fields(inputFileAvroEntity
					.getFieldsList().get(i).getFieldName()));
			fieldDataTypes[i] = inputFileAvroEntity.getFieldsList()
					.get(i).getFieldDataType();
			fieldFormat[i] = inputFileAvroEntity.getFieldsList().get(i)
					.getFieldFormat() != null ? inputFileAvroEntity
					.getFieldsList().get(i).getFieldFormat() : "";
			fieldScale[i] = inputFileAvroEntity.getFieldsList().get(i)
					.getFieldScale();
			fieldPrecision[i] = inputFileAvroEntity.getFieldsList()
					.get(i).getFieldPrecision();
			fieldScaleType[i] = inputFileAvroEntity.getFieldsList()
					.get(i).getFieldScaleType();
		}
	}

	protected void prepareScheme() {
		LOG.debug("Applying CustomAvroScheme to read data from avro file");
		AvroDescriptor avroDescriptor = new AvroDescriptor(inputFields,
				dataTypeMapping(fieldDataTypes), fieldPrecision, fieldScale);
		scheme = new CustomAvroScheme(avroDescriptor);
	}

	private Class<?>[] dataTypeMapping(String[] fieldDataTypes) {
		Class<?>[] types = new Class<?>[fieldDataTypes.length];
		for (int i = 0; i < fieldDataTypes.length; i++) {
			try {
				types[i] = Class.forName(fieldDataTypes[i]);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(
						"'"
								+ fieldDataTypes[i]
								+ "' class not found while applying datatypes for component '"
								+ inputFileAvroEntity.getComponentId()
								+ "' ", e);
			}
		}
		return types;
	}

	@Override
	public void initializeEntity(InputFileAvroEntity assemblyEntityBase) {
		this.inputFileAvroEntity=assemblyEntityBase;
	}

}