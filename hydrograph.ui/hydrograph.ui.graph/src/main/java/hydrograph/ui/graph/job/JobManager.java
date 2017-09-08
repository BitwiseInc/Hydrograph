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

 
package hydrograph.ui.graph.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.common.util.MultiParameterFileUIUtils;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.datastructures.executiontracking.ViewExecutionTrackingDetails;
import hydrograph.ui.datastructures.parametergrid.ParameterFile;
import hydrograph.ui.datastructures.parametergrid.filetype.ParamterFileTypes;
import hydrograph.ui.dataviewer.window.DebugDataViewer;
import hydrograph.ui.graph.Activator;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.dialog.SaveJobFileBeforeRunDialog;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.execution.tracking.datastructure.ExecutionStatus;
import hydrograph.ui.graph.execution.tracking.logger.ExecutionTrackingFileLogger;
import hydrograph.ui.graph.execution.tracking.preferences.ExecutionPreferenceConstants;
import hydrograph.ui.graph.execution.tracking.preferences.JobRunPreference;
import hydrograph.ui.graph.execution.tracking.replay.ViewExecutionHistoryUtility;
import hydrograph.ui.graph.execution.tracking.utils.TrackingDisplayUtils;
import hydrograph.ui.graph.execution.tracking.windows.ExecutionTrackingConsole;
import hydrograph.ui.graph.handler.JobHandler;
import hydrograph.ui.graph.handler.StopJobHandler;
import hydrograph.ui.graph.utility.CanvasUtils;
import hydrograph.ui.graph.utility.DataViewerUtility;
import hydrograph.ui.graph.utility.JobScpAndProcessUtility;
import hydrograph.ui.graph.utility.MessageBox;
import hydrograph.ui.graph.utility.SubJobUtility;
import hydrograph.ui.joblogger.JobLogger;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.parametergrid.dialog.MultiParameterFileDialog;
import hydrograph.ui.propertywindow.runconfig.RunConfigDialog;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;


/**
 * 
 * Job manager maintains list of executing job. This class is responsible for executing and killing given job
 * 
 * @author Bitwise
 * 
 */
public class JobManager {

	/** The logger. */
	private static Logger logger = LogFactory.INSTANCE.getLogger(JobManager.class);
	
	/** The running jobs map. */
	private Map<String, Job> runningJobsMap;
	
	/** The instance. */
	public static JobManager INSTANCE = new JobManager();
	
	/** The local mode. */
	private boolean localMode;
	
	/** The Constant DEBUG_FILE_EXTENTION. */
	private static final String DEBUG_FILE_EXTENTION="_debug.xml";
	
	/** The Constant PROJECT_METADATA_FILE. */
	public static final String PROJECT_METADATA_FILE=File.separator+"project.metadata";
	
	/** The data viewer map. */
	private Map<String,DebugDataViewer> dataViewerMap;		
	
	/** The previously executed jobs. */
	private Map<String,Job> previouslyExecutedJobs;
	
	/** The active canvas. */
	private String activeCanvas;
	
	/** The execution tracking consoles. */
	private Map<String,ExecutionTrackingConsole> executionTrackingConsoles;
	
	
	/**
	 * Checks if is local mode.
	 *
	 * @return true, if is local mode
	 */
	public boolean isLocalMode() {
		return localMode;
	}

	/**
	 * Gets the previously executed jobs.
	 *
	 * @return the previously executed jobs
	 */
	public Map<String, Job> getPreviouslyExecutedJobs() {
		return previouslyExecutedJobs;
	}
	
	/**
	 * Sets the local mode.
	 *
	 * @param localMode the new local mode
	 */
	public void setLocalMode(boolean localMode) {
		this.localMode = localMode;
	}
	
	/**
	 * Gets the data viewer map.
	 *
	 * @return the data viewer map
	 */
	public Map<String, DebugDataViewer> getDataViewerMap() {
		return dataViewerMap;
	}

	/**
	 * Sets the data viewer map.
	 *
	 * @param dataViewerMap2 the data viewer map2
	 */
	public void setDataViewerMap(Map<String, DebugDataViewer> dataViewerMap2) {
		this.dataViewerMap = dataViewerMap2;
	}
	
	/**
	 * Gets the execution tracking consoles.
	 *
	 * @return the execution tracking consoles
	 */
	public Map<String, ExecutionTrackingConsole> getExecutionTrackingConsoles() {
		return executionTrackingConsoles;
	}

