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
package hydrograph.engine.core.component.entity.base;

import hydrograph.engine.core.component.entity.elements.InSocket;
import hydrograph.engine.core.component.entity.elements.Operation;
import hydrograph.engine.core.component.entity.elements.OutSocket;

import java.util.ArrayList;
import java.util.List;
/**
 * The Class OperationEntityBase.
 *
 * @author Bitwise
 *
 */
public class OperationEntityBase extends AssemblyEntityBase {

	private boolean operationPresent;
	private int numOperations;
	private List<Operation> operationList;
	private List<OutSocket> outSocketList;
	private List<InSocket> inSocketList;

	/**
	 * @return the numOperations
	 */
	
	public int getNumOperations() {
		return numOperations;
	}

	/**
	 * @param numOperations
	 *            the numOperations to set
	 */
	public void setNumOperations(int numOperations) {
		this.numOperations = numOperations;
	}

	/**
	 * @return the operationPresent
	 */
	public boolean isOperationPresent() {
		return operationPresent;
	}

	/**
	 * @param operationPresent
	 *            the operationPresent to set
	 */
	public void setOperationPresent(boolean operationPresent) {
		this.operationPresent = operationPresent;
	}

	/**
	 * Overwrites the list of operations. If operations have been added using
	 * {@link #setOperation(Operation)} method, the changes are overwritten by
	 * this method
	 * 
	 * @param operationList
	 *            The operationList to set
	 */
	public void setOperationsList(List<Operation> operationList) {
		this.operationList = operationList;
		if (operationList != null) {
			operationPresent = true;
			numOperations = operationList.size();
		} else {
			operationPresent = false;
			numOperations = 0;
		}
	}

	/**
	 * @return List<{@link Operation}>
	 */
	public List<Operation> getOperationsList() {
		return this.operationList;
	}

	/**
	 * Adds the operation to the list of operations
	 * 
	 * @param operation
	 *            the operation to add to the list
	 */
	public void setOperation(Operation operation) {
		if (this.operationList == null) {
			this.operationList = new ArrayList<Operation>();
		}
		this.operationList.add(operation);
	}

	/**
	 * @return {@link Operation}
	 */
	public Operation getOperation() {
		return this.operationList.get(0);
	}

	/**
	 * @return the outSocketList
	 */
	public List<OutSocket> getOutSocketList() {
		return outSocketList;
	}

	/**
	 * @param outSocketList
	 *            the outSocketList to set
	 */
	public void setOutSocketList(List<OutSocket> outSocketList) {
		this.outSocketList = outSocketList;
	}
	
	
	/**
	 * @return the inSocketList
	 */
	public List<InSocket> getInSocketList() {
		return inSocketList;
	}

	/**
	 * @param # inSocketList
	 *            the inSocketList to set
	 */
	public void setInSocketList(List<InSocket> outSocketList) {
		this.inSocketList = outSocketList;
	}
	
}
