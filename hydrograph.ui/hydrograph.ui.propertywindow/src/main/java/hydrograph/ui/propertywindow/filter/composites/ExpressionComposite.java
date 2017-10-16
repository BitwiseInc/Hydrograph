package hydrograph.ui.propertywindow.filter.composites;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.datastructure.filter.ExpressionData;
import hydrograph.ui.common.util.FilterLogicExternalOperationExpressionUtil;
import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.mapping.ExternalWidgetData;
import hydrograph.ui.expression.editor.launcher.LaunchExpressionEditor;
import hydrograph.ui.expression.editor.util.FieldDataTypeMap;
import hydrograph.ui.propertywindow.filter.FilterExpressionOperationDialog;
import hydrograph.ui.propertywindow.widgets.customwidgets.operational.external.ExpresssionOperationImportExportComposite;
import hydrograph.ui.propertywindow.widgets.customwidgets.operational.external.ImportExportType;

public class ExpressionComposite extends Composite {
	private Text idTextBox;
	private Text logicTextBox;
	private FilterExpressionOperationDialog dialog;
	private Button expressionRadioButton;
	private Button operationRadioButton;
	private ExpressionData expressionDataStructure;
	private InputFieldsComposite inputFieldComposite;

	/**
	 * Create the composite.
	 * 
	 * @param dialog
	 * 
	 * @param parent
	 * @param style
	 */
	public ExpressionComposite(FilterExpressionOperationDialog dialog, Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.dialog = dialog;
		this.expressionDataStructure = dialog.getDataStructure().getExpressionEditorData();

		Composite middleComposite = new Composite(this, SWT.BORDER);
		middleComposite.setLayout(new GridLayout(2, false));
		middleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		inputFieldComposite = new InputFieldsComposite(middleComposite, dialog,SWT.NONE,
				expressionDataStructure.getInputFields());
		GridData gd_composite2 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_composite2.widthHint = 170;
		inputFieldComposite.setLayoutData(gd_composite2);

		createOtherFieldComposite(middleComposite);

		createExternalComposite();

		refresh();
	}

	public void refresh() {
		inputFieldComposite.refresh();
		idTextBox.setText(expressionDataStructure.getId());
		logicTextBox.setText(expressionDataStructure.getExpression());
	}

	private void createExternalComposite() {
		ExpresssionImportExportComposite compositen = new ExpresssionImportExportComposite(this, SWT.BORDER,
				ImportExportType.EXPRESSION, expressionDataStructure.getExternalExpressionData());
		compositen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}

	private void createOtherFieldComposite(Composite middleComposite) {
		Composite middleRightComposite = new Composite(middleComposite, SWT.NONE);
		middleRightComposite.setLayout(new GridLayout(2, false));
		middleRightComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		Label lblSwitchTo = new Label(middleRightComposite, SWT.NONE);
		lblSwitchTo.setText("Switch to");

		createSwitchToComposite(middleRightComposite);

		insertSpace(middleRightComposite);

		Label lblId = new Label(middleRightComposite, SWT.NONE);
		lblId.setText("Expression Id");

		createSimpleIdTextBox(middleRightComposite);

		insertSpace(middleRightComposite);

		Label lblLogic = new Label(middleRightComposite, SWT.NONE);
		lblLogic.setText("Expression ");

		createExpressionEditingTextBox(middleRightComposite);
	}

