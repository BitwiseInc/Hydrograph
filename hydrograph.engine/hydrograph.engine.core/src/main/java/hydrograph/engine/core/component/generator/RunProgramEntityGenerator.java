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

import hydrograph.engine.core.component.entity.RunProgramEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.component.generator.base.CommandComponentGeneratorBase;
import hydrograph.engine.jaxb.commandtypes.RunProgram;
import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
/**
 * The Class RunProgramEntityGenerator.
 *
 * @author Bitwise
 *
 */
public class RunProgramEntityGenerator extends CommandComponentGeneratorBase {

	private RunProgram runProgram;
	private RunProgramEntity runProgramEntity;

	public RunProgramEntityGenerator(TypeBaseComponent typeCommandComponent) {
		super(typeCommandComponent);
	}

	@Override
	public void createEntity() {
		runProgramEntity = new RunProgramEntity();
	}

	@Override
	public void initializeEntity() {
		runProgramEntity.setComponentId(runProgram.getId());
		runProgramEntity.setBatch(runProgram.getBatch());
		runProgramEntity.setComponentName(runProgram.getName());
		runProgramEntity.setCommand(runProgram.getCommand().getValue());
	}


	@Override
	public AssemblyEntityBase getEntity() {
		return runProgramEntity;
	}

	@Override
	public void castComponentFromBase(TypeBaseComponent baseComponent) {
		this.runProgram = (RunProgram) baseComponent;
	}

	
}
