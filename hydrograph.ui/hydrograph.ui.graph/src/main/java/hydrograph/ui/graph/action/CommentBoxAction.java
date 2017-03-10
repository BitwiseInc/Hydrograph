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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.graph.command.CommentBoxCommand;
import hydrograph.ui.graph.controller.ContainerEditPart;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.model.CommentBox;
import hydrograph.ui.graph.model.Container;
/**
 * The Class Action use to create Comment Box
 * 
 * @author Bitwise
 */
public class CommentBoxAction extends SelectionAction{
	private static String COMMENT_BOX = "Comment Box";
	private String LABEL = "Label";
	/**
	 * Instantiates a new CommentBoxAction.
	 * 
	 * @param part
	 *            the part
	 * @param action
	 *            the action
	 */
	
public CommentBoxAction(IWorkbenchPart part, IAction action){
		super(part);
		setLazyEnablementCalculation(true);
	}

	@Override
	protected boolean calculateEnabled() {
		if(getSelectedObjects()!=null && getSelectedObjects().size()==1 && getSelectedObjects().get(0) instanceof ContainerEditPart)
			return true;
		return false;
	}

	@Override
	protected void init() {
		super.init();
		setText(COMMENT_BOX); 
		setId(Constants.COMMENT_BOX);
		setHoverImageDescriptor(getImageDisDescriptor());
		setImageDescriptor(getImageDisDescriptor());
		setDisabledImageDescriptor(getImageDisDescriptor());
		setEnabled(false);
	}
	
	/**
	 * Creating command for comment box
	 */
	private Command createCommentBoxCommand(){
		ELTGraphicalEditor editor = (ELTGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(editor != null){
		Container container = editor.getContainer();
		
		org.eclipse.draw2d.geometry.Point point = editor.location;
		CommentBox label = new CommentBox(LABEL);
		label.setSize(new Dimension(300, 60));
		label.setLocation(point);
		CommentBoxCommand command = new CommentBoxCommand(label,LABEL,container);
		return command;
		}
		return null;
	}
	
	/**
	 * return ImageDescriptor for comment box
	 * @return ImageDescriptor
	 */
	private ImageDescriptor getImageDisDescriptor() {
		ImageDescriptor imageDescriptor = new ImageDescriptor() {

			@Override
			public ImageData getImageData() {
				return new ImageData(XMLConfigUtil.INSTANCE.CONFIG_FILES_PATH+"/icons/comment-box-icon.png");
			}
		};
		return imageDescriptor;
	}
	
	@Override
	public void run() {
		execute(createCommentBoxCommand());
	}
}
