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
package hydrograph.ui.datastructure.property;

/**
 * The Class JarInformationDetails.
 * Provides the data structure for Installation Detail Window
 * 
 * @author Bitwise
 */
public class JarInformationDetails {

	private String name;
	private String versionNo;
	private String groupId;
	private String artifactNo;
	private String licenseInfo;
	private String path;

	public String getLicenseInfo() {
		return licenseInfo;
	}

	public void setLicenseInfo(String licenseInfo) {
		this.licenseInfo = licenseInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactNo() {
		return artifactNo;
	}

	public void setArtifactNo(String artifactNo) {
		this.artifactNo = artifactNo;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("JarInformation [ ");
		stringBuilder.append("\nName: ");
		stringBuilder.append(name);
		stringBuilder.append("\nVersion No: ");
		stringBuilder.append(versionNo);
		stringBuilder.append("\nGroup Id: ");
		stringBuilder.append(groupId);
		stringBuilder.append("\nArtifact No: ");
		stringBuilder.append(artifactNo);
		stringBuilder.append("\nLicense Info: ");
		stringBuilder.append(licenseInfo);
		stringBuilder.append(" ]");
		return stringBuilder.toString();
	}
}
