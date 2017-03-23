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

import hydrograph.engine.core.component.entity.JoinEntity;
import hydrograph.engine.core.component.entity.utils.OperationEntityUtils;
import hydrograph.engine.core.component.generator.base.OperationComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.Join;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class JoinEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class JoinEntityGenerator extends OperationComponentGeneratorBase {

	private JoinEntity joinEntity;
	private Join jaxbJoin;
	private static Logger LOG = LoggerFactory
			.getLogger(JoinEntityGenerator.class);

	public JoinEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbJoin = (Join) baseComponent;
	}

	@Override
	public void createEntity() {
		joinEntity = new JoinEntity();
	}

	@Override
	public void initializeEntity() {

		LOG.trace("Initializing join entity for component: " + jaxbJoin.getId());
		joinEntity.setComponentId(jaxbJoin.getId());
		joinEntity.setBatch(jaxbJoin.getBatch());
		joinEntity.setComponentName(jaxbJoin.getName());

		if (jaxbJoin.getOutSocket() == null) {
			throw new NullPointerException(
					"No out socket defined for join component: "
							+ jaxbJoin.getId());
		}
		joinEntity.setKeyFields(OperationEntityUtils
				.extractKeyFieldsListFromOutSockets(jaxbJoin.getKeys()));
		joinEntity.setOutSocketList(OperationEntityUtils
				.extractOutSocketList(jaxbJoin.getOutSocket()));
		joinEntity.setInSocketList(OperationEntityUtils.extractInSocketList(jaxbJoin.getInSocket()));
		joinEntity.setRuntimeProperties(OperationEntityUtils
				.extractRuntimeProperties(jaxbJoin.getRuntimeProperties()));
	}

	@Override
	public JoinEntity getEntity() {
		return joinEntity;
	}



}
