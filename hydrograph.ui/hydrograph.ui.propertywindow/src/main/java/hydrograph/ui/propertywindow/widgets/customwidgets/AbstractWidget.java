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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.ComponentCacheUtil;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.schema.propagation.SchemaData;
import hydrograph.ui.propertywindow.handlers.ShowHidePropertyHelpHandler;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.ELTComponenetProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialog;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.validators.impl.IValidator;


/**
 * 
 * @author Bitwise 
 * Sep 08, 2015
 * 
 */

public abstract class AbstractWidget {
	protected ComponentConfigrationProperty componentConfigrationProperty;
	protected ComponentMiscellaneousProperties componentMiscellaneousProperties;
	protected PropertyDialogButtonBar propertyDialogButtonBar;
	protected Text firstTextWidget=null;
	protected WidgetConfig widgetConfig;
	protected ELTComponenetProperties allComponenetProperties;
	private String toolTipErrorMessage =  null;
	private Component component;
	private Schema schemaForInternalPapogation;
	private List<String> operationFieldList;
	protected PropertyDialog propertyDialog;
	private Control propertyHelpWidget;
	private String propertyHelpText;
	private CTabFolder tabFolder; 
	private Property property; 
	private SchemaData schemaData;
	protected static boolean isSchemaUpdated;
	private static final String DATA_KEY = "0";
	private static final String SEPERATOR = "#";
	
	public CTabFolder getTabFolder() {
		return tabFolder;
	}

	public boolean isSchemaUpdated() {
		return isSchemaUpdated;
	}

	public void setSchemaUpdated(boolean isSchemaUpdated1) {
		isSchemaUpdated = isSchemaUpdated1;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public void setTabFolder(CTabFolder tabFolder) {
		this.tabFolder = tabFolder;
	}

	protected Component getComponent() {
		return component;
	}

	/**
	 * 
	 * Set component name
	 * 
	 * @param component
	 */
	public void setComponent(Component component) {
		this.component = component;
	}

	public AbstractWidget() {
		schemaData = new SchemaData();
	}
		
	/**
	 *  get schema for internal propagation
	 *  
	 * @return {@link Schema}
	 */
	public Schema getSchemaForInternalPropagation() {
		return schemaForInternalPapogation;
	}

	/**
	 * Set schema for internal propagation
	 * 
	 * @param schemaForInternalPapogation
	 */
	public void setSchemaForInternalPapogation(Schema schemaForInternalPapogation) {
		this.schemaForInternalPapogation = schemaForInternalPapogation;
	}
	
	/**
	 * 
	 * Get operation field list
	 * 
	 * @return - list of operation fields
	 */
	public List<String> getOperationFieldList() {
		return operationFieldList;
	}

	/**
	 * 
	 * Set operation field list
	 * 
	 * @param operationFieldList
	 */
	public void setOperationFieldList(List<String> operationFieldList) {
		this.operationFieldList = operationFieldList;
	}

	/**
	 * Instantiates a new abstract widget.
	 * 
	 * @param componentConfigrationProperty
	 *            the component configration property
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public AbstractWidget(
			ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		schemaData = new SchemaData();
		this.componentConfigrationProperty = componentConfigrationProperty;
		this.componentMiscellaneousProperties = componentMiscellaneousProperties;
		this.propertyDialogButtonBar = propertyDialogButtonBar;
	}

	/**
	 * Attach to property sub group.
	 * 
	 * @param subGroup
	 *            the sub group
	 */
	public abstract void attachToPropertySubGroup(AbstractELTContainerWidget subGroup);

	/**
	 * 
	 * get component properties
	 * 
	 */
	public abstract LinkedHashMap<String, Object> getProperties();
	
	public abstract boolean isWidgetValid();
		
	public abstract void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList);
	
	public Text getFirstTextWidget(){
		return firstTextWidget;
	}

	/**
	 * 
	 * Get widget configurations 
	 * 
	 * @return {@link WidgetConfig}
	 */
	public WidgetConfig getWidgetConfig() {
		return widgetConfig;
	}

	/**
	 * 
	 * Set widget configurations 
	 * 
	 * @param widgetConfig
	 */
	public void setWidgetConfig(WidgetConfig widgetConfig) {
		this.widgetConfig = widgetConfig;
	}
	
	/**
	 * 
	 * Set tooltip message
	 * 
	 * @param toolTipErrorMessage
	 */
	protected void setToolTipMessage(String toolTipErrorMessage){
		this.toolTipErrorMessage = toolTipErrorMessage;
	}
	
	/**
	 * 
	 * Get tooltip error message
	 * 
	 * @return
	 */
	public String getToolTipErrorMessage(){
		return toolTipErrorMessage ;
	}
	
	/**
	 * 
	 * Get property name
	 * 
	 * @return
	 */
	public String getPropertyName(){
		return componentConfigrationProperty.getPropertyName();
	}
	
	/**
	 * 
	 * get all component properties
	 * 
	 * @return
	 */
	public ELTComponenetProperties getEltComponenetProperties() {
		return allComponenetProperties;
	}

	/**
	 * 
	 * Set all component properties
	 * 
	 * @param eltComponenetProperties
	 */
	public void setEltComponenetProperties(
			ELTComponenetProperties eltComponenetProperties) {
		this.allComponenetProperties = eltComponenetProperties;
	}

