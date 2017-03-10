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
import cascading.scheme.hadoop.SequenceFile;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.assembly.utils.InputOutputFieldsAndTypesCreator;
import hydrograph.engine.core.component.entity.InputFileSequenceFormatEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class InputFileSequenceFormatAssembly extends BaseComponent<InputFileSequenceFormatEntity> {

	private static final long serialVersionUID = 1875266476440018910L;

	private Pipe pipe;
	@SuppressWarnings("rawtypes")
	private Tap tap;
	private SequenceFile scheme;
	private FlowDef flowDef;
	private static Logger LOG = LoggerFactory.getLogger(InputFileSequenceFormatAssembly.class);
	private InputOutputFieldsAndTypesCreator<InputFileSequenceFormatEntity> fieldsCreator;
	private InputFileSequenceFormatEntity inputFileSequenceFormatEntity;

	public InputFileSequenceFormatAssembly(InputFileSequenceFormatEntity assemblyEntityBase,
			ComponentParameters componentParameters) {
		super(assemblyEntityBase, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try {
			fieldsCreator = new InputOutputFieldsAndTypesCreator<InputFileSequenceFormatEntity>(inputFileSequenceFormatEntity);
			generateTapsAndPipes();
			flowDef = flowDef.addSource(pipe, tap);
			for (OutSocket outSocket : inputFileSequenceFormatEntity.getOutSocketList()) {

				LOG.trace("Creating input file sequence format assembly for '"
						+ inputFileSequenceFormatEntity.getComponentId() + "' for socket: '" + outSocket.getSocketId()
						+ "' of type: '" + outSocket.getSocketType() + "'");
				setOutLink(outSocket.getSocketType(), outSocket.getSocketId(),
						inputFileSequenceFormatEntity.getComponentId(), pipe, scheme.getSourceFields());
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public void generateTapsAndPipes() throws IOException {
		try {
			prepareScheme();
		} catch (Exception e) {
			LOG.error("Error in preparing scheme for component '" + inputFileSequenceFormatEntity.getComponentId()
					+ "': " + e.getMessage());
			throw new RuntimeException(e);
		}
		flowDef = componentParameters.getFlowDef();

		// initializing each pipe and tap
		tap = new Hfs(scheme, inputFileSequenceFormatEntity.getPath());
		pipe = new Pipe(inputFileSequenceFormatEntity.getComponentId()+
				inputFileSequenceFormatEntity.getOutSocketList().get(0).getSocketId());

		setHadoopProperties(pipe.getStepConfigDef());
		setHadoopProperties(tap.getStepConfigDef());
	}

	protected void prepareScheme() {
		Fields inputFields = fieldsCreator.makeFieldsWithTypes();
		scheme = new SequenceFile(inputFields);

	}

	@Override
	public void initializeEntity(InputFileSequenceFormatEntity assemblyEntityBase) {
		this.inputFileSequenceFormatEntity = assemblyEntityBase;
	}

}
