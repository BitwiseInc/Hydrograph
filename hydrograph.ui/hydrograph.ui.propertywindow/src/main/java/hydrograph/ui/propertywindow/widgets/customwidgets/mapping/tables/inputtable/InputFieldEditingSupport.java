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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.mapping.tables.inputtable;

import hydrograph.ui.datastructure.property.mapping.InputField;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Text;


public class InputFieldEditingSupport extends EditingSupport{

	private TableViewer viewer;
	private CellEditor cellEditor;
	//public boolean editingsuport = true;
	public InputFieldEditingSupport(TableViewer  viewer) {
		super(viewer);
		this.viewer = viewer;
		//cellEditor = new TextCellEditor(viewer.getTable(), SWT.MULTI | SWT.WRAP | SWT.BORDER);
		cellEditor = new TextCellEditor(viewer.getTable());
		
		final Text aaa = (Text)cellEditor.getControl();
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((InputField)element).getFieldName();
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		((InputField)element).setFieldName((String)userInputValue);
		viewer.update(element, null);
	}
}
