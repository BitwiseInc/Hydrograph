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

import hydrograph.ui.datastructure.property.MixedSchemeGridRow;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * The Class MixedSchemeGridLabelProvider.
 * 
 * @author Bitwise
 */

public class MixedSchemeGridLabelProvider implements ITableLabelProvider,ITableColorProvider{

	/**
	 * Adds a listener
	 * 
	 * @param listener
	 *            the listener
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {		
	}

	/**
	 * Disposes any created resources
	 */
	@Override
	public void dispose() {		
	}

	/**
	 * Returns whether altering this property on this element will affect the
	 * label
	 * 
	 * @param element
	 *            the element
	 * @param property
	 *            the property
	 * @return boolean
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
	/**
	 * Removes a listener
	 * 
	 * @param listener
	 *            the listener
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		return null;
	}

	/**
	 * Returns the image
	 * 
	 * @param element
	 *            the element
	 * @param columnIndex
	 *            the column index
	 * @return Image
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * Returns the column text
	 * 
	 * @param element
	 *            the element
	 * @param columnIndex
	 *            the column index
	 * @return String
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		MixedSchemeGridRow mixedSchemeGridRow = (MixedSchemeGridRow)element;
		
		switch (columnIndex) {
		case 0:
			return mixedSchemeGridRow.getFieldName(); 
		case 1:
			return GridWidgetCommonBuilder.getDataTypeKey()[mixedSchemeGridRow.getDataType().intValue()];   
		case 2:
			return mixedSchemeGridRow.getLength().toString();
		case 3:
			return mixedSchemeGridRow.getDelimiter().toString();
		case 4: 
			return mixedSchemeGridRow.getScale().toString();
		case 5:
			if(mixedSchemeGridRow.getScaleType()!=null)
			{
			return GridWidgetCommonBuilder.getScaleTypeKey()[mixedSchemeGridRow.getScaleType().intValue()];  
			}
			else
			{
				return GridWidgetCommonBuilder.getScaleTypeKey()[0];
			}
		case 6: 
			return mixedSchemeGridRow.getDateFormat();
		case 7: 
			return mixedSchemeGridRow.getPrecision(); 
		case 8:
			return mixedSchemeGridRow.getDescription();
		}
		return null;
	}

}
