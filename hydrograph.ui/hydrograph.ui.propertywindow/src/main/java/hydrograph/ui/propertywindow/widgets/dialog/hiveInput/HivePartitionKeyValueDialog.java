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

package hydrograph.ui.propertywindow.widgets.dialog.hiveInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
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
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.InputHivePartitionColumn;
import hydrograph.ui.datastructure.property.InputHivePartitionKeyValues;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.runtimeproperty.PropertyContentProvider;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTCellModifier;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTFilterContentProvider;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTFilterLabelProvider;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * 
 * The class to create Partition key field dialog.
 * 
 * @author Bitwise
 * 
 */

public class HivePartitionKeyValueDialog extends Dialog {
	private static final Logger logger = LogFactory.INSTANCE
			.getLogger(HivePartitionKeyValueDialog.class);

	private final List<FilterProperties> propertyList;
	private List<String> fieldNameList;
	private List<InputHivePartitionColumn> hivePartitionColumns;
	private String componentName;

	private static final String[] PROPS = { Constants.COMPONENT_NAME };
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
	private List<String> sourceFieldsList;

	PropertyDialogButtonBar propertyDialogButtonBar;
	private boolean closeDialog;
	private boolean okPressed;
	private Button deleteButton;
	private Button upButton;
	private Button downButton;
	private Composite container;
	private Composite PartKeyComposite;
	private Composite partkeyValueComposite;
	
	private TableViewer keyValueTableViewer;
	private Button btnCheckButton;
	private List<HivePartitionFields> keyValues;
	private Set<String> keyValueColumns;

	public HivePartitionKeyValueDialog(Shell parentShell,PropertyDialogButtonBar propertyDialogButtonBar) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
		
		propertyList = new ArrayList<FilterProperties>();
		fieldNameList = new ArrayList<String>();
		keyValues= new ArrayList<>();
		hivePartitionColumns = new ArrayList<InputHivePartitionColumn>();
		keyValueColumns = new HashSet<>();
		
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
			targetTableViewer.editElement(
					targetTableViewer.getElementAt(propertyList.size() - 1), 0);
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
	 * @param hivePartitionKeyValues
	 */
	public void setRuntimePropertySet(InputHivePartitionKeyValues hivePartitionKeyValues) {
		this.hivePartitionColumns=hivePartitionKeyValues.getKeyValues();
		this.fieldNameList = hivePartitionKeyValues.getKey();
		
		
	}
	
