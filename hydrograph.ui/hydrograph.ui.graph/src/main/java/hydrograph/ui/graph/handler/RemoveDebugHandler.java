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

 
package hydrograph.ui.graph.handler;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.graph.Messages;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.controller.PortEditPart;
import hydrograph.ui.graph.debugconverter.DebugHelper;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.job.RunStopButtonCommunicator;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.utility.MessageBox;


public class RemoveDebugHandler extends AbstractHandler{

	public RemoveDebugHandler() {
		RunStopButtonCommunicator.Removewatcher.setHandler(this);
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (DebugHelper.INSTANCE.hasMoreWatchPoints()) {
			ELTGraphicalEditor editor = (ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getActiveEditor();
			GraphicalViewer graphicalViewer = (GraphicalViewer) ((GraphicalEditor) editor)
					.getAdapter(GraphicalViewer.class);
			for (Iterator<EditPart> iterator = graphicalViewer.getEditPartRegistry().values().iterator(); iterator
					.hasNext();) {
				EditPart editPart = (EditPart) iterator.next();
				if (editPart instanceof ComponentEditPart) {
					Component comp = ((ComponentEditPart) editPart).getCastedModel();
					comp.clearWatchers();
				} else if (editPart instanceof PortEditPart) {
					((PortEditPart) editPart).getPortFigure().removeWatcherColor();
					((PortEditPart) editPart).getPortFigure().setWatched(false);
					((PortEditPart) editPart).getCastedModel().setWatched(false);
				}
			}
			showMessage(Messages.WATCH_POINT_REMOVED_SUCCESSFULLY);
		} else {
			showMessage(Messages.NO_WATCH_POINT_AVAILABLE);
		}
		return null;

	}

	private void showMessage(String message) {
		MessageBox.INSTANCE.showMessage("Information", message);
	}
}
