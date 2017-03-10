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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * The listener interface for receiving JoinInputCountFocusOut events. The class that is interested in processing a JoinInputCountFocusOut
 * event implements this interface, and the object created with that class is registered with a component using the
 * component's <code>addJoinInputCountFocusOutListener<code> method. When
 * the JoinInputCountFocusOut event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see JoinInputCountFocusOutEvent
 */

public class JoinInputCountFocusOutListener implements IELTListener {
	private int minimunPortCount;
	private Component currentComponent;

	@Override
	public int getListenerType() {
		return SWT.FocusOut;
	}

	@Override
	public Listener getListener(PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helpers,
			Widget... widgets) {
		final Widget[] widgetList = widgets;
		if (helpers != null) {
			if (helpers.get(HelperType.MINIMUM_PORT_COUNT) != null){
				minimunPortCount = Integer.valueOf((String) helpers.get(HelperType.MINIMUM_PORT_COUNT));
			}
			currentComponent = (Component) helpers.get(HelperType.CURRENT_COMPONENT);
		}
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.type == SWT.FocusOut) {
					Text textBox = (Text) widgetList[0];
					String textBoxValue = ((Text) event.widget).getText();
					if(StringUtils.isNotBlank(textBoxValue) && StringUtils.isNumeric(textBoxValue)){
						if ((Integer.parseInt(textBoxValue) < minimunPortCount || Integer.parseInt(textBoxValue) > 25)
								&& currentComponent.getProperties().get(Constants.UNUSED_AND_INPUT_PORT_COUNT_PROPERTY) != null){
								textBox.setText((String) currentComponent.getProperties().get(
										Constants.UNUSED_AND_INPUT_PORT_COUNT_PROPERTY));
						}
					}
				}
			}
		};
		return listener;
	}

}