	/**
	 * 
	 * Refresh internal schema
	 * 
	 */
	public void refresh(){
				
	}
	
	/**
	 * 
	 * Refresh the widget value.
	 * 
	 */
	public void refresh(Object value){
						
	}
	

	 public void showHideErrorSymbol(boolean isWidgetValid)
	   {
		   if(!isWidgetValid)
		   {
			   for(CTabItem item:getTabFolder().getItems())
				{
					if(StringUtils.equalsIgnoreCase(StringUtils.trim(item.getText()),getPropertyName()))
							{
						    item.setImage(ImagePathConstant.COMPONENT_ERROR_ICON.getImageFromRegistry());
							}			
				}	
		   }
		   else
		   {
			   for(CTabItem item:getTabFolder().getItems())
				{
					if(StringUtils.equalsIgnoreCase(StringUtils.trim(item.getText()),getPropertyName()))
							{
						    item.setImage(null);
							}			
				}	
		   }
	   }
	
	
	
	/**
	 * 
	 * Set property dialog
	 * 
	 * @param {@link PropertyDialog}
	 */
	public void setPropertyDialog(PropertyDialog propertyDialog) {
		this.propertyDialog = propertyDialog;
	}
	
	/**
	 * 
	 * Set the widget to which you want to attach property help
	 * 
	 * @param propertyHelpWidget
	 */
	protected void setPropertyHelpWidget(Control propertyHelpWidget){
		this.propertyHelpWidget = propertyHelpWidget;
	}

	/**
	 * 
	 * Set property help text
	 * 
	 * @param propertyHelpText
	 */
	public void setPropertyHelpText(String propertyHelpText) {
		this.propertyHelpText = propertyHelpText;
	}

	/**
	 * 
	 * Attach help to property help widget
	 * 
	 */
	public void setPropertyHelp() {

		if (null != propertyHelpWidget && null != this.propertyHelpWidget.getData(DATA_KEY)) {
			String array[] = propertyHelpText.split(SEPERATOR);
			
			for(int index=0;index<=array.length-1;index++){
				Control propertyHelpWidget = (Control)this.propertyHelpWidget.getData(String.valueOf(index));
			   if(null!=propertyHelpWidget  && null!= ShowHidePropertyHelpHandler.getInstance() && ShowHidePropertyHelpHandler.getInstance().isShowHidePropertyHelpChecked()){
				   propertyHelpWidget.setToolTipText(array[index]);
				   propertyHelpWidget.setCursor(new Cursor(propertyHelpWidget.getDisplay(), SWT.CURSOR_HELP));		
				}
			}
		}else if (ShowHidePropertyHelpHandler.getInstance() != null && propertyHelpText != null && propertyHelpWidget != null
				&& ShowHidePropertyHelpHandler.getInstance().isShowHidePropertyHelpChecked()){
			propertyHelpWidget.setToolTipText(propertyHelpText);
			propertyHelpWidget.setCursor(new Cursor(propertyHelpWidget.getDisplay(), SWT.CURSOR_HELP));
		}
			
		
	}
	
	public boolean verifySchemaFile(){
		return true;
	}
	
	/**
	 * @return
	 */
	public boolean canClosePropertyDialog(){
		return true;
	}
	
	public void showHideErrorSymbol(List<AbstractWidget> widgetList)
	{
		boolean isErrorPresent=false;
		 for(AbstractWidget abstractWidget:widgetList)	
	 	 {
		 if(StringUtils.equals(abstractWidget.getProperty().getPropertyGroup(), property.getPropertyGroup()) &&!(abstractWidget.isWidgetValid()))
	 	  {
	 			isErrorPresent=true;
	 			break;
	 		}	
	 	 }	
	 	if(isErrorPresent)
	 	{
	 	for(CTabItem item:getTabFolder().getItems())
		{
			if(StringUtils.equalsIgnoreCase(StringUtils.trim(item.getText()),property.getPropertyGroup()))
					{
				    item.setImage(ImagePathConstant.COMPONENT_ERROR_ICON.getImageFromRegistry());
					}			
		}	
	 	}
	 	else
	 	{
	 		for(CTabItem item:getTabFolder().getItems())
			{
				if(StringUtils.equalsIgnoreCase(StringUtils.trim(item.getText()),property.getPropertyGroup()))
						{
					    item.setImage(null);
						}			
			}	
	 	}
	}
	public boolean validateAgainstValidationRule(Object object){
		boolean isComponentValid=true;
		ComponentCacheUtil.INSTANCE.getProperties(getComponent().getComponentName());
		List<String> validators = ComponentCacheUtil.INSTANCE.getValidatorsForProperty(
							getComponent().getComponentName(),getPropertyName());
			IValidator validator = null;
			for (String validatorName : validators) {
				try {
					validator = (IValidator) Class.forName(Constants.VALIDATOR_PACKAGE_PREFIX
									+ validatorName).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					throw new RuntimeException("Failed to create validator", e);
				}
				isComponentValid=validator.validate(object,
						getPropertyName(),schemaData.getInputSchema(getComponent()),
						false);
				if(!isComponentValid)
				break;	
			}
		return isComponentValid;
	}
	
	
}
