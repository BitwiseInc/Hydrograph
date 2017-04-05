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
import hydrograph.engine.core.component.entity.elements.SchemaField;

import java.util.Arrays;
import java.util.List;
/**
 * The Class OutputFileParquetEntity.
 *
 * @author Bitwise
 *
 */
public class OutputFileParquetEntity extends InputOutputEntityBase {

	private String path;
	private List<SchemaField> schemaFieldList;
	private boolean overWrite;

	public boolean isOverWrite() {
		return overWrite;
	}

	public void setOverWrite(boolean overWrite) {
		this.overWrite = overWrite;
	}

	public List<SchemaField> getFieldsList() {
		return schemaFieldList;
	}

	public void setFieldsList(List<SchemaField> schemaFieldsList) {
		this.schemaFieldList = schemaFieldsList;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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
		StringBuilder str = new StringBuilder("Output file parquet entity info:\n");
		str.append(super.toString());
		str.append("Path: " + path);

		str.append("\nfields: ");
		if (schemaFieldList != null) {
			str.append(Arrays.toString(schemaFieldList.toArray()));
		}
		return str.toString();
	}
}