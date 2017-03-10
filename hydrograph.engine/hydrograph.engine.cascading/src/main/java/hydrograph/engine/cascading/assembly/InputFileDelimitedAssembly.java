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
import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.assembly.utils.InputOutputFieldsAndTypesCreator;
import hydrograph.engine.cascading.scheme.HydrographDelimitedParser;
import hydrograph.engine.core.component.entity.InputFileDelimitedEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class InputFileDelimitedAssembly extends BaseComponent<InputFileDelimitedEntity> {

	private static final long serialVersionUID = -2946197683137950707L;

	private Pipe pipe;
	@SuppressWarnings("rawtypes")
	private Tap tap;
	@SuppressWarnings("rawtypes")
	private Scheme scheme;
	private FlowDef flowDef;
	private InputFileDelimitedEntity inputFileDelimitedEntity;
	private static Logger LOG = LoggerFactory.getLogger(InputFileDelimitedAssembly.class);

	private InputOutputFieldsAndTypesCreator<InputFileDelimitedEntity> fieldsCreator;

	public InputFileDelimitedAssembly(InputFileDelimitedEntity baseComponentEntity, ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	@Override
	protected void createAssembly() {
		try {
		fieldsCreator = new InputOutputFieldsAndTypesCreator<InputFileDelimitedEntity>(inputFileDelimitedEntity);
		LOG.debug("InputFile Delimited Component: [ Fields List : " + Arrays.toString(fieldsCreator.getFieldNames()) + ", Field Types : "
				+ Arrays.toString(fieldsCreator.getFieldDataTypes()) + ", Delimiter : '" + inputFileDelimitedEntity.getDelimiter()
				+ "' , Path : " + inputFileDelimitedEntity.getPath() + ", Batch : "
				+ inputFileDelimitedEntity.getBatch() + "]");
			generateTapsAndPipes();
			flowDef = flowDef.addSource(pipe, tap);

			if (LOG.isTraceEnabled()) {
				LOG.trace(inputFileDelimitedEntity.toString());
			}

			for (OutSocket outSocket : inputFileDelimitedEntity.getOutSocketList()) {
				LOG.trace("Creating input file delimited assembly for '" + inputFileDelimitedEntity.getComponentId()
						+ "' for socket: '" + outSocket.getSocketId() + "' of type: '" + outSocket.getSocketType()
						+ "'");
				setOutLink(outSocket.getSocketType(), outSocket.getSocketId(),
						inputFileDelimitedEntity.getComponentId(), pipe, scheme.getSourceFields());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	public void generateTapsAndPipes() throws IOException {
		try {
			prepareScheme();
		} catch (Exception e) {
			LOG.error("Error in preparing scheme for component '" + inputFileDelimitedEntity.getComponentId() + "': "
					+ e.getMessage());
			throw new RuntimeException(e);
		}
		flowDef = componentParameters.getFlowDef();

		// initializing each pipe and tap
		tap = new Hfs(scheme, inputFileDelimitedEntity.getPath());
		pipe = new Pipe(inputFileDelimitedEntity.getComponentId()+inputFileDelimitedEntity.getOutSocketList().get(0).getSocketId());

		setHadoopProperties(pipe.getStepConfigDef());
		setHadoopProperties(tap.getStepConfigDef());
	}

	protected void prepareScheme() {

		Fields inputFields = fieldsCreator.makeFieldsWithTypes();
		HydrographDelimitedParser delimitedParser = new HydrographDelimitedParser(inputFileDelimitedEntity.getDelimiter(),
				inputFileDelimitedEntity.getQuote(), null, inputFileDelimitedEntity.isStrict(),
				inputFileDelimitedEntity.isSafe());
		scheme = new TextDelimited(inputFields, null, inputFileDelimitedEntity.isHasHeader(), false,
				inputFileDelimitedEntity.getCharset(), delimitedParser);
	}

	@Override
	public void initializeEntity(InputFileDelimitedEntity assemblyEntityBase) {
		this.inputFileDelimitedEntity=assemblyEntityBase;
	}

}