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
package hydrograph.ui.graph.job;

/**
 * The job class. The class is a data structure of basic job entity
 * @author Bitwise
 *
 */
public class Job implements Cloneable{
	
	private String localJobID;
	private Process localJobProcess;
	private String remoteJobProcessID;
	private String consoleName;
	private String canvasName;
	private String host;
	private String username;
	private String password;
	private String keyFile;	
	private boolean usePassword;
	private String jobProjectDirectory;
	private String jobStatus;
	private boolean remoteMode;
	private boolean debugMode;
	private String ipAddress;
	private String userId;
	private String basePath;
	private String uniqueJobId;
	private String portNumber;
	private String debugFilePath;
	private boolean isExecutionTrackOn=true;
	
	public Job(String localJobID,String consoleName, String canvasName, String ipAddress,String userId, 
			String basePath, String password) {
		this.localJobID = localJobID;
		this.consoleName = consoleName;
		this.canvasName = canvasName;
		this.ipAddress = ipAddress;
		this.userId = userId;
		this.password = password;
		this.basePath = basePath;
		this.password = password;
		this.debugMode = false;
		this.remoteMode=false;
	}
	
	public boolean isDebugMode() {
		return debugMode;
	}


	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public String getIpAddress() {
		return ipAddress;
	}


	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getBasePath() {
		return basePath;
	}


	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}


	public String getLocalJobID() {
		return localJobID;
	}

	public void setLocalJobID(String localJobID) {
		this.localJobID = localJobID;
	}

	public Process getLocalJobProcess() {
		return localJobProcess;
	}

	public void setLocalJobProcess(Process localJobProcess) {
		this.localJobProcess = localJobProcess;
	}

	public String getRemoteJobProcessID() {
		return remoteJobProcessID;
	}

	public void setRemoteJobProcessID(String remoteJobProcessID) {
		this.remoteJobProcessID = remoteJobProcessID;
	}

	public String getConsoleName() {
		return consoleName;
	}

	public void setConsoleName(String consoleName) {
		this.consoleName = consoleName;
	}

	public String getCanvasName() {
		return canvasName;
	}

	public void setCanvasName(String canvasName) {
		this.canvasName = canvasName;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getJobProjectDirectory() {
		return jobProjectDirectory;
	}

	public void setJobProjectDirectory(String jobProjectDirectory) {
		this.jobProjectDirectory = jobProjectDirectory;
	}
	
	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	public boolean isRemoteMode() {
		return remoteMode;
	}

	public void setRemoteMode(boolean remoteMode) {
		this.remoteMode = remoteMode;
	}

	public String getUniqueJobId() {
		return uniqueJobId;
	}

	public void setUniqueJobId(String uniqueJobId) {
		this.uniqueJobId = uniqueJobId;
	}

	public String getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

	public String getDebugFilePath() {
		return debugFilePath;
	}

	public void setDebugFilePath(String debugFilePath) {
		this.debugFilePath = debugFilePath;
	}


	public boolean isExecutionTrack() {
		return isExecutionTrackOn;
	}

	public void setExecutionTrack(boolean isExecutionTrack) {
		this.isExecutionTrackOn = isExecutionTrack;
	}	
	
	
	/**
	 * @return the keyFile
	 */
	public String getKeyFile() {
		return keyFile;
	}

	/**
	 * @param keyFile the keyFile to set
	 */
	public void setKeyFile(String keyFile) {
		this.keyFile = keyFile;
	}

	/**
	 * @return the usePassword
	 */
	public boolean isUsePassword() {
		return usePassword;
	}

	/**
	 * @param usePassword the usePassword to set
	 */
	public void setUsePassword(boolean usePassword) {
		this.usePassword = usePassword;
	}

	@Override
	public String toString() {
		return "Job [localJobID=" + localJobID + ", localJobProcess=" + localJobProcess + ", remoteJobProcessID="
				+ remoteJobProcessID + ", consoleName=" + consoleName + ", canvasName=" + canvasName + ", host=" + host
				+ ", username=" + username + ", usePassword=" + usePassword + " keyFile=" + keyFile + ", jobProjectDirectory=" + jobProjectDirectory
				+ ", jobStatus=" + jobStatus + ", remoteMode=" + remoteMode + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
