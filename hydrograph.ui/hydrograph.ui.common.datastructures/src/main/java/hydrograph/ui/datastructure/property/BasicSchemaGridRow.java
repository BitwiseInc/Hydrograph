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
 * limitations under the License.
 *******************************************************************************/

 
package hydrograph.ui.datastructure.property;

/**
 * The Class BasicSchemaGridRow.
 * 
 * BasicSchemaGridRow is the data-structure used for storing schema of below components
 * 
 * Input Components 	: IFDelimited, IHiveParquet, IHiveTextFile 
 * Output Components 	: OFDelimited, OHiveParquet, OHiveTextFile  
 * Transform Components : Aggregate, Cumulate, Join, Lookup, Transform, Normalize
 * 
 * @author Bitwise
 */
public class BasicSchemaGridRow extends GridRow {

	public BasicSchemaGridRow copy() {
		BasicSchemaGridRow tempschemaGrid = new BasicSchemaGridRow();
		tempschemaGrid.setDataType(getDataType());
		tempschemaGrid.setDateFormat(getDateFormat());
		tempschemaGrid.setFieldName(getFieldName());
		tempschemaGrid.setScale(getScale());
		tempschemaGrid.setDataTypeValue(getDataTypeValue());
		tempschemaGrid.setScaleType(getScaleType());
		tempschemaGrid.setScaleTypeValue(getScaleTypeValue());
		tempschemaGrid.setPrecision(getPrecision());
		tempschemaGrid.setDescription(getDescription());

		return tempschemaGrid;
	}
}