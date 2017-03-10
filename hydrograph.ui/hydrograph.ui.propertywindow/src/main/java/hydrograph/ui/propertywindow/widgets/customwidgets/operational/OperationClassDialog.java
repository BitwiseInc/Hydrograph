
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

package hydrograph.ui.propertywindow.widgets.customwidgets.operational;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.component.config.Operations;
import hydrograph.ui.common.component.config.TypeInfo;
import hydrograph.ui.common.datastructures.tooltip.TootlTipErrorMessage;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.propertywindow.messagebox.ConfirmCancelMessageBox;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.filterproperty.ELTFilterContentProvider;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultCheckBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultCombo;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultTextBox;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTSWTWidgets;
import hydrograph.ui.propertywindow.widgets.interfaces.IOperationClassDialog;
import hydrograph.ui.propertywindow.widgets.utility.FilterOperationClassUtility;
import hydrograph.ui.propertywindow.widgets.utility.SchemaButtonsSyncUtility;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

public class OperationClassDialog extends Dialog implements IOperationClassDialog {

	private static final String PATH = "path";
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	private Text fileName;
	private Combo operationClasses;
	private Button isParameterCheckBox;
	private Button applyButton;
	private Button okButton;
	private Composite container;
	private boolean isOkPressed;
	private boolean isCancelPressed;
	private boolean isApplyPressed;
	private boolean closeDialog;
	private PropertyDialogButtonBar operationClassDialogButtonBar;
	private TootlTipErrorMessage tootlTipErrorMessage = new TootlTipErrorMessage();
	private WidgetConfig widgetConfig;
	private String componentName;
	private ControlDecoration alphanumericDecorator;
	private ControlDecoration emptyDecorator;
	private ControlDecoration parameterDecorator;
	private boolean isYesPressed = false;
	private boolean isNoPressed = false;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private PropertyDialogButtonBar opeartionClassDialogButtonBar;
	private Button cancelButton;
    private TransformDialog transformDialog;
	private MappingSheetRow mappingSheetRow;
    private Label errorLabel; 
	private Integer windowButtonWidth = 30;
	private Integer windowButtonHeight = 25;
	private Integer macButtonWidth = 40;
	private Integer macButtonHeight = 30;
	private Composite buttonComposite;
	private ELTSWTWidgets widget = new ELTSWTWidgets();
	private boolean ctrlKeyPressed = false;
	private Table table_2;
	private  TableViewer nameValueTableViewer;

