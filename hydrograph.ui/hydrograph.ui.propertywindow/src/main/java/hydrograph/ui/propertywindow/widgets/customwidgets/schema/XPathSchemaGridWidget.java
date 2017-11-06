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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.TextBoxWithLabelWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.TextBoxWithLableConfig;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.listeners.grid.schema.ELTCellEditorFieldValidator;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * Class for XPathSchemaGridWidget.
 * 
 * @author Bitwise
 */
public class XPathSchemaGridWidget extends ELTSchemaGridWidget {
	/**
	 * Instantiates a new ELT XPath schema grid widget.
	 * 
	 * @param componentConfigrationProperty
	 *            the component configration property
	 * @param componentMiscellaneousProperties
	 *            the component miscellaneous properties
	 * @param propertyDialogButtonBar
	 *            the property dialog button bar
	 */
	public XPathSchemaGridWidget(ComponentConfigrationProperty componentConfigrationProperty,
			ComponentMiscellaneousProperties componentMiscellaneousProperties, PropertyDialogButtonBar propertyDialogButtonBar) {
		super(componentConfigrationProperty, componentMiscellaneousProperties,propertyDialogButtonBar);
		this.gridRowType = Messages.XPATH_GRID_ROW;
	}
	
	@Override
	protected Map<String, Integer> getPropertiesToShow() {
		Map<String, Integer> columns = new HashMap<>();
		columns.put(FIELDNAME, 0);
		columns.put(DATATYPE, 1);
		columns.put(XPATH, 2);
		columns.put(SCALE, 3);
		columns.put(SCALE_TYPE, 4);
		columns.put(DATEFORMAT, 5);
		columns.put(PRECISION, 6);
		columns.put(FIELD_DESCRIPTION, 7);
	
		return columns;
		//sequence: FIELDNAME, DATATYPE, XPATH, SCALE, SCALE_TYPE, DATEFORMAT, PRECISION, FIELD_DESCRIPTION
	}

	@Override
	protected XPathGridWidgetBuilder getGridWidgetBuilder() {
		return XPathGridWidgetBuilder.INSTANCE;
	}
	
	protected IStructuredContentProvider getContentProvider() {
		return new IStructuredContentProvider() {
			
			@Override
			public Object[] getElements(Object inputElement) {
				  return ((List) inputElement).toArray();
			}
		};
	}
	
	protected ITableLabelProvider getLableProvider() {
		
		return new ITableLabelProvider() {
			
			@Override
			public void removeListener(ILabelProviderListener listener) {
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			
			@Override
			public void dispose() {
			}
			
			@Override
			public void addListener(ILabelProviderListener listener) {
			}
			
			@Override
			public String getColumnText(Object element, int columnIndex) {
				XPathGridRow xPathGrid = (XPathGridRow) element;
				
				switch (columnIndex) {
				case 0:
					return xPathGrid.getFieldName(); 
				case 1:
					return GridWidgetCommonBuilder.getDataTypeKey()[xPathGrid.getDataType().intValue()];   
				case 2:
					return xPathGrid.getXPath();
				case 3: 
					return xPathGrid.getScale().toString();
				case 4:
					if(xPathGrid.getScaleType()!=null)
					{
					return GridWidgetCommonBuilder.getScaleTypeKey()[xPathGrid.getScaleType().intValue()];  
					}
					else
					{
						return GridWidgetCommonBuilder.getScaleTypeKey()[0];
					}
				case 5:
					return xPathGrid.getDateFormat();
				case 6: 
					return xPathGrid.getPrecision(); 
				case 7: 
					return xPathGrid.getDescription();
				}
				return null;
			}
			
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		};
	}
	
	protected XPathSchemaGridCellModifier getCellModifier() {
		return new XPathSchemaGridCellModifier(this,tableViewer);
	}

	@Override
	protected void addValidators() {
	    editors[0].setValidator(new ELTCellEditorFieldValidator(table, schemaGridRowList, fieldNameDecorator,
	    		isFieldNameAlphanumericDecorator,propertyDialogButtonBar));
	}
	
	//Adding the decorator to show error message when field name same.
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

	public void validateInternalSchemaPropogatedData(Schema propogatedSchema)
	{	
		showHideErrorSymbol(validateAgainstValidationRule(propogatedSchema));
		
	}
		
	@Override
	public void addModifyListener(final Property property,  ArrayList<AbstractWidget> widgetList) {
		for(AbstractWidget widget:widgetList){
			if(widget instanceof TextBoxWithLabelWidget){
				TextBoxWithLabelWidget textBoxWithLabelWidget=((TextBoxWithLabelWidget)widget);
				TextBoxWithLableConfig textBoxWithLableConfig=textBoxWithLabelWidget.getWidgetConfig();
				if(Messages.LOOP_XPATH_QUERY.equals(textBoxWithLableConfig.getName())){
					table.setData(textBoxWithLabelWidget.getTextBox());
					break;
				}
			}
		}
	    attachListener();
	}
}
