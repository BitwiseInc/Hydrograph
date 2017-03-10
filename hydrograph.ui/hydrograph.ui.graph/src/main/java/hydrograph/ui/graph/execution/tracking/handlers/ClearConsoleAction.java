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

package hydrograph.ui.graph.execution.tracking.handlers;

import hydrograph.ui.graph.execution.tracking.windows.ExecutionTrackingConsole;

import org.eclipse.jface.action.Action;

/**
 * The Class ClearConsoleAction.
 */
public class ClearConsoleAction extends Action{
	
	/** The execution tracking console. */
	private ExecutionTrackingConsole executionTrackingConsole;
	
	/** The Constant LABEL. */
	private static final String LABEL="&Clear Console";
	
	/**
	 * Instantiates a new clear console action.
	 *
	 * @param executionTrackingConsole the execution tracking console
	 */
	public ClearConsoleAction(ExecutionTrackingConsole executionTrackingConsole){
		super(LABEL);
		this.executionTrackingConsole = executionTrackingConsole;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		executionTrackingConsole.clearConsole();
		super.run();
	}
}