	public OperationClassDialog(Shell parentShell, String componentName, MappingSheetRow mappingSheetRow,
			PropertyDialogButtonBar propertyDialogButtonBar, WidgetConfig widgetConfig,TransformDialog transformDialog ) {
		super(parentShell);
		setShellStyle(SWT.CLOSE |SWT.RESIZE);
		this.widgetConfig = widgetConfig;
		this.componentName = componentName;
		this.propertyDialogButtonBar = propertyDialogButtonBar;
		opeartionClassDialogButtonBar = new PropertyDialogButtonBar(parentShell);
		this.mappingSheetRow = mappingSheetRow;
		this.transformDialog=transformDialog;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	public Control createDialogArea(Composite parent) {
		container = (Composite) super.createDialogArea(parent);

		container.setLayout(new GridLayout(1, false));

		container.getShell().setText(Messages.OPERATION_CLASS);

		operationClassDialogButtonBar = new PropertyDialogButtonBar(container);

		Composite composite = new Composite(container, SWT.BORDER);
		container.getShell().setMinimumSize(550, 400);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_composite.heightHint = 104;
		gd_composite.widthHint = 450;
		composite.setLayoutData(gd_composite);

		composite.setLayout(new GridLayout(1, false));

		AbstractELTWidget fileNameText = new ELTDefaultTextBox().grabExcessHorizontalSpace(true).textBoxWidth(150);

		AbstractELTWidget isParameterCheckbox = new ELTDefaultCheckBox(Constants.IS_PARAMETER).checkBoxLableWidth(100);

		Operations operations = XMLConfigUtil.INSTANCE.getComponent(componentName).getOperations();
		List<TypeInfo> typeInfos = operations.getStdOperation();
		String optionsOfComboOfOperationClasses[] = new String[typeInfos.size() + 1];
		optionsOfComboOfOperationClasses[0] = Messages.CUSTOM;
		for (int i = 0; i < typeInfos.size(); i++) {
			optionsOfComboOfOperationClasses[i + 1] = typeInfos.get(i).getName();
		}
		AbstractELTWidget comboOfOperationClasses = new ELTDefaultCombo().defaultText(optionsOfComboOfOperationClasses)
				.comboBoxWidth(90);

		FilterOperationClassUtility.INSTANCE.createOperationalClass(composite, operationClassDialogButtonBar,
				comboOfOperationClasses, isParameterCheckbox, fileNameText, tootlTipErrorMessage, widgetConfig, this,
				propertyDialogButtonBar, opeartionClassDialogButtonBar);
		fileName = (Text) fileNameText.getSWTWidgetControl();
		fileName.setData(PATH, mappingSheetRow.getOperationClassFullPath());
		operationClasses = (Combo) comboOfOperationClasses.getSWTWidgetControl();
        
		FilterOperationClassUtility.INSTANCE.enableAndDisableButtons(true, false);
		FilterOperationClassUtility.INSTANCE.setComponentName(componentName);
		isParameterCheckBox = (Button) isParameterCheckbox.getSWTWidgetControl();
		alphanumericDecorator = WidgetUtility.addDecorator(fileName, Messages.CHARACTERSET);
		alphanumericDecorator.setMarginWidth(2);
		emptyDecorator = WidgetUtility.addDecorator(fileName, Messages.OperationClassBlank);
		emptyDecorator.setMarginWidth(2);
		parameterDecorator = WidgetUtility.addDecorator(fileName, Messages.PARAMETER_ERROR);
		parameterDecorator.setMarginWidth(2);

		buttonComposite = new Composite(container, SWT.NONE);
		GridData gd_composite_3 = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_composite_3.heightHint = 44;
		gd_composite_3.widthHint = 450;
		buttonComposite.setLayoutData(gd_composite_3);

		Composite nameValueComposite = new Composite(container, SWT.None);
		GridData gd_nameValueComposite = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_nameValueComposite.widthHint = 450;
		gd_nameValueComposite.heightHint = 251;
		nameValueComposite.setLayoutData(gd_nameValueComposite);
		GridLayout name_gd = new GridLayout(1, false);
		name_gd.marginWidth=0;
		name_gd.marginRight=0;
		nameValueComposite.setLayout(name_gd);

		nameValueTableViewer = new TableViewer(nameValueComposite, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI);
		table_2 = nameValueTableViewer.getTable();
		addResizbleListner(table_2);
		GridData gd_table_2 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table_2.heightHint = 100;
		if(OSValidator.isMac()){
		gd_table_2.widthHint = 507;
		}
		else{
			gd_table_2.widthHint =504; 
		}
		table_2.setLayoutData(gd_table_2);
		transformDialog.setTableViewer(nameValueTableViewer, nameValueComposite, new String[] {
				Messages.PROPERTY_NAME, Messages.PROPERTY_VALUE }, new ELTFilterContentProvider());
		nameValueTableViewer.setLabelProvider(new PropertyLabelProvider());
		nameValueTableViewer.setCellModifier(new PropertyGridCellModifier(this,nameValueTableViewer,operationClassDialogButtonBar));
		nameValueTableViewer.setInput(mappingSheetRow.getNameValueProperty());
		attachShortcutListner(nameValueTableViewer);
		table_2.getColumn(0).setWidth(259);
		table_2.getColumn(1).setWidth(262);

		int addButtonSize;
		if(OSValidator.isMac()){
			addButtonSize=318;
		}else{
			addButtonSize = 325;
		}
		Button addButton = widget.buttonWidget(buttonComposite, SWT.CENTER, new int[] { addButtonSize, 17, 20, 15 }, "");
		addButton.setImage(ImagePathConstant.ADD_BUTTON.getImageFromRegistry());
		addButton.setToolTipText(Messages.ADD_KEY_SHORTCUT_TOOLTIP);
		SchemaButtonsSyncUtility.INSTANCE.buttonSize(addButton, macButtonWidth, macButtonHeight, windowButtonWidth, windowButtonHeight);
		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				addNewRow(nameValueTableViewer);
			}
		});
		
		table_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				addNewRow(nameValueTableViewer);
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}
		}); 
		
		int deleteButtonSize;
		if(OSValidator.isMac()){
			deleteButtonSize = 348;
		}else{
			deleteButtonSize = 355;
		}
		Button deleteButton = widget.buttonWidget(buttonComposite, SWT.CENTER, new int[] { deleteButtonSize, 17, 20, 15 }, "");
		deleteButton.setImage(ImagePathConstant.DELETE_BUTTON.getImageFromRegistry());
		deleteButton.setToolTipText(Messages.DELETE_KEY_SHORTCUT_TOOLTIP);
		SchemaButtonsSyncUtility.INSTANCE.buttonSize(deleteButton, macButtonWidth, macButtonHeight, windowButtonWidth, windowButtonHeight);
		deleteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				deleteRow(nameValueTableViewer);
			}

		});

		int upButtonSize;
		if(OSValidator.isMac()){
			upButtonSize = 378;
		}else{
			upButtonSize = 385;
		}
		Button upButton = widget.buttonWidget(buttonComposite, SWT.CENTER, new int[] { upButtonSize, 17, 20, 15 }, "");
		upButton.setImage(ImagePathConstant.MOVEUP_BUTTON.getImageFromRegistry());
		upButton.setToolTipText(Messages.MOVE_UP_KEY_SHORTCUT_TOOLTIP);
		SchemaButtonsSyncUtility.INSTANCE.buttonSize(upButton, macButtonWidth, macButtonHeight, windowButtonWidth, windowButtonHeight);
		upButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				moveRowUp(nameValueTableViewer);
			}

		});
		
		int downButtonSize;
		if(OSValidator.isMac()){
			downButtonSize = 408;
		}else{
			downButtonSize = 415;
		}
		Button downButton = widget.buttonWidget(buttonComposite, SWT.CENTER, new int[] { downButtonSize, 17, 20, 15 }, "");
		downButton.setImage(ImagePathConstant.MOVEDOWN_BUTTON.getImageFromRegistry());
		downButton.setToolTipText(Messages.MOVE_DOWN_KEY_SHORTCUT_TOOLTIP);
		SchemaButtonsSyncUtility.INSTANCE.buttonSize(downButton, macButtonWidth, macButtonHeight, windowButtonWidth, windowButtonHeight);	
		downButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				moveRowDown(nameValueTableViewer);
			}

		});

		populateWidget();
		Composite errorComposite=new Composite(container, SWT.NONE);
		errorComposite.setLayout(new GridLayout(1,false));
		GridData griddata=new GridData(SWT.TOP,SWT.TOP,false,false,1,1);
		
		errorComposite.setLayoutData(griddata);
		
		errorLabel=new Label(errorComposite,SWT.NONE);
		errorLabel.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
		errorLabel.setText(Messages.EmptyFiledNotification);
		errorLabel.setVisible(false);
		checkNameValueFieldBlankOrNot();
		return container;
	}

	private void moveRowUp(TableViewer nameValueTableViewer)
	{

		Table table = nameValueTableViewer.getTable();
		int[] indexes = table.getSelectionIndices();
		for (int index : indexes) {

			if (index > 0) {
				Collections.swap(mappingSheetRow.getNameValueProperty(), index, index - 1);
				nameValueTableViewer.refresh();
				applyButton.setEnabled(true);
			}
		}
	
	}
	
	private void moveRowDown(TableViewer nameValueTableViewer)
	{
		Table table = nameValueTableViewer.getTable();
		int[] indexes = table.getSelectionIndices();
		for (int i = indexes.length - 1; i > -1; i--) {

			if (indexes[i] < mappingSheetRow.getNameValueProperty().size() - 1) {
				Collections.swap(mappingSheetRow.getNameValueProperty(), indexes[i], indexes[i] + 1);
				nameValueTableViewer.refresh();
				applyButton.setEnabled(true);

			}
		}
	
	}
	
	private void deleteRow(TableViewer nameValueTableViewer)
	{
		WidgetUtility.setCursorOnDeleteRow(nameValueTableViewer, mappingSheetRow.getNameValueProperty());
		nameValueTableViewer.refresh();
	
	}
	
	private void addNewRow(TableViewer nameValueTableViewer){
		NameValueProperty nameValueProperty = new NameValueProperty();
		nameValueProperty.setPropertyName("");
		nameValueProperty.setPropertyValue("");
		if (!mappingSheetRow.getNameValueProperty().contains(nameValueProperty)) {
			mappingSheetRow.getNameValueProperty().add(nameValueProperty);
			nameValueTableViewer.refresh();
			nameValueTableViewer.editElement(nameValueTableViewer.getElementAt(mappingSheetRow.getNameValueProperty().size() - 1), 0);
			applyButton.setEnabled(true);
		}
	}
	private void addResizbleListner(final Table table) {
		table.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				table.getColumn(0).setWidth((table.getSize().x/2)-3);
				if(OSValidator.isMac()){
					table.getColumn(1).setWidth((table.getSize().x/2)+1);
				}else{
					table.getColumn(1).setWidth((table.getSize().x/2)-2);
				}
			}
			
			public void controlMoved(ControlEvent e) {/*do nothing*/}
		});
		
	}
	
	public void pressOK() {
		okPressed();
		isYesPressed = true;
	}

	/**
	 * Populate widget.
	 */
	public void populateWidget() {
		
		if (mappingSheetRow.getOperationClassPath()!=null && StringUtils.isNotBlank(mappingSheetRow.getOperationClassPath())) 
		{
			fileName.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			emptyDecorator.hide();
			mappingSheetRow.setComboBoxValue(mappingSheetRow.getComboBoxValue());
			fileName.setText(mappingSheetRow.getOperationClassPath());
			operationClasses.setText(mappingSheetRow.getComboBoxValue());
			isParameterCheckBox.setSelection(mappingSheetRow.isClassParameter());
			if (!StringUtils.equalsIgnoreCase(Messages.CUSTOM, mappingSheetRow.getComboBoxValue())) {
				fileName.setEnabled(false);
				FilterOperationClassUtility.INSTANCE.enableAndDisableButtons(false, false);
				isParameterCheckBox.setEnabled(false);
			} else {
				isParameterCheckBox.setEnabled(true);
				if (isParameterCheckBox.getSelection()) {
					FilterOperationClassUtility.INSTANCE.enableAndDisableButtons(true, true);
				}
			}
		} else {
			FilterOperationClassUtility.INSTANCE.getOpenBtn().setEnabled(false);
			fileName.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
			operationClasses.select(0);
			isParameterCheckBox.setEnabled(false);
		}
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		createApplyButton(parent);

		operationClassDialogButtonBar.setPropertyDialogButtonBar(okButton, applyButton, cancelButton);
		alphanumericDecorator.hide();
		parameterDecorator.hide();
		isParameterCheckBox.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isParameterCheckBox.getSelection()) {
					hasTextBoxAlphanumericCharactorsOnly(fileName.getText());
					if (StringUtils.isNotBlank(fileName.getText()) && !fileName.getText().startsWith("@{")
							&& !fileName.getText().endsWith("}")) {
						fileName.setText("@{" + fileName.getText() + "}");
						fileName.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
					}
				} else {
					if (StringUtils.isNotBlank(fileName.getText()) && fileName.getText().startsWith("@{")) {
						fileName.setText(fileName.getText().substring(2, fileName.getText().length() - 1));
						fileName.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
					}
					okButton.setEnabled(true);
					applyButton.setEnabled(true);
					alphanumericDecorator.hide();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		fileName.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent e) {
				String currentText = ((Text) e.widget).getText();
				String textBoxValue = (currentText.substring(0, e.start) + e.text + currentText.substring(e.end))
						.trim();
				if (isParameterCheckBox.getSelection()) {
					if (StringUtils.isNotBlank(textBoxValue)
							&& (!textBoxValue.startsWith("@{") || !textBoxValue.endsWith("}"))) {
						((Text) e.widget).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
						parameterDecorator.show();
						emptyDecorator.hide();
						okButton.setEnabled(false);
						applyButton.setEnabled(false);
					} else {
						((Text) e.widget).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
						emptyDecorator.show();
						parameterDecorator.hide();
						okButton.setEnabled(true);
						hasTextBoxAlphanumericCharactorsOnly(textBoxValue);
					}
				} else {
					if (StringUtils.isNotBlank(textBoxValue)) {
						((Text) e.widget).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
						applyButton.setEnabled(true);
						isParameterCheckBox.setEnabled(true);
						emptyDecorator.hide();
					} else {
						((Text) e.widget).setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
						isParameterCheckBox.setEnabled(false);
						emptyDecorator.show();
					}
				}
			}
		});

	}

	private void hasTextBoxAlphanumericCharactorsOnly(String textBoxValue) {
		if (StringUtils.isNotBlank(textBoxValue)) {
			fileName.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 255));
			emptyDecorator.hide();
			Matcher matchs = Pattern.compile(Constants.REGEX).matcher(textBoxValue);
			if (!matchs.matches()) {
				alphanumericDecorator.show();
				okButton.setEnabled(false);
				applyButton.setEnabled(false);
			} else {
				alphanumericDecorator.hide();
				okButton.setEnabled(true);
				applyButton.setEnabled(true);
			}
		} else {
			fileName.setBackground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 255, 204));
			emptyDecorator.show();
			alphanumericDecorator.hide();
			okButton.setEnabled(false);
			applyButton.setEnabled(false);
		}
	}

	private void createApplyButton(Composite parent) {
		applyButton = createButton(parent, IDialogConstants.NO_ID, "Apply", false);
		disableApplyButton();
		opeartionClassDialogButtonBar.setPropertyDialogButtonBar(okButton, applyButton, cancelButton);
	}

	private void disableApplyButton() {
		applyButton.setEnabled(false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(540, 500);
	}

	

	@Override
	protected void cancelPressed() {
		if (applyButton.isEnabled()) {

			if (!isNoPressed) {
				ConfirmCancelMessageBox confirmCancelMessageBox = new ConfirmCancelMessageBox(container);
				MessageBox confirmCancleMessagebox = confirmCancelMessageBox.getMessageBox();

				if (confirmCancleMessagebox.open() == SWT.OK) {
					closeDialog=super.close();
				}
			} else {
				closeDialog=super.close();
			}

		} else {
			closeDialog=super.close();
		}
		isCancelPressed=true;
	}

	@Override
	protected void okPressed() {
		
		if(OSValidator.isMac()){
			for(CellEditor cellEditor : nameValueTableViewer.getCellEditors()){
				if(cellEditor !=null){
				cellEditor.getControl().setEnabled(false); //Saves the existing value of CellEditor
				cellEditor.getControl().setEnabled(true);
				}
			}
		}
        if(checkNameValueFieldBlankOrNot()) 
        {
        	mappingSheetRow = new MappingSheetRow(mappingSheetRow.getInputFields(), mappingSheetRow.getOutputList(),
			mappingSheetRow.getOperationID(), operationClasses.getText(), fileName.getText(),
			mappingSheetRow.getNameValueProperty(), isParameterCheckBox.getSelection(),
			mappingSheetRow.getWholeOperationParameterValue(), 
			mappingSheetRow.isWholeOperationParameter(),
			(String)fileName.getData(PATH),mappingSheetRow.isExpression(),null,null,mappingSheetRow.isActive());
		    isOkPressed=true;
		    super.okPressed();
        }
	}
	
	public  boolean checkNameValueFieldBlankOrNot()
	{
		if(!mappingSheetRow.getNameValueProperty().isEmpty())
		{
			for(NameValueProperty nameValueProperty:mappingSheetRow.getNameValueProperty())
			{
				if(StringUtils.isBlank(nameValueProperty.getPropertyName()) && StringUtils.isBlank(nameValueProperty.getPropertyValue()))
				{
					errorLabel.setText(Messages.EmptyFiledNotification);
					errorLabel.setVisible(true);
					return false;
				}	
				else if(StringUtils.isBlank(nameValueProperty.getPropertyName()))
						{
					errorLabel.setText(Messages.EmptyNameNotification);
					errorLabel.setVisible(true);
					return false;
						}		
				else if(StringUtils.isBlank(nameValueProperty.getPropertyValue()))
				{
			        errorLabel.setText(Messages.EmptyValueNotification+"                   ");
			        errorLabel.setVisible(true);
			        return true;
				}	
			}	
		}
		errorLabel.setVisible(false);
	  return true;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == 3 && checkNameValueFieldBlankOrNot()) {
			mappingSheetRow = new MappingSheetRow(mappingSheetRow.getInputFields(), mappingSheetRow.getOutputList(),
					mappingSheetRow.getOperationID(), operationClasses.getText(), fileName.getText(),
					mappingSheetRow.getNameValueProperty(), isParameterCheckBox.getSelection(),
					mappingSheetRow.getWholeOperationParameterValue(), mappingSheetRow.isWholeOperationParameter(),
					(String)fileName.getData(PATH),mappingSheetRow.isExpression(),null,null,mappingSheetRow.isActive());
			applyButton.setEnabled(false);
			isApplyPressed=true;
		} else {
			super.buttonPressed(buttonId);
		}

	}

	public MappingSheetRow getMappingSheetRow() {
		return mappingSheetRow;
	}

	public String getTootlTipErrorMessage() {
		return tootlTipErrorMessage.getErrorMessage();
	}

	/**
	 * 
	 * returns true if ok button pressed from code
	 * 
	 * @return boolean
	 */
	public boolean isYesPressed() {
		return isYesPressed;
	}
	
	public boolean isNoPressed() {
		return isNoPressed;
	}
	
	public boolean isOkPressed()
	{
	return isOkPressed;	
	}
	
	public boolean isCancelPressed()
	{
		return isCancelPressed;
	}
	public boolean isApplyPressed()
	{
	return isApplyPressed;	
	}
	
	@Override
	public void pressCancel() {
		isNoPressed = true;
		cancelPressed();
	}

	private void attachShortcutListner(final TableViewer nameValueTableViewer){
		Control currentControl = table_2;
		
		currentControl.addKeyListener(new KeyListener() {						
			
			@Override
			public void keyReleased(KeyEvent event) {				
				if(event.keyCode == SWT.CTRL || event.keyCode == SWT.COMMAND){					
					ctrlKeyPressed = false;
				}							
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				if(event.keyCode == SWT.CTRL || event.keyCode == SWT.COMMAND){					
					ctrlKeyPressed = true;
				}
								
				if (ctrlKeyPressed && event.keyCode == Constants.KEY_D) {				
					deleteRow(nameValueTableViewer);
				}
				
				else if (ctrlKeyPressed && event.keyCode == Constants.KEY_N){
					addNewRow(nameValueTableViewer);
				}
				
				else if (ctrlKeyPressed && event.keyCode == SWT.ARROW_UP){
					moveRowUp(nameValueTableViewer);				
				}
				
				else if (ctrlKeyPressed && event.keyCode == SWT.ARROW_DOWN){
					moveRowDown(nameValueTableViewer);
				}
			}
		});
	}
	
	@Override
	public boolean close() {
		if(!isOkPressed){
			cancelPressed();			
			return closeDialog;
		}else{
			return super.close();
		}		
	}
	/**
	 * 
	 * returns true if cancel button pressed from code
	 * 
	 * @return boolean
	 */
	
}
