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

import hydrograph.engine.core.component.entity.OutputFileFixedWidthEntity;
import hydrograph.engine.core.component.entity.utils.OutputEntityUtils;
import hydrograph.engine.core.component.generator.base.OutputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TrueFalse;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.outputtypes.TextFileFixedWidth;
/**
 * The Class OutputFileFixedWidthEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputFileFixedWidthEntityGenerator extends OutputComponentGeneratorBase {

	public OutputFileFixedWidthEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
		// TODO Auto-generated constructor stub
	}

	private OutputFileFixedWidthEntity outputFileFixedWidthEntity;
	private TextFileFixedWidth jaxbOutputFileFixedWidth;

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbOutputFileFixedWidth = (TextFileFixedWidth) baseComponent;
	}

	@Override
	public void createEntity() {
		outputFileFixedWidthEntity = new OutputFileFixedWidthEntity();
	}

	@Override
	public void initializeEntity() {

		outputFileFixedWidthEntity.setComponentId(jaxbOutputFileFixedWidth.getId());
		outputFileFixedWidthEntity.setBatch(jaxbOutputFileFixedWidth.getBatch());
		outputFileFixedWidthEntity.setComponentName(jaxbOutputFileFixedWidth.getName());
		outputFileFixedWidthEntity.setPath(jaxbOutputFileFixedWidth.getPath().getUri());
		outputFileFixedWidthEntity.setSafe(
				jaxbOutputFileFixedWidth.getSafe() != null ? jaxbOutputFileFixedWidth.getSafe().isValue() : false);
		outputFileFixedWidthEntity.setStrict(
				jaxbOutputFileFixedWidth.getStrict() != null ? jaxbOutputFileFixedWidth.getStrict().isValue() : false);
		outputFileFixedWidthEntity.setCharset(jaxbOutputFileFixedWidth.getCharset() != null
				? jaxbOutputFileFixedWidth.getCharset().getValue().value() : "UTF-8");
		outputFileFixedWidthEntity.setRuntimeProperties(
				OutputEntityUtils.extractRuntimeProperties(jaxbOutputFileFixedWidth.getRuntimeProperties()));
		outputFileFixedWidthEntity.setFieldsList(OutputEntityUtils.extractOutputFields(
				jaxbOutputFileFixedWidth.getInSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
		if (jaxbOutputFileFixedWidth.getOverWrite() != null
				&& (TrueFalse.FALSE).equals(jaxbOutputFileFixedWidth.getOverWrite().getValue()))
			outputFileFixedWidthEntity.setOverWrite(false);
		else
			outputFileFixedWidthEntity.setOverWrite(true);
	}

	@Override
	public OutputFileFixedWidthEntity getEntity() {
		return outputFileFixedWidthEntity;
	}

	
}