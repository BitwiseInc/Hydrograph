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

package hydrograph.ui.dataviewer.support;

import hydrograph.ui.dataviewer.datastructures.RowData;
import hydrograph.ui.dataviewer.datastructures.RowField;

import java.util.Comparator;


/**
 * The Class TypeBasedComparator.
 * Provides mechanism for Type specific Comparator
 * @author Bitwise
 *
 */
public class TypeBasedComparator implements Comparator<Object> {
	
	private SortOrder sortType;
	private int columnToSort;
	private SortDataType sortDataType;
	private String dateFormat;
	
	public TypeBasedComparator(SortOrder sortType, int columnToSort, SortDataType sortDataType, String dateFormat) {
		this.sortType = sortType;
		this.columnToSort = columnToSort;
		this.sortDataType = sortDataType;
		this.dateFormat = dateFormat;
	}

	@Override
	public int compare(Object row1, Object row2) {
		
		RowField rowField1 = ((RowField)((RowData)row1).getRowFields().get(columnToSort));
		RowField rowField2 = ((RowField)((RowData)row2).getRowFields().get(columnToSort));
		
		switch (sortType) {
		case ASC:
			return sortDataType.compareTo(rowField1.getValue(), rowField2.getValue(), dateFormat);
		case DSC:
			return sortDataType.compareTo(rowField2.getValue(), rowField1.getValue(), dateFormat);
		default:
			return sortDataType.compareTo(rowField1.getValue(), rowField2.getValue(), dateFormat);
		}
	}
}