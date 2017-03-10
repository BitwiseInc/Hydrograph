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

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
/***
 * VerifyCharacterLimitListener class verifies the character limit provided for values
 * @author Bitwise
 *
 */
public class VerifyCharacterLimitListener implements IELTListener{
	
	private ControlDecoration txtDecorator;
	private int characterLimit;

	@Override
	public int getListenerType() {
		return SWT.Modify;
	}

	@Override
	public Listener getListener(PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helpers,
			Widget... widgets) {
		final Widget[] widgetList = widgets;	
		if (helpers != null) {
			txtDecorator = (ControlDecoration) helpers.get(HelperType.CONTROL_DECORATION);
			characterLimit = (int) helpers.get(HelperType.CHARACTER_LIMIT);
		}

		
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Text text = (Text) widgetList[0];
				text.setTextLimit(characterLimit);
				String value = ((Text) widgets[0]).getText().trim();
				Matcher matchs = Pattern.compile(Constants.PARAMETER_REGEX).matcher(value);
				if ((!matchs.matches())) {
					txtDecorator.setDescriptionText(Messages.FIELDCHARACTER);
					txtDecorator.show();
					event.doit = false;
				} else if (matchs.matches()) {
					txtDecorator.hide();
				}
			}
		};
	return listener;

}
}
