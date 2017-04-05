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

import hydrograph.engine.core.component.entity.base.InputOutputEntityBase;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;

import java.util.Arrays;
import java.util.List;
/**
 * The Class GenerateRecordEntity.
 *
 * @author Bitwise
 *
 */
public class GenerateRecordEntity extends InputOutputEntityBase{

	private Long recordCount;
	private List<OutSocket> outSocketList;
	private List<SchemaField> fieldsList;

	public List<OutSocket> getOutSocketList() {
		return outSocketList;
	}

	public void setOutSocketList(List<OutSocket> outSocketList) {
		this.outSocketList = outSocketList;
	}

	public List<SchemaField> getFieldsList() {
		return fieldsList;
	}

	public void setFieldsList(List<SchemaField> fieldsList) {
		this.fieldsList = fieldsList;
	}

	public Long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Long recordCount) {
		this.recordCount = recordCount;
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
		StringBuilder str = new StringBuilder(
				"Generate record entity information\n");
		str.append(super.toString());
		str.append("Record count: " + recordCount);

		str.append("\n Schema fields: ");
		if (fieldsList != null) {
			str.append(Arrays.toString(fieldsList.toArray()));
		}

		str.append("\nOut socket(s): ");
		if (outSocketList != null) {
			str.append(Arrays.toString(outSocketList.toArray()));
		}
		return str.toString();
	}
}
