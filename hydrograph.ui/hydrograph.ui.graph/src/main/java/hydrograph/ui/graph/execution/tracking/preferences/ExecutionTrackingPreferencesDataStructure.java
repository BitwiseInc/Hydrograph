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
package hydrograph.ui.graph.execution.tracking.preferences;

/**
 * @author Bitwise
 *This datastructure stores all execution tracking prefernces.
 *
 */
public class ExecutionTrackingPreferencesDataStructure {

	private String localPortNoText;
	private String remotePortNoText;
	private String remoteHostNameText;
	private String trackingLogPathText;
	private boolean enableTrackingCheckBox;
	private boolean overrideRemoteHostButton;
	
	/**
	 * @return Local port number
	 */
	public String getLocalPortNoText() {
		return localPortNoText;
	}

	/**
	 * @param localPortNoText (set value for local port number) 
	 */
	public void setLocalPortNoText(String localPortNoText) {
		this.localPortNoText = localPortNoText;
	}

	/**
	 * @return Remote port number
	 */
	public String getRemotePortNoText() {
		return remotePortNoText;
	}

	/**
	 * @param remotePortNoText
	 */
	public void setRemotePortNoText(String remotePortNoText) {
		this.remotePortNoText = remotePortNoText;
	}

	/**
	 * @return
	 */
	public String getRemoteHostNameText() {
		return remoteHostNameText;
	}

	/**
	 * @param remoteHostNameText (set Remote port number)
	 */
	public void setRemoteHostNameText(String remoteHostNameText) {
		this.remoteHostNameText = remoteHostNameText;
	}

	/**
	 * @return Tracking log path
	 */
	public String getTrackingLogPathText() {
		return trackingLogPathText;
	}

	/**
	 * @param trackingLogPathText (set tracking log path)
	 */
	public void setTrackingLogPathText(String trackingLogPathText) {
		this.trackingLogPathText = trackingLogPathText;
	}

	/**
	 * @return if tracking is enable/disable.
	 */
	public boolean isEnableTrackingCheckBox() {
		return enableTrackingCheckBox;
	}

	/**
	 * @param enableTrackingCheckBox (enable/disable tracking)
	 */
	public void setEnableTrackingCheckBox(boolean enableTrackingCheckBox) {
		this.enableTrackingCheckBox = enableTrackingCheckBox;
	}

	/**
	 * @return if remote host is overriden
	 */
	public boolean isOverrideRemoteHostButton() {
		return overrideRemoteHostButton;
	}

	/**
	 * @param overrideRemoteHostButton (override remote )
	 */
	public void setOverrideRemoteHostButton(boolean overrideRemoteHostButton) {
		this.overrideRemoteHostButton = overrideRemoteHostButton;
	}
	
	
}
