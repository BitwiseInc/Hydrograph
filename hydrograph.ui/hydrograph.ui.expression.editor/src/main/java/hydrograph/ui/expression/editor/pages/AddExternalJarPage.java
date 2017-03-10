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
package hydrograph.ui.expression.editor.pages;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.PathConstant;
import hydrograph.ui.expression.editor.composites.CategoriesDialogSourceComposite;
import hydrograph.ui.expression.editor.composites.CategoriesDialogTargetComposite;
import hydrograph.ui.expression.editor.dialogs.AddCategoreisDialog;
import hydrograph.ui.expression.editor.jar.util.BuildExpressionEditorDataSturcture;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;
import hydrograph.ui.logging.factory.LogFactory;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.slf4j.Logger;

public class AddExternalJarPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	private static final String DIALOG_TITLE = "Configuration";
	private CategoriesDialogTargetComposite categoriesDialogTargetComposite;
	private CategoriesDialogSourceComposite categoriesDialogSourceComposite;
	private Logger LOGGER = LogFactory.INSTANCE.getLogger(AddExternalJarPage.class);
	private AddCategoreisDialog addCategoreisDialog;
	
	public AddExternalJarPage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(Composite parent) {
		ResourcesPlugin.getWorkspace().getRoot().getProject(ExpressionEditorUtil.INSTANCE.lastString(getTitle(), Constants.SPACE));
		noDefaultAndApplyButton();
		Composite container = parent;
		container.setLayout(new GridLayout(1, false));
		this.getShell().setText(DIALOG_TITLE);
		Composite mainComposite = new Composite(container, SWT.BORDER);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		SashForm sashForm = new SashForm(mainComposite, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		categoriesDialogSourceComposite=new CategoriesDialogSourceComposite(sashForm, this, SWT.NONE);
		categoriesDialogTargetComposite=new CategoriesDialogTargetComposite(sashForm, categoriesDialogSourceComposite, SWT.NONE);
		
		sashForm.setWeights(new int[] {1, 1});

		return container;
	}

	public boolean createPropertyFileForSavingData()  {
		IProject iProject=BuildExpressionEditorDataSturcture.INSTANCE.getCurrentProject();
		IFolder iFolder=iProject.getFolder(PathConstant.PROJECT_RESOURCES_FOLDER);
		Properties properties=new Properties();
		FileOutputStream file=null;
		boolean isFileCreated=false;
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
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE,new NullProgressMonitor());
			isFileCreated= true;
		} catch (IOException | CoreException exception) {
			LOGGER.error("Exception occurred while saving jar file path at projects setting folder",exception);
		}
		finally{
			if(file!=null)
				try {
					file.close();
				} catch (IOException e) {
					LOGGER.warn("IOException occurred while closing output-stream of file",e);
				}
		}
		return isFileCreated;
	}
	
	@Override
	public boolean performOk() {
		createPropertyFileForSavingData();
		return super.performOk();
	}
	
}
