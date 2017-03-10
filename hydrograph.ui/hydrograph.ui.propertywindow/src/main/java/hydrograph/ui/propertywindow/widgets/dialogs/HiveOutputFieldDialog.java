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

 
package hydrograph.ui.propertywindow.widgets.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.dialog.hiveInput.HiveFieldDialogHelper;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTCellModifier;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTFilterContentProvider;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTFilterLabelProvider;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;


/**
 * 
 * The class to create partition key field dialog for Hive Output.
 * 
 * @author Bitwise
 * 
 */

public class HiveOutputFieldDialog extends Dialog {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(HiveOutputFieldDialog.class);

	private final List<FilterProperties> propertyList;
	private List<String> fieldNameList;
	private String componentName;

	private final String PROPERTY_EXISTS_ERROR = Messages.RuntimePropertAlreadyExists;
	private static final String[] PROPS = { Constants.COMPONENT_NAME };
	private final String PROPERTY_NAME_BLANK_ERROR = Messages.EmptyFieldNameNotification;
	private Label lblPropertyError;
	private TableViewer targetTableViewer;
	private TableViewer sourceTableViewer;
	private Table sourceTable;
	private Table targetTable;

	private boolean isAnyUpdatePerformed;

	private TableColumn sourceTableColumn;
	private TableViewerColumn tableViewerColumn;
	private DragSource dragSource;
	private DropTarget dropTarget;
	protected List<String> sourceFieldsList;

	PropertyDialogButtonBar propertyDialogButtonBar;
	private boolean closeDialog;
	private boolean okPressed;
	private Label deleteButton;
	private Label upButton;
	private Label downButton;

	protected Button okButton;
	private static final String INFORMATION="Information";
	private boolean ctrlKeyPressed = false;
	

	public HiveOutputFieldDialog(Shell parentShell, PropertyDialogButtonBar propertyDialogButtonBar) {
		super(parentShell);

		propertyList = new ArrayList<FilterProperties>();
		fieldNameList = new ArrayList<String>();
		this.propertyDialogButtonBar = propertyDialogButtonBar;
	}

	// Add New Property After Validating old properties
	private void addNewProperty(TableViewer tv, String fieldName) {
		if (isPropertyAlreadyExists(fieldName))
			return;
		isAnyUpdatePerformed = true;
		FilterProperties filter = new FilterProperties();
		if (fieldName == null)
			fieldName = "";
		if (propertyList.size() != 0) {
			if (!validate())
				return;
			filter.setPropertyname(fieldName); //$NON-NLS-1$
			propertyList.add(filter);
			tv.refresh();
			targetTableViewer.editElement(targetTableViewer.getElementAt(propertyList.size() - 1), 0);
		} else {
			filter.setPropertyname(fieldName);//$NON-NLS-1$
			propertyList.add(filter);
			tv.refresh();
			targetTableViewer.editElement(targetTableViewer.getElementAt(0), 0);
		}
	}

	/**
	 * 
	 * Returns the list of key field names
	 * 
	 * @return - list of key fields
	 */
	public List<String> getFieldNameList() {
		return fieldNameList;
	}

	/**
	 * set the list of key fields
	 * 
	 * @param runtimePropertySet
	 */
	public void setRuntimePropertySet(List<String> runtimePropertySet) {
		this.fieldNameList = runtimePropertySet;
	}

	/**
	 * 
	 * returns the name of component
	 * 
	 * @return - name of component
	 */
	public String getComponentName() {
		return componentName;
	}

	/**
	 * 
	 * Set the name of component
	 * 
	 * @param componentName
	 */
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	// Loads Already Saved Properties..
	private void loadProperties(TableViewer tv) {

		if (fieldNameList != null && !fieldNameList.isEmpty()) {
			for (String key : fieldNameList) {
				FilterProperties filter = new FilterProperties();
				if (validateBeforeLoad(key)) {
					filter.setPropertyname(key);
					propertyList.add(filter);
				}
			}
			tv.refresh();
		} else {

			logger.debug("LodProperties :: Empty Map");
		}
	}

