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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

// TODO: Auto-generated Javadoc
/**
 * The Class ELTDefaultCombo.
 * 
 * @author Bitwise
 */
public class ELTDefaultCombo extends AbstractELTWidget {
	private Combo defaultELTcom;
	private int textboxWidth = 65;
	private String[] defaultTextMessage;

	@Override
	public void attachWidget(Composite container) {
		defaultELTcom = new Combo(container, SWT.READ_ONLY);
		defaultELTcom.setItems(defaultTextMessage);
		// defaultELTcom.setItems(new String[] {"True","false"});
		// defaultELTcom.setItem(0, "");
		GridData gd_defaultELTTextBox = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		
		if (OSValidator.isMac()) {
			gd_defaultELTTextBox.horizontalIndent=-1;
			gd_defaultELTTextBox.widthHint = textboxWidth+50;
		}
		else{
			gd_defaultELTTextBox.horizontalIndent=1;
			gd_defaultELTTextBox.widthHint = textboxWidth;
		}
			
		defaultELTcom.setLayoutData(gd_defaultELTTextBox);

		widget = defaultELTcom;
	}

	/**
	 * Default text.
	 * 
	 * @param defaultTextMessage
	 *            the default text message
	 * @return the ELT default combo
	 */
	public ELTDefaultCombo defaultText(String[] defaultTextMessage) {
		this.defaultTextMessage = defaultTextMessage;
		return this;
	}

	/**
	 * Grab excess horizontal space.
	 * 
	 * @param grabExcessSpace
	 *            the grab excess space
	 * @return the ELT default combo
	 */
	public ELTDefaultCombo grabExcessHorizontalSpace(boolean grabExcessSpace) {
		return this;
	}

	/**
	 * Combo box width.
	 * 
	 * @param textboxWidth
	 *            the textbox width
	 * @return the ELT default combo
	 */
	public ELTDefaultCombo comboBoxWidth(int textboxWidth) {
		this.textboxWidth = textboxWidth;
		return this;
	}
}
