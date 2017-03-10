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

 
package hydrograph.ui.propertywindow.generaterecords.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.SchemaGridContentProvider;
import hydrograph.ui.propertywindow.widgets.listeners.grid.schema.ELTCellEditorFieldValidator;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;



/**
 * This class is used to configure GenerateRecords Schema Grid Widget.
 * 
 * @author Bitwise
 */
public class GenerateRecordsGridWidget extends ELTSchemaGridWidget {

	public GenerateRecordsGridWidget(PropertyDialogButtonBar propertyDialogButtonBar) {
		this.propertyDialogButtonBar = propertyDialogButtonBar;
		this.gridRowType = Messages.GENERATE_RECORD_GRID_ROW;
	}

	/**
	 * Instantiates a new GenerateRecordsGridWidget.
	 *
	 */
	public GenerateRecordsGridWidget(ComponentConfigrationProperty componentConfigurationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigurationProperty, componentMiscellaneousProperties, propertyDialogButtonBar);
		this.gridRowType = Messages.GENERATE_RECORD_GRID_ROW;
	}

	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget#getPropertiesToShow()
	 */
	@Override
	protected Map<String, Integer> getPropertiesToShow() {
		Map<String, Integer> columns = new HashMap<>();
		columns.put(FIELDNAME, 0);
		columns.put(DATATYPE, 1);
		columns.put(DATEFORMAT, 2);
		columns.put(PRECISION, 3);
		columns.put(SCALE, 4);
		columns.put(SCALE_TYPE, 5);
		columns.put(FIELD_DESCRIPTION, 6);
		columns.put(LENGTH, 7);
		columns.put(RANGE_FROM, 8);
		columns.put(RANGE_TO, 9);
		columns.put(DEFAULT_VALUE, 10);
		return columns;
		//return new String[]{ FIELDNAME, DATATYPE, DATEFORMAT, PRECISION, SCALE, SCALE_TYPE, FIELD_DESCRIPTION, LENGTH, RANGE_FROM, RANGE_TO, DEFAULT_VALUE };
	}

	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget#getGridWidgetBuilder()
	 */
	@Override
	protected GenerateRecordsGridWidgetBuilder getGridWidgetBuilder() {
		return GenerateRecordsGridWidgetBuilder.INSTANCE;
	}

	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget#getContentProvider()
	 */
	protected SchemaGridContentProvider getContentProvider() {
		return new SchemaGridContentProvider();
	}

	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget#getLableProvider()
	 */
	protected GenerateRecordsGridLabelProvider getLableProvider() {
		return new GenerateRecordsGridLabelProvider();
	}

	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget#getCellModifier()
	 */
	protected GenerateRecordsGridCellModifier getCellModifier() {
		return new GenerateRecordsGridCellModifier(this,tableViewer);
	}

	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget#addValidators()
	 */
	@Override
	protected void addValidators() {
		
		editors[0].setValidator(new ELTCellEditorFieldValidator(table, schemaGridRowList, fieldNameDecorator,isFieldNameAlphanumericDecorator,propertyDialogButtonBar));
	}

	
	/* (non-Javadoc)
	 * @see hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget#setDecorator()
	 */
	@Override
	protected void setDecorator() {
		fieldNameDecorator = WidgetUtility.addDecorator(editors[0].getControl(), Messages.FIELDNAMEERROR);
		isFieldNameAlphanumericDecorator = WidgetUtility.addDecorator(editors[0].getControl(),
				Messages.FIELDNAME_NOT_ALPHANUMERIC_ERROR);
	}

	@Override
	public boolean isWidgetValid() {
		return applySchemaValidationRule();
	}

	

	@Override
	public void addModifyListener(Property property,  ArrayList<AbstractWidget> widgetList) {
		attachListener();
		
	}
}