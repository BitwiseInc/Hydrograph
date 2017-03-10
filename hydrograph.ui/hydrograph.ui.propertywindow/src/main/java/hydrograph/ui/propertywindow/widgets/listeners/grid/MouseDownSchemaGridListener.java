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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.MouseActionListener;

public class MouseDownSchemaGridListener extends MouseActionListener{
	@Override
	public int getListenerType() {
		return SWT.MouseDown;
	}
	@Override
	public void mouseAction(PropertyDialogButtonBar propertyDialogButtonBar,
			ListenerHelper helpers, Event event, Widget... widgets) {
		 Table table=(Table)widgets[0];
		 Label label=(Label)event.widget;
		 Event e = new Event();
         e.item = (TableItem) label.getData("_TABLEITEM");
         table.setSelection(new TableItem[] { (TableItem) e.item });
         table.notifyListeners(SWT.Selection, e);
	}

}
