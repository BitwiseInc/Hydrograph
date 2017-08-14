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

 
package hydrograph.ui.propertywindow.propertydialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import hydrograph.ui.common.util.ComponentCacheUtil;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.graph.schema.propagation.SchemaData;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.constants.ELTProperties;
import hydrograph.ui.propertywindow.messagebox.ConfirmCancelMessageBox;
import hydrograph.ui.propertywindow.property.ELTComponenetProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.validators.ComponentValidator;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.interfaces.IOperationClassDialog;
import hydrograph.ui.validators.impl.IValidator;



/**
 * 
 * @author Bitwise
 * Sep 07, 2015
 * 
 */
public class PropertyDialog extends Dialog implements IOperationClassDialog{
	private static final Logger logger = LogFactory.INSTANCE.getLogger(PropertyDialog.class);
	private Composite container;
	private final LinkedHashMap<String, LinkedHashMap<String, ArrayList<Property>>> propertyTree;
	private PropertyDialogBuilder propertyDialogBuilder;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private final String componentName;
	private Button applyButton;
	private boolean propertyChanged=false;	
	private final ELTComponenetProperties componentProperties;
	
	private boolean isPropertyWindowValid;
	
	private Map<String,String> toolTipErrorMessages;
	
	private Component component;
	
	private boolean isCancelButtonPressed = false;
	
	private boolean closeDialog;
	
	private boolean okPressed;
	
	private String selectedTab;
	
	private SchemaData schemaData;
	
	private boolean isCancelPressed;
	private Button okButton;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param propertyTree 
	 * @param component 
	 * @param ComponentProperties 
	 */
	public PropertyDialog(LinkedHashMap<String, LinkedHashMap<String, ArrayList<Property>>> propertyTree,ELTComponenetProperties eltComponenetProperties,Map<String, String> toolTipErrorMessages, Component component) {		
		super(Display.getCurrent().getActiveShell());
		this.propertyTree = propertyTree;
		this.componentProperties = eltComponenetProperties;
		componentName = (String) this.componentProperties.getComponentConfigurationProperty(ELTProperties.NAME_PROPERTY.propertyName());
		this.component = component;
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL | SWT.RESIZE);
		/**
		 * 	Initialize it with true, if any one of the property is invalid then mark this status as false
		 */
		isPropertyWindowValid = true;
		
