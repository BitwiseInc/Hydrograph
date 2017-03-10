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

package hydrograph.ui.dataviewer.datastructures;

import java.util.List;

/**
 * The Class RowData.
 * Provides Table Row representation for views used in View Data functionality.  
 * 
 * @author Bitwise
 *
 */
public class RowData {
	private int rowNumber;
	private List<RowField> rowFields;

	public RowData(List<RowField> rowFields,int rowNumber) {
		super();
		this.rowFields = rowFields;
		this.rowNumber = rowNumber;
	}
	
	/**
	 * 
	 * Get row field list
	 * 
	 * @return list of fields
	 */
	public List<RowField> getRowFields() {
		return rowFields;
	}

	/**
	 * 
	 * Get row number
	 * 
	 * @return row number
	 */
	public int getRowNumber() {
		return rowNumber;
	}

	@Override
	public String toString() {
		return "RowData [rowNumber=" + rowNumber + ", fields=" + rowFields + "]";
	}
	
}
