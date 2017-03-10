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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import hydrograph.ui.graph.editor.ELTGraphicalEditor;
import hydrograph.ui.graph.model.CommentBox;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.Model;
import hydrograph.ui.logging.factory.LogFactory;


/**
 * The Class ComponentPasteCommand.
 */
public class ComponentPasteCommand extends Command {
	private static final String UNDERSCORE = "_";
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(ComponentPasteCommand.class);
	private int pasteCounter=0;
	private Map<Object,Object> list = new HashMap<>();

	@Override
	public boolean canExecute() {
		List bList = (ArrayList) Clipboard.getDefault().getContents();
		if (bList == null || bList.isEmpty())
			return false;
		Iterator it = bList.iterator();
		while (it.hasNext()) {
			Object node = (Object) it.next();
			if (isPastableNode(node)) {
				list.put(node, null);
			}
		}

		return true;
	}

	@Override
	public void execute() {
		if (!canExecute())
			return;
		Iterator<Object> it = list.keySet().iterator();
		while (it.hasNext()) {
			Object node = it.next();                              
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				if(node instanceof Component){
					Component clonedComponent =  ((Component)node).clone();
					clonedComponent.setPrefix(getPrefix(node));
					clonedComponent.setParent(((ELTGraphicalEditor) page.getActiveEditor()).getContainer());
					Point location = ((Component) node).getLocation();
					int incrementedLocation = pasteCounter * 20;
					clonedComponent.setLocation(new Point(location.getCopy().x + incrementedLocation,
						location.getCopy().y + incrementedLocation));
					list.put(node,clonedComponent);
				}
				
				else if(node instanceof CommentBox){
					CommentBox clonedLabel = ((CommentBox) node).clone();
					clonedLabel.setParent(((ELTGraphicalEditor) page.getActiveEditor()).getContainer());
					clonedLabel.setLabelContents(((CommentBox)node).getLabelContents());
					Point location = ((CommentBox)node).getLocation();
					int incrementedLocation = pasteCounter * 20;
					clonedLabel.setLocation(new Point(location.getCopy().x + incrementedLocation,
					location.getCopy().y + incrementedLocation));
					list.put(node,clonedLabel);
				}
			} catch (CloneNotSupportedException e) {
				LOGGER.error("Object could not cloned", e);
				
			}
		}
		redo();
	}

	private String getPrefix(Object node) {
		String currentName=((Component) node).getComponentLabel().getLabelContents();
		String prefix=currentName;
		StringBuffer buffer=new StringBuffer(currentName);
		try {
		if(buffer.lastIndexOf(UNDERSCORE)!=-1 && (buffer.lastIndexOf(UNDERSCORE)!=buffer.length())){
			String substring = StringUtils.trim(buffer.substring(buffer.lastIndexOf(UNDERSCORE)+1,buffer.length()));
			if(StringUtils.isNumeric(substring)){
				prefix=buffer.substring(0,buffer.lastIndexOf(UNDERSCORE)); 
			}
		}}
		catch (Exception exception) {
			LOGGER.warn("Cannot process component name for detecting prefix : ",exception.getMessage());
		}
		return prefix;
	}

	@Override
	public void redo() {
		Iterator<Object> it = list.values().iterator();
		while (it.hasNext()) {
			Model node = (Model) it.next();
			if (isPastableNode(node)) {
				if(node instanceof Component){
					((Component)node).getParent().addChild(((Component)node));
					}
					
					else if(node instanceof CommentBox){
						((CommentBox)node).getParent().addChild(((CommentBox)node));
					}
			}
		}
		
		pasteLinks();
		
	}

	private void pasteLinks() {
		for(Object originalNode:list.keySet()){
			if(originalNode instanceof Component){ 
				Component node =  (Component) originalNode;
			if(!((Component) originalNode).getSourceConnections().isEmpty()){
				
				for(Link originlink: ((Component) originalNode).getSourceConnections()){
					Component targetComponent = originlink.getTarget();
					Component newSource = (Component) list.get(originalNode);
					Component newtarget = (Component) list.get(targetComponent);					
					
					if(newSource!=null && newtarget!=null){
						Link link = new Link();
						link.setSourceTerminal(originlink.getSourceTerminal());
						link.setTargetTerminal(originlink.getTargetTerminal());
						link.setSource(newSource);
						link.setTarget(newtarget);
						newSource.connectOutput(link);
						newtarget.connectInput(link);
					}
				}
			}
		   }
		}
	}

	@Override
	public boolean canUndo() {
		return !(list.isEmpty());
	}

	@Override
	public void undo() {
		Iterator<Object> it = list.values().iterator();
		while (it.hasNext()) {
			Object object = it.next();
			if(object instanceof Component){
			Component node = (Component) object;
			if (isPastableNode(node)) {
				node.getParent().removeChild(node);
			  }
		   }
		}
	}

	/**
	 * Checks if is pastable node.
	 * 
	 * @param node
	 *            the node
	 * @return true, if is pastable node
	 */
	public boolean isPastableNode(Object node) {
		if (node instanceof Component)
			return true;
		if (node instanceof Link)
			return true;
		if (node instanceof CommentBox)
			return true;
		return false;
	}

	/**
	 * 
	 * get paste counter
	 * 
	 * @return
	 */
	public int getPasteCounter() {
		return pasteCounter;
	}

	/**
	 * 
	 * Set paste counter
	 * 
	 * @param pasteCounter
	 */
	public void setPasteCounter(int pasteCounter) {
		this.pasteCounter = pasteCounter;
	}

}
