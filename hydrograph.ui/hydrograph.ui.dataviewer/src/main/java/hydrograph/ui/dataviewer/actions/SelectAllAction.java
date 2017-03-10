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

import hydrograph.ui.dataviewer.window.DebugDataViewer;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
/**
 * The Class SelectAllAction.
 * Responsible for 'select All' functionality on view data window.
 * 
 * @author Bitwise
 *
 */
public class SelectAllAction extends Action{
	
	private static final String LABEL="Select &All@Ctrl+A";
	private DebugDataViewer debugDataViewer;
	
	public SelectAllAction(DebugDataViewer debugDataViewer) {
    	super(LABEL);
    	this.debugDataViewer = debugDataViewer;
    	setAccelerator(SWT.MOD1 | 'a');
	}
	
	@Override
	public void run() {
		if (debugDataViewer.getUnformattedViewTextarea()!=null && debugDataViewer.getUnformattedViewTextarea().isVisible()){
			debugDataViewer.getUnformattedViewTextarea().selectAll();
		}else if (debugDataViewer.getFormattedViewTextarea()!=null && debugDataViewer.getFormattedViewTextarea().isVisible()){
			debugDataViewer.getFormattedViewTextarea().selectAll();
		}else{
			debugDataViewer.selectAllCells();
		}
	}
}
