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
 * limitations under the License.
 *******************************************************************************/
package hydrograph.ui.graph.execution.tracking.datastructure;



import java.util.Map;

/**
 * The Class ComponentStatus.
 * @author Bitwise
 */
public class ComponentStatus {
	
	/** The component id. */
	private String componentId;  
	
	/** The component name. */
	private String componentName;  
	
	/** The current status. */
	private String currentStatus;
	
	private String batch;
	
	/** The processed record count. */
	private Map<String, Long> processedRecordCount;

	/**
	 * Instantiates a new component status.
	 *
	 * @param componentId the component id
	 * @param componentName the component name
	 * @param currentStatus the current status
	 * @param processedRecordCount the processed record count
	 */
	public ComponentStatus(String componentId, String componentName, String currentStatus,String batch,
			Map<String, Long> processedRecordCount) {
		super();
		this.componentId = componentId;
		this.componentName = componentName;
		this.currentStatus = currentStatus;
		this.batch=batch;
		this.processedRecordCount = processedRecordCount;
	}
	
	

	/**
	 * Gets the component id.
	 *
	 * @return the component id
	 */
	public String getComponentId() {
		return componentId;
	}

	/**
	 * Gets the component name.
	 *
	 * @return the component name
	 */
	public String getComponentName() {
		return componentName;
	}

	/**
	 * Gets the current status.
	 *
	 * @return the current status
	 */
	public String getCurrentStatus() {
		return currentStatus;
	}
	
	

	public String getBatch() {
		return batch;
	}

	/**
	 * Gets the processed record count.
	 *
	 * @return the processed record count
	 */
	public Map<String, Long> getProcessedRecordCount() {
		return processedRecordCount;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batch == null) ? 0 : batch.hashCode());
		result = prime * result + ((componentId == null) ? 0 : componentId.hashCode());
		result = prime * result + ((componentName == null) ? 0 : componentName.hashCode());
		result = prime * result + ((currentStatus == null) ? 0 : currentStatus.hashCode());
		result = prime * result + ((processedRecordCount == null) ? 0 : processedRecordCount.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComponentStatus other = (ComponentStatus) obj;
		if (batch == null) {
			if (other.batch != null)
				return false;
		} else if (!batch.equals(other.batch))
			return false;
		if (componentId == null) {
			if (other.componentId != null)
				return false;
		} else if (!componentId.equals(other.componentId))
			return false;
		if (componentName == null) {
			if (other.componentName != null)
				return false;
		} else if (!componentName.equals(other.componentName))
			return false;
		if (currentStatus == null) {
			if (other.currentStatus != null)
				return false;
		} else if (!currentStatus.equals(other.currentStatus))
			return false;
		if (processedRecordCount == null) {
			if (other.processedRecordCount != null)
				return false;
		} else if (!processedRecordCount.equals(other.processedRecordCount))
			return false;
		return true;
	}

}
