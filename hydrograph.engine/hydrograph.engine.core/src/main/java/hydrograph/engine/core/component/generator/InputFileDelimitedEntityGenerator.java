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

import hydrograph.engine.core.component.entity.InputFileDelimitedEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.core.utilities.GeneralUtilities;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.inputtypes.TextFileDelimited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class InputFileDelimitedEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class InputFileDelimitedEntityGenerator extends InputComponentGeneratorBase {

	private InputFileDelimitedEntity inputFileDelimitedEntity;
	private TextFileDelimited jaxbFileDelimited;
	//private GeneralUtilities generalUtilities ;
	private static Logger LOG = LoggerFactory.getLogger(InputFileDelimitedEntityGenerator.class);

	public InputFileDelimitedEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbFileDelimited = (TextFileDelimited) baseComponent;
	}

	@Override
	public void createEntity() {
		inputFileDelimitedEntity = new InputFileDelimitedEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing input file delimited entity for component: " + jaxbFileDelimited.getId());
		inputFileDelimitedEntity.setComponentId(jaxbFileDelimited.getId());
		inputFileDelimitedEntity.setBatch(jaxbFileDelimited.getBatch());
		inputFileDelimitedEntity.setComponentName(jaxbFileDelimited.getName());
		inputFileDelimitedEntity.setPath(jaxbFileDelimited.getPath().getUri());
		inputFileDelimitedEntity.setDelimiter(GeneralUtilities.parseHex(jaxbFileDelimited.getDelimiter().getValue()));
		inputFileDelimitedEntity.setSafe(jaxbFileDelimited.getSafe() != null ? jaxbFileDelimited.getSafe().isValue() : false);
		inputFileDelimitedEntity
				.setStrict(jaxbFileDelimited.getStrict() != null ? jaxbFileDelimited.getStrict().isValue() : true);
		inputFileDelimitedEntity.setCharset(
				jaxbFileDelimited.getCharset() != null ? jaxbFileDelimited.getCharset().getValue().value() : "UTF-8");
		inputFileDelimitedEntity.setFieldsList(InputEntityUtils.extractInputFields(
				jaxbFileDelimited.getOutSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));

		inputFileDelimitedEntity.setHasHeader(
				jaxbFileDelimited.getHasHeader() != null ? jaxbFileDelimited.getHasHeader().isValue() : false);
		inputFileDelimitedEntity
				.setQuote(jaxbFileDelimited.getQuote() != null ? jaxbFileDelimited.getQuote().getValue() : null);

		inputFileDelimitedEntity.setRuntimeProperties(
				InputEntityUtils.extractRuntimeProperties(jaxbFileDelimited.getRuntimeProperties()));

		inputFileDelimitedEntity.setOutSocketList(InputEntityUtils.extractOutSocket(jaxbFileDelimited.getOutSocket()));

	}



	@Override
	public InputFileDelimitedEntity getEntity() {
		return inputFileDelimitedEntity;
	}

	
}
