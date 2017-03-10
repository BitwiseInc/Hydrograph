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
package hydrograph.ui.dataviewer.filter;

/**
 * The Class RetainFilter.
 * Stores the Retain Filter Flag for View Data Filters.
 * @author Bitwise
 *
 */
public class RetainFilter {
	private boolean retainFilter=false;

	/**
	 * Gets the retain filter.
	 * 
	 * @return the retain filter
	 */
	public boolean getRetainFilter() {
		return retainFilter;
	}

	/**
	 * Sets the retain filter.
	 * 
	 * @param retainFilter
	 *            the new retain filter
	 */
	public void setRetainFilter(boolean retainFilter) {
		this.retainFilter = retainFilter;
	}
}
