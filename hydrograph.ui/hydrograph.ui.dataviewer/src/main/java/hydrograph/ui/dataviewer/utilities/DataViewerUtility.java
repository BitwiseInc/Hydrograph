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

package hydrograph.ui.dataviewer.utilities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.schema.Field;
import hydrograph.ui.common.schema.Fields;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.dataviewer.actions.ResetSortAction;
import hydrograph.ui.dataviewer.constants.Messages;
import hydrograph.ui.dataviewer.window.DebugDataViewer;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;

/**
 * The Class DataViewerUtility.
 * Data viewer utility class that holds utility methods specific to Data viewer UI.
 * 
 * @author Bitwise
 *
 */
public class DataViewerUtility {
	public final static DataViewerUtility INSTANCE = new DataViewerUtility();
	
	private DataViewerUtility() {
	}
	
	/**
	 * 
	 * Reset sort on data viewer
	 * 
	 */
	public void resetSort(DebugDataViewer debugDataViewer) {
		if (debugDataViewer.getRecentlySortedColumn() != null
				&& !debugDataViewer.getRecentlySortedColumn().isDisposed()) {
			debugDataViewer.getRecentlySortedColumn().setImage(null);
			debugDataViewer.setSortedColumnName(null);
			debugDataViewer.setRecentlySortedColumn(null);
		}
		debugDataViewer.getDataViewLoader().updateDataViewLists();
		debugDataViewer.getDataViewLoader().reloadloadViews();
		debugDataViewer.getActionFactory().getAction(ResetSortAction.class.getName()).setEnabled(false);
	}
	
	/**
	 * Gets the schema.
	 * 
	 * @param csvDebugFileName
	 *            the csv debug file name
	 * @return the schema
	 */
	public List<GridRow> getSchema(String csvDebugFileName) {
		List<GridRow> gridRowList = new ArrayList<>();

		Fields dataViewerFileSchema = ViewDataSchemaHelper.INSTANCE
				.getFieldsFromSchema(csvDebugFileName);
		for (Field field : dataViewerFileSchema.getField()) {
			GridRow gridRow = new GridRow();

			gridRow.setFieldName(field.getName());
			gridRow.setDataType(GridWidgetCommonBuilder
					.getDataTypeByValue(field.getType().value()));
			gridRow.setDataTypeValue(field.getType().value());

			if (StringUtils.isNotEmpty(field.getFormat())) {
				gridRow.setDateFormat(field.getFormat());
			} else {
				gridRow.setDateFormat("");
			}
			if (field.getPrecision() != null) {
				gridRow.setPrecision(String.valueOf(field.getPrecision()));
			} else {
				gridRow.setPrecision("");
			}
			if (field.getScale() != null) {
				gridRow.setScale(Integer.toString(field.getScale()));
			} else {
				gridRow.setScale("");
			}

			if (StringUtils.isNotEmpty(field.getDescription()))
				gridRow.setDescription(field.getDescription());
			else {
				gridRow.setDescription("");
			}
			if (field.getScaleType() != null) {
				gridRow.setScaleType(GridWidgetCommonBuilder
						.getScaleTypeByValue(field.getScaleType().value()));
				gridRow.setScaleTypeValue(GridWidgetCommonBuilder
						.getScaleTypeValue()[GridWidgetCommonBuilder
						.getScaleTypeByValue(field.getScaleType().value())]);
			} else {
				gridRow.setScaleType(GridWidgetCommonBuilder
						.getScaleTypeByValue(Messages.SCALE_TYPE_NONE));
				gridRow.setScaleTypeValue(GridWidgetCommonBuilder
						.getScaleTypeValue()[Integer
						.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX)]);
			}

			gridRowList.add(gridRow);
		}
		return gridRowList;
}
}
