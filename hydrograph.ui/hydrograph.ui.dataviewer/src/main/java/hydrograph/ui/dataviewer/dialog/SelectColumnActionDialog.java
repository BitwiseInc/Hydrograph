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

package hydrograph.ui.dataviewer.dialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import hydrograph.ui.common.util.ImagePathConstant;

/**
 * The Class SelectColumnActionDialog.
 * Provides mechanism for selecting columns to be displayed in data viewer.
 * 
 * @author Bitwise
 *
 */
public class SelectColumnActionDialog extends Dialog {
	private java.util.List<String> allColumns;
	private java.util.List<String> selectedColumns;
	private List listAllComlumns;
	private List listSelectedColumns;
	private Label moveUpLable;
	private Label moveDownLable;
	private Button okButton;
	private static final String SELECT_COLUMNS = "Select Columns";
	private static final String ALL_COLUMNS = "All Columns";
	private static final String SELECTED_COLUMNS = "Selected Columns";
	private static final String SKIP = "skip";
	private boolean isAllColumnsAsce = false;
	private boolean isSelectedColumnsAsce = false;
	private Button sortAll;
	private Button sortSelected;
	private Comparator comparator = Collections.reverseOrder();
	/**
	 * @param parentShell
	 * @param selectColumnAction
	 * @param selectedColumns2 
	 * @param allColumns2 
	 */
	public SelectColumnActionDialog(Shell parentShell, java.util.List<String> allColumns, java.util.List<String> selectedColumns) {
		super(parentShell);
		this.allColumns  = new ArrayList<>();
		this.allColumns.addAll(allColumns);
		this.selectedColumns = new ArrayList<>();
		this.selectedColumns.addAll(selectedColumns);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL | SWT.RESIZE |SWT.MIN |SWT.MAX );
	}
	
	/**
	 * Creates the SelectColumn Window
	 */
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		SashForm sashForm = new SashForm(container, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Shell shell=container.getShell();
		shell.setText(SELECT_COLUMNS);
		shell.setImage(ImagePathConstant.TABLE_ICON.getImageFromRegistry());
		
		Composite allColumnsComposite = new Composite(sashForm, SWT.NONE);
		allColumnsComposite.setLayout(new GridLayout(2, false));
		
		Composite lblAndBtncomposite = new Composite(allColumnsComposite, SWT.NONE);
		lblAndBtncomposite.setLayout(new GridLayout(2, false));
		GridData gd_lblAndBtncomposite = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_lblAndBtncomposite.heightHint = 34;
		lblAndBtncomposite.setLayoutData(gd_lblAndBtncomposite);
		
		Label lblAllColumns = new Label(lblAndBtncomposite, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
		gd_lblNewLabel.widthHint = 79;
		lblAllColumns.setLayoutData(gd_lblNewLabel);
		lblAllColumns.setText(ALL_COLUMNS);
		FontData[] allColumnFont = lblAllColumns.getFont().getFontData();
		allColumnFont[0].setHeight(11);
		lblAllColumns.setFont( new Font(shell.getDisplay(),allColumnFont[0]));
		
		sortAll = new Button(lblAndBtncomposite, SWT.NONE);
		sortAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(allColumnsComposite, SWT.NONE);
		sortAll.setImage(ImagePathConstant.SORT_ASC.getImageFromRegistry());
		
		
		listAllComlumns = new List(allColumnsComposite, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
		listAllComlumns.setItems(Arrays.copyOf(allColumns.toArray(),allColumns.toArray().length,String[].class));
		GridData gd_listAllComlumns = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_listAllComlumns.widthHint = 228;
		listAllComlumns.setLayoutData(gd_listAllComlumns);

		Composite allColumnsControlButtons = new Composite(allColumnsComposite, SWT.NONE);
		allColumnsControlButtons.setLayout(null);
		GridData gd_allColumnsControlButtons = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_allColumnsControlButtons.widthHint = 40;
		gd_allColumnsControlButtons.heightHint = 41;
		allColumnsControlButtons.setLayoutData(gd_allColumnsControlButtons);
		
		Label selectAllLabel = new Label(allColumnsControlButtons, SWT.NONE);
		selectAllLabel.setBounds(6, 70, 25, 25);
		selectAllLabel.setImage(ImagePathConstant.SELECT_ALL_ICON.getImageFromRegistry());
		
		Label selectLabel = new Label(allColumnsControlButtons, SWT.NONE);
		selectLabel.setBounds(6, 110, 25, 25);
		selectLabel.setImage(ImagePathConstant.SELECT_ICON.getImageFromRegistry());
		
		Label disSelectLabel = new Label(allColumnsControlButtons, SWT.NONE);
		disSelectLabel.setBounds(6, 150, 25, 25);
		disSelectLabel.setImage(ImagePathConstant.DESELECT_ICON.getImageFromRegistry());
		
		Label removeAll = new Label(allColumnsControlButtons, SWT.NONE);
		removeAll.setBounds(6, 190, 25, 25);
		removeAll.setImage(ImagePathConstant.DESELECT_ALL_ICON.getImageFromRegistry());
		
		
		
		Composite selectColumnComposite = new Composite(sashForm, SWT.NONE);
		selectColumnComposite.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(selectColumnComposite, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		gd_composite.heightHint = 34;
		composite.setLayoutData(gd_composite);
		
		Label lblSelectedColumns = new Label(composite, SWT.NONE);
		GridData gd_lblSelectedColumns = new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 1);
		gd_lblSelectedColumns.heightHint = 36;
		gd_lblSelectedColumns.widthHint = 120;
		lblSelectedColumns.setLayoutData(gd_lblSelectedColumns);
		lblSelectedColumns.setText(SELECTED_COLUMNS);
		FontData[] selectColumnFont = lblSelectedColumns.getFont().getFontData();
		selectColumnFont[0].setHeight(11);
		lblSelectedColumns.setFont( new Font(shell.getDisplay(),selectColumnFont[0]));
		
		
		sortSelected = new Button(composite, SWT.NONE);
		GridData gd_sortSelected = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		sortSelected.setLayoutData(gd_sortSelected);
		sortSelected.setImage(ImagePathConstant.SORT_ASC.getImageFromRegistry());
		new Label(selectColumnComposite, SWT.NONE);
		
		listSelectedColumns = new List(selectColumnComposite, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL);
		GridData gd_listSelectedColumns = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_listSelectedColumns.widthHint = 228;
		listSelectedColumns.setLayoutData(gd_listSelectedColumns);
		listSelectedColumns.setItems(Arrays.copyOf(selectedColumns.toArray(),selectedColumns.toArray().length,String[].class));

		Composite moveElementsComposite = new Composite(selectColumnComposite, SWT.NONE);
		GridData gd_moveElementsComposite = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_moveElementsComposite.heightHint = 271;
		gd_moveElementsComposite.widthHint = 30;
		moveElementsComposite.setLayoutData(gd_moveElementsComposite);
		
		moveUpLable = new Label(moveElementsComposite, SWT.NONE);
		moveUpLable.setBounds(0, 114, 24, 24);
		moveUpLable.setImage(ImagePathConstant.UP_ICON.getImageFromRegistry());
		
		moveDownLable = new Label(moveElementsComposite, SWT.NONE);
		moveDownLable .setBounds(0, 160, 24, 25);
		moveDownLable.setImage(ImagePathConstant.DOWN_ICON.getImageFromRegistry());

		addListeners(selectAllLabel, selectLabel, disSelectLabel, removeAll);
		sashForm.setWeights(new int[] {297, 274});

		sortAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(allColumns.size() > 1){
					if(!isAllColumnsAsce){
						Collections.sort(allColumns);
						listAllComlumns.setItems(Arrays.copyOf(allColumns.toArray(),allColumns.toArray().length,String[].class));
						isAllColumnsAsce = true;
						sortAll.setImage(ImagePathConstant.SORT_ASC.getImageFromRegistry());
					}
					else{
						Collections.sort(allColumns,comparator);
						listAllComlumns.setItems(Arrays.copyOf(allColumns.toArray(),allColumns.toArray().length,String[].class));
						isAllColumnsAsce = false;
						sortAll.setImage(ImagePathConstant.SORT_DESC.getImageFromRegistry());
					}
				}
			}
		});
		
		sortSelected.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(selectedColumns.size() > 1){
					if(!isSelectedColumnsAsce){
						Collections.sort(selectedColumns);
						listSelectedColumns.setItems(Arrays.copyOf(selectedColumns.toArray(),selectedColumns.toArray().length,String[].class));
						sortSelected.setImage(ImagePathConstant.SORT_ASC.getImageFromRegistry());
						isSelectedColumnsAsce = true;
					}
					else{
						Collections.sort(selectedColumns, comparator);
						listSelectedColumns.setItems(Arrays.copyOf(selectedColumns.toArray(),selectedColumns.toArray().length,String[].class));
						sortSelected.setImage(ImagePathConstant.SORT_DESC.getImageFromRegistry());
						isSelectedColumnsAsce = false;
					}
				}
			}
		});
		
		return container;
		
		
	}

	/**
	 * @param selectAllLabel
	 * @param selectLabel
	 * @param disSelectLabel
	 * @param removeAll
	 * Add listeners to move data up/down/left/right
	 */
	private void addListeners(Label selectAllLabel, Label selectLabel,Label disSelectLabel, Label removeAll) {
		selectLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (listAllComlumns.getSelection().length > 0) {
					for (String string : listAllComlumns.getSelection()) {
						allColumns.remove(string);
 						listAllComlumns.remove(string);
						selectedColumns.add(string);
						listSelectedColumns.add(string);
						okButton.setEnabled(true);
					}
				}
			}
		});		
		
		disSelectLabel.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				if (listSelectedColumns.getSelection().length > 0) {
					for (String string : listSelectedColumns.getSelection()) {
						selectedColumns.remove(string);
						listSelectedColumns.remove(string);
						allColumns.add(string);
						listAllComlumns.add(string);
					}
					if(listSelectedColumns.getItemCount()==0){
						okButton.setEnabled(false);
					}
				}
			}
		});
		
		selectAllLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				for (int i = 0; i < allColumns.size(); i++) {
					listSelectedColumns.add(allColumns.get(i));
				}
				selectedColumns.addAll(allColumns);
				allColumns.clear();
				listAllComlumns.removeAll();
				okButton.setEnabled(true);
			}
		});

		removeAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				for (int i = 0; i < selectedColumns.size(); i++) {
					listAllComlumns.add(selectedColumns.get(i));
				}
				allColumns.addAll(selectedColumns);
				selectedColumns.clear();
				listSelectedColumns.removeAll();
				if(listSelectedColumns.getItemCount()==0){
				okButton.setEnabled(false);
				}
			}
		});
		
		moveDownLable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				int[] indices = listSelectedColumns.getSelectionIndices();
				Map<Integer, String> map = new HashMap<Integer, String>();
				if (Arrays.asList(indices).contains(selectedColumns.size() - 1))
					map.put(indices[indices.length - 1], SKIP);
				for (int index = indices.length - 1; index >= 0; index--) {
					if (indices[index] < listSelectedColumns.getItemCount() - 1 && !map.containsKey(indices[index] + 1)) {
						Collections.swap(selectedColumns, indices[index],indices[index] + 1);
						listSelectedColumns.setItems(Arrays.copyOf(selectedColumns.toArray(),
								selectedColumns.toArray().length, String[].class));
						int[] temp2 = new int[indices.length];
						for (int i = 0; i < indices.length; i++) {
							if (map.containsKey(indices[i]))
								temp2[i] = indices[i];
							else
								temp2[i] = indices[i] + 1;
						}
						listSelectedColumns.setSelection(temp2);
					}else
						map.put(indices[index], SKIP);
				}
			}
		});
		
		moveUpLable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				int[] indices = listSelectedColumns.getSelectionIndices();
				Map<Integer, String> map = new HashMap<Integer, String>();
				if (Arrays.asList(indices).contains(0))
					map.put(0, SKIP);
				for (int index : indices) {
					if (index > 0 && !map.containsKey(index - 1)) {
						Collections.swap(selectedColumns, index, index - 1);
						listSelectedColumns.setItems(Arrays.copyOf(selectedColumns.toArray(),
								selectedColumns.toArray().length, String[].class));
						int[] temp2 = new int[indices.length];
						for (int i = 0; i < indices.length; i++) {
							if (map.containsKey(indices[i]))
								temp2[i] = indices[i];
							else
								temp2[i] = indices[i] - 1;
						}
						listSelectedColumns.setSelection(temp2);
					} else
						map.put(index, SKIP);
				}
			}

		});
		
		listSelectedColumns.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e){
				
				if (listSelectedColumns.getSelection().length > 0) {
					for (String string : listSelectedColumns.getSelection()) {
						selectedColumns.remove(string);
						listSelectedColumns.remove(string);
						allColumns.add(string);
						listAllComlumns.add(string);
					}
					if(listSelectedColumns.getItemCount()==0){
					okButton.setEnabled(false);
					}
				}
			}
		});
		
		listAllComlumns.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (listAllComlumns.getSelection().length > 0) {
					for (String string : listAllComlumns.getSelection()) {
						allColumns.remove(string);
						listAllComlumns.remove(string);
						selectedColumns.add(string);
						listSelectedColumns.add(string);
						okButton.setEnabled(true);
					}
				}
			}

		});
	}
	
	/**
	 * Initialize the window
	 */
	protected Point getInitialSize() {
		return new Point(600, 400);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton=createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,true);
		if(listSelectedColumns.getItemCount()==0){
			okButton.setEnabled(false);
		}
		createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return all columns List
	 * @return List
	 */
	public java.util.List<String> getAllColumns() {
		return allColumns;
	}

	/**
	 * Return list for selected columns
	 * @return List
	 */
	public java.util.List<String> getSelectedColumns() {
		return selectedColumns;
	}
}
