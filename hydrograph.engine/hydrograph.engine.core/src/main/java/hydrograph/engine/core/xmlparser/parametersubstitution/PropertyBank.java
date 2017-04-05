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

import java.util.Properties;
/**
 * The Class PropertyBank.
 *
 * @author Bitwise
 */
public class PropertyBank implements IParameterBank {

	private Properties props = new Properties();

	public PropertyBank addProperties(Properties newProps) {
		props.putAll(newProps);
		return this;
	}

	@Override
	public String getParameter(String name) {
		if (GeneralUtilities.nullORblank(name)) {
			return null;
		}
		return props.getProperty(name);
	}

}