		this.toolTipErrorMessages = toolTipErrorMessages;
		schemaData = new SchemaData();
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		createPropertyDialogContainer(parent);
		propertyDialogButtonBar = new PropertyDialogButtonBar(container);
		propertyDialogBuilder = new PropertyDialogBuilder(container,propertyTree,componentProperties,propertyDialogButtonBar,component,this);
		propertyDialogBuilder.buildPropertyWindow();
		return container;
	}

	private void createPropertyDialogContainer(Composite parent) {
		container = (Composite) super.createDialogArea(parent);
		setPropertyDialogContainerLayout();
		setPropertyDialogTitle();
	}

	private void setPropertyDialogContainerLayout() {
		container.setLayout(new GridLayout(1, false));
	}

	private void setPropertyDialogTitle() {
		container.getShell().setText(componentName + " - Properties");
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createOKButton(parent);
		Button cancelButton = createCancelButton(parent);
		createApplyButton(parent);		
		attachPropertyDialogButtonBarToEatchWidgetOnPropertyWindow(okButton,
				cancelButton);
	}

	private void attachPropertyDialogButtonBarToEatchWidgetOnPropertyWindow(
			Button okButton, Button cancelButton) {
		propertyDialogButtonBar.setPropertyDialogButtonBar(okButton, applyButton, cancelButton);
	}

	private void createApplyButton(Composite parent) {
		applyButton = createButton(parent, IDialogConstants.NO_ID,
				"Apply", false);
		disableApplyButton();
	}
	

	@Override
	protected void buttonPressed(int buttonId) {
		// If Apply Button pressed(3 is index of apply button);
		if(buttonId == 3){
			applyButton.setFocus();
			applyButtonAction();
		}
		updateComponentValidityStatus();
		super.buttonPressed(buttonId);
	}

	private void updateComponentValidityStatus() {
		String statusString = null;
		if(isPropertyWindowValid){
			statusString = "VALID";
		}
		else{
			statusString = "ERROR";
		}
		componentProperties.getComponentConfigurationProperties().put("validityStatus", statusString);
	}

	private void applyButtonAction() {
		boolean windowValidityStaus = Boolean.TRUE;
		boolean verifiedSchema = Boolean.TRUE;
		for(AbstractWidget customWidget : propertyDialogBuilder.getELTWidgetList()){
			LinkedHashMap<String, Object> properties= customWidget.getProperties();
			if(properties != null){
				
				windowValidityStaus = validateWidget(windowValidityStaus, customWidget,properties);
				if (customWidget instanceof ELTSchemaGridWidget) {
					verifiedSchema = customWidget.verifySchemaFile();
					if (verifiedSchema){
						savePropertiesInComponentModel(customWidget,properties);
					}
				} else{
					savePropertiesInComponentModel(customWidget,properties);
				}
			}
		}
		isPropertyWindowValid = windowValidityStaus;
		
		validateComponentState();
		
		updateComponentValidityStatus();
		
		propertyChanged=true;
		disableApplyButton();
	}

	private void savePropertiesInComponentModel(AbstractWidget eltWidget,LinkedHashMap<String, Object> properties) {
		LinkedHashMap<String, Object> tempPropert = properties;
		LinkedHashMap<String, Object> componentConfigurationProperties = componentProperties.getComponentConfigurationProperties();
		for(String propName : tempPropert.keySet()){
			componentConfigurationProperties.put(propName, tempPropert.get(propName));
		}
	}
	
	private void disableApplyButton() {
		applyButton.setEnabled(false);
	}

	private Button createCancelButton(Composite parent) {
		Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		return cancelButton;
	}

	private Button createOKButton(Composite parent) {
		okButton=createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		return okButton;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		
		return new Point(500,670);
		
	  }
	
	 protected Point getDefaultSize()
	  {
	    return getShell().computeSize(450, 620, true);
	  }
	
	@Override
	protected void okPressed() {
		okButton.setFocus();
		boolean windowValidityStaus = Boolean.TRUE;
		boolean verifiedSchema = Boolean.TRUE;
		boolean isWindowClose = true;
		for (AbstractWidget customWidget : propertyDialogBuilder.getELTWidgetList()) {
			LinkedHashMap<String, Object> properties= customWidget.getProperties();
			if (properties != null) {
				windowValidityStaus = validateWidget(windowValidityStaus, customWidget,properties);
				if (customWidget instanceof ELTSchemaGridWidget) {
					verifiedSchema = customWidget.verifySchemaFile();
					if (verifiedSchema){
						savePropertiesInComponentModel(customWidget,properties);
					}
				} else{
					savePropertiesInComponentModel(customWidget,properties);
				}
				if(isWindowClose)
				isWindowClose = customWidget.canClosePropertyDialog();
			}
		}
		if (applyButton.isEnabled())
			propertyChanged = true;

		isPropertyWindowValid = windowValidityStaus;
		
		validateComponentState();
		
		updateComponentValidityStatus();

		okPressed = true;
		if (verifiedSchema && isWindowClose){
			super.okPressed();
		}
		if(component.isContinuousSchemaPropogationAllow()&& !(component instanceof SubjobComponent))
		component.setContinuousSchemaPropogationAllow(false);	
	}

	
	/**
	 * 
	 * press ok button
	 * 
	 */
	public void pressOK(){
		okPressed();
	}
	
	@Override
	protected void cancelPressed() {
		setCancelPressed(true);
		boolean windowValidityStaus = Boolean.TRUE;
		for (AbstractWidget customWidget : propertyDialogBuilder.getELTWidgetList()) {
			Map<String, Object> properties= customWidget.getEltComponenetProperties().getComponentConfigurationProperties();
			
			if (properties != null) {
				windowValidityStaus = validateWidget(windowValidityStaus, customWidget,properties);
			}
		}
		isPropertyWindowValid = windowValidityStaus;
		validateComponentState();
		updateComponentValidityStatus();
		if (applyButton.isEnabled()) {
			if (!isCancelButtonPressed) {
				ConfirmCancelMessageBox confirmCancelMessageBox = new ConfirmCancelMessageBox(container);
				MessageBox confirmCancleMessagebox = confirmCancelMessageBox.getMessageBox();

				if (confirmCancleMessagebox.open() == SWT.OK) {
					closeDialog = super.close();
				}
			} else {
				closeDialog = super.close();
			}
		} else {
			closeDialog = super.close();
		}
	}

	private void validateComponentState() {
		if(!ComponentValidator.INSTANCE.validate(component)){
			isPropertyWindowValid = false;
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);		
		String imagePath = null;
		//TODO Please uncomment below code before build.
		try{
			newShell.setImage(ImagePathConstant.APP_ICON.getImageFromRegistry());
			if(OSValidator.isMac()){
				newShell.setMinimumSize(new Point(500, 500));
			}else{
				newShell.setMinimumSize(new Point(500, 525));
			}

		}catch(SWTError e){
			logger.debug("Unable to access image" , e);
		}
	}


	/**
	 * 
	 * returns true if user made changes in property window
	 * 
	 * @return boolean
	 */
	public boolean isPropertyChanged(){
		return propertyChanged;
	}

	
	private boolean validateWidget(Boolean windowValidityStaus, AbstractWidget customWidget,Map<String, Object> properties) {
		List<String> validators = ComponentCacheUtil.INSTANCE.getValidatorsForProperty(
				(String) this.componentProperties.getComponentMiscellaneousProperty(Constants.COMPONENT_ORIGINAL_NAME), 
				customWidget.getPropertyName());
		
		IValidator validator = null;
		for (String validatorName : validators) {
			try {
				validator = (IValidator) Class.forName(Constants.VALIDATOR_PACKAGE_PREFIX + validatorName).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				logger.error("Failed to create validator", e);
				throw new RuntimeException("Failed to create validator", e);
			}
			boolean status = validator.validateMap(properties, customWidget.getPropertyName(),schemaData.getInputSchema(component));
			//NOTE : here if any of the property is not valid then whole component is not valid 
			if(status == false){
				windowValidityStaus = Boolean.FALSE;
				logger.debug("{} is not valid", customWidget.getPropertyName());
			}
			appendErrorMessage(customWidget, validator);
		}
		return windowValidityStaus;
	}

	private void appendErrorMessage(AbstractWidget customWidget, IValidator validator) {
		if(toolTipErrorMessages.containsKey(customWidget.getPropertyName())){
			String errorMessage = toolTipErrorMessages.get(customWidget.getPropertyName());
				errorMessage = errorMessage + "\n" + validator.getErrorMessage();
		}else{
			if (!StringUtils.isBlank(validator.getErrorMessage())) {
				toolTipErrorMessages.put(customWidget.getPropertyName(), validator.getErrorMessage());
			}
		}
	}

	@Override
	public void pressCancel() {
		isCancelButtonPressed = true;
		cancelPressed();
	}

	@Override
	public boolean close() {
		if(!okPressed){
			cancelPressed();			
			return closeDialog;
		}else{
			return super.close();
		}		
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public boolean isCancelPressed() {
		return isCancelPressed;
	}

	public void setCancelPressed(boolean isCancelPressed) {
		this.isCancelPressed = isCancelPressed;
	}

	
	
}
