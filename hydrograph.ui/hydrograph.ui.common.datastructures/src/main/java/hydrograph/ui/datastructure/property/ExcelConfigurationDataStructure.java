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

import java.util.LinkedHashMap;
import java.util.Map;

import hydrograph.ui.common.cloneableinterface.IDataStructure;
import hydrograph.ui.common.datastructures.messages.Messages;

/**
 * ExcelConfigurationDataStructure for Excel Component
 * @author Bitwise
 *
 */
public class ExcelConfigurationDataStructure  implements IDataStructure{
	
	private String fieldName;
	private Map<String,String> headerMap =  new LinkedHashMap<String, String>();
	private Map<String,String> dataMap =  new LinkedHashMap<String, String>();
	
	public ExcelConfigurationDataStructure() {
		initializeHeaderMap();
		initializeDataMap();
	}
	
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Map<String, String> getHeaderMap() {
		return headerMap;
	}

	public void setHeaderMap(Map<String, String> headerMap) {
		this.headerMap = headerMap;
	}

	public Map<String, String> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, String> dataMap) {
		this.dataMap = dataMap;
	}

	private void initializeDataMap() {
		String[] excelElements = Messages.EXCEL_FORMATS.split("\\|");
		dataMap.put(excelElements[0], "THIN"); //Border Style
		dataMap.put(excelElements[1], "All"); //Border Range
		dataMap.put(excelElements[2], "Center"); //Horizontal Alignment
		dataMap.put(excelElements[3], "Center"); //Vertical Alignment
		dataMap.put(excelElements[4], "1|Arial Unicode MS|20.25|1|WINDOWS|1|-27|0|0|0|700|0|0|0|0|3|2|1|34|Arial Unicode MS|#000000"); //Font
		dataMap.put(excelElements[5], "#ffffff"); //Cell Background Color 
		dataMap.put(excelElements[6], "#000000"); //Border Color 
		
	}

	private void initializeHeaderMap() {
		String[] excelElements = Messages.EXCEL_FORMATS.split("\\|");
		headerMap.put(excelElements[0], "THIN"); //Border Style
		headerMap.put(excelElements[1], "All"); //Border Range
		headerMap.put(excelElements[2], "Center"); //Horizontal Alignment
		headerMap.put(excelElements[3], "Center"); //Vertical Alignment
		headerMap.put(excelElements[4], "1|Arial Unicode MS|20.25|1|WINDOWS|1|-27|0|0|0|700|0|0|0|0|3|2|1|34|Arial Unicode MS|#000000"); //Font
		headerMap.put(excelElements[5], "#ffffff"); //Cell Background Color 
		headerMap.put(excelElements[6], "#000000"); //Border Color 
	}

	@Override
	public Object clone() {
		ExcelConfigurationDataStructure excelConfigurationDataStructure = new ExcelConfigurationDataStructure();
		excelConfigurationDataStructure.setFieldName(this.fieldName);
		excelConfigurationDataStructure.setDataMap(getDataMap());
		excelConfigurationDataStructure.setHeaderMap(getHeaderMap());
		return excelConfigurationDataStructure;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" Excel Format [Field =" + getFieldName() + ", Header Format =" + getHeaderMap().toString()+" ,Data Format = "+getDataMap().toString());
		builder.append("]");
		return builder.toString();
	}
}

