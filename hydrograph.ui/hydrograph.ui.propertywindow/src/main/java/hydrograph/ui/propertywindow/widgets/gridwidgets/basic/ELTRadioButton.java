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

 
package hydrograph.ui.propertywindow.widgets.gridwidgets.basic;

import hydrograph.ui.common.util.OSValidator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

// TODO: Auto-generated Javadoc
/**
 * The Class ELTRadioButton.
 * 
 * @author Bitwise
 */
public class ELTRadioButton extends AbstractELTWidget{

	private Button defaultELTButton;
	private String buttonText="Button";
	private boolean grabExcessSpace=false;
	
	/**
	 * Instantiates a new ELT radio button.
	 * 
	 * @param buttonText
	 *            the button text
	 */
	public ELTRadioButton(String buttonText){
		this.buttonText = buttonText;
	}
	
	@Override
	public void attachWidget(Composite container) {
		defaultELTButton = new Button(container, SWT.RADIO);
		GridData gd_defaultELTButton = new GridData(SWT.FILL, SWT.FILL, grabExcessSpace, false, 1, 1);
		if (OSValidator.isMac()) {
			gd_defaultELTButton.horizontalIndent=6;
		}
		defaultELTButton.setLayoutData(gd_defaultELTButton);
		defaultELTButton.setText(buttonText);
		
		widget = defaultELTButton;
	}
	
	/**
	 * Grab excess horizontal space.
	 * 
	 * @param grabExcessSpace
	 *            the grab excess space
	 * @return the ELT radio button
	 */
	public ELTRadioButton grabExcessHorizontalSpace(boolean grabExcessSpace){
		this.grabExcessSpace = grabExcessSpace;
		return this;
	}
	
	/**
	 * Visible.
	 * 
	 * @param visiblity
	 *            the visiblity
	 */
	public void visible(boolean visiblity){
		defaultELTButton.setVisible(visiblity);
	}

}
