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

 
package hydrograph.ui.common.datastructures.tooltip;


/**
 * The Class TootlTipErrorMessage.
 * This POJO holds tooltip error messages that will be displayed for component properties.
 * 
 * @author Bitwise
 */
public class TootlTipErrorMessage {
	String errorMessage;
	
	/**
	 * Instantiates a new tootl tip error message.
	 */
	public TootlTipErrorMessage() {
		errorMessage = "";
	}

	/**
	 * Gets the error message.
	 * 
	 * @return the error message
	 */
	public String getErrorMessage(){
		return errorMessage;
	}
	
	/**
	 * Sets the error message.
	 * 
	 * @param message
	 *            the new error message
	 */
	public void setErrorMessage(String message){
		errorMessage = message;
	}
}
