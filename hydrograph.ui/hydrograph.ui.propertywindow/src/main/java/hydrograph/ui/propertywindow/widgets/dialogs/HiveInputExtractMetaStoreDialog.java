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
package hydrograph.ui.propertywindow.widgets.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import hydrograph.ui.common.util.CustomColorRegistry;
/**
 * HiveInputExtractMetaStoreDialog used for taking users credentials
 * @author Bitwise
 *
 */
public class HiveInputExtractMetaStoreDialog extends Dialog {
	private static final String CREDENTIAL_BLANK_ERROR = "UserName/Password can't be blank";
	private List<String> userCredentials;
	private Label lblPropertyError;
	private Text username;
	private Text password;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public HiveInputExtractMetaStoreDialog(Shell parentShell) {
		super(parentShell);
		userCredentials= new ArrayList<>();
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		getShell().setText("Credentials Required");
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.getLayout();
	
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Label lblUserName = new Label(composite, SWT.NONE);
		lblUserName.setText("User Name");
		new Label(composite, SWT.NONE);
		
		username = new Text(composite, SWT.BORDER);
		username.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		
		Label lblPassword = new Label(composite, SWT.NONE);
		lblPassword.setText("Password");
		new Label(composite, SWT.NONE);
		
		password = new Text(composite, SWT.PASSWORD|SWT.BORDER);
		password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		createErrorComposite(container);

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		
		
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * @param parent
	 */
	private void createErrorComposite(Composite parent) {
		GridLayout gl_container = new GridLayout(1, false);
		gl_container.marginHeight = 2;
		gl_container.verticalSpacing = 2;
		parent.setLayout(gl_container);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite errorComposite = new Composite(parent, SWT.NONE);
		GridLayout gl_errorComposite = new GridLayout(1, false);
		gl_errorComposite.marginHeight = 0;
		errorComposite.setLayout(gl_errorComposite);
		errorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		addErrorLabel(errorComposite);
	}
	
	
	private void addErrorLabel(Composite container) {
		
		lblPropertyError = new Label(container, SWT.NONE);
		lblPropertyError.setForeground(CustomColorRegistry.INSTANCE.getColorFromRegistry( 255, 0, 0));
		lblPropertyError.setText(CREDENTIAL_BLANK_ERROR);
		lblPropertyError.setVisible(false);
		lblPropertyError.setData("Error", lblPropertyError);
		
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(350, 180);
	}
	

	public List<String> getProperties(){
				
		return userCredentials;
	}
	
   @Override
   protected void okPressed() {
	   
	   if(StringUtils.isNotBlank(username.getText())&&StringUtils.isNotBlank(password.getText())){
		   lblPropertyError.setVisible(false);
		   userCredentials.add(username.getText());
		   userCredentials.add(password.getText());
		   super.okPressed();
	   }else{
		  lblPropertyError.setVisible(true);
	   }
	   
	  
}
   /**
    * Method for cancel button
    */
   @Override
   protected void cancelPressed() {
	
	super.cancelPressed();
}

}
