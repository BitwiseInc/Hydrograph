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

import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.Messages;
import hydrograph.ui.expression.editor.datastructure.ClassDetails;
import hydrograph.ui.expression.editor.datastructure.MethodDetails;
import hydrograph.ui.expression.editor.repo.ClassRepo;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class CategoriesComposite extends Composite {
	public static final String KEY_FOR_ACCESSING_CLASS_FROM_METHOD_LIST = "class";
	private List classNamelist;
	private List methodList;
	private Browser descriptionStyledText;
	private FunctionsComposite functionsComposite;
	private CategoriesUpperComposite categoriesUpperComposite;
	private Text functionSearchTextBox;
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CategoriesComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		categoriesUpperComposite = new CategoriesUpperComposite(this, SWT.BORDER);
		categoriesUpperComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		classNamelist = new List(this, SWT.BORDER | SWT.V_SCROLL);
		classNamelist.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		loadClassesFromRepo();
		addListnersToClassNameList(classNamelist);
		addSelectionListnerToClassNameList();
		categoriesUpperComposite.setClassNameList(classNamelist);
	}

	private void addSelectionListnerToClassNameList() {
		classNamelist.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(classNamelist.getItemCount()!=0 && !StringUtils.startsWith(classNamelist.getItem(0),Messages.CANNOT_SEARCH_INPUT_STRING)){
				ClassDetails classDetails = (ClassDetails) classNamelist.getData(String.valueOf(classNamelist
						.getSelectionIndex()));
				if (classDetails != null && StringUtils.isNotBlank(classDetails.getJavaDoc())) {
					descriptionStyledText.setText(classDetails.getJavaDoc());
				} else {
					descriptionStyledText.setText(Messages.JAVA_DOC_NOT_AVAILABLE);
				}
				}else
				{
					methodList.removeAll();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {/* Do- nothing */
			}
		});
	}

	private void loadClassesFromRepo() {
		for (ClassDetails classDetails : ClassRepo.INSTANCE.getClassList()) {
			classNamelist.add(classDetails.getDisplayName());
			classNamelist.setData(String.valueOf(classNamelist.getItemCount() - 1), classDetails);
		}
		if(classNamelist.getItemCount()==0){
			categoriesUpperComposite.getSearchTextBox().setEnabled(false);
		}
	}

	private void addListnersToClassNameList(final List classNamelist) {
		classNamelist.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(classNamelist.getItemCount()!=0 && !StringUtils.startsWith(classNamelist.getItem(0),Messages.CANNOT_SEARCH_INPUT_STRING)){
				ClassDetails classDetails = (ClassDetails) classNamelist.getData(String.valueOf(classNamelist
						.getSelectionIndex()));
				methodList.removeAll();
				functionSearchTextBox.setEnabled(true);
				if (classDetails != null) {
					for (MethodDetails methodDetails : classDetails.getMethodList()) {
						methodList.add(methodDetails.getSignature());
						methodList.setData(String.valueOf(methodList.getItemCount() - 1), methodDetails);
						methodList.setData(KEY_FOR_ACCESSING_CLASS_FROM_METHOD_LIST,classDetails);
					}
				}
				if(functionsComposite!=null){
					functionsComposite.refresh();
				}
				}else{
					functionSearchTextBox.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setMethodList(List methodList) {
		this.methodList = methodList;
	}

	public void refreshList() {
		functionsComposite.refresh();
		classNamelist.removeAll();
		methodList.removeAll();
		descriptionStyledText.setText(Constants.EMPTY_STRING);
		loadClassesFromRepo();
	}

	public void setDescriptionStyledText(Browser descriptionStyledText) {
		this.descriptionStyledText = descriptionStyledText;
	}

	public void setFunctionsComposite(FunctionsComposite functionsComposite) {
		this.functionsComposite=functionsComposite;
	}

	public List getClassNamelist() {
		return classNamelist;
	}
	
	/**
	 * Clears method-list and description text box.
	 * 
	 */
	public void clearDescriptionAndMethodList() {
		functionsComposite.refresh();
		methodList.removeAll();
		descriptionStyledText.setText(Constants.EMPTY_STRING);
	}

	/**
	 * Links function-search textbox to Categories composite. 
	 * @param functionSearchTextBox
	 */
	public void setFunctionSearchBox(Text functionSearchTextBox) {
		this.functionSearchTextBox=functionSearchTextBox;
		categoriesUpperComposite.setFunctionSearchBox(functionSearchTextBox);
	}
}
