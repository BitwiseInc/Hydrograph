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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.JoinConfigProperty;
import hydrograph.ui.datastructure.property.JoinMappingGrid;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.schema.propagation.helper.SchemaPropagationHelper;
import hydrograph.ui.propertywindow.widgets.customwidgets.joinproperty.ELTJoinConfigGrid;
import hydrograph.ui.propertywindow.widgets.customwidgets.joinproperty.JoinMapGrid;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;


/**
 * @author
 * 
 */
public class ELTJoinWidget extends AbstractWidget {

	public static int value;
	private Object properties;
	private String propertyName;
	private LinkedHashMap<String, Object> property = new LinkedHashMap<>();
	private List<AbstractWidget> widgets;
	// private JoinMappingGrid lookupPropertyGrid;
	private List<JoinConfigProperty> configProperty;

	public ELTJoinWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propertyDialogButtonBar);
		this.properties = (List<JoinConfigProperty>) componentConfigrationProperty.getPropertyValue();
		if (properties == null) {
			configProperty = new ArrayList<>();
		} else {
			configProperty = (List<JoinConfigProperty>) properties;
		}
		this.propertyName = componentConfigProp.getPropertyName();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget#
	 * attachToPropertySubGroup(hydrograph.ui.
	 * propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget)
	 */
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());
		eltSuDefaultSubgroupComposite.createContainerWidget();

		LinkedHashMap<String, Object> map = allComponenetProperties.getComponentConfigurationProperties();
		for (String key : map.keySet()) {
			if (key.equalsIgnoreCase("inPortCount")) {
				String data = (String) map.get(key);
				if (Integer.parseInt(data) >= 2) {
					value = Integer.parseInt(data);
				} else {
					value = 2;
				}
			}
		}

		AbstractELTWidget eltDefaultLable = new ELTDefaultLable("Join\nConfiguration");
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
				ELTJoinConfigGrid eltJoinConfigGrid = new ELTJoinConfigGrid(((Button) eltDefaultButton
						.getSWTWidgetControl()).getShell(), propertyDialogButtonBar, configProperty,getComponent());
				ELTJoinMapWidget  joinMapWidget = null;
				for(AbstractWidget abstractWidget:widgets)
				{
					if(abstractWidget instanceof ELTJoinMapWidget)
					{
						joinMapWidget=(ELTJoinMapWidget)abstractWidget;
						break;
					}
				}
				if (joinMapWidget != null) {
					JoinMappingGrid joinMappingGrid = (JoinMappingGrid) joinMapWidget.getProperties()
							.get("join_mapping");
					eltJoinConfigGrid.setSourceFieldList(joinMappingGrid.getLookupInputProperties());
				}
				eltJoinConfigGrid.setPropagatedFieldProperty(SchemaPropagationHelper.INSTANCE
						.getFieldsForFilterWidget(getComponent()));
				eltJoinConfigGrid.open();
				showHideErrorSymbol(widgets);
				
			}

		});
	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		removeExtraRecordsOfJoinConfig();
		property.put(propertyName, configProperty);
		return property;
	}
	
	/**
	 * Removes extra records of Join configuration window which are more than Input Count on Apply button.
	 */
	public void removeExtraRecordsOfJoinConfig(){
		String count=(String)getComponent().getProperties().get(Constants.INPUT_PORT_COUNT_PROPERTY);
		int inputPortValue=Integer.valueOf(count);
		if(configProperty != null && !configProperty.isEmpty()){
			if(configProperty.size()>inputPortValue){
			ListIterator <JoinConfigProperty>itr =configProperty.listIterator(inputPortValue);
				while(itr.hasNext()){
					itr.next();
					itr.remove();
				}
			}
		}
	}
	
	@Override
	public boolean isWidgetValid() {
	 return validateAgainstValidationRule(configProperty);
	}

	@Override
	public void addModifyListener(Property property,  ArrayList<AbstractWidget> widgetList) {
		widgets=widgetList;
	}
}
