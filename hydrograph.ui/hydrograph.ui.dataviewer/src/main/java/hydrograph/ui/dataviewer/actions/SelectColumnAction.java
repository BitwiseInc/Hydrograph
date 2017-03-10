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
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import hydrograph.ui.dataviewer.dialog.SelectColumnActionDialog;
import hydrograph.ui.dataviewer.support.SortOrder;
import hydrograph.ui.dataviewer.utilities.DataViewerUtility;
import hydrograph.ui.dataviewer.window.DebugDataViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


/**
 * The class SelectColumnAction.
 * Action for selecting columns displayed in view data window.
 * 
 * @author Bitwise
 *
 */
public class SelectColumnAction extends Action {
	private DebugDataViewer debugDataViewer;
	private List<String> allColumns;
	private List<String> selectedColumns;
	private static final String LABEL = "Select Columns";
	private Image sortType;
	private boolean isSortingEnable;
	public SelectColumnAction(DebugDataViewer debugDataViewer) {
		super(LABEL);
		this.debugDataViewer = debugDataViewer;
		allColumns=new ArrayList<String>();
		selectedColumns= new ArrayList<String>();
	} 

	@Override
	public void run() {
		debugDataViewer.getColumnList();
		if(allColumns.size()==0 && selectedColumns.size()==0){
			allColumns.addAll(debugDataViewer.getColumnList());
		}
		isSortingEnable=false;
		SelectColumnActionDialog selectColumnActionDialog = new SelectColumnActionDialog(Display.getDefault().getActiveShell(), allColumns,selectedColumns);
		if (selectColumnActionDialog.open() != 1) {
			selectedColumns.clear();
			allColumns.clear();
			selectedColumns.addAll(selectColumnActionDialog.getSelectedColumns());
			allColumns.addAll(selectColumnActionDialog.getAllColumns());
			diposeTable();
			recreateViews();
		}
	}

	

	/**
	 * Dispose the current view
	 */
	public void diposeTable() {
		for (int index = debugDataViewer.getTableViewer().getTable().getColumns().length-1; index >= 0; index--) {
			debugDataViewer.getTableViewer().getTable().getColumns()[index].setImage(null);
			debugDataViewer.getTableViewer().getTable().getColumns()[index].dispose();
		}
		debugDataViewer.getDataViewerAdapter().setColumnList(selectedColumns);
	}

	/**
	 * Recreate views with user's input
	 */
	public void recreateViews() {
		String sortColumnsName=debugDataViewer.getSortedColumnName();
		if(SortOrder.ASC == debugDataViewer.getSortOrder()){
			sortType=debugDataViewer.getAscendingIcon();
		}
		else {
			sortType=debugDataViewer.getDescendingIcon();
		}
		TableViewer tableViewer=debugDataViewer.getTableViewer();
		debugDataViewer.createGridViewTableColumns(tableViewer);
		debugDataViewer.getDataViewLoader().reloadloadViews();
		debugDataViewer.getTableViewer().getTable().getColumn(0).pack();
		for (int index = debugDataViewer.getTableViewer().getTable().getColumns().length-1; index >= 0; index--) {
			if(debugDataViewer.getTableViewer().getTable().getColumns()[index].getText().equals(sortColumnsName)){
				debugDataViewer.getTableViewer().getTable().getColumn(index).setImage(sortType);
				debugDataViewer.setRecentlySortedColumn(debugDataViewer.getTableViewer().getTable().getColumn(index));
				isSortingEnable=true;
			}
		}
		if(!isSortingEnable){
			DataViewerUtility.INSTANCE.resetSort(debugDataViewer);
			debugDataViewer.setRecentlySortedColumn(null);
			debugDataViewer.setSortedColumnName(null);
		}
	}

	/**
	 * Get list of Selected Columns
	 * 
	 *  @return List
	 */
	public List<String> getSelectedColumns() {
		return selectedColumns;
	}
	
	/**
	 * 
	 * Set new column list
	 * 
	 * @param allColumns
	 */
	public void setAllColumns(List<String> allColumns) {
		this.allColumns = allColumns;
	}

	/**
	 * 
	 * Set newly selected columns
	 * 
	 * @param selectedColumns
	 */
	public void setSelectedColumns(List<String> selectedColumns) {
		this.selectedColumns = selectedColumns;
	}
}
