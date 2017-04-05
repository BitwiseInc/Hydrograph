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

import hydrograph.engine.core.component.entity.OutputFileParquetEntity;
import hydrograph.engine.core.component.entity.utils.OutputEntityUtils;
import hydrograph.engine.core.component.generator.base.OutputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TrueFalse;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.outputtypes.ParquetFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class OutputFileParquetEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class OutputFileParquetEntityGenerator extends OutputComponentGeneratorBase {

	private ParquetFile jaxbParquetFile;
	private OutputFileParquetEntity outputFileParquetEntity;
	private static Logger LOG = LoggerFactory.getLogger(OutputFileParquetEntityGenerator.class);

	public OutputFileParquetEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbParquetFile = (ParquetFile) baseComponent;
	}

	@Override
	public void createEntity() {
		outputFileParquetEntity = new OutputFileParquetEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing output file parquet entity for component: " + jaxbParquetFile.getId());
		outputFileParquetEntity.setComponentId(jaxbParquetFile.getId());
		outputFileParquetEntity.setBatch(jaxbParquetFile.getBatch());
		outputFileParquetEntity.setComponentName(jaxbParquetFile.getName());
		outputFileParquetEntity.setPath(jaxbParquetFile.getPath().getUri());
		outputFileParquetEntity.setFieldsList(OutputEntityUtils.extractOutputFields(
				jaxbParquetFile.getInSocket().get(0).getSchema().getFieldOrRecordOrIncludeExternalSchema()));

		outputFileParquetEntity.setRuntimeProperties(
				OutputEntityUtils.extractRuntimeProperties(jaxbParquetFile.getRuntimeProperties()));
		if (jaxbParquetFile.getOverWrite() != null
				&& (TrueFalse.FALSE).equals(jaxbParquetFile.getOverWrite().getValue()))
			outputFileParquetEntity.setOverWrite(false);
		else
			outputFileParquetEntity.setOverWrite(true);
	}


	@Override
	public OutputFileParquetEntity getEntity() {
		return outputFileParquetEntity;
	}
}
