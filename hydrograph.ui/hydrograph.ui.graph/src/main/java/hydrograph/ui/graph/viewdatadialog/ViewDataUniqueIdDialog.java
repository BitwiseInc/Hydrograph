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

package hydrograph.ui.graph.viewdatadialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import hydrograph.ui.common.datastructures.dataviewer.JobDetails;


/**
 * The class ViewDataUniqueIdDialog is used to show view data history
 * @author Bitwise
 *
 */
public class ViewDataUniqueIdDialog extends Dialog{
	
	private List<JobDetails> jobDetails;
	private String selectedUniqueJobId;
	private String[] titles = {"Job Id", "Time Stamp", "Execution Mode", "Job Status"};
	private Table table;
	private boolean okPressed;
	
	public ViewDataUniqueIdDialog(Shell parentShell, List<JobDetails> jobDetails) {
		super(parentShell);
		this.jobDetails = jobDetails;
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("ViewData Execution History");
		container.setLayout(new GridLayout(1, false));
		
		
		Composite composite1 = new Composite(container, SWT.BORDER);
		GridData gd_scrolledComposite1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_scrolledComposite1.heightHint = 236;
		gd_scrolledComposite1.widthHint = 844;
		composite1.setLayoutData(gd_scrolledComposite1);
		
		table = new Table(composite1, SWT.BORDER | SWT.Selection | SWT.FULL_SELECTION );
		table.setBounds(0, 0, 842, 234);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
	    for (int i = 0; i < titles.length; i++) {
	      TableColumn column = new TableColumn(table, SWT.NONE);
	      column.setWidth(212);
	      column.setText(titles[i]);
	    }
		
	    jobDetails.sort((job1, job2)-> job2.getUniqueJobID().compareTo(job1.getUniqueJobID()));
	    
	    for(JobDetails job : jobDetails){
    		String timeStamp = getTimeStamp(job.getUniqueJobID());
    		TableItem items = new TableItem(table, SWT.None);
    		items.setText(0, job.getUniqueJobID());
    		items.setText(1, timeStamp);
    		String mode = getJobExecutionMode(job.isRemote());
    		items.setText(2, mode);
    		items.setText(3, job.getJobStatus());
	    }
	    
	    table.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TableItem[] item = table.getSelection();
				 for (int i = 0; i < item.length; i++){
					 TableItem selectedItem = item[i];
					 selectedUniqueJobId = selectedItem.getText();
			      }
			}
		});
		return super.createDialogArea(parent);
	}
	
	
	private String getJobExecutionMode(boolean executionMode){
		String runningMode = "";
		if(executionMode){
			runningMode = "Remote";
		}else{
			runningMode = "Local";
		}
		return runningMode;
	}
	
	/**
	 * The function will return selected unique job id
	 *@return String
	 */
	public String getSelectedUniqueJobId(){
		return selectedUniqueJobId;
	}
	
	@Override
	protected void okPressed() {
		if(selectedUniqueJobId == null){
			selectedUniqueJobId = jobDetails.get(0).getUniqueJobID();
		}
		okPressed = true;
		super.okPressed();
	}
	
	@Override
	protected void cancelPressed() {
		selectedUniqueJobId = "";
		super.cancelPressed();
	}
	
	@Override
	public boolean close(){
		if(!okPressed)
		selectedUniqueJobId = "";
		return super.close();
	}
	
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
}
