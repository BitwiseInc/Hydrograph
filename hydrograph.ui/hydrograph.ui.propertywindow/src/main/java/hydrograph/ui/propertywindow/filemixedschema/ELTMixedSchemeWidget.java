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

package hydrograph.ui.propertywindow.filemixedschema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.SchemaGridContentProvider;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.listeners.grid.schema.ELTCellEditorFieldValidator;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * The Class ELTMixedScheme.
 * 
 * @author Bitwise
 */
public class ELTMixedSchemeWidget extends ELTSchemaGridWidget {
	
	
	public ELTMixedSchemeWidget(PropertyDialogButtonBar propertyDialogButtonBar) {
		this.propertyDialogButtonBar = propertyDialogButtonBar;
		this.gridRowType = Messages.MIXEDSCHEME_GRID_ROW;
	}
	
	/**
	 * Instantiates a new ELT Mixed scheme.
	 * 
	 * @param componentConfigrationProperty
	 *            the component configration property
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public ELTMixedSchemeWidget(ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties, PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties, propertyDialogButtonBar);
		this.gridRowType = Messages.MIXEDSCHEME_GRID_ROW;
	}	
	

	@Override
	protected Map<String, Integer> getPropertiesToShow() {
		Map<String, Integer> columns = new HashMap<>();
		columns.put(FIELDNAME, 0);
		columns.put(DATATYPE, 1);
		columns.put(LENGTH, 2);
		columns.put(DELIMITER, 3);
		columns.put(SCALE, 4);
		columns.put(SCALE_TYPE, 5);
		columns.put(DATEFORMAT, 6);
		columns.put(PRECISION, 7);
		columns.put(FIELD_DESCRIPTION, 8);
		return columns;
	}

	@Override
	protected MixedSchemeGridWidgetBuilder getGridWidgetBuilder() {
		return MixedSchemeGridWidgetBuilder.INSTANCE;
	}

	@Override
	protected SchemaGridContentProvider getContentProvider() {
		return new SchemaGridContentProvider();
	}

	@Override
	protected MixedSchemeGridLabelProvider getLableProvider() {
		return new MixedSchemeGridLabelProvider();
	}

	@Override
	protected MixedSchemeGridCellModifier getCellModifier() {
		return new MixedSchemeGridCellModifier(this,tableViewer);
	}

	@Override
	protected void addValidators() {
		editors[0].setValidator(new ELTCellEditorFieldValidator(table, schemaGridRowList, fieldNameDecorator,isFieldNameAlphanumericDecorator,propertyDialogButtonBar));
	}

	@Override
	protected void setDecorator() {
		fieldNameDecorator = WidgetUtility.addDecorator(editors[0].getControl(),Messages.FIELDNAMEERROR);
		isFieldNameAlphanumericDecorator=WidgetUtility.addDecorator(editors[0].getControl(),Messages.FIELDNAME_NOT_ALPHANUMERIC_ERROR);
		fieldNameDecorator.setMarginWidth(8);
		isFieldNameAlphanumericDecorator.setMarginWidth(8);

	}
	
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		super.attachToPropertySubGroup(container);
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
