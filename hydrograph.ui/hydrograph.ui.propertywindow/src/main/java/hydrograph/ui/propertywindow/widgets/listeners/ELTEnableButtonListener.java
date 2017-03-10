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


import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;


/**
 * The listener interface for receiving ELTEnableButton events. The class that is interested in processing a
 * ELTEnableButton event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addELTEnableButtonListener<code> method. When
 * the ELTEnableButton event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTEnableButtonEvent
 */
public class ELTEnableButtonListener implements IELTListener{

	@Override
	public int getListenerType() {
		 
		return SWT.Selection;
	}
	@Override
	public Listener getListener(PropertyDialogButtonBar propertyDialogButtonBar,ListenerHelper helpers, Widget... widgets) {
		final Widget[] widgetList = widgets;
				 
		Listener listener=new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (((Button)widgetList[0]).getSelection()) {
					((Button)widgetList[1]).setEnabled(false);
					((Button)widgetList[2]).setEnabled(false);
					((Button)widgetList[3]).setEnabled(false);
				} else {
					((Button)widgetList[1]).setEnabled(true);
					((Button)widgetList[2]).setEnabled(true);
					((Button)widgetList[3]).setEnabled(true);
				}
				}
		};
		return listener;
	}

	
}
