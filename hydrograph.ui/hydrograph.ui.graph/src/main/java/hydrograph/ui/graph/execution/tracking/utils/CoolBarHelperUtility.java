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
package hydrograph.ui.graph.execution.tracking.utils;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.WorkbenchWidgetsUtils;
import hydrograph.ui.graph.editor.ELTGraphicalEditor;

/**
 * @author Bitwise
 * Utility to enable/disable cool bar items
 *
 */
public class CoolBarHelperUtility {

	public static CoolBarHelperUtility COOLBAR_ITEMS_UTILITY = new CoolBarHelperUtility();

	/**
	 * disable or enable coolbar icons
	 * 
	 * @param enabled
	 */
	public void disableCoolBarIcons(boolean enabled) {
		ToolBarContributionItem toolBarContributionItem = WorkbenchWidgetsUtils.INSTANCE
				.getToolBarMangerOrMenuManagerFromCoolBar("hydrograph.ui.graph.toolbar1");
		for (IContributionItem contributionItem : toolBarContributionItem.getToolBarManager().getItems()) {
			ToolItem toolItem = WorkbenchWidgetsUtils.INSTANCE
					.getToolItemFromToolBarManger(toolBarContributionItem.getId(), contributionItem.getId());
			if (toolItem != null) {
				IWorkbenchPart partView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActivePart();
				if (!((ELTGraphicalEditor) partView).getContainer().isOpenedForTracking()) {
					toolItem.getParent().setEnabled(true);
					enableSaveAs(true);
				} else {
					toolItem.getParent().setEnabled(false);
					enableSaveAs(false);
				}
				break;
			}
		}
		ToolBarContributionItem toolBarContributionItem2 = WorkbenchWidgetsUtils.INSTANCE
				.getToolBarMangerOrMenuManagerFromCoolBar(Constants.RUN_STOP_BUTTON_TOOLBAR_ID);
		for (IContributionItem contributionItem : toolBarContributionItem2.getToolBarManager().getItems()) {
			ToolItem toolItem = WorkbenchWidgetsUtils.INSTANCE
					.getToolItemFromToolBarManger(toolBarContributionItem2.getId(), contributionItem.getId());
			if (toolItem != null) {
				toolItem.getParent().setEnabled(enabled);
				break;
			}
		}
	}

	/**
	 * @param enable Edit option for tracking sub job is removed.
	 */
	@SuppressWarnings("restriction")
	public void enableSaveAs(boolean enable) {
		 IContributionItem contributionItem  = (IContributionItem) ((WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getMenuBarManager().getItems()[2];
		 contributionItem.setVisible(enable);
	}

}
