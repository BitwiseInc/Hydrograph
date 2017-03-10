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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.datastructure.property.OperationSystemProperties;


/**
 * 
 * @author Bitwise
 *
 */
public class OperationSystemLabelProvider implements ITableLabelProvider , ITableColorProvider {
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
	  OperationSystemProperties opSystemProperties = (OperationSystemProperties) element;
    switch (columnIndex) {
    case 0:
      return opSystemProperties.getOpSysValue();

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

		return CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 230);
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return CustomColorRegistry.INSTANCE.getColorFromRegistry( 100, 0, 0);
	} 
}