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

 
package hydrograph.ui.perspective.config;

import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Aug 26, 2015
 * 
 */

public class ELTPerspectiveConfig {
	IWorkbenchWindowConfigurer configurer;	
	PerspectiveRemover perspectiveRemover;
		
	/**
	 * Instantiates a new ELT perspective config.
	 * 
	 * @param configurer
	 *            the configurer
	 */
	public ELTPerspectiveConfig(IWorkbenchWindowConfigurer configurer){
		this.configurer=configurer;
		perspectiveRemover=new PerspectiveRemover();
	}
	
	private void dockPrespectiveBarToRightTop(){
		PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.TOP_RIGHT);
	}
	
	/**
	 * Sets the default elt prespective configurations.
	 */
	public void setDefaultELTPrespectiveConfigurations(){
		
		configurer.setInitialSize(configurer.getInitialSize());
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(false);
        configurer.setTitle("Hydrograph"); //$NON-NLS-1$
        configurer.setShowPerspectiveBar(true);
        
        dockPrespectiveBarToRightTop();
        
        perspectiveRemover.removeUnWantedPerspectives();
	}
}
