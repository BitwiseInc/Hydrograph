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

import hydrograph.ui.propertywindow.factory.ListenerFactory.Listners;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class DropDownConfig implements WidgetConfig {
	private String name;
	private List<String> items = new LinkedList<>();
	private List<Listners> textBoxListeners = new ArrayList<>();
	private List<Listners> dropDownListeners = new ArrayList<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getItems() {
		return items;
	}
	
	public void setItems(List<String> items) {
		this.items = items;
	}
	public List<Listners> getTextBoxListeners() {
		return textBoxListeners;
	}
	public void setTextBoxListeners(List<Listners> textBoxListeners) {
		this.textBoxListeners = textBoxListeners;
	}
	public List<Listners> getDropDownListeners() {
		return dropDownListeners;
	}
	public void setDropDownListeners(List<Listners> dropDownListeners) {
		this.dropDownListeners = dropDownListeners;
	}
}
