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

import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.slf4j.Logger;


/**
 * @author Bitwise
 */
public class ResourceFileSelectionDialog extends ElementTreeSelectionDialog {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(ResourceFileSelectionDialog.class);
	
    private String[] extensions;

    private static ITreeContentProvider contentProvider = new ITreeContentProvider() {
        public Object[] getChildren(Object element) {
            if (element instanceof IContainer) {
                try {
                    return ((IContainer) element).members();
                }
                catch (CoreException e) {
                }
            }
            return null;
        }

        public Object getParent(Object element) {
            return ((IResource) element).getParent();
        }

        public boolean hasChildren(Object element) {
            return element instanceof IContainer;
        }

        public Object[] getElements(Object input) {
            return (Object[]) input;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        } 
    };

    private static final IStatus OK = new Status(IStatus.OK, "0", 0, "", null);
    private static final IStatus ERROR = new Status(IStatus.ERROR, "1", 1, "", null);

    /*
     * Validator
     */
    private ISelectionStatusValidator validator = new ISelectionStatusValidator() {
        public IStatus validate(Object[] selection) {
            return selection.length == 1 && selection[0] instanceof IFile
                    && checkExtension(((IFile) selection[0]).getFileExtension()) ? OK : ERROR;
        }
    };

	/**
	 * Instantiates a new resource file selection dialog.
	 * 
	 * @param title
	 *            the title
	 * @param message
	 *            the message
	 * @param type
	 *            the type
	 */
    public ResourceFileSelectionDialog(String title, String message, String[] type) {
        this(Display.getDefault().getActiveShell(), WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
                contentProvider);
        this.extensions = type;

        setTitle(title);
        setMessage(message);
        setInput(computeInput());
        setValidator(validator);
    }

	/**
	 * Instantiates a new resource file selection dialog.
	 * 
	 * @param parent
	 *            the parent
	 * @param labelProvider
	 *            the label provider
	 * @param contentProvider
	 *            the content provider
	 */
    public ResourceFileSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
        super(parent, labelProvider, contentProvider);
    } 

    /*
     * Show projects
     */
    private Object[] computeInput() {
        /*
         * Refresh projects tree.
         */
    		
    	IFileEditorInput editorInput=(IFileEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
    	IProject[] projects = new IProject[1];
    	projects[0]=editorInput.getFile().getProject();
    	
    	if(projects !=null){
    		for (int i = 0; i < projects.length; i++) {
    			try {
    				projects[i].refreshLocal(IResource.DEPTH_INFINITE, null);
    			} catch (CoreException e) {
    				logger.debug("Unable to refresh local file");
    			}
    		}
    	}
        try {
            ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_ONE, null);
        } catch (CoreException e) {
        }
        List<IProject> openProjects = new ArrayList<IProject>(projects.length);
        for (int i = 0; i < projects.length; i++) {
            if (projects[i].isOpen()) {
                openProjects.add(projects[i]);
            }
        } 
        return openProjects.toArray();
    }

    /*
     * Check file extension{
     * 
     */
    private boolean checkExtension(String name) {
        if (name.equals("*")) {
            return true;
        }

        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].equals(name)) {
                return true;
            }
        }
        return false;
    }
}