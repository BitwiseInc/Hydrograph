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

import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.LookupConfigProperty;
import hydrograph.ui.datastructure.property.LookupMappingGrid;
import hydrograph.ui.graph.model.PortTypeEnum;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.lookupproperty.ELTLookupConfigGrid;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;


public class ELTLookupConfigWidget extends AbstractWidget {
    
	private List<AbstractWidget> widgets;
	private LookupConfigProperty properties;
	private String propertyName;
	private LinkedHashMap<String, Object> property = new LinkedHashMap<>();

	public ELTLookupConfigWidget(ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties, propertyDialogButtonBar);
		this.propertyName = componentConfigrationProperty.getPropertyName();
		this.properties = (LookupConfigProperty) componentConfigrationProperty.getPropertyValue();
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());
		eltSuDefaultSubgroupComposite.createContainerWidget();

		AbstractELTWidget eltDefaultLable = new ELTDefaultLable("Lookup\nConfiguration");
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);

		setPropertyHelpWidget((Control) eltDefaultLable.getSWTWidgetControl());
		
		final AbstractELTWidget eltDefaultButton;
		if(OSValidator.isMac()){
			eltDefaultButton = new ELTDefaultButton("Edit").buttonWidth(120);
		}else{
			eltDefaultButton = new ELTDefaultButton("Edit");
		}
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultButton);

		((Button) eltDefaultButton.getSWTWidgetControl()).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (properties == null) {
					properties = new LookupConfigProperty();
					properties.setDriverKey("");
					properties.setLookupKey("");
				}
				ELTLookupMapWidget  lookUpMapWidget = null;
				for(AbstractWidget abstractWidget:widgets)
				{
					if(abstractWidget instanceof ELTLookupMapWidget)
					{
						lookUpMapWidget=(ELTLookupMapWidget)abstractWidget;
						break;
					}
				}
				if (lookUpMapWidget != null) {
					LookupMappingGrid joinMappingGrid = (LookupMappingGrid) lookUpMapWidget.getProperties()
							.get("hash_join_map");
					ELTLookupConfigGrid eltLookupConfigGrid = new ELTLookupConfigGrid(
							((Button) eltDefaultButton.getSWTWidgetControl()).getShell(), propertyDialogButtonBar,
							properties);
					eltLookupConfigGrid.setSourceFieldList(joinMappingGrid.getLookupInputProperties());
					eltLookupConfigGrid.open();
				}
				setPortTypes();
				
				showHideErrorSymbol(widgets);
			}

			private void setPortTypes() {
				if(properties.isSelected()){
					getComponent().getPorts().get(Messages.IN1_PORT).setPortType(PortTypeEnum.DRIVER);
					getComponent().getPorts().get(Messages.IN1_PORT).setLabelOfPort(Messages.DRIVER_PORT_LABEL);
					getComponent().getPorts().get(Messages.IN0_PORT).setPortType(PortTypeEnum.LOOKUP);
					getComponent().getPorts().get(Messages.IN0_PORT).setLabelOfPort(Messages.LOOKUP_PORT_LABEL);
				}else{
					getComponent().getPorts().get(Messages.IN1_PORT).setPortType(PortTypeEnum.LOOKUP);
					getComponent().getPorts().get(Messages.IN1_PORT).setLabelOfPort(Messages.LOOKUP_PORT_LABEL);
					getComponent().getPorts().get(Messages.IN0_PORT).setPortType(PortTypeEnum.DRIVER);
					getComponent().getPorts().get(Messages.IN0_PORT).setLabelOfPort(Messages.DRIVER_PORT_LABEL);
				}
			}
		});
	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		property.put(propertyName, properties);
		return property;
	}

	@Override
	public boolean isWidgetValid() {
		LookupConfigProperty lookupConfigProperty = (LookupConfigProperty)properties;
		return validateAgainstValidationRule(lookupConfigProperty);
	}

	

	@Override
	public void addModifyListener(Property property,  ArrayList<AbstractWidget> widgetList) {
		widgets=widgetList;
	}

}
