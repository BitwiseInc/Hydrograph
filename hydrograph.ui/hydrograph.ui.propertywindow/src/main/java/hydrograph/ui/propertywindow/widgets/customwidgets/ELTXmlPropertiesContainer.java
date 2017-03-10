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
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.xmlPropertiesContainer.XMLTextContainer;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;


/**
 * @author Bitwise
 * 
 * This class is used to launch property window which shows XML content of component
 *
 */
public class ELTXmlPropertiesContainer extends AbstractWidget{
	
	private LinkedHashMap<String, Object> property=new LinkedHashMap<>();
	 private String propertyName;
	private String xmlContent=null;
	
	/**
	 * @param componentConfigrationProperty
	 * @param componentMiscellaneousProperties
	 * @param propertyDialogButtonBar
	 */
	public ELTXmlPropertiesContainer(
			ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties,propertyDialogButtonBar);
		this.propertyName = componentConfigrationProperty.getPropertyName();
		this.xmlContent=(String) componentConfigrationProperty.getPropertyValue();

	}
	
	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget#attachToPropertySubGroup(hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget)
	 */
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(container.getContainerControl());
		eltSuDefaultSubgroupComposite.createContainerWidget();
		 
		
		AbstractELTWidget eltDefaultLable = new ELTDefaultLable("Properties");
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);
		setPropertyHelpWidget((Control) eltDefaultLable.getSWTWidgetControl());
		
		final AbstractELTWidget eltDefaultButton = new ELTDefaultButton("Edit");
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultButton);
		((Button) eltDefaultButton.getSWTWidgetControl()).addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				XMLTextContainer xmlTextContainer=new XMLTextContainer();
				if(xmlContent!=null)
					xmlTextContainer.setXmlText(xmlContent);
				xmlContent=xmlTextContainer.launchXMLTextContainerWindow();
			}
			
		});
	}


	@Override
	public LinkedHashMap<String, Object> getProperties() {
		 property.put(this.propertyName,xmlContent );
	return property;
	}

	@Override
	public boolean isWidgetValid() {
		return true;
	}

	

	@Override
	public void addModifyListener(Property property,  ArrayList<AbstractWidget> widgetList) {
	}
	


	
}
