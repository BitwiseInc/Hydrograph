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

package hydrograph.ui.dataviewer.actions;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * The Class ViewDataGridMenuCreator.
 * Creates drop down menu to switch view on coolbar
 * 
 * @author Bitwise
 *
 */
public class ViewDataGridMenuCreator implements IMenuCreator{

	private ActionFactory actionFactory;

	public ViewDataGridMenuCreator(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	@Override
	public Menu getMenu(Control parent) {
		Menu menu=new Menu(parent);		
		ActionContributionItem gridViewMenuitem=new ActionContributionItem(actionFactory.getAction(GridViewAction.class.getName()));
		ActionContributionItem formattedViewMenuItem=new ActionContributionItem(actionFactory.getAction(FormattedViewAction.class.getName()));
		ActionContributionItem unformattedViewMenuItem=new ActionContributionItem(actionFactory.getAction(UnformattedViewAction.class.getName()));
		
		gridViewMenuitem.fill(menu, 0);
		formattedViewMenuItem.fill(menu,1);
		unformattedViewMenuItem.fill(menu,2);
		
		return menu;
	}

	@Override
	public Menu getMenu(Menu parent) {
		return parent;
	}

	@Override
	public void dispose() {
		//Nothing to do
	}
}