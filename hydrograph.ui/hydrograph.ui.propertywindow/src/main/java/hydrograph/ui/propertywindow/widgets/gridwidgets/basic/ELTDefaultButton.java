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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.common.util.OSValidator;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 18, 2015
 * 
 */

public class ELTDefaultButton extends AbstractELTWidget{
	private Button defaultELTButton;
	
	private String buttonText="Button";
	//private int buttonWidth=92;
	private int buttonWidth=94;
	private int buttonHeight=25;
	private boolean grabExcessSpace=false;

	
	/**
	 * Instantiates a new ELT default button.
	 * 
	 * @param buttonText
	 *            the button text
	 */
	public ELTDefaultButton(String buttonText){
		this.buttonText = buttonText;
	}
	
	@Override
	public void attachWidget(Composite container) {
		defaultELTButton = new Button(container, SWT.CENTER);
		GridData gd_defaultELTButton = new GridData(SWT.FILL, SWT.CENTER, grabExcessSpace, false, 1, 1);
		gd_defaultELTButton.widthHint = buttonWidth;
		if (OSValidator.isMac()) {
		gd_defaultELTButton.horizontalIndent=-3;
		}
		gd_defaultELTButton.heightHint = buttonHeight;
		defaultELTButton.setLayoutData(gd_defaultELTButton);
		defaultELTButton.setText(buttonText);
		
		widget = defaultELTButton;
	}
		
	/**
	 * Button width.
	 * 
	 * @param buttonWidth
	 *            the button width
	 * @return the ELT default button
	 */
	public ELTDefaultButton buttonWidth(int buttonWidth){
		this.buttonWidth = buttonWidth;
		return this;
	}
	
	public ELTDefaultButton buttonHeight(int buttonHeight){
		this.buttonHeight = buttonHeight;
		return this;
	}
	/**
	 * Grab excess horizontal space.
	 * 
	 * @param grabExcessSpace
	 *            the grab excess space
	 * @return the ELT default button
	 */
	public ELTDefaultButton grabExcessHorizontalSpace(boolean grabExcessSpace){
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

	public void setImage(ImagePathConstant img) {
		defaultELTButton.setImage(img.getImageFromRegistry());
		
	}

	public void setToolTipText(String tooltip)
	{
		defaultELTButton.setToolTipText(tooltip);
	}
	
	public void setEnabled(Boolean value) {
		defaultELTButton.setEnabled(value);
	}
}
