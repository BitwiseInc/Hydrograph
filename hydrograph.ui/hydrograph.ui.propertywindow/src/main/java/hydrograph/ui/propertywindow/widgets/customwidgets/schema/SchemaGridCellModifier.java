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

import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.propertywindow.widgets.utility.DataType;
import hydrograph.ui.propertywindow.widgets.utility.SchemaRowValidation;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;



/**
 * @author Bitwise
 * This class represents the cell modifier for the SchemaEditor program
 */

public class SchemaGridCellModifier implements ICellModifier {
	private Viewer viewer;
    private ELTSchemaGridWidget eltGenericSchemaGridWidget;
	/**
	 * Instantiates a new schema grid cell modifier.
	 * 
	 * @param viewer
	 *            the viewer
	 */
	public SchemaGridCellModifier(ELTSchemaGridWidget eltGenericSchemaGridWidget,Viewer viewer) {
		this.viewer = viewer;
		this.eltGenericSchemaGridWidget=eltGenericSchemaGridWidget;
	}


	@Override
	public boolean canModify(Object element, String property) {
		// Allow editing of all values
		BasicSchemaGridRow basicSchemaGridRow = (BasicSchemaGridRow) element;
		if (ELTSchemaGridWidget.DATEFORMAT.equals(property))
		{
			if(DataType.DATE_CLASS.equals(basicSchemaGridRow.getDataTypeValue()))
				return true;
			else 
				return false; 	
		}
		if (ELTSchemaGridWidget.SCALE.equals(property))
		{
			if(DataType.BIGDECIMAL_CLASS.getValue().equals(basicSchemaGridRow.getDataTypeValue()))
				return true;
			else {
				return false; 	
			}
		}

		if (ELTSchemaGridWidget.SCALE_TYPE.equals(property))
		{
			if(DataType.BIGDECIMAL_CLASS.getValue().equals(basicSchemaGridRow.getDataTypeValue()))
				return true;
			else {
				return false; 	
			}
		}

		if (ELTSchemaGridWidget.PRECISION.equals(property))
		{
			if(DataType.BIGDECIMAL_CLASS.getValue().equals(basicSchemaGridRow.getDataTypeValue()))
				return true;
			else {
				return false; 	
			}
		}

		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		BasicSchemaGridRow basicSchemaGridRow = (BasicSchemaGridRow) element;
		if (ELTSchemaGridWidget.FIELDNAME.equals(property))
			return basicSchemaGridRow.getFieldName();
		else if (ELTSchemaGridWidget.DATATYPE.equals(property))
			return basicSchemaGridRow.getDataType();
		else if (ELTSchemaGridWidget.DATEFORMAT.equals(property))
			return String.valueOf(basicSchemaGridRow.getDateFormat());
		else if (ELTSchemaGridWidget.PRECISION.equals(property))
			return basicSchemaGridRow.getPrecision();
		else if (ELTSchemaGridWidget.SCALE.equals(property))
			return String.valueOf(basicSchemaGridRow.getScale());
		else if (ELTSchemaGridWidget.SCALE_TYPE.equals(property))
			return basicSchemaGridRow.getScaleType();
		else if (ELTSchemaGridWidget.FIELD_DESCRIPTION.equals(property))
			return basicSchemaGridRow.getDescription();

		else
			return null;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		Object object=null;
		if (element instanceof Item)
		{
			object = ((Item) element).getData();
			
		}
		BasicSchemaGridRow basicSchemaGridRow = (BasicSchemaGridRow) object;
		if (basicSchemaGridRow != null) {
		if (ELTSchemaGridWidget.FIELDNAME.equals(property))
			basicSchemaGridRow.setFieldName(((String) value).trim());
		else if (ELTSchemaGridWidget.DATATYPE.equals(property)){
			if(StringUtils.equals(DataType.BIGDECIMAL_CLASS.getValue(), GeneralGridWidgetBuilder.getDataTypeValue()[(Integer)value]))
			{
				basicSchemaGridRow.setScaleType(2); 
				basicSchemaGridRow.setScaleTypeValue(GeneralGridWidgetBuilder.getScaleTypeValue()[2]);
				basicSchemaGridRow.setScale(String.valueOf(1));
			}
			basicSchemaGridRow.setDataType((Integer)value);
			basicSchemaGridRow.setDataTypeValue(GeneralGridWidgetBuilder.getDataTypeValue()[(Integer)value]);
		}
		else if (ELTSchemaGridWidget.DATEFORMAT.equals(property))
		{
			basicSchemaGridRow.setDateFormat( ((String) value).trim()); 
		}	
		else if (ELTSchemaGridWidget.PRECISION.equals(property))
		{	
			basicSchemaGridRow.setPrecision(((String) value).trim()); 
		}
		else if (ELTSchemaGridWidget.SCALE.equals(property))
			{
			 basicSchemaGridRow.setScale(((String) value).trim());
			}
			
		else if (ELTSchemaGridWidget.SCALE_TYPE.equals(property)){
			basicSchemaGridRow.setScaleType((Integer)value); 
			basicSchemaGridRow.setScaleTypeValue(GeneralGridWidgetBuilder.getScaleTypeValue()[(Integer)value]);
			
		}
		else if (ELTSchemaGridWidget.FIELD_DESCRIPTION.equals(property))
			basicSchemaGridRow.setDescription(((String) value).trim());


		if (isResetNeeded(basicSchemaGridRow, property)){
			basicSchemaGridRow.setScale("");
			basicSchemaGridRow.setScaleTypeValue(GeneralGridWidgetBuilder.getScaleTypeValue()[0]);
			basicSchemaGridRow.setScaleType(0);
			basicSchemaGridRow.setPrecision("");
		}
		resetDateFormat(basicSchemaGridRow, property);
		viewer.refresh();
		SchemaRowValidation.INSTANCE.highlightInvalidRowWithRedColor(basicSchemaGridRow, (TableItem)element,eltGenericSchemaGridWidget.getTable(), eltGenericSchemaGridWidget.getComponentType());
	}
		eltGenericSchemaGridWidget.showHideErrorSymbol(eltGenericSchemaGridWidget.isWidgetValid());
		eltGenericSchemaGridWidget.setSchemaUpdated(true);
	}


