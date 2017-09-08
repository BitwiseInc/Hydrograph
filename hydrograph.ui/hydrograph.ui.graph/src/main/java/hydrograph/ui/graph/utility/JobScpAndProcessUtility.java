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

package hydrograph.ui.graph.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import ch.qos.logback.classic.pattern.Util;
import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.ExternalWidgetData;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.execution.tracking.connection.HydrographServerConnection;
import hydrograph.ui.graph.execution.tracking.preferences.Utils;
import hydrograph.ui.graph.execution.tracking.utils.TrackingDisplayUtils;
import hydrograph.ui.graph.handler.StopJobHandler;
import hydrograph.ui.graph.job.GradleCommandConstants;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.graph.job.JobManager;
import hydrograph.ui.graph.job.JobStatus;
import hydrograph.ui.graph.job.RunStopButtonCommunicator;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.joblogger.JobLogger;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
/**
 * 
 * JobScpAndProcessUtility use to create gradle command and process builder.
 * 
 * @author Bitwise
 *
 */
public class JobScpAndProcessUtility {

	private static Logger logger = LogFactory.INSTANCE.getLogger(JobScpAndProcessUtility.class);
	public static final JobScpAndProcessUtility INSTANCE = new JobScpAndProcessUtility();
	private static final String JPS_COMMAND_TO_FIND_JAVA_PROCESES = "jps -m";
	private static final String WINDOWS_COMMAND_TO_KILL_PROCESS = "TASKKILL /F /PID ";
	private static final String MAC_COMMAND_TO_KILL_PROCESS = "bash -c kill -9 ";
	private static final String JOB_KILLED_SUCCESSFULLY = "JOB KILLED SUCCESSFULLY";

