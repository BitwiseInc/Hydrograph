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
package hydrograph.engine.core.component.entity;

import hydrograph.engine.core.component.entity.base.OperationEntityBase;
import hydrograph.engine.core.component.entity.elements.Operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * The Class ExecutionTrackingEntity.
 *
 * @author Bitwise
 *
 */
public class ExecutionTrackingEntity extends OperationEntityBase {

	/*
	 * @param operation the operation to set
	 */
	public void setOperation(Operation operation) {
		List<Operation> operationList = new ArrayList<Operation>();
		operationList.add(operation);
		setOperationsList(operationList);
	}

	/**
	 * @return the operation
	 */
	public Operation getOperation() {
		if(getOperationsList() == null){
			return null;
		}
		return this.getOperationsList().get(0);
	}

	/**
	 * Returns a string with the values for all the members of this entity
	 * object.
	 * <p>
	 * Use cautiously as this is a very heavy operation.
	 * 
	 * @see hydrograph.engine.core.component.entity.base.AssemblyEntityBase#toString()
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("ExecutionTracking entity information\n");
		str.append(super.toString());

		if (isOperationPresent()) {
			if (getOperation() != null) {
				str.append(getNumOperations() + " operation(s) present, Operation info: ");
				str.append(getOperation().toString());
			} else {
				str.append("Operation not present\n");
			}
		} else {
			str.append("Operation not present\n");
		}

		str.append("\nOut socket(s): ");
		if (getOutSocketList() != null) {
			str.append(Arrays.toString(getOutSocketList().toArray()));
		}
		return str.toString();
	}
}
