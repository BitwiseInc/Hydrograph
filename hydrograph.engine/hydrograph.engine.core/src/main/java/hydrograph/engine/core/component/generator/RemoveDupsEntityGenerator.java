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

import hydrograph.engine.core.component.entity.RemoveDupsEntity;
import hydrograph.engine.core.component.entity.utils.StraightPullEntityUtils;
import hydrograph.engine.core.component.generator.base.StraightPullComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.straightpulltypes.RemoveDups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class RemoveDupsEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class RemoveDupsEntityGenerator extends
		StraightPullComponentGeneratorBase {

	private RemoveDups jaxbRemoveDups;
	private RemoveDupsEntity removeDupsEntity;
	private static Logger LOG = LoggerFactory
			.getLogger(RemoveDupsEntityGenerator.class);

	public RemoveDupsEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}


	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbRemoveDups = (RemoveDups) baseComponent;

	}

	@Override
	public void createEntity() {
		removeDupsEntity = new RemoveDupsEntity();

	}

	@Override
	public void initializeEntity() {
		
		LOG.trace("Initializing remove dups entity for component: "
				+ jaxbRemoveDups.getId());
		removeDupsEntity.setComponentId(jaxbRemoveDups.getId());
		removeDupsEntity.setBatch(jaxbRemoveDups.getBatch());
		removeDupsEntity.setComponentName(jaxbRemoveDups.getName());
		removeDupsEntity.setKeep(jaxbRemoveDups.getKeep().getValue().name());
		removeDupsEntity.setKeyFields(StraightPullEntityUtils
				.extractKeyFields(jaxbRemoveDups.getPrimaryKeys()));
		removeDupsEntity.setSecondaryKeyFields(StraightPullEntityUtils
				.extractSecondaryKeyFields(jaxbRemoveDups.getSecondaryKeys()));
		removeDupsEntity
				.setRuntimeProperties(StraightPullEntityUtils
						.extractRuntimeProperties(jaxbRemoveDups
								.getRuntimeProperties()));
		removeDupsEntity.setOutSocketList(StraightPullEntityUtils
				.extractOutSocketList(jaxbRemoveDups.getOutSocket()));
	}


	@Override
	public RemoveDupsEntity getEntity() {
		return removeDupsEntity;
	}
}
