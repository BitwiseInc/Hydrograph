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

import hydrograph.engine.core.component.entity.DiscardEntity;
import hydrograph.engine.core.component.generator.base.OutputComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.outputtypes.Discard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class DiscardEntityGenerator.
 *
 * @author Bitwise
 *
 */

public class DiscardEntityGenerator extends OutputComponentGeneratorBase {

	private DiscardEntity discardEntity;
	private Discard jaxbDiscard;
	private static Logger LOG = LoggerFactory
			.getLogger(DiscardEntityGenerator.class);

	public DiscardEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbDiscard = (Discard) baseComponent;
	}

	@Override
	public void createEntity() {
		discardEntity = new DiscardEntity();
	}

	@Override
	public void initializeEntity() {
		
		LOG.trace("Initializing discard entity for component: "
				+ jaxbDiscard.getId());
		discardEntity.setComponentId(jaxbDiscard.getId());
		discardEntity.setBatch(jaxbDiscard.getBatch());
		discardEntity.setComponentName(jaxbDiscard.getName());
	}



	@Override
	public DiscardEntity getEntity() {
		return discardEntity;
	}

	
}