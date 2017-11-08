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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.widgets.dialogs.join.support.JoinMappingEditingSupport;

/**
 * The Class WidgetUtility.
 * 
 * @author Bitwise
 */
public class WidgetUtility {
	private static final String ERROR = "Error";
	private static final String ERROR_MESSAGE = "Please Select row to delete";
	

	private WidgetUtility(){
	}
	 
	/**
	 * Creates the table viewer.
	 * 
	 * @param tableViewer
	 *            the table viewer
	 * @param iStructuredContentProvider
	 *            the i structured content provider
	 * @param iTableLabelProvider
	 *            the i table label provider
	 * @return the table viewer
	 */
	public static TableViewer createTableViewer( TableViewer tableViewer,IStructuredContentProvider iStructuredContentProvider,ITableLabelProvider iTableLabelProvider){
		tableViewer.setContentProvider(iStructuredContentProvider);
		tableViewer.setLabelProvider(iTableLabelProvider);
		return tableViewer;

	}
	
	/**
	 * Creates the table columns.
	 * 
	 * @param table
	 *            the table
	 * @param fields
	 *            the fields
	 */
	public static void createTableColumns(Table table,String[] fields){
		for (String field : fields) {
			TableColumn tc = new TableColumn(table, SWT.CENTER);
			tc.setText(field);
			tc.setMoveable(true);
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
	
	
	/**
	 * This Method use to create error message decorator,Its show an error image with message on applied controller field. 
	 * @param control
	 * @param message
	 * @return ControlDecoration
	 */
	
	public static ControlDecoration addDecorator(Control control,String message){
		ControlDecoration txtDecorator = new ControlDecoration(control,SWT.LEFT);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		Image img = fieldDecoration.getImage();
		txtDecorator.setImage(img);
		txtDecorator.setDescriptionText(message);
		return txtDecorator; 
	} 
	


	/**
	 * Checks if is file extention.
	 * @param file the file
	 * @param extention the extention
	 * @return true, if is file extention
	 */
	public static boolean isFileExtention(String file,String extention) {
		return extention.equalsIgnoreCase(file.substring(file.lastIndexOf(".")));
	}

	/**
	 * Error message.
	 * 
	 * @param message
	 *            the message
	 */
	public static void errorMessage(String message) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		messageBox.setText(ERROR);
		messageBox.setMessage(message);
		messageBox.open();
	}
	/**
	 * create SWT MessageBox
	 * 
	 * @param message to be shown 
	 * @param title of widow
	 * @param style to be set on window           
	 */
	
	public static int createMessageBox(String message,String windowTitle,int style) {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell,style);
		messageBox.setText(windowTitle);
		messageBox.setMessage(message);
		return messageBox.open();
	}
	
	
	/**
	 * create SWT MessageBox
	 * 
	 * @param message to be shown 
	 * @param title of widow
	 * @param style to be set on window     
	 * @param shell currentActive shell      
	 */
	
	public static int createMessageBox(String message,String windowTitle,int style,Shell shell) {
		MessageBox messageBox = new MessageBox(shell,style);
		messageBox.setText(windowTitle);
		messageBox.setMessage(message);
		return messageBox.open();
	}
	
	/**
	 * Elt confirm message.
	 * 
	 * @param message
	 *            the message
	 * @return true, if successful
	 */
	public static boolean eltConfirmMessage(String message){
	    return MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), Messages.WARNING, message);
	}

	/**
	 * Adds the text editor.
	 * @param table the table
	 * @param cellEditor the cell editor
	 * @param position the position
	 */
	public static void addTextEditor(Table table, CellEditor[] cellEditor, int position){
		cellEditor[position]=new TextCellEditor(table);
	}

	/**
	 * Adds the combo box.
	 * @param table the table
	 * @param cellEditor the cell editor
	 * @param data the data
	 * @param position the position
	 */
	public static void addComboBox(Table table, CellEditor[] cellEditor, String[] data, int position){
		cellEditor[position] = new ComboBoxCellEditor(table, data,SWT.READ_ONLY);		
	}
	
	/**
	 * Set the cursor on delete the row from table.
	 * @param tableViewer
	 * @param gridList
	 */
	public static void setCursorOnDeleteRow(TableViewer tableViewer, List<?> gridList){
		Table table = tableViewer.getTable();
		int[] indexes = table.getSelectionIndices();
		if (table.getSelectionIndex() == -1) {
			WidgetUtility.errorMessage(ERROR_MESSAGE);
		} else {
			table.remove(indexes);
			List listOfItemsToRemove= new ArrayList();
			for (int index : indexes) { 
				listOfItemsToRemove.add(gridList.get(index));
			}
			gridList.removeAll(listOfItemsToRemove);
			
		//highlight after deletion
		if(indexes.length == 1 && gridList.size() > 0){//only one item is deleted
			if(gridList.size() == 1){//list contains only one element
				table.select(0);// select the first element
				tableViewer.editElement(tableViewer.getElementAt(0), 0);
			}
			else if(gridList.size() == indexes[0]){//deleted last item 
				table.select(gridList.size() - 1);//select the last element which now at the end of the list
				tableViewer.editElement(tableViewer.getElementAt(gridList.size() - 1), 0);
			}
			else if(gridList.size() > indexes[0]){//deleted element from middle of the list
				table.select( indexes[0] == 0 ? 0 : (indexes[0] - 1) );//select the element from at the previous location
				tableViewer.editElement(tableViewer.getElementAt(indexes[0] == 0 ? 0 : (indexes[0] - 1)), 0);
			}
		}
		else if(indexes.length >= 2){//multiple items are selected for deletion
			if(indexes[0] == 0){//delete from 0 to ...
				if(gridList.size() >= 1){//list contains only one element
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
	}

	public static void addVerifyListnerToOutputEditingSupport(JoinMappingEditingSupport outputEditingSupport) {
		((Text)outputEditingSupport.getEditor().getControl()).addVerifyListener(new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent e) {
				String text=e.text;
				Matcher matcher=Pattern.compile(Constants.REGEX).matcher(text);
				
				if(matcher.matches()){
					e.doit=true;
				}else{
					e.doit=false;
				}
			}
		});
	}
}
