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

package hydrograph.ui.propertywindow.widgets.listeners;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.listeners.grid.ELTGridDetails;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;

/**
 * The class ELTShortcutKeyGridListener listens the Schema grid. It attaches the KeyListener to Schema grid and 
 * processes the key pressed events. Basically, it facilitates the keyboard shortcuts for functionality like add 
 * row and delete row.
 * 
 * @see ELTSelectionTaskListener
 */

public class ELTShortcutKeyGridListener extends ELTSelectionTaskListener {
	private Table table;
	private int rowSequence=0;
	private boolean ctrlKeyPressed = false;
	private boolean isKeyListenerAttached = false;
	
	@Override
	public int getListenerType() {
      return SWT.Selection;
	}
	
	@Override
	public void selectionListenerAction(final PropertyDialogButtonBar propertyDialogButtonBar, final ListenerHelper helpers, Widget... widget) {
		
		ELTGridDetails gridDetails = (ELTGridDetails) helpers.get(HelperType.SCHEMA_GRID);		
		final TableViewer tableViewer = gridDetails.getTableViewer();
		final int columns = tableViewer.getCellEditors().length;
		final Widget[] widgets = widget;		
		
		table=(Table)widgets[0];
				
		if (!isKeyListenerAttached){
			for (int i =0; i < columns; i++){
				attachShortcutKeyListener(tableViewer.getCellEditors()[i].getControl(), propertyDialogButtonBar, helpers, widgets);
			}				
			attachShortcutKeyListener(table, propertyDialogButtonBar, helpers, widgets);
		}
	}
		
	private void attachShortcutKeyListener(Control currentControl, final PropertyDialogButtonBar propertyDialogButtonBar, final ListenerHelper helpers, final Widget[] widgets) {
		
		isKeyListenerAttached = true;
		currentControl.addKeyListener(new KeyListener() {
		@Override				
		public void keyReleased(KeyEvent event) {				
			if(event.keyCode == SWT.CTRL || event.keyCode == SWT.COMMAND){					
				ctrlKeyPressed = false;
			}
				
		}
			
		@Override
		public void keyPressed(KeyEvent event) {
			if(event.keyCode == SWT.CTRL || event.keyCode == SWT.COMMAND){					
				ctrlKeyPressed = true;
			}
			
			if (ctrlKeyPressed && event.keyCode == Constants.KEY_N){
				propertyDialogButtonBar.enableApplyButton(true);							
				table.getParent().getShell().setFocus();
				((Button) widgets[1]).setEnabled(true);
				ELTGridDetails eltGridDetails = (ELTGridDetails)helpers.get(HelperType.SCHEMA_GRID);
				if (eltGridDetails.getGrids().size() >= 1) {
					((Button) widgets[2]).setEnabled(true);
					((Button) widgets[3]).setEnabled(true);
				}
				GridWidgetCommonBuilder gridCommonBuilder = eltGridDetails.getGridWidgetCommonBuilder();
				gridCommonBuilder.setRowSequence(rowSequence);
				gridCommonBuilder.createDefaultSchema(eltGridDetails.getGrids(), eltGridDetails.getTableViewer(), eltGridDetails.getLabel());
				rowSequence++;
				((Component)helpers.get(HelperType.COMPONENT)).setLatestChangesInSchema(true);
			}
				
			else if (ctrlKeyPressed && event.keyCode == Constants.KEY_D){
				propertyDialogButtonBar.enableApplyButton(true);
				ELTGridDetails gridDetails = (ELTGridDetails) helpers.get(HelperType.SCHEMA_GRID);					
				TableViewer tableViewer = gridDetails.getTableViewer();
				Table table = tableViewer.getTable();
				int[] indexes=table.getSelectionIndices();
				if (table.getSelectionIndex() == -1) {
					WidgetUtility.errorMessage("Please Select row to delete");
				} else {
					table.remove(indexes);
					List listOfItemsToRemove= new ArrayList();
					for (int index : indexes) { 
						listOfItemsToRemove.add(gridDetails.getGrids().get(index));
					}
				List<GridRow> gridsList = gridDetails.getGrids();
				gridsList.removeAll(listOfItemsToRemove);						
						
				//highlight after deletion
				if(indexes.length == 1 && gridsList.size() > 0){//only one item is deleted
					if(gridsList.size() == 1){//list contains only one element
						table.select(0);// select the first element
						tableViewer.editElement(tableViewer.getElementAt(0), 0);
					}
					else if(gridsList.size() == indexes[0]){//deleted last item 
						table.select(gridsList.size() - 1);//select the last element which now at the end of the list
						tableViewer.editElement(tableViewer.getElementAt(gridsList.size() - 1), 0);
					}
					else if(gridsList.size() > indexes[0]){//deleted element from middle of the list
						table.select( indexes[0] == 0 ? 0 : (indexes[0] - 1) );//select the element from at the previous location
						tableViewer.editElement(tableViewer.getElementAt(indexes[0] == 0 ? 0 : (indexes[0] - 1)), 0);
					}
				}
				else if(indexes.length >= 2){//multiple items are selected for deletion
					if(indexes[0] == 0){//delete from 0 to ...
						if(gridsList.size() >= 1){//list contains only one element
							table.select(0);//select the remaining element
							tableViewer.editElement(tableViewer.getElementAt(0), 0);
						}
					}
					else{//delete started from element other than 0th element
						table.select((indexes[0])-1);//select element before the start of selection   
						tableViewer.editElement(tableViewer.getElementAt((indexes[0])-1), 0);
					}
				}
			}
					
				if (gridDetails.getGrids().size() >= 1) {
					((Button) widgets[1]).setEnabled(true);
				} else {
					((Button) widgets[1]).setEnabled(false);
				}
					
				if (gridDetails.getGrids().size() >= 2) {
					((Button) widgets[2]).setEnabled(true);
					((Button) widgets[3]).setEnabled(true);
				} else {
					((Button) widgets[2]).setEnabled(false);
					((Button) widgets[3]).setEnabled(false);
				}
					((Component)helpers.get(HelperType.COMPONENT)).setLatestChangesInSchema(true);
				}
			}
		});	
	}
	
}	
