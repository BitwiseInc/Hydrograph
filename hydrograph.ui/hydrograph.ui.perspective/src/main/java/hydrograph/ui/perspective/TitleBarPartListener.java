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

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/**
 * Display the relative path of current file on Title Bar
 * @author Bitwise
 *
 */
public class TitleBarPartListener implements IPartListener{
	
	private String windowTitlePrefix = "ELT Development - ";
	private String windowTitleSuffix = " - Hydrograph";
	private String windowTitleDefault = "ELT Development - Hydrograph";

	@Override
	public void partActivated(IWorkbenchPart part) {
		Shell shell= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		IEditorReference[] editorReference= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		
		if(part instanceof EditorPart){
			if(shell != null){
			shell.setText(windowTitlePrefix+((EditorPart)part).getTitleToolTip()+windowTitleSuffix);
			}
		}else{
			if(editorReference!=null && editorReference.length==0){
				shell.setText(windowTitleDefault);
			} 
		}
	}	


	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		
		
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		
		
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
	
		
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		
		
	}

}
