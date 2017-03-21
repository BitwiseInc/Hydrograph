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

import hydrograph.engine.core.component.entity.base.StraightPullEntityBase;
import hydrograph.engine.core.component.entity.elements.OutSocket;

import java.util.ArrayList;
import java.util.List;
/**
 * The Class UnionAllEntity.
 *
 * @author Bitwise
 *
 */
public class UnionAllEntity extends StraightPullEntityBase {

	public OutSocket getOutSocket() {
		return getOutSocketList().get(0); // UnionAllEntity has provision for
											// just 1 out socket
	}

	public void setOutSocket(OutSocket outSocket) {
		List<OutSocket> listOutSocket = new ArrayList<OutSocket>();
		listOutSocket.add(outSocket);
		setOutSocketList(listOutSocket);
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

		StringBuilder str = new StringBuilder("Union all entity information\n");
		str.append(super.toString());

		str.append("Out socket(s): ");
		if (getOutSocket() != null) {
			str.append(getOutSocket().toString());
		}
		return str.toString();
	}

}