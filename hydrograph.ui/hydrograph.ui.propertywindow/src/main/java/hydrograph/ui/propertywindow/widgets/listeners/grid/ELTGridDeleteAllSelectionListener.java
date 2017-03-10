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

import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ELTSelectionTaskListener;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;


/**
 * The listener interface for receiving ELTGridDeleteAllSelection events. The class that is interested in processing a
 * ELTGridDeleteAllSelection event implements this interface, and the object created with that class is registered with
 * a component using the component's <code>addELTGridDeleteAllSelectionListener<code> method. When
 * the ELTGridDeleteAllSelection event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTGridDeleteAllSelectionEvent
 */
public class ELTGridDeleteAllSelectionListener extends ELTSelectionTaskListener{

	@Override
	public void selectionListenerAction(PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helpers, Widget... widgets) {
		propertyDialogButtonBar.enableApplyButton(true);
		Table table = (Table) widgets[0];
		boolean userAns = WidgetUtility.eltConfirmMessage("Do you really want to delete all properties?");
		if (userAns) {
			ELTGridDetails gridDetails = (ELTGridDetails) helpers.get(HelperType.SCHEMA_GRID);
			gridDetails.getGrids().removeAll(gridDetails.getGrids());
			gridDetails.getTableViewer().refresh();
		}
	}
}
