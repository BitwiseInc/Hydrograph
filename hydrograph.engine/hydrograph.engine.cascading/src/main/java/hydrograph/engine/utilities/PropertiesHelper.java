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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHelper {

	private static Logger LOG = LoggerFactory.getLogger(PropertiesHelper.class);

	private PropertiesHelper() {
	}

	public static Properties getProperties(String fileName) {
		Properties properties = new Properties();

		String propFileName;

		propFileName = System.getProperty(fileName);
		if (propFileName == null) {
			propFileName = fileName;
			try {
				properties.load(ClassLoader
						.getSystemResourceAsStream(propFileName));
			} catch (Exception e) {
				throw new UnableToLoadComponentAssemblyMapException(e);
			}
		} else {
			FileReader reader = null;
			try {
				reader = new FileReader(propFileName);
				properties.load(reader);
			} catch (Exception e) {
				LOG.error("", e);
				throw new UnableToLoadComponentAssemblyMapException(e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						LOG.error("", e);
						throw new UnableToLoadComponentAssemblyMapException(e);
					}
				}
			}
		}
		return properties;
	}

	private static class UnableToLoadComponentAssemblyMapException extends
			RuntimeException {

		private static final long serialVersionUID = 8525128253121062929L;

		public UnableToLoadComponentAssemblyMapException(Throwable e) {
			super(e);
		}
	}
}
