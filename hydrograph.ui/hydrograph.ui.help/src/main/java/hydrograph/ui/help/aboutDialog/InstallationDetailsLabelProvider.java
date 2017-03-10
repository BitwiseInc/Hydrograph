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
package hydrograph.ui.help.aboutDialog;

import hydrograph.ui.datastructure.property.JarInformationDetails;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * The Class InstallationDetailsLabelProvider.
 * Provides column labels for the JarInformationDetails Tab.
 * 
 * @author Bitwise
 */
public class InstallationDetailsLabelProvider implements ITableLabelProvider, ITableColorProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		JarInformationDetails jarInformationDetails = (JarInformationDetails) element;
		switch (columnIndex) {
		case 0:
			return jarInformationDetails.getName();
		case 1:
			return jarInformationDetails.getVersionNo();
		case 2:
			return jarInformationDetails.getGroupId();
		case 3:
			return jarInformationDetails.getArtifactNo();
		case 4:
			return jarInformationDetails.getLicenseInfo();
		case 5:
			return jarInformationDetails.getPath();
		}
		return null;
	}

	
}

	

