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

 
package hydrograph.ui.engine.ui.repository;

/**
 * @author Bitwise
 *	This class is used to store in-socket details of components present in target XML.
 */
public class InSocketDetail {
	
	private String componentId;
	private String fromComponentId;
	private String fromSocketId;
	private String inSocketType;
	/**
	 * @return String
	 * 				the componentId
	 */			
	public String getComponentId() {
		return componentId;
	}

	/**
	 * @param inSocketId
	 */
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	/**
	 * @return String
	 * 			the fromComponentId
	 */
	public String getFromComponentId() {
		return fromComponentId;
	}

	/**
	 * @param fromComponentId
	 */
	public void setFromComponentId(String fromComponentId) {
		this.fromComponentId = fromComponentId;
	}

	/**
	 * @return String
	 * 		the fromSocketId
	 * 
	 */		
	public String getFromSocketId() {
		return fromSocketId;
	}

	/**
	 * @param fromSocketId
	 */
	public void setFromSocketId(String fromSocketId) {
		this.fromSocketId = fromSocketId;
	}

	
	public String getInSocketType() {
		return inSocketType;
	}

	public void setInSocketType(String inSocketType) {
		this.inSocketType = inSocketType;
	}

	@Override
	public String toString() {
		
		return "ComponentId :"+componentId+
				", From SocketId :"+fromSocketId+
				", From ComponentId :"+fromComponentId;
	}
}
