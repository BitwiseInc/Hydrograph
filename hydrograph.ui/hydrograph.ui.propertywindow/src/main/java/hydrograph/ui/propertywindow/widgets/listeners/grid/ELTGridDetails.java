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

 
package hydrograph.ui.propertywindow.widgets.listeners.grid;

import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Label;



/**
 * The Class ELTGridDetails.
 * 
 * @author Bitwise
 */
public class ELTGridDetails {

	private List<GridRow> grids;
	private TableViewer tableViewer;
	private Label label;
	private GridWidgetCommonBuilder gridWidgetCommonBuilder;
	
	/**
	 * Instantiates a new ELT grid details.
	 * 
	 * @param grids
	 *            the grids
	 * @param tableViewer
	 *            the table viewer
	 * @param label
	 *            the label
	 * @param gridWidgetCommonBuilder
	 *            the grid widget common builder
	 */
	public ELTGridDetails(List<GridRow> grids, TableViewer tableViewer,
			Label label,GridWidgetCommonBuilder gridWidgetCommonBuilder) {
		super();
		this.grids = grids;
		this.tableViewer = tableViewer;
		this.label = label;
		this.gridWidgetCommonBuilder=gridWidgetCommonBuilder;
	}
	
	public List<GridRow> getGrids() {
		return grids;
	}

	public void setGrids(List<GridRow> grids) {
		this.grids = grids;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public GridWidgetCommonBuilder getGridWidgetCommonBuilder() {
		return gridWidgetCommonBuilder;
	}

	public void setGridWidgetCommonBuilder(
			GridWidgetCommonBuilder gridWidgetCommonBuilder) {
		this.gridWidgetCommonBuilder = gridWidgetCommonBuilder;
	}

	

}
