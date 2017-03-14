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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.mapping.InputField;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.schema.propagation.helper.SchemaPropagationHelper;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.SingleColumnGridConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.operational.TransformWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.dialogs.FieldDialog;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;


public class SingleColumnWidget extends AbstractWidget {

	public static final String SINGLE_COLUMN_WIDGET_KEY = "single-column-widget";
	protected String propertyName;
	private List<String> set;
	protected SingleColumnGridConfig gridConfig = null;
	private ArrayList<AbstractWidget> widgets;
	Button button ;

	public SingleColumnWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {

		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
		intialize(componentConfigProp);
	}

	protected void intialize(ComponentConfigrationProperty componentConfigProp) {
		propertyName = componentConfigProp.getPropertyName();
		setProperties(componentConfigProp.getPropertyName(), componentConfigProp.getPropertyValue());
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		ELTDefaultSubgroupComposite defaultSubgroupComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());
		defaultSubgroupComposite.createContainerWidget();

		AbstractELTWidget defaultLable = new ELTDefaultLable(gridConfig.getLabelName());
		defaultSubgroupComposite.attachWidget(defaultLable);
		setPropertyHelpWidget((Control) defaultLable.getSWTWidgetControl());
		
		
		AbstractELTWidget defaultButton;
		if(OSValidator.isMac()){
			defaultButton = new ELTDefaultButton(Constants.EDIT).buttonWidth(120);
		}else{
			defaultButton = new ELTDefaultButton(Constants.EDIT);
		}
		defaultSubgroupComposite.attachWidget(defaultButton);
		button = (Button) defaultButton.getSWTWidgetControl();
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				onDoubleClick();
			}

			
		});
		addDataToGroup(button);
	}

	
	private void addDataToGroup(Button button) {
		Composite composite=(button.getParent());
		//Group grup=(Group) composite.getParent();
		composite.getParent().setData(SINGLE_COLUMN_WIDGET_KEY,button);
	}

	public void setEditButtonEnable(Boolean enable){
		button.setEnabled(enable);
	}
	protected void onDoubleClick() {
		FieldDialog fieldDialog = new FieldDialog(new Shell(), propertyDialogButtonBar);
		fieldDialog.setComponentName(gridConfig.getComponentName());
		if (getProperties().get(propertyName) == null) {
			setProperties(propertyName, new ArrayList<String>());
		}
		fieldDialog.setSourceFieldsFromPropagatedSchema(getPropagatedSchema());
		fieldDialog.setRuntimePropertySet(new ArrayList<String>(set));
		fieldDialog.open();

		setProperties(propertyName, fieldDialog.getFieldNameList());
        showHideErrorSymbol(widgets);
	} 
	
	
	
	private void setProperties(String propertyName, Object properties) {
		this.propertyName = propertyName;
		this.set = (List<String>) properties;

	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		LinkedHashMap<String, Object> property = new LinkedHashMap<>();
		if(this.set==null){
			this.set=new ArrayList<String>();
		}	
		property.put(propertyName, this.set);
		return property;
	}

	@Override
	public void setWidgetConfig(WidgetConfig widgetConfig) {
		gridConfig = (SingleColumnGridConfig) widgetConfig;
	}

	protected List<String> getPropagatedSchema() {
		List<String> propogatedFields=new ArrayList<>();	
		if(StringUtils.equalsIgnoreCase(getComponent().getComponentName(),Constants.AGGREGATE)
		 ||StringUtils.equalsIgnoreCase(getComponent().getComponentName(),Constants.CUMULATE)
		 || StringUtils.equalsIgnoreCase(getComponent().getComponentName(),Constants.GROUP_COMBINE))
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
