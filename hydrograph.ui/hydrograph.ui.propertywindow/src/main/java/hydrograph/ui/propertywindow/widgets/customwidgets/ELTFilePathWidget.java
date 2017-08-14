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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory.Listners;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.FilePathConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;



/**
 * The Class ELTFilePathWidget.
 * 
 * @author Bitwise
 */
public class ELTFilePathWidget extends AbstractWidget{
	
	private Text textBox;
	private Object properties;
	private String propertyName;
	private ControlDecoration txtDecorator;
	private ControlDecoration decorator;
	private Button button;
	private Cursor cursor;
	protected FilePathConfig filepathConfig;
	private Logger LOGGER = LogFactory.INSTANCE.getLogger(ELTFilePathWidget.class);
	/**
	 * Instantiates a new ELT file path widget.
	 * 
	 * @param componentConfigrationProperty
	 *            the component configration property
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public ELTFilePathWidget(
			ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties,
				propertyDialogButtonBar);

		this.properties =  componentConfigrationProperty.getPropertyValue();
		this.propertyName = componentConfigrationProperty.getPropertyName();
	}
	
	public void setWidgetConfig(WidgetConfig widgetConfig) {
		filepathConfig = (FilePathConfig) widgetConfig;
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(container.getContainerControl());
		eltSuDefaultSubgroupComposite.createContainerWidget();

		AbstractELTWidget eltDefaultLable = new ELTDefaultLable(filepathConfig.getLabel());
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);
		setPropertyHelpWidget((Control) eltDefaultLable.getSWTWidgetControl());
		
		AbstractELTWidget eltDefaultTextBox = new ELTDefaultTextBox().grabExcessHorizontalSpace(true).textBoxWidth(200);
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultTextBox);
		
		textBox = (Text) eltDefaultTextBox.getSWTWidgetControl();
		decorator=WidgetUtility.addDecorator(textBox, Messages.EMPTYFIELDMESSAGE);
		
		textBox.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if(textBox.getText().isEmpty() && filepathConfig.isMandatory()){
					decorator.show();
					textBox.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
				}
				else{
					decorator.hide();
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				decorator.hide();
				textBox.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			}
		});
	
		
		AbstractELTWidget eltDefaultButton = new ELTDefaultButton("...").buttonWidth(35);
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultButton);
		button=(Button)eltDefaultButton.getSWTWidgetControl();
		
		button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				decorator.hide();
				textBox.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
			
		
		txtDecorator = WidgetUtility.addDecorator(textBox, Messages.CHARACTERSET);
		txtDecorator.setMarginWidth(3);
		decorator.setMarginWidth(3);
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, txtDecorator);
		helper.put(HelperType.CURRENT_COMPONENT, getComponent());
		helper.put(HelperType.FILE_EXTENSION,filepathConfig.getfileExtension());	
		
		try {
			eltDefaultTextBox.attachListener(ListenerFactory.Listners.EVENT_CHANGE.getListener(),
					propertyDialogButtonBar, null, eltDefaultTextBox.getSWTWidgetControl());
			if (filepathConfig.isMandatory())
				eltDefaultTextBox.attachListener(ListenerFactory.Listners.MODIFY.getListener(), propertyDialogButtonBar,
						helper, eltDefaultTextBox.getSWTWidgetControl());

			if (StringUtils.equalsIgnoreCase(Constants.OUTPUT, getComponent().getCategory())) {
				eltDefaultButton.attachListener(ListenerFactory.Listners.DIRECTORY_DIALOG_SELECTION.getListener(),
						propertyDialogButtonBar, helper, eltDefaultButton.getSWTWidgetControl(),
						eltDefaultTextBox.getSWTWidgetControl());
				eltDefaultTextBox.attachListener(ListenerFactory.Listners.FILE_PATH_MODIFY.getListener(),
						propertyDialogButtonBar, helper, eltDefaultTextBox.getSWTWidgetControl());
			} else {
				eltDefaultButton.attachListener(ListenerFactory.Listners.FILE_DIALOG_SELECTION.getListener(),
						propertyDialogButtonBar, helper, eltDefaultButton.getSWTWidgetControl(),
						eltDefaultTextBox.getSWTWidgetControl());
				// eltDefaultTextBox.attachListener(listenerFactory.getListener("ELTFocusOutListener"),propertyDialogButtonBar, helper,eltDefaultTextBox.getSWTWidgetControl());
					if(filepathConfig.getListeners() != null){
						for(Listners listener : filepathConfig.getListeners()){
							eltDefaultTextBox.attachListener(listener.getListener(),
									propertyDialogButtonBar, helper, eltDefaultTextBox.getSWTWidgetControl());
						}
					}
				}
		} catch (Exception exception) {
			LOGGER.error("Exception occurred while attaching listeners to ELTFileWidget",exception);
		}
		/**
		 *parameter resolution at dev phase 
		 */
		Utils.INSTANCE.loadProperties();
		cursor = container.getContainerControl().getDisplay().getSystemCursor(SWT.CURSOR_HAND);
		
		populateWidget();
	}

	
	private void populateWidget(){		
		String property = (String)properties;
		if(StringUtils.isBlank(property) && filepathConfig.isMandatory()){
			textBox.setText("");
			decorator.show();
			//setToolTipMessage(toolTipErrorMessage)
		}
		else{
			textBox.setText(property);
			decorator.hide();
			txtDecorator.hide();
			Utils.INSTANCE.addMouseMoveListener(textBox, cursor);
		}
	}

	private void setToolTipErrorMessage(){
		String toolTipErrorMessage = null;
		if(decorator.isVisible())
			toolTipErrorMessage = decorator.getDescriptionText();
		
		if(txtDecorator.isVisible())
			toolTipErrorMessage = toolTipErrorMessage + "\n" + txtDecorator.getDescriptionText();
		
		setToolTipMessage(toolTipErrorMessage);
	}
	
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		LinkedHashMap<String, Object> property=new LinkedHashMap<>();
		property.put(propertyName, textBox.getText());
		setToolTipErrorMessage();
		
		return property;
	}

	
	public boolean isWidgetValid() {
		 return validateAgainstValidationRule(textBox.getText());
	}


	public Text getTextBox() {
		return textBox;
	}


	@Override
	public void addModifyListener(final Property property, final ArrayList<AbstractWidget> widgetList) {
		textBox.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Utils.INSTANCE.addMouseMoveListener(textBox, cursor);
				showHideErrorSymbol(widgetList);
			}
		});
	}
	
}
