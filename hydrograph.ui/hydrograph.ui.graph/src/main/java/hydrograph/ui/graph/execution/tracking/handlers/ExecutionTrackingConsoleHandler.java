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

import hydrograph.ui.graph.execution.tracking.utils.ExecutionTrackingConsoleUtils;
import hydrograph.ui.graph.job.RunStopButtonCommunicator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * 
 * The handler class to open Execution tracking console
 * 
 * @author Bitwise
 *
 */
public class ExecutionTrackingConsoleHandler extends AbstractHandler{
	
	public ExecutionTrackingConsoleHandler() {
		RunStopButtonCommunicator.ExecutionTrackingConsole.setHandler(this);
	}
	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {	
		ExecutionTrackingConsoleUtils.INSTANCE.openExecutionTrackingConsole();
		
		return null;
	}

	/**
	 * 
	 * Enable/Disable stop button
	 * 
	 * @param enable
	 */
	public void setExecutionTrackingConsoleEnabled(boolean enable) {
		setBaseEnabled(enable);
	}
	
}
