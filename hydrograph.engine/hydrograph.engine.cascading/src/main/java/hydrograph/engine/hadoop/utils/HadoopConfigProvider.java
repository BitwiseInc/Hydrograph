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
package hydrograph.engine.hadoop.utils;

import org.apache.hadoop.mapred.JobConf;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class HadoopConfigProvider {

	private Properties props = new Properties();

	public HadoopConfigProvider() {
	}

	public HadoopConfigProvider(Properties updateProps) {
		props.putAll(updateProps);
	}

	public void updateProps(Properties updateProps) {
		props.putAll(updateProps);
	}

	public JobConf getJobConf() {
		JobConf jobConf = new JobConf();
		copyProperties(jobConf, props);
		return jobConf;

	}

	private static void copyProperties(JobConf jobConf,
			Map<Object, Object> properties) {
		if (properties instanceof Properties) {
			Properties props = (Properties) properties;
			Set<String> keys = props.stringPropertyNames();

			for (String key : keys)
				jobConf.set(key, props.getProperty(key));
		} else {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				if (entry.getValue() != null)
					jobConf.set(entry.getKey().toString(), entry.getValue()
							.toString());
			}
		}
	}
}
