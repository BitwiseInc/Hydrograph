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

import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.eclipse.swt.widgets.Control;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Text;


/**
 * The Class ELTComponentID.
 * 
 * @author Bitwise
 */
public class ELTComponentID extends AbstractWidget{
	private ELTDefaultTextBox eltDefaultTextBox;
	
	/**
	 * Instantiates a new ELT component base type.
	 * 
	 * @param componentConfigrationProperty
	 *            the component configration property
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public ELTComponentID(
			ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties,
				propertyDialogButtonBar);
	}
	
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget subGroup) {
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(subGroup.getContainerControl());
		eltSuDefaultSubgroupComposite.createContainerWidget();
		
		AbstractELTWidget eltDefaultLable = new ELTDefaultLable("ID");
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);
		
		setPropertyHelpWidget((Control) eltDefaultLable.getSWTWidgetControl());
		
		eltDefaultTextBox = new ELTDefaultTextBox().grabExcessHorizontalSpace(true).textBoxWidth(100);
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultTextBox);
		eltDefaultTextBox.setEnabled(false);
		
		populateWidget();
	}

	
	private void populateWidget() {
		String componentId = getComponent().getComponentId();
		String componentName = getComponent().getComponentLabel().getLabelContents();
		if (StringUtils.isNotBlank(componentId)) {
			((Text) eltDefaultTextBox.getSWTWidgetControl()).setText(componentId);
		} else {
			if (StringUtils.isNotBlank(componentName)) {
				((Text) eltDefaultTextBox.getSWTWidgetControl()).setText(componentName);
				getComponent().setComponentId(componentName);
			}
		}

	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		return null;
	}

	@Override
	public boolean isWidgetValid() {
		return true;
	}

	@Override
	public void addModifyListener(Property property,  ArrayList<AbstractWidget> widgetList) {
	}
}
