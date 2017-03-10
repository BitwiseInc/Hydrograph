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

import hydrograph.ui.logging.factory.LogFactory;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.slf4j.Logger;


/**
 * The Class ConsoleHandler.
 * <p>
 * Creates Console Handler
 * 
 * @author Bitwise
 */
public class ConsoleHandler extends AbstractHandler implements IHandler {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ConsoleHandler.class);
	private static final String consoleView = "hydrograph.ui.project.structure.console.HydrographConsole";
	
	/**
	 * open console view
	 * @param event
	 * @return Object
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(consoleView);
		} catch (PartInitException e) {
			logger.error("Failed to show view : ", e);
		}
		return null;
	}

}
