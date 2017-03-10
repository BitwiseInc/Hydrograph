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

package hydrograph.ui.propertywindow.custom.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Bitwise
 * 
 * Base class for all cell editors which will be
 * used as a Custom-combo box.
 *
 */
abstract class CustomAbstractComboBoxCellEditor extends CellEditor {

	/**
	 * Create a new cell-editor
	 *
	 * @param parent
	 *            the parent of the combo
	 * @param style
	 *            the style used to create the combo
	 */
	CustomAbstractComboBoxCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * Creates a new cell editor with no control and no set of choices.
	 * Initially, the cell editor has no cell validator.
	 *
	 */
	CustomAbstractComboBoxCellEditor() {
	}

	/*
	 * @see org.eclipse.jface.viewers.CellEditor#activate(org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent)
	 */
	public void activate(ColumnViewerEditorActivationEvent activationEvent) {
		super.activate(activationEvent);
	}
}