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
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;

/**
 * ExcelFileNameWidget provides Textbox for providing file name of excel sheet
 * @author Bitwise
 *
 */
public class ExcelFileNameWidget extends TextBoxWithLabelWidget{
	
	private Cursor cursor;
	private AbstractELTContainerWidget container;
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ExcelFileNameWidget.class);

	public ExcelFileNameWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
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
		String extension = "";
		logger.trace("Populating {} textbox", textBoxConfig.getName());
		String property = propertyValue;
		if(StringUtils.isNotBlank(property) ){
			int i = property.lastIndexOf('.');
			if (i > 0) {
			    extension = property.substring(i);
			}
			if ((StringUtils.equals(extension, ".xls") || StringUtils.equals(extension,".xlsx")) || ParameterUtil.isParameter(property)){
				txtDecorator.hide();
			}else{
				txtDecorator.show();
			}
			textBox.setText(property);
			Utils.INSTANCE.addMouseMoveListener(textBox, cursor);
		}
		else{
			textBox.setText("");
			txtDecorator.show();
		}
	}
}
