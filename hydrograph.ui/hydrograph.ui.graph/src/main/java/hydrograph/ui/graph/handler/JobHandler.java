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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.WorkbenchWidgetsUtils;
import hydrograph.ui.dataviewer.utilities.Utils;
import hydrograph.ui.graph.Activator;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.dialog.SaveJobFileBeforeRunDialog;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.execution.tracking.preferences.JobRunPreference;
import hydrograph.ui.graph.job.RunStopButtonCommunicator;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.utility.SubJobUtility;
import hydrograph.ui.graph.utility.ViewDataUtils;
import hydrograph.ui.propertywindow.runconfig.RunConfigDialog;
/**
 * JobHandler Handles both debug and normal run.
 *
 * @author Bitwise
 */
public class JobHandler extends AbstractHandler {

	/**
	 * Instantiates a new job handler.
	 */
	public JobHandler() {
		RunStopButtonCommunicator.RunJob.setHandler(this);
	} 
	
	/**
	 * Enable disable run button.
	 *
	 * @param enable the new run job enabled
	 */
	public void setRunJobEnabled(boolean enable) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				setBaseEnabled(enable);
				ToolItem item = WorkbenchWidgetsUtils.INSTANCE.getToolItemFromToolBarManger(
						Constants.RUN_STOP_BUTTON_TOOLBAR_ID, Constants.RUN_BUITTON_TOOLITEM_ID);
				if (item != null) {
					item.setEnabled(enable);
				}
			}
		});
	}

	/**
	 * Enable disable debug button.
	 *
	 * @param enable the new debug job enabled
	 */
	public void setDebugJobEnabled(boolean enable){
		setRunJobEnabled(enable);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(jobIsSaved() && !isDummyComponentAvailable()){
				if(validateGraphProperties()){
				if(confirmationFromUser()){
					executeJob();
				}
				}
				else {
					executeJob();
				}
		}
		return null;
	
	}
	
	private boolean isDummyComponentAvailable() {
		ELTGraphicalEditor editor = SubJobUtility.getCurrentEditor();
		Container container=editor.getContainer();
		for(Component component:container.getUIComponentList()){
			if(StringUtils.equals(component.getComponentName(), "UnknownComponent")){
				MessageBox messageBox=new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_WARNING);
				messageBox.setMessage("Cannot run job with Dummy Component");
				messageBox.setText("Warning");
				messageBox.open();
				return true;
			}
		}
		return false;
	}

	private boolean jobIsSaved(){
		ELTGraphicalEditor editor = SubJobUtility.getCurrentEditor();
		if(editor.isDirty())
		{
			if(!StringUtils.equals(Activator.getDefault().getPreferenceStore().getString(JobRunPreference.SAVE_JOB_BEFORE_RUN_PREFRENCE), MessageDialogWithToggle.ALWAYS)){
			SaveJobFileBeforeRunDialog messageBox = new SaveJobFileBeforeRunDialog(Display.getCurrent().getActiveShell(),"'"+editor.getEditorInput().getName()+"' "+Messages.CONFIRM_TO_SAVE_JOB_BEFORE_RUN );
		    int rc = messageBox.open();
		    if(rc!=IDialogConstants.OK_ID){
		    	return false;
		    	}
		    }
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().doSave(null);	    
		}
		return true;
	}
	
	
	public void executeJob()
	{
		RunConfigDialog runConfigDialog = getRunConfiguration();
		ViewDataUtils dataUtils = ViewDataUtils.getInstance();
		
		if(!runConfigDialog.getHost().equalsIgnoreCase("localhost")){
			Utils.INSTANCE.setHostValue(runConfigDialog.getHost());
		}
		
		String uniqueJobId = getUniqueJobId();
		String timeStamp = dataUtils.getTimeStamp();
		uniqueJobId = uniqueJobId + "_" + timeStamp;
		setUniqueJobId(uniqueJobId);
		
		if(runConfigDialog.isDebug()){
			new DebugHandler().execute(runConfigDialog);
		}
		else{
			new RunJobHandler().execute(runConfigDialog);
		}
	}

	/**
	 * Gets the run configuration.
	 *
	 * @return the run configuration
	 */
	private RunConfigDialog getRunConfiguration() {
		RunConfigDialog runConfigDialog = new RunConfigDialog(Display.getDefault().getActiveShell());
		runConfigDialog.open();
		return runConfigDialog;
	}
	
	private boolean confirmationFromUser() {
		
		/*MessageDialog messageDialog = new MessageDialog(Display.getCurrent().getActiveShell(),Messages.CONFIRM_FOR_GRAPH_PROPS_RUN_JOB_TITLE, null,
				Messages.CONFIRM_FOR_GRAPH_PROPS_RUN_JOB, MessageDialog.QUESTION, new String[] { "Yes",
		  "No" }, 0);*/
		
		MessageBox dialog = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		dialog.setText(Messages.CONFIRM_FOR_GRAPH_PROPS_RUN_JOB_TITLE);
		dialog.setMessage(Messages.CONFIRM_FOR_GRAPH_PROPS_RUN_JOB);
		
		int response = dialog.open();
		 if(response == 0){
	        	return true;
	        } else {
	        	return false;
	        }
	}

	private boolean validateGraphProperties() {
		Map<String, String> graphPropertiesMap = null;
		boolean retValue = false;
		ELTGraphicalEditor editor = (ELTGraphicalEditor) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
	
		if (null != editor) {
	
			graphPropertiesMap = (Map<String, String>) editor.getContainer()
					.getGraphRuntimeProperties();
	
			for (String key : graphPropertiesMap.keySet()) {
	
				if (StringUtils.isBlank(graphPropertiesMap.get(key))) {
	
					retValue = true;
	
					break;
				}
	
			}
	
		}
	
		return retValue;
	}

	private String getUniqueJobId(){
		String jobId = "";
		ELTGraphicalEditor eltGraphicalEditor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(!(eltGraphicalEditor.getEditorInput() instanceof GraphicalEditor)){
			jobId = eltGraphicalEditor.getContainer().getUniqueJobId();
			return jobId;
		}
		return jobId;
	}
	
	private void setUniqueJobId(String uniqueJobId){
		ELTGraphicalEditor eltGraphicalEditor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(!(eltGraphicalEditor.getEditorInput() instanceof GraphicalEditor)){
				eltGraphicalEditor.setUniqueJobId(uniqueJobId);
		}
	}
	
}
