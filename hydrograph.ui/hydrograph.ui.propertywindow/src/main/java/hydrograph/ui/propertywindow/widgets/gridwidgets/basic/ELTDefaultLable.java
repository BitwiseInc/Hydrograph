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
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import hydrograph.ui.common.util.OSValidator;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise Sep 18, 2015
 * 
 */

public class ELTDefaultLable extends AbstractELTWidget {

	Label defaultELTLable;

	private int lableWidth = 90;
	private String lableText = "Lable";

	/**
	 * Instantiates a new ELT default lable.
	 * 
	 * @param lableText
	 *            the lable text
	 */
	public ELTDefaultLable(String lableText) {
		this.lableText = lableText;
	}

	public void setImage(String img) {
		defaultELTLable.setImage(new Image(null, img));
	}
	
	public void setToolTipText(String tooltip)
	{
		defaultELTLable.setToolTipText(tooltip);
	}

	public void addMouseUpListener(MouseListener listener) {
		defaultELTLable.addMouseListener(listener);
	}

	@Override
	public void attachWidget(Composite container) {
		defaultELTLable = new Label(container, SWT.NONE);
		GridData gd_defaultELTLable = new GridData(SWT.FILL, SWT.FILL, false,
				false, 1, 1);
		if(OSValidator.isMac()){
			gd_defaultELTLable.widthHint = lableWidth+4;
		}else{
			gd_defaultELTLable.widthHint = lableWidth;
		}
		defaultELTLable.setLayoutData(gd_defaultELTLable);
		defaultELTLable.setText(lableText);

		widget = defaultELTLable;
	}

	/**
	 * Lable width.
	 * 
	 * @param lableWidth
	 *            the lable width
	 * @return the ELT default lable
	 */
	public ELTDefaultLable lableWidth(int lableWidth) {
		this.lableWidth = lableWidth;
		return this;
	}

	public void setEnabled(Boolean value) {
		defaultELTLable.setEnabled(value);
	}

}
