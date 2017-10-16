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
package hydrograph.ui.datastructure.property;

import java.util.ArrayList;
import java.util.List;

import hydrograph.ui.common.cloneableinterface.IDataStructure;

/**
 * ExcelFormattingDataStructure for Excel Component
 * @author Bitwise
 *
 */
public class ExcelFormattingDataStructure implements IDataStructure{
	
	private String copyOfField;
	private List<String> copyFieldList = new ArrayList<String>();
	private List<ExcelConfigurationDataStructure> listOfExcelConfiguration = new ArrayList<ExcelConfigurationDataStructure>();
	
	public List<String> getCopyFieldList() {
		return copyFieldList;
	}

	public void setCopyFieldList(List<String> copyFieldList) {
		this.copyFieldList = copyFieldList;
	}

	public String getCopyOfField() {
		return copyOfField;
	}

	public void setCopyOfField(String copyOfField) {
		this.copyOfField = copyOfField;
	}

	public List<ExcelConfigurationDataStructure> getListOfExcelConfiguration() {
		return listOfExcelConfiguration;
	}

	public void setListOfExcelConfiguration(List<ExcelConfigurationDataStructure> listOfExcelConfiguration) {
		this.listOfExcelConfiguration = listOfExcelConfiguration;
	}

	@Override
	public Object clone() {
		ExcelFormattingDataStructure excelFormattingDataStructure = new ExcelFormattingDataStructure();
		excelFormattingDataStructure.setCopyOfField(this.copyOfField);
		excelFormattingDataStructure.setCopyFieldList(new ArrayList<>(getCopyFieldList()));
		excelFormattingDataStructure.setListOfExcelConfiguration(new ArrayList<>(getListOfExcelConfiguration()));
		return excelFormattingDataStructure;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" Excel Format [Copy Of Field =" + getCopyOfField() + ", List Of Copy Fields = "+ getCopyFieldList()+ ", List of Format  =" + getListOfExcelConfiguration().toString());
		builder.append("]");
		return builder.toString();
	}
}
