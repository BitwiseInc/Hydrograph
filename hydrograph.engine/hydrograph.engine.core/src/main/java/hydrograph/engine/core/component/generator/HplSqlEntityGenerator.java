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

import hydrograph.engine.core.component.entity.HplSqlEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.component.generator.base.CommandComponentGeneratorBase;
import hydrograph.engine.jaxb.commandtypes.Hplsql;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
/**
 * The Class HplSqlEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class HplSqlEntityGenerator extends CommandComponentGeneratorBase {

	private Hplsql hplsql;
	private HplSqlEntity hplSqlEntity;

	public HplSqlEntityGenerator(TypeBaseComponent typeCommandComponent) {
		super(typeCommandComponent);
	}

	@Override
	public void createEntity() {
		hplSqlEntity = new HplSqlEntity();
	}

	@Override
	public void initializeEntity() {
		hplSqlEntity.setComponentId(hplsql.getId());
		hplSqlEntity.setBatch(hplsql.getBatch());
		hplSqlEntity.setComponentName(hplsql.getName());
		hplSqlEntity.setCommand(hplsql.getCommand().getCmd());
		if (hplsql.getExecute().getUri() != null)
			hplSqlEntity.setUri(hplsql.getExecute().getUri().getValue());
		if (hplsql.getExecute().getQuery() != null)
			hplSqlEntity.setQuery(hplsql.getExecute().getQuery().getValue());
	}



	@Override
	public AssemblyEntityBase getEntity() {
		return hplSqlEntity;
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		this.hplsql = (Hplsql) baseComponent;
	}

	
}
