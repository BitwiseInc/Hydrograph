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
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;

/**
 * ExtraURLParameterValidationForDBComponents used to validate DB components widget
 * @author Bitwise
 *
 */
public class ExtraURLParameterValidationForDBComponents implements IELTListener{
	
	private ControlDecoration txtDecorator;
	
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
		}

		Listener listener=new Listener() {
			@Override
			public void handleEvent(Event event) {
				String string = ((Text) widgetList[0]).getText();
				if (StringUtils.isNotBlank(string)) {
					Matcher matchs=Pattern.compile(Constants.DB_REGEX).matcher(string);
				if(matchs.matches() || ParameterUtil.isParameter(string)){
					//txtDecorator.hide();
				/*}else if(ParameterUtil.isParameter(string)){*/
					txtDecorator.hide();
					((Text) widgetList[0]).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
				}else{
					txtDecorator.setDescriptionText(Messages.ADDITIONAL_PARAMETER_ERROR_DECORATOR_MESSAGE);
					txtDecorator.show();
				}
			}else{
				txtDecorator.hide();
				((Text) widgetList[0]).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			}
		}
		};
	return listener;
}

}


