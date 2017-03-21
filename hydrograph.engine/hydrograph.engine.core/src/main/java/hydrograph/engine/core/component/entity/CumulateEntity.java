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
import hydrograph.engine.core.component.entity.elements.KeyField;

import java.util.Arrays;
/**
 * The Class CumulateEntity.
 *
 * @author Bitwise
 *
 */
public class CumulateEntity extends OperationEntityBase {

	private KeyField[] keyFields;
	private KeyField[] secondaryKeyFields;

	/**
	 * @return keyFields
	 */
	public KeyField[] getKeyFields() {
		return keyFields != null ? keyFields.clone() : null;
	}

	/**
	 * @param keyFields
	 *            the keyFields to set
	 */
	public void setKeyFields(KeyField[] keyFields) {
		this.keyFields = keyFields != null ? keyFields.clone() : null;
	}

	/**
	 * @return secondaryKeyFields
	 */
	public KeyField[] getSecondaryKeyFields() {
		return secondaryKeyFields != null ? secondaryKeyFields.clone() : null;
	}

	/**
	 * @param secondaryKeyFields
	 *            the secondaryKeyFields to set
	 */
	public void setSecondaryKeyFields(KeyField[] secondaryKeyFields) {
		this.secondaryKeyFields = secondaryKeyFields != null ? secondaryKeyFields.clone() : null;
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
		StringBuilder str = new StringBuilder("Cumulate entity information\n");
		str.append(super.toString());

		str.append("Key fields: ");
		if (keyFields != null) {
			str.append(Arrays.toString(keyFields));
		}

		str.append("\nSecondary key fields: ");
		if (secondaryKeyFields != null) {
			str.append(Arrays.toString(secondaryKeyFields));
		}

		if (isOperationPresent()) {
			str.append("\n" + getNumOperations()
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