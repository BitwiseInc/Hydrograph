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

import hydrograph.engine.core.props.OrderedProperties;
import hydrograph.engine.core.utilities.OrderedPropertiesHelper;

import java.io.IOException;

public class ExecutionTrackingUtilities {
	/**
	 * To check execution tracking is enable.
	 *
	 * @param executionTrackingKey
	 * @return an string containing execution tracking class
	 */
	public static String getExecutionTrackingClass(String executionTrackingKey) {
		OrderedProperties properties;
		try {
			properties = OrderedPropertiesHelper.getOrderedProperties("RegisterPlugin.properties");
		} catch (IOException e) {
			throw new RuntimeException("Error reading the properties file: RegisterPlugin.properties" + e);
		}

		return properties.getProperty(executionTrackingKey);
	}

}
