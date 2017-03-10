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

 
package hydrograph.ui.graph.action;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import hydrograph.ui.graph.command.ComponentCopyCommand;
import hydrograph.ui.graph.controller.CommentBoxEditPart;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.model.CommentBox;
import hydrograph.ui.graph.model.Component;


/**
 * The Class CopyAction.
 */
public class CopyAction extends SelectionAction {
	PasteAction pasteAction;
	
	/**
	 * Instantiates a new copy action.
	 * 
	 * @param part
	 *            the part
	 * @param action
	 *            the action
	 */
	public CopyAction(IWorkbenchPart part, IAction action) {
		super(part);
		this.pasteAction = (PasteAction) action;
		setLazyEnablementCalculation(true);
	}

	@Override
	protected void init() {
		super.init();
		
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setText("Copy");
		setId(ActionFactory.COPY.getId());
		setHoverImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		setEnabled(false);
	}

	private Command createCopyCommand(List<Object> selectedObjects) {
		ComponentCopyCommand copyCommand =new ComponentCopyCommand();
		if (selectedObjects == null || selectedObjects.isEmpty()) {
			return null;
		}
		Object node = null;
		Iterator<Object> it = selectedObjects.iterator();
		boolean enabled=false;
		for(Object obj:selectedObjects)
		{
			if(obj instanceof ComponentEditPart || obj instanceof CommentBoxEditPart)
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
				copyCommand.addElement(node);
			}
			else if(obj instanceof CommentBoxEditPart){
				node = (CommentBox)((EditPart)obj).getModel();
				copyCommand.addElement(node);
			}
		}
		return copyCommand;
		}
		else 
		return null;	
	}

	@Override
	protected boolean calculateEnabled() {
		Command cmd = createCopyCommand(getSelectedObjects());
		if (cmd == null){
			ContributionItemManager.COPY.setEnable(false);
			return false;
		}else{
			ContributionItemManager.COPY.setEnable(true);
			return true;
		}
	}

	@Override
	public void run() {
		Command cmd = createCopyCommand(getSelectedObjects());
		if (cmd != null && cmd.canExecute()) {
			cmd.execute();
			pasteAction.setPasteCounter(1);
			pasteAction.update();
		}
	}

}
