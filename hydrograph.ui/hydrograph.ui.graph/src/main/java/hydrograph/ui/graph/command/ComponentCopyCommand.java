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

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

/**
 * The Class ComponentCopyCommand.
 */
public class ComponentCopyCommand extends Command {
   
	private ArrayList<Object> list = new ArrayList<Object>();
   
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
	public boolean canExecute() {
		if (list == null || list.isEmpty())
			return false;
		Iterator<Object> it = list.iterator();
		while (it.hasNext()) {
			if (!isCopyableNode(it.next()))
				return false;
		}
		return true;
	}

	@Override
	public void execute() {
		if (canExecute()){
			Clipboard.getDefault().setContents(list);
		}
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	/**
	 * Checks if is copyable node.
	 * 
	 * @param node
	 *            the node
	 * @return true, if is copyable node
	 */
	public boolean isCopyableNode(Object node) {
		if (node instanceof Object)
			return true;
		return false;
	}

}
