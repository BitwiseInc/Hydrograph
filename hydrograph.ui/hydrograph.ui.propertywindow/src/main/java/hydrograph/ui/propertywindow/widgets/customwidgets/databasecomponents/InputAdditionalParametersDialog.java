package hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.utils.Utils;
import hydrograph.ui.propertywindow.widgets.dialogs.FieldDialogForDBComponents;
import hydrograph.ui.propertywindow.widgets.listeners.ExtraURLParameterValidationForDBComponents;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.listeners.VerifyNumericandParameterForDBComponents;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

public class InputAdditionalParametersDialog extends Dialog {
	private Text noOfPartitionsTextBox;
	private Text partitionKeyUpperBoundTextBox;
	private Text partitionKeyLowerBoundTextBox;
	private Text fetchSizeTextBox;
	private String windowLabel;
	private List<String> schemaFields;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	public Map<String, Object> additionalParameterValue;
	private Label noOfPartitionsLabel;
	private Label partitionKeysLabel;
	private Label partitionKeyUpperBoundLabel;
	private Label partitionKeyLowerBoundLabel;
	private Label fetchSizeLabel;
	private Label additionalDBParametersLabel;
	protected String selectedPartitionKey;
	protected Map<String, String> runtimeValueMap;
	private Button partitionKeyButton;
	private ControlDecoration noOfPartitionControlDecoration;
	private ControlDecoration partitionKeyUpperBoundControlDecoration;
	private ControlDecoration partitionKeyLowerBoundControlDecoration;
	private ControlDecoration fetchSizeControlDecoration;
	private Text additionalParameterTextBox;
	private ControlDecoration additionalParameterControlDecoration;
	private ControlDecoration partitionKeyControlDecoration;
	private Cursor cursor;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param windowTitle
	 * @param propertyDialogButtonBar
	 * @param schemaFields
	 * @param initialMap
	 * @param cursor 
	 */
	public InputAdditionalParametersDialog(Shell parentShell, String windowTitle,
			PropertyDialogButtonBar propertyDialogButtonBar, List<String> schemaFields,
			Map<String, Object> initialMap, Cursor cursor) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL);
		if (StringUtils.isNotBlank(windowTitle))
			windowLabel = windowTitle;
		else
			windowLabel = Constants.ADDITIONAL_PARAMETERS_FOR_DB_WINDOW_LABEL;
		this.propertyDialogButtonBar = propertyDialogButtonBar;
		this.schemaFields = schemaFields;
		this.additionalParameterValue = initialMap;
		this.cursor = cursor;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.getShell().setText(windowLabel);

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		noOfPartitionsLabel = new Label(composite, SWT.NONE);
		GridData gd_noOfPartitionsLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_noOfPartitionsLabel.widthHint = 218;
		noOfPartitionsLabel.setLayoutData(gd_noOfPartitionsLabel);
		noOfPartitionsLabel.setText(Messages.NO_OF_PARTITIONS);

		noOfPartitionsTextBox = new Text(composite, SWT.BORDER);
		noOfPartitionControlDecoration = WidgetUtility.addDecorator(noOfPartitionsTextBox,
				Messages.NO_OF_PARTITION_ERROR_DECORATOR_MESSAGE);
		noOfPartitionControlDecoration.hide();
		GridData gd_noOfPartitionTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_noOfPartitionTextBox.horizontalIndent = 10;
		noOfPartitionsTextBox.setLayoutData(gd_noOfPartitionTextBox);

		partitionKeysLabel = new Label(composite, SWT.NONE);
		GridData gd_partitionKeysLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_partitionKeysLabel.widthHint = 218;
		partitionKeysLabel.setLayoutData(gd_partitionKeysLabel);
		partitionKeysLabel.setText(Messages.PARTITION_KEY);

		partitionKeyButton = new Button(composite, SWT.NONE);
		GridData gd_partitionKeyButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		partitionKeyControlDecoration = WidgetUtility.addDecorator(partitionKeyButton,
				Messages.PARTITION_KEY_ERROR_DECORATOR_MESSAGE);
		partitionKeyControlDecoration.hide();
		gd_partitionKeyButton.widthHint = 90;
		gd_partitionKeyButton.horizontalIndent = 10;
		partitionKeyButton.setLayoutData(gd_partitionKeyButton);
		partitionKeyButton.setText(Messages.EDIT_BUTTON_LABEL);
		partitionKeyButton.setEnabled(false);

		partitionKeyUpperBoundLabel = new Label(composite, SWT.NONE);
		GridData gd_partitionKeyUpperBoundLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_partitionKeyUpperBoundLabel.widthHint = 218;
		partitionKeyUpperBoundLabel.setLayoutData(gd_partitionKeyUpperBoundLabel);
		partitionKeyUpperBoundLabel.setText(Messages.PARTITION_KEY_UPPER_BOUND);

		partitionKeyUpperBoundTextBox = new Text(composite, SWT.BORDER);
		partitionKeyUpperBoundControlDecoration = WidgetUtility.addDecorator(partitionKeyUpperBoundTextBox,
				Messages.UPPER_BOUND_ERROR_DECORATOR_MESSAGE);
		partitionKeyUpperBoundControlDecoration.hide();
		GridData gd_partitionKeyUpperBoundTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_partitionKeyUpperBoundTextBox.horizontalIndent = 10;
		partitionKeyUpperBoundTextBox.setLayoutData(gd_partitionKeyUpperBoundTextBox);
		partitionKeyUpperBoundTextBox.setEnabled(false);

		partitionKeyLowerBoundLabel = new Label(composite, SWT.NONE);
		GridData gd_partitionKeyLowerBoundLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_partitionKeyLowerBoundLabel.widthHint = 218;
		partitionKeyLowerBoundLabel.setLayoutData(gd_partitionKeyLowerBoundLabel);
		partitionKeyLowerBoundLabel.setText(Messages.PARTITION_KEY_LOWER_BOUND);

		partitionKeyLowerBoundTextBox = new Text(composite, SWT.BORDER);
		partitionKeyLowerBoundControlDecoration = WidgetUtility.addDecorator(partitionKeyLowerBoundTextBox,
				Messages.LOWER_BOUND_ERROR_DECORATOR_MESSAGE);
		partitionKeyLowerBoundControlDecoration.hide();
		GridData gd_partitionKeyLowerBoundTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_partitionKeyLowerBoundTextBox.horizontalIndent = 10;
		partitionKeyLowerBoundTextBox.setLayoutData(gd_partitionKeyLowerBoundTextBox);
		partitionKeyLowerBoundTextBox.setEnabled(false);

		fetchSizeLabel = new Label(composite, SWT.NONE);
		GridData gd_fetchSizeLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_fetchSizeLabel.widthHint = 218;
		fetchSizeLabel.setLayoutData(gd_fetchSizeLabel);
		fetchSizeLabel.setText(Messages.FETCH_SIZE);

		fetchSizeTextBox = new Text(composite, SWT.BORDER);
		fetchSizeControlDecoration = WidgetUtility.addDecorator(fetchSizeTextBox,
				Messages.FETCH_SIZE_ERROR_DECORATOR_MESSAGE);
		fetchSizeControlDecoration.hide();
		GridData gd_fetchSizeTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_fetchSizeTextBox.horizontalIndent = 10;
		fetchSizeTextBox.setLayoutData(gd_fetchSizeTextBox);
		if (StringUtils.isNotBlank(fetchSizeTextBox.getText())) {
			fetchSizeTextBox.setText(fetchSizeTextBox.getText());
		} else {
			fetchSizeTextBox.setText("1000");
		}

		additionalDBParametersLabel = new Label(composite, SWT.NONE);
		GridData gd_additionalDBParametersLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_additionalDBParametersLabel.widthHint = 218;
		additionalDBParametersLabel.setLayoutData(gd_additionalDBParametersLabel);
		additionalDBParametersLabel.setText(Messages.ADDITIONAL_DB_PARAMETERS);

		additionalParameterTextBox = new Text(composite, SWT.BORDER);
		additionalParameterControlDecoration = WidgetUtility.addDecorator(additionalParameterTextBox,
				Messages.ADDITIONAL_PARAMETER_ERROR_DECORATOR_MESSAGE);
		additionalParameterControlDecoration.hide();
		GridData gd_additionalParameter = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_additionalParameter.horizontalIndent = 10;
		additionalParameterTextBox.setLayoutData(gd_additionalParameter);

		addSelectionListenerToPartitionKeyButton(partitionKeyButton);

		addModifyListenerToNoOfPartitionTextBox(noOfPartitionsTextBox);

		addValidationToWidgets(noOfPartitionsTextBox, noOfPartitionControlDecoration);
		addValidationToWidgets(partitionKeyUpperBoundTextBox, partitionKeyUpperBoundControlDecoration);
		addValidationToWidgets(partitionKeyLowerBoundTextBox, partitionKeyLowerBoundControlDecoration);
		addValidationToWidgets(fetchSizeTextBox, fetchSizeControlDecoration);
		addValidationToAdditionalParameterWidget(additionalParameterTextBox, additionalParameterControlDecoration);
		addAdditionalParameterMapValues();

		return container;
	}

	private void addValidationToAdditionalParameterWidget(Text additionalParameterTextBox,
			ControlDecoration additionalParameterControlDecoration) {

		ExtraURLParameterValidationForDBComponents extraURLParameterValidation = new ExtraURLParameterValidationForDBComponents();
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, additionalParameterControlDecoration);
		additionalParameterTextBox.addListener(SWT.Modify,
				extraURLParameterValidation.getListener(propertyDialogButtonBar, helper, additionalParameterTextBox));
	}

	private void addValidationToWidgets(Text textBox, ControlDecoration txtDecorator) {

		VerifyNumericandParameterForDBComponents numericValidationForDBComponents = new VerifyNumericandParameterForDBComponents();
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, txtDecorator);

		textBox.addListener(SWT.Modify,
				numericValidationForDBComponents.getListener(propertyDialogButtonBar, helper, textBox));
	}

	private void addModifyListenerToNoOfPartitionTextBox(Text noOfPartitionsTextBox) {
		noOfPartitionsTextBox.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				partitionKeyButton.setEnabled(true);
				partitionKeyLowerBoundTextBox.setEnabled(true);
				partitionKeyUpperBoundTextBox.setEnabled(true);
				if (StringUtils.isNotBlank(noOfPartitionsTextBox.getText())) {
					if (StringUtils.isBlank(partitionKeyLowerBoundTextBox.getText())
							|| StringUtils.isBlank(partitionKeyUpperBoundTextBox.getText())) {
						partitionKeyLowerBoundControlDecoration.show();
						partitionKeyUpperBoundControlDecoration.show();
					}
					if(StringUtils.isBlank(selectedPartitionKey) && StringUtils.isEmpty(selectedPartitionKey)){
						partitionKeyControlDecoration.show();
					}
				}
				propertyDialogButtonBar.enableApplyButton(true);
			}
		});

		noOfPartitionsTextBox.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (StringUtils.isBlank(noOfPartitionsTextBox.getText())
						&& StringUtils.isEmpty(noOfPartitionsTextBox.getText())) {
					partitionKeyButton.setEnabled(false);
					partitionKeyLowerBoundTextBox.setEnabled(false);
					partitionKeyUpperBoundTextBox.setEnabled(false);

				} else if (StringUtils.isNotBlank(noOfPartitionsTextBox.getText())) {
					if (StringUtils.isBlank(partitionKeyLowerBoundTextBox.getText())
							|| StringUtils.isBlank(partitionKeyUpperBoundTextBox.getText())) {
						partitionKeyLowerBoundControlDecoration.show();
						partitionKeyUpperBoundControlDecoration.show();
					}
					if(StringUtils.isBlank(selectedPartitionKey) && StringUtils.isEmpty(selectedPartitionKey)){
						partitionKeyControlDecoration.show();
					}
				}

			}

			@Override
			public void focusGained(FocusEvent e) {

				if (StringUtils.isNotBlank(noOfPartitionsTextBox.getText())
						&& StringUtils.isNotEmpty(noOfPartitionsTextBox.getText())) {
					partitionKeyButton.setEnabled(true);
					partitionKeyLowerBoundTextBox.setEnabled(true);
					partitionKeyUpperBoundTextBox.setEnabled(true);

				}
			}
		});
	}

	private void addAdditionalParameterMapValues() {

		if (additionalParameterValue != null && !additionalParameterValue.isEmpty()) {
			if (additionalParameterValue.get(Constants.NO_OF_PARTITION) != null) {
				noOfPartitionsTextBox.setText(additionalParameterValue.get(Constants.NO_OF_PARTITION).toString());
				Utils.INSTANCE.addMouseMoveListenerForTextBox(noOfPartitionsTextBox, cursor);
				if (StringUtils.isNotBlank((String) additionalParameterValue.get(Constants.DB_PARTITION_KEY))) {
					partitionKeyButton.setEnabled(true);
					partitionKeyControlDecoration.hide();
				} else {
					partitionKeyControlDecoration.show();
				}
				if (additionalParameterValue.get(Constants.PARTITION_KEY_LOWER_BOUND) != null) {
					partitionKeyLowerBoundTextBox
							.setText(additionalParameterValue.get(Constants.PARTITION_KEY_LOWER_BOUND).toString());
					Utils.INSTANCE.addMouseMoveListenerForTextBox(partitionKeyLowerBoundTextBox, cursor);
					partitionKeyLowerBoundControlDecoration.hide();
				} else {
					partitionKeyLowerBoundControlDecoration.show();
				}
				if (additionalParameterValue.get(Constants.PARTITION_KEY_UPPER_BOUND) != null) {
					partitionKeyUpperBoundTextBox
							.setText(additionalParameterValue.get(Constants.PARTITION_KEY_UPPER_BOUND).toString());
					Utils.INSTANCE.addMouseMoveListenerForTextBox(partitionKeyUpperBoundTextBox, cursor);
					partitionKeyUpperBoundControlDecoration.hide();
				} else {
					partitionKeyUpperBoundControlDecoration.show();
				}
			}
			if (StringUtils.isNotBlank((String) additionalParameterValue.get(Constants.FECTH_SIZE))) {
				fetchSizeTextBox.setText((String) additionalParameterValue.get(Constants.FECTH_SIZE));
				Utils.INSTANCE.addMouseMoveListenerForTextBox(fetchSizeTextBox, cursor);
				fetchSizeControlDecoration.hide();
			} else {
				fetchSizeTextBox.setText("1000");
				fetchSizeControlDecoration.hide();
			}

			if (StringUtils.isNotBlank((String) additionalParameterValue.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB))) {
				additionalParameterTextBox
						.setText((String) additionalParameterValue.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB));
				additionalParameterControlDecoration.hide();
			}
		}

	}

	private void addSelectionListenerToPartitionKeyButton(Button partitionKeyButton) {
		partitionKeyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FieldDialogForDBComponents fieldDialog = new FieldDialogForDBComponents(new Shell(),
						propertyDialogButtonBar);
				fieldDialog.setComponentName(Messages.PARTITION_KEYS_FOR_DB_COMPONENT);
				fieldDialog.setSourceFieldsFromPropagatedSchema(schemaFields);
				if (StringUtils.isNotBlank((String) additionalParameterValue.get(Constants.DB_PARTITION_KEY))) {
					fieldDialog.setPropertyFromCommaSepratedString(
							(String) additionalParameterValue.get(Constants.DB_PARTITION_KEY));
				}
				fieldDialog.open();
				selectedPartitionKey = fieldDialog.getResultAsCommaSeprated();
				if (StringUtils.isEmpty(selectedPartitionKey) || StringUtils.isBlank(selectedPartitionKey)) {
					partitionKeyControlDecoration.show();
				} else {
					partitionKeyControlDecoration.hide();
				}

				propertyDialogButtonBar.enableApplyButton(true);
			}
		});

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
		return new Point(450, 276);
	}

	public Map<String, Object> getAdditionalParameterDetails() {
		return additionalParameterValue;

	}

	@Override
	protected void okPressed() {

		additionalParameterValue.clear();

		if (StringUtils.isNotBlank(noOfPartitionsTextBox.getText())) {
			additionalParameterValue.put(noOfPartitionsLabel.getText(), noOfPartitionsTextBox.getText());
		}
		if (StringUtils.isNotBlank(selectedPartitionKey)) {
			additionalParameterValue.put(partitionKeysLabel.getText(), selectedPartitionKey);
		}
		if (StringUtils.isNotBlank(partitionKeyUpperBoundTextBox.getText())) {
			additionalParameterValue.put(partitionKeyUpperBoundLabel.getText(),
					partitionKeyUpperBoundTextBox.getText());
		}
		if (StringUtils.isNotBlank(partitionKeyLowerBoundTextBox.getText())) {
			additionalParameterValue.put(partitionKeyLowerBoundLabel.getText(),
					partitionKeyLowerBoundTextBox.getText());
		}
		if (StringUtils.isNotBlank(fetchSizeTextBox.getText())) {
			additionalParameterValue.put(fetchSizeLabel.getText(), fetchSizeTextBox.getText());
		}
		if (StringUtils.isNotBlank(additionalParameterTextBox.getText())) {
			additionalParameterValue.put(additionalDBParametersLabel.getText(), additionalParameterTextBox.getText());
		}
		super.okPressed();
	}

}
