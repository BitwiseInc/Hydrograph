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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 18, 2015
 * 
 */

public class ELTDefaultTextBox extends AbstractELTWidget{
	
	private Text defaultELTTextBox;
	private int textboxWidth=100;
	private String defaultTextMessage = "";
	private boolean grabExcessSpace = true;
		

	/**
	 * Instantiates a new ELT default text box.
	 */
	public ELTDefaultTextBox(){}
		
	@Override
	public void attachWidget(Composite container) {
		defaultELTTextBox = new Text(container, SWT.BORDER);
		GridData gd_defaultELTTextBox = new GridData(SWT.FILL, SWT.CENTER, grabExcessSpace, false, 1, 1);
		if (OSValidator.isMac()) {
			gd_defaultELTTextBox.horizontalIndent=2;
		}
		else{
			gd_defaultELTTextBox.horizontalIndent=1;
		}
		
		
		
		gd_defaultELTTextBox.widthHint = textboxWidth;
		defaultELTTextBox.setLayoutData(gd_defaultELTTextBox);
		defaultELTTextBox.setText(defaultTextMessage);
		
		widget = defaultELTTextBox;
	}
	
	/**
	 * Text box width.
	 * 
	 * @param textboxWidth
	 *            the textbox width
	 * @return the ELT default text box
	 */
	public ELTDefaultTextBox textBoxWidth(int textboxWidth){
		this.textboxWidth = textboxWidth;
		return this;
	}
	
	/**
	 * Default text.
	 * 
	 * @param defaultTextMessage
	 *            the default text message
	 * @return the ELT default text box
	 */
	public ELTDefaultTextBox defaultText(String defaultTextMessage){
		this.defaultTextMessage = defaultTextMessage;
		return this;
	}
	
	/**
	 * Grab excess horizontal space.
	 * 
	 * @param grabExcessSpace
	 *            the grab excess space
	 * @return the ELT default text box
	 */
	public ELTDefaultTextBox grabExcessHorizontalSpace(boolean grabExcessSpace){
		this.grabExcessSpace = grabExcessSpace;
		return this;
	}
	
	/**
	 * Visibility.
	 * 
	 * @param visible
	 *            the visible
	 * @return the ELT default text box
	 */
	public ELTDefaultTextBox visibility(boolean visible){ 
		defaultELTTextBox.setVisible(visible);
		return this;
	}
	
	/**
	 * Sets the enabled.
	 * 
	 * @param enabled
	 *            the enabled
	 * @return the ELT default text box
	 */
	public ELTDefaultTextBox setEnabled(boolean enabled){ 
		defaultELTTextBox.setEnabled(enabled);
		return this;
	}
	
}
