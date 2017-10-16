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

import hydrograph.engine.core.component.entity.NormalizeEntity;
import hydrograph.engine.core.component.entity.utils.OperationEntityUtils;
import hydrograph.engine.core.component.generator.base.OperationComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.Normalize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class NormalizeEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class NormalizeEntityGenerator extends OperationComponentGeneratorBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NormalizeEntity normalizeEntity;
	private Normalize jaxbNormalize;
	private static Logger LOG = LoggerFactory.getLogger(NormalizeEntityGenerator.class);

	public NormalizeEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}


	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbNormalize = (Normalize) baseComponent;

	}

	@Override
	public void createEntity() {
		normalizeEntity = new NormalizeEntity();

	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing normalize entity for component: " + jaxbNormalize.getId());
		normalizeEntity.setComponentId(jaxbNormalize.getId());
		normalizeEntity.setBatch(jaxbNormalize.getBatch());
		normalizeEntity.setComponentName(jaxbNormalize.getName());
		// check if operation is present
		if (jaxbNormalize.getOperationOrExpressionOrIncludeExternalOperation() != null && jaxbNormalize.getOperationOrExpressionOrIncludeExternalOperation().size() > 0) {

			LOG.trace("Operation(s) present for normalize component: " + jaxbNormalize.getId() + ", processing");
			// set the number of operations in the normalize component and set
			// operation present to true
			normalizeEntity.setNumOperations(jaxbNormalize.getOperationOrExpressionOrIncludeExternalOperation().size());
			normalizeEntity.setOperationPresent(true);
			normalizeEntity.setOperationsList(OperationEntityUtils.extractOperations(jaxbNormalize.getOperationOrExpressionOrIncludeExternalOperation()));
		} else {

			LOG.trace("Operation not present for normalize component: " + jaxbNormalize.getId()
					+ ", skipped operation processing");
			// default the number of operations in the normalize component to 0
			// and set operation present to false
			normalizeEntity.setNumOperations(0);
			normalizeEntity.setOperationPresent(false);
		}
		OperationEntityUtils
		.checkIfOutputRecordCountIsPresentInCaseOfExpressionProcessing(
				normalizeEntity.getOperationsList(),
				jaxbNormalize.getOutputRecordCount());
		normalizeEntity.setOutputRecordCount(OperationEntityUtils
				.extractOutputRecordCount(jaxbNormalize
						.getOutputRecordCount()));
		normalizeEntity.setInSocketList(OperationEntityUtils.extractInSocketList(jaxbNormalize.getInSocket()));

		OperationEntityUtils
				.checkIfOutputRecordCountIsPresentInCaseOfExpressionProcessing(
						normalizeEntity.getOperationsList(),
						jaxbNormalize.getOutputRecordCount());
		normalizeEntity.setOutputRecordCount(OperationEntityUtils
				.extractOutputRecordCount(jaxbNormalize
						.getOutputRecordCount()));

		if (jaxbNormalize.getOutSocket() == null) {
			throw new NullPointerException("No out socket defined for normalize component: " + jaxbNormalize.getId());
		}

		normalizeEntity.setRuntimeProperties(
				OperationEntityUtils.extractRuntimeProperties(jaxbNormalize.getRuntimeProperties()));
		normalizeEntity.setOutSocketList(OperationEntityUtils.extractOutSocketList(jaxbNormalize.getOutSocket()));
	}


	@Override
	public NormalizeEntity getEntity() {
		return normalizeEntity;
	}

}
