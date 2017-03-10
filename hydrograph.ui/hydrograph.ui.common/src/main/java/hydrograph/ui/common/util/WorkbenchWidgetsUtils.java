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

package hydrograph.ui.common.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.ui.workbench.renderers.swt.HandledContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * This class is used for fetching SWT widgets of respective
 * workbench components like ToolBar, ToolItem, MenuManager etc
 * 
 * @author Bitwise
 *
 */
@SuppressWarnings("restriction")
public class WorkbenchWidgetsUtils {

	public static final WorkbenchWidgetsUtils INSTANCE = new WorkbenchWidgetsUtils();

	private WorkbenchWidgetsUtils() {
	}

	/**
	 * Returns tool-bar instance of given id
	 * @param toolBarId
	 * 		toolBar -  Id
	 * @return
	 * 		toolBar with given toolBar-id 
	 */	
	public ToolBarContributionItem getToolBarMangerOrMenuManagerFromCoolBar(String toolBarId) {
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IContributionItem[] contributionItems = ((WorkbenchWindow) workbenchWindow).getActionBars().getCoolBarManager()
				.getItems();
		for (IContributionItem contributionItem : contributionItems) {
			if (contributionItem instanceof ToolBarContributionItem
					&& StringUtils.equals(contributionItem.getId(), toolBarId)) {
				return (ToolBarContributionItem) contributionItem;
			}
		}
		return null;
	}

	/**
	 * Returns tool-item of tool-bar
	 * 
	 * @param toolBarId
	 * @param toolItemId
	 * @return
	 * 		ToolItem of given id.
	 */
	public ToolItem getToolItemFromToolBarManger(String toolBarId, String toolItemId) {
		ToolBarContributionItem toolBarContributionItem = getToolBarMangerOrMenuManagerFromCoolBar(toolBarId);
		if (toolBarContributionItem != null && toolBarContributionItem.getToolBarManager() != null) {
			for (IContributionItem contributionItem : toolBarContributionItem.getToolBarManager().getItems()) {
				if (contributionItem instanceof HandledContributionItem
						&& StringUtils.equals(toolItemId, contributionItem.getId())) {
					ToolItem item = (ToolItem) ((HandledContributionItem) contributionItem).getWidget();
					return item;
				}
			}
		}
		return null;
	}
}
