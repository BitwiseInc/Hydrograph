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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.common.datastructures.tooltip.TootlTipErrorMessage;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;


/**
 * The listener interface for receiving ELTEmptyTextModify events. The class that is interested in processing a
 * ELTEmptyTextModify event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addELTEmptyTextModifyListener<code> method. When
 * the ELTEmptyTextModify event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTEmptyTextModifyEvent
 */
public class ELTEmptyTextModifyListener implements IELTListener {
	private TootlTipErrorMessage tootlTipErrorMessage;

	
	private ControlDecoration fieldNameDecorator;
	@Override
	public int getListenerType() {

		return SWT.Modify;
	}

	@Override
	public Listener getListener(
			PropertyDialogButtonBar propertyDialogButtonBar,
			ListenerHelper helpers, Widget... widgets) {
		
		if (helpers != null) {
			tootlTipErrorMessage = (TootlTipErrorMessage)helpers.get(HelperType.TOOLTIP_ERROR_MESSAGE); 
		}
		
		final Widget[] widgetList = widgets;
		fieldNameDecorator = WidgetUtility
				.addDecorator((Text) widgetList[0],
						Messages.OperationClassBlank);
		
		if(StringUtils.isEmpty(((Text) widgetList[0]).getText())){
			((Button) widgetList[1]).setEnabled(false);
		}
		
		tootlTipErrorMessage.setErrorMessage(Messages.OperationClassBlank);
		//validationStatus.setIsValid(true);
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (!((Button) widgetList[2]).getSelection()) {
					//Text box is empty
					if(StringUtils.isEmpty(((Text) widgetList[0]).getText())){
						((Text) widgetList[0]).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
						//Disable the edit button
						((Button) widgetList[1]).setEnabled(false);
						fieldNameDecorator.show();
						((Text) widgetList[0]).setToolTipText(fieldNameDecorator.getDescriptionText());
					}else{
							((Button) widgetList[1]).setEnabled(true);
							fieldNameDecorator.hide();
						}
					
				}
				else{
					fieldNameDecorator.hide();
				}
				setToolTipErrorMessage(fieldNameDecorator);
			}

		};
		
		return listener;
	}
	
	private void setToolTipErrorMessage(ControlDecoration fieldNameDecorator){
		String errmsg=null;	
		if(fieldNameDecorator.isVisible())
			errmsg = errmsg + "\n" + fieldNameDecorator.getDescriptionText();
		
		tootlTipErrorMessage.setErrorMessage(errmsg);
		
	}
}
