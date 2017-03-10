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
 * The listener interface for receiving ELTMouseDown events. The class that is interested in processing a ELTMouseDown
 * event implements this interface, and the object created with that class is registered with a component using the
 * component's <code>addELTMouseDownListener<code> method. When
 * the ELTMouseDown event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTMouseDownEvent
 */
public abstract class ELTMouseDownListener implements IELTListener{

	@Override
	public int getListenerType() {
		// TODO Auto-generated method stub
		return SWT.MouseDown;
	}

	@Override
	public Listener getListener(
			final PropertyDialogButtonBar propertyDialogButtonBar,
			final ListenerHelper helpers, final Widget... widgets) {
		Listener listener=new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				mouseDownAction(propertyDialogButtonBar,helpers, widgets);
			}
			};
	return listener;
	}
	
	/**
	 * Mouse down action.
	 * 
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 * @param helpers
	 *            the helpers
	 * @param widgets
	 *            the widgets
	 */
	public abstract void mouseDownAction(
			PropertyDialogButtonBar propertyDialogButtonBar,
			final ListenerHelper helpers, Widget... widgets);	
}
