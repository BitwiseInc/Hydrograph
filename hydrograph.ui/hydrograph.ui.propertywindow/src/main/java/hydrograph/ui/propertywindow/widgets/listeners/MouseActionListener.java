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

public  abstract class MouseActionListener implements IELTListener  {
	@Override
	public int getListenerType() {
		return SWT.MouseHover;
	}

	@Override
	public Listener getListener(
			final PropertyDialogButtonBar propertyDialogButtonBar,
			final ListenerHelper helpers, final Widget... widgets) {
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				mouseAction(propertyDialogButtonBar,helpers,event,widgets);
			}
		};
		return listener;
	}
	public abstract void mouseAction(
			PropertyDialogButtonBar propertyDialogButtonBar,
			final ListenerHelper helpers,Event event, Widget... widgets);	
}
