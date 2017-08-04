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
package hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.handlers.ShowHidePropertyHelpHandler;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ExtraURLParameterValidationForDBComponents;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.listeners.VerifyNumericAndParameterForDBComponents;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * InputAdditionalParametersDialog to create additional parameter dialog
 * @author Bitwise
 *
 */
public class InputAdditionalParametersDialog extends Dialog {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(InputAdditionalParametersDialog.class);
	public static final String FETCH_SIZE_VALUE = "1000";
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
	private Combo partitionKeyComboBox;
	private ControlDecoration noOfPartitionControlDecoration;
	private ControlDecoration partitionKeyUpperBoundControlDecoration;
	private ControlDecoration partitionKeyLowerBoundControlDecoration;
	private ControlDecoration fetchSizeControlDecoration;
	private Text additionalParameterTextBox;
	private ControlDecoration additionalParameterControlDecoration;
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
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL | SWT.RESIZE);
		if (StringUtils.isNotBlank(windowTitle))
			windowLabel = windowTitle;
		else
			windowLabel = Messages.ADDITIONAL_PARAMETERS_FOR_DB_WINDOW_LABEL;
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

		int CONST_HEIGHT = 276;
		
		Shell shell = container.getShell();
		
		shell.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                Rectangle rect = shell.getBounds();
                if(rect.width != CONST_HEIGHT) {
                    shell.setBounds(rect.x, rect.y, rect.width, CONST_HEIGHT);
                }
            }
        });
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		noOfPartitionsLabel = new Label(composite, SWT.NONE);
		GridData gd_noOfPartitionsLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_noOfPartitionsLabel.widthHint = 180;
		noOfPartitionsLabel.setLayoutData(gd_noOfPartitionsLabel);
		noOfPartitionsLabel.setText(Messages.NO_OF_PARTITIONS);
		
		noOfPartitionsTextBox = new Text(composite, SWT.BORDER);
		noOfPartitionControlDecoration = WidgetUtility.addDecorator(noOfPartitionsTextBox,
				Messages.DB_NUMERIC_PARAMETERZIATION_ERROR);
		noOfPartitionControlDecoration.setMarginWidth(2);
		noOfPartitionControlDecoration.hide();
		GridData gd_noOfPartitionTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_noOfPartitionTextBox.horizontalIndent = 10;
		noOfPartitionsTextBox.setLayoutData(gd_noOfPartitionTextBox);

		partitionKeysLabel = new Label(composite, SWT.NONE);
		GridData gd_partitionKeysLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_partitionKeysLabel.widthHint = 180;
		partitionKeysLabel.setLayoutData(gd_partitionKeysLabel);
		partitionKeysLabel.setText(Messages.PARTITION_KEY);
		
		partitionKeyComboBox = new Combo(composite, SWT.DROP_DOWN);
		partitionKeyComboBox.setItems(schemaFields.toArray(new String[schemaFields.size()]));
		GridData gd_partitionKeyButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		partitionKeyComboBox.setVisibleItemCount(11);
		partitionKeyComboBox.setEnabled(false);
		
		gd_partitionKeyButton.widthHint = 90;
		gd_partitionKeyButton.horizontalIndent = 10;
		partitionKeyComboBox.setLayoutData(gd_partitionKeyButton);
		
		new AutoCompleteField(partitionKeyComboBox, new ComboContentAdapter(), schemaFields.toArray(new String[schemaFields.size()]));
		
		selectedPartitionKey = (String) additionalParameterValue.get(Constants.DB_PARTITION_KEY);
		
		if(StringUtils.isNotBlank(selectedPartitionKey)){
		partitionKeyComboBox.setText(selectedPartitionKey);
		}
		else{
			partitionKeyComboBox.select(0);
		}
		
		partitionKeyLowerBoundLabel = new Label(composite, SWT.NONE);
		GridData gd_partitionKeyLowerBoundLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_partitionKeyLowerBoundLabel.widthHint = 180;
		partitionKeyLowerBoundLabel.setLayoutData(gd_partitionKeyLowerBoundLabel);
		partitionKeyLowerBoundLabel.setText(Messages.PARTITION_KEY_LOWER_BOUND);
		
		partitionKeyLowerBoundTextBox = new Text(composite, SWT.BORDER);
		partitionKeyLowerBoundControlDecoration = WidgetUtility.addDecorator(partitionKeyLowerBoundTextBox,
				Messages.DB_NUMERIC_PARAMETERZIATION_ERROR);
		partitionKeyLowerBoundControlDecoration.setMarginWidth(2);
		partitionKeyLowerBoundControlDecoration.hide();
		GridData gd_partitionKeyLowerBoundTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_partitionKeyLowerBoundTextBox.horizontalIndent = 10;
		partitionKeyLowerBoundTextBox.setLayoutData(gd_partitionKeyLowerBoundTextBox);
		partitionKeyLowerBoundTextBox.setEnabled(false);
		
		partitionKeyUpperBoundLabel = new Label(composite, SWT.NONE);
		GridData gd_partitionKeyUpperBoundLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_partitionKeyUpperBoundLabel.widthHint = 180;
		partitionKeyUpperBoundLabel.setLayoutData(gd_partitionKeyUpperBoundLabel);
		partitionKeyUpperBoundLabel.setText(Messages.PARTITION_KEY_UPPER_BOUND);

		partitionKeyUpperBoundTextBox = new Text(composite, SWT.BORDER);
		partitionKeyUpperBoundControlDecoration = WidgetUtility.addDecorator(partitionKeyUpperBoundTextBox,
				Messages.DB_NUMERIC_PARAMETERZIATION_ERROR);
		partitionKeyUpperBoundControlDecoration.setMarginWidth(2);
		partitionKeyUpperBoundControlDecoration.hide();
		GridData gd_partitionKeyUpperBoundTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_partitionKeyUpperBoundTextBox.horizontalIndent = 10;
		partitionKeyUpperBoundTextBox.setLayoutData(gd_partitionKeyUpperBoundTextBox);
		partitionKeyUpperBoundTextBox.setEnabled(false);

		fetchSizeLabel = new Label(composite, SWT.NONE);
		GridData gd_fetchSizeLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_fetchSizeLabel.widthHint = 180;
		fetchSizeLabel.setLayoutData(gd_fetchSizeLabel);
		fetchSizeLabel.setText(Messages.FETCH_SIZE);
		
		fetchSizeTextBox = new Text(composite, SWT.BORDER);
		fetchSizeControlDecoration = WidgetUtility.addDecorator(fetchSizeTextBox,
				Messages.FETCH_SIZE_ERROR_DECORATOR_MESSAGE);
		fetchSizeControlDecoration.setMarginWidth(2);
		fetchSizeControlDecoration.hide();
		GridData gd_fetchSizeTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_fetchSizeTextBox.horizontalIndent = 10;
		fetchSizeTextBox.setLayoutData(gd_fetchSizeTextBox);
		fetchSizeTextBox.setText(FETCH_SIZE_VALUE);

		additionalDBParametersLabel = new Label(composite, SWT.NONE);
		GridData gd_additionalDBParametersLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_additionalDBParametersLabel.widthHint = 180;
		additionalDBParametersLabel.setLayoutData(gd_additionalDBParametersLabel);
		additionalDBParametersLabel.setText(Messages.ADDITIONAL_DB_PARAMETERS);

		additionalParameterTextBox = new Text(composite, SWT.BORDER);
		additionalParameterControlDecoration = WidgetUtility.addDecorator(additionalParameterTextBox,
				Messages.ADDITIONAL_PARAMETER_ERROR_DECORATOR_MESSAGE);
		additionalParameterControlDecoration.setMarginWidth(2);
		additionalParameterControlDecoration.hide();
		GridData gd_additionalParameter = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_additionalParameter.horizontalIndent = 10;
		additionalParameterTextBox.setLayoutData(gd_additionalParameter);

		addModifyListenerToNoOfPartitionTextBox(noOfPartitionsTextBox);
		addModifyListener(partitionKeyUpperBoundTextBox);
		addModifyListener(partitionKeyLowerBoundTextBox);
		addModifyListener(fetchSizeTextBox);
		addModifyListener(additionalParameterTextBox);

		addValidationToWidgets(noOfPartitionsTextBox, noOfPartitionControlDecoration);
		partitionKeyUpperBoundTextBox.addModifyListener(new ModifyListenerForDBComp(partitionKeyUpperBoundTextBox, 
				partitionKeyLowerBoundTextBox, partitionKeyLowerBoundControlDecoration, partitionKeyUpperBoundControlDecoration));
		
		partitionKeyLowerBoundTextBox.addModifyListener(new ModifyListenerForDBComp(partitionKeyUpperBoundTextBox, 
				partitionKeyLowerBoundTextBox, partitionKeyLowerBoundControlDecoration, partitionKeyUpperBoundControlDecoration));
		
		addValidationToWidgets(fetchSizeTextBox, fetchSizeControlDecoration);
		
		addValidationToAdditionalParameterWidget(additionalParameterTextBox, additionalParameterControlDecoration);
		
		addAdditionalParameterMapValues();

		getShell().setMinimumSize(getInitialSize());
		
		setPropertyHelpText();
		
		return container;
	}

	private void setPropertyHelpText() {
		if(ShowHidePropertyHelpHandler.getInstance() != null 
				&& ShowHidePropertyHelpHandler.getInstance().isShowHidePropertyHelpChecked()){
			noOfPartitionsLabel.setToolTipText(Messages.NUMBER_OF_PARTITIONS);
			noOfPartitionsLabel.setCursor(new Cursor(noOfPartitionsLabel.getDisplay(), SWT.CURSOR_HELP));
			
			partitionKeysLabel.setToolTipText(Messages.PARTITONS_KEY);
			partitionKeysLabel.setCursor(new Cursor(partitionKeysLabel.getDisplay(), SWT.CURSOR_HELP));
			
			partitionKeyUpperBoundLabel.setToolTipText(Messages.UPPER_BOUND_LABEL);
			partitionKeyUpperBoundLabel.setCursor(new Cursor(partitionKeyUpperBoundLabel.getDisplay(), SWT.CURSOR_HELP));
			
			partitionKeyLowerBoundLabel.setToolTipText(Messages.LOWER_BOUND_LABEL);
			partitionKeyLowerBoundLabel.setCursor(new Cursor(partitionKeyLowerBoundLabel.getDisplay(), SWT.CURSOR_HELP));
			
			fetchSizeLabel.setToolTipText(Messages.FETCH_SIZE_PARAM);
			fetchSizeLabel.setCursor(new Cursor(fetchSizeLabel.getDisplay(), SWT.CURSOR_HELP));
			
			additionalDBParametersLabel.setToolTipText(Messages.ADDITIONAL_DB_PARAMETER);
			additionalDBParametersLabel.setCursor(new Cursor(additionalDBParametersLabel.getDisplay(), SWT.CURSOR_HELP));
			}
	}

	private void addModifyListener(Text text){
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Utils.INSTANCE.addMouseMoveListener(text, cursor);	
			}
		});
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
		VerifyNumericAndParameterForDBComponents numericValidationForDBComponents = new VerifyNumericAndParameterForDBComponents();
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, txtDecorator);

		textBox.addListener(SWT.Modify,
				numericValidationForDBComponents.getListener(propertyDialogButtonBar, helper, textBox));
	}

	private void addModifyListenerToNoOfPartitionTextBox(Text noOfPartitionsTextBox) {
		noOfPartitionsTextBox.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {
				if(StringUtils.isNotBlank(noOfPartitionsTextBox.getText())){
				partitionKeyComboBox.setEnabled(true);
				partitionKeyLowerBoundTextBox.setEnabled(true);
				partitionKeyUpperBoundTextBox.setEnabled(true);
					
					if(StringUtils.isBlank(partitionKeyLowerBoundTextBox.getText())){ partitionKeyLowerBoundControlDecoration.show();}
					if(StringUtils.isBlank(partitionKeyUpperBoundTextBox.getText())){ partitionKeyUpperBoundControlDecoration.show();}
					
				}else{
					partitionKeyLowerBoundControlDecoration.hide();
					partitionKeyUpperBoundControlDecoration.hide();
					partitionKeyComboBox.setEnabled(false);
					partitionKeyLowerBoundTextBox.setEnabled(false);
					partitionKeyUpperBoundTextBox.setEnabled(false);
				}
				Utils.INSTANCE.addMouseMoveListener(noOfPartitionsTextBox, cursor);	
			}
		});

		noOfPartitionsTextBox.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (StringUtils.isBlank(noOfPartitionsTextBox.getText())
						&& StringUtils.isEmpty(noOfPartitionsTextBox.getText())) {
					partitionKeyComboBox.setEnabled(false);
					partitionKeyLowerBoundTextBox.setEnabled(false);
					partitionKeyUpperBoundTextBox.setEnabled(false);
				}
			}

			@Override
			public void focusGained(FocusEvent e) {

				if (StringUtils.isNotBlank(noOfPartitionsTextBox.getText())
						&& StringUtils.isNotEmpty(noOfPartitionsTextBox.getText())) {
					partitionKeyComboBox.setEnabled(true);
					partitionKeyLowerBoundTextBox.setEnabled(true);
					partitionKeyUpperBoundTextBox.setEnabled(true);
				}
			}
		});
	}

	private void addAdditionalParameterMapValues() {

		if (additionalParameterValue != null && !additionalParameterValue.isEmpty()) {
			if (additionalParameterValue.get(Constants.NUMBER_OF_PARTITIONS) != null 
					&& StringUtils.isNotEmpty((String)additionalParameterValue.get(Constants.NUMBER_OF_PARTITIONS))) {
				noOfPartitionsTextBox.setText(additionalParameterValue.get(Constants.NUMBER_OF_PARTITIONS).toString());
				Utils.INSTANCE.addMouseMoveListener(noOfPartitionsTextBox, cursor);	
				if (additionalParameterValue.get(Constants.NOP_LOWER_BOUND) != null 
						&& StringUtils.isNotEmpty((String) additionalParameterValue.get(Constants.NOP_LOWER_BOUND))) {
					partitionKeyLowerBoundTextBox.setText(additionalParameterValue.get(Constants.NOP_LOWER_BOUND).toString());
					Utils.INSTANCE.addMouseMoveListener(partitionKeyLowerBoundTextBox, cursor);
					partitionKeyLowerBoundControlDecoration.hide();
				} else {
					partitionKeyLowerBoundControlDecoration.show();
				}
				if (additionalParameterValue.get(Constants.NOP_UPPER_BOUND) != null 
						&& StringUtils.isNotEmpty((String) additionalParameterValue.get(Constants.NOP_UPPER_BOUND))) {
					partitionKeyUpperBoundTextBox
							.setText(additionalParameterValue.get(Constants.NOP_UPPER_BOUND).toString());
					Utils.INSTANCE.addMouseMoveListener(partitionKeyUpperBoundTextBox, cursor);
					partitionKeyUpperBoundControlDecoration.hide();
				} else {
					partitionKeyUpperBoundControlDecoration.show();
				}
			}else{
				partitionKeyLowerBoundControlDecoration.hide();
				partitionKeyUpperBoundControlDecoration.hide();
			}
			fetchSizeTextBox.setText((String) additionalParameterValue.get(Constants.ADDITIONAL_DB_FETCH_SIZE));
			Utils.INSTANCE.addMouseMoveListener(fetchSizeTextBox, cursor);

			if (StringUtils.isNotBlank((String) additionalParameterValue.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB))) {
				additionalParameterTextBox
						.setText((String) additionalParameterValue.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB));
				Utils.INSTANCE.addMouseMoveListener(additionalParameterTextBox, cursor);
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
		
		additionalParameterValue= new LinkedHashMap<>();
		if (StringUtils.isNotBlank(noOfPartitionsTextBox.getText())) {
			additionalParameterValue.put(Constants.NUMBER_OF_PARTITIONS, noOfPartitionsTextBox.getText());
			additionalParameterValue.put(Constants.DB_PARTITION_KEY, partitionKeyComboBox.getText());
			additionalParameterValue.put(Constants.NOP_UPPER_BOUND, partitionKeyUpperBoundTextBox.getText());
			additionalParameterValue.put(Constants.NOP_LOWER_BOUND, partitionKeyLowerBoundTextBox.getText());
		}else{
			additionalParameterValue.put(Constants.NUMBER_OF_PARTITIONS, noOfPartitionsTextBox.getText());
		}
		
		additionalParameterValue.put(Constants.ADDITIONAL_DB_FETCH_SIZE, fetchSizeTextBox.getText());
	
		additionalParameterValue.put(Constants.ADDITIONAL_PARAMETERS_FOR_DB, additionalParameterTextBox.getText());
			
		super.okPressed();
	}
	
}