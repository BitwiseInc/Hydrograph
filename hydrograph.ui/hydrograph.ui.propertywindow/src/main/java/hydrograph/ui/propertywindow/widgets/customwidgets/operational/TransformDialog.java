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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ExternalOperationExpressionUtil;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.PathUtility;
import hydrograph.ui.common.util.TransformMappingFeatureUtility;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.InputField;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.transform.viewdata.TransformViewDataDialog;
import hydrograph.ui.propertywindow.utils.SWTResourceManager;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.OperationClassConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.mapping.tables.inputtable.InputFieldColumnLabelProvider;
import hydrograph.ui.propertywindow.widgets.customwidgets.mapping.tables.inputtable.TableContentProvider;
import hydrograph.ui.propertywindow.widgets.customwidgets.operational.external.ExpresssionOperationImportExportComposite;
import hydrograph.ui.propertywindow.widgets.customwidgets.operational.external.ImportExportType;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTCellModifier;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTFilterContentProvider;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTFilterLabelProvider;
import hydrograph.ui.propertywindow.widgets.filterproperty.ErrorLabelProvider;
import hydrograph.ui.propertywindow.widgets.filterproperty.TransformMappingOutputTableCellModifier;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTSWTWidgets;
import hydrograph.ui.propertywindow.widgets.interfaces.IOperationClassDialog;
import hydrograph.ui.propertywindow.widgets.utility.DragDropUtility;
import hydrograph.ui.propertywindow.widgets.utility.SchemaButtonsSyncUtility;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;
import hydrograph.ui.validators.utils.ValidatorUtility;

/**
 * @author Bitwise
 *
 */

public class TransformDialog extends Dialog implements IOperationClassDialog {

	private static final String EXITING_TRANSFORM_EDITOR = "Exiting Transform Editor";
	private static final String OUTPUT_DELETE_BUTTON = "outputDeleteButton";
	private static final String OUTPUT_ADD_BUTTON = "outputAddButton";
	private static final String OPERATION_OUTPUT_FIELD_TABLE_VIEWER = "operationOutputFieldTableViewer";
	private static final String INPUT_DELETE_BUTTON = "inputDeletButton";
	private static final String INPUT_ADD_BUTTON = "inputAddButton";
	private static final String OPERATION_INPUT_FIELD_TABLE_VIEWER = "operationInputFieldTableViewer";
	private static final String OPERATION_ID_TEXT_BOX = "operationIDTextBox";
	private static final String OPERATION_CLASS_TEXT_BOX = "operationClassTextBox";
	private static final String PARAMETER_TEXT_BOX = "parameterTextBox";
	private static final String BTN_NEW_BUTTON = "btnNewButton";
	private static final String OUTPUT_TABLE_VIEWER="OutputTableViewer";
	private MappingSheetRow mappingSheetRowForOperationClass; 
	
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */

