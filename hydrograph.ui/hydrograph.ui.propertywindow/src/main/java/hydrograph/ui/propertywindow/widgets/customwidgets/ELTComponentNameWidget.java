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


import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.utils.ComponentNameValidator;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.factory.ListenerFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.listeners.ELTNormalFocusOutListener;
import hydrograph.ui.propertywindow.widgets.listeners.ELTVerifyComponentNameListener;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;


/**
 * The Class ELTComponentNameWidget.
 * 
 * @author Bitwise
 */
public class ELTComponentNameWidget extends AbstractWidget {
	private static final String COMPONENT_NAMES = "componentNames";
	private static final String NAME = "Name";

	private static final Logger logger = LogFactory.INSTANCE.getLogger(ELTComponentNameWidget.class);

	private String newName = "newName";
	private String oldName = "oldName";
	private Text text;
	private String propertyName;

	private ELTVerifyComponentNameListener listener;
	private ELTNormalFocusOutListener focusOutListener;
	private ControlDecoration txtDecorator;

	/**
	 * Instantiates a new ELT component name widget.
	 * 
	 * @param componentConfigurationProperty
	 *            the component configuration property
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public ELTComponentNameWidget(ComponentConfigrationProperty componentConfigurationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigurationProperty, componentMiscellaneousProperties, propertyDialogButtonBar);

		this.propertyName = componentConfigurationProperty.getPropertyName();
		this.oldName = (String) componentConfigurationProperty.getPropertyValue();
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {

		ELTDefaultSubgroupComposite eltDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());
		eltDefaultSubgroupComposite.createContainerWidget();

		AbstractELTWidget eltDefaultLable = new ELTDefaultLable(NAME);
		eltDefaultSubgroupComposite.attachWidget(eltDefaultLable);
		setPropertyHelpWidget((Control) eltDefaultLable.getSWTWidgetControl());

		AbstractELTWidget eltDefaultTextBox = new ELTDefaultTextBox().defaultText("Hello")
				.grabExcessHorizontalSpace(true);
		eltDefaultSubgroupComposite.attachWidget(eltDefaultTextBox);

		text = (Text) eltDefaultTextBox.getSWTWidgetControl();
		text.setFocus();
		text.setTextLimit(256);
		firstTextWidget = text;
		txtDecorator = WidgetUtility.addDecorator(text, Messages.FIELD_LABEL_ERROR);
		txtDecorator.setMarginWidth(3);
		ListenerHelper listenerHelper = new ListenerHelper();
		listenerHelper.put(HelperType.CONTROL_DECORATION, txtDecorator);
		listenerHelper.put(HelperType.CURRENT_COMPONENT, getComponent());
		try {
			listener = (ELTVerifyComponentNameListener) ListenerFactory.Listners.VERIFY_COMPONENT_NAME.getListener();
			listener.setNames((ArrayList<String>) super.componentMiscellaneousProperties
					.getComponentMiscellaneousProperty(COMPONENT_NAMES));
			eltDefaultTextBox.attachListener(listener, propertyDialogButtonBar, listenerHelper,
					eltDefaultTextBox.getSWTWidgetControl());

			focusOutListener = (ELTNormalFocusOutListener) ListenerFactory.Listners.NORMAL_FOCUS_OUT.getListener();
			listener.setNames((ArrayList<String>) super.componentMiscellaneousProperties
					.getComponentMiscellaneousProperty(COMPONENT_NAMES));
			eltDefaultTextBox.attachListener(focusOutListener, propertyDialogButtonBar, listenerHelper,
					eltDefaultTextBox.getSWTWidgetControl());

		} catch (Exception exception) {
			logger.error("Exception occured", exception);
		}

		populateWidget();

	}

	private void populateWidget() {
		listener.setOldName(oldName);
		text.setText(oldName);

	}

	private void setToolTipErrorMessage() {
		String toolTipErrorMessage = null;

		if (txtDecorator.isVisible())
			toolTipErrorMessage = txtDecorator.getDescriptionText();

		setToolTipMessage(toolTipErrorMessage);
	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		newName = text.getText().trim();
		LinkedHashMap<String, Object> property = new LinkedHashMap<>();
		Container container = getComponent().getParent();
		if (newName != null && newName != ""
				&& ComponentNameValidator.INSTANCE.isUniqueComponentName(container, newName)) {
			property.put(propertyName, newName);
			// ((ArrayList<String>) super.componentMiscellaneousProperties
			// .getComponentMiscellaneousProperty(COMPONENT_NAMES)).remove(oldName);
			// ((ArrayList<String>) super.componentMiscellaneousProperties
			// .getComponentMiscellaneousProperty(COMPONENT_NAMES)).add(newName);
			oldName = newName;
		} else {
			// old name already should be there in the names arraylist
			property.put(propertyName, oldName);
		}

		setToolTipErrorMessage();
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
