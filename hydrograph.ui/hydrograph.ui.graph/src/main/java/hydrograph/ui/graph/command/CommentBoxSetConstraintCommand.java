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


import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

import hydrograph.ui.graph.model.CommentBox;


/**
 * The Class ComponentSetConstraintCommand.
 * @author BITWISE
 */
public class CommentBoxSetConstraintCommand extends Command {
	/** Stores the new size and location. */
	private final Rectangle newBounds;
	/** Stores the old size and location. */
	private Rectangle oldBounds;
	/** A request to move/resize an edit part. */
	private final ChangeBoundsRequest request;

	/** Shape to manipulate. */
	private final CommentBox shape;
	
	private Dimension newSize;
	private Dimension oldSize;
	
	/**
	 * Create a command that can resize and/or move a shape.
	 * 
	 * @param shape the shape to manipulate
	 * @param request the move and resize request
	 * @param newBounds the new size and location
	 * @throws IllegalArgumentException if any of the parameters is null
	 */
	public CommentBoxSetConstraintCommand(CommentBox shape, ChangeBoundsRequest request, Rectangle newBounds) {
		if (shape == null || request == null || newBounds == null) {
			throw new IllegalArgumentException();
		}
		this.shape = shape;
		this.request = request;
		this.newBounds = newBounds.getCopy();
		this.newSize = newBounds.getSize();
		setLabel("move / resize");
	}
	
	@Override
	public boolean canExecute(){
		Object type = request.getType();
		// make sure the Request is of a type we support:
		return (StringUtils.equals(RequestConstants.REQ_MOVE, (String) type) || StringUtils.equals(RequestConstants.REQ_MOVE_CHILDREN, (String) type)
				|| StringUtils.equals(RequestConstants.REQ_RESIZE_CHILDREN, (String) type) || StringUtils.equals(RequestConstants.REQ_RESIZE, (String) type));
	}
	
	@Override
	public void execute(){
		oldSize = shape.getSize();
		oldBounds = new Rectangle(shape.getLocation(), shape.getSize());
		redo();
	}
	
	@Override
	public void redo(){
		shape.setSize(newSize);
		shape.setLocation(newBounds.getLocation());
	}

	@Override
	public void undo(){
		shape.setSize(oldSize);
		shape.setLocation(oldBounds.getLocation());
	}
	
	@Override
	public String getLabel() {
		if (oldSize.equals(newSize))
			return "Move Object";
		return "Resize Object";
	}
}
