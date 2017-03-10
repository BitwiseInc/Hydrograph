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

import hydrograph.ui.propertywindow.utils.SWTResourceManager;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.widgets.ColumnLayout;


// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 18, 2015
 * 
 */

public class ELTDefaultSubgroup extends AbstractELTContainerWidget{
	private Group subGroup;
	private String subgroupName="Default Sub Group";
	
	/**
	 * Instantiates a new ELT default subgroup.
	 * 
	 * @param container
	 *            the container
	 */
	public ELTDefaultSubgroup(Composite container) {
		super(container);
	}

	@Override
	public void createContainerWidget(){
		createGroupWidget();
		subGroup.setLayout(getGroupWidgetLayout());
		super.outputContainer = subGroup;
	}

	private ColumnLayout getGroupWidgetLayout() {
		ColumnLayout subGroupLayout = new ColumnLayout();
		subGroupLayout.maxNumColumns = 1;
		subGroupLayout.bottomMargin = 0;
		subGroupLayout.topMargin = 20;
		subGroupLayout.rightMargin = 0;
		return subGroupLayout;
	}

	private void createGroupWidget() {
		subGroup = new Group(inputContainer, SWT.NONE);
		subGroup.setText(subgroupName);
		subGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
	}
	
	@Override
	public ELTDefaultSubgroup numberOfBasicWidgets(int subWidgetCount){
		return this;
	}
	
	/**
	 * Sub group name.
	 * 
	 * @param subgroupName
	 *            the subgroup name
	 * @return the ELT default subgroup
	 */
	public ELTDefaultSubgroup subGroupName(String subgroupName){
		this.subgroupName = subgroupName;
		return this;
	}

	@Override
	public void attachWidget(AbstractELTWidget eltWidget) {
		eltWidget.attachWidget(subGroup);	
	}

	
}
