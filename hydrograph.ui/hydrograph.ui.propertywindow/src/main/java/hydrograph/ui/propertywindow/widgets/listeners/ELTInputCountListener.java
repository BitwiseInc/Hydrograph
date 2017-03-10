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
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * The listener interface for receiving ELTInputCount events. The class that is interested in processing a ELTInputCount
 * event implements this interface, and the object created with that class is registered with a component using the
 * component's <code>addELTInputCountListener<code> method. When
 * the ELTInputCount event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTInputCountEvent
 */

public class ELTInputCountListener implements IELTListener{

	private int minimunPortCount;
	private ControlDecoration txtDecorator;
	@Override
	public int getListenerType() {
		return SWT.Modify;
	}

	@Override
	public Listener getListener(final PropertyDialogButtonBar propertyDialogButtonBar,ListenerHelper helper, Widget... widgets) {
		final Widget[] widgetList = widgets;
		if (helper != null) {
			txtDecorator = (ControlDecoration) helper.get(HelperType.CONTROL_DECORATION);
			if (helper.get(HelperType.MINIMUM_PORT_COUNT) != null)
				minimunPortCount = Integer.valueOf((String) helper.get(HelperType.MINIMUM_PORT_COUNT));
		}

		Listener listener=new Listener() {
			@Override
			public void handleEvent(Event event) {
				String string =((Text) widgetList[0]).getText().trim();	
				if(StringUtils.isNotEmpty(string)){
					if(event.type == SWT.Modify){
						if(Integer.parseInt(string) < minimunPortCount || Integer.parseInt(string) > 25 ){
							txtDecorator.setDescriptionText(Messages.bind(Messages.PORT_VALUE, minimunPortCount-1));
							txtDecorator.show();
							propertyDialogButtonBar.enableOKButton(false);
							propertyDialogButtonBar.enableApplyButton(false);
						} else {
							txtDecorator.hide();
							propertyDialogButtonBar.enableOKButton(true);
							propertyDialogButtonBar.enableApplyButton(true);
						}
					}
				}
				else
				{
					propertyDialogButtonBar.enableApplyButton(true);
					txtDecorator.show();
				}
			}
		};
		return listener;
	}
}
