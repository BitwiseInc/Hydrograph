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
package hydrograph.ui.propertywindow.widgets.customwidgets.excelcomponent;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.ExcelConfigurationDataStructure;
import hydrograph.ui.datastructure.property.ExcelFormattingDataStructure;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * ExcelFormattingDialog provides configuration settings for the Excel Component 
 * @author Bitwise
 *
 */
public class ExcelFormattingDialog extends Dialog {

	private Button deleteButton;
	private Table targetTable;
	private Table table_1;
	private TableViewer targetTableViewer;
	private DragSource dragSource;
	private List<String> schemaFields;
	private DropTarget dropTarget;
	private ExcelFormattingDataStructure excelFormattingDataStructure;
	private PropertyDialogButtonBar propDialogButtonBar;
	private List<ExcelConfigurationDataStructure> listOfExcelConfiguration;
	private CCombo combo;
	private Composite top_composite;
	private List<String> draggedFields = new ArrayList<>();
	private TableViewer avaliableFieldTableViewer;
	private String windowLabel;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param windowLabel 
	 * @param propDialogButtonBar
	 * @param schemaFields
	 * @param excelFormattingDataStructure
	 */
	public ExcelFormattingDialog(Shell parentShell, String windowLabel, List<String> schemaFields,
			PropertyDialogButtonBar propDialogButtonBar, ExcelFormattingDataStructure excelFormattingDataStructure) {
		super(parentShell);
		this.schemaFields = schemaFields;
		this.excelFormattingDataStructure = excelFormattingDataStructure;
		this.listOfExcelConfiguration = new ArrayList<>();
		this.propDialogButtonBar = propDialogButtonBar;
		if(StringUtils.isNotBlank(windowLabel))
			this.windowLabel=windowLabel;
		else
			this.windowLabel=Messages.EXCEL_FORMATTING_WINDOW_LABEL;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.getShell().setText(this.windowLabel);
		
		Composite main_composite = new Composite(container, SWT.NONE);
		main_composite.setLayout(new GridLayout(1, false));
		main_composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		createTopComposite(main_composite);

		createTableComposite(main_composite);

		populateWidget();

		return container;
	}

	private void populateWidget() {
		
		if(excelFormattingDataStructure !=null){
			List<ExcelConfigurationDataStructure> listOfExcelConfiguration2 = excelFormattingDataStructure.getListOfExcelConfiguration();
			if(listOfExcelConfiguration2 !=null && listOfExcelConfiguration2.size() >0 ){
				draggedFields.clear();
				for(ExcelConfigurationDataStructure excelConfigurationDataStructure :  listOfExcelConfiguration2){
					if(StringUtils.isNotBlank(excelConfigurationDataStructure.getFieldName())){
						draggedFields.add(excelConfigurationDataStructure.getFieldName());
					}
				}
				draggedFields.add(0, "Select");
				combo.setItems(convertToArray(draggedFields));
			}
			if(StringUtils.isNotBlank(excelFormattingDataStructure.getCopyOfField())){
				combo.setText(excelFormattingDataStructure.getCopyOfField());
			}
			if(listOfExcelConfiguration2 !=null){
				this.listOfExcelConfiguration.clear();
				ArrayList tmplist = (ArrayList)this.excelFormattingDataStructure.getListOfExcelConfiguration();
				this.listOfExcelConfiguration.addAll((ArrayList)tmplist.clone());
				targetTableViewer.setInput(listOfExcelConfiguration);
			}
		}
		
		highlightDropFields();
		enableDeleteButton();
	}

