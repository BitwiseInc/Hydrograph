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

import java.util.ArrayList;

import hydrograph.ui.dataviewer.window.DebugDataViewer;

import org.eclipse.jface.action.Action;

/**
 * The Class ResetColumnsAction.
 * 
 * Responsible for resetting the columns to default in the Select Column Action.
 * 
 * @author Bitwise
 *
 */
public class ResetColumnsAction extends Action{
	
	private DebugDataViewer debugDataViewer;
	private static final String LABEL = "Reset Columns";
	private SelectColumnAction selectColumnAction;
	
	public ResetColumnsAction(DebugDataViewer debugDataViewer){
		super(LABEL);
		this.debugDataViewer = debugDataViewer;
	}
	
	@Override
	public void run() {
		selectColumnAction = ((SelectColumnAction)debugDataViewer.getActionFactory().getAction(SelectColumnAction.class.getName()));
		selectColumnAction.diposeTable();
		selectColumnAction.setSelectedColumns(new ArrayList<String>());
		selectColumnAction .setAllColumns(new ArrayList<String>());
		debugDataViewer.getDataViewerAdapter().setColumnList(new ArrayList<String>(debugDataViewer.getDataViewerAdapter().getAllColumnsMap().keySet()));
		selectColumnAction.recreateViews();
	}
}
