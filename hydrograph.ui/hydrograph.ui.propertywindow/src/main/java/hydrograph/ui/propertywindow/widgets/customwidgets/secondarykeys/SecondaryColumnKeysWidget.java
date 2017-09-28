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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.secondarykeys;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.InputField;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.schema.propagation.helper.SchemaPropagationHelper;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.EditButtonWithLabelConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.operational.TransformWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;


/**
 * The Class ELTRuntimePropertiesWidget.
 * 
 * @author Bitwise
 */
public class SecondaryColumnKeysWidget extends AbstractWidget {

	private LinkedHashMap<String, String> InstializeMap;
	private String propertyName;
	private Shell shell;
	private ArrayList<AbstractWidget> widgets;
	private Logger logger = LogFactory.INSTANCE.getLogger(SecondaryColumnKeysWidget.class);
	private EditButtonWithLabelConfig buttonWithLabelConfig;
	private LinkedHashMap<String, Object> tempPropertyMap;

	/**
	 * Instantiates a new ELT runtime properties widget.
	 * 
	 * @param componentConfigrationProperty
	 *            the component configration property
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	@SuppressWarnings("unchecked")
	public SecondaryColumnKeysWidget(ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties, propertyDialogButtonBar);

		this.propertyName = componentConfigrationProperty.getPropertyName();
		this.InstializeMap = (LinkedHashMap<String, String>) componentConfigrationProperty.getPropertyValue();

		tempPropertyMap = new LinkedHashMap<String, Object>();
	}

	public void setWidgetConfig(WidgetConfig widgetConfig) {
		buttonWithLabelConfig = (EditButtonWithLabelConfig) widgetConfig;
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {

		ELTDefaultSubgroupComposite runtimeComposite = new ELTDefaultSubgroupComposite(container.getContainerControl());
		runtimeComposite.createContainerWidget();
		shell = runtimeComposite.getContainerControl().getShell();

		ELTDefaultLable defaultLable1 = new ELTDefaultLable(buttonWithLabelConfig.getName());
		runtimeComposite.attachWidget(defaultLable1);
		setPropertyHelpWidget((Control) defaultLable1.getSWTWidgetControl());
		
		ELTDefaultButton eltDefaultButton = new ELTDefaultButton("Edit");
		if(OSValidator.isMac()){
			eltDefaultButton.buttonWidth(120);
		}

		runtimeComposite.attachWidget(eltDefaultButton);

		try {
			eltDefaultButton.attachListener(ListenerFactory.Listners.RUNTIME_BUTTON_CLICK.getListener(),
					propertyDialogButtonBar, new ListenerHelper(this.getClass().getName(), this),
					eltDefaultButton.getSWTWidgetControl());

		} catch (Exception e1) {
          logger.error("Failed to attach listener",e1);
		}

	}

	/**
	 * Sets the properties.
	 * 
	 * @param propertyName
	 *            the property name
	 * @param properties
	 *            the properties
	 */
	public void setProperties(String propertyName, Object properties) {
		this.propertyName = propertyName;
		this.InstializeMap = (LinkedHashMap<String, String>) properties;
	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {

		tempPropertyMap.put(this.propertyName, this.InstializeMap);
		return (tempPropertyMap);
	}

	/**
	 * New window launcher.
	 */
	public void newWindowLauncher() {
		
		SecondaryColumnKeysDialog secondaryColumnDialog = new SecondaryColumnKeysDialog(shell, propertyDialogButtonBar, buttonWithLabelConfig);
		if (getProperties().get(propertyName) == null) {
			setProperties(propertyName, new LinkedHashMap<String, String>());

		}
		secondaryColumnDialog.setSourceFieldsFromPropagatedSchema(getPropagatedSchema());
		secondaryColumnDialog.setSecondaryColumnsMap(new LinkedHashMap<String,String>(InstializeMap));
		
		secondaryColumnDialog.open();
		
		setProperties(propertyName, secondaryColumnDialog.getSecondaryColumnsMap());
        showHideErrorSymbol(widgets);
	}

	private List<String> getPropagatedSchema() {
		List<String> propogatedFields=new ArrayList<>();
		if(StringUtils.equalsIgnoreCase(getComponent().getComponentName(),Constants.AGGREGATE)
				 ||StringUtils.equalsIgnoreCase(getComponent().getComponentName(),Constants.CUMULATE)
				 || StringUtils.equalsIgnoreCase(getComponent().getComponentName(),Constants.GROUP_COMBINE) )
				{
					TransformWidget transformWidget = null;
					for(AbstractWidget abstractWidget:widgets)
					{
						if(abstractWidget instanceof TransformWidget)
						{
							transformWidget=(TransformWidget)abstractWidget;
							break;
						}
					}		
					
			if (transformWidget != null) {
				TransformMapping transformMapping = (TransformMapping) transformWidget.getProperties()
						.get(Constants.OPERATION);
				for (InputField inputField : transformMapping.getInputFields()) {
					propogatedFields.add(inputField.getFieldName());
				}
			}
			    return propogatedFields;
				}
		else if(StringUtils.equalsIgnoreCase(getComponent().getComponentName(),Constants.FILTER)
				||StringUtils.equalsIgnoreCase(getComponent().getCategory(),Constants.STRAIGHTPULL))
		{	
			ELTSchemaGridWidget  schemaWidget = null;
			for(AbstractWidget abstractWidget:widgets)
			{
				if(abstractWidget instanceof ELTSchemaGridWidget)
				{
					schemaWidget=(ELTSchemaGridWidget)abstractWidget;
					break;
				}
			}
			if (schemaWidget != null) {
				schemaWidget.refresh();
				List<GridRow> gridRowList = (List<GridRow>) schemaWidget.getTableViewer().getInput();
				for (GridRow gridRow : gridRowList) {
					propogatedFields.add(gridRow.getFieldName());
				}
			}
			return propogatedFields;
		}else if(StringUtils.equalsIgnoreCase(getComponent().getComponentName(),Constants.OUTPUT_EXCEL)){
			List<String> list = new ArrayList<String>();
			Schema schema = (Schema) getComponent().getProperties().get(
					Constants.SCHEMA_PROPERTY_NAME);
			if (schema != null && schema.getGridRow() != null) {
				List<GridRow> gridRows = schema.getGridRow();
				if (gridRows != null) {
					for (GridRow gridRow : gridRows) {
						list.add(gridRow.getFieldName());
					}
				}
			}
			return list;
		}
		return SchemaPropagationHelper.INSTANCE.getFieldsForFilterWidget(getComponent()).get(
				Constants.INPUT_SOCKET_TYPE + 0);
	}

	@Override
	public boolean isWidgetValid() {
			return validateAgainstValidationRule(getProperties().get(propertyName));
	}

	
	@Override
	public void addModifyListener(Property property,  ArrayList<AbstractWidget> widgetList) {
		widgets=widgetList;
		
	}

}
