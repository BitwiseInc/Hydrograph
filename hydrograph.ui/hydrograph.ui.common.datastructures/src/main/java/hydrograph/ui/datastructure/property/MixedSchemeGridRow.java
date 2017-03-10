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
 * MixedSchemeGridRow is the data-structure used for storing schema of Mixed-Scheme components
 * 
 * @author Bitwise
 *
 */
public class MixedSchemeGridRow extends FixedWidthGridRow{
	//private String delimiter;
	public String getDelimiter() {
		return this.delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MixedSchemeGridRow [length=");
		builder.append(", delimiter=");
		builder.append(delimiter);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
	
	public GridRow copy() {
		MixedSchemeGridRow tempschemeGrid = new MixedSchemeGridRow();
		tempschemeGrid.setDataType(getDataType());
		tempschemeGrid.setDataTypeValue(getDataTypeValue());
		tempschemeGrid.setDateFormat(getDateFormat());
		tempschemeGrid.setPrecision(getPrecision());
		tempschemeGrid.setFieldName(getFieldName());
		tempschemeGrid.setScale(getScale());
		tempschemeGrid.setScaleType(getScaleType());
		tempschemeGrid.setScaleTypeValue(getScaleTypeValue());
		tempschemeGrid.setDescription(getDescription());
		tempschemeGrid.setLength(getLength());
		tempschemeGrid.setDelimiter(delimiter);
		return tempschemeGrid;
	}

}
