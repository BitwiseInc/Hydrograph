/********************************************************************************
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
 ******************************************************************************/
package hydrograph.server.execution.tracking.server.status.datastructures;

import java.util.List;

/**
 * The Class ExecutionStatus.
 */
public class ExecutionStatus {
	
	/** The component status. */
	private List<ComponentStatus> componentStatus;
	
	/** The job id. */
	private String jobId;
	
	/** The type. */
	private String type;
	
	/** The client id**/
	private String clientId;
	
	/**
	 * Instantiates a new execution status.
	 *
	 * @param componentStatus the component status
	 */
	public ExecutionStatus(List<ComponentStatus> componentStatus) {
		super();
		this.componentStatus = componentStatus;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the component status.
	 *
	 * @return the component status
	 */
	public List<ComponentStatus> getComponentStatus() {
		return componentStatus;
	}

	/**
	 * Sets the component status.
	 *
	 * @param componentStatus the new component status
	 */
	public void setComponentStatus(List<ComponentStatus> componentStatus) {
		this.componentStatus = componentStatus;
	}


	/**
	 * Gets the job id.
	 *
	 * @return the job id
	 */
	public String getJobId() {
		return jobId;
	}

	/**
	 * Sets the job id.
	 *
	 * @param jobId the new job id
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/**
	 * Gets the client id 
	 * @return the client id
	 */
	public String getClientId() {
		return clientId;
	}
    
	/**
	 * Sets the client id
	 * @param clientId
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	@Override
	public String toString() {
		return "ExecutionStatus [componentStatus=" + componentStatus + ", jobId=" + jobId + ", type=" + type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((componentStatus == null) ? 0 : componentStatus.hashCode());
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
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
		ExecutionStatus other = (ExecutionStatus) obj;
		if (componentStatus == null) {
			if (other.componentStatus != null)
				return false;
		} else if (!componentStatus.equals(other.componentStatus))
			return false;
		if (jobId == null) {
			if (other.jobId != null)
				return false;
		} else if (!jobId.equals(other.jobId))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if(clientId==null){
			if(other.clientId!=null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		return true;
	}

	

	

}
