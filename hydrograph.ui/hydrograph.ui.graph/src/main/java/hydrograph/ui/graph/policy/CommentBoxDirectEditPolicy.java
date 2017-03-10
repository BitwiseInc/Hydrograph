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



import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import hydrograph.ui.graph.command.CommentCommand;
import hydrograph.ui.graph.controller.CommentBoxEditPart;
import hydrograph.ui.graph.figure.CommentBoxFigure;
import hydrograph.ui.graph.model.CommentBox;

/**
 * The Class CommentBoxDirectEditPolicy.
 * 
 * @author Bitwise
 * 
 */
public class CommentBoxDirectEditPolicy 
	extends DirectEditPolicy {

	/**
	 * Returns the Command to perform the direct edit.
	 * 
	 * @param request
	 *            the DirectEditRequest
	 * @return the command to perform the direct edit
	 */
protected Command getDirectEditCommand(DirectEditRequest edit){
	String labelText = (String)edit.getCellEditor().getValue();
	CommentBoxEditPart label = (CommentBoxEditPart)getHost();
	CommentCommand command = new CommentCommand((CommentBox)(label.getModel()), labelText);
	return command;
}

/**
 * Override to show the current direct edit value in the host's Figure.
 * Although the CellEditor will probably cover the figure's display of this
 * value, updating the figure will cause its preferred size to reflect the
 * new value.
 * 
 * @param request
 *            the DirectEditRequest
 */
protected void showCurrentEditValue(DirectEditRequest request){
	String value = (String)request.getCellEditor().getValue();
	((CommentBoxFigure)getHostFigure()).setText(value,(CommentBox)getHost().getModel());
	//prevent async layout from placing the cell editor twice.
	 getHostFigure().getUpdateManager().performUpdate();
	
}

}
