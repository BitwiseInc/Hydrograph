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


package hydrograph.ui.propertywindow.widgets.dialogs;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.utility.Extensions;
import hydrograph.ui.propertywindow.widgets.utility.FilterOperationClassUtility;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.slf4j.Logger;


/**
 * The Class ExternalSchemaFileSelectionDialog.
 *
 * @author Bitwise
 */
public class ExternalSchemaFileSelectionDialog extends ElementTreeSelectionDialog {

	/** The Constant logger. */
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ExternalSchemaFileSelectionDialog.class);
	
    /** The extensions. */
    private String[] extensions;
    
    /** The elt default text. */
    private AbstractELTWidget eltDefaultText;
    
    /** The filter operation class utility. */
    private FilterOperationClassUtility filterOperationClassUtility ;
    
    /** The content provider. */
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

    /** The Constant OK. */
    private static final IStatus OK = new Status(IStatus.OK, "0", 0, "", null);
    
    /** The Constant ERROR. */
    private static final IStatus ERROR = new Status(IStatus.ERROR, "1", 1, "", null);


    /** The validator. */
    private ISelectionStatusValidator validator = new ISelectionStatusValidator() {
        public IStatus validate(Object[] selection) {
        	if(selection.length == 1 && selection[0] instanceof IFile)
        	{
        		((Text)eltDefaultText.getSWTWidgetControl()).setText(((IFile) selection[0]).getName());
        	}
        	if(extensions.length!=0 && (Extensions.SCHEMA.toString().equalsIgnoreCase(extensions[0])  || Extensions.XML.toString().equalsIgnoreCase(extensions[0]))) {
        	 if(selection.length == 1 && selection[0] instanceof IFolder)
        	 {
        		 return OK;
        	 }
        	}
        	
            return selection.length == 1 && selection[0] instanceof IFile
                    && checkExtension(((IFile) selection[0]).getFileExtension()) ? OK : ERROR;
        }
    };

	/**
	 * Instantiates a new external schema selection dialog including validator for schema extension
	 *
	 * @param title            the title
	 * @param message            the message
	 * @param type            the type
	 * @param filterOperationClassUtility the filter operation class utility
	 */
    public ExternalSchemaFileSelectionDialog(String title, String message, String[] type,FilterOperationClassUtility filterOperationClassUtility) {
        this(Display.getDefault().getActiveShell(), WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
                contentProvider);
        this.extensions = type;
        
        setTitle(title);
        setMessage(message);
        setInput(computeInput());
        setValidator(validator);
        this.filterOperationClassUtility=filterOperationClassUtility;
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
    public ExternalSchemaFileSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
        super(parent, labelProvider, contentProvider);
        setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL);
    } 

    /**
     * Compute input.
     *
     * @return the object[]
     */
    private Object[] computeInput() {
        /*
         * Refresh projects tree.
         */
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (int i = 0; i < projects.length; i++) {
            try {
                projects[i].refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException e) {
            	logger.debug("Unable to refresh local file");
            }
        }

        try {
            ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_ONE, null);
        } catch (CoreException e) {
        	logger.debug("Unable to refresh local file");
        }
        List<IProject> openProjects = new ArrayList<IProject>(projects.length);
        for (int i = 0; i < projects.length; i++) {
            if (projects[i].isOpen()) {
                openProjects.add(projects[i]);
            }
        } 
        return openProjects.toArray();
    }

    /**
     * Check extension.
     *
     * @param name the name
     * @return true, if successful
     */
    private boolean checkExtension(String name) {
        if (StringUtils.equals(name,"*")) {
            return true;
        }

        for (int i = 0; i < extensions.length; i++) {
            if (StringUtils.equals(extensions[i],name)) {
                return true;
            }
        } 
        return false;
    } 
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
    	Composite composite=super.createDialogArea(parent).getParent();
		ELTDefaultSubgroupComposite eltSuDefaultSubgroupComposite = new ELTDefaultSubgroupComposite(composite);
		eltSuDefaultSubgroupComposite.createContainerWidget();
		eltSuDefaultSubgroupComposite.numberOfBasicWidgets(2);

		AbstractELTWidget eltDefaultLable = new ELTDefaultLable("      "+ Messages.FILE_NAME +"  :");
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultLable);
		eltDefaultText= new ELTDefaultTextBox().grabExcessHorizontalSpace(true).textBoxWidth(280);
		eltSuDefaultSubgroupComposite.attachWidget(eltDefaultText);
    	return composite;
    }


/* (non-Javadoc)
 * @see org.eclipse.ui.dialogs.SelectionStatusDialog#okPressed()
 */
@Override
protected void okPressed() {
	filterOperationClassUtility.setFileNameTextBoxValue(((Text)eltDefaultText.getSWTWidgetControl()).getText());
	super.okPressed();
}


}