	/**
	 * 
	 * @return
	 */
	public InputHivePartitionKeyValues getRuntimePropertySet(){
		InputHivePartitionKeyValues inputHivePartitionKeyValues = new InputHivePartitionKeyValues();
		inputHivePartitionKeyValues.setKey(this.fieldNameList);
		inputHivePartitionKeyValues.setKeyValues(this.hivePartitionColumns);
	
		return inputHivePartitionKeyValues;
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

	/**
	 * 
	 */
	private void loadKeyValueProperties(TableViewer keyValueTableViewer) {
		
		keyValues.clear();
		keyValues=HiveFieldDialogHelper.INSTANCE.getSavedColumnData(hivePartitionColumns,new ArrayList<String>(fieldNameList));
		
		if(keyValues.size()>0||hivePartitionColumns.size()>0){
			
			btnCheckButton.setSelection(true);
			partkeyValueComposite.setVisible(true);
			
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
	    getShell().setText(Constants.PARTITION_KEYS_WINDOW_TITLE);
		
		container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		SashForm outterSashForm = new SashForm(container, SWT.BORDER);
		outterSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		PartKeyComposite = new Composite(outterSashForm, SWT.NONE);
		addButtonPanel(PartKeyComposite);
		SashForm srcTgtTableSashForm = new SashForm(PartKeyComposite, SWT.NONE);
		srcTgtTableSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		createSourceTable(srcTgtTableSashForm);
		createTargetTable(srcTgtTableSashForm);
		
		partkeyValueComposite = new Composite(outterSashForm, SWT.NONE);
		createKeyValueComposite(partkeyValueComposite);
		
		outterSashForm.setWeights(new int[] {451, 176});
		
		checkTargetFieldsSequence();
		
		getShell().setMinimumSize(400,500);
		return container;
	}

	/**
	 * @param partkeyValueComposite2
	 */
	private void createKeyValueComposite(Composite keyValueComposite) {
		
		keyValueComposite.setLayout(new GridLayout(1, false));
		
		Composite keyValueButtonPanelcmpst = new Composite(keyValueComposite, SWT.NONE);
		keyValueButtonPanelcmpst.setLayout(new GridLayout(2, false));
		GridData gd_keyValueButtonPanelcmpst = new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1);
		gd_keyValueButtonPanelcmpst.widthHint = 324;
		
		keyValueComposite.setVisible(false);
		
		keyValueButtonPanelcmpst.setLayoutData(gd_keyValueButtonPanelcmpst);
						
		final Button keyValAddButton = new Button(keyValueButtonPanelcmpst, SWT.NONE);
		final Button keyValueDelButton = new Button(keyValueButtonPanelcmpst, SWT.NONE);
		
		keyValAddButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		keyValAddButton.setText("");
		keyValAddButton.setImage(ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
		keyValAddButton.addSelectionListener(addButtonListner(keyValueDelButton));
		keyValAddButton.setToolTipText(Messages.HIVE_PARTI_KEY_VALUE_ADD_TOOLTIP);
		
	    
		keyValueDelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		keyValueDelButton.setText("");
		keyValueDelButton.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		keyValueDelButton.addSelectionListener(deleteButtonListner(keyValueDelButton));
		keyValueDelButton.setToolTipText(Messages.HIVE_PARTI_KEY_VALUE_DEL_TOOLTIP);
		
		keyValueTableViewer = new TableViewer(partkeyValueComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		
		Table table = keyValueTableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		keyValueTableViewer.setCellModifier(new HiveFieldDialogCellModifier(keyValueTableViewer));
		
		loadKeyValueProperties(keyValueTableViewer);
		
		if(keyValues.size()==0){
			keyValueDelButton.setEnabled(false);
		}
		
		if(keyValues.size()>0){
			
			keyValueColumns.clear();
			for (int i = 0; i < propertyList.size(); i++) {
	
				TableViewerColumn tableViewerColumn = new TableViewerColumn(keyValueTableViewer, SWT.NONE);
				TableColumn fieldColumn = tableViewerColumn.getColumn();
				fieldColumn.setWidth(100);
				fieldColumn.setText(propertyList.get(i).getPropertyname());
				keyValueColumns.add(propertyList.get(i).getPropertyname());
	
			 }
		}		
		TableViewerEditor.create(keyValueTableViewer, new ColumnViewerEditorActivationStrategy(keyValueTableViewer),
			ColumnViewerEditor.KEYBOARD_ACTIVATION | ColumnViewerEditor.TABBING_HORIZONTAL
			| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL);
        
		keyValueTableViewer.setColumnProperties(getColumnProperties(propertyList));
		
		keyValueTableViewer.setContentProvider(new PropertyContentProvider());
		keyValueTableViewer.setLabelProvider(new HiveFieldDialogLableProvider());
		
	    keyValueTableViewer.setInput(keyValues);
		
		CellEditor[] cellEditors= new CellEditor[propertyList.size()];
		
		for(int i=0;i<propertyList.size();i++){
			cellEditors[i]=new TextCellEditor(table);
		}
		
		keyValueTableViewer.setCellEditors(cellEditors);
		
		keyValueTableViewer.setData(Constants.PARTITION_KEYS, propertyList);
		
		keyValueTableViewer.getTable().addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (validate()) {
					addNewRow();
				}
				if (keyValues.size() >0) {
					keyValueDelButton.setEnabled(true);
				}
			}
		});
		
		keyValueTableViewer.getTable().addMouseListener(new SingleClickEvent(new Runnable() {
			@Override
			public void run() {
					validate();
			}
		}));
	}
	
