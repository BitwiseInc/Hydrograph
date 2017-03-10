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
package hydrograph.ui.menus.handlers;

import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jdt.ui.actions.OpenNewClassWizardAction;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import hydrograph.ui.menus.messages.Messages;

/**
 * The Class NewCustomClassHandler.
 * <p>
 * Handler for creating new custom classes for Filter, Aggregate, Transform, Cumulate and Normalize components.
 * 
 * 
 * @author Bitwise
 */
public class NewCustomClassHandler extends AbstractHandler implements IHandler {

	private Logger logger = LogFactory.INSTANCE.getLogger(NewCustomClassHandler.class);
	private static final String FILTER_CLASS = "hydrograph.ui.menus.new.newFilterClass";
	private static final String AGGREGATOR_CLASS = "hydrograph.ui.menus.new.newAggClass";
	private static final String TRANFORM_CLASS = "hydrograph.ui.menus.new.newTranformClass";
	private static final String CUMULATE_CLASS = "hydrograph.ui.menus.new.newCumulateClass";
	private static final String NORMALISE_CLASS = "hydrograph.ui.menus.new.newNormalizeClass";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		try {
			OpenNewClassWizardAction wizard = new OpenNewClassWizardAction();
			wizard.setOpenEditorOnFinish(false);
			final NewClassWizardPage page = new NewClassWizardPage();
			page.setSuperClass("java.lang.Object", true);

			List<String> interfaceList = new ArrayList<String>();

			String ClassType = event.getCommand().getId();

			if (ClassType.equalsIgnoreCase(FILTER_CLASS)) {
				interfaceList.add(Messages.INF_FILTER_BASE_CLASS);
			} else if (ClassType.equalsIgnoreCase(AGGREGATOR_CLASS)) {
				interfaceList.add(Messages.INF_AGGREGATOR_BASE_CLASS);
			} else if (ClassType.equalsIgnoreCase(TRANFORM_CLASS)) {
				interfaceList.add(Messages.INF_TRANFORM_BASE_CLASS);
			}else if (ClassType.equalsIgnoreCase(CUMULATE_CLASS)) {
				interfaceList.add(Messages.INF_CUMULATE_BASE_CLASS);
			}else if (ClassType.equalsIgnoreCase(NORMALISE_CLASS)) {
				interfaceList.add(Messages.INF_NORMALIZE_BASE_CLASS);
			}

			page.setMethodStubSelection(false, false,true, true);
			page.setSuperInterfaces(interfaceList, false);

			wizard.setConfiguredWizardPage(page);
			if (OSValidator.isMac()) {
				Display.getDefault().timerExec(0, new Runnable() {

					@Override
					public void run() {
						page.getControl().forceFocus();
					}
				});
			}
			wizard.run();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

}
