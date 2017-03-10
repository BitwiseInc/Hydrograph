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

 
package hydrograph.ui.propertywindow.widgets.listeners.grid;

import hydrograph.ui.propertywindow.custom.celleditor.CustomComboBoxCellEditor;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.extended.GridCellEditorListener;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;


/**
 * 
 * @author Bitwise
 * Oct 12, 2015
 * 
 */

public class GridChangeListener {
	private ArrayList<CellEditor> cellEditors;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private GridChangeListener(){
		
	}
	
	/**
	 * Instantiates a new grid change listener.
	 * 
	 * @param cellEditors
	 *            the cell editors
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public GridChangeListener(CellEditor[] cellEditors,PropertyDialogButtonBar propertyDialogButtonBar){
		this.cellEditors = new ArrayList<>();
		this.cellEditors.addAll(Arrays.asList(cellEditors));
		this.propertyDialogButtonBar = propertyDialogButtonBar;
	}
	
	/**
	 * Attach cell change listener.
	 */
	public void attachCellChangeListener(){
		for(CellEditor cellEditor : cellEditors){
			if(cellEditor instanceof TextCellEditor)
				cellEditor.addListener(new GridCellEditorListener(propertyDialogButtonBar));
			else if(cellEditor instanceof CustomComboBoxCellEditor){
				attachComboChangeListener(cellEditor);
			}
		}
	}
	private void attachComboChangeListener(CellEditor cellEditor) {
		((Combo)cellEditor.getControl()).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				propertyDialogButtonBar.enableApplyButton(true);
				super.widgetSelected(e);
			}
			
		});
	}
}
