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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.websocket.Session;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.slf4j.Logger;

import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.graph.execution.tracking.connection.HydrographServerConnection;
import hydrograph.ui.graph.execution.tracking.preferences.Utils;
import hydrograph.ui.graph.execution.tracking.replay.ViewExecutionHistoryUtility;
import hydrograph.ui.graph.execution.tracking.utils.TrackingDisplayUtils;
import hydrograph.ui.graph.handler.JobHandler;
import hydrograph.ui.graph.handler.StopJobHandler;
import hydrograph.ui.graph.utility.JobScpAndProcessUtility;
import hydrograph.ui.graph.utility.ViewDataUtils;
import hydrograph.ui.joblogger.JobLogger;
import hydrograph.ui.logging.factory.LogFactory;


/**
 * The Class DebugLocalJobLauncher run the job in debug mode locally.
 * 
 */
public class DebugLocalJobLauncher extends AbstractJobLauncher{

	/** The logger. */
	private static Logger logger = LogFactory.INSTANCE.getLogger(DebugLocalJobLauncher.class);
	
	/** The Constant BUILD_SUCCESSFUL. */
	private static final String BUILD_SUCCESSFUL="BUILD SUCCESSFUL";
	
	/** The Constant BUILD_FAILED. */
	private static final String BUILD_FAILED="BUILD FAILED";
	
	/** The Constant JOB_FAILED. */
	private static final String JOB_FAILED="JOB FAILED";
	
	/** The Constant JOB_COMPLETED_SUCCESSFULLY. */
	private static final String JOB_COMPLETED_SUCCESSFULLY="JOB COMPLETED SUCCESSFULLY";
	private static final String JOB_KILLED_SUCCESSFULLY = "JOB KILLED SUCCESSFULLY";
	
	
	/**
	 * Run the job in debug mode locally.
	 * 
	 * @param xmlPath
	 * @param debugXmlPath
	 * @param paramFile
	 * @param job
	 * @param gefCanvas
	 * @param externalFiles Empty list required only in remote run.
	 * @param subJobList Empty list required only in remote run.
	 * 
	 * 
	 */
	@Override
public void launchJobInDebug(String xmlPath, String debugXmlPath,String paramFile,String userFunctionsPropertyFile, Job job,	DefaultGEFCanvas gefCanvas,List<String> externalFiles,List<String> subJobList) {
		Session session=null;

		if(isExecutionTrackingOn()){
			HydrographServerConnection hydrographServerConnection = new HydrographServerConnection();
			session = hydrographServerConnection.connectToServer(job, job.getUniqueJobId(), 
					webSocketLocalHost);
		if(hydrographServerConnection.getSelection() == 1){
			TrackingDisplayUtils.INSTANCE.closeWebSocketConnection(session);
			return;
		}
		} 
		String projectName = xmlPath.split("/", 2)[0];
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		job.setJobProjectDirectory(project.getLocation().toOSString());

		String gradleCommand;

		job.setJobStatus(JobStatus.RUNNING);
		((JobHandler) RunStopButtonCommunicator.RunJob.getHandler()).setRunJobEnabled(false);
		((StopJobHandler) RunStopButtonCommunicator.StopJob.getHandler()).setStopJobEnabled(true);
		
		enableLockedResources(gefCanvas);
		gradleCommand = getExecututeJobCommand(xmlPath, paramFile,userFunctionsPropertyFile, debugXmlPath,job);
		executeCommand(job, project, gradleCommand, gefCanvas);

		if(job.getJobStatus().equalsIgnoreCase(JobStatus.RUNNING) || job.getJobStatus().equalsIgnoreCase(JobStatus.PENDING)){
			job.setJobStatus(JobStatus.SUCCESS);
		}

		if (job.getCanvasName().equals(JobManager.INSTANCE.getActiveCanvas())) {
			JobManager.INSTANCE.enableRunJob(true);
		}
		
		ViewDataUtils.getInstance().addViewDataJobDetails(job.getConsoleName(), job);
		refreshProject(gefCanvas);
		JobManager.INSTANCE.removeJob(job.getCanvasName());
		ViewExecutionHistoryUtility.INSTANCE.addTrackingJobs(job.getConsoleName(), job);
		TrackingDisplayUtils.INSTANCE.closeWebSocketConnection(session);
	}

