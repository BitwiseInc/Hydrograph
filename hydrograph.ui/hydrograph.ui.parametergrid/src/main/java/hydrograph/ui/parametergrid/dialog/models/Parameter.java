/********************************************************************************
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
 ******************************************************************************/
package hydrograph.ui.parametergrid.dialog.models;

/**
 * 
 * The parameter class
 * 
 * @author Bitwise
 *
 */
public class Parameter {
	private String parameterName;
	private String parameterValue;
	
	public Parameter(String parameterName, String parameterValue) {
		super();
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
	}

	/**
	 * 
	 * Get parameter name
	 * 
	 * @return String
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * 
	 * Set parameter name
	 * 
	 * @param parameterName
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	/**
	 * 
	 * Get parameter value
	 * 
	 * @return String
	 */
	public String getParameterValue() {
		return parameterValue;
	}

	/**
	 * 
	 * Set parameter value
	 * 
	 * @param parameterValue
	 */
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parameterName == null) ? 0 : parameterName.hashCode());
		result = prime * result + ((parameterValue == null) ? 0 : parameterValue.hashCode());
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
		Parameter other = (Parameter) obj;
		if (parameterName == null) {
			if (other.parameterName != null)
				return false;
		} else if (!parameterName.equals(other.parameterName))
			return false;
		if (parameterValue == null) {
			if (other.parameterValue != null)
				return false;
		} else if (!parameterValue.equals(other.parameterValue))
			return false;
		return true;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Parameter parameter = new Parameter(this.parameterName, this.parameterValue);
		return parameter;
	}

	@Override
	public String toString() {
		return  parameterName + "\n" + parameterValue ;
	}
	
}
