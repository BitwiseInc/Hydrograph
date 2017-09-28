/********************************************************************************
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
 ******************************************************************************/

package hydrograph.ui.parametergrid.dialog.support;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import hydrograph.ui.parametergrid.constants.MultiParameterFileDialogConstants;
import hydrograph.ui.parametergrid.dialog.MultiParameterFileDialog;
import hydrograph.ui.parametergrid.dialog.models.Parameter;


/**
 * 
 * Cell editor for Parameter Grid
 * 
 * @author Bitwise
 *
 */
public class ParameterEditingSupport extends EditingSupport {
	private final TableViewer viewer;
	private final CellEditor editor;
	private String columnName;
	private MultiParameterFileDialog multiParameterFileDialog;
	
	public ParameterEditingSupport(TableViewer viewer,String columnName,MultiParameterFileDialog multiParameterFileDialog) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new TextCellEditor(viewer.getTable());
		this.columnName = columnName;
		this.multiParameterFileDialog=multiParameterFileDialog;
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
		if(MultiParameterFileDialogConstants.PARAMETER_NAME.equals(columnName))
			return ((Parameter) element).getParameterName();
		else if(MultiParameterFileDialogConstants.PARAMETER_VALUE.equals(columnName))
			return ((Parameter) element).getParameterValue();
		else
			return null;
	}

	@Override
	protected void setValue(Object element, Object userInputValue) {
		
		if(MultiParameterFileDialogConstants.PARAMETER_NAME.equals(columnName)){
			if(!StringUtils.equals(((Parameter) element).getParameterName(),String.valueOf(userInputValue))){
				((Parameter) element).setParameterName(String.valueOf(userInputValue));
				multiParameterFileDialog.getApplyButton().setEnabled(true);
			}
			
		}
		else if(MultiParameterFileDialogConstants.PARAMETER_VALUE.equals(columnName)){
			if(!StringUtils.equals(((Parameter) element).getParameterValue(),String.valueOf(userInputValue))){
				((Parameter) element).setParameterValue(String.valueOf(userInputValue));
				multiParameterFileDialog.getApplyButton().setEnabled(true);
			}
		}
		viewer.update(element, null);
	}
}
