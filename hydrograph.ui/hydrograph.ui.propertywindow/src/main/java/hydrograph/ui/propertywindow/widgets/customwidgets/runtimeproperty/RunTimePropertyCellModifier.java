
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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.runtimeproperty;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;


/**
 * The Class RunTimePropertyCellModifier.
 * 
 * @author Bitwise
 */
public class RunTimePropertyCellModifier implements ICellModifier {
	private Viewer viewer;
	private static final String PROPERTY_NAME = "PROPERTY_NAME"; 
	private static final String PROPERTY_VALUE = "PROPERTY_VALUE";

	/**
	 * Instantiates a new run time property cell modifier.
	 * 
	 * @param viewer
	 *            the viewer
	 */
	public RunTimePropertyCellModifier(Viewer viewer) {
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
		RuntimeProperties p = (RuntimeProperties) element;
		
		if (PROPERTY_NAME.equals(property)) {

			return p.getPropertyName();

		} else if (PROPERTY_VALUE.equals(property))
			return p.getPropertyValue();
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
		if (viewer.getData(Constants.WINDOW_TITLE) != null
				&& StringUtils.equalsIgnoreCase((String) viewer.getData(Constants.WINDOW_TITLE),
						Constants.SUBJOB_WINDOW_LABEL))
			customizedModifyForSubgraph(element, property, value);
		else {
		if (element instanceof Item)
			element = ((Item) element).getData();

		RuntimeProperties runtimePropertyRow = (RuntimeProperties) element;
		
		if (StringUtils.equals(PROPERTY_NAME,property)){
			if(ParameterUtil.isParameter((String)value)){
				runtimePropertyRow.setPropertyValue((String) value);
			}
			runtimePropertyRow.setPropertyName(((String) value));
		}
		else if (StringUtils.equals(PROPERTY_VALUE,property)){
			if(ParameterUtil.isParameter((String)value)){
				runtimePropertyRow.setPropertyName((String) value);
			}
			runtimePropertyRow.setPropertyValue((String) value);
		}
		}
		// Force the viewer to refresh
		viewer.refresh();
	}

	private void customizedModifyForSubgraph(Object element, String property, Object value) {
		if (element instanceof Item)
			element = ((Item) element).getData();

		RuntimeProperties runtimePropertyRow = (RuntimeProperties) element;

		if (StringUtils.equals(PROPERTY_NAME,property)) {
			runtimePropertyRow.setPropertyName(((String) value));
		} else if (StringUtils.equals(PROPERTY_VALUE,property)) {
			runtimePropertyRow.setPropertyValue((String) value);
		}
	}
		
	
}