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
/**
 * 
 */
package hydrograph.engine.core.component.generator;

import hydrograph.engine.core.component.entity.SortEntity;
import hydrograph.engine.core.component.entity.utils.StraightPullEntityUtils;
import hydrograph.engine.core.component.generator.base.StraightPullComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.straightpulltypes.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SortEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class SortEntityGenerator extends StraightPullComponentGeneratorBase {

	SortEntity sortEntity;
	Sort jaxbSort;
	private static Logger LOG = LoggerFactory.getLogger(SortEntityGenerator.class);

	public SortEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hydrograph.engine.cascading.assembly.generator.base.
	 * AssemblyGeneratorBase# castComponentFromBase
	 * (hydrograph.engine.graph.commontypes.TypeBaseComponent)
	 */
	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbSort = (Sort) baseComponent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hydrograph.engine.cascading.assembly.generator.base.
	 * AssemblyGeneratorBase# createEntity ()
	 */
	@Override
	public void createEntity() {
		sortEntity = new SortEntity();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hydrograph.engine.cascading.assembly.generator.base.
	 * AssemblyGeneratorBase# initializeEntity()
	 */
	@Override
	public void initializeEntity() {

		LOG.trace("Initializing sort entity for component: " + jaxbSort.getId());
		sortEntity.setComponentId(jaxbSort.getId());
		sortEntity.setBatch(jaxbSort.getBatch());
		sortEntity.setComponentName(jaxbSort.getName());
		sortEntity.setRuntimeProperties(
				StraightPullEntityUtils.extractRuntimeProperties(jaxbSort.getRuntimeProperties()));
		sortEntity.setKeyFields(StraightPullEntityUtils.extractKeyFields(jaxbSort.getPrimaryKeys()));
		sortEntity
				.setSecondaryKeyFields(StraightPullEntityUtils.extractSecondaryKeyFields(jaxbSort.getSecondaryKeys()));

		if (jaxbSort.getOutSocket() == null) {
			throw new NullPointerException("No out socket defined for sort component: " + jaxbSort.getId());
		}

		sortEntity.setOutSocketList(StraightPullEntityUtils.extractOutSocketList(jaxbSort.getOutSocket()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hydrograph.engine.cascading.assembly.generator.base.
	 * AssemblyGeneratorBase# createAssembly
	 * (hydrograph.engine.cascading.assembly.infra.ComponentParameters)
	 */

	@Override
	public SortEntity getEntity() {
		return sortEntity;
	}
}