	/**
	 * Execute command.
	 *
	 * @param job the job
	 * @param project the project
	 * @param gradleCommand the gradle command
	 * @param gefCanvas the gef canvas
	 */
	private void executeCommand(Job job, IProject project, String gradleCommand, DefaultGEFCanvas gefCanvas) {
		ProcessBuilder processBuilder = JobScpAndProcessUtility.INSTANCE.getProcess(project, gradleCommand);
		try {
			Process process = processBuilder.start();

			job.setLocalJobProcess(process);
			JobLogger joblogger = initJobLogger(gefCanvas,job.getUniqueJobId());

			JobManager.INSTANCE.addJob(job);
			logProcessLogsAsynchronously(joblogger, process, job);

		} catch (IOException e) {
			logger.debug("Unable to execute the job", e);
		}
	}
	
	/**
	 * Gets the executute job command.
	 *
	 * @param xmlPath the xml path
	 * @param paramFile the param file
	 * @param debugXmlPath the debug xml path
	 * @param job the job
	 * @return the executute job command
	 */
	private String getExecututeJobCommand(String xmlPath, String paramFile,String userFunctionsPropertyFile, String debugXmlPath, Job job) {
		StringBuffer exeCommond = new StringBuffer();
		
		exeCommond.append(GradleCommandConstants.GCMD_EXECUTE_DEBUG_LOCAL_JOB )
			.append(GradleCommandConstants.GPARAM_PARAM_FILE + paramFile + GradleCommandConstants.GPARAM_JOB_XML)
			.append(xmlPath.split("/", 2)[1] + GradleCommandConstants.GPARAM_LOCAL_JOB)
			.append(GradleCommandConstants.GPARAM_JOB_DEBUG_XML + debugXmlPath.split("/", 2)[1])
			.append(GradleCommandConstants.GPARAM_JOB_BASE_PATH + job.getBasePath())
			.append(GradleCommandConstants.GPARAM_UNIQUE_JOB_ID + job.getUniqueJobId())
			.append(GradleCommandConstants.GPARAM_IS_EXECUTION_TRACKING_ON + job.isExecutionTrack())
			.append(GradleCommandConstants.GPARAM_EXECUTION_TRACKING_PORT)
			.append(TrackingDisplayUtils.INSTANCE.getPortFromPreference())
			.append(GradleCommandConstants.GPARAM_USER_DEFINED_FUNCTIONS_PATH + userFunctionsPropertyFile)
			.append(GradleCommandConstants.GPARAM_CONSOLE_LOGGING_LEVEL + Utils.INSTANCE.getConsoleLogLevel());

	return exeCommond.toString();
	}
	
	/**
	 * Log process logs asynchronously.
	 *
	 * @param joblogger the joblogger
	 * @param process the process
	 * @param job the job
	 */
	private void logProcessLogsAsynchronously(final JobLogger joblogger, final Process process, final Job job) {

		InputStream stream = process.getInputStream();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream));
			String line = null;

			while ((line = reader.readLine()) != null) {
				if (line.contains("Gradle build daemon has been stopped.")) {
					job.setJobStatus(JobStatus.KILLED);
					joblogger.logMessage(JOB_KILLED_SUCCESSFULLY);
					break;
				}else if(line.contains(JOB_FAILED)){
					job.setJobStatus(JobStatus.FAILED);
				}
				
				if(!line.contains(BUILD_SUCCESSFUL) && !line.contains(BUILD_FAILED)){
					joblogger.logMessage(line);
				}else{
					if (job.getJobStatus().equalsIgnoreCase(JobStatus.KILLED)){
							joblogger.logMessage(JOB_KILLED_SUCCESSFULLY);
						} else if(job.getJobStatus().equalsIgnoreCase(JobStatus.FAILED)){
							joblogger.logMessage(JOB_FAILED);
						}else if(line.contains(BUILD_FAILED)){
							joblogger.logMessage(BUILD_FAILED);
						}else{
							joblogger.logMessage(JOB_COMPLETED_SUCCESSFULLY);
						}
				}
			}
		} catch (IOException e) {
			logger.info("Error occured while reading run job log", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("Ignore the exception", e);
				}
			}
		}
		joblogger.logJobEndInfo(job.getUniqueJobId(), ViewExecutionHistoryUtility.getInstance().getTrackingLogPath());
		joblogger.close();
	}


	@Override
	public void launchJob(String xmlPath, String paramFile,String userFunctionsPropertyFile, Job job,
			DefaultGEFCanvas gefCanvas,List<String> externalSchemaFiles,List<String> subJobList) {
		
	}

	@Override
	public void killJob(Job jobToKill) {
		JobScpAndProcessUtility.INSTANCE.killLocalJobProcess(jobToKill);
	}

}
