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

import hydrograph.engine.core.utilities.GeneralUtilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * The Class FileParameters.
 *
 * @author Bitwise
 */
public class FileParameters implements IParameterBank {

	private Properties params = new Properties();

	public FileParameters() {

	}

	public FileParameters(String commaSeperatedPaths) throws IOException {
		loadCommaSeperatedFiles(commaSeperatedPaths);
	}

	public FileParameters loadFile(String path) throws IOException {

		if (GeneralUtilities.nullORblank(path)) {
			throw new FileParametersException(
					"Input path for loading parameter file is null or blank.");
		}

		InputStream inStream = null;
		try {
			inStream = new FileInputStream(path.trim());
			params.load(inStream);

		} finally {
			if (inStream != null) {
				inStream.close();
			}
		}
		return this;

	}

	public FileParameters loadCommaSeperatedFiles(String commaSeperatedPaths)
			throws IOException {

		if (commaSeperatedPaths == null) {
			throw new FileParametersException(
					"Input path for loading parameter file is null.");
		}

		String[] paths = commaSeperatedPaths.split(",");

		for (String path : paths) {
			loadFile(path);
		}

		return this;

	}

	public String getParameter(String key) {

		if (GeneralUtilities.nullORblank(key)) {
			throw new FileParametersException(
					"Input parameter key is null or balnk.");
		}
		return params.getProperty(key);
	}

	private class FileParametersException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7734120319028227489L;

		public FileParametersException(String msg) {
			super(msg);
		}
	}

}
