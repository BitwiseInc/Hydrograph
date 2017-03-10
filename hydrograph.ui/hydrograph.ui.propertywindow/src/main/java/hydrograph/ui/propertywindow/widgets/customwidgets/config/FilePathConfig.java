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

package hydrograph.ui.propertywindow.widgets.customwidgets.config;

import java.util.ArrayList;
import java.util.List;

import hydrograph.ui.propertywindow.factory.ListenerFactory.Listners;

/**
 *
 * This class is used to configure file-path widget
 * 
 * @author Bitwise
 *
 */
public class FilePathConfig implements WidgetConfig {
	private String label;
	private List<Listners> listeners = new ArrayList<>();
	private boolean isMandatory;
	private String[] fileExtension;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Listners> getListeners() {
		return listeners;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	public String[] getfileExtension(){
		return fileExtension;
	}
	
	public void setfileExtension(String[] fileExtension){
		this.fileExtension=fileExtension;
	}
}
