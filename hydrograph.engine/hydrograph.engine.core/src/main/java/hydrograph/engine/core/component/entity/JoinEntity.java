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
import hydrograph.engine.core.component.entity.elements.JoinKeyFields;

import java.util.Arrays;
import java.util.List;
/**
 * The Class JoinEntity.
 *
 * @author Bitwise
 *
 */
public class JoinEntity extends OperationEntityBase {

	private List<JoinKeyFields> joinKeyFields;

	/**
	 * @return the joinKeyFields
	 */
	public List<JoinKeyFields> getKeyFields() {
		return joinKeyFields;
	}

	/**
	 * @param joinKeyFields
	 *            the joinKeyFields to set
	 */
	public void setKeyFields(List<JoinKeyFields> joinKeyFields) {
		this.joinKeyFields = joinKeyFields;
	}

	public int getAllKeyFieldSize() {
		// int i = 0;
		return joinKeyFields.size();
		/*
		 * for (JoinKeyFields keyFields2 : joinKeyFields) { i = i +
		 * keyFields2.getFields().size(); }
		 * 
		 * return i;
		 */
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
		StringBuilder str = new StringBuilder("Join entity info:\n");
		str.append(super.toString());

		str.append("key fields: ");
		if (joinKeyFields != null) {
			str.append(Arrays.toString(joinKeyFields.toArray()));
		}

		str.append("\nout socket(s): ");
		if (getOutSocketList() != null) {
			str.append(Arrays.toString(getOutSocketList().toArray()));
		}
		return str.toString();
	}
}