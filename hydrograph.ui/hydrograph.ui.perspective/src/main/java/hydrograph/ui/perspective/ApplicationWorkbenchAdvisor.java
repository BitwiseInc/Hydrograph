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

 
package hydrograph.ui.perspective;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;

/**
 * The Class ApplicationWorkbenchAdvisor.
 * 
 * @author Bitwise
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	
	private static final String PERSPECTIVE_ID = "hydrograph.ui.perspective.ETLPerspective"; //$NON-NLS-1$
	
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
    	IDE.registerAdapters();
    	 PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.ENABLE_DETACHED_VIEWS, false);
    	 configurer.setShowProgressIndicator(true);
    	 configurer.setShowFastViewBars(true);
    	 configurer.setShowStatusLine(true);
    	 configurer.getWindow().getPartService().addPartListener(new TitleBarPartListener());
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	
	
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public IAdaptable getDefaultPageInput() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
	}
	
	
}
