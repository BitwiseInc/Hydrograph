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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.dialog.hiveInput.SingleClickEvent;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;



/**
 * Class for runtime property dialog.
 * This class is responsible to provide UI to read runtime properties from user. 
 * 
 * @author Bitwise
 *
 */
public class RuntimePropertyDialog extends Dialog {

	private static final String ERROR = "ERROR";
	
	private static final String[] PROPS = { Constants.PROPERTY_NAME, Constants.PROPERTY_VALUE };

	private String PROPERTY_EXISTS_ERROR = Messages.RuntimePropertAlreadyExists;
	private String PROPERTY_NAME_BLANK_ERROR = Messages.EmptyNameNotification;
	private String PROPERTY_VALUE_BLANK_ERROR = Messages.EmptyValueNotification;
	private TableViewer tableViewer;

	private List<RuntimeProperties> propertyList;
	private Map<String, String> runtimePropertyMap;

	private Table table;
	private Label lblPropertyError;
	private Button deleteButton;
	private Button upButton;
	private Button downButton;
	private String windowLabel;
	private boolean isAnyUpdatePerformed;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private boolean closeDialog;	
	private boolean okPressed;
	private boolean okPressedAfterUpdate;
	private static final String INFORMATION="Information";
	private boolean ctrlKeyPressed = false;
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param propertyDialogButtonBar
	 */
	public RuntimePropertyDialog(Shell parentShell, PropertyDialogButtonBar propertyDialogButtonBar, String windowLabel) {
		super(parentShell);
		if(StringUtils.isNotBlank(windowLabel))
			this.windowLabel=windowLabel;
		else
			this.windowLabel=Constants.RUNTIME_PROPERTIES_WINDOW_LABEL;
		propertyList = new LinkedList<RuntimeProperties>();
		runtimePropertyMap = new LinkedHashMap<String, String>();
		this.propertyDialogButtonBar = propertyDialogButtonBar;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		isAnyUpdatePerformed = false;
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText(windowLabel);
		ColumnLayout cl_container = new ColumnLayout();
		cl_container.verticalSpacing = 0;
		cl_container.maxNumColumns = 1;
		container.setLayout(cl_container);

		addButtonPanel(container);

		Composite composite_2 = new Composite(container, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, false));
		ColumnLayoutData cld_composite_2 = new ColumnLayoutData();
		cld_composite_2.heightHint = 453;
		composite_2.setLayoutData(cld_composite_2);

		createTable(composite_2);

		addErrorLabel(container);
		
