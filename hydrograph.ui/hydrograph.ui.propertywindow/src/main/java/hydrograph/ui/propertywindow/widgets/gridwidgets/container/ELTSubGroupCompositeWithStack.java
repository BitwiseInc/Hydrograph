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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
/**
 * Creates composite having stack layout for widgets
 * @author Bitwise
 *
 */
public class ELTSubGroupCompositeWithStack extends AbstractELTContainerWidget {
	
	Composite composite;

	public ELTSubGroupCompositeWithStack(Composite container) {
		super(container);
	}

	@Override
	public void createContainerWidget() {
		composite = new Composite(inputContainer, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 40;
		composite.setLayout(layout);
		super.outputContainer = composite;		   	
		
	}
	/**
	 * Create composite with stack layout
	 * @param layout
	 */
	public void createStackContainerWidget(StackLayout layout){
		
		composite = new Composite(inputContainer, SWT.NONE);
		composite.setLayout(layout);
		super.outputContainer = composite;	
		
	}

	@Override
	public AbstractELTContainerWidget numberOfBasicWidgets(int subWidgetCount) {
		composite.setLayout(new GridLayout(subWidgetCount, false));
		return this;
	}

	@Override
	public void attachWidget(AbstractELTWidget eltWidget) {
		eltWidget.attachWidget(composite);
		
	}

}
