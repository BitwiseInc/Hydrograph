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

 package hydrograph.ui.propertywindow.widgets.dialog.hiveInput;

import hydrograph.ui.datastructure.property.InputHivePartitionColumn;
import hydrograph.ui.propertywindow.messages.Messages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
/**
 * Hive field dialog helper class.
 * @author Bitwise
 *
 */
public class HiveFieldDialogHelper {

public static final HiveFieldDialogHelper INSTANCE = new HiveFieldDialogHelper();	


private HiveFieldDialogHelper(){
	
}
/**
 * Arranges key Values data 
 * @param rowFields
 * @param columnNameList
 * @return
 */
public InputHivePartitionColumn arrangeColumndata(List<String> rowFields, List<String> columnNameList){
	
	InputHivePartitionColumn hivePartitionColumn=null;
	
	
	if(!columnNameList.isEmpty()&& null!=columnNameList.get(0)){
		hivePartitionColumn = new InputHivePartitionColumn();  
		hivePartitionColumn.setName(columnNameList.get(0));
		hivePartitionColumn.setValue(rowFields.get(0));
		
		columnNameList.remove(0);
		rowFields.remove(0);
		
		hivePartitionColumn.setInputHivePartitionColumn(arrangeColumndata(rowFields,columnNameList));
				
	}
		  
	return hivePartitionColumn;
	
	
}

/**
 * Retrieve Saved data to render.
 * @param hivePartitionColumnsl
 * @param fieldNameList
 * @return
 */
public List<HivePartitionFields> getSavedColumnData(List<InputHivePartitionColumn> incomingList,List<String> fieldNameList){
	
	List<HivePartitionFields> fieldsDialogList = new ArrayList<>();
	
	for (InputHivePartitionColumn inputHivePartitionColumn : incomingList) {
		
		HivePartitionFields tempObj = new HivePartitionFields();
		
		tempObj.setRowFields(extractListFromObject
				(inputHivePartitionColumn,new ArrayList<String>(),fieldNameList));
		fieldsDialogList.add(tempObj);
		
	}
	
	checkAndAdjustRowsColumns(fieldsDialogList,fieldNameList,incomingList);
	
	return fieldsDialogList;
}



/**
 * Refreshes key values colunm and its data
 * @param propertyList
 * @param keyValues
 * @param isKeyDeleted 
 */
public List<HivePartitionFields> refreshKeyColumnsAndValues(List<String> propertyList,List<HivePartitionFields> keyValues,List<String> colNames, boolean isKeyDeleted) {
	if(isKeyDeleted){
		for (String deltedColumn : checkIfNewColumnAddedOrDeleted(new ArrayList<>(propertyList),colNames)) {
			for(HivePartitionFields tempRows:keyValues){
				tempRows.getRowFields().remove(colNames.indexOf(deltedColumn));
			}
		}	
	}else{
		
		for (int i=0; i<checkIfNewColumnAddedOrDeleted(colNames,new ArrayList<>(propertyList)).size();i++) {
			for(HivePartitionFields tempRows:keyValues){
				tempRows.getRowFields().add("");
			}
		}
		
	}
	return keyValues;
}

/**
 * 
 * @param keyValues
 * @param keyList
 * @param incomingList2 
 */
private void checkAndAdjustRowsColumns(List<HivePartitionFields> keyValues,List<String> keyList, List<InputHivePartitionColumn> incomingList)
{
	Set<String> colNames= new HashSet<>();
	for (int i=0; i < incomingList.size();i++) {
		
		colNames=extractColumnNamesFromObject(incomingList.get(i), colNames);
		 
		List<String> notAvailableFields = ListUtils.subtract(keyList,new ArrayList<>(colNames));   
		
	    if(null!=notAvailableFields&&notAvailableFields.size()>0){
	     for(String fieldName:notAvailableFields){
	    	 
	    	keyValues.get(i).getRowFields().add(keyList.indexOf(fieldName), "");
	     }
		}
	     colNames.clear();
	}

}

/**
 * 
 * @param columnNames
 * @param keyList
 * @return
 */
private List<String> checkIfNewColumnAddedOrDeleted(List<String> columnNames,List<String> keyList) {
	
	if(null!=columnNames&&!columnNames.isEmpty()&&!keyList.isEmpty()){
	
	return ListUtils.subtract(keyList, columnNames);
	
	}
	
	return new ArrayList<String>();

}
/**
 * 
 * @param hivePartitionColumn
 * @param temp
 * @param keyList
 * @return
 */
private List<String> extractListFromObject(InputHivePartitionColumn hivePartitionColumn,List<String> temp, final List<String> keyList){
	
	for (String colName : keyList) {
		if (colName.equalsIgnoreCase(hivePartitionColumn.getName())) {

			temp.add(hivePartitionColumn.getValue());
			
		}
	}  
	
	if(null!=hivePartitionColumn.getInputHivePartitionColumn()){
		extractListFromObject(hivePartitionColumn.getInputHivePartitionColumn(),temp, keyList);
	}
	
	return temp;
	
}

/**
 * 
 * @param hivePartitionColumn
 * @param colNames
 * @return
 */
private Set<String> extractColumnNamesFromObject(InputHivePartitionColumn hivePartitionColumn,Set<String> colNames){
	
	colNames.add(hivePartitionColumn.getName());
	
	if(null!=hivePartitionColumn.getInputHivePartitionColumn()){
		
		extractColumnNamesFromObject(hivePartitionColumn.getInputHivePartitionColumn(),colNames);
	}
	
	return colNames;
}


/**
 * Disposes all columns in table
 * @param tableViewer
 */
public void disposeAllColumns(TableViewer tableViewer ,List<HivePartitionFields> fieldsDialogList){
	fieldsDialogList.clear();
	tableViewer.refresh();
	
	TableColumn[] columns = tableViewer.getTable().getColumns();
	for (TableColumn tc : columns) {
		tc.dispose();
	}
	tableViewer.getTable().redraw();
	}

/**
 * 
 * Compares available fields and selected partition key fields for 
 *   hive input and output components. 
 * 
 */
public boolean compare_fields(TableItem[] items,List<String> sourceFieldsList)
{
	ListIterator<String> t_itr,s_itr;
	boolean is_equal=true;
	
	List<String> target_fields = new ArrayList<String>();
	if(items.length > 0){
		for (TableItem tableItem : items){
			target_fields.add((String) tableItem.getText());
		}
	
	
	List<String> source_field = new ArrayList<String>(sourceFieldsList);
	
	t_itr=target_fields.listIterator(target_fields.size());
	s_itr = source_field.listIterator(source_field.size());
	
	
	while(t_itr.hasPrevious() & s_itr.hasPrevious()){
		if(StringUtils.equals(s_itr.previous(),t_itr.previous())){
			is_equal=true;
		}
		else{
			is_equal=false;
			break;
		}
	}
	}
	return is_equal;
	
}

/**
 * 
 * Message dialog to be displayed if compare_fields() method returns false. 
 * 
 */
public int Message_Dialog()
{
	
	
		MessageDialog dialog = new MessageDialog(Display.getCurrent().getActiveShell(), "Rearrange Fields", null,
		    Messages.HIVE_PARTI_SEQ_ERROR, MessageDialog.ERROR, new String[] { "Rearrange Schema",
		  "Rearrange Partition Fields" }, 0);
		int result = dialog.open();
		return result;
}

/**
 * 
 * Change color of selected partition key fields to red if compare_fields() method returns false.
 * 
 */
public boolean compareAndChangeColor(TableViewer tableViewer,List<String> sourceFieldsList){
	boolean check_field=compare_fields(tableViewer.getTable().getItems(),sourceFieldsList);
	if(!check_field){
		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		for (TableItem tableItem : tableViewer.getTable().getItems()){
			tableItem.setForeground(color);
		}
	}else{
		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		for (TableItem tableItem : tableViewer.getTable().getItems()){
			tableItem.setForeground(color);
		}
		
	}
	tableViewer.refresh();
	return check_field;
}


}
