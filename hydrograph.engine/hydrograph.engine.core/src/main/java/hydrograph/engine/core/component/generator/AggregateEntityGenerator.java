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

import hydrograph.engine.core.component.entity.AggregateEntity;
import hydrograph.engine.core.component.entity.utils.OperationEntityUtils;
import hydrograph.engine.core.component.generator.base.OperationComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class AggregateEntityGenerator.
 *
 * @author Bitwise
 *
 */

public class AggregateEntityGenerator extends OperationComponentGeneratorBase {

	private AggregateEntity aggregateEntity;
	private Aggregate jaxbAggregate;
	private static Logger LOG = LoggerFactory.getLogger(AggregateEntityGenerator.class);

	public AggregateEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbAggregate = (Aggregate) baseComponent;

	}

	@Override
	public void createEntity() {
		aggregateEntity = new AggregateEntity();
	}

	@Override
	public void initializeEntity() {
		LOG.trace("Initializing aggregate entity for component: " + jaxbAggregate.getId());
		aggregateEntity.setComponentId(jaxbAggregate.getId());
		aggregateEntity.setBatch(jaxbAggregate.getBatch());
		aggregateEntity.setComponentName(jaxbAggregate.getName());

		// check if operation is present
		if (jaxbAggregate.getOperationOrExpressionOrIncludeExternalOperation() != null && jaxbAggregate.getOperationOrExpressionOrIncludeExternalOperation().size() > 0) {

			LOG.trace("Operation(s) present for aggregate component: " + jaxbAggregate.getId() + ", processing");
			// set the number of operations in the transform component and set
			// operation present to true
			aggregateEntity.setNumOperations(jaxbAggregate.getOperationOrExpressionOrIncludeExternalOperation().size());
			aggregateEntity.setOperationPresent(true);
			aggregateEntity.setOperationsList(OperationEntityUtils.extractOperations(jaxbAggregate.getOperationOrExpressionOrIncludeExternalOperation()));
		} else {
			LOG.trace("Operation not present for aggregate component: " + jaxbAggregate.getId()
					+ ", skipped operation processing");
			// default the number of operations in the transform component to 0
			// and set operation present to false
			aggregateEntity.setNumOperations(0);
			aggregateEntity.setOperationPresent(false);
		}

		if (jaxbAggregate.getOutSocket() == null) {
			throw new NullPointerException("No out socket defined for aggregate component: " + jaxbAggregate.getId());
		}

		aggregateEntity.setOutSocketList(OperationEntityUtils.extractOutSocketList(jaxbAggregate.getOutSocket()));
aggregateEntity.setInSocketList(OperationEntityUtils.extractInSocketList(jaxbAggregate.getInSocket()));
		aggregateEntity.setKeyFields(OperationEntityUtils.extractKeyFields(jaxbAggregate.getPrimaryKeys()));
		aggregateEntity.setSecondaryKeyFields(
				OperationEntityUtils.extractSecondaryKeyFields(jaxbAggregate.getSecondaryKeys()));

		aggregateEntity.setRuntimeProperties(
				OperationEntityUtils.extractRuntimeProperties(jaxbAggregate.getRuntimeProperties()));
	}

	@Override
	public AggregateEntity getEntity() {
		return aggregateEntity;
	}


}
