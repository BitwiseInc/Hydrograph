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

package hydrograph.ui.dataviewer.find;

import hydrograph.ui.dataviewer.actions.FindAction;
import hydrograph.ui.dataviewer.constants.Views;
import hydrograph.ui.dataviewer.window.DebugDataViewer;
import hydrograph.ui.logging.factory.LogFactory;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;


/**
 * The Class FindViewDataDialog.
 * Provides Dialog for Data Viewer Find functionality. 
 * @author Bitwise
 *
 */
public class FindViewDataDialog extends Dialog{

	private static final Logger logger = LogFactory.INSTANCE.getLogger(FindViewDataDialog.class);
	public static final int CLOSE = 9999;
	private static final String labelText = "String Not Found";
	private TableViewer debugDataViewer;
	private StyledText formatedStyledText;
	private StyledText unFormatedStyledText;
	private CTabFolder cTabFolder;
	private Text findText;
	private int findRowIndex=0;
	private int findColIndex=1;
	private int prevColSelection = 0;
	private int prevRowSelection = 0;
	private int formattedViewPrevLineIndex = 0;
	private int formattedViewNextLineIndex = 0;
	private int unFormattedViewPrevLineIndex = 0;
	private int unFormattedViewNextLineIndex = 0;
	private String textData = null;
	private Label label;
	private FindAction findAction;
	private Button btnNext;
	private Button closeButton;
	private boolean flag = false;
	private long pageNo = 1;
	private DebugDataViewer dataViewer;
	
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param debugDataViewer 
	 * @param formatedStyledText
	 * @param unFormatedStyledText
	 * @param cTabFolder
	 */
	public FindViewDataDialog(Shell parentShell, DebugDataViewer dataViewer) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE);
		this.debugDataViewer = dataViewer.getTableViewer();
		this.formatedStyledText = dataViewer.getFormattedViewTextarea();
		this.unFormatedStyledText = dataViewer.getUnformattedViewTextarea();
		this.cTabFolder = dataViewer.getCurrentView();
		this.dataViewer = dataViewer;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("Find");
		GridData parentCompositeData = new GridData(SWT.FILL, SWT.BEGINNING, true, true, 3, 3);
		parentCompositeData.heightHint = 150;
		parentCompositeData.widthHint = 300;
		parentCompositeData.grabExcessHorizontalSpace = true;
		parentCompositeData.grabExcessVerticalSpace = true;
			
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(parentCompositeData);
		
		
		container.getShell().addShellListener(new ShellListener() {
			@Override
			public void shellIconified(ShellEvent e) {}
			@Override
			public void shellDeiconified(ShellEvent e) {}
			@Override
			public void shellDeactivated(ShellEvent e) {}
			@Override
			public void shellClosed(ShellEvent e) {
				CTabItem tabItem = cTabFolder.getSelection();
				if (tabItem.getData("VIEW_NAME").equals(Views.GRID_VIEW_NAME)) {
					logger.debug("Grid View");
					clearTableItemBgColor(debugDataViewer);
					if(debugDataViewer != null){
						debugDataViewer.setData("SELECTED_ROW_INDEX", null);
						debugDataViewer.setData("SEELCTED_COLUMN_INDEX", null);
					}
					dataViewer.getTablecursor().setVisible(true);
				} else if (tabItem.getData("VIEW_NAME").equals(Views.HORIZONTAL_VIEW_NAME)) {
					logger.debug("HORIZONTAL View");
				} else if (tabItem.getData("VIEW_NAME").equals(Views.FORMATTED_VIEW_NAME)) {
					logger.debug("FORMATTED View");
					clearStyledTextBgColor(formatedStyledText, textData);
				} else if (tabItem.getData("VIEW_NAME").equals(Views.UNFORMATTED_VIEW_NAME)) {
					logger.debug("UNFORMATTED View");
					clearStyledTextBgColor(unFormatedStyledText, textData);
				}
			}
			@Override
			public void shellActivated(ShellEvent e) {}
		});
		
		Composite composite_1 = new Composite(container, SWT.BORDER);
		GridLayout generalGroupLayout = new GridLayout(2, true);
		generalGroupLayout.verticalSpacing = 0;
		generalGroupLayout.marginWidth = 0;
		generalGroupLayout.marginHeight = 0;
		generalGroupLayout.horizontalSpacing = 0;
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, true, 3, 3);
		gridData.heightHint = 66;
		gridData.widthHint = 240;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		composite_1.setLayoutData(gridData);
		composite_1.setLayout(generalGroupLayout);
		
		Composite composite = new Composite(composite_1, SWT.None);
		composite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_composite.widthHint = 210;
		composite.setLayoutData(gd_composite);
		
		Label lblFind = new Label(composite, SWT.NONE);
		lblFind.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFind.setText("Find:  ");
		
		findText = new Text(composite, SWT.BORDER);
		findText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		new Label(composite_1, SWT.None).setVisible(false);
		
		Composite composite_2 = new Composite(composite_1, SWT.None);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_2.heightHint = 40;
		gd_composite_2.widthHint = 230;
		composite_2.setLayoutData(gd_composite_2);
		
		final Button btnPrevious = new Button(composite_2, SWT.NONE);
		btnPrevious.setBounds(0, 0, 75, 25);
		btnPrevious.setText("Prev");
		btnPrevious.setEnabled(false);
		btnPrevious.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textData = findText.getText();
				CTabItem tabItem = cTabFolder.getSelection();
				if (tabItem.getData("VIEW_NAME").equals(Views.GRID_VIEW_NAME)) {
					logger.trace("---------Grid View on Prev---------");
					checkPageNo();
					if(isExistInTable(debugDataViewer, textData)){
						if(flag){
							clearTableItemBgColor(debugDataViewer); 
							flag = false;
							findRowIndex = debugDataViewer.getTable().getItems().length - 1;
							findColIndex = debugDataViewer.getTable().getColumnCount();
							}
						reverseTableTraverse(debugDataViewer, dataViewer.getTablecursor());
					}
				} else if (tabItem.getData("VIEW_NAME").equals(Views.HORIZONTAL_VIEW_NAME)) {
					logger.debug("HORIZONTAL View");
				} else if (tabItem.getData("VIEW_NAME").equals(Views.FORMATTED_VIEW_NAME)) {
					logger.trace("------------FORMATTED View on Prev------------------------------");
					formatedStyledText = dataViewer.getFormattedViewTextarea();
					checkPageNo();
					if(!isTextExist(formatedStyledText, textData)){
						clearStyledTextBgColor(formatedStyledText, textData);
						int[] resultIndex =StyledTextEventListener.INSTANCE.prevButtonListener(formatedStyledText, textData, formattedViewPrevLineIndex, formattedViewNextLineIndex);
						formattedViewPrevLineIndex = resultIndex[0];
						formattedViewNextLineIndex = resultIndex[1];
					}
					
				} else if (tabItem.getData("VIEW_NAME").equals(Views.UNFORMATTED_VIEW_NAME)) {
					logger.trace("------------UNFORMATTED View on Prev------------");
					unFormatedStyledText = dataViewer.getUnformattedViewTextarea();
					checkPageNo();
					if(!isTextExist(unFormatedStyledText, textData)){
						clearStyledTextBgColor(unFormatedStyledText, textData);
						int[] resultIndex =StyledTextEventListener.INSTANCE.prevButtonListener(unFormatedStyledText, textData, unFormattedViewPrevLineIndex, unFormattedViewNextLineIndex);
						unFormattedViewPrevLineIndex = resultIndex[0];
						unFormattedViewNextLineIndex = resultIndex[1];
					}
				}
			}
		});
		
		btnNext = new Button(composite_2, SWT.NONE);
		btnNext.setBounds(81, 0, 78, 25);
		btnNext.setText("Next");
		btnNext.setEnabled(false);
		parent.getShell().setDefaultButton(btnNext);
		GridData gd_btnNext = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		btnNext.setLayoutData(gd_btnNext);
		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textData = findText.getText();
				CTabItem tabItem = cTabFolder.getSelection();
				if (tabItem.getData("VIEW_NAME").equals(Views.GRID_VIEW_NAME)) {
					logger.trace("--------Grid View on Next--------");
					checkPageNo();
					if(isExistInTable(debugDataViewer, textData)){
						if(flag){
							clearTableItemBgColor(debugDataViewer); flag = false;
						}
						forwardTableTraverse(debugDataViewer, dataViewer.getTablecursor());
					}
				} else if (tabItem.getData("VIEW_NAME").equals(Views.HORIZONTAL_VIEW_NAME)) {
					logger.debug("HORIZONTAL View");
				} else if (tabItem.getData("VIEW_NAME").equals(Views.FORMATTED_VIEW_NAME)) {
					logger.trace("---------------------------FORMATTED View on Next-------------------------");
					formatedStyledText = dataViewer.getFormattedViewTextarea();
					checkPageNo();
					if(textData != null && !textData.equalsIgnoreCase(findText.getText())){
						formattedViewPrevLineIndex = 0;
						formattedViewNextLineIndex = 0;
						textData = findText.getText();
					}else{
						textData = findText.getText();
					} 
					if(!isTextExist(formatedStyledText, textData)){
						clearStyledTextBgColor(formatedStyledText, textData);
						int[] resultIndex = StyledTextEventListener.INSTANCE.nextButtonListener(formatedStyledText, textData, formattedViewPrevLineIndex, formattedViewNextLineIndex);
						formattedViewPrevLineIndex = resultIndex[0];
						formattedViewNextLineIndex = resultIndex[1];
					}
					
				} else if (tabItem.getData("VIEW_NAME").equals(Views.UNFORMATTED_VIEW_NAME)) {
					logger.trace("---------------------------UNFORMATTED View on Next-------------------------");
					unFormatedStyledText = dataViewer.getUnformattedViewTextarea();
					checkPageNo();
					if(textData != null && !textData.equalsIgnoreCase(findText.getText())){
						unFormattedViewPrevLineIndex = 0;
						unFormattedViewNextLineIndex = 0;
						textData = findText.getText();
					}else{
						textData = findText.getText();
					}
					if(!isTextExist(unFormatedStyledText, textData)){
						clearStyledTextBgColor(unFormatedStyledText, textData);
						int[] resultIndex = StyledTextEventListener.INSTANCE.nextButtonListener(unFormatedStyledText, textData, unFormattedViewPrevLineIndex, unFormattedViewNextLineIndex);
						unFormattedViewPrevLineIndex = resultIndex[0];
						unFormattedViewNextLineIndex = resultIndex[1];
					}
				}
			}
		});
		
		final Button btnAll = new Button(composite_2, SWT.NONE);
		btnAll.setBounds(166, 0, 78, 25);
		btnAll.setText("All");
		btnAll.setEnabled(false);
		GridData gd_btnNewButton = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		btnAll.setLayoutData(gd_btnNewButton);
		btnAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CTabItem tabItem = cTabFolder.getSelection();
				textData = findText.getText();
				if (tabItem.getData("VIEW_NAME").equals(Views.GRID_VIEW_NAME)) {
					logger.trace("-----------Grid View on All----------------");
					flag = true;
					checkPageNo();
					if(isExistInTable(debugDataViewer, textData)){
						clearTableItemBgColor(debugDataViewer);
						selectAllInTable(debugDataViewer, dataViewer.getTablecursor());
					}
				} else if (tabItem.getData("VIEW_NAME").equals(Views.HORIZONTAL_VIEW_NAME)) {
					logger.debug("HORIZONTAL View");
				} else if (tabItem.getData("VIEW_NAME").equals(Views.FORMATTED_VIEW_NAME)) {
					logger.trace("-----------FORMATTED View on All--------------");
					formatedStyledText = dataViewer.getFormattedViewTextarea();
					checkPageNo();
					if(!isTextExist(formatedStyledText, textData)){
						clearStyledTextBgColor(formatedStyledText, textData);
						StyledTextEventListener.INSTANCE.allButtonListener(formatedStyledText, textData, null, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY), label);  
					}
				} else if (tabItem.getData("VIEW_NAME").equals(Views.UNFORMATTED_VIEW_NAME)) {
					logger.trace("UNFORMATTED View on All--------------");
					unFormatedStyledText = dataViewer.getUnformattedViewTextarea();
					checkPageNo();
					if(!isTextExist(unFormatedStyledText, textData)){
						clearStyledTextBgColor(unFormatedStyledText, textData);
						StyledTextEventListener.INSTANCE.allButtonListener(unFormatedStyledText, textData, null, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY), label); 
					}
				}
			}
		});
		
		findText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				Text txt = (Text)event.widget;
				if(StringUtils.isNotBlank(txt.getText())){
					btnPrevious.setEnabled(true);
					btnNext.setEnabled(true);
					btnAll.setEnabled(true);
				}else{
					btnPrevious.setEnabled(false);
					btnNext.setEnabled(false);
					btnAll.setEnabled(false);
					label.setVisible(false);
				}
			}
		});
		
		return container;
	}
	
	private void checkPageNo(){
		if(dataViewer.getCurrentPage() != pageNo){
			findRowIndex = 0;
			findColIndex = 0;
			formattedViewPrevLineIndex = 0;
			formattedViewNextLineIndex = 0;
			unFormattedViewPrevLineIndex = 0;
			unFormattedViewNextLineIndex = 0;
			pageNo = dataViewer.getCurrentPage();
		}
	}
	
	private boolean isTextExist(StyledText styledText, String text){
		if(StringUtils.isNotBlank(styledText.getText())){
			int textIndex = StringUtils.indexOf(StringUtils.lowerCase(styledText.getText()), StringUtils.lowerCase(text), 0);
			if(textIndex < 0){
				label.setVisible(true);
				label.setText(labelText);
				return true;
			}else{
				label.setVisible(false);
				return false;
			}
		}else{ return false;}
		
	}
	
	private void forwardTableTraverse(TableViewer debugDataViewer, TableCursor tableCursor){
		TableItem previousSelectedTableItem = null;
		if(debugDataViewer.getData("SELECTED_ROW_INDEX")!=null){
			previousSelectedTableItem = debugDataViewer.getTable().getItem((int) debugDataViewer.getData("SELECTED_ROW_INDEX"));
			findColIndex++;
		}
		Table table = debugDataViewer.getTable();
		TableItem[] tableItems = table.getItems();
		if(findColIndex == prevColSelection && findRowIndex == prevRowSelection){
			findColIndex++;
		}
		if(findRowIndex < 0){
			findRowIndex = 0;
		}
		for(;findRowIndex<tableItems.length;){
			TableItem tableItem = tableItems[findRowIndex];
			for(;findColIndex <= table.getColumnCount();findColIndex++){
				if(StringUtils.containsIgnoreCase(tableItem.getText(findColIndex), findText.getText())){
					if(prevColSelection > 0){
						previousSelectedTableItem.setBackground(prevColSelection, Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
					}
					label.setVisible(false);
					table.showItem(tableItem);
					table.showColumn(table.getColumn(findColIndex));
					tableCursor.setSelection(findRowIndex, findColIndex);
					tableCursor.setVisible(false);
					tableItem.setBackground(findColIndex, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
					debugDataViewer.setData("SELECTED_ROW_INDEX", findRowIndex);
					debugDataViewer.setData("SEELCTED_COLUMN_INDEX", findColIndex);
					prevColSelection = findColIndex;
					return;
				}
			}
			findRowIndex++;
			findColIndex=1;
			if(findRowIndex >= tableItems.length){ findRowIndex = 0;}
		}
	}
	
	private void reverseTableTraverse(TableViewer debugDataViewer, TableCursor tableCursor){
		TableItem previousSelectedTableItem = null;
		if(debugDataViewer.getData("SELECTED_ROW_INDEX")!=null){
			previousSelectedTableItem = debugDataViewer.getTable().getItem((int) debugDataViewer.getData("SELECTED_ROW_INDEX"));
			findColIndex -= 1;
		}
		if(findRowIndex < 0){
			findRowIndex = debugDataViewer.getTable().getItems().length - 1;
		}
		Table table = debugDataViewer.getTable();
		TableItem[] tableItems = table.getItems();
		for(; findRowIndex >=0; findRowIndex--){
			TableItem tableItem = tableItems[findRowIndex];
			for( ; findColIndex > 0 ;findColIndex--){
				if(StringUtils.containsIgnoreCase(tableItem.getText(findColIndex), findText.getText())){
					if(prevColSelection > 0){
						previousSelectedTableItem.setBackground(prevColSelection, Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
					}
					label.setVisible(false);
					table.showItem(tableItem);
					table.showColumn(table.getColumn(findColIndex));
					tableCursor.setSelection(findRowIndex, findColIndex);
					tableCursor.setVisible(false);
					tableItem.setBackground(findColIndex,Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
					if(findColIndex<=0){debugDataViewer.setData("SELECTED_ROW_INDEX", findRowIndex-1);}else{
						debugDataViewer.setData("SELECTED_ROW_INDEX", findRowIndex);
					}
					debugDataViewer.setData("SEELCTED_COLUMN_INDEX", findColIndex);
					prevColSelection = findColIndex;
					prevRowSelection = findRowIndex;
					return ;
				}
				
			}
			if(findColIndex <= 0){ findColIndex = table.getColumnCount();}
			if(findRowIndex == 0){ findRowIndex = tableItems.length; }
		}
	}
	
	private void selectAllInTable(TableViewer debugDataViewer, TableCursor tableCursor){
		Table table = debugDataViewer.getTable();
		TableItem[] tableItems = table.getItems();
		int recordCount = 0;
		
		for(int rowIndex = 0; rowIndex < tableItems.length; rowIndex++){
			TableItem tableItem = tableItems[rowIndex];
			for(int colIndex = 1; colIndex <= table.getColumnCount(); colIndex++){
				if(StringUtils.containsIgnoreCase(tableItem.getText(colIndex), findText.getText())){
					label.setVisible(false);
					tableItem.setBackground(colIndex, Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
					recordCount++;
				}
			}
			findColIndex=1;
		}
		
		findRowIndex = 0;
		findColIndex = 0;
		
		if(recordCount > 0){
			label.setVisible(true);
			label.setText("Match count - " + recordCount);
		}else{ label.setVisible(false); }
		
	}
	
	  private void clearStyledTextBgColor(StyledText styledText, String textData){
		  if(StringUtils.isBlank(textData) || styledText == null){
			  return;
		  }else{
			  StyleRange[] prevRanges = new StyleRange[1];
			  prevRanges[0] = new StyleRange(0, 1, null, Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			  styledText.replaceStyleRanges(0, styledText.getText().length(), prevRanges);
		  }
	  }
	 
	  
	  private boolean isExistInTable(TableViewer tableViewer, String text){
		  boolean isDataExist = false;
		  Table table = tableViewer.getTable();
		  TableItem[] tableItems = table.getItems();
		  for(int i=0;i<tableItems.length;i++){
			TableItem tableItem = tableItems[i];
			for(int j=1;j <= table.getColumnCount()-1;j++){
				if(StringUtils.containsIgnoreCase(tableItem.getText(j), text)){
					label.setVisible(false);
					isDataExist = true;
					return isDataExist;
				}else{
					label.setVisible(true);
					label.setText(labelText);
					isDataExist =  false;
				}
				
			}
		  }
		return isDataExist;
	  }
	  
	  private void clearTableItemBgColor(TableViewer debugDataViewer){
		  if(debugDataViewer == null){
			  return;
		  }
		  Table table = debugDataViewer.getTable();
		  TableItem[] tableItems = table.getItems();
		  for(int i=0;i<tableItems.length;i++){
			TableItem tableItem = tableItems[i];
			for(int j=1;j <= table.getColumnCount()-1;j++){
				tableItem.getText(j);
				tableItem.setBackground(j, Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			}
		  }
	  }
	  
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(530, 153);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		parent.setLayout(new GridLayout(1,false));
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 0, 0);
		parent.setLayoutData(gridData);
		
		label=new  Label(parent, SWT.None);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 0, 0));
		label.setText(labelText + "                ");		
		label.setVisible(false);
		
		closeButton = createButton(parent, CLOSE, "Close", false);
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearTableItemBgColor(debugDataViewer);
				if(debugDataViewer != null){
					 debugDataViewer.setData("SELECTED_ROW_INDEX", null);
					 debugDataViewer.setData("SEELCTED_COLUMN_INDEX", null);
				}
				dataViewer.getTablecursor().setVisible(true);
				clearStyledTextBgColor(formatedStyledText, textData);
				clearStyledTextBgColor(unFormatedStyledText, textData);
				close();
			}
		});
	}

	/**
	 * Opens the find child window for the View Data window.
	 * 
	 * @param findAction
	 *            the find action
	 * @return the int
	 */
	public int open(FindAction findAction) {
		this.findAction=findAction;
		findAction.isOpened=true;
		return this.open();
	}
	
	@Override
	public boolean close() {
		this.findAction.isOpened=false;
		return super.close();
	}

	
	
	
}
