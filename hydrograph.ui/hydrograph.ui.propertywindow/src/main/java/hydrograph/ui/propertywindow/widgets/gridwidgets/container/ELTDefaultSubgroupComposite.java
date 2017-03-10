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

 
package hydrograph.ui.propertywindow.widgets.gridwidgets.container;

import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


/**
 * 
 * 
 * Default composite for widgets in property window
 * 
 * @author Bitwise
 * Sep 21, 2015
 * 
 */

public class ELTDefaultSubgroupComposite extends AbstractELTContainerWidget{
	
	Composite subGroupComposite;
	
	/**
	 * Instantiates a new ELT default subgroup composite.
	 * 
	 * @param container
	 *            the container
	 */
	public ELTDefaultSubgroupComposite(Composite container) {
		super(container);
	}

	@Override
	public void createContainerWidget() {
		subGroupComposite = new Composite(inputContainer, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		subGroupComposite.setLayout(layout);
		subGroupComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		super.outputContainer = subGroupComposite;		
	}

	@Override
	public void attachWidget(AbstractELTWidget eltWidget) {
		eltWidget.attachWidget(subGroupComposite);		
	}
	
	@Override
	public ELTDefaultSubgroupComposite numberOfBasicWidgets(int subWidgetCount){
		subGroupComposite.setLayout(new GridLayout(subWidgetCount, false));
		subGroupComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		return this;
	}

}
