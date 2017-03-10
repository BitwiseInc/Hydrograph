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

import hydrograph.engine.adapters.base.CommandAdapterBase;
import hydrograph.engine.commandtype.component.BaseCommandComponent;
import hydrograph.engine.commandtype.component.RunProgram;
import hydrograph.engine.core.component.generator.RunProgramEntityGenerator;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;

public class RunProgramAdapter extends CommandAdapterBase {

	
	private static final long serialVersionUID = 4972095665271276000L;
	BaseCommandComponent runProgram;
	RunProgramEntityGenerator entityGenerator;

	public RunProgramAdapter(TypeBaseComponent typeCommandComponent) {

		entityGenerator = new RunProgramEntityGenerator(typeCommandComponent);

	}

	@Override
	public BaseCommandComponent getComponent() {

		try {
			runProgram = new RunProgram(entityGenerator.getEntity());
		} catch (Throwable e) {
			throw new RuntimeException("Assembly Creation Error for RunProgram Component : " + e.getMessage());

		}
		return runProgram;
	}

}
