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

package hydrograph.ui.propertywindow.widgets.dialogs.join.support;

import hydrograph.ui.datastructure.property.LookupMapProperty;
import hydrograph.ui.propertywindow.widgets.dialogs.join.utils.JoinMapDialogConstants;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;


/**
 * 
 * Cell editor for Parameter Grid
 * 
 * @author Bitwise
 *
 */
public class JoinMappingEditingSupport extends EditingSupport {
	private final TableViewer viewer;
	private final CellEditor editor;
	private String columnName;
		
	public JoinMappingEditingSupport(TableViewer viewer,String columnName) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(viewer.getTable());
		this.columnName = columnName;
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
		if(JoinMapDialogConstants.INPUT_FIELD.equals(columnName))
			return ((LookupMapProperty) element).getSource_Field();
		else if(JoinMapDialogConstants.OUTPUT_FIELD.equals(columnName))
			return ((LookupMapProperty) element).getOutput_Field();
		else
			return null;
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		
		if(JoinMapDialogConstants.INPUT_FIELD.equals(columnName))
			((LookupMapProperty) element).setSource_Field(String.valueOf(userInputValue));
		else if(JoinMapDialogConstants.OUTPUT_FIELD.equals(columnName))
			((LookupMapProperty) element).setOutput_Field(String.valueOf(userInputValue));
		
		viewer.refresh();
		viewer.update(element, null);
	}
	
	public CellEditor getEditor(){
		return editor;
		
	}
}
