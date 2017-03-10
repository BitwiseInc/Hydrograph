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

 
package hydrograph.ui.project.structure.wizard;

import hydrograph.ui.project.structure.CustomMessages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;


/**
 * The Class CustomWizard.
 * 
 * @author Bitwise
 */
public class CustomWizard extends Wizard implements INewWizard, IExecutableExtension  {
	
	private WizardNewProjectCreationPage pageOne;
	private IConfigurationElement configurationElement;

	/**
	 * Instantiates a new custom wizard.
	 */
	public CustomWizard() {
		setWindowTitle(CustomMessages.CustomWizard_WINDOW_TITLE);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {
		IProject project = ProjectStructureCreator.INSTANCE.createProject(pageOne.getProjectName(), pageOne.getProjectLocationURI());
	    BasicNewProjectResourceWizard.updatePerspective(configurationElement);
	    return project!=null ? true : false;
	}

	@Override
	public void addPages() {
		super.addPages();
		pageOne = new WizardNewProjectCreationPage();
	    pageOne.setTitle(CustomMessages.CustomWizard_CREATE_ETL_PROJECT);
	    pageOne.setDescription(CustomMessages.CustomWizard_PROJECT_DESCRIPTION);
	    addPage(pageOne);
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		configurationElement = config;
	}
}
