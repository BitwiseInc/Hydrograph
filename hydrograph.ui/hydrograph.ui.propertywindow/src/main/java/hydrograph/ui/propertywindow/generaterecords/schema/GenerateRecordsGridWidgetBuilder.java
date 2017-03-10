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

 
package hydrograph.ui.propertywindow.generaterecords.schema;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GenerateRecordSchemaGridRow;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;


/**
 * This class creates the grid structure for Generate Records Schema Grid Widget.
 * 
 * @author Bitwise
 *
 */
public class GenerateRecordsGridWidgetBuilder extends GridWidgetCommonBuilder {

	public static GenerateRecordsGridWidgetBuilder INSTANCE = new GenerateRecordsGridWidgetBuilder();
	
	private GenerateRecordsGridWidgetBuilder() {
	}

	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder#createCellEditorList(org.eclipse.swt.widgets.Table, int)
	 */
	public CellEditor[] createCellEditorList(Table table, int size) {
		
		return null;
		
	}

	
	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder#createDefaultSchema(java.util.List, org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Label)
	 */
	@Override
	public void createDefaultSchema(List grids, TableViewer tableViewer, Label errorLabel) {
		int rowSequence = getRowSequence();

		GenerateRecordSchemaGridRow generateRecordSchemaGridRow = new GenerateRecordSchemaGridRow();
		generateRecordSchemaGridRow.setFieldName(Constants.SCHEMA_DEFAULT_FIELD_NAME_SUFFIX+ rowSequence++);
		generateRecordSchemaGridRow.setDateFormat("");
		generateRecordSchemaGridRow.setPrecision("");
		generateRecordSchemaGridRow.setScale("");
		generateRecordSchemaGridRow.setScaleType(Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX));
		generateRecordSchemaGridRow.setScaleTypeValue(getScaleTypeValue()[Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX)]); 
		generateRecordSchemaGridRow.setDataType(Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX));
		generateRecordSchemaGridRow.setDataTypeValue(getDataTypeValue()[Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX)]);
		generateRecordSchemaGridRow.setDescription("");
		generateRecordSchemaGridRow.setLength("");
		generateRecordSchemaGridRow.setRangeFrom("");
		generateRecordSchemaGridRow.setRangeTo("");
		generateRecordSchemaGridRow.setDefaultValue("");

		int numberOfRows = grids.size();
		do {
			if (!grids.contains(generateRecordSchemaGridRow)) {
				grids.add(generateRecordSchemaGridRow);
				tableViewer.setInput(grids);
				tableViewer.refresh();
				tableViewer.editElement(tableViewer.getElementAt(grids.size() == 0 ? grids.size() : grids.size() - 1),
						0);
				break;
			}
			generateRecordSchemaGridRow.setFieldName(Constants.SCHEMA_DEFAULT_FIELD_NAME_SUFFIX + rowSequence++);
			numberOfRows--;
		} while (numberOfRows >= -1);

	}

	@Override
	public CellEditor[] createCellEditorList(Table table,
			Map<String, Integer> columns) {
		CellEditor[] cellEditor = createCellEditor(columns.size());
		addTextEditor(table, cellEditor, columns, (Messages.FIELDNAME));
		addComboBox(table, cellEditor, getDataTypeKey(), 1);
		addTextEditor(table, cellEditor, columns, Messages.DATEFORMAT);
		addTextEditor(table, cellEditor, columns, Messages.PRECISION);
		addTextEditor(table, cellEditor, columns, Messages.SCALE);
		addComboBox(table, cellEditor, getScaleTypeKey(), 5);
		addTextEditor(table, cellEditor, columns, Messages.FIELD_DESCRIPTION);
		addTextEditor(table, cellEditor, columns, Messages.LENGTH);
		addTextEditor(table, cellEditor, columns, Messages.RANGE_FROM);
		addTextEditor(table, cellEditor, columns, Messages.RANGE_TO);
		addTextEditor(table, cellEditor, columns, Messages.DEFAULT_VALUE);
		return cellEditor;
	}
}
