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
package hydrograph.ui.propertywindow.filter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import hydrograph.ui.common.datastructure.filter.FilterLogicDataStructure;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.SingleColumnGridConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;

/**
 * The Class FilterLogic
 * 
 * @author Bitwise
 *
 */
public class FilterLogicWidget extends AbstractWidget {
	protected String propertyName;
	protected SingleColumnGridConfig gridConfig = null;
	private ArrayList<AbstractWidget> widgets;
	private FilterLogicDataStructure dataStructure;
	Button button;

	public FilterLogicWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
		this.propertyName = componentConfigProp.getPropertyName();
		if (componentConfigProp.getPropertyValue() != null) {
			dataStructure = (FilterLogicDataStructure) componentConfigProp.getPropertyValue();
		} else {
			dataStructure = new FilterLogicDataStructure(Constants.FILTER);
		}
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget subGroup) {
		ELTDefaultSubgroupComposite defaultSubgroupComposite = new ELTDefaultSubgroupComposite(
				subGroup.getContainerControl());
		defaultSubgroupComposite.createContainerWidget();
		dataStructure.setComponentName(getComponent().getComponentName());
		AbstractELTWidget defaultLable = null;
		if(StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.PARTITION_BY_EXPRESSION)){
			defaultLable=new ELTDefaultLable("Partition Logic");
		}else {
			defaultLable=new ELTDefaultLable("Filter Logic");
		}
		defaultSubgroupComposite.attachWidget(defaultLable);
		setPropertyHelpWidget((Control) defaultLable.getSWTWidgetControl());

		AbstractELTWidget defaultButton;
		if (OSValidator.isMac()) {
			defaultButton = new ELTDefaultButton(Constants.EDIT).buttonWidth(120);
		} else {
			defaultButton = new ELTDefaultButton(Constants.EDIT);
		}
		defaultSubgroupComposite.attachWidget(defaultButton);
		button = (Button) defaultButton.getSWTWidgetControl();
		addSelectionListerner();

	}

	private void addSelectionListerner() {
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FilterLogicDataStructure clonedDataStructure = (FilterLogicDataStructure) dataStructure.clone();
				FilterExpressionOperationDialog dialog = new FilterExpressionOperationDialog(
						Display.getCurrent().getActiveShell(), clonedDataStructure, getComponent(),
						propertyDialogButtonBar, widgetConfig, getInputSchema());
				
				if (StringUtils.equalsIgnoreCase(getComponent().getComponentName(), Constants.FILTER)) {
					dialog.setTitle("Filter Editor");
				} else if (StringUtils.equalsIgnoreCase(getComponent().getComponentName(),
						Constants.PARTITION_BY_EXPRESSION)) {
					dialog.setTitle("Partition Logic Editor");
				}

				if (dialog.open() == IDialogConstants.OK_ID) {
					dataStructure=clonedDataStructure;
				}
				showHideErrorSymbol(widgets);
				
				if(dialog.isYesButtonPressed()) {
					propertyDialog.pressOK();
				}
				else if(dialog.isNoButtonPressed()) {
					propertyDialog.pressCancel();
				}
			}
		});
	}

	private List<FixedWidthGridRow> getInputSchema() {
		ELTSchemaGridWidget schemaWidget = null;
		for (AbstractWidget abstractWidget : widgets) {
			if (abstractWidget instanceof ELTSchemaGridWidget) {
				schemaWidget = (ELTSchemaGridWidget) abstractWidget;
				break;
			}
		}
		if (schemaWidget != null) {
			schemaWidget.refresh();
		}
		List<GridRow> gridRowList = (List<GridRow>) schemaWidget.getTableViewer().getInput();
		return SchemaSyncUtility.INSTANCE.convertGridRowsSchemaToFixedSchemaGridRows(gridRowList);
	}

	@Override
	public LinkedHashMap<String, Object> getProperties() {
		LinkedHashMap<String, Object> property = new LinkedHashMap<>();
		property.put(propertyName, dataStructure);
		return property;
	}

	@Override
	public boolean isWidgetValid() {
		return validateAgainstValidationRule(getProperties().get(propertyName));
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {
		widgets = widgetList;
	}

}
