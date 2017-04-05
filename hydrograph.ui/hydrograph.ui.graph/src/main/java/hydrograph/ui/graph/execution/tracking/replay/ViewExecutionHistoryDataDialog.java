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
package hydrograph.ui.graph.execution.tracking.replay;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.datastructures.executiontracking.ViewExecutionTrackingDetails;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.execution.tracking.datastructure.ExecutionStatus;
import hydrograph.ui.graph.handler.ViewExecutionHistoryHandler;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.graph.job.JobManager;

/**
 * 
 * The Class ViewExecutionHistoryDataDialog used to show execution tracking view history.
 * @author Bitwise
 *
 */
public class ViewExecutionHistoryDataDialog extends Dialog {
	
	private static final String VIEW_TRACKING_HISTORY = "View Execution Tracking History"; 
	private static final String EXECUTION_HISTORY_DIALOG="Execution History Dialog";
	private static final String BROWSE_TRACKING_FILE="Browse Tracking File"; 
	private static final String EXECUTION_TRACKING_LOG_FILE_EXTENTION = "*.track.log";
	private static final String REMOTE_MODE = "Remote";
	private static final String LOCAL_MODE = "Local";
	
	private Text trackingFileText;
	private Table table;
	private String filePath;
	private String selectedUniqueJobId;
	private List<Job> jobDetails;
	private ViewExecutionHistoryHandler viewExecutionHistoryHandler;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ViewExecutionHistoryDataDialog(Shell parentShell, ViewExecutionHistoryHandler viewExecutionHistoryHandler, List<Job> jobDetails) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
		this.jobDetails = jobDetails;
		this.viewExecutionHistoryHandler=viewExecutionHistoryHandler;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.getShell().setText(VIEW_TRACKING_HISTORY);
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewer tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
		tblclmnNewColumn.setWidth(263);
		tblclmnNewColumn.setText("Job Id");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_1 = tableViewerColumn_1.getColumn();
		tblclmnNewColumn_1.setWidth(207);
		tblclmnNewColumn_1.setText("Time Stamp");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_2 = tableViewerColumn_2.getColumn();
		tblclmnNewColumn_2.setWidth(154);
		tblclmnNewColumn_2.setText("Execution Mode");
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_3 = tableViewerColumn_3.getColumn();
		tblclmnNewColumn_3.setWidth(119);
		tblclmnNewColumn_3.setText("Job Status");
		
		setTableColumnValues(tableViewer, jobDetails);
		
