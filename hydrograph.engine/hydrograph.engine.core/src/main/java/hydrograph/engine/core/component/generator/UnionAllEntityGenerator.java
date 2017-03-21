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

import hydrograph.engine.core.component.entity.UnionAllEntity;
import hydrograph.engine.core.component.entity.utils.StraightPullEntityUtils;
import hydrograph.engine.core.component.generator.base.StraightPullComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.straightpulltypes.UnionAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class UnionAllEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class UnionAllEntityGenerator extends
		StraightPullComponentGeneratorBase {

	private UnionAll jaxbUnionAll;
	private UnionAllEntity unionAllEntity;
	private static Logger LOG = LoggerFactory
			.getLogger(UnionAllEntityGenerator.class);

	public UnionAllEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbUnionAll = (UnionAll) baseComponent;
	}

	@Override
	public void createEntity() {
		unionAllEntity = new UnionAllEntity();
	}

	@Override
	public void initializeEntity() {
		
		LOG.trace("Initializing union all entity for component: "
				+ jaxbUnionAll.getId());
		unionAllEntity.setComponentId(jaxbUnionAll.getId());
		unionAllEntity.setBatch(jaxbUnionAll.getBatch());
		unionAllEntity.setComponentName(jaxbUnionAll.getName());
		unionAllEntity.setOutSocket(StraightPullEntityUtils.extractOutSocketList(
				jaxbUnionAll.getOutSocket()).get(0));
		unionAllEntity.setRuntimeProperties(StraightPullEntityUtils
				.extractRuntimeProperties(jaxbUnionAll.getRuntimeProperties()));
	}

	@Override
	public UnionAllEntity getEntity() {
		// TODO Auto-generated method stub
		return unionAllEntity;
	}
}