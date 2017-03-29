/********************************************************************************
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
 ******************************************************************************/

package hydrograph.ui.parametergrid.dialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.common.swt.customwidget.HydroGroup;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.XMLUtil;
import hydrograph.ui.datastructures.parametergrid.ParameterFile;
import hydrograph.ui.datastructures.parametergrid.filetype.ParamterFileTypes;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.parametergrid.constants.ErrorMessages;
import hydrograph.ui.parametergrid.constants.MessageType;
import hydrograph.ui.parametergrid.constants.MultiParameterFileDialogConstants;
import hydrograph.ui.parametergrid.dialog.models.Parameter;
import hydrograph.ui.parametergrid.dialog.models.ParameterWithFilePath;
import hydrograph.ui.parametergrid.dialog.support.ParameterEditingSupport;
import hydrograph.ui.parametergrid.utils.ParameterFileManager;
import hydrograph.ui.parametergrid.utils.SWTResourceManager;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * 
 * UI for multi parameter file dialog
 * 
 * @author Bitwise
 * 
 */
public class MultiParameterFileDialog extends Dialog {
	

	private static final String FILE_NAME_VALIDATION_EXPRESSION = "[\\w]*";

	private static final int PROPERTY_VALUE_COLUMN_INDEX = 1;

	private static final Logger logger = LogFactory.INSTANCE
			.getLogger(MultiParameterFileDialog.class);

	private TableViewer filePathTableViewer;
	private TableViewer parameterTableViewer;
	private TableViewer parameterSearchTableViewer;
	private Text parameterFileTextBox;
	private SashForm mainSashForm;
	private List<ParameterFile> parameterFiles;
	private List<ParameterFile> jobLevelParamterFiles;
	private List<Parameter> parameters;
	private List<ParameterWithFilePath> parameterSearchBoxItems;
	private List<ParameterWithFilePath> parameterSearchBoxItemsFixed;
	private String activeProjectLocation;
	private boolean runGraph;
	private boolean ctrlKeyPressed = false;
	private static final String DROP_BOX_TEXT = "\nDrop parameter file here to delete";
	private boolean okPressed;
	private boolean ifNotified = false;
	private static final Base64 base64 = new Base64();
	private Composite container_1;
	private Table table_2;
	private static final String TABLE_TYPE_KEY="TABLE_TYPE";
	private IStructuredSelection previousSelection = null;
	
	private Button applyButton;
	private Composite composite_9;
	
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	@Deprecated
	public MultiParameterFileDialog(Shell parentShell) {
		super(parentShell);
		//setShellStyle(SWT.CLOSE | SWT.MAX | SWT.RESIZE);
		if (parameterFiles == null)
			parameterFiles = new LinkedList<>();

		parameters = new LinkedList<>();
		parameterSearchBoxItems = new LinkedList<>();
		parameterSearchBoxItemsFixed = new LinkedList<>();
		
	}

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public MultiParameterFileDialog(Shell parentShell,
			String activeProjectLocation) {
		super(parentShell);
		if (parameterFiles == null)
			parameterFiles = new LinkedList<>();

		parameters = new LinkedList<>();
		parameterSearchBoxItems = new LinkedList<>();
		parameterSearchBoxItemsFixed = new LinkedList<>();
		

		this.activeProjectLocation = activeProjectLocation;
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL
				| SWT.RESIZE);
		
		jobLevelParamterFiles = new ArrayList<>();
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		getShell().setText(
				MultiParameterFileDialogConstants.PARAMETER_FILE_DIALOG_TEXT);
		
		container_1 = (Composite) super.createDialogArea(parent);
		mainSashForm = new SashForm(container_1, SWT.HORIZONTAL);
		mainSashForm.setSashWidth(6);
		GridData gd_mainSashForm = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_mainSashForm.heightHint = 476;
		gd_mainSashForm.widthHint = 851;
		mainSashForm.setLayoutData(gd_mainSashForm);
		createParameterFilesBox(container_1);
		populateFilePathTableViewer();

		Composite composite = createParameterFileViewOuterComposite(mainSashForm);
		SashForm childSashForm=new SashForm(composite, SWT.VERTICAL);
		childSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		createViewParameterFileBox(childSashForm);
		ParameterFile jobSpecificFile = getJobSpecificFile();
		if (jobSpecificFile != null)
			populateViewParameterFileBox(jobSpecificFile);

		createParameterSearchBox(childSashForm);
		mainSashForm.setWeights(new int[] {260, 214});
		
		Label lblNewLabel = new Label(container_1, SWT.NONE);
		lblNewLabel.setText(Messages.NOTE_FOR_SAME_PARAMETERS_DEFINED_IN_MULTIPLE_PARAMETER_FILES_THE_LOWERMOST_FILE_WILL_BE_GIVEN_PRECEDENCE_OVER_OTHERS);
		return container_1;
	}

	private ParameterFile getJobSpecificFile() {
		ParameterFile jobSpecificFile = null;
		for (ParameterFile parameterFile : parameterFiles) {
			if (parameterFile.getFileType().equals(ParamterFileTypes.JOB_SPECIFIC)) {
				jobSpecificFile = parameterFile;
				break;
			}
		}
		return jobSpecificFile;
	}
	
	private String getParamterFileLocation(ParameterFile parameterFile){
		String paramterFileLocation;;
		if(parameterFile.getFileType().equals(ParamterFileTypes.JOB_LEVEL)){
			paramterFileLocation = activeProjectLocation + File.separator
					+ MultiParameterFileDialogConstants.JOB_PARAMETER_DIRECTORY_NAME + File.separator + parameterFile.getFileName() ;
		}else if(parameterFile.getFileType().equals(ParamterFileTypes.PROJECT_LEVEL)){
			paramterFileLocation = activeProjectLocation + File.separator
					+ MultiParameterFileDialogConstants.GLOBAL_PARAMETER_DIRECTORY_NAME + File.separator + parameterFile.getFileName();
		}else{
			paramterFileLocation = activeProjectLocation + File.separator
					+ MultiParameterFileDialogConstants.JOB_PARAMETER_DIRECTORY_NAME + File.separator + parameterFile.getFileName() + ".properties" ;
		}
		return paramterFileLocation;
	}

	private void populateViewParameterFileBox(ParameterFile parameterFile) {
		//parameterFileTextBox.setText(file.getPath());
		try {
			Map<String, String> parameterMap = new LinkedHashMap<>();
			parameterMap = ParameterFileManager.getInstance().getParameterMap(getParamterFileLocation(parameterFile));
			setGridData(parameters, parameterMap);
			parameterTableViewer.setData("CURRENT_PARAM_FILE", getParamterFileLocation(parameterFile));
		} catch (IOException ioException) {

			MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR
					| SWT.OK);

			messageBox.setText(MessageType.ERROR.messageType());
			messageBox.setMessage(ErrorMessages.UNABLE_TO_POPULATE_PARAM_FILE
					+ ioException.getMessage());
			messageBox.open();

			logger.debug("Unable to populate parameter file", ioException);

		}

		parameterTableViewer.refresh();
	}

	private void searchParameter(String text) {
		parameterSearchBoxItems.clear();

		for (ParameterWithFilePath parameterSearchBoxItem : parameterSearchBoxItemsFixed) {
			if (parameterSearchBoxItem.toString().toLowerCase().contains(text)) {
				parameterSearchBoxItems.add(parameterSearchBoxItem);
			}
		}
	}

	private void createParameterSearchBox(Composite composite) {
		HydroGroup grpAllProperties = new HydroGroup(composite, SWT.NONE);
		grpAllProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		GridLayout gl_grpAllProperties = new GridLayout(1, false);
		gl_grpAllProperties.horizontalSpacing = 0;
		gl_grpAllProperties.verticalSpacing = 0;
		gl_grpAllProperties.marginHeight = 0;
		gl_grpAllProperties.marginWidth = 0;
		grpAllProperties.setLayout(gl_grpAllProperties);
		grpAllProperties.setHydroGroupText(MultiParameterFileDialogConstants.SEARCH_ALL_PARAMETERS);
		grpAllProperties.setHydroGroupBorderBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		grpAllProperties.getHydroGroupClientArea().setLayout(new GridLayout(1, false));
		Composite composite_5 = new Composite(grpAllProperties.getHydroGroupClientArea(), SWT.NONE);
		GridLayout gl_composite_5 = new GridLayout(1, false);
		gl_composite_5.verticalSpacing = 0;
		gl_composite_5.marginWidth = 0;
		gl_composite_5.marginHeight = 0;
		gl_composite_5.horizontalSpacing = 0;
		composite_5.setLayout(gl_composite_5);
		composite_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		createSearchBox(composite_5);

		createSearchViewGrid(composite_5);
		populateParameterSearchBox();

	}

	private void createSearchViewGrid(Composite composite_5) {
		Composite composite_7 = new Composite(composite_5, SWT.NONE);
		composite_7.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		composite_7.setLayout(new GridLayout(1, false));

		parameterSearchTableViewer = new TableViewer(composite_7, SWT.BORDER
				| SWT.FULL_SELECTION);
		Table table_1 = parameterSearchTableViewer.getTable();
		table_1.setLinesVisible(true);
		table_1.setHeaderVisible(true);
		table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		parameterSearchTableViewer
				.setContentProvider(new ArrayContentProvider());
		ColumnViewerToolTipSupport.enableFor(parameterSearchTableViewer,
				ToolTip.NO_RECREATE);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				parameterSearchTableViewer, SWT.NONE);
		TableColumn tblclmnFilePath_1 = tableViewerColumn.getColumn();
		tblclmnFilePath_1.setWidth(164);
		tblclmnFilePath_1
				.setText(MultiParameterFileDialogConstants.TABLE_COLUMN_LIST_OF_PARAMETER_FILES);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getToolTipText(Object element) {
				String tooltip = MultiParameterFileDialogConstants.PARAMETER_FILE
						+ ": "
						+ ((ParameterWithFilePath) element).getParameterFile()
								.getFilePathViewString()
						+ "\n "
						+ MultiParameterFileDialogConstants.PARAMETER_NAME
						+ ": "
						+ ((ParameterWithFilePath) element).getParameterName()
						+ "\n "
						+ MultiParameterFileDialogConstants.PARAMETER_VALUE
						+ ": "
						+ ((ParameterWithFilePath) element).getParameterValue();
				return tooltip;
			}

			@Override
			public Point getToolTipShift(Object object) {
				return new Point(5, 5);
			}

			@Override
			public int getToolTipDisplayDelayTime(Object object) {
				return 100; // msec
			}

			@Override
			public int getToolTipTimeDisplayed(Object object) {
				return 5000; // msec
			}

			@Override
			public Color getToolTipBackgroundColor(Object object) {
				return Display.getCurrent().getSystemColor(
						SWT.COLOR_WIDGET_BACKGROUND);
			}

			@Override
			public String getText(Object element) {
				ParameterWithFilePath p = (ParameterWithFilePath) element;
				return p.getParameterFile().getFilePathViewString();
			}
		});

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				parameterSearchTableViewer, SWT.NONE);
		TableColumn tblclmnParameterName = tableViewerColumn_1.getColumn();
		tblclmnParameterName.setWidth(140);
		tblclmnParameterName
				.setText(MultiParameterFileDialogConstants.PARAMETER_NAME);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getToolTipText(Object element) {
				String tooltip = MultiParameterFileDialogConstants.PARAMETER_FILE
						+ ": "
						+ ((ParameterWithFilePath) element).getParameterFile()
								.getFilePathViewString()
						+ "\n "
						+ MultiParameterFileDialogConstants.PARAMETER_NAME
						+ ": "
						+ ((ParameterWithFilePath) element).getParameterName()
						+ "\n "
						+ MultiParameterFileDialogConstants.PARAMETER_VALUE
						+ ": "
						+ ((ParameterWithFilePath) element).getParameterValue();
				return tooltip;
			}

			@Override
			public Point getToolTipShift(Object object) {
				return new Point(5, 5);
			}

			@Override
			public int getToolTipDisplayDelayTime(Object object) {
				return 100; // msec
			}

			@Override
			public int getToolTipTimeDisplayed(Object object) {
				return 5000; // msec
			}

			@Override
			public Color getToolTipBackgroundColor(Object object) {
				return Display.getCurrent().getSystemColor(
						SWT.COLOR_WIDGET_BACKGROUND);
			}

			@Override
			public String getText(Object element) {
				ParameterWithFilePath p = (ParameterWithFilePath) element;
				return p.getParameterName();
			}
		});

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				parameterSearchTableViewer, SWT.NONE);
		TableColumn tblclmnParameterValue = tableViewerColumn_2.getColumn();
		tblclmnParameterValue.setWidth(133);
		tblclmnParameterValue
				.setText(MultiParameterFileDialogConstants.PARAMETER_VALUE);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getToolTipText(Object element) {
				String tooltip = MultiParameterFileDialogConstants.PARAMETER_FILE
						+ ": "
						+ ((ParameterWithFilePath) element).getParameterFile()
								.getFilePathViewString()
						+ "\n "
						+ MultiParameterFileDialogConstants.PARAMETER_NAME
						+ ": "
						+ ((ParameterWithFilePath) element).getParameterName()
						+ "\n "
						+ MultiParameterFileDialogConstants.PARAMETER_VALUE
						+ ": "
						+ ((ParameterWithFilePath) element).getParameterValue();
				return tooltip;
			}

			@Override
			public Point getToolTipShift(Object object) {
				return new Point(5, 5);
			}

			@Override
			public int getToolTipDisplayDelayTime(Object object) {
				return 100; // msec
			}

			@Override
			public int getToolTipTimeDisplayed(Object object) {
				return 5000; // msec
			}

			@Override
			public Color getToolTipBackgroundColor(Object object) {
				return Display.getCurrent().getSystemColor(
						SWT.COLOR_WIDGET_BACKGROUND);
			}

			@Override
			public String getText(Object element) {
				return ((ParameterWithFilePath) element).getParameterValue();
			}
		});

		setTableLayoutToMappingTable(parameterSearchTableViewer);
	}

	private void createSearchBox(Composite composite_5) {
		Composite composite_6 = new Composite(composite_5, SWT.NONE);
		composite_6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1));
		composite_6.setLayout(new GridLayout(2, false));

		Label lblSearch = new Label(composite_6, SWT.NONE);
		lblSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblSearch.setText("Search");

		final Text text_1 = new Text(composite_6, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		text_1.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (text_1.getText().isEmpty()) {
					populateParameterSearchBox();
				} else {
					searchParameter(text_1.getText().toLowerCase());
				}

				parameterSearchTableViewer.refresh();
			}
		});
	}

	private void populateParameterSearchBox() {

		parameterSearchBoxItems.clear();
		parameterSearchBoxItemsFixed.clear();

		for (ParameterFile parameterFile : parameterFiles) {
			try {
				Map<String, String> parameterMap = new LinkedHashMap<>();
				parameterMap = ParameterFileManager.getInstance().getParameterMap(getParamterFileLocation(parameterFile));

				for (String paramater : parameterMap.keySet()) {
					ParameterWithFilePath parameterWithFilePath = new ParameterWithFilePath(
							paramater, parameterMap.get(paramater), parameterFile);

					if (!parameterSearchBoxItems
							.contains(parameterWithFilePath))
						parameterSearchBoxItems.add(parameterWithFilePath);
				}

			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

		}
		if (parameterSearchBoxItems.size() != 0) {
			parameterSearchTableViewer.setInput(parameterSearchBoxItems);
			parameterSearchBoxItemsFixed.addAll(parameterSearchBoxItems);
		}

		parameterSearchTableViewer.refresh();
	}

	private void setGridData(List<Parameter> parameterList,
			Map<String, String> parameters) {
		parameterList.clear();
		for (String parameter : parameters.keySet()) {
			parameterList.add(new Parameter(parameter, parameters
					.get(parameter)));
		}
	}

	private void createViewParameterFileBox(Composite composite) {
		HydroGroup  grpPropertyFileView = new HydroGroup(composite, SWT.NONE);
		grpPropertyFileView.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true, 1, 1));
		grpPropertyFileView.setHydroGroupText(MultiParameterFileDialogConstants.PARAMETER_FILE_VIEW);
		grpPropertyFileView.setHydroGroupBorderBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		GridLayout gl_grpPropertyFileView = new GridLayout(1, false);
		gl_grpPropertyFileView.verticalSpacing = 0;
		gl_grpPropertyFileView.marginHeight = 0;
		gl_grpPropertyFileView.horizontalSpacing = 0;
		gl_grpPropertyFileView.marginWidth = 0;
		grpPropertyFileView.setLayout(gl_grpPropertyFileView);
		grpPropertyFileView.getHydroGroupClientArea().setLayout(new GridLayout(1, false));
		Composite composite_4 = new Composite(grpPropertyFileView.getHydroGroupClientArea(), SWT.None);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		composite_4.setLayout(new GridLayout(1, false));

		Composite composite_8 = new Composite(composite_4, SWT.NONE);
		composite_8.setLayout(new GridLayout(5, false));
		composite_8.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false,
				false, 1, 1));

		Button btnAdd_1 = new Button(composite_8, SWT.NONE);
		btnAdd_1.setToolTipText(Messages.ADD_KEY_SHORTCUT_TOOLTIP);
		btnAdd_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		btnAdd_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addNewRow(parameterTableViewer);
				applyButton.setEnabled(true);
			}
		});
		btnAdd_1.setImage(ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
		

		Button btnDelete = new Button(composite_8, SWT.NONE);
		btnDelete.setToolTipText(Messages.DELETE_KEY_SHORTCUT_TOOLTIP);
		btnDelete.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				deleteRow(parameterTableViewer);
				applyButton.setEnabled(true);
			}
		});
		btnDelete.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		

		Button btnUp = new Button(composite_8, SWT.NONE);
		btnUp.setToolTipText(Messages.MOVE_UP_KEY_SHORTCUT_TOOLTIP);
		btnUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1));
		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveRowUp(parameterTableViewer);
				applyButton.setEnabled(true);
			}
		});
		btnUp.setImage(ImagePathConstant.MOVEUP_BUTTON.getImageFromRegistry());
		

		Button btnDown = new Button(composite_8, SWT.NONE);
		btnDown.setToolTipText(Messages.MOVE_DOWN_KEY_SHORTCUT_TOOLTIP);
		btnDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveRowDown(parameterTableViewer);
				applyButton.setEnabled(true);
			}
		});
		btnDown.setImage(ImagePathConstant.MOVEDOWN_BUTTON.getImageFromRegistry());
		new Label(composite_8, SWT.NONE);

        Composite composite_1 = new Composite(composite_4, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		
		parameterTableViewer = new TableViewer(composite_1, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		table_2 = parameterTableViewer.getTable();
		table_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table_2.setLinesVisible(true);
		table_2.setHeaderVisible(true);
		parameterTableViewer.setContentProvider(new ArrayContentProvider());
		parameterTableViewer.setData(TABLE_TYPE_KEY, "parameterTableViewer");
		attachShortcutListner(parameterTableViewer,table_2);
		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(parameterTableViewer, new FocusCellOwnerDrawHighlighter(parameterTableViewer));
		ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(parameterTableViewer);
		TableViewerEditor.create(parameterTableViewer, focusCellManager, activationSupport, ColumnViewerEditor.TABBING_HORIZONTAL | 
			    ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | 
			    ColumnViewerEditor.TABBING_VERTICAL |
			    ColumnViewerEditor.KEYBOARD_ACTIVATION);
		final TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(
				parameterTableViewer, SWT.NONE);
		ColumnViewerToolTipSupport.enableFor(parameterTableViewer,
				ToolTip.NO_RECREATE);
		TableColumn tblclmnParameterName_1 = tableViewerColumn_3.getColumn();
		tblclmnParameterName_1.setWidth(190);
		tblclmnParameterName_1
				.setText(MultiParameterFileDialogConstants.PARAMETER_NAME);
		tableViewerColumn_3.setEditingSupport(new ParameterEditingSupport(
				parameterTableViewer,
				MultiParameterFileDialogConstants.PARAMETER_NAME,this));
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			
			@Override
			public String getToolTipText(Object element) {
				Parameter p = (Parameter) element;
				if(StringUtils.isEmpty(p.getParameterName())){
					return ErrorMessages.NAME_VALUE_CANNOT_BE_BLANK;
				}
				else{
				return null;
				}
			}
			
		   
			
			@Override
			public String getText(Object element) {
				Parameter p = (Parameter) element;
				return p.getParameterName();
			}
			
			@Override
			public Color getBackground(Object element) {
				Parameter p = (Parameter) element;
				if(StringUtils.isEmpty(p.getParameterName())){
					return CustomColorRegistry.INSTANCE.getColorFromRegistry( 0xFF, 0xDD, 0xDD);
				}
				return super.getBackground(element);
			}
			
		});

		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(
				parameterTableViewer, SWT.NONE);
		TableColumn tblclmnParameterValue_1 = tableViewerColumn_5.getColumn();
		tblclmnParameterValue_1.setWidth(170);
		tblclmnParameterValue_1
				.setText(MultiParameterFileDialogConstants.PARAMETER_VALUE);
		tableViewerColumn_5.setEditingSupport(new ParameterEditingSupport(
				parameterTableViewer,
				MultiParameterFileDialogConstants.PARAMETER_VALUE,this));
		tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Parameter p = (Parameter) element;
				return p.getParameterValue();
			}
		});

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				parameterTableViewer, SWT.NONE);
		TableColumn tblclmnEdit = tableViewerColumn.getColumn();
		tblclmnEdit.setWidth(84);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();

				// DO NOT REMOVE THIS CONDITION. The condition is return to
				// prevent multiple updates on single item
				if (item.getData("UPDATED") == null) {
					item.setData("UPDATED", "TRUE");
				} else {
					return;
				}

				final Composite buttonPane = new Composite(parameterTableViewer
						.getTable(), SWT.NONE);
				buttonPane.setLayout(new FillLayout());

				final Button button = new Button(buttonPane, SWT.NONE);
				button.setText(MultiParameterFileDialogConstants.EDIT_BUTTON_TEXT);

				final TableEditor editor = new TableEditor(parameterTableViewer
						.getTable());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				editor.setEditor(buttonPane, item, cell.getColumnIndex());
				editor.layout();
				//Added Key Event on Edit Button
				button.addKeyListener(new KeyListener() {						
					
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
							deleteRow(parameterTableViewer);
						}
						
						else if (ctrlKeyPressed && event.keyCode == Constants.KEY_N){
							addNewRow(parameterTableViewer);
						}
						
						else if (ctrlKeyPressed && event.keyCode == SWT.ARROW_UP){
							moveRowUp(parameterTableViewer);				
						}
						
						else if (ctrlKeyPressed && event.keyCode == SWT.ARROW_DOWN){
							moveRowDown(parameterTableViewer);
						}
					}
				});
				button.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						String initialParameterValue = item
								.getText(PROPERTY_VALUE_COLUMN_INDEX);
						ParamterValueDialog paramterValueDialog = new ParamterValueDialog(
								getShell(), XMLUtil
										.formatXML(initialParameterValue));
						paramterValueDialog.open();

						int index = Arrays.asList(
								parameterTableViewer.getTable().getItems())
								.indexOf(item);

						if (StringUtils.isNotEmpty(paramterValueDialog
								.getParamterValue())) {
							String newParameterValue = paramterValueDialog
									.getParamterValue().replaceAll("\r", " ")
									.replaceAll("\n", " ").replaceAll("\t", " ")
									.replace("  ", " ");
							parameters.get(index).setParameterValue(
									newParameterValue);
						}else{
							parameters.get(index).setParameterValue("");
						}
						
						parameterTableViewer.refresh();
						applyButton.setEnabled(true);
					}
				});
				
				item.addDisposeListener(new DisposeListener() {

					@Override
					public void widgetDisposed(DisposeEvent e) {
						button.dispose();
						buttonPane.dispose();
						editor.dispose();
					}
				});
			}
		});
		
		parameterTableViewer.setInput(parameters);
		getShell().setFocus();		
		enableTabbing(filePathTableViewer);
		setTableLayoutToMappingTable(parameterTableViewer);
		
		parameterTableViewer.getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				addNewRow(parameterTableViewer);
				applyButton.setEnabled(true);
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}
		});
	}
	
	public Button getApplyButton() {
		return applyButton;
	}

	private void addNewRow(TableViewer parameterTableViewer){
	Parameter parameter = new Parameter(
			MultiParameterFileDialogConstants.DefaultParameter,
			MultiParameterFileDialogConstants.DefaultValue);
		parameters.add(parameter);
		parameterTableViewer.refresh();
		if (parameters.size() != 0) {
			parameterTableViewer.editElement(parameterTableViewer.getElementAt(parameters.size() - 1), 0);
		} else {
			parameterTableViewer.editElement(parameterTableViewer.getElementAt(0), 0);
		}
	
	}
	
	private void deleteRow(TableViewer parameterTableViewer ){
		Table table = parameterTableViewer.getTable();
		int selectionIndex = table.getSelectionIndex();
		int[] indexs = table.getSelectionIndices();
		if (selectionIndex == -1) {
			WidgetUtility
					.errorMessage(ErrorMessages.SELECT_ROW_TO_DELETE);
		} else {
			table.remove(indexs);
			int itemsRemoved = 0;
			for (int index : indexs) {
				parameters.remove(index - itemsRemoved);
				itemsRemoved++;
			}
			parameterTableViewer.getTable().removeAll();
			parameterTableViewer.refresh();
		}
		
		if(indexs.length == 1 && parameters.size() > 0){//only one item is deleted
			if(parameters.size() == 1){//list contains only one element
				table.select(0);// select the first element
				parameterTableViewer.editElement(parameterTableViewer.getElementAt(0), 0);
			}
			else if(parameters.size() == indexs[0]){//deleted last item 
				table.select(parameters.size() - 1);//select the last element which now at the end of the list
				parameterTableViewer.editElement(parameterTableViewer.getElementAt(parameters.size() - 1), 0);
			}
			else if(parameters.size() > indexs[0]){//deleted element from middle of the list
				table.select( indexs[0] == 0 ? 0 : (indexs[0] - 1) );//select the element from at the previous location
				parameterTableViewer.editElement(parameterTableViewer.getElementAt(indexs[0] == 0 ? 0 : (indexs[0] - 1)), 0);
			}
		}
		else if(indexs.length >= 2){//multiple items are selected for deletion
			if(indexs[0] == 0){//delete from 0 to ...
				if(parameters.size() >= 1){//list contains only one element
					table.select(0);//select the remaining element
					parameterTableViewer.editElement(parameterTableViewer.getElementAt(0), 0);
				}
			}
			else{//delete started from element other than 0th element
				table.select((indexs[0])-1);//select element before the start of selection   
				parameterTableViewer.editElement(parameterTableViewer.getElementAt((indexs[0])-1), 0);
			}
		}
	}
	
	private void moveRowUp( TableViewer parameterTableViewer ){
		Table table = parameterTableViewer.getTable();
		int[] indexes = table.getSelectionIndices();
		for (int index : indexes) {

			if (index > 0) {
				Collections.swap((List<Parameter>) parameters, index,
						index - 1);
				parameterTableViewer.refresh();

			}
		}
	}
	
	
	private void moveRowDown(TableViewer parameterTableViewer){
		Table table = parameterTableViewer.getTable();
		int[] indexes = table.getSelectionIndices();
		for (int i = indexes.length - 1; i > -1; i--) {

			if (indexes[i] < parameters.size() - 1) {
				Collections.swap((List<Parameter>) parameters,
						indexes[i], indexes[i] + 1);
				parameterTableViewer.refresh();

			}
		}
	}
	
	
	private void attachShortcutListner(final TableViewer parameterTableViewer,Table table){
		Control currentControl = table;
		
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
					deleteRow(parameterTableViewer);
				}
				
				else if (ctrlKeyPressed && event.keyCode == Constants.KEY_N){
					addNewRow(parameterTableViewer);
				}
				
				else if (ctrlKeyPressed && event.keyCode == SWT.ARROW_UP){
					moveRowUp(parameterTableViewer);				
				}
				
				else if (ctrlKeyPressed && event.keyCode == SWT.ARROW_DOWN){
					moveRowDown(parameterTableViewer);
				}
			}
		});
	}

	private boolean saveParameters() {
		String currentFilePath = (String) parameterTableViewer
				.getData(MultiParameterFileDialogConstants.CURRENT_PARAM_FILE);
		if (!StringUtils.isEmpty(currentFilePath)) {
			
			Map<String, String> parameterMap = new LinkedHashMap<>();
			for (Parameter parameter : parameters) {
				if(StringUtils.isEmpty(parameter.getParameterName())){
					MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
					messageBox.setText(ErrorMessages.WARNING);
					messageBox.setMessage(ErrorMessages.BLANK_PARAMETER_WILL_BE_LOST);
					int response = messageBox.open();
					if (response != SWT.OK) {
						return false;
					}
				}else{
				parameterMap.put(parameter.getParameterName(),
						parameter.getParameterValue());
				}
			}
			try {
				ParameterFileManager.getInstance().storeParameters(parameterMap, null, currentFilePath);
				ifNotified = false;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		populateParameterSearchBox();
		return true;
	}

	private Composite createParameterFileViewOuterComposite(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);
		return composite;
	}

	private void createParameterFilesBox(Composite container) {
				
		mainSashForm.setLayout(new GridLayout(2, false));

		Composite composite_1 = new Composite(mainSashForm, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1);
		composite_1.setLayoutData(gd_composite_1);
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.horizontalSpacing = 0;
		composite_1.setLayout(gl_composite_1);
		HydroGroup grpPropertyFiles = new HydroGroup(composite_1, SWT.NONE);
		grpPropertyFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1));
		GridLayout gl_grpPropertyFiles = new GridLayout(1, false);
		gl_grpPropertyFiles.marginWidth = 0;
		gl_grpPropertyFiles.marginHeight = 0;
		gl_grpPropertyFiles.horizontalSpacing =0;
		gl_grpPropertyFiles.verticalSpacing =0;
		grpPropertyFiles.setLayout(gl_grpPropertyFiles);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		grpPropertyFiles.getHydroGroupClientArea().setLayout(gridLayout);
		grpPropertyFiles.setHydroGroupText(MultiParameterFileDialogConstants.TABLE_COLUMN_LIST_OF_PARAMETER_FILES);
		grpPropertyFiles.setHydroGroupBorderBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		composite_9 = new Composite(grpPropertyFiles.getHydroGroupClientArea(), SWT.NONE);
		composite_9.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));
		GridLayout gl_composite_9 = new GridLayout(1, false);
		gl_composite_9.verticalSpacing = 0;
		gl_composite_9.marginHeight = 0;
		gl_composite_9.marginWidth = 0;
		composite_9.setLayout(gl_composite_9);

		createParameterFilesBoxButtonPanel(composite_9);

		createParameterFilesBoxGridView(composite_9);
		
		createParameterFilesBoxTrashBox(composite_9);
	}

	private void createParameterFilesBoxTrashBox(Composite composite_2) {
		Composite composite_1 = new Composite(composite_2, SWT.None);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		composite_1.setData("org.eclipse.e4.ui.css.id", "ParameterFileDropBox");
		
		Composite composite_1_1 = new Composite(composite_1, SWT.NONE);
		composite_1_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite_1_1 = new GridLayout(1, false);
		composite_1_1.setLayout(gl_composite_1_1);
		composite_1_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		composite_1_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		Label lblDrop1 = new Label(composite_1_1, SWT.NONE);
		lblDrop1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		lblDrop1.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDrop1.setText(DROP_BOX_TEXT);
		
		Composite composite_1_2 = new Composite(composite_1, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		composite_1_2.setLayout(gl_composite);
		composite_1_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		composite_1_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		Label lblForImage = new Label(composite_1_2, SWT.NONE);
		lblForImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		lblForImage.setImage(ImagePathConstant.MULTI_PARAMETERGRID_DROP_BOX.getImageFromRegistry());
	   
		DropTarget dt = new DropTarget(composite_1, DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter() {

			public void drop(DropTargetEvent event) {
				List<ParameterFile> filesToRemove = new ArrayList<>();
				;
				try {

					filesToRemove = (List) deserializeFromString((String) event.data);
				} catch (UnsupportedEncodingException e) {
					logger.debug(
							ErrorMessages.UNABLE_TO_REMOVE_JOB_SPECIFIC_FILE, e);

					MessageBox messageBox = new MessageBox(new Shell(),
							SWT.ICON_ERROR | SWT.OK);

					messageBox.setText(MessageType.INFO.messageType());
					messageBox
							.setMessage(ErrorMessages.UNABLE_TO_REMOVE_JOB_SPECIFIC_FILE);
					messageBox.open();
				}

				ParameterFile jobSpecificFile = getJobSpecificFile();

				if (jobSpecificFile != null
						&& filesToRemove.contains(jobSpecificFile)) {
					filesToRemove.remove(jobSpecificFile);

					MessageBox messageBox = new MessageBox(new Shell(),
							SWT.ICON_INFORMATION | SWT.OK);

					messageBox.setText(MessageType.INFO.messageType());
					messageBox
							.setMessage(ErrorMessages.UNABLE_TO_REMOVE_JOB_SPECIFIC_FILE);
					messageBox.open();
				}

				parameterFiles.removeAll(filesToRemove);
				jobLevelParamterFiles.retainAll(parameterFiles);
				
				filePathTableViewer.refresh();
				populateParameterSearchBox();
				populateViewParameterFileBox(jobSpecificFile);
				applyButton.setEnabled(true);
			}
		});
	}

	private void createParameterFilesBoxGridView(Composite composite_2) {
				
		Composite composite_4 = new Composite(composite_2, SWT.NONE);
		composite_4.setLayout(new GridLayout(1, false));
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
				1, 1));

		filePathTableViewer = new TableViewer(composite_4, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		Table table = filePathTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		filePathTableViewer.setContentProvider(new ArrayContentProvider());
		ColumnViewerToolTipSupport.enableFor(filePathTableViewer,
				ToolTip.NO_RECREATE);
		enableTabbing(filePathTableViewer);
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		filePathTableViewer.addDragSupport(operations, transferTypes,
				new DragSourceListener() {

					@Override
					public void dragStart(DragSourceEvent event) {
						// Do Nothing
					}

					@Override
					public void dragSetData(DragSourceEvent event) {
						TableItem[] selectedTableItems = filePathTableViewer
								.getTable().getSelection();
						ArrayList<ParameterFile> filePathList = new ArrayList<ParameterFile>();
						for (TableItem selectedItem : selectedTableItems) {
							ParameterFile filePath = (ParameterFile) selectedItem
									.getData();
							filePathList.add(filePath);
						}
						try {
							event.data = serializeToString(filePathList);
						} catch (UnsupportedEncodingException e) {
							logger.debug(
									ErrorMessages.UNABLE_TO_REMOVE_JOB_SPECIFIC_FILE,
									e);

							MessageBox messageBox = new MessageBox(new Shell(),
									SWT.ICON_ERROR | SWT.OK);

							messageBox.setText(MessageType.INFO.messageType());
							messageBox
									.setMessage(ErrorMessages.UNABLE_TO_REMOVE_JOB_SPECIFIC_FILE);
							messageBox.open();
						}
					}

					@Override
					public void dragFinished(DragSourceEvent event) {
						// Do Nothing
					}
				});

		filePathTableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						if(!ifNotified){
						if(!saveParameters()){
							ifNotified = true;
							filePathTableViewer.setSelection(previousSelection);
							ifNotified=false;
							return;
						}
						IStructuredSelection selection = (IStructuredSelection) filePathTableViewer
								.getSelection();
						ParameterFile selectedFile = (ParameterFile) selection
								.getFirstElement();
						if (selectedFile != null) {
							previousSelection = (IStructuredSelection) filePathTableViewer.getSelection();;
							populateViewParameterFileBox(selectedFile);
						}
						}
						else{
							return;
						}
					}
				});

		final TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(
				filePathTableViewer, SWT.NONE);
		TableColumn tblclmnFilePath = tableViewerColumn_4.getColumn();
		tblclmnFilePath.setWidth(280);
		tblclmnFilePath
				.setText(MultiParameterFileDialogConstants.TABLE_COLUMN_LIST_OF_PARAMETER_FILES);

		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getToolTipText(Object element) {
				return ((ParameterFile) element).getFilePathViewString();
			}

			@Override
			public Point getToolTipShift(Object object) {
				return new Point(5, 5);
			}

			@Override
			public int getToolTipDisplayDelayTime(Object object) {
				return 100; // msec
			}

			@Override
			public int getToolTipTimeDisplayed(Object object) {
				return 5000; // msec
			}

			@Override
			public Color getToolTipBackgroundColor(Object object) {
				return Display.getCurrent().getSystemColor(
						SWT.COLOR_WIDGET_BACKGROUND);
			}

			@Override
			public Color getBackground(Object element) {

				return super.getBackground(element);
			}

			@Override
			public Color getForeground(Object element) {
				ParameterFile parameterFile = (ParameterFile) element;
				if (parameterFile.getFileType().equals(ParamterFileTypes.JOB_SPECIFIC))
					return CustomColorRegistry.INSTANCE.getColorFromRegistry( 0, 0, 255);
				return super.getForeground(element);
			}

			@Override
			public String getText(Object element) {
				ParameterFile p = (ParameterFile) element;
				return p.getFilePathViewString();
			}
		});

		setTableLayoutToMappingTable(filePathTableViewer);
	}
	
	private FileDialog initializeFileDialog(Shell shell) {
		String[] filterExt = { "*.properties" };
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
		fileDialog.setText(MultiParameterFileDialogConstants.OPEN_FILE_DIALOG_NAME);				
		fileDialog.setFilterExtensions(filterExt);
		return fileDialog;
	}
	
	private boolean importParamterFileToProject(String[] listOfFilesToBeImported, String source,String destination, ParamterFileTypes paramterFileTypes) {

		for (String fileName : listOfFilesToBeImported) {
			String absoluteFileName = source + fileName;
			IPath destinationIPath=new Path(destination);
			destinationIPath=destinationIPath.append(fileName);
			File destinationFile=destinationIPath.toFile();
			try {
				if (!ifDuplicate(listOfFilesToBeImported, paramterFileTypes)) {
					if (StringUtils.equalsIgnoreCase(absoluteFileName, destinationFile.toString())) {
						return true;
					} else if (destinationFile.exists()) {
						int returnCode = doUserConfirmsToOverRide();
						if (returnCode == SWT.YES) {
							FileUtils.copyFileToDirectory(new File(absoluteFileName), new File(destination));
						} else if (returnCode == SWT.NO) {
							return true;
						} else {
							return false;
						}
					} else {
						FileUtils.copyFileToDirectory(new File(absoluteFileName), new File(destination));
					}
				}
			} catch (IOException e1) {
				if(StringUtils.endsWithIgnoreCase(e1.getMessage(), ErrorMessages.IO_EXCEPTION_MESSAGE_FOR_SAME_FILE)){
					return true;
				}
				MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
				messageBox.setText(MessageType.ERROR.messageType());
				messageBox.setMessage(ErrorMessages.UNABLE_TO_POPULATE_PARAM_FILE + " " + e1.getMessage());
				messageBox.open();				
				logger.error("Unable to copy prameter file in current project work space");
				return false;
			}
		}
		return true;
	}
	
	
	private int doUserConfirmsToOverRide() {
		MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO|SWT.CANCEL);
		messageBox.setMessage("File already exists in project, do you want to overwrite?");
		return messageBox.open();
	}

	
	
	private boolean isParamterFileNameExistInFileGrid(String[] listOfFilesToBeImported, ParamterFileTypes paramterFileTypes) {
		if (ifDuplicate(listOfFilesToBeImported, paramterFileTypes)) {
			MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_INFORMATION | SWT.OK);
			messageBox.setText(MessageType.INFO.messageType());
			messageBox.setMessage(ErrorMessages.FILE_EXIST);
			messageBox.open();
			return true;
		}
		return false;
	}
	
	private String getFileLocation(String importedParamterFile) {
		IPath iPath = new Path(importedParamterFile);
		String importedFileLocation = iPath.removeLastSegments(1).toOSString() + File.separator;
		return importedFileLocation;
	}
	
	private void updateParameterGridWindow(String[] listOfFilesToBeImported, String importLocation,ParamterFileTypes paramterFileTypes) {
		for (String fileName : listOfFilesToBeImported) {
			if (fileName != null) {
				String absoluteFileName = importLocation + fileName;
				parameterFileTextBox.setText(absoluteFileName);
				parameterFiles.add(new ParameterFile(fileName, paramterFileTypes));
				if(paramterFileTypes.equals(ParamterFileTypes.JOB_LEVEL)){
					jobLevelParamterFiles.add(new ParameterFile(fileName, paramterFileTypes));
				}
				try {
					parameterTableViewer.setData(MultiParameterFileDialogConstants.CURRENT_PARAM_FILE,
							absoluteFileName);
					Map<String, String> parameterMap = new LinkedHashMap<>();
					parameterMap = ParameterFileManager.getInstance().getParameterMap(absoluteFileName);
					setGridData(parameters, parameterMap);
				} catch (IOException ioException) {
					MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
					messageBox.setText(MessageType.ERROR.messageType());
					messageBox.setMessage(ErrorMessages.UNABLE_TO_POPULATE_PARAM_FILE
							+ ioException.getMessage());
					messageBox.open();
					logger.debug("Unable to populate parameter file", ioException.getMessage());
				}
				filePathTableViewer.refresh();
				parameterTableViewer.refresh();
				populateParameterSearchBox();
			}
		}
		if(paramterFileTypes.equals(ParamterFileTypes.JOB_LEVEL)){
			getComponentCanvas().addJobLevelParamterFiles(jobLevelParamterFiles);
		}
		parameterFileTextBox.setText("");
	}
	
	private void addFilesToParamterGrid(Shell shell,String importDirectoryLocation,ParamterFileTypes paramterFileTypes) {
		String importLocation = activeProjectLocation + File.separator + importDirectoryLocation + File.separator;
		
		if (!saveParameters()) {
			return;
		}

		String[] listOfFilesToBeImported ;
		String fileToBeImport;
		if(parameterFileTextBox.getText().isEmpty()){
			FileDialog fileDialog = initializeFileDialog(shell);
			fileToBeImport = fileDialog.open();
			if (StringUtils.isBlank(fileToBeImport)) {
				return;
			}
			listOfFilesToBeImported = fileDialog.getFileNames();
		}else{
			java.nio.file.Path path = Paths.get(parameterFileTextBox.getText());
			listOfFilesToBeImported = new String[1];
			listOfFilesToBeImported[0] = path.getFileName().toString();
			fileToBeImport = parameterFileTextBox.getText();
		}
		
		String locationOfFilesToBeImported = getFileLocation(fileToBeImport);
		
		if(!importParamterFileToProject(listOfFilesToBeImported, locationOfFilesToBeImported,importLocation,paramterFileTypes)){
			return;
		}
		
		if(isParamterFileNameExistInFileGrid(listOfFilesToBeImported, paramterFileTypes)){
			return;
		}
		
		updateParameterGridWindow(listOfFilesToBeImported, importLocation,paramterFileTypes);
	}
	
	private ParameterFile getNewValue(Shell shell,final String value,ParamterFileTypes paramterFileTypes ) {
	
	
	    InputDialog paramterFileName = new InputDialog(shell, "Paramter File",
	            "Enter paramter file name", value, new IInputValidator() {

	    	@Override
	        public String isValid(final String string) {
	            if (StringUtils.isEmpty(string) || !string.matches(FILE_NAME_VALIDATION_EXPRESSION)) {
	                return Messages.PARAMETER_FIELD_VALIDATION;
	            }
	            
	            ParameterFile parameterFile = new ParameterFile(string + ".properties", paramterFileTypes);
				if(parameterFiles.contains(parameterFile)){
					return Messages.PARAMETER_FILE_EXISTS;
				}
	            
	            return null;
	        }	    	
	    });
	    if (paramterFileName.open() == Window.OK) {
	    	 ParameterFile parameterFile = new ParameterFile(paramterFileName.getValue() + ".properties", paramterFileTypes);
	        return parameterFile;
	    } else {
	        return null;
	    }
	}
	
	private void createNewParamterFile(String file) throws IOException{
		FileUtils.touch(new File(file));
	}
	 
	
	private void createParameterFilesBoxButtonPanel(Composite composite_2) {
		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,false, 1, 1));
		GridLayout gridLayout=new GridLayout(8, false);
        if(OSValidator.isMac()){	
        	gridLayout.horizontalSpacing=-10;
        }
		composite_3.setLayout(gridLayout);
		Label lblFile = new Label(composite_3, SWT.NONE);
		lblFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,1, 1));
		lblFile.setText("File");
		lblFile.setVisible(false);
		parameterFileTextBox = new Text(composite_3, SWT.BORDER);
		parameterFileTextBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,true, true, 1, 1));
		parameterFileTextBox.setVisible(false);
		Button btnAddJob = new Button(composite_3, SWT.NONE);
		btnAddJob.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ParameterFile paramterFile=getNewValue(e.widget.getDisplay().getActiveShell(), "jobprameter",ParamterFileTypes.JOB_LEVEL);
				if(paramterFile!=null){
					String importLocation = activeProjectLocation + File.separator + MultiParameterFileDialogConstants.JOB_PARAMETER_DIRECTORY_NAME + File.separator ;
					String paramterFileAbsilutePath = importLocation + paramterFile.getFileName();
					try {
						createNewParamterFile(paramterFileAbsilutePath);
						String[] listOfFilesToBeImported = new String[1];
						listOfFilesToBeImported[0] = paramterFile.getFileName();
						updateParameterGridWindow(listOfFilesToBeImported, importLocation, ParamterFileTypes.JOB_LEVEL);
						applyButton.setEnabled(true);
					} catch (IOException e1) {
						logger.debug("Unable to add parameter file",e1);
					}
				}				
			}
		});
		btnAddJob.setText(" Add Job ");
		
		Button btnLoadJob = new Button(composite_3, SWT.NONE);
		btnLoadJob.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ParameterFile paramterFile=getNewValue(e.widget.getDisplay().getActiveShell(), "projectprameter",ParamterFileTypes.PROJECT_LEVEL);
				if(paramterFile!=null){
					String importLocation = activeProjectLocation + File.separator + MultiParameterFileDialogConstants.GLOBAL_PARAMETER_DIRECTORY_NAME + File.separator ;
					String paramterFileAbsilutePath = importLocation + paramterFile.getFileName();
					try {
						createNewParamterFile(paramterFileAbsilutePath);
						String[] listOfFilesToBeImported = new String[1];
						listOfFilesToBeImported[0] = paramterFile.getFileName();
						updateParameterGridWindow(listOfFilesToBeImported, importLocation, ParamterFileTypes.PROJECT_LEVEL);
						applyButton.setEnabled(true);
					} catch (IOException e1) {
						logger.debug("Unable to add parameter file",e1);
					}
				}
			}
		});
		btnLoadJob.setText("Add Project ");
		
		Button btnAddJobParam = new Button(composite_3, SWT.NONE);
		btnAddJobParam.setText("Load Job");
		final Button btnAddProjectParam = new Button(composite_3, SWT.NONE);
		btnAddProjectParam.setText("Load Project");
		
		Button btnUp_1 = new Button(composite_3, SWT.NONE);
		btnUp_1.setToolTipText(Messages.MOVE_SCHEMA_UP_TOOLTIP);
		btnUp_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Table table = filePathTableViewer.getTable();
				int[] indexes = table.getSelectionIndices();
				for (int index : indexes) {
					if (index > 0) {
						Collections.swap((List<ParameterFile>) parameterFiles,
								index, index - 1);
						filePathTableViewer.refresh();
					}
				}
				applyButton.setEnabled(true);
			}
		});
		btnUp_1.setImage(ImagePathConstant.MOVEUP_BUTTON.getImageFromRegistry());
		
		Button btnDown_1 = new Button(composite_3, SWT.NONE);
		btnDown_1.setToolTipText(Messages.MOVE_SCHEMA_DOWN_TOOLTIP);
		btnDown_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Table table = filePathTableViewer.getTable();
				int[] indexes = table.getSelectionIndices();
				for (int i = indexes.length - 1; i > -1; i--) {
					if (indexes[i] < parameterFiles.size() - 1) {
						Collections.swap((List<ParameterFile>) parameterFiles,indexes[i], indexes[i] + 1);
						filePathTableViewer.refresh();
					}
				}
				applyButton.setEnabled(true);
			}
		});
		btnDown_1.setImage(ImagePathConstant.MOVEDOWN_BUTTON.getImageFromRegistry());
		btnAddProjectParam.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addFilesToParamterGrid(btnAddProjectParam.getShell(),MultiParameterFileDialogConstants.GLOBAL_PARAMETER_DIRECTORY_NAME,ParamterFileTypes.PROJECT_LEVEL);
				applyButton.setEnabled(true);
			}			
		});
		btnAddJobParam.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addFilesToParamterGrid(btnAddProjectParam.getShell(),MultiParameterFileDialogConstants.JOB_PARAMETER_DIRECTORY_NAME,ParamterFileTypes.JOB_LEVEL);
				applyButton.setEnabled(true);
			}
		});
	}

	private boolean ifDuplicate(String file[], ParamterFileTypes paramterFileTypes) {
		for (int i = 0; i < file.length; i++) {
			ParameterFile parameterFile = new ParameterFile(file[i], paramterFileTypes) ;
			if(parameterFiles.contains(parameterFile)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		 Button okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,true);
		okButton.setFocus();
		applyButton = createButton(parent, IDialogConstants.NO_ID, "Apply", false);
		applyButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, false);
	}


	/**
	 * 
	 * Set parameter file list
	 * 
	 * @param parameterFiles
	 */
	public void setParameterFiles(List<ParameterFile> parameterFiles) {
		this.parameterFiles = parameterFiles;
	}

	private void updateParamterFileSequence(){
		if(!getComponentCanvas().getParamterFileSequence().isEmpty()){
			parameterFiles.clear();
			parameterFiles.addAll(getComponentCanvas().getParamterFileSequence());
		}
		
	}
	
	private void populateFilePathTableViewer() {
		
		updateParamterFileSequence();
		
		filePathTableViewer.setInput(parameterFiles);
		filePathTableViewer.refresh();
	}
	
	@Override
	protected void okPressed() {
		saveParamterDialogChanges();
		super.okPressed();
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.NO_ID){
			saveParamterDialogChanges();
			applyButton.setEnabled(false);
		}
		super.buttonPressed(buttonId);
	}

	private void saveParamterDialogChanges() {
		List<ParameterFile> tempParameterFiles = new LinkedList<>();
		tempParameterFiles.addAll(parameterFiles);

		getComponentCanvas().saveParamterFileSequence(parameterFiles);
		
		tempParameterFiles.removeAll(jobLevelParamterFiles);
		
		saveParameters();

			tempParameterFiles.remove(getJobSpecificFile());
			getComponentCanvas().addJobLevelParamterFiles(jobLevelParamterFiles);





		runGraph = true;
		okPressed = true;
	}

	/**
	 * 
	 * Returns true if we have all valid parameter file list
	 * 
	 * @return
	 */
	public boolean canRunGraph() {
		return runGraph;
	}

	@Override
	protected void cancelPressed() {
		runGraph = false;
		super.cancelPressed();
	}

	/**
	 * 
	 * Returns list of parameter files(comma separated)
	 * 
	 * @return
	 */
	public String getParameterFilesForExecution() {

		String activeParameterFiles = "";

		for (ParameterFile parameterFile : parameterFiles) {
			activeParameterFiles = activeParameterFiles + getParamterFileLocation(parameterFile) + ",";
		}
		if (activeParameterFiles.length() != 0)
			return activeParameterFiles.substring(0,
					activeParameterFiles.length() - 1);
		else
			return activeParameterFiles;
	}

	@Override
	public boolean close() {
		if (!okPressed)
			runGraph = false;

		return super.close();
	}

	/**
	 * 
	 * Serialize object to string. Serialized string will not be in human
	 * readable format
	 * 
	 * @param input
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	private String serializeToString(Serializable input)
			throws UnsupportedEncodingException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		ObjectOutputStream out = null;
		try {
			// stream closed in the finally
			out = new ObjectOutputStream(baos);
			out.writeObject(input);

		} catch (IOException ex) {
			throw new SerializationException(ex);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}

		byte[] repr = baos.toByteArray();
		String decoded = new String(base64.encode(repr));
		return decoded;
	}

	/**
	 * 
	 * deserialize string converted to Object.
	 * 
	 * @param input
	 * @return Object
	 * @throws UnsupportedEncodingException
	 */
	private Object deserializeFromString(String input)
			throws UnsupportedEncodingException {
		byte[] repr = base64.decode(input.getBytes());
		ByteArrayInputStream bais = new ByteArrayInputStream(repr);

		ObjectInputStream in = null;
		try {
			// stream closed in the finally
			 List<String> acceptedObjectList = new ArrayList<String>();
			 acceptedObjectList.add(java.util.ArrayList.class.getName());
			 acceptedObjectList.add(ParameterFile.class.getName());
			 acceptedObjectList.add(ParamterFileTypes.class.getName());
			 acceptedObjectList.add(java.lang.Enum.class.getName());
			 in = new LookAheadObjectInputStream(bais,acceptedObjectList);
			return in.readObject();

		} catch (ClassNotFoundException ex) {
			throw new SerializationException(ex);
		}catch (InvalidClassException ex) {
			int shellStyle= SWT.APPLICATION_MODAL | SWT.OK | SWT.ICON_ERROR;
			org.eclipse.swt.widgets.MessageBox messageBox = new  org.eclipse.swt.widgets.MessageBox(Display.getDefault().getActiveShell(),shellStyle);
			messageBox.setText("Invalid file data");
			messageBox.setMessage("Invalid entry in list");
			messageBox.open();
		    throw new SerializationException(ex);
		} 
		catch (IOException ex) {
			throw new SerializationException(ex);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}
	}

	private void enableTabbing(TableViewer tableViewer) {
		TableViewerEditor.create(tableViewer,
				new ColumnViewerEditorActivationStrategy(tableViewer),
				ColumnViewerEditor.KEYBOARD_ACTIVATION
						| ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL);
	}

	private void setTableLayoutToMappingTable(TableViewer tableViewer) {
		TableColumnLayout layout = new TableColumnLayout();
		tableViewer.getControl().getParent().setLayout(layout);

		for (int columnIndex = 0, n = tableViewer.getTable().getColumnCount(); columnIndex < n; columnIndex++) {
			tableViewer.getTable().getColumn(columnIndex).pack();
		}

		for (int i = 0; i < tableViewer.getTable().getColumnCount(); i++) {
			if(tableViewer.getData(TABLE_TYPE_KEY) == null ){
				layout.setColumnData(tableViewer.getTable().getColumn(i),
						new ColumnWeightData(1));	
			}else{
				if(i!=2){
					layout.setColumnData(tableViewer.getTable().getColumn(i),
							new ColumnWeightData(3));	
				}else{
					layout.setColumnData(tableViewer.getTable().getColumn(i),
							new ColumnWeightData(1));	
				}
				
			}
			
		}
	}
	
	/**
	 * 
	 * Returns active editor as {@link DefaultGEFCanvas}
	 * 
	 * @return {@link DefaultGEFCanvas}
	 */
	private DefaultGEFCanvas getComponentCanvas() {
		DefaultGEFCanvas activeEditor = (DefaultGEFCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if( activeEditor instanceof DefaultGEFCanvas){
			return activeEditor;
		}
		else{
			return null;
		}
	}
	
	public List<ParameterFile> getJobLevelParamterFiles() {
		return jobLevelParamterFiles;
	}
	
	public void setJobLevelParamterFiles(List list) {
		this.jobLevelParamterFiles.addAll(list);
	}
	
	@Override
	protected Point getInitialSize() {
		if(OSValidator.isMac()){
			return new Point(900,476);
		}
		return new Point(800,476);
	}
}
