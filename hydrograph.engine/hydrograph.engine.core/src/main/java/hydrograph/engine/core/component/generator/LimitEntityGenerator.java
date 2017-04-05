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

import hydrograph.engine.core.component.entity.LimitEntity;
import hydrograph.engine.core.component.entity.utils.StraightPullEntityUtils;
import hydrograph.engine.core.component.generator.base.StraightPullComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.straightpulltypes.Limit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class LimitEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class LimitEntityGenerator extends StraightPullComponentGeneratorBase {

	private Limit jaxbLimit;
	private LimitEntity limitEntity;
	private static Logger LOG = LoggerFactory
			.getLogger(LimitEntityGenerator.class);

	public LimitEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbLimit = (Limit) baseComponent;
	}

	@Override
	public void createEntity() {
		limitEntity = new LimitEntity();
	}

	@Override
	public void initializeEntity() {
		
		LOG.trace("Initializing limit entity for component: "
				+ jaxbLimit.getId());
		limitEntity.setComponentId(jaxbLimit.getId());
		limitEntity.setBatch(jaxbLimit.getBatch());
		limitEntity.setComponentName(jaxbLimit.getName());
		limitEntity.setMaxRecord(jaxbLimit.getMaxRecords().getValue());
		limitEntity.setRuntimeProperties(StraightPullEntityUtils
				.extractRuntimeProperties(jaxbLimit.getRuntimeProperties()));
		limitEntity.setOutSocketList(StraightPullEntityUtils
				.extractOutSocketList(jaxbLimit.getOutSocket()));
	}

	@Override
	public LimitEntity getEntity() {
		return limitEntity;
	}

	
}