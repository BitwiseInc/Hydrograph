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

 
package hydrograph.ui.propertywindow.widgets.utility;

import hydrograph.ui.common.util.SchemaFieldUtil;
import hydrograph.ui.propertywindow.custom.celleditor.CustomComboBoxCellEditor;
import hydrograph.ui.propertywindow.messages.Messages;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;



/**
 * The Class GridWidgetCommonBuilder.
 * 
 * @author Bitwise
 */
public abstract class GridWidgetCommonBuilder {
	
	private int rowSequence=0; 
	
	protected int getRowSequence() {
		return rowSequence;
	}

	public void setRowSequence(int rowSequence) {
		this.rowSequence = rowSequence;
	}

	/**
	 * Creates the default schema.
	 * 
	 * @param grids
	 *            the grids
	 * @param tableViewer
	 *            the table viewer
	 * @param errorLabel
	 *            the error label
	 * @param rowSequence 
	 */
	public abstract void createDefaultSchema(List grids,TableViewer tableViewer,Label errorLabel);
	
	/**
	 * Creates the cell editor list.
	 * 
	 * @param table
	 *            the table
	 * @param size
	 *            the size
	 * @return the cell editor[]
	 */
	
	public abstract CellEditor[] createCellEditorList(Table table,
			Map<String, Integer> columns) ;
	/**
	 * Creates the cell editor.
	 * 
	 * @param size
	 *            the size
	 * @return the cell editor[]
	 */
	protected CellEditor[] createCellEditor(int size){
		CellEditor[] cellEditor = new CellEditor[size];
		return cellEditor;
	}
		
	/**
	 * Adds the text editor.
	 * 
	 * @param table
	 *            the table
	 * @param cellEditor
	 *            the cell editor
	 * @param position
	 *            the position
	 */
	protected void addTextEditor(Table table, CellEditor[] cellEditor, Map<String, Integer> columns, String columnsName){
		
		cellEditor[columns.get(columnsName)]=new TextCellEditor(table);
		if(columnsName.equals(Messages.PRECISION) || columnsName.equals(Messages.SCALE)){
 			TextCellEditor editor = (TextCellEditor) cellEditor[columns.get(columnsName)];
 			Text txt = (Text) editor.getControl();
 			txt.setTextLimit(2);
 		}
	}
	
	/**
	 * Adds the combo box.
	 * 
	 * @param table
	 *            the table
	 * @param cellEditor
	 *            the cell editor
	 * @param data
	 *            the data
	 * @param position
	 *            the position
	 */
	protected void addComboBox(Table table, CellEditor[] cellEditor, String[] data, int position){
		cellEditor[position] = new CustomComboBoxCellEditor(table, data, SWT.READ_ONLY);		
	}
	
	public static String[] dataTypeList;
	public static String[] dataTypeKey;
	public static String[] dataTypeValue;
	public static String[] scaleTypeList;
	public static String[] scaleTypeKey;
	public static String[] scaleTypeValue;

	/**
	 * Sets the data type key value.
	 */
	public static void setDataTypeKeyValue() {
		if (dataTypeList != null)
		{
			dataTypeKey= new String[dataTypeList.length];
			dataTypeValue=new String[dataTypeList.length];
			for (int i=0;i<dataTypeList.length;i++) {
				String[] data = dataTypeList[i].split("#");
				dataTypeKey[i]=data[0];
				dataTypeValue[i]=data[1];
			}
		}
		else {
			String schemaList = Messages.DATATYPELIST;
			dataTypeList = schemaList.split(",");
			dataTypeKey= new String[dataTypeList.length];
			dataTypeValue=new String[dataTypeList.length];
			for (int i=0;i<dataTypeList.length;i++) {
				String[] data = dataTypeList[i].split("#");
				dataTypeKey[i]=data[0];
				dataTypeValue[i]=data[1];
			}
		}
			
	}

	public static Integer getDataTypeByValue(String value) {
			return SchemaFieldUtil.INSTANCE.getDataTypeByValue(value);
	}

	public static void setScaleTypeKeyValue() {
		if (scaleTypeList != null)
		{
			scaleTypeKey= new String[scaleTypeList.length];
			scaleTypeValue=new String[scaleTypeList.length];
			for (int i=0;i<scaleTypeList.length;i++) {
				String[] data = scaleTypeList[i].split("#");
				scaleTypeKey[i]=data[0];
				scaleTypeValue[i]=data[1];
			}
		}
		else {
			String schemaList = Messages.SCALETYPELIST;
			scaleTypeList = schemaList.split(",");
			scaleTypeKey= new String[scaleTypeList.length];
			scaleTypeValue=new String[scaleTypeList.length];
			for (int i=0;i<scaleTypeList.length;i++) {
				String[] data = scaleTypeList[i].split("#");
				scaleTypeKey[i]=data[0];
				scaleTypeValue[i]=data[1];
			}
		}
			
	}
	
	public static Integer getScaleTypeByValue(String value) {
		return SchemaFieldUtil.INSTANCE.getDataTypeByValue(value);
	}
	
	public static String[] getDataTypeValue() {
		if(dataTypeValue!=null){
			return dataTypeValue;
		}
		else{
			setDataTypeKeyValue();
			return dataTypeValue;
		}
	}
	
	public static String[] getDataTypeKey() {
		if(dataTypeKey!=null){
			return dataTypeKey;
		}
		else{
			setDataTypeKeyValue();
			return dataTypeKey;
		}
	}
	
	public static String[] getScaleTypeValue() {
		if(scaleTypeValue!=null){
			return scaleTypeValue;
		}
		else{
			setScaleTypeKeyValue();
			return scaleTypeValue;
		}
	}
	
	public static String[] getScaleTypeKey() {
		if(scaleTypeKey!=null){
			return scaleTypeKey;
		}
		else{
			setScaleTypeKeyValue();
			return scaleTypeKey;
		}
	}
	
	public static String[] getScaleType(){
		return scaleTypeList;
	}

	
}
