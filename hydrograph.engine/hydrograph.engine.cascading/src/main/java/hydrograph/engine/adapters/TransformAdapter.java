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

import hydrograph.engine.adapters.base.OperationAdapterBase;
import hydrograph.engine.cascading.assembly.TransformAssembly;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.TransformEntity;
import hydrograph.engine.core.component.generator.TransformEntityGenerator;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;

public class TransformAdapter extends OperationAdapterBase {

	
	private static final long serialVersionUID = -2854670998242947917L;
	TransformAssembly transformAssembly;
	TransformEntityGenerator transformGenerator;

	public TransformAdapter(TypeBaseComponent baseComponent) {

		transformGenerator = new TransformEntityGenerator(baseComponent);
	}

	@Override
	public void createAssembly(ComponentParameters componentParameters) {
		transformAssembly = new TransformAssembly(transformGenerator.getEntity(), componentParameters);

	}

	@Override
	public BaseComponent<TransformEntity> getAssembly() {

		return transformAssembly;
	}

}
