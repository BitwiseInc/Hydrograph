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


package hydrograph.ui.propertywindow.widgets.customwidgets.operational;

import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.dialogs.ELTOperationClassDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;


/**
 * @author Bitwise
 * This class represents the cell modifier for the SchemaEditor program
 */

public class PropertyGridCellModifier implements ICellModifier {
	private Viewer viewer;
	TransformDialog transformDialog;
	OperationClassDialog operationClassDialog; 
	ELTOperationClassDialog eltOperationClassDialog;
	/** The Constant PROPERTY_NAME. */
	private static final String PROPERTY_NAME = "Source";

	/** The Constant PROPERTY_VALUE. */
	private static final String PROPERTY_VALUE = "Target";
	private PropertyDialogButtonBar propertyDialogButtonBar ;
	/**
	 * Instantiates a new schema grid cell modifier.
	 * 
	 * @param viewer
	 *            the viewer
	 */
	public PropertyGridCellModifier(Viewer viewer) {
		this.viewer = viewer;
	}

	public PropertyGridCellModifier(ELTOperationClassDialog eltOperationClassDialog,Viewer viewer,PropertyDialogButtonBar propertyDialogButtonBar) {
		this.eltOperationClassDialog = eltOperationClassDialog;
		this.viewer = viewer;
		this.propertyDialogButtonBar=propertyDialogButtonBar;
	}
    
	public PropertyGridCellModifier(OperationClassDialog operationClassDialog,Viewer viewer,PropertyDialogButtonBar propertyDialogButtonBar) {
		this.viewer = viewer;
		this.propertyDialogButtonBar=propertyDialogButtonBar;
		this.operationClassDialog=operationClassDialog;
	}
	
	public PropertyGridCellModifier(TransformDialog transformDialogNew ,Viewer viewer) {
		this.viewer = viewer;
		this.transformDialog=transformDialogNew;
	}



	/**
	 * Returns whether the property can be modified
	 * 
	 * @param element
	 *            the element
	 * @param property
	 *            the property
	 * @return boolean
	 */
	public boolean canModify(Object element, String property) {
		// Allow editing of all values
		return true;
	}

	/**
	 * Returns the value for the property
	 * 
	 * @param element
	 *            the element
	 * @param property
	 *            the property
	 * @return Object
	 */ 
	public Object getValue(Object element, String property) {
		NameValueProperty nameValueProperty = (NameValueProperty) element;

		if (PROPERTY_NAME.equals(property)||Messages.PROPERTY_NAME.equals(property))
			return nameValueProperty.getPropertyName();
		else if (PROPERTY_VALUE.equals(property)||Messages.PROPERTY_VALUE.equals(property))
			return nameValueProperty.getPropertyValue();
		else
			return null;

	}

	/**
	 * Modifies the element
	 * 
	 * @param element
	 *            the element
	 * @param property
	 *            the property
	 * @param value
	 *            the value
	 */
	public void modify(Object element, String property, Object value) {
		if (element instanceof Item)
			element = ((Item) element).getData();
		
		NameValueProperty nameValueProperty = (NameValueProperty) element;
		if (PROPERTY_NAME.equals(property)){
			if(ParameterUtil.isParameter((String)value)){
				nameValueProperty.setPropertyValue((String)value);
			}
			nameValueProperty.setPropertyName(((String) value).trim());
			transformDialog.showHideValidationMessage();
		}

		if (PROPERTY_VALUE.equals(property))
		{	
			if(ParameterUtil.isParameter((String)value)){
				nameValueProperty.setPropertyName((String)value);
			}
			
			
			nameValueProperty.setPropertyValue(((String) value).trim());
			int indexOfSelectedField=transformDialog.getATMapping().getOutputFieldList().indexOf(nameValueProperty.getFilterProperty());
			nameValueProperty.getFilterProperty().setPropertyname(((String) value).trim());
			if(indexOfSelectedField==-1)
			transformDialog.getATMapping().getOutputFieldList().add(nameValueProperty.getFilterProperty());
			
			
			transformDialog.refreshOutputTable();	
			transformDialog.showHideValidationMessage();

		}
		if(Messages.PROPERTY_NAME.equals(property))
		{
			nameValueProperty.setPropertyName(((String) value).trim());
			if(propertyDialogButtonBar!=null ){
				propertyDialogButtonBar.enableApplyButton(true);
			}
			if(operationClassDialog!=null)
			{
			operationClassDialog.checkNameValueFieldBlankOrNot();	
			}
			else{
				if(eltOperationClassDialog!=null){
					eltOperationClassDialog.checkNameValueFieldBlankOrNot();
				}
			}
		}	
		if(Messages.PROPERTY_VALUE.equals(property))
		{	
			nameValueProperty.setPropertyValue(((String) value).trim());
			if(propertyDialogButtonBar!=null ){
				propertyDialogButtonBar.enableApplyButton(true);
			}
			if(operationClassDialog!=null)
			{
			operationClassDialog.checkNameValueFieldBlankOrNot();	
			}
			else{
				if(eltOperationClassDialog!=null){
					eltOperationClassDialog.checkNameValueFieldBlankOrNot();
				}
			}
		}


		viewer.refresh();
	}



}
