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

package hydrograph.ui.expression.editor.evaluate;

import hydrograph.ui.common.util.ParameterUtil;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;

/**
 * The Class FieldNameAndValueCellModifier.
 * 
 * @author Bitwise
 */
public class FieldNameAndValueCellModifier implements ICellModifier {
	private Viewer viewer;

	/**
	 * Instantiates a new run time property cell modifier.
	 * 
	 * @param viewer
	 *            the viewer
	 */
	public FieldNameAndValueCellModifier(Viewer viewer) {
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
		if (StringUtils.equals(EvalDialogFieldTable.FIELD_VALUE_PROPERTY, property))
			return true;
		else
			return false;
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
		FieldNameAndValue fieldNameAndValue = (FieldNameAndValue) element;

		if (EvalDialogFieldTable.FIELD_NAME_PROPERTY.equals(property)) {
			return fieldNameAndValue.getFieldName();

		} else if (EvalDialogFieldTable.FIELD_DATATYPE_PROPERTY.equals(property)) {
			return fieldNameAndValue.getFieldName();

		} else if (EvalDialogFieldTable.FIELD_VALUE_PROPERTY.equals(property))
			return fieldNameAndValue.getFieldValue();
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

		FieldNameAndValue fieldNameAndValue = (FieldNameAndValue) element;

		if (StringUtils.equals(EvalDialogFieldTable.FIELD_NAME_PROPERTY, property)) {
			fieldNameAndValue.setFieldName((String) value);
		} else if (StringUtils.equals(EvalDialogFieldTable.FIELD_VALUE_PROPERTY, property)) {
			fieldNameAndValue.setFieldValue((String) value);
		}

		viewer.refresh();
	}

}