	/**
	 * Sets the execution tracking consoles.
	 *
	 * @param executionTrackingConsoles the execution tracking consoles
	 */
	public void setExecutionTrackingConsoles(
			Map<String, ExecutionTrackingConsole> executionTrackingConsoles) {
		this.executionTrackingConsoles = executionTrackingConsoles;
	}
	
	/**
	 * Instantiates a new job manager.
	 */
	private JobManager() {
		previouslyExecutedJobs = new LinkedHashMap<>();
		runningJobsMap = new LinkedHashMap<>();
		executionTrackingConsoles = new LinkedHashMap<>();
	}
	
	/**
	 * Returns active editor as {@link DefaultGEFCanvas}.
	 *
	 * @return {@link DefaultGEFCanvas}
	 */
	private DefaultGEFCanvas getComponentCanvas() {		
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof DefaultGEFCanvas){
			return (DefaultGEFCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		}
		else{
			return null;
		}
	}

	/**
	 * Register job with Job Manager.
	 *
	 * @param job            - {@link Job}
	 */
	void addJob(Job job) {
		
		//openJobTrackingConsole(job);
		
		runningJobsMap.put(job.getLocalJobID(), job);
		
		logger.debug("Added job " + job.getCanvasName() + " to job map");
	}


	/**
	 * Deregister job with Job Manager.
	 *
	 * @param canvasId the canvas id
	 */
	void removeJob(String canvasId) {		
		if(runningJobsMap.get(canvasId)==null){
			return;
		}
		
		List<ExecutionStatus> executionStatusListOfCurrentJob = new ArrayList<>();
		List<ExecutionStatus> executionStatusList = ExecutionTrackingFileLogger.INSTANCE.getExecutionStatusList();
		for (ExecutionStatus executionStatus : executionStatusList) {
			if (executionStatus.getJobId().equalsIgnoreCase(runningJobsMap.get(canvasId).getUniqueJobId())) {
				 executionStatusListOfCurrentJob.add(executionStatus);
			}
		}
		ExecutionTrackingFileLogger.INSTANCE.getExecutionStatusList().removeAll(executionStatusListOfCurrentJob);
		runningJobsMap.remove(canvasId);		
		logger.debug("Removed job " + canvasId + " from jobmap");
	}

	/**
	 * Toggles state of Run and Stop button if enabled is true Run button will enable and stop button will disable if
	 * enable is false Run button will disbale and stop will enable.
	 *
	 * @param enabled the enabled
	 */
	public void enableRunJob(boolean enabled) {
		((JobHandler)RunStopButtonCommunicator.RunJob.getHandler()).setRunJobEnabled(enabled);
		((StopJobHandler)RunStopButtonCommunicator.StopJob.getHandler()).setStopJobEnabled(!enabled);
	}
	
