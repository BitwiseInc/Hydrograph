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

 
package hydrograph.ui.propertywindow.widgets.listeners.extended;

import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;

import org.eclipse.jface.viewers.ICellEditorListener;


// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Oct 12, 2015
 * 
 */

public class GridCellEditorListener implements ICellEditorListener{

	private PropertyDialogButtonBar propertyDialogButtonBar;
	
	/**
	 * Instantiates a new grid cell editor listener.
	 * 
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public GridCellEditorListener(PropertyDialogButtonBar propertyDialogButtonBar){
		this.propertyDialogButtonBar = propertyDialogButtonBar;
	}
	
	@Override
	public void applyEditorValue() {
		// TODO Auto-generated method stub
	}

	@Override
	public void cancelEditor() {
		// TODO Auto-generated method stub
	}

	@Override
	public void editorValueChanged(boolean oldValidState, boolean newValidState) {
		propertyDialogButtonBar.enableApplyButton(true);	
		
	}

}
