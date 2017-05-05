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

 
package hydrograph.ui.graph.action.subjob;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.action.PasteAction;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.InputSubjobComponent;
import hydrograph.ui.graph.utility.SubJobUtility;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.widgets.utility.SubjobUtility;


/**
 * The Class SubJobOpenAction use to open sub graph.
 * 
 * @author Bitwise
 */
public class SubJobOpenAction extends SelectionAction{
	PasteAction pasteAction;
	ComponentEditPart edComponentEditPart;
	
	Logger logger = LogFactory.INSTANCE.getLogger(SubJobOpenAction.class);
	/**
	 * Instantiates a new cut action.
	 * 
	 * @param part
	 *            the part
	 * @param action
	 *            the action
	 */
	public SubJobOpenAction(IWorkbenchPart part, IAction action) {
		super(part);
		this.pasteAction = (PasteAction) action;
		setLazyEnablementCalculation(true);
	}

	@Override
	protected void init() {
		super.init();
		
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setText(Constants.SUBJOB_OPEN); 
		setId(Constants.SUBJOB_OPEN);
		setEnabled(false);
	}



	/*
	 * Open the sub graph that saved in sub graph component path property.
	 */
	@Override
	public void run() {
		List<Object> selectedObjects = getSelectedObjects();
		SubJobUtility subJobUtility = new SubJobUtility();
		String mainJobFilePath=subJobUtility.getCurrentEditor().getTitleToolTip();
		Container container = null;
		if (selectedObjects != null && !selectedObjects.isEmpty()) {
			for (Object obj : selectedObjects) {
					
				if (obj instanceof ComponentEditPart) {
					if (((ComponentEditPart) obj).getCastedModel().getCategory()
							.equalsIgnoreCase(Constants.SUBJOB_COMPONENT_CATEGORY)) {
						Component subjobComponent = ((ComponentEditPart) obj).getCastedModel();
						String pathProperty = (String) subjobComponent.getProperties().get(
								Constants.PATH_PROPERTY_NAME);
						if (StringUtils.isNotBlank(pathProperty)) {
							try {
								IPath jobFilePath = new Path(pathProperty);
								if (SubJobUtility.isFileExistsOnLocalFileSystem(jobFilePath)) {
									container = openEditor(jobFilePath);
									if (container != null){
										container.setLinkedMainGraphPath(mainJobFilePath);
										container.setSubjobComponentEditPart(obj);

                                      for (Component component : container.getUIComponentList()) {
										subJobUtility.propogateSchemaToSubjob(subjobComponent, component);
										}

										if(subjobComponent.isContinuousSchemaPropogationAllow())
										{
											Component component1=null;
											for(Object object:container.getChildren())
											{
												if(object instanceof Component)
												{
													 component1=(Component)object;
													if(component1 instanceof InputSubjobComponent)
													{
														SubjobUtility.INSTANCE.initializeSchemaMapForInputSubJobComponent
														(component1, subjobComponent);
														break;
													}	
												}
													
											}
											if(component1!=null)
											SubjobUtility.INSTANCE.setFlagForContinuousSchemaPropogation(component1);
											subjobComponent.setContinuousSchemaPropogationAllow(false);
										}	
										
										subjobComponent.getSubJobContainer().put(Constants.SUBJOB_CONTAINER, container);
									}
									((ComponentEditPart) obj).refresh();
								} else
									MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
											"Subjob File does not exists");
								
							} catch (CoreException | IllegalArgumentException exception) {
								logger.error("Unable to open subjob" + exception);
								MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
										"Unable to open subjob : Invalid file path\n" + exception.getMessage());
							}
						} else
							MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
									"Unable to open subjob : Subjob file path is empty");
					}
				}

			}
		}
	}

	private Container openEditor(IPath jobFilePath) throws CoreException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (!isJobAlreadyOpen(jobFilePath)) {
			if (ResourcesPlugin.getWorkspace().getRoot().getFile(jobFilePath).exists()) {
				IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(jobFilePath);
				IDE.openEditor(page, iFile);
		} else {
			if (jobFilePath.toFile().exists()) {
				IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(jobFilePath.toFile());
				IEditorInput store = new FileStoreEditorInput(fileStore);
				IDE.openEditorOnFileStore(page, fileStore);
			}
		}

		return SubJobUtility.getCurrentEditor().getContainer();
		}else
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",
					"Unable to open subjob : "+jobFilePath.lastSegment()+" Subjob is already open \n" +
							"Please close the job and retry");
		return null;
	}
		
	private boolean isJobAlreadyOpen(IPath jobFilePath) {
		
		String jobPathRelative = StringUtils.removeStart(jobFilePath.toString(), "..");
		jobPathRelative=StringUtils.removeStart(jobPathRelative, "/");
		String jobPathAbsolute = StringUtils.replace(jobPathRelative, "/", "\\");
		for (IEditorReference editorRefrence : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences()) {
			if (StringUtils.equals(editorRefrence.getTitleToolTip(), jobPathRelative)) {
				return true;
			}else if (StringUtils.equals(editorRefrence.getTitleToolTip(), jobPathAbsolute)) {
				return true;
			}
		}
		return false;
	}
		
	@Override
	protected boolean calculateEnabled() {
		List<Object> selectedObjects = getSelectedObjects();
		if (selectedObjects != null && !selectedObjects.isEmpty() && selectedObjects.size() == 1) {
			for (Object obj : selectedObjects) {
				if (obj instanceof ComponentEditPart) {
					if (Constants.SUBJOB_COMPONENT.equalsIgnoreCase(((ComponentEditPart) obj).getCastedModel()
							.getComponentName()))
						return true;
				}
			}
		}
		return false;
	}	
	
   }
