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

package hydrograph.ui.expression.editor.dialogs;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.expression.editor.Constants;
import hydrograph.ui.expression.editor.Messages;
import hydrograph.ui.expression.editor.color.manager.JavaLineStyler;
import hydrograph.ui.expression.editor.composites.AvailableFieldsComposite;
import hydrograph.ui.expression.editor.composites.CategoriesComposite;
import hydrograph.ui.expression.editor.composites.DescriptionComposite;
import hydrograph.ui.expression.editor.composites.ExpressionEditorComposite;
import hydrograph.ui.expression.editor.composites.FunctionsComposite;
import hydrograph.ui.expression.editor.repo.ClassRepo;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;

/**
 * @author Bitwise
 *
 *This class creates window for expression editor. 
 */
public class ExpressionEditorDialog extends Dialog {

	public static final String FIELD_DATA_TYPE_MAP = "fieldMap";
	public static final String COMPONENT_NAME_KEY = "component-name";
	public static final String INPUT_FILEDS_SCHEMA_KEY = "input-field-schema";
	public static final String TITLE_SUFFIX_KEY = "dialog-title-key";
	
	private StyledText expressionEditorTextBox;
	private AvailableFieldsComposite availableFieldsComposite;
	private ExpressionEditorComposite expressionEditorComposite;
	private CategoriesComposite categoriesComposite;
	private FunctionsComposite functionsComposite;
	private DescriptionComposite descriptionComposite;
	private List<String> selectedInputFields;
	private JavaLineStyler javaLineStyler;
	private String newExpressionText;
	private String oldExpressionText;
	private Map<String, Class<?>> fieldMap;
	private Composite container;
	private SashForm containerSashForm;
	private SashForm upperSashForm;
	private ExpressionEditorData expressionEditorData;
	private List<FixedWidthGridRow> inputFieldSchema;
	private String windowTitleSuffix;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param inputFieldSchema 
	 * @param windowTitleSuffix 
	 */
	public ExpressionEditorDialog(Shell parentShell, ExpressionEditorData expressionEditorData, List<FixedWidthGridRow> inputFieldSchema, String windowTitleSuffix) {
		
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL);
		this.fieldMap = expressionEditorData.getCombinedFieldDatatypeMap();
		this.selectedInputFields = new ArrayList<>(fieldMap.keySet());
		javaLineStyler = new JavaLineStyler(selectedInputFields);
		this.oldExpressionText = expressionEditorData.getExpression();
		this.expressionEditorData = expressionEditorData;
		this.inputFieldSchema=inputFieldSchema;
		this.windowTitleSuffix=windowTitleSuffix;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.getShell().setText(getTitle());

		containerSashForm = new SashForm(container, SWT.VERTICAL);
		containerSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite upperComposite = new Composite(containerSashForm, SWT.BORDER);
		upperComposite.setLayout(new GridLayout(1, false));

		upperSashForm = new SashForm(upperComposite, SWT.NONE);
		upperSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		availableFieldsComposite = new AvailableFieldsComposite(upperSashForm, SWT.NONE, fieldMap);

		expressionEditorComposite = new ExpressionEditorComposite(upperSashForm, SWT.NONE, javaLineStyler);
		this.expressionEditorTextBox = expressionEditorComposite.getExpressionEditor();
		upperSashForm.setWeights(new int[] { 288, 576 });

		availableFieldsComposite.setExpressionEditor(expressionEditorTextBox);
		
		Composite composite = new Composite(containerSashForm, SWT.BORDER);
		composite.setLayout(new GridLayout(1, false));

		SashForm lowerSashForm = new SashForm(composite, SWT.NONE);
		lowerSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		categoriesComposite = new CategoriesComposite(lowerSashForm, SWT.NONE);
		functionsComposite = new FunctionsComposite(lowerSashForm,expressionEditorTextBox ,categoriesComposite, SWT.NONE);
		descriptionComposite = new DescriptionComposite(lowerSashForm, functionsComposite, categoriesComposite,
				SWT.NONE);

		containerSashForm.setWeights(new int[] { 1, 1 });

		intializeWidgets();
		return container;
	}

	private String getTitle() {
		StringBuffer title=new StringBuffer(Messages.EXPRESSION_EDITOR_TITLE);
		if(StringUtils.isNotBlank(windowTitleSuffix)){
			title.append(Constants.SPACE);
			title.append(Constants.DASH);
			title.append(Constants.SPACE);
			title.append(windowTitleSuffix);
		}
		return title.toString();
	}

	private void intializeWidgets() {
		expressionEditorTextBox.setFocus();
		expressionEditorTextBox.setText(oldExpressionText);
		expressionEditorTextBox.setData(FIELD_DATA_TYPE_MAP, fieldMap);
		expressionEditorTextBox.setData(COMPONENT_NAME_KEY, expressionEditorData.getComponentName());
		expressionEditorTextBox.setData(INPUT_FILEDS_SCHEMA_KEY,inputFieldSchema);
		expressionEditorTextBox.setData(TITLE_SUFFIX_KEY,windowTitleSuffix);
		getShell().setMaximized(true);
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		container.getShell().layout(true, true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Point newSize = container.getShell().computeSize(screenSize.width, screenSize.height, true);
		container.getShell().setSize(newSize);
		return newSize;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#close()
	 */
	public boolean close() {
		if (preCloseActivity()) {
			ExpressionEditorUtil.validateExpression(expressionEditorTextBox.getText(),(Map<String, Class<?>>) expressionEditorTextBox
					.getData(ExpressionEditorDialog.FIELD_DATA_TYPE_MAP),expressionEditorData);
			ClassRepo.INSTANCE.flusRepo();
			expressionEditorComposite.getViewer().getSourceViewerDecorationSupport().dispose();
			return super.close();
		}
		return false;
	}

	@Override
	protected void okPressed() {
		oldExpressionText=expressionEditorTextBox.getText();
		newExpressionText=oldExpressionText;
		super.okPressed();
	}
	
	private boolean preCloseActivity() {
		if (!StringUtils.equals(oldExpressionText, expressionEditorTextBox.getText())) {
			if (confirmToExitWithoutSave()) {
				return true;
			}else
				return false;
		}else{
			return true;
		}
	}

	private boolean confirmToExitWithoutSave() {
		return MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "Exiting expression editor",Messages.MESSAGE_TO_EXIT_WITHOUT_SAVE);
	}

	/**
	 * @return the expressionEditorTextBox
	 */
	public StyledText getExpressionEditorTextBox() {
		return expressionEditorTextBox;
	}

	/**
	 * @return the newExpressionText
	 */
	public String getNewExpressionText() {
		return newExpressionText;
	}

	
	/**
	 * @return the containerSashForm
	 */
	public SashForm getContainerSashForm() {
		return containerSashForm;
	}

	

	/**
	 * @return the upperSashForm
	 */
	public SashForm getUpperSashForm() {
		return upperSashForm;
	}

	

	
}
