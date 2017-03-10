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

 
package hydrograph.ui.propertywindow.widgets.customwidgets;

import org.slf4j.Logger;

import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory.Listners;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;


/**
 * Widget for showing text box in property window.
 * 
 * @author Bitwise
 */
public class TextBoxWithLabelWidgetWithoutAnyValidation extends TextBoxWithLabelWidget{
	private static final Logger logger = LogFactory.INSTANCE.getLogger(TextBoxWithLabelWidgetWithoutAnyValidation.class);
	/**
	 * Instantiates a new text box widget with provided configurations
	 * 
	 * @param componentConfigProp
	 *            the component configuration property
	 * @param componentMiscProps
	 *            the component miscellaneous properties
	 * @param propDialogButtonBar
	 *            the property dialog button bar
	 */
	public TextBoxWithLabelWidgetWithoutAnyValidation(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
		this.propertyName = componentConfigProp.getPropertyName();
		this.propertyValue = componentConfigProp.getPropertyValue()==null?"":String.valueOf(componentConfigProp.getPropertyValue());
	}

	protected void setToolTipErrorMessage(){
		setToolTipMessage("");
	}
	
	@Override
	protected void attachListeners(AbstractELTWidget textBoxWidget) {
		try {
				textBoxWidget.attachListener(Listners.EVENT_CHANGE.getListener(), propertyDialogButtonBar, null, textBoxWidget.getSWTWidgetControl());
		} catch (Exception exception) {
			logger.error("Failed in attaching listeners to {}, {}", textBoxConfig.getName(), exception);
		}
	}
	
	protected void populateWidget(){
			super.populateWidget();
			txtDecorator.hide();
	}
	
	@Override
	public void refresh(Object value) {
		
		textBox.setText((String)value);
	}

}
