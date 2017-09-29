package hydrograph.ui.propertywindow.filter.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.datastructure.filter.OperationClassData;
import hydrograph.ui.common.util.FilterLogicExternalOperationExpressionUtil;
import hydrograph.ui.datastructure.property.OperationClassProperty;
import hydrograph.ui.datastructure.property.mapping.ExternalWidgetData;
import hydrograph.ui.propertywindow.filter.FilterExpressionOperationDialog;
import hydrograph.ui.propertywindow.widgets.customwidgets.operational.external.ExpresssionOperationImportExportComposite;
import hydrograph.ui.propertywindow.widgets.customwidgets.operational.external.ImportExportType;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.dialogs.ELTOperationClassDialog;

public class OperationComposite extends Composite {
	private Text idTextBox;
	private Text logicTextBox;
	private FilterExpressionOperationDialog dialog;
	private Button operationRadioButton;
	private Button expressionRadioButton;
	private OperationClassData operationDataStructure;
	private InputFieldsComposite inputFieldComposite;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public OperationComposite(FilterExpressionOperationDialog dialog, Composite parent, int style) {
		super(parent, style);
		this.dialog = dialog;
		this.operationDataStructure = dialog.getDataStructure().getOperationClassData();

		setLayout(new GridLayout(1, false));
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite middleComposite = new Composite(this, SWT.BORDER);
		middleComposite.setLayout(new GridLayout(2, false));
		middleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		inputFieldComposite = new InputFieldsComposite(middleComposite, dialog,SWT.NONE,
				operationDataStructure.getInputFields());
		GridData gd_composite2 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_composite2.widthHint = 170;
		inputFieldComposite.setLayoutData(gd_composite2);

		createOtherFieldComposite(middleComposite);

		createExternalComposite();

		refresh();
	}

	public void refresh() {
		inputFieldComposite.refresh();
		idTextBox.setText(operationDataStructure.getId());
		logicTextBox.setText(operationDataStructure.getQualifiedOperationClassName());
	}

	private void createExternalComposite() {
		OperationImportExportComposite composite = new OperationImportExportComposite(this, SWT.BORDER,
				ImportExportType.OPERATION, operationDataStructure.getExternalOperationClassData());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
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
		lblId.setText("Operation Id");

		createSimpleIdTextBox(middleRightComposite);

		insertSpace(middleRightComposite);

		Label lblLogic = new Label(middleRightComposite, SWT.NONE);
		lblLogic.setText("Operation ");

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
		idTextBox.setText(operationDataStructure.getId());
		idTextBox.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				operationDataStructure.setId(idTextBox.getText());
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
		logicTextBox.setText(operationDataStructure.getQualifiedOperationClassName());

		Button openEditorButton = new Button(composite, SWT.NONE);
		openEditorButton.setText("...");
		openEditorButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ELTOperationClassDialog eltOperationClassDialog = new ELTOperationClassDialog(
						Display.getCurrent().getActiveShell(), dialog.getPropertyDialogButtonBar(),
						createDSForClassWindow(), dialog.getWidgetConfig(), dialog.getComponent().getComponentName());
				eltOperationClassDialog.open();
				updateOperationDS(eltOperationClassDialog);
				if (eltOperationClassDialog.isYesPressed()) {
					dialog.pressOK();
				} else if (eltOperationClassDialog.isNoPressed()) {
					dialog.pressCancel();
				}
				dialog.refreshErrorLogs();
			}

		});

	}

	private void updateOperationDS(ELTOperationClassDialog eltOperationClassDialog) {
		operationDataStructure.setQualifiedOperationClassName(
				eltOperationClassDialog.getOperationClassProperty().getOperationClassPath());
		logicTextBox.setText(operationDataStructure.getQualifiedOperationClassName());
		// if (eltOperationClassDialog.isCancelPressed() &&
		// (!(eltOperationClassDialog.isApplyPressed()))) {
		// operationDataStructure.setNameValuePropertyList(oldOperationClassProperty.getNameValuePropertyList());
		// }

	}

	private OperationClassProperty createDSForClassWindow() {
		OperationClassProperty classProperty = new OperationClassProperty(
				operationDataStructure.getQualifiedOperationClassName(), operationDataStructure.getClassProperties(),
				operationDataStructure.getSimpleOperationClassName());

		return classProperty;
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
		operationRadioButton.setSelection(true);
		operationRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				dialog.switchToOperation();
//				dialog.getDataStructure().setOperation(true);
//				dialog.refreshErrorLogs();
			}
		});
	}

	private void createSwitchToExpressionButton(Composite switchToCompsite) {
		expressionRadioButton = new Button(switchToCompsite, SWT.RADIO);
		expressionRadioButton.setText("Expression");
		expressionRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog.switchToExpression();
				dialog.getDataStructure().setOperation(false);
				dialog.refreshErrorLogs();
			}
		});
	}

	public void resetRadioButton() {
		operationRadioButton.setSelection(true);
		expressionRadioButton.setSelection(false);
	}

	private void insertSpace(Composite composite) {
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	class OperationImportExportComposite extends ExpresssionOperationImportExportComposite {

		public OperationImportExportComposite(Composite parent, int style, ImportExportType type,
				ExternalWidgetData externalWidgetData) {
			super(parent, style, type, externalWidgetData);
		}

		@Override
		protected void importButtonSelection(Button widget) {
			FilterLogicExternalOperationExpressionUtil.INSTANCE.importOperation(getFile(), operationDataStructure, true,
					dialog.getComponent().getComponentName());
			refresh();
			dialog.refreshErrorLogs();
		}

		@Override
		protected void exportButtonSelection(Button widget) {
			FilterLogicExternalOperationExpressionUtil.INSTANCE.exportOperation(getFile(), operationDataStructure, true);
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