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

import hydrograph.ui.datastructures.parametergrid.ParameterFile;

/**
 * 
 * Extended parameter class, which contains ParameterFile as new private object
 * 
 * @author Bitwise
 *
 */
public class ParameterWithFilePath extends Parameter{
	private ParameterFile parameterFile;
	
	public ParameterWithFilePath(String parameterName, String parameterValue,ParameterFile filePath) {
		super(parameterName, parameterValue);
		this.parameterFile = filePath;
	}

	/**
	 * 
	 * Get Parameter File
	 *  
	 * @return {@link ParameterFile}
	 */
	public ParameterFile getParameterFile() {
		return parameterFile;
	}

	/**
	 * Set parameter file
	 * 
	 * @param {@link ParameterFile}
	 */
	public void setParameterFile(ParameterFile parameterFile) {
		this.parameterFile = parameterFile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((parameterFile == null) ? 0 : parameterFile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj!=null){
		 if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParameterWithFilePath other = (ParameterWithFilePath) obj;
		if (parameterFile == null) {
			if (other.parameterFile != null)
				return false;
		} else if (!parameterFile.equals(other.parameterFile))
			return false;
		return true;
	}
		return false;
}
	@Override
	public String toString() {
		return super.toString();
	}	
}
