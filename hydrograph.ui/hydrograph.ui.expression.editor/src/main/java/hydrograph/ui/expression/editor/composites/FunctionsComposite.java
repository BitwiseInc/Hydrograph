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
import hydrograph.ui.expression.editor.datastructure.MethodDetails;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class FunctionsComposite extends Composite {

	private List methodList;
	protected Browser descriptionStyledText;
	private FunctionsUpperComposite functionUppersComposite;
	private StyledText expressionEditorTextBox;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param expressionEditorTextBox 
	 * @param categoriesComposite
	 * @param style
	 */
	public FunctionsComposite(Composite parent, StyledText expressionEditorTextBox, CategoriesComposite categoriesComposite, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		this.expressionEditorTextBox=expressionEditorTextBox;
		functionUppersComposite = new FunctionsUpperComposite(this, SWT.BORDER);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite.heightHint = 35;
		functionUppersComposite.setLayoutData(gd_composite);

		methodList = new List(this, SWT.BORDER | SWT.V_SCROLL);
		methodList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		addDragSupport();

		linkFunctionAndClassComposite(categoriesComposite);
		functionUppersComposite.setMethodList(methodList);
		functionUppersComposite.setClassNameList(categoriesComposite.getClassNamelist());
		
		addListnersToMethodList(methodList);
		addDoubleClickListner(methodList);
	}

	private void addDoubleClickListner(final List methodList) {
		methodList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if(methodList.getSelectionIndex()!=-1 && !StringUtils.startsWith(methodList.getItem(methodList.getSelectionIndex()),Messages.CANNOT_SEARCH_INPUT_STRING)){
				MethodDetails methodDetails = (MethodDetails) methodList.getData(String.valueOf(methodList
						.getSelectionIndex()));
					if(methodDetails !=null && StringUtils.isNotBlank(methodDetails.getPlaceHolder())){
						expressionEditorTextBox.insert(methodDetails.getPlaceHolder());
					}
		}
			}
		});
	}

	private void addDragSupport() {
		ExpressionEditorUtil.INSTANCE.getDragSource(methodList).addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
				if (methodList.getItemCount() != 0
						&& !StringUtils.startsWith(methodList.getItem(0),Messages.CANNOT_SEARCH_INPUT_STRING)) {
				MethodDetails methodDetails = (MethodDetails) methodList.getData(String.valueOf(methodList
						.getSelectionIndex()));
				event.data = methodDetails.getPlaceHolder();
			}else
				event.data=StringUtils.EMPTY+"#"+StringUtils.EMPTY;
			}
		});
	}

	private void linkFunctionAndClassComposite(CategoriesComposite categoriesComposite) {
		categoriesComposite.setMethodList(methodList);
		categoriesComposite.setFunctionsComposite(this);
		categoriesComposite.setFunctionSearchBox(functionUppersComposite.getFunctionSearchTextBox());
	}

	private void addListnersToMethodList(final List methodsList) {
		methodsList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performSelectionActivity(methodsList);
			}
		});
		
		methodsList.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {/*Do-Nothing*/}
			@Override
			public void mouseDoubleClick(MouseEvent e) {/*Do-Nothing*/}
			@Override
			public void mouseDown(MouseEvent e) {
				performSelectionActivity(methodsList);
			}
			
		});
	}

	private void performSelectionActivity(List methodsList){
		if (methodsList.getItemCount() != 0
				&& !StringUtils.startsWith(methodsList.getItem(0),Messages.CANNOT_SEARCH_INPUT_STRING)) {
			MethodDetails methodDetails = (MethodDetails) methodsList.getData(String.valueOf(methodsList
					.getSelectionIndex()));
			if (methodDetails != null && StringUtils.isNotBlank(methodDetails.getJavaDoc())) {
				descriptionStyledText.setText(methodDetails.getJavaDoc());
			} else {
				descriptionStyledText.setText(Messages.JAVA_DOC_NOT_AVAILABLE);
			}
		}else{
			
		}
			
	}
	
	public void setDescriptionStyledText(Browser descriptionStyledText) {
		this.descriptionStyledText = descriptionStyledText;
		functionUppersComposite.setDescriptionText(descriptionStyledText);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void refresh() {
		functionUppersComposite.refresh();
	}

}
