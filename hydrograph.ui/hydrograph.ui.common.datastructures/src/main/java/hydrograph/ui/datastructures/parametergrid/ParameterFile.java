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
 
package hydrograph.ui.datastructures.parametergrid;

import hydrograph.ui.common.cloneableinterface.IDataStructure;
import hydrograph.ui.datastructures.parametergrid.filetype.ParamterFileTypes;

import java.io.Serializable;


/**
 * 
 * The class to hold parameter file with its metadata
 * 
 * @author Bitwise
 *
 */
public class ParameterFile  implements Serializable,IDataStructure{
	private static final long serialVersionUID = 5403262912433893757L;
	private String fileName;
	private ParamterFileTypes fileType;
		
	public ParameterFile(String fileName, ParamterFileTypes fileType) {
		this.fileName = fileName;
		this.fileType = fileType;
	}

	/**
	 * 
	 * Returns string to show in parameter file path grid
	 * 
	 * @return String
	 */
	public String getFilePathViewString(){
		return this.fileName + " - " + fileType.toString();
	}
	
	/**
	 * 
	 * Returns name of parameter file
	 * 
	 * @return String
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * 
	 * Set name of parameter file
	 * 
	 * @param String
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ParamterFileTypes getFileType() {
		return fileType;
	}

	public void setFileType(ParamterFileTypes fileType) {
		this.fileType = fileType;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((fileType == null) ? 0 : fileType.hashCode());
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
		ParameterFile other = (ParameterFile) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (fileType != other.fileType)
			return false;
		return true;
	}

	@Override
	public Object clone() {
		ParameterFile filePath = new ParameterFile(this.fileName,this.fileType);
		return filePath;
	}

	@Override
	public String toString() {
		return fileName + " - " + fileType;
	}
	
	
	
}