	private JobScpAndProcessUtility(){
		
	}
	/**
	 * Gradle command to SCP project jar at remote server.
	 * @param job
	 * @return command
	 */
	public  String getLibararyScpCommand(Job job) {
		StringBuffer command = new StringBuffer();
		command.append(GradleCommandConstants.GCMD_SCP_JAR).append(GradleCommandConstants.GPARAM_HOST)
			.append(job.getHost()).append(GradleCommandConstants.GPARAM_USERNAME).append(job.getUsername())
			.append(GradleCommandConstants.GPARAM_PASSWORD).append(job.getPassword())
			.append(GradleCommandConstants.GPARAM_PASSKEY).append(job.getKeyFile())
			.append(GradleCommandConstants.GPARAM_USE_PASSWORD).append(job.isUsePassword());
		return command.toString();
	}
	
	
	/**
	 * Gradle command to SCP project jar at remote server.
	 * @param job
	 * @return command
	 */
	public  String getScpCommandForMovingLibFolderJarFiles(Job job) {
		StringBuffer command = new StringBuffer();
		command.append(GradleCommandConstants.GCMD_SCP_LIB_FOLDER_JAR_FILES).append(GradleCommandConstants.GPARAM_HOST).append(job.getHost())
			.append(GradleCommandConstants.GPARAM_USERNAME).append(job.getUsername())
			.append(GradleCommandConstants.GPARAM_PASSWORD).append(job.getPassword())
			.append(GradleCommandConstants.GPARAM_PASSKEY).append(job.getKeyFile())
			.append(GradleCommandConstants.GPARAM_USE_PASSWORD).append(job.isUsePassword());
		return command.toString();
	}
	
	
	/**
	 * Gradle command to SCP project jar at remote server.
	 * @param job
	 * @return command
	 */
	public  String getScpCommandForMovingUserFunctionsPropertyFile(Job job) {
		StringBuffer command = new StringBuffer();
		command.append(GradleCommandConstants.GCMD_SCP_USER_FUNCTIONS_PROPERTY_FILE).append(GradleCommandConstants.GPARAM_HOST).append(job.getHost())
			.append(GradleCommandConstants.GPARAM_USERNAME).append(job.getUsername())
			.append(GradleCommandConstants.GPARAM_PASSWORD).append(job.getPassword())
			.append(GradleCommandConstants.GPARAM_PASSKEY).append(job.getKeyFile())
			.append(GradleCommandConstants.GPARAM_USE_PASSWORD).append(job.isUsePassword());
		return command.toString();
	}
	
	
	/**
	 * Gradle command to SCP job xml at remote server.
	 * @param xmlPath
	 * @param debugXmlPath
	 * @param job
	 * @return command
	 */
	public  String getJobXMLScpCommand(String xmlPath, String debugXmlPath, Job job) {
		StringBuffer command = new StringBuffer();
		command.append(GradleCommandConstants.GCMD_SCP_JOB_XML).append(GradleCommandConstants.GPARAM_HOST).append(job.getHost())
			.append(GradleCommandConstants.GPARAM_USERNAME + job.getUsername())
			.append(GradleCommandConstants.GPARAM_PASSWORD).append(job.getPassword())
			.append(GradleCommandConstants.GPARAM_PASSKEY).append(job.getKeyFile())
			.append(GradleCommandConstants.GPARAM_USE_PASSWORD).append(job.isUsePassword())
			.append(GradleCommandConstants.GPARAM_JOB_XML).append(xmlPath.split("/", 2)[1]);
		
		if(!"".equalsIgnoreCase(debugXmlPath.trim()))
			command.append(GradleCommandConstants.GPARAM_JOB_DEBUG_XML).append(debugXmlPath.split("/", 2)[1]);
		
		return command.toString();
	}
	/**
	 * Gradle command to SCP param file at remote server.
	 * @param paramFile
	 * @param job
	 * @return command
	 */
	public  String getParameterFileScpCommand(String paramFile, Job job) {
		StringBuffer command = new StringBuffer();
		command.append(GradleCommandConstants.GCMD_SCP_PARM_FILE).append(GradleCommandConstants.GPARAM_HOST).append(job.getHost())
			.append(GradleCommandConstants.GPARAM_USERNAME).append(job.getUsername())
			.append(GradleCommandConstants.GPARAM_PASSWORD).append(job.getPassword())
			.append(GradleCommandConstants.GPARAM_PASSKEY).append(job.getKeyFile())
			.append(GradleCommandConstants.GPARAM_USE_PASSWORD).append(job.isUsePassword())
			.append(GradleCommandConstants.GPARAM_PARAM_FILE).append("\""+ paramFile+"\"");
		return command.toString();
	}
	/**
	 * Gradle command to execute job on remote server.
	 * @param xmlPath
	 * @param debugXmlPath
	 * @param paramFile
	 * @param userFunctionsPropertyFile 
	 * @param job
	 * @return command
	 */
	public  String getExecututeJobCommand(String xmlPath,String debugXmlPath, String paramFile, String userFunctionsPropertyFile, Job job) {
		StringBuffer command = new StringBuffer();
		if(!"".equalsIgnoreCase(debugXmlPath.trim())){
			command.append(GradleCommandConstants.GCMD_EXECUTE_DEBUG_REMOTE_JOB)
					.append(GradleCommandConstants.GPARAM_HOST).append(job.getHost())
					.append(GradleCommandConstants.GPARAM_USERNAME).append(job.getUsername())
					.append(GradleCommandConstants.GPARAM_PASSWORD).append(job.getPassword())
					.append(GradleCommandConstants.GPARAM_PASSKEY).append(job.getKeyFile())
					.append(GradleCommandConstants.GPARAM_USE_PASSWORD).append(job.isUsePassword())
					.append(GradleCommandConstants.GPARAM_PARAM_FILE).append("\"").append(paramFile).append("\"")
					.append(GradleCommandConstants.GPARAM_JOB_XML).append(xmlPath.split("/", 2)[1]) 
					.append(GradleCommandConstants.GPARAM_JOB_DEBUG_XML).append(debugXmlPath.split("/", 2)[1]) 
					.append(GradleCommandConstants.GPARAM_JOB_BASE_PATH).append(job.getBasePath()) 
					.append(GradleCommandConstants.GPARAM_UNIQUE_JOB_ID).append(job.getUniqueJobId()) 
					.append(GradleCommandConstants.GPARAM_USER_DEFINED_FUNCTIONS_PATH).append(userFunctionsPropertyFile)
					.append(GradleCommandConstants.GPARAM_IS_EXECUTION_TRACKING_ON).append(job.isExecutionTrack())
					.append(GradleCommandConstants.GPARAM_EXECUTION_TRACKING_PORT).append(TrackingDisplayUtils.INSTANCE.getRemotePortFromPreference())
					.append(GradleCommandConstants.GPARAM_CONSOLE_LOGGING_LEVEL+Utils.INSTANCE.getConsoleLogLevel());

		}else{
			command.append(GradleCommandConstants.GCMD_EXECUTE_REMOTE_JOB)
			.append(GradleCommandConstants.GPARAM_HOST).append(job.getHost()).append(GradleCommandConstants.GPARAM_USERNAME )
			.append(job.getUsername())
			.append(GradleCommandConstants.GPARAM_PASSWORD).append(job.getPassword())
			.append(GradleCommandConstants.GPARAM_PASSKEY).append(job.getKeyFile())
			.append(GradleCommandConstants.GPARAM_USE_PASSWORD).append(job.isUsePassword())
			.append(GradleCommandConstants.GPARAM_PARAM_FILE).append("\"").append(paramFile).append("\"")
			.append(GradleCommandConstants.GPARAM_JOB_XML).append(xmlPath.split("/", 2)[1])
			.append(GradleCommandConstants.GPARAM_UNIQUE_JOB_ID).append(job.getUniqueJobId())
			.append(GradleCommandConstants.GPARAM_USER_DEFINED_FUNCTIONS_PATH).append(userFunctionsPropertyFile)
			.append(GradleCommandConstants.GPARAM_IS_EXECUTION_TRACKING_ON).append(job.isExecutionTrack())
			.append(GradleCommandConstants.GPARAM_EXECUTION_TRACKING_PORT).append(TrackingDisplayUtils.INSTANCE.getRemotePortFromPreference())
			.append(GradleCommandConstants.GPARAM_CONSOLE_LOGGING_LEVEL+Utils.INSTANCE.getConsoleLogLevel());
		}
		logger.debug("Gradle Command: {}", command.toString());
		return command.toString();
	}
	
