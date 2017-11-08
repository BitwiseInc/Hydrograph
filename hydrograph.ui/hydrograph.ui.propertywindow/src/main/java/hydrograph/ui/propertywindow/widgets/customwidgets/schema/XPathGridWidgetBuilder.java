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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.schema;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;


/**
 * The Class XPathGridWidgetBuilder.
 * 
 * @author Bitwise
 */
public class XPathGridWidgetBuilder extends GridWidgetCommonBuilder {
	
	public static XPathGridWidgetBuilder INSTANCE = new XPathGridWidgetBuilder();
	private XPathGridWidgetBuilder() {}
	
	@Override
	public CellEditor[] createCellEditorList(Table table,
			Map<String, Integer> columns) {
		CellEditor[] cellEditor = createCellEditor(columns.size());
		addTextEditor(table,cellEditor, columns, Messages.FIELDNAME);
		addComboBox(table, cellEditor, getDataTypeKey(), columns.get(Messages.DATATYPE));
		addTextEditor(table, cellEditor, columns, Messages.XPATH);
		addTextEditor(table, cellEditor, columns, Messages.SCALE);
		addComboBox(table, cellEditor, getScaleTypeKey(), columns.get(Messages.SCALE_TYPE));
		addTextEditor(table, cellEditor, columns, Messages.DATEFORMAT);
		addTextEditor(table, cellEditor, columns, Messages.PRECISION);
		addTextEditor(table, cellEditor, columns, Messages.FIELD_DESCRIPTION);
		return cellEditor;
	}
	
	public CellEditor[] createCellEditorList(Table table,int size){
		return null;
	}

	/*
	 * Table mouse click event.
	 * Add new column in schema grid with default values.
	 * 
	 */
	@Override
	public void createDefaultSchema(List grids, TableViewer tableViewer, Label errorLabel) {
		int rowSequence = getRowSequence();
		XPathGridRow schemaGrid = new XPathGridRow();
		schemaGrid.setFieldName("DefaultField" + rowSequence);
		schemaGrid.setDateFormat("");
		schemaGrid.setXPath(schemaGrid.getFieldName());
		Text loopXpathTextBox=(Text)tableViewer.getTable().getData();
		if(StringUtils.isNotBlank(loopXpathTextBox.getText())){
			schemaGrid.setAbsolutexPath(loopXpathTextBox.getText().trim()+Path.SEPARATOR+schemaGrid.getXPath());
			
		}else{
			schemaGrid.setAbsolutexPath(schemaGrid.getXPath());
		}
		schemaGrid.setPrecision("");
		schemaGrid.setScale("");
		schemaGrid.setScaleType(Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX));
		schemaGrid.setScaleTypeValue(getScaleTypeValue()[Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX)]);
		schemaGrid.setDataType(Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX));
		schemaGrid.setDataTypeValue(getDataTypeValue()[Integer.valueOf(Constants.DEFAULT_INDEX_VALUE_FOR_COMBOBOX)]);
		schemaGrid.setDescription("");
		
		int numberOfRows=grids.size();
		do{
			if(!grids.contains(schemaGrid) && !isXPathDupliucate(schemaGrid,grids)){
				grids.add(schemaGrid);  
				tableViewer.setInput(grids);
				tableViewer.refresh();
				tableViewer.editElement(tableViewer.getElementAt(grids.size() == 0 ? grids.size() : grids.size() - 1), 0);
				break;
			}
			schemaGrid.setFieldName("DefaultField" + rowSequence++);
			schemaGrid.setXPath(schemaGrid.getFieldName());
			if(StringUtils.isNotBlank(loopXpathTextBox.getText())){
				schemaGrid.setAbsolutexPath(loopXpathTextBox.getText().trim()+Path.SEPARATOR+schemaGrid.getXPath());
				
			}else{
				schemaGrid.setAbsolutexPath(schemaGrid.getXPath());
			}
			numberOfRows--;
		}while(numberOfRows>=-1);
 		
	}

	private boolean isXPathDupliucate(XPathGridRow schemaGrid,List<XPathGridRow> grids) {
		return grids.stream().anyMatch(xPathGridRow->xPathGridRow.getXPath().equals(schemaGrid.getXPath()));
	}

	
}
