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

import hydrograph.ui.datastructure.property.OperationSystemProperties;
import hydrograph.ui.propertywindow.messages.Messages;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;


// TODO: Auto-generated Javadoc
/**
 * @author Bitwise
 * This class represents the cell modifier for the SchemaEditor program
 */

public class OperationSystemCellModifier implements ICellModifier {
  private Viewer viewer;

  /**
	 * Instantiates a new schema grid cell modifier.
	 * 
	 * @param viewer
	 *            the viewer
	 */
  public OperationSystemCellModifier(Viewer viewer) {
    this.viewer = viewer;
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
	  OperationSystemProperties p = (OperationSystemProperties) element;
	  
    if (Messages.OPERATIONAL_SYSTEM_FIELD.equals(property) ||Messages.OPERATIONAL_SYSTEM_FIELD.equals(property) )
        return p.getOpSysValue();
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
 
    OperationSystemProperties p = (OperationSystemProperties) element;
    if (Messages.OPERATIONAL_SYSTEM_FIELD.equals(property) ||Messages.OPERATIONAL_SYSTEM_FIELD.equals(property) )
      p.setOpSysValue(((String) value).trim());

    viewer.refresh();
  }
  
  
  
}