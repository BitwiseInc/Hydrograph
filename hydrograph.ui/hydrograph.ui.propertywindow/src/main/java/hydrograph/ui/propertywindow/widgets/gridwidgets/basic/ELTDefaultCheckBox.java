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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

// TODO: Auto-generated Javadoc
/**
 * The Class ELTDefaultCheckBox.
 * 
 * @author Bitwise
 */
public class ELTDefaultCheckBox extends AbstractELTWidget {
	private Button defaultELTCheckBox;
	private int checkBoxWidth = 75;
	private String checkBoxLable = "Checkbox";
	private boolean grabExcessSpace = false;

	@Override
	public void attachWidget(Composite container) {
		defaultELTCheckBox = new Button(container, SWT.CHECK);
		GridData gd_defaultELTCheckBox = new GridData(SWT.FILL, SWT.CENTER,
				grabExcessSpace, false, 1, 1);
		gd_defaultELTCheckBox.widthHint = checkBoxWidth;
		defaultELTCheckBox.setLayoutData(gd_defaultELTCheckBox);

		defaultELTCheckBox.setText(checkBoxLable);
		widget = defaultELTCheckBox;
	}

	/**
	 * Instantiates a new ELT default check box.
	 * 
	 * @param checkBoxLable
	 *            the check box lable
	 */
	public ELTDefaultCheckBox(String checkBoxLable) {
		super();
		this.checkBoxLable = checkBoxLable;
	}

	/**
	 * Check box lable width.
	 * 
	 * @param width
	 *            the width
	 * @return the ELT default check box
	 */
	public ELTDefaultCheckBox checkBoxLableWidth(int width) {
		checkBoxWidth = width;
		return this;
	}

	/**
	 * Grab excess horizontal space.
	 * 
	 * @param grabExcessSpace
	 *            the grab excess space
	 * @return the ELT default check box
	 */
	public ELTDefaultCheckBox grabExcessHorizontalSpace(boolean grabExcessSpace) {
		this.grabExcessSpace = grabExcessSpace;
		return this;
	}
}
