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


import hydrograph.ui.common.datastructures.dataviewer.JobDetails;
import hydrograph.ui.datastructure.property.GridRow;

import java.util.List;

/**
 * The Class RemoteFilterJson.
 * Provides mechanism for creating json strings for Job and Filter Conditions.
 * @author Bitwise
 *
 */
public class RemoteFilterJson{
	private String condition;
	private List<GridRow> schema;
	private int fileSize;
	private JobDetails jobDetails;
	
	/**
	 * Instantiates a new remote filter json.
	 * 
	 * @param condition
	 *            the condition
	 * @param schema
	 *            the schema
	 * @param fileSize
	 *            the file size
	 * @param jobDetails
	 *            the job details
	 */
	public RemoteFilterJson(String condition, List<GridRow> schema, int fileSize, JobDetails jobDetails) {
		this.condition = condition;
		this.schema = schema;
		this.fileSize = fileSize;
		this.jobDetails = jobDetails;
	}

	/**
	 * Gets the condition.
	 * 
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}
	
	/**
	 * Sets the condition.
	 * 
	 * @param condition
	 *            the new condition
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	/**
	 * Gets the schema.
	 * 
	 * @return the schema
	 */
	public List<GridRow> getSchema() {
		return schema;
	}
	
	/**
	 * Sets the schema.
	 * 
	 * @param schema
	 *            the new schema
	 */
	public void setSchema(List<GridRow> schema) {
		this.schema = schema;
	}
	
	/**
	 * Gets the file size.
	 * 
	 * @return the file size
	 */
	public int getFileSize() {
		return fileSize;
	}
	
	/**
	 * Sets the file size.
	 * 
	 * @param fileSize
	 *            the new file size
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	
	/**
	 * Gets the job details.
	 * 
	 * @return the job details
	 */
	public JobDetails getJobDetails() {
		return jobDetails;
	}
	
	/**
	 * Sets the job details.
	 * 
	 * @param jobDetails
	 *            the new job details
	 */
	public void setJobDetails(JobDetails jobDetails) {
		this.jobDetails = jobDetails;
	}
	@Override
	public String toString() {
		return "RemoteFilterJson [condition=" + condition + ", schema="
				+ schema + ", fileSize=" + fileSize + ", jobDetails="
				+ jobDetails + "]";
	}
}
