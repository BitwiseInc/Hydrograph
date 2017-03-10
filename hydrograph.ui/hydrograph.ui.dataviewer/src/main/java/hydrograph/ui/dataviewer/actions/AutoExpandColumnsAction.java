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
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 * Action to auto expand grid view columns
 * 
 * @author Bitwise
 *
 */
public class AutoExpandColumnsAction extends Action {
	private static final String LABEL="Auto E&xapand Columns@Ctrl+X";
	private DebugDataViewer debugDataViewer;
	
	public AutoExpandColumnsAction(DebugDataViewer debugDataViewer){
		super(LABEL);
		setAccelerator(SWT.MOD1 | 'x');
		this.debugDataViewer = debugDataViewer;
	}
	
	
	@Override
	public void run() {
		for(TableColumn tableColumn : this.debugDataViewer.getTableViewer().getTable().getColumns()){
			tableColumn.pack();
		}
		
	}
}
