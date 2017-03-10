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
package hydrograph.engine.commandtype.component;

import hydrograph.engine.core.component.entity.RunProgramEntity;
import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunProgram extends BaseCommandComponent {

	private static Logger LOG = LoggerFactory.getLogger(RunProgram.class);
	private RunProgramEntity runProgramEntity;

	public RunProgram(AssemblyEntityBase runProgramEntity) throws Throwable {
		super(runProgramEntity);
	}

	@Override
	public void castEntityFromBase(AssemblyEntityBase baseComponentEntity) {
		this.runProgramEntity = (RunProgramEntity) baseComponentEntity;
	}

	@Override
	public int executeCommand() throws Throwable  {
		String command = this.runProgramEntity.getCommand();
		try {
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				LOG.debug("Command: " + command);
				command = "cmd /c " + command;
			}
			LOG.debug("Executing Command.");
			Process p = Runtime.getRuntime().exec(command);
			// BufferedReader stdInput = new BufferedReader(new
			// InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			// read any errors from the attempted command and throw the
			// exception, if any
			String errorMessage = "";
			String s;
			while ((s = stdError.readLine()) != null) {
				errorMessage += s;
			}
			
			//Commented below code as it was not working properly.
			/*if (errorMessage.length() > 0) {
				throw new RuntimeException(errorMessage).initCause(new RuntimeException(errorMessage));
			}*/
			return p.waitFor();

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}