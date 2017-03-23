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
package hydrograph.engine.core.component.entity.elements;

import java.io.Serializable;
/**
 * The Class InSocket.
 *
 * @author Bitwise
 *
 */
public class InSocket implements Serializable{

	private String fromComponentId;
	private String fromSocketType;
	private String fromSocketId;
	private String inSocketId;
	private String inSocketType;

	public InSocket(String fromComponentId, String fromSocketId,
			String inSocketId) {
		this.fromComponentId = fromComponentId;
		this.fromSocketId = fromSocketId;
		this.inSocketId = inSocketId;
	}

	public String getFromComponentId() {
		return fromComponentId;
	}

	public String getFromSocketType() {
		return fromSocketType;
	}

	public void setFromSocketType(String fromSocketType) {
		this.fromSocketType = fromSocketType;
	}

	public String getFromSocketId() {
		return fromSocketId;
	}

	public String getInSocketId() {
		return inSocketId;
	}

	public String getInSocketType() {
		return inSocketType;
	}

	public void setInSocketType(String inSocketType) {
		this.inSocketType = inSocketType;
	}

	public String toString() {
		StringBuilder str = new StringBuilder("In socket info: ");
		str.append("from component id: " + fromComponentId);
		str.append(" | from socket type: " + fromSocketType);
		str.append(" | from socket id: " + fromSocketId);
		str.append(" | id: " + inSocketId);
		str.append(" | type: " + inSocketType);

		return str.toString();
	}

}
