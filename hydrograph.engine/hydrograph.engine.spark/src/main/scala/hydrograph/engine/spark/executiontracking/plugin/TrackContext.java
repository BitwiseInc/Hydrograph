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
package hydrograph.engine.spark.executiontracking.plugin;

/**
 * The Class TrackContext.
 *
 * @author Bitwise
 *
 */
public class TrackContext {
	private String fromComponentId;
	private String fromOutSocketId;
	private String fromOutSocketType;
	private String batch;
	private String componentName;

	/**
	 * Method getFromComponentId returns the previous component's id
	 * 
	 * @return fromComponentId
	 */
	public String getFromComponentId() {
		return fromComponentId;
	}

	/**
	 * Method setFromComponentId stores the previous component's id
	 * 
	 * @param fromComponentId
	 *            - previous component's id
	 */
	public void setFromComponentId(String fromComponentId) {
		this.fromComponentId = fromComponentId;
	}

	/**
	 * Method getFromOutSocketId returns the previous components output socket
	 * connected.
	 * 
	 * @return the fromOutSocketId - output socket id of previous component
	 */
	public String getFromOutSocketId() {
		return fromOutSocketId;
	}

	/**
	 * Method setFromOutSocketId stores the output socket of previous component
	 * connected.
	 * 
	 * @param fromOutSocketId
	 *            - output socket id of previous component
	 */
	public void setFromOutSocketId(String fromOutSocketId) {
		this.fromOutSocketId = fromOutSocketId;
	}

	/**
	 * @return the fromOutSocketType
	 */
	public String getFromOutSocketType() {
		return fromOutSocketType;
	}

	/**
	 * @param fromOutSocketType
	 *            fromOutSocketType to set
	 */
	public void setFromOutSocketType(String fromOutSocketType) {
		this.fromOutSocketType = fromOutSocketType;
	}

	/**
	 * @return the batch
	 */
	public String getBatch() {
		return batch;
	}

	/**
	 * @param batch
	 *            batch to set
	 */
	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

}
