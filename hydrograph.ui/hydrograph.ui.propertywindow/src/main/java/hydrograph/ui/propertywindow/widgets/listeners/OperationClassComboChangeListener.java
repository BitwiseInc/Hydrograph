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

import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.utility.FilterOperationClassUtility;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;


public class OperationClassComboChangeListener implements IELTListener{
	@Override
	public int getListenerType() {
		
		return SWT.Selection;
	}

	@Override
	public Listener getListener(final PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helpers,
			Widget... widgets) {
		final Widget[] widgetList = widgets;

		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				String comboValue = ((Combo) widgetList[0]).getText();
				propertyDialogButtonBar.enableApplyButton(true);
				if (Messages.CUSTOM.equalsIgnoreCase(comboValue) && !FilterOperationClassUtility.INSTANCE.isCheckBoxSelected()) {
					((Text) widgetList[1]).setText("");
					((Text) widgetList[1]).setEnabled(true);
					FilterOperationClassUtility.INSTANCE.enableAndDisableButtons(true, false);
				} else {
					if(FilterOperationClassUtility.INSTANCE.isCheckBoxSelected())
					{
						MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
						messageBox.setText(Messages.ERROR);
						messageBox.setMessage(Messages.CHECKBOX_DISABLE_MESSAGE);
						if (messageBox.open() == SWT.OK) {
							((Combo) widgetList[0]).setText(Messages.CUSTOM);
						} 
					}
					else
					{
						FilterOperationClassUtility.INSTANCE.setOperationClassNameInTextBox(comboValue, (Text)widgetList[1]);
						((Text) widgetList[1]).setEnabled(false);
						FilterOperationClassUtility.INSTANCE.enableAndDisableButtons(false, false);
						((Button) widgetList[2]).setEnabled(false);
					}
				}
			} 
		};
		return listener;
	}

}
