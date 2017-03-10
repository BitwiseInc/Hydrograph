/********************************************************************************
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
 ******************************************************************************/

 
package hydrograph.ui.propertywindow.widgets.listeners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;


/**
 * The listener interface for receiving ELTVerifyText events. The class that is interested in processing a ELTVerifyText
 * event implements this interface, and the object created with that class is registered with a component using the
 * component's <code>addELTVerifyTextListener<code> method. When
 * the ELTVerifyText event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTVerifyTextEvent
 */
public class ELTVerifyTextListener implements IELTListener{
	protected String regex =Constants.REGEX;
	protected String errorMessage=Messages.CHARACTERSET;
	private ControlDecoration txtDecorator;
	Logger logger = LogFactory.INSTANCE.getLogger(ELTVerifyTextListener.class);
	@Override
	public int getListenerType() {
		return SWT.Verify;
	}

	@Override
	public Listener getListener(PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helper,  Widget... widgets) {
			if (helper != null) {
				txtDecorator = (ControlDecoration) helper.get(HelperType.CONTROL_DECORATION);
			}
				Listener listener=new Listener() {
				
				@Override
				public void handleEvent(Event event) {
					String string=event.text;
					Matcher matchs=Pattern.compile(regex).matcher(string);
					logger.debug(this+"::ELTVerifyTextListener is called");
					if(!matchs.matches()){
						txtDecorator.setDescriptionText(errorMessage);
						txtDecorator.show();
						event.doit=false;
						logger.trace("Pattern does not matches !matchs.matches() with :" + string);
					}else{
						txtDecorator.hide();
						logger.trace("Pattern matches with :" + string);
					}
				}
			};
		return listener;
	}
}


