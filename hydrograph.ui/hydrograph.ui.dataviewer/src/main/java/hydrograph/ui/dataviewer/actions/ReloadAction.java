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

package hydrograph.ui.dataviewer.actions;

import hydrograph.ui.common.datastructures.dataviewer.JobDetails;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.communication.debugservice.DebugServiceClient;
import hydrograph.ui.communication.utilities.SCPUtility;
import hydrograph.ui.dataviewer.Activator;
import hydrograph.ui.dataviewer.constants.Messages;
import hydrograph.ui.dataviewer.constants.StatusConstants;
import hydrograph.ui.dataviewer.datastructures.StatusMessage;
import hydrograph.ui.dataviewer.preferencepage.ViewDataPreferencesVO;
import hydrograph.ui.dataviewer.utilities.DataViewerUtility;
import hydrograph.ui.dataviewer.utilities.Utils;
import hydrograph.ui.dataviewer.window.DebugDataViewer;
import hydrograph.ui.logging.factory.LogFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import org.apache.commons.httpclient.HttpException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import com.jcraft.jsch.JSchException;

/**
 * The Class ReloadAction.
 * ReloadAction reloads the data from debug file based on file size and page size preferences from internal preference window
 * 
 * @author Bitwise
 * 
 */
public class ReloadAction extends Action {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ReloadAction.class);

	private JobDetails jobDetails;
	private DebugDataViewer debugDataViewer;
	private ViewDataPreferencesVO viewDataPreferencesVO;	
	private static final String LABEL = "&Reload";

	private Integer lastDownloadedFileSize;
	public boolean ifFilterReset = false;
	
	/**
	 * Instantiates a new reload action.
	 * 
	 * @param debugDataViewer
	 *            the debug data viewer
	 */
	public ReloadAction(DebugDataViewer debugDataViewer) {
		super(LABEL);
		this.debugDataViewer = debugDataViewer;
		lastDownloadedFileSize = Integer.valueOf(Utils.INSTANCE.getFileSize());
	}

	private String getDebugFilePathFromDebugService() throws IOException {
		return DebugServiceClient.INSTANCE.getDebugFile(jobDetails, Integer.toString(viewDataPreferencesVO.getFileSize())).trim();
	}
	
	private void deleteCSVDebugDataFile() {		
		
		try {
			DebugServiceClient.INSTANCE.deleteDebugFile(jobDetails);
		} catch (NumberFormatException e) {
			logger.warn("Unable to delete debug csv file",e);
		} catch (HttpException e) {
			logger.warn("Unable to delete debug csv file",e);
		} catch (MalformedURLException e) {
			logger.warn("Unable to delete debug csv file",e);
		} catch (IOException e) {
			logger.warn("Unable to delete debug csv file",e);
		}
			
	}
	
	@Override
	public void run() {
		
		viewDataPreferencesVO = debugDataViewer.getViewDataPreferences();
		DataViewerUtility.INSTANCE.resetSort(debugDataViewer);
		Job job = new Job(Messages.LOADING_DEBUG_FILE) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				debugDataViewer.disbleDataViewerUIControls();
				
				if(lastDownloadedFileSize!=viewDataPreferencesVO.getFileSize() || ifFilterReset){
					int returnCode=downloadDebugFile();	
					
					if(StatusConstants.ERROR == returnCode){
						return Status.CANCEL_STATUS;
					}			
				}
				
				try {
					closeExistingDebugFileConnection();
					if(lastDownloadedFileSize!=viewDataPreferencesVO.getFileSize()|| ifFilterReset){
						debugDataViewer.getDataViewerAdapter().reinitializeAdapter(viewDataPreferencesVO.getPageSize(),true);	
					}else{
						SelectColumnAction selectColumnAction =(SelectColumnAction) debugDataViewer.getActionFactory().getAction(SelectColumnAction.class.getName());
						debugDataViewer.getDataViewerAdapter().reinitializeAdapter(viewDataPreferencesVO.getPageSize(),false);
						if(selectColumnAction.getSelectedColumns().size()!=0){
							debugDataViewer.getDataViewerAdapter().setColumnList(selectColumnAction.getSelectedColumns());
						}
					}
					
				} 
				
				catch (ClassNotFoundException e){
					Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,  e.getMessage(), e);
					showDetailErrorMessage(Messages.UNABLE_TO_RELOAD_DEBUG_FILE+": unable to load CSV Driver", status);
					return Status.CANCEL_STATUS;
				}
				catch (SQLException | IOException exception) {
					Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,  exception.getMessage(), exception);
					showDetailErrorMessage(Messages.UNABLE_TO_RELOAD_DEBUG_FILE+": unable to read view data file", status);

					if(debugDataViewer.getDataViewerAdapter()!=null){
						debugDataViewer.getDataViewerAdapter().closeConnection();
						return Status.CANCEL_STATUS ;
					}
					debugDataViewer.close();
				}
				
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						debugDataViewer.getStatusManager().getStatusLineManager().getProgressMonitor().done();
						debugDataViewer.getActionFactory().enableAllActions(true);
						
						debugDataViewer.getStatusManager().setStatus(new StatusMessage(StatusConstants.SUCCESS));
						debugDataViewer.getStatusManager().enableInitialPaginationContols();
						debugDataViewer.getStatusManager().clearJumpToPageText();
						updateDataViewerViews();
						if(lastDownloadedFileSize!=viewDataPreferencesVO.getFileSize() || ifFilterReset){
							debugDataViewer.submitRecordCountJob();
							setIfFilterReset(false);
						}					
						lastDownloadedFileSize = viewDataPreferencesVO.getFileSize();
						DataViewerUtility.INSTANCE.resetSort(debugDataViewer);
						debugDataViewer.redrawTableCursor();
					}
				});
				
				return Status.OK_STATUS;
					
			}
		};
		job.schedule();
	}

	
	private int downloadDebugFile(){
		loadNewDebugFileInformation();

		String dataViewerFilePath = getLocalDataViewerFileLocation();

		String csvDebugFileLocation = getCSVDebugFileLocation();
		if(csvDebugFileLocation==null){
			return StatusConstants.ERROR;
		}
		
		String csvDebugFileName = getCSVDebugFileName(csvDebugFileLocation);
		
		int returnCode;
		if (!jobDetails.isRemote()) {			
			String dataViewerFileAbsolutePath = getDataViewerFileAbsolutePath(dataViewerFilePath, csvDebugFileName);
			 returnCode=copyCSVDebugFileAtDataViewerDebugFileLocation(csvDebugFileLocation, dataViewerFileAbsolutePath);
			 deleteCSVDebugDataFile();
		} else {
			 returnCode=scpRemoteCSVDebugFileToDataViewerDebugFileLocation(dataViewerFilePath, csvDebugFileLocation);
			 deleteCSVDebugDataFile();
		}
		return returnCode;
	}

	private void showDetailErrorMessage(final String errorType, final Status status) {
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				Utils.INSTANCE.showDetailErrorMessage(errorType, status);
			}
		});
	}
	
	
	private void updateDataViewerViews() {
		this.debugDataViewer.getDataViewLoader().updateDataViewLists();
		this.debugDataViewer.getDataViewLoader().reloadloadViews();
	}

	private int scpRemoteCSVDebugFileToDataViewerDebugFileLocation(String dataViewerFilePath, String csvDebugFileLocation) {
		try {
			SCPUtility.INSTANCE.scpFileFromRemoteServer(jobDetails.getHost(), jobDetails.getUsername(),
					jobDetails.getPassword(), csvDebugFileLocation.trim(), dataViewerFilePath);
		} catch (JSchException | IOException e) {
			logger.error("unable to copy Debug csv file to data viewer file location",e);
			
			Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,  e.getMessage(), e);
			showDetailErrorMessage(Messages.UNABLE_TO_RELOAD_DEBUG_FILE+": Unable to copy Debug csv file to data viewer file location", status);
			
			return StatusConstants.ERROR;
		}
		
		return StatusConstants.SUCCESS;		
	}

	private int copyCSVDebugFileAtDataViewerDebugFileLocation(String csvDebugFileLocation, String dataViewerFileAbsolutePath) {
		try {
			Files.copy(Paths.get(csvDebugFileLocation), Paths.get(dataViewerFileAbsolutePath), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logger.error("unable to copy Debug csv file to data viewer file location",e);
			Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,  e.getMessage(), e);
			showDetailErrorMessage(Messages.UNABLE_TO_RELOAD_DEBUG_FILE+": Unable to copy Debug csv file to data viewer file location", status);
			return StatusConstants.ERROR;
		}
		return StatusConstants.SUCCESS;
	}

	private String getDataViewerFileAbsolutePath(String dataViewerFilePath, String csvDebugFileName) {
		String dataViewerFile;
		if (OSValidator.isWindows()) {
			dataViewerFile = (dataViewerFilePath + "\\" + csvDebugFileName.trim() + ".csv").trim();
		} else {
			dataViewerFile = (dataViewerFilePath + "/" + csvDebugFileName.trim() + ".csv").trim();
		}
		return dataViewerFile;
	}

	private String getCSVDebugFileName(String csvDebugFileLocation) {
		String csvDebugFileName = csvDebugFileLocation.substring(csvDebugFileLocation.lastIndexOf("/") + 1, csvDebugFileLocation.length()).replace(".csv", "");
		return csvDebugFileName;
	}

	private String getCSVDebugFileLocation() {
		String csvDebugFileLocation = null;		
		try {
			csvDebugFileLocation = getDebugFilePathFromDebugService();
		} catch (IOException e) {
			Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,  e.getMessage(), e);
			showDetailErrorMessage(Messages.UNABLE_TO_RELOAD_DEBUG_FILE + ": unable to reload files ,please check if service is running", status);
			logger.error("Failed to get csv debug file path from service", e);
			return csvDebugFileLocation;
		}
		return csvDebugFileLocation;
	}

	private String getLocalDataViewerFileLocation() {
		String debugFileLocation = Utils.INSTANCE.getDataViewerDebugFilePath().trim();
		if (OSValidator.isWindows()) {
			if (debugFileLocation.startsWith("/")) {
				debugFileLocation = debugFileLocation.replaceFirst("/", "").replace("/", "\\");
			}
		}
		return debugFileLocation;
	}

	private void loadNewDebugFileInformation() {
		jobDetails = debugDataViewer.getJobDetails();
	}

	private void closeExistingDebugFileConnection() {
		this.debugDataViewer.getDataViewerAdapter().closeConnection();
	}
	
	/**
	 * Gets the if filter reset.
	 * 
	 * @return the if filter reset
	 */
	public boolean getIfFilterReset(){
		return ifFilterReset;
	}
	
	/**
	 * Sets the if filter reset.
	 * 
	 * @param ifFilterReset
	 *            the new if filter reset
	 */
	public void setIfFilterReset(boolean ifFilterReset){
		this.ifFilterReset = ifFilterReset;
	}
	
}
