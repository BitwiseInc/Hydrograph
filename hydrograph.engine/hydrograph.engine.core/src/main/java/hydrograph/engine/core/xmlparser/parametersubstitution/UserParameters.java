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
package hydrograph.engine.core.xmlparser.parametersubstitution;

import hydrograph.engine.core.utilities.CommandLineOptionsProcessor;
import hydrograph.engine.core.utilities.GeneralUtilities;

import java.io.IOException;
/**
 * The Class UserParameters.
 *
 * @author Bitwise
 */
public class UserParameters implements IParameterBank {

	private CommandLineOptionsProcessor optionProcessor = new CommandLineOptionsProcessor();
	private FileParameters fileParameters = new FileParameters();
	private IParameterBank commandLineParams;

	public UserParameters(String[] args) throws IOException {
		commandLineParams = optionProcessor.getParams(args);
		String commaSeperatedPaths = optionProcessor.getParamFiles(args);
		if (commaSeperatedPaths != null) {
			fileParameters.loadCommaSeperatedFiles(commaSeperatedPaths);
		}

	}

	public String getParameter(String name) {
		if (GeneralUtilities.nullORblank(name)) {
			return null;
		}

		// system property is highest priority
		String val = commandLineParams.getParameter(name);

		// file parameters is second priority
		if (val == null) {
			val = fileParameters.getParameter(name);
		}

		// Environment variable is last priority
		if (val == null) {
			val = System.getenv(name);
		}

		return val;
	}

}
