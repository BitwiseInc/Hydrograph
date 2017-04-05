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

import hydrograph.engine.core.component.entity.InputFileMixedSchemeEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.inputtypes.TextFileMixedScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
/**
 * The Class InputFileMixedSchemeEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class InputFileMixedSchemeEntityGenerator extends
		InputComponentGeneratorBase {

	private InputFileMixedSchemeEntity inputFileMixedSchemeEntity;
	private TextFileMixedScheme jaxbTextFileMixedScheme;
	private static Logger LOG = LoggerFactory
			.getLogger(InputFileMixedSchemeEntityGenerator.class);

	public InputFileMixedSchemeEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbTextFileMixedScheme = (TextFileMixedScheme) baseComponent;
	}

	@Override
	public void createEntity() {
		inputFileMixedSchemeEntity = new InputFileMixedSchemeEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing input file mixed scheme entity for component: "
				+ jaxbTextFileMixedScheme.getId());
		inputFileMixedSchemeEntity.setComponentId(jaxbTextFileMixedScheme
				.getId());
		inputFileMixedSchemeEntity.setBatch(jaxbTextFileMixedScheme.getBatch());
		inputFileMixedSchemeEntity.setComponentName(jaxbTextFileMixedScheme.getName());
		inputFileMixedSchemeEntity.setCharset(jaxbTextFileMixedScheme
				.getCharset() != null ? jaxbTextFileMixedScheme.getCharset()
				.getValue().value() : "UTF-8");

		inputFileMixedSchemeEntity.setPath(jaxbTextFileMixedScheme.getPath()
				.getUri());
		inputFileMixedSchemeEntity
				.setSafe(jaxbTextFileMixedScheme.getSafe() != null ? jaxbTextFileMixedScheme
						.getSafe().isValue() : false);

		inputFileMixedSchemeEntity.setFieldsList(InputEntityUtils
				.extractInputFields(jaxbTextFileMixedScheme.getOutSocket()
						.get(0).getSchema()
						.getFieldOrRecordOrIncludeExternalSchema()));

		inputFileMixedSchemeEntity.setRuntimeProperties(InputEntityUtils
				.extractRuntimeProperties(jaxbTextFileMixedScheme
						.getRuntimeProperties()));

		inputFileMixedSchemeEntity.setStrict(jaxbTextFileMixedScheme
				.getStrict() != null ? jaxbTextFileMixedScheme.getStrict()
				.isValue() : true);

		inputFileMixedSchemeEntity.setOutSocketList(InputEntityUtils
				.extractOutSocket(jaxbTextFileMixedScheme.getOutSocket()));
		inputFileMixedSchemeEntity
				.setQuote(jaxbTextFileMixedScheme.getQuote() != null ? jaxbTextFileMixedScheme
						.getQuote().getValue() : "");
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
	public InputFileMixedSchemeEntity getEntity() {
		return inputFileMixedSchemeEntity;
	}

}
