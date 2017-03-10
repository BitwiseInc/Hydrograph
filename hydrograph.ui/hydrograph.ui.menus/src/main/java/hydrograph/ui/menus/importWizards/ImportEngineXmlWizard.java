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

 
package hydrograph.ui.menus.importWizards;

import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.menus.messages.Messages;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.slf4j.Logger;


/**
 * The Class ImportEngineXmlWizard.
 * <p>
 * This will open a wizard window for importing Engine (target) XML into Hydrograph so that user can view the respctive
 * components on job canvas.
 * 
 * @author Bitwise
 */
public class ImportEngineXmlWizard extends Wizard implements IImportWizard {
	private static final Logger lOGGEER = LogFactory.INSTANCE.getLogger(ImportEngineXmlWizard.class);
	private ImportEngineXmlWizardPage mainPage;

	/**
	 * Instantiates a new import engine xml wizard.
	 */
	public ImportEngineXmlWizard() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		lOGGEER.debug("Import target XML : Creating files before finish");
		IFile file = mainPage.createNewFile();
        if (file == null)
            return false;
        return true;
	}
	 
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		lOGGEER.debug("Initializing Import Engine Wizard");
		setWindowTitle(Messages.IMPORT_WINDOW_TITLE_TEXT); 
		setNeedsProgressMonitor(true);
		mainPage = new ImportEngineXmlWizardPage(Messages.IMPORT_WINDOW_TITLE_TEXT,selection);
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
    	lOGGEER.debug("Adding pages to Import Engine Wizard");
        super.addPages(); 
        addPage(mainPage);        
    }

}