	/**
	 * Give command that move all external files to remote server on created directory same as project relative path.  
	 * @param externalFiles
	 * @param job
	 * @return String Command to scp external files.
	 */
	public String getExternalFilesScpCommand(List<String> externalFiles,Job job) {
		StringBuffer command = new StringBuffer();
		String externalfiles =  StringUtils.join(externalFiles, ",");
		command.append(GradleCommandConstants.GCMD_SCP_EXTERNAL_FILES).append(GradleCommandConstants.GPARAM_HOST).append(job.getHost())
		.append(GradleCommandConstants.GPARAM_USERNAME).append(job.getUsername())
		.append(GradleCommandConstants.GPARAM_PASSWORD).append(job.getPassword())
		.append(GradleCommandConstants.GPARAM_PASSKEY).append(job.getKeyFile())
		.append(GradleCommandConstants.GPARAM_USE_PASSWORD).append(job.isUsePassword())
		.append(GradleCommandConstants.GPARAM_MOVE_FILES).append("\"").append(externalfiles).append("\"");
		return command.toString();
	}
	
	
	/**
	 * 
	 * @param externalFiles
	 * @param job
	 * @return
	 */
	public String getSubjobScpCommand(List<String> subJobList,Job job) {
		StringBuffer command = new StringBuffer();
		String subJobFiles =  StringUtils.join(subJobList, ",");
		command.append(GradleCommandConstants.GCMD_SCP_SUBJOB_FILES).append( GradleCommandConstants.GPARAM_HOST).append(job.getHost())
		.append(GradleCommandConstants.GPARAM_USERNAME).append(job.getUsername())
		.append(GradleCommandConstants.GPARAM_PASSWORD).append(job.getPassword())
		.append(GradleCommandConstants.GPARAM_PASSKEY).append(job.getKeyFile())
		.append(GradleCommandConstants.GPARAM_USE_PASSWORD).append(job.isUsePassword())
		.append(GradleCommandConstants.GPARAM_MOVE_SUBJOB).append("\"").append(subJobFiles).append("\"");
		return command.toString();
	}

