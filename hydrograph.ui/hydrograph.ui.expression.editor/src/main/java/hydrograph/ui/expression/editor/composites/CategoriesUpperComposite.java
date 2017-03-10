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

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.Messages;
import hydrograph.ui.expression.editor.datastructure.ClassDetails;
import hydrograph.ui.expression.editor.dialogs.AddCategoreisDialog;
import hydrograph.ui.expression.editor.repo.ClassRepo;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;

public class CategoriesUpperComposite extends Composite {
	private static final String ADD_CATEGORIES = "Add";
	private CategoriesComposite categoriesComposite;
	private Button btnAddPackages;
	private Text searchTextBox;
	private List classNameList;
	private Text functionSearchTextBox;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param classNamelist 
	 * @param style
	 */
	public CategoriesUpperComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		Label lblCategories = new Label(this, SWT.NONE);
		lblCategories.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblCategories.setText("Categories");
		categoriesComposite=(CategoriesComposite)parent;
		btnAddPackages = new Button(this, SWT.NONE);
		btnAddPackages.setText(ADD_CATEGORIES);
		btnAddPackages.setVisible(false);
		createSearchTextBox(this);
		
		addListnersToAddPackageButton(parent);
	}

	private void createSearchTextBox(Composite headerComposite) {
		searchTextBox = new Text(headerComposite, SWT.BORDER);
		GridData gd_searchTextBox = new GridData(SWT.RIGHT, SWT.CENTER, false, true, 0, 0);
		gd_searchTextBox.widthHint = 150;
		searchTextBox.setLayoutData(gd_searchTextBox);
		searchTextBox.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry(128,128,128));
		searchTextBox.setText(Constants.DEFAULT_SEARCH_TEXT);
		addListnersToSearchTextBox();
		ExpressionEditorUtil.INSTANCE.addFocusListenerToSearchTextBox(searchTextBox);
	}
	
	private void addListnersToSearchTextBox() {
		searchTextBox.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if(!StringUtils.equals(Constants.DEFAULT_SEARCH_TEXT, searchTextBox.getText())){
				classNameList.removeAll();
				for(ClassDetails classDetails:ClassRepo.INSTANCE.getClassList()){
						if(StringUtils.containsIgnoreCase(classDetails.getcName(),searchTextBox.getText())){
							classNameList.add(classDetails.getDisplayName());
							classNameList.setData(String.valueOf(classNameList.getItemCount() - 1), classDetails);
						}
					}
				if(classNameList.getItemCount()==0 && StringUtils.isNotBlank(searchTextBox.getText())){
					classNameList.add(Messages.CANNOT_SEARCH_INPUT_STRING+searchTextBox.getText());
				}
				categoriesComposite.clearDescriptionAndMethodList();
				}
				functionSearchTextBox.setEnabled(false);
			}
		});
		
	}

	private void addListnersToAddPackageButton(final Composite parent) {
		btnAddPackages.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddCategoreisDialog addCategoreisDialog=new AddCategoreisDialog(Display.getCurrent().getActiveShell());
				int returnCode=addCategoreisDialog.open();
				if(returnCode==0){
					((CategoriesComposite)parent).refreshList();
					refresh();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
	}

	protected void refresh() {
		searchTextBox.setText(Constants.DEFAULT_SEARCH_TEXT);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setClassNameList(List classNamelist) {
		this.classNameList=classNamelist;		
	}

	/**
	 * Links function-search textbox to Categories upper composite. 
	 * @param functionSearchTextBox
	 */
	public void setFunctionSearchBox(Text functionSearchTextBox) {
		this.functionSearchTextBox=functionSearchTextBox;
	}

	/**
	 * Returns categories search textbox
	 * 
	 * @return
	 */
	public Text getSearchTextBox() {
		return searchTextBox;
	}
}
