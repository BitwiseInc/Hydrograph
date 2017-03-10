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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;

/**
 * The listener for receiving Focus in events on Delimiter widget.
 * It will not trim the text entered.
 * @author BITWISE 
 */


public class DelimiterFocusInListener implements IELTListener{
	private ControlDecoration txtDecorator;

	@Override
	public int getListenerType() {
		return SWT.FocusIn;
	}

	@Override
	public Listener getListener(PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helper, Widget... widgets) {
		final Widget[] widgetList = widgets;
		if (helper != null){
			txtDecorator = (ControlDecoration) helper.get(HelperType.CONTROL_DECORATION);
		}
		
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				String charSet = ((Text) widgetList[0]).getText();
				if(SWT.FocusIn == event.type) {
						if (StringUtils.isEmpty(charSet)) {
						txtDecorator.show();
						((Text) widgetList[0]).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
						((Text) widgetList[0]).setToolTipText(txtDecorator.getDescriptionText());
					} else {
						txtDecorator.hide();
					}
				}
			}
		};
		return listener;
	}
}
