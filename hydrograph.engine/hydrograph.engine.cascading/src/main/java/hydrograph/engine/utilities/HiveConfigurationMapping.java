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
package hydrograph.engine.utilities;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class HiveConfigurationMapping {

	private static final String HIVE_CONF_MAP_FILE = "HiveConfigurationMapping";
	private static ResourceBundle RESOURCE_BUNDLE = null;

	private HiveConfigurationMapping() {
	}

	public static String getHiveConf(String key) {
		if (getResourceBundle() == null) {
			throw new UnableToLoadHiveConfMapFileException(
					"Hive Configuration mapping file not found");
		} else {
			try {

				return getResourceBundle().getString(key);
			} catch (MissingResourceException e) {
				throw new UnableToFindHiveConfPathException(
						key
								+ " Configuration path not present in hive conf mapping file ");
			}
		}
	}

	private static ResourceBundle getResourceBundle() {
		String propFileName;
		propFileName = System.getProperty(HIVE_CONF_MAP_FILE);
		if (propFileName == null)
			try {
				propFileName = HIVE_CONF_MAP_FILE;
				RESOURCE_BUNDLE = ResourceBundle.getBundle(propFileName);
			} catch (MissingResourceException u) {
				return null;
			}
		return RESOURCE_BUNDLE;
	}

	private static class UnableToFindHiveConfPathException extends
			RuntimeException {

		private static final long serialVersionUID = 712295301265415676L;

		private UnableToFindHiveConfPathException(String string) {

			super(string);
		}

	}

	private static class UnableToLoadHiveConfMapFileException extends
			RuntimeException {

		private static final long serialVersionUID = -8797637642021524755L;

		private UnableToLoadHiveConfMapFileException(String string) {

			super(string);
		}
	}

}