	private boolean validateBeforeLoad(String key) {
		if (key.trim().isEmpty()) {
			return false;
		}
		return true;

	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		isAnyUpdatePerformed = false;

		if (Constants.KEY_FIELDS_WINDOW_TITLE.equalsIgnoreCase(componentName)) {
			getShell().setText(Constants.KEY_FIELDS_WINDOW_TITLE);
		}
		if (Constants.OPERATION_FIELDS_WINDOW_TITLE.equalsIgnoreCase(componentName)) {
			getShell().setText(Constants.OPERATION_FIELDS_WINDOW_TITLE);
		}
		if (Constants.JOIN_KEYS_WINDOW_TITLE.equalsIgnoreCase(componentName)) {
			getShell().setText(Constants.JOIN_KEYS_WINDOW_TITLE);
		}
		if(Constants.LOOKUP_KEYS_WINDOW_TITLE.equalsIgnoreCase(componentName)){
			getShell().setText(Constants.LOOKUP_KEYS_WINDOW_TITLE);
		}
		if(Constants.PARTITION_KEYS_WINDOW_TITLE.equalsIgnoreCase(componentName)){
			getShell().setText(Constants.PARTITION_KEYS_WINDOW_TITLE);
		}
		Composite container = (Composite) super.createDialogArea(parent);
		ColumnLayout cl_container = new ColumnLayout();
		cl_container.verticalSpacing = 0;
		cl_container.maxNumColumns = 1;
		container.setLayout(cl_container);

		addButtonPanel(container);

		Composite tableComposite = new Composite(container, SWT.NONE);
		tableComposite.setLayout(new GridLayout(2, false));
		ColumnLayoutData cld_composite_2 = new ColumnLayoutData();
		cld_composite_2.heightHint = 453;
		tableComposite.setLayoutData(cld_composite_2);
		
		createSourceTable(tableComposite);
		createTargetTable(tableComposite);

		addErrorLabel(container);
		checkFieldsOnStartup();
		return container;
	}
	
	protected void checkFieldsOnStartup() {
		HiveFieldDialogHelper.INSTANCE.compareAndChangeColor(getTargetTableViewer(),sourceFieldsList);
		
	}

	private void addErrorLabel(Composite container) {
		Composite composite_3 = new Composite(container, SWT.NONE);
		composite_3.setLayout(new ColumnLayout());
		ColumnLayoutData cld_composite_3 = new ColumnLayoutData();
		cld_composite_3.heightHint = 72;
		composite_3.setLayoutData(cld_composite_3);

		lblPropertyError = new Label(composite_3, SWT.NONE);
		ColumnLayoutData cld_lblPropertyError = new ColumnLayoutData();
		cld_lblPropertyError.heightHint = 24;
		lblPropertyError.setLayoutData(cld_lblPropertyError);
		lblPropertyError.setVisible(false);
		lblPropertyError.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
	}