	private Composite container;
	private CellEditor[] editors;
	private ExpandBar expandBar = null;
	private ELTSWTWidgets widget = new ELTSWTWidgets();
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private boolean isYesButtonPressed;
	private boolean isNoButtonPressed;
	private boolean cancelPressed;
	private boolean okPressed;
	private Table tableViewerTable;
	private Component component;
	private WidgetConfig widgetConfig;
	private TableViewer operationalInputFieldTableViewer;
	private TableViewer operationalOutputFieldTableViewer;
	private ScrolledComposite scrolledComposite;
	private TableViewer inputFieldTableViewer;
	private TableViewer mappingTableViewer;
	private TransformMapping transformMapping;
	private TableViewer outputFieldViewer;
	private Map<String,List<FilterProperties>> temporaryOutputFieldMap;
	private MappingSheetRow mappingSheetRowForExpression;
	private Label errorLabel;
	private boolean isOperationInputFieldDuplicate;
	private TransformDialog transformDialog;
	private List<Label> errorLabelList;
	private TableViewer errorTableViewer;
	private Composite errorComposite;
	private Map<String,List<String>> duplicateOperationInputFieldMap;
	private Map<String,List<String>> duplicateFieldMap;
	private ControlDecoration isFieldNameAlphanumericDecorator;
	private ControlDecoration fieldNameDecorator;
	private SashForm mainSashForm;
	private SashForm middleSashForm;
	private List<FilterProperties> finalSortedList;
	private Set<Integer> outputFieldIndices = new LinkedHashSet<Integer>();
	private Button deleteLabel;
	private Button addLabel;
	private Button viewTransform;
	private TransformMapping oldTransformMapping;
	private Button isAllPassthrougButton;
	/**
    * @param parentShell
    * @param component
    * @param widgetConfig
    * @param atMapping
    */
	public TransformDialog(Shell parentShell, Component component, WidgetConfig widgetConfig, TransformMapping atMapping) {

		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL);
		this.transformMapping = atMapping;
		oldTransformMapping=(TransformMapping) atMapping.clone();
		isYesButtonPressed = false;
		isNoButtonPressed = false;
		this.component = component;
		this.widgetConfig = widgetConfig;
		this.transformDialog = this;
		temporaryOutputFieldMap=new HashMap<String,List<FilterProperties>>();
		errorLabelList=new ArrayList<>();
		duplicateOperationInputFieldMap=new HashMap<String,List<String>>();
		duplicateFieldMap=new HashMap<String,List<String>>();
	}  

	/**
	 * @wbp.parser.constructor
	 */

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(3, false));
		container.getShell().setText(Messages.TRANSFORM_EDITOR);
		propertyDialogButtonBar = new PropertyDialogButtonBar(container);
		mainSashForm = new SashForm(container, SWT.SMOOTH);
		mainSashForm.setSashWidth(5);
		mainSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		createInputFieldTable(mainSashForm);
        createOperationClassGrid(mainSashForm);
		createOutputFieldTable(mainSashForm);
		if(OSValidator.isMac()){
			mainSashForm.setWeights(new int[] {54, 242, 120});
		}else{
			mainSashForm.setWeights(new int[] {67, 242, 107});
		}
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Point newSize = container.getShell().computeSize(screenSize.width/2, screenSize.height/2, true);
		getShell().setMinimumSize(newSize);
		return mainSashForm;
	}

	private void createInputFieldTable(Composite container) {

		Composite inputFieldComposite = new Composite(container, SWT.NONE);
		
		inputFieldComposite.setLayout(new GridLayout(2, false));

		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.widthHint = 250;
		inputFieldComposite.setLayoutData(gd_composite);
		new Label(inputFieldComposite, SWT.NONE);
		inputFieldTableViewer = new TableViewer(inputFieldComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);

		setTableViewer(inputFieldTableViewer, inputFieldComposite, new String[] { Messages.OPERATIONAL_SYSTEM_FIELD },
				new TableContentProvider());
		inputFieldTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		DragDropUtility.INSTANCE.applyDragFromTableViewer(inputFieldTableViewer.getTable());
		inputFieldTableViewer.setLabelProvider(new InputFieldColumnLabelProvider());
		inputFieldTableViewer.setInput(transformMapping.getInputFields());
        addControlListener(inputFieldTableViewer.getTable());
        inputFieldTableViewer.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) 
			{
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(null,
						inputFieldTableViewer,outputFieldViewer,null,null);
  			}
		});
	}

	private void createOutputFieldTable(Composite composite) {

		Composite outputComposite = new Composite(composite, SWT.NONE);
		outputComposite.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.widthHint = 250;
		outputComposite.setLayoutData(gridData);

		Composite buttonComposite = new Composite(outputComposite, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, true));
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		Composite outputFieldComposite = new Composite(outputComposite, SWT.NONE);
		outputFieldComposite.setLayout(new GridLayout(1, false));
		outputFieldComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Button btnPull = new Button(buttonComposite, SWT.NONE);
		btnPull.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//MessageDialog dialog = new MessageDialog(new Shell(), Constants.SYNC_CONFIRM, null, Constants.SYNC_CONFIRM_MESSAGE, MessageDialog.QUESTION, new String[] {"OK", "Cancel" }, 0);
				MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
				dialog.setText(Constants.SYNC_CONFIRM);
				dialog.setMessage(Constants.SYNC_CONFIRM_MESSAGE);
				int dialogResult =dialog.open();
				if(dialogResult == SWT.OK){
					syncTransformFieldsWithSchema();
				}
			}
		});
		btnPull.setBounds(20, 10, 20, 20);
		btnPull.setImage(ImagePathConstant.PULL_BUTTON.getImageFromRegistry());
		btnPull.setToolTipText("Pull Schema");
	
		outputFieldViewer = new TableViewer(outputFieldComposite, SWT.BORDER | SWT.FULL_SELECTION|SWT.MULTI);
		setTableViewer(outputFieldViewer, outputFieldComposite, new String[] { Messages.OUTPUT_FIELD },
				new ELTFilterContentProvider());
		outputFieldViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		outputFieldViewer.setCellModifier(new TransformMappingOutputTableCellModifier(outputFieldViewer,transformDialog));
		outputFieldViewer.setLabelProvider(new ELTFilterLabelProvider());
		refreshOutputTable();
		setIsOperationInputFieldDuplicate();
		showHideValidationMessage();
		outputFieldViewer.getControl().addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseUp(MouseEvent e) {
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(null,
						inputFieldTableViewer,outputFieldViewer,null,null);
			}
		 });
		addControlListener(outputFieldViewer.getTable());
		createExternalOutputFields(outputFieldComposite);
	}

	private void createExternalOutputFields(Composite outputFieldComposite) {
		final ExpresssionOperationImportExportComposite container = new ExpresssionOperationImportExportComposite(
				outputFieldComposite, SWT.BORDER, ImportExportType.FIELDS,
				transformMapping.getExternalOutputFieldsData()) {

			@Override
			protected void exportButtonSelection(Button widget) {
				ExternalOperationExpressionUtil.INSTANCE.exportOutputFields(getFile(), transformMapping, true,component.getComponentName());
				transformDialog.showHideValidationMessage();
			}

			@Override
			protected void importButtonSelection(Button widget) {
				ExternalOperationExpressionUtil.INSTANCE.importOutputFields(getFile(), transformMapping, true,  component.getComponentName());
				refresh();
			}

			@Override
			protected void interalRadioButtonSelection(Button widget) {
				transformDialog.showHideValidationMessage();
			}

			@Override
			protected void externalRadioButtonSelection(Button widget) {
				transformDialog.showHideValidationMessage();
			}
		};

		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}
	
	private void addListenerForRowHighlighting(
			AbstractExpressionComposite expressionComposite ) 
	{
		expressionComposite.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				Composite composite=(Composite)e.widget;
				AbstractExpressionComposite expressionComposite=(AbstractExpressionComposite)composite;
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(expressionComposite.getExpressionIdTextBox(),
					inputFieldTableViewer,outputFieldViewer,transformMapping,finalSortedList);
			}
		});
    	expressionComposite.getExpressionIdTextBox().getParent().addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				Composite composite=(Composite)e.widget;
				AbstractExpressionComposite expressionComposite=(AbstractExpressionComposite)composite.getParent().getParent();
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(expressionComposite.getExpressionIdTextBox(),
						inputFieldTableViewer,outputFieldViewer,transformMapping,finalSortedList);
				
			}
		});
    	expressionComposite.getTableViewer().getControl().addMouseListener(new MouseAdapter() 
    	{
    		public void mouseUp(MouseEvent e) {
				Composite composite=(Composite)e.widget;
				AbstractExpressionComposite expressionComposite=(AbstractExpressionComposite)composite.getParent().getParent().getParent();
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(expressionComposite.getExpressionIdTextBox(),
						inputFieldTableViewer,outputFieldViewer,transformMapping,finalSortedList);
				
			}
		});
    	expressionComposite.getOutputFieldTextBox().getParent().addMouseListener(new MouseAdapter() 
    	{
    		public void mouseUp(MouseEvent e) {
				Composite composite=(Composite)e.widget;
				AbstractExpressionComposite expressionComposite=(AbstractExpressionComposite)composite.getParent().getParent();
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(expressionComposite.getExpressionIdTextBox(),
						inputFieldTableViewer,outputFieldViewer,transformMapping,finalSortedList);
				
			}
		});
    	expressionComposite.getAddButton().getParent().addMouseListener(new MouseAdapter() 
    	{
    		public void mouseUp(MouseEvent e) {
				Composite composite=(Composite)e.widget;
				AbstractExpressionComposite expressionComposite=(AbstractExpressionComposite)composite.getParent().getParent().getParent();
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(expressionComposite.getExpressionIdTextBox(),
						inputFieldTableViewer,outputFieldViewer,transformMapping,finalSortedList);
				
			}
		});
	}
	
	
	private void addListenerForRowHighlightingForOperationComposite(
			OperationClassComposite operationClassComposite ) {
		operationClassComposite.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				Composite composite=(Composite)e.widget;
				OperationClassComposite operationClassComposite=(OperationClassComposite)composite;
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(operationClassComposite.getOperationIdTextBox(),
					inputFieldTableViewer,outputFieldViewer,transformMapping,finalSortedList);
			}
		});
    	operationClassComposite.getOperationIdTextBox().getParent().addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				Composite composite=(Composite)e.widget;
				OperationClassComposite operationClassComposite=(OperationClassComposite)composite.getParent().getParent();
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(operationClassComposite.getOperationIdTextBox(),
						inputFieldTableViewer,outputFieldViewer,transformMapping,finalSortedList);
				
			}
		});
    	operationClassComposite.getInputTableViewer().getControl().addMouseListener(new MouseAdapter() 
    	{
    		public void mouseUp(MouseEvent e) {
				Composite composite=(Composite)e.widget;
				OperationClassComposite operationClassComposite=(OperationClassComposite)composite.getParent().getParent().getParent();
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(operationClassComposite.getOperationIdTextBox(),
						inputFieldTableViewer,outputFieldViewer,transformMapping,finalSortedList);
				
			}
		});
    	operationClassComposite.getOutputTableViewer().getControl().addMouseListener(new MouseAdapter() 
    	{
    		public void mouseUp(MouseEvent e) {
				Composite composite=(Composite)e.widget;
				OperationClassComposite operationClassComposite=(OperationClassComposite)composite.getParent().getParent().getParent();
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(operationClassComposite.getOperationIdTextBox(),
						inputFieldTableViewer,outputFieldViewer,transformMapping,finalSortedList);
				
			}
		});
    	operationClassComposite.getAddButtonInputTable().getParent().addMouseListener(new MouseAdapter() 
    	{
    		public void mouseUp(MouseEvent e) {
				Composite composite=(Composite)e.widget;
				OperationClassComposite operationClassComposite=(OperationClassComposite)composite.getParent().getParent().getParent();
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(operationClassComposite.getOperationIdTextBox(),
						inputFieldTableViewer,outputFieldViewer,transformMapping,finalSortedList);
				
			}
		});
    	operationClassComposite.getAddButtonOutputTable().getParent().addMouseListener(new MouseAdapter() 
    	{
    		public void mouseUp(MouseEvent e) {
				Composite composite=(Composite)e.widget;
				OperationClassComposite operationClassComposite=(OperationClassComposite)composite.getParent().getParent().getParent();
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(operationClassComposite.getOperationIdTextBox(),
						inputFieldTableViewer,outputFieldViewer,transformMapping,finalSortedList);
				
			}
		});
	}
	
	private void addControlListener(Table table) {
		table.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Table table = (Table) e.widget;
				Rectangle area = table.getClientArea();
				int totalAreaWidth = area.width;
				int diff = totalAreaWidth - (table.getColumn(0).getWidth());
				table.getColumn(0).setWidth(diff + table.getColumn(0).getWidth());
			}
		});
	}
    
	public void addInactiveMappingSheetRowObject(List<MappingSheetRow> mappingSheetRows)
	{
		List<MappingSheetRow> newMappingSheetRows=new ArrayList<>(mappingSheetRows);
	 	for(MappingSheetRow mappingSheetRow:newMappingSheetRows)
	 	{   
	 		String operationId;
	 		int n=mappingSheetRows.indexOf(mappingSheetRow);
	 		if(n==0)
	 		n++;	
	 		List<FilterProperties> inputFieldListOperationClass = new ArrayList<>();
 			List<FilterProperties> outputListOperationClass = new ArrayList<>();
 			List<NameValueProperty> nameValuePropertyOperationClass = new ArrayList<>();
	 		if(mappingSheetRow.isExpression())
	 		{
	 			operationId=Messages.OPERATION_ID_PREFIX+n;
	 			
	 			mappingSheetRowForOperationClass = new MappingSheetRow(inputFieldListOperationClass, 
	 					outputListOperationClass, operationId, Messages.CUSTOM, "",
	 					nameValuePropertyOperationClass, false, "", false, "",false,null,null,false);	
	 			transformMapping.getMappingSheetRows().add(mappingSheetRows.indexOf(mappingSheetRow),mappingSheetRowForOperationClass);
	 		}
	 		
	 		else
	 		{
	 			operationId="Expression:"+n;
	 			ExpressionEditorData expressionEditorData=new ExpressionEditorData("",component.getComponentName());
	 			ExpressionEditorData mergeExpressionEditorDataForGroupCombine=new ExpressionEditorData("",component.getComponentName());
	 	    	mappingSheetRowForExpression = new MappingSheetRow(inputFieldListOperationClass, outputListOperationClass, 
	 	    			operationId, Messages.CUSTOM, "",
	 	    			nameValuePropertyOperationClass, false, "", false, "",true,expressionEditorData,mergeExpressionEditorDataForGroupCombine,false);
	 	    	transformMapping.getMappingSheetRows().add(mappingSheetRows.indexOf(mappingSheetRow)+1,
	 	    			mappingSheetRowForExpression);	
	 		}
	 		
	 	}
	}
	private void createOperationClassGrid(Composite parentComposite) {

		Composite middleComposite = new Composite(parentComposite, SWT.NONE);
		middleComposite.setLayout(new GridLayout(1, false));
		Composite topAddButtonComposite = new Composite(middleComposite, SWT.NONE);
		GridData gd_topAddButtonComposite = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_topAddButtonComposite.heightHint = 40;
		topAddButtonComposite.setLayoutData(gd_topAddButtonComposite);
		
		middleSashForm = new SashForm(middleComposite, SWT.SMOOTH|SWT.VERTICAL|SWT.BORDER);
		middleSashForm.setSashWidth(1);
		middleSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));

		scrolledComposite = new ScrolledComposite(middleSashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		scrolledComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		scrolledComposite.setLayout(new GridLayout(1, false));

		GridData gd_scrolledComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_scrolledComposite.minimumHeight = 170;
		gd_scrolledComposite.heightHint = 200;
		scrolledComposite.setLayoutData(gd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setVisible(true);

		expandBar = new ExpandBar(scrolledComposite, SWT.NONE);
		expandBar.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		expandBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		expandBar.setVisible(true);
		expandBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		
		
		addLabel = widget.buttonWidget(topAddButtonComposite, SWT.CENTER, new int[] { 130, 10, 20, 15 }, "");
		addLabel.setImage(ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
		SchemaButtonsSyncUtility.INSTANCE.buttonSize(addLabel,43,30,30,25);
		addLabel.setToolTipText(Messages.ADD_OPERATION_CONTROL);
		
		addLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) 
		 {
				addOperations();	
		 }
		});
		
		if(OSValidator.isMac()){
			deleteLabel = widget.buttonWidget(topAddButtonComposite, SWT.CENTER, new int[] { 165, 11, 20, 15 },"");
		}else{
			deleteLabel = widget.buttonWidget(topAddButtonComposite, SWT.CENTER, new int[] { 165, 10, 20, 15 },"");
		}
		SchemaButtonsSyncUtility.INSTANCE.buttonSize(deleteLabel,44,28,30,25);
		deleteLabel.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		deleteLabel.setToolTipText(Messages.DELETE_OPERATION_CONTROL);
		deleteLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				
				if (transformMapping.getMappingSheetRows().isEmpty()) {
					WidgetUtility.errorMessage(Messages.OPERATION_LIST_EMPTY);

				} else {
					OperationClassDeleteDialog operationClassDeleteDialog = new OperationClassDeleteDialog(deleteLabel
							.getShell(), transformMapping, expandBar,getComponent());
					operationClassDeleteDialog.open();
					refreshOutputTable();
					showHideValidationMessage();
					

				}
				scrolledComposite.setMinSize(expandBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});
     
		Label lblOperationsControl = new Label(topAddButtonComposite, SWT.NONE);
		if(OSValidator.isMac()){
			lblOperationsControl.setFont(SWTResourceManager.getFont("Segoe UI", 13, SWT.NORMAL));
			lblOperationsControl.setBounds(0, 15, 129, 25);
		}else {
			lblOperationsControl.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
			lblOperationsControl.setBounds(0, 10, 129, 25);
		}
		lblOperationsControl.setText(Messages.OPERATION_CONTROL);
		
		if(OSValidator.isMac()){
			viewTransform = widget.buttonWidget(topAddButtonComposite, SWT.CENTER, new int[] {205,10,95,15}, "View Transform");
			viewTransform.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		} else{
			viewTransform = widget.buttonWidget(topAddButtonComposite, SWT.CENTER, new int[] {200,10,95,15}, "View Transform");
			viewTransform.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		}
		SchemaButtonsSyncUtility.INSTANCE.buttonSize(viewTransform,105,29,95,25);
		viewTransform.addSelectionListener(new SelectionAdapter() {
			@Override
			public void  widgetSelected(SelectionEvent e) {
				TransformViewDataDialog transformViewDataDialog = new TransformViewDataDialog(Display.getCurrent().getActiveShell());
				transformViewDataDialog.getMappingSheet(transformMapping.getMappingSheetRows(),transformMapping.getMapAndPassthroughField());
				transformViewDataDialog.open();
			}
			
		});
		
		loadExpressionAndOperationData();
		createMapAndPassthroughTable(middleSashForm);
		if(OSValidator.isMac()){
			middleSashForm.setWeights(new int[] {85, 58, 27});
			}else if(Constants.AGGREGATE.equalsIgnoreCase(component.getComponentName())||
					Constants.CUMULATE.equalsIgnoreCase(component.getComponentName())||
					Constants.GROUP_COMBINE.equalsIgnoreCase(component.getComponentName())
					){
				middleSashForm.setWeights(new int[] {65, 54, 23});
			}
			else
			{
				middleSashForm.setWeights(new int[] {56, 54, 23});
		    }	
      }
	private void loadExpressionAndOperationData() {
		if (!transformMapping.getMappingSheetRows().isEmpty()) {
			List<MappingSheetRow> activeMappingSheetRow=TransformMappingFeatureUtility.INSTANCE.
			getActiveMappingSheetRow(transformMapping.getMappingSheetRows());
			/**
			 * backward Job compatabaility code
			 */
			if(activeMappingSheetRow.size()==0)
			{
				for(MappingSheetRow mappingSheetRow:transformMapping.getMappingSheetRows())
				{
				mappingSheetRow.setActive(true);
				}
				activeMappingSheetRow=TransformMappingFeatureUtility.INSTANCE.
						getActiveMappingSheetRow(transformMapping.getMappingSheetRows());
			}	
			 /**
			  * execute if target xml imported or Job is backward
			  */
			if(activeMappingSheetRow.size()==transformMapping.getMappingSheetRows().size())
			{
			 addInactiveMappingSheetRowObject(transformMapping.getMappingSheetRows()); 
			}
            boolean isNoActiveOperationPresent=true;
			for(int i=0;i<transformMapping.getMappingSheetRows().size();i++)
			{
				if(transformMapping.getMappingSheetRows().get(i).isExpression())
				{
					mappingSheetRowForExpression=transformMapping.getMappingSheetRows().get(i);
					mappingSheetRowForOperationClass=transformMapping.getMappingSheetRows().get(i+1);
				}	
				else 
				{
					mappingSheetRowForOperationClass=transformMapping.getMappingSheetRows().get(i);
					mappingSheetRowForExpression=transformMapping.getMappingSheetRows().get(i+1);
				}
                if(Constants.NORMALIZE.equalsIgnoreCase(component.getComponentName())){
                	if(transformMapping.isExpression() &&mappingSheetRowForOperationClass.isActive())
					{
						i++;
						continue;
					}	
					else if(!transformMapping.isExpression()&&mappingSheetRowForExpression.isActive())
					{
						i++;
						continue;
					}
                }
				addExpandItem(scrolledComposite);
				if(mappingSheetRowForExpression.isActive())
				setDuplicateOperationInputFieldMap(mappingSheetRowForExpression);
				else
				setDuplicateOperationInputFieldMap(mappingSheetRowForOperationClass);	
				 i++;
				 if(mappingSheetRowForOperationClass.isActive())
				 isNoActiveOperationPresent=false;		 
			}	
			if(Constants.NORMALIZE.equalsIgnoreCase(component.getComponentName())){
				if(!transformMapping.isExpression())
				{
				if(isNoActiveOperationPresent)
				addOperations();	
				addLabel.setEnabled(false);
				deleteLabel.setEnabled(false);
				}
			}	
		}else if(Constants.NORMALIZE.equalsIgnoreCase(component.getComponentName())){
			if(!transformMapping.isExpression())
			{	
			addOperations();
			addLabel.setEnabled(false);
			deleteLabel.setEnabled(false);
			}
		}
      }
	
	private void createMapAndPassthroughTable(Composite middleComposite) {
		
		MapAndPassthroughComposite mapAndPassThroughFieldComposite =new MapAndPassthroughComposite(middleComposite, SWT.NONE);
		
		GridData gd_mappingTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_mappingTableComposite.minimumHeight=180;
		mapAndPassThroughFieldComposite.setLayoutData(gd_mappingTableComposite);
		isAllPassthrougButton=mapAndPassThroughFieldComposite.getCheckButton();
		if(transformMapping.isAllInputFieldsArePassthrough()){
			isAllPassthrougButton.setSelection(true);
		}
		
		addSelectionListenerToCheckButton(mapAndPassThroughFieldComposite.getCheckButton());
		transformMapping.getMapAndPassthroughField();
		mappingTableViewer=mapAndPassThroughFieldComposite.getTableViewer();
		setTableViewer(mappingTableViewer, mapAndPassThroughFieldComposite, new String[] { Messages.SOURCE, Messages.TARGET },
				new ELTFilterContentProvider());
		mappingTableViewer.setLabelProvider(new PropertyLabelProvider());
		mappingTableViewer.setCellModifier(new PropertyGridCellModifier(this, mappingTableViewer));
		mappingTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		mappingTableViewer.setInput(transformMapping.getMapAndPassthroughField());
        mappingTableViewer.getControl().addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(null,
						inputFieldTableViewer,outputFieldViewer,null,null);
			}
		});
		CellEditor[] editor=mappingTableViewer.getCellEditors();
		
		for(int i=0;i<=1;i++)
		{	
		fieldNameDecorator = WidgetUtility.addDecorator(editor[i].getControl(),Messages.FIELDNAME_SHOULD_NOT_BE_BLANK);
		isFieldNameAlphanumericDecorator=WidgetUtility.addDecorator(editor[i].getControl(),Messages.FIELDNAME_NOT_ALPHANUMERIC_ERROR);	
		editor[i].setValidator(new TransformCellEditorFieldValidator(fieldNameDecorator,isFieldNameAlphanumericDecorator));
		if(i==0)
		{	
		isFieldNameAlphanumericDecorator.setMarginWidth(8);
		fieldNameDecorator.setMarginWidth(8);
		}
		}
		
		mappingTableViewer.getTable().addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Table table = (Table) e.widget;
				int columnCount = table.getColumnCount();

				Rectangle area = table.getClientArea();
				int totalAreaWidth = area.width;
				table.getColumn(0).setWidth(area.width / 2);
				int lineWidth = table.getGridLineWidth();
				int totalGridLineWidth = (2 - 1) * lineWidth;
				int totalColumnWidth = 0;
				for (TableColumn column : table.getColumns()) {
					totalColumnWidth = totalColumnWidth + column.getWidth();
				}
				int diff = totalAreaWidth - (totalColumnWidth + totalGridLineWidth);

				TableColumn lastCol = table.getColumns()[columnCount - 1];
				lastCol.setWidth(diff + lastCol.getWidth());

			}
		});
		
		 mappingTableViewer.getTable().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					addNewRowForMappingField();	
				}
		 });

		DragDropUtility.INSTANCE.applyDrop(mappingTableViewer,
				new DragDropTransformOpImp(this, transformMapping,null, false,false,
						mappingTableViewer));

		Button mapFieldAddLabel = mapAndPassThroughFieldComposite.getAddButton();
		mapFieldAddLabel.setImage(ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
		mapFieldAddLabel.setToolTipText(Messages.ADD_SCHEMA_TOOLTIP);
		mapFieldAddLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				addNewRowForMappingField();
			}
		});

		Button mapFieldDeletLabel = mapAndPassThroughFieldComposite.getDeleteButton();
		mapFieldDeletLabel.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		mapFieldDeletLabel.setToolTipText(Messages.DELETE_SCHEMA_TOOLTIP);

		mapFieldDeletLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				Table table = mappingTableViewer.getTable();
				int temp = table.getSelectionIndex();
				int[] indexs = table.getSelectionIndices();
				if (temp == -1) {
					WidgetUtility.errorMessage(Messages.SelectRowToDelete);
				} 
				else 
				{
					table.remove(indexs);
					List<NameValueProperty> tempList = new ArrayList<NameValueProperty >();
					for (int index : indexs) {
						tempList.add(transformMapping.getMapAndPassthroughField().get(index));
					}
					
					for(NameValueProperty nameValueProperty:tempList)
					{	
						int index=-1;	
						for(int j=0;j<transformMapping.getOutputFieldList().size();j++)
						{
							
							if(transformMapping.getOutputFieldList().get(j)==nameValueProperty.getFilterProperty())
							{
								index=j;
								break;
							}	
						}
						if(index!=-1)
						transformMapping.getOutputFieldList().remove(index);
						transformMapping.getMapAndPassthroughField().remove(nameValueProperty);
					}
					TransformMappingFeatureUtility.INSTANCE.setCursorOnDeleteRow(mappingTableViewer, indexs,
                            transformMapping.getMapAndPassthroughField());
					refreshOutputTable();
					showHideValidationMessage();
				}
			}
		});

		errorComposite = new Composite(middleComposite, SWT.NONE);
		errorComposite.setLayout(new GridLayout(1, false));
		GridData errorCompositeData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		errorCompositeData.minimumHeight=60;
		
		
		errorComposite.setLayoutData(errorCompositeData);

		errorTableViewer = new TableViewer(errorComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		setTableViewer(errorTableViewer, errorComposite, new String[] { "Error Log" }, new ELTFilterContentProvider());
		errorTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		errorTableViewer.setLabelProvider(new ErrorLabelProvider());
		errorTableViewer.setInput(errorLabelList);
		errorTableViewer.getControl().addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				TransformMappingFeatureUtility.INSTANCE.highlightInputAndOutputFields(null,
						inputFieldTableViewer,outputFieldViewer,null,null);
			}
		});
        addControlListener(errorTableViewer.getTable());
	}

	private void addSelectionListenerToCheckButton(Button checkButton) {
		checkButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button=(Button)e.widget;
				if(button.getSelection()){
					transformMapping.setAllInputFieldsArePassthrough(true);
					for(InputField inputField:transformMapping.getInputFields()){
						NameValueProperty nameValueProperty=new NameValueProperty();
						nameValueProperty.getFilterProperty().setPropertyname(inputField.getFieldName());
						nameValueProperty.setPropertyName(inputField.getFieldName());
						nameValueProperty.setPropertyValue(inputField.getFieldName());
						if(!transformMapping.getMapAndPassthroughField().contains(nameValueProperty)){
							transformMapping.getMapAndPassthroughField().add(nameValueProperty);
							transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());
							refreshOutputTable();
							showHideValidationMessage();
						}
					}
				}else{
					transformMapping.setAllInputFieldsArePassthrough(false);
				}
			}
		});
	}

	private void addNewRowForMappingField() {
		NameValueProperty nameValueProperty = new NameValueProperty();
		nameValueProperty.setPropertyName("");
		nameValueProperty.setPropertyValue("");
		if (!transformMapping.getMapAndPassthroughField().contains(nameValueProperty)) {
			transformMapping.getMapAndPassthroughField().add(nameValueProperty);
			mappingTableViewer.refresh();
			int currentSize = transformMapping.getMapAndPassthroughField().size();
			int i = currentSize == 0 ? currentSize : currentSize - 1;
			mappingTableViewer.editElement(mappingTableViewer.getElementAt(i), 0);
			component.setLatestChangesInSchema(false);
		}
}

	private void addExpandItem(final ScrolledComposite scrollBarComposite) 
	{
		final ExpandItem expandItem = new ExpandItem(expandBar, SWT.V_SCROLL);
		expandItem.setExpanded(true);
		if (OSValidator.isMac()) {
			if (Constants.AGGREGATE.equalsIgnoreCase(component.getComponentName())
					|| Constants.CUMULATE.equalsIgnoreCase(component.getComponentName())) {
				expandItem.setHeight(415);
			} else if (Constants.GROUP_COMBINE.equalsIgnoreCase(component.getComponentName())) {
				expandItem.setHeight(460);
			} else {
				expandItem.setHeight(362);
			}
		} else if (Constants.AGGREGATE.equalsIgnoreCase(component.getComponentName())
				|| Constants.CUMULATE.equalsIgnoreCase(component.getComponentName())) {
			expandItem.setHeight(409);
		} else if (Constants.GROUP_COMBINE.equalsIgnoreCase(component.getComponentName())) {
			expandItem.setHeight(450);
		} else {
			expandItem.setHeight(350);
		}
		expandBar.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				scrollBarComposite.setMinSize(expandBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});
		final OperationClassComposite operationClassComposite = createOperationClassComposite(expandItem);
		final AbstractExpressionComposite expressionComposite = createExpressionComposite(expandItem,
				operationClassComposite);
		if (mappingSheetRowForExpression.isActive()) {
			expandItem.setControl(expressionComposite);
			expandItem.setText(mappingSheetRowForExpression.getOperationID());
		} else if (mappingSheetRowForOperationClass.isActive()) {
			expandItem.setControl(operationClassComposite);
			expandItem.setText(mappingSheetRowForOperationClass.getOperationID());

		}
		if (!Constants.NORMALIZE.equalsIgnoreCase(component.getComponentName())) {
			attachListenerOnSwitchToClassButton(expandItem, operationClassComposite, expressionComposite);
			attachListenerOnSwitchToExpressiomButton(expandItem, operationClassComposite, expressionComposite);
		}
		showHideValidationMessage();
		scrollBarComposite.setContent(expandBar);
		scrollBarComposite.setMinSize(expandBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void attachListenerOnSwitchToExpressiomButton(final ExpandItem expandItem,
			final OperationClassComposite operationClassComposite,
			final AbstractExpressionComposite expressionComposite) {
		operationClassComposite.getSwitchToExpressionButton().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button toggleButton = (Button) e.widget;
				if (toggleButton.getSelection()) {
					toggleButton.setSelection(false);
					expandItem.setControl(expressionComposite);
					expandItem.setText(expressionComposite.getExpressionIdTextBox().getText());
					MappingSheetRow mappingSheetRowForExpressionClass = (MappingSheetRow) expressionComposite
							.getData(Messages.MAPPING_SHEET);
					MappingSheetRow mappingSheetRowForOperationClass = (MappingSheetRow) operationClassComposite
							.getBrowseButton().getData(Messages.MAPPING_SHEET);
					mappingSheetRowForOperationClass.setActive(false);
					mappingSheetRowForExpressionClass.setActive(true);
					expressionComposite.getSwitchToExpressionButton().setSelection(true);
					removeExpressionOrOperationOutputFieldFromOutputList(mappingSheetRowForOperationClass);
					transformMapping.getOutputFieldList().addAll(mappingSheetRowForExpressionClass.getOutputList());
					expressionComposite.setVisible(true);
					operationClassComposite.setVisible(false);
					setDuplicateOperationInputFieldMap(mappingSheetRowForExpressionClass);
					refreshOutputTable();
					showHideValidationMessage();
					if (Constants.AGGREGATE.equalsIgnoreCase(component.getComponentName())
							|| Constants.TRANSFORM.equalsIgnoreCase(component.getComponentName())
							|| Constants.CUMULATE.equalsIgnoreCase(component.getComponentName())) {
//						expandItem.setHeight(289);
//						middleSashForm.setWeights(new int[] { 65, 54, 23 });
						scrolledComposite.setMinSize(expandBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					} else if (Constants.GROUP_COMBINE.equalsIgnoreCase(component.getComponentName())) {
//						expandItem.setHeight(340);
//						middleSashForm.setWeights(new int[] { 85, 44, 23 });
						scrolledComposite.setMinSize(expandBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					}
				}
			}
		});
	}

	private void attachListenerOnSwitchToClassButton(
			final ExpandItem expandItem,
			final OperationClassComposite operationClassComposite,
			final AbstractExpressionComposite expressionComposite) {
		expressionComposite.getSwitchToClassButton().addSelectionListener(new SelectionAdapter() {
 			@Override
 			public void widgetSelected(SelectionEvent e) 
 			{
 				Button toggleButton=(Button)e.widget;
 				if(toggleButton.getSelection())
 				{
 					toggleButton.setSelection(false);
 					expandItem.setControl(operationClassComposite);
 					expandItem.setText(operationClassComposite.getOperationIdTextBox().getText());
 					MappingSheetRow mappingSheetRowForExpressionClass=(MappingSheetRow)expressionComposite.getData(Messages.MAPPING_SHEET);
 					MappingSheetRow mappingSheetRowForOperationClass=
 							(MappingSheetRow)operationClassComposite.getBrowseButton().getData(Messages.MAPPING_SHEET);
 					removeExpressionOrOperationOutputFieldFromOutputList(mappingSheetRowForExpressionClass);
 					transformMapping.getOutputFieldList().addAll(mappingSheetRowForOperationClass.getOutputList());
 					mappingSheetRowForExpressionClass.setActive(false);
 					mappingSheetRowForOperationClass.setActive(true);
 					operationClassComposite.getSwitchToClassButton().setSelection(true);
 					expressionComposite.setVisible(false);
 					operationClassComposite.setVisible(true);
 					setDuplicateOperationInputFieldMap(mappingSheetRowForOperationClass);
 					refreshOutputTable();
 					showHideValidationMessage();
 					if(Constants.AGGREGATE.equalsIgnoreCase(component.getComponentName())||
 		    	     	   Constants.TRANSFORM.equalsIgnoreCase(component.getComponentName()) ||
 		    	     	 Constants.GROUP_COMBINE.equalsIgnoreCase(component.getComponentName()))
 		    	     	{
// 		    	     		expandItem.setHeight(220);
// 		    	     		middleSashForm.setWeights(new int[] {54, 59, 25});
 		    	     		scrolledComposite.setMinSize(expandBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
 		    	     	}
    				}	
 			}
 		});
	}

	private AbstractExpressionComposite createExpressionComposite(
			final ExpandItem expandItem,
			final OperationClassComposite operationClassComposite) {
		final AbstractExpressionComposite expressionComposite=ExpressionCompositeFactory.INSTANCE.
				getComposite(expandBar,mappingSheetRowForExpression,getComponent(),widgetConfig);
		expressionComposite.setTransformDialog(this);
		expressionComposite.setTransformMapping(transformMapping);

		expressionComposite.getExpressionIdTextBox().setText(mappingSheetRowForExpression.getOperationID());
     	expressionComposite.getExpressionIdTextBox().setData(expandItem);
     	expressionComposite.setData(Messages.MAPPING_SHEET, mappingSheetRowForExpression);
     	OperationClassConfig configurationForTransformWidget;
     	configurationForTransformWidget = (OperationClassConfig) widgetConfig;
     	if (StringUtils.equalsIgnoreCase(Constants.AGGREGATE, configurationForTransformWidget.getComponentName())
				|| StringUtils.equalsIgnoreCase(Constants.CUMULATE,configurationForTransformWidget.getComponentName())
				|| StringUtils.equalsIgnoreCase(Constants.GROUP_COMBINE,configurationForTransformWidget.getComponentName())) {
     	addModifyListenerToComboDataTypes(expressionComposite.getComboDataTypes(),expressionComposite,mappingSheetRowForExpression);
     	addModifyListenerToAccumulator(expressionComposite.getTextAccumulator(),expressionComposite,mappingSheetRowForExpression);
     	addModifyListnerToAccumulatorIsParameter(expressionComposite.getIsParamAccumulator(),mappingSheetRowForExpression);
    	if(StringUtils.isNotBlank(mappingSheetRowForExpression.getAccumulator()))
			expressionComposite.getTextAccumulator().setText(mappingSheetRowForExpression.getAccumulator());
     	}
     	addListenerForRowHighlighting(expressionComposite);
     	addModifyListenerToOperationClassAndExpressionTextBox(expressionComposite.getExressionTextBox());
     	if(StringUtils.equalsIgnoreCase(Constants.GROUP_COMBINE,configurationForTransformWidget.getComponentName())){
     	addModifyListenerToOperationClassAndExpressionTextBox(expressionComposite.getExressionTextBox2());
     	}
     	attachModifyListenerToIdTextBox(mappingSheetRowForExpression, expressionComposite.getExpressionIdTextBox());
     	attachFocusListenerToIdTextBox(expressionComposite.getExpressionIdTextBox());
     	addIsParamSelectionListener(expressionComposite.getIsParamButton(), mappingSheetRowForExpression);
     	operationalInputFieldTableViewer=expressionComposite.getTableViewer();
     	operationalInputFieldTableViewer = setTableViewer(operationalInputFieldTableViewer,
     			 operationalInputFieldTableViewer.getControl().getParent(), new String[] {Messages.INNER_OPERATION_INPUT_FIELD },
 				new ELTFilterContentProvider());
		if (OSValidator.isMac()) {
			operationalInputFieldTableViewer.getTable().getColumn(0).setWidth(147);
		} else {
			operationalInputFieldTableViewer.getTable().getColumn(0).setWidth(145);
		}
     	 operationalInputFieldTableViewer.setInput(mappingSheetRowForExpression.getInputFields());
     	 operationalInputFieldTableViewer.setCellModifier(new ELTCellModifier(operationalInputFieldTableViewer,
     			 this,mappingSheetRowForExpression));
     	 operationalInputFieldTableViewer.setLabelProvider(new ELTFilterLabelProvider());
     	 attachFocuListenerToParamaterTextBox(expressionComposite.getParameterTextBox());
     	 addModifyAndFocusListenerToOutputField(expressionComposite.getOutputFieldTextBox(),mappingSheetRowForExpression);
          DragDropTransformOpImp dragDropTransformOpImpnew = new DragDropTransformOpImp
        		  (this,transformMapping,mappingSheetRowForExpression.getInputFields(),true,true,operationalInputFieldTableViewer,expressionComposite);
  		 DragDropUtility.INSTANCE.applyDrop(operationalInputFieldTableViewer, dragDropTransformOpImpnew);
  		 intializeFunctionalityToExpressionWidget(expressionComposite,mappingSheetRowForExpression,operationalInputFieldTableViewer);
  		 operationalInputTableDoubleClick(mappingSheetRowForExpression,operationalInputFieldTableViewer);  
  		 return expressionComposite;
	}

	private void addModifyListnerToAccumulatorIsParameter(Button isParamAccumulator,
			MappingSheetRow mappingSheetRow) {
		
			isParamAccumulator.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					mappingSheetRow.setAccumulatorParameter(isParamAccumulator.getSelection());
					showHideValidationMessage();
				}
			});
	}
	private void addModifyListenerToComboDataTypes(Combo combo,final AbstractExpressionComposite expressionComposite,MappingSheetRow mappingSheetRow) {
		combo.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent e) {
				Combo accumulatorDataType =(Combo)e.widget;
				mappingSheetRow.setComboDataType( accumulatorDataType.getText());
				boolean isValidValue = validate(expressionComposite.getTextAccumulator().getText(),accumulatorDataType.getText());
				if(!isValidValue){
					expressionComposite.getTextAccumulator().setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255,255,000));
				}else{
					expressionComposite.getTextAccumulator().setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255,255,255));
				}
				showHideValidationMessage();
			}
		});
	}

	private void addModifyListenerToAccumulator(Text text,final AbstractExpressionComposite expressionComposite,MappingSheetRow mappingSheetRow) {
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Text accumulatorTextBox=(Text)e.widget;
				mappingSheetRow.setAccumulator(accumulatorTextBox.getText());
				boolean isValidValue = validate(accumulatorTextBox.getText(),expressionComposite.getComboDataTypes().getText());
				if(!isValidValue && (!expressionComposite.getIsParamAccumulator().getSelection()||StringUtils.isBlank(accumulatorTextBox.getText()))){
					expressionComposite.getTextAccumulator().setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255,255,000));
				}else{
					expressionComposite.getTextAccumulator().setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255,255,255));
				}
				showHideValidationMessage();
			}
		});
	}

	private OperationClassComposite createOperationClassComposite(
			final ExpandItem expandItem) {
		final OperationClassComposite operationClassComposite=new OperationClassComposite(this,expandBar, 
    			SWT.NONE,mappingSheetRowForOperationClass,getComponent(),widgetConfig,transformMapping);
		expandItem.setData(OUTPUT_TABLE_VIEWER, operationClassComposite.getOutputTableViewer());
		operationClassComposite.getOperationIdTextBox().setText(mappingSheetRowForOperationClass.getOperationID());
		operationClassComposite.getOperationIdTextBox().setData(expandItem); 
		addListenerForRowHighlightingForOperationComposite(operationClassComposite);
		addModifyListenerToOperationClassAndExpressionTextBox(operationClassComposite.getOperationTextBox());
		attachModifyListenerToIdTextBox(mappingSheetRowForOperationClass, operationClassComposite.getOperationIdTextBox());
		attachFocusListenerToIdTextBox(operationClassComposite.getOperationIdTextBox());
		addIsParamSelectionListenerForOperationClassWidget(operationClassComposite.getBtnIsParam(), mappingSheetRowForOperationClass);
		operationalInputFieldTableViewer=operationClassComposite.getInputTableViewer();
    	operationalInputFieldTableViewer = setTableViewer(operationalInputFieldTableViewer,
    			 operationalInputFieldTableViewer.getControl().getParent(), new String[] {Messages.INNER_OPERATION_INPUT_FIELD },
				new ELTFilterContentProvider());
    	if (OSValidator.isMac()) {
			operationalInputFieldTableViewer.getTable().getColumn(0).setWidth(147);
		} else {
			operationalInputFieldTableViewer.getTable().getColumn(0).setWidth(145);
		}
    	 operationalInputFieldTableViewer.setInput(mappingSheetRowForOperationClass.getInputFields());
    	 operationalInputFieldTableViewer.setCellModifier(new ELTCellModifier(operationalInputFieldTableViewer, this,
    			 mappingSheetRowForOperationClass));
    	 operationalInputFieldTableViewer.setLabelProvider(new ELTFilterLabelProvider());
    	 CellEditor[] editor=operationalInputFieldTableViewer.getCellEditors();
 		 fieldNameDecorator = WidgetUtility.addDecorator(editor[0].getControl(),Messages.FIELDNAME_SHOULD_NOT_BE_BLANK);
 		 isFieldNameAlphanumericDecorator=WidgetUtility.addDecorator(editor[0].getControl(),Messages.FIELDNAME_NOT_ALPHANUMERIC_ERROR);	
 		 editors[0].setValidator(new TransformCellEditorFieldValidator(fieldNameDecorator,isFieldNameAlphanumericDecorator));
		 isFieldNameAlphanumericDecorator.setMarginWidth(8);
		 fieldNameDecorator.setMarginWidth(8);
 		 
 		 operationalOutputFieldTableViewer=operationClassComposite.getOutputTableViewer();
    	 operationalOutputFieldTableViewer = setTableViewer(operationalOutputFieldTableViewer,
    			 operationalOutputFieldTableViewer.getControl().getParent(), new String[] {Messages.INNER_OPERATION_OUTPUT_FIELD },
				new ELTFilterContentProvider());
    	 if (OSValidator.isMac()) {
    		 operationalOutputFieldTableViewer.getTable().getColumn(0).setWidth(147);
 		} else {
 			operationalOutputFieldTableViewer.getTable().getColumn(0).setWidth(145);
 		}
    	 operationalOutputFieldTableViewer.setCellModifier(new ELTCellModifier(operationalOutputFieldTableViewer, this));
    	 operationalOutputFieldTableViewer.setInput(mappingSheetRowForOperationClass.getOutputList());
    	 operationalOutputFieldTableViewer.setLabelProvider(new ELTFilterLabelProvider());
    	 
    	 CellEditor[] operationalOutputFieldEditor=operationalOutputFieldTableViewer.getCellEditors();
    	 ControlDecoration operationalOutputfieldDecorator = WidgetUtility.addDecorator(operationalOutputFieldEditor[0].getControl(),Messages.FIELDNAME_SHOULD_NOT_BE_BLANK);
    	 ControlDecoration operationalOutputfieldAlphaNumeric = WidgetUtility.addDecorator(operationalOutputFieldEditor[0].getControl(),Messages.FIELDNAME_NOT_ALPHANUMERIC_ERROR);	
    	 operationalOutputFieldEditor[0].setValidator(new TransformCellEditorFieldValidator(operationalOutputfieldDecorator,operationalOutputfieldAlphaNumeric));
    	 operationalOutputfieldAlphaNumeric.setMarginWidth(8);
    	 operationalOutputfieldDecorator.setMarginWidth(8);
    	 
    	 attachFocuListenerToParamaterTextBox(operationClassComposite.getParameterTextBox());
    	 addSelectionListenerToBrowseButton(operationClassComposite);
    	 operationClassComposite.getBrowseButton().setData(Messages.MAPPING_SHEET,mappingSheetRowForOperationClass);
    	 operationClassComposite.getBrowseButton().setData(OPERATION_CLASS_TEXT_BOX, operationClassComposite.getOperationTextBox());
    	 DragDropTransformOpImp dragDropTransformOpImpnew1 = new DragDropTransformOpImp(this,
				 temporaryOutputFieldMap,
				 mappingSheetRowForOperationClass.getOutputList(), mappingSheetRowForOperationClass.getInputFields(), true,
				operationalInputFieldTableViewer, operationalOutputFieldTableViewer,transformMapping.getOutputFieldList());
 		 DragDropUtility.INSTANCE.applyDrop(operationalInputFieldTableViewer, dragDropTransformOpImpnew1);
    	 intializeFunctionalityToOperationClassWidget
    	 (operationClassComposite, mappingSheetRowForOperationClass, operationalInputFieldTableViewer, operationalOutputFieldTableViewer);
    	 operationClassComposite.setVisible(false);
    	 operationalOutputTableDoubleClick(mappingSheetRowForOperationClass,operationalOutputFieldTableViewer); 
    	 operationalInputTableDoubleClick(mappingSheetRowForOperationClass,operationalInputFieldTableViewer);  
    	 return operationClassComposite;
	}
	
	private void operationalOutputTableDoubleClick(final MappingSheetRow mappingSheetRow,final TableViewer tableViewer) {
		tableViewer.getTable().addMouseListener(new MouseAdapter() {
 			@Override
 			public void mouseDoubleClick(MouseEvent e) {
 				operationOutputTableAddButton(mappingSheetRow,tableViewer);
 			}
 			@Override
 			public void mouseDown(MouseEvent e) {
 			}
 		});
	}

	private void operationalInputTableDoubleClick(final MappingSheetRow mappingSheetRow,final TableViewer tableViewer) {
		tableViewer.getTable().addMouseListener(new MouseAdapter() {
  			@Override
  			public void mouseDoubleClick(MouseEvent e) {
  				operationInputTableAddButton(mappingSheetRow,tableViewer);
  			}

  			@Override
  			public void mouseDown(MouseEvent e) {
  			}
  		});
	}
	
	private void removeExpressionOrOperationOutputFieldFromOutputList(
			MappingSheetRow mappingSheetRowForExpressionClass) {
		for(FilterProperties expressionOutputField:mappingSheetRowForExpressionClass.getOutputList())
		{
			int index=-1;	
			for(int j=0;j<transformMapping.getOutputFieldList().size();j++)
			{
				if(transformMapping.getOutputFieldList().get(j)==expressionOutputField)
				{
					index=j;
					break;
				}	
			}
			if(index!=-1)
			transformMapping.getOutputFieldList().remove(index);
			}
			
			
		}
		
	
	private void addSelectionListenerToBrowseButton(
			final OperationClassComposite operationClassComposite) {
		operationClassComposite.getBrowseButton().addSelectionListener(new SelectionAdapter() {
    			
 			@Override
 			public void widgetSelected(SelectionEvent e) {
 				MappingSheetRow orignalMappingSheetRow = (MappingSheetRow) ((Button) e.widget).getData(Messages.MAPPING_SHEET);
 				Text operationClassTextBox=(Text)((Button)e.widget).getData(OPERATION_CLASS_TEXT_BOX);
 				MappingSheetRow oldMappingSheetRow = (MappingSheetRow) orignalMappingSheetRow.clone();
 				OperationClassDialog operationClassDialog = new OperationClassDialog(operationClassComposite.getBrowseButton().getShell(), component
 						.getComponentName(), orignalMappingSheetRow, propertyDialogButtonBar, widgetConfig,
 						transformDialog);
 				operationClassDialog.open();
 				orignalMappingSheetRow.setComboBoxValue(operationClassDialog.getMappingSheetRow().getComboBoxValue());
 				orignalMappingSheetRow.setOperationClassPath(operationClassDialog.getMappingSheetRow()
 						.getOperationClassPath());
 				orignalMappingSheetRow.setClassParameter(operationClassDialog.getMappingSheetRow().isClassParameter());
                
 				orignalMappingSheetRow.setOperationClassFullPath(operationClassDialog.getMappingSheetRow()
 						.getOperationClassFullPath());
 				operationClassTextBox.setText(operationClassDialog.getMappingSheetRow().getOperationClassPath());
 				
 				if (operationClassDialog.isCancelPressed() && (!(operationClassDialog.isApplyPressed()))) {
 					orignalMappingSheetRow.setNameValueProperty(oldMappingSheetRow.getNameValueProperty());
 				}
 				if (operationClassDialog.isNoPressed())
 					pressCancel();
 				if (operationClassDialog.isYesPressed())
 					pressOK();
 				super.widgetSelected(e);
 			}

 		});
	}
	
	private void intializeFunctionalityToOperationClassWidget
	(OperationClassComposite operationClassComposite,MappingSheetRow mappingSheetRow,
			TableViewer operationalInputFieldTableViewer,
			TableViewer operationOutputFieldTableViewer)
	{
		Button inputddButton=operationClassComposite.getAddButtonInputTable();
		addButtonListener(mappingSheetRow,operationalInputFieldTableViewer,inputddButton);
		Button inputDeleteButton=operationClassComposite.getDeletButtonInputTable();
		deleteButtonListener(mappingSheetRow, operationalInputFieldTableViewer, inputDeleteButton);
		Button outputAddButton=operationClassComposite.getAddButtonOutputTable();
		Button outputDeleteButton=operationClassComposite.getDeletButtonOutputTable();
		addButtonListenerForOperationClassComposite(mappingSheetRow, operationOutputFieldTableViewer, outputAddButton);
		deleteButtonListenerForOperationClassWidget(mappingSheetRow, operationOutputFieldTableViewer, outputDeleteButton);
	}
	
	
	
	
	private void intializeFunctionalityToExpressionWidget
	(AbstractExpressionComposite expressionComposite,MappingSheetRow mappingSheetRow,TableViewer operationalInputFieldTableViewer)
	{
		Button addButton=expressionComposite.getAddButton();
		addButtonListener(mappingSheetRow,operationalInputFieldTableViewer,addButton);
		Button deleteButton=expressionComposite.getDeletButton();
		deleteButtonListener(mappingSheetRow, operationalInputFieldTableViewer, deleteButton);
		operationalInputFieldTableViewer.setData(AbstractExpressionComposite.EXPRESSION_COMPOSITE_KEY, expressionComposite);
	}

	private void addModifyListenerToOperationClassAndExpressionTextBox(Text textBox) {
		textBox.addModifyListener(new ModifyListener() {
		   @Override
			public void modifyText(ModifyEvent e) {
			 showHideValidationMessage();
			}
		});
	}

	private void attachFocusListenerToIdTextBox(Text operationIDTextBox) {
		operationIDTextBox.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				Text textBox = (Text) e.widget;
				if(StringUtils.isBlank(textBox.getText()))
				{
					textBox.setText((String) textBox.getData(Messages.PERVIOUS_VALUE));
				}	
				showHideValidationMessage();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				Text textBox=(Text) e.widget;
				textBox.setData(Messages.PERVIOUS_VALUE,textBox.getText());
			}
		});
	}


	private void attachModifyListenerToIdTextBox(
			final MappingSheetRow mappingSheetRow,Text operationIDTextBox) {
		operationIDTextBox.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text textBox = (Text) e.widget;

				ExpandItem expandItem = (ExpandItem) textBox.getData();

				expandItem.setText(textBox.getText());
				mappingSheetRow.setOperationID(textBox.getText());
			}
		});
	}

	private void attachFocuListenerToParamaterTextBox(Text text) {
		text.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				Text textBox=(Text)e.widget;
				String parameterText=textBox.getText();
				parameterText=StringUtils.replace(StringUtils.replace(parameterText,Constants.PARAMETER_PREFIX , ""),Constants.PARAMETER_SUFFIX,"");
				textBox.setText(Constants.PARAMETER_PREFIX+parameterText+Constants.PARAMETER_SUFFIX);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				Text textBox=(Text)e.widget;
				String parameterText=textBox.getText();
				parameterText=StringUtils.replace(StringUtils.replace(parameterText, Constants.PARAMETER_PREFIX, ""),Constants.PARAMETER_SUFFIX,"");
				textBox.setText(parameterText);
			}
		});
	}
	private void deleteButtonListenerForOperationClassWidget(
			final MappingSheetRow mappingSheetRow,
			final TableViewer operationOutputtableViewer, Button deleteLabel) {
		deleteLabel.setToolTipText(Messages.DELETE_SCHEMA_TOOLTIP);
		deleteLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Table table = operationOutputtableViewer.getTable();
				int temp = table.getSelectionIndex();
				int[] indexs = table.getSelectionIndices();
				mappingSheetRow.getOutputList().size();
				if (temp == -1) {
					WidgetUtility.errorMessage(Messages.SelectRowToDelete);
				} else {
					table.remove(indexs);

					List<FilterProperties> tempList = new ArrayList<FilterProperties>();
					for (int index : indexs) {
						tempList.add(mappingSheetRow.getOutputList().get(index));
					}
					for(FilterProperties filterProperties: tempList)
					{	
					mappingSheetRow.getOutputList().remove(filterProperties);
					int index=-1;	
					for(int j=0;j<transformMapping.getOutputFieldList().size();j++)
					{
						if(transformMapping.getOutputFieldList().get(j)==filterProperties)
						{
							index=j;
							break;
						}	
					}
					if(index!=-1)
					transformMapping.getOutputFieldList().remove(index);
					
					}
					TransformMappingFeatureUtility.INSTANCE.setCursorOnDeleteRow(operationOutputtableViewer, indexs,
                            mappingSheetRow.getOutputList());
					refreshOutputTable();
					showHideValidationMessage();
				}
			}

		});
	}

	private void addButtonListenerForOperationClassComposite(
			final MappingSheetRow mappingSheetRow,
			final TableViewer operationOutputtableViewer, Button addLabel) {
		addLabel.setToolTipText(Messages.ADD_SCHEMA_TOOLTIP);
		addLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				operationOutputTableAddButton(mappingSheetRow,
						operationOutputtableViewer);
				}
		});
	}
	
	private void operationOutputTableAddButton(
			final MappingSheetRow mappingSheetRow,
			final TableViewer operationOutputtableViewer) {
		FilterProperties f = new FilterProperties();
		f.setPropertyname("");

		if (!mappingSheetRow.getOutputList().contains(f)) {

			mappingSheetRow.getOutputList().add(f);
			operationOutputtableViewer.refresh();
			int i = mappingSheetRow.getOutputList().size() == 0 ? mappingSheetRow.getOutputList().size()
					: mappingSheetRow.getOutputList().size() - 1;
			operationalOutputFieldTableViewer.editElement(operationOutputtableViewer.getElementAt(i), 0);
			component.setLatestChangesInSchema(false);
		}
	}
	
    private void addIsParamSelectionListenerForOperationClassWidget(Button btnIsParam,final MappingSheetRow mappingSheetRow)
    {
    	btnIsParam.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Button text = (Button) e.widget;
				Text parameterTextBox = (Text) text.getData(PARAMETER_TEXT_BOX);
				TableViewer operationInputFieldTableViewer = (TableViewer) text
						.getData(OPERATION_INPUT_FIELD_TABLE_VIEWER);
				TableViewer operationOutputFieldTableViewer = (TableViewer) text
						.getData(OPERATION_OUTPUT_FIELD_TABLE_VIEWER);
				
				Text operationClassTextBox = (Text) text.getData(OPERATION_CLASS_TEXT_BOX);
				Text operationIDTextBox = (Text) text.getData(OPERATION_ID_TEXT_BOX);
				Button btnNewButton = (Button) text.getData(BTN_NEW_BUTTON);
				Button inputAdd = (Button) text.getData(INPUT_ADD_BUTTON);
				Button inputDelete = (Button) text.getData(INPUT_DELETE_BUTTON);
				Button outputAdd = (Button) text.getData(OUTPUT_ADD_BUTTON);
				Button outputDelete = (Button) text.getData(OUTPUT_DELETE_BUTTON);

				if (text.getSelection()) {
					if (WidgetUtility.eltConfirmMessage(Messages.ALL_DATA_WILL_BE_LOST_DO_YOU_WISH_TO_CONTINUE)) {
						mappingSheetRow.setWholeOperationParameter(text.getSelection());
						parameterTextBox.setEnabled(true);

						operationInputFieldTableViewer.getTable().setEnabled(false);
						operationInputFieldTableViewer.getTable().clearAll();
						operationOutputFieldTableViewer.getTable().setEnabled(false);
						operationOutputFieldTableViewer.getTable().clearAll();
						
						operationClassTextBox.setEnabled(false);
						operationClassTextBox.setText("");
						operationIDTextBox.setEnabled(false);

						btnNewButton.setEnabled(false);
						outputAdd.setEnabled(false);
						outputDelete.setEnabled(false);
						inputAdd.setEnabled(false);
						inputDelete.setEnabled(false);
						transformMapping.getOutputFieldList().removeAll(mappingSheetRow.getOutputList());
				        mappingSheetRow.getOutputList().clear();
						mappingSheetRow.getInputFields().clear();
						mappingSheetRow.setComboBoxValue(Messages.CUSTOM);
						mappingSheetRow.getNameValueProperty().clear();
						mappingSheetRow.setClassParameter(false);
						mappingSheetRow.setOperationClassPath("");
						refreshOutputTable();
						showHideValidationMessage();
                   
					} else
						text.setSelection(false);
				} else {
					parameterTextBox.setText("");
					mappingSheetRow.setWholeOperationParameter(text.getSelection());
					parameterTextBox.setEnabled(false);

					operationInputFieldTableViewer.getTable().setEnabled(true);
					operationInputFieldTableViewer.refresh();
					operationOutputFieldTableViewer.getTable().setEnabled(true);
					operationOutputFieldTableViewer.refresh();
					operationClassTextBox.setEnabled(true);
					operationClassTextBox.setText("");
					operationIDTextBox.setEnabled(true);

					btnNewButton.setEnabled(true);
					inputAdd.setEnabled(true);
					inputDelete.setEnabled(true);
					outputAdd.setEnabled(true);
					outputDelete.setEnabled(true);
					 

				}
			}

		});	
    }
	private void addIsParamSelectionListener(Button btnIsParam,final MappingSheetRow mappingSheetRow) {
		btnIsParam.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				
				Button isParam=(Button)e.widget;
			    TableViewer tableViewer=(TableViewer)isParam.getData("inputFieldTable");
				Button addButton=(Button) isParam.getData("addButton");
				Button deleteButton=(Button) isParam.getData("deleteButton");
				Text expressionIdTextBox=(Text) isParam.getData("expressionIdTextBox");
				Button browseButton=(Button) isParam.getData("expressionEditorButton");
				Text outputFieldTextBox=(Text) isParam.getData("outputFieldTextBox");
				Text expressionTextBox=(Text) isParam.getData("expressionTextBox");
				Text parameterTextBox=(Text)isParam.getData("parameterTextBox");
				Text textAccumulator = (Text)isParam.getData("textAccumulator");
				Button isParamAccumulator = (Button)isParam.getData("isParamAccumulator");
				Combo comboDataTypes = (Combo)isParam.getData("comboDataTypes");
				Text mergeExpressionTextBox=(Text)isParam.getData(Constants.EXPRESSION_TEXT_BOX1);
				Button mergeExpressionBrowseButton=(Button)isParam.getData(Constants.EXPRESSION_EDITOR_BUTTON1);
				if(isParam.getSelection())
				{
					if (WidgetUtility.eltConfirmMessage(Messages.ALL_DATA_WILL_BE_LOST_DO_YOU_WISH_TO_CONTINUE)) 
					{
						parameterTextBox.setEnabled(true);
						mappingSheetRow.setWholeOperationParameter(true);
						mappingSheetRow.getExpressionEditorData().setExpression("");
						mappingSheetRow.getExpressionEditorData().getSelectedInputFieldsForExpression().clear();
						tableViewer.getTable().clearAll();
						tableViewer.getTable().setEnabled(false);
						addButton.setEnabled(false);
						deleteButton.setEnabled(false);
						expressionIdTextBox.setEnabled(false);
						expressionTextBox.setText("");
						browseButton.setEnabled(false);
						if(Constants.GROUP_COMBINE.equalsIgnoreCase(component.getComponentName())){
							mergeExpressionBrowseButton.setEnabled(false);
							mergeExpressionTextBox.setText("");
							mappingSheetRow.getMergeExpressionDataForGroupCombine().setExpression("");
							mappingSheetRow.getMergeExpressionDataForGroupCombine().getSelectedInputFieldsForExpression().clear();
						}
						outputFieldTextBox.setText("");
						outputFieldTextBox.setEnabled(false);
						textAccumulator.setEnabled(false);
						isParamAccumulator.setEnabled(false);
						comboDataTypes.setEnabled(false);
						
						mappingSheetRow.getInputFields().clear();
						mappingSheetRow.getOutputList().clear();
						refreshOutputTable();
						showHideValidationMessage();
					}
					else
						isParam.setSelection(false);
				}	
				else
				{
					parameterTextBox.setText("");
					expressionTextBox.setText("");
					parameterTextBox.setEnabled(false);
					mappingSheetRow.setWholeOperationParameter(false);
					tableViewer.getTable().setEnabled(true);
					tableViewer.refresh();
					addButton.setEnabled(true);
					deleteButton.setEnabled(true);
					expressionIdTextBox.setEnabled(true);
					browseButton.setEnabled(true);
					outputFieldTextBox.setEnabled(true);
					textAccumulator.setEnabled(true);
					isParamAccumulator.setEnabled(true);
					comboDataTypes.setEnabled(true);
					if(Constants.GROUP_COMBINE.equalsIgnoreCase(component.getComponentName())){
						mergeExpressionBrowseButton.setEnabled(true);
						mergeExpressionTextBox.setText("");
					}
				}	
			}
		});
	}
	
	public void refreshOutputTable() 
	{
		finalSortedList=new ArrayList<>();
		temporaryOutputFieldMap.clear();
		temporaryOutputFieldMap
				.put("MapAndPassThroughFields",convertNameValueToFilterProperties(transformMapping.getMapAndPassthroughField()));
		for (MappingSheetRow mappingSheetRow1 : transformMapping.getMappingSheetRows()) {
			if(mappingSheetRow1.isActive())
			temporaryOutputFieldMap.put(mappingSheetRow1.getOperationID(),mappingSheetRow1.getOutputList());

		}
		
		SchemaSyncUtility.INSTANCE.unionFilter(transformMapping.getOutputFieldList(), 
				finalSortedList);
		
	    outputFieldViewer.setInput(finalSortedList);
		outputFieldViewer.refresh();
		mappingTableViewer.refresh();
	}
	/**
	 * @return finalsortedList
	 */
	public List<FilterProperties> getFinalSortedList() {
		return finalSortedList;
	}

	/**
	 * @param temporaryOutputFieldListTemp
	 * @return
	 */
	public Map<String,List<String>> getDuplicateOutputFieldMap(Map<String,List<FilterProperties> > temporaryOutputFieldListTemp) {
		Set<String> setToCheckDuplicates = new HashSet<String>();
		if(duplicateFieldMap!=null)
		duplicateFieldMap.clear();
		for (Map.Entry<String, List<FilterProperties>> entry: temporaryOutputFieldListTemp.entrySet()) 
		{
			List<FilterProperties>  temporaryOutputFieldList=entry.getValue();
			List<String> duplicateFields=new ArrayList<>();
			for (FilterProperties filterProperties : temporaryOutputFieldList) {
			if (!setToCheckDuplicates.add(filterProperties.getPropertyname())) {
				duplicateFields.add(filterProperties.getPropertyname());
			}
		}
			duplicateFieldMap.put(entry.getKey(),duplicateFields);	
		}
		return duplicateFieldMap;
	}
	
	/**
	 * @param mappingSheetRow
	 */
	public void setDuplicateOperationInputFieldMap(MappingSheetRow mappingSheetRow) {
		    duplicateOperationInputFieldMap.clear();
			List<FilterProperties> temp=mappingSheetRow.getInputFields();
			List<String> duplicateFields=new ArrayList<>();
			Set<String> setToCheckDuplicates = new HashSet<String>();
			for (FilterProperties filterProperties : temp)   {
				if (!setToCheckDuplicates.add(filterProperties.getPropertyname())) {
					
					duplicateFields.add(filterProperties.getPropertyname());
				}
				
			}
			duplicateOperationInputFieldMap.put(mappingSheetRow.getOperationID(),duplicateFields);
	}
    
     /**
     * check if error exist
     */
    public void showHideValidationMessage()
   	{		
		if(errorTableViewer!=null)
		{
			
			errorLabelList.clear();
			outputFieldIndices.clear();
			setErrorMessageForDuplicateOutputField();
			setErrorMessageForDuplicateInputField();
			setErrorMessageForInvalidMapFields();
			setErrorMessageForAccumulator();
			setErrorMessageIfExpressionIsNotValid();
			setErrorMessageIfMergeExpressionIsNotValid();
			isAllExternalFielsAreInSync();
		   Set<String> setToCheckDuplicates = showErrorIfOperationClassOrExpressionBlankOrOperationIDDuplicate(); 	
     	   errorTableViewer.getTable().setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
	       errorTableViewer.refresh();
	       setToCheckDuplicates.clear();
		}
	}
    
	private void isAllExternalFielsAreInSync() {
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				for (MappingSheetRow mappingSheetRow : transformMapping.getMappingSheetRows()) {
					try {
						if (mappingSheetRow.isActive() && mappingSheetRow.isExpression()
								&& mappingSheetRow.getExternalExpresion().isExternal()) {

							ExternalOperationExpressionUtil.INSTANCE.validateUIExpressionWithExternalFile(
									mappingSheetRow,
									PathUtility.INSTANCE.getPath(mappingSheetRow.getExternalExpresion().getFilePath(),
											Constants.XML_EXTENSION, false, Constants.XML_EXTENSION));

						} if (mappingSheetRow.isActive() && !mappingSheetRow.isExpression()
								&& mappingSheetRow.getExternalOperation().isExternal()) {
							ExternalOperationExpressionUtil.INSTANCE.validateUIOperationWithExternalFile(
									mappingSheetRow,
									PathUtility.INSTANCE.getPath(mappingSheetRow.getExternalOperation().getFilePath(),
											Constants.XML_EXTENSION, false, Constants.XML_EXTENSION));
						}

						if (transformMapping.getExternalOutputFieldsData().isExternal() && StringUtils
								.isNotBlank(transformMapping.getExternalOutputFieldsData().getFilePath())) {
							ExternalOperationExpressionUtil.INSTANCE.validateUIMappingFieldsWithExternalFile(
									transformMapping,
									PathUtility.INSTANCE.getPath(
											transformMapping.getExternalOutputFieldsData().getFilePath(),
											Constants.XML_EXTENSION, false, Constants.XML_EXTENSION));
						}

					} catch (RuntimeException exception) {
						updateErrorListOnOutOfSync(exception);
					}
				}
			}
		});
	}

	private void updateErrorListOnOutOfSync(RuntimeException exception) {
		if(StringUtils.isNotBlank(exception.getMessage())){
		for(Label label:errorLabelList){
			if(StringUtils.equals(label.getText(), exception.getMessage())){
				return;
			}
		}
		errorLabel = new Label(errorTableViewer.getTable(), SWT.NONE);
		errorLabel.setVisible(true);
		errorLabel.setText(exception.getMessage());
		errorLabelList.add(errorLabel);
		errorTableViewer.refresh();
		}
	}

	private void setErrorMessageIfMergeExpressionIsNotValid() {
		for(MappingSheetRow mappingSheetRow:transformMapping.getMappingSheetRows())
    	{	
	   	if(mappingSheetRow.isActive()
	   	   &&mappingSheetRow.isExpression()
	   	   &&StringUtils.isNotBlank(mappingSheetRow.getMergeExpressionDataForGroupCombine().getExpression())
	   	   &&!(mappingSheetRow.getMergeExpressionDataForGroupCombine().isValid())
	       )
	   		
	   	{
	   		errorLabel=new Label( errorTableViewer.getTable(), SWT.NONE);
			errorLabel.setVisible(true);
			errorLabel.setText(mappingSheetRow.getMergeExpressionDataForGroupCombine().getErrorMessage()+" for Merge Expression"+mappingSheetRow.getOperationID()); 
			errorLabelList.add(errorLabel);
	   	}	
    	}
		
	}

	private void setErrorMessageForAccumulator() {
		OperationClassConfig configurationForTransformWidget;
		configurationForTransformWidget = (OperationClassConfig) widgetConfig;
		transformMapping.getMappingSheetRows().forEach(mappingSheetRow -> {
			if (mappingSheetRow.isActive() && mappingSheetRow.isExpression()
					&& (StringUtils.equalsIgnoreCase(Constants.AGGREGATE,configurationForTransformWidget.getComponentName())
							|| StringUtils.equalsIgnoreCase(Constants.CUMULATE,configurationForTransformWidget.getComponentName())
							|| StringUtils.equalsIgnoreCase(Constants.GROUP_COMBINE, configurationForTransformWidget.getComponentName()))) {
				boolean logError = true;
				boolean isValidValue = validate(mappingSheetRow.getAccumulator(), mappingSheetRow.getComboDataType());
				if (!isValidValue) {
					errorLabel = new Label(errorTableViewer.getTable(), SWT.NONE);
					errorLabel.setVisible(true);
					if (StringUtils.isBlank(mappingSheetRow.getAccumulator())) {
						errorLabel.setText("Accmulator value can not be blank for " + mappingSheetRow.getOperationID());
					} else {
						errorLabel.setText("Accmulator value \"" + mappingSheetRow.getAccumulator()
								+ "\" does not match with its data type for " + mappingSheetRow.getOperationID());
					}
				} else {
					errorLabel = null;
				}
				for (Label tempErrorLabel : errorLabelList) {
					if (errorLabel == null
							|| StringUtils.equalsIgnoreCase(errorLabel.getText(), tempErrorLabel.getText())) {
						logError = false;
					}
				}
				if (logError && errorLabel != null && (!mappingSheetRow.isAccumulatorParameter()
						|| StringUtils.isBlank(mappingSheetRow.getAccumulator()))) {
					errorLabelList.add(errorLabel);
				}
			}
		});
	}
	
    private boolean validate(String value, String dataType){
		if(StringUtils.isNotBlank(value)){
			try{
				if(dataType.equals(Messages.DATATYPE_INTEGER)){
					Integer.valueOf(value);
					return true;
				}else if(dataType.equals(Messages.DATATYPE_STRING)){
					String.valueOf(value);
					return true;
				}
				else if(dataType.equals(Messages.DATATYPE_DOUBLE)){
					Double.valueOf(value);
					return true;
				}
				else if(dataType.equals(Messages.DATATYPE_FLOAT)){
					Float.valueOf(value);
					return true;
				}
				else if(dataType.equals(Messages.DATATYPE_SHORT)){
					Short.valueOf(value);
					return true;
				}
				else if(Boolean.FALSE.toString().equalsIgnoreCase(value) || Boolean.TRUE.toString().equalsIgnoreCase(value)){
					return true;
				}
				else if(dataType.equals(Messages.DATATYPE_BIGDECIMAL)){
					new BigDecimal(value);
					return true;
				}
				else if(dataType.equals(Messages.DATATYPE_LONG)){
					Long.valueOf(value);
					return true;
				}
				else if(dataType.equals(Messages.DATATYPE_DATE)){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
					sdf.setLenient(false);
					sdf.parse(value);
					return true;
				}
				else {
					return false;
				}
			}catch(Exception exception){
				return false;
			}
		}else{
			return false;
		}
	}
 
    private void setErrorMessageIfExpressionIsNotValid() 
    {
    	for(MappingSheetRow mappingSheetRow:transformMapping.getMappingSheetRows())
    	{	
	   	if(mappingSheetRow.isActive()
	   	   &&mappingSheetRow.isExpression()
	   	   &&StringUtils.isNotBlank(mappingSheetRow.getExpressionEditorData().getExpression())
	   	   &&!(mappingSheetRow.getExpressionEditorData().isValid())
	       )
	   		
	   	{
	   		errorLabel=new Label( errorTableViewer.getTable(), SWT.NONE);
			errorLabel.setVisible(true);
			errorLabel.setText(mappingSheetRow.getExpressionEditorData().getErrorMessage()+" for "+mappingSheetRow.getOperationID()); 
			errorLabelList.add(errorLabel);
	   	}	
    	}
	}

	private void setErrorMessageForInvalidMapFields(){
    	List<String> inputFieldNames = new ArrayList<String>();
    	for(InputField inputField:transformMapping.getInputFields()){
    		inputFieldNames.add(inputField.getFieldName());
    	}
    	
    	for(int i = 0 ;i< transformMapping.getMapAndPassthroughField().size();i++){
    		if(!inputFieldNames.contains(transformMapping.getMapAndPassthroughField().get(i).getPropertyName())){
    		    errorLabel=new Label( errorTableViewer.getTable(), SWT.NONE);
    			errorLabel.setVisible(true);
    			errorLabel.setText("Field '"+transformMapping.getMapAndPassthroughField().get(i).getPropertyName()+"' is not present in Input Fields"); 
    			errorLabelList.add(errorLabel);
    			outputFieldIndices.add(i);
    		}
    	}
    	for(int indices=0 ; indices<mappingTableViewer.getTable().getItemCount();indices++){
    		if(outputFieldIndices.contains(indices)){
    			mappingTableViewer.getTable().getItem(indices).setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255,0,0));
    		}else{
    			mappingTableViewer.getTable().getItem(indices).setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 0, 0, 0));
    		}
    	}
    }

    private Set<String> showErrorIfOperationClassOrExpressionBlankOrOperationIDDuplicate() 
    {
	    Set<String> setToCheckDuplicates = new HashSet<String>();
	    Button isParam = null;
	    Text idTextBox=null;
		 for(ExpandItem item:expandBar.getItems() )
		{
			if(item.getControl() !=null && item.getControl().getClass()==OperationClassComposite.class)
			{
				Text operationClassTextBox=((OperationClassComposite)item.getControl()).getOperationTextBox();
				isParam=((OperationClassComposite)item.getControl()).getBtnIsParam();
				idTextBox=((OperationClassComposite)item.getControl()).getOperationIdTextBox();
				MappingSheetRow mappingSheetRow=
						(MappingSheetRow)((OperationClassComposite)item.getControl()).getBrowseButton().getData(Messages.MAPPING_SHEET);
				 
			if(operationClassTextBox!=null)
			{
			   if(StringUtils.isBlank(operationClassTextBox.getText()) && !isParam.getSelection())
			   {
				intializeErrorLabelObject(idTextBox,"Operation Class must not be blank for");
			   }
			   else if(!isParam.getSelection()
					   &&!mappingSheetRow.isClassParameter()
					   && !(ValidatorUtility.INSTANCE.isClassFilePresentOnBuildPath(operationClassTextBox.getText()))
					   )
			   {
				   intializeErrorLabelObject(idTextBox,"Java class is not present on build path of current project for");
			   }	
			}
			}
			if(item.getControl() instanceof AbstractExpressionComposite)
			{
			Text expressionTextBox=((AbstractExpressionComposite)item.getControl()).getExressionTextBox();
			Text expressionTextBox2=((AbstractExpressionComposite)item.getControl()).getExressionTextBox2();
			Text outputFieldTextBox=((AbstractExpressionComposite)item.getControl()).getOutputFieldTextBox();
			idTextBox=((AbstractExpressionComposite)item.getControl()).getExpressionIdTextBox();
			   isParam=((AbstractExpressionComposite)item.getControl()).getIsParamButton();
			if(expressionTextBox!=null)
			{
				if(StringUtils.isBlank(expressionTextBox.getText()) && !isParam.getSelection())
			   {
				intializeErrorLabelObject(idTextBox,"Expression must not be blank for");
			   }
				
			}
			
			if(expressionTextBox2!=null)
			{
				if(StringUtils.isBlank(expressionTextBox2.getText()) && !isParam.getSelection())
			   {
				intializeErrorLabelObject(idTextBox,"MergeExpression must not be blank for");
			   }
				
			}
			if(outputFieldTextBox!=null)
			{
				if(StringUtils.isBlank(outputFieldTextBox.getText()) && !isParam.getSelection())
				 {
					intializeErrorLabelObject(idTextBox,"Output Field must not be blank for");
				 }
			}	
			}
			if(!setToCheckDuplicates.add(idTextBox.getText())&&!isParam.getSelection())
			{
				intializeErrorLabelObject(idTextBox,"Duplicate Operation Id");
			}	
		}
	 return setToCheckDuplicates;
   }

	private void intializeErrorLabelObject(Text idTextBox,String errorMessage) {
		errorLabel=new Label( errorTableViewer.getTable(), SWT.NONE);
		errorLabel.setVisible(true); 
		errorLabel.setText(errorMessage+" "+idTextBox.getText()); 	
		errorLabelList.add(errorLabel);
	}

   private void setErrorMessageForDuplicateInputField() {
	if(!duplicateOperationInputFieldMap.isEmpty())
   {
	   for(Map.Entry<String, List<String>> entry:duplicateOperationInputFieldMap.entrySet())
	   {
		   for(String f:entry.getValue())
		   {   
			   boolean logError=true;
			   errorLabel=new Label( errorTableViewer.getTable(), SWT.NONE);
			   errorLabel.setVisible(true); 
			   errorLabel.setText("Duplicate Operation Input Field"+" "+f +" "+"exist in"+" "+entry.getKey()); 
			   for(Label tempErrorLabel:errorLabelList) {
				   if(StringUtils.equalsIgnoreCase(errorLabel.getText(),tempErrorLabel.getText()))
				   logError=false;
			   }
			   if(logError)
			   errorLabelList.add(errorLabel);
		   }
	   }
   }
}

   private void setErrorMessageForDuplicateOutputField() {
	Map<String,List<String>> duplicateOutputFieldMap=getDuplicateOutputFieldMap(temporaryOutputFieldMap);
	   if(!duplicateOutputFieldMap.isEmpty())
	   {	for (Map.Entry<String,List<String>> entry: duplicateOutputFieldMap.entrySet()) 
		{
		   for(String f:entry.getValue())
		   {   
			   boolean logError=true;   
		    errorLabel=new Label( errorTableViewer.getTable(), SWT.NONE);
			errorLabel.setVisible(true);
			errorLabel.setText("Duplicate Output Field "+" "+f+" "+"exist in"+" "+entry.getKey()); 
			
			for(Label tempErrorLabel:errorLabelList) {
				   if(StringUtils.equalsIgnoreCase(errorLabel.getText(),tempErrorLabel.getText()))
				   logError=false;
			   }
			if(logError)
			errorLabelList.add(errorLabel);
		   }
	    }
	   }
}

	/**
	 * @param nameValueProperty
	 * @return
	 */
	public List<FilterProperties> convertNameValueToFilterProperties(List<NameValueProperty> nameValueProperty) {
		List<FilterProperties> filterProperties = new ArrayList<>();

		for (NameValueProperty nameValue : nameValueProperty) {
			FilterProperties filterProperty = new FilterProperties();
			filterProperty.setPropertyname(nameValue.getPropertyValue());
			filterProperties.add(filterProperty);
		}
		return filterProperties;
	}
	
	private void deleteButtonListener(final MappingSheetRow mappingSheetRow,
			final TableViewer operationalInputFieldTableViewer,Widget button) {
		button.addListener(SWT.MouseUp,new Listener(){

			@Override
			public void handleEvent(Event event) {
				Table table = operationalInputFieldTableViewer.getTable();
				String[] itemsToBeDeleted=getSlectedFiledsName(table.getSelection());
				int temp = table.getSelectionIndex();
				 mappingSheetRow.getInputFields().size();
				int[] indexs = table.getSelectionIndices();
				if (temp == -1) 
				{
					WidgetUtility.errorMessage(Messages.SelectRowToDelete);
				} 
				else 
				{
					table.remove(indexs);
					List<FilterProperties> tempList = new ArrayList<FilterProperties>();
					for (int index : indexs) {
						tempList.add(mappingSheetRow.getInputFields().get(index));
					}
					  for(FilterProperties filterProperties:tempList)
					  {
					   mappingSheetRow.getInputFields().remove(filterProperties);
					  }
					   TransformMappingFeatureUtility.INSTANCE.setCursorOnDeleteRow(operationalInputFieldTableViewer, indexs,
                              mappingSheetRow.getInputFields());
					   setDuplicateOperationInputFieldMap( mappingSheetRow);
					   removeInputFieldsFromExpressionEditiorData(mappingSheetRow, itemsToBeDeleted);
					   showHideValidationMessage();

				}

			}

			private String[] getSlectedFiledsName(TableItem[] selection) {
				String[] fields=new String[selection.length];
				for(int index= 0;index <selection.length;index++){
					fields[index]=selection[index].getText();
				}
				return  fields;
			}

			private void removeInputFieldsFromExpressionEditiorData(MappingSheetRow mappingSheetRow,
					String[] itemsToBeDeleted) {
				boolean isInputFieldRemovedFromExpression = false;
				ExpressionEditorData expressionEditorData = mappingSheetRow.getExpressionEditorData();
				for (String item : itemsToBeDeleted) {
					if (expressionEditorData!=null && (expressionEditorData.getfieldsUsedInExpression().remove(item)
							| expressionEditorData.getSelectedInputFieldsForExpression().remove(item) != null)) {
						isInputFieldRemovedFromExpression = true;
					}
				}
				if (isInputFieldRemovedFromExpression) {
					ExpressionEditorUtil.validateExpression(expressionEditorData.getExpression(),
							expressionEditorData.getSelectedInputFieldsForExpression(), expressionEditorData);
				}
			}

		});
	}
    
