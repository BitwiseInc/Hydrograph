/********************************************************************************
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
 ******************************************************************************/

 
package hydrograph.ui.parametergrid.textgridwidget;

import hydrograph.ui.parametergrid.textgridwidget.columns.TextGridColumnLayout;
import hydrograph.ui.parametergrid.textgridwidget.columns.TextGridRowLayout;
import hydrograph.ui.parametergrid.textgridwidget.rows.TextGridRowBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;


public class TextGrid {
	private Composite container;
	private List<Composite> textGrid;
	private Map<Integer,TextGridRowLayout> rowLayouts;
	
	private Composite innerComposite;	
	private ScrolledComposite textGridComposite;
	private ColumnLayoutData textGridCompositeLayoutData;
	
	private Composite lastAddedRow;
	private Composite headerRow;
	//---------
	
	public TextGrid(Composite container){
		this.container = container;
		textGrid = new LinkedList<>();
		rowLayouts = new LinkedHashMap<>();
		initializeGridComposites();
	}
	
	
	public void addRow(TextGridRowLayout rowLayout,List<String> rowData){
		 TextGridRowBuilder textGridRowBuilder = new TextGridRowBuilder(innerComposite, rowLayout,rowData);	
		 lastAddedRow = textGridRowBuilder.addRaw();
		 textGrid.add(lastAddedRow);
	}
	
	public Composite addEmptyRow(TextGridRowLayout rowLayout){
		 TextGridRowBuilder textGridRowBuilder = new TextGridRowBuilder(innerComposite, rowLayout,null);	
		 lastAddedRow = textGridRowBuilder.addRaw();
		 textGrid.add(lastAddedRow);
		 return lastAddedRow;
	}
	
	public void addDisabledRow(TextGridRowLayout rowLayout,List<String> rowData){
		 TextGridRowBuilder textGridRowBuilder = new TextGridRowBuilder(innerComposite, rowLayout,rowData,false);	
		 lastAddedRow = textGridRowBuilder.addRaw();
		 textGrid.add(lastAddedRow);
	}
	
	public void addHeaderRow(TextGridRowLayout rowLayout,
			List<String> header) {
		
		 TextGridRowBuilder textGridRowBuilder = new TextGridRowBuilder(innerComposite, rowLayout,header);	
		 headerRow = textGridRowBuilder.addHeader();
	}
	
	public Composite getHeaderComposite(){
		return headerRow;
	}
		
	public List<Composite> getGrid(){
		return textGrid;
	}
		
	public void scrollToLastRow(){
		textGridComposite.showControl(lastAddedRow.getChildren()[0]);
	}
	
	private void initializeGridComposites(){
		textGridComposite = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textGridCompositeLayoutData = new ColumnLayoutData();
		textGridCompositeLayoutData.heightHint = 267;
		textGridComposite.setLayoutData(textGridCompositeLayoutData);
		textGridComposite.setExpandVertical(true);
		textGridComposite.setExpandHorizontal(true);
		
		innerComposite = new Composite(textGridComposite, SWT.NONE);
		ColumnLayout cl_composite_1 = new ColumnLayout();
		cl_composite_1.maxNumColumns = 1;
		innerComposite.setLayout(cl_composite_1);
		
		textGridComposite.setContent(innerComposite);
	}
		
	public void refresh(){
		textGridComposite.setMinSize(innerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		innerComposite.setBounds(innerComposite.getBounds().x, innerComposite.getBounds().y, innerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, innerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	}
	
	public void setHeight(int height){
		textGridCompositeLayoutData.heightHint = height;
	}
		
	public List<List<String>> getData(){
		List<List<String>> gridData = new LinkedList<>();
		for(int i=0;i<textGrid.size();i++){
			Control[] rowColumns = textGrid.get(i).getChildren();
			List<String> rowData = new LinkedList<>();
			for(int j=1;j<rowColumns.length;j++){
				rowData.add(((Text)rowColumns[j]).getText());
			}
			
			gridData.add(rowData);
		}
		return gridData;
	}
	
	public void removeSelectedRows(){
		List<Control> removedRows = new LinkedList<>();
		for(Control row : textGrid){
			//rows.dispose();
			for(Control column : ((Composite)row).getChildren()){
				if(column instanceof Button){
					if(((Button)column).getSelection()){
						row.dispose();
						removedRows.add(row);
						break;
					}
				}				
			}
		}
		
		for(Control removedRow: removedRows){
			textGrid.remove(removedRow);
		}
		
		refresh();
	}


	public void selectRow(int rowId) {
		((Button)textGrid.get(rowId).getChildren()[0]).setSelection(true);
	}


	public void clearSelections() {
		for(Control row : textGrid){
			((Button)((Composite)row).getChildren()[0]).setSelection(false);
		}
	}


	public void selectAllRows() {
		for(Control row : textGrid){
			if(((Button)((Composite)row).getChildren()[0]).isEnabled())
			((Button)((Composite)row).getChildren()[0]).setSelection(true);
		}
	}


	public void clear() {
		// TODO Auto-generated method stub
		for(Control row : textGrid){
			row.dispose();
		}
		
		textGrid.clear();
		refresh();
	}
	
	public Composite getLastAddedRow(){
		if(textGrid.size()>0)
			return textGrid.get(textGrid.size()-1);
		else
			return null;
	}
}