	private void createSimpleIdTextBox(Composite composite_1) {
		Composite composite = new Composite(composite_1, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite.heightHint = 29;
		composite.setLayoutData(gd_composite);

		idTextBox = new Text(composite, SWT.BORDER);
		idTextBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		idTextBox.setText(expressionDataStructure.getId());
		idTextBox.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				expressionDataStructure.setId(idTextBox.getText());
				dialog.refreshErrorLogs();
			}
		});

	}

	private void createExpressionEditingTextBox(Composite composite_1) {
		Composite composite = new Composite(composite_1, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite.heightHint = 29;
		composite.setLayoutData(gd_composite);

		logicTextBox = new Text(composite, SWT.BORDER);
		logicTextBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		logicTextBox.setEditable(false);
		logicTextBox.setText(expressionDataStructure.getExpressionEditorData().getExpression());

		Button openEditorButton = new Button(composite, SWT.NONE);
		openEditorButton.setText("...");
		openEditorButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ExpressionEditorData expressionEditorData = expressionDataStructure.getExpressionEditorData();
				expressionEditorData.getSelectedInputFieldsForExpression().clear();
				expressionEditorData.getSelectedInputFieldsForExpression().putAll(FieldDataTypeMap.INSTANCE
						.createFieldDataTypeMap(expressionDataStructure.getInputFields(), dialog.getSchemaFields()));
				LaunchExpressionEditor launchExpressionEditor = new LaunchExpressionEditor();
				String oldExpression = expressionEditorData.getExpression();
				launchExpressionEditor.launchExpressionEditor(expressionEditorData, dialog.getSchemaFields(),
						dialog.getComponent().getComponentLabel().getLabelContents());
				if (!StringUtils.equals(expressionEditorData.getExpression(), oldExpression)) {
					dialog.getPropertyDialogButtonBar().enableApplyButton(true);
				}
				logicTextBox.setText(expressionEditorData.getExpression());
				dialog.refreshErrorLogs();
			}
		});

	}

	private void createSwitchToComposite(Composite middleRightComposite) {

		Composite switchToCompsite = new Composite(middleRightComposite, SWT.NONE);
		switchToCompsite.setLayout(new GridLayout(4, false));
		GridData gd_switchToCompsite = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_switchToCompsite.heightHint = 22;
		switchToCompsite.setLayoutData(gd_switchToCompsite);

		createSwitchToExpressionButton(switchToCompsite);

		insertSpace(switchToCompsite);

		createSwitchToOperationButton(switchToCompsite);

	}

	private void createSwitchToOperationButton(Composite switchToCompsite) {
		operationRadioButton = new Button(switchToCompsite, SWT.RADIO);
		operationRadioButton.setText("Operation");
		operationRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog.switchToOperation();
				dialog.getDataStructure().setOperation(true);
				dialog.refreshErrorLogs();
			}
		});
	}

	private void createSwitchToExpressionButton(Composite switchToCompsite) {
		expressionRadioButton = new Button(switchToCompsite, SWT.RADIO);
		expressionRadioButton.setText("Expression");
		expressionRadioButton.setSelection(true);
		expressionRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				dialog.switchToExpression();
//				dialog.getDataStructure().setOperation(false);
//				dialog.refreshErrorLogs();
			}
		});
	}

	public void resetRadioButton() {
		expressionRadioButton.setSelection(true);
		operationRadioButton.setSelection(false);
	}

	private void insertSpace(Composite composite) {
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	class ExpresssionImportExportComposite extends ExpresssionOperationImportExportComposite {

		public ExpresssionImportExportComposite(Composite parent, int style, ImportExportType type,
				ExternalWidgetData externalWidgetData) {
			super(parent, style, type, externalWidgetData);
		}

		@Override
		protected void importButtonSelection(Button widget) {
			FilterLogicExternalOperationExpressionUtil.INSTANCE.importExpression(getFile(), expressionDataStructure, true,
					dialog.getComponent().getComponentName());
			dialog.refreshErrorLogs();
			refresh();
		}

		@Override
		protected void exportButtonSelection(Button widget) {
			FilterLogicExternalOperationExpressionUtil.INSTANCE.exportExpression(getFile(), expressionDataStructure, true);
			dialog.refreshErrorLogs();
		}

		@Override
		protected void interalRadioButtonSelection(Button widget) {
			dialog.refreshErrorLogs();
		}

		@Override
		protected void externalRadioButtonSelection(Button widget) {
			dialog.refreshErrorLogs();
		}
		
		@Override
		protected void onPathModify(ExternalWidgetData externalWidgetData) {
			super.onPathModify(externalWidgetData);
			dialog.refreshErrorLogs();
		}
	}
}
