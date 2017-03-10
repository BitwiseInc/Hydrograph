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

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;


/**
 * The listener interface for receiving ELTVerifyComponentName events. The class that is interested in processing a
 * ELTVerifyComponentName event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addELTVerifyComponentNameListener<code> method. When
 * the ELTVerifyComponentName event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTVerifyComponentNameEvent
 */
public class ELTVerifyComponentNameListener implements IELTListener {
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ELTVerifyComponentNameListener.class);
	private ArrayList<String> names;
	private String oldName;
	private Component currentComponent;
	private ControlDecoration txtDecorator;
	
	@Override
	public int getListenerType() {
		return SWT.Verify;
	}

	@Override
	public Listener getListener(final PropertyDialogButtonBar propertyDialogButtonBar, ListenerHelper helpers,  Widget... widgets) {
		Widget[] widgetList = widgets;
		final Text text = (Text) widgetList[0];
		
		if(helpers != null){
			if (helpers != null) {
				currentComponent=(Component) helpers.get(HelperType.CURRENT_COMPONENT);
				txtDecorator = (ControlDecoration) helpers.get(HelperType.CONTROL_DECORATION);
				txtDecorator.hide();
			}
		}
		

		Listener listener = new Listener() {

			@Override
			public void handleEvent(Event e) {
				if (e.type == SWT.Verify) {
					logger.debug("<<<<<<<<<<"+e.text.toString()+">>>>>>>>>>>");
					String currentText = ((Text) e.widget).getText();
					String newName = (currentText.substring(0, e.start) + e.text + currentText.substring(e.end)).trim();
					Matcher matchName = Pattern.compile("[\\w+]*").matcher(newName.replaceAll("[\\W&&[\\ \\.\\-]]*", ""));
					logger.debug("new text: {}", newName);
					if (newName == null || newName.equals("")) {
						// e.doit=false;
						text.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
						text.setToolTipText(Messages.FIELD_LABEL_ERROR);
						propertyDialogButtonBar.enableOKButton(false);
						propertyDialogButtonBar.enableApplyButton(false);
						txtDecorator.setDescriptionText(Messages.FIELD_LABEL_ERROR);
						txtDecorator.show();
					}else if(!matchName.matches()){
						text.setToolTipText(Messages.INVALID_CHARACTERS);
						txtDecorator.setDescriptionText(Messages.INVALID_CHARACTERS);
						txtDecorator.show();
						e.doit=false;
					} else if(!newName.equalsIgnoreCase(oldName) && !isUniqueCompName(newName)) {
						text.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry(255,255,204));
						text.setToolTipText(Messages.FIELD_LABEL_ERROR);
						propertyDialogButtonBar.enableOKButton(false);
						propertyDialogButtonBar.enableApplyButton(false);
						txtDecorator.setDescriptionText(Messages.FIELD_LABEL_ERROR);
						txtDecorator.show();
					}
					else{
						text.setBackground(null);
						text.setToolTipText("");
						text.setMessage("");
						propertyDialogButtonBar.enableOKButton(true);
						propertyDialogButtonBar.enableApplyButton(true);
						txtDecorator.hide();
					}
				}
			}
		};
		return listener;
	}

	public ArrayList<String> getNames() {
		return names;
	}

	public void setNames(ArrayList<String> names) {
		this.names = names;
	}
	
	private boolean isUniqueCompName(String componentName) {
		componentName = componentName.trim();
		boolean result = true;

		for (Component component : currentComponent.getParent().getUIComponentList()) {
			if (component.getComponentLabel()!=null && StringUtils.equalsIgnoreCase(component.getComponentLabel().getLabelContents(), componentName)) {
				result = false;
				break;
			}
	}
		logger.debug("result: {}", result);
		return result;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
}
