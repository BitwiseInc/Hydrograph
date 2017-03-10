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

import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Bitwise
 *
 * A cell editor that represents a list of combo box items.
 * This class may be instantiated; but it is not intended to be sub classed.
 */
public class CustomComboBoxCellEditor extends CustomAbstractComboBoxCellEditor {

	private String[] comboItems;

	int selection;
	
	Combo comboBox;

	/**
	 * The method will create a new cell editor containing a combo box having a list of
	 * choices.The cell editor value is the zero-based index of the selected item.
	 *
	 * @param parent
	 *            the parent control
	 * @param items
	 *            the list of strings for the combo box
	 * @param style
	 *            the style bits
	 */
	public CustomComboBoxCellEditor(Composite parent, String[] items, int style) {
		super(parent, style);
		setItems(items);
	}

	/**
	 * Returns the item list for combo box
	 *
	 */
	public String[] getItems() {
		return this.comboItems;
	}

	/**
	 *This method helps in setting the list of combo items
	 *
	 * @param comboItems
	 *            the list of choices for the combo box
	 */
	public void setItems(String[] comboItems) {
		Assert.isNotNull(comboItems);
		this.comboItems = comboItems;
		forPopulatingComboBoxItems();
	}

	protected Control createControl(Composite parent) {

		comboBox = new Combo(parent, getStyle());
		comboBox.setFont(parent.getFont());

		forPopulatingComboBoxItems();

		comboBox.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				keyReleaseOccured(e);
			}
		});

		comboBox.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent event) {
				applyEditorValueAndDeactivate();
			}

			public void widgetSelected(SelectionEvent event) {
				selection = comboBox.getSelectionIndex();
			}
		});

		comboBox.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE
						|| e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
				}
			}
		});

		comboBox.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				CustomComboBoxCellEditor.this.focusLost();
			}
		});
		return comboBox;
	}

	/**
	 * The method returns the zero-based index
	 * of the current selection.
	 *
	 * @returns an integer value
	 */
	protected Object doGetValue() {
		return new Integer(selection);
	}

	protected void doSetFocus() {
		comboBox.setFocus();
	}

	/**
	 * The method sets the minimum width of the
	 * cell as of 10 characters
	 */
	public LayoutData getLayoutData() {
		LayoutData layoutData = super.getLayoutData();
		if ((comboBox == null) || comboBox.isDisposed()) {
			layoutData.minimumWidth = 60;
		} else {
			GC gc = new GC(comboBox);
			layoutData.minimumWidth = (gc.getFontMetrics()
					.getAverageCharWidth() * 10) + 10;
			gc.dispose();
		}
		return layoutData;
	}

	protected void doSetValue(Object value) {
		Assert.isTrue(comboBox != null && (value instanceof Integer));
		selection = ((Integer) value).intValue();
		comboBox.select(selection);
	}

	private void forPopulatingComboBoxItems() {
		if (comboBox != null && comboItems != null) {
			comboBox.removeAll();
			for (int i = 0; i < comboItems.length; i++) {
				comboBox.add(comboItems[i], i);
			}

			setValueValid(true);
			selection = 0;
		}
	}

	private void applyEditorValueAndDeactivate() {
		selection = comboBox.getSelectionIndex();
		Object newValue = doGetValue();
		markDirty();
		boolean isValid = isCorrect(newValue);
		setValueValid(isValid);

		if (!isValid) {
			if (comboItems.length > 0 && selection >= 0 && selection < comboItems.length) {
				setErrorMessage(MessageFormat.format(getErrorMessage(),
						new Object[] { comboItems[selection] }));
			} else {
				setErrorMessage(MessageFormat.format(getErrorMessage(),
						new Object[] { comboBox.getText() }));
			}
		}

		fireApplyEditorValue();
		deactivate();
	}

	protected void focusLost() {
		if (isActivated()) {
			applyEditorValueAndDeactivate();
		}
	}

	protected void keyReleaseOccured(KeyEvent keyEvent) {
		if (keyEvent.character == '\u001b') { 
			fireCancelEditor();
		} else if (keyEvent.character == SWT.TAB) {
			applyEditorValueAndDeactivate();
		}
	}
}
