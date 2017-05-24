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

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.propertywindow.handlers.ShowHidePropertyHelpHandler;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.dialogs.FieldDialog;
/**
 * LoadTypeConfigurationDialog class creates the dialog for different load types in DB components
 * @author Bitwise
 *
 */
public class LoadTypeConfigurationDialog extends Dialog {
	private Text updateTextBox;
	private Text newTableTextBox;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private String windowLabel;
	private boolean okPressed;
	private List<String> schemaFields;
	private Button updateRadioButton;
	public Map<String, String> loadTypeConfigurationSelectedValue;
	private Button newTableRadioButton;
	private Button insertRadioButton;
	private Button replaceRadioButton;
	private Button[] radioButtons = new Button[]{newTableRadioButton, insertRadioButton, replaceRadioButton};
	private boolean ShowHidePropertyHelpChecked;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public LoadTypeConfigurationDialog(Shell parentShell, PropertyDialogButtonBar propertyDialogButtonBar , String windowLabel , List<String> fields ,  Map<String, String> loadTypeConfigurationSelectedValue) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL);
		this.propertyDialogButtonBar = propertyDialogButtonBar;
		if(StringUtils.isNotBlank(windowLabel))
			this.windowLabel=windowLabel;
		else
			this.windowLabel=Messages.LOAD_TYPE_CONFIGURATION_WINDOW_LABEL;
		this.schemaFields = fields;
		this.loadTypeConfigurationSelectedValue = loadTypeConfigurationSelectedValue;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.getShell().setText(windowLabel);
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(1, false));
		
		Group grpLoadType = new Group(composite, SWT.NONE);
		grpLoadType.setText(Messages.LOAD_TYPE);
		grpLoadType.setLayout(new GridLayout(1, false));
		grpLoadType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite loadConfigurationComposite = new Composite(grpLoadType, SWT.NONE);
		loadConfigurationComposite.setLayout(new GridLayout(3, false));
		GridData gd_loadConfigurationComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_loadConfigurationComposite.heightHint = 82;
		loadConfigurationComposite.setLayoutData(gd_loadConfigurationComposite);
		
		//TODO
		/*
		 * Currently, we are not showing update widget. So, below code will be comment out.
		 * Engine team is currently working on this. So, we can use this code in future.
		 */
		/*updateRadioButton = new Button(loadConfigurationComposite, SWT.RADIO);
		updateRadioButton.setText(Constants.LOAD_TYPE_UPDATE_KEY);
		
		updateTextBox = new Text(loadConfigurationComposite, SWT.BORDER);
		updateTextBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		updateTextBox.setEnabled(false);*/
		
		/*Button updateKeysButton = new Button(loadConfigurationComposite, SWT.NONE);
		
		updateKeysButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		updateKeysButton.setText(Messages.UPDATE_BY_KEYS);
		updateKeysButton.setEnabled(false);*/
		
		newTableRadioButton = new Button(loadConfigurationComposite, SWT.RADIO);
		newTableRadioButton.setText(Constants.LOAD_TYPE_NEW_TABLE_KEY);
		newTableRadioButton.setSelection(true);
		
		newTableTextBox = new Text(loadConfigurationComposite, SWT.BORDER);
		newTableTextBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		newTableTextBox.setEnabled(true);
		
		Button primaryKeysButton = new Button(loadConfigurationComposite, SWT.NONE);
		primaryKeysButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		primaryKeysButton.setText(Messages.PRIMARY_KEYS_WINDOW_LABEL);
		primaryKeysButton.setEnabled(true);
		
		insertRadioButton = new Button(loadConfigurationComposite, SWT.RADIO);
		insertRadioButton.setText(Constants.LOAD_TYPE_INSERT_KEY);
		new Label(loadConfigurationComposite, SWT.NONE);
		new Label(loadConfigurationComposite, SWT.NONE);
		
		replaceRadioButton = new Button(loadConfigurationComposite, SWT.RADIO);
		replaceRadioButton.setText(Constants.LOAD_TYPE_REPLACE_KEY);
		new Label(loadConfigurationComposite, SWT.NONE);
		new Label(loadConfigurationComposite, SWT.NONE);
		insertRadioButton.addSelectionListener(buttonSelectionListener(/*updateTextBox*/ newTableTextBox, /*updateKeysButton*/ primaryKeysButton));
		replaceRadioButton.addSelectionListener(buttonSelectionListener(/*updateTextBox*/ newTableTextBox, /*updateKeysButton*/ primaryKeysButton));
		
		//TODO as above
		/*updateRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					//updateKeysButton.setEnabled(true);
					updateTextBox.setEnabled(true);
					primaryKeysButton.setEnabled(false);
					newTableTextBox.setEnabled(false);
				}
		});*/
		
		newTableRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newTableRadioButton.setSelection(true);
				primaryKeysButton.setEnabled(true);
				newTableTextBox.setEnabled(true);
				if(OSValidator.isMac()){
					newTableRadioButton.setFocus();
				}
				//checkButtonSelection(radioButtons, newTableRadioButton);
				//TODO as above
				//updateKeysButton.setEnabled(false);
				//updateTextBox.setEnabled(false);
			}
		});
		
		//updateKeysButton.addSelectionListener(updateAndPrimaryWidgetSelection(updateKeysButton, updateTextBox));
		
		primaryKeysButton.addSelectionListener(updateAndPrimaryWidgetSelection(primaryKeysButton, newTableTextBox));
		
		if(loadTypeConfigurationSelectedValue!=null && !loadTypeConfigurationSelectedValue.isEmpty() ){
			if(loadTypeConfigurationSelectedValue.get(Constants.LOAD_TYPE_NEW_TABLE_KEY) != null){
				newTableRadioButton.setSelection(true);
				newTableTextBox.setText(loadTypeConfigurationSelectedValue.get(Constants.LOAD_TYPE_NEW_TABLE_KEY));
			}else if(loadTypeConfigurationSelectedValue.get(Constants.LOAD_TYPE_INSERT_KEY) != null){
				insertRadioButton.setSelection(true);
				newTableRadioButton.setSelection(false);
				primaryKeysButton.setEnabled(false);
				newTableTextBox.setEnabled(false);
			}else if(loadTypeConfigurationSelectedValue.get(Constants.LOAD_TYPE_REPLACE_KEY) != null){
				replaceRadioButton.setSelection(true);
				newTableRadioButton.setSelection(false);
				primaryKeysButton.setEnabled(false);
				newTableTextBox.setEnabled(false);
			}
			//TODO as above
			/*else if(loadTypeConfigurationSelectedValue.get(Constants.LOAD_TYPE_UPDATE_KEY) != null){
				updateRadioButton.setSelection(true);
				updateTextBox.setText(loadTypeConfigurationSelectedValue.get(Constants.LOAD_TYPE_UPDATE_KEY));
				//updateKeysButton.setEnabled(true);
				updateTextBox.setEnabled(true);
			}*/
		}else{
			//updateRadioButton.setEnabled(true);
		}
		
		setPropertyHelpText();
		return container;
	}
	
	/**
	 * Set the Property Help Text
	 */
	private void setPropertyHelpText() {
		if(ShowHidePropertyHelpHandler.getInstance() != null 
				&& ShowHidePropertyHelpHandler.getInstance().isShowHidePropertyHelpChecked()){
			newTableRadioButton.setToolTipText(Messages.LOADCONFIG_NEWTABLE);
			newTableRadioButton.setCursor(new Cursor(newTableRadioButton.getDisplay(), SWT.CURSOR_HELP));
			
			insertRadioButton.setToolTipText(Messages.LOADCONFIG_INSERT);
			insertRadioButton.setCursor(new Cursor(insertRadioButton.getDisplay(), SWT.CURSOR_HELP));
			
			replaceRadioButton.setToolTipText(Messages.LOADCONFIG_REPLACE);
			replaceRadioButton.setCursor(new Cursor(replaceRadioButton.getDisplay(), SWT.CURSOR_HELP));
		}
	}
	
	/**
	 * The Function will call to disable the widgets
	 * @param textbox1
	 * @param textbox2
	 * @param buttonWidgets
	 * @return Selection Adapter
	 */
	private SelectionAdapter buttonSelectionListener( Text textbox2,Widget... buttonWidgets){
		Supplier<Stream<Widget>> streamSupplier = () -> Stream.of(buttonWidgets);
		SelectionAdapter adapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newTableRadioButton.setSelection(false);
				textbox2.setEnabled(false);
				streamSupplier.get().forEach((Widget widgets) ->{((Button)widgets).setEnabled(false);});
				if(OSValidator.isMac()){
					((Button)event.getSource()).setFocus();
				}
				propertyDialogButtonBar.enableApplyButton(true);
			}
		};
		return adapter;
	}
	
	/**
	 * The Function will cal on update and primary key button selection listener
	 * @param butoon
	 * @param textBox
	 * @return Selection Adapter
	 */
	private SelectionAdapter updateAndPrimaryWidgetSelection(Button butoon, Text textBox){
		SelectionAdapter adapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String buttonText;
				//TODO as above
				/*if(StringUtils.equalsIgnoreCase(butoon.getText(), Messages.UPDATE_BY_KEYS)){
					buttonText = Messages.UPDATE_KEYS_WINDOW_LABEL;
				}else{*/
				buttonText = Messages.PRIMARY_KEYS_WINDOW_LABEL;
				FieldDialog fieldDialog = new FieldDialog(new Shell(), propertyDialogButtonBar);
				fieldDialog.setComponentName(buttonText);
				fieldDialog.setSourceFieldsFromPropagatedSchema(schemaFields);
				if(StringUtils.isNotBlank(textBox.getText())){
					fieldDialog.setPropertyFromCommaSepratedString(textBox.getText());
				}
				fieldDialog.open();
				String valueForNewTableTextBox = fieldDialog.getResultAsCommaSeprated();
				if(valueForNewTableTextBox !=null){
					textBox.setText(valueForNewTableTextBox);
				}
			}
		};
		return adapter;	
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	/**
	 * Getter for LoadType Selected value
	 * @return
	 */
	public Map<String, String> getSelectedPropertyValue(){
		return loadTypeConfigurationSelectedValue;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(686, 226);
	}
	
	@Override
	protected void okPressed() {
		loadTypeConfigurationSelectedValue.clear();
		//TODO as above
		/*if(updateRadioButton.getSelection()){
				loadTypeConfigurationSelectedValue.put(updateRadioButton.getText() ,updateTextBox.getText() );
		}*/
		
		if(newTableRadioButton.getSelection()){
			loadTypeConfigurationSelectedValue.put(newTableRadioButton.getText(), newTableTextBox.getText());
		}
		
		if(insertRadioButton.getSelection()){
			loadTypeConfigurationSelectedValue.put(insertRadioButton.getText(), insertRadioButton.getText());
		}
		
		if(replaceRadioButton.getSelection()){
			loadTypeConfigurationSelectedValue.put(replaceRadioButton.getText(), replaceRadioButton.getText());
		}	
		super.okPressed();
	}
	
	public boolean isOkPressed(){
		return this.okPressed;
	}
}
