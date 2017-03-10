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
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.Messages;
import hydrograph.ui.expression.editor.datastructure.ClassDetails;
import hydrograph.ui.expression.editor.datastructure.MethodDetails;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;

public class FunctionsUpperComposite extends Composite {
	private static final String TITLE = "Functions";
	private List methodList;
	private Text functionSearchTextBox;
	private Browser descriptionStyledText;
	private List classNameList;
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public FunctionsUpperComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblFunctions = new Label(this, SWT.NONE);
		lblFunctions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		lblFunctions.setText(TITLE);

		createSearchTextBox(this);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setMethodList(List methodList) {
		this.methodList = methodList;
	}

	private void createSearchTextBox(Composite headerComposite) {
		functionSearchTextBox = new Text(headerComposite, SWT.BORDER);
		GridData gd_searchTextBox = new GridData(SWT.RIGHT, SWT.CENTER, true, true, 0, 0);
		gd_searchTextBox.widthHint = 150;
		functionSearchTextBox.setLayoutData(gd_searchTextBox);
		functionSearchTextBox.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 128, 128, 128));
		functionSearchTextBox.setText(Constants.DEFAULT_SEARCH_TEXT);
		functionSearchTextBox.setEnabled(false);
		addListnersToSearchTextBox();
		ExpressionEditorUtil.INSTANCE.addFocusListenerToSearchTextBox(functionSearchTextBox);
	}

	private void addListnersToSearchTextBox() {
		functionSearchTextBox.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (!StringUtils.equals(Constants.DEFAULT_SEARCH_TEXT, functionSearchTextBox.getText()) && (classNameList.getSelectionCount()!=0 &&
						!StringUtils.startsWith(classNameList.getItem(0), Messages.CANNOT_SEARCH_INPUT_STRING))) {
					methodList.removeAll();
					ClassDetails classDetails = (ClassDetails) methodList
							.getData(CategoriesComposite.KEY_FOR_ACCESSING_CLASS_FROM_METHOD_LIST);
					if (classDetails != null) {
						for (MethodDetails methodDetails : classDetails.getMethodList()) {
							if (StringUtils.containsIgnoreCase(methodDetails.getMethodName(), functionSearchTextBox.getText())) {
								methodList.add(methodDetails.getSignature());
								methodList.setData(String.valueOf(methodList.getItemCount() - 1), methodDetails);
							}
						}
					}
					if(methodList.getItemCount()==0 && StringUtils.isNotBlank(functionSearchTextBox.getText())){
						methodList.add(Messages.CANNOT_SEARCH_INPUT_STRING+functionSearchTextBox.getText());
					}
					descriptionStyledText.setText(Constants.EMPTY_STRING);
				}
			}
		});
	}

	public void refresh() {
		functionSearchTextBox.setText(Constants.DEFAULT_SEARCH_TEXT);
	}

	public Text getFunctionSearchTextBox() {
		return functionSearchTextBox;
	}
	
	public void setDescriptionText(Browser descriptionStyledText) {
		this.descriptionStyledText=descriptionStyledText;
	}

	public void setClassNameList(List classNamelist) {
		this.classNameList=classNamelist;
	}

}
