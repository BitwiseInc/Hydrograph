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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.propertywindow.handlers.ShowHidePropertyHelpHandler;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.listeners.ExtraURLParameterValidationForDBComponents;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper;
import hydrograph.ui.propertywindow.widgets.listeners.ListenerHelper.HelperType;
import hydrograph.ui.propertywindow.widgets.listeners.VerifyNumericAndParameterForDBComponents;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * OutputAdditionalParametersDialog to create parameter dialog for DB output components
 * @author Bitwise
 *
 */
public class OutputAdditionalParametersDialog extends Dialog {
	public static final String CHUNK_SIZE_VALUE = "1000";
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
	private boolean ShowHidePropertyHelpChecked;

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
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL | SWT.RESIZE);
		if (StringUtils.isNotBlank(windowTitle))
			windowLabel = windowTitle;
		else
			windowLabel = Messages.ADDITIONAL_PARAMETERS_FOR_DB_WINDOW_LABEL;
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
		
		int CONST_HEIGHT = 181;
				
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
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		chunkSize = new Label(composite, SWT.NONE);
		GridData gd_chunkSize = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_chunkSize.widthHint = 150;
		chunkSize.setLayoutData(gd_chunkSize);
		chunkSize.setText(Messages.DB_CHUNK_SIZE);

		chunkSizeTextBox = new Text(composite, SWT.BORDER);
		GridData gd_chunkSizeTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_chunkSizeTextBox.horizontalIndent = 10;
		chunkSizeTextBox.setLayoutData(gd_chunkSizeTextBox);
		chunkSizeTextBox.setText(CHUNK_SIZE_VALUE);
		controlDecoration = WidgetUtility.addDecorator(chunkSizeTextBox, Messages.DB_NUMERIC_PARAMETERZIATION_ERROR);
		controlDecoration.hide();
		controlDecoration.setMarginWidth(2);

		additionalDBParametersLabel = new Label(composite, SWT.NONE);
		GridData gd_additionalDBParametersLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_additionalDBParametersLabel.widthHint = 150;
		additionalDBParametersLabel.setLayoutData(gd_additionalDBParametersLabel);
		additionalDBParametersLabel.setText(Messages.ADDITIONAL_DB_PARAMETERS);

		additionalParameterTextBox = new Text(composite, SWT.BORDER);
		additionalParameterControlDecoration = WidgetUtility.addDecorator(additionalParameterTextBox,Messages.ADDITIONAL_PARAMETER_ERROR_DECORATOR_MESSAGE);
		additionalParameterControlDecoration.setMarginWidth(2);
		additionalParameterControlDecoration.hide();
		GridData gd_additionalParameter = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_additionalParameter.horizontalIndent = 10;
		additionalParameterTextBox.setLayoutData(gd_additionalParameter);

		addListenerToChunkSize(chunkSizeTextBox);
		
		addModifyListener(chunkSizeTextBox);
		addModifyListener(additionalParameterTextBox);

		addListenerToAdditionalParameter(additionalParameterTextBox);

		addOutputAdditionalParameterValues();
		
		getShell().setMinimumSize(getInitialSize());
		
		setPropertyHelpText();

		return container;
	}

	private void setPropertyHelpText() {
		if(ShowHidePropertyHelpHandler.getInstance() != null 
				&& ShowHidePropertyHelpHandler.getInstance().isShowHidePropertyHelpChecked()){
				chunkSize.setToolTipText(Messages.CHUNK_SIZE);
				chunkSize.setCursor(new Cursor(chunkSize.getDisplay(), SWT.CURSOR_HELP));
				
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
	
	private void addListenerToAdditionalParameter(Text additionalParameterTextBox) {
		ExtraURLParameterValidationForDBComponents extraURLParameterValidation = new ExtraURLParameterValidationForDBComponents();
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, additionalParameterControlDecoration);
		additionalParameterTextBox.addListener(SWT.Modify,extraURLParameterValidation.getListener(propertyDialogButtonBar, helper, additionalParameterTextBox));
		
	}

	private void addListenerToChunkSize(Text chunkSizeTextBox) {
		VerifyNumericAndParameterForDBComponents numericValidationForDBComponents = new VerifyNumericAndParameterForDBComponents();
		ListenerHelper helper = new ListenerHelper();
		helper.put(HelperType.CONTROL_DECORATION, controlDecoration);
		chunkSizeTextBox.addListener(SWT.Modify,numericValidationForDBComponents.getListener(propertyDialogButtonBar, helper, chunkSizeTextBox));
	}

	private void addOutputAdditionalParameterValues() {

		if (outputAdditionalParameterValues != null && !outputAdditionalParameterValues.isEmpty()) {
				chunkSizeTextBox.setText((String) outputAdditionalParameterValues.get(Constants.ADDITIONAL_DB_CHUNK_SIZE));
				Utils.INSTANCE.addMouseMoveListener(chunkSizeTextBox, cursor);	
			if (StringUtils.isNotBlank((String) outputAdditionalParameterValues.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB))) {
				additionalParameterTextBox
						.setText((String) outputAdditionalParameterValues.get(Constants.ADDITIONAL_PARAMETERS_FOR_DB));
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
		outputAdditionalParameterValues = new LinkedHashMap<>();
		outputAdditionalParameterValues.put(Constants.ADDITIONAL_DB_CHUNK_SIZE, chunkSizeTextBox.getText());
		outputAdditionalParameterValues.put(Constants.ADDITIONAL_PARAMETERS_FOR_DB, additionalParameterTextBox.getText());
		super.okPressed();
	}

}
