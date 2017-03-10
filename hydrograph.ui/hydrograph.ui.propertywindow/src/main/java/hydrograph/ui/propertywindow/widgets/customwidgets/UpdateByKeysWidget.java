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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.dialogs.FieldDialog;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

public class UpdateByKeysWidget extends AbstractWidget{

	private static final Logger logger = LogFactory.INSTANCE.getLogger(UpdateByKeysWidget.class);
	private String propertyName;
	private String propertyValue;
	private Button selectKeysButton;
	private List<AbstractWidget> widgets;
	private Text updateByKeysTextBox;
	LinkedHashMap<String, Object> tempPropertyMap;
	private Map<String, String> initialMap;
	List<String> schemaFields;
	private ControlDecoration updateByKeysTextBoxDecorator;

	public UpdateByKeysWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
		this.propertyName = componentConfigProp.getPropertyName();
		this.propertyValue = (String) componentConfigProp.getPropertyValue();
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget subGroup) {
		
		logger.debug("Starting {} button creation");
		ELTDefaultSubgroupComposite selectUpdateKeysComposite = new ELTDefaultSubgroupComposite(subGroup.getContainerControl());
		selectUpdateKeysComposite.createContainerWidget();
		
		ELTDefaultLable defaultLable1 = new ELTDefaultLable(Messages.LABEL_UPDATE_BY_KEYS);
		selectUpdateKeysComposite.attachWidget(defaultLable1);
		setPropertyHelpWidget((Control) defaultLable1.getSWTWidgetControl());
		
		ELTDefaultTextBox defaultUpdateByKeysTextBox = new ELTDefaultTextBox();
		selectUpdateKeysComposite.attachWidget(defaultUpdateByKeysTextBox);
		 updateByKeysTextBox=(Text)defaultUpdateByKeysTextBox.getSWTWidgetControl();
		 updateByKeysTextBox.setEnabled(false);
		setPropertyHelpWidget((Control) defaultUpdateByKeysTextBox.getSWTWidgetControl());
		
		ELTDefaultButton eltDefaultButton = new ELTDefaultButton(Messages.LABEL_SELECT_KEYS);
		selectUpdateKeysComposite.attachWidget(eltDefaultButton);
		selectKeysButton=(Button)eltDefaultButton.getSWTWidgetControl();
		
		updateByKeysTextBoxDecorator = WidgetUtility.addDecorator(updateByKeysTextBox, Messages.bind(Messages.EMPTY_FIELD, "JDBC Driver \n Class"));
		updateByKeysTextBoxDecorator.setMarginWidth(3);

		if(StringUtils.isNotEmpty(propertyValue)){
			updateByKeysTextBox.setText(propertyValue);
			updateByKeysTextBoxDecorator.hide();
		}
		
		attachButtonListner(selectKeysButton);
		
		
		updateByKeysTextBox.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent event) {
				Text textBox=(Text)event.widget;
				if(StringUtils.isBlank(textBox.getText())){
					updateByKeysTextBoxDecorator.show();
				}else{
					updateByKeysTextBoxDecorator.hide();
				}
			}
		});
		
	}
	
	

	private void attachButtonListner(Button selectKeysButton) {
		selectKeysButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				String buttonText = Messages.UPDATE_KEYS_WINDOW_LABEL;
				FieldDialog fieldDialog = new FieldDialog(new Shell(), propertyDialogButtonBar);
				fieldDialog.setComponentName(buttonText);
				fieldDialog.setSourceFieldsFromPropagatedSchema(getPropagatedSchema());
				if(StringUtils.isNotBlank(updateByKeysTextBox.getText())){
					fieldDialog.setPropertyFromCommaSepratedString(updateByKeysTextBox.getText());
				}
				fieldDialog.open();
				String valueForNewTableTextBox = fieldDialog.getResultAsCommaSeprated();
				if(valueForNewTableTextBox !=null){
					updateByKeysTextBox.setText(valueForNewTableTextBox);
				}
				showHideErrorSymbol(widgets);
			}
		});
		
	}
	

	/**
	 * Propogates the schema from GridRow
	 */
	protected List<String> getPropagatedSchema() {
		List<String> list = new ArrayList<String>();
		Schema schema = (Schema) getComponent().getProperties().get(
				Constants.SCHEMA_PROPERTY_NAME);
		if (schema != null && schema.getGridRow() != null) {
			List<GridRow> gridRows = schema.getGridRow();
			if (gridRows != null) {
				for (GridRow gridRow : gridRows) {
					list.add(gridRow.getFieldName());
				}
			}
		}
		return list;
	}
	
	/**
	 * Set the tool tip error message
	 */
	protected void setToolTipErrorMessage() {
		String toolTipErrorMessage = null;

		if (StringUtils.isBlank(updateByKeysTextBox.getText()))
			toolTipErrorMessage = "Text can not be blank";

		setToolTipMessage(toolTipErrorMessage);
	}
	
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		tempPropertyMap = new LinkedHashMap<>();
		tempPropertyMap.put(this.propertyName, updateByKeysTextBox.getText());
		
		setToolTipErrorMessage();
		return tempPropertyMap;
	}

	@Override
	public boolean isWidgetValid() {
		if (StringUtils.isNotBlank(updateByKeysTextBox.getText())) {
			return true;
		}
		return false;
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {
		widgets = widgetList;
	}

}
