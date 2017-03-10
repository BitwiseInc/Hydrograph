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
package hydrograph.ui.dataviewer.datasetinformation;

import hydrograph.ui.datastructure.property.GridRow;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * The Class DatasetLabelProvider.
 * Provides column labels for the view data window.
 * @author Bitwise
 */

public class DatasetLabelProvider implements ITableLabelProvider, ITableColorProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		
		
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

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

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		GridRow gridRow = (GridRow) element;
		switch (columnIndex) {
		case 0:
			return gridRow.getFieldName();
		case 1:
			String[] dataTypeCanonicalName=StringUtils.split(gridRow.getDataTypeValue(),"."); 
			return dataTypeCanonicalName[dataTypeCanonicalName.length-1];
		case 2:
			return gridRow.getDateFormat();
		case 3:
			return gridRow.getPrecision();
		case 4 :
			return gridRow.getScale();
		case 5:
			return gridRow.getScaleTypeValue();
		case 6:
			return gridRow.getDescription();
		}
		return null;
	}
}
