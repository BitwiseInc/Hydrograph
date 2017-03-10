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

import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;

import hydrograph.ui.common.swt.customwidget.HydroGroup;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;

/**
 * 
 * Property window compatible HydroGroup widget
 * 
 * @author Bitwise
 * 
 */

public class ELTHydroSubGroup extends AbstractELTContainerWidget{
	private HydroGroup subGroup;
	private String subgroupName="Default Sub Group";
	
	/**
	 * Instantiates a new ELT default subgroup.
	 * 
	 * @param container
	 *            the container
	 */
	public ELTHydroSubGroup(Composite container) {
		super(container);
	}

	@Override
	public void createContainerWidget(){
		createGroupWidget();
		subGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		subGroup.getHydroGroupClientArea().setLayout(new GridLayout(1, false));
		super.outputContainer = subGroup.getHydroGroupClientArea();
	}

	
	private Image getImage(String subgroupName){
		Bundle bundle = Platform.getBundle("hydrograph.ui.propertywindow");
		
		URL fullPathString = null;
		if(StringUtils.equalsIgnoreCase(subgroupName, "CONFIGURATION")){
			fullPathString = BundleUtility.find(bundle, "icons/config.png");
		}else if(StringUtils.equalsIgnoreCase(subgroupName, "DISPLAY")){
			fullPathString = BundleUtility.find(bundle, "icons/display.png");
		}else if(StringUtils.equalsIgnoreCase(subgroupName, "RECORD STRUCTURE DETAILS")){
			fullPathString = BundleUtility.find(bundle, "icons/schema.png");
		}
		if(fullPathString!=null){
			Image image = ImageDescriptor.createFromURL(fullPathString).createImage();
			return image;
		}else{
			return null;
		}
	}
	
	private void createGroupWidget() {
		subGroup = new HydroGroup(inputContainer, SWT.NONE);
		subGroup.setHydroGroupText(subgroupName);
		//subGroup.setHydroGroupImage(getImage(subgroupName));
		subGroup.setHydroGroupLabelFont(new Font(null, "Arial", 8, SWT.BOLD));
	}
	
	@Override
	public ELTHydroSubGroup numberOfBasicWidgets(int subWidgetCount){
		return this;
	}
	
	/**
	 * Sub group name.
	 * 
	 * @param subgroupName
	 *            the subgroup name
	 * @return the ELT default subgroup
	 */
	public ELTHydroSubGroup subGroupName(String subgroupName){
		this.subgroupName = subgroupName;
		return this;
	}

	@Override
	public void attachWidget(AbstractELTWidget eltWidget) {
		eltWidget.attachWidget(subGroup);	
	}

	
}
