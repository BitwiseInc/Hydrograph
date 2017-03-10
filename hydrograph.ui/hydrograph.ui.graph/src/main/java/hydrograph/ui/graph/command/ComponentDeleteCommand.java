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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.controller.PortEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.model.CommentBox;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.Model;
import hydrograph.ui.graph.model.Port;



/**
 * The Class ComponentDeleteCommand.
 */
public class ComponentDeleteCommand extends Command {
	
	private List<Model> selectedComponents;
	private Set<Model> deleteComponents;
	private final Container parent;
	private boolean wasRemoved;
	private final List<Link> sourceConnections;
	private final List<Link> targetConnections;

	/**
	 * Instantiates a new component delete command.
	 * 
	 */
	public ComponentDeleteCommand() {

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();	
		this.parent=(((ELTGraphicalEditor) page.getActiveEditor()).getContainer());
		selectedComponents = new ArrayList<Model>();
		deleteComponents = new LinkedHashSet<>();
		sourceConnections = new ArrayList<Link>();
		targetConnections = new ArrayList<Link>();

	}

	/**
	 * 
	 * Add component to delete in selected component list
	 * 
	 * @param node
	 */
	public void addComponentToDelete(Model node){
		if(node instanceof Component || node instanceof CommentBox){
		selectedComponents.add(node);
		}
	}
	/**
	 * 
	 * returns false if selected component list is empty
	 * 
	 * @return boolean
	 */
	public boolean hasComponentToDelete(){
		return !selectedComponents.isEmpty();
	}

	@Override
	public boolean canExecute() {
		if (selectedComponents == null || selectedComponents.isEmpty())
			return false;

		Iterator<Model> it = selectedComponents.iterator();
		while (it.hasNext()) {
			Model node = it.next();
			if (isDeletableNode(node)) {
				deleteComponents.add(node);
			}
		}
		return true;

	}

	private boolean isDeletableNode(Model node) {
		if (node instanceof Component || node instanceof CommentBox)
			return true;
		else
			return false;
	}

	@Override
	public boolean canUndo() {
		return wasRemoved;
	}



	@Override
	public void execute() {
		redo();
	}

	@Override
	public void redo() {
		Iterator<Model> it = deleteComponents.iterator();
		while(it.hasNext()){
			Model model = it.next();
			if(model instanceof Component){
				Component deleteComp = (Component) model;
				deleteConnections(deleteComp);
				wasRemoved = parent.removeChild(deleteComp);
			}
			else if(model instanceof CommentBox){
				CommentBox deleteLable = (CommentBox)model;
				wasRemoved = parent.removeChild(deleteLable);
			}
		}

	}

	@Override
	public void undo() {
		Iterator<Model> it = deleteComponents.iterator();
		while(it.hasNext()){
			Model model = it.next();
			if(model instanceof Component){
				Component restoreComp=(Component)model;
				parent.addChild(restoreComp);
				restoreConnections();
			}
			else if(model instanceof CommentBox){
				CommentBox restoreComp = (CommentBox)model;
				parent.addChild(restoreComp);
			}
		}

	}

	private void deleteConnections(Component component) {

		sourceConnections.addAll(component.getSourceConnections());
		for (int i = 0; i < sourceConnections.size(); i++) {
			Link link = sourceConnections.get(i);
			link.detachSource();
			link.detachTarget();
			if(link.getSource()!=null)
				link.getSource().freeOutputPort(link.getSourceTerminal());
			if(link.getTarget()!=null)
				link.getTarget().freeInputPort(link.getTargetTerminal());
		}

		targetConnections.addAll(component.getTargetConnections());
		for (int i = 0; i < targetConnections.size(); i++) {
			Link link = targetConnections.get(i);
			
			Port sourcePort = link.getSource().getPort(link.getSourceTerminal());
			if(sourcePort.isWatched()){
				removeWatch(sourcePort, link.getSource());
			}
			
			link.detachSource();
			link.detachTarget();
			if(link.getSource()!=null)
				link.getSource().freeOutputPort(link.getSourceTerminal());
			if(link.getTarget()!=null)
				link.getTarget().freeInputPort(link.getTargetTerminal());
		}
	}

	private void removeWatch(Port sourcePort, Component sourceComponent){
		ELTGraphicalEditor editor=(ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		GraphicalViewer	graphicalViewer =(GraphicalViewer) ((GraphicalEditor)editor).getAdapter(GraphicalViewer.class);
		for (Object objectEditPart : graphicalViewer.getEditPartRegistry().values()) 
		{
			EditPart editPart = (EditPart) objectEditPart;
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
	 
	private void restoreConnections() {
		for (int i = 0; i < sourceConnections.size(); i++) {
			Link link = sourceConnections.get(i);
			link.attachSource();
			link.getSource().engageOutputPort(link.getSourceTerminal());
			link.attachTarget();
			link.getTarget().engageInputPort(link.getTargetTerminal());
		}
		sourceConnections.clear();
		for (int i = 0; i < targetConnections.size(); i++) {
			Link link = targetConnections.get(i);
			link.attachSource();
			link.getSource().engageOutputPort(link.getSourceTerminal());
			link.attachTarget();
			link.getTarget().engageInputPort(link.getTargetTerminal());
		}
		targetConnections.clear();
	}

}
