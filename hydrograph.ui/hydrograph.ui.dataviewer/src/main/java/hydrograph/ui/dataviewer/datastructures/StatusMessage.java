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

package hydrograph.ui.dataviewer.datastructures;

import org.eclipse.core.runtime.Status;

/**
 * The Class StatusMessage.
 * Provides the Status message to be displayed in status bar of the View Data Windows.
 * 
 * @author Bitwise
 *
 */
public class StatusMessage {
	private int returnCode;
	private String returnMessage="";
	private Status errorStatus;
	
	public static class Builder{
		private final int returnCode;
		private final String returnMessage;
		private Status errorStatus;
	
	public Builder(int returnCode,String returnMessage){
		this.returnCode = returnCode;
		this.returnMessage = returnMessage;
	}
	
	public Builder errorStatus(Status val){
		errorStatus = val;
		return this;
	}
	public StatusMessage build(){
		return new StatusMessage(this);
	}
	}
	private StatusMessage(Builder builder){
		returnCode = builder.returnCode;
		returnMessage = builder.returnMessage;
		errorStatus = builder.errorStatus;
	}
	public StatusMessage(int returnCode, String returnMessage) {
		this.returnCode = returnCode;
		this.returnMessage = returnMessage;
	}

	public StatusMessage(int returnCode) {
		this.returnCode = returnCode;
	}

	public Status getErrorStatus(){
		return errorStatus;
	}
	
	
	
	
	/**
	 * 
	 * Get return code
	 * 
	 * @return return code
	 */
	public int getReturnCode() {
		return returnCode;
	}

	/**
	 * 
	 * Get status Message
	 * 
	 * @return status message 
	 */
	public String getStatusMessage() {
		return returnMessage;
	}

	@Override
	public String toString() {
		return "StatusMessage [returnCode=" + returnCode + ", returnMessage=" + returnMessage + "]";
	}
}
