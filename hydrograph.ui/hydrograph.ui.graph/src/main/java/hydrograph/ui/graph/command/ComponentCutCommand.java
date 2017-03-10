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
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import hydrograph.ui.graph.model.CommentBox;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;


/**
 * The Class ComponentCutCommand represents Cut command for Component.
 * 
 * @author Bitwise
 */
public class ComponentCutCommand extends Command {
	private ArrayList<Object> list = new ArrayList<Object>();
	private boolean wasRemoved;
	private final List<Link> sourceConnections = new ArrayList<Link>();
	private final List<Link> targetConnections = new ArrayList<Link>();

	private void deleteConnections(Component component) {
		sourceConnections.addAll(component.getSourceConnections());
		for (int i = 0; i < sourceConnections.size(); i++) {
			Link link = sourceConnections.get(i);
			link.detachSource();
			link.detachTarget();
			if (link.getSource() != null)
				link.getSource().freeOutputPort(link.getSourceTerminal());
			if (link.getTarget() != null)
				link.getTarget().freeInputPort(link.getTargetTerminal());
		}

		targetConnections.addAll(component.getTargetConnections());
		for (int i = 0; i < targetConnections.size(); i++) {
			Link link = targetConnections.get(i);
			link.detachSource();
			link.detachTarget();
			if (link.getSource() != null)
				link.getSource().freeOutputPort(link.getSourceTerminal());
			if (link.getTarget() != null)
				link.getTarget().freeInputPort(link.getTargetTerminal());
		}
	}

	private void restoreConnections() {
		for (int i = 0; i < sourceConnections.size(); i++) {
			Link link = sourceConnections.get(i);
			link.attachSource();
			link.getSource().engageOutputPort(link.getSourceTerminal());
			link.attachTarget();
			link.getTarget().engageInputPort(link.getTargetTerminal());
		}
		sourceConnections.clear();
		for (int i = 0; i < targetConnections.size(); i++) {
			Link link = targetConnections.get(i);
			link.attachSource();
			link.getSource().engageOutputPort(link.getSourceTerminal());
			link.attachTarget();
			link.getTarget().engageInputPort(link.getTargetTerminal());
		}
		targetConnections.clear();
	}

	@Override
	public boolean canUndo() {
		return wasRemoved;
	}

	/**
	 * Adds the element.
	 * 
	 * @param node
	 *            the node
	 * @return true, if successful
	 */

	public boolean addElement(Object node) {
		if (!list.contains(node)) {

			return list.add(node);
		}
		return false;
	}

	@Override
	public void undo() {
		Iterator<Object> it = list.iterator();
		while (it.hasNext()) {
			Object object = it.next();
			if(object instanceof Component){
			Component node = (Component) object;
			node.getParent().addChild(node);
			restoreConnections();
			}
			else if(object instanceof CommentBox){
				CommentBox node = (CommentBox)object;
				node.getParent().addChild(node);
			}
		}

	}

	@Override
	public boolean canExecute() {
		if (list == null || list.isEmpty())
			return false;
		Iterator<Object> it = list.iterator();
		while (it.hasNext()) {
			if (!isCutNode(it.next()))
				return false;
		}
		return true;
	}

	@Override
	public void redo() {
		Iterator<Object> it = list.iterator();
		while (it.hasNext()) {
			Object object = it.next();
			if(object instanceof Component){
				Component node = (Component)object;
			deleteConnections(node);
			wasRemoved = node.getParent().removeChild(node);
			}
			else if(object instanceof CommentBox){
				CommentBox node = (CommentBox)object;
				wasRemoved = node.getParent().removeChild(node);
			}
			
		}
	}

	@Override
	public void execute() {
		Clipboard.getDefault().setContents(list);
		redo();
	}

	/**
	 * Checks if is copyable node.
	 * 
	 * @param node
	 *            the node
	 * @return true, if is copyable node
	 */
	public boolean isCutNode(Object node) {
		if (node instanceof Component)
			return true;
		if (node instanceof CommentBox)
			return true;
		return false;
	}

}
