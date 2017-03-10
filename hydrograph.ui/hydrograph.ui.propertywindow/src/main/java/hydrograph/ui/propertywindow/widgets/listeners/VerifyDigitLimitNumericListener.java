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

import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;


/**
 * The listener interface for receiving VerifyTwoDigitNumericListener events. The class that is interested in processing a
 * VerifyTwoDigitNumericListener event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addVerifyTwoDigitNumericListener<code> method. When
 * the VerifyTwoDigitNumericListener event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see VerifyDigitLimitNumericListener
 */
public class VerifyDigitLimitNumericListener  implements IELTListener{

	private ControlDecoration txtDecorator;
	private int characterLimit;
	
	@Override
	public int getListenerType() {
		return SWT.Verify;
	}

	@Override
	public Listener getListener(
			PropertyDialogButtonBar propertyDialogButtonBar,
			ListenerHelper helpers, Widget... widgets) {
		final Widget[] widgetList = widgets;	
		if (helpers != null) {
			txtDecorator = (ControlDecoration) helpers.get(HelperType.CONTROL_DECORATION);
			characterLimit = (int) helpers.get(HelperType.CHARACTER_LIMIT);
		}

		
		Listener listener=new Listener() {
			@Override
			public void handleEvent(Event event) {
				Text text = (Text)widgetList[0];
				text.setTextLimit(characterLimit);
				String string=event.text;
				Matcher matchs=Pattern.compile("[\\d]*").matcher(string);
				if(!matchs.matches()){
					txtDecorator.setDescriptionText(Messages.FIELDBATCH);
					txtDecorator.show();
					event.doit=false;
			}else
				txtDecorator.hide();
			}
		};
	return listener;
	}

}
