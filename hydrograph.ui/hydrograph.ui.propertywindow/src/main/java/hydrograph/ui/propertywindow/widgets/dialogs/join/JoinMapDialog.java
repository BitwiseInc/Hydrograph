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
package hydrograph.ui.propertywindow.widgets.dialogs.join;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.JoinConfigProperty;
import hydrograph.ui.datastructure.property.JoinMappingGrid;
import hydrograph.ui.datastructure.property.LookupMapProperty;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.dialogs.join.support.JoinMappingEditingSupport;
import hydrograph.ui.propertywindow.widgets.dialogs.join.utils.JoinMapDialogConstants;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * 
 * Join mapping dialog
 * 
 * @author Bitwise
 *
 */
public class JoinMapDialog extends Dialog {
	private Component component;
	private JoinMappingGrid joinMappingGrid;
	private List<List<FilterProperties>> inputPorts;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private int inputPortValue;
	private TableViewer mappingTableViewer;
	private List<LookupMapProperty> mappingTableItemList;
	private Map<String, Button> radioButtonMap;
	private JoinMappingGrid oldJoinMappingGrid;
	private JoinMappingGrid newJoinMappingGrid;
	private List<String> allInputFields;
	private Button btnPull;
	private Button btnDown;
	private Button btnUp;
	private Button btnDelete;
	private Button btnAdd;
	
	private static final String PORT_ID_KEY = "PORT_ID";
	private static final String NO_COPY="None";
	private static final String INPUT_PORT_ID_PREFIX="in";
	private static final String COPY_RADIO_BUTTON_TEXT_PREFIX="Copy of in";
	private static final String EXPAND_ITEM_TEXT_PREFIX="Port in";
	
	private static final String FIELD_TOOLTIP_MESSAGE_NO_SUCH_INPUT_FIELD="No such input field";
	private static final String FIELD_TOOLTIP_MESSAGE_FIELD_CANT_BE_EMPTY="Field can not be empty";
	private static final String FIELD_TOOLTIP_MESSAGE_DUPLICATE_FIELDS="Duplicate field";
	private static final String MAPPING_TABLE_ITEM_DELIMILATOR="#";
	
	
	
	private static final String PULL_TOOLTIP = "Pull schema";
	
