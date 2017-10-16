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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

/**
 * 
 * This class represents composite for map and pass through fields.
 * 
 * @author Bitwise
 *
 */
public class MapAndPassthroughComposite extends Composite {
	public static final String MAP_PASSTHROUGH_FIELDS = "Map/Passthrough Fields";
	public static final String PASSTHROUGH_INPUT_FIELDS = "Passthrough Input Fields";
	private Table table;
	private Button addButton;
	private Button deleteButton;
	private Button checkButton;
	private TableViewer tableViewer;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MapAndPassthroughComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		checkButton = new Button(composite_2, SWT.CHECK);
		checkButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		checkButton.setText(PASSTHROUGH_INPUT_FIELDS);
		
		Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayout(new GridLayout(1, false));
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label lblMapFields = new Label(composite_3, SWT.NONE);
		
		lblMapFields.setText(MAP_PASSTHROUGH_FIELDS);
		
		Composite composite_4 = new Composite(composite, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));
		composite_4.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		
		addButton = new Button(composite_4, SWT.NONE);
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		deleteButton = new Button(composite_4, SWT.NONE);
		deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		

	}

	/**
	 * @return the addButton
	 */
	public Button getAddButton() {
		return addButton;
	}

	/**
	 * @return the deleteButton
	 */
	public Button getDeleteButton() {
		return deleteButton;
	}

	/**
	 * @return the checkButton
	 */
	public Button getCheckButton() {
		return checkButton;
	}

	public Table getTable() {
		return table;
	}

	
	/**
	 * @return the tableViewer
	 */
	public TableViewer getTableViewer() {
		return tableViewer;
	}

	@Override
	protected void checkSubclass() {
	}
}
