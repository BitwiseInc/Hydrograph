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

import hydrograph.engine.core.component.entity.LookupEntity;
import hydrograph.engine.core.component.entity.utils.OperationEntityUtils;
import hydrograph.engine.core.component.generator.base.OperationComponentGeneratorBase;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.operationstypes.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class LookupEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class LookupEntityGenerator extends OperationComponentGeneratorBase {

	private LookupEntity lookupEntity;
	private Lookup jaxbLookup;
	private static Logger LOG = LoggerFactory
			.getLogger(LookupEntityGenerator.class);

	public LookupEntityGenerator(TypeBaseComponent baseComponent) {
		super(baseComponent);
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		jaxbLookup = (Lookup) baseComponent;
	}

	@Override
	public void createEntity() {
		lookupEntity = new LookupEntity();
	}

	@Override
	public void initializeEntity() {
		LOG.trace("Initializing hash join entity for component: "
				+ jaxbLookup.getId());
		lookupEntity.setComponentId(jaxbLookup.getId());
		lookupEntity.setBatch(jaxbLookup.getBatch());
		lookupEntity.setComponentName(jaxbLookup.getName());

		if (jaxbLookup.getOutSocket() == null) {
			throw new NullPointerException("No out socket defined for hash join component: " + jaxbLookup.getId());
		}

		lookupEntity.setKeyFields(
				OperationEntityUtils.extractKeyFieldsListFromOutSocketsForLookup(jaxbLookup.getKeys()));
		lookupEntity.setOutSocketList(OperationEntityUtils.extractOutSocketList(jaxbLookup.getOutSocket()));
		lookupEntity.setInSocketList(OperationEntityUtils.extractInSocketList(jaxbLookup.getInSocket()));
//		hashJoinEntity.setInSocketMap(OperationEntityUtils.extractInSocketMap(jaxbHashJoin.getInSocket()));
		lookupEntity.setRuntimeProperties(
				OperationEntityUtils.extractRuntimeProperties(jaxbLookup.getRuntimeProperties()));
		
		if(jaxbLookup.getMatch() != null)
			lookupEntity.setMatch(jaxbLookup.getMatch().getValue().value());
		else
			throw new NullPointerException("No 'match' option set for hash join component: " + jaxbLookup.getId());
	}

	@Override
	public LookupEntity getEntity() {
		return lookupEntity;
	}

	

}
