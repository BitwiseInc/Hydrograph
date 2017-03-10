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


import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;


/**
 * The Class ApplicationActionBarAdvisor.
 * 
 * @author Bitwise
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	
	private static final String HYDROGRAPH_UI_GRAPH_TOOLBAR_ID = "hydrograph.ui.graph.toolbar1";
	private IWorkbenchAction openPerspectiveAction;
	 
	/**
	 * Instantiates a new application action bar advisor.
	 * 
	 * @param configurer
	 *            the configurer
	 */
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	
    	openPerspectiveAction = ActionFactory.OPEN_PERSPECTIVE_DIALOG.create(window);
        register(openPerspectiveAction);
        IWorkbenchAction refreshAction=ActionFactory.REFRESH.create(window);
        register(refreshAction);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	
    }
    
    @Override
    protected void fillCoolBar(ICoolBarManager coolBar) {
    	super.fillCoolBar(coolBar);
    	IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.LEFT);
		coolBar.add(new ToolBarContributionItem(toolbar, HYDROGRAPH_UI_GRAPH_TOOLBAR_ID));

    }
}
