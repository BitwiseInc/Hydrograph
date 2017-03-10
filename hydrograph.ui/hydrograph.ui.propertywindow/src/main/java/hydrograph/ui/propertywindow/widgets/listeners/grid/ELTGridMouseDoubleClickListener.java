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
import hydrograph.ui.propertywindow.widgets.listeners.ELTMouseDoubleClickListener;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Widget;


/**
 * The listener interface for receiving ELTGridMouseDoubleClick events. The class that is interested in processing a
 * ELTGridMouseDoubleClick event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addELTGridMouseDoubleClickListener<code> method. When
 * the ELTGridMouseDoubleClick event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTGridMouseDoubleClickEvent
 */
public class ELTGridMouseDoubleClickListener extends ELTMouseDoubleClickListener{
	@Override
	public void mouseDoubleClickAction(PropertyDialogButtonBar propertyDialogButtonBar,ListenerHelper helpers, Widget... widgets){
		((Button) widgets[1]).setEnabled(true);
		propertyDialogButtonBar.enableApplyButton(true);
		ELTGridDetails gridDetails = (ELTGridDetails) helpers.get(HelperType.SCHEMA_GRID);
		if (gridDetails.getGrids().size() >= 1) {
			((Button) widgets[2]).setEnabled(true);
			((Button) widgets[3]).setEnabled(true);
		}
		gridDetails.getGridWidgetCommonBuilder().createDefaultSchema(gridDetails.getGrids(), gridDetails.getTableViewer(), gridDetails.getLabel());
	}
}
