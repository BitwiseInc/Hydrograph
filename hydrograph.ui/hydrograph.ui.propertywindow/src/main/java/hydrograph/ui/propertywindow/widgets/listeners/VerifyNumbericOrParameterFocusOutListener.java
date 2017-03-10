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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;


/**
 * Listener for text box which will either accept numeric value or parameter.
 * if the value is parameter, then it will add @{} after the focus is out.
 */
public class VerifyNumbericOrParameterFocusOutListener implements IELTListener{

	@Override
	public int getListenerType() {
		return SWT.FocusOut;
	}

	@Override
	public Listener getListener(PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helper,  Widget... widgets) {
		final Widget[] widgetList = widgets;
			Listener listener=new Listener() {
				@Override
				public void handleEvent(Event event) {
					String string=((Text) widgetList[0]).getText().trim();
					Matcher matcher=Pattern.compile("[\\d]*").matcher(string);
					if(!matcher.matches() && StringUtils.isNotBlank(string)){
						((Text) widgetList[0]).setText(string.replace("@{", "").replace("}", ""));
						((Text) widgetList[0]).setText("@{"+((Text) widgetList[0]).getText()+"}");
						((Text) widgetList[0]).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
					}
				}
			};
		return listener;
	}
}