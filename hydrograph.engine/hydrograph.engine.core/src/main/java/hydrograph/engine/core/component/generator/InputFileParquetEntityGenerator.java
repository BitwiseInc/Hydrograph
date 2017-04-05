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

import hydrograph.engine.core.component.entity.InputFileParquetEntity;
import hydrograph.engine.core.component.entity.utils.InputEntityUtils;
import hydrograph.engine.core.component.generator.base.InputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.inputtypes.ParquetFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class InputFileParquetEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class InputFileParquetEntityGenerator extends InputComponentGeneratorBase {

	private ParquetFile jaxbParquetFile;
	private InputFileParquetEntity inputFileParquetEntity;
	private static Logger LOG = LoggerFactory.getLogger(InputFileParquetEntityGenerator.class);

	public InputFileParquetEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}


	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbParquetFile = (ParquetFile) baseComponent;
	}

	@Override
	public void createEntity() {
		inputFileParquetEntity = new InputFileParquetEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing input file parquet entity for component: " + jaxbParquetFile.getId());
		inputFileParquetEntity.setComponentId(jaxbParquetFile.getId());
		inputFileParquetEntity.setPath(jaxbParquetFile.getPath().getUri());
		inputFileParquetEntity.setFieldsList(InputEntityUtils.extractInputFields(
				jaxbParquetFile.getOutSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));
		inputFileParquetEntity.setOutSocketList(InputEntityUtils.extractOutSocket(jaxbParquetFile.getOutSocket()));
		inputFileParquetEntity.setRuntimeProperties(
				InputEntityUtils.extractRuntimeProperties(jaxbParquetFile.getRuntimeProperties()));
	}


	@Override
	public InputFileParquetEntity getEntity() {
		// TODO Auto-generated method stub
		return inputFileParquetEntity;
	}

	
}