	private static final String INPUT_TABLE_COLUMN_TEXT="Input Fields";
	private static final String DIALOG_TITLE="Join Mapping Dialog";
	private boolean ctrlKeyPressed = false;
	private Table table;
	private JoinMappingEditingSupport outputEditingSupport;
	private JoinMappingEditingSupport inputEditingSupport;
	
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @wbp.parser.constructor
	 */@Deprecated
	public JoinMapDialog(Shell parentShell) {
		super(parentShell);
	
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL
				| SWT.RESIZE);
	}

	public JoinMapDialog(Shell parentShell, Component component, JoinMappingGrid joinPropertyGrid,
			PropertyDialogButtonBar propertyDialogButtonBar) {
		super(parentShell);
		
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL
				| SWT.RESIZE);
		this.joinMappingGrid = joinPropertyGrid;
		this.component=component;
		radioButtonMap = new LinkedHashMap<>();
		inputPorts = new ArrayList<>();

		this.propertyDialogButtonBar = propertyDialogButtonBar;
		allInputFields = new LinkedList<>();		
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
		container.getShell().setText(DIALOG_TITLE);
		
		SashForm composite = new SashForm(container, SWT.SMOOTH);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		createInputFieldExpandBarSection(composite);

		creatFieldMappingSection(composite);

		createCopyInputToOutputFieldSection(composite);
		composite.setWeights(new int[] {215, 559, 116});

		populateJoinMapDialog();
		
		return container;
	}


	private void createCopyInputToOutputFieldSection(Composite composite) {
		Composite composite_3 = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_3 = new GridLayout(1, false);
		gl_composite_3.verticalSpacing = 0;
		gl_composite_3.marginWidth = 0;
		gl_composite_3.marginHeight = 0;
		gl_composite_3.horizontalSpacing = 0;
		composite_3.setLayout(gl_composite_3);
		GridData gd_composite_3 = new GridData(SWT.FILL, SWT.FILL, false, true,
				1, 1);
		gd_composite_3.widthHint = 121;
		composite_3.setLayoutData(gd_composite_3);
		composite_3.setBounds(0, 0, 64, 64);

		Composite composite_8 = new Composite(composite_3, SWT.BORDER);
		GridLayout gl_composite_8 = new GridLayout(1, false);
		gl_composite_8.verticalSpacing = 0;
		gl_composite_8.marginWidth = 0;
		gl_composite_8.horizontalSpacing = 0;
		gl_composite_8.marginHeight = 0;
		composite_8.setLayout(gl_composite_8);
		composite_8.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		ScrolledComposite scrolledComposite_2 = new ScrolledComposite(
				composite_8, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true, 1, 1));
		scrolledComposite_2.setExpandHorizontal(true);
		scrolledComposite_2.setExpandVertical(true);
		scrolledComposite_2.setAlwaysShowScrollBars(false);
		attachMouseScrollButtonListener(scrolledComposite_2);
		

		Composite composite_12 = new Composite(scrolledComposite_2, SWT.NONE);
		composite_12.setLayout(new GridLayout(1, false));

		Button btnRadioButton_None = new Button(composite_12, SWT.RADIO);
		btnRadioButton_None.setText(NO_COPY);

		btnRadioButton_None.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!mappingTableViewer.getTable().isEnabled()) {
					joinMappingGrid.setButtonText(((Button) e.widget).getText());
					joinMappingGrid.setIsSelected(false);
					mappingTableItemList.clear();
					mappingTableViewer.refresh();
					mappingTableViewer.getTable().setEnabled(true);
					btnAdd.setEnabled(true);
					btnPull.setEnabled(true);
				}
			}
		});
		radioButtonMap.put(btnRadioButton_None.getText(), btnRadioButton_None);

		for (int i = 0; i < inputPortValue; i++) {
			Button btnRadioButton = new Button(composite_12, SWT.RADIO);
			btnRadioButton.setText(COPY_RADIO_BUTTON_TEXT_PREFIX + i);
			btnRadioButton.setData(PORT_ID_KEY, i);
			radioButtonMap.put(btnRadioButton.getText(), btnRadioButton);
			btnRadioButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					enableMappingTableButtonPanel(false);
					joinMappingGrid.setButtonText(((Button) e.widget).getText());
					joinMappingGrid.setIsSelected(true);
					mappingTableViewer.getTable().setEnabled(false);

					List<FilterProperties> inputFieldList = inputPorts
							.get((int) ((Button) e.widget).getData(PORT_ID_KEY));

					String inputPortID = INPUT_PORT_ID_PREFIX
							+ ((Button) e.widget).getData(PORT_ID_KEY);

					if (inputFieldList != null) {
						copyFieldsWhenCopyOfIsSelected(inputFieldList, inputPortID);
						mappingTableViewer.refresh();

					}
				}
			});
		}

		scrolledComposite_2.setContent(composite_12);
		scrolledComposite_2.setMinSize(composite_12.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
	}
	
	private void attachMouseScrollButtonListener(final ScrolledComposite scrolledComposite){
		scrolledComposite.addListener(SWT.MouseWheel, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int wheelCount = event.count;
				wheelCount = (int) Math.ceil(wheelCount / 3.0f);
				while (wheelCount < 0) {
					scrolledComposite.getVerticalBar().setIncrement(4);
					wheelCount++;
				}

				while (wheelCount > 0) {
					scrolledComposite.getVerticalBar().setIncrement(-4);
					wheelCount--;
				}
			}
		});
	}

	private void copyFieldsWhenCopyOfIsSelected(List<FilterProperties> inputFieldList, String inputPortID) {
		mappingTableItemList.clear();
		for (FilterProperties properties : inputFieldList) {
			LookupMapProperty property = new LookupMapProperty();
			property.setSource_Field(inputPortID + "." + properties.getPropertyname());
			property.setOutput_Field(properties.getPropertyname());
			mappingTableItemList.add(property);
		}
	}
	
	private void creatFieldMappingSection(Composite composite) {

		Composite composite_2 = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(1, false);
		gl_composite_2.verticalSpacing = 0;
		gl_composite_2.marginWidth = 0;
		gl_composite_2.marginHeight = 0;
		gl_composite_2.horizontalSpacing = 0;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		composite_2.setBounds(0, 0, 64, 64);

		createButtonSectionInFieldMappingSection(composite_2);

		createMappingTableInFieldMappingSection(composite_2);
	}

	private void createMappingTableInFieldMappingSection(Composite composite_2) {
		Composite composite_5 = new Composite(composite_2, SWT.NONE);
		GridLayout gl_composite_5 = new GridLayout(1, false);
		gl_composite_5.verticalSpacing = 0;
		gl_composite_5.marginWidth = 0;
		gl_composite_5.marginHeight = 0;
		gl_composite_5.horizontalSpacing = 0;
		composite_5.setLayout(gl_composite_5);
		composite_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		ScrolledComposite scrolledComposite = new ScrolledComposite(
				composite_5, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite_6 = new Composite(scrolledComposite, SWT.NONE);
		GridLayout gl_composite_6 = new GridLayout(1, false);
		gl_composite_6.verticalSpacing = 0;
		gl_composite_6.marginWidth = 0;
		gl_composite_6.marginHeight = 0;
		gl_composite_6.horizontalSpacing = 0;
		composite_6.setLayout(gl_composite_6);

		table = createMappingTable(composite_6);
		attachShortcutListner();

		addTabFunctionalityInMappingTable();
		
		createInputFieldColumnInMappingTable();
		createOutputFieldColumnInMappingTable();

		setTableLayoutToMappingTable(table);

		mappingTableViewer.setInput(mappingTableItemList);

		attachDropFunctionalityToMappingTable();

		scrolledComposite.setContent(composite_6);
		scrolledComposite.setMinSize(composite_6.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));

	}

	private void setTableLayoutToMappingTable(Table table) {
		TableColumnLayout layout = new TableColumnLayout();
		mappingTableViewer.getControl().getParent().setLayout(layout);

		for (int columnIndex = 0, n = table.getColumnCount(); columnIndex < n; columnIndex++) {
			table.getColumn(columnIndex).pack();
		}

		for (int i = 0; i < mappingTableViewer.getTable().getColumnCount(); i++) {
			layout.setColumnData(mappingTableViewer.getTable().getColumn(i),
					new ColumnWeightData(1));
		}
	}

	private void addTabFunctionalityInMappingTable() {
		TableViewerEditor.create(mappingTableViewer,
				new ColumnViewerEditorActivationStrategy(mappingTableViewer),
				ColumnViewerEditor.KEYBOARD_ACTIVATION
						| ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL);
	}

	private Table createMappingTable(Composite composite_6) {
		mappingTableViewer = new TableViewer(composite_6, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		Table table = mappingTableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.widthHint = 374;
		table.setLayoutData(gd_table);
		mappingTableViewer.setContentProvider(new ArrayContentProvider());
		ColumnViewerToolTipSupport.enableFor(mappingTableViewer);
		return table;
	}

	private void attachDropFunctionalityToMappingTable() {
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
		DropTarget target = new DropTarget(mappingTableViewer.getTable(),
				operations);
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

				String[] dropData = ((String) event.data).split(Pattern
						.quote(MAPPING_TABLE_ITEM_DELIMILATOR));
				List<String> tempOutputFieldList = getOutputFieldList();
				
				if(getDuplicateDropAllowed()){
					for (String data : dropData) {
						dropItem(data);
					}
				}else{
					for (String data : dropData) {
							dropItem(data);
						//Currently, we allowed duplicate values
						/*if(!tempOutputFieldList.contains(StringUtils.lowerCase(data.split("\\.")[1]))){
						}
						else{
							WidgetUtility.errorMessage(Messages.MAPPING_WINDOW_DUPLICATE_FIELD);
							break;
						}*/
					}
				}
				refreshButtonStatus();
			}
		});
	}

	private void dropItem(String data){
		LookupMapProperty mappingTableItem = new LookupMapProperty();
		mappingTableItem.setSource_Field(data);
		mappingTableItem.setOutput_Field(data.split("\\.")[1]);
		mappingTableItemList.add(mappingTableItem);
		mappingTableViewer.refresh();
	}
	
	private List<String> getOutputFieldList(){
		List<String> outputFieldList = new LinkedList<>();
		for(LookupMapProperty lookupMapProperty : mappingTableItemList){
			outputFieldList.add(StringUtils.lowerCase(lookupMapProperty.getOutput_Field()));
		}
		return outputFieldList;
	}

	private boolean getDuplicateDropAllowed(){
		List<JoinConfigProperty> configObject = (List) component.getProperties().get("join_config");
			if(configObject == null || configObject.size() == 0){
				return false;
			}
			else{
				for(int i = 0 ; i<configObject.size(); i++){
					if(configObject.get(i).getRecordRequired() == 0){
						return false;
					}
				}
			}
			return true;
		}
	
	private void createOutputFieldColumnInMappingTable() {
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				mappingTableViewer, SWT.NONE);
		TableColumn tblclmnPropertyValue = tableViewerColumn_1.getColumn();
		tblclmnPropertyValue.setWidth(148);
		tblclmnPropertyValue.setText(JoinMapDialogConstants.OUTPUT_FIELD);
		outputEditingSupport = new JoinMappingEditingSupport(mappingTableViewer, JoinMapDialogConstants.OUTPUT_FIELD);
		WidgetUtility.addVerifyListnerToOutputEditingSupport(outputEditingSupport);	
		tableViewerColumn_1.setEditingSupport(outputEditingSupport);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			String tooltipText;

			@Override
			public Image getImage(Object element) {
				Image image = ImagePathConstant.DELETE_ICON.getImageFromRegistry();

				LookupMapProperty lookupMapProperty = (LookupMapProperty) element;

				if (StringUtils.isBlank(lookupMapProperty.getOutput_Field()))
					return image;
				else
					return super.getImage(element);
			}
			
			private List<String> getOutputFieldList(){
				List<String> outputFieldList = new LinkedList<>();
				for(LookupMapProperty lookupMapProperty : mappingTableItemList){
					outputFieldList.add(lookupMapProperty.getOutput_Field());
				}
				return outputFieldList;
			}
			
			@Override
			public String getToolTipText(Object element) {
				tooltipText = null;
				
				int occurrences = Collections.frequency(getOutputFieldList(),
						((LookupMapProperty)element).getOutput_Field());
				if (occurrences > 1) {
					tooltipText = FIELD_TOOLTIP_MESSAGE_DUPLICATE_FIELDS;
				}

				LookupMapProperty lookupMapProperty = (LookupMapProperty) element;
				if (StringUtils.isBlank(lookupMapProperty.getSource_Field()))
					tooltipText = FIELD_TOOLTIP_MESSAGE_FIELD_CANT_BE_EMPTY;

				return tooltipText;
			}

			@Override
			public Color getForeground(Object element) {				
				int occurrences = Collections.frequency(getOutputFieldList(),
						((LookupMapProperty)element).getOutput_Field());
				if (occurrences > 1) {
					return CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0);
				} else {
					return super.getForeground(element);
				}

			}

			@Override
			public Color getBackground(Object element) {
				LookupMapProperty lookupMapProperty = (LookupMapProperty) element;

				if (StringUtils.isBlank(lookupMapProperty.getOutput_Field()))
					return CustomColorRegistry.INSTANCE.getColorFromRegistry( 0xFF, 0xDD, 0xDD);
				else
					return super.getBackground(element);
			}

			@Override
			public String getText(Object element) {
				LookupMapProperty lookupMapProperty = (LookupMapProperty) element;
				
				if(ParameterUtil.isParameter(lookupMapProperty.getOutput_Field()))
					lookupMapProperty.setSource_Field(lookupMapProperty.getOutput_Field());
				
				return lookupMapProperty.getOutput_Field();
			}
		});
	}

	

	private void createInputFieldColumnInMappingTable() {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				mappingTableViewer, SWT.NONE);
		TableColumn tblclmnPropertyName = tableViewerColumn.getColumn();
		tblclmnPropertyName.setWidth(169);
		tblclmnPropertyName.setText(JoinMapDialogConstants.INPUT_FIELD);
		inputEditingSupport = new JoinMappingEditingSupport(mappingTableViewer, JoinMapDialogConstants.INPUT_FIELD);
		tableViewerColumn.setEditingSupport(inputEditingSupport);

		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			String tooltipText;

			@Override
			public Image getImage(Object element) {
				Image image = ImagePathConstant.DELETE_ICON.getImageFromRegistry();

				LookupMapProperty lookupMapProperty = (LookupMapProperty) element;

				if (StringUtils.isBlank(lookupMapProperty.getSource_Field()))
					return image;
				else
					return super.getImage(element);
			}
			
			@Override
			public String getToolTipText(Object element) {
				tooltipText = null;
				LookupMapProperty lookupMapProperty = (LookupMapProperty) element;
				if (!allInputFields.contains(lookupMapProperty
						.getSource_Field()) && !ParameterUtil.isParameter(lookupMapProperty.getSource_Field())) {
					tooltipText = FIELD_TOOLTIP_MESSAGE_NO_SUCH_INPUT_FIELD;
				}

				if (StringUtils.isBlank(lookupMapProperty.getSource_Field()))
					tooltipText = FIELD_TOOLTIP_MESSAGE_FIELD_CANT_BE_EMPTY;
				
				return tooltipText;
			}

			@Override
			public Color getForeground(Object element) {
				LookupMapProperty lookupMapProperty = (LookupMapProperty) element;
				if (!allInputFields.contains(lookupMapProperty
						.getSource_Field()) && !ParameterUtil.isParameter(lookupMapProperty.getSource_Field())) {
					return CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0);
				} else {
					return super.getForeground(element);
				}

			}

			@Override
			public Color getBackground(Object element) {
				LookupMapProperty lookupMapProperty = (LookupMapProperty) element;

				if (StringUtils.isBlank(lookupMapProperty.getSource_Field()))
					return CustomColorRegistry.INSTANCE.getColorFromRegistry( 0xFF, 0xDD, 0xDD);
				else
					return super.getBackground(element);
			}

			@Override
			public String getText(Object element) {
				LookupMapProperty lookupMapProperty = (LookupMapProperty) element;
				if(ParameterUtil.isParameter(lookupMapProperty.getSource_Field()))
					lookupMapProperty.setOutput_Field(lookupMapProperty.getSource_Field());
				
				return lookupMapProperty.getSource_Field();
			}

		});
	}

	private void createButtonSectionInFieldMappingSection(Composite composite) {

		Composite composite_4 = new Composite(composite, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));
		GridData gd_composite_4 = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		gd_composite_4.heightHint = 40;
		composite_4.setLayoutData(gd_composite_4);

		Composite composite_10 = new Composite(composite_4, SWT.NONE);
		GridLayout gl_composite_10 = new GridLayout(1, false);
		gl_composite_10.verticalSpacing = 0;
		gl_composite_10.marginWidth = 0;
		gl_composite_10.marginHeight = 0;
		composite_10.setLayout(gl_composite_10);
		composite_10.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				true, 1, 1));

		Label lblMappingView = new Label(composite_10, SWT.NONE);
		lblMappingView.setText("Output Mapping");

		Composite composite_11 = new Composite(composite_4, SWT.NONE);
		GridLayout gl_composite_11 = new GridLayout(5, false);
		gl_composite_11.verticalSpacing = 0;
		gl_composite_11.marginWidth = 0;
		gl_composite_11.marginHeight = 0;
		composite_11.setLayout(gl_composite_11);
		composite_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
				true, 1, 1));

		createPullButton(composite_11);
		
		createAddButton(composite_11);

		createDeleteButton(composite_11);

		createUpButton(composite_11);

		createDownButton(composite_11);
	}
	
	private void deleteRow(){

		Table table = mappingTableViewer.getTable();
		setValueForCellEditor();
		int selectionIndex = table.getSelectionIndex();
		int[] indexs = table.getSelectionIndices();
		if (selectionIndex == -1) {
			WidgetUtility.errorMessage("Select Rows to delete");
		} else {
			table.remove(indexs);
			int itemsRemoved=0;
			for (int index : indexs) {
				mappingTableItemList.remove(index-itemsRemoved);
				if(index-itemsRemoved-1 != -1){
					table.setSelection(index-itemsRemoved-1);
				}else{
					table.setSelection(0);
				}
				itemsRemoved++;
			}
			mappingTableViewer.refresh();
		}
		refreshButtonStatus();
	
	}
	
	private void addNewRow(){
		setValueForCellEditor();
		LookupMapProperty lookupMapProperty = new LookupMapProperty();
		lookupMapProperty.setOutput_Field("");
		lookupMapProperty.setSource_Field("");
		if(!mappingTableItemList.contains(lookupMapProperty)){
		mappingTableItemList.add(lookupMapProperty);
		mappingTableViewer.refresh();
		mappingTableViewer.editElement(lookupMapProperty, 0);
		refreshButtonStatus();
		}
	}

	private void moveRowUp(){
		
		Table table = mappingTableViewer.getTable();
		setValueForCellEditor();
		int[] indexes = table.getSelectionIndices();
		for (int index : indexes) {

			if (index > 0) {
				Collections.swap(
						(List<LookupMapProperty>) mappingTableItemList,
						index, index - 1);
				mappingTableViewer.refresh();
			}
		}
		refreshButtonStatus();
	
	}

	private void moveRowDown(){

		Table table = mappingTableViewer.getTable();
		setValueForCellEditor();
		int[] indexes = table.getSelectionIndices();
		for (int i = indexes.length - 1; i > -1; i--) {

			if (indexes[i] < mappingTableItemList.size() - 1) {
				Collections.swap(
						(List<LookupMapProperty>) mappingTableItemList,
						indexes[i], indexes[i] + 1);
				mappingTableViewer.refresh();

			}
		}
		refreshButtonStatus();
	
	}
	private void attachShortcutListner(){
		Control currentControl= table;
		
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
	
	private void createPullButton(Composite composite_11) {
		btnPull = new Button(composite_11, SWT.NONE);
		btnPull.setImage(ImagePathConstant.PULL_BUTTON.getImageFromRegistry());
		btnPull.setToolTipText(PULL_TOOLTIP);
		
		btnPull.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<LookupMapProperty> mappingTableItemListCopy=new LinkedList<>();
				mappingTableItemListCopy.addAll(mappingTableItemList);
				mappingTableItemList.clear();
				mappingTableViewer.refresh(); 
				
				Schema schema = (Schema) component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
				if(schema==null){
					return;
				}
				List<String> schemaFieldList = getSchemaFieldList(schema);
				if(schemaFieldList.size() == 0){
					return;
				}
				
				for(String fieldName:schemaFieldList){
					LookupMapProperty row = getMappingTableItem(mappingTableItemListCopy,fieldName);
					if(row!=null){
						mappingTableItemList.add(row);
					}else{
						row=new LookupMapProperty();
						row.setSource_Field("");
						row.setOutput_Field(fieldName);
						mappingTableItemList.add(row);
					}
				}
				
				mappingTableViewer.refresh(); 				
				
			}

			private List<String> getSchemaFieldList(Schema schema) {
				List<String> schemaFieldList = new LinkedList<>();
				
				for(GridRow gridRow: schema.getGridRow()){
					schemaFieldList.add(gridRow.getFieldName());
				}
				return schemaFieldList;
			}
		});
	}

	private LookupMapProperty getMappingTableItem(List<LookupMapProperty> mappingTableItemListClone, String fieldName) {
		for(LookupMapProperty row:mappingTableItemListClone){
			if(StringUtils.equals(fieldName, row.getOutput_Field())){
				return row;
			}
		}
		return null;
	}

	private void createDownButton(Composite composite_11) {
		btnDown = new Button(composite_11, SWT.NONE);
		btnDown.setToolTipText(Messages.MOVE_DOWN_KEY_SHORTCUT_TOOLTIP);
		btnDown.setImage(ImagePathConstant.MOVEDOWN_BUTTON.getImageFromRegistry());
		
		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveRowDown();
			}
		});
	}

	private void createUpButton(Composite composite_11) {
		btnUp = new Button(composite_11, SWT.NONE);
		btnUp.setToolTipText(Messages.MOVE_UP_KEY_SHORTCUT_TOOLTIP);
		btnUp.setImage(ImagePathConstant.MOVEUP_BUTTON.getImageFromRegistry());
		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				 moveRowUp();
			}
		});
	}

	private void createDeleteButton(Composite composite_11) {
		
		btnDelete = new Button(composite_11, SWT.NONE);
		btnDelete.setToolTipText(Messages.DELETE_KEY_SHORTCUT_TOOLTIP);
		btnDelete.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());

		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteRow();
			}
		});
	}

	private void createAddButton(Composite composite_11) {
		btnAdd = new Button(composite_11, SWT.NONE);
		btnAdd.setToolTipText(Messages.ADD_KEY_SHORTCUT_TOOLTIP);
		btnAdd.setImage(ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addNewRow();
			}
		});
	}

	private void createInputFieldExpandBarSection(Composite composite) {
		Composite composite_1 = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.horizontalSpacing = 0;
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		composite_1.setLayout(gl_composite_1);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, false, true,
				1, 1);
		gd_composite_1.widthHint = 276;
		composite_1.setLayoutData(gd_composite_1);
		composite_1.setBounds(0, 0, 64, 64);

		final ScrolledComposite scrolledComposite_1 = new ScrolledComposite(
				composite_1, SWT.BORDER | SWT.V_SCROLL );
		scrolledComposite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true, 1, 1));
		scrolledComposite_1.setExpandHorizontal(true);
		scrolledComposite_1.setExpandVertical(true);
		
		Composite composite_7 = new Composite(scrolledComposite_1, SWT.NONE);
		composite_7.setLayout(new GridLayout(1, false));

		final ExpandBar expandBar = new ExpandBar(composite_7, SWT.H_SCROLL);
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		
		expandBar.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 240, 240, 240));
		populateInputFieldExpandBarSection(expandBar);

		expandBar.getItem(0).setExpanded(true);

		scrolledComposite_1.setContent(composite_7);
		scrolledComposite_1.setMinSize(composite_7.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));
		
		
		composite_7.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				
				for(ExpandItem expandItem:expandBar.getItems()){
					((TableColumn)expandItem.getData("TableColumn")).setWidth(scrolledComposite_1.getSize().x);
				}
			}
		});
	}

	private void populateInputFieldExpandBarSection(ExpandBar expandBar) {
		List<FilterProperties> inputPortFieldList = null;
		String count=(String)component.getProperties().get(Constants.INPUT_PORT_COUNT_PROPERTY);
		inputPortValue=Integer.valueOf(count);
		for (int i = 0; i < inputPortValue; i++) {
			if (joinMappingGrid != null) {
				if (joinMappingGrid.getLookupInputProperties() != null
						&& !joinMappingGrid.getLookupInputProperties()
								.isEmpty()) {
					if (i < joinMappingGrid.getLookupInputProperties().size())
						inputPortFieldList = joinMappingGrid
								.getLookupInputProperties().get(i);
					else
						inputPortFieldList = new ArrayList<>();
				} else {
					inputPortFieldList = new ArrayList<>();
				}
			}
			if (inputPorts != null) {
				inputPorts.add(inputPortFieldList);
				if (inputPortFieldList != null) {
					for (FilterProperties inputField : inputPortFieldList) {
						allInputFields.add(INPUT_PORT_ID_PREFIX + i + "." + inputField.getPropertyname());
					}
				}

			}
			
			addExpandItem(expandBar, inputPortFieldList, i);
		}
	}

	private void addExpandItem(ExpandBar expandBar,
			List<FilterProperties> inputPortFieldList, int portNumber) {
		ExpandItem xpndtmItem = new ExpandItem(expandBar, SWT.NONE);
		xpndtmItem.setText(EXPAND_ITEM_TEXT_PREFIX + portNumber);
		
		Composite composite_13 = new Composite(expandBar, SWT.NONE);
		xpndtmItem.setControl(composite_13);
		composite_13.setLayout(new GridLayout(1, false));

		TableViewer tableViewer_1 = new TableViewer(composite_13, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI );
		final Table table_1 = tableViewer_1.getTable();
		table_1.setLinesVisible(true);
		table_1.setHeaderVisible(true);
		table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer_1.setContentProvider(new ArrayContentProvider());
		
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				tableViewer_1, SWT.NONE);
		
		TableColumn tblclmnInputFields = tableViewerColumn_2.getColumn();
		
		tblclmnInputFields.setText(INPUT_TABLE_COLUMN_TEXT);

		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				FilterProperties tableField = (FilterProperties) element;
				return tableField.getPropertyname();
			}
		});

		tableViewer_1.setInput(inputPortFieldList);
		xpndtmItem.setData("TableColumn", table_1.getColumn(0));

		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		final String portLabel = INPUT_PORT_ID_PREFIX + portNumber + ".";
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
		// final Table table = (Table) sourceControl;
		DragSource source = new DragSource(table_1, operations);
		source.setTransfer(types);
		source.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
				// Set the data to be the first selected item's text
				event.data = addDelimeter(portLabel, table_1.getSelection());
			}
		});

		xpndtmItem.setHeight(190);
		xpndtmItem.setExpanded(true);
		
		table_1.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				Table table = (Table) e.widget;
				Rectangle area = table.getClientArea();
				int totalAreaWidth = area.width;
				int diff = totalAreaWidth - (table.getColumn(0).getWidth());
				table.getColumn(0).setWidth(diff + table.getColumn(0).getWidth());
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		});
	}

	private String addDelimeter(String portLabel, TableItem[] selectedTableItems) {
		StringBuffer buffer = new StringBuffer();
		for (TableItem tableItem : selectedTableItems) {
			buffer.append(portLabel + tableItem.getText() + MAPPING_TABLE_ITEM_DELIMILATOR);
		}
		return buffer.toString();
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}


	@Override
	protected void okPressed() {
		
		setValueForCellEditor();
		joinMappingGrid.setLookupInputProperties(inputPorts);
		joinMappingGrid.setLookupMapProperties(mappingTableItemList);
		
		populateCurrentItemsOfTable();

		if (!oldJoinMappingGrid.equals(newJoinMappingGrid)) {
			propertyDialogButtonBar.enableApplyButton(true);
		}
		super.okPressed();
	}
	
	@Override
	protected void cancelPressed(){
		joinMappingGrid.setLookupInputProperties(oldJoinMappingGrid.getLookupInputProperties());
		joinMappingGrid.setLookupMapProperties(oldJoinMappingGrid.getLookupMapProperties());
		super.cancelPressed();
	}

	private void populateJoinMapDialog() {

		if (joinMappingGrid.getLookupMapProperties() != null
				&& !joinMappingGrid.getLookupMapProperties().isEmpty()) {
			mappingTableItemList = joinMappingGrid.getLookupMapProperties();
		} else {
			mappingTableItemList = new LinkedList<>();
		}

		if (joinMappingGrid.getButtonText() != null
				&& !StringUtils.equals(joinMappingGrid.getButtonText(), NO_COPY)) {
			radioButtonMap.get(joinMappingGrid.getButtonText()).setSelection(
					true);
			mappingTableViewer.getTable().setEnabled(false);
			enableMappingTableButtonPanel(false);
			String inputPortID=StringUtils.remove(joinMappingGrid.getButtonText(), Constants.COPY_FROM_INPUT_PORT_PROPERTY);
			copyFieldsWhenCopyOfIsSelected(inputPorts.get(Integer.parseInt(StringUtils.remove(inputPortID, Constants.INPUT_SOCKET_TYPE))), inputPortID);
			

		} else {
			radioButtonMap.get(NO_COPY).setSelection(true);
		}

		mappingTableViewer.setInput(mappingTableItemList);

		mappingTableViewer.refresh();

		populatePreviousItemsOfTable();
		
		mouseDoubleClick();
	}
	
	 private void mouseDoubleClick() {
		mappingTableViewer.getTable().addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				addNewRow();
			}
		});
		
		if(mappingTableViewer.getTable().getEnabled()){
		refreshButtonStatus();
		}

	}

	private void populatePreviousItemsOfTable() {
		oldJoinMappingGrid = joinMappingGrid.clone();
	}

	private void populateCurrentItemsOfTable() {
		newJoinMappingGrid = joinMappingGrid.clone();
	}
	
	private void enableMappingTableButtonPanel(boolean enabled){
		btnAdd.setEnabled(enabled);
		btnDelete.setEnabled(enabled);
		btnDown.setEnabled(enabled);
		btnUp.setEnabled(enabled);
		btnPull.setEnabled(enabled);
	}
	
	private void refreshButtonStatus(){
		if(mappingTableItemList.size()>=1){
			btnDelete.setEnabled(true);
		}
		
		if(mappingTableItemList.size()>1){
			btnDown.setEnabled(true);
			btnUp.setEnabled(true);
		}
		if(mappingTableItemList.size() == 0){
			btnDelete.setEnabled(false);
			btnDown.setEnabled(false);
			btnUp.setEnabled(false);
		}
		
	}
	
	private void setValueForCellEditor(){
		
		if(outputEditingSupport !=null && outputEditingSupport.getEditor()!=null){
			outputEditingSupport.getEditor().getControl().setEnabled(false);
			outputEditingSupport.getEditor().getControl().setEnabled(true);
		}
		
		if(inputEditingSupport !=null && inputEditingSupport.getEditor()!=null){
			inputEditingSupport.getEditor().getControl().setEnabled(false);
			inputEditingSupport.getEditor().getControl().setEnabled(true);
		}
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(800,500);
	}
}
