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

import hydrograph.engine.core.component.entity.FilterEntity;
import hydrograph.engine.core.component.entity.utils.OperationEntityUtils;
import hydrograph.engine.core.component.generator.base.OperationComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class FilterEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class FilterEntityGenerator extends OperationComponentGeneratorBase {

	private FilterEntity filterEntity;
	private Filter jaxbFilter;
	private static Logger LOG = LoggerFactory
			.getLogger(FilterEntityGenerator.class);
	
	public FilterEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbFilter = (Filter) baseComponent;
	}

	@Override
	public void createEntity() {
		filterEntity = new FilterEntity();
	}

	@Override
	public void initializeEntity() {
		LOG.trace("Initializing filter entity for component: "
				+ jaxbFilter.getId());
		filterEntity.setComponentId(jaxbFilter.getId());
		filterEntity.setBatch(jaxbFilter.getBatch());
		filterEntity.setComponentName(jaxbFilter.getName());

		// check if operation is present
		if (jaxbFilter.getOperationOrExpressionOrIncludeExternalOperation() != null) {
			
			LOG.trace("Operation(s) present for filter component: "
					+ jaxbFilter.getId() + ", processing");
			filterEntity.setNumOperations(jaxbFilter.getOperationOrExpressionOrIncludeExternalOperation().size());
			filterEntity.setOperationPresent(true);
			filterEntity.setOperation(OperationEntityUtils.extractOperations(
					jaxbFilter.getOperationOrExpressionOrIncludeExternalOperation()).get(0));
		} else {
			LOG.trace("Operation not present for filter component: "
					+ jaxbFilter.getId() + ", skipped operation processing");
			filterEntity.setNumOperations(0);
			filterEntity.setOperationPresent(false);
		}

		if (jaxbFilter.getOutSocket() == null) {
			throw new NullPointerException(
					"No out socket defined for filter component: "
							+ jaxbFilter.getId());
		}
		filterEntity.setOutSocketList(OperationEntityUtils
				.extractOutSocketList(jaxbFilter.getOutSocket()));
		filterEntity.setRuntimeProperties(OperationEntityUtils
				.extractRuntimeProperties(jaxbFilter.getRuntimeProperties()));
	}

	@Override
	public FilterEntity getEntity() {
		return filterEntity;
	}


}
