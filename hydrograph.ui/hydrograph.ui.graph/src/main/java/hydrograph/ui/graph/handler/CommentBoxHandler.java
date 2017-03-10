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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.graph.command.CommentBoxCommand;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.model.CommentBox;
import hydrograph.ui.graph.model.Container;
/**
 * The Class CommentBoxHandler.
 * 
 * @author Bitwise
 * 
 */
public class CommentBoxHandler extends AbstractHandler{
	private Point oldLocation ;
	private Point newLocation ;
	private String LABEL = "Label";
	List <CommentBox> commentBoxList = new ArrayList();
	/**
	 * display the comment box
	 * @param event
	 * @return Object
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		ELTGraphicalEditor editor = (ELTGraphicalEditor)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		Container container = editor.getContainer();
		
		editor.setDirty(true);
		if(container.getChildren().isEmpty() && !(commentBoxList.isEmpty())){
			commentBoxList.clear();
		}
		
		for(Object obj : container.getChildren()){
			if((obj instanceof CommentBox)){
				commentBoxList.add((CommentBox) obj);
			}
			else{
			commentBoxList.clear();
			}
		}
		
		if(commentBoxList.isEmpty()){
		oldLocation = new Point(0,0);
		}
		else{
			oldLocation = (commentBoxList.get(commentBoxList.size()-1)).getLocation();	
		}
		
		newLocation = new Point(oldLocation.x+6 ,oldLocation.y+6);
		CommentBox comment = new CommentBox(LABEL);
		comment.setLocation(newLocation);
		CommentBoxCommand command = new CommentBoxCommand(comment,LABEL,container);
		command.execute();
		return null;
	}
}
