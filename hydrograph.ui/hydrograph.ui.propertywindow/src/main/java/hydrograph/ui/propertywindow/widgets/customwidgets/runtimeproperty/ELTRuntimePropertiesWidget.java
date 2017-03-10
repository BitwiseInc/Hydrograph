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

package hydrograph.ui.propertywindow.widgets.customwidgets.runtimeproperty;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory;
import hydrograph.ui.propertywindow.messages.Messages;
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
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

/**
 * Creates the Property window for Runtime Properties
 * 
 * @author Bitwise
 */
public class ELTRuntimePropertiesWidget extends AbstractWidget {
	private static final Logger logger = LogFactory.INSTANCE
			.getLogger(ELTRuntimePropertiesWidget.class);
	private Map<String, String> initialMap;
	private String propertyName;
	private Shell shell;
	private RuntimeConfig runtimeConfig;
	private List<AbstractWidget> widgets;
	protected ControlDecoration buttonDecorator;

	/**
	 * Instantiates a new ELT runtime properties widget.
	 * 
	 * @param componentConfigProp
	 *            the component configration property
	 * @param componentMiscProps
	 *            the component miscellaneous properties
	 * @param propDialogButtonBar
	 *            the property dialog button bar
	 */
	public ELTRuntimePropertiesWidget(
			ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps,
			PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);

		this.propertyName = componentConfigProp.getPropertyName();
		this.initialMap = (Map<String, String>) componentConfigProp
				.getPropertyValue();

		// since this window does all the validation
		// we can assume that it is valid always
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {

		ELTDefaultSubgroupComposite runtimeComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());
		runtimeComposite.createContainerWidget();
		shell = runtimeComposite.getContainerControl().getShell();
		runtimeConfig = (RuntimeConfig) widgetConfig;

		ELTDefaultLable defaultLable1 = new ELTDefaultLable(
				runtimeConfig.getLabel());
		runtimeComposite.attachWidget(defaultLable1);
		setPropertyHelpWidget((Control) defaultLable1.getSWTWidgetControl());

		ELTDefaultButton eltDefaultButton = new ELTDefaultButton(Constants.EDIT);
		if(OSValidator.isMac()){
			eltDefaultButton.buttonWidth(120);
		}

		runtimeComposite.attachWidget(eltDefaultButton);

		buttonDecorator = WidgetUtility.addDecorator(
				(Control) eltDefaultButton.getSWTWidgetControl(),
				Messages.bind(Messages.EmptyValueNotification, runtimeConfig.getLabel()));
		if (OSValidator.isMac()) {
			buttonDecorator.setMarginWidth(-2);
		}
		else{
			buttonDecorator.setMarginWidth(3);
		}
		setDecoratorsVisibility();

		try {
			eltDefaultButton
					.attachListener(
							ListenerFactory.Listners.RUNTIME_BUTTON_CLICK
									.getListener(),
							propertyDialogButtonBar,
							new ListenerHelper(this.getClass().getName(), this),
							eltDefaultButton.getSWTWidgetControl());

		} catch (Exception exception) {
			logger.error(
					"Error occured while attaching listener to Runtime Properties window",
					exception);
		}
	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		LinkedHashMap<String, Object> tempPropertyMap = new LinkedHashMap<>();
		tempPropertyMap.put(this.propertyName, this.initialMap);
		setToolTipErrorMessage();
		return tempPropertyMap;
	}

	/**
	 * New window launcher.
	 */
	public void newWindowLauncher() {
		if (getProperties().get(propertyName) == null) {
			initialMap = new HashMap<String, String>();
		}

		RuntimePropertyDialog runtimePropertyDialog = new RuntimePropertyDialog(
				shell, propertyDialogButtonBar, runtimeConfig.getWindowLabel());
		runtimePropertyDialog.setRuntimeProperties(new LinkedHashMap<>(
				initialMap));
		runtimePropertyDialog.open();

		initialMap = runtimePropertyDialog.getRuntimeProperties();

		if (runtimePropertyDialog.isOkPressed()) {

			showHideErrorSymbol(widgets);
		}

		setDecoratorsVisibility();

	}

	@Override
	public boolean isWidgetValid() {
		return validateAgainstValidationRule(initialMap);
	}

	@Override
	public void addModifyListener(Property property,
			ArrayList<AbstractWidget> widgetList) {
		widgets = widgetList;
	}

	protected void setToolTipErrorMessage() {
		String toolTipErrorMessage = null;

		if (buttonDecorator.isVisible())
			toolTipErrorMessage = buttonDecorator.getDescriptionText();

		setToolTipMessage(toolTipErrorMessage);
	}

	protected void setDecoratorsVisibility() {

		if (!isWidgetValid()) {
			buttonDecorator.show();
		} else {
          buttonDecorator.hide();
		}

	}
}