	private void resetDateFormat(BasicSchemaGridRow row, String property){
		if(ELTSchemaGridWidget.DATATYPE.equals(property) && StringUtils.isNotBlank(row.getDataTypeValue())){
			if(!(DataType.DATE_CLASS.equals(row.getDataTypeValue()))){
				row.setDateFormat("");
			}

		}
	}


	private boolean isResetNeeded(BasicSchemaGridRow basicSchemaGridRow, String property) {
		if(ELTSchemaGridWidget.DATATYPE.equals(property) && StringUtils.isNotBlank(basicSchemaGridRow.getDataTypeValue())){
			if(DataType.INTEGER_CLASS.equals(basicSchemaGridRow.getDataTypeValue()) 
					||DataType.LONG_CLASS.equals(basicSchemaGridRow.getDataTypeValue())
					||DataType.STRING_CLASS.equals(basicSchemaGridRow.getDataTypeValue())
					||DataType.SHORT_CLASS.equals(basicSchemaGridRow.getDataTypeValue())
					||DataType.BOOLEAN_CLASS.equals(basicSchemaGridRow.getDataTypeValue())
					||DataType.FLOAT_CLASS.equals(basicSchemaGridRow.getDataTypeValue())
					||DataType.DOUBLE_CLASS.equals(basicSchemaGridRow.getDataTypeValue())
					||DataType.DATE_CLASS.equals(basicSchemaGridRow.getDataTypeValue())){
				return true;
			}	
		}
		return false;
	}


}