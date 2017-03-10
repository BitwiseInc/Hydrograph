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

package hydrograph.ui.dataviewer.window;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import hydrograph.ui.common.datastructures.dataviewer.JobDetails;
import hydrograph.ui.common.schema.Field;
import hydrograph.ui.common.schema.Fields;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.PreferenceConstants;
import hydrograph.ui.common.util.SWTResourceManager;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.dataviewer.Activator;
import hydrograph.ui.dataviewer.actions.ActionFactory;
import hydrograph.ui.dataviewer.actions.AutoExpandColumnsAction;
import hydrograph.ui.dataviewer.actions.ClearFilterAction;
import hydrograph.ui.dataviewer.actions.CopyAction;
import hydrograph.ui.dataviewer.actions.DatasetInformationAction;
import hydrograph.ui.dataviewer.actions.ExportAction;
import hydrograph.ui.dataviewer.actions.FilterAction;
import hydrograph.ui.dataviewer.actions.FindAction;
import hydrograph.ui.dataviewer.actions.FormattedViewAction;
import hydrograph.ui.dataviewer.actions.GridViewAction;
import hydrograph.ui.dataviewer.actions.PreferencesAction;
import hydrograph.ui.dataviewer.actions.ReloadAction;
import hydrograph.ui.dataviewer.actions.ResetColumnsAction;
import hydrograph.ui.dataviewer.actions.ResetSortAction;
import hydrograph.ui.dataviewer.actions.SelectAllAction;
import hydrograph.ui.dataviewer.actions.SelectColumnAction;
import hydrograph.ui.dataviewer.actions.UnformattedViewAction;
import hydrograph.ui.dataviewer.actions.ViewDataGridMenuCreator;
import hydrograph.ui.dataviewer.adapters.DataViewerAdapter;
import hydrograph.ui.dataviewer.constants.ControlConstants;
import hydrograph.ui.dataviewer.constants.DataViewerColors;
import hydrograph.ui.dataviewer.constants.MenuConstants;
import hydrograph.ui.dataviewer.constants.Messages;
import hydrograph.ui.dataviewer.constants.StatusConstants;
import hydrograph.ui.dataviewer.constants.Views;
import hydrograph.ui.dataviewer.datastructures.RowData;
import hydrograph.ui.dataviewer.datastructures.StatusMessage;
import hydrograph.ui.dataviewer.filemanager.DataViewerFileManager;
import hydrograph.ui.dataviewer.filter.FilterConditions;
import hydrograph.ui.dataviewer.listeners.DataViewerListeners;
import hydrograph.ui.dataviewer.preferencepage.ViewDataPreferencesVO;
import hydrograph.ui.dataviewer.support.SortDataType;
import hydrograph.ui.dataviewer.support.SortOrder;
import hydrograph.ui.dataviewer.support.StatusManager;
import hydrograph.ui.dataviewer.support.TypeBasedComparator;
import hydrograph.ui.dataviewer.utilities.Utils;
import hydrograph.ui.dataviewer.utilities.ViewDataSchemaHelper;
import hydrograph.ui.dataviewer.viewloders.DataViewLoader;
import hydrograph.ui.logging.factory.LogFactory;
/**
 * The Class DebugDataViewer.
 * Builds Data viewer window
 * 
 * @author Bitwise
 *
 */
public class DebugDataViewer extends ApplicationWindow {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(DebugDataViewer.class);

	private CTabFolder tabFolder;

	private StyledText unformattedViewTextarea;
	private StyledText formattedViewTextarea;
	private TableViewer horizontalViewTableViewer;
	private TableViewer gridViewTableViewer;

	private DataViewerAdapter dataViewerAdapter;
	private Map<String, Control> windowControls;

	private List<RowData> gridViewData;
	private List<RowData> formattedViewData;
	private List<RowData> unformattedViewData;

	private String debugFileLocation;
	private String debugFileName;
	private double downlodedFileSize;

	private ActionFactory actionFactory;

	private DataViewLoader dataViewLoader;
	private DataViewerListeners dataViewerListeners;
	private ViewDataPreferencesVO viewDataPreferencesVO = new ViewDataPreferencesVO();

	private static final String DELIMITER = "delimiter";
	private static final String QUOTE_CHARACTOR = "quoteCharactor";
	private static final String INCLUDE_HEADERS = "includeHeader";
	private static final String DEFAULT_DELIMITER = ",";
	private static final String DEFAULT_QUOTE_CHARACTOR = "\"";
	private static final String DEFAULT = "default";
	private static final String FILE_SIZE = "VIEW_DATA_FILE_SIZE";
	private static final String PAGE_SIZE = "VIEW_DATA_PAGE_SIZE";
	private static final String DEFAULT_FILE_SIZE = "100";
	private static final String DEFAULT_PAGE_SIZE = "100";
	private static String SCHEMA_FILE_EXTENTION=".xml";

	private JobDetails jobDetails;
	private StatusManager statusManager;

	private Action dropDownAction;
	private String dataViewerWindowName;
	
	private Fields dataViewerFileSchema;
	
	private Map<String, DebugDataViewer> dataViewerMap;
	
	private SortOrder sortOrder;
		
	private TableCursor tableCursor;
	private Point selectionStartPoint ;
	private Point startCell=null;
	
	private volatile boolean shiftKeyPressed;
	private volatile boolean ctrlKeyPressed;
	private List<Point> currentSelection = new ArrayList<>();
	
	private Image ascending;
	private Image descending;
	
	private TableColumn recentlySortedColumn;
	private String sortedColumnName;
	
	private FilterConditions conditions;
	private String localCondition = "";
	private String remoteCondition = "";
	private boolean isOverWritten = false;
	
	
	/**
	 * Create the application window,
	 * 
	 * @wbp.parser.constructor
	 */
	@Deprecated
	public DebugDataViewer() {
		super(null);
		createActions();
		addCoolBar(SWT.FLAT);
		addMenuBar();
		addStatusLine();
		windowControls = new LinkedHashMap<>();
		gridViewData = new LinkedList<>();
		formattedViewData = new LinkedList<>();
	}

	
	public DebugDataViewer( JobDetails jobDetails, String dataViewerWindowName) {
		super(null);
		createActions();
		addCoolBar(SWT.FLAT);
		addMenuBar();
		addStatusLine();	
		this.jobDetails = jobDetails;
		this.dataViewerWindowName = dataViewerWindowName;
		windowControls = new LinkedHashMap<>();
		gridViewData = new LinkedList<>();
		formattedViewData = new LinkedList<>();
		sortOrder=SortOrder.DSC;
		
		ascending=ImagePathConstant.SORT_ASC.getImageFromRegistry();
		descending=ImagePathConstant.SORT_DESC.getImageFromRegistry();
	}

	/**
	 * @return Sorted Column Name
	 */
	public String getSortedColumnName() {
		return sortedColumnName;
	}
	
	/**
	 * 
	 * Returns sort order
	 * 
	 * @return
	 */
	public SortOrder getSortOrder() {
		return sortOrder;
	}
	
	/**
	 * 
	 * Set name of sorted column 
	 * 
	 * @param sortedColumnName
	 */
	public void setSortedColumnName(String sortedColumnName) {
		this.sortedColumnName = sortedColumnName;
	}

	
	/**
	 * 
	 * Get image for ascending order
	 * 
	 * @return ASC Image
	 */
	

