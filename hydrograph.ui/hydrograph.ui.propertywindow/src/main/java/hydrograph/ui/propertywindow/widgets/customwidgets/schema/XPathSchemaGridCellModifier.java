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

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.propertywindow.widgets.utility.DataType;
import hydrograph.ui.propertywindow.widgets.utility.SchemaRowValidation;



/**
 * @author Bitwise
 * This class represents the cell modifier for the SchemaEditor program
 */

public class XPathSchemaGridCellModifier implements ICellModifier {
	private TableViewer viewer;
    private XPathSchemaGridWidget xPathSchemGridWidget;
   
	/**
	 * Instantiates a new schema grid cell modifier.
	 * 
	 * @param viewer
	 *            the viewer
	 */
	public XPathSchemaGridCellModifier(XPathSchemaGridWidget eltGenericSchemaGridWidget, TableViewer viewer) {
		this.viewer = viewer;
		this.xPathSchemGridWidget=eltGenericSchemaGridWidget;
		
	}


	@Override
	public boolean canModify(Object element, String property) {
		// Allow editing of all values
		XPathGridRow xPathGridRow = (XPathGridRow) element;
		if (XPathSchemaGridWidget.DATEFORMAT.equals(property)){
			if(DataType.DATE_CLASS.equals(xPathGridRow.getDataTypeValue())){
				return true;
			}
			else{ 
				return false;
			}
		}
		if (XPathSchemaGridWidget.SCALE.equals(property)){
			if(DataType.BIGDECIMAL_CLASS.getValue().equals(xPathGridRow.getDataTypeValue())){
				return true;
			}
			else {
				return false; 	
			}
		}
		if (XPathSchemaGridWidget.SCALE_TYPE.equals(property))
		{
			if(DataType.BIGDECIMAL_CLASS.getValue().equals(xPathGridRow.getDataTypeValue()))
				return true;
			else {
				return false; 	
			}
		}

		if (XPathSchemaGridWidget.PRECISION.equals(property)){
			if(DataType.BIGDECIMAL_CLASS.getValue().equals(xPathGridRow.getDataTypeValue())){
				return true;
			}
			else {
				return false; 	
			}
		}
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		XPathGridRow xPathGridRow = (XPathGridRow) element;
		if (XPathSchemaGridWidget.FIELDNAME.equals(property))
			return xPathGridRow.getFieldName();
		else if (XPathSchemaGridWidget.DATATYPE.equals(property))
			return xPathGridRow.getDataType();
		else if (XPathSchemaGridWidget.XPATH.equals(property))
			return xPathGridRow.getXPath();
		else if (XPathSchemaGridWidget.DATEFORMAT.equals(property))
			return String.valueOf(xPathGridRow.getDateFormat());
		else if (XPathSchemaGridWidget.PRECISION.equals(property))
			return xPathGridRow.getPrecision();
		else if (XPathSchemaGridWidget.SCALE.equals(property))
			return String.valueOf(xPathGridRow.getScale());
		else if (XPathSchemaGridWidget.SCALE_TYPE.equals(property))
			return xPathGridRow.getScaleType();
		else if (XPathSchemaGridWidget.FIELD_DESCRIPTION.equals(property))
			return xPathGridRow.getDescription();
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
		XPathGridRow xPathGridRow = (XPathGridRow) object;
		if (xPathGridRow != null) {
		if (XPathSchemaGridWidget.FIELDNAME.equals(property)){
			xPathGridRow.setFieldName(((String) value).trim());
		}
		else if (XPathSchemaGridWidget.DATATYPE.equals(property)){
			if(StringUtils.equals(DataType.BIGDECIMAL_CLASS.getValue(), GeneralGridWidgetBuilder.getDataTypeValue()[(Integer)value])){
				xPathGridRow.setScaleType(2); 
				xPathGridRow.setScaleTypeValue(GeneralGridWidgetBuilder.getScaleTypeValue()[2]);
				xPathGridRow.setScale(String.valueOf(1));
			}
			xPathGridRow.setDataType((Integer)value);
			xPathGridRow.setDataTypeValue(GeneralGridWidgetBuilder.getDataTypeValue()[(Integer)value]);
		}
		else if (XPathSchemaGridWidget.XPATH.equals(property)){
			String xPath=((String) value).trim();
			xPathGridRow.setXPath(xPath);
			 Text loopXpathTextBox=(Text)viewer.getTable().getData();
			if(StringUtils.isNotBlank(loopXpathTextBox.getText())){
				if(!xPathGridRow.getAbsolutexPath().startsWith(loopXpathTextBox.getText())){
					xPathGridRow.setAbsolutexPath(loopXpathTextBox.getText().trim()+Path.SEPARATOR+xPath);
				}else{
					xPathGridRow.setAbsolutexPath(xPath);
				}
			}else{
				xPathGridRow.setAbsolutexPath(xPath);
			}
		}
		else if (XPathSchemaGridWidget.DATEFORMAT.equals(property)){
			xPathGridRow.setDateFormat(((String) value).trim()); 
		}	
		else if (XPathSchemaGridWidget.PRECISION.equals(property)){	
			xPathGridRow.setPrecision(((String) value).trim()); 
		}
		else if (XPathSchemaGridWidget.SCALE.equals(property)){
			 xPathGridRow.setScale(((String) value).trim());
		}
		else if (XPathSchemaGridWidget.SCALE_TYPE.equals(property)){
			xPathGridRow.setScaleType((Integer)value); 
			xPathGridRow.setScaleTypeValue(GeneralGridWidgetBuilder.getScaleTypeValue()[(Integer)value]);
			
		}
		else if (XPathSchemaGridWidget.FIELD_DESCRIPTION.equals(property)){
			xPathGridRow.setDescription(((String) value).trim());
		}

		if (isResetNeeded(xPathGridRow, property)){
			xPathGridRow.setScale("");
			xPathGridRow.setScaleTypeValue(GeneralGridWidgetBuilder.getScaleTypeValue()[0]);
			xPathGridRow.setScaleType(0);
			xPathGridRow.setPrecision("");
		}
		resetDateFormat(xPathGridRow, property);
		viewer.refresh();
		SchemaRowValidation.INSTANCE.highlightInvalidRowWithRedColor(xPathGridRow, (TableItem)element,xPathSchemGridWidget.getTable(), xPathSchemGridWidget.getComponentType());
	}	
		xPathSchemGridWidget.showHideErrorSymbol(xPathSchemGridWidget.isWidgetValid());
		xPathSchemGridWidget.setSchemaUpdated(true);
	}


