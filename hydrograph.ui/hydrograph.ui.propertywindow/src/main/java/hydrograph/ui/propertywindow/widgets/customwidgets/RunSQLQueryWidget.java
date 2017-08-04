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
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * RunSQL component Query widget
 * @author Bitwise
 *
 */
public class RunSQLQueryWidget extends AbstractWidget{
	private String propertyName;
	protected String propertyValue;
	private StyledText styledText;
	private List<AbstractWidget> widgets;
	private ControlDecoration txtDecorator;
	private LinkedHashMap<String, Object> property = new LinkedHashMap<>();
	private Cursor cursor;
	
	public RunSQLQueryWidget(ComponentConfigrationProperty componentConfigProp,
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
		
		AbstractELTWidget eltDefaultLable = new ELTDefaultLable(Messages.RUN_SQL_QUERY);
		defaultSubgroupComposite.attachWidget(eltDefaultLable);

		styledText=new StyledText(defaultSubgroupComposite.getContainerControl(), SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gridData=new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint=80;
		gridData.widthHint = 120;
		styledText.setLayoutData(gridData);
		
		txtDecorator = WidgetUtility.addDecorator(styledText, Messages.bind(Messages.EMPTY_FIELD, Messages.EXECUTION_COMMAND));
		
		AbstractELTWidget eltDefaultButton = new ELTDefaultButton("");
			defaultSubgroupComposite.attachWidget(eltDefaultButton);
			((Button) eltDefaultButton.getSWTWidgetControl()).setVisible(false);
			
			AbstractELTWidget label = new ELTDefaultLable("");
			defaultSubgroupComposite.attachWidget(label);
			((Label) label.getSWTWidgetControl()).setVisible(false);
			
			AbstractELTWidget textLabel = new ELTDefaultLable(Messages.RUN_SQL_MESSAGE);
			defaultSubgroupComposite.attachWidget(textLabel);
			((Label) textLabel.getSWTWidgetControl()).setLayoutData(new GridData(0, 0, true, false));
		
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, txtDecorator);
		
		Utils.INSTANCE.loadProperties();
		cursor = container.getContainerControl().getDisplay().getSystemCursor(SWT.CURSOR_HAND);
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
			Utils.INSTANCE.addMouseMoveListener(styledText, cursor);
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
		styledText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Utils.INSTANCE.addMouseMoveListener(styledText, cursor);
			}
		});
	}

}
