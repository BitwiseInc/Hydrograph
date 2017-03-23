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
package hydrograph.engine.core.component.entity.base;

import java.io.Serializable;
import java.util.Properties;
/**
 * The Class AssemblyEntityBase.
 *
 * @author Bitwise
 *
 */
public class AssemblyEntityBase implements Serializable{

	private String componentId;
	private Properties runtimeProperties;
	private String batch;
	private String componentName;

	/**
	 * @return the componentId
	 */
	public String getComponentId() {
		return componentId;
	}

	/**
	 * @param componentId
	 *            the componentID to set
	 */
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	/**
	 * @return the runtimeProperties
	 */
	public Properties getRuntimeProperties() {
		return runtimeProperties;
	}

	/**
	 * @param runtimeProperties
	 *            the runtimeProperties to set
	 */
	public void setRuntimeProperties(Properties runtimeProperties) {
		this.runtimeProperties = runtimeProperties;
	}

	/**
	 * @return the batch
	 */
	public String getBatch() {
		return batch;
	}

	/**
	 * @param batch
	 *            the batch to set
	 */
	public void setBatch(String batch) {
		this.batch = batch;
	}

	public boolean isOperationPresent() {
		return Boolean.FALSE;
	}

	@Override
	public String toString() {

		return "Component ID: " + componentId + " | batch: " + batch
				+ " | runtime properties: " + runtimeProperties + "\n";
	}

	/**
	 * @return the name
	 */
	public String getComponentName() {
		return componentName;
	}

	/**
	 * @param name
	 * 				the name to set
	 */
	public void setComponentName(String name) {
		this.componentName = name;
	}
}
