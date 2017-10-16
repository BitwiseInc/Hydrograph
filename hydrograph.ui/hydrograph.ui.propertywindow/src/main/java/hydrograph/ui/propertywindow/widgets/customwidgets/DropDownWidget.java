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
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.datastructures.ComboBoxParameter;
import hydrograph.ui.propertywindow.factory.ListenerFactory.Listners;
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
import hydrograph.ui.propertywindow.widgets.listeners.IELTListener;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;


/**
 * Widget for showing Drop-down in property window.
 * 
 * @author Bitwise
 */
public class DropDownWidget extends AbstractWidget{
	private static final Logger logger = LogFactory.INSTANCE.getLogger(DropDownWidget.class);
	
	private Combo combo;
	private Text text;
	private LinkedHashMap<String, Object> property=new LinkedHashMap<>();
	private String propertyName;
	private String properties;
	private ComboBoxParameter comboBoxParameter=new ComboBoxParameter();
	private ControlDecoration txtDecorator;
	private DropDownConfig dropDownConfig;
	private List<AbstractWidget> widgetList;

	private Cursor cursor;
	
	/**
	 * Instantiates a new ELT safe widget.
	 * 
	 * @param componentConfigProp
	 *            the component configuration property
	 * @param componentMiscProps
	 *            the component miscellaneous properties
	 * @param propDialogButtonBar
	 *            the property dialog button bar
	 */
	public DropDownWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);

		this.propertyName = componentConfigProp.getPropertyName();
		if(componentConfigProp.getPropertyValue() instanceof String){
			this.properties =  (String) componentConfigProp.getPropertyValue(); 
		}else if(componentConfigProp.getPropertyValue() instanceof Boolean){
			boolean value = (boolean) componentConfigProp.getPropertyValue();
			this.properties =  value+""; 
		}
		
	}
	
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(container.getContainerControl());
		eltSuDefaultSubgroupComposite.createContainerWidget();
		
		Utils.INSTANCE.loadProperties();
		this.cursor = container.getContainerControl().getDisplay().getSystemCursor(SWT.CURSOR_HAND);
		AbstractELTWidget defaultLabel = new ELTDefaultLable(dropDownConfig.getName());
		eltSuDefaultSubgroupComposite.attachWidget(defaultLabel);
		setPropertyHelpWidget((Control) defaultLabel.getSWTWidgetControl());
		
		AbstractELTWidget defaultCombo = new ELTDefaultCombo().defaultText(convertToArray(dropDownConfig.getItems()));
		eltSuDefaultSubgroupComposite.attachWidget(defaultCombo);
		combo=(Combo)defaultCombo.getSWTWidgetControl();
		combo.select(0);
		
		if(!StringUtils.equalsIgnoreCase(propertyName, "failOnError")){
			ELTDefaultTextBox eltDefaultTextBox = new ELTDefaultTextBox().grabExcessHorizontalSpace(true);
			eltSuDefaultSubgroupComposite.attachWidget(eltDefaultTextBox);
			eltDefaultTextBox.visibility(false);
			text=(Text)eltDefaultTextBox.getSWTWidgetControl();
			
			txtDecorator = WidgetUtility.addDecorator(text, Messages.bind(Messages.EMPTY_FIELD, dropDownConfig.getName()));
			
			ListenerHelper helper = new ListenerHelper();
			helper.put(HelperType.CONTROL_DECORATION, txtDecorator);
			
				try {
					for (Listners listenerNameConstant : dropDownConfig.getDropDownListeners()) {
						IELTListener listener = listenerNameConstant.getListener();
						defaultCombo.attachListener(listener,propertyDialogButtonBar, helper,defaultCombo.getSWTWidgetControl(),
								eltDefaultTextBox.getSWTWidgetControl());
					}
					for (Listners listenerNameConstant : dropDownConfig.getTextBoxListeners()) {
						IELTListener listener = listenerNameConstant.getListener();
						eltDefaultTextBox.attachListener(listener, propertyDialogButtonBar, helper,eltDefaultTextBox.getSWTWidgetControl());
					}
				} catch (Exception exception) {
					logger.error("Failed in attaching listeners to {}, {}", dropDownConfig.getName(), exception);
				}
		}
		addComboSelectionListner();
		 populateWidget();
	}
	
	private boolean addComboSelectionListner() {
		
		combo.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				showHideErrorSymbol(widgetList);
				propertyDialogButtonBar.enableApplyButton(true);
			}
			
		});
		return true;
	}
	
	private void populateWidget(){	
		if(StringUtils.isNotBlank(properties)){
			if(dropDownConfig.getItems().contains(properties)){
				int indexOf = dropDownConfig.getItems().indexOf(properties);
				combo.select(indexOf);
			}
			else{
				combo.select(dropDownConfig.getItems().size() - 1);
				if(text != null){
					text.setVisible(true);
					if (StringUtils.isNotEmpty(properties)){
						text.setText(properties);
						Utils.INSTANCE.addMouseMoveListener(text, cursor);
						txtDecorator.hide();
					}
					else{
						text.setText("");
						txtDecorator.show();
					}
				}
			}
		}
	}

	private void setToolTipErrorMessage(){
		String toolTipErrorMessage = null;
		if(txtDecorator !=null){
			if(txtDecorator.isVisible())
				toolTipErrorMessage = txtDecorator.getDescriptionText();
		}
						
		setToolTipMessage(toolTipErrorMessage);
	}
	
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		if( combo.getText().equalsIgnoreCase(Constants.PARAMETER)){
			comboBoxParameter.setOption(text.getText());
			comboBoxParameter.setOptionValue(text.getText());
		}else{
			comboBoxParameter.setOption(combo.getText());
		}
		property.put(propertyName,comboBoxParameter.getOption());
		setToolTipErrorMessage();
		return property;
	}

	@Override
	public void setWidgetConfig(WidgetConfig widgetConfig) {
		this.dropDownConfig = (DropDownConfig) widgetConfig;
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
	public boolean isWidgetValid() {
			if(StringUtils.equals(combo.getText(), Constants.PARAMETER))
			{	
			 return validateAgainstValidationRule(text.getText());
			}
			return true;
	}

	
   @Override
	public void addModifyListener(Property property, final ArrayList<AbstractWidget> widgetList) {
	   this.widgetList = widgetList;
	   if(text !=null){
		   text.addModifyListener(new ModifyListener() {
			   @Override
			   public void modifyText(ModifyEvent e) {
				   showHideErrorSymbol(widgetList);
				   Utils.INSTANCE.addMouseMoveListener(text, cursor);
			   }
		   });
	   }
		
	}


}