	/**
	 * 
	 * @param propertyList
	 * @return
	 */
	private String[] getColumnProperties(List<FilterProperties> propertyList) {
		Object [] tempArray=propertyList.toArray();
		String[] str=new String[propertyList.size()];
		for(int i=0;i<str.length;i++){
			str[i]=(String)(((FilterProperties) tempArray[i]).getPropertyname());
		}
		return str;
		
	}
	
 /**
  * 
  * @param srcTgtTableSashForm
  */
	private void createSourceTable(SashForm srcTgtTableSashForm) {
		
		sourceTableViewer = new TableViewer(srcTgtTableSashForm, SWT.BORDER
				| SWT.MULTI | SWT.FULL_SELECTION);
		sourceTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (sourceTable.getSelection().length == 1) {
					addNewProperty(targetTableViewer,sourceTable.getSelection()[0].getText());
					checkTargetFieldsSequence();
					refreshKeyValueColums(propertyList,false);
					enableControlButons();
				}
			}
		});
		sourceTable = sourceTableViewer.getTable();
		sourceTable.setLinesVisible(true);
		sourceTable.setHeaderVisible(true);
		sourceTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));

		tableViewerColumn = new TableViewerColumn(sourceTableViewer, SWT.LEFT);
		sourceTableColumn = tableViewerColumn.getColumn();
		sourceTableColumn.setWidth(300);
		sourceTableColumn.setText(Messages.AVAILABLE_FIELDS_HEADER);
		getSourceFieldsFromPropagatedSchema(sourceTable);
		dragSource = new DragSource(sourceTable, DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) { // Set the data to
																// be the first
																// selected
																// item's text

				event.data = formatDataToTransfer(sourceTable.getSelection());
			}

		});
	}

	private void createTargetTable(Composite srcTgtTableSashForm) {
		targetTableViewer = new TableViewer(srcTgtTableSashForm, SWT.BORDER
				| SWT.MULTI|SWT.FULL_SELECTION);
		targetTable = targetTableViewer.getTable();
		targetTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true,
				1, 1));

		targetTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				addNewProperty(targetTableViewer, null);
				checkTargetFieldsSequence();
				refreshKeyValueColums(propertyList,false);
				enableControlButons();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				lblPropertyError.setVisible(false);
			}
		});
		targetTableViewer.getTable().addTraverseListener(
				new TraverseListener() {

					@Override
					public void keyTraversed(TraverseEvent e) {
						if (e.keyCode == SWT.ARROW_UP) {
							e.doit = false;
						} else if (e.keyCode == SWT.ARROW_DOWN) {
							e.doit = false;
						}

					}
				});
		
		targetTableViewer.getTable().addMouseListener(new SingleClickEvent(new Runnable() {
			@Override
			public void run() {
					validate();
			}
		}));

		targetTable.setBounds(196, 70, 324, 400);
		targetTableViewer.setContentProvider(new ELTFilterContentProvider());
		targetTableViewer.setLabelProvider(new ELTFilterLabelProvider());
		targetTableViewer.setInput(propertyList);

		TableColumn targetTableColumn = new TableColumn(targetTable, SWT.LEFT);
		targetTableColumn.setText("Field Name");
		targetTableColumn.setWidth(300);
		targetTable.setHeaderVisible(true);
		targetTable.setLinesVisible(true);

		// enables the tab functionality
		TableViewerEditor.create(targetTableViewer,
				new ColumnViewerEditorActivationStrategy(targetTableViewer),
				ColumnViewerEditor.KEYBOARD_ACTIVATION
						| ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL);

		CellEditor propertyNameEditor = new TextCellEditor(targetTable);

		CellEditor[] editors = new CellEditor[] { propertyNameEditor };
		propertyNameEditor
				.setValidator(createNameEditorValidator(Messages.EmptyFieldNameNotification));

		targetTableViewer.setColumnProperties(PROPS);
		targetTableViewer
				.setCellModifier(new ELTCellModifier(targetTableViewer));
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
				for (String fieldName : getformatedData((String) event.data))
					addNewProperty(targetTableViewer, fieldName);
				checkTargetFieldsSequence();
				refreshKeyValueColums(propertyList,false);
				enableControlButons();
			}
		});	
			
	}

	/**
	 * 
	 * @param propertyList
	 * @param isKeyDeleted 
	 */
	private void refreshKeyValueColums(List<FilterProperties> propertyList, boolean isKeyDeleted) {
			
			if(btnCheckButton.getSelection()){
				List<HivePartitionFields> tempKeyValues= new ArrayList<>(keyValues);
				List<String> tempKeys= new ArrayList<>();
				
				HiveFieldDialogHelper.INSTANCE.disposeAllColumns(keyValueTableViewer, keyValues);
				for (FilterProperties tempkey : propertyList) {
					tempKeys.add(tempkey.getPropertyname());
				}
				
				keyValues=HiveFieldDialogHelper.INSTANCE.
						refreshKeyColumnsAndValues(tempKeys, tempKeyValues, new ArrayList<String>(keyValueColumns),isKeyDeleted);				
				
				createKeyValueTableAndArrangeColumns(btnCheckButton.getSelection(), propertyList);
			}
			
		}
		
	
	/**
	 * 	
	 * @param container
	 */
	private void addErrorLabel(Composite container) {
			
		lblPropertyError = new Label(container, SWT.NONE);
		lblPropertyError.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true,0,0));
		lblPropertyError.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
		lblPropertyError.setText(Messages.HIVE_FIELD_DIALOG_ERROR);
		lblPropertyError.setVisible(false);
		lblPropertyError.setData("Error", lblPropertyError);
		keyValueTableViewer.setData("Error", lblPropertyError);
	}
	
	

	protected Composite addButtonPanel(Composite KeyComposite) {
		
		KeyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,	1));
	
		KeyComposite.setLayout(new GridLayout(1, false));
		Composite partKeyButtonPanel = new Composite(KeyComposite, SWT.NONE);
		partKeyButtonPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,false, 1, 1));
		partKeyButtonPanel.setSize(607, 25);
		partKeyButtonPanel.setLayout(new GridLayout(5, false));

		btnCheckButton = new Button(partKeyButtonPanel, SWT.CHECK);
		btnCheckButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnCheckButton.setText(Messages.HIVE_PARTI_KEY_CHECKBOX_NAME);
		
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(propertyList.size()>0){
					
					lblPropertyError.setVisible(false);	
					createKeyValueTableAndArrangeColumns(btnCheckButton.getSelection(),propertyList);
				
				}else{
					
					lblPropertyError.setVisible(true);
					lblPropertyError.setText(Messages.EMPTY_TARGET_FIELD_ERROR);
					btnCheckButton.setSelection(false);
				}
			}
						
		});
		
		
		deleteButton = new Button(partKeyButtonPanel, SWT.NONE);
		deleteButton.setToolTipText(Messages.DELETE_SCHEMA_TOOLTIP);
		deleteButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		deleteButton.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		deleteButton.setToolTipText(Messages.HIVE_PARTI_KEY_DEL_TOOLTIP);
		attachDeleteButtonListener(deleteButton);

		upButton = new Button(partKeyButtonPanel, SWT.NONE);
		upButton.setToolTipText(Messages.MOVE_SCHEMA_UP_TOOLTIP);
		upButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		upButton.setImage(ImagePathConstant.MOVEUP_BUTTON.getImageFromRegistry());
		attachUpButtonListener(upButton);

		downButton = new Button(partKeyButtonPanel, SWT.NONE);
		downButton.setToolTipText(Messages.MOVE_SCHEMA_DOWN_TOOLTIP);
		downButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		downButton.setImage(ImagePathConstant.MOVEDOWN_BUTTON.getImageFromRegistry());
		attachDownButtonListerner(downButton);
		deleteButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);
		new Label(partKeyButtonPanel, SWT.NONE);
		return partKeyButtonPanel;
	}
	
	/**
	 * @param partitionKeyList 
	 * @param detail 
	 * 
	 */
	private void createKeyValueTableAndArrangeColumns(boolean selection, List<FilterProperties> partitionKeyList) {
		
				
		container.setBounds(container.getBounds().x, container.getBounds().y, container.getBounds().width, container.getBounds().height+3);
		
		if(selection){
		partkeyValueComposite.setVisible(true);	
		keyValueColumns.clear();
		for (int i = 0; i < partitionKeyList.size(); i++) {

			TableViewerColumn tableViewerColumn = new TableViewerColumn(keyValueTableViewer, SWT.NONE);
			TableColumn fieldColumn = tableViewerColumn.getColumn();
			fieldColumn.setWidth(100);
			fieldColumn.setText(partitionKeyList.get(i).getPropertyname());
			keyValueColumns.add(partitionKeyList.get(i).getPropertyname());
			
		}
		
		keyValueTableViewer.setCellModifier(new HiveFieldDialogCellModifier(keyValueTableViewer));
		keyValueTableViewer.setColumnProperties(getColumnProperties(partitionKeyList));
		keyValueTableViewer.setContentProvider(new PropertyContentProvider());
		keyValueTableViewer.setLabelProvider(new HiveFieldDialogLableProvider());
		keyValueTableViewer.setInput(keyValues);
		CellEditor[] cellEditors= new CellEditor[partitionKeyList.size()];
		
		for(int i=0;i<partitionKeyList.size();i++){
			cellEditors[i]=new TextCellEditor(keyValueTableViewer.getTable());
		}
		
		keyValueTableViewer.setCellEditors(cellEditors);
		
		}else{
			isAnyUpdatePerformed=true;
			partkeyValueComposite.setVisible(false);
			HiveFieldDialogHelper.INSTANCE.disposeAllColumns(keyValueTableViewer, keyValues);
		}
		
		keyValueTableViewer.refresh();
		
		container.setBounds(container.getBounds().x, container.getBounds().y, container.getBounds().width, container.getBounds().height-3);
		
	}
	
	
	
	/**
	 * 
	 * @param downButton
	 */

	private void attachDownButtonListerner(Button downButton) {
		downButton.addMouseListener(new MouseListener() {
			int index1 = 0, index2 = 0;

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
				index1 = targetTable.getSelectionIndex();
				index1 = targetTable.getSelectionIndex();

				if (index1 < propertyList.size() - 1) {
					String data = targetTableViewer.getTable().getItem(index1)
							.getText();
					index2 = index1 + 1;
					String data2 = targetTableViewer.getTable().getItem(index2)
							.getText();

					FilterProperties filter = new FilterProperties();
					filter.setPropertyname(data2);
					propertyList.set(index1, filter);

					filter = new FilterProperties();
					filter.setPropertyname(data);
					propertyList.set(index2, filter);
					targetTableViewer.refresh();
					targetTable.setSelection(index1 + 1);
					checkTargetFieldsSequence();
					swapKeyValueData(index1,index2);
					refreshKeyValueColums(propertyList, false);
				}
			}
		});

	}
    /**
     * 
     * @param upButton
     */
	private void attachUpButtonListener(Button upButton) {
		upButton.addMouseListener(new MouseListener() {
			int index1 = 0, index2 = 0;

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
				index1 = targetTable.getSelectionIndex();

				if (index1 > 0) {
					index2 = index1 - 1;
					String data = targetTableViewer.getTable().getItem(index1)
							.getText();
					String data2 = targetTableViewer.getTable().getItem(index2)
							.getText();

					FilterProperties filter = new FilterProperties();
					filter.setPropertyname(data2);
					propertyList.set(index1, filter);

					filter = new FilterProperties();
					filter.setPropertyname(data);
					propertyList.set(index2, filter);
					targetTableViewer.refresh();
					targetTable.setSelection(index1 - 1);
					checkTargetFieldsSequence();
					swapKeyValueData(index1,index2);
					refreshKeyValueColums(propertyList, false);
				}
			}
		});

	}
	
	
  /**
   * 
   * @param index1
   * @param index2
   */
	protected void swapKeyValueData(int index1, int index2) {
		
		for (HivePartitionFields keyValueRowData : keyValues) {
			
			Collections.swap(((List<String>)keyValueRowData.getRowFields()), index1, index2);
		}
		
	}
