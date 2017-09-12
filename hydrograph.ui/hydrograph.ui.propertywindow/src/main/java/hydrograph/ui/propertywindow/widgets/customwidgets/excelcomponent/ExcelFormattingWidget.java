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
package hydrograph.ui.propertywindow.widgets.customwidgets.excelcomponent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.ExcelFormattingDataStructure;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
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
 * ExcelFormattingWidget for Excel Component
 * @author Bitwise
 *
 */
public class ExcelFormattingWidget extends AbstractWidget{
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ExcelFormattingWidget.class);
	private String propertyName;
	private ExcelFormattingDataStructure excelFormattingDataStructure;
	private Shell shell;
	private RuntimeConfig runtimeConfig;
	private PropertyDialogButtonBar propDialogButtonBar;
	private List<String> schemaFields;
	private ArrayList<AbstractWidget> widgets;
	/**
	 * Instantiates a new ELTLoadTypeConfiguration widget.
	 * 
	 * @param componentConfigProp
	 *            the component configration property
	 * @param componentMiscProps
	 *            the component miscellaneous properties
	 * @param propDialogButtonBar
	 *            the property dialog button bar
	 */
	public ExcelFormattingWidget(
			ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps,
			PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);

		this.propertyName = componentConfigProp.getPropertyName();
		this.excelFormattingDataStructure = (ExcelFormattingDataStructure) componentConfigProp
				.getPropertyValue();
		this.propDialogButtonBar = propDialogButtonBar;
		if(excelFormattingDataStructure==null){
			this.excelFormattingDataStructure = new ExcelFormattingDataStructure();
		}
	}
	
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget subGroup) {
		ELTDefaultSubgroupComposite excelFormattingComposite = new ELTDefaultSubgroupComposite(
				subGroup.getContainerControl());
		excelFormattingComposite.createContainerWidget();
		shell = excelFormattingComposite.getContainerControl().getShell();
		runtimeConfig = (RuntimeConfig) widgetConfig;

		ELTDefaultLable defaultLable1 = new ELTDefaultLable(
				runtimeConfig.getLabel());
		excelFormattingComposite.attachWidget(defaultLable1);
		setPropertyHelpWidget((Control) defaultLable1.getSWTWidgetControl());

		ELTDefaultButton eltDefaultButton = new ELTDefaultButton(Constants.EDIT);
		if(OSValidator.isMac()){
			eltDefaultButton.buttonWidth(120);
		}
		excelFormattingComposite.attachWidget(eltDefaultButton);


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
		tempPropertyMap.put(this.propertyName, this.excelFormattingDataStructure);
		setToolTipErrorMessage();
		showHideErrorSymbol(widgets);
		return tempPropertyMap;
	}

	@Override
	public boolean isWidgetValid() {
		return validateAgainstValidationRule(this.excelFormattingDataStructure);
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {
		this.widgets=widgetList;
		
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

	public void newWindowLauncher() {
		schemaFields=getPropagatedSchema();
		ExcelFormattingDialog excelFormattingDialog = new ExcelFormattingDialog(shell,runtimeConfig.getWindowLabel(),schemaFields,propDialogButtonBar,this.excelFormattingDataStructure);
		excelFormattingDialog.open();
		if(excelFormattingDialog.getExcelFormattingData() !=null){
			this.excelFormattingDataStructure = excelFormattingDialog.getExcelFormattingData();
			propDialogButtonBar.enableApplyButton(true);
		}
	}
	
	/**
	 * Set the tool tip error message
	 */
	protected void setToolTipErrorMessage() {
		String toolTipErrorMessage = null;

		setToolTipMessage(toolTipErrorMessage);
	}

}
