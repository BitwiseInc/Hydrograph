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

import hydrograph.engine.core.component.entity.PartitionByExpressionEntity;
import hydrograph.engine.core.component.entity.utils.OperationEntityUtils;
import hydrograph.engine.core.component.generator.base.OperationComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.PartitionByExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class PartitionByExpressionEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class PartitionByExpressionEntityGenerator extends OperationComponentGeneratorBase {

	private PartitionByExpressionEntity partitionByExpressionEntity;
	private PartitionByExpression jaxbPartitionByExpression;
	
	private static Logger LOG = LoggerFactory.getLogger(PartitionByExpressionEntityGenerator.class);

	public PartitionByExpressionEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbPartitionByExpression = (PartitionByExpression) baseComponent;
	}

	@Override
	public void createEntity() {
		partitionByExpressionEntity = new PartitionByExpressionEntity();
	}

	@Override
	public void initializeEntity() {
		LOG.trace("Initializing aggregate entity for component: " + jaxbPartitionByExpression.getId());

		partitionByExpressionEntity.setComponentId(jaxbPartitionByExpression.getId());
		partitionByExpressionEntity.setBatch(jaxbPartitionByExpression.getBatch());
		partitionByExpressionEntity.setComponentName(jaxbPartitionByExpression.getName());

		// check if operation is present
		if (jaxbPartitionByExpression.getOperationOrExpressionOrIncludeExternalOperation() != null) {

			LOG.trace("Operation present for PartitionByExpression component: " + jaxbPartitionByExpression.getId()
					+ ", processing");
			partitionByExpressionEntity.setOperation(
					OperationEntityUtils.extractOperations(jaxbPartitionByExpression.getOperationOrExpressionOrIncludeExternalOperation()).get(0));
		} else {
			LOG.trace("Operation not present for aggregate component: " + jaxbPartitionByExpression.getId()
					+ ", skipped operation processing");
		}
		if (jaxbPartitionByExpression.getOutSocket() == null) {
			throw new NullPointerException(
					"No out socket defined for partitionByExpression component: " + jaxbPartitionByExpression.getId());
		}
		partitionByExpressionEntity.setOutSocketList(OperationEntityUtils.extractOutSocketList(jaxbPartitionByExpression.getOutSocket()));
		partitionByExpressionEntity.setNumPartitions(jaxbPartitionByExpression.getNoOfPartitions().getValue());
		partitionByExpressionEntity.setRuntimeProperties(
				OperationEntityUtils.extractRuntimeProperties(jaxbPartitionByExpression.getRuntimeProperties()));
	}

	@Override
	public PartitionByExpressionEntity getEntity() {
		return partitionByExpressionEntity;
	}

}
