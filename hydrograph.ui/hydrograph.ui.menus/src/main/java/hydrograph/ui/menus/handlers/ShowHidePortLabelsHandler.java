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

 
package hydrograph.ui.menus.handlers;

import hydrograph.ui.graph.controller.PortEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.figure.PortFigure;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;


/**
 * The Class ShowHidePortLabelsHandler.
 * <p>
 * Creates Show Hide Port Labels Handler
 * 
 * @author Bitwise
 */
public class ShowHidePortLabelsHandler extends AbstractHandler implements IHandler,IElementUpdater {
	private UIElement element;
	/**
	 * show and hide port labels of components
	 * @param event
	 * @return Object
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		boolean toggleValue=false;
		if(editor==null)
			element.setChecked(false);
		if(editor!=null && editor instanceof ELTGraphicalEditor)
		{
			GraphicalViewer	graphicalViewer =(GraphicalViewer) ((GraphicalEditor)editor).getAdapter(GraphicalViewer.class);
			for (Iterator<EditPart> ite = graphicalViewer.getEditPartRegistry().values().iterator(); 
					ite.hasNext();)
			{
				EditPart editPart = (EditPart) ite.next();
				if(editPart instanceof PortEditPart) 
				{
					PortFigure portFigure=((PortEditPart)editPart).getPortFigure();
					toggleValue=portFigure.isDisplayPortlabels();
				}
			}
			for (Iterator<EditPart> ite = graphicalViewer.getEditPartRegistry().values().iterator(); 
					ite.hasNext();)
			{
				EditPart editPart = (EditPart) ite.next();
				if(editPart instanceof PortEditPart) 
				{
					PortFigure portFigure=((PortEditPart)editPart).getPortFigure();
					portFigure.setDisplayPortlabels(!toggleValue);
					element.setChecked(portFigure.isDisplayPortlabels());
					portFigure.repaint();
				}
			}
		}
		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		this.element=element;
	}

}
