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

 
package hydrograph.ui.propertywindow.widgets.customwidgets.lookupproperty;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.LookupConfigProperty;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.dialogs.FieldDialog;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;


public class ELTLookupConfigGrid extends Dialog {

	private Text driverText;
	private Text lookupText;
	private Button[] radio = new Button[2];
	private LookupConfigProperty configProperty;
	private LookupConfigProperty oldConfigProperty;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private String driverKey, lookupKey;
	private Label driverEditLableAsButton, lookupEditLableAsButton;
	private Map<String, List<String>> propagatedFiledNames;

	private static final String LOOKUP_PORT = "Lookup Port";
	private static final String LOOKUP_KEYS = "Lookup Key(s)";
	private static final String PORT = "Port";
	private static final String INSERT_IMAGE ="Insert Image";
	private static final String LOOKUP_CONFIG="Lookup Configuration";
	
	private static final String IN_PORT0= "in0";
	private static final String IN_PORT1="in1";
	 private List<List<FilterProperties>> sourceFieldList;
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param propertyDialogButtonBar2
	 * @param lookupConfigProperty
	 */
	public ELTLookupConfigGrid(Shell parentShell, PropertyDialogButtonBar propertyDialogButtonBar,
			LookupConfigProperty lookupConfigProperty) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL);
		configProperty = lookupConfigProperty;
		oldConfigProperty = (LookupConfigProperty) lookupConfigProperty.clone();
		driverKey = configProperty.getDriverKey();
		lookupKey = configProperty.getLookupKey();
		this.propertyDialogButtonBar = propertyDialogButtonBar;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	public Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText(LOOKUP_CONFIG);
		container.setLayout(new FillLayout(SWT.VERTICAL));

		Composite composite = new Composite(container, SWT.BORDER);
		composite.setLayout(new RowLayout(SWT.VERTICAL));

		Label lblNewLabel = new Label(composite, SWT.CENTER);
		lblNewLabel.setLayoutData(new RowData(137, 21));
		lblNewLabel.setText(LOOKUP_CONFIG);

		Composite portComposite = new Composite(composite, SWT.BORDER);
		portComposite.setLayoutData(new RowData(436, 60));
		portComposite.setBounds(10, 10, 200, 100);

		labelWidget(portComposite, SWT.CENTER | SWT.READ_ONLY, new int[] { 5, 5, 100, 20 }, LOOKUP_PORT);
		radio[0] = buttonWidget(portComposite, SWT.RADIO, new int[] { 105, 5, 90, 20 },
				IN_PORT0);
		radio[1] = buttonWidget(portComposite, SWT.RADIO, new int[] { 105, 25, 90, 20 },
				IN_PORT1);

		for (int i = 0; i < radio.length; i++) {
			radio[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					Button button = (Button) event.widget;
					if (button.getText().equalsIgnoreCase(IN_PORT1)) {
						radio[0].setSelection(false);
						radio[1].setSelection(true);
						configProperty.setSelected(false);

					} else {
						radio[1].setSelection(false);
						radio[0].setSelection(true);
						configProperty.setSelected(true);
					}
				}
			});
		}

		// ---------------------------------------------------------------------
		Composite keyComposite = new Composite(composite, SWT.BORDER);
		keyComposite.setLayoutData(new RowData(436, 100));

		labelWidget(keyComposite, SWT.CENTER | SWT.READ_ONLY, new int[] { 10, 10, 175, 15 }, PORT);
		labelWidget(keyComposite, SWT.CENTER | SWT.READ_ONLY, new int[] { 191, 10, 235, 15 }, LOOKUP_KEYS);

		textBoxWidget(keyComposite, new int[] { 10, 31, 175, 21 }, IN_PORT0, false);
		textBoxWidget(keyComposite, new int[] { 10, 58, 175, 21 }, IN_PORT1, false);

		driverText = textBoxWidget(keyComposite, new int[] { 191, 31, 220, 21 }, "", false);
		lookupText = textBoxWidget(keyComposite, new int[] { 191, 58, 220, 21 }, "", false);
		driverText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
		lookupText.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
		labelWidget(keyComposite, SWT.CENTER | SWT.READ_ONLY, new int[] { 10, 10, 175, 15 }, PORT);

		driverEditLableAsButton = labelWidget(keyComposite, SWT.CENTER | SWT.READ_ONLY, new int[] { 415, 28, 20, 20 },
				INSERT_IMAGE);
		driverEditLableAsButton.setImage(ImagePathConstant.EDIT_BUTTON.getImageFromRegistry());
		
		driverEditLableAsButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				driverKey = launchDialogToSelectFields(driverKey, IN_PORT0);
				driverText.setText(driverKey);

			}

		});

		lookupEditLableAsButton = labelWidget(keyComposite, SWT.CENTER | SWT.READ_ONLY, new int[] { 415, 58, 20, 20 },
				"");
		lookupEditLableAsButton.setImage(ImagePathConstant.EDIT_BUTTON.getImageFromRegistry());

		lookupEditLableAsButton.addMouseListener(new MouseAdapter() {

            @Override
			public void mouseUp(MouseEvent e) {
				lookupKey = launchDialogToSelectFields(lookupKey, IN_PORT1);
				lookupText.setText(lookupKey);

			}

		});

		
		populateWidget();
		return container;
	}

	public void populateWidget() {
		if (StringUtils.isNotBlank(configProperty.getDriverKey())) {
			driverText.setText(configProperty.getDriverKey());
		}
		if (StringUtils.isNotBlank(configProperty.getLookupKey())) {
			lookupText.setText(configProperty.getLookupKey());
		}
		radio[0].setSelection(configProperty.isSelected() ? true : false);
		radio[1].setSelection(configProperty.isSelected() ? false : true);
	}

	public Button buttonWidget(Composite parent, int style, int[] bounds, String value) {
		Button button = new Button(parent, style);
		button.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
		button.setText(value);

		return button;
	}

	public Text textBoxWidget(Composite parent, int[] bounds, String textValue, boolean value) {
		Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY | SWT.LEFT);
		text.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
		text.setText(textValue);
		text.setEditable(value);

		return text;
	}
	public void setSourceFieldList(List<List<FilterProperties>> sourceFieldList) {
		this.sourceFieldList = sourceFieldList;
	}
	public Label labelWidget(Composite parent, int style, int[] bounds, String value) {
		Label label = new Label(parent, style);
		label.setBounds(bounds[0], bounds[1], bounds[2], bounds[3]);
		label.setText(value);

		return label;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(470, 420);
	}
	

	private String launchDialogToSelectFields(String availableValues, String socketId) {
		String teminalNumber=socketId.substring(socketId.length()-1);
		FieldDialog fieldDialog = new FieldDialog(new Shell(), propertyDialogButtonBar);
		fieldDialog.setPropertyFromCommaSepratedString(availableValues);
		if(!sourceFieldList.isEmpty() &&Integer.parseInt(teminalNumber) < sourceFieldList.size())
		{
		fieldDialog.setSourceFieldsFromPropagatedSchema(SchemaSyncUtility.INSTANCE.
				converterFilterPropertyListToStringList(sourceFieldList.get(Integer.parseInt(teminalNumber))));
		}
		fieldDialog.setComponentName(Constants.LOOKUP_KEYS_WINDOW_TITLE);
		fieldDialog.open();
		return fieldDialog.getResultAsCommaSeprated();

	}

	/**
	 * set propagated field names
	 * 
	 * @param propagatedFiledNames
	 */
	public void setPropagatedFieldProperty(Map<String, List<String>> propagatedFiledNames) {
		this.propagatedFiledNames = propagatedFiledNames;
	}

	@Override
	protected void okPressed() {
		configProperty.setLookupKey(lookupKey);
		configProperty.setDriverKey(driverKey);
		if(!oldConfigProperty.equals(configProperty)){
			propertyDialogButtonBar.enableApplyButton(true);
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.INFORMATION, Messages.LOOKUP_PORT_CHANGE);
		}
		
		
		super.okPressed();
	}
}
