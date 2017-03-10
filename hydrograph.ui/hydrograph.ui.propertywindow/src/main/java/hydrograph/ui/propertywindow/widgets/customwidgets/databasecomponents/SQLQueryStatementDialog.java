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
package hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import hydrograph.ui.propertywindow.messages.Messages;

/**
 * The Class SQLQueryStatementDialog
 * @author Bitwise
 *
 */
public class SQLQueryStatementDialog extends Dialog {

	private StyledText styledText;
	private  String styleTextValue;
	private String textValue;
	private String styleTextOldValue;
	private boolean isTextChanged = false;
	private int MAX_STACK_SIZE;
	private List<String> undoActionList;
	private List<String> redoActionList;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SQLQueryStatementDialog(Shell parentShell, String textValue) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL | SWT.RESIZE);
		this.textValue = textValue;
		undoActionList = new LinkedList<>();
		redoActionList = new LinkedList<>();
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText(Messages.DATABASE_SQL_QUERY);
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label sqlQueryLabel = new Label(composite, SWT.NONE);
		sqlQueryLabel.setText(Messages.SQL_QUERY_STATEMENT);
		
		styledText = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL|SWT.MULTI|SWT.WRAP);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		styledText.setFont(new Font(Display.getCurrent(),"Courier New",9,SWT.NORMAL));
		styledText.setText(textValue);
		
		styledText.addExtendedModifyListener(new ExtendedModifyListener() {
			@Override
			public void modifyText(ExtendedModifyEvent event) {
				MAX_STACK_SIZE = styledText.getText().length();
				String currText = styledText.getText();
		        String newText = currText.substring(event.start, event.start + event.length);
		        if(newText != null && newText.length() > 0){
			          if(undoActionList.size() == MAX_STACK_SIZE){
			        	  undoActionList.remove(undoActionList.size() - 1);
			          }
			          undoActionList.add(0, newText);
		        }
			}
		});
		
		styledText.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent event) {
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				 boolean isCtrl = (event.stateMask & SWT.CTRL) > 0; 
                 boolean isAlt = (event.stateMask & SWT.ALT) > 0; 
                 if(isCtrl && !isAlt) { 
                     boolean isShift = (event.stateMask & SWT.SHIFT) > 0; 
                     if(!isShift && event.keyCode == 'z') { 
                         undo();
                     } else if(!isShift && event.keyCode == 'y' || isShift && event.keyCode == 'z') { 
                         redo(); 
                     }  
                 } 
			}
		});
		
		styleTextOldValue = styledText.getText();

		return container;
	}

	private void undo() {
	    if (undoActionList.size() > 0) {
	      String lastEdit = (String) undoActionList.remove(0);
	      int editLength = lastEdit.length();
	      String currText = styledText.getText();
	      int startReplaceIndex = currText.length() - editLength;
	      styledText.replaceTextRange(startReplaceIndex, editLength, "");
	      redoActionList.add(0, lastEdit);
	    }
	}
	
	
	private void redo() {
	    if (redoActionList.size() > 0) {
	      String text = (String) redoActionList.remove(0);
	      moveCursorToEnd();
	      styledText.append(text);
	      moveCursorToEnd();
	    }
	}
	
	private void moveCursorToEnd() {
	    styledText.setCaretOffset(styledText.getText().length());
	}
	
	private void compareTextValue(String newTextValue){
		if(styleTextOldValue != newTextValue){
			isTextChanged = true;
		}else{
			isTextChanged = false;
		}
	}
	
	/**
	 * The Function will return boolean if text value will be changed
	 * @return boolean
	 */
	public boolean isTextValueChanged(){
		return isTextChanged;
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		 createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	 @Override
	protected void okPressed() {
		 if(styledText !=null){
			  styleTextValue = styledText.getText();
			 if(StringUtils.isNotBlank(styleTextValue)){
				 compareTextValue(styleTextValue);
				 setStyleTextSqlQuery(styleTextValue);
			 }
		 }
		 
		super.okPressed();
	}
	 
	 public String getStyleTextSqlQuery(){
		 return styleTextValue;
	 }
	 
	 
	 public String setStyleTextSqlQuery(String styleTextValue){
		 return styleTextValue;
	 }
	 
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(612, 254);
	}

}
