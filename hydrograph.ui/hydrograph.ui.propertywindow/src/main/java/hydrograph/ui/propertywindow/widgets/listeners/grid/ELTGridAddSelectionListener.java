
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

import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ELTSelectionTaskListener;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;


/**
 * The listener interface for receiving ELTGridAddSelection events. The class that is interested in processing a
 * ELTGridAddSelection event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addELTGridAddSelectionListener<code> method. When
 * the ELTGridAddSelection event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTGridAddSelectionEvent
 */
public class ELTGridAddSelectionListener extends ELTSelectionTaskListener{

	private Table table;
	private int rowSequence=0;
	@Override
	public int getListenerType() {
      return SWT.Selection;
	}
	
	@Override
	public void selectionListenerAction(PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helpers, Widget... widgets) {
		propertyDialogButtonBar.enableApplyButton(true);
		table=(Table)widgets[0];
		table.getParent().getShell().setFocus();
		((Button) widgets[1]).setEnabled(true);
		ELTGridDetails eltGridDetails = (ELTGridDetails)helpers.get(HelperType.SCHEMA_GRID);
		if (eltGridDetails.getGrids().size() >= 1) {
			((Button) widgets[2]).setEnabled(true);
			((Button) widgets[3]).setEnabled(true);
		}
		GridWidgetCommonBuilder gridCommonBuilder = eltGridDetails.getGridWidgetCommonBuilder();
		gridCommonBuilder.setRowSequence(rowSequence);
		gridCommonBuilder.createDefaultSchema(eltGridDetails.getGrids(), eltGridDetails.getTableViewer(), eltGridDetails.getLabel());
		rowSequence++;
		((Component)helpers.get(HelperType.COMPONENT)).setLatestChangesInSchema(true);
	}
}