	/**
	 * 
	 * return directory creation command to create directory structure on remote server needed to run job and move required files.  
	 * @param job
	 * @param paramFile 
	 * @param xmlPath
	 * @param project
	 * @param externalFiles
	 * @return String (Command)
	 */
	public  String getCreateDirectoryCommand(Job job,String paramFile,String  xmlPath,String project,List<String> externalFiles,List<String> subJobList) {
		xmlPath = getDirectoryPath(xmlPath);
		//Get comma separated files from list.
		String external_Files="";
		String subJobFiles="";
		if(!externalFiles.isEmpty())
			external_Files = getCommaSeparatedDirectories(externalFiles);
		
		if(!subJobList.isEmpty()) 
			subJobFiles = getCommaSeparatedDirectories(subJobList);
		StringBuffer command = new StringBuffer();
		command.append(GradleCommandConstants.GCMD_CREATE_DIRECTORIES).append(GradleCommandConstants.GPARAM_HOST).append(job.getHost())
		.append(GradleCommandConstants.GPARAM_USERNAME).append(job.getUsername())
		.append(GradleCommandConstants.GPARAM_PASSWORD).append(job.getPassword())
		.append(GradleCommandConstants.GPARAM_PASSKEY).append(job.getKeyFile())
		.append(GradleCommandConstants.GPARAM_USE_PASSWORD).append(job.isUsePassword())
		.append(GradleCommandConstants.GPARAM_JOB_XML).append(xmlPath)
		.append(GradleCommandConstants.GPARAM_MOVE_PARAM_FILE).append(project).append("/")
		.append(GradleCommandConstants.REMOTE_FIXED_DIRECTORY_PARAM)
		.append(GradleCommandConstants.GPARAM_MOVE_EXTERNAL_FILES).append(external_Files)
		.append(GradleCommandConstants.GPARAM_MOVE_SUBJOB_FILES).append(subJobFiles)
		.append(GradleCommandConstants.GPARAM_MOVE_JAR).append(project).append("/")
		.append(GradleCommandConstants.REMOTE_FIXED_DIRECTORY_LIB).append(GradleCommandConstants.GPARAM_RESOUCES_FILES)
		.append(project).append("/").append(GradleCommandConstants.REMOTE_FIXED_DIRECTORY_RESOURCES);
		return command.toString();
	}

	/**
	 * 
	 * Create process builder as per operating system.
	 * @param project
	 * @param gradleCommand
	 * @return process builder
	 */
	public  ProcessBuilder getProcess(IProject project, String gradleCommand) {
		String[] runCommand = new String[3];
		if (OSValidator.isWindows()) {
			String[] command = { Messages.CMD, "/c", gradleCommand };
			runCommand = command;

		} else if (OSValidator.isMac()) {
			String[] command = { Messages.SHELL, "-c", gradleCommand };
			runCommand = command;
		}

		ProcessBuilder processBuilder = new ProcessBuilder(runCommand);
		processBuilder.directory(new File(project.getLocation().toOSString()));
		processBuilder.redirectErrorStream(true);
		return processBuilder;

	} 

	/**
	 * Remove file name and return directory path.
	 * @param path
	 * @return
	 */
	public  String getDirectoryPath(String path){
		if(path.length() > 0 )
		{
		    int endIndex = path.lastIndexOf("/");
		    if (endIndex != -1)  
		    {
		    	path = path.substring(0, endIndex);
		    	return path;
		    }
		
		}
		return "";
	}
	
	/**
	 * Create directory comma separated string from list.
	 * 
	 * @param fileList
	 * @return
	 */
	private String getCommaSeparatedDirectories(List<String> fileList ){
		List<String> directories = new ArrayList<>();
		for (String externalFile : fileList) {
					externalFile=getDirectoryPath(externalFile);
					directories.add(externalFile);
			}
		
		String files=StringUtils.join(directories, ",");
		return files;
	}
	
