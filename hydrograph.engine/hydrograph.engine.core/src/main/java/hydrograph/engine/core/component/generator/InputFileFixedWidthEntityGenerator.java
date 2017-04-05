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

import hydrograph.engine.core.component.entity.InputFileFixedWidthEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.inputtypes.TextFileFixedWidth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
/**
 * The Class InputFileFixedWidthEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class InputFileFixedWidthEntityGenerator extends
		InputComponentGeneratorBase {
	private TextFileFixedWidth jaxbInputFileFixedWidth;
	private InputFileFixedWidthEntity inputFileFixedWidthEntity;
	private static Logger LOG = LoggerFactory
			.getLogger(InputFileFixedWidthEntityGenerator.class);

	public InputFileFixedWidthEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbInputFileFixedWidth = (TextFileFixedWidth) baseComponent;
	}

	@Override
	public void createEntity() {
		inputFileFixedWidthEntity = new InputFileFixedWidthEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing input file fixed width entity for component: "
				+ jaxbInputFileFixedWidth.getId());
		inputFileFixedWidthEntity.setComponentId(jaxbInputFileFixedWidth
				.getId());
		inputFileFixedWidthEntity.setBatch(jaxbInputFileFixedWidth.getBatch());
		inputFileFixedWidthEntity.setComponentName(jaxbInputFileFixedWidth.getName());
		inputFileFixedWidthEntity.setPath(jaxbInputFileFixedWidth.getPath()
				.getUri());
		inputFileFixedWidthEntity
				.setSafe(jaxbInputFileFixedWidth.getSafe() != null ? jaxbInputFileFixedWidth
						.getSafe().isValue() : false);
		inputFileFixedWidthEntity
				.setStrict(jaxbInputFileFixedWidth.getStrict() != null ? jaxbInputFileFixedWidth
						.getStrict().isValue() : true);
		inputFileFixedWidthEntity.setCharset(jaxbInputFileFixedWidth
				.getCharset() != null ? jaxbInputFileFixedWidth.getCharset()
				.getValue().value() : "UTF-8");

		inputFileFixedWidthEntity.setFieldsList(InputEntityUtils
				.extractInputFields(jaxbInputFileFixedWidth.getOutSocket()
						.get(0).getSchema()
						.getFieldOrRecordOrIncludeExternalSchema()));
		inputFileFixedWidthEntity.setRuntimeProperties(InputEntityUtils
				.extractRuntimeProperties(jaxbInputFileFixedWidth
						.getRuntimeProperties()));

		inputFileFixedWidthEntity.setOutSocketList(InputEntityUtils
				.extractOutSocket(jaxbInputFileFixedWidth.getOutSocket()));
	}

	

	private Properties getRunTimeProperties(List<Property> list) {
		Properties properties = new Properties();
		for (Property property : list) {
			properties.put(property.getName(), property.getValue());
		}
		return properties;
	}

	private Properties getRuntimeProperty() {
		return new Properties();
	}



	@Override
	public InputFileFixedWidthEntity getEntity() {
		return inputFileFixedWidthEntity;
	}

}
