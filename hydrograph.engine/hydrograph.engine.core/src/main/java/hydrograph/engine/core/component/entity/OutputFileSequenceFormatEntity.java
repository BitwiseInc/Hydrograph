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

import java.util.List;
/**
 * The Class OutputFileSequenceFormatEntity.
 *
 * @author Bitwise
 *
 */
public class OutputFileSequenceFormatEntity extends InputOutputEntityBase {

	private String path;
	private List<SchemaField> schemaFieldList;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	public List<SchemaField> getFieldsList() {
		return schemaFieldList;
	}
	
	@Override
	public void setFieldsList(List<SchemaField> schemaFieldList) {
		this.schemaFieldList=schemaFieldList;
	}

}
