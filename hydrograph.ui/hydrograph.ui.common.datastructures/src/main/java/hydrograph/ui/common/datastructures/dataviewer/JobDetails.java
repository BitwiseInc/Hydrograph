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
package hydrograph.ui.common.datastructures.dataviewer;

/**
 * The Class JobDetails
 * Data structure to hold debug service client information
 * 
 * @author Bitwise
 *
 */
public class JobDetails {
	private String host;
	private String port;
	private String username;
	private String password;
	private String basepath;

	private String uniqueJobID;
	private String componentID;
	private String componentSocketID;

	private boolean isRemote;
	private String jobStatus;

	/**
	 * Instantiates a new job details.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param basepath
	 *            the basepath
	 * @param uniqueJobID
	 *            the unique job ID
	 * @param componentID
	 *            the component ID
	 * @param componentSocketID
	 *            the component socket ID
	 * @param isRemote
	 *            the is remote
	 */
	public JobDetails(String host, String port, String username, String password, String basepath, String uniqueJobID,
			String componentID, String componentSocketID, boolean isRemote, String jobStatus) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.basepath = basepath;
		this.uniqueJobID = uniqueJobID;
		this.componentID = componentID;
		this.componentSocketID = componentSocketID;
		this.isRemote = isRemote;
		this.jobStatus = jobStatus;
	}
	

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}


	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public String getPort() {
		return port;
	}


	/**
	 * Gets the username.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}


	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}


	/**
	 * Gets the basepath.
	 * 
	 * @return the basepath
	 */
	public String getBasepath() {
		return basepath;
	}


	/**
	 * Gets the unique job ID.
	 * 
	 * @return the unique job ID
	 */
	public String getUniqueJobID() {
		return uniqueJobID;
	}


	/**
	 * Gets the component ID.
	 * 
	 * @return the component ID
	 */
	public String getComponentID() {
		return componentID;
	}

	

	public String getJobStatus() {
		return jobStatus;
	}


	/**
	 * @param jobStatus
	 */
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}


	/**
	 * @param componentID
	 */
	public void setComponentID(String componentID) {
		this.componentID = componentID;
	}


	/**
	 * @param componentSocketID
	 */
	public void setComponentSocketID(String componentSocketID) {
		this.componentSocketID = componentSocketID;
	}


	/**
	 * Gets the component socket ID.
	 * 
	 * @return the component socket ID
	 */
	public String getComponentSocketID() {
		return componentSocketID;
	}


	/**
	 * Checks if is remote.
	 * 
	 * @return true, if is remote
	 */
	public boolean isRemote() {
		return isRemote;
	}
	

	/**
	 * @param uniqueJobID
	 */
	public void setUniqueJobID(String uniqueJobID) {
		this.uniqueJobID = uniqueJobID;
	}


	@Override
	public String toString() {
		return "JobDetails [host=" + host + ", port=" + port + ", username=" + username + ", password=" + password
				+ ", basepath=" + basepath + ", uniqueJobID=" + uniqueJobID + ", componentID=" + componentID
				+ ", componentSocketID=" + componentSocketID + ", isRemote=" + isRemote + ", jobStatus=" + jobStatus
				+ "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((basepath == null) ? 0 : basepath.hashCode());
		result = prime * result + ((componentID == null) ? 0 : componentID.hashCode());
		result = prime * result + ((componentSocketID == null) ? 0 : componentSocketID.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + (isRemote ? 1231 : 1237);
		result = prime * result + ((jobStatus == null) ? 0 : jobStatus.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((uniqueJobID == null) ? 0 : uniqueJobID.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		JobDetails other = (JobDetails) obj;
		if (basepath == null) {
			if (other.basepath != null)
				return false;
		} else if (!basepath.equals(other.basepath))
			return false;
		if (componentID == null) {
			if (other.componentID != null)
				return false;
		} else if (!componentID.equals(other.componentID))
			return false;
		if (componentSocketID == null) {
			if (other.componentSocketID != null)
				return false;
		} else if (!componentSocketID.equals(other.componentSocketID))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (isRemote != other.isRemote)
			return false;
		if (jobStatus == null) {
			if (other.jobStatus != null)
				return false;
		} else if (!jobStatus.equals(other.jobStatus))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (uniqueJobID == null) {
			if (other.uniqueJobID != null)
				return false;
		} else if (!uniqueJobID.equals(other.uniqueJobID))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
