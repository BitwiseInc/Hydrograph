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
package hydrograph.ui.propertywindow.widgets.utility;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

public class CustomizeNewClassWizardPage extends NewClassWizardPage {

	@Override
	protected void createContainerControls(Composite parent, int nColumns) {

		super.createContainerControls(parent, nColumns);
		Text text = (Text) parent.getChildren()[1];
		text.setEditable(false);
		IEditorInput editorInput = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor().getEditorInput();
		if (editorInput instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
			IProject project = fileEditorInput.getFile().getProject();
			if (project != null) {
				IFolder srcFolder = project.getFolder("src/main/java");
				if (srcFolder != null && srcFolder.exists()) {
					text.setText(project.getName() + "/" + srcFolder.getProjectRelativePath().toString());
				}
			}
			Button button = (Button) parent.getChildren()[2];
			button.setEnabled(false);
		}
	}
}
