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

 
package hydrograph.ui.propertywindow.propertydialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 22, 2015
 * 
 */

public class PropertyDialogButtonBar {
	private Button okButton;
	private Button applyButton;
	private Button cancelButton;
	
	
	/**
	 * Instantiates a new property dialog button bar.
	 * 
	 * @param composite
	 *            the composite
	 */
	public PropertyDialogButtonBar(Composite composite){
		okButton = new Button(composite, SWT.PUSH);
		applyButton = new Button(composite, SWT.PUSH);
		cancelButton = new Button(composite, SWT.PUSH);
	}
	
	/**
	 * Sets the property dialog button bar.
	 * 
	 * @param okButton
	 *            the ok button
	 * @param applyButton
	 *            the apply button
	 * @param cancelButton
	 *            the cancel button
	 */
	public void setPropertyDialogButtonBar(Button okButton,Button applyButton,Button cancelButton){
		disposePropertyDialogButtonBar();
		this.okButton=okButton;
		if(applyButton != null){
			this.applyButton=applyButton;
		}
		this.cancelButton=cancelButton;
	}

	private void disposePropertyDialogButtonBar() {
		this.okButton.dispose();
		this.applyButton.dispose();
		this.cancelButton.dispose();
	}

	/**
	 * Enable ok button.
	 * 
	 * @param status
	 *            the status
	 */
	public void enableOKButton(boolean status){
		okButton.setEnabled(status);
	}
	
	/**
	 * Enable apply button.
	 * 
	 * @param status
	 *            the status
	 */
	public void enableApplyButton(boolean status){
		applyButton.setEnabled(status);
	}
	
	/**
	 * Enable cancel button.
	 * 
	 * @param status
	 *            the status
	 */
	public void enableCancelButton(boolean status){
		cancelButton.setEnabled(status);
	}
	
	public boolean isApplyEnabled(){
		return applyButton.isEnabled();
	}
}