	/**
	 * Collect all external external files from active editor, also check for subjob and nested subjob. 
	 * @return list of external files.
	 */
	public List<String> getExternalFilesList() {
		List<String> externalFilesPathList=new ArrayList<>();	
		ELTGraphicalEditor editor = (ELTGraphicalEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		List<String> tranformComponentList = Arrays.asList(Constants.TRANSFORM,Constants.AGGREGATE,Constants.NORMALIZE,Constants.
				CUMULATE,Constants.GROUP_COMBINE);
		String finalParamPath=null;
		if (editor != null && editor instanceof ELTGraphicalEditor) {
			GraphicalViewer graphicalViewer = (GraphicalViewer) ((GraphicalEditor) editor)
					.getAdapter(GraphicalViewer.class);
			for (Iterator<EditPart> ite = graphicalViewer.getEditPartRegistry().values().iterator(); ite.hasNext();) {
				EditPart editPart = ite.next();
				if (editPart instanceof ComponentEditPart) {
					Component component = ((ComponentEditPart) editPart).getCastedModel();
					addExternalTransformFiles(externalFilesPathList, component,tranformComponentList);
					Schema  schema = (Schema) component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
					if(schema!=null && schema.getIsExternal()){
						externalFilesPathList.add(getParamValue(schema.getExternalSchemaPath()));
					}
					if (Constants.SUBJOB_COMPONENT.equals(component.getComponentName())) {
						String subJobPath = (String) component.getProperties().get(Constants.PATH_PROPERTY_NAME);
						checkSubJobForExternalFiles(externalFilesPathList, subJobPath, tranformComponentList);
					}
				}
			}
		}
		return externalFilesPathList;
	}
	
	
	private String getParamValue(String paramName) {
		String finalParamPath = null;
		if (ParameterUtil.containsParameter(paramName, '/')) {
			String paramValue = hydrograph.ui.common.property.util.Utils.INSTANCE.getParamValue(paramName);
			finalParamPath = hydrograph.ui.common.property.util.Utils.INSTANCE.getParamFilePath(paramName, paramValue);
			while (ParameterUtil.containsParameter(finalParamPath, '/')) {
				paramValue = hydrograph.ui.common.property.util.Utils.INSTANCE.getParamValue(paramName);
				finalParamPath = hydrograph.ui.common.property.util.Utils.INSTANCE.getParamFilePath(paramName, paramValue);
			}
		}
		
		if(finalParamPath == null)
			finalParamPath = paramName;
		return finalParamPath;
	}

	/**
	 * @param externalFilePathList
	 * @param component
	 * @param tranformComponentList
	 * 
	 * Adds the external files used in transform components
	 */
	private void addExternalTransformFiles(List<String> externalFilePathList, Component component, List<String> tranformComponentList) {
		if (tranformComponentList.stream().anyMatch(component.getComponentName()::equalsIgnoreCase)) {
			TransformMapping transformMapping = (TransformMapping) component.getProperties().get(Constants.OPERATION);
			ExternalWidgetData externaWidgetData = transformMapping.getExternalOutputFieldsData();
			if (externaWidgetData.isExternal()) {
				externalFilePathList.add(externaWidgetData.getFilePath());
			}
			transformMapping.getMappingSheetRows().stream().filter(e->e.isActive()).forEach(mappingSheetRow -> {
				if(mappingSheetRow.isExpression()){
					if(mappingSheetRow.getExternalExpresion().isExternal()){
						externalFilePathList.add(getParamValue(mappingSheetRow.getExternalExpresion().getFilePath()));
					}
				}else{
					if(mappingSheetRow.getExternalOperation().isExternal()){
						externalFilePathList.add(getParamValue(mappingSheetRow.getExternalOperation().getFilePath()));
					}
				}
			});
		}
	}
	
	/**
	 * Collect all subjob file from active editor, also check for nested subjob. 
	 * @return list of subjob.
	 */
	public List<String> getSubJobList() {
		ArrayList<String> subJobList=new ArrayList<>();
		ELTGraphicalEditor editor = (ELTGraphicalEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor != null && editor instanceof ELTGraphicalEditor) {
			GraphicalViewer graphicalViewer = (GraphicalViewer) ((GraphicalEditor) editor)
					.getAdapter(GraphicalViewer.class);
			for (Iterator<EditPart> ite = graphicalViewer.getEditPartRegistry().values().iterator(); ite.hasNext();) {
				EditPart editPart = ite.next();
				if (editPart instanceof ComponentEditPart) {
					Component component = ((ComponentEditPart) editPart).getCastedModel();
					if(Constants.SUBJOB_COMPONENT.equals(component.getComponentName())){
					  String subJobPath=(String) component.getProperties().get(Constants.PATH_PROPERTY_NAME);
					  subJobList.add(subJobPath);
					  checkNestedSubJob(subJobList, subJobPath);
					}
				}
			}
		}
		return subJobList;
	}

	/**
	 * Check for subjob container and nested subjob.
	 * @param subJobList
	 * @param subJobPath
	 */
	private void checkNestedSubJob(List<String> subJobList,String subJobPath) {
			Object obj=null;
			try {
				obj = CanvasUtils.INSTANCE.fromXMLToObject(new FileInputStream(new File(JobManager.getAbsolutePathFromFile(new Path(subJobPath)))));
			} catch (FileNotFoundException e) {
				logger.error("subjob xml not found "+e);
			}
			if(obj!=null && obj instanceof Container){
			  Container container = (Container) obj;
			  for (Component component : container.getUIComponentList()) {
					if(Constants.SUBJOB_COMPONENT.equals(component.getComponentName())){
						  String subJob=(String) component.getProperties().get(Constants.PATH_PROPERTY_NAME);
						  subJobList.add(subJob);
						  checkNestedSubJob(subJobList, subJob);
					}
	     		}
		  }
	}
	
	/**
	 * Check nested subjob to collect external files.
	 * @param externalFilesPathList
	 * @param subJobPath
	 * @param tranformComponentList 
	 */
	private void checkSubJobForExternalFiles(List<String> externalFilesPathList,String subJobPath, List<String> tranformComponentList) {
		Object obj=null;
		try {
			obj = CanvasUtils.INSTANCE.fromXMLToObject(new FileInputStream(new File(JobManager.getAbsolutePathFromFile(new Path(subJobPath)))));
		} catch (FileNotFoundException e) {
			logger.error("subjob xml not found "+e);
		}
		if(obj!=null && obj instanceof Container){
		  Container container = (Container) obj;
		  for (Component component : container.getUIComponentList()){
			  addExternalTransformFiles(externalFilesPathList, component, tranformComponentList);
			  	Schema  schema = (Schema) component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
				if(schema!=null && schema.getIsExternal()){
					externalFilesPathList.add(schema.getExternalSchemaPath());
				}
				if(Constants.SUBJOB_COMPONENT.equals(component.getComponentName())){
					  String subJob=(String) component.getProperties().get(Constants.PATH_PROPERTY_NAME);
					  checkSubJobForExternalFiles(externalFilesPathList, subJob,tranformComponentList);
				}
		  	}
		}
	}

	public void	killRemoteJobProcess(Job jobToKill){

		/*final Timer timer = new Timer();
		String[] runCommand = new String[3];
		if (OSValidator.isWindows()) {
			String[] command = { Messages.CMD, "/c", GradleCommandConstants.KILL_GRADLE_DAEMON };
			runCommand = command;

		} else if (OSValidator.isMac()) {
			String[] command = { Messages.SHELL, "-c", GradleCommandConstants.KILL_GRADLE_DAEMON };
			runCommand = command;
		}

		final ProcessBuilder processBuilder = new ProcessBuilder(runCommand);
		processBuilder.redirectErrorStream(true);
		jobToKill.setJobStatus(JobStatus.KILLED);

		DefaultGEFCanvas gefCanvas = CanvasUtils.INSTANCE.getComponentCanvas();
		JobLogger joblogger = JobManager.INSTANCE.initJobLogger(gefCanvas);
		JobManager.INSTANCE.releaseResources(jobToKill, gefCanvas, joblogger);
		long killTime = System.currentTimeMillis();
		final long end = killTime + Long.valueOf(Messages.KILLTIME);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if(System.currentTimeMillis() < end) {
					try {
						processBuilder.start();
					} catch (IOException e) {
						logger.info("Fail to run kill process.");
					}
				}else{
					timer.cancel();
				}

			}
		};
		timer.schedule(task, 0l, 1000);*/
		((StopJobHandler) RunStopButtonCommunicator.StopJob.getHandler()).setStopJobEnabled(false);
		MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				SWT.ICON_WARNING | SWT.YES | SWT.NO);
		messageBox.setText(Messages.KILL_JOB_MESSAGEBOX_TITLE);
		messageBox.setMessage(Messages.KILL_JOB_MESSAGE);
		if (messageBox.open() == SWT.YES) {
			jobToKill.setJobStatus(JobStatus.KILLED);
			Session session = null;
			HydrographServerConnection hydrographServerConnection = new HydrographServerConnection();
			try {
				String remoteUrl = TrackingDisplayUtils.INSTANCE.getWebSocketRemoteUrl(jobToKill);
				session = hydrographServerConnection.connectToKillJob(jobToKill.getUniqueJobId(), remoteUrl);
				Thread.sleep(8000);
			} catch (Throwable e1) {
				showMessageBox(e1.getMessage(),"Error", SWT.ERROR);
				logger.error(e1.getMessage());
			} finally {
				closeWebSocketConnection(session);
			}
			DefaultGEFCanvas gefCanvas = CanvasUtils.INSTANCE.getComponentCanvas();
			JobLogger joblogger = JobManager.INSTANCE.initJobLogger(gefCanvas);
			JobManager.INSTANCE.releaseResources(jobToKill, gefCanvas, joblogger);
			TrackingDisplayUtils.INSTANCE.clearTrackingStatus(jobToKill.getUniqueJobId());
		}else{
			((StopJobHandler) RunStopButtonCommunicator.StopJob.getHandler()).setStopJobEnabled(true);
		}
	}
	
