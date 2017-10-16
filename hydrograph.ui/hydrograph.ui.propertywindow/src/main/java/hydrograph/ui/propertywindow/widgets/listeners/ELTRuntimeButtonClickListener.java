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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.propertywindow.ftp.AuthenticationWidget;
import hydrograph.ui.propertywindow.ftp.OperationConfigWidget;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents.InputAdditionalParametersWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents.LoadTypeConfigurationWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents.OutputAdditionalParametersWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.excelcomponent.ExcelFormattingWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.runtimeproperty.ELTRuntimePropertiesWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.secondarykeys.SecondaryColumnKeysWidget;


/**
 * The listener interface for receiving ELTRuntimeButtonClick events. The class that is interested in processing a
 * ELTRuntimeButtonClick event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addELTRuntimeButtonClickListener<code> method. When
 * the ELTRuntimeButtonClick event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTRuntimeButtonClickEvent
 */
public class ELTRuntimeButtonClickListener implements IELTListener {

	@Override
	public int getListenerType() {
		return SWT.Selection;
	}

	@Override
	public Listener getListener(PropertyDialogButtonBar propertyDialogButtonBar, final ListenerHelper helpers, Widget... widgets) {
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWT.Selection) {
					if(helpers.object instanceof ELTRuntimePropertiesWidget)
						((ELTRuntimePropertiesWidget)helpers.object).newWindowLauncher();
					else if(helpers.object instanceof SecondaryColumnKeysWidget) 
						((SecondaryColumnKeysWidget)helpers.object).newWindowLauncher();
					else if(helpers.object instanceof LoadTypeConfigurationWidget) 
						((LoadTypeConfigurationWidget)helpers.object).newWindowLauncher();
					else if(helpers.object instanceof InputAdditionalParametersWidget) 
						((InputAdditionalParametersWidget)helpers.object).newWindowLauncher();
					else if(helpers.object instanceof OutputAdditionalParametersWidget) 
						((OutputAdditionalParametersWidget)helpers.object).newWindowLauncher();
					else if(helpers.object instanceof AuthenticationWidget)
						((AuthenticationWidget)helpers.object).newWindowLauncher();
					else if(helpers.object instanceof OperationConfigWidget)
						((OperationConfigWidget)helpers.object).newWindowLauncher();
					else if(helpers.object instanceof ExcelFormattingWidget) 
						((ExcelFormattingWidget)helpers.object).newWindowLauncher();
				}

			}
		};

		return listener;
	}
}
