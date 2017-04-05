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

import hydrograph.engine.core.component.entity.OutputFileMixedSchemeEntity;
import hydrograph.engine.core.component.entity.utils.OutputEntityUtils;
import hydrograph.engine.core.component.generator.base.OutputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TrueFalse;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.outputtypes.TextFileMixedScheme;
/**
 * The Class OutputFileMixedSchemeEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputFileMixedSchemeEntityGenerator extends OutputComponentGeneratorBase {

	private OutputFileMixedSchemeEntity outputFileMixedSchemeEntity;
	private TextFileMixedScheme jaxbTextFileMixedScheme;

	public OutputFileMixedSchemeEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbTextFileMixedScheme = (TextFileMixedScheme) baseComponent;
	}

	@Override
	public void createEntity() {
		outputFileMixedSchemeEntity = new OutputFileMixedSchemeEntity();
	}

	@Override
	public void initializeEntity() {

		outputFileMixedSchemeEntity.setComponentId(jaxbTextFileMixedScheme.getId());
		outputFileMixedSchemeEntity.setBatch(jaxbTextFileMixedScheme.getBatch());
		outputFileMixedSchemeEntity.setComponentName(jaxbTextFileMixedScheme.getName());
		outputFileMixedSchemeEntity.setCharset(jaxbTextFileMixedScheme.getCharset() != null
				? jaxbTextFileMixedScheme.getCharset().getValue().value() : "UTF-8");

		outputFileMixedSchemeEntity.setPath(jaxbTextFileMixedScheme.getPath().getUri());
		outputFileMixedSchemeEntity.setSafe(
				jaxbTextFileMixedScheme.getSafe() != null ? jaxbTextFileMixedScheme.getSafe().isValue() : false);

		outputFileMixedSchemeEntity.setFieldsList(OutputEntityUtils.extractOutputFields(
				jaxbTextFileMixedScheme.getInSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));

		outputFileMixedSchemeEntity.setRuntimeProperties(
				OutputEntityUtils.extractRuntimeProperties(jaxbTextFileMixedScheme.getRuntimeProperties()));
		outputFileMixedSchemeEntity
		.setQuote(jaxbTextFileMixedScheme.getQuote() != null ? jaxbTextFileMixedScheme
				.getQuote().getValue() : "");

		outputFileMixedSchemeEntity.setStrict(
				jaxbTextFileMixedScheme.getStrict() != null ? jaxbTextFileMixedScheme.getStrict().isValue() : true);
		if (jaxbTextFileMixedScheme.getOverWrite() != null
				&& (TrueFalse.FALSE).equals(jaxbTextFileMixedScheme.getOverWrite().getValue()))
			outputFileMixedSchemeEntity.setOverWrite(false);
		else
			outputFileMixedSchemeEntity.setOverWrite(true);

	}

	@Override
	public OutputFileMixedSchemeEntity getEntity() {
		return outputFileMixedSchemeEntity;
	}
}
