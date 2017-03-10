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

package hydrograph.ui.expression.editor.evaluate;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.swt.customwidget.HydroGroup;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.Messages;
import hydrograph.ui.expression.editor.dialogs.ExpressionEditorDialog;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;

/**
 * @author Bitwise
 * This class creates Evaluate Dialog of expression Editor
 *
 */
public class EvaluateDialog extends Dialog {
	private static final String OUTPUT_COSOLE_ERROR_PREFIX = "Error : ";
	private static final String OUTPUT_CONSOLE_PREFIX = "Output : ";
	private StyledText outputConsole;
	private StyledText expressionEditor;
	private Composite previousExpressionEditorComposite;
	private Button evaluateButton;
	private EvaluateDialog evaluateDialog;
	private Text searchTextBox;
	private EvalDialogFieldTable evalDialogFieldTable;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public EvaluateDialog(Shell parentShell,StyledText expressionEditor) {
		super(parentShell);
		setShellStyle(SWT.CLOSE|SWT.RESIZE);
		this.expressionEditor=expressionEditor;
		previousExpressionEditorComposite=expressionEditor.getParent();
		evaluateDialog=this;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.getShell().setText(getTitle());
		
		SashForm sashForm = new SashForm(container, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0));
		
		HydroGroup expressionEditorcomposite = new HydroGroup(sashForm, SWT.NORMAL);
		expressionEditorcomposite.setHydroGroupText(Messages.EVALUATE_EXPRESSION_EDITOR_GROUP_HEADER);
		GridLayout gd = new GridLayout(1, false);
		expressionEditorcomposite.getHydroGroupClientArea().setLayout(gd);
		expressionEditor.setParent(expressionEditorcomposite.getHydroGroupClientArea());
		expressionEditorcomposite.setLayout(new GridLayout(1, false));
		
		HydroGroup fieldTableComposite = new HydroGroup(sashForm, SWT.NORMAL);
		fieldTableComposite.setLayout(new GridLayout(1, false));
		fieldTableComposite.setHydroGroupText(Messages.EVALUATE_FIELD_NAMES_GROUP_HEADER);
		fieldTableComposite.getHydroGroupClientArea().setLayout(gd);
		createSearchTextBox(fieldTableComposite.getHydroGroupClientArea());


		evalDialogFieldTable = new EvalDialogFieldTable().createFieldTable(fieldTableComposite.getHydroGroupClientArea(),
				(Map<String, Class<?>>) expressionEditor.getData(ExpressionEditorDialog.FIELD_DATA_TYPE_MAP),
				(List<FixedWidthGridRow>) expressionEditor.getData(ExpressionEditorDialog.INPUT_FILEDS_SCHEMA_KEY));
		
		
		HydroGroup errorComposite = new HydroGroup(sashForm, SWT.NORMAL);
		errorComposite.setLayout(new GridLayout(1, false));
		errorComposite.setHydroGroupText(Messages.EVALUATE_OUTPUT_CONSOLE_GROUP_HEADER);
		errorComposite.getHydroGroupClientArea().setLayout(gd);	
		createOutputConsole(errorComposite.getHydroGroupClientArea());
		sashForm.setWeights(new int[] {121, 242, 140});
		
		return container;
	}

	private String getTitle() {
		String titleSuffix=(String) expressionEditor.getData(ExpressionEditorDialog.TITLE_SUFFIX_KEY);
		StringBuffer title=new StringBuffer(Messages.EXPRESSION_EDITOR_EVALUATE_DIALOG_TITLE);
		if(StringUtils.isNotBlank(titleSuffix)){
			title.append(Constants.SPACE);
			title.append(Constants.DASH);
			title.append(Constants.SPACE);
			title.append(titleSuffix);
		}
		return title.toString();
	}

	private void createOutputConsole(Composite errorComposite) {
		outputConsole = new StyledText(errorComposite, SWT.BORDER|SWT.V_SCROLL|SWT.WRAP);
		outputConsole.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		outputConsole.setEditable(false);
	}

	private void createSearchTextBox(Composite fieldTableComposite) {
		searchTextBox = new Text(fieldTableComposite, SWT.BORDER);
		searchTextBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		searchTextBox.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 128,128,128));
		searchTextBox.setText(Constants.DEFAULT_SEARCH_TEXT);
		ExpressionEditorUtil.INSTANCE.addFocusListenerToSearchTextBox(searchTextBox);
		searchTextBox.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				evalDialogFieldTable.getTableViewer().resetFilters();
				if(!StringUtils.equals(Constants.DEFAULT_SEARCH_TEXT, searchTextBox.getText())){
					ViewerFilter filter=new ViewerFilter() {
						@Override
						public boolean select(Viewer viewer, Object parentElement, Object element) {
							if(element!=null && element instanceof FieldNameAndValue){
								FieldNameAndValue fieldNameAndValue=(FieldNameAndValue) element;
								if(StringUtils.containsIgnoreCase(fieldNameAndValue.getFieldName(),searchTextBox.getText()) ){
									return true;
								}
							}
							return false;
						}
					};
					ViewerFilter[] filters={filter};
					evalDialogFieldTable.getTableViewer().setFilters(filters);
				}
			}
		});
	}

	

	void showOutput(String output) {
		StringBuffer buffer=new StringBuffer();
		buffer.append(OUTPUT_CONSOLE_PREFIX+output);
		outputConsole.setText(buffer.toString());
		outputConsole.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
	}

	void showError(String error) {
		StringBuffer buffer=new StringBuffer();
		buffer.append(OUTPUT_COSOLE_ERROR_PREFIX+error);
		outputConsole.setText(buffer.toString());
		outputConsole.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
	}
	
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	evaluateButton = createButton(parent, IDialogConstants.NO_ID, "Evaluate", false);
	evaluateButton.setFocus();
	addListenerToEvaluateButton();
	createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, false);
	}

	private void addListenerToEvaluateButton() {
		evaluateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchTextBox.setText(Constants.DEFAULT_SEARCH_TEXT);
				evalDialogFieldTable.getTableViewer().resetFilters();
				EvaluateExpression evaluateExpression=new EvaluateExpression(expressionEditor,outputConsole,evaluateDialog);
				if(evaluateExpression.isValidExpression()){
					 try {
						Object[] returnObject=evalDialogFieldTable.validateDataTypeValues();
						String object=evaluateExpression.invokeEvaluateFunctionFromJar(expressionEditor.getText(),(String[]) returnObject[0],(Object[])returnObject[1]);
						if(object!=null){
							showOutput(object);
					}
				} catch (InvalidDataTypeValueException invalidDataTypeValueException) {
					showError(invalidDataTypeValueException.getMessage());
				}
			}
		}});
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(554, 600);
	}
	
	@Override
	public boolean close() {
		expressionEditor.setParent(previousExpressionEditorComposite);
		previousExpressionEditorComposite.setSize(previousExpressionEditorComposite.getSize().x+1,
				previousExpressionEditorComposite.getSize().y+1);
		previousExpressionEditorComposite.setSize(previousExpressionEditorComposite.getSize().x-1,
				previousExpressionEditorComposite.getSize().y-1);
		expressionEditor.setFocus();
		return super.close();
	}
	
	@Override
	public int open() {
		return super.open();
	}

}