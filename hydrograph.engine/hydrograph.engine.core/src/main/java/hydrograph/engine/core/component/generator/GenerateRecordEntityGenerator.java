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

import hydrograph.engine.core.component.entity.GenerateRecordEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.inputtypes.GenerateRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class GenerateRecordEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class GenerateRecordEntityGenerator extends InputComponentGeneratorBase {
	private hydrograph.engine.jaxb.inputtypes.GenerateRecord jaxbGenerateRecord;
	private GenerateRecordEntity generateRecordEntity;
	private static Logger LOG = LoggerFactory.getLogger(GenerateRecordEntityGenerator.class);

	public GenerateRecordEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);

	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbGenerateRecord = (GenerateRecord) baseComponent;
	}

	@Override
	public void createEntity() {
		generateRecordEntity = new GenerateRecordEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing generate record entity for component: " + jaxbGenerateRecord.getId());
		generateRecordEntity.setComponentId(jaxbGenerateRecord.getId());
		generateRecordEntity.setBatch(jaxbGenerateRecord.getBatch());
		generateRecordEntity.setComponentName(jaxbGenerateRecord.getName());
		generateRecordEntity.setRecordCount(jaxbGenerateRecord.getRecordCount().getValue());

		generateRecordEntity.setFieldsList(InputEntityUtils.extractInputFields(
				jaxbGenerateRecord.getOutSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));

		generateRecordEntity.setOutSocketList(InputEntityUtils.extractOutSocket(jaxbGenerateRecord.getOutSocket()));
		generateRecordEntity.setRuntimeProperties(
				InputEntityUtils.extractRuntimeProperties(jaxbGenerateRecord.getRuntimeProperties()));
	}



	@Override
	public GenerateRecordEntity getEntity() {
		return generateRecordEntity;
	}

	
}