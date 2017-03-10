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

 
package hydrograph.ui.propertywindow.widgets.listeners.grid;

import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.OperationField;
import hydrograph.ui.datastructure.property.OperationSystemProperties;
import hydrograph.ui.datastructure.property.PropertyField;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;

import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Table;


// TODO: Auto-generated Javadoc
/**
 * The Class ELTCellEditorFieldValidator.
 * 
 * @author Bitwise
 */
public class ELTCellEditorTransformValidator implements ICellEditorValidator {

	private Table table;
	private List grids;
	private ControlDecoration fieldNameDecorator;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private boolean isSingleColumn;

	/**
	 * Instantiates a new ELT cell editor field validator.
	 * 
	 * @param table
	 *            the table
	 * @param schemaGrids
	 *            the schema grids
	 * @param fieldNameDecorator
	 *            the field name decorator
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public ELTCellEditorTransformValidator(Table table, List grids,
			ControlDecoration fieldNameDecorator,
			PropertyDialogButtonBar propertyDialogButtonBar,
			boolean isSingleColumn) {
		super();
		this.table = table;
		this.grids = grids;
		this.fieldNameDecorator = fieldNameDecorator;
		this.propertyDialogButtonBar = propertyDialogButtonBar;
		this.isSingleColumn = isSingleColumn;
	}

	@Override
	public String isValid(Object value) {
		String selectedGrid = table.getItem(table.getSelectionIndex())
				.getText();
		String validValue = (String) value;
		PropertyField propertyField;
		if (isSingleColumn) {
			if (grids.get(0) instanceof OperationSystemProperties) {
				OperationSystemProperties opSProperties = new OperationSystemProperties();
				opSProperties.setOpSysValue(validValue);
				propertyField=opSProperties;
			} else {
				OperationField opField = new OperationField();
				opField.setName(validValue);
				propertyField = opField;
			}
		} else { 
			NameValueProperty nameValueProperty = new NameValueProperty();
			nameValueProperty.setPropertyName(validValue);
			propertyField = nameValueProperty;

		}
		if (grids.contains(propertyField)) {
			fieldNameDecorator.show();
			return "error";
		} else
			fieldNameDecorator.hide();
		
		return null;
	}

}
