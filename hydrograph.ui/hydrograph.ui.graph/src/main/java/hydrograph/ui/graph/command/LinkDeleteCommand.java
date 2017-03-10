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


package hydrograph.ui.graph.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.draw2d.Graphics;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.controller.PortEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.Model;
import hydrograph.ui.graph.model.Port;
import hydrograph.ui.logging.factory.LogFactory;



/**
 * @author Bitwise The Class LinkDeleteCommand.
 */
public class LinkDeleteCommand extends Command{

	private static final Logger logger = LogFactory.INSTANCE.getLogger(LinkDeleteCommand.class);
	
	private List<Model> selectedLinks;
	private Set<Model> deleteLinks;
	

	public LinkDeleteCommand() {
		super("Delete connection");
		selectedLinks=new ArrayList<Model>();
		deleteLinks=new LinkedHashSet<Model>();
		setLabel("Delete Connection");
	}
	
	public void addLinkToDelete(Model link){
		selectedLinks.add(link);
	}

	public boolean hasLinkToDelete(){
		return !selectedLinks.isEmpty();
	}

	@Override
	public boolean canExecute() {
		
		if (selectedLinks == null || selectedLinks.isEmpty())
			return false;

		deleteLinks.clear();
		Iterator<Model> it = selectedLinks.iterator();
		while (it.hasNext()) {
			Link node = (Link) it.next();
			if (isDeletableNode(node)) {
				deleteLinks.add(node);
			}
		}
		return true;
	}
	
	private boolean isDeletableNode(Model node) {
		if (node instanceof Link)
			return true;
		else
			return false;
	}

	@Override
	public void execute() {
		redo();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void redo() {

		Iterator<Model> it = deleteLinks.iterator();
		while(it.hasNext()){
			Link deleteLink=(Link)it.next();
			Port sourcePort = deleteLink.getSource().getPort(deleteLink.getSourceTerminal());
			if(sourcePort.isWatched()){
				removeWatch(sourcePort, deleteLink.getSource());
			}
			
			deleteLink.detachSource();
			deleteLink.detachTarget();
			deleteLink.getSource().freeOutputPort(deleteLink.getSourceTerminal());
			deleteLink.getTarget().freeInputPort(deleteLink.getTargetTerminal());
		}

	}

	private void removeWatch(Port sourcePort, Component sourceComponent){
		ELTGraphicalEditor editor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		GraphicalViewer	graphicalViewer =(GraphicalViewer) ((GraphicalEditor)editor).getAdapter(GraphicalViewer.class);
		for (Iterator<EditPart> iterator = graphicalViewer.getEditPartRegistry().values().iterator(); iterator.hasNext();)
		{
			EditPart editPart = (EditPart) iterator.next();
			if(editPart instanceof ComponentEditPart) 
			{
				Component comp = ((ComponentEditPart)editPart).getCastedModel();
				if(comp.equals(sourceComponent)){
					List<PortEditPart> portEditParts = editPart.getChildren();
					for(AbstractGraphicalEditPart part:portEditParts)
					{
						if(part instanceof PortEditPart){
							if(((PortEditPart)part).getCastedModel().getTerminal().equals(sourcePort.getTerminal())){
								((PortEditPart)part).getPortFigure().removeWatcherColor();
								((PortEditPart)part).getPortFigure().setWatched(false);
							} 
						}
					}
				}
			} 
		}
	}


	@Override
	public void undo() {
		
		Iterator<Model> it = deleteLinks.iterator();
		while(it.hasNext()){
			Link restoreLink=(Link)it.next();
			
			restoreLink.setLineStyle(Graphics.LINE_SOLID);
			restoreLink.attachSource();
			restoreLink.getSource().engageOutputPort(restoreLink.getSourceTerminal());
			
			restoreLink.setLineStyle(Graphics.LINE_SOLID);
			restoreLink.attachTarget();
			restoreLink.getTarget().engageInputPort(restoreLink.getTargetTerminal());
			
		}

	}
}