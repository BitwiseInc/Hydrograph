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

 
package hydrograph.ui.propertywindow.widgets.listeners.grid.schema;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;

import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Table;


/**
 * The Class ELTCellEditorFieldValidator.
 * 
 * @author Bitwise
 */
public class ELTCellEditorFieldValidator implements ICellEditorValidator {

	private Table table;
	private List schemaGrids;
	private ControlDecoration fieldNameDecorator;
	private ControlDecoration isFieldNameAlphanumericDecorator;
	private PropertyDialogButtonBar propertyDialogButtonBar;

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
	public ELTCellEditorFieldValidator(Table table, List schemaGrids,
			ControlDecoration fieldNameDecorator,ControlDecoration isFieldNameAlphanumericDecorator,PropertyDialogButtonBar propertyDialogButtonBar) {
		super();
		this.table = table;
		this.schemaGrids = schemaGrids;
		this.fieldNameDecorator = fieldNameDecorator;
		this.isFieldNameAlphanumericDecorator = isFieldNameAlphanumericDecorator;
		this.propertyDialogButtonBar=propertyDialogButtonBar;
	}

	@Override
	public String isValid(Object value) {
		String fieldName=(String) value;
		if(fieldName.equals("")){     
			fieldNameDecorator.show();   
			return "Error";   
		}else{  
			fieldNameDecorator.hide(); 
			if(isFieldNameAlphanumeric(fieldName))
				isFieldNameAlphanumericDecorator.hide();
			else{
				isFieldNameAlphanumericDecorator.show();
				return "Error";
			}
		}
		String selectedGrid = table.getItem(table.getSelectionIndex()).getText();
		for (int i = 0; i < schemaGrids.size(); i++) {
			GridRow schemaGrid = (GridRow)schemaGrids.get(i);
			String stringValue = (String) value;
			if ((schemaGrid.getFieldName().equalsIgnoreCase(stringValue) &&
					!selectedGrid.equalsIgnoreCase(stringValue))) {

				fieldNameDecorator.show();
				/*propertyDialogButtonBar.enableOKButton(false);
				propertyDialogButtonBar.enableApplyButton(false);*/
				return "Error";
			}
			else{ 
				fieldNameDecorator.hide();
				/*propertyDialogButtonBar.enableOKButton(true);
				propertyDialogButtonBar.enableApplyButton(true);*/
			}
		}
		return null;
	}

	private boolean isFieldNameAlphanumeric(String fieldName){
		return (!fieldName.matches(Constants.REGEX))?false:true;
	}
}
