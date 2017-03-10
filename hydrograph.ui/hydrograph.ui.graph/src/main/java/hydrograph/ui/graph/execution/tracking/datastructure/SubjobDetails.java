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

package hydrograph.ui.graph.execution.tracking.datastructure;

/**
 * The Class SubjobDetails.
 * @author Bitwise
 */

public class SubjobDetails {
	private String sourceTerminal;
	private String targetTerminal;
	
	public SubjobDetails(String sourceTerminal, String targetTerminal) {
		this.sourceTerminal = sourceTerminal;
		this.targetTerminal = targetTerminal;
	}

	/**
	 * @return source terminal of the subjob
	 */
	public String getSourceTerminal() {
		return sourceTerminal;
	}

	/**
	 * set source terminal of the subjob
	 * @param sourceTerminal
	 */
	public void setSourceTerminal(String sourceTerminal) {
		this.sourceTerminal = sourceTerminal;
	}

	/**
	 * @return target terminal of subjob
	 */
	public String getTargetTerminal() {
		return targetTerminal;
	}

	/**
	 * set target terminal of subjob
	 * @param targetTerminal
	 */
	public void setTargetTerminal(String targetTerminal) {
		this.targetTerminal = targetTerminal;
	}

	@Override
	public String toString() {
		return "SubjobDetails [sourceTerminal=" + sourceTerminal
				+ ", targetTerminal=" + targetTerminal + "]";
	}
	
	
}
