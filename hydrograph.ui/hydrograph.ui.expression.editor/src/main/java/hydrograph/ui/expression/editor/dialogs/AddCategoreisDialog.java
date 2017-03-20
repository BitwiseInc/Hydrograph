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

package hydrograph.ui.expression.editor.dialogs;

import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.PathConstant;
import hydrograph.ui.expression.editor.composites.CategoriesDialogSourceComposite;
import hydrograph.ui.expression.editor.composites.CategoriesDialogTargetComposite;
import hydrograph.ui.expression.editor.jar.util.BuildExpressionEditorDataSturcture;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;

public class AddCategoreisDialog extends Dialog {
	private static final String DIALOG_TITLE = "Configuration";
	private CategoriesDialogTargetComposite categoriesDialogTargetComposite;
	private CategoriesDialogSourceComposite categoriesDialogSourceComposite;
	private Logger LOGGER = LogFactory.INSTANCE.getLogger(AddCategoreisDialog.class);
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AddCategoreisDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.MAX|SWT.RESIZE|SWT.APPLICATION_MODAL);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		this.getShell().setText(DIALOG_TITLE);
		Composite mainComposite = new Composite(container, SWT.BORDER);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		SashForm sashForm = new SashForm(mainComposite, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		categoriesDialogSourceComposite=new CategoriesDialogSourceComposite(sashForm,null, SWT.NONE);
		categoriesDialogTargetComposite=new CategoriesDialogTargetComposite(sashForm,categoriesDialogSourceComposite, SWT.NONE);
		
		sashForm.setWeights(new int[] {1, 1});

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(600, 500);
	}
	
	@Override
	protected void okPressed() {
		if(createPropertyFileForSavingData()){
//			BuildExpressionEditorDataSturcture.INSTANCE.refreshRepo();
		}
		super.okPressed();
	}
	
	
	public boolean createPropertyFileForSavingData()  {
		IProject iProject=BuildExpressionEditorDataSturcture.INSTANCE.getCurrentProject();
		IFolder iFolder=iProject.getFolder(PathConstant.PROJECT_RESOURCES_FOLDER);
		FileOutputStream file = null;
		Properties properties=new Properties();
		try {
			if(!iFolder.exists()){
				iFolder.create(true, true, new NullProgressMonitor());
			}
			for(String items:categoriesDialogTargetComposite.getTargetList().getItems()){
				String jarFileName=StringUtils.trim(StringUtils.substringAfter(items, Constants.DASH));
				String packageName=StringUtils.trim(StringUtils.substringBefore(items, Constants.DASH));
				properties.setProperty(packageName,jarFileName );
			}
			
			file=new FileOutputStream(iFolder.getLocation().toString()+File.separator+PathConstant.EXPRESSION_EDITOR_EXTERNAL_JARS_PROPERTIES_FILES);
			properties.store(file, "");
			return true;
		} catch (IOException | CoreException exception) {
			LOGGER.error("Exception occurred while saving jar file path at projects setting folder",exception);
		}
		finally{
		      if (file != null) {
		            try {
		            	file.close();
		            } catch (IOException e) {
		            	LOGGER.warn("Exception in closing output stream. The error message is " + e.getMessage());
		            }
		        }
		}
		return false;
	}

	public static void main(String[] args) {
		AddCategoreisDialog dialog=new AddCategoreisDialog(new Shell());
		dialog.setShellStyle(SWT.MAX|SWT.MIN|SWT.CLOSE);
		dialog.open();
	}
}
