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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;
import hydrograph.ui.dataviewer.constants.MessageBoxText;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.execution.tracking.datastructure.ExecutionStatus;
import hydrograph.ui.graph.execution.tracking.replay.ViewExecutionHistoryComponentDialog;
import hydrograph.ui.graph.execution.tracking.replay.ViewExecutionHistoryDataDialog;
import hydrograph.ui.graph.execution.tracking.replay.ViewExecutionHistoryUtility;
import hydrograph.ui.graph.execution.tracking.utils.TrackingStatusUpdateUtils;
import hydrograph.ui.graph.job.Job;
import hydrograph.ui.graph.job.JobStatus;
import hydrograph.ui.graph.utility.MessageBox;
import hydrograph.ui.logging.factory.LogFactory;
/**
 * 
 * Open tracking dialog window where user can view previous execution tracking view history.
 * @author Bitwise
 *
 */
public class ViewExecutionHistoryHandler extends AbstractHandler{

	/** The logger. */
	private static Logger logger = LogFactory.INSTANCE.getLogger(ViewExecutionHistoryHandler.class);
	
	
	
	private List<String> compNameList = new ArrayList<>();
	private List<String> missedCompList = new ArrayList<>();;

	
	/**
	 * Show view execution history for selected jobId.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String consoleName = getComponentCanvas().getActiveProject() + "." + getComponentCanvas().getJobName();
		
		Map<String, List<Job>> jobDetails1 = ViewExecutionHistoryUtility.INSTANCE.getTrackingJobs();
		List<Job> tmpList = jobDetails1.get(consoleName);
		
		if(tmpList==null){
			MessageBox.INSTANCE.showMessage(MessageBoxText.INFO, Messages.RUN_THE_JOB);
			return "";
		}
		logger.debug("Call to Execution history Dialog");
		ViewExecutionHistoryDataDialog dialog = new ViewExecutionHistoryDataDialog(Display.getDefault().getActiveShell(), this, tmpList);
		dialog.open();
		
		if(!(missedCompList.size() > 0) && !(compNameList.size() > 0)){
			return true;
		}
		ViewExecutionHistoryComponentDialog componentDialog = new ViewExecutionHistoryComponentDialog(Display.getDefault().getActiveShell(), compNameList, missedCompList);
		componentDialog.open();
		
		
		compNameList.clear();
		missedCompList.clear();
		
		return null;
	}
	
	/**
	 * Apply status and count on editor according to jobid.
	 * @param executionStatus
	 * @return boolean (the status if replay was successful(true) or not(false))
	 */
	public boolean replayExecutionTracking(ExecutionStatus executionStatus){
		ViewExecutionHistoryUtility.INSTANCE.addTrackingStatus(executionStatus.getJobId(), executionStatus);
		ViewExecutionHistoryUtility.INSTANCE.getUnusedCompsOnCanvas().clear();
		
		ELTGraphicalEditor eltGraphicalEditor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();
		
		if(!(eltGraphicalEditor.getEditorInput() instanceof GraphicalEditor)){
			
			String currentJobName = eltGraphicalEditor.getActiveProject() + "." + eltGraphicalEditor.getJobName();
			Job job = eltGraphicalEditor.getJobInstance(currentJobName);
			if(job != null){
				job.setJobStatus(JobStatus.SUCCESS);
			}
			
			if(!executionStatus.getJobId().startsWith(eltGraphicalEditor.getContainer().getUniqueJobId())){
				ViewExecutionHistoryUtility.INSTANCE.getMessageDialog(Messages.INVALID_LOG_FILE +" "+eltGraphicalEditor.getContainer().getUniqueJobId());
				return false;
			}else{
				TrackingStatusUpdateUtils.INSTANCE.updateEditorWithCompStatus(executionStatus, eltGraphicalEditor, true);
				compNameList = new ArrayList<>();
				missedCompList = ViewExecutionHistoryUtility.INSTANCE.getMissedComponents(executionStatus);
				
				ViewExecutionHistoryUtility.INSTANCE.getExtraComponentList(executionStatus);
				ViewExecutionHistoryUtility.INSTANCE.getUnusedCompsOnCanvas().forEach((compId, compName)->{
					compNameList.add(compId);
				});
				
				return true;
			} 
		}
		return false;
		
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

	
}
