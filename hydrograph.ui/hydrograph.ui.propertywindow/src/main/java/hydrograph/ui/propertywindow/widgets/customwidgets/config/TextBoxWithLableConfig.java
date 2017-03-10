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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hydrograph.ui.propertywindow.factory.ListenerFactory.Listners;


public class TextBoxWithLableConfig implements WidgetConfig {
	private String name;
	private int characterLimit;
	private List<Listners> listeners = new ArrayList<>();
	private boolean grabExcessSpace = false;
	private int widgetWidth=100;
	private Map<String, String> otherAttributes = new HashMap<String, String>();
	private boolean enabled = true;
	private boolean isMandatory = true;
    
    public boolean isEnabled() {
                    return enabled;
    }
    public void setEnabled(boolean enabled) {
                    this.enabled = enabled;
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Listners> getListeners() {
		return listeners;
	}
	public void setListeners(List<Listners> listeners) {
		this.listeners = listeners;
	}
	public boolean getGrabExcessSpace() {
		return grabExcessSpace;
	}
	public void setGrabExcessSpace(boolean grabExcessSpace) {
		this.grabExcessSpace = grabExcessSpace;
	}
	public int getwidgetWidth(){
		return widgetWidth;
	}
	public void setWidgetWidth(int widgetWidth){
		this.widgetWidth = widgetWidth;
	}
	public Map<String,String> getOtherAttributes() {
		return otherAttributes;
	}
	public int getCharacterLimit() {
		return characterLimit;
	}
	public void setCharacterLimit(int characterLimit) {
		this.characterLimit = characterLimit;
	}
	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	
	
}
