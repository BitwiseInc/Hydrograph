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


import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;


/**
 * The listener interface for receiving ELTCheckFileExtension events. The class that is interested in processing a
 * ELTCheckFileExtension event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addELTCheckFileExtensionListener<code> method. When
 * the ELTCheckFileExtension event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTCheckFileExtensionEvent
 */
public class ELTCheckFileExtensionListener implements IELTListener{

	@Override
	public int getListenerType() {
		
		return SWT.Modify;
	}
	@Override
	public Listener getListener(PropertyDialogButtonBar propertyDialogButtonBar,ListenerHelper helpers, Widget... widgets) {
		final Widget[] widgetList = widgets;
				
		Listener listener=new Listener() { 
			@Override
			public void handleEvent(Event event) {
				if(!((Button)widgetList[1]).getSelection()){
				ControlDecoration	fieldNameMustJava = WidgetUtility.addDecorator((Text)widgetList[0],Messages.INVALID_FILE);
				if(!WidgetUtility.isFileExtention((((Text)widgetList[0]).getText()).trim(), ".java") && !(((Text)widgetList[0]).getText().trim().isEmpty())){
					fieldNameMustJava.show();
				((Text)widgetList[0]).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255,
						255, 204));
				}
					else  
					{   
						((Text)widgetList[0]).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255,
							255, 255));
					fieldNameMustJava.hide(); 
					}
				}
			} 
		};
		return listener;
	}

	
}
