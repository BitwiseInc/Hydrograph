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
package hydrograph.engine.core.props;

import hydrograph.engine.core.utilities.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
/**
 * The Class PropertiesLoader.
 *
 * @author Bitwise
 *
 */
public class PropertiesLoader {

	public static final String ENGINE_PROPERTY_FILE = "engine.properties";
	public static final String INPUT_SERVICE_PROPERTY_FILE = "input_service.properties";
	public static final String RUNTIME_SERVICE_PROPERTY_FILE = "runtime_service.properties";
	//public static final String XPATH_PROPERTY_FILE = "xpath.properties";

	private Properties engineProps;
	private Properties inputServiceProps;
	private Properties runtimeServiceProps;
	//private Properties xpathProps;

	private static final Logger LOG = LoggerFactory
			.getLogger(PropertiesLoader.class);

	private static PropertiesLoader loaderInstance = null;

	private PropertiesLoader() {
		loadAllProperties();

	}

	public static PropertiesLoader getInstance() {
		if (loaderInstance == null) {
			loaderInstance = new PropertiesLoader();
		}
		return loaderInstance;
	}

	public String getInputServiceClassName() {
		String name = engineProps.getProperty("inputService");
		if (name == null) {
			throw new PropertiesException(
					"Unable to find property inputService in engine properties");
		}
		return name;
	}

	public String getRuntimeServiceClassName() {
		String name = engineProps.getProperty("runtimeService");
		if (name == null) {
			throw new PropertiesException(
					"Unable to find property runtimeService in engine properties");
		}
		return name;
	}

	public Properties getEngineProperties() {
		return engineProps;
	}

	public Properties getInputServiceProperties() {
		return inputServiceProps;
	}

	public Properties getRuntimeServiceProperties() {
		return runtimeServiceProps;
	}

	/*public Properties getXpathProperties() {
		return xpathProps;
	}*/

	private void loadAllProperties() {
		try {
			engineProps = PropertiesHelper.getProperties(ENGINE_PROPERTY_FILE);
		} catch (IOException e) {
			LOG.error("Error loading engine property file: " + ENGINE_PROPERTY_FILE, e);
			throw new RuntimeException(e);
		}
		try {
			inputServiceProps = PropertiesHelper.getProperties(INPUT_SERVICE_PROPERTY_FILE);
		} catch (IOException e) {
			LOG.error("Error loading input service property file: " + INPUT_SERVICE_PROPERTY_FILE, e);
			throw new RuntimeException(e);
		}
		try {
			runtimeServiceProps = PropertiesHelper.getProperties(RUNTIME_SERVICE_PROPERTY_FILE);
		} catch (IOException e) {
			LOG.error("Error loading runtime service property file: " + RUNTIME_SERVICE_PROPERTY_FILE, e);
			throw new RuntimeException(e);
		}
		//xpathProps = PropertiesHelper.getProperties(XPATH_PROPERTY_FILE);
	}

	private class PropertiesException extends RuntimeException {
		private static final long serialVersionUID = 6239127278298773996L;

		public PropertiesException(String msg) {
			super(msg);
		}
	}
}
