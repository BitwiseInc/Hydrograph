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
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTRadioButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;


/**
 * The Class ELTRetentionlogicWidget.
 * 
 * @author Bitwise
 */
public class ELTRetentionLogicWidget extends AbstractWidget{
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ELTRetentionLogicWidget.class);
	
	private final String propertyName;
	private final  LinkedHashMap<String, Object> property=new LinkedHashMap<>();
	private String properties;
	private AbstractELTWidget First,Last,Unique;
	
	/**
	 * Instantiates a new ELT retentionlogic widget.
	 * 
	 * @param componentConfigrationProperty
	 *            the component configration property
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public ELTRetentionLogicWidget(
			ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties,
				propertyDialogButtonBar);
		this.propertyName = componentConfigrationProperty.getPropertyName();
		this.properties =  (String)componentConfigrationProperty.getPropertyValue();
		// This will be valid always as one of the value will be selected
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(container.getContainerControl());
		eltSuDefaultSubgroupComposite.createContainerWidget();
		eltSuDefaultSubgroupComposite.numberOfBasicWidgets(4);
		
		AbstractELTWidget eltDefaultLable = new ELTDefaultLable("Retain");
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);
		setPropertyHelpWidget((Control) eltDefaultLable.getSWTWidgetControl());
		
		SelectionListener selectionListener = new SelectionAdapter () {
			
	         @Override
			public void widgetSelected(SelectionEvent event) {
	        	 Button button = ((Button) event.widget);
	           properties = button.getText();
	           propertyDialogButtonBar.enableApplyButton(true);
	            logger.debug( "Radio Button Value",button.getText());
	           // button.getSelection();
	         };
	      };
		
		First = new ELTRadioButton("First");
		eltSuDefaultSubgroupComposite.attachWidget(First);
		((Button) First.getSWTWidgetControl()).addSelectionListener(selectionListener);
		//button=(Button) First.getSWTWidgetControl();
		
		Last = new ELTRadioButton("Last");
		eltSuDefaultSubgroupComposite.attachWidget(Last);
		((Button) Last.getSWTWidgetControl()).addSelectionListener(selectionListener);
		
		Unique = new ELTRadioButton("Unique");
		eltSuDefaultSubgroupComposite.attachWidget(Unique);
		((Button) Unique.getSWTWidgetControl()).addSelectionListener(selectionListener);
		
		populateWidget();
		
	}
	
	private void populateWidget(){
		if(StringUtils.isBlank(this.properties))
			this.properties=Constants.FIRST;
		switch(this.properties){
			case "First":
				((Button) First.getSWTWidgetControl()).setSelection(true);
				break;
			case "first":
				((Button) First.getSWTWidgetControl()).setSelection(true);
				break;
			case "last":
				((Button) Last.getSWTWidgetControl()).setSelection(true); 
				break;
			case "Last":
				((Button) Last.getSWTWidgetControl()).setSelection(true); 
				break;
			case "uniqueonly":
				((Button) Unique.getSWTWidgetControl()).setSelection(true);  
				break;
			case "Unique":
				((Button) Unique.getSWTWidgetControl()).setSelection(true);  
				break;
		}
		
	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		property.put(propertyName, properties);
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
