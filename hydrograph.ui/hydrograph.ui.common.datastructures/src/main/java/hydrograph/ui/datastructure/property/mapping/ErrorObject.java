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

 
package hydrograph.ui.datastructure.property.mapping;

import hydrograph.ui.common.cloneableinterface.IDataStructure;

/**
 * This class is a data structure to save error information used in {@link InputField}
 * 
 * @author Bitwise
 *
 */
public class ErrorObject implements IDataStructure {
	boolean hasError;
	String errorMessage;
	
	/**
	 * 
	 * @param hasError - if error exist
	 * @param errorMessage - error message
	 */
	public ErrorObject(boolean hasError, String errorMessage) {
		super();
		this.hasError = hasError;
		this.errorMessage = errorMessage;
	}

	/**
	 * 
	 * returns true if it is error
	 * 
	 * @return boolean
	 */
	public boolean isHasError() {
		return hasError;
	}

	/**
	 * set true of it is error
	 * 
	 * @param hasError
	 */
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}

	/**
	 * 
	 * returns error message
	 * 
	 * @return error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * 
	 * set error message
	 * 
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@Override
	public Object clone() {
		return new ErrorObject(hasError, errorMessage);
	}

	@Override
	public String toString() {
		return "ErrorObject [hasError=" + hasError + ", errorMessage="
				+ errorMessage + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + (hasError ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErrorObject other = (ErrorObject) obj;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (hasError != other.hasError)
			return false;
		return true;
	}
	
	
	
}
