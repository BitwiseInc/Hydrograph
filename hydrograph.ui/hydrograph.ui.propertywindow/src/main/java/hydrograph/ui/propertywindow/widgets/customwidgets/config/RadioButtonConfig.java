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
 * Class for specifying Radio button widget configuration 
 * @author Bitwise
 */
public class RadioButtonConfig implements WidgetConfig {
	private String widgetDisplayNames[];
	private String propertyName;
	private List<Listners> radioButtonListners = new ArrayList<>();
	
	/**
	 * @return the widgetDisplayNames
	 */
	public String[] getWidgetDisplayNames() {
		return widgetDisplayNames;
	}

	/**
	 * @param widgetDisplayNames the widgetDisplayNames to set
	 */
	public void setWidgetDisplayNames(String[] widgetDisplayNames) {
		this.widgetDisplayNames = widgetDisplayNames;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public List<Listners> getRadioButtonListners() {
		return radioButtonListners;
	}

	public void setRadioButtonListners(List<Listners> radioButtonListners) {
		this.radioButtonListners = radioButtonListners;
	}

}
