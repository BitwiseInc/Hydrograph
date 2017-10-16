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

import hydrograph.engine.core.component.entity.CumulateEntity;
import hydrograph.engine.core.component.entity.utils.OperationEntityUtils;
import hydrograph.engine.core.component.generator.base.OperationComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.Cumulate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class CumulateEntityGenerator.
 *
 * @author Bitwise
 *
 */

public class CumulateEntityGenerator extends OperationComponentGeneratorBase {

	private CumulateEntity cumulateEntity;
	private Cumulate jaxbCumulate;
	private static Logger LOG = LoggerFactory.getLogger(CumulateEntityGenerator.class);

	public CumulateEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbCumulate = (Cumulate) baseComponent;
	}


	@Override
	public void createEntity() {
		cumulateEntity = new CumulateEntity();
	}

	@Override
	public void initializeEntity() {
		LOG.trace("Initializing cumulate entity for component: " + jaxbCumulate.getId());
		cumulateEntity.setComponentId(jaxbCumulate.getId());
		cumulateEntity.setBatch(jaxbCumulate.getBatch());
		cumulateEntity.setComponentName(jaxbCumulate.getName());

		// check if operation is present
		if (jaxbCumulate.getOperationOrExpressionOrIncludeExternalOperation() != null && jaxbCumulate.getOperationOrExpressionOrIncludeExternalOperation().size() > 0) {

			LOG.trace("Operation(s) present for cumulate component: " + jaxbCumulate.getId() + ", processing");
			// set the number of operations in the transform component and set
			// operation present to true
			cumulateEntity.setNumOperations(jaxbCumulate.getOperationOrExpressionOrIncludeExternalOperation().size());
			cumulateEntity.setOperationPresent(true);
			cumulateEntity.setOperationsList(OperationEntityUtils.extractOperations(jaxbCumulate.getOperationOrExpressionOrIncludeExternalOperation()));
		} else {
			LOG.trace("Operation not present for cumulate component: " + jaxbCumulate.getId()
					+ ", skipped operation processing");
			// default the number of operations in the transform component to 0
			// and set operation present to false
			cumulateEntity.setNumOperations(0);
			cumulateEntity.setOperationPresent(false);
		}

		if (jaxbCumulate.getOutSocket() == null) {
			throw new NullPointerException("No out socket defined for cumulate component: " + jaxbCumulate.getId());
		}
		cumulateEntity.setInSocketList(OperationEntityUtils.extractInSocketList(jaxbCumulate.getInSocket()));
		cumulateEntity.setOutSocketList(OperationEntityUtils.extractOutSocketList(jaxbCumulate.getOutSocket()));
		cumulateEntity.setKeyFields(OperationEntityUtils.extractKeyFields(jaxbCumulate.getPrimaryKeys()));
		cumulateEntity
				.setSecondaryKeyFields(OperationEntityUtils.extractSecondaryKeyFields(jaxbCumulate.getSecondaryKeys()));
		cumulateEntity.setRuntimeProperties(
				OperationEntityUtils.extractRuntimeProperties(jaxbCumulate.getRuntimeProperties()));
	}

	@Override
	public CumulateEntity getEntity() {
		// TODO Auto-generated method stub
		return cumulateEntity;
	}

	
}