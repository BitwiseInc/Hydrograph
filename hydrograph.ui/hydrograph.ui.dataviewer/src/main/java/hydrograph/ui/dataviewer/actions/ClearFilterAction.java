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

import hydrograph.ui.dataviewer.filter.FilterConditions;
import hydrograph.ui.dataviewer.window.DebugDataViewer;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.Action;

/**
 * Action class for Clear Filter Menu Item
 * This will clear all the filters and pulls the original data from server
 * @author Bitwise
 *
 */
public class ClearFilterAction extends Action {

	private DebugDataViewer debugDataViewer;
	private static final String LABEL = "Clear Filter";
	
	public ClearFilterAction(DebugDataViewer debugDataViewer) {
		super(LABEL);
		this.debugDataViewer = debugDataViewer;
	}
	
	@Override
	public void run() {
		debugDataViewer.setLocalCondition("");
		debugDataViewer.setRemoteCondition("");
		debugDataViewer.setConditions(new FilterConditions());
		debugDataViewer.getDataViewerAdapter().setFilterCondition("");
		((ReloadAction)debugDataViewer.getActionFactory().getAction(ReloadAction.class.getName())).setIfFilterReset(true);
		((ReloadAction)debugDataViewer.getActionFactory().getAction(ReloadAction.class.getName())).run();
		debugDataViewer.enableDisableFilter();
		
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if(StringUtils.isEmpty(debugDataViewer.getLocalCondition()) && StringUtils.isEmpty(debugDataViewer.getRemoteCondition())){
			super.setEnabled(false);
		}
		else{
			super.setEnabled(true);
		}
	}
	
}
