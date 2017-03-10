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

 
package hydrograph.ui.graph.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving resourceChange events. The class that is interested in processing a
 * resourceChange event implements this interface, and the object created with that class is registered with a component
 * using the component's <code>addResourceChangeListener<code> method. When
 * the resourceChange event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ResourceChangeEvent
 */
public class ResourceChangeListener implements IResourceChangeListener,	IResourceDeltaVisitor {
	private IEditorPart editorPart;

	/**
	 * Instantiates a new resource change listener.
	 * 
	 * @param editorPart
	 *            the editor part
	 */
	public ResourceChangeListener(IEditorPart editorPart) {
		this.editorPart = editorPart;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		try {
			if (delta != null)
				delta.accept(this);

		}
		catch (CoreException exception) {
		}
	}

	private void closeEditorDoNotSave() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				editorPart.getSite().getPage()
						.closeEditor(editorPart, false);
			}
		});

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	public boolean visit(IResourceDelta delta) {
		if (delta == null)
			return true;

		IEditorInput editorInput = editorPart.getEditorInput();
		if (editorInput instanceof FileEditorInput) {

			if (!delta.getResource().equals(
					((FileEditorInput) editorInput).getFile())) {
				return true;
			}
		} else {
			return true; // this is not an input type our editor handles
		}

		if (delta.getKind() == IResourceDelta.REMOVED) {
			if ((IResourceDelta.MOVED_TO & delta.getFlags()) == 0) {

				/*
				 *  * if the file was deleted NOTE: The case where an open,
				 * unsaved file is deleted being handled by the PartListener
				 * added to the Workbench in the initialize() method.
				 */

				if (!editorPart.isDirty()) {
					closeEditorDoNotSave();
				}
			}

			else { // else if it was moved or renamed
				final IFile newFile = ResourcesPlugin.getWorkspace()
						.getRoot().getFile(delta.getMovedToPath());

				Display display = editorPart.getSite().getShell().getDisplay();
				display.asyncExec(new Runnable() {
					public void run() {
						((ELTGraphicalEditor)editorPart).setInput(new FileEditorInput(newFile));
					}
				});
			}
		}
		return false;
	}
}
