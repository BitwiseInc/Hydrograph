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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.datastructure.property.FTPAuthOperationDetails;

/**
 * @author bitwise
 *
 */
public class FTPAuthenticEditorUtility {
	private FTPAuthenticEditorUtility instance;
	private List<Object> datasetList;

	public FTPAuthenticEditorUtility() {
		instance =this;
		datasetList = new LinkedList<>();
	}
	
	/**
	 * @return instance of FTPOperationConfigUtility
	 */
	public FTPAuthenticEditorUtility getInstance() {
		return instance;
	}
	
	/**
	 * @param container
	 * @return
	 */
	public Control addBasicAuthenticationComposite(Composite container, FTPAuthOperationDetails authOperationDetails,Text text1) {
		Composite basicAuthComposite = new Composite(container, SWT.BORDER);
		basicAuthComposite.setLayout(new GridLayout(2, false));
		basicAuthComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		FTPWidgetUtility ftpWidgetUtility = new FTPWidgetUtility();
		ftpWidgetUtility.createLabel(basicAuthComposite, "User ID");
		Text userIdTextBox = (Text) ftpWidgetUtility.createText(basicAuthComposite, "", SWT.BORDER);
		text1 = userIdTextBox;
		
		ftpWidgetUtility.createLabel(basicAuthComposite, "Password");
		ftpWidgetUtility.createText(basicAuthComposite, "", SWT.PASSWORD|SWT.BORDER);
		
		return basicAuthComposite;
	}
	
	/**
	 * @param container
	 * @return
	 */
	public Control addIdKeyComposite(Composite container, FTPAuthOperationDetails authOperationDetails) {
		Composite keyFileComposite = new Composite(container, SWT.BORDER);
		keyFileComposite.setLayout(new GridLayout(3, false));
		keyFileComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		FTPWidgetUtility ftpWidgetUtility = new FTPWidgetUtility();
		ftpWidgetUtility.createLabel(keyFileComposite, "User ID");
		ftpWidgetUtility.createText(keyFileComposite, "", SWT.BORDER);
		Button keyFileBrwsBtn1 = new Button(keyFileComposite, SWT.NONE);
		keyFileBrwsBtn1.setVisible(false);
		
		ftpWidgetUtility.createLabel(keyFileComposite, "Public/Private Key");
		Text privateKeyTxt = (Text) ftpWidgetUtility.createText(keyFileComposite, "", SWT.BORDER);
		Button keyFileBrwsBtn = new Button(keyFileComposite, SWT.NONE);
		keyFileBrwsBtn.setText("...");
		
		selectionListener(keyFileBrwsBtn, privateKeyTxt);
	
		return keyFileComposite;
	}
	
	/**
	 * @param control
	 * @return
	 */
	public Control addBasicAuthKeyComposite(Composite control, FTPAuthOperationDetails authOperationDetails){
		Composite basicAuthKeyComposite = new Composite(control, SWT.BORDER);
		basicAuthKeyComposite.setLayout(new GridLayout(3, false));
		basicAuthKeyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		FTPWidgetUtility ftpWidgetUtility = new FTPWidgetUtility();
		ftpWidgetUtility.createLabel(basicAuthKeyComposite, "User ID");
		ftpWidgetUtility.createText(basicAuthKeyComposite, "", SWT.BORDER);
		new Button(basicAuthKeyComposite, SWT.NONE).setVisible(false);
		
		ftpWidgetUtility.createLabel(basicAuthKeyComposite, "Password");
		ftpWidgetUtility.createText(basicAuthKeyComposite, "", SWT.PASSWORD|SWT.BORDER);
		new Button(basicAuthKeyComposite, SWT.NONE).setVisible(false);
		
		ftpWidgetUtility.createLabel(basicAuthKeyComposite, "Public/Private Key");
		Text privateKeyTxt = (Text) ftpWidgetUtility.createText(basicAuthKeyComposite, "", SWT.BORDER);
		Button keyFileBrwsBtn = new Button(basicAuthKeyComposite, SWT.NONE);
		keyFileBrwsBtn.setText("...");
		
		selectionListener(keyFileBrwsBtn, privateKeyTxt);
		
		return basicAuthKeyComposite;
	}
	
	/**
	 * @return data field list
	 */
	public List<Object> getAuthDataList(){
		return datasetList;
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
	
}
