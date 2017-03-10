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

 
package hydrograph.ui.propertywindow.widgets.listeners.grid.transform;

import hydrograph.ui.datastructure.property.OperationField;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;


/**
 * The listener interface for receiving ELTGridAddSelection events. The class that is interested in processing a
 * ELTGridAddSelection event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addELTGridAddSelectionListener<code> method. When
 * the ELTGridAddSelection event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTGridAddSelectionEvent
 */
public class ELTTransforAddSelectionListener extends GridWidgetCommonBuilder {

	@Override
	public void createDefaultSchema(List grids, TableViewer tableViewer,
			Label errorLabel) {
		OperationField operationField = new OperationField();
		operationField.setName("");
 		if(!grids.contains(operationField)){
			grids.add(operationField);  
			tableViewer.setInput(grids);
			tableViewer.refresh();
			tableViewer.editElement(tableViewer.getElementAt(grids.size() == 0 ? grids.size() : grids.size() - 1), 0);
		}	 
	} 

	@Override
	public CellEditor[] createCellEditorList(Table table,
			Map<String, Integer> columns) {
		return null;
	}


}