		return container;
	}

	  	private void addErrorLabel(Composite container) {
		Composite composite_3 = new Composite(container, SWT.NONE);
		ColumnLayout cl_coposite_3 = new ColumnLayout();
		cl_coposite_3.topMargin=0;
		composite_3.setLayout(cl_coposite_3);
		ColumnLayoutData cld_composite_3 = new ColumnLayoutData();
		cld_composite_3.heightHint = 19;
		composite_3.setLayoutData(cld_composite_3);

		lblPropertyError = new Label(composite_3, SWT.NONE);
		ColumnLayoutData cld_lblPropertyError = new ColumnLayoutData();
		cld_lblPropertyError.heightHint = 25;
		lblPropertyError.setLayoutData(cld_lblPropertyError);
		lblPropertyError.setVisible(false);
		lblPropertyError.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
	}

	/**
	 * Returns runtime property map
	 *  
	 * @return - runtimePropertyMap -  Map<String, String>
	 */
	public Map<String, String> getRuntimeProperties() {
		return runtimePropertyMap;
	}

	private void addButtonPanel(Composite container) {
		Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayout(new GridLayout(4, false));
		ColumnLayoutData cld_composite_1 = new ColumnLayoutData();
		cld_composite_1.horizontalAlignment = ColumnLayoutData.RIGHT;
		cld_composite_1.heightHint = 30;
		composite_1.setLayoutData(cld_composite_1);

		Button addButton = new Button(composite_1, SWT.NONE);
		addButton.setToolTipText(Messages.ADD_KEY_SHORTCUT_TOOLTIP);
		addButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		addButton.setImage(ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
		attachAddButtonListern(addButton);

		deleteButton = new Button(composite_1, SWT.NONE);
		deleteButton.setToolTipText(Messages.DELETE_KEY_SHORTCUT_TOOLTIP);
		deleteButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		deleteButton.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		attachDeleteButtonListener(deleteButton);

		upButton = new Button(composite_1, SWT.NONE);
		upButton.setToolTipText(Messages.MOVE_UP_KEY_SHORTCUT_TOOLTIP);
		upButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		upButton.setImage(ImagePathConstant.MOVEUP_BUTTON.getImageFromRegistry());
		attachUpButtonListener(upButton);

		downButton = new Button(composite_1, SWT.NONE);
		downButton.setToolTipText(Messages.MOVE_DOWN_KEY_SHORTCUT_TOOLTIP);
		downButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		downButton.setImage(ImagePathConstant.MOVEDOWN_BUTTON.getImageFromRegistry());
		attachDownButtonListerner(downButton);
		deleteButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
	}

	private void attachDownButtonListerner(Button downButton) {
		
		downButton.addMouseListener(new MouseAdapter() {
			
            @Override
			public void mouseUp(MouseEvent e) {
            	moveRowDown();
            }
		});

	}
	
	private void moveRowDown()
	{
		int index1 = 0, index2 = 0;
		index1 = table.getSelectionIndex();

		if (index1 < propertyList.size() - 1) {
			String text = tableViewer.getTable().getItem(index1).getText(0);
			String text1 = tableViewer.getTable().getItem(index1).getText(1);

			index2 = index1 + 1;

			String data = tableViewer.getTable().getItem(index2).getText(0);
			String data1 = tableViewer.getTable().getItem(index2).getText(1);

			RuntimeProperties p = new RuntimeProperties();
			p.setPropertyName(data);
			p.setPropertyValue(data1);
			propertyList.set(index1, p);
			p = new RuntimeProperties();
			p.setPropertyName(text);
			p.setPropertyValue(text1);
			propertyList.set(index2, p);
			tableViewer.refresh();
			table.setSelection(index1 + 1);
		}
	
	}

	private void attachUpButtonListener(Button upButton) {
		upButton.addMouseListener(new MouseAdapter() {
			

			@Override
			public void mouseUp(MouseEvent e) {
				moveRowUp();
			}
		});

	}

	private void moveRowUp()
	{
		int index1 = 0, index2 = 0;
		index1 = table.getSelectionIndex();

		if (index1 > 0) {
			String text = tableViewer.getTable().getItem(index1).getText(0);
			String text1 = tableViewer.getTable().getItem(index1).getText(1);
			index2 = index1 - 1;
			String data = tableViewer.getTable().getItem(index2).getText(0);
			String data2 = tableViewer.getTable().getItem(index2).getText(1);

			RuntimeProperties p = new RuntimeProperties();
			p.setPropertyName(data);
			p.setPropertyValue(data2);
			propertyList.set(index1, p);
			p = new RuntimeProperties();
			p.setPropertyName(text);
			p.setPropertyValue(text1);
			propertyList.set(index2, p);
			tableViewer.refresh();
			table.setSelection(index1 - 1);
		}
	
	}
	private void attachDeleteButtonListener(final Button deleteButton) {
		deleteButton.addMouseListener(new MouseAdapter() {
            
			@Override
			public void mouseUp(MouseEvent e) {
				deleteRow();
			}

		});

	}
	
	private void deleteRow()
	{

		WidgetUtility.setCursorOnDeleteRow(tableViewer, propertyList);
		isAnyUpdatePerformed = true;
		tableViewer.refresh();
		
		if (propertyList.size() < 1) {
			deleteButton.setEnabled(false);
		} 
		if (propertyList.size()<= 1) {
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		} 
		if(lblPropertyError.getVisible()){
			lblPropertyError.setVisible(false);
		}
	}

	private void attachAddButtonListern(Button addButton) {
		addButton.addMouseListener(new MouseAdapter() {
            @Override
			public void mouseUp(MouseEvent e) {
            	addNewRow();
            }

		});
	}
	
	private void addNewRow()
	{

		table.getParent().setFocus();
		tableViewer.getControl().getShell().setFocus();
		addNewProperty(tableViewer);
		if (propertyList.size() >= 1) {
			deleteButton.setEnabled(true);
		}
		if (propertyList.size() >= 2) {
			upButton.setEnabled(true);
			downButton.setEnabled(true);
		}
	
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {
		
		if(OSValidator.isMac()){
			for(CellEditor cellEditor : tableViewer.getCellEditors()){
				if(cellEditor !=null){
				cellEditor.getControl().setEnabled(false); //Saves the existing value of CellEditor
				cellEditor.getControl().setEnabled(true);
				}
			}
		}

		if (validate()) {
			runtimePropertyMap.clear();
			for (RuntimeProperties temp : propertyList) {
				runtimePropertyMap.put(temp.getPropertyName(), temp.getPropertyValue());
			}
			
			if (isAnyUpdatePerformed && propertyDialogButtonBar != null) {
				propertyDialogButtonBar.enableApplyButton(true);
			} else if (isAnyUpdatePerformed)
				okPressedAfterUpdate = true;
			okPressed = true;
			super.okPressed();
		}

	}
	
	@Override
	protected void cancelPressed() {
		if (isAnyUpdatePerformed) {
			int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO |SWT.ICON_INFORMATION;
			MessageBox messageBox = new MessageBox(new Shell(), style);
			messageBox.setText(INFORMATION);
			messageBox.setMessage(Messages.MessageBeforeClosingWindow);
			if (messageBox.open() == SWT.YES) {
				closeDialog = super.close();
			}
		} else {
			closeDialog = super.close();
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(646, 587);
	}

	// Add New Property After Validating old properties
	private void addNewProperty(TableViewer tabViewer) {
		isAnyUpdatePerformed = true;
		RuntimeProperties property = new RuntimeProperties();
		if (propertyList.size() != 0) {
			if (!validate()) {
				return;
			}
			property.setPropertyName("");
			property.setPropertyValue("");
			propertyList.add(property);
			tabViewer.refresh();
			tableViewer.editElement(tableViewer.getElementAt(propertyList.size() - 1), 0);
		} else {
			property.setPropertyName("");
			property.setPropertyValue("");
			propertyList.add(property);
			tabViewer.refresh();
			tableViewer.editElement(tableViewer.getElementAt(0), 0);
		}
	}

	/**
	 * 
	 * Set runtime properties.
	 * 
	 * @param runtimePropertyMap
	 */
	public void setRuntimeProperties(Map<String, String> runtimePropertyMap) {
		this.runtimePropertyMap = runtimePropertyMap;
	}

	// Loads Already Saved Properties..
	private void loadProperties(TableViewer tv) {

		if (runtimePropertyMap != null && !runtimePropertyMap.isEmpty()) {
			for (String key : runtimePropertyMap.keySet()) {
				RuntimeProperties p = new RuntimeProperties();
				if (validateBeforeLoad(key, runtimePropertyMap.get(key))) {
					p.setPropertyName(key);
					p.setPropertyValue(runtimePropertyMap.get(key));
					propertyList.add(p);
				}
			}
			tv.refresh();

		} //$NON-NLS-1$

	}

	private boolean validateBeforeLoad(String key, String keyValue) {

		if (key.trim().isEmpty()) {
			return false;
		}
		return true;

	}

	// Method for creating Table
	private void createTable(Composite composite) {
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION|SWT.NO_SCROLL | SWT.V_SCROLL);
		tableViewer.setData(Constants.WINDOW_TITLE, windowLabel);
		table = tableViewer.getTable();
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = 422;
		gd_table.widthHint = 431;
		table.setLayoutData(gd_table);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				addNewProperty(tableViewer);
				if (propertyList.size() >= 1) {
					deleteButton.setEnabled(true);
				} 
				if (propertyList.size() >= 2) {
					upButton.setEnabled(true);
					downButton.setEnabled(true);
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				lblPropertyError.setVisible(false);
			}
		});
		
		attachShortcutListner(Constants.PROPERTY_TABLE);
		tableViewer.getTable().addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.keyCode == SWT.ARROW_UP) {
					e.doit = false;
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					e.doit = false;
				} else if (e.keyCode == SWT.TRAVERSE_ARROW_NEXT) {
					e.doit = false;
				} else if (e.keyCode == SWT.TRAVERSE_ARROW_PREVIOUS) {
					e.doit = false;
				}

			}
		});

		// table.setBounds(10, 68, 465, 400);
		tableViewer.setContentProvider(new PropertyContentProvider());
		tableViewer.setLabelProvider(new PropertyLabelProvider());
		tableViewer.setInput(propertyList);

		TableColumn column1 = new TableColumn(table, SWT.LEFT_TO_RIGHT);
		column1.setText(Messages.PROPERTY_NAME);
		TableColumn column2 = new TableColumn(table, SWT.LEFT_TO_RIGHT);
		column2.setText(Messages.PROPERTY_VALUE);

		for (int i = 0, n = table.getColumnCount(); i < n; i++) {
			table.getColumn(i).pack();
		}
		column1.setWidth(280);
		column2.setWidth(374);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final CellEditor propertyNameeditor = new TextCellEditor(table);

		CellEditor propertyValueeditor = new TextCellEditor(table);
		CellEditor[] editors = new CellEditor[] { propertyNameeditor, propertyValueeditor };

		propertyNameeditor.setValidator(createNameEditorValidator(PROPERTY_NAME_BLANK_ERROR));
		propertyValueeditor.setValidator(createValueEditorValidator(PROPERTY_VALUE_BLANK_ERROR));

		tableViewer.setColumnProperties(PROPS);
		tableViewer.setCellModifier(new RunTimePropertyCellModifier(tableViewer));
		tableViewer.setCellEditors(editors);

		// enables the tab functionality
		TableViewerEditor.create(tableViewer, new ColumnViewerEditorActivationStrategy(tableViewer),
				ColumnViewerEditor.KEYBOARD_ACTIVATION | ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL);

		loadProperties(tableViewer);
		if (propertyList.size() != 0) {
			deleteButton.setEnabled(true);
		}
		if (propertyList.size() >= 2) {
			upButton.setEnabled(true);
			downButton.setEnabled(true);
		}
		attachShortcutListner(Constants.PROPERTY_NAME);
		attachShortcutListner(Constants.PROPERTY_VALUE);
		
		tableViewer.getTable().addMouseListener(new SingleClickEvent(new Runnable() {
			@Override
			public void run() {
				validate();
			}
		}));

	}
	
	private void attachShortcutListner(String controlName){
		Control currentControl;
				
		if (controlName == Constants.PROPERTY_NAME)
			currentControl = tableViewer.getCellEditors()[0].getControl();
		else if(controlName == Constants.PROPERTY_VALUE)
			currentControl = tableViewer.getCellEditors()[1].getControl();
		else
			currentControl = table;
		
		currentControl.addKeyListener(new KeyListener() {						
			
			@Override
			public void keyReleased(KeyEvent event) {				
				if(event.keyCode == SWT.CTRL || event.keyCode == SWT.COMMAND){					
					ctrlKeyPressed = false;
				}							
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				if(event.keyCode == SWT.CTRL || event.keyCode == SWT.COMMAND){					
					ctrlKeyPressed = true;
				}
								
				if (ctrlKeyPressed && event.keyCode == Constants.KEY_D) {				
					deleteRow();
				}
				
				else if (ctrlKeyPressed && event.keyCode == Constants.KEY_N){
					addNewRow();
				}
				
				else if (ctrlKeyPressed && event.keyCode == SWT.ARROW_UP){
					moveRowUp();				
				}
				
				else if (ctrlKeyPressed && event.keyCode == SWT.ARROW_DOWN){
					moveRowDown();
				}
			}
		});
	}

	/**
	 * Validate.
	 * 
	 * @return true, if successful
	 */
	private boolean validate() {
		int propertyCounter = 0;
		for (RuntimeProperties runtimeProperties : propertyList) {
			if (runtimeProperties.getPropertyName().trim().isEmpty()) {
				table.setSelection(propertyCounter);
				lblPropertyError.setVisible(true);
				lblPropertyError.setText(Messages.EmptyNameNotification);
				return false;
			}
			propertyCounter++;
		}
		return true;
	}

	// Creates CellNAme Validator for table's cells
	private ICellEditorValidator createNameEditorValidator(final String ErrorMessage) {
		ICellEditorValidator propertyValidator = new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				isAnyUpdatePerformed = true;
				String currentSelectedFld = table.getItem(table.getSelectionIndex()).getText();
				String valueToValidate = String.valueOf(value).trim();
				if (StringUtils.isEmpty(valueToValidate)) {
					lblPropertyError.setText(ErrorMessage);
					lblPropertyError.setVisible(true);
					return ERROR;
				}

				for (RuntimeProperties temp : propertyList) {
					if (!currentSelectedFld.equalsIgnoreCase(valueToValidate)
							&& temp.getPropertyName().equalsIgnoreCase(valueToValidate)) {

						lblPropertyError.setText(PROPERTY_EXISTS_ERROR);
						lblPropertyError.setVisible(true);
						return ERROR;
					} else {
						lblPropertyError.setVisible(false);
					}
				}
				return null;
			}
		};
		return propertyValidator;
	}

	// Creates Value Validator for table's cells
	private ICellEditorValidator createValueEditorValidator(final String ErrorMessage) {
		ICellEditorValidator propertyValidator = new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				isAnyUpdatePerformed = true;
				String valueToValidate = String.valueOf(value).trim();
				if (valueToValidate.isEmpty()) {
					lblPropertyError.setText(ErrorMessage);
					lblPropertyError.setVisible(true);
					return ERROR; //$NON-NLS-1$
				} else {
					lblPropertyError.setVisible(false);
				}
				return null;

			}
		};
		return propertyValidator;
	}
	
	@Override
	public boolean close() {
		if(!okPressed){
			cancelPressed();			
			return closeDialog;
		}else{
			return super.close();
		}		
	}

	/**
	 * This method is used to determine whether ok button is pressed after any update.
	 * 
	 * @return
	 */
	public boolean isOkPressedAfterUpdate(){
		return this.okPressedAfterUpdate;
	}
	
	public boolean isOkPressed(){
		
		return this.okPressed;
	}
	
}
