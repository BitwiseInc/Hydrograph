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

 
package hydrograph.ui.propertywindow.widgets.listeners;

import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * 
 * @author Bitwise
 * Sep 24, 2015
 * 
 */

public class ListenerHelper {
	
	/**
	 * The Enum HelperType.
	 * 
	 * @author Bitwise
	 */
	public enum HelperType{
		CONTROL_DECORATION,
		VALIDATION_STATUS,
		SCHEMA_GRID,
		TOOLTIP_ERROR_MESSAGE,
		CURRENT_COMPONENT,
		WIDGET_CONFIG,
		MINIMUM_PORT_COUNT,
		OPERATION_CLASS_DIALOG_OK_CONTROL, 
		OPERATION_CLASS_DIALOG_APPLY_BUTTON, 
		CHARACTER_LIMIT,
		COMPONENT_TYPE,
		PROPERTY_DIALOG_BUTTON_BAR,
		COMPONENT,
		FILE_EXTENSION;
	}
	
	private Map<HelperType, Object> helpers;
	
	/**
	 * Instantiates a new listener helper.
	 */
	public ListenerHelper() {
		this.helpers = new HashMap<>();
	}
	
	/**
	 * Put.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void put(HelperType key, Object value){
		helpers.put(key, value);
	}
	
	/**
	 * Gets the.
	 * 
	 * @param key
	 *            the key
	 * @return the object
	 */
	public Object get(HelperType key){
		return helpers.get(key);
	}
	
	
	//TODO : remove this code once all of its references are remove
	String type;
	Object object;
	
	/**
	 * Instantiates a new listener helper.
	 * 
	 * @param type
	 *            the type
	 * @param object
	 *            the object
	 */
	public ListenerHelper(String type, Object object) {
		super();
		this.type = type;
		this.object = object;
	}
	public String getType() {
		return type;
	}
	public Object getObject() {
		return object;
	}
}