private void addModifyAndFocusListenerToOutputField(Text outputFieldTextBox2,final MappingSheetRow mappingSheetRow) {
	 
	outputFieldTextBox2.addFocusListener(new FocusAdapter() {
		
		@Override
		public void focusLost(FocusEvent e) {
         refreshOutputTable();
         showHideValidationMessage();
		}
	});
		outputFieldTextBox2.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				Text textBox=(Text) e.widget;
				if(mappingSheetRow.getOutputList().isEmpty())
				{
					FilterProperties filterProperties=new FilterProperties();	
					filterProperties.setPropertyname(textBox.getText());
				    mappingSheetRow.getOutputList().add(filterProperties);
				    transformMapping.getOutputFieldList().add(filterProperties);
				}
				else
				{	
				mappingSheetRow.getOutputList().get(0).setPropertyname(textBox.getText());	
				
				}
			}
		});
	}

private void addButtonListener(final MappingSheetRow mappingSheetRow,
			final TableViewer operationalInputFieldTableViewer,Widget addButton) {
		addButton.addListener(SWT.MouseUp, new Listener(){

			@Override
			public void handleEvent(Event event) {
				operationInputTableAddButton(mappingSheetRow,
						operationalInputFieldTableViewer);
				}
		});
	}

private void operationInputTableAddButton(
		 MappingSheetRow mappingSheetRow,
		 TableViewer tableViewer) {
	FilterProperties filterProperties = new FilterProperties();
	filterProperties.setPropertyname("");
	if (!mappingSheetRow.getInputFields().contains(filterProperties)) {
		mappingSheetRow.getInputFields().add(filterProperties);

		tableViewer.refresh();
		int i = mappingSheetRow.getInputFields().size() == 0 ? mappingSheetRow.getInputFields().size()
				: mappingSheetRow.getInputFields().size() - 1;
		tableViewer.editElement(tableViewer.getElementAt(i), 0);
	}
}

	private void setIsOperationInputFieldDuplicate() {
		if (!transformMapping.getMappingSheetRows().isEmpty()) {
			Set<FilterProperties> set = null;
			List<MappingSheetRow> mappingSheetRows = transformMapping.getMappingSheetRows();
			for (MappingSheetRow mappingSheetRow : mappingSheetRows) {
				set = new HashSet<FilterProperties>(mappingSheetRow.getInputFields());
				if (set.size() < mappingSheetRow.getInputFields().size()) {
					isOperationInputFieldDuplicate = true;
					break;
				}
			}
				if(set!=null){
				set.clear();
				}
			}
		}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		propertyDialogButtonBar.setPropertyDialogButtonBar(okButton, null, cancelButton);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		container.getShell().layout(true, true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Point newSize = container.getShell().computeSize(screenSize.width, screenSize.height, true);
		container.getShell().setSize(newSize);
		return newSize;
	}

	@Override
	protected void okPressed() {
		
		if(OSValidator.isMac()){
		
			List<TableViewer> tableViewer = new ArrayList<TableViewer>();
			tableViewer.add(mappingTableViewer);
			tableViewer.add(outputFieldViewer);
			tableViewer.add(operationalInputFieldTableViewer);
			tableViewer.add(operationalOutputFieldTableViewer);
			
			for(TableViewer tableView : tableViewer){
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
		
			okPressed = true;
			super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		cancelPressed = true;
		super.cancelPressed();
	}

	public TransformMapping getATMapping() {
		return transformMapping;
	}

	public Component getComponent() {
		return component;
	}

	public void pressOK() {
		isYesButtonPressed = true;
		okPressed();
	}

	public void pressCancel() {
		isNoButtonPressed = true;
		cancelPressed();
	}

	public boolean isCancelPressed() {
		return cancelPressed;
	}

	public boolean isOkPressed() {
		return okPressed;
	}

	/**
	 * 
	 * returns true if ok button pressed from code
	 * 
	 * @return boolean
	 */
	public boolean isYesButtonPressed() {
		return isYesButtonPressed;
	}

	public boolean isOperationInputFieldDupluicate() {
		return isOperationInputFieldDuplicate;
	}

	/**
	 * 
	 * returns true of cancel button pressed from code
	 * 
	 * @return boolean
	 */
	public boolean isNoButtonPressed() {
		return isNoButtonPressed;
	}

	public TableViewer setTableViewer(TableViewer tableViewer, Composite composite, String[] prop,
			IStructuredContentProvider iStructuredContentProvider) {

		tableViewer.setContentProvider(iStructuredContentProvider);

		tableViewer.setColumnProperties(prop);

		tableViewerTable = tableViewer.getTable();

		tableViewerTable.setVisible(true);
		tableViewerTable.setLinesVisible(true);
		tableViewerTable.setHeaderVisible(true);
		createTableColumns(tableViewerTable, prop);
		editors = createCellEditorList(tableViewerTable, prop.length);
		tableViewer.setCellEditors(editors);

		TableViewerEditor.create(tableViewer, new ColumnViewerEditorActivationStrategy(tableViewer),
				ColumnViewerEditor.KEYBOARD_ACTIVATION | ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL);

		return tableViewer;
	}

	public static void createTableColumns(Table table, String[] fields) {
		for (String field : fields) {
			TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
			tableColumn.setText(field);

			tableColumn.setWidth(100);
			tableColumn.pack();
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	public static CellEditor[] createCellEditorList(Table table, int size) {
		CellEditor[] cellEditor = new CellEditor[size];
		for (int i = 0; i < size; i++)
			addTextEditor(table, cellEditor, i);

		return cellEditor;
	}

	protected static void addTextEditor(Table table, CellEditor[] cellEditor, int position) {

		cellEditor[position] = new TextCellEditor(table, SWT.COLOR_GREEN);
	}
	
	
	private void syncTransformFieldsWithSchema() {
		
		List<FilterProperties> filterProperties = convertSchemaToFilterProperty();
		if(checkIfSchemaIsBlank(filterProperties))
		{
		return;
		} 	
		SchemaSyncUtility.INSTANCE.removeOpFields(filterProperties, transformMapping,
				transformMapping.getMappingSheetRows(),component.getComponentName());
		Schema schema = (Schema) component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
		List<NameValueProperty> outputFileds= SchemaSyncUtility.INSTANCE.getComponentSchemaAsProperty(schema.getGridRow());
		SchemaSyncUtility.INSTANCE.filterCommonMapFields(outputFileds, transformMapping);
		Map<Integer,FilterProperties> indexValueParameterMap=SchemaSyncUtility.INSTANCE.retainIndexAndValueOfParameterFields
				(transformMapping.getOutputFieldList());
		transformMapping.getOutputFieldList().clear();
		SchemaSyncUtility.INSTANCE.addOperationFieldAndMapPassthroughfieldToOutputField(transformMapping);
		List<FilterProperties> finalSortedList=SchemaSyncUtility.INSTANCE.
				sortOutputFieldToMatchSchemaSequence(filterProperties,transformMapping);
		transformMapping.getOutputFieldList().clear();
		transformMapping.getOutputFieldList().addAll(finalSortedList);
		SchemaSyncUtility.INSTANCE.addParamtereFieldsToSameIndexAsBeforePull(indexValueParameterMap,transformMapping);
		refreshOutputTable();
		for(ExpandItem item:expandBar.getItems())
		{
			TableViewer tableViewer=(TableViewer)item.getData(OUTPUT_TABLE_VIEWER);
			if(tableViewer!=null)
			tableViewer.refresh();
		}
	}

	private boolean checkIfSchemaIsBlank(List<FilterProperties> filterProperties) {
		if(filterProperties.isEmpty())
		{
			
			transformMapping.getOutputFieldList().clear();
			for(MappingSheetRow mappingSheetRow:transformMapping.getMappingSheetRows())
			{
				mappingSheetRow.getOutputList().clear();
			}
			for(ExpandItem item:expandBar.getItems())
			{
				
				TableViewer tableViewer=(TableViewer)item.getData(OUTPUT_TABLE_VIEWER);
				if(tableViewer!=null)
				tableViewer.refresh();
			}
			transformMapping.getMapAndPassthroughField().clear();
			mappingTableViewer.refresh();
			finalSortedList.clear();
			outputFieldViewer.refresh();
			
		}
		return filterProperties.isEmpty();
	}
	/**
	 * convert schema to filter property 
	 * @return  list
	 */
	private List<FilterProperties> convertSchemaToFilterProperty(){
		List<FilterProperties> outputFileds = new ArrayList<>();
		Schema schema = (Schema) component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
		    if(schema==null)
			 return outputFileds;  
			for (GridRow gridRow : schema.getGridRow()) {
				FilterProperties filterProperty = new FilterProperties();
				filterProperty.setPropertyname(gridRow.getFieldName());
				outputFileds.add(filterProperty);
			}
		return outputFileds;
	}
	
	private void addOperations() {
		if (expandBar.getItemCount()>0){
			for (ExpandItem expandItem : expandBar.getItems()) {
				expandItem.setExpanded(false);
			}
		}
		List<MappingSheetRow> activeMappingSheetRows=
				TransformMappingFeatureUtility.INSTANCE.getActiveMappingSheetRow(transformMapping.getMappingSheetRows());
		int n = activeMappingSheetRows.size()+1;
		String operationID;
		operationID= Messages.OPERATION_ID_PREFIX + n;	
		
		Set<String>	operationIds = new HashSet<String>();
		
		transformMapping.getMappingSheetRows().forEach(mappingSheetRow->{
			operationIds.add(mappingSheetRow.getOperationID());
			}
		);
		
		//If duplicate Operation Id (for Operation)exists
		while(operationIds.contains(operationID)){
			operationID = Messages.OPERATION_ID_PREFIX + ++n;
		}
		List<FilterProperties> inputFieldListOperationClass = new ArrayList<>();
		List<FilterProperties> outputListOperationClass = new ArrayList<>();
		List<NameValueProperty> nameValuePropertyOperationClass = new ArrayList<>();
		mappingSheetRowForOperationClass = new MappingSheetRow(inputFieldListOperationClass, 
				outputListOperationClass, operationID, Messages.CUSTOM, "",
				nameValuePropertyOperationClass, false, "", false, "",false,null,null,setActiveMappingSheetForOperationClass());	
		transformMapping.getMappingSheetRows().add(mappingSheetRowForOperationClass);
			
		List<FilterProperties> inputFieldList = new ArrayList<>();
		List<FilterProperties> outputList = new ArrayList<>();
		List<NameValueProperty> nameValueProperty = new ArrayList<>();
		operationID=Messages.EXPRESSION_ID_PREFIX + n;
		
		//If duplicate Operation Id (for Expression)exists
		while(operationIds.contains(operationID)){
			operationID = Messages.EXPRESSION_ID_PREFIX + ++n;
		}
		
		ExpressionEditorData expressionEditorData=new ExpressionEditorData("",component.getComponentName());
		ExpressionEditorData mergeExpressionEditorDataForGroupCombine=new ExpressionEditorData("",component.getComponentName());
    	mappingSheetRowForExpression = new MappingSheetRow(inputFieldList, outputList, operationID, Messages.CUSTOM, "",
				nameValueProperty, false, "", false, "",true,expressionEditorData,mergeExpressionEditorDataForGroupCombine,setActiveMappingSheetForExpression());
    	transformMapping.getMappingSheetRows().add(mappingSheetRowForExpression);
		addExpandItem(scrolledComposite);
	}

	private boolean setActiveMappingSheetForExpression() {
		boolean isExpressionActive=true;
		if(StringUtils.equalsIgnoreCase(Constants.NORMALIZE,((OperationClassConfig)widgetConfig).getComponentName())
				&&!transformMapping.isExpression())
			isExpressionActive=false;
		return isExpressionActive;
	}

	private boolean setActiveMappingSheetForOperationClass() {
		boolean operationClassActiveOrNot = false;
		if(StringUtils.equalsIgnoreCase(Constants.NORMALIZE,((OperationClassConfig)widgetConfig).getComponentName())
				&&!transformMapping.isExpression())
		operationClassActiveOrNot=true;
		return operationClassActiveOrNot;
	}
	
	/**
	 * 
	 * Close method get called on closing the dialog.
	 * 
	 */
	public boolean close() {
		if (preClose()==SWT.YES) {
			return super.close();
		}
		cancelPressed=false;
		return false;
	}
	private int preClose()
	{
		if (!oldTransformMapping.equals(transformMapping)&&!okPressed)
		{	
		 return WidgetUtility.createMessageBox
		 (Messages.ALL_UNSAVED_CHANGES_WILL_BE_LOST_DO_YOU_WISH_TO_EXIT, EXITING_TRANSFORM_EDITOR,SWT.ICON_QUESTION | SWT.YES|SWT.NO);	
		}
		return SWT.YES;
	}
	public void refresh(){
		for(ExpandItem expandItem:expandBar.getItems()){
			expandItem.setExpanded(false);
			expandItem.dispose();
		}
		isAllPassthrougButton.setSelection(transformMapping.isAllInputFieldsArePassthrough());
		loadExpressionAndOperationData();		
		mappingTableViewer.refresh();
		refreshOutputTable();
		showHideValidationMessage();
		
		
	}
}