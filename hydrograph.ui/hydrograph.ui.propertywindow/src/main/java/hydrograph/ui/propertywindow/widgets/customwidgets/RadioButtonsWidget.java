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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

import hydrograph.ui.datastructure.property.MatchValueProperty;
import hydrograph.ui.propertywindow.factory.ListenerFactory.Listners;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.RadioButtonConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTRadioButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.listeners.IELTListener;


/**
 * Class for displaying radio button on property window
 * @author Bitwise
 *
 */
public class RadioButtonsWidget extends AbstractWidget {

	private final String propertyName;
	private final LinkedHashMap<String, Object> property = new LinkedHashMap<>();
	private String[] buttonText;
	private Button[] buttons;
	private MatchValueProperty matchValue;
	private RadioButtonConfig radioButtonConfig;
	
	public RadioButtonsWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps,PropertyDialogButtonBar propertyDialogButtonBar) {
		
		super(componentConfigProp, componentMiscProps,	propertyDialogButtonBar);
		this.propertyName = componentConfigrationProperty.getPropertyName();
		
		if (componentConfigrationProperty.getPropertyValue() == null) {
			matchValue = new MatchValueProperty();
		} else {
			matchValue = (MatchValueProperty) componentConfigrationProperty.getPropertyValue();
		}
	}

	public void setWidgetConfig(WidgetConfig widgetConfig) {
		radioButtonConfig = (RadioButtonConfig) widgetConfig;
	}
	
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		ELTDefaultSubgroupComposite defaultSubgroupComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());
		buttonText = radioButtonConfig.getWidgetDisplayNames();
		buttons = new Button[buttonText.length];
		defaultSubgroupComposite.createContainerWidget();
		defaultSubgroupComposite.numberOfBasicWidgets(buttonText.length + 1);

		AbstractELTWidget defaultLabel = new ELTDefaultLable(radioButtonConfig.getPropertyName());
		defaultSubgroupComposite.attachWidget(defaultLabel);

		setPropertyHelpWidget((Control) defaultLabel.getSWTWidgetControl());
		
		SelectionListener selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button button = ((Button) event.widget);

				matchValue.setMatchValue(button.getText());
				matchValue.setRadioButtonSelected(true);
				propertyDialogButtonBar.enableApplyButton(true);
			}
		};

		
		for (int i = 0; i < buttonText.length; i++) {
			ELTRadioButton eltRadioButton = new ELTRadioButton(buttonText[i]);
			defaultSubgroupComposite.attachWidget(eltRadioButton);
			buttons[i] = ((Button) eltRadioButton.getSWTWidgetControl());
			((Button) eltRadioButton.getSWTWidgetControl()).addSelectionListener(selectionListener);
		}
		buttons[0].setSelection(true);
		
		if(!radioButtonConfig.getRadioButtonListners().isEmpty()){
			Stream<Button> stream = Arrays.stream(buttons);
			stream.forEach(button -> {
			Listners radioListners = radioButtonConfig.getRadioButtonListners().get(0);
			IELTListener listener = radioListners.getListener();
			Listener listnr = listener.getListener(propertyDialogButtonBar, null, button);
			button.addListener(SWT.Selection, listnr);});
		}
		 populateWidget();
	}


	public void populateWidget() {
		for (int i = 1; i < buttons.length; i++) {
			if (StringUtils.isNotBlank(matchValue.getMatchValue())) {
				if (matchValue.getMatchValue().equalsIgnoreCase(
						buttons[i].getText())) {
					buttons[i].setSelection(true);
					buttons[0].setSelection(false);
				}
			} else {
				buttons[0].setSelection(true);
			}
		}
	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		property.put(propertyName, matchValue);
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
