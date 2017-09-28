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
package hydrograph.ui.propertywindow.widgets.customwidgets.excelcomponent;

import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import hydrograph.ui.datastructure.property.ExcelConfigurationDataStructure;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;

/**
 * DataFormattingEditingSupport provides editing support for Data format column
 * @author Bitwise
 *
 */
public class DataFormattingEditingSupport extends EditingSupport{
	
	private CellEditor editor;
	private TableViewer viewer;
	private PropertyDialogButtonBar propDialogButtonBar;
	
	public DataFormattingEditingSupport(TableViewer viewer, PropertyDialogButtonBar propDialogButtonBar) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new HeaderFormatCustomCellEditior(viewer);
		this.propDialogButtonBar = propDialogButtonBar;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		ExcelConfigurationDataStructure excelConfigurationDataStructure = (ExcelConfigurationDataStructure)element;
		if(excelConfigurationDataStructure !=null){
			return excelConfigurationDataStructure.getDataMap();
		}
		return "";
	}

	@Override
	protected void setValue(Object element, Object value) {
		ExcelConfigurationDataStructure excelConfigurationDataStructure = (ExcelConfigurationDataStructure)element;
		excelConfigurationDataStructure.setDataMap((Map<String, String>) value);
		viewer.update(excelConfigurationDataStructure, null);
		propDialogButtonBar.enableApplyButton(true);
	}

}