	/**
	 * execute job.
	 *
	 * @param job            - {@link Job} to execute
	 * @param uniqueJobId the unique job id
	 * @param runConfigDialog the run config dialog
	 */
	public void executeJob(final Job job, String uniqueJobId,RunConfigDialog runConfigDialog) {
		List<String> externalFiles;
		List<String> subJobList;
		enableRunJob(false);
		final DefaultGEFCanvas gefCanvas = CanvasUtils.INSTANCE.getComponentCanvas();

		if (!saveJobBeforeExecute(gefCanvas)){
			return;
		}

		if (!runConfigDialog.proceedToRunGraph()){
			enableRunJob(true);
			return;
		}
		
		final MultiParameterFileDialog parameterGrid = getParameterFileDialog();
		if (parameterGrid.canRunGraph() == false){
			logger.debug("Not running graph");
			enableRunJob(true);
			return;
		}
		logger.debug("property File :" + parameterGrid.getParameterFilesForExecution());

		TrackingDisplayUtils.INSTANCE.clearTrackingStatus();
		final String xmlPath = getJobXMLPath();
		if (xmlPath == null){
			WidgetUtility.errorMessage(Messages.OPEN_GRAPH_TO_RUN);
			return;
		}

		String clusterPassword = getClusterPassword(runConfigDialog);

		String uniqueJobID = "";
		ELTGraphicalEditor eltGraphicalEditor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		uniqueJobID = eltGraphicalEditor.getUniqueJobId();
		job.setUniqueJobId(uniqueJobID);
		job.setUsername(runConfigDialog.getUsername());
		job.setPassword(clusterPassword);
		job.setHost(runConfigDialog.getHost());
		job.setDebugMode(false);
		job.setRemoteMode(runConfigDialog.isRemoteMode());
		
		if(runConfigDialog.isRemoteMode()){
			externalFiles=JobScpAndProcessUtility.INSTANCE.getExternalFilesList();
			subJobList=JobScpAndProcessUtility.INSTANCE.getSubJobList();
		}else{
			externalFiles=Collections.EMPTY_LIST;
			subJobList=Collections.EMPTY_LIST;
		}
		
		gefCanvas.disableRunningJobResource();
		
		DataViewerUtility.INSTANCE.closeDataViewerWindows(previouslyExecutedJobs.get(job.getConsoleName()));
		
		if(previouslyExecutedJobs.get(job.getConsoleName())!= null){
			if(runConfigDialog.isDebug()){
				previouslyExecutedJobs.get(job.getConsoleName()).setDebugMode(true);
			}else{
				previouslyExecutedJobs.get(job.getConsoleName()).setDebugMode(false);
			}
		}
		addExecutionDetails(job);
		
		launchJob(job, gefCanvas, parameterGrid, xmlPath,getUserFunctionsPropertertyFile() ,externalFiles,subJobList);
	}

	/**
	 * Gets the user functions properterty file.
	 *
	 * @return the user functions properterty file
	 */
	protected String getUserFunctionsPropertertyFile() {
		String userFunctionsPropertyFileRelativePath="";
		IProject project=getCurrentProjectFromActiveGraph();
		if(project!=null){
			IFolder folder=project.getFolder("resources");
			if(folder.exists()){
				IFile file=folder.getFile("UserFunctions.properties");
				if(file.exists()){
					userFunctionsPropertyFileRelativePath=file.getProjectRelativePath().toString();
				}
			}
		}
		return userFunctionsPropertyFileRelativePath;
	}

	/**
	 * Gets the current project from active graph.
	 *
	 * @return the current project from active graph
	 */
	private static IProject getCurrentProjectFromActiveGraph() {
		IEditorInput editorInput=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
		if(editorInput instanceof IFileEditorInput){
			return ((IFileEditorInput)editorInput).getFile().getProject();
		}
		return null;
	}
	
	/**
	 * This method responsible to run the job in debug mode.
	 *
	 * @param job the job
	 * @param isRemote the is remote
	 * @param userName the user name
	 */
	public void executeJobInDebug(final Job job, boolean isRemote, String userName) {
	
		List<String> externalSchemaFiles;
		List<String> subJobList;
		enableRunJob(false);
		final DefaultGEFCanvas gefCanvas = CanvasUtils.INSTANCE.getComponentCanvas();

		if (!saveJobBeforeExecute(gefCanvas)){
			return;
		}
		
		final MultiParameterFileDialog parameterGrid = getParameterFileDialog();
		if (parameterGrid.canRunGraph() == false){
			logger.debug("Not running graph");
			enableRunJob(true);
			return;
		}
		logger.debug("property File :" + parameterGrid.getParameterFilesForExecution());
	
		TrackingDisplayUtils.INSTANCE.clearTrackingStatus(job.getUniqueJobId());
		TrackingDisplayUtils.INSTANCE.changeStatusForExecTracking(job);
		final String xmlPath = getJobXMLPath();
		String debugXmlPath = getJobDebugXMLPath();
		if (xmlPath == null){
			WidgetUtility.errorMessage(Messages.OPEN_GRAPH_TO_RUN);
			return;
		}
		
		job.setUsername(userName);
		job.setRemoteMode(isRemote);
		job.setHost(job.getIpAddress());

		if(isRemote){
			externalSchemaFiles=JobScpAndProcessUtility.INSTANCE.getExternalFilesList();
			subJobList=JobScpAndProcessUtility.INSTANCE.getSubJobList();
		}else{
			externalSchemaFiles=Collections.EMPTY_LIST;
			subJobList=Collections.EMPTY_LIST;
		}

		gefCanvas.disableRunningJobResource();
		
		previouslyExecutedJobs.put(job.getConsoleName(), job);
		
		addExecutionDetails(job);
		
		launchJobWithDebugParameter(job, gefCanvas, parameterGrid, xmlPath, debugXmlPath,getUserFunctionsPropertertyFile() ,externalSchemaFiles,subJobList);
		
	}
	
