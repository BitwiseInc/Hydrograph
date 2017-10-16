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

import hydrograph.engine.core.component.entity.GroupCombineEntity;
import hydrograph.engine.core.component.entity.utils.OperationEntityUtils;
import hydrograph.engine.core.component.generator.base.OperationComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.Groupcombine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GroupCombineEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class GroupCombineEntityGenerator extends OperationComponentGeneratorBase {

	private GroupCombineEntity groupCombineEntity;
	private Groupcombine jaxbGroupCombine;
	private static Logger LOG = LoggerFactory.getLogger(GroupCombineEntityGenerator.class);

	public GroupCombineEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbGroupCombine = (Groupcombine) baseComponent;

	}

	@Override
	public void createEntity() {
		groupCombineEntity = new GroupCombineEntity();
	}

	@Override
	public void initializeEntity() {
		LOG.trace("Initializing aggregate entity for component: " + jaxbGroupCombine.getId());
		groupCombineEntity.setComponentId(jaxbGroupCombine.getId());
		groupCombineEntity.setBatch(jaxbGroupCombine.getBatch());
		groupCombineEntity.setComponentName(jaxbGroupCombine.getName());
		// check if operation is present
		if (jaxbGroupCombine.getOperationOrExpressionOrIncludeExternalOperation() != null && jaxbGroupCombine.getOperationOrExpressionOrIncludeExternalOperation().size() > 0) {

			LOG.trace("Operation(s) present for aggregate component: " + jaxbGroupCombine.getId() + ", processing");
			// set the number of operations in the transform component and set
			// operation present to true
			groupCombineEntity.setNumOperations(jaxbGroupCombine.getOperationOrExpressionOrIncludeExternalOperation().size());
			groupCombineEntity.setOperationPresent(true);
			groupCombineEntity.setOperationsList(OperationEntityUtils.extractOperations(jaxbGroupCombine.getOperationOrExpressionOrIncludeExternalOperation()));
		} else {
			LOG.trace("Operation not present for aggregate component: " + jaxbGroupCombine.getId()
					+ ", skipped operation processing");
			// default the number of operations in the transform component to 0
			// and set operation present to false
			groupCombineEntity.setNumOperations(0);
			groupCombineEntity.setOperationPresent(false);
		}

		if (jaxbGroupCombine.getOutSocket() == null) {
			throw new NullPointerException("No out socket defined for aggregate component: " + jaxbGroupCombine.getId());
		}

		groupCombineEntity.setOutSocketList(OperationEntityUtils.extractOutSocketList(jaxbGroupCombine.getOutSocket()));
groupCombineEntity.setInSocketList(OperationEntityUtils.extractInSocketList(jaxbGroupCombine.getInSocket()));
		groupCombineEntity.setKeyFields(OperationEntityUtils.extractKeyFields(jaxbGroupCombine.getPrimaryKeys()));
		groupCombineEntity.setRuntimeProperties(
				OperationEntityUtils.extractRuntimeProperties(jaxbGroupCombine.getRuntimeProperties()));
	}

	@Override
	public GroupCombineEntity getEntity() {
		return groupCombineEntity;
	}


}
