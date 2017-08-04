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

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.slf4j.Logger;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;

/**
 * Widget for Port_No in DB Components 
 * @author Bitwise
 *
 */
public class PortWidget extends TextBoxWithLabelWidget{

	private PropertyDialogButtonBar propDialogButtonBar;
	private Cursor cursor;
	private AbstractELTContainerWidget container;
	private static final Logger logger = LogFactory.INSTANCE.getLogger(PortWidget.class);
	
	public PortWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
		this.propDialogButtonBar = propDialogButtonBar;
	}
	
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		this.container=container;
		super.attachToPropertySubGroup(container);
		
	}
	
	@Override
	protected void populateWidget() {
		Utils.INSTANCE.loadProperties();
		cursor = container.getContainerControl().getDisplay().getSystemCursor(SWT.CURSOR_HAND);
		
		logger.trace("Populating {} textbox", textBoxConfig.getName());
		String property = propertyValue;
		if(StringUtils.isNotBlank(property) ){
			textBox.setText(property);
			Utils.INSTANCE.addMouseMoveListener(textBox, cursor);
		}
		else{
			textBox.setText("");
			txtDecorator.show();
		}
	}
}
