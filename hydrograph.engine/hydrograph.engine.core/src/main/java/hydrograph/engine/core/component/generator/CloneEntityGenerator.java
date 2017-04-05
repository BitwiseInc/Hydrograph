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

import hydrograph.engine.core.component.entity.CloneEntity;
import hydrograph.engine.core.component.entity.utils.StraightPullEntityUtils;
import hydrograph.engine.core.component.generator.base.StraightPullComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.straightpulltypes.Clone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class CloneEntityGenerator.
 *
 * @author Bitwise
 *
 */

public class CloneEntityGenerator extends StraightPullComponentGeneratorBase {

	CloneEntity cloneEntity;
	private Clone jaxbClone;
	private static Logger LOG = LoggerFactory
			.getLogger(CloneEntityGenerator.class);
	
	public CloneEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbClone = (Clone) baseComponent;
	}

	@Override
	public void createEntity() {
		cloneEntity = new CloneEntity();
	}

	@Override
	public void initializeEntity() {
		LOG.trace("Initializing clone entity for component: "
				+ jaxbClone.getId());
		cloneEntity.setComponentId(jaxbClone.getId());
		cloneEntity.setBatch(jaxbClone.getBatch());
		cloneEntity.setComponentName(jaxbClone.getName());
		cloneEntity.setOutSocketList(StraightPullEntityUtils
				.extractOutSocketList(jaxbClone.getOutSocket()));
		cloneEntity.setRuntimeProperties(StraightPullEntityUtils
				.extractRuntimeProperties(jaxbClone.getRuntimeProperties()));
	}



	@Override
	public CloneEntity getEntity() {
		
		return cloneEntity;
	}

	
}
