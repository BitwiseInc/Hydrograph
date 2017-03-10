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


package hydrograph.ui.graph.action.debug;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.controller.LinkEditPart;
import hydrograph.ui.graph.controller.PortEditPart;
import hydrograph.ui.graph.debugconverter.DebugHelper;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Bitwise
 *
 */
public class AddWatcherAction extends SelectionAction{

	private Long limitValue;
	private boolean isWatcher;
	
	public AddWatcherAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(true);
	}

	@Override
	protected void init() {
		super.init();
		setText(Messages.ADD_WATCH_POINT_TEXT);
		setId(Constants.ADD_WATCH_POINT_ID);
		setEnabled(false);
	}

	private void checkWatchPoint(List<Object> selectedObjects){
		for(Object obj:selectedObjects) {
			if(obj instanceof LinkEditPart) {
				Link link = (Link)((LinkEditPart)obj).getModel();
				isWatcher = DebugHelper.INSTANCE.checkWatcher(link.getSource(), link.getSourceTerminal());
			}
		}
	}
	

	private void addWatchPoint(List<Object> selectedObjects) {
 
		for(Object obj:selectedObjects)		{
			if(obj instanceof LinkEditPart)			{
				Link link = (Link)((LinkEditPart)obj).getModel();
				link.getSource().addWatcherTerminal(link.getSourceTerminal(), limitValue);
				changePortColor(link.getSource(), link.getSourceTerminal());
				if(!PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().isDirty())
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().doSave(null);
			} 
		}
		
	}

	private void changePortColor(Component selectedComponent, String portName){
		EditPart editPart = (EditPart) selectedComponent.getComponentEditPart();
		List<PortEditPart> portEdit = editPart.getChildren();
		for(AbstractGraphicalEditPart part : portEdit){
			if(part instanceof PortEditPart && ((PortEditPart)part).getCastedModel().getTerminal().equals(portName)){
					((PortEditPart)part).getPortFigure().changeWatcherColor();
					((PortEditPart)part).getCastedModel().setWatched(true);
					((PortEditPart)part).getPortFigure().setWatched(true);
					((PortEditPart)part).getPortFigure().repaint();
			}
		}
	}


	@Override
	public void run() {
		super.run();
		List<Object> selectedObjects =getSelectedObjects();
		limitValue = -1L;
		//((RemoveDebugHandler)RunStopButtonCommunicator.Removewatcher.getHandler()).setRemoveWatcherEnabled(true);
		addWatchPoint(selectedObjects);		 
	}

	@Override
	protected boolean calculateEnabled() {
		List<Object> selectedObject = getSelectedObjects();
		checkWatchPoint(selectedObject);
		if(!selectedObject.isEmpty()){
		for(Object obj : getSelectedObjects()){
			if(obj instanceof LinkEditPart && !isWatcher)	{
				
				return true;
			} 
		  }
		}
		return false;
	}
}
