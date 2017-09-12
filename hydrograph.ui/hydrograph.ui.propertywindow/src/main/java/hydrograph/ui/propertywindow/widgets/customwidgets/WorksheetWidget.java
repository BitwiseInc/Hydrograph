/********************************************************************************
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
 ******************************************************************************/
package hydrograph.ui.propertywindow.widgets.customwidgets;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.WorkSheetValueProperty;
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
import hydrograph.ui.propertywindow.widgets.listeners.ELTEventChangeListener;
import hydrograph.ui.propertywindow.widgets.listeners.ELTModifyListener;
import hydrograph.ui.propertywindow.widgets.listeners.ELTNormalFocusOutListener;
import hydrograph.ui.propertywindow.widgets.listeners.FocusInListener;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * WorksheetWidget class provide the widget for Work sheet Name
 * 
 * @author Bitwise
 *
 */
public class WorksheetWidget extends AbstractWidget {
	private PropertyDialogButtonBar propDialogButtonBar;
	private ELTDefaultSubgroupComposite eltSubgroupComposite;
	private Text textBox;
	private ControlDecoration txtDecorator;
	private Cursor cursor;
	private WorkSheetValueProperty workSheetValueProperty;
	private String propertyName;
	private List<AbstractWidget> widgetList;
	private Button isFieldCheckBox;

	public WorksheetWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
		this.propDialogButtonBar = propDialogButtonBar;
		this.propertyName = componentConfigProp.getPropertyName();
		this.workSheetValueProperty = (WorkSheetValueProperty) componentConfigProp.getPropertyValue();
		if (workSheetValueProperty == null) {
			workSheetValueProperty = new WorkSheetValueProperty();
		}

	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget subGroup) {
		eltSubgroupComposite = new ELTDefaultSubgroupComposite(subGroup.getContainerControl());
		eltSubgroupComposite.createContainerWidget();
		Utils.INSTANCE.loadProperties();
		this.cursor = subGroup.getContainerControl().getDisplay().getSystemCursor(SWT.CURSOR_HAND);

		AbstractELTWidget label = new ELTDefaultLable(Messages.LABEL_WORKSHEET_NAME + " ");
		eltSubgroupComposite.attachWidget(label);
		setPropertyHelpWidget((Control) label.getSWTWidgetControl());

		AbstractELTWidget textBoxWidget = new ELTDefaultTextBox();
		eltSubgroupComposite.attachWidget(textBoxWidget);

		textBox = (Text) textBoxWidget.getSWTWidgetControl();
		txtDecorator = WidgetUtility.addDecorator(textBox, Messages.bind(Messages.EMPTY_FIELD, ((Label) label.getSWTWidgetControl()).getText()));
		txtDecorator.setMarginWidth(3);
		txtDecorator.show();
		textBox.setToolTipText("Worksheet Name can be static or FieldName or a Parameter");
		GridData gridData = (GridData) textBox.getLayoutData();
		if (OSValidator.isMac()) {
			gridData.widthHint = 106;
		} else {
			gridData.widthHint = 80;
		}
		attachListeners(textBox);

		isFieldCheckBox = new Button(eltSubgroupComposite.getContainerControl(), SWT.CHECK);
		isFieldCheckBox.setEnabled(false);
		isFieldCheckBox.setText(Constants.IS_FIELD);
		attachSelectionListener(isFieldCheckBox);
		populateWidget();
	}

	private void attachSelectionListener(Button isFieldCheckBox) {
		isFieldCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				propDialogButtonBar.enableApplyButton(true);
			}
		});
	}

	private void populateWidget() {
		if (this.workSheetValueProperty != null) {
			if (StringUtils.isNotBlank(workSheetValueProperty.getWorksheetName())) {
				textBox.setText(workSheetValueProperty.getWorksheetName());
				txtDecorator.hide();
				isFieldCheckBox.setEnabled(true);
				Utils.INSTANCE.addMouseMoveListener(textBox, cursor);
			} else {
				txtDecorator.show();
				textBox.setText("");
			}

			if (workSheetValueProperty.isField()) {
				isFieldCheckBox.setSelection(true);
			}
		}
	}

	private void attachListeners(Text textBox) {

		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, txtDecorator);
		textBox.addListener(SWT.CHANGED,
				new ELTEventChangeListener().getListener(propertyDialogButtonBar, helper, textBox));
		textBox.addListener(SWT.Modify, new ELTModifyListener().getListener(propertyDialogButtonBar, helper, textBox));
		textBox.addListener(SWT.FocusIn, new FocusInListener().getListener(propDialogButtonBar, helper, textBox));
		textBox.addListener(SWT.FocusOut,
				new ELTNormalFocusOutListener().getListener(propDialogButtonBar, helper, textBox));
	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		LinkedHashMap<String, Object> property = new LinkedHashMap<>();
		WorkSheetValueProperty workSheetValueProperty = new WorkSheetValueProperty();
		if (StringUtils.isNotBlank(textBox.getText())) {
			workSheetValueProperty.setWorksheetName(textBox.getText());
		}
		if (isFieldCheckBox.getSelection()) {
			workSheetValueProperty.setField(isFieldCheckBox.getSelection());
		} else {
			workSheetValueProperty.setField(false);
		}
		property.put(propertyName, workSheetValueProperty);
		setToolTipErrorMessage();
		showHideErrorSymbol(this.widgetList);
		return property;
	}

	@Override
	public boolean isWidgetValid() {
		WorkSheetValueProperty property = new WorkSheetValueProperty();
		property.setWorksheetName(textBox.getText());
		return validateAgainstValidationRule(property);
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {
		this.widgetList = widgetList;
		textBox.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (StringUtils.isNotBlank(textBox.getText())) {
					isFieldCheckBox.setEnabled(true);
					 showHideErrorSymbol(widgetList);
				}
				Utils.INSTANCE.addMouseMoveListener(textBox, cursor);
				propDialogButtonBar.enableApplyButton(true);
			}
		});
	}

	/**
	 * Sets the tool tip error message
	 */
	protected void setToolTipErrorMessage() {
		String toolTipErrorMessage = null;

		if (txtDecorator.isVisible()) {
			toolTipErrorMessage = txtDecorator.getDescriptionText();
			setToolTipMessage(toolTipErrorMessage);
		}
	}

}
