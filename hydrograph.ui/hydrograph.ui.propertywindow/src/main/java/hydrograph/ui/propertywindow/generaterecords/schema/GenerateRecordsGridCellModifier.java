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

import hydrograph.ui.datastructure.property.GenerateRecordSchemaGridRow;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.GeneralGridWidgetBuilder;
import hydrograph.ui.propertywindow.widgets.utility.DataType;
import hydrograph.ui.propertywindow.widgets.utility.SchemaRowValidation;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;


/**
 * This class is used for cell modification of GenerateRecords Schema Grid.
 * 
 * @author Bitwise
 */
public class GenerateRecordsGridCellModifier implements ICellModifier {
	private Viewer viewer;
    private GenerateRecordsGridWidget generateRecordsGridWidget;
	/**
	 * Instantiates a new Generate Records Grid CellModifier.
	 * 
	 * @param viewer
	 *            the viewer
	 */
	public GenerateRecordsGridCellModifier(GenerateRecordsGridWidget generateRecordsGridWidget,Viewer viewer) {
		this.viewer = viewer;
		this.generateRecordsGridWidget=generateRecordsGridWidget;
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
	@Override
	public boolean canModify(Object element, String property) {

		GenerateRecordSchemaGridRow generateRecordsSchemaGridRow = (GenerateRecordSchemaGridRow) element;
		if (ELTSchemaGridWidget.DATEFORMAT.equals(property)) {
			if (Date.class.getCanonicalName().equalsIgnoreCase(generateRecordsSchemaGridRow.getDataTypeValue()))
				return true;
			else
				return false;
		}
		if (ELTSchemaGridWidget.SCALE.equals(property)) {
			if (BigDecimal.class.getCanonicalName().equalsIgnoreCase(generateRecordsSchemaGridRow.getDataTypeValue()))
				return true;
			else
				return false;
		}
		if (ELTSchemaGridWidget.SCALE_TYPE.equals(property))
		{
			if(DataType.BIGDECIMAL_CLASS.getValue().equals(generateRecordsSchemaGridRow.getDataTypeValue()))
				return true;
			else {
				return false; 	
			}
		}
		if (ELTSchemaGridWidget.RANGE_FROM.equals(property) || ELTSchemaGridWidget.RANGE_TO.equals(property)) {
			if (String.class.getCanonicalName().equalsIgnoreCase(generateRecordsSchemaGridRow.getDataTypeValue())
					|| Boolean.class.getCanonicalName().equalsIgnoreCase(generateRecordsSchemaGridRow.getDataTypeValue()))
				return false;
			else
				return true;
		}
		if (ELTSchemaGridWidget.PRECISION.equals(property))
		{
			if(DataType.BIGDECIMAL_CLASS.getValue().equals(generateRecordsSchemaGridRow.getDataTypeValue()))
				return true;
			else {
				return false; 	
			}
		}
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
	@Override
	public Object getValue(Object element, String property) {
		GenerateRecordSchemaGridRow pgenerateRecordSchemaGridRow = (GenerateRecordSchemaGridRow) element;
		if (ELTSchemaGridWidget.FIELDNAME.equals(property))
			return pgenerateRecordSchemaGridRow.getFieldName();
		else if (ELTSchemaGridWidget.DATEFORMAT.equals(property))
			return String.valueOf(pgenerateRecordSchemaGridRow.getDateFormat());
		else if (ELTSchemaGridWidget.PRECISION.equals(property))
			return pgenerateRecordSchemaGridRow.getPrecision();
		else if (ELTSchemaGridWidget.SCALE_TYPE.equals(property))
			return pgenerateRecordSchemaGridRow.getScaleType();
		else if (ELTSchemaGridWidget.SCALE.equals(property))
			return String.valueOf(pgenerateRecordSchemaGridRow.getScale());
		else if (ELTSchemaGridWidget.DATATYPE.equals(property))
			return pgenerateRecordSchemaGridRow.getDataType();
		else if (ELTSchemaGridWidget.FIELD_DESCRIPTION.equals(property))
			return pgenerateRecordSchemaGridRow.getDescription();
		else if (ELTSchemaGridWidget.LENGTH.equals(property))
			return pgenerateRecordSchemaGridRow.getLength();
		else if (ELTSchemaGridWidget.RANGE_FROM.equals(property))
			return pgenerateRecordSchemaGridRow.getRangeFrom();
		else if (ELTSchemaGridWidget.RANGE_TO.equals(property))
			return pgenerateRecordSchemaGridRow.getRangeTo();
		else if (ELTSchemaGridWidget.DEFAULT_VALUE.equals(property))
			return pgenerateRecordSchemaGridRow.getDefaultValue();
		else
			return null;
		
	}

	/*
	 * Modifies the GenerateRecordSchemaGridRow object by cell data
	 * 
	 * @param element current item
	 * 
	 * @param property property to modify
	 * 
	 * @param value modified value
	 */
	@Override
	public void modify(Object element, String property, Object value) {
		Object object=null;
		if (element instanceof Item)
		{	
			object=((Item) element).getData();
		}	

		GenerateRecordSchemaGridRow generateRecordSchemaGridRow = (GenerateRecordSchemaGridRow) object;
		if (generateRecordSchemaGridRow != null) {
		if (ELTSchemaGridWidget.FIELDNAME.equals(property))
			generateRecordSchemaGridRow.setFieldName(((String) value).trim());
		else if (ELTSchemaGridWidget.DATEFORMAT.equals(property))
			generateRecordSchemaGridRow.setDateFormat(((String) value).trim());
		else if (ELTSchemaGridWidget.PRECISION.equals(property))
			generateRecordSchemaGridRow.setPrecision(((String) value).trim()); 
		else if (ELTSchemaGridWidget.SCALE.equals(property))
			generateRecordSchemaGridRow.setScale(((String) value).trim());
		else if (ELTSchemaGridWidget.SCALE_TYPE.equals(property)) {
			generateRecordSchemaGridRow.setScaleType(((Integer) value));
			generateRecordSchemaGridRow.setScaleTypeValue(GeneralGridWidgetBuilder.getScaleTypeValue()[(Integer) value]);
		}
		else if (ELTSchemaGridWidget.DATATYPE.equals(property)) {
			if(StringUtils.equals(DataType.BIGDECIMAL_CLASS.getValue(), GeneralGridWidgetBuilder.getDataTypeValue()[(Integer)value]))
			{
				generateRecordSchemaGridRow.setScaleType(2); 
				generateRecordSchemaGridRow.setScaleTypeValue(GeneralGridWidgetBuilder.getScaleTypeValue()[2]);
				generateRecordSchemaGridRow.setScale(String.valueOf(1));
			}
			generateRecordSchemaGridRow.setDataType((Integer) value);
			generateRecordSchemaGridRow.setDataTypeValue(GeneralGridWidgetBuilder.getDataTypeValue()[(Integer) value]);
		} else if (ELTSchemaGridWidget.FIELD_DESCRIPTION.equals(property)) {
			generateRecordSchemaGridRow.setDescription((((String) value).trim()));
		}
		else if (ELTSchemaGridWidget.LENGTH.equals(property)) {
			generateRecordSchemaGridRow.setLength(((String) value).trim());
		} else if (ELTSchemaGridWidget.RANGE_FROM.equals(property)) {
			generateRecordSchemaGridRow.setRangeFrom(((String) value).trim());
		} else if (ELTSchemaGridWidget.RANGE_TO.equals(property)) {
			generateRecordSchemaGridRow.setRangeTo(((String) value).trim());
		} else if (ELTSchemaGridWidget.DEFAULT_VALUE.equals(property)) {
			generateRecordSchemaGridRow.setDefaultValue(((String) value).trim());
		}
		
		if (isResetNeeded(generateRecordSchemaGridRow, property)){
			generateRecordSchemaGridRow.setScale("");
			generateRecordSchemaGridRow.setScaleTypeValue(GeneralGridWidgetBuilder.getScaleTypeValue()[0]);
			generateRecordSchemaGridRow.setScaleType(0);
			generateRecordSchemaGridRow.setPrecision("");
		}
		resetDateFormat(generateRecordSchemaGridRow, property);
		viewer.refresh();
		SchemaRowValidation.INSTANCE.highlightInvalidRowWithRedColor(generateRecordSchemaGridRow,(TableItem)element,generateRecordsGridWidget.getTable(), generateRecordsGridWidget.getComponentType());
	}	
		generateRecordsGridWidget.showHideErrorSymbol(generateRecordsGridWidget.isWidgetValid());
	}

	private void resetDateFormat(GenerateRecordSchemaGridRow generateRecordSchemaGridRow, String property){
		if(ELTSchemaGridWidget.DATATYPE.equals(property) && StringUtils.isNotBlank(generateRecordSchemaGridRow.getDataTypeValue())){
			if(!(DataType.DATE_CLASS.equals(generateRecordSchemaGridRow.getDataTypeValue()))){
				generateRecordSchemaGridRow.setDateFormat("");
			}

		}
	}
	
	private boolean isResetNeeded(GenerateRecordSchemaGridRow generateRecordSchemaGridRow, String property) {
		if(ELTSchemaGridWidget.DATATYPE.equals(property) && StringUtils.isNotBlank(generateRecordSchemaGridRow.getDataTypeValue())){
			if(DataType.INTEGER_CLASS.equals(generateRecordSchemaGridRow.getDataTypeValue()) 
					||DataType.LONG_CLASS.equals(generateRecordSchemaGridRow.getDataTypeValue())
					||DataType.STRING_CLASS.equals(generateRecordSchemaGridRow.getDataTypeValue())
					||DataType.SHORT_CLASS.equals(generateRecordSchemaGridRow.getDataTypeValue())
					||DataType.BOOLEAN_CLASS.equals(generateRecordSchemaGridRow.getDataTypeValue())
					||DataType.FLOAT_CLASS.equals(generateRecordSchemaGridRow.getDataTypeValue())
					||DataType.DOUBLE_CLASS.equals(generateRecordSchemaGridRow.getDataTypeValue())
					||DataType.DATE_CLASS.equals(generateRecordSchemaGridRow.getDataTypeValue())){
				return true;
			}	
		}
		return false;
	}

}