	/**
	 * Check for remote or local run and start the job in new thread.  
	 *
	 * @param job the job
	 * @param gefCanvas the gef canvas
	 * @param parameterGrid the parameter grid
	 * @param xmlPath the xml path
	 * @param userFunctionsPropertertyFile the user functions properterty file
	 * @param externalFiles the external schema files
	 * @param subJobList the sub job list
	 */
	private void launchJob(final Job job, final DefaultGEFCanvas gefCanvas, final MultiParameterFileDialog parameterGrid,
			final String xmlPath,final String userFunctionsPropertertyFile,final List<String> externalFiles,final List<String> subJobList) {
		if (job.isRemoteMode()) {
			job.setExecutionTrack(isExecutionTrackingOn());
			new Thread(new Runnable() {
				@Override
				public void run() {
					AbstractJobLauncher jobLauncher = new RemoteJobLauncher();
					jobLauncher.launchJob(xmlPath, parameterGrid.getParameterFilesForExecution(),userFunctionsPropertertyFile ,job, gefCanvas,externalFiles,subJobList);
				}
			}).start();
		} else {
			setLocalMode(true);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					job.setExecutionTrack(isExecutionTrackingOn());
					AbstractJobLauncher jobLauncher = new LocalJobLauncher();
					jobLauncher.launchJob(xmlPath, parameterGrid.getParameterFilesForExecution(), userFunctionsPropertertyFile ,job, gefCanvas,externalFiles,subJobList);
				}
			}).start();
		}
	}

	/**
	 * Check for remote or local mode and start debug run in new thread.
	 *   
	 *
	 * @param job the job
	 * @param gefCanvas the gef canvas
	 * @param parameterGrid the parameter grid
	 * @param xmlPath the xml path
	 * @param debugXmlPath the debug xml path
	 * @param userFunctionsPropertertyFile the user functions properterty file
	 * @param externalSchemaFiles the external schema files
	 * @param subJobList the sub job list
	 */
	private void launchJobWithDebugParameter(final Job job, final DefaultGEFCanvas gefCanvas, final MultiParameterFileDialog parameterGrid,
			final String xmlPath, final String debugXmlPath,final String userFunctionsPropertertyFile,final List<String> externalSchemaFiles,final List<String> subJobList) {
		if (job.isRemoteMode()) {
			setLocalMode(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					AbstractJobLauncher jobLauncher = new DebugRemoteJobLauncher();
					jobLauncher.launchJobInDebug(xmlPath, debugXmlPath,  parameterGrid.getParameterFilesForExecution(),userFunctionsPropertertyFile, job, gefCanvas,externalSchemaFiles,subJobList);
				}

			}).start();
		} else {
			setLocalMode(true);
			new Thread(new Runnable() {
				@Override
				public void run() {
					AbstractJobLauncher jobLauncher = new DebugLocalJobLauncher();
					jobLauncher.launchJobInDebug(xmlPath, debugXmlPath, parameterGrid.getParameterFilesForExecution(),userFunctionsPropertertyFile ,job, gefCanvas, externalSchemaFiles,subJobList);
				}
			}).start();
		}

	}
	
	/**
	 * Gets the cluster password.
	 *
	 * @param runConfigDialog the run config dialog
	 * @return the cluster password
	 */
	private String getClusterPassword(RunConfigDialog runConfigDialog) {
		String clusterPassword = runConfigDialog.getClusterPassword() != null ? runConfigDialog.getClusterPassword()
				: "";
		return clusterPassword;
	}

	/**
	 * Get xml file path from active editor.
	 *
	 * @return the job xml path
	 */
	private String getJobXMLPath() {
		IEditorPart iEditorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		String xmlPath = iEditorPart.getEditorInput().getToolTipText()
				.replace(Messages.JOBEXTENSION, Messages.XMLEXTENSION);
		return xmlPath;
	}

	/**
	 * Gets the job debug xml path.
	 *
	 * @return the job debug xml path
	 */
	private String getJobDebugXMLPath() {
		IEditorPart iEditorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		String debugXmlPath = iEditorPart.getEditorInput().getToolTipText().replace(Messages.JOBEXTENSION,DEBUG_FILE_EXTENTION);
		 
		return debugXmlPath;
	}
	
	/**
	 * Gets the parameter file dialog.
	 *
	 * @return the parameter file dialog
	 */
	private MultiParameterFileDialog getParameterFileDialog(){
	    String activeProjectLocation=MultiParameterFileUIUtils.getActiveProjectLocation();
		List<ParameterFile> filepathList = new LinkedList<>();
		
		updateParameterFileListWithJobSpecificFile(filepathList,activeProjectLocation);
		
		File jobSpecificParamFile = new File(getComponentCanvas().getParameterFile());
		if(!jobSpecificParamFile.exists()){
			try {
				jobSpecificParamFile.createNewFile();
			} catch (IOException e) {
				logger.debug("Unable to create job specific file ", e);
				MessageBox.INSTANCE.showMessage(MessageBox.ERROR, Messages.UNABLE_TO_CREATE_JOB_SPECIFIC_FILE);
			}
		}
		
				
		
		filepathList.addAll(getComponentCanvas().getJobLevelParamterFiles());
		MultiParameterFileDialog parameterFileDialog = new MultiParameterFileDialog(Display.getDefault().getActiveShell(), activeProjectLocation);
		parameterFileDialog.setParameterFiles(filepathList);
		parameterFileDialog.setJobLevelParamterFiles(getComponentCanvas().getJobLevelParamterFiles());
		parameterFileDialog.open();
		if(SubJobUtility.getCurrentEditor().isDirty())
		{
		   if(!StringUtils.equals(Activator.getDefault().getPreferenceStore()
				.getString(JobRunPreference.SAVE_JOB_BEFORE_RUN_PREFRENCE), MessageDialogWithToggle.ALWAYS)){
			   	SaveJobFileBeforeRunDialog messageBox = new SaveJobFileBeforeRunDialog
			    (Display.getCurrent().getActiveShell(),"'"+SubJobUtility.getCurrentEditor().getEditorInput().getName()+"' "+Messages.DO_YOU_WANT_TO_SAVE_CHANGES );
			   	if(messageBox.open()==IDialogConstants.OK_ID){
			   		SubJobUtility.getCurrentEditor().doSave(null);
			   		SubJobUtility.getCurrentEditor().setDirty(false);
		    	}
		   }
		   else{
			   SubJobUtility.getCurrentEditor().doSave(null);
			   SubJobUtility.getCurrentEditor().setDirty(false);
		   }	
		}
		
		return parameterFileDialog;
	}
	
	/**
	 * Update parameter file list with job specific file.
	 *
	 * @param parameterFileList the parameter file list
	 * @param activeProjectLocation the active project location
	 */
	private void updateParameterFileListWithJobSpecificFile(List<ParameterFile> parameterFileList, String activeProjectLocation) {
		parameterFileList.add(new ParameterFile(getComponentCanvas().getJobName(), ParamterFileTypes.JOB_SPECIFIC));
	}

	/**
	 * Save job before execute.
	 *
	 * @param gefCanvas the gef canvas
	 * @return true, if successful
	 */
	private boolean saveJobBeforeExecute(final DefaultGEFCanvas gefCanvas) {
			try {
				//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().doSave(null);
				enableRunJob(true);
				if (gefCanvas.getParameterFile() == null || CanvasUtils.INSTANCE.isDirtyEditor()) {
					return false;
				} else {
					return true;
				}
			} catch (Exception e) {
				logger.debug("Unable to save graph ", e);
				enableRunJob(true);
				return false;
			}
		}

	/**
	 * Kill the job for given jobId.
	 *
	 * @param jobId the job id
	 * @param gefCanvas the gef canvas
	 */
	public void killJob(String jobId, DefaultGEFCanvas gefCanvas) {
		Job jobToKill = runningJobsMap.get(jobId);

		jobToKill.setJobStatus(JobStatus.KILLED);

		if (jobToKill.getRemoteJobProcessID() != null){
			killRemoteProcess(jobToKill,gefCanvas);
		}

	}
	
	/**
	 * Kill the job for given jobId.
	 *
	 * @param jobId the job id
	 */
	public void killJob(String jobId) {	
		Job jobToKill = runningJobsMap.get(jobId);
		if(jobToKill.isRemoteMode()){
			if(jobToKill.isDebugMode()){
				AbstractJobLauncher killObj = new DebugRemoteJobLauncher();
				killObj.killJob(jobToKill);
			}else{
				AbstractJobLauncher killObj = new RemoteJobLauncher();
				killObj.killJob(jobToKill);
			}
		}else{
				AbstractJobLauncher killObj = new LocalJobLauncher();
				killObj.killJob(jobToKill);	
		}
	}

	/**
	 * Inits the job logger.
	 *
	 * @param gefCanvas the gef canvas
	 * @return the job logger
	 */
	public JobLogger initJobLogger(DefaultGEFCanvas gefCanvas) {
		final JobLogger joblogger = new JobLogger(gefCanvas.getActiveProject(), gefCanvas.getJobName(), gefCanvas.getUniqueJobId());
		return joblogger;
	}

	/**
	 * Gets the running job.
	 *
	 * @param consoleName the console name
	 * @return the running job
	 */
	public Job getRunningJob(String consoleName) {
		return runningJobsMap.get(consoleName);
	}

	/**
	 * Kill remote process.
	 *
	 * @param job the job
	 * @param gefCanvas the gef canvas
	 */
	private void killRemoteProcess(Job job, DefaultGEFCanvas gefCanvas) {

		String gradleCommand = getKillJobCommand(job);
		String[] runCommand = new String[3];
		if (OSValidator.isWindows()){
			String[] command = { Messages.CMD, "/c", gradleCommand };
			runCommand = command;

		} else if (OSValidator.isMac()){
			String[] command = { Messages.SHELL, "-c", gradleCommand };
			runCommand = command;
		}

		ProcessBuilder processBuilder = new ProcessBuilder(runCommand);
		processBuilder.directory(new File(job.getJobProjectDirectory()));
		processBuilder.redirectErrorStream(true);
		try {
			Process process = processBuilder.start();
			if(gefCanvas!=null)
			logKillProcessLogsAsyncronously(process, job, gefCanvas);
		} catch (IOException e) {
			logger.debug("Unable to kill the job", e);
		}
	}
	
	/**
	 * This method kills all running remote job. 
	 * 
	 */
	public void killALLRemoteProcess() {
		for (Entry<String, Job> entry : JobManager.INSTANCE.getRunningJobsMap().entrySet()) {
			if ( entry.getValue().isRemoteMode()) {
				killRemoteProcess( entry.getValue(), null);
			}
		}
	}

	/**
	 * Release resources.
	 *
	 * @param job the job
	 * @param gefCanvas the gef canvas
	 * @param joblogger the joblogger
	 */
	public void releaseResources(Job job, DefaultGEFCanvas gefCanvas, JobLogger joblogger) {
		enableLockedResources(gefCanvas);
		refreshProject(gefCanvas);
		if (job.getCanvasName().equals(JobManager.INSTANCE.getActiveCanvas())){
			JobManager.INSTANCE.enableRunJob(true);
		}
		JobManager.INSTANCE.removeJob(job.getCanvasName());
		joblogger.close();
		JobManager.INSTANCE.removeJob(job.getLocalJobID());
	}

	/**
	 * Enables locked resources..like job canvas
	 *
	 * @param gefCanvas the gef canvas
	 */
	protected void enableLockedResources(final DefaultGEFCanvas gefCanvas) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				gefCanvas.enableRunningJobResource();
			}
		});
	}

	/**
	 * Refresh project directory corresponding to given {@link DefaultGEFCanvas}.
	 *
	 * @param gefCanvas the gef canvas
	 */
	protected void refreshProject(DefaultGEFCanvas gefCanvas) {
		IEditorPart iEditorPart = ((IEditorPart) gefCanvas);
		String projectName = ((IFileEditorInput) iEditorPart.getEditorInput()).getFile().getProject().getName();
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		try {
			iProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			logger.error("Error while refreshing the project", e);
		}
	}

	/**
	 * Log kill process logs asyncronously.
	 *
	 * @param process the process
	 * @param job the job
	 * @param gefCanvas the gef canvas
	 */
	private void logKillProcessLogsAsyncronously(final Process process, final Job job, final DefaultGEFCanvas gefCanvas) {
		final JobLogger joblogger = initJobLogger(gefCanvas);
		new Thread(new Runnable() {
			private InputStream stream = process.getInputStream();

			@Override
			public void run() {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(stream));
					String line = null;

					while ((line = reader.readLine()) != null) {
						joblogger.logMessage(line);
					}
				} catch (IOException e) {
					logger.info("Error occured while reading run job log", e);
				} finally {
					if (reader != null){
						try {
							reader.close();
						} catch (IOException e) {
							logger.error("Ignore the exception", e);
						}
					}
				}

				releaseResources(job, gefCanvas, joblogger);
			}

		}).start();
	}
	
	/**
	 * Create Gradle command to kill the job.
	 *
	 * @param job the job
	 * @return the kill job command
	 */
	private String getKillJobCommand(Job job) {
		return GradleCommandConstants.GCMD_KILL_REMOTE_JOB + GradleCommandConstants.GPARAM_HOST + job.getHost()
				+ GradleCommandConstants.GPARAM_USERNAME + job.getUsername() + GradleCommandConstants.GPARAM_PASSWORD
				+ job.getPassword() + GradleCommandConstants.GPARAM_REMOTE_PROCESSID + job.getRemoteJobProcessID();
	}

	/**
	 * isJobRunning() returns true of job is executing for given console.
	 *
	 * @param consoleName the console name
	 * @return true, if is job running
	 */
	public boolean isJobRunning(String consoleName) {
		return runningJobsMap.containsKey(consoleName);
	}

	/**
	 * Set active console id.
	 *
	 * @param activeCanvas the new active canvas id
	 */
	public void setActiveCanvasId(String activeCanvas) {
		this.activeCanvas = activeCanvas;
	}

	/**
	 * Returns active canvas id.
	 *
	 * @return - String (active canvas id)
	 */
	public String getActiveCanvas() {
		return activeCanvas;
	}

	/**
	 * Gets the running jobs map.
	 *
	 * @return the running jobs map
	 */
	public Map<String, Job> getRunningJobsMap() {
		return runningJobsMap;
	}

	/**
	 * Check if the file path is absolute else return workspace file path.
	 *
	 * @param jobFilePath the job file path
	 * @return the absolute path from file
	 */
	public static String getAbsolutePathFromFile(IPath jobFilePath) {
		if (ResourcesPlugin.getWorkspace().getRoot().getFile(jobFilePath).exists()) {
			return ResourcesPlugin.getWorkspace().getRoot().getFile(jobFilePath).getLocation().toString();
		} else if (jobFilePath.toFile().exists()) {
			return jobFilePath.toFile().getAbsolutePath();
		}
		return "";
	}
	
	
	/**
	 * Checks if is execution tracking on.
	 *
	 * @return true, if is execution tracking on
	 */
	public boolean isExecutionTrackingOn(){
		boolean isExeTrackingOn = Platform.getPreferencesService().getBoolean(Activator.PLUGIN_ID, 
				ExecutionPreferenceConstants.EXECUTION_TRACKING, true, null);
		
		return isExeTrackingOn;
	}
	
	private void addExecutionDetails(Job job){
		if(job.isExecutionTrack()){
			//add execution tracking details on job execution
			ViewExecutionHistoryUtility.INSTANCE.addTrackingPathDetails(job.getUniqueJobId(), 
					new ViewExecutionTrackingDetails.Builder(job.getUniqueJobId(), ViewExecutionHistoryUtility.INSTANCE.getTrackingLogPath(),
							job.isExecutionTrack()).build());
		}
	}

	/**
	 * Kill the local job on tool close for given jobId.
	 * @param jobId
	 */
	public void killLocalJobProcess(Job job){
		if(!job.isRemoteMode()){
			try(InputStream	stream = Runtime.getRuntime().exec("jps -m").getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
				String line = null;
				while ((line = reader.readLine()) != null) {
					if(line.contains(job.getUniqueJobId())){
						logger.debug("Trying to kill the running job on tool close for local");
						String[] jpsOutput = line.split(" ");
						if (OSValidator.isWindows()) {
							Runtime.getRuntime().exec("TASKKILL /F /PID "+ jpsOutput[0]);
						}else if (OSValidator.isMac()){
							Runtime.getRuntime().exec("bash -c kill -9 "+jpsOutput[0]);
						}
						return;
					}
				}
			}catch(IOException e){
				logger.info("Fail to run kill process.");
			}
		}
	}
}
