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

import hydrograph.engine.core.component.entity.OutputFileSequenceFormatEntity;
import hydrograph.engine.core.component.entity.utils.OutputEntityUtils;
import hydrograph.engine.core.component.generator.base.OutputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.outputtypes.SequenceOutputFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class OutputFileSequenceFormatEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputFileSequenceFormatEntityGenerator extends OutputComponentGeneratorBase {

	private OutputFileSequenceFormatEntity outputFileSequenceFormatEntity;
	private SequenceOutputFile jaxbSequenceOutputFile;
	private static Logger LOG = LoggerFactory.getLogger(OutputFileSequenceFormatEntityGenerator.class);

	public OutputFileSequenceFormatEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbSequenceOutputFile = (SequenceOutputFile) baseComponent;
	}

	@Override
	public void createEntity() {
		outputFileSequenceFormatEntity = new OutputFileSequenceFormatEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing output file delimited entity for component: " + jaxbSequenceOutputFile.getId());
		outputFileSequenceFormatEntity.setComponentId(jaxbSequenceOutputFile.getId());
		outputFileSequenceFormatEntity.setBatch(jaxbSequenceOutputFile.getBatch());
		outputFileSequenceFormatEntity.setComponentName(jaxbSequenceOutputFile.getName());
		outputFileSequenceFormatEntity.setPath(jaxbSequenceOutputFile.getPath().getUri());
		outputFileSequenceFormatEntity.setFieldsList(OutputEntityUtils.extractOutputFields(
				jaxbSequenceOutputFile.getInSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
		outputFileSequenceFormatEntity.setRuntimeProperties(
				OutputEntityUtils.extractRuntimeProperties(jaxbSequenceOutputFile.getRuntimeProperties()));
		
	}

	@Override
	public OutputFileSequenceFormatEntity getEntity() {
		return outputFileSequenceFormatEntity;
	}

}