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

import hydrograph.engine.core.component.entity.InputFileAvroEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.inputtypes.AvroFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class InputFileAvroEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class InputFileAvroEntityGenerator extends
		InputComponentGeneratorBase {

	private AvroFile jaxbAvroFile;
	private InputFileAvroEntity inputFileAvroEntity;
	private static Logger LOG = LoggerFactory
			.getLogger(InputFileAvroEntityGenerator.class);

	public InputFileAvroEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbAvroFile = (AvroFile) baseComponent;
	}

	@Override
	public void createEntity() {
		inputFileAvroEntity = new InputFileAvroEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing input file Avro entity for component: "
				+ jaxbAvroFile.getId());
		inputFileAvroEntity.setComponentId(jaxbAvroFile.getId());
		inputFileAvroEntity.setPath(jaxbAvroFile.getPath().getUri());
		inputFileAvroEntity.setSafe(jaxbAvroFile.getSafe() != null ? jaxbAvroFile.getSafe().isValue() : false);
		inputFileAvroEntity
				.setStrict(jaxbAvroFile.getStrict() != null ? jaxbAvroFile.getStrict().isValue() : true);
		inputFileAvroEntity
				.setFieldsList(InputEntityUtils
						.extractInputFields(jaxbAvroFile.getOutSocket()
								.get(0).getSchema()
								.getFieldOrRecordOrIncludeExternalSchema()));
		inputFileAvroEntity.setOutSocketList(InputEntityUtils
				.extractOutSocket(jaxbAvroFile.getOutSocket()));
		inputFileAvroEntity.setRuntimeProperties(InputEntityUtils
				.extractRuntimeProperties(jaxbAvroFile
						.getRuntimeProperties()));
	}



	@Override
	public InputFileAvroEntity getEntity() {
		return inputFileAvroEntity;
	}

	
}