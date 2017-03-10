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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.slf4j.Logger;

import hydrograph.ui.logging.factory.LogFactory;


public class ProjectExplorerView extends CommonNavigator {
			
	Logger logger = LogFactory.INSTANCE.getLogger(ProjectExplorerView.class);
	@Override
	public void createPartControl(Composite aParent) {
		super.createPartControl(aParent);	
	}

	public boolean isSaveAsAllowed() {
		IEditorPart editorPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (editorPart != null) {
			return editorPart.isSaveAsAllowed();
			}
		return false;
	}

	@Override
	public void doSaveAs() {
	
		IEditorPart editorPart = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (editorPart != null) {
			editorPart.doSaveAs();
			}
	}
}
