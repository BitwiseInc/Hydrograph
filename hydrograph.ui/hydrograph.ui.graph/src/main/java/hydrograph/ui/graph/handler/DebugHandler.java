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

package hydrograph.ui.graph.handler;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.dataviewer.utilities.Utils;
import hydrograph.ui.graph.debugconverter.DebugConverter;
import hydrograph.ui.graph.debugconverter.SchemaHelper;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.graph.job.JobManager;
import hydrograph.ui.graph.utility.CanvasUtils;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.runconfig.RunConfigDialog;


/**
 * Handler use to run the job on debug mode.
 * @author Bitwise
 *
 */
public class DebugHandler{
	
	/** Default debug service port */
	private static final String DEFAULT_DEBUG_SERVICE_PORT = "8004";

	/** The logger. */
	private Logger logger = LogFactory.INSTANCE.getLogger(DebugHandler.class);
	
	/** The job map. */
	private static Map<String,Job> jobMap = new HashMap<>();	
	
	/** The current job Ipath. */
	private IPath currentJobIPath=null;
	
	/** The unique job id. */
	private String uniqueJobID =null;
	
	/** The base path. */
	private String basePath = null;
	
	/** The current job name. */
	private String currentJobName = null;
	
	
	
	public DebugHandler() {
		
	}
	
	/**
	 * Checks if job has been run and its entry is present in the map.
	 *
	 * @param key the job name
	 * @return boolean
	 */
	public static boolean hasJob(String key){
		return jobMap.containsKey(key);
	}
	
	/**
	 * Gets the job.
	 *
	 * @param jobName the job name
	 * @return the job
	 */
	public static Job getJob(String jobName) {
		return jobMap.get(jobName);
	}
	
	 
	/**
	 * Adds the debug job.
	 *
	 * @param jobId the job id
	 * @param debugJob the debug job
	 */
	public void addDebugJob(String jobId, Job debugJob){
		jobMap.put(jobId, debugJob);
		if(RunJobHandler.hasJob(jobId)){
			RunJobHandler.removeJob(jobId);
		}
	}
	
	/**
	 * Removes the job.
	 *
	 * @param key the job name
	 * @return the job
	 */
	public static void removeJob(String jobId) {
		jobMap.remove(jobId);
		
	}
	
	/**
	 * Gets the component canvas.
	 *
	 * @return the component canvas
	 */
	private DefaultGEFCanvas getComponentCanvas() {		
		if(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof DefaultGEFCanvas)
			return (DefaultGEFCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		else
			return null;
	}
	
	/**
	 * Checks if is dirty editor.
	 *
	 * @return true, if is dirty editor
	 */
	private boolean isDirtyEditor(){
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().isDirty();
	}

	/**
	 * Creates the debug xml.
	 *
	 * @throws Exception the exception
	 */
	private void createDebugXml() throws Exception{
		String currentJobPath=null;
		ELTGraphicalEditor eltGraphicalEditor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(!(eltGraphicalEditor.getEditorInput() instanceof GraphicalEditor)){
			currentJobIPath=new Path(eltGraphicalEditor.getTitleToolTip());
		}
		
		DebugConverter converter = new DebugConverter();
			
		try {
			
			uniqueJobID= eltGraphicalEditor.getUniqueJobId();
			currentJobPath = currentJobIPath.lastSegment().replace(Constants.JOB_EXTENSION, Constants.DEBUG_EXTENSION);
			currentJobName = currentJobIPath.lastSegment().replace(Constants.JOB_EXTENSION, "");
			currentJobIPath = currentJobIPath.removeLastSegments(1).append(currentJobPath);
			
			converter.marshall(converter.getParam(), ResourcesPlugin.getWorkspace().getRoot().getFile(currentJobIPath));
		} catch (JAXBException | IOException  | CoreException exception) {
			logger.error(exception.getMessage(), exception);
		} 
	}
	
	/*
	 * execute method launch the job in debug mode.
	 */
	public Object execute(RunConfigDialog runConfigDialog){		
		if(getComponentCanvas().getParameterFile() == null || isDirtyEditor()){
			try{
				//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().doSave(null);
				JobManager.INSTANCE.enableRunJob(true);
				if(getComponentCanvas().getParameterFile() == null || isDirtyEditor()){
					CanvasUtils.INSTANCE.getComponentCanvas().restoreMenuToolContextItemsState();					
					return null;
				}
			}catch(Exception e){
				logger.debug("Unable to save graph ", e);
				CanvasUtils.INSTANCE.getComponentCanvas().restoreMenuToolContextItemsState();
					JobManager.INSTANCE.enableRunJob(true);
			}
		}
		
		try {
			createDebugXml();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		String clusterPassword = runConfigDialog.getClusterPassword()!=null ? runConfigDialog.getClusterPassword():"";
		basePath = runConfigDialog.getBasePath();
		String host = runConfigDialog.getHost();
		String userId = runConfigDialog.getUserId();
		if(!runConfigDialog.proceedToRunGraph()){
			JobManager.INSTANCE.enableRunJob(true);
			CanvasUtils.INSTANCE.getComponentCanvas().restoreMenuToolContextItemsState();			
			return null;
		}
		
		String consoleName= getComponentCanvas().getActiveProject() + "." + getComponentCanvas().getJobName();
		String canvasName = consoleName;
		String localJobID = consoleName;

		Job job = new Job(localJobID, consoleName, canvasName, basePath, host, userId, clusterPassword); 
		job.setBasePath(basePath);
		job.setIpAddress(host);
		job.setUserId(userId);
		job.setHost(runConfigDialog.getHost());
		job.setUsername(runConfigDialog.getUsername());
		job.setRemoteMode(runConfigDialog.isRemoteMode());
		job.setPassword(clusterPassword);
		job.setUniqueJobId(uniqueJobID);
		job.setKeyFile(runConfigDialog.getKeyFile());
		job.setUsePassword(runConfigDialog.getIsUsePassword());
		
		IFile file=ResourcesPlugin.getWorkspace().getRoot().getFile(currentJobIPath);
		job.setDebugFilePath(file.getFullPath().toString());
		
		String portNumber = Utils.INSTANCE.getServicePortNo();
		
		if(!StringUtils.isEmpty(portNumber)){
			job.setPortNumber(portNumber);
		}else{
			job.setPortNumber(DEFAULT_DEBUG_SERVICE_PORT);
		}
		job.setDebugMode(true);
		job.setPassword(clusterPassword);
		job.setRemoteMode(runConfigDialog.isRemoteMode());
		 
		addDebugJob(localJobID, job);
		
		JobManager.INSTANCE.executeJobInDebug(job, runConfigDialog.isRemoteMode(), runConfigDialog.getUsername());
		CanvasUtils.INSTANCE.getComponentCanvas().restoreMenuToolContextItemsState();	
		
		exportSchemaFile();
		
		return null;
	}
	
	private void exportSchemaFile(){
		String validPath = null;
		String filePath = null;
		ELTGraphicalEditor editor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		String jobId = editor.getJobId();
		String path = Utils.INSTANCE.getDataViewerDebugFilePath();
		if(StringUtils.isNotBlank(path)){
			logger.debug("validating file path : {}", path);
			validPath = SchemaHelper.INSTANCE.validatePath(path);
			
			createDirectoryIfNotExist(validPath);
			
			filePath = validPath + jobId;
				try {
					SchemaHelper.INSTANCE.exportSchemaFile(filePath);
				} catch (CoreException exception) {
					logger.error("Failed to create file", exception);
				}
		}else{
			logger.debug("File path does not exist : {}", path);
		}
	}


	private void createDirectoryIfNotExist(String validPath) {
		File directory = new File(validPath);
		if(!directory.exists()){
			directory.mkdirs();
		}
	}
	
	/**
	 * Gets the job id.
	 *
	 * @return the job id
	 */
	public String getJobId() {
		return uniqueJobID;
	}

	/**
	 * Gets the base path.
	 *
	 * @return the base path
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * Gets the job map.
	 *
	 * @return the job map
	 */
	public static Map<String, Job> getJobMap() {
		return jobMap;
	}

}
