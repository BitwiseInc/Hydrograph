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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.PlatformUI;

import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.graph.job.JobManager;
import hydrograph.ui.graph.utility.CanvasUtils;
import hydrograph.ui.propertywindow.runconfig.RunConfigDialog;

/**
 * Handler use to run the job using gradle command.
 * 
 * @author Bitwise
 * @version 1.0
 * @since 2015-10-27
 */
public class RunJobHandler{
	
	private static Map<String,Job> jobMap = new HashMap<>();

	private Job generateJob(String localJobID, String consoleName, String canvasName) {
		Job job = new Job(localJobID, consoleName, canvasName, null, null, null, null);
		jobMap.put(localJobID, job);
		if(DebugHandler.hasJob(localJobID)){
			DebugHandler.removeJob(localJobID);
		}
		return job;
	}

	private DefaultGEFCanvas getComponentCanvas() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof DefaultGEFCanvas)
			return (DefaultGEFCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		else
			return null;
	}
	
	/*
	 * 
	 * Execute command to run the job.
	 */ 
	public Object execute(RunConfigDialog runConfigDialog) {
				
		//((ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()).getViewer().deselectAll();
		String consoleName = getComponentCanvas().getActiveProject() + "." + getComponentCanvas().getJobName();
		String canvasName = consoleName;
		String localJobID = consoleName;

		Job job = generateJob(localJobID, consoleName, canvasName);
		job.setUsePassword(runConfigDialog.getIsUsePassword());
		job.setKeyFile(runConfigDialog.getKeyFile());
		JobManager.INSTANCE.executeJob(job, null,runConfigDialog);
		
		CanvasUtils.INSTANCE.getComponentCanvas().restoreMenuToolContextItemsState();		
		return null;
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
	 * @param key the job name
	 * @return the job
	 */
	public static Job getJob(String key){
		return jobMap.get(key);
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

}