	private void resetDateFormat(XPathGridRow row, String property){
		if(XPathSchemaGridWidget.DATATYPE.equals(property) && StringUtils.isNotBlank(row.getDataTypeValue())){
			if(!(DataType.DATE_CLASS.equals(row.getDataTypeValue()))){
				row.setDateFormat("");
			}
		}
	}

	private boolean isResetNeeded(XPathGridRow xPathGridRow, String property) {
		if(XPathSchemaGridWidget.DATATYPE.equals(property) && StringUtils.isNotBlank(xPathGridRow.getDataTypeValue())){
			if(DataType.INTEGER_CLASS.equals(xPathGridRow.getDataTypeValue()) 
					||DataType.LONG_CLASS.equals(xPathGridRow.getDataTypeValue())
					||DataType.STRING_CLASS.equals(xPathGridRow.getDataTypeValue())
					||DataType.SHORT_CLASS.equals(xPathGridRow.getDataTypeValue())
					||DataType.BOOLEAN_CLASS.equals(xPathGridRow.getDataTypeValue())
					||DataType.FLOAT_CLASS.equals(xPathGridRow.getDataTypeValue())
					||DataType.DOUBLE_CLASS.equals(xPathGridRow.getDataTypeValue())
					||DataType.DATE_CLASS.equals(xPathGridRow.getDataTypeValue())){
				return true;
			}	
		}
		return false;
	}
}