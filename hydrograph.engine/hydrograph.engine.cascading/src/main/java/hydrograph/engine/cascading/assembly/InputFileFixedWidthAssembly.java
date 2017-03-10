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
import hydrograph.engine.cascading.assembly.utils.InputOutputFieldsAndTypesCreator;
import hydrograph.engine.cascading.scheme.TextFixedWidth;
import hydrograph.engine.core.component.entity.InputFileFixedWidthEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class InputFileFixedWidthAssembly extends BaseComponent<InputFileFixedWidthEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8145806669418685707L;

	InputFileFixedWidthEntity inputFileFixedWidthEntity;
	Pipe pipe;
	@SuppressWarnings("rawtypes")
	Tap tap;
	@SuppressWarnings("rawtypes")
	Scheme scheme;
	FlowDef flowDef;
	private static Logger LOG = LoggerFactory
			.getLogger(InputFileFixedWidthAssembly.class);

	private InputOutputFieldsAndTypesCreator<InputFileFixedWidthEntity> fieldsCreator;

	public InputFileFixedWidthAssembly(InputFileFixedWidthEntity baseComponentEntity,
			ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try {
		fieldsCreator = new InputOutputFieldsAndTypesCreator<InputFileFixedWidthEntity>(
				inputFileFixedWidthEntity);
		LOG.debug("InputFile Fixed Width Component: [ Fields List : " + Arrays.toString(fieldsCreator.getFieldNames()) + ", Field Types : "
				+ Arrays.toString(fieldsCreator.getFieldDataTypes()) + ", Field Length : " + Arrays.toString(fieldsCreator.getFieldLength())
				+ " , Path : " + inputFileFixedWidthEntity.getPath() + ", Batch : "
				+ inputFileFixedWidthEntity.getBatch() + "]");
			generateTapsAndPipes();
			flowDef = flowDef.addSource(pipe, tap);

			if (LOG.isTraceEnabled()) {
				LOG.trace(inputFileFixedWidthEntity.toString());
			}
			for (OutSocket outSocket : inputFileFixedWidthEntity
					.getOutSocketList()) {
				LOG.trace("Creating input file fixed width assembly for '"
						+ inputFileFixedWidthEntity.getComponentId()
						+ "' for socket: '" + outSocket.getSocketId()
						+ "' of type: '" + outSocket.getSocketType() + "'");
				setOutLink(outSocket.getSocketType(), outSocket.getSocketId(),
						inputFileFixedWidthEntity.getComponentId(), pipe,
						scheme.getSourceFields());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void generateTapsAndPipes() throws IOException {
		try{
			prepareScheme();
		}
		catch(Exception e) {
			LOG.error("Error in preparing scheme for component '"
					+ inputFileFixedWidthEntity.getComponentId() + "': " + e.getMessage());
			throw new RuntimeException(e);
		}
		flowDef = componentParameters.getFlowDef();

		// initializing each pipe and tap
		tap = new Hfs(scheme, inputFileFixedWidthEntity.getPath());
		pipe = new Pipe(inputFileFixedWidthEntity.getComponentId()+inputFileFixedWidthEntity.getOutSocketList().get(0).getSocketId());
		setHadoopProperties(tap.getStepConfigDef());
		setHadoopProperties(pipe.getStepConfigDef());
	}

	protected void prepareScheme() {

		int[] fieldLength = new int[inputFileFixedWidthEntity.getFieldsList()
				.size()];
		for (int i = 0; i < inputFileFixedWidthEntity.getFieldsList().size(); i++) {
			fieldLength[i] = inputFileFixedWidthEntity.getFieldsList().get(i)
					.getFieldLength();
		}

		Fields inputFields = fieldsCreator.makeFieldsWithTypes();
		scheme = new TextFixedWidth(inputFields, fieldLength, null,
				inputFileFixedWidthEntity.isStrict(),
				inputFileFixedWidthEntity.isSafe(),
				inputFileFixedWidthEntity.getCharset());

	}

	@Override
	public void initializeEntity(InputFileFixedWidthEntity assemblyEntityBase) {
		this.inputFileFixedWidthEntity=assemblyEntityBase;
	}

}