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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.lookupproperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.LookupMapProperty;
import hydrograph.ui.datastructure.property.LookupMappingGrid;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.joinlookupproperty.JoinContentProvider;
import hydrograph.ui.propertywindow.widgets.customwidgets.joinlookupproperty.LookupCellModifier;
import hydrograph.ui.propertywindow.widgets.customwidgets.joinlookupproperty.LookupLabelProvider;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTCellModifier;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTFilterContentProvider;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTFilterLabelProvider;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTSWTWidgets;


public class ELTLookupMapWizard extends Dialog {

	private Label propertyError;
	private TableViewer outputTableViewer;
	private Button okButton;
	private TableViewer viewer1 = null;
	private TableViewer viewer2 = null;
	private TableViewer[] inputTableViewer = new TableViewer[2];

	public static String PROPERTY_NAME = "Source Field";
	public static String PROPERTY_VALUE = "Output Field";
	public static final String OPERATIONAL_INPUT_FIELD = "Field Name";

	private String[] INPUT_COLUMN_NAME = { OPERATIONAL_INPUT_FIELD };
	private String[] COLUMN_NAME = { PROPERTY_NAME, PROPERTY_VALUE };

	private List<LookupMapProperty> joinOutputList;
	private List<FilterProperties> joinInputList1;
	private List<FilterProperties> joinInputList2;
	private List<List<FilterProperties>> joinInputList = new ArrayList<>();
	private ELTSWTWidgets eltswtWidgets = new ELTSWTWidgets();
	private LookupMappingGrid lookupPropertyGrid;
	private static final String INFORMATION = "Information";
	private TableItem[] previousItems;
	private TableItem[] currentItems;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private List<String> sourceFieldList=new ArrayList<>();
	private HashMap<String, List<String>> inputFieldMap=new HashMap<String, List<String>>();
	private Label deleteButton;
	private Label upButton;
	private Label downButton;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param propertyDialogButtonBar 
	 */
	public ELTLookupMapWizard(Shell parentShell,
			LookupMappingGrid lookupPropertyGrid, PropertyDialogButtonBar propertyDialogButtonBar) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL);
		this.lookupPropertyGrid = lookupPropertyGrid;
		this.propertyDialogButtonBar=propertyDialogButtonBar;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("Lookup Mapping");
		container.setLayout(new GridLayout(4, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Composite composite = new Composite(container, SWT.None);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite.heightHint = 574;
		gd_composite.widthHint = 257;
		composite.setLayoutData(gd_composite);

		if (lookupPropertyGrid != null) {
			if (lookupPropertyGrid.getLookupInputProperties() != null
					&& !lookupPropertyGrid.getLookupInputProperties().isEmpty()) {
				joinInputList1 = lookupPropertyGrid.getLookupInputProperties()
						.get(0);
				joinInputList2 = lookupPropertyGrid.getLookupInputProperties()
						.get(1);
			} else {
				joinInputList1 = new ArrayList<>();
				joinInputList2 = new ArrayList<>();
			}
			viewer1 = createComposite(composite, 10, joinInputList1, 0);
			viewer2 = createComposite(composite, 290, joinInputList2, 1);

			if (lookupPropertyGrid.getLookupMapProperties() != null
					&& !lookupPropertyGrid.getLookupMapProperties().isEmpty()) {
				joinOutputList = lookupPropertyGrid.getLookupMapProperties();
			} else {
				joinOutputList = new ArrayList<>();
			}
		}
		if (joinInputList != null) {
			joinInputList.add(joinInputList1);
			joinInputList.add(joinInputList2);
		}

		Composite composite_1 = new Composite(container, SWT.None);
		GridData gd_composite_1 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite_1.heightHint = 600;
		gd_composite_1.widthHint = 400;
		composite_1.setLayoutData(gd_composite_1);

		labelWidget(composite_1, SWT.None, new int[] { 0, 6, 100, 18 },
				"Output Mapping");
		outputTableViewer = eltswtWidgets.createTableViewer(composite_1,
				COLUMN_NAME, new int[] { 0, 30, 398, 538 }, 196,
				new JoinContentProvider(), new LookupLabelProvider());
		outputTableViewer.getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				joinOutputProperty(outputTableViewer, null);
				changeColorOfNonMappedFields();
				if (joinOutputList.size() >= 1) {
					deleteButton.setEnabled(true);
				}
				if (joinOutputList.size() >= 2) {
					upButton.setEnabled(true);
					downButton.setEnabled(true);
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
				validateDuplicatesInOutputField();
				getListOfNonMappedFields(inputFieldMap);
				changeColorOfNonMappedFields();
			}
		});
		eltswtWidgets.createTableColumns(outputTableViewer.getTable(),
				COLUMN_NAME, 196);
		CellEditor[] editors = eltswtWidgets.createCellEditorList(
				outputTableViewer.getTable(), 2);
		editors[0].setValidator(sourceEditorValidator(outputTableViewer,
				Messages.EmptySourceFieldNotification, joinOutputList));
		editors[1].setValidator(outputFieldEditorValidator(outputTableViewer,
				Messages.EmptySourceFieldNotification, joinOutputList));

		outputTableViewer.setColumnProperties(COLUMN_NAME);
		outputTableViewer.setCellModifier(new LookupCellModifier(
				outputTableViewer));
		outputTableViewer.setCellEditors(editors);
		outputTableViewer.setInput(joinOutputList);

		outputTableViewer.getTable().addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (((TableItem) event.item) != null) {
					if (StringUtils.isNotBlank(((TableItem) event.item)
							.getText())) {
						String[] data = (((TableItem) event.item).getText())
								.split(Pattern.quote("."));
						if (data != null && data.length == 2) {
							FilterProperties filter = new FilterProperties();
							filter.setPropertyname(data[1]);
							if (joinInputList1.indexOf(filter) >= 0) {
								inputTableViewer[0].getTable().setSelection(
										joinInputList1.indexOf(filter));
							} else if (joinInputList2.indexOf(filter) >= 0) {
								inputTableViewer[1].getTable().setSelection(
										joinInputList2.indexOf(filter));
							}
						}
					}
				}
			}
		});
		TableViewerEditor.create(outputTableViewer,
				new ColumnViewerEditorActivationStrategy(outputTableViewer),
				ColumnViewerEditor.KEYBOARD_ACTIVATION
						| ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL);

		propertyError = new Label(composite_1, SWT.None);
		propertyError.setBounds(0, 572, 350, 25);
		propertyError.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
		propertyError.setVisible(false);

		Composite composite_2 = new Composite(composite_1, SWT.None);
		composite_2.setBounds(276, 4, 110, 24);
		createLabel(composite_2);

		new Label(container, SWT.NONE);

		populateWidget();

		dropData(outputTableViewer, joinOutputList, true);
	
		populatePreviousItemsOfTable();
		outputTableViewer.getTable().addMouseTrackListener(new MouseTrackListener() {
			
			@Override
			public void mouseHover(MouseEvent e) {
				changeColorOfNonMappedFields();
			}
			
			@Override
			public void mouseExit(MouseEvent e) {
				changeColorOfNonMappedFields();
				
			}
			
			@Override
			public void mouseEnter(MouseEvent e) {
				changeColorOfNonMappedFields();
			}
		});
		inputFieldMap = setMapOfInputFieldsPerPort();
		sourceFieldList=getListOfNonMappedFields(inputFieldMap);
		return container;
	}
	
	public void changeColorOfNonMappedFields() {
		if (outputTableViewer.getTable().getItems() != null) {
			TableItem[] items = outputTableViewer.getTable().getItems();
			for (int i = 0; i < joinOutputList.size(); i++) {
				for (String sourceField : sourceFieldList) {
					if (joinOutputList.get(i).getSource_Field().equalsIgnoreCase(sourceField) &&
							!ParameterUtil.isParameter(sourceField)) {
						items[i].setForeground(0, CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
					}
				}
			}
		}
	}
	
	

	private List<String> getListOfNonMappedFields(HashMap<String, List<String>> inputFieldMap) {
		Iterator iterator = inputFieldMap.entrySet().iterator();
	    while (iterator.hasNext()) {
	        Map.Entry portFieldPair = (Map.Entry)iterator.next();
	        for (LookupMapProperty lookupMapProperty : joinOutputList) {
				String port = "";
				if (lookupMapProperty.getSource_Field().length() >= 3) {
					port = lookupMapProperty.getSource_Field().substring(0, 3);
				}
	        	String source_field = lookupMapProperty.getSource_Field().substring(lookupMapProperty.getSource_Field().lastIndexOf(".") + 1);
				if (portFieldPair.getKey().equals(port)) {
					List<String> value = (ArrayList<String>) portFieldPair.getValue();
					if (!value.isEmpty() && !checkIfSourceFieldExists(value, source_field)) {
						sourceFieldList.add(port + "." + source_field);
					}
				}
				else {
					sourceFieldList.add(source_field);
				}
				if (port.equalsIgnoreCase("")) {
					sourceFieldList.add(source_field);
				}
			}
	    }
	    return sourceFieldList;
	}

	private boolean checkIfSourceFieldExists(List<String> list, String sourceField) {
		for (String field : list) {
			if (field.equalsIgnoreCase(sourceField)) {
				return true;
			}
		}
		return false;
	}

	private HashMap<String, List<String>> setMapOfInputFieldsPerPort() {
		HashMap<String, List<String>> inputFieldMap = new HashMap<String, List<String>>();
		int j = 0;
		for (List<FilterProperties> inputFieldList : joinInputList) {
			List<String> inputFieldListPerPort = new ArrayList<>();
			for (FilterProperties inputField : inputFieldList) {
				for (LookupMapProperty lookupMapProperty : joinOutputList) {
					char charactor = ' ';
					if (lookupMapProperty.getSource_Field().length() >= 3) {
						charactor = lookupMapProperty.getSource_Field().charAt(2);
					}
					if (Character.toString(charactor).equalsIgnoreCase(Integer.toString(j))) {
						if (!inputFieldListPerPort.contains(inputField.getPropertyname())) {
							inputFieldListPerPort.add(inputField.getPropertyname());
						}
					}
				}
			}
			inputFieldMap.put("in" + j, inputFieldListPerPort);
			j++;
		}
		return inputFieldMap;
	}

	private void populatePreviousItemsOfTable() {
		if (outputTableViewer.getTable().getItems() != null) {
			previousItems = outputTableViewer.getTable().getItems();
		}
	}
	private TableViewer createComposite(Composite parent, int y,
			final List<FilterProperties> joinInputList,
			final int tableViewerIndex) {
		Composite comGrid = new Composite(parent, SWT.BORDER);
		comGrid.setLayoutData(new RowData(267, 136));
		comGrid.setLayout(new RowLayout(SWT.VERTICAL));
		comGrid.setBounds(15, y, 233, 268);

		labelWidget(comGrid, SWT.LEFT, new int[] { 0, 5, 90, 20 },
				"Input Index : in" + tableViewerIndex);

		inputTableViewer[tableViewerIndex] = eltswtWidgets.createTableViewer(
				comGrid, INPUT_COLUMN_NAME, new int[] { 0, 30, 229, 232 }, 224,
				new ELTFilterContentProvider(), new ELTFilterLabelProvider());
		
		inputTableViewer[tableViewerIndex].getTable().addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseHover(MouseEvent e) {
				changeColorOfNonMappedFields();

			}

			@Override
			public void mouseExit(MouseEvent e) {
				changeColorOfNonMappedFields();

			}

			@Override
			public void mouseEnter(MouseEvent e) {
				changeColorOfNonMappedFields();

			}
		});
		eltswtWidgets.createTableColumns(
				inputTableViewer[tableViewerIndex].getTable(),
				INPUT_COLUMN_NAME, 224);
		CellEditor[] editors = eltswtWidgets.createCellEditorList(
				inputTableViewer[tableViewerIndex].getTable(), 1);
		editors[0]
				.setValidator(valueEditorValidation(joinInputList,
						Messages.EMPTYFIELDMESSAGE,
						inputTableViewer[tableViewerIndex]));
		inputTableViewer[tableViewerIndex].setCellModifier(new ELTCellModifier(
				inputTableViewer[tableViewerIndex]));
		inputTableViewer[tableViewerIndex]
				.setColumnProperties(INPUT_COLUMN_NAME);
		inputTableViewer[tableViewerIndex].setCellEditors(editors);
		inputTableViewer[tableViewerIndex].setInput(joinInputList);

		eltswtWidgets
				.applyDragFromTableViewer(
						inputTableViewer[tableViewerIndex].getTable(),
						tableViewerIndex);
		return inputTableViewer[tableViewerIndex];
	}

	
	private void createLabel(Composite parent) {
		Label addButton = eltswtWidgets.labelWidget(parent, SWT.CENTER | SWT.PUSH, new int[] { 0, 0, 25, 20 }, "", ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
		addButton.setToolTipText(Messages.ADD_SCHEMA_TOOLTIP);

		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				joinOutputProperty(outputTableViewer, null);
				if (joinOutputList.size() >= 1) {
					deleteButton.setEnabled(true);
				} 
				if (joinOutputList.size() >= 2) {
					upButton.setEnabled(true);
					downButton.setEnabled(true);
				}
			}
		});

		deleteButton = eltswtWidgets.labelWidget(parent, SWT.CENTER, new int[] { 25, 0, 25, 20 }, "", ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		deleteButton.setToolTipText(Messages.DELETE_SCHEMA_TOOLTIP);
		deleteButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				int index = 0;
				IStructuredSelection selection = (IStructuredSelection) outputTableViewer.getSelection();
				for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
					Object selectedObject = iterator.next();
					index = joinOutputList.indexOf(selectedObject);
					outputTableViewer.remove(selectedObject);
					joinOutputList.remove(selectedObject);
				}
				outputTableViewer.refresh();
				if (index != 0)
					outputTableViewer.editElement(outputTableViewer.getElementAt(index - 1), 0);
				if (joinOutputList.size() < 1) {
					deleteButton.setEnabled(false);
				} 
				if (joinOutputList.size() <= 1) {
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				} 
			}

		});

		upButton = eltswtWidgets.labelWidget(parent, SWT.CENTER, new int[] { 50, 0, 25, 20 }, "", ImagePathConstant.UP_ICON.getImageFromRegistry());
		upButton.setToolTipText(Messages.MOVE_SCHEMA_UP_TOOLTIP);
		upButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				int index2 = 0;
				int index1 = outputTableViewer.getTable().getSelectionIndex();

				if (index1 > 0) {
					String text = outputTableViewer.getTable().getItem(index1).getText(0);
					String text1 = outputTableViewer.getTable().getItem(index1).getText(1);
					index2 = index1 - 1;
					String data = outputTableViewer.getTable().getItem(index2).getText(0);
					String data2 = outputTableViewer.getTable().getItem(index2).getText(1);

					LookupMapProperty property = new LookupMapProperty();
					property.setSource_Field(data);
					property.setOutput_Field(data2);
					joinOutputList.set(index1, property);

					property = new LookupMapProperty();
					property.setSource_Field(text);
					property.setOutput_Field(text1);
					joinOutputList.set(index2, property);
					outputTableViewer.refresh();
					outputTableViewer.getTable().setSelection(index1 - 1);
				}
			}
        });

		downButton = eltswtWidgets.labelWidget(parent, SWT.CENTER, new int[] { 74, 0, 25, 20 }, "", ImagePathConstant.DOWN_ICON.getImageFromRegistry());
		downButton.setToolTipText(Messages.MOVE_SCHEMA_DOWN_TOOLTIP);
		downButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				int index1 = outputTableViewer.getTable().getSelectionIndex();
				int index2 = 0;

				if (index1 < joinOutputList.size() - 1) {
					String text = outputTableViewer.getTable().getItem(index1).getText(0);
					String text1 = outputTableViewer.getTable().getItem(index1).getText(1);

					index2 = index1 + 1;

					String data = outputTableViewer.getTable().getItem(index2).getText(0);
					String data1 = outputTableViewer.getTable().getItem(index2).getText(1);

					LookupMapProperty p = new LookupMapProperty();
					p.setSource_Field(data);
					p.setOutput_Field(data1);
					joinOutputList.set(index1, p);

					p = new LookupMapProperty();
					p.setSource_Field(text);
					p.setOutput_Field(text1);
					joinOutputList.set(index2, p);
					outputTableViewer.refresh();
					outputTableViewer.getTable().setSelection(index1 + 1);

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
		okButton = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	private boolean validation(String outputFieldValue, String sourceFieldValue) {
		int propertycount = 0;
		int propertyValuecount = 0;
		for (LookupMapProperty join : joinOutputList) {
			if (join.getSource_Field().trim().isEmpty()) {
				outputTableViewer.getTable().setSelection(propertycount);
				propertyError.setVisible(true);
				propertyError.setText(Messages.EmptySourceFieldNotification);
				okButton.setEnabled(false);
				return false;
			} else if (join.getOutput_Field().trim().isEmpty()) {
				outputTableViewer.getTable().setSelection(propertyValuecount);
				propertyError.setVisible(true);
				propertyError.setText(Messages.EmptyOutputFieldNotification);
				return false;
			} else {
				propertyError.setVisible(false);
				okButton.setEnabled(true);
			}

			propertycount++;
			propertyValuecount++;
		}
		for (LookupMapProperty join : joinOutputList) {
			if (join.getSource_Field() != null && join.getSource_Field().equalsIgnoreCase(sourceFieldValue)) {
				return false;
			}
		}
		return true;
	}

	private boolean inputTabvalidation() {
		int propertycount = 0;
		for (FilterProperties join : joinInputList.get(0)) {
			if (!join.getPropertyname().trim().isEmpty()) {
				Matcher match = Pattern.compile(Constants.REGEX).matcher(
						join.getPropertyname());
				if (!match.matches()) {
					outputTableViewer.getTable().setSelection(propertycount);
					propertyError.setVisible(true);
					propertyError
							.setText(Messages.PROPERTY_NAME_ALLOWED_CHARACTERS);
					okButton.setEnabled(false);
					return false;
				}
			} else {
				outputTableViewer.getTable().setSelection(propertycount);
				propertyError.setVisible(true);
				propertyError.setText(Messages.EmptySourceFieldNotification);
				okButton.setEnabled(false);
				return false;
			}

			propertycount++;
		}
		return true;
	}

	// Creates Value Validator for table's cells
	private ICellEditorValidator valueEditorValidation(
			final List<FilterProperties> joinInputList,
			final String ErrorMessage, final TableViewer viewer) {
		ICellEditorValidator propertyValidator = new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				String selectedField = viewer.getTable()
						.getItem(viewer.getTable().getSelectionIndex())
						.getText();
				String valueToValidate = String.valueOf(value).trim();
				Matcher match = Pattern.compile(Constants.REGEX).matcher(
						valueToValidate);
				if (valueToValidate.isEmpty()) {
					propertyError.setText(ErrorMessage);
					propertyError.setVisible(true);
					return "ERROR"; //$NON-NLS-1$
				} else if (!match.matches()) {
					propertyError.setVisible(true);
					propertyError
							.setText(Messages.PROPERTY_NAME_ALLOWED_CHARACTERS);
					okButton.setEnabled(false);
					return "ERROR";
				} else {
					propertyError.setVisible(false);
					okButton.setEnabled(true);
				}

				for (FilterProperties property : joinInputList) {
					if (!selectedField.equalsIgnoreCase(valueToValidate)
							&& property.getPropertyname().trim()
									.equalsIgnoreCase(valueToValidate)) {
						propertyError.setVisible(true);
						propertyError
								.setText(Messages.FieldNameAlreadyExists);
						okButton.setEnabled(false);
						return "ERROR";
					} else {
						propertyError.setVisible(false);
						okButton.setEnabled(true);
					}
				}
				validateDuplicatesInOutputField();
				return null;
			}
		};
		return propertyValidator;
	}

	public void populateWidget() {
		if (lookupPropertyGrid != null) {
			inputTableViewer[0].refresh();
			inputTableViewer[1].refresh();
			outputTableViewer.refresh();
		}
		if (joinOutputList.size() != 0) {
			deleteButton.setEnabled(true);
		}
		else
		{
			deleteButton.setEnabled(false);
		}
		if (joinOutputList.size() >= 2) {
			upButton.setEnabled(true);
			downButton.setEnabled(true);
		}
		else
		{
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
	}

	public void getLookupPropertyGrid() {
		lookupPropertyGrid.setLookupInputProperties(joinInputList);
		lookupPropertyGrid.setLookupMapProperties(joinOutputList);
	}

	@Override
	protected void okPressed() {
		
		if(OSValidator.isMac()){
			List<TableViewer> tableViewers = new ArrayList<TableViewer>();
			tableViewers.add(outputTableViewer);
			tableViewers.addAll(java.util.Arrays.asList(inputTableViewer));
			
			for(TableViewer views : tableViewers){
				if(views !=null){
				for(CellEditor cellEditor : views.getCellEditors()){
					if(cellEditor !=null){
					cellEditor.getControl().setEnabled(false); //Saves the existing value of CellEditor
					cellEditor.getControl().setEnabled(true);
					}
				}
				}
			}
		}
		populateCurrentItemsOfTable();
		if (previousItems.length == 0 && currentItems.length != 0) {
			propertyDialogButtonBar.enableApplyButton(true);
		} else if ((currentItems.length != 0 && previousItems.length != 0)) {
			if (!Arrays.equals(currentItems, previousItems))
				propertyDialogButtonBar.enableApplyButton(true);
		}
		getLookupPropertyGrid();
		super.close();
	}

	private Boolean hasOutputMappingInTableChanged() {
		boolean returnValue = false;
		populateCurrentItemsOfTable();
		if (currentItems.length == 0 && previousItems.length == 0) {
			super.close();
		} else {
			if (!Arrays.equals(currentItems, previousItems)) {
				int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO | SWT.ICON_INFORMATION;
				MessageBox messageBox = new MessageBox(new Shell(), style);
				messageBox.setText(INFORMATION);
				messageBox.setMessage(Messages.MessageBeforeClosingWindow);
				if (messageBox.open() == SWT.YES) {
					joinOutputList.clear();
					LookupMapProperty[] lookupMapPropertyObjects = new LookupMapProperty[previousItems.length];
					for (int i = 0; i < previousItems.length; i++) {
						if(!previousItems[i].isDisposed())
						{
						lookupMapPropertyObjects[i] = (LookupMapProperty) previousItems[i].getData();
						joinOutputList.add(lookupMapPropertyObjects[i]);
						}
					}
					getLookupPropertyGrid();
					returnValue = super.close();
				}
			} else {
				returnValue = super.close();
			}
		}
		return returnValue;
	}

	@Override
	protected void cancelPressed() {
		hasOutputMappingInTableChanged();
	}

	@Override
	public boolean close() {
		return hasOutputMappingInTableChanged();
	}

	private void populateCurrentItemsOfTable() {
		if (outputTableViewer.getTable().getItems() != null) {
			currentItems = outputTableViewer.getTable().getItems();
		}
	}

	public Button buttonWidget(Composite parent, int style, int[] bounds,
			String value, Image image) {
		Button button = new Button(parent, style);
		button.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
		button.setText(value);
		button.setImage(image);

		return button;
	}

	public Label labelWidget(Composite parent, int style, int[] bounds,
			String value) {
		Label label = new Label(parent, style);
		label.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
		label.setText(value);
		// label.setImage(image);

		return label;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(700, 719);
	}

	// Creates CellNAme Validator for table's cells
	private ICellEditorValidator sourceEditorValidator(
			final TableViewer viewer, final String errorMessage,
			final List<LookupMapProperty> propertyList) {
		ICellEditorValidator propertyValidator = new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				String currentSelectedFld = viewer.getTable()
						.getItem(viewer.getTable().getSelectionIndex())
						.getText();
				String valueToValidate = String.valueOf(value).trim();
				if (StringUtils.isEmpty(valueToValidate)) {
					propertyError.setText(errorMessage);
					propertyError.setVisible(true);
					okButton.setEnabled(false);
					return "ERROR";
				} else {
					okButton.setEnabled(true);
				}
				for (LookupMapProperty temp : propertyList) {
					if (!currentSelectedFld.equalsIgnoreCase(valueToValidate)
							&& temp.getSource_Field().equalsIgnoreCase(
									valueToValidate)) {
						propertyError
								.setText(Messages.SourceFieldAlreadyExists);
						propertyError.setVisible(true);
						okButton.setEnabled(false);
						return "ERROR";
					} else {
						propertyError.setVisible(false);
						okButton.setEnabled(true);
						validateDuplicatesInOutputField();
					}
				}
				return null;
			}
		};
		return propertyValidator;
	}

	// Creates CellValue Validator for table's cells
	private ICellEditorValidator outputFieldEditorValidator(
			final TableViewer viewer, final String errorMessage,
			final List<LookupMapProperty> propertyList) {
		ICellEditorValidator propertyValidator = new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				String currentSelectedFld = viewer.getTable()
						.getItem(viewer.getTable().getSelectionIndex())
						.getText(1);
				String valueToValidate = String.valueOf(value).trim();
				if (StringUtils.isEmpty(valueToValidate)) {
					propertyError.setText(errorMessage);
					propertyError.setVisible(true);
					okButton.setEnabled(false);
					return "ERROR";
				} else {
					okButton.setEnabled(true);
				}
				for (LookupMapProperty temp : propertyList) {
					if (!currentSelectedFld.equalsIgnoreCase(valueToValidate)
							&& temp.getOutput_Field().equalsIgnoreCase(
									valueToValidate)) {
						propertyError.setText(Messages.OutputFieldAlreadyExists);
						propertyError.setVisible(true);
						okButton.setEnabled(false);
						validateDuplicatesInOutputField();
						return "ERROR";
					} else {
						propertyError.setVisible(false);
						okButton.setEnabled(true);
					}
				}
				validateDuplicatesInOutputField();
				return null;
			}
		};
		validateDuplicatesInOutputField();
		return propertyValidator;
	}

	private void joinOutputProperty(TableViewer viewer, String sourceFieldValue) {

		String outputFieldValue = null;
		if (sourceFieldValue == null) {
			sourceFieldValue = "";
			outputFieldValue = "";
		} else {
			outputFieldValue = sourceFieldValue.split("\\.")[1];
		}
		LookupMapProperty property = new LookupMapProperty();

		if (joinOutputList.size() != 0) {
			if (!validation(outputFieldValue, sourceFieldValue))
				return;
			property.setSource_Field(sourceFieldValue);
			property.setOutput_Field(outputFieldValue);
			joinOutputList.add(property);
			viewer.refresh();
			viewer.editElement(property,
					0);
		} else {
			property.setSource_Field(sourceFieldValue);
			property.setOutput_Field(outputFieldValue);
			joinOutputList.add(property);
			viewer.refresh();
			viewer.editElement(property, 0);
		}
	}

	private void validateDuplicatesInOutputField() {
		boolean duplicateFound = false;
		TableItem[] items = outputTableViewer.getTable().getItems();
		LookupMapProperty[] objectsInGui = new LookupMapProperty[items.length];
		for (int i = 0; i < items.length; i++) {
			objectsInGui[i] = (LookupMapProperty) items[i].getData();
			for (int j = i + 1; j < items.length; j++) {
				if (((LookupMapProperty) items[i].getData()).getOutput_Field().equalsIgnoreCase(
						((LookupMapProperty) items[j].getData()).getOutput_Field())) {
					duplicateFound = true;
					break;
				}
			}
			if (duplicateFound)
				break;

		}
		if (duplicateFound) {
			okButton.setEnabled(false);
			propertyError.setText(Messages.OutputFieldAlreadyExists);
			propertyError.setVisible(true);
		} else {
			if (okButton != null && propertyError != null) {
				okButton.setEnabled(true);
				propertyError.setVisible(false);
			}
		}
	}

   public void dropData(final TableViewer tableViewer,
			final List<LookupMapProperty> listOfFields,
			final boolean isSingleColumn) {
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
		DropTarget target = new DropTarget(tableViewer.getTable(), operations);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
			}

			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}
				if (isSingleColumn) {
					String[] dropData = ((String) event.data).split(Pattern.quote("#"));
					for (String data : dropData) {
						joinOutputProperty(tableViewer, data);
					}
					if (joinOutputList.size() >= 1) {
						deleteButton.setEnabled(true);
					}					
					if (joinOutputList.size() >= 2) {
						upButton.setEnabled(true);
						downButton.setEnabled(true);
					}
					validateDuplicatesInOutputField();
				}
			}
		});
	}

}