	protected Composite addButtonPanel(Composite container) {
		Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayout(new GridLayout(5, false));
		ColumnLayoutData cld_composite_1 = new ColumnLayoutData();
		cld_composite_1.horizontalAlignment = ColumnLayoutData.RIGHT;
		cld_composite_1.heightHint = 28;
		composite_1.setLayoutData(cld_composite_1);

		Label addButton = new Label(composite_1, SWT.NONE);
		addButton.setToolTipText(Messages.ADD_KEY_SHORTCUT_TOOLTIP);
		addButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		addButton.setImage(ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
		attachAddButtonListern(addButton);

		deleteButton = new Label(composite_1, SWT.NONE);
		deleteButton.setToolTipText(Messages.DELETE_KEY_SHORTCUT_TOOLTIP);
		deleteButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		deleteButton.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		attachDeleteButtonListener(deleteButton);

		upButton = new Label(composite_1, SWT.NONE);
		upButton.setToolTipText(Messages.MOVE_UP_KEY_SHORTCUT_TOOLTIP);
		upButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		upButton.setImage(ImagePathConstant.MOVEUP_BUTTON.getImageFromRegistry());
		attachUpButtonListener(upButton);

		downButton = new Label(composite_1, SWT.NONE);
		downButton.setToolTipText(Messages.MOVE_DOWN_KEY_SHORTCUT_TOOLTIP);
		downButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		downButton.setImage(ImagePathConstant.MOVEDOWN_BUTTON.getImageFromRegistry());
		attachDownButtonListerner(downButton);
		deleteButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
		return composite_1;
	}

	private void attachDownButtonListerner(Label downButton) {
		downButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// Nothing to do

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// Nothing to do

			}

			@Override
			public void mouseUp(MouseEvent e) {
				moveRowDown();
			}
		});

	}
	
	private void moveRowDown()
	{
		int index1 = 0, index2 = 0;
		index1 = targetTable.getSelectionIndex();

		if (index1 < propertyList.size() - 1) {
			String data = targetTableViewer.getTable().getItem(index1).getText();
			index2 = index1 + 1;
			String data2 = targetTableViewer.getTable().getItem(index2).getText();

			FilterProperties filter = new FilterProperties();
			filter.setPropertyname(data2);
			propertyList.set(index1, filter);

			filter = new FilterProperties();
			filter.setPropertyname(data);
			propertyList.set(index2, filter);
			targetTableViewer.refresh();
			targetTable.setSelection(index1 + 1);
			checkFieldsOnStartup();
		}
	
	}

	private void attachUpButtonListener(Label upButton) {
		upButton.addMouseListener(new MouseListener() {
			

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// Nothing to do

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// Nothing to do

			}

			@Override
			public void mouseUp(MouseEvent e) {
				moveRowUp();
			}
		});

	}

	private void moveRowUp()
	{
		int index1 = 0, index2 = 0;
		index1 = targetTable.getSelectionIndex();

		if (index1 > 0) {
			index2 = index1 - 1;
			String data = targetTableViewer.getTable().getItem(index1).getText();
			String data2 = targetTableViewer.getTable().getItem(index2).getText();

			FilterProperties filter = new FilterProperties();
			filter.setPropertyname(data2);
			propertyList.set(index1, filter);

			filter = new FilterProperties();
			filter.setPropertyname(data);
			propertyList.set(index2, filter);
			targetTableViewer.refresh();
			targetTable.setSelection(index1 - 1);
			checkFieldsOnStartup();
		}
	
	}
	
	private void attachDeleteButtonListener(final Label deleteButton) {
		deleteButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// Nothing to do
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// Nothing to do
			}

			@Override
			public void mouseUp(MouseEvent e) {
				deleteRow();
			}

		});

	}
	
	private void deleteRow()
	{
		WidgetUtility.setCursorOnDeleteRow(targetTableViewer, propertyList);
		isAnyUpdatePerformed = true;
		targetTableViewer.refresh();
		
		
		if (propertyList.size() < 1) {
			deleteButton.setEnabled(false);
		} 
		if (propertyList.size() <=1) {
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		} 
		checkFieldsOnStartup();
	
	}

	private void attachAddButtonListern(Label addButton) {
		addButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// Nothing to do

			}

			@Override
			public void mouseDown(MouseEvent e) {
				// Nothing to do

			}

			@Override
			public void mouseUp(MouseEvent e) {
				addNewRow();
			}

		});
	}

	private void addNewRow()
	{
		targetTable.getParent().setFocus();
		addNewProperty(targetTableViewer, null);
		enableControlButons();
		checkFieldsOnStartup();
	
	}
	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton=createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(646, 587);
	}

	private void createTargetTable(Composite container) {
		targetTableViewer = new TableViewer(container, SWT.BORDER | SWT.MULTI|SWT.FULL_SELECTION);
		targetTable = targetTableViewer.getTable();
		GridData gd_table_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table_1.heightHint = 449;
		gd_table_1.widthHint = 285;
		targetTable.setLayoutData(gd_table_1);

		targetTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				addNewProperty(targetTableViewer, null);
				enableControlButons();
				checkFieldsOnStartup();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				lblPropertyError.setVisible(false);
			}
		});
		targetTableViewer.getTable().addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.keyCode == SWT.ARROW_UP) {
					e.doit = false;
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					e.doit = false;
				}

			}
		});

		targetTable.setBounds(196, 70, 324, 400);
		targetTableViewer.setContentProvider(new ELTFilterContentProvider());
		targetTableViewer.setLabelProvider(new ELTFilterLabelProvider());
		targetTableViewer.setInput(propertyList);

		TableColumn targetTableColumn = new TableColumn(targetTable, SWT.LEFT);
		targetTableColumn.setText("Field Name");
		targetTableColumn.setWidth(352);
		targetTable.setHeaderVisible(true);
		targetTable.setLinesVisible(true);

		// enables the tab functionality
		TableViewerEditor.create(targetTableViewer, new ColumnViewerEditorActivationStrategy(targetTableViewer),
				ColumnViewerEditor.KEYBOARD_ACTIVATION | ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL);

		CellEditor propertyNameEditor = new TextCellEditor(targetTable);

		CellEditor[] editors = new CellEditor[] { propertyNameEditor };
		propertyNameEditor.setValidator(createNameEditorValidator(PROPERTY_NAME_BLANK_ERROR));

		targetTableViewer.setColumnProperties(PROPS);
		targetTableViewer.setCellModifier(new ELTCellModifier(targetTableViewer));
		targetTableViewer.setCellEditors(editors);
		
		
		loadProperties(targetTableViewer);
		
		if (propertyList.size() != 0) {
			deleteButton.setEnabled(true);
		}
		if (propertyList.size() >= 2) {
			upButton.setEnabled(true);
			downButton.setEnabled(true);
		}

		dropTarget = new DropTarget(targetTable, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				operationOnDrop(event);
			}

		});
		attachShortcutListner(targetTable);

	}
	
	protected void operationOnDrop(DropTargetEvent event) {
		for (String fieldName : getformatedData((String) event.data))
				addNewProperty(targetTableViewer, fieldName);
		enableControlButons();
		HiveFieldDialogHelper.INSTANCE.compareAndChangeColor(getTargetTableViewer(),sourceFieldsList);
	}

	public void createSourceTable(Composite container) {

		sourceTableViewer = new TableViewer(container, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		sourceTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if(sourceTable.getSelection().length==1){
				addNewProperty(targetTableViewer, sourceTable.getSelection()[0].getText());
				enableControlButons();
				checkFieldsOnStartup();
				}
			}
		});
		sourceTable = sourceTableViewer.getTable();
		sourceTable.setLinesVisible(true);
		sourceTable.setHeaderVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = 437;
		gd_table.widthHint = 189;
		sourceTable.setLayoutData(gd_table);

		tableViewerColumn = new TableViewerColumn(sourceTableViewer, SWT.LEFT);
		sourceTableColumn = tableViewerColumn.getColumn();
		sourceTableColumn.setWidth(255);
		sourceTableColumn.setText(Messages.AVAILABLE_FIELDS_HEADER);
		getSourceFieldsFromPropagatedSchema(sourceTable);
		dragSource = new DragSource(sourceTable, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) { // Set the data to be the first selected item's text

				event.data = formatDataToTransfer(sourceTable.getSelection());
			}

		});
	}

	/**
	 * Validate.
	 * 
	 * @return true, if successful
	 */
	protected boolean validate() {

		int propertyCounter = 0;

		for (FilterProperties temp : propertyList) {
			if (!temp.getPropertyname().trim().isEmpty()) {
				// String Regex = "[\\@]{1}[\\{]{1}[\\w]*[\\}]{1}||[\\w]*"; --- TODO PLEASE DO NOT REMOVE THIS COMMENT
				Matcher matchs = Pattern.compile(Constants.REGEX).matcher(temp.getPropertyname().trim());
				if (!matchs.matches()) {
					targetTable.setSelection(propertyCounter);
					lblPropertyError.setVisible(true);
					lblPropertyError.setText(Messages.ALLOWED_CHARACTERS);
					return false;
				}
			} else {
				targetTable.setSelection(propertyCounter);
				lblPropertyError.setVisible(true);
				lblPropertyError.setText(Messages.EmptyFieldNameNotification);
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
				String currentSelectedFld = targetTable.getItem(targetTable.getSelectionIndex()).getText();
				String valueToValidate = String.valueOf(value).trim();
				if (valueToValidate.isEmpty()) {
					lblPropertyError.setText(ErrorMessage);
					lblPropertyError.setVisible(true);
					return Constants.ERROR; //$NON-NLS-1$
				} else {
					lblPropertyError.setVisible(false);
				}

				for (FilterProperties temp : propertyList) {
					if (!currentSelectedFld.equalsIgnoreCase(valueToValidate)
							&& temp.getPropertyname().trim().equalsIgnoreCase(valueToValidate)) {
						lblPropertyError.setText(PROPERTY_EXISTS_ERROR);
						lblPropertyError.setVisible(true);
						return Constants.ERROR; //$NON-NLS-1$
					}

					lblPropertyError.setVisible(false);
				}

				return null;

			}
		};
		return propertyValidator;
	}

	private String formatDataToTransfer(TableItem[] selectedTableItems) {
		StringBuffer buffer = new StringBuffer();
		for (TableItem tableItem : selectedTableItems) {
			buffer.append(tableItem.getText() + "#");
		}
		return buffer.toString();
	}

	private String[] getformatedData(String formatedString) {
		String[] fieldNameArray = null;
		if (formatedString != null) {
			fieldNameArray = formatedString.split("#");
		}
		return fieldNameArray;
	}

	private boolean isPropertyAlreadyExists(String valueToValidate) {
		for (FilterProperties temp : propertyList)
			if (temp.getPropertyname().trim().equalsIgnoreCase(valueToValidate))
				return true;
		return false;
	}

	private void getSourceFieldsFromPropagatedSchema(Table sourceTable) {
		TableItem sourceTableItem = null;
		if (sourceFieldsList != null && !sourceFieldsList.isEmpty())
			for (String filedName : sourceFieldsList) {
				sourceTableItem = new TableItem(sourceTable, SWT.NONE);
				sourceTableItem.setText(filedName);
			}

	}

	/**
	 * @param fieldNameList
	 */
	public void setSourceFieldsFromPropagatedSchema(List<String> fieldNameList) {
		this.sourceFieldsList = fieldNameList;

	}

	/**
	 * @return String, String having comma separated field names
	 */
	public String getResultAsCommaSeprated() {
		StringBuffer result = new StringBuffer();
		for (String fieldName : fieldNameList)
			result.append(fieldName + ",");
		if (result.lastIndexOf(",") != -1)
			result = result.deleteCharAt(result.lastIndexOf(","));
		return result.toString();
	}

	/**
	 * This method sets the property from comma separated String
	 * 
	 * @param commaSeperatedString
	 *            , Comma separated string,
	 * 
	 */
	public void setPropertyFromCommaSepratedString(String commaSeperatedString) {
		String[] fieldNameArray = null;
		if (commaSeperatedString != null) {
			fieldNameArray = commaSeperatedString.split(",");
			for (String fieldName : fieldNameArray) {
				fieldNameList.add(fieldName);
			}
		}
	}

	@Override
	protected void okPressed() {
		
		if(OSValidator.isMac()){
			
			for(CellEditor cellEditor : targetTableViewer.getCellEditors()){
				if(cellEditor !=null){
				cellEditor.getControl().setEnabled(false); //Saves the existing value of CellEditor
				cellEditor.getControl().setEnabled(true);
				}
			}
		}
		if (validate()) {
			fieldNameList.clear();
			for (FilterProperties temp : propertyList) {
				fieldNameList.add(temp.getPropertyname());
			}

			if (isAnyUpdatePerformed) {
				propertyDialogButtonBar.enableApplyButton(true);
			}
			
			if(!HiveFieldDialogHelper.INSTANCE.compareAndChangeColor(getTargetTableViewer(),sourceFieldsList)){
				int rc=HiveFieldDialogHelper.INSTANCE.Message_Dialog();
				   if(rc==0){
					   super.okPressed();
				   }
				   else if(rc==1){
					   return;
				   }
			}
			
			okPressed=true;
			super.okPressed();
		} else {
			return;
		}
		
		
	}

	@Override
	protected void cancelPressed() {
		if (isAnyUpdatePerformed) {
			int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO |SWT.ICON_INFORMATION;
			MessageBox messageBox = new MessageBox(getShell(), style);
			messageBox.setText(INFORMATION); //$NON-NLS-1$
			messageBox.setMessage(Messages.MessageBeforeClosingWindow);
			if (messageBox.open() == SWT.YES) {
				closeDialog = super.close();
			}
		} else {
			closeDialog = super.close();
		}

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

	private void enableControlButons() {
		if (propertyList.size() >= 1) {
			deleteButton.setEnabled(true);
		}
		if (propertyList.size() >= 2) {
			upButton.setEnabled(true);
			downButton.setEnabled(true);
		}
	}
	
	protected TableViewer getTargetTableViewer() {
		return targetTableViewer;
	}
	
	private void attachShortcutListner(Table targetTable){
		Control currentControl=targetTable;
		
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
	
	
}
