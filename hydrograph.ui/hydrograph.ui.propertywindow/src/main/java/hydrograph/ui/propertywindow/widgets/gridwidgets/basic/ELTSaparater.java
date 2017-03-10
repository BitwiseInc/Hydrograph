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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 28, 2015
 * 
 */

public class ELTSaparater extends AbstractELTWidget{

	private Label label;
	private boolean visible = false;
	private int lableWidth=75;
	private boolean grabExcessSpace = true;
	
	@Override
	public void attachWidget(Composite container) {
		// TODO Auto-generated method stub
		label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_defaultELTLable = new GridData(SWT.LEFT, SWT.CENTER, grabExcessSpace, false, 1, 1);
		gd_defaultELTLable.widthHint = lableWidth;
		label.setLayoutData(gd_defaultELTLable);	
		label.setVisible(visible);
		
		widget = label;
	}

	/**
	 * Lable width.
	 * 
	 * @param lableWidth
	 *            the lable width
	 * @return the ELT saparater
	 */
	public ELTSaparater lableWidth(int lableWidth){
		this.lableWidth = lableWidth;
		return this;
	}
	
	/**
	 * Grab excess horizontal space.
	 * 
	 * @param grabExcessSpace
	 *            the grab excess space
	 * @return the ELT saparater
	 */
	public ELTSaparater grabExcessHorizontalSpace(boolean grabExcessSpace){
		this.grabExcessSpace = grabExcessSpace;
		return this;
	}
	
	/**
	 * Visibility.
	 * 
	 * @param visible
	 *            the visible
	 * @return the ELT saparater
	 */
	public ELTSaparater visibility(boolean visible){ 
		this.visible =visible;
		return this;
	}
}
