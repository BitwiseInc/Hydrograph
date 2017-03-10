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

import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Model;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;


// TODO: Auto-generated Javadoc
/**
 * The Class ComponentCutCommand represents Cut command for Component.
 * 
 * @author Bitwise
 */
public class SubJobCommand extends Command {
	private ArrayList list = new ArrayList();

	private boolean wasRemoved;
 


	@Override
	public boolean canUndo() {
		return false;
	}

	/**
	 * Adds the element.
	 * 
	 * @param node
	 *            the node
	 * @return true, if successful
	 */

	public boolean addElement(Component node) {
		if (!list.contains(node)) {

			return list.add(node);
		}
		return false;
	}
	
/*	public boolean addElementLink(Link link) {
		if (!links.contains(link)) {

			return links.add(link);
		}
		return false;
	}
*/

	@Override
	public void undo() {
		Iterator<Component> it = list.iterator();
		while (it.hasNext()) {
			Component node = it.next();
			node.getParent().addChild(node);

		}

	}

	@Override
	public boolean canExecute() {
		if (list == null || list.isEmpty())
			return false;
		Iterator<Component> it = list.iterator();
		while (it.hasNext()) {
			if (!isCutNode(it.next()))
				return false;
		}
		return true;
	}

	@Override
	public void redo() {
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Model node = (Model) it.next();
			if(node instanceof Component)
			wasRemoved = ((Component)node).getParent().removeChild((Component)node);
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
	 * @param node the node
	 * @return true, if is copyable node
	 */
	public boolean isCutNode(Component node) {
		if (node instanceof Component)
			return true;
		return false;
	}

}
