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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.editor.ELTGraphicalEditorInput;
import hydrograph.ui.logging.factory.LogFactory;
/**
 * This WizardPage can create an empty .job file for the GraphicalEditor.
 * @author Bitwise
 */
public class JobCreationPage extends WizardNewFileCreationPage {
	private static final String JOBS_FOLDER_NAME = "/jobs";
	private Logger logger = LogFactory.INSTANCE.getLogger(JobCreationPage.class);
	private static int jobCounter = 1;
	private static final String DEFAULT_EXTENSION = ".job";
	private final IWorkbench workbench;
    private static final String ERROR="Error";
    private boolean isPageCreatedForSavingSubJob;
	private IFile newFile;
    
    
	/**
	 * Create a new wizard page instance.
	 * 
	 * @param workbench
	 *            the current workbench
	 * @param selection
	 *            the current object selection
	 * @see JobCreationWizard#init(IWorkbench, IStructuredSelection)
	 */
	JobCreationPage(IWorkbench workbench, IStructuredSelection selection,boolean isPageCreatedForSavingSubJob) {
		super("jobCreationPage1", selection);
		this.workbench = workbench;
		setTitle(Messages.JOB_WIZARD_TITLE);
		setDescription(Messages.CREATE_NEW + DEFAULT_EXTENSION + " " + Messages.FILE);
		this.isPageCreatedForSavingSubJob=isPageCreatedForSavingSubJob;
	}

	/*
	 * 
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createControl(org .eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		setFileName(Messages.JOB + jobCounter++ + DEFAULT_EXTENSION);
		setPageComplete(validatePage());
	}

	/**
	 * This method will be invoked, when the "Finish" button is pressed.
	 * 
	 * @see JobCreationWizard#performFinish()
	 */
	boolean finish() {
		String[] fileName=this.getFileName().split(Constants.SEPERATOR);
		String propertyFile=fileName[0]+Constants.PROPERTIES_EXTENSION;
		IPath propertyFilePath = new Path(Constants.PARAM+propertyFile);
		IProject iProject=ResourcesPlugin.getWorkspace().getRoot().getProject(this.getContainerFullPath().segment(0));
		IFile proertyIFile=iProject.getFile(propertyFilePath);
		if (fileName[0].length() > 50) {
			return showErrorMessage(Messages.FILE_NAME_ERROR);
		}
		else if (proertyIFile.exists()) {
			return showErrorMessage(Messages.PROPERTY_FILE_ERROR);
		}
		else {
    	   if(!getFileName().endsWith(DEFAULT_EXTENSION)){
    		   this.setFileName(this.getFileName().concat(DEFAULT_EXTENSION));
    	   }
			IPath filePath = new Path(this.getContainerFullPath() + "/" + this.getFileName());
			newFile = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
			if(isPageCreatedForSavingSubJob){
				return true;
			}
			if (newFile.getFullPath().toOSString().contains(" ")) {
				MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
				messageBox.setText("Error");
				messageBox.setMessage("The Job Name has spaces");
				if (messageBox.open() == SWT.OK)
					return false;
			}
			// open newly created job in the editor
			IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
			ELTGraphicalEditorInput input = new ELTGraphicalEditorInput(getFileName());
			if (page != null) {
				try {
					openJobInEditor(page, input);
				} catch (PartInitException e) {
					logger.error("Error while opening job", e);
					return false;
				}
			}
			createFilesOfJob();
		}
		return true;

	}

	private void openJobInEditor(IWorkbenchPage page, ELTGraphicalEditorInput input) throws PartInitException {
		page.openEditor(input, ELTGraphicalEditor.ID, true);
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
		if (activeWindow != null) {
			final IWorkbenchPage activePage = activeWindow.getActivePage();
			if (activePage != null) {
				activePage.activate(activePage.findEditor(input));
			}
		}
		
	}

	private boolean showErrorMessage(String errorMessage) {
		MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
		messageBox.setText(ERROR);
		messageBox.setMessage(errorMessage);
		if (messageBox.open() == SWT.OK) {
			this.getControl().getShell().open();
		}
		return false;
	}

	private void createFilesOfJob() {
		ELTGraphicalEditor editor = (ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();
		IPath filePath = new Path(this.getContainerFullPath() + "/" + this.getFileName());
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
		editor.saveJob(file,false);
		editor.saveParameters();
		
	}

	/*
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
	 */
	protected InputStream getInitialContents() {
		ByteArrayInputStream bais = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.flush();
			oos.close();
			bais = new ByteArrayInputStream(baos.toByteArray());
		} catch (IOException e) {
			logger.error("Error while job wizard creation", e);
		}
		return bais;
	}

	/**
	 * Return true, if the file name entered in this page is valid.
	 */
	private boolean validateFilename() {
		if (getFileName() != null) {
			return true;
		}
			return false;
	}

	/*
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validatePage()
	 */
	protected boolean validatePage() {
		boolean returnCode=  super.validatePage() && validateFilename();
		if(returnCode){
			IPath iPath=new Path(getContainerFullPath()+JOBS_FOLDER_NAME);
			IFolder folder=ResourcesPlugin.getWorkspace().getRoot().getFolder(iPath);
			if(!StringUtils.endsWithIgnoreCase(getFileName(), Constants.JOB_EXTENSION)){
				IFile newFile= folder.getFile(getFileName()+Constants.JOB_EXTENSION);
				if(newFile.exists()){
					setErrorMessage("'"+newFile.getName()+"'"+Constants.ALREADY_EXISTS);
					return false;
				}
			}
		}
		return returnCode;
		
	}
	
	public IFile getNewFile() {
		return newFile;
	}
}