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


import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import hydrograph.ui.graph.command.ComponentDeleteCommand;
import hydrograph.ui.graph.command.LinkDeleteCommand;
import hydrograph.ui.graph.controller.CommentBoxEditPart;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.controller.LinkEditPart;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.Model;



/**
 * @author Bitwise The Class DeleteAction.
 */
public class DeleteAction extends SelectionAction {

	/**
	 * Instantiates a new Delete action.
	 * 
	 * @param part
	 *            the part
	 */
	public DeleteAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(true);
	}

	@Override
	protected void init() {
		super.init();

		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setText("Delete");
		setId(ActionFactory.DELETE.getId());
		setHoverImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setEnabled(false);
	}

	private Command createDeleteCommand(List<Object> selectedObjects) {
		if (selectedObjects == null || selectedObjects.isEmpty()) {
			return null;
		}

		ComponentDeleteCommand componentDeleteCommand = new ComponentDeleteCommand();
		LinkDeleteCommand linkDeleteCommand = new LinkDeleteCommand();

		populateDeleteCommands(selectedObjects, componentDeleteCommand,
				linkDeleteCommand);

		if(componentDeleteCommand.hasComponentToDelete())
			return componentDeleteCommand;

		if(linkDeleteCommand.hasLinkToDelete())
			return linkDeleteCommand;
		
		return null;
	}

	private void populateDeleteCommands(List<Object> selectedObjects,
			ComponentDeleteCommand componentDeleteCommand,
			LinkDeleteCommand linkDeleteCommand) {
		Model node;
		for(Object obj:selectedObjects)
		{
			if(obj instanceof ComponentEditPart || obj instanceof CommentBoxEditPart)
			{
				node = (Model) ((EditPart)obj).getModel();
				componentDeleteCommand.addComponentToDelete((Model)node);
			}
			if(obj instanceof LinkEditPart)
			{
				node = (Link) ((EditPart)obj).getModel();
				linkDeleteCommand.addLinkToDelete((Link)node);
			}	
			
		}
	}

	@Override
	protected boolean calculateEnabled() {
		Command cmd = createDeleteCommand(getSelectedObjects());
		if (cmd == null){
			ContributionItemManager.DELETE.setEnable(false);
			return false;
		}else{
			ContributionItemManager.DELETE.setEnable(true);
			return true;
		}
	}

	@Override
	public void run() {
		Command cmd = createDeleteCommand(getSelectedObjects());
		if (cmd != null && cmd.canExecute()) {
			execute(cmd);
		}
	}

}

