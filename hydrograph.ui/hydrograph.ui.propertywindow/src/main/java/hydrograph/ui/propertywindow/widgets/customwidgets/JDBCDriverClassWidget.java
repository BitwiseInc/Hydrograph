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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.DropDownConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultCombo;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

public class JDBCDriverClassWidget extends AbstractWidget{
	private static final Logger logger = LogFactory.INSTANCE.getLogger(UpdateByKeysWidget.class);
	private String propertyName;
	private String propertyValue;
	private Text jdbcDriverClassTextBox;
	private DropDownConfig dropDownConfig;
	private Combo combo;
	private ControlDecoration jdbcDriverClassTextBoxDecorator;
	private Cursor cursor;
	public JDBCDriverClassWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
		this.propertyName = componentConfigProp.getPropertyName();
		this.propertyValue =  (String)componentConfigProp.getPropertyValue();
		
	}
	
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget subGroup) {
		logger.debug("Starting {} button creation");
		
		ELTDefaultSubgroupComposite jdbcDriverClassComposite = new ELTDefaultSubgroupComposite(subGroup.getContainerControl());
		jdbcDriverClassComposite.createContainerWidget();
		
		AbstractELTWidget defaultLabel = new ELTDefaultLable(dropDownConfig.getName());
		jdbcDriverClassComposite.attachWidget(defaultLabel);
		setPropertyHelpWidget((Control) defaultLabel.getSWTWidgetControl());
		
		AbstractELTWidget defaultCombo = new ELTDefaultCombo().defaultText(convertToArray(dropDownConfig.getItems()));
		jdbcDriverClassComposite.attachWidget(defaultCombo);
		combo=(Combo)defaultCombo.getSWTWidgetControl();
		combo.select(0);
		
		ELTDefaultLable dummyLabel = new ELTDefaultLable("");
		jdbcDriverClassComposite.attachWidget(dummyLabel);
		
		
		ELTDefaultLable driverClassLable = new ELTDefaultLable(Messages.LABEL_JDBC_DRIVER_CLASS);
		jdbcDriverClassComposite.attachWidget(driverClassLable);
		setPropertyHelpWidget((Control) driverClassLable.getSWTWidgetControl());
		
		ELTDefaultTextBox textBoxWidget = new ELTDefaultTextBox().grabExcessHorizontalSpace(true).textBoxWidth(120);
		jdbcDriverClassComposite.attachWidget(textBoxWidget);
		jdbcDriverClassTextBox=(Text)textBoxWidget.getSWTWidgetControl();
		jdbcDriverClassTextBoxDecorator = WidgetUtility.addDecorator(jdbcDriverClassTextBox, Messages.bind(Messages.EMPTY_FIELD, Messages.LABEL_JDBC_DRIVER_CLASS));
		jdbcDriverClassTextBoxDecorator.setMarginWidth(3);
		jdbcDriverClassTextBoxDecorator.hide();
		ListenerHelper helper1 = new ListenerHelper();
		helper1.put(HelperType.CONTROL_DECORATION, jdbcDriverClassTextBoxDecorator);

		try {
			textBoxWidget.attachListener(ListenerFactory.Listners.MODIFY.getListener(), propertyDialogButtonBar,
					helper1, textBoxWidget.getSWTWidgetControl());
			textBoxWidget.attachListener(ListenerFactory.Listners.EVENT_CHANGE.getListener(), propertyDialogButtonBar,
					helper1, textBoxWidget.getSWTWidgetControl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		cursor=subGroup.getContainerControl().getDisplay().getSystemCursor(SWT.CURSOR_HAND);
		
		addComboSelectionListner();
		 populateWidget();
		
	}
	
	
	private void populateWidget(){	
		jdbcDriverClassTextBox.setText(propertyValue);
		if(StringUtils.equalsIgnoreCase(propertyValue, Messages.ORACLE_DRIVER_CLASS)){
			combo.select(dropDownConfig.getItems().indexOf(Messages.ORACLE));
		}else if(StringUtils.equalsIgnoreCase(propertyValue, Messages.REDSHIFT_DRIVER_CLASS)){
			combo.select(dropDownConfig.getItems().indexOf(Messages.REDSHIFT));
		}else if(StringUtils.equalsIgnoreCase(propertyValue, Messages.MYSQL_DRIVER_CLASS)){
			combo.select(dropDownConfig.getItems().indexOf(Messages.MYSQL));
		}else if(StringUtils.equalsIgnoreCase(propertyValue, Messages.TERADATA_DRIVER_CLASS)){
			combo.select(dropDownConfig.getItems().indexOf(Messages.TERADATA));
		}else{
			combo.select(dropDownConfig.getItems().indexOf(Messages.OTHERS));
		}
		
//		if(propertyValue.isParameter()){
//			combo.select(dropDownConfig.getItems().indexOf(Constants.PARAMETER));
//		}else{
//			if(dropDownConfig.getItems().contains(propertyValue.getDataBaseValue())){
//				int indexOf = dropDownConfig.getItems().indexOf(propertyValue.getDataBaseValue());
//				combo.select(indexOf);
//			}
//		}
//			jdbcDriverClassTextBox.setText(propertyValue.getJdbcDriverClassValue());
	}
	
	private boolean addComboSelectionListner() {
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String str = ((Combo)event.getSource()).getText();
				if(dropDownConfig.getItems().contains(str)){
					jdbcDriverClassTextBoxDecorator.hide();
					jdbcDriverClassTextBox.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
					if(str.equalsIgnoreCase(Messages.ORACLE)){
						jdbcDriverClassTextBox.setText(Messages.ORACLE_DRIVER_CLASS);
					}else if(str.equalsIgnoreCase(Messages.REDSHIFT)){
						jdbcDriverClassTextBox.setText(Messages.REDSHIFT_DRIVER_CLASS);
					}else if(str.equalsIgnoreCase(Messages.MYSQL)){
						jdbcDriverClassTextBox.setText(Messages.MYSQL_DRIVER_CLASS);
					}else if(str.equalsIgnoreCase(Messages.TERADATA)){
						jdbcDriverClassTextBox.setText(Messages.TERADATA_DRIVER_CLASS);
					}else if(str.equalsIgnoreCase(Messages.OTHERS)){
						jdbcDriverClassTextBox.setText("");
						jdbcDriverClassTextBox.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
						jdbcDriverClassTextBoxDecorator.setMarginWidth(3);
						jdbcDriverClassTextBoxDecorator.show();
					}
				}
			}
			
		});
		return true;
	}
	
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		LinkedHashMap<String, Object> propertymap = new LinkedHashMap<>();
		propertymap.put(this.propertyName, jdbcDriverClassTextBox.getText());
		return propertymap;
	}

	@Override
	public boolean isWidgetValid() {
		if ( StringUtils.isNotBlank(jdbcDriverClassTextBox.getText())){
			return true;
		}
		return false;
	}
	
	
	@Override
	public void addModifyListener(Property property,final ArrayList<AbstractWidget> widgetList) {
	       jdbcDriverClassTextBox.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
				 Utils.INSTANCE.addMouseMoveListener(jdbcDriverClassTextBox, cursor);
				 showHideErrorSymbol(widgetList);
				}
			});
	}
	
	private String[] convertToArray(List<String> items) {
		String[] stringItemsList = new String[items.size()];
		int index = 0;
		for (String item : items) {
			stringItemsList[index++] = item;
		}
		return stringItemsList;
	}
	
	
	@Override
	public void setWidgetConfig(WidgetConfig widgetConfig) {
		this.dropDownConfig = (DropDownConfig) widgetConfig;
	}
	
}
