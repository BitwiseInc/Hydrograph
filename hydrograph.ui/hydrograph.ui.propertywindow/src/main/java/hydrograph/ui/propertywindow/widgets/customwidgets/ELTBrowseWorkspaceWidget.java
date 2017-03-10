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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.Extensions;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;



/**
 * The Class ELTBrowseWorkspaceWidget use to open dialog window of current workspace.
 * The dialog window can be use to import and export file with specified extension
 * @author Bitwise
 */
public class ELTBrowseWorkspaceWidget extends AbstractWidget{
	
	/** The sub job path. */
	private Text subJobPath;
	
	/** The properties. */
	private Object properties;
	
	/** The property name. */
	private String propertyName;
	
	/** The txt decorator. */
	private ControlDecoration txtDecorator;
	
	/** The decorator. */
	private ControlDecoration decorator;

	/** The logger. */
	private Logger LOGGER = LogFactory.INSTANCE.getLogger(ELTBrowseWorkspaceWidget.class);
	
	/**
	 * Instantiates a new ELT file path widget.
	 * 
	 * @param componentConfigurationProperty
	 *            the component configuration property
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public ELTBrowseWorkspaceWidget(
			ComponentConfigrationProperty componentConfigurationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigurationProperty, componentMiscellaneousProperties,
				propertyDialogButtonBar);

		this.properties =  componentConfigurationProperty.getPropertyValue();
		this.propertyName = componentConfigurationProperty.getPropertyName();
	}
	

	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget#attachToPropertySubGroup(hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget)
	 */
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(container.getContainerControl());
		eltSuDefaultSubgroupComposite.createContainerWidget();

		AbstractELTWidget eltDefaultLable = new ELTDefaultLable(Messages.SUBJOB_PATH);
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);

		AbstractELTWidget eltDefaultTextBox = new ELTDefaultTextBox().grabExcessHorizontalSpace(true).textBoxWidth(200);
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultTextBox);

		subJobPath = (Text) eltDefaultTextBox.getSWTWidgetControl();
		subJobPath.setToolTipText(Messages.CHARACTERSET);
		decorator = WidgetUtility.addDecorator(subJobPath, Messages.EMPTYFIELDMESSAGE);
		decorator.hide();
		subJobPath.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (subJobPath.getText().isEmpty()) {
					decorator.show();
					subJobPath.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 250, 250, 250));
				} else {
					decorator.hide();
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				decorator.hide();
				subJobPath.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			}
		});

		AbstractELTWidget eltDefaultButton = new ELTDefaultButton(Messages.BROWSE_BUTTON).buttonWidth(35);
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultButton);
		Button browseButton = (Button) eltDefaultButton.getSWTWidgetControl();

		browseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				decorator.hide();
				subJobPath.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// Nothing to Do
			}

		});


		txtDecorator = WidgetUtility.addDecorator(subJobPath, Messages.CHARACTERSET);
		txtDecorator.setMarginWidth(3);
		decorator.setMarginWidth(3);

		txtDecorator.hide();

		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, txtDecorator);
		helper.put(HelperType.FILE_EXTENSION,Extensions.JOB.toString().toLowerCase()); 

		try { 
			eltDefaultTextBox.attachListener(ListenerFactory.Listners.EVENT_CHANGE.getListener(),
					propertyDialogButtonBar, null, eltDefaultTextBox.getSWTWidgetControl());
			eltDefaultTextBox.attachListener(ListenerFactory.Listners.MODIFY.getListener(), propertyDialogButtonBar,
					helper, eltDefaultTextBox.getSWTWidgetControl());
			eltDefaultButton.attachListener(ListenerFactory.Listners.BROWSE_FILE_LISTNER.getListener(),
					propertyDialogButtonBar, helper,subJobPath);

		} catch (Exception e1) {
			LOGGER.error("Fail to attach listener.");
		}
		
		populateWidget();
	}

	/**
	 * Populate widget.
	 */
	private void populateWidget(){		
		String property = (String)properties;
		if(StringUtils.isNotBlank(property)){
			subJobPath.setText(property);	
			decorator.hide();
			txtDecorator.hide();
		}
		else{
			subJobPath.setText("");
			decorator.show();
		}
	}

	/**
	 * Sets the tool tip error message.
	 */
	private void setToolTipErrorMessage(){
		String toolTipErrorMessage = null;
		if(decorator.isVisible()){
			toolTipErrorMessage = decorator.getDescriptionText();
		}
		
		if(txtDecorator.isVisible()){
			toolTipErrorMessage = toolTipErrorMessage + "\n" + txtDecorator.getDescriptionText();
		}
		
		setToolTipMessage(toolTipErrorMessage);
	}
	
	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget#getProperties()
	 */
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		LinkedHashMap<String, Object> property=new LinkedHashMap<>();
		property.put(propertyName, subJobPath.getText());
		setToolTipErrorMessage();
		
		return property;
	}

	
	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget#isWidgetValid()
	 */
	public boolean isWidgetValid() {
		 return validateAgainstValidationRule(subJobPath.getText());
	}


	/**
	 * Gets the text box.
	 *
	 * @return the text box
	 */
	public Text getTextBox() {
		return subJobPath;
	}


	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget#addModifyListener(hydrograph.ui.propertywindow.property.Property, java.util.ArrayList)
	 */
	@Override
	public void addModifyListener(final Property property, final ArrayList<AbstractWidget> widgetList) {
		subJobPath.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				showHideErrorSymbol(widgetList);
			}
		});
	}
}
