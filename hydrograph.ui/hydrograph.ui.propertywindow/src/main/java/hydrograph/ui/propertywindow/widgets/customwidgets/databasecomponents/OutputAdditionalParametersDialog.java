package hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.utils.Utils;
import hydrograph.ui.propertywindow.widgets.listeners.ExtraURLParameterValidationForDBComponents;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.listeners.VerifyNumericandParameterForDBComponents;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

public class OutputAdditionalParametersDialog extends Dialog {
	private Text chunkSizeTextBox;
	private String windowLabel;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private Map<String, Object> outputAdditionalParameterValues;
	private Label chunkSize;
	private Label additionalDBParametersLabel;
	protected Map<String, String> runtimePropertyValue;
	private ControlDecoration controlDecoration;
	private Text additionalParameterTextBox;
	private ControlDecoration additionalParameterControlDecoration;
	private Cursor cursor;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param propertyDialogButtonBar
	 * @param initialMap
	 * @param cursor 
	 * @param string
	 */
	public OutputAdditionalParametersDialog(Shell parentShell, String windowTitle,
			PropertyDialogButtonBar propertyDialogButtonBar, Map<String, Object> initialMap, Cursor cursor) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL);
		if (StringUtils.isNotBlank(windowTitle))
			windowLabel = windowTitle;
		else
			windowLabel = Constants.ADDITIONAL_PARAMETERS_FOR_DB_WINDOW_LABEL;
		this.propertyDialogButtonBar = propertyDialogButtonBar;
		this.outputAdditionalParameterValues = initialMap;
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

		chunkSize = new Label(composite, SWT.NONE);
		GridData gd_chunkSize = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_chunkSize.widthHint = 218;
		chunkSize.setLayoutData(gd_chunkSize);
		chunkSize.setText(Messages.DB_CHUNK_SIZE);

		chunkSizeTextBox = new Text(composite, SWT.BORDER);
		GridData gd_chunkSizeTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_chunkSizeTextBox.horizontalIndent = 10;
		chunkSizeTextBox.setLayoutData(gd_chunkSizeTextBox);
		controlDecoration = WidgetUtility.addDecorator(chunkSizeTextBox, Messages.CHUNK_SIZE_ERROR_DECORATOR_MESSAGE);
		if (StringUtils.isBlank(chunkSizeTextBox.getText())) {
			chunkSizeTextBox.setText("1000");
			controlDecoration.hide();
		}

		additionalDBParametersLabel = new Label(composite, SWT.NONE);
		GridData gd_additionalDBParametersLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_additionalDBParametersLabel.widthHint = 218;
		additionalDBParametersLabel.setLayoutData(gd_additionalDBParametersLabel);
		additionalDBParametersLabel.setText(Messages.ADDITIONAL_DB_PARAMETERS);

		additionalParameterTextBox = new Text(composite, SWT.BORDER);
		additionalParameterControlDecoration = WidgetUtility.addDecorator(additionalParameterTextBox,Messages.ADDITIONAL_PARAMETER_ERROR_DECORATOR_MESSAGE);
		additionalParameterControlDecoration.hide();
		GridData gd_additionalParameter = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_additionalParameter.horizontalIndent = 10;
		additionalParameterTextBox.setLayoutData(gd_additionalParameter);

		addListenerToChunkSize(chunkSizeTextBox);

		addListenerToAdditionalParameter(additionalParameterTextBox);

		addOutputAdditionalParameterValues();

		return container;
	}

	private void addListenerToAdditionalParameter(Text additionalParameterTextBox) {
		ExtraURLParameterValidationForDBComponents extraURLParameterValidation = new ExtraURLParameterValidationForDBComponents();
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, additionalParameterControlDecoration);
		additionalParameterTextBox.addListener(SWT.Modify,extraURLParameterValidation.getListener(propertyDialogButtonBar, helper, additionalParameterTextBox));
		
	}

	private void addListenerToChunkSize(Text chunkSizeTextBox) {
		VerifyNumericandParameterForDBComponents numericValidationForDBComponents = new VerifyNumericandParameterForDBComponents();
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, controlDecoration);
		chunkSizeTextBox.addListener(SWT.Modify,numericValidationForDBComponents.getListener(propertyDialogButtonBar, helper, chunkSizeTextBox));
	}

	private void addOutputAdditionalParameterValues() {

		if (outputAdditionalParameterValues != null && !outputAdditionalParameterValues.isEmpty()) {
			if (StringUtils.isNotBlank((String) outputAdditionalParameterValues.get(Constants.DB_CHUNK_SIZE))) {
				chunkSizeTextBox.setText((String) outputAdditionalParameterValues.get(Constants.DB_CHUNK_SIZE));
				Utils.INSTANCE.addMouseMoveListenerForTextBox(chunkSizeTextBox, cursor);
			}
			if (StringUtils
					.isNotBlank((String) outputAdditionalParameterValues.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB))) {
				additionalParameterTextBox
						.setText((String) outputAdditionalParameterValues.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB));
				Utils.INSTANCE.addMouseMoveListenerForTextBox(additionalParameterTextBox, cursor);
			}
		}
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

	public Map<String, Object> getOutputAdditionalParameterValues() {
		return outputAdditionalParameterValues;

	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 181);
	}

	@Override
	protected void okPressed() {

		outputAdditionalParameterValues.clear();

		if (StringUtils.isNotBlank(chunkSizeTextBox.getText())) {
			outputAdditionalParameterValues.put(chunkSize.getText(), chunkSizeTextBox.getText());
		}
		if (StringUtils.isNotBlank(additionalParameterTextBox.getText())) {
			outputAdditionalParameterValues.put(additionalDBParametersLabel.getText(),
					additionalParameterTextBox.getText());
		}
		super.okPressed();
	}

}
