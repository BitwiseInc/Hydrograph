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
package hydrograph.engine.adapters;

import hydrograph.engine.adapters.base.StraightPullAdapterBase;
import hydrograph.engine.cascading.assembly.CloneAssembly;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.CloneEntity;
import hydrograph.engine.core.component.generator.CloneEntityGenerator;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;

public class CloneAdapter extends StraightPullAdapterBase {

	
	private static final long serialVersionUID = 7023106844888384267L;
	CloneAssembly cloneAssembly;
	CloneEntityGenerator cloneGenerator;

	public CloneAdapter(TypeBaseComponent baseComponent) {

		cloneGenerator = new CloneEntityGenerator(baseComponent);
	}

	@Override
	public void createAssembly(ComponentParameters componentParameters) {
		cloneAssembly = new CloneAssembly(cloneGenerator.getEntity(), componentParameters);

	}

	@Override
	public BaseComponent<CloneEntity> getAssembly() {

		return cloneAssembly;
	}

}
