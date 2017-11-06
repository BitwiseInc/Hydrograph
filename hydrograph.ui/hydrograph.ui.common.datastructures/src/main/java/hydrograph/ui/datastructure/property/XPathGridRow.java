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

/**
 * The Class XPathGridRow.
 * 
 * XPathGridRow is the data-structure used for storing schema of below components
 * 
 * Input Components 	: IXml 
 * Output Components 	: OXml  
 * 
 * @author Bitwise
 */
public class XPathGridRow extends GridRow {
	private String xPath;
	private String absolutexPath;
	
	
	/**
	 * @return the absolutexPath
	 */
	public String getAbsolutexPath() {
		return absolutexPath;
	}


	/**
	 * @param absolutexPath the absolutexPath to set
	 */
	public void setAbsolutexPath(String absolutexPath) {
		this.absolutexPath = absolutexPath;
	}


	/**
	 * @return the xpath
	 */
	public String getXPath() {
		return xPath;
	}


	/**
	 * @param xpath the xpath to set
	 */
	public void setXPath(String xpath) {
		this.xPath = xpath;
	}


	public XPathGridRow copy() {
		XPathGridRow schemaGrid = new XPathGridRow();
		schemaGrid.setDataType(getDataType());
		schemaGrid.setDateFormat(getDateFormat());
		schemaGrid.setFieldName(getFieldName());
		schemaGrid.setScale(getScale());
		schemaGrid.setDataTypeValue(getDataTypeValue());
		schemaGrid.setScaleType(getScaleType());
		schemaGrid.setScaleTypeValue(getScaleTypeValue());
		schemaGrid.setPrecision(getPrecision());
		schemaGrid.setDescription(getDescription());
		schemaGrid.setXPath(xPath);
		schemaGrid.setAbsolutexPath(absolutexPath);
		return schemaGrid;
	}
}