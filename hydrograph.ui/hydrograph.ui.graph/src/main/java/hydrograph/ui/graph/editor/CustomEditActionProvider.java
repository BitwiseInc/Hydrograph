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
package hydrograph.ui.graph.editor;

import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.navigator.resources.actions.EditActionProvider;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;

import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * CustomEditActionProvider to add custom paste action in the context menu of project explorer.
 * 
 * @author Bitwise
 *
 */
public class CustomEditActionProvider extends EditActionProvider {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(CustomEditActionProvider.class);
	private static final String COPY_ACTION_ID="org.eclipse.ui.CopyAction";
	private static final String PASTE_ACTION_ID="org.eclipse.ui.PasteAction";
	private static final String PASTE_COMMAND_ID="org.eclipse.ui.edit.paste";
	private static final String MENU_PLUGIN_NAME="hydrograph.ui.menus";
	private static final String PASTE_ACTION_TEXT="&Paste@Ctrl+V";
	private static final String PARAM="param";

	@SuppressWarnings("restriction")
	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);

		ActionContributionItem pasteContribution = getPasteContribution(menu.getItems());
		menu.remove(pasteContribution);
		IAction pasteAction = new Action(PASTE_ACTION_TEXT) {
			@Override
			public void run() {
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench()
						.getService(IHandlerService.class);
				try {
					JobCopyParticipant.setCopiedFileList(new ArrayList<>());
					handlerService.executeCommand(PASTE_COMMAND_ID, null);
				} catch (Exception exception) {
					logger.warn("Error while pasting job files :: {}",exception.getMessage());
				}
			}
		};
		pasteAction.setAccelerator(SWT.MOD1 | 'v');
		Bundle bundle = Platform.getBundle(MENU_PLUGIN_NAME);
		URL imagePath = BundleUtility.find(bundle,ImagePathConstant.PASTE_IMAGE_PATH.getValue());
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(imagePath);
		pasteAction.setImageDescriptor(imageDescriptor);
		menu.insertAfter(COPY_ACTION_ID, pasteAction);
	}

	private ActionContributionItem getPasteContribution(IContributionItem[] items) {
		for (IContributionItem contributionItem : items) {
			if (StringUtils.equals(contributionItem.getId(),PASTE_ACTION_ID)) {
				return (ActionContributionItem) contributionItem;
			}
		}
		return null;
	}

}
