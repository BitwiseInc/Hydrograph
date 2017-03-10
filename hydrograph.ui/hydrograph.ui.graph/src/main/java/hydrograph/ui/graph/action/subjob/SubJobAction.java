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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.action.PasteAction;
import hydrograph.ui.graph.command.ComponentCreateCommand;
import hydrograph.ui.graph.command.SubJobCommand;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Component.ValidityStatus;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.LinkComparatorBySourceLocation;
import hydrograph.ui.graph.model.components.InputSubjobComponent;
import hydrograph.ui.graph.model.components.OutputSubjobComponent;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.graph.utility.SubJobUtility;
/**
 * The Class SubJobAction use to create sub graph.
 * 
 * @author Bitwise
 */
public class SubJobAction extends SelectionAction{
	
	/** The paste action. */
	//TODO : remove pasteAction is not needed.
	PasteAction pasteAction;
	
	/** The ed component edit part. */
	ComponentEditPart componentEditPart;
	/**
	 * Instantiates a new cut action.
	 * 
	 * @param part
	 *            the part
	 * @param action
	 *            the action
	 */
	public SubJobAction(IWorkbenchPart part, IAction action) {
		super(part);
		this.pasteAction = (PasteAction) action;
		setLazyEnablementCalculation(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#init()
	 */
	@Override
	protected void init() {
		super.init();
		
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setText(Constants.SUBJOB_CREATE); 
		setId(Constants.SUBJOB_CREATE);
		setEnabled(false);
	}

	/**
	 * Creates the sub job command.
	 *
	 * @param selectedObjects the selected objects
	 * @return the command
	 */
	private Command createSubJobCommand(List<Object> selectedObjects) {
		SubJobCommand cutCommand =new SubJobCommand();
		if (selectedObjects == null || selectedObjects.isEmpty()) {
			return null;
		}
		Component node = null;
		boolean enabled=false;
		for(Object obj:selectedObjects)
		{
			if(obj instanceof ComponentEditPart)
			{
				enabled=true;
				break;
			}	
		}
		if(enabled)
		{	
		for(Object obj:selectedObjects)
		{
			if (obj instanceof ComponentEditPart) {
				node = (Component) ((EditPart)obj).getModel();
				cutCommand.addElement(node);
			}
		}
		return cutCommand;
		}
		else 
    	return null;	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	@Override
	protected boolean calculateEnabled() {
		Command cmd = createSubJobCommand(getSelectedObjects());
		if (cmd == null){
			return false;
		}else{
			return true;
		}
 
	}

	
	/* 
	 * Create sub graph
	 */
	@Override  
	public void run() { 
		
		if(notConfirmedByUser())
			return;
		
		SubJobUtility subJobUtility = new SubJobUtility();
	
		IFile file=subJobUtility.openSubJobSaveDialog();
		if(file!=null)
		{	
		ELTGraphicalEditor editor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		Container containerOld=editor.getContainer(); 
		String validityStatus = null;
			for (int i = 0; i < containerOld.getUIComponentList().size(); i++) {
				if (!(containerOld.getUIComponentList().get(i) instanceof InputSubjobComponent || containerOld.getUIComponentList()
						.get(i) instanceof OutputSubjobComponent)) {
					Component component = containerOld.getUIComponentList().get(i);
					if (component.getProperties().get(Constants.VALIDITY_STATUS) != null
							&& ((StringUtils.equalsIgnoreCase(ValidityStatus.ERROR.name(), component.getProperties().get(Constants.VALIDITY_STATUS).toString()))
							|| StringUtils.equalsIgnoreCase(ValidityStatus.WARN.name(),
									component.getProperties().get(Constants.VALIDITY_STATUS)
											.toString()))) {
						validityStatus = ValidityStatus.ERROR.name();
						break;
					} else {
						validityStatus = ValidityStatus.VALID.name();
					}
				}
			}
	   	execute(createSubJobCommand(getSelectedObjects())); 
    	List clipboardList = (List) Clipboard.getDefault().getContents();
    	SubjobComponent subjobComponent= new SubjobComponent();
		ComponentCreateCommand createComponent = new ComponentCreateCommand(subjobComponent,containerOld,new Rectangle(((Component)clipboardList.get(0)).getLocation(),((Component)clipboardList.get(0)).getSize()));
		createComponent.execute(); 
			
		subjobComponent.getProperties().put(Constants.VALIDITY_STATUS,validityStatus);
		GraphicalViewer	graphicalViewer =(GraphicalViewer) ((GraphicalEditor)editor).getAdapter(GraphicalViewer.class);
		for (Iterator<EditPart> ite = graphicalViewer.getEditPartRegistry().values().iterator(); ite.hasNext();)
		{
			EditPart editPart = (EditPart) ite.next();
			
			if(editPart instanceof ComponentEditPart && Constants.SUBJOB_COMPONENT_CATEGORY.equalsIgnoreCase(((ComponentEditPart) editPart).getCastedModel().getCategory())) 
			{ Component tempComponent=((ComponentEditPart) editPart).getCastedModel();
				if (StringUtils.equals(tempComponent.getComponentLabel().getLabelContents(), subjobComponent.getComponentLabel().getLabelContents())) {
					componentEditPart= (ComponentEditPart) editPart;
				}
			} 
		}

		/*
		 * Collect all input and output links for missing target or source. 
		 */
		List< Link> inLinks = new ArrayList<>();
		List< Link> outLinks = new ArrayList<>();
		for (Object object : clipboardList) {
				Component component = (Component)object;
				if(component!= null){
					List<Link> tarLinks= component.getTargetConnections();
					for(int i=0;i<tarLinks.size();i++){
						if (!clipboardList.contains(tarLinks.get(i).getSource())) {
							inLinks.add(tarLinks.get(i));
						}
					}
					List<Link> sourLinks= component.getSourceConnections();
					for(int i=0;i<sourLinks.size();i++){
						if (!clipboardList.contains(sourLinks.get(i).getTarget())) {
							outLinks.add(sourLinks.get(i)); 
						}
					}
					   
				}   
		}  
		Collections.sort(inLinks, new LinkComparatorBySourceLocation());
		Collections.sort(outLinks, new LinkComparatorBySourceLocation());
		/*
		 * Update main sub graph component size and properties
		 */
		subJobUtility.updateSubJobModelProperties(componentEditPart, inLinks.size(), outLinks.size(), file);
		
		/*
		 * Create Input port in main subjob component.
		 */
		subJobUtility.createDynamicInputPort(inLinks, componentEditPart);
		/*
		 * Create output port in main subjob component.
		 */
		subJobUtility.createDynamicOutputPort(outLinks, componentEditPart)	;
		/*
		 * Generate subjob target xml.
		 */
		Container container=subJobUtility.createSubJobXmlAndGetContainer(componentEditPart,clipboardList,file);
		finishSubjobCreation(subjobComponent,componentEditPart,container);
		}
	}

	
	private void finishSubjobCreation(Component subjobComponent, ComponentEditPart componentEditPart, Container container) {
		subjobComponent.getProperties().put(Constants.SUBJOB_VERSION,container.getSubjobVersion());
		SubJobUtility.getCurrentEditor().setDirty(true);
		componentEditPart.updateComponentStatus();
		SubJobUtility.getCurrentEditor().getViewer().select(componentEditPart);
	}

	private boolean notConfirmedByUser() {
		MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.YES
				| SWT.NO);
		messageBox.setMessage(Messages.CONFIRM_TO_CREATE_SUBJOB_MESSAGE);
		messageBox.setText(Messages.CONFIRM_TO_CREATE_SUBJOB_WINDOW_TITLE);
		int response = messageBox.open();
		if (response == SWT.YES) {
			return false;
		} else
			return true;
	}
}
