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

 
package hydrograph.ui.propertywindow.runconfig;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * This class is specially designed to handle notifications in RunConfigDialog
 * 
 * @author Bitwise
 *
 */
public class Notification {
	private List<String> errors = new ArrayList<>();

	public void addError(String message) {
		errors.add(message);
	}

	/**
	 * 
	 * Returns true if there are notifications added to the object
	 * 
	 * @return boolean
	 */
	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	/**
	 * 
	 * Returns combined message for all added notifications
	 * 
	 * @return String - error message
	 */
	public String errorMessage() {
		return StringUtils.join(errors, ",\n");
	}

	@Override
	public String toString() {
		return "Notification [errors=" + errors + "]";
	}
	
	
}