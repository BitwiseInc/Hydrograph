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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.plugin.debug.entity;

/**
 * The Class DebugPoint.
 *
 * @author Bitwise
 *
 */
public class DebugPoint {

	private String fromComponentId;
	private String outSocketType = "out";
	private String outSocketId;
	private long limit = 100;
	private String outputPath = "testData/DebugOutput/";

	/**
	 * @return limit value
	 */
	public Long getLimit() {
		return limit;
	}

	public String getOutSocketType() {
		return outSocketType;
	}

	public void setOutSocketType(String outSocketType) {
		this.outSocketType = outSocketType;
	}

	/**
	 * @param limit
	 * 
	 */
	public void setLimit(Long limit) {
		this.limit = limit;
	}

	/**
	 * @return outSocketId
	 */
	public String getOutSocketId() {
		return outSocketId;
	}

	/**
	 * @param outSocketId
	 */
	public void setOutSocketId(String outSocketId) {
		this.outSocketId = outSocketId;
	}

	/**
	 * @return fromComponentId
	 */
	public String getFromComponentId() {
		return fromComponentId;
	}

	/**
	 * @param fromComponentId
	 */
	public void setFromComponentId(String fromComponentId) {
		this.fromComponentId = fromComponentId;
	}

	/**
	 * @return outputPath
	 */
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * @param outputPath
	 */
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

}
