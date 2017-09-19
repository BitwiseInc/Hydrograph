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

 
package hydrograph.ui.propertywindow.widgets.listeners;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;


/**
 * The listener interface for receiving ELTSelection events. The class that is interested in processing a ELTSelection
 * event implements this interface, and the object created with that class is registered with a component using the
 * component's <code>addELTSelectionListener<code> method. When
 * the ELTSelection event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTSelectionEvent
 */
public class ELTSelectionListener implements IELTListener {
	private ControlDecoration txtDecorator;
	
	@Override
	public int getListenerType() {

		return SWT.Selection;
	}

	@Override
	public Listener getListener(final PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helper, Widget... widgets) {
		final Widget[] widgetList = widgets;

		if (helper != null) {
			txtDecorator = (ControlDecoration) helper.get(HelperType.CONTROL_DECORATION);
		}

		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (((Combo) widgetList[0]).getText().equals("Parameter") || ((Combo) widgetList[0]).getText().equals("Others")) {
					((Text) widgetList[1]).setVisible(true);
					((Text) widgetList[1]).setFocus();
					txtDecorator.hide();
				} else {
					((Text) widgetList[1]).setVisible(false);
					txtDecorator.hide();
				}
				propertyDialogButtonBar.enableApplyButton(true);
			}
		};
		return listener;
	}
}
