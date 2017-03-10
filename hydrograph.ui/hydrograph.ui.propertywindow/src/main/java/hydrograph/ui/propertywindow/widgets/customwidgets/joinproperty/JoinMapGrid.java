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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.joinproperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
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
import hydrograph.ui.datastructure.property.JoinMappingGrid;
import hydrograph.ui.datastructure.property.LookupMapProperty;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTJoinWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.joinlookupproperty.JoinContentProvider;
import hydrograph.ui.propertywindow.widgets.customwidgets.joinlookupproperty.LookupCellModifier;
import hydrograph.ui.propertywindow.widgets.customwidgets.joinlookupproperty.LookupLabelProvider;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTCellModifier;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTFilterContentProvider;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTFilterLabelProvider;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTSWTWidgets;


public class JoinMapGrid extends Dialog {

	private static final String NONE = "None";
	private Label errorLabel;
	private TableViewer outputTableViewer;
	private int j = 20;
	private Button okButton;
	private int inputPortValue = ELTJoinWidget.value;
	private TableViewer[] inputTableViewer = new TableViewer[inputPortValue];
	private Button[] radio = new Button[inputPortValue + 1];
	private Composite expandItemComposite;

	public static final String OPERATIONAL_INPUT_FIELD = "Field Name";
	protected static final String ERROR = null;
	public static String PROPERTY_NAME = "Source Field";
	public static String PROPERTY_VALUE = "Output Field";
	private String[] COLUMN_NAME = { PROPERTY_NAME, PROPERTY_VALUE };
	private String[] INPUT_COLUMN_NAME = { OPERATIONAL_INPUT_FIELD };
	private String previousRadioButtonSelection = NONE;
	private List<FilterProperties> joinInputList;
	private List<LookupMapProperty> joinOutputList;
	private List<List<FilterProperties>> joinInputSchemaList = new ArrayList<>();
	private ELTSWTWidgets widget = new ELTSWTWidgets();
	private JoinMappingGrid joinMappingGrid;
	private static final String INFORMATION = "Information";
	private TableItem[] previousItems;
	private TableItem[] currentItems;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private List<String> nonMappedFieldList=new ArrayList<>();
	private HashMap<String, List<String>> inputFieldMap=new HashMap<String, List<String>>();
	private String IN="in";
	private Button deleteButton;
	private Button upButton;
	private Button downButton;
	

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param propertyDialogButtonBar 
	 */
	public JoinMapGrid(Shell parentShell, JoinMappingGrid joinPropertyGrid, PropertyDialogButtonBar propertyDialogButtonBar) {
		super(parentShell);
		
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.WRAP
				| SWT.APPLICATION_MODAL);
		this.joinMappingGrid = joinPropertyGrid;
		this.propertyDialogButtonBar=propertyDialogButtonBar;
	
	}
	public void getJoinPropertyGrid() {
		joinMappingGrid.setLookupInputProperties(joinInputSchemaList);
		joinMappingGrid.setLookupMapProperties(joinOutputList);
	}


	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("Join Mapping");
		container.setFocus();
		container.setLayout(new GridLayout(6, false));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Composite composite = new Composite(container, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite.heightHint = 595;
		gd_composite.widthHint = 281;
		composite.setLayoutData(gd_composite);

		final ScrolledComposite scrolledComposite = new ScrolledComposite(
				composite, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_scrolledComposite = new GridData(SWT.LEFT, SWT.CENTER,
				false, false, 1, 1);
		gd_scrolledComposite.heightHint = 542;
		gd_scrolledComposite.widthHint = 240;
		scrolledComposite.setLayoutData(gd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		final ExpandBar expandBar = new ExpandBar(scrolledComposite, SWT.NONE);
		expandBar.setLayoutData(new RowData(200, 550));

		for (int i = 0; i < inputPortValue; i++) {
			if (joinMappingGrid != null) {
				if (joinMappingGrid.getLookupInputProperties() != null
						&& !joinMappingGrid.getLookupInputProperties()
								.isEmpty()) {
					if (i < joinMappingGrid.getLookupInputProperties().size())
						joinInputList = joinMappingGrid
								.getLookupInputProperties().get(i);
					else
						joinInputList = new ArrayList<>();
				} else {
					joinInputList = new ArrayList<>();
				}
			}
			if (joinInputSchemaList != null) {
				joinInputSchemaList.add(joinInputList);
			}
			expandItemComposite = (Composite) createComposite(expandBar,
					joinInputList, i);
		}

		if (joinMappingGrid.getLookupMapProperties() != null
				&& !joinMappingGrid.getLookupMapProperties().isEmpty()) {
			joinOutputList = joinMappingGrid.getLookupMapProperties();
		} else {
			joinOutputList = new ArrayList<>();
		}
		expandBar.getItem(0).setExpanded(true);
		expandBar.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry(250,
				250, 250));
		Listener updateScrolledSize = new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						scrolledComposite.setMinSize(expandBar.computeSize(
								SWT.DEFAULT, SWT.DEFAULT));
					}
				});
			}
		};
		
		expandBar.addListener(SWT.Expand, updateScrolledSize);
		expandBar.addListener(SWT.Collapse, updateScrolledSize);
		expandBar.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent event) {
			}

			@Override
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.ESC) {
					close();
					event.doit = false;
				}
			}
		});
		scrolledComposite.setContent(expandBar);
		scrolledComposite.setMinSize(expandBar.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));

		errorLabel = new Label(composite, SWT.None);
		errorLabel.setAlignment(SWT.LEFT_TO_RIGHT);
		GridData gd_lblNewLabel_1 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblNewLabel_1.heightHint = 20;
		gd_lblNewLabel_1.widthHint = 260;
		errorLabel.setLayoutData(gd_lblNewLabel_1);
		errorLabel.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
		errorLabel.setText("PropertyError");
		errorLabel.setVisible(false);
		new Label(container, SWT.NONE);

		Composite composite_1 = new Composite(container, SWT.None);
		GridData gd_composite_1 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite_1.widthHint = 398;
		gd_composite_1.heightHint = 596;
		composite_1.setLayoutData(gd_composite_1);

		Composite composite_5 = new Composite(composite_1, SWT.None);
		composite_5.setBounds(290, 4, 100, 24);
		createLabel(composite_5);

		outputTableViewer = widget.createTableViewer(composite_1, COLUMN_NAME,
				new int[] { 0, 30, 398, 538 }, 196, new JoinContentProvider(),
				new LookupLabelProvider());

		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setBounds(10, 11, 92, 15);
		lblNewLabel.setText("Output Mapping");
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
		widget.createTableColumns(outputTableViewer.getTable(), COLUMN_NAME,
				196);
		CellEditor[] editors = widget.createCellEditorList(
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
							for (int i = 0; i < inputPortValue; i++) {
								if (joinInputSchemaList != null) {
									if (joinInputSchemaList.get(i).contains(
											filter)) {
										ExpandItem item = expandBar.getItem(i);
										item.setExpanded(true);
										inputTableViewer[i]
												.getTable()
												.setSelection(
														joinInputSchemaList
																.get(i)
																.indexOf(filter));
									}
								}
							}
						}
					}
				}
			}
		});
		errorLabel = new Label(composite_1, SWT.None);
		errorLabel.setBounds(0, 576, 350, 25);
		errorLabel.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
		errorLabel.setVisible(false);

		new Label(container, SWT.NONE);

		Composite composite_2 = new Composite(container, SWT.BORDER);
		composite_2.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridData gd_composite_2 = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_composite_2.heightHint = 595;
		gd_composite_2.widthHint = 133;
		composite_2.setLayoutData(gd_composite_2);

		ScrolledComposite scrolledComposite_1 = new ScrolledComposite(
				composite_2, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite_1.setLayoutData(new RowData(100, 564));

		Composite composite_3 = new Composite(scrolledComposite_1, SWT.BORDER);
		composite_3.setLayout(new RowLayout(SWT.VERTICAL));

		radio[0] = widget.buttonWidget(composite_3, SWT.RADIO, new int[] { 0,
				0, 90, 20 }, NONE);
		for (int i = 1, k = 0; i < radio.length; i++, k++) {
			radio[i] = widget.buttonWidget(composite_3, SWT.RADIO, new int[] {
					0, j, 90, 20 }, Constants.COPY_FROM_INPUT_PORT_PROPERTY +"in" + k);
			j = j + 20;
		}
		scrolledComposite_1.setContent(composite_3);
		scrolledComposite_1.setExpandHorizontal(true);
		scrolledComposite_1.setExpandVertical(true);
		scrolledComposite_1.setMinSize(composite_3.computeSize(SWT.DEFAULT,
				SWT.DEFAULT));

		for (int i = 0; i < radio.length; i++) {
			final int inPortIndex = i;
			radio[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {

					Button button = (Button) event.widget;
					button.getSelection();
					if (button.getSelection()) {
						if (NONE.equalsIgnoreCase(button.getText())) {
							outputTableViewer.getTable().setEnabled(true);
							joinMappingGrid.setButtonText(button.getText());
							joinMappingGrid.setIsSelected(false);
							if (!NONE.equals(previousRadioButtonSelection)) {
								joinOutputList.clear();
								outputTableViewer.refresh();
							}
							previousRadioButtonSelection = NONE;
						} else {
							okButton.setEnabled(true);
							errorLabel.setVisible(false);
							radio[0].setSelection(false);
							outputTableViewer.getTable().setEnabled(false);
							joinMappingGrid.setButtonText(button.getText());
							joinMappingGrid.setIsSelected(true);
							addAllFieldsFromSocketId(inPortIndex - 1);
						}
					}
				}
			});
		}
		populate();
		if (joinOutputList != null) {
			dropData(outputTableViewer, joinOutputList, true);
		}
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
		nonMappedFieldList=getListOfNonMappedFields(inputFieldMap);
		return container;
	}
	


	public void changeColorOfNonMappedFields() {
		if (outputTableViewer.getTable().getItems() != null) {
			TableItem[] items = outputTableViewer.getTable().getItems();
			for (int i = 0; i < joinOutputList.size(); i++) {
				for (String sourceField : nonMappedFieldList) {
					if (joinOutputList.get(i).getSource_Field().equalsIgnoreCase(sourceField) &&
							!ParameterUtil.isParameter(sourceField)) {
						items[i].setForeground(0, CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
					}
				}
			}
		}
	}
	
	private List<String> getListOfNonMappedFields(HashMap<String, List<String>> inputFieldMap) {
		Iterator<Entry<String, List<String>>> iterator = inputFieldMap.entrySet().iterator();
	    while (iterator.hasNext()) {
	        Map.Entry portFieldPair = (Map.Entry)iterator.next();
	        for (LookupMapProperty lookupMapProperty : joinOutputList) {
	        	String port = "";
				if (lookupMapProperty.getSource_Field().length() >= 3) {
					port = lookupMapProperty.getSource_Field().substring(0, 3);
				}
	        	String source_field = lookupMapProperty.getSource_Field().substring(lookupMapProperty.getSource_Field().lastIndexOf(".") + 1);
				if(portFieldPair.getKey().equals(port))
				{
					List<String> value = (ArrayList<String>) portFieldPair.getValue();
					if(!value.isEmpty()&&!checkIfSourceFieldExists(value, source_field))
					{
						 nonMappedFieldList.add(port+"."+source_field);
					}
				}
				else {
					nonMappedFieldList.add(source_field);
				}
				if (port.equalsIgnoreCase("")) {
					nonMappedFieldList.add(source_field);
				}
			}
	    }
	    return nonMappedFieldList;
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
		for (List<FilterProperties> inputFieldList : joinInputSchemaList) {
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
			inputFieldMap.put(IN + j, inputFieldListPerPort);
			j++;
		}
		return inputFieldMap;
	}
	
	

	private void populatePreviousItemsOfTable() {
		if (outputTableViewer.getTable().getItems() != null) {
			previousItems = outputTableViewer.getTable().getItems();
		}
	}
	private void populateCurrentItemsOfTable() {
		if (outputTableViewer.getTable().getItems() != null) {
			currentItems = outputTableViewer.getTable().getItems();
		}
	}

	private void addAllFieldsFromSocketId(int inSocketIndex) {
		List<FilterProperties> inputFieldList = joinInputSchemaList
				.get(inSocketIndex);
		LookupMapProperty property = null;
		if (inputFieldList != null) {
			joinOutputList.clear();
			previousRadioButtonSelection = Constants.INPUT_SOCKET_TYPE
					+ inSocketIndex;
			for (FilterProperties properties : inputFieldList) {
				property = new LookupMapProperty();
				property.setSource_Field(previousRadioButtonSelection + "."
						+ properties.getPropertyname());
				property.setOutput_Field(properties.getPropertyname());
				joinOutputList.add(property);
			}
			outputTableViewer.refresh();
		}
	}

	private void populate() {
		Boolean radioButtonSelected = false;

		for (int i = 0; i < radio.length; i++) {
			if (StringUtils.isNotBlank(joinMappingGrid.getButtonText())) {
				if (joinMappingGrid.getButtonText().equalsIgnoreCase(
						radio[i].getText()))
					if (joinMappingGrid.getButtonText()
							.equalsIgnoreCase("None")) {
						radio[0].setSelection(true);
						outputTableViewer.getTable().setEnabled(true);
					} else {
						radio[i].setSelection(true);
						outputTableViewer.getTable().setEnabled(false);
						radioButtonSelected = true;
					}
			} else {
				radio[i].setSelection(false);
			}
		}
		if (!radioButtonSelected) {
			radio[0].setSelection(true);
			outputTableViewer.getTable().setEnabled(true);
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

	private Control createComposite(ExpandBar expandBar,
			final List<FilterProperties> joinInputList,
			final int tableViewerIndex) {

		ExpandItem xpndtmItem = new ExpandItem(expandBar, SWT.NONE);
		xpndtmItem.setText("Input index : in" + tableViewerIndex);

		Composite comGrid = new Composite(expandBar, SWT.BORDER);
		comGrid.setBounds(15, 0, 226, 200);

		xpndtmItem.setControl(comGrid);
		xpndtmItem.setHeight(270);
		xpndtmItem.setExpanded(false);
		
		inputTableViewer[tableViewerIndex] = widget.createTableViewer(comGrid,
				INPUT_COLUMN_NAME, new int[] { 7,7, 229, 251 }, 224,
				new ELTFilterContentProvider(),new ELTFilterLabelProvider());
		
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
		widget.createTableColumns(
				inputTableViewer[tableViewerIndex].getTable(),
				INPUT_COLUMN_NAME, 224);
		CellEditor[] editors = widget.createCellEditorList(
				inputTableViewer[tableViewerIndex].getTable(), 1);
		editors[0].setValidator(inputSchemaEditorValidation(joinInputList,
				Messages.EmptySourceFieldNotification,
				inputTableViewer[tableViewerIndex]));
		inputTableViewer[tableViewerIndex].setCellModifier(new ELTCellModifier(
				inputTableViewer[tableViewerIndex]));
		inputTableViewer[tableViewerIndex]
				.setColumnProperties(INPUT_COLUMN_NAME);
		inputTableViewer[tableViewerIndex].setCellEditors(editors);
		inputTableViewer[tableViewerIndex].setInput(joinInputList);
        widget.applyDragFromTableViewer(
				inputTableViewer[tableViewerIndex].getTable(), tableViewerIndex);

		return comGrid;
	}
    
	private void createLabel(Composite parent) {
		Button addButton = widget.buttonWidget(parent, SWT.CENTER | SWT.PUSH, new int[] { 0, 0, 25, 20 }, "");
		addButton.setImage(ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
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

	    deleteButton = widget.buttonWidget(parent, SWT.CENTER, new int[] { 25, 0, 25, 20 }, "");
	    deleteButton.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
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
				if (joinOutputList.size()<= 1) {
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				}
			}
		});

		upButton = widget.buttonWidget(parent, SWT.CENTER, new int[] { 50, 0, 25, 20 }, "");
		 upButton.setImage(ImagePathConstant.UP_ICON.getImageFromRegistry());
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

		downButton = widget.buttonWidget(parent, SWT.CENTER, new int[] { 74, 0, 25, 20 }, "");
		 downButton.setImage(ImagePathConstant.DOWN_ICON.getImageFromRegistry());
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

	public Label labelWidget(Composite parent, int style, int[] bounds,
			String value) {
		Label label = new Label(parent, style);
		label.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
		label.setText(value);
		// label.setImage(image);

		return label;
	}

	public Button buttonWidget(Composite parent, int style, int[] bounds,
			String value, Image image) {
		Button button = new Button(parent, style);
		button.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
		button.setText(value);
		button.setImage(image);
		return button;
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

	@Override
	protected void okPressed() {
		
		
		if(OSValidator.isMac())
		{   
			List<TableViewer> tableViewers = new ArrayList<TableViewer>();
			tableViewers.add(outputTableViewer);
			tableViewers.addAll(java.util.Arrays.asList(inputTableViewer));
			for(TableViewer tableView : tableViewers){
				if(tableView !=null){
			for(CellEditor cellEditor : tableView.getCellEditors()){
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
		getJoinPropertyGrid();
		super.close();
	}

	@Override
	protected void cancelPressed() {
		hasOutputMappingInTableChanged();
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
					getJoinPropertyGrid();
					returnValue = super.close();
				}
			} else {
				returnValue = super.close();
			}
		}
		return returnValue;
	}

	@Override
	public boolean close() {
		return hasOutputMappingInTableChanged();
	}
	protected boolean inputSchemavalidate(List<FilterProperties> inputList,
			TableViewer tableViewer) {
		int propertyCounter = 0;
		for (FilterProperties temp : inputList) {
			if (!temp.getPropertyname().trim().isEmpty()) {
				Matcher matchs = Pattern.compile(Constants.REGEX).matcher(
						temp.getPropertyname().trim());
				if (!matchs.matches()) {
					tableViewer.getTable().setSelection(propertyCounter);
					errorLabel.setVisible(true);
					errorLabel.setText(Messages.ALLOWED_CHARACTERS);
					return false;
				}
			} else {
				tableViewer.getTable().setSelection(propertyCounter);
				errorLabel.setVisible(true);
				errorLabel.setText(Messages.EmptySourceFieldNotification);
				return false;
			}

			propertyCounter++;

		}
		return true;
	}

	private boolean validateOutputSchema(String outputFieldValue, String sourceFieldValue) {
		int propertycount = 0;
		int propertyValuecount = 0;
		for (LookupMapProperty join : joinOutputList) {
			if (join.getSource_Field().trim().isEmpty()) {
				outputTableViewer.getTable().setSelection(propertycount);
				errorLabel.setVisible(true);
				errorLabel.setText(Messages.EmptySourceFieldNotification);
				return false;
			} else if (join.getOutput_Field().trim().isEmpty()) {
				outputTableViewer.getTable().setSelection(propertyValuecount);
				errorLabel.setVisible(true);
				errorLabel.setText(Messages.EmptyOutputFieldNotification);
				return false;
			} else {
				errorLabel.setVisible(false);
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

	private void validateDuplicatesInOutputField() {
		boolean duplicateFound = false;
		if (outputTableViewer.getTable().getItems() != null) {
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
				errorLabel.setText(Messages.OutputFieldAlreadyExists);
				errorLabel.setVisible(true);
			} else {
				if (okButton != null && errorLabel != null) {
					okButton.setEnabled(true);
					errorLabel.setVisible(false);
				}
			}
		}
	}
  
	// Creates Value Validator for table's cells
	private ICellEditorValidator inputSchemaEditorValidation(
			final List<FilterProperties> joinInputList,
			final String errorMessage, final TableViewer viewer) {
		ICellEditorValidator propertyValidator = new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				String currentSelectedFld = viewer.getTable()
						.getItem(viewer.getTable().getSelectionIndex())
						.getText();
				String valueToValidate = String.valueOf(value).trim();
				for (FilterProperties temp : joinInputList) {
					if (!currentSelectedFld.equalsIgnoreCase(valueToValidate)
							&& temp.getPropertyname().equalsIgnoreCase(
									valueToValidate)) {
						errorLabel
								.setText(Messages.FieldNameAlreadyExists);
						errorLabel.setVisible(true);
						okButton.setEnabled(false);
						return "ERROR";
					} else
						errorLabel.setVisible(false);
					okButton.setEnabled(true);
				}
				validateDuplicatesInOutputField();
				return null;
			}
		};
		return propertyValidator;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(870, 757);
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
					errorLabel.setText(errorMessage);
					errorLabel.setVisible(true);
					okButton.setEnabled(false);
					return "ERROR";
				} else {
					okButton.setEnabled(true);
				}
				for (LookupMapProperty temp : propertyList) {
					if (!currentSelectedFld.equalsIgnoreCase(valueToValidate)
							&& temp.getSource_Field().equalsIgnoreCase(
									valueToValidate)) {
						errorLabel
								.setText(Messages.SourceFieldAlreadyExists);
						errorLabel.setVisible(true);
						okButton.setEnabled(false);
						return "ERROR";
					} else {
						errorLabel.setVisible(false);
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
					errorLabel.setText(errorMessage);
					errorLabel.setVisible(true);
					okButton.setEnabled(false);
					return "ERROR";
				} else {
					okButton.setEnabled(true);
				}
				for (LookupMapProperty temp : propertyList) {
					if (!currentSelectedFld.equalsIgnoreCase(valueToValidate)
							&& temp.getOutput_Field().equalsIgnoreCase(
									valueToValidate)) {
						errorLabel
								.setText(Messages.OutputFieldAlreadyExists);
						errorLabel.setVisible(true);
						okButton.setEnabled(false);
						validateDuplicatesInOutputField();
						return "ERROR";
					} else {
						errorLabel.setVisible(false);
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
			if (!validateOutputSchema(outputFieldValue, sourceFieldValue))
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