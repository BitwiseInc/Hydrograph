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

import hydrograph.engine.core.component.entity.OutputFileAvroEntity;
import hydrograph.engine.core.component.entity.utils.OutputEntityUtils;
import hydrograph.engine.core.component.generator.base.OutputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TrueFalse;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.outputtypes.AvroFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class OutputFileAvroEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputFileAvroEntityGenerator extends OutputComponentGeneratorBase {

	private AvroFile jaxbAvroFile;
	private OutputFileAvroEntity outputFileAvroEntity;
	private static Logger LOG = LoggerFactory.getLogger(OutputFileAvroEntityGenerator.class);

	public OutputFileAvroEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbAvroFile = (AvroFile) baseComponent;
	}

	@Override
	public void createEntity() {
		outputFileAvroEntity = new OutputFileAvroEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing output file Avro entity for component: " + jaxbAvroFile.getId());
		outputFileAvroEntity.setComponentId(jaxbAvroFile.getId());
		outputFileAvroEntity.setBatch(jaxbAvroFile.getBatch());
		outputFileAvroEntity.setComponentName(jaxbAvroFile.getName());
		outputFileAvroEntity.setPath(jaxbAvroFile.getPath().getUri());
		outputFileAvroEntity.setFieldsList(OutputEntityUtils.extractOutputFields(
				jaxbAvroFile.getInSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));

		outputFileAvroEntity.setRuntimeProperties(
				OutputEntityUtils.extractRuntimeProperties(jaxbAvroFile.getRuntimeProperties()));
		if (jaxbAvroFile.getOverWrite() != null
				&& (TrueFalse.FALSE).equals(jaxbAvroFile.getOverWrite().getValue()))
			outputFileAvroEntity.setOverWrite(false);
		else
			outputFileAvroEntity.setOverWrite(true);
	}



	@Override
	public OutputFileAvroEntity getEntity() {
		return outputFileAvroEntity;
	}

	
}
