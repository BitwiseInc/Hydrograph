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

import hydrograph.ui.datastructure.property.PropertyField;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ELTSelectionTaskListener;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.listeners.grid.ELTGridDetails;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;


/**
 * The listener interface for receiving ELTGridDeleteSelection events. The class that is interested in processing a
 * ELTGridDeleteSelection event implements this interface, and the object created with that class is registered with a
 * component using the component's <code>addELTGridDeleteSelectionListener<code> method. When
 * the ELTGridDeleteSelection event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ELTGridDeleteSelectionEvent
 */
public class ELTTransformDeleteSelectionListener extends ELTSelectionTaskListener{

	@Override
	public void selectionListenerAction(
			PropertyDialogButtonBar propertyDialogButtonBar,
			ListenerHelper helpers, Widget... widgets) {

		ELTGridDetails gridDetails = (ELTGridDetails) helpers.get(HelperType.SCHEMA_GRID);
		Table table =gridDetails.getTableViewer().getTable();
		int temp = table.getSelectionIndex();
		int[] indexs=table.getSelectionIndices();
		if (temp == -1) {
			WidgetUtility.errorMessage("Please Select row to delete");
		} else {
			table.remove(indexs);
			ArrayList tempList= new ArrayList();
			for (int index :indexs) { 
//				PropertyField test =(PropertyField) gridDetails.getGrids().get(index);
//				tempList.add(test);
			}
			 gridDetails.getGrids().removeAll(tempList); 
			
		}		
	}

	
}
