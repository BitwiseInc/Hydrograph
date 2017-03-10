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
import org.slf4j.Logger;

import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;

/**
 * Widget for accepting delimiter in property window.
 * 
 * @author Bitwise
 */

public class DelimiterWidget extends TextBoxWithLabelWidget{

	private static final Logger logger = LogFactory.INSTANCE.getLogger(DelimiterWidget.class);
	
	public DelimiterWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps,
			PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
		
	}

	protected void populateWidget(){
		logger.debug("Populating {} textbox", textBoxConfig.getName());
		String property = propertyValue;
		if(!StringUtils.isEmpty(property) ){
			textBox.setText(property);
			txtDecorator.hide();
		}
		else{
			textBox.setText("");
			txtDecorator.show();
		}
	}
	
	@Override
	public void refresh(Object value) {
		
		textBox.setText((String)value);
	}
}
