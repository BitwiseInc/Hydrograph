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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class ELTSchemaTableComposite extends AbstractELTContainerWidget{
	
	Composite composite;

	public ELTSchemaTableComposite(Composite container) {
		super(container);
		
	}

	@Override
	public void createContainerWidget() {
		composite = new Composite(inputContainer, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = -4;
		layout.marginWidth=0;
		layout.marginLeft = 0;
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