	public Image getAscendingIcon() {
		return ascending;
	}

	/**
	 * 
	 *Get image for descending order
	 * 
	 * @return DES Image
	 */
	public Image getDescendingIcon() {
		return descending;
	}


	public void downloadDebugFiles(final boolean filterApplied,final boolean remoteOkPressed) {
		Job job = new Job(Messages.LOADING_DEBUG_FILE) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				disbleDataViewerUIControls();
				
				DataViewerFileManager dataViewerFileManager = new DataViewerFileManager(jobDetails);
				final StatusMessage statusMessage = dataViewerFileManager.downloadDataViewerFiles(filterApplied,getConditions(),isOverWritten);


				if (StatusConstants.ERROR == statusMessage.getReturnCode()) {
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							Utils.INSTANCE.showDetailErrorMessage(statusMessage.getStatusMessage(), statusMessage.getErrorStatus());
							getShell().close();
						}
					});
					return Status.CANCEL_STATUS;
				}

				debugFileName = dataViewerFileManager.getDataViewerFileName();
				debugFileLocation = dataViewerFileManager.getDataViewerFilePath();
				downlodedFileSize = dataViewerFileManager.getDebugFileSize();
				setDebugFileLocation(debugFileLocation);
				setDebugFileName(debugFileName);
				setDownloadedFileSize(downlodedFileSize);
				if (getConditions() != null) {
					if (getConditions().getRetainLocal() || getConditions().getRetainRemote()) {
						showDataInDebugViewer(true,remoteOkPressed);
					}
					 else {
						if (!filterApplied) {
							showDataInDebugViewer(false, false);
						} else {
							showDataInDebugViewer(false, remoteOkPressed);
						}
					}
				}
				else
				{
					showDataInDebugViewer(false, remoteOkPressed);
				}
				dataViewerFileSchema = ViewDataSchemaHelper.INSTANCE.getFieldsFromSchema(debugFileLocation + debugFileName + SCHEMA_FILE_EXTENTION);
				
				if(dataViewerFileSchema != null){
					syncSchemaWithReceivedDataFile();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	private void syncSchemaWithReceivedDataFile() {
		List<Field> fieldList = dataViewerFileSchema.getField();
		List<Field> newFieldList = new ArrayList<>();
		if(dataViewerAdapter!=null){
			for(String field: dataViewerAdapter.getColumnList()){
				newFieldList.add(getField(fieldList,field));
			}
			dataViewerFileSchema.getField().clear();
			dataViewerFileSchema.getField().addAll(newFieldList);
		}
	}


	private Field getField(List<Field> fieldList, String name) {		
		for(Field field: fieldList){
			if(StringUtils.equals(field.getName(), name)){
				return field;
			}
		}		
		return null;
	}


	public String getDebugFileLocation() {
		return debugFileLocation;
	}


	public void setDebugFileLocation(String debugFileLocation) {
		this.debugFileLocation = debugFileLocation;
	}
	
	public void setDownloadedFileSize(double downlodedFileSize) {
		this.downlodedFileSize = downlodedFileSize;
	}
	
	public double getDownloadedFileSize() {
		return downlodedFileSize;
	}

	public String getDebugFileName() {
		return debugFileName;
	}


	public void setDebugFileName(String debugFileName) {
		this.debugFileName = debugFileName;
	}


	public void loadDebugFileInDataViewer(boolean remoteOkPressed) {
		statusManager.getStatusLineManager().getProgressMonitor().done();

		dataViewLoader = new DataViewLoader(unformattedViewTextarea, formattedViewTextarea, horizontalViewTableViewer,
				gridViewTableViewer, gridViewData, formattedViewData, unformattedViewData, dataViewerAdapter, tabFolder);

		dataViewerListeners.setDataViewerAdpater(dataViewerAdapter);
		dataViewerListeners.setDataViewLoader(dataViewLoader);

		statusManager.setDataViewerAdapter(dataViewerAdapter);
		statusManager.setStatus(new StatusMessage(StatusConstants.SUCCESS));
		statusManager.enableInitialPaginationContols();
		statusManager.clearJumpToPageText();

		dataViewLoader.updateDataViewLists();

		updateGridViewTable(remoteOkPressed);

		dataViewLoader.reloadloadViews();
		statusManager.enableInitialPaginationContols();
		actionFactory.enableAllActions(true);
		submitRecordCountJob();		
	}
	
	public void showDataInDebugViewer(final boolean filterApplied, final boolean remoteOkPressed) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					initializeDataFileAdapter(filterApplied,getConditions());
				} catch (ClassNotFoundException e){
					Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,  e.getMessage(), e);
					Utils.INSTANCE.showDetailErrorMessage(Messages.UNABLE_TO_LOAD_DEBUG_FILE+": unable to load CSV Driver", status);
					logger.error("Unable to load debug file", e);					
					if (dataViewerAdapter != null) {
						dataViewerAdapter.closeConnection();
					}
					getShell().close();
				}
				catch (SQLException | IOException exception) {
					Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,  exception.getMessage(), exception);
					Utils.INSTANCE.showDetailErrorMessage(Messages.UNABLE_TO_LOAD_DEBUG_FILE+": unable to read view data schema/file", status);
					logger.error("Unable to load debug file", exception);					
					if (dataViewerAdapter != null) {
						dataViewerAdapter.closeConnection();
					}
					getShell().close();
				}
				loadDebugFileInDataViewer(remoteOkPressed);
			}
		});
	}

	public void disbleDataViewerUIControls() {
		Display.getDefault().syncExec(new Runnable() {					
			@Override
			public void run() {
				statusManager.enablePaginationPanel(false);
				actionFactory.enableAllActions(false);
				statusManager.getStatusLineManager().getProgressMonitor().beginTask(Messages.LOADING_DEBUG_FILE, IProgressMonitor.UNKNOWN);
			}
		});
	}
	
	/**
	 * Get Action factory
	 * 
	 * @return {@link ActionFactory}
	 */
	public ActionFactory getActionFactory() {
		return actionFactory;
	}
	
	/**
	 * 
	 * Get reload information to reload debug file
	 * 
	 * @return {@link JobDetails}
	 */
	public JobDetails getJobDetails() {
		return jobDetails;
	}

	/**
	 * 
	 * Get data viewer adapter
	 * 
	 * @return {@link DataViewerAdapter}
	 */
	public DataViewerAdapter getDataViewerAdapter() {
		return dataViewerAdapter;
	}

	/**
	 * get data view loader
	 * 
	 * @return {@link DataViewLoader}
	 */
	public DataViewLoader getDataViewLoader() {
		return dataViewLoader;
	}
	
		
	/**
	 * 
	 * get Unformatted View Textarea
	 * 
	 * @return {@link StyledText}
	 */
	public StyledText getUnformattedViewTextarea() {
		return unformattedViewTextarea;
	}

	/**
	 * 
	 * Get list of columns
	 * 
	 * @return
	 */
	public List<String> getColumnList(){
		return dataViewerAdapter.getColumnList();
	}
	
	/**
	 * 
	 * Get Formatted View Textarea
	 * 
	 * @return {@link StyledText}
	 */
	public StyledText getFormattedViewTextarea() {
		return formattedViewTextarea;
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		setDataViewerWindowTitle();
		getShell().setMinimumSize(ControlConstants.DATA_VIEWER_MINIMUM_SIZE);
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		tabFolder = new CTabFolder(container, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		/*Below code is to color the tabs of data viewer window*/
		/*tabFolder.setSelectionBackground(new Color(null, 14, 76, 145));
		tabFolder.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		tabFolder.setSelectionForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));*/

		createGridViewTabItem();
		
		dataViewerListeners = new DataViewerListeners();		
		dataViewerListeners.setWindowControls(windowControls);
		dataViewerListeners.addTabFolderSelectionChangeListener(tabFolder);
		dataViewerListeners.setStatusManager(statusManager);
		dataViewerListeners.setDebugDataViewer(this);
		
		statusManager.setWindowControls(windowControls);
		createPaginationPanel(container);
		
		tabFolder.setSelection(0);
		downloadDebugFiles(false,false);
		
		return container;
	}

	/**
	 * 
	 * Get status manager
	 * 
	 * @return {@link StatusManager}
	 */
	public StatusManager getStatusManager() {
		return statusManager;
	}

	private void initializeDataFileAdapter(boolean filterApplied, FilterConditions filterConditions) throws ClassNotFoundException,
			SQLException, IOException{
		
		if(filterConditions!=null && filterApplied)
		{
			dataViewerAdapter=new DataViewerAdapter(debugFileLocation,
					debugFileName, Utils.INSTANCE.getDefaultPageSize(),
					PreferenceConstants.INITIAL_OFFSET, this,filterConditions.getLocalCondition());
			if(StringUtils.isEmpty(filterConditions.getLocalCondition())){
				setLocalCondition("");
			}
		}
		else
		{
			dataViewerAdapter = new DataViewerAdapter(debugFileLocation,
					debugFileName, Utils.INSTANCE.getDefaultPageSize(),
					PreferenceConstants.INITIAL_OFFSET, this);
		}
	}

	private void setDataViewerWindowTitle() {
		getShell().setText(Messages.DATA_VIEWER + " - " + dataViewerWindowName);
	}

	private void createPaginationPanel(Composite container) {
		Composite composite_2 = new Composite(container, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.verticalSpacing = 0;
		gl_composite_2.marginWidth = 0;
		gl_composite_2.marginHeight = 0;
		gl_composite_2.horizontalSpacing = 0;
		composite_2.setLayout(gl_composite_2);

		createPageSwitchPanel(composite_2);
		createPageJumpPanel(composite_2);
	}

	private void createPageJumpPanel(Composite composite_2) {
		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		composite_3.setLayout(new GridLayout(3, false));

		createJumpPageLabel(composite_3);

		createJumpPageTextBox(composite_3);

		createJumpPageButton(composite_3);

	}

	private void createJumpPageButton(Composite composite_3) {
		Button jumpPageButton = new Button(composite_3, SWT.NONE);
		dataViewerListeners.attachJumpPageListener(jumpPageButton);
		jumpPageButton.setText(ControlConstants.JUMP_BUTTON_DISPLAY_TEXT);
		windowControls.put(ControlConstants.JUMP_BUTTON, jumpPageButton);
	}

	private void createJumpPageTextBox(Composite composite_3) {
		Text jumpPageTextBox = new Text(composite_3, SWT.BORDER);
		jumpPageTextBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		jumpPageTextBox.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				String currentText = ((Text) e.widget).getText();
				String pageNumberText = currentText.substring(0, e.start) + e.text + currentText.substring(e.end);
				try {
					long pageNumber = Long.valueOf(pageNumberText);
					if (pageNumber < 1) {
						e.doit = false;
					}
				} catch (NumberFormatException ex) {
					if (!pageNumberText.equals(""))
						e.doit = false;
				}
			}
		});

		dataViewerListeners.attachJumpPageListener(jumpPageTextBox);
		windowControls.put(ControlConstants.JUMP_TEXT, jumpPageTextBox);

	}

	private void createJumpPageLabel(Composite composite_3) {
		Label label = new Label(composite_3, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText(ControlConstants.JUMP_TO_PAGE_LABEL_TEXT);
	}

	private void createPageSwitchPanel(Composite composite_2) {
		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		composite_3.setLayout(new GridLayout(3, false));

		createPreviousPageButton(composite_3);
		createPageNumberDisplay(composite_3);
		createNextPageButton(composite_3);
	}

	private void createNextPageButton(Composite composite_3) {
		Button nextPageButton = new Button(composite_3, SWT.NONE);
		dataViewerListeners.attachNextPageButtonListener(nextPageButton);
		nextPageButton.setText(ControlConstants.NEXT_BUTTON_DISPLAY_TEXT);
		windowControls.put(ControlConstants.NEXT_BUTTON, nextPageButton);

	}

	private void createPageNumberDisplay(Composite composite_3) {
		Text pageNumberDisplayTextBox = new Text(composite_3, SWT.BORDER | SWT.CENTER);
		pageNumberDisplayTextBox.setEnabled(false);
		pageNumberDisplayTextBox.setEditable(false);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 178;
		pageNumberDisplayTextBox.setLayoutData(gd_text);

		windowControls.put(ControlConstants.PAGE_NUMBER_DISPLAY, pageNumberDisplayTextBox);
	}

	public void submitRecordCountJob() {
		Job job = new Job(Messages.FETCHING_TOTAL_NUMBER_OF_RECORDS) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						statusManager.getStatusLineManager().getProgressMonitor().beginTask(Messages.FETCHING_TOTAL_NUMBER_OF_RECORDS, IProgressMonitor.UNKNOWN);
					}
				});
				
				final StatusMessage status = dataViewerAdapter.fetchRowCount();

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						statusManager.getStatusLineManager().getProgressMonitor().done();
						statusManager.setStatus(status);
						statusManager.enableJumpPagePanel(true);
						actionFactory.getAction(ResetSortAction.class.getName()).setEnabled(false);
						actionFactory.getAction(ClearFilterAction.class.getName()).setEnabled(false);
					}
				});
				return Status.OK_STATUS;
			}
		};

		job.schedule();
	}

	private void createPreviousPageButton(Composite composite_3) {
		Button button = new Button(composite_3, SWT.NONE);
		dataViewerListeners.attachPreviousPageButtonListener(button);
		button.setText(ControlConstants.PREVIOUS_BUTTON_DISPLAY_TEXT);
		windowControls.put(ControlConstants.PREVIOUS_BUTTON, button);
	}

	/**
	 * 
	 * Create unformatted view tab in data viewer tab folder
	 * 
	 */
	public void createUnformattedViewTabItem() {
		if (isViewTabExist(Views.UNFORMATTED_VIEW_NAME)) {
			CTabItem item = getViewTabItem(Views.UNFORMATTED_VIEW_NAME);
			tabFolder.setSelection(item);
			dataViewLoader.reloadloadViews();
			return;
		}

		CTabItem tbtmUnformattedView = new CTabItem(tabFolder, SWT.CLOSE);
		tbtmUnformattedView.setData(Views.VIEW_NAME_KEY, Views.UNFORMATTED_VIEW_NAME);
		tbtmUnformattedView.setText(Views.UNFORMATTED_VIEW_DISPLAY_NAME);
		{
			Composite composite = new Composite(tabFolder, SWT.NONE);
			tbtmUnformattedView.setControl(composite);
			composite.setLayout(new GridLayout(1, false));
			{
				unformattedViewTextarea = new StyledText(composite, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL
						| SWT.H_SCROLL);
				unformattedViewTextarea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			}
		}

		tabFolder.setSelection(tbtmUnformattedView);
		dataViewLoader.setUnformattedViewTextarea(unformattedViewTextarea);
		dataViewLoader.reloadloadViews();
		actionFactory.getAction(SelectColumnAction.class.getName()).setEnabled(false);
	}

	/**
	 * 
	 * Create formatted view tab in data viewer tab folder
	 * 
	 */
	public void createFormatedViewTabItem() {
		if (isViewTabExist(Views.FORMATTED_VIEW_NAME)) {
			CTabItem item = getViewTabItem(Views.FORMATTED_VIEW_NAME);
			tabFolder.setSelection(item);
			dataViewLoader.reloadloadViews();
			return;
		}

		CTabItem tbtmFormattedView = new CTabItem(tabFolder, SWT.CLOSE);
		tbtmFormattedView.setData(Views.VIEW_NAME_KEY, Views.FORMATTED_VIEW_NAME);
		tbtmFormattedView.setText(Views.FORMATTED_VIEW_DISPLAYE_NAME);
		{
			Composite composite = new Composite(tabFolder, SWT.NONE);
			tbtmFormattedView.setControl(composite);
			composite.setLayout(new GridLayout(1, false));
			{
				formattedViewTextarea = new StyledText(composite, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL
						| SWT.V_SCROLL);
				formattedViewTextarea.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
				formattedViewTextarea.setEditable(false);
				formattedViewTextarea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			}
		}
		tabFolder.setSelection(tbtmFormattedView);
		dataViewLoader.setFormattedViewTextarea(formattedViewTextarea);
		dataViewLoader.reloadloadViews();
	}

	private void createHorizantalViewTabItem() {
		CTabItem tbtmHorizantalView = new CTabItem(tabFolder, SWT.CLOSE);
		tbtmHorizantalView.setData(Views.VIEW_NAME_KEY, Views.HORIZONTAL_VIEW_NAME);
		tbtmHorizantalView.setText(Views.HORIZONTAL_VIEW_DISPLAY_NAME);
		{
			Composite composite = new Composite(tabFolder, SWT.NONE);
			tbtmHorizantalView.setControl(composite);
			composite.setLayout(new GridLayout(1, false));
			{
				ScrolledComposite scrolledComposite = new ScrolledComposite(composite, SWT.BORDER | SWT.H_SCROLL
						| SWT.V_SCROLL);
				scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				scrolledComposite.setExpandHorizontal(true);
				scrolledComposite.setExpandVertical(true);

				Composite stackLayoutComposite = new Composite(scrolledComposite, SWT.NONE);
				StackLayout stackLayout = new StackLayout();
				stackLayoutComposite.setLayout(stackLayout);

				{
					Composite composite_4 = new Composite(stackLayoutComposite, SWT.NONE);
					GridLayout gl_composite_4 = new GridLayout(1, false);
					gl_composite_4.verticalSpacing = 0;
					gl_composite_4.marginWidth = 0;
					gl_composite_4.marginHeight = 0;
					gl_composite_4.horizontalSpacing = 0;
					composite_4.setLayout(gl_composite_4);
					{
						horizontalViewTableViewer = new TableViewer(composite_4, SWT.BORDER | SWT.FULL_SELECTION);
						Table table_1 = horizontalViewTableViewer.getTable();
						table_1.setLinesVisible(true);
						table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
					}
					stackLayout.topControl = composite_4;
				}

				scrolledComposite.getShowFocusedControl();
				scrolledComposite.setShowFocusedControl(true);

				scrolledComposite.setContent(stackLayoutComposite);
				scrolledComposite.setMinSize(stackLayoutComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

				installMouseWheelScrollRecursively(scrolledComposite);

				setTableLayoutToMappingTable(horizontalViewTableViewer);
				horizontalViewTableViewer.setContentProvider(new ArrayContentProvider());

				dataViewLoader.updateDataViewLists();
				horizontalViewTableViewer.setInput(gridViewData);
				horizontalViewTableViewer.refresh();

				for (int i = 0, n = horizontalViewTableViewer.getTable().getColumnCount(); i < n; i++)
					horizontalViewTableViewer.getTable().getColumn(i).pack();

				horizontalViewTableViewer.refresh();
			}
		}
	}

	private boolean isViewTabExist(String viewName) {
		for (int index = 0; index < tabFolder.getItemCount(); index++) {
			if (viewName.equals(tabFolder.getItem(index).getData(Views.VIEW_NAME_KEY))) {
				return true;
			}
		}
		return false;
	}

	private CTabItem getViewTabItem(String viewName) {
		for (int index = 0; index < tabFolder.getItemCount(); index++) {
			if (viewName.equals(tabFolder.getItem(index).getData(Views.VIEW_NAME_KEY))) {
				return tabFolder.getItem(index);
			}
		}
		return null;
	}

	/**
	 * 
	 * Create grid view tab Item
	 * 
	 */
	public void createGridViewTabItem() {
		if (isViewTabExist(Views.GRID_VIEW_NAME)) {
			CTabItem item = getViewTabItem(Views.GRID_VIEW_NAME);
			tabFolder.setSelection(item);
			return;
		}

		CTabItem tbtmGridview = new CTabItem(tabFolder, SWT.NONE);
		tbtmGridview.setData(Views.VIEW_NAME_KEY, Views.GRID_VIEW_NAME);
		tbtmGridview.setText(Views.GRID_VIEW_DISPLAY_NAME);
		{
			Composite composite = new Composite(tabFolder, SWT.NONE);
			tbtmGridview.setControl(composite);

			composite.setLayout(new GridLayout(1, false));
			{
				ScrolledComposite scrolledComposite = new ScrolledComposite(composite, SWT.BORDER | SWT.H_SCROLL
						| SWT.V_SCROLL);
				scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				scrolledComposite.setExpandHorizontal(true);
				scrolledComposite.setExpandVertical(true);

				Composite stackLayoutComposite = new Composite(scrolledComposite, SWT.NONE);
				StackLayout stackLayout = new StackLayout();
				stackLayoutComposite.setLayout(stackLayout);
				{
					Composite composite_1 = new Composite(stackLayoutComposite, SWT.NONE);
					GridLayout gl_composite_1 = new GridLayout(1, false);
					gl_composite_1.verticalSpacing = 0;
					gl_composite_1.marginWidth = 0;
					gl_composite_1.marginHeight = 0;
					gl_composite_1.horizontalSpacing = 0;
					composite_1.setLayout(gl_composite_1);
					composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
					{
						gridViewTableViewer = new TableViewer(composite_1, SWT.BORDER| SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
						Table table = gridViewTableViewer.getTable();
						table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
						table.setLinesVisible(true);
						table.setHeaderVisible(true);
												
						gridViewTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
							private boolean update;
						    @Override
						    public void selectionChanged(SelectionChangedEvent event) {
						    	if (!update) {
						            update = true;
						            gridViewTableViewer.setSelection(null);
						            update = false;
						        }
						    }
						});
						
						gridViewTableViewer.getTable().addMouseListener(new MouseAdapter() {
							private Point selectionEndPoint ;			
					        public void mouseDown(MouseEvent e) {
					        	clearSelection(currentSelection,gridViewTableViewer);
					        	if(e.button == 1 && (e.stateMask & SWT.SHIFT) != 0){	        		
					        		selectionEndPoint = new Point(e.x, e.y);
					        		List<Point> cellsToBeSelected = getCellRectangle(startCell,selectionEndPoint,gridViewTableViewer,true);
					        		if(cellsToBeSelected!=null){
					        			clearSelection(currentSelection,gridViewTableViewer);
					        			highlightCells(cellsToBeSelected, gridViewTableViewer);
					        			currentSelection.addAll(cellsToBeSelected);
					        		}
					        	}
					        }
					        
					        @Override
					        public void mouseUp(MouseEvent e) {
					        	
					        }
					    });
						
					}
					stackLayout.topControl = composite_1;
				}

				scrolledComposite.getShowFocusedControl();
				scrolledComposite.setShowFocusedControl(true);

				scrolledComposite.setContent(stackLayoutComposite);
				scrolledComposite.setMinSize(stackLayoutComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
				attachCellNavigator();

				//updateGridViewTable();
			}
		}

	}
	
	private void attachCellNavigator(){
		tableCursor = new TableCursor(gridViewTableViewer.getTable(), SWT.NONE);
		tableCursor.setBackground(DataViewerColors.COLOR_CELL_SELECTION);
		
		tableCursor.addControlListener(new ControlListener() {
			private Point previousCellSize;
			private boolean controlResized;
			@Override
			public void controlResized(ControlEvent e) {				
				ViewerCell cell = gridViewTableViewer.getCell(getActualTableCursorLocation());
				
				if(cell==null){
					return;
				}
				
				Point currentCellSize=new Point(cell.getBounds().width,cell.getBounds().height);
				
				if(previousCellSize==null){					
					previousCellSize = new Point(currentCellSize.x,currentCellSize.y);
				}

				if (!controlResized) {
					controlResized = true;

					tableCursor.setSize(currentCellSize.x + 4, currentCellSize.y + 4);
					
					Point currentLocation = tableCursor.getLocation();
					
					tableCursor.setLocation(currentLocation.x - 2, currentLocation.y - 2);
					
					previousCellSize = new Point(currentCellSize.x, currentCellSize.y);
				} else {
					controlResized = false;
				}
						
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				// Nothing to do
				
			}
		});
		
		tableCursor.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {				
				if(e.keyCode == SWT.SHIFT){
					selectionStartPoint = new Point(-1, -1);
					shiftKeyPressed = false;
					return;
				}
				
				if(e.keyCode == SWT.CTRL || e.keyCode == SWT.COMMAND){
					ctrlKeyPressed = false;
					return;
				}
				
				if(!((e.stateMask & SWT.SHIFT) != 0)){
					clearSelection(currentSelection,gridViewTableViewer);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if((e.keyCode == SWT.SHIFT) && shiftKeyPressed==false){
					selectionStartPoint = getActualTableCursorLocation();
					startCell = getCellId(selectionStartPoint,gridViewTableViewer);
					shiftKeyPressed = true;
				}
				
				if((e.keyCode == SWT.CTRL || e.keyCode == SWT.COMMAND) && ctrlKeyPressed==false){
					selectionStartPoint = getActualTableCursorLocation();
					startCell = getCellId(selectionStartPoint,gridViewTableViewer);
					ctrlKeyPressed = true;
				}
				
				
				if(ctrlKeyPressed && !shiftKeyPressed){
					if(e.keyCode == SWT.ARROW_RIGHT){
						tableCursor.setSelection(gridViewTableViewer.getTable().indexOf(tableCursor.getRow()), gridViewTableViewer.getTable().getColumnCount()-1);
						return;	
					}
					
					if(e.keyCode == SWT.ARROW_LEFT){
						tableCursor.setSelection(gridViewTableViewer.getTable().indexOf(tableCursor.getRow()), 0);
						return;	
					}
					
					if(e.keyCode == SWT.ARROW_UP){
						tableCursor.setSelection(0, tableCursor.getColumn());
						return;	
					}
					
					if(e.keyCode == SWT.ARROW_DOWN){
						tableCursor.setSelection(gridViewTableViewer.getTable().getItemCount()-1, tableCursor.getColumn());
						return;	
					}
				}
				
				if (shiftKeyPressed && ctrlKeyPressed) {
					if(e.keyCode == SWT.ARROW_RIGHT){
						tableCursor.setSelection(gridViewTableViewer.getTable().indexOf(tableCursor.getRow()), gridViewTableViewer.getTable().getColumnCount()-1);
						selectCellsOnArrowKeys(gridViewTableViewer, e);
						return;	
					}
					
					if(e.keyCode == SWT.ARROW_LEFT){
						tableCursor.setSelection(gridViewTableViewer.getTable().indexOf(tableCursor.getRow()), 0);
						selectCellsOnArrowKeys(gridViewTableViewer, e);
						return;	
					}
					
					if(e.keyCode == SWT.ARROW_UP){
						tableCursor.setSelection(0, tableCursor.getColumn());
						selectCellsOnArrowKeys(gridViewTableViewer, e);
						return;	
					}
					
					if(e.keyCode == SWT.ARROW_DOWN){
						tableCursor.setSelection(gridViewTableViewer.getTable().getItemCount()-1, tableCursor.getColumn());
						selectCellsOnArrowKeys(gridViewTableViewer, e);
						return;	
					}
					
				}
				
				if ((e.keyCode == SWT.ARROW_UP) || (e.keyCode == SWT.ARROW_DOWN) || (e.keyCode == SWT.ARROW_LEFT) || (e.keyCode == SWT.ARROW_RIGHT) ) {
					selectCellsOnArrowKeys(gridViewTableViewer, e);
				}				
				
			}

			private void selectCellsOnArrowKeys(final TableViewer tableViewer, KeyEvent e) {
				if((e.stateMask & SWT.SHIFT) != 0){	        						
					Point selectionEndPoint = getActualTableCursorLocation();
					List<Point> cellsToBeSelected = getCellRectangle(startCell,selectionEndPoint,tableViewer,true);
					if(cellsToBeSelected!=null){
						clearSelection(currentSelection,tableViewer);
						highlightCells(cellsToBeSelected, tableViewer);
						currentSelection.addAll(cellsToBeSelected);
					}
				}
			}
		});
		
	}
	
	private void clearSelection(List<Point> currentSelection,TableViewer tableViewer){
    	for(Point cell: currentSelection){
    		tableViewer.getTable().getItem(cell.x).setBackground(cell.y, DataViewerColors.COLOR_WHITE);
    		if(cell.y==0){
    			tableViewer.getTable().getItem(cell.x).setBackground(cell.y, SWTResourceManager.getColor(SWT.COLOR_GRAY));	
    		}
		}
    		
		currentSelection.clear();
		tableCursor.redraw();
    }
	
	/**
	 * 
	 * Redraw table cursor
	 * 
	 */
	public void redrawTableCursor(){
		clearSelection(currentSelection, gridViewTableViewer);
		tableCursor.forceFocus();
		tableCursor.redraw();
	}
	
	/**
	 * 
	 * Highlight all cells
	 * 
	 */
	public void selectAllCells(){
		selectionStartPoint = new Point(0, 0);
		startCell = new Point(0, 0);
		Point selectionEndPoint = new Point(gridViewTableViewer.getTable().getItemCount()-1, gridViewTableViewer.getTable().getColumnCount()-1);			
		List<Point> cellsToBeSelected = getCellRectangle(startCell,selectionEndPoint,gridViewTableViewer,false);
		if(cellsToBeSelected!=null){
			clearSelection(currentSelection,gridViewTableViewer);
			highlightCells(cellsToBeSelected, gridViewTableViewer);
			currentSelection.addAll(cellsToBeSelected);
		}
	}
	
	/**
	 * Get List of selected cells
	 * 
	 * @return list of {@link Point}
	 */
	public List<Point> getSelectedCell(){
		if(currentSelection.size() > 0){
			return currentSelection;
		}else{
			Point cell=getCellId(getActualTableCursorLocation(), gridViewTableViewer);
			List currentCell= new ArrayList<>();
			if(cell!=null){
				currentCell.add(cell);
			}
			return currentCell;
		}
		
	}
	
	private Point getActualTableCursorLocation(){
		return new Point(tableCursor.getLocation().x + 2, tableCursor.getLocation().y+2);
	}
	
	private void highlightCells(List<Point> cellsToBeHighlight,TableViewer tableViewer) {
		for(Point cell : cellsToBeHighlight){
			tableViewer.getTable().getItem(cell.x).setBackground(cell.y, DataViewerColors.COLOR_CELL_SELECTION);	
		}
	}
	
	private Point getCellId(Point mouseLocation,TableViewer tableViewer){
		ViewerCell cell = tableViewer.getCell(mouseLocation);
		if(cell==null){
			return null;
		}
		int columnIndex = cell.getColumnIndex();				
		int rowIndex = tableViewer.getTable().indexOf((TableItem)cell.getItem())  ;
		return new Point(rowIndex, columnIndex);
	}

	private List<Point> getCellRectangle(Point startCell,Point selectionEndPoint,TableViewer tableViewer,boolean mouseLocation){
    	List<Point> currentSelection = new ArrayList<>();
    	
    	Point endCell=null;
    	if(mouseLocation){
    		endCell = getCellId(selectionEndPoint,tableViewer);
    	}else{
    		endCell = new Point(selectionEndPoint.x, selectionEndPoint.y);
    	}
				
		if(startCell==null || endCell==null){
			return null;
		}
		
		int minX = Math.min(startCell.x, endCell.x);
		int minY = Math.min(startCell.y, endCell.y);
		
		int maxX = Math.max(startCell.x, endCell.x);
		int maxY = Math.max(startCell.y, endCell.y);
		
		int tmpMaxY;
		while(minX<=maxX){
			tmpMaxY = maxY;
			while(minY<=tmpMaxY){
				Point cell = new Point(maxX,tmpMaxY);
				currentSelection.add(cell);
				tmpMaxY--;
			}
			maxX--;
		}
		
		return currentSelection;
	}
	
	private void updateGridViewTable(boolean remoteOkPressed) {
		if(!remoteOkPressed)
		createGridViewTableColumns(gridViewTableViewer);

		gridViewTableViewer.setContentProvider(new ArrayContentProvider());
		gridViewTableViewer.setInput(gridViewData);

		dataViewLoader.setGridViewTableViewer(gridViewTableViewer);
		dataViewLoader.updateDataViewLists();

		gridViewTableViewer.getTable().getColumn(0).pack();

		gridViewTableViewer.refresh();
	}

	private TableColumnLayout setTableLayoutToMappingTable(TableViewer tableViewer) {
		TableColumnLayout layout = new TableColumnLayout();
		tableViewer.getControl().getParent().setLayout(layout);

		tableViewer.refresh();
		return layout;
	}

	private void createGridViewTableIndexColumn(final TableViewer tableViewer) {
		final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnItem = tableViewerColumn.getColumn();
		tblclmnItem.setWidth(100);

		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public Color getBackground(Object element) {
				return SWTResourceManager.getColor(SWT.COLOR_GRAY);
			}

			@Override
			public String getText(Object element) {
				RowData p = (RowData) element;
				return String.valueOf(p.getRowNumber());
			}
		});
		
		tableViewerColumn.getColumn().addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectAllCells();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// Do Nothing				
			}
		});
	}

	public void createGridViewTableColumns(final TableViewer tableViewer) {
		createGridViewTableIndexColumn(tableViewer);
		int index = 0;
		dataViewerFileSchema = ViewDataSchemaHelper.INSTANCE.getFieldsFromSchema(debugFileLocation + debugFileName + SCHEMA_FILE_EXTENTION);
		syncSchemaWithReceivedDataFile();
		for (String columnName : dataViewerAdapter.getColumnList()) {
			final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			TableColumn tblclmnItem = tableViewerColumn.getColumn();
			tblclmnItem.setWidth(100);
			tblclmnItem.setText(columnName);
			

			tableViewerColumn.getColumn().setData(Views.COLUMN_ID_KEY,
					(int) dataViewerAdapter.getAllColumnsMap().get(tableViewerColumn.getColumn().getText()));

			tableViewerColumn.getColumn().setData(Views.COLUMN_ID_KEY, index);
			tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					RowData p = (RowData) element;
					return p.getRowFields()
							.get((int) dataViewerAdapter.getAllColumnsMap()
									.get(tableViewerColumn.getColumn().getText())).getValue();
				}
			});

			if(dataViewerFileSchema!=null){
				
				tableViewerColumn.getColumn().setToolTipText(
						getColumnToolTip(dataViewerFileSchema.getField().get(index)));
			}
			
			tableViewerColumn.getColumn().addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					
					if(recentlySortedColumn!=null && !recentlySortedColumn.isDisposed()){
						recentlySortedColumn.setImage(null);
						//Code to sort each column in ASC order on first column click
						if(!StringUtils.equals(recentlySortedColumn.getText(), ((TableColumn)e.widget).getText())){
							sortOrder=SortOrder.DSC;
						}
					}
					
					int columnIndex=(int) e.widget.getData(Views.COLUMN_ID_KEY);
					String columnDataType=dataViewerFileSchema.getField().get(columnIndex).getType().value();
					String dateFormat = dataViewerFileSchema.getField().get(columnIndex).getFormat();
					int originalColumnIndex=(int)dataViewerAdapter.getAllColumnsMap().get(tableViewerColumn.getColumn().getText());
					
					if(sortOrder==null || SortOrder.ASC == sortOrder){
						Collections.sort(gridViewData,new TypeBasedComparator(SortOrder.DSC, originalColumnIndex, getSortType(columnDataType), dateFormat));
						sortOrder=SortOrder.DSC;
						((TableColumn)e.widget).setImage(descending);
					}else{
						Collections.sort(gridViewData,new TypeBasedComparator(SortOrder.ASC, originalColumnIndex, getSortType(columnDataType), dateFormat));
						sortOrder=SortOrder.ASC;
						((TableColumn)e.widget).setImage(ascending);
					}
					dataViewLoader.syncOtherViewsDataWithGridViewData();
					dataViewLoader.reloadloadViews();
					recentlySortedColumn = ((TableColumn)e.widget);
					actionFactory.getAction(ResetSortAction.class.getName()).setEnabled(true);
					sortedColumnName=((TableColumn)e.widget).getText();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// Do Nothing
				}
			});
			index++;
		}
	}
	
	public TableColumn getRecentlySortedColumn() {
		return recentlySortedColumn;
	}
	
	public void setRecentlySortedColumn(TableColumn recentlySortedColumn) {
		this.recentlySortedColumn = recentlySortedColumn;
	}


	private static SortDataType getSortType(String sortDataType) {

		for (SortDataType sortDataTypeObject : SortDataType.values()) {
			if (sortDataTypeObject.getDataType().equals(sortDataType)) {
				return sortDataTypeObject;
			}
		}
		return null;
	}
	
	private String getColumnToolTip(Field field){
		String tooltipText;
		if(field.getType().value().equals(Date.class.getName())){
			tooltipText = "Field Name: " + field.getName() + "\n" + 
					  	  "Data type: " + field.getType().value().split("\\.")[2] + "\n" +
					      "Format: " + field.getFormat() + "\n";
		}else{
			tooltipText = "Field Name: " + field.getName() + "\n" + 
					  	  "Data type: " + field.getType().value().split("\\.")[2] + "\n";
		}
		return tooltipText;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Do Nothing
	}

	/**
	 * Create the menu manager.
	 * 
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager(MenuConstants.MENU);
		menuManager.setVisible(true);

		createFileMenu(menuManager);
		createEditMenu(menuManager);
		createViewMenu(menuManager);
		createDataMenu(menuManager);
		createWindowMenu(menuManager);

		return menuManager;
	}

	private MenuManager createMenu(MenuManager menuManager, String menuName) {
		MenuManager menu = new MenuManager(menuName);
		menuManager.add(menu);
		menuManager.setVisible(true);
		return menu;
	}

	private void createFileMenu(MenuManager menuManager) {
		MenuManager fileMenu = createMenu(menuManager, MenuConstants.FILE);
		menuManager.add(fileMenu);
		fileMenu.setVisible(true);

		if (actionFactory == null) {
			actionFactory = new ActionFactory(this);
		}
		
		fileMenu.add(actionFactory.getAction(ExportAction.class.getName()));
	}


	
	private void createWindowMenu(MenuManager menuManager) {
		MenuManager windowMenu = createMenu(menuManager, MenuConstants.WINDOW);
		menuManager.add(windowMenu);
		windowMenu.setVisible(true);

		windowMenu.add(actionFactory.getAction(DatasetInformationAction.class.getName()));
		windowMenu.add(actionFactory.getAction(AutoExpandColumnsAction.class.getName()));
	}
	

	private void createEditMenu(MenuManager menuManager) {
		MenuManager editMenu = createMenu(menuManager, MenuConstants.EDIT);
		menuManager.add(editMenu);
		editMenu.setVisible(true);

		if (actionFactory == null) {
			actionFactory = new ActionFactory(this);
		}
		
		editMenu.add(actionFactory.getAction(SelectAllAction.class.getName()));
		editMenu.add(actionFactory.getAction(CopyAction.class.getName()));
		editMenu.add(actionFactory.getAction(FindAction.class.getName()));
		editMenu.add(actionFactory.getAction(SelectColumnAction.class.getName()));
		editMenu.add(actionFactory.getAction(ResetColumnsAction.class.getName()));
	}

	private void createViewMenu(MenuManager menuManager) {
		MenuManager viewMenu = createMenu(menuManager, MenuConstants.VIEW);
		menuManager.add(viewMenu);
		viewMenu.setVisible(true);

		if (actionFactory == null) {
			actionFactory = new ActionFactory(this);
		}
		
		viewMenu.add(actionFactory.getAction(GridViewAction.class.getName()));
		viewMenu.add(actionFactory.getAction(FormattedViewAction.class.getName()));
		viewMenu.add(actionFactory.getAction(UnformattedViewAction.class.getName()));
		viewMenu.add(new Separator());
		viewMenu.add(actionFactory.getAction(ReloadAction.class.getName()));
		viewDataPreferencesVO = getViewDataPreferencesFromPreferenceFile();
		viewMenu.add(actionFactory.getAction(PreferencesAction.class.getName()));
	}

	
	private void createDataMenu(MenuManager menuManager) {
		MenuManager dataMenu = createMenu(menuManager, MenuConstants.Data);
		menuManager.add(dataMenu);
		dataMenu.setVisible(true);

		if (actionFactory == null) {
			actionFactory = new ActionFactory(this);
		}
		
		dataMenu.add(actionFactory.getAction(ResetSortAction.class.getName()));
		dataMenu.add(actionFactory.getAction(FilterAction.class.getName()));
		dataMenu.add(actionFactory.getAction(ClearFilterAction.class.getName()));
	}
	
	/**
	 * 
	 * Get data viewer preferences from preference file
	 * 
	 * @return {@link ViewDataPreferencesVO}
	 */
	public ViewDataPreferencesVO getViewDataPreferencesFromPreferenceFile() {
		boolean includeHeaderValue = false;
		IEclipsePreferences eclipsePreferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		String delimiter = eclipsePreferences.get(DELIMITER, DEFAULT);
		String quoteCharactor = eclipsePreferences.get(QUOTE_CHARACTOR, DEFAULT);
		String includeHeader = eclipsePreferences.get(INCLUDE_HEADERS, DEFAULT);
		String fileSize = eclipsePreferences.get(FILE_SIZE, DEFAULT);
		String pageSize = eclipsePreferences.get(PAGE_SIZE, DEFAULT);
		delimiter = delimiter.equalsIgnoreCase(DEFAULT) ? DEFAULT_DELIMITER : delimiter;
		quoteCharactor = quoteCharactor.equalsIgnoreCase(DEFAULT) ? DEFAULT_QUOTE_CHARACTOR : quoteCharactor;
		includeHeaderValue = includeHeader.equalsIgnoreCase(DEFAULT) ? true : false;
		fileSize = fileSize.equalsIgnoreCase(DEFAULT) ? DEFAULT_FILE_SIZE : fileSize;
		pageSize = pageSize.equalsIgnoreCase(DEFAULT) ? DEFAULT_PAGE_SIZE : pageSize;
		ViewDataPreferencesVO viewDataPreferencesVO = new ViewDataPreferencesVO(delimiter, quoteCharactor,
				includeHeaderValue, Integer.parseInt(fileSize), Integer.parseInt(pageSize));
		return viewDataPreferencesVO;
	}

	/**
	 * Create the coolbar manager.
	 * 
	 * @return the coolbar manager
	 */
	@Override
	protected CoolBarManager createCoolBarManager(int style) {
		CoolBarManager coolBarManager = new CoolBarManager(style);

		actionFactory = new ActionFactory(this);

		ToolBarManager toolBarManager = new ToolBarManager();
		coolBarManager.add(toolBarManager);
		addtoolbarAction(toolBarManager, ImagePathConstant.DATA_VIEWER_EXPORT,
				actionFactory.getAction(ExportAction.class.getName()));

		/*
		 * addtoolbarAction( toolBarManager, (XMLConfigUtil.CONFIG_FILES_PATH + ImagePathConstant.DATA_VIEWER_FIND),
		 * actionFactory.getAction(FindAction.class.getName()));
		 */
		addtoolbarAction(toolBarManager, ImagePathConstant.DATA_VIEWER_RELOAD,
				actionFactory.getAction(ReloadAction.class.getName()));
		/*
		 * addtoolbarAction( toolBarManager, (XMLConfigUtil.CONFIG_FILES_PATH + ImagePathConstant.DATA_VIEWER_FILTER),
		 * actionFactory.getAction(FilterAction.class.getName()));
		 */
		addtoolbarAction(toolBarManager,ImagePathConstant.RESET_SORT,
				actionFactory.getAction(ResetSortAction.class.getName()));
		
		addtoolbarAction(toolBarManager,ImagePathConstant.TABLE_ICON,
				actionFactory.getAction(SelectColumnAction.class.getName()));

		addtoolbarAction(toolBarManager, ImagePathConstant.FIND_DATA, 
				actionFactory.getAction(FindAction.class.getName()));
		
		addtoolbarAction(toolBarManager, ImagePathConstant.AUTO_ADJUST_COLUMNS, 
				actionFactory.getAction(AutoExpandColumnsAction.class.getName()));
		
		dropDownAction = new Action("", SWT.DROP_DOWN) {
			@Override
			public void run() {
				tabFolder.showItem(tabFolder.getItem(0));
				tabFolder.setSelection(0);
			}
		};
		dropDownAction.setImageDescriptor(new ImageDescriptor() {

			@Override
			public ImageData getImageData() {
				return ImagePathConstant.DATA_VIEWER_SWITCH_VIEW.getImageFromRegistry().getImageData();
			}
		});

		dropDownAction.setMenuCreator(new ViewDataGridMenuCreator(actionFactory));
		toolBarManager.add(dropDownAction);

		return coolBarManager;
	}

	private void addtoolbarAction(ToolBarManager toolBarManager, final ImagePathConstant imagePath, Action action) {

		ImageDescriptor exportImageDescriptor = new ImageDescriptor() {
			@Override
			public ImageData getImageData() {
				return imagePath.getImageFromRegistry().getImageData();
			}
		};
		action.setImageDescriptor(exportImageDescriptor);
		toolBarManager.add(action);
	}

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		statusLineManager.appendToGroup(StatusLineManager.END_GROUP, new Separator(StatusLineManager.END_GROUP));
		statusLineManager.appendToGroup(StatusLineManager.END_GROUP, dropDownAction);
		statusManager = new StatusManager();
		statusManager.setStatusLineManager(statusLineManager);

		return statusLineManager;
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(848, 469);
	}

	/**
	 * make wheel scrolling available by installing a wheel listener on this scrollable's parent and hierarchy of
	 * children
	 * 
	 * @param scrollable
	 *            the scrolledComposite to wheel-scroll
	 */
	private void installMouseWheelScrollRecursively(final ScrolledComposite scrollable) {
		MouseWheelListener scroller = createMouseWheelScroller(scrollable);
		if (scrollable.getParent() != null){
			scrollable.getParent().addMouseWheelListener(scroller);
		}			
		installMouseWheelScrollRecursively(scroller, scrollable);
	}

	private MouseWheelListener createMouseWheelScroller(final ScrolledComposite scrollable) {
		return new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent e) {
				Point currentScroll = scrollable.getOrigin();
				scrollable.setOrigin(currentScroll.x, currentScroll.y - (e.count * 5));
			}
		};
	}

	private void installMouseWheelScrollRecursively(MouseWheelListener scroller, Control c) {
		c.addMouseWheelListener(scroller);
		if (c instanceof Composite) {
			Composite comp = (Composite) c;
			for (Control child : comp.getChildren()) {
				installMouseWheelScrollRecursively(scroller, child);
			}
		}
	}

	/**
	 * 
	 * Get grid view tableviewer
	 * 
	 * @return {@link TableViewer}
	 */
	public TableViewer getTableViewer() {
		return gridViewTableViewer;
	}

	/**
	 * 
	 * Get View Data Preferences
	 * 
	 * @return {@link ViewDataPreferencesVO}
	 */
	public ViewDataPreferencesVO getViewDataPreferences() {
		return viewDataPreferencesVO;
	}

	@Override
	public boolean close() {
		if(dataViewerAdapter!=null){
			dataViewerAdapter.closeConnection();
		}		
		dataViewerMap.remove(dataViewerWindowName);
		return super.close();
	}


	public boolean isOverWritten() {
		return isOverWritten;
	}


	public void setOverWritten(boolean isOverWritten) {
		this.isOverWritten = isOverWritten;
	}


	public Object getDataViewerWindowTitle() {
		return dataViewerWindowName;
	}

	public void setDataViewerMap(Map<String, DebugDataViewer> dataViewerMap) {
		this.dataViewerMap = dataViewerMap;
	}
	
	public CTabFolder getCurrentView(){
		return tabFolder;
	}

	public void setConditions(FilterConditions conditons) {
		this.conditions=conditons;
		if(!StringUtils.isEmpty(conditions.getLocalCondition()) && conditons.getRetainLocal()){
			this.localCondition=conditons.getLocalCondition();
		}
		if(!StringUtils.isEmpty(conditions.getRemoteCondition())){
			this.remoteCondition = conditons.getRemoteCondition();
		}
	}
	public FilterConditions getConditions(){
		return conditions;
	}
	

	public String getLocalCondition() {
		return localCondition;
	}
	
	public void setLocalCondition(String localCondition) {
		this.localCondition = localCondition;
		enableDisableFilter();
	}


	public String getRemoteCondition() {
		return remoteCondition;
	}


	public void setRemoteCondition(String remoteCondition) {
		this.remoteCondition = remoteCondition;
		enableDisableFilter();
	}
	
	public void enableDisableFilter(){
		if(StringUtils.isEmpty(remoteCondition) && StringUtils.isEmpty(localCondition)){
			actionFactory.getAction(ClearFilterAction.class.getName()).setEnabled(false);
		}
		else{
			actionFactory.getAction(ClearFilterAction.class.getName()).setEnabled(true);
		}
	}

	
	/*
	 * Get View Data Current Page in Grid View  
	 */
	public long getCurrentPage(){
		return dataViewerAdapter.getCurrentPageNumber();
	}
	
	/*
	 * Get Table Cursor in TableViewer in Grid View  
	 */
	public TableCursor getTablecursor(){
		return tableCursor;
	}
	/**
	 * clear jumpTo text while applying filter.
	 */
	public void clearJumpToText(){
		
		statusManager.clearJumpToPageText();
		
	}
}


