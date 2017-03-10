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

 
package hydrograph.ui.engine.ui.converter;


public class LinkingData {
	
	private String sourceComponentId;
	private  String targetComponentId;
	private String sourceTerminal;
	private String targetTerminal;
	private boolean isMultiplePortsAllowed;
		
	private LinkingData() {
	}

	public LinkingData(String sourceComponentId, String targetComponentId,String sourceTerminal,String targetTerminal) {
		super();
		this.sourceComponentId = sourceComponentId;
		this.targetComponentId = targetComponentId;
		setSourceTerminal(sourceTerminal);
		setTargetTerminal(targetTerminal);

	}

	public String getSourceComponentId() {
		return sourceComponentId;
	}

	public void setSourceComponentId(String sourceComponentId) {
		this.sourceComponentId = sourceComponentId;
	}

	public String getTargetComponentId() {
		return targetComponentId;
	}

	public void setTargetComponentId(String targetComponentId) {
		this.targetComponentId = targetComponentId;
	}
	
	public String getSourceTerminal() {
		return sourceTerminal;
	}

	public void setSourceTerminal(String sourceTerminal) {
		this.sourceTerminal=sourceTerminal;

	}
	
	public String getTargetTerminal() {
		return targetTerminal;
	}

	public void setTargetTerminal(String targetTerminal) {
		this.targetTerminal=targetTerminal;
	}

	@Override
	public String toString() {
	
		return "Source Component ID:"+this.sourceComponentId+" | "
				+ "Source Terminal:"+this.sourceTerminal+" | "
				+ "Target Component ID:"+this.targetComponentId+" | "
				+ "Target Terminal:"+this.targetTerminal+" | "
				+ "Multiple Port Allowed:"+this.isMultiplePortsAllowed;
	}
}

