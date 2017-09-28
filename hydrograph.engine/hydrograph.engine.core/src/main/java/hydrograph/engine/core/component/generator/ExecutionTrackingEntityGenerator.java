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

import hydrograph.engine.core.component.entity.ExecutionTrackingEntity;
import hydrograph.engine.core.component.entity.utils.OperationEntityUtils;
import hydrograph.engine.core.component.generator.base.OperationComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.Executiontracking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class ExecutionTrackingEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class ExecutionTrackingEntityGenerator extends OperationComponentGeneratorBase {

	private ExecutionTrackingEntity executionTrackingEntity;
	private Executiontracking jaxbExecutionTracking;
	private static Logger LOG = LoggerFactory
			.getLogger(ExecutionTrackingEntityGenerator.class);

	public ExecutionTrackingEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbExecutionTracking = (Executiontracking) baseComponent;
	}

	@Override
	public void createEntity() {
		executionTrackingEntity = new ExecutionTrackingEntity();
	}

	@Override
	public void initializeEntity() {
		LOG.trace("Initializing ExecutionTracking entity for component: "
				+ jaxbExecutionTracking.getId());
		executionTrackingEntity.setComponentId(jaxbExecutionTracking.getId());
		executionTrackingEntity.setBatch(jaxbExecutionTracking.getBatch());
		executionTrackingEntity.setComponentName(jaxbExecutionTracking.getName());

		// check if operation is present
		if (jaxbExecutionTracking.getOperationOrExpressionOrIncludeExternalOperation() != null) {
			
			LOG.trace("Operation(s) present for ExecutionTracking component: "
					+ jaxbExecutionTracking.getId() + ", processing");
			executionTrackingEntity.setNumOperations(jaxbExecutionTracking.getOperationOrExpressionOrIncludeExternalOperation().size());
			executionTrackingEntity.setOperationPresent(true);
			executionTrackingEntity.setOperation(OperationEntityUtils.extractOperations(
					jaxbExecutionTracking.getOperationOrExpressionOrIncludeExternalOperation()).get(0));
		} else {
			LOG.trace("Operation not present for ExecutionTracking component: "
					+ jaxbExecutionTracking.getId() + ", skipped operation processing");
			executionTrackingEntity.setNumOperations(0);
			executionTrackingEntity.setOperationPresent(false);
		}

		if (jaxbExecutionTracking.getOutSocket() == null) {
			throw new NullPointerException(
					"No out socket defined for ExecutionTracking component: "
							+ jaxbExecutionTracking.getId());
		}
		executionTrackingEntity.setOutSocketList(OperationEntityUtils
				.extractOutSocketList(jaxbExecutionTracking.getOutSocket()));
		executionTrackingEntity.setRuntimeProperties(OperationEntityUtils
				.extractRuntimeProperties(jaxbExecutionTracking.getRuntimeProperties()));
	}

	@Override
	public ExecutionTrackingEntity getEntity() {
		return executionTrackingEntity;
	}


}