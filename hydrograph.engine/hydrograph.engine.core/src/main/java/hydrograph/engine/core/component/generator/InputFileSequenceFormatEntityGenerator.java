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
package hydrograph.engine.core.component.generator;

import hydrograph.engine.core.component.entity.InputFileSequenceFormatEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.inputtypes.SequenceInputFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class InputFileSequenceFormatEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class InputFileSequenceFormatEntityGenerator extends InputComponentGeneratorBase {

	private InputFileSequenceFormatEntity inputFileSequenceFormatEntity;
	private SequenceInputFile jaxbFileSequenceFormat;
	private static Logger LOG = LoggerFactory.getLogger(InputFileSequenceFormatEntityGenerator.class);

	public InputFileSequenceFormatEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}
	
	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbFileSequenceFormat = (SequenceInputFile) baseComponent;
	}

	@Override
	public void createEntity() {
		inputFileSequenceFormatEntity = new InputFileSequenceFormatEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing input file delimited entity for component: " + jaxbFileSequenceFormat.getId());
		inputFileSequenceFormatEntity.setComponentId(jaxbFileSequenceFormat.getId());
		inputFileSequenceFormatEntity.setBatch(jaxbFileSequenceFormat.getBatch());
		inputFileSequenceFormatEntity.setComponentName(jaxbFileSequenceFormat.getName());
		inputFileSequenceFormatEntity.setPath(jaxbFileSequenceFormat.getPath().getUri());
		inputFileSequenceFormatEntity.setFieldsList(InputEntityUtils.extractInputFields(
				jaxbFileSequenceFormat.getOutSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
		inputFileSequenceFormatEntity.setRuntimeProperties(
				InputEntityUtils.extractRuntimeProperties(jaxbFileSequenceFormat.getRuntimeProperties()));

		inputFileSequenceFormatEntity.setOutSocketList(InputEntityUtils.extractOutSocket(jaxbFileSequenceFormat.getOutSocket()));

	}

	

	@Override
	public InputFileSequenceFormatEntity getEntity() {
		return inputFileSequenceFormatEntity;
	}
}
