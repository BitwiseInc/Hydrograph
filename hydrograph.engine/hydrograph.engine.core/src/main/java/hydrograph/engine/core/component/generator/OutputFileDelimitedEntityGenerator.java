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

import hydrograph.engine.core.component.entity.OutputFileDelimitedEntity;
import hydrograph.engine.core.component.entity.utils.OutputEntityUtils;
import hydrograph.engine.core.component.generator.base.OutputComponentGeneratorBase;
import hydrograph.engine.core.utilities.GeneralUtilities;
import hydrograph.engine.jaxb.commontypes.TrueFalse;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.outputtypes.TextFileDelimited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class OutputFileDelimitedEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputFileDelimitedEntityGenerator extends OutputComponentGeneratorBase {

	private OutputFileDelimitedEntity outputFileDelimitedEntity;
	private TextFileDelimited jaxbOutputFileDelimited;
	private static Logger LOG = LoggerFactory.getLogger(OutputFileDelimitedEntityGenerator.class);

	public OutputFileDelimitedEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
		// TODO Auto-generated constructor stub
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbOutputFileDelimited = (TextFileDelimited) baseComponent;
	}

	@Override
	public void createEntity() {
		outputFileDelimitedEntity = new OutputFileDelimitedEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing output file delimited entity for component: " + jaxbOutputFileDelimited.getId());
		outputFileDelimitedEntity.setComponentId(jaxbOutputFileDelimited.getId());
		outputFileDelimitedEntity.setBatch(jaxbOutputFileDelimited.getBatch());
		outputFileDelimitedEntity.setComponentName(jaxbOutputFileDelimited.getName());
		outputFileDelimitedEntity.setPath(jaxbOutputFileDelimited.getPath().getUri());
		outputFileDelimitedEntity.setSafe(
				jaxbOutputFileDelimited.getSafe() != null ? jaxbOutputFileDelimited.getSafe().isValue() : false);
		outputFileDelimitedEntity
				.setDelimiter(GeneralUtilities.parseHex(jaxbOutputFileDelimited.getDelimiter().getValue()));
		outputFileDelimitedEntity.setCharset(jaxbOutputFileDelimited.getCharset() != null
				? jaxbOutputFileDelimited.getCharset().getValue().value() : "UTF-8");

		outputFileDelimitedEntity.setFieldsList(OutputEntityUtils.extractOutputFields(
				jaxbOutputFileDelimited.getInSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));

		outputFileDelimitedEntity.setRuntimeProperties(
				OutputEntityUtils.extractRuntimeProperties(jaxbOutputFileDelimited.getRuntimeProperties()));
		outputFileDelimitedEntity.setQuote(
				jaxbOutputFileDelimited.getQuote() != null ? jaxbOutputFileDelimited.getQuote().getValue() : null);

		outputFileDelimitedEntity.setHasHeader(jaxbOutputFileDelimited.getHasHeader() != null
				? jaxbOutputFileDelimited.getHasHeader().isValue() : false);
		outputFileDelimitedEntity.setStrict(
				jaxbOutputFileDelimited.getStrict() != null ? jaxbOutputFileDelimited.getStrict().isValue() : true);
		if (jaxbOutputFileDelimited.getOverWrite() != null
				&& (TrueFalse.FALSE).equals(jaxbOutputFileDelimited.getOverWrite().getValue()))
			outputFileDelimitedEntity.setOverWrite(false);
		else
			outputFileDelimitedEntity.setOverWrite(true);
	}



	@Override
	public OutputFileDelimitedEntity getEntity() {
		return outputFileDelimitedEntity;
	}

	
}