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

 
package hydrograph.ui.project.structure.console;

import hydrograph.ui.common.interfaces.console.IHydrographConsole;
import hydrograph.ui.common.interfaces.parametergrid.DefaultGEFCanvas;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.internal.console.ConsoleView;


/**
 * 
 * Hydrograph's custom console view
 * 
 * @author Bitwise
 * 
 */

public class HydrographConsole extends ConsoleView implements IHydrographConsole {

	private boolean consoleClosed;
	private static String DEFAULT_CONSOLE = "NewConsole";

	@Override
	public void partActivated(IWorkbenchPart part) {
		super.partActivated(part);
		consoleClosed = true;
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		super.partDeactivated(part);
		consoleClosed = false;
	}

	@Override
	protected void partVisible(IWorkbenchPart part) {
		super.partVisible(part);
		consoleClosed = false;
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		super.partClosed(part);
		consoleClosed = true;
	}

	@Override
	public boolean isConsoleClosed() {
		return consoleClosed;
	}

	private DefaultGEFCanvas getComponentCanvas() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof DefaultGEFCanvas)
			return (DefaultGEFCanvas) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
		else
			return null;
	}

	private IConsole getConsole(String consoleName, IConsoleManager conMan) {
		IConsole[] existing = conMan.getConsoles();
		MessageConsole messageConsole = null;
		for (int i = 0; i < existing.length; i++) {
			if (existing[i].getName().equals(consoleName)) {
				messageConsole = (MessageConsole) existing[i];

				return messageConsole;
			}
		}
		return null;
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		super.partOpened(part);

		if (getComponentCanvas() != null) {
			ConsolePlugin plugin = ConsolePlugin.getDefault();
			IConsoleManager conMan = plugin.getConsoleManager();

			String consoleName = getComponentCanvas().getActiveProject() + "." + getComponentCanvas().getJobName();

			IConsole consoleToShow = getConsole(consoleName, conMan);

			if (consoleToShow != null) {
//				Fix for : Console window is getting displayed if user maximize canvas window and then try to create new job (Ctrl+J)
//				conMan.showConsoleView(consoleToShow);
			} else {
				addDummyConsole();
			}
		}
	}

	private void addDummyConsole() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();

		IConsole consoleToShow = getConsole(DEFAULT_CONSOLE, conMan);

		if (consoleToShow == null) {
			consoleToShow = createNewMessageConsole(DEFAULT_CONSOLE, conMan);
		}
//		Fix for : Console window is getting displayed if user maximize canvas window and then try to create new job (Ctrl+J)
//		conMan.showConsoleView(consoleToShow);
	}

	private MessageConsole createNewMessageConsole(String consoleName, IConsoleManager conMan) {
		MessageConsole messageConsole;
		messageConsole = new MessageConsole(consoleName, null);
		conMan.addConsoles(new IConsole[] { messageConsole });
		return messageConsole;
	}
}