/**
 * 
 * @param deleteButton
 */
private void attachDeleteButtonListener(final Button deleteButton) {
		deleteButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
			@Override
			public void mouseDown(MouseEvent e) {	}
			@Override
			public void mouseUp(MouseEvent e) {
				WidgetUtility.setCursorOnDeleteRow(targetTableViewer, propertyList);
				isAnyUpdatePerformed = true;
				targetTableViewer.refresh();
				checkTargetFieldsSequence();
				refreshKeyValueColums(propertyList,true);
				
				if (propertyList.size() < 1) {
					deleteButton.setEnabled(false);
				}
				if (propertyList.size() <= 1) {
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				}
				
				if(btnCheckButton.getSelection()&&propertyList.size()==0){
					keyValues.clear();
					keyValueColumns.clear();
					partkeyValueComposite.setVisible(false);
					btnCheckButton.setSelection(false);
					keyValueTableViewer.refresh();
					container.setBounds(container.getBounds().x, container.getBounds().y, container.getBounds().width, container.getBounds().height-2);
				}
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
		
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		addErrorLabel(composite);
		
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
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
				// String Regex = "[\\@]{1}[\\{]{1}[\\w]*[\\}]{1}||[\\w]*"; ---
				// TODO PLEASE DO NOT REMOVE THIS COMMENT
				Matcher matchs = Pattern.compile(Constants.REGEX).matcher(
						temp.getPropertyname().trim());
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
		
		if (keyValueColumns.size() != 0) {
			return HiveFieldDialogCellModifier.validatePartitionKeyTable(keyValueTableViewer,
					lblPropertyError);
		}
		return true;
	}

	/**
	 * 
	 * @param ErrorMessage
	 * @return
	 */
	private ICellEditorValidator createNameEditorValidator(
			final String ErrorMessage) {
		ICellEditorValidator propertyValidator = new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				isAnyUpdatePerformed = true;
				String currentSelectedFld = targetTable.getItem(
						targetTable.getSelectionIndex()).getText();
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
							&& temp.getPropertyname().trim()
									.equalsIgnoreCase(valueToValidate)) {
						lblPropertyError.setText(Messages.RuntimePropertAlreadyExists);
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

  /**
   * 
   * @param selectedTableItems
   * @return
   */
	private String formatDataToTransfer(TableItem[] selectedTableItems) {
		StringBuffer buffer = new StringBuffer();
		for (TableItem tableItem : selectedTableItems) {
			buffer.append(tableItem.getText() + "#");
		}
		return buffer.toString();
	}
/**
 * 
 * @param formatedString
 * @return
 */
	private String[] getformatedData(String formatedString) {
		String[] fieldNameArray = null;
		if (formatedString != null) {
			fieldNameArray = formatedString.split("#");
		}
		return fieldNameArray;
	}
/**
 * 
 * @param valueToValidate
 * @return
 */
	private boolean isPropertyAlreadyExists(String valueToValidate) {
		for (FilterProperties temp : propertyList)
			if (temp.getPropertyname().trim().equalsIgnoreCase(valueToValidate))
				return true;
		return false;
	}
/**
 * 
 * @param sourceTable
 */
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

	@Override
	protected void okPressed() {
		
		if(OSValidator.isMac()){
			
			List<TableViewer> tableViewers = new ArrayList<TableViewer>();
			tableViewers.add(keyValueTableViewer);
			tableViewers.add(targetTableViewer);
			for(TableViewer tableView : tableViewers){
				for(CellEditor cellEditor : tableView.getCellEditors()){
					if(cellEditor !=null){
					cellEditor.getControl().setEnabled(false); //Saves the existing value of CellEditor
					cellEditor.getControl().setEnabled(true);
					}
				}
			}
		}
		if (validate()) {
			fieldNameList.clear();
			hivePartitionColumns.clear();
			
			
			for (FilterProperties temp : propertyList) {
				fieldNameList.add(temp.getPropertyname());
			}
			
			for (HivePartitionFields hivePartitionFieldDialog  : keyValues) {
				
				hivePartitionColumns.add(HiveFieldDialogHelper.INSTANCE.
						arrangeColumndata(new ArrayList<>(hivePartitionFieldDialog.getRowFields()),new ArrayList<String>(fieldNameList)));
				isAnyUpdatePerformed=true;
			}

			if (isAnyUpdatePerformed) {
				propertyDialogButtonBar.enableApplyButton(true);
			}
			

			if(!HiveFieldDialogHelper.INSTANCE.compareAndChangeColor(getTargetTableViewer(),sourceFieldsList)){
				int rc=HiveFieldDialogHelper.INSTANCE.Message_Dialog();
				   if(rc==0){
					   okPressed = true;
					   super.okPressed();
				   }
				   else if(rc==1){
					   return;
				   }
			}
		
			   okPressed = true;
			   super.okPressed();	
			
		} else {
			return;
		}
	}

	@Override
	protected void cancelPressed() {
		if (isAnyUpdatePerformed) {
			int style = SWT.APPLICATION_MODAL | SWT.OK | SWT.CANCEL |SWT.ICON_INFORMATION;
			MessageBox messageBox = new MessageBox(getShell(), style);
			messageBox.setText(Messages.INFORMATION); //$NON-NLS-1$
			messageBox.setMessage(Messages.MessageBeforeClosingWindow);
			if (messageBox.open() == SWT.OK) {
				closeDialog = super.close();
			}
		} else {
			closeDialog = super.close();
		}

	}

	@Override
	public boolean close() {
		if (!okPressed) {
			cancelPressed();
			return closeDialog;
		} else {
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
	
	
	
	
	/**
	 * 
	 * @param keyValueDelButton
	 * @return
	 */
	private SelectionListener addButtonListner(final Button keyValueDelButton) {
       SelectionListener listener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(validate()){
				addNewRow();
				}
				if (keyValues.size() >0) {
					keyValueDelButton.setEnabled(true);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			
		};
		return listener;
	}
	private void addNewRow() {
		if (!lblPropertyError.getVisible() || keyValues.size() < 1) {
			isAnyUpdatePerformed = true;

			HivePartitionFields fieldDialog = new HivePartitionFields();

			List<String> rowFields = new ArrayList<>();

			for (FilterProperties string : propertyList) {
				rowFields.add("");
			}
			fieldDialog.setRowFields(rowFields);
			keyValues.add(fieldDialog);

			keyValueTableViewer.refresh();

			keyValueTableViewer.editElement(keyValueTableViewer.getElementAt(keyValues.size() - 1), 0);
		}
	}
	/**
	 * 
	 * @param keyValueDelButton
	 * @return
	 */
	private SelectionListener deleteButtonListner(final Button keyValueDelButton) {
	       SelectionListener listener = new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					WidgetUtility.setCursorOnDeleteRow(keyValueTableViewer, keyValues);
					isAnyUpdatePerformed=true;
					if(lblPropertyError.getVisible()){
						lblPropertyError.setVisible(false);
					}
					keyValueTableViewer.refresh();
					
					if (keyValues.size() < 1) {
						keyValueDelButton.setEnabled(false);
					} 
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {}
			};
			return listener;
		}
	/**
	 * 
	 * @return
	 */
	private boolean checkTargetFieldsSequence() {

		 boolean result = HiveFieldDialogHelper.INSTANCE.compareAndChangeColor(getTargetTableViewer(),sourceFieldsList);
		 if(!result && keyValueTableViewer.getTable().getColumnCount()==0){
			 btnCheckButton.setEnabled(false);
		 }else{
			 btnCheckButton.setEnabled(true); 
		 }
		 return result;
	}
	
}



