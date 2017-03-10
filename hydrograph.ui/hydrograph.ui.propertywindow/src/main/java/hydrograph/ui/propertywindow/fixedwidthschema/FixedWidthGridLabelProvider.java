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

 
package hydrograph.ui.propertywindow.fixedwidthschema;

import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;


/**
 * The Class FixedWidthGridLabelProvider.
 * 
 * @author Bitwise
 */
public class FixedWidthGridLabelProvider implements ITableLabelProvider,ITableColorProvider {

	/**
	 * Returns the image
	 * 
	 * @param element
	 *            the element
	 * @param columnIndex
	 *            the column index
	 * @return Image
	 */
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
	public String getColumnText(Object element, int columnIndex) {
		FixedWidthGridRow fixedGrid = (FixedWidthGridRow) element;
		
		switch (columnIndex) {
		case 0:
			return fixedGrid.getFieldName(); 
		case 1:
			return GridWidgetCommonBuilder.getDataTypeKey()[fixedGrid.getDataType().intValue()];   
		case 2:
			return fixedGrid.getLength().toString();
		case 3: 
			return fixedGrid.getScale().toString();
		case 4:
			if(fixedGrid.getScaleType()!=null)
			{
			return GridWidgetCommonBuilder.getScaleTypeKey()[fixedGrid.getScaleType().intValue()];  
			}
			else
			{
				return GridWidgetCommonBuilder.getScaleTypeKey()[0];
			}
		case 5:
			return fixedGrid.getDateFormat();
		case 6: 
			return fixedGrid.getPrecision(); 
		case 7: 
			return fixedGrid.getDescription();
		}
		return null;
	}

	/**
	 * Adds a listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addListener(ILabelProviderListener listener) {
		// Ignore it
	}

	/**
	 * Disposes any created resources
	 */
	public void dispose() {
		// Nothing to dispose
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
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * Removes a listener
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeListener(ILabelProviderListener listener) {
		// Ignore
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {

		return null;
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return null;
	} 
}