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
package hydrograph.ui.propertywindow.runprogram;

import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;

/**
 * The Class RunComponentWidget
 * @author Bitwise
 *
 */
public class RunComponentWidget extends AbstractWidget{
	private String propertyName;
	protected String propertyValue;
	private StyledText styledText;
	private List<AbstractWidget> widgets;
	private ControlDecoration txtDecorator;
	private LinkedHashMap<String, Object> property = new LinkedHashMap<>();

	
	public RunComponentWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propertyDialogButtonBar);
		this.propertyName = componentConfigProp.getPropertyName();
		this.propertyValue =  String.valueOf(componentConfigProp.getPropertyValue());
		
	}
	
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		ELTDefaultSubgroupComposite defaultSubgroupComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());
		defaultSubgroupComposite.createContainerWidget();
		
		AbstractELTWidget eltDefaultLable = new ELTDefaultLable(Messages.EXECUTION_COMMAND);
		defaultSubgroupComposite.attachWidget(eltDefaultLable);

		styledText=new StyledText(defaultSubgroupComposite.getContainerControl(), SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gridData=new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint=80;
		styledText.setLayoutData(gridData);
		
		txtDecorator = WidgetUtility.addDecorator(styledText, Messages.bind(Messages.EMPTY_FIELD, Messages.EXECUTION_COMMAND));
		
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, txtDecorator);
		
		populateWidget();
		
		styledText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if(StringUtils.isNotBlank(styledText.getText())){
					txtDecorator.hide();
				}else{
					txtDecorator.show();
				}
				showHideErrorSymbol(widgets);
				propertyDialogButtonBar.enableApplyButton(true);
			}
		});
	}

	
	private void setToolTipErrorMessage(){
		String toolTipErrorMessage = null;
		if(txtDecorator.isVisible())
			toolTipErrorMessage = txtDecorator.getDescriptionText();
						
		setToolTipMessage(toolTipErrorMessage);
	}
	
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		property.put(propertyName, styledText.getText());
		setToolTipErrorMessage();
		
		return property;
	}

	
	private void populateWidget(){
		if(StringUtils.isNotBlank(propertyValue) ){
			styledText.setText(propertyValue);
			txtDecorator.hide();
		}
		else{
			styledText.setText("");
			txtDecorator.show();
		}
	}
	
	@Override
	public boolean isWidgetValid() {
		return validateAgainstValidationRule(styledText.getText());
	}

	@Override
	public void addModifyListener(Property property,ArrayList<AbstractWidget> widgetList) {
		widgets=widgetList;
	}

}
