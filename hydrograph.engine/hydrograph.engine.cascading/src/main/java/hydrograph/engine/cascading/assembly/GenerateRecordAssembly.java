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
import cascading.tap.Tap;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.assembly.utils.InputOutputFieldsAndTypesCreator;
import hydrograph.engine.cascading.tap.MemorySourceTap;
import hydrograph.engine.cascading.tuplegenerator.GenerateDataEntity;
import hydrograph.engine.cascading.tuplegenerator.RandomTupleGenerator;
import hydrograph.engine.core.component.entity.GenerateRecordEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GenerateRecordAssembly extends BaseComponent<GenerateRecordEntity> {

	private static final long serialVersionUID = -4353128451811282091L;

	GenerateRecordEntity generateRecordEntity;
	Pipe pipes;
	@SuppressWarnings("rawtypes")
	Tap tap;
	FlowDef flowDef;
	private RandomTupleGenerator tupleGenerator;
	private static Logger LOG = LoggerFactory.getLogger(GenerateRecordAssembly.class);
	private InputOutputFieldsAndTypesCreator<GenerateRecordEntity> fieldsCreator;

	public GenerateRecordAssembly(GenerateRecordEntity baseComponentEntity, ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	private GenerateDataEntity initializeGenerateDataEntity() {

		GenerateDataEntity generateDataEntity = new GenerateDataEntity();
		Fields inputField = fieldsCreator.makeFieldsWithTypes();
		generateDataEntity.setFieldScale(fieldsCreator.getFieldScale());
		generateDataEntity.setFieldFormat(fieldsCreator.getFieldFormat());
		generateDataEntity.setFieldLength(fieldsCreator.getFieldLength());
		generateDataEntity.setFieldDefaultValue(fieldsCreator.getFieldDefaultValue());
		generateDataEntity.setFieldFromRangeValue(fieldsCreator.getFieldFromRangeValue());
		generateDataEntity.setFieldToRangeValue(fieldsCreator.getFieldToRangeValue());
		generateDataEntity.setInputFields(inputField);
		generateDataEntity.setDataTypes(inputField.getTypes());
		generateDataEntity.setRecordCount(generateRecordEntity.getRecordCount());
		return generateDataEntity;
	}

	@Override
	protected void createAssembly() {
		try {
			fieldsCreator = new InputOutputFieldsAndTypesCreator<GenerateRecordEntity>(generateRecordEntity);
			generateTapsAndPipes();
			flowDef = flowDef.addSource(pipes, tap);

			if (LOG.isTraceEnabled()) {
				LOG.trace(generateRecordEntity.toString());
			}
			for (OutSocket outSocket : generateRecordEntity.getOutSocketList()) {

				LOG.trace("Creating generate record assembly for '" + generateRecordEntity.getComponentId()
						+ "' for socket: '" + outSocket.getSocketId() + "' of type: '" + outSocket.getSocketType()
						+ "'");
				setOutLink(outSocket.getSocketType(), outSocket.getSocketId(), generateRecordEntity.getComponentId(),
						pipes, tap.getSourceFields());

			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public void generateTapsAndPipes() throws IOException {
		flowDef = componentParameters.getFlowDef();

		// create pipes and taps
		GenerateDataEntity generateDataEntity = initializeGenerateDataEntity();
		tupleGenerator = new RandomTupleGenerator(generateDataEntity);

		// initializing each pipe and tap
		tap = new MemorySourceTap(tupleGenerator, generateDataEntity.getInputFields(),
				generateDataEntity.getRecordCount());
		pipes = new Pipe(generateRecordEntity.getComponentId()+
				generateRecordEntity.getOutSocketList().get(0).getSocketId());
		setHadoopProperties(pipes.getStepConfigDef());
		setHadoopProperties(tap.getStepConfigDef());
	}

	@Override
	public void initializeEntity(GenerateRecordEntity assemblyEntityBase) {
		this.generateRecordEntity = assemblyEntityBase;
	}
}