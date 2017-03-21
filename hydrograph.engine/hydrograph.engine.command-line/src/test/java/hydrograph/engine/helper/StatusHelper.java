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
package hydrograph.engine.helper;

import hydrograph.engine.execution.tracking.ComponentInfo;

import java.util.List;
import java.util.Map;
/**
 * The Class StatusHelper.
 *
 * @author Bitwise
 *
 */
public class StatusHelper {

	List<ComponentInfo> status;
	
	public StatusHelper(List<ComponentInfo> statusList){
		status = statusList;
	}
	
	public String getComponentId(String compId){
		for (ComponentInfo componentInfo : status) {
			if(componentInfo.getComponentId().equals(compId))
			return componentInfo.getComponentId();
		}
		return null;
	}
	
	public String getCurrentStatus(String compId){
		for (ComponentInfo componentInfo : status) {
			if(componentInfo.getComponentId().equals(compId))
			{
			return componentInfo.getCurrentStatus();
			}
		}
		return null;
	}
	
	public Map<String, Long> getProcessedRecords(String compId){
		for (ComponentInfo componentInfo : status) {
			if(componentInfo.getComponentId().equals(compId))
			{
			return componentInfo.getProcessedRecords();
			}
		}
		return null;
	}
	
	public Map<String, String> getStatusPerSocketMap(String compId){
		for (ComponentInfo componentInfo : status) {
			if(componentInfo.getComponentId().equals(compId))
			{
			return componentInfo.getStatusPerSocketMap();
			}
		}
		return null;
	}

}
