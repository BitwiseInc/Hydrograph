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

 
package hydrograph.ui.graph.policy;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import hydrograph.ui.graph.command.CommentBoxSetConstraintCommand;
import hydrograph.ui.graph.command.ComponentCreateCommand;
import hydrograph.ui.graph.command.ComponentSetConstraintCommand;
import hydrograph.ui.graph.controller.CommentBoxEditPart;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.model.CommentBox;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;


/**
  * EditPolicy for the Figure used by this edit part. 
  * Creates the model components as and when requested(ex. Drag and drop from pallet)
  * Children of XYLayoutEditPolicy can be used in Figures with XYLayout. *
 */
public class ShapesXYLayoutEditPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		//creates the component
		return new ComponentCreateCommand((Component) request.getNewObject(), //custom component
				(Container) getHost().getModel(),
				(Rectangle) getConstraintFor(request));
	}

	/**
	 * Creates the command which is used to move and/or resize a shape
	 */
	@Override
	protected Command createChangeConstraintCommand(ChangeBoundsRequest request, EditPart child, Object constraint) {
		if (child instanceof ComponentEditPart && constraint instanceof Rectangle) {
			//return a command that can move and/or resize a Shape
			return new ComponentSetConstraintCommand((Component) child.getModel(),
					request, (Rectangle) constraint);
		}
		else if (child instanceof CommentBoxEditPart && constraint instanceof Rectangle){
			return new CommentBoxSetConstraintCommand((CommentBox) child.getModel(), request , (Rectangle) constraint);
		}
		return super.createChangeConstraintCommand(request, child,
				constraint);
	}
	
	@Override 
	protected EditPolicy createChildEditPolicy(EditPart child) { 
		if (child instanceof CommentBoxEditPart){
			return new CommentBoxResizableEditPolicy();
		}
		return new ComponentResizableEditPolicy();
		//return new NonResizableEditPolicy(); 
	} 
	
}
