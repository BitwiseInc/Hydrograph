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
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;


/**
 * The Class GenerateRecordsGridLabelProvider.
 * 
 * @author Bitwise
 */
public class GenerateRecordsGridLabelProvider implements ITableLabelProvider, ITableColorProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		GenerateRecordSchemaGridRow generateRecordsSchemaGridRow = (GenerateRecordSchemaGridRow) element;
		switch (columnIndex) {

		case 0:
			return generateRecordsSchemaGridRow.getFieldName(); 
		case 1:
			return GridWidgetCommonBuilder.getDataTypeKey()[generateRecordsSchemaGridRow.getDataType().intValue()];   
		case 2:
			return generateRecordsSchemaGridRow.getDateFormat();
		case 3:
			return generateRecordsSchemaGridRow.getPrecision(); 
		case 4: 
			return generateRecordsSchemaGridRow.getScale().toString();
		case 5:
			if (generateRecordsSchemaGridRow.getScaleType() != null) {
				return GridWidgetCommonBuilder.getScaleTypeKey()[generateRecordsSchemaGridRow.getScaleType().intValue()];
			} else {
				return GridWidgetCommonBuilder.getScaleTypeKey()[0];
			}
		case 6: 
			return generateRecordsSchemaGridRow.getDescription();
		case 7: 
			return generateRecordsSchemaGridRow.getLength().toString();
		case 8:
			return generateRecordsSchemaGridRow.getRangeFrom().toString();
		case 9:
			return generateRecordsSchemaGridRow.getRangeTo().toString();
		case 10:
			return generateRecordsSchemaGridRow.getDefaultValue().toString();

		}
		return null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		return;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		return;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
	 */
	@Override
	public Color getBackground(Object element, int columnIndex) {
        return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
	 */
	@Override
	public Color getForeground(Object element, int columnIndex) {
		return null;
	}
}