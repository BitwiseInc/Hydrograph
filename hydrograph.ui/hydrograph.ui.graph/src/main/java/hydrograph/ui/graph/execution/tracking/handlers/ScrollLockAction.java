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

import org.eclipse.jface.action.Action;

import hydrograph.ui.graph.execution.tracking.windows.ExecutionTrackingConsole;

/**
 * The Class ScrollLockAction
 * @author Bitwise
 *
 */
public class ScrollLockAction extends Action {
	
	/** The execution tracking console. */
	private ExecutionTrackingConsole executionTrackingConsole;
	
	/** The Constant LABEL. */
	private static final String LABEL="&Scroll Lock";
	
	
	/**
	 * Instantiates scroll lock action.
	 * @param executionTrackingConsole
	 */
	public ScrollLockAction(ExecutionTrackingConsole executionTrackingConsole) {
		super(LABEL);
		this.executionTrackingConsole = executionTrackingConsole;
		setChecked(false);
	}
	
	/* (non-Javadoc))
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		if(isChecked()){
			setChecked(true);
			executionTrackingConsole.lockScrollBar(true);
		}else{
			setChecked(false);
			executionTrackingConsole.lockScrollBar(false);
		}
		
		super.run();
		
	}
}