		tableViewer.getTable().addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TableItem[] item = table.getSelection();
				 for (int i = 0; i < item.length; i++){
					 TableItem selectedItem = item[i];
					 selectedUniqueJobId = selectedItem.getText();
			      }
			}
		});
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new GridLayout(3, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Label lblNewLabel = new Label(composite_2, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText(BROWSE_TRACKING_FILE);
		
		trackingFileText = new Text(composite_2, SWT.BORDER);
		trackingFileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button browseButton = new Button(composite_2, SWT.NONE);
		browseButton.setText("...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(composite.getShell(),  SWT.OPEN  );
				fileDialog.setText(EXECUTION_HISTORY_DIALOG);
				String[] filterExt = { EXECUTION_TRACKING_LOG_FILE_EXTENTION };
				fileDialog.setFilterExtensions(filterExt);
				fileDialog.setFilterPath(ViewExecutionHistoryUtility.INSTANCE.getLogPath());
				String path = fileDialog.open();
				if (path == null) return;
				trackingFileText.setText(path);
				trackingFileText.setToolTipText(path);
			}
		    });

		trackingFileText.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		filePath = ((Text)event.widget).getText();
	    	}
		});
		
		return container;
	}
	
	
	/**
	 * The Function will set Table column values
	 * @param tableViewer
	 * @param jobDetails
	 */
	private void setTableColumnValues(TableViewer tableViewer, List<Job> jobDetails){
		jobDetails.sort((job1, job2)-> job2.getUniqueJobId().compareTo(job1.getUniqueJobId()));
		jobDetails.forEach(job -> {
			String timeStamp = getTimeStamp(job.getUniqueJobId());
	    	TableItem items = new TableItem(table, SWT.None);
	    	items.setText(0, job.getUniqueJobId());
	    	items.setText(1, timeStamp);
	    	String mode = getJobExecutionMode(job.isRemoteMode());
	    	items.setText(2, mode);
	    	items.setText(3, job.getJobStatus());
		});
	}

	/**
	 * @param executionMode
	 * @return Job Execution Mode
	 */
	private String getJobExecutionMode(boolean executionMode){
		String runningMode = "";
		if(executionMode){
			runningMode = REMOTE_MODE;
		}else{
			runningMode = LOCAL_MODE;
		}
		return runningMode;
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * The function will return selected unique job id
	 *@return String
	 */
	public String getSelectedUniqueJobId(){
		return selectedUniqueJobId;
	}
	
	/**
	 * The function will return selected tracking log file path
	 *@return String
	 */
	public String getTrackingFilePath(){
		return filePath;
	}
	
	@Override
	protected void okPressed() {
		filePath=trackingFileText.getText();
		if(filePath != null){
		}else{
			selectedUniqueJobId = jobDetails.get(0).getUniqueJobId();
		}
		try {
			ExecutionStatus executionStatus = null;
			if(getTrackingFilePath().trim().isEmpty()){
				if(!StringUtils.isEmpty(getSelectedUniqueJobId())){
					executionStatus= ViewExecutionHistoryUtility.INSTANCE.readJsonLogFile(getSelectedUniqueJobId(), JobManager.INSTANCE.isLocalMode(), 
							getSelectedIdFilePath(selectedUniqueJobId), true);
					ViewExecutionHistoryUtility.INSTANCE.getSelectedTrackingDetailsForSubjob().clear();
					ViewExecutionHistoryUtility.INSTANCE.addSelectedTrackingDetails(new ViewExecutionTrackingDetails.Builder(selectedUniqueJobId, 
							getSelectedIdFilePath(selectedUniqueJobId), true).build());
				}else{
					super.okPressed();
				}
			}
			else{
				executionStatus= ViewExecutionHistoryUtility.INSTANCE.readBrowsedJsonLogFile(getTrackingFilePath().trim());
			}
			/*Return from this method if replay not working for old history, so that the view history window will not be closed	and 
			 * proper error message will be displayed over the view history window.*/
			if(executionStatus!=null){
				boolean status = viewExecutionHistoryHandler.replayExecutionTracking(executionStatus);
				if(!status){
					return;
				}
			}else
				return;
		} catch (FileNotFoundException e) {
			ViewExecutionHistoryUtility.INSTANCE.getMessageDialog(Messages.FILE_DOES_NOT_EXIST);
			return;
		}catch(Exception e){
			ViewExecutionHistoryUtility.INSTANCE.getMessageDialog(Messages.INVALID_FILE_FORMAT+" " + getUniqueJobId());
			return;
		}
		super.okPressed();
	}
	
	/**
	 * @return job id for current open job
	 */
	private String getUniqueJobId(){
		String jobId = "";
		ELTGraphicalEditor eltGraphicalEditor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(!(eltGraphicalEditor.getEditorInput() instanceof GraphicalEditor)){
			jobId = eltGraphicalEditor.getContainer().getUniqueJobId();
			return jobId;
		}
		return jobId;
	}
	
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 346);
	}
	
	/**
	 * @param jobId
	 * @return Time Stamp
	 */
	private String getTimeStamp(String jobId){
		String timeStamp;
		String jobUniqueId = jobId;
		
		String[] s1 = jobUniqueId.split("_");
		timeStamp = s1[s1.length-1];
		long times = Long.parseLong(timeStamp);
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
		timeStamp = dateFormat.format(new Date(times));
		
		return timeStamp;
	}
	
	/**
 	 * The Function will return file log path corresponding to selected run job id
 	 * @param selectedUniqueJobId
 	 * @return file path
 	 */
 	private String getSelectedIdFilePath(String selectedUniqueJobId){
 		Map<String, ViewExecutionTrackingDetails> trackingMap = ViewExecutionHistoryUtility.INSTANCE.getTrackingPathDetails();
 		ViewExecutionTrackingDetails trackingDetails = trackingMap.get(selectedUniqueJobId);
 		
 		return trackingDetails.getSelectedLogFilePath();
 	}
	
}