	private void createTableComposite(Composite main_composite) {
		Composite tableComposite = new Composite(main_composite, SWT.NONE);
		tableComposite.setLayout(new GridLayout(1, false));
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		SashForm sashForm = new SashForm(tableComposite, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		createSourceTable(sashForm);

		createTragetTable(sashForm);
		sashForm.setWeights(new int[] { 189, 385 });
	}

	private void createTragetTable(SashForm sashForm) {
		targetTableViewer = new TableViewer(sashForm, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		targetTable = targetTableViewer.getTable();
		targetTable.setLinesVisible(true);
		targetTable.setHeaderVisible(true);

		TableViewerColumn columnNameViewer = new TableViewerColumn(targetTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = columnNameViewer.getColumn();
		tblclmnNewColumn.setWidth(148);
		tblclmnNewColumn.setText(Messages.COLUMN_NAME);
		columnNameViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((ExcelConfigurationDataStructure) element).getFieldName();
			}
		});

		TableViewerColumn headerFormatViewer = new TableViewerColumn(targetTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_3 = headerFormatViewer.getColumn();
		tblclmnNewColumn_3.setWidth(117);
		tblclmnNewColumn_3.setText(Messages.HEADER_FORMAT);
		tblclmnNewColumn_3.setToolTipText(Messages.HEADER_FORMAT_TOOLTIP);
		headerFormatViewer
				.setEditingSupport(new HeaderFormattingEditingSupport(targetTableViewer, propDialogButtonBar));
		headerFormatViewer.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((ExcelConfigurationDataStructure) element).getHeaderMap().toString();
			}
			
			@Override
			public String getToolTipText(Object element) {
				return ((ExcelConfigurationDataStructure) element).getHeaderMap().toString();
			}
		});

		TableViewerColumn dataFormatViewer = new TableViewerColumn(targetTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_1 = dataFormatViewer.getColumn();
		tblclmnNewColumn_1.setWidth(138);
		tblclmnNewColumn_1.setText(Messages.DATA_FORMAT);
		tblclmnNewColumn_1.setToolTipText(Messages.DATA_FORMAT_TOOLTIP);
		dataFormatViewer.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((ExcelConfigurationDataStructure) element).getDataMap().toString();
			}
			
			@Override
			public String getToolTipText(Object element) {
				return ((ExcelConfigurationDataStructure) element).getDataMap().toString();
			}
		});
		dataFormatViewer.setEditingSupport(new DataFormattingEditingSupport(targetTableViewer, propDialogButtonBar));

		targetTableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				return ((List) inputElement).toArray();
			}
		});
		if(!schemaFields.isEmpty() && schemaFields.size()>0){
			targetTableViewer.setInput(listOfExcelConfiguration);
		}else{
			targetTableViewer.setInput(new ArrayList<>());
		}
		

		TableViewerEditor.create(targetTableViewer, new ColumnViewerEditorActivationStrategy(targetTableViewer),
				ColumnViewerEditor.KEYBOARD_ACTIVATION | ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL);

		enableDeleteButton();

		attachDropListener();
	}

	private void attachDropListener() {
		dropTarget = new DropTarget(targetTable, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dropTarget.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				for (String fieldName : getformatedData((String) event.data)) {
					if(isPropertyAlreadyExists(fieldName)){
						return;
					}else{
						ExcelConfigurationDataStructure excelConfigurationDataStructure = new ExcelConfigurationDataStructure();
						excelConfigurationDataStructure.setFieldName(fieldName);
						listOfExcelConfiguration.add(excelConfigurationDataStructure);
						targetTableViewer.refresh();
						draggedFields.add(fieldName);
						enableDeleteButton();
					}
				}
				combo.setItems(convertToArray(draggedFields));
				combo.select(0);
				top_composite.layout();
				top_composite.getParent().layout();
				highlightDropFields();

			}
		});
	}

	private void enableDeleteButton() {
		if (listOfExcelConfiguration.size() != 0) {
			deleteButton.setEnabled(true);
		}
	}

	private void createSourceTable(SashForm sashForm) {
	    avaliableFieldTableViewer = new TableViewer(sashForm, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		table_1 = avaliableFieldTableViewer.getTable();
		table_1.setLinesVisible(true);
		table_1.setHeaderVisible(true);
		avaliableFieldTableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				return ((List) inputElement).toArray();
			}
		});
		avaliableFieldTableViewer.setInput(schemaFields);

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(avaliableFieldTableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_2 = tableViewerColumn_2.getColumn();
		tblclmnNewColumn_2.setWidth(196);
		tblclmnNewColumn_2.setText(Messages.AVAILABLE_FIELDS_HEADER);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider());

		attachDragListener();
	}

	private void attachDragListener() {
		dragSource = new DragSource(table_1, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
				// Set the data to be the first selected item's text
				event.data = formatDataToTransfer(table_1.getSelection());
			}

		});
	}

	private void createTopComposite(Composite main_composite) {
		top_composite = new Composite(main_composite, SWT.NONE);
		top_composite.setLayout(new GridLayout(4, false));
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_composite_1.heightHint = 45;
		top_composite.setLayoutData(gd_composite_1);

		Label applyAllLabel = new Label(top_composite, SWT.NONE);
		applyAllLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		applyAllLabel.setText(Messages.REMAINING_FIELDS);
		applyAllLabel.setToolTipText(Messages.REMAINING_FIELDS_TOOLTIP);

		combo = new CCombo(top_composite, SWT.NONE);
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_combo.widthHint = 143;
		combo.setLayoutData(gd_combo);
		draggedFields.add(0,"Select");
		combo.setItems(convertToArray(draggedFields));
		combo.select(0);
		
		Composite composite_2 = new Composite(top_composite, SWT.NONE);
		composite_2.setLayout(new GridLayout(3, false));
		composite_2.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, true, 1, 1));

		deleteButton = new Button(composite_2, SWT.NONE);
		deleteButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
		deleteButton.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry()); 
		attachDeleteButtonListener(deleteButton);

		deleteButton.setEnabled(false);
	}


	private void attachDeleteButtonListener(Button deleteButton) {
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setValueForCellEditor();
				TableItem[] selection = targetTableViewer.getTable().getSelection();
				String comboValue = combo.getText();
				for(TableItem item : selection){
					if(item != null){
						for(String listvalue : new ArrayList<>(draggedFields)){
							if(StringUtils.equals(listvalue, item.getText())){
								draggedFields.remove(listvalue);
							}
						}
					}
					
					combo.setItems(convertToArray(draggedFields));
					if(StringUtils.equals(comboValue, item.getText())){
						combo.select(0);
					}else{
						combo.setText(comboValue);
					}
					
				}
				
				top_composite.layout();
				top_composite.getParent().layout();

				WidgetUtility.setCursorOnDeleteRow(targetTableViewer, listOfExcelConfiguration);
				targetTableViewer.refresh();
				if (listOfExcelConfiguration.size() < 1) {
					deleteButton.setEnabled(false);
				}
				
				highlightDropFields();
			}
		});
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

	private String formatDataToTransfer(TableItem[] selectedTableItems) {
		StringBuffer buffer = new StringBuffer();
		for (TableItem tableItem : selectedTableItems) {
			buffer.append(tableItem.getText() + "#");
		}
		return buffer.toString();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(654, 571);
	}
	
	private void highlightDropFields(){
		TableItem[] items = avaliableFieldTableViewer.getTable().getItems();
		for(TableItem item : items){
			item.setForeground(new Color(null, 0,0,0));
			for(String draggedItem : draggedFields){
				if(StringUtils.equals(draggedItem, item.getText())){
					item.setForeground(new Color(null, 128,0,0));
				}
			}
		}
	}
	
	private void setValueForCellEditor() {
		if (OSValidator.isMac()) {
			for (CellEditor cellEditor : targetTableViewer.getCellEditors()) {
				if (cellEditor != null) {
					cellEditor.getControl().setEnabled(false); // Saves the  existing value of  CellEditor
					cellEditor.getControl().setEnabled(true);
				}
			}
		}
	}


	private String[] getformatedData(String formatedString) {
		String[] fieldNameArray = null;
		if (formatedString != null) {
			fieldNameArray = formatedString.split("#");
		}
		return fieldNameArray;
	}
	
	private boolean isPropertyAlreadyExists(String valueToValidate) {
		for (ExcelConfigurationDataStructure temp : listOfExcelConfiguration)
			if (temp.getFieldName().trim().equalsIgnoreCase(valueToValidate))
				return true;
		return false;
	}
	
	private String[] convertToArray(List<String> items) {
		String[] stringItemsList = new String[items.size()];
		int index = 0;
		for (String item : items) {
			stringItemsList[index++] = item;
		}
		return stringItemsList;
	}
	
	@Override
	protected void cancelPressed() {
		listOfExcelConfiguration.clear();
		draggedFields.clear();
		super.cancelPressed();
	}
	
	public ExcelFormattingDataStructure getExcelFormattingData(){
		return excelFormattingDataStructure;
	}
	
	@Override
	protected void okPressed() {
		this.excelFormattingDataStructure = new ExcelFormattingDataStructure();
		
		if(StringUtils.isNotBlank(combo.getText())){
			excelFormattingDataStructure.setCopyOfField(combo.getText());
		}
		
		if(!StringUtils.equals(combo.getText(),"Select")){
			if(schemaFields !=null && !schemaFields.isEmpty() && draggedFields !=null && !draggedFields.isEmpty()){
				List remainingFields = ListUtils.subtract(schemaFields, draggedFields);
				excelFormattingDataStructure.setCopyFieldList(remainingFields);
			}
		}else{
			excelFormattingDataStructure.setCopyFieldList(new ArrayList<>());
		}
		
		
		
		if(targetTableViewer.getInput() !=null){
			excelFormattingDataStructure.setListOfExcelConfiguration((List<ExcelConfigurationDataStructure>) targetTableViewer.getInput());
		}
		super.okPressed();
	}
}
