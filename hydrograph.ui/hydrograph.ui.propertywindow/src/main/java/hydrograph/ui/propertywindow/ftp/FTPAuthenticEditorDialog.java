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
package hydrograph.ui.propertywindow.ftp;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;

import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.handlers.ShowHidePropertyHelpHandler;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/**
 * AuthenticationEditorDialog create authentication editor dialog
 * @author bitwise
 *
 */
public class FTPAuthenticEditorDialog extends Dialog{
	private static final Logger logger = LogFactory.INSTANCE.getLogger(FTPAuthenticEditorDialog.class);
	private String windowLabel;
	private String protocolText;
	private Cursor cursor;
	private PropertyDialogButtonBar propertyDialogButtonBar;
	private Combo authenticationModeCombo;
	private String[] optionList;
	private Composite keyFileComposite;
	private Composite basicAuthComposite;
	private Map<String, FTPAuthOperationDetails> authOperationSelectionMap;
	private Text text1;
	private Text text2;
	
	

	public FTPAuthenticEditorDialog(Shell parentShell, String windowTitle,PropertyDialogButtonBar propertyDialogButtonBar,
			Map<String, FTPAuthOperationDetails> initialMap, Cursor cursor, String[] optionList, String protocolText) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.WRAP | SWT.APPLICATION_MODAL | SWT.RESIZE);
		if (StringUtils.isNotBlank(windowTitle))
			windowLabel = windowTitle;
		else
			windowLabel = Messages.ADDITIONAL_PARAMETERS_FOR_DB_WINDOW_LABEL;
		this.propertyDialogButtonBar = propertyDialogButtonBar;
		this.cursor = cursor;
		this.optionList = optionList;
		this.protocolText = protocolText;
		this.authOperationSelectionMap = initialMap;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		logger.debug("authentication editor dialog created");
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.getShell().setText(windowLabel);

		int CONST_HEIGHT = 226;
		
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
		
		Composite composite = new Composite(container, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		FTPWidgetUtility ftpWidgetUtility = new FTPWidgetUtility();
		Label partitionKeysLabel = (Label) ftpWidgetUtility.createLabel(composite, "Authentication Mode");
		setPropertyHelpText(partitionKeysLabel, "User needs to provide credentials to connect to the ftp server");
		authenticationModeCombo = (Combo) ftpWidgetUtility.CreateCombo(composite, optionList);
		
		
		Composite composite2 = new Composite(container, SWT.NONE);
		composite2.setLayout(new GridLayout(1, false));
		composite2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Composite stackComposite = new Composite(composite2, SWT.NONE);
		StackLayout layout = new StackLayout();
		stackComposite.setLayout(layout);
		stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		stackComposite.getShell().setText("Authentication Editor");
		createAuthenticationEditorDialog(stackComposite, layout);
		
		addModifyListener(text1);
		addModifyListener(text2);
		
		populateWidget(stackComposite, layout);
		
		return container;
	}
	
	
	private void setPropertyHelpText(Label label, String message) {
		if(ShowHidePropertyHelpHandler.getInstance() != null 
				&& ShowHidePropertyHelpHandler.getInstance().isShowHidePropertyHelpChecked()){
			label.setToolTipText(message);
			label.setCursor(new Cursor(label.getDisplay(), SWT.CURSOR_HELP));
		}
		
	}

	private void addModifyListener(Text text){
		if(text != null && !text.isDisposed()){
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					Utils.INSTANCE.addMouseMoveListener(text, cursor);	
				}
			});
		}
	}
	
	private void populateWidget(Composite stackComposite, StackLayout stackLayout){
		String comboText1;
		String ComboText2;
		if(StringUtils.equalsIgnoreCase(protocolText, "AWS S3 HTTPS")){
			comboText1 = "AWS S3 Access Key";
			ComboText2 = "AWS S3 Property File";
		}else{
			comboText1 = Constants.STAND_AUTH;
			ComboText2 = "User ID and Key";
		}
		for(Map.Entry<String, FTPAuthOperationDetails> map : authOperationSelectionMap.entrySet()){
			authenticationModeCombo.setText(map.getKey());
			if(StringUtils.equalsIgnoreCase(comboText1, map.getKey())){
				authenticationModeCombo.select(0);
				basicAuthComposite = (Composite) addBasicAuthenticationComposite(stackComposite);
				stackLayout.topControl = basicAuthComposite;
				FTPAuthOperationDetails authOperationDetails = map.getValue();
				text1.setText(authOperationDetails.getField1());
				Utils.INSTANCE.addMouseMoveListener(text1, cursor);
				if(text2 != null){
					text2.setText(authOperationDetails.getField2());
					Utils.INSTANCE.addMouseMoveListener(text2, cursor);
				}
			}else if(StringUtils.equalsIgnoreCase(ComboText2, map.getKey())){
				authenticationModeCombo.select(1);
				keyFileComposite = (Composite) addIdKeyComposite(stackComposite);
				stackLayout.topControl = keyFileComposite;
				FTPAuthOperationDetails authOperationDetails = map.getValue();
				if(text1 != null && authOperationDetails.getField1()!=null){
					text1.setText(authOperationDetails.getField1());
					Utils.INSTANCE.addMouseMoveListener(text1, cursor);
				}
				text2.setText(authOperationDetails.getField2());
				Utils.INSTANCE.addMouseMoveListener(text2, cursor);
			}
		}
	}
	
	private void createAuthenticationEditorDialog(Composite stackComposite, StackLayout stackLayout){
		if(basicAuthComposite == null || basicAuthComposite.isDisposed()){
			basicAuthComposite = (Composite) addBasicAuthenticationComposite(stackComposite);
		}
		stackLayout.topControl = basicAuthComposite;
		authenticationModeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(authenticationModeCombo.getSelectionIndex() == 1){
					disposeComposite(basicAuthComposite);
					keyFileComposite = (Composite) addIdKeyComposite(stackComposite);
					updateWidgetsValue(text1, text2);
					stackLayout.topControl = keyFileComposite;
					refereshComposite(stackComposite);
				}else{
					if(optionList.length >1){
						disposeComposite(keyFileComposite);
						basicAuthComposite = (Composite) addBasicAuthenticationComposite(stackComposite);
						updateWidgetsValue(text1, text2);
						stackLayout.topControl = basicAuthComposite;
						refereshComposite(stackComposite);
					}
				}
				
				refreshUI();
			}
		});
	}
	
	private void refereshComposite(Composite composite){
		composite.layout();
		composite.getParent().layout();
		composite.getParent().getParent().layout();
		composite.getParent().getParent().getParent().layout();
	}
	
	private void disposeComposite(Composite composite){
		if(composite !=null && !composite.isDisposed()){
			composite.dispose();
		}
	}
	
	private void updateWidgetsValue(Widget... widgets){
		if(optionList.length >1){
			for(int i = 0;  i <= widgets.length - 1; i++){
				if(widgets[i] != null && !widgets[i].isDisposed()){
					((Text)widgets[i]).setText("");;
				}
			}
		}
	}
	
	/**
	 * @param container
	 * @return
	 */
	private Control addBasicAuthenticationComposite(Composite container) {
		Composite basicAuthComposite = new Composite(container, SWT.BORDER);
		basicAuthComposite.setLayout(new GridLayout(2, false));
		basicAuthComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		String label1Text = null;
		String label2Text = null;
		int textStyle = 0;
		if(StringUtils.equalsIgnoreCase(protocolText, "AWS S3 HTTPS")){
			label1Text = "Access Key ID";
			label2Text = "Secret Access Key";
			textStyle = SWT.BORDER;
		}else{
			label1Text = "User ID";
			label2Text = "Password";
			textStyle = SWT.PASSWORD | SWT.BORDER;
		}
		
		FTPWidgetUtility ftpWidgetUtility = new FTPWidgetUtility();
		Label label1 = (Label) ftpWidgetUtility.createLabel(basicAuthComposite, label1Text);
		setPropertyHelpText(label1, "Used to provide the value for authentication");
		text1 = (Text) ftpWidgetUtility.createText(basicAuthComposite, "", SWT.BORDER);
		Utils.INSTANCE.addMouseMoveListener(text1, cursor);	
		
		Label label2 = (Label) ftpWidgetUtility.createLabel(basicAuthComposite, label2Text);
		setPropertyHelpText(label2, "Used to provide the value for authentication");
		text2 = (Text) ftpWidgetUtility.createText(basicAuthComposite, "", textStyle);
		Utils.INSTANCE.addMouseMoveListener(text2, cursor);	
		
		ControlDecoration text1ControlDecoration = WidgetUtility.addDecorator(text1,Messages.EMPTYFIELDMESSAGE);
		ControlDecoration text2ControlDecoration = WidgetUtility.addDecorator(text2,Messages.EMPTYFIELDMESSAGE);
		
		FTPWidgetUtility widgetUtility = new FTPWidgetUtility();
		widgetUtility.validateWidgetText(text1, propertyDialogButtonBar, cursor, text1ControlDecoration);
		widgetUtility.validateEmptyWidgetText(text2, propertyDialogButtonBar, cursor, text2ControlDecoration);
		
		addModifyListener(text1);
		addModifyListener(text2);
		
		return basicAuthComposite;
	}
	
	/**
	 * @param container
	 * @return
	 */
	private Control addIdKeyComposite(Composite container) {
		Composite keyFileComposite = new Composite(container, SWT.BORDER);
		keyFileComposite.setLayout(new GridLayout(3, false));
		keyFileComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		FTPWidgetUtility ftpWidgetUtility = new FTPWidgetUtility();
		if(!StringUtils.equalsIgnoreCase(protocolText, "AWS S3 HTTPS")){
			Label userIdLbl = (Label) ftpWidgetUtility.createLabel(keyFileComposite, "User ID");
			text1 = (Text) ftpWidgetUtility.createText(keyFileComposite, "", SWT.BORDER);
			new Button(keyFileComposite, SWT.NONE).setVisible(false);
		}
		
		String label2Text = null;
		if(StringUtils.equalsIgnoreCase(protocolText, "AWS S3 HTTPS")){
			label2Text = "Porperty File";
		}else{
			label2Text = "Private Key";
		}
		
		Label privateKeyLbl = (Label) ftpWidgetUtility.createLabel(keyFileComposite, label2Text);
		setPropertyHelpText(privateKeyLbl, "Used to provide the value for authentication");
		privateKeyLbl.setCursor(new Cursor(privateKeyLbl.getDisplay(), SWT.CURSOR_HELP));
		text2 = (Text) ftpWidgetUtility.createText(keyFileComposite, "", SWT.BORDER);
		Utils.INSTANCE.addMouseMoveListener(text2, cursor);
		Button keyFileBrwsBtn = new Button(keyFileComposite, SWT.NONE);
		keyFileBrwsBtn.setText("...");
		
		selectionListener(keyFileBrwsBtn, text2);
		
		ControlDecoration text2ControlDecoration = WidgetUtility.addDecorator(text2,Messages.EMPTYFIELDMESSAGE);
		
		FTPWidgetUtility widgetUtility = new FTPWidgetUtility();
		if(text1 != null && !text1.isDisposed()){
			ControlDecoration text1ControlDecoration = WidgetUtility.addDecorator(text1,Messages.EMPTYFIELDMESSAGE);
			widgetUtility.validateWidgetText(text1, propertyDialogButtonBar, cursor, text1ControlDecoration);
		}
		widgetUtility.validateEmptyWidgetText(text2, propertyDialogButtonBar, cursor, text2ControlDecoration);
		
		if(text1!=null){
			addModifyListener(text1);
		}
		addModifyListener(text2);
		
		return keyFileComposite;
	}
	
	private void selectionListener(Button button, Text txt){
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog filedialog = new FileDialog(button.getShell(), SWT.None);
				txt.setText(filedialog.open());
			}
		});
	}
	
	/**
	 * @return param value map
	 */
	public Map<String, FTPAuthOperationDetails> getAdditionalParameterDetails() {
		return authOperationSelectionMap;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(450, 276);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	
	@Override
	protected void okPressed() {
		FTPAuthOperationDetails authOperationDetails;
		String text1Value = "";
		String text2Value = "";
		if(text1 != null && !text1.isDisposed()){
			text1Value = text1.getText();
		}
		if(text2 != null && !text2.isDisposed()){
			text2Value = text2.getText();
		}
		authOperationSelectionMap = new LinkedHashMap<String, FTPAuthOperationDetails>();
		if(authenticationModeCombo.getSelectionIndex() ==1){
			if(StringUtils.equalsIgnoreCase(protocolText, "AWS S3 HTTPS")){
				authOperationDetails = new FTPAuthOperationDetails(null, 
						text2Value, null, null, null, protocolText);
			}else{
				authOperationDetails = new FTPAuthOperationDetails(text1Value, 
						text2Value, null, null, null, protocolText);
			}
		}else{
			authOperationDetails = new FTPAuthOperationDetails(text1Value, 
					text2Value, null, null, null, protocolText);
		}
		
		authOperationSelectionMap.put(authenticationModeCombo.getText(), authOperationDetails);
		
		super.okPressed();
	}
	
	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}
	
	
	private void refreshUI(){
		this.getShell().setSize(this.getShell().getSize().x-1, this.getShell().getSize().y-1);
		this.getShell().setSize(this.getShell().getSize().x+1, this.getShell().getSize().y+1);
	}
	

}
