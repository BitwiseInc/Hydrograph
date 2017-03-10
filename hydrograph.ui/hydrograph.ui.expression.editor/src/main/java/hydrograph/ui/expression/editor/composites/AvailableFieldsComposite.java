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

package hydrograph.ui.expression.editor.composites;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.Messages;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;

/**
 * @author Bitwise
 * 
 * This class creates available fields section
 * in expression editor.
 *
 */
public class AvailableFieldsComposite extends Composite {
	private Table table;
	private TableColumn availableFieldsNameColumn ;
	private List<String> inputFields;
	private TableViewer tableViewer;
	private StyledText expressionEditor;
	private Text searchTextBox;
	private Composite headerComposite_1;
	private TableColumn availableFieldsDataTypeColumn;
	private Map<String,Class<?>> fieldMap;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AvailableFieldsComposite(Composite parent, int style , Map<String,Class<?>> fieldMap) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		this.inputFields=new ArrayList<>(fieldMap.keySet());
		this.fieldMap=fieldMap;
		headerComposite_1 = new Composite(this, SWT.NONE);
		headerComposite_1.setLayout(new GridLayout(2, false));
		GridData gd_headerComposite_1 = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_headerComposite_1.widthHint = 437;
		gd_headerComposite_1.heightHint = 39;
		headerComposite_1.setLayoutData(gd_headerComposite_1);
		
		Label lblAvailableFields = new Label(headerComposite_1, SWT.NONE);
		lblAvailableFields.setText(Messages.AVAILABLE_INPUT_FIELDS);
		
		createSearchTextBox(headerComposite_1);
		
		tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		availableFieldsNameColumn = new TableColumn(table, SWT.NONE);
		availableFieldsNameColumn.setText(Messages.FIELD_NAME);
		availableFieldsDataTypeColumn = new TableColumn(table, SWT.NONE);
		availableFieldsDataTypeColumn.setText(Messages.DATA_TYPE);
		ExpressionEditorUtil.INSTANCE.addDragSupport(table);
		loadData();
		addControlListener();
		addDoubleClickListener();
		
	}

	private void addDoubleClickListener() {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if(table.getSelectionIndex()!=-1){
					expressionEditor.insert(table.getItem(table.getSelectionIndex()).getText());
				}
			}
		});
	}

	private void createSearchTextBox(Composite headerComposite) {
		searchTextBox = new Text(headerComposite, SWT.BORDER);
		GridData gd_searchTextBox = new GridData(SWT.RIGHT, SWT.CENTER, true, true, 0, 0);
		gd_searchTextBox.widthHint = 191;
		searchTextBox.setLayoutData(gd_searchTextBox);
		searchTextBox.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 128,128,128));
		searchTextBox.setText(Constants.DEFAULT_SEARCH_TEXT);
		addListnersToSearchTextBox();
		ExpressionEditorUtil.INSTANCE.addFocusListenerToSearchTextBox(searchTextBox);
	}

	private void addListnersToSearchTextBox() {
		searchTextBox.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if(!StringUtils.equals(Constants.DEFAULT_SEARCH_TEXT, searchTextBox.getText())){
				table.removeAll();
				for(String field:inputFields){
					if(StringUtils.containsIgnoreCase(field,searchTextBox.getText())){
						TableItem tableItem = new TableItem(table, SWT.NONE);
						tableItem.setText(0,field);
						tableItem.setText(1, fieldMap.get(field).getSimpleName());
					}
				}
				
				if(table.getItemCount()==0 && StringUtils.isNotBlank(searchTextBox.getText())){
					new TableItem(table, SWT.NONE).setText(Messages.CANNOT_SEARCH_INPUT_STRING+searchTextBox.getText());
				}
			}
				
			}
		});
	}

	private void addControlListener() {
		table.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
                Table table = (Table) e.widget;
                table.getColumn(0).setWidth(table.getClientArea().width / 2);
                table.getColumn(1).setWidth((table.getClientArea().width / 2)-1);
            }
		});
	}
	
	private void loadData() {
		if (inputFields != null) {
			for (String field : inputFields) {
				TableItem tableItem=new TableItem(table, SWT.NONE);
				tableItem.setText(0,field);
				tableItem.setText(1,fieldMap.get(field).getSimpleName());
			}
		}
		if(table.getItemCount()==0){
			searchTextBox.setEnabled(false);
		}
	}
	
	@Override
	protected void checkSubclass() {
		
	}

	/**
	 * @param expressionEditor the expressionEditor to set
	 */
	public void setExpressionEditor(StyledText expressionEditor) {
		this.expressionEditor = expressionEditor;
	}

	
}
