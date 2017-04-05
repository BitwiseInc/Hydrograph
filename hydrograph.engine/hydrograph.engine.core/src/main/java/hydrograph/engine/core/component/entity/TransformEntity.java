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

import java.util.Arrays;
/**
 * The Class TransformEntity.
 *
 * @author Bitwise
 *
 */
public class TransformEntity extends OperationEntityBase {

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

		StringBuilder str = new StringBuilder("Transform entity information\n");
		str.append(super.toString());

		if (isOperationPresent()) {
			str.append(getNumOperations()
					+ " operation(s) present, Operation info: ");
			if (getOperationsList() != null) {
				str.append(Arrays.toString(getOperationsList().toArray()));
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