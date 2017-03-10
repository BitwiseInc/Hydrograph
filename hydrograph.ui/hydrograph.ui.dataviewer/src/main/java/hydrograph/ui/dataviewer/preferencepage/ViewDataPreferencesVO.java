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

package hydrograph.ui.dataviewer.preferencepage;

/**
 * The Class ViewDataPreferencesVO
 * This VO class is responsible for storing View Data Preferences
 * 
 * @author Bitwise
 *
 */
public class ViewDataPreferencesVO {
	private String delimiter;
	private String quoteCharactor;
	private Boolean includeHeaders;
	private int fileSize;
	private int pageSize;

	public ViewDataPreferencesVO() {
		delimiter = ",";
		quoteCharactor = "\"";
		includeHeaders = true;
		fileSize=100;
		pageSize=100;

	}
	public ViewDataPreferencesVO(String delimiter, String quoteCharactor, boolean includeHeaders,int fileSize,int pageSize) {
		this.delimiter=delimiter;
		this.quoteCharactor=quoteCharactor;
		this.includeHeaders=includeHeaders;
		this.fileSize=fileSize;
		this.pageSize=pageSize;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getQuoteCharactor() {
		return quoteCharactor;
	}

	public void setQuoteCharactor(String quoteCharactor) {
		this.quoteCharactor = quoteCharactor;
	}

	public Boolean getIncludeHeaders() {
		return includeHeaders;
	}

	public void setIncludeHeaders(Boolean includeHeaders) {
		this.includeHeaders = includeHeaders;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
