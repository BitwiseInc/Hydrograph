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

import java.util.ArrayList;
import java.util.List;

import hydrograph.ui.common.cloneableinterface.IDataStructure;

/**
 * Data-structure to store external operation/expression/mapping data
 * 
 * @author Bitwise
 *
 */
public class ExternalWidgetData implements IDataStructure {

	private boolean isExternal;
	private String filePath;
	private List<String> errorLogs=new ArrayList<String>();

	public ExternalWidgetData(boolean isExternal,String filePath) {
		this.isExternal=isExternal;
		this.filePath=filePath;
	}
	
	public boolean isExternal() {
		return isExternal;
	}

	public void setExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public List<String> getErrorLogs() {
		return errorLogs;
	}

	public void setErrorLogs(List<String> errorLogs) {
		this.errorLogs = errorLogs;
	}

	public Object clone() {
		ExternalWidgetData data=new ExternalWidgetData(isExternal,filePath);
		data.errorLogs=new ArrayList<String>(errorLogs);
		return data;
	}

	/**
	 * Clears all data
	 * 
	 */
	public void clear() {
		filePath="";
		isExternal=false;
		errorLogs.clear();
	};
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;

		ExternalWidgetData externalWidgetData = (ExternalWidgetData) obj;
		if (this.isExternal != externalWidgetData.isExternal)
			return false;
		if (this.getFilePath() != null && !this.getFilePath().equals(externalWidgetData.getFilePath()))
			return false;
		if (externalWidgetData.getFilePath() != null && !externalWidgetData.getFilePath().equals(this.getFilePath()))
			return false;

		return true;
	}
}
