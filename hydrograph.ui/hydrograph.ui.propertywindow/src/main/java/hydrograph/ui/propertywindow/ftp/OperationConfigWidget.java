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
package hydrograph.ui.propertywindow.ftp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.datastructure.property.FTPProtocolDetails;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.RuntimeConfig;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;

/**
 * The Class OperationConfigWidget used to create widget
 * @author Bitwise
 *
 */
public class OperationConfigWidget extends AbstractWidget{
	private static final Logger logger = LogFactory.INSTANCE.getLogger(OperationConfigWidget.class);
	private String propertyName;
	private RuntimeConfig runtimeConfig;
	private Shell shell;
	private Cursor cursor;
	private Map<String, FTPAuthOperationDetails> initialMap;
	private ArrayList<AbstractWidget> widgets;
	private LinkedHashMap<String, Object> tempPropertyMap;
	
	public OperationConfigWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);

		this.propertyName = componentConfigProp.getPropertyName();
		if (componentConfigProp.getPropertyValue() != null
				&& (Map.class).isAssignableFrom(componentConfigProp.getPropertyValue().getClass())) {
			this.initialMap = (Map<String, FTPAuthOperationDetails>) componentConfigProp.getPropertyValue();
		}
		if (initialMap == null) {
			this.initialMap = new LinkedHashMap<String, FTPAuthOperationDetails>();
		}
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget subGroup) {
		ELTDefaultSubgroupComposite additionalParameterComposite = new ELTDefaultSubgroupComposite(
				subGroup.getContainerControl());
		additionalParameterComposite.createContainerWidget();
		this.shell = additionalParameterComposite.getContainerControl().getShell();
		this.runtimeConfig = (RuntimeConfig) widgetConfig;
		
		Utils.INSTANCE.loadProperties();
		cursor = subGroup.getContainerControl().getDisplay().getSystemCursor(SWT.CURSOR_HAND);
		
		ELTDefaultLable additionalParameterLabel = new ELTDefaultLable(runtimeConfig.getLabel());
		additionalParameterComposite.attachWidget(additionalParameterLabel);
		setPropertyHelpWidget((Control) additionalParameterLabel.getSWTWidgetControl());

		ELTDefaultButton additionalParameterButton = new ELTDefaultButton(Constants.EDIT);

		additionalParameterComposite.attachWidget(additionalParameterButton);
		try {
			additionalParameterButton.attachListener(ListenerFactory.Listners.RUNTIME_BUTTON_CLICK.getListener(),
					propertyDialogButtonBar, new ListenerHelper(this.getClass().getName(), this),
					additionalParameterButton.getSWTWidgetControl());

		} catch (Exception exception) {
			logger.error("Error occured while attaching listener to Runtime Properties window", exception);
		}
		
	}
	
	public void newWindowLauncher() {
		String [] optionList = null;
		initialMap = new LinkedHashMap<>(initialMap);
		String protocoltext = null;
		for(AbstractWidget widget : widgets){
			if(widget.getPropertyName().equals(Constants.PROTOCOL_SELECTION)){
				FTPProtocolDetails protocolDetails = (FTPProtocolDetails) widget.getProperties().get(Constants.PROTOCOL_SELECTION);
				if(protocolDetails!= null){
					if(StringUtils.equalsIgnoreCase(protocolDetails.getProtocol(), Constants.AWS_S3)){
						protocoltext = protocolDetails.getProtocol();
						optionList = new String[]{Constants.GET_FILE_S3, Constants.PUT_FILE_S3};
					}else{
						protocoltext = protocolDetails.getProtocol();
						optionList = new String[]{Constants.GET_FILE, Constants.PUT_FILE};
					}
				}
			}
		}
		
		String selectedText = protocoltext;
		boolean bol = initialMap.entrySet().stream().anyMatch(val -> val.getValue().getProtocolSelection().equals(selectedText));
		if(!bol){
			initialMap = new LinkedHashMap<>();
		}
		
		FTPOperationConfigDialog authenticationEditorDialog = new FTPOperationConfigDialog(shell, 
				"", propertyDialogButtonBar, initialMap, cursor, optionList, protocoltext);
		authenticationEditorDialog.open();
		
		Map<String, FTPAuthOperationDetails> newValues = authenticationEditorDialog.getOperationParamDetails();
		
		if(isAnyUpdate(initialMap,newValues)){
			propertyDialogButtonBar.enableApplyButton(true);
		}
		
		initialMap = newValues;
		showHideErrorSymbol(widgets);
	}
	
	private boolean isAnyUpdate(Map<String, FTPAuthOperationDetails> oldMap, Map<String, FTPAuthOperationDetails> newMap) {
		if(!oldMap.entrySet().isEmpty()){
			for(Entry< String , FTPAuthOperationDetails> entry : oldMap.entrySet()){
				FTPAuthOperationDetails str = newMap.get(entry.getKey());
				FTPAuthOperationDetails str2 = entry.getValue();
				if(str != null && str2!= null){
					if(!str.equals(str2)){
						return true;
					}
				}else {
					propertyDialogButtonBar.enableApplyButton(true);
				}
			}
		}else{
			return true;
		}
		return false;
	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		tempPropertyMap = new LinkedHashMap<>();
		tempPropertyMap.put(this.propertyName, this.initialMap);
		setToolTipErrorMessage();
		return tempPropertyMap;
	}
	
	/**
	 * Set the tool tip error message
	 */
	protected void setToolTipErrorMessage() {
		String toolTipErrorMessage = null;
		setToolTipMessage(toolTipErrorMessage);
	}

	@Override
	public boolean isWidgetValid() {
		return validateAgainstValidationRule(initialMap);
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {
		widgets = widgetList;
	}

}
