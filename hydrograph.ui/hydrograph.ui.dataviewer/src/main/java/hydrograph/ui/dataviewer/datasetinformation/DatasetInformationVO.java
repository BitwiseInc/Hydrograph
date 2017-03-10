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
package hydrograph.ui.dataviewer.datasetinformation;

/**
 * The Class DatasetInformationVO.
 * This is a value object class storing the values of Dataset Information Window fields.
 * @author Bitwise
 */
public class DatasetInformationVO {
	private String viewDataFilePath="";
	private String chunkFilePath="";
	private String sizeOfData;
	private String noOfRecords="";
	private String pageSize="";
	private String delimeter="";
	private String quote="";
	private String filterExpression="";
	private String edgeNode="";
	private String userName="";
	private String localFilter="";
	private String remoteFilter="";
	private String acctualFileSize;
	
	public String getAcctualFileSize() {
		return acctualFileSize;
	}

	public void setAcctualFileSize(String acctualFileSize) {
		this.acctualFileSize = acctualFileSize;
	}

	public String getViewDataFilePath() {
		return viewDataFilePath;
	}
	
	public void setViewDataFilePath(String viewDataFilePath) {
		this.viewDataFilePath = viewDataFilePath;
	}
	
	public String getEdgeNode() {
		return edgeNode;
	}
	
	public void setEdgeNode(String edgeNode) {
		this.edgeNode = edgeNode;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getChunkFilePath() {
		return chunkFilePath;
	}
	
	public void setChunkFilePath(String chunkFilePath) {
		this.chunkFilePath = chunkFilePath;
	}
	
	public String getSizeOfData() {
		return sizeOfData;
	}
	
	public void setSizeOfData(String integerFieldEditor) {
		this.sizeOfData = integerFieldEditor;
	}
	
	public String getNoOfRecords() {
		return noOfRecords;
	}
	
	public void setNoOfRecords(String noOfRecords) {
		this.noOfRecords = noOfRecords;
	}
	
	public String getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getDelimeter() {
		return delimeter;
	}
	
	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}
	
	public String getQuote() {
		return quote;
	}
	
	public void setQuote(String quote) {
		this.quote = quote;
	}
	
	public String getFilterExpression() {
		return filterExpression;
	}
	
	public void setFilterExpression(String filterExpression) {
		this.filterExpression = filterExpression;
	}
	public String getLocalFilter() {
		return localFilter;
	}

	public void setLocalFilter(String localFilter) {
		this.localFilter = localFilter;
	}

	public String getRemoteFilter() {
		return remoteFilter;
	}

	public void setRemoteFilter(String remoteFilter) {
		this.remoteFilter = remoteFilter;
	}
	

}
