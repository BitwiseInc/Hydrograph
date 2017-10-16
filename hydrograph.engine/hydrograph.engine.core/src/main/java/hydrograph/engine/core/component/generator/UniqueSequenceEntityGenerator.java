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

import hydrograph.engine.core.component.entity.UniqueSequenceEntity;
import hydrograph.engine.core.component.entity.utils.OperationEntityUtils;
import hydrograph.engine.core.component.generator.base.OperationComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.GenerateSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class UniqueSequenceEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class UniqueSequenceEntityGenerator extends
		OperationComponentGeneratorBase {

	private UniqueSequenceEntity uniqueSequenceEntity;
	private GenerateSequence jaxbGenerateSequence;
	private static Logger LOG = LoggerFactory
			.getLogger(UniqueSequenceEntityGenerator.class);

	public UniqueSequenceEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbGenerateSequence = (GenerateSequence) baseComponent;
	}

	@Override
	public void createEntity() {
		uniqueSequenceEntity = new UniqueSequenceEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing generate sequence entity for component: "
				+ jaxbGenerateSequence.getId());
		uniqueSequenceEntity.setComponentId(jaxbGenerateSequence.getId());
		uniqueSequenceEntity.setBatch(jaxbGenerateSequence.getBatch());
		uniqueSequenceEntity.setComponentName(jaxbGenerateSequence.getName());
		// check if operation is present
		if (jaxbGenerateSequence.getOperationOrExpressionOrIncludeExternalOperation() != null) {
			LOG.trace("Operation(s) present for unique sequence component: "
					+ jaxbGenerateSequence.getId() + ", processing");
			// set the number of operations in the transform component and set
			// operation present to true
			uniqueSequenceEntity.setNumOperations(jaxbGenerateSequence
					.getOperationOrExpressionOrIncludeExternalOperation().size());
			uniqueSequenceEntity.setOperationPresent(true);
			uniqueSequenceEntity.setOperationsList(OperationEntityUtils
					.extractOperations(jaxbGenerateSequence.getOperationOrExpressionOrIncludeExternalOperation()));
		} else {
			LOG.trace("Operation not present for unique sequence component: "
					+ jaxbGenerateSequence.getId()
					+ ", skipped operation processing");
			// default the number of operations in the transform component to 0
			// and set operation present to false
			uniqueSequenceEntity.setNumOperations(0);
			uniqueSequenceEntity.setOperationPresent(false);
		}

		if (jaxbGenerateSequence.getOutSocket() == null) {
			throw new NullPointerException(
					"No out socket defined for unique sequence component: "
							+ jaxbGenerateSequence.getId());
		}

		uniqueSequenceEntity.setOutSocketList(OperationEntityUtils
				.extractOutSocketList(jaxbGenerateSequence.getOutSocket()));
		uniqueSequenceEntity.setRuntimeProperties(OperationEntityUtils
				.extractRuntimeProperties(jaxbGenerateSequence
						.getRuntimeProperties()));

		uniqueSequenceEntity.setInSocketList(OperationEntityUtils
				.extractInSocketList(jaxbGenerateSequence.getInSocket()));
		uniqueSequenceEntity.setRuntimeProperties(OperationEntityUtils
				.extractRuntimeProperties(jaxbGenerateSequence.getRuntimeProperties()));

	}


	@Override
	public UniqueSequenceEntity getEntity() {
		return uniqueSequenceEntity;
	}
}