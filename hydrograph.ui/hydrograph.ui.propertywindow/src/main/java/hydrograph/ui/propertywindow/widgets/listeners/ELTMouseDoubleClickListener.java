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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;


// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving ELTMouseDoubleClick events. The class that is interested in processing a
 * ELTMouseDoubleClick event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addELTMouseDoubleClickListener<code> method. When
 * the ELTMouseDoubleClick event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTMouseDoubleClickEvent
 */
public abstract class ELTMouseDoubleClickListener implements IELTListener{

	@Override
	public int getListenerType() {
		// TODO Auto-generated method stub
		return SWT.MouseDoubleClick;
	}

	@Override
	public Listener getListener(
			final PropertyDialogButtonBar propertyDialogButtonBar,
			final ListenerHelper helpers, final Widget... widgets) {
		Listener listener=new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				mouseDoubleClickAction(propertyDialogButtonBar,helpers, widgets);
			}
			};
	return listener;
	}

/**
 * Mouse double click action.
 * 
 * @param propertyDialogButtonBar
 *            the property dialog button bar
 * @param helpers
 *            the helpers
 * @param widgets
 *            the widgets
 */
public abstract void mouseDoubleClickAction(
		PropertyDialogButtonBar propertyDialogButtonBar,
		final ListenerHelper helpers, Widget... widgets);	
}
