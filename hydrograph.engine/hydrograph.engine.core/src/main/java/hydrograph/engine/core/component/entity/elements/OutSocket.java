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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Class OutSocket.
 *
 * @author Bitwise
 *
 */
public class OutSocket implements Serializable {

	private String socketId;
	private String socketType = "out";
	private List<MapField> mapFieldsList = new ArrayList<MapField>();
	private List<OperationField> operationFieldsList = new ArrayList<OperationField>();
	private String copyOfInSocketId;
	private List<PassThroughField> passThroughFieldsList = new ArrayList<PassThroughField>();

	/**
	 * @param socketId
	 */
	public OutSocket(String socketId) {
		this.socketId = socketId;
	}

	/**
	 * @param socketId
	 * @param socketType
	 */
	public OutSocket(String socketId, String socketType) {
		this.socketId = socketId;
		this.socketType = socketType;
	}

	public String getCopyOfInSocketId() {
		return copyOfInSocketId;
	}

	public void setCopyOfInSocketId(String copyOfInSocketId) {
		this.copyOfInSocketId = copyOfInSocketId;
	}

	/**
	 * @return mapFieldsList
	 */
	public List<MapField> getMapFieldsList() {
		return mapFieldsList;
	}

	/**
	 * @param mapFieldsList
	 */
	public void setMapFieldsList(List<MapField> mapFieldsList) {
		this.mapFieldsList = mapFieldsList;
	}

	/**
	 * @return passThroughFieldsList
	 */
	public List<PassThroughField> getPassThroughFieldsList() {
		return passThroughFieldsList;
	}

	/**
	 * @param passThroughFieldsList
	 */
	public void setPassThroughFieldsList(
			List<PassThroughField> passThroughFieldsList) {
		this.passThroughFieldsList = passThroughFieldsList;
	}

	/**
	 * @return the socketId
	 */
	public String getSocketId() {
		return socketId;
	}

	/**
	 * @return the socketType
	 */
	public String getSocketType() {
		return socketType;
	}

	/**
	 * @param socketType
	 *            the socketType to set
	 */
	public void setSocketType(String socketType) {
		this.socketType = socketType;
	}

	/**
	 * @return the operationField
	 */
	public List<OperationField> getOperationFieldList() {
		return operationFieldsList;
	}

	/**
	 * @param operationFieldsList
	 *            the operationField to set
	 */
	public void setOperationFieldList(List<OperationField> operationFieldsList) {
		this.operationFieldsList = operationFieldsList;
	}

	public List<String> getAllOutputFields() {
		List<String> outFields = new ArrayList<String>();

		for (PassThroughField passThrough : getPassThroughFieldsList()) {
			outFields.add(passThrough.getName());
		}

		for (MapField mapfield : getMapFieldsList()) {
			outFields.add(mapfield.getName());
		}

		for (OperationField operation : getOperationFieldList()) {
			outFields.add(operation.getName());
		}

		return outFields;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder str = new StringBuilder();
		str.append("socket id: " + socketId + " | socket type: " + socketType
				+ " | ");

		str.append("map fields: ");
		if (mapFieldsList != null) {
			str.append(Arrays.toString(mapFieldsList.toArray()));
		}

		str.append(" | operation fields: ");
		if (operationFieldsList != null) {
			str.append(Arrays.toString(operationFieldsList.toArray()));
		}

		str.append(" | pass through fields: ");
		if (passThroughFieldsList != null) {
			str.append(Arrays.toString(passThroughFieldsList.toArray()));
		}

		str.append(" | copy of in socket: " + copyOfInSocketId);

		return str.toString();
	}
}
