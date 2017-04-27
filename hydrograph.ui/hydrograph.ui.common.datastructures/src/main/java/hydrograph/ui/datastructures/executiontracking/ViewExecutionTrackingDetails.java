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
package hydrograph.ui.datastructures.executiontracking;

/**
 * The Class ViewExecutionTrackingDetails holds tracking details
 * @author Bitwise
 *
 */
public class ViewExecutionTrackingDetails {
	private boolean viewHistoryTracking = false;
 	private String selectedUniqueJobId;
 	private String selectedLogFilePath;
 	
 	/**
 	 * @param builder
 	 */
 	private ViewExecutionTrackingDetails(Builder builder) {
 		this.selectedUniqueJobId = builder.selectedUniqueJobId;
 		this.selectedLogFilePath = builder.selectedLogFilePath;
 		this.viewHistoryTracking = builder.viewHistoryTracking;
 	}
 	
 	
 	/**
 	 * The Class Builder
 	 * @author Bitwise
 	 *
 	 */
 	public static class Builder{
 		protected ViewExecutionTrackingDetails executionTrackingDetails;
 		//required parameter
 		private String selectedUniqueJobId;
 		private String selectedLogFilePath;
 		private boolean viewHistoryTracking = true;
 		
 		/**
 		 * @param selectedUniqueJobId
 		 * @param selectedLogFilePath
 		 * @param isRemoteMode
 		 * @param viewHistoryTracking
 		 */
 		public Builder(String selectedUniqueJobId, String selectedLogFilePath, boolean viewHistoryTracking){
 			this.selectedUniqueJobId = selectedUniqueJobId;
 			this.selectedLogFilePath = selectedLogFilePath;
 			this.viewHistoryTracking = viewHistoryTracking;
 		}
 		
 		/**
 		 * The Function will return new instance.
 		 * @return object
 		 */
 		public ViewExecutionTrackingDetails build(){
 			return new ViewExecutionTrackingDetails(this);
 		}
 		
 		/**
 		 * @param viewHistoryTracking
 		 * @return
 		 */
 		public Builder viewHistoryTracking(boolean viewHistoryTracking) {
 			this.viewHistoryTracking = viewHistoryTracking;
 			return this;
 		}
 		
 		/**
 		 * @param executionTrackingDetails
 		 * @return
 		 */
 		public Builder copy(ViewExecutionTrackingDetails executionTrackingDetails){
 			this.selectedUniqueJobId = executionTrackingDetails.selectedUniqueJobId;
 			this.selectedLogFilePath = executionTrackingDetails.selectedLogFilePath;
 			this.viewHistoryTracking = executionTrackingDetails.viewHistoryTracking;
 			
 			return this;
 		}
 	}
 	
 	/**
 	 * The Function will return selected run job id
 	 * @return job id
 	 */
 	public String getSelectedUniqueJobId(){
 		return selectedUniqueJobId;
 	}
 	
 	/**
 	 * The Function will return log file path
 	 * @return file path
 	 */
 	public String getSelectedLogFilePath(){
 		return selectedLogFilePath;
 	}
 
 	/**
 	 * @return
 	 */
 	public boolean isViewHistoryTracking(){
 		return viewHistoryTracking;
 	}

	@Override
	public String toString() {
		return "ViewExecutionTrackingDetails [viewHistoryTracking=" + viewHistoryTracking + ", selectedUniqueJobId="
				+ selectedUniqueJobId + ", selectedLogFilePath=" + selectedLogFilePath + "]";
	}
 	
}