	private void showMessageBox(String message,String header,int SWT_Type)
	{
		MessageBox box=new MessageBox(Display.getCurrent().getActiveShell(), SWT_Type);
		box.setMessage(message);
		box.setText(header);
		box.open();
	}
	
	public void	killLocalJobProcess(final Job jobToKill){

		MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				SWT.ICON_WARNING | SWT.YES | SWT.NO);
		messageBox.setText(Messages.KILL_JOB_MESSAGEBOX_TITLE);
		messageBox.setMessage(Messages.KILL_JOB_MESSAGE);
		if (messageBox.open() == SWT.YES) {
			((StopJobHandler) RunStopButtonCommunicator.StopJob.getHandler()).setStopJobEnabled(false);
			jobToKill.setJobStatus(JobStatus.KILLED);

			DefaultGEFCanvas gefCanvas = CanvasUtils.INSTANCE.getComponentCanvas();
			JobLogger joblogger = JobManager.INSTANCE.initJobLogger(gefCanvas);
			JobManager.INSTANCE.releaseResources(jobToKill, gefCanvas, joblogger);
			try {
				Process	process = Runtime.getRuntime().exec(JPS_COMMAND_TO_FIND_JAVA_PROCESES);
				
				InputStream stream = process.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				String line = null;

				while ((line = reader.readLine()) != null) {
					if(line.contains(jobToKill.getUniqueJobId())){
						logger.debug("Trying to kill the job using taskkill for local");
						jobToKill.setJobStatus(JobStatus.KILLED);
						String[] jpsOutput = line.split(" ");
						Process killProcess = null;
						if (OSValidator.isWindows()) {
							killProcess = Runtime.getRuntime().exec(WINDOWS_COMMAND_TO_KILL_PROCESS+ jpsOutput[0]);
						}else if (OSValidator.isMac()){
							killProcess = Runtime.getRuntime().exec(MAC_COMMAND_TO_KILL_PROCESS+jpsOutput[0]);
						}
						if (killProcess != null) {
							InputStream streamKill = killProcess.getInputStream();
							BufferedReader readerKill = new BufferedReader(new InputStreamReader(streamKill));
							String lineKill = null;
							while ((lineKill = readerKill.readLine()) != null) {
								logger.info("Kill log: " + lineKill);
								joblogger.logMessage(lineKill);
								joblogger.logMessage(JOB_KILLED_SUCCESSFULLY);
							}
						}
						break;
					}
				}
			} catch (IOException e) {
				logger.info("Fail to run kill process.");
			}
		}else{
			((StopJobHandler) RunStopButtonCommunicator.StopJob.getHandler()).setStopJobEnabled(true);
		}
		
	}

	/**
	 * Close Websocket connection Connection
	 * @param session
	 */
	private void closeWebSocketConnection(final Session session ){
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
		}

		if (session != null && session.isOpen()) {
			try {
				CloseReason closeReason = new CloseReason(
						CloseCodes.NORMAL_CLOSURE, "Session Closed");
				session.close(closeReason);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
