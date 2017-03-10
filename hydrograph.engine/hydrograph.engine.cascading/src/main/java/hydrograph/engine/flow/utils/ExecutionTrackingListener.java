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
package hydrograph.engine.flow.utils;

import cascading.stats.StatsListener;
import hydrograph.engine.cascading.integration.RuntimeContext;
import hydrograph.engine.execution.tracking.ComponentInfo;

import java.util.List;

/**
 * Interface ExecutionTrackingListener adds listener to the framework. It also
 * provides updated status of execution.
 * 
 * @author bitwise
 */
public interface ExecutionTrackingListener extends StatsListener {

	/**
	 * Method addListener adds listener to the framework
	 * 
	 * @param runtimeContext
	 *            - {@link RuntimeContext}
	 */
	public void addListener(RuntimeContext runtimeContext);

	/**
	 * Method getStatus whenever invoked gives the current statistics of
	 * components in Hydrograph.
	 * 
	 * @return List of {@link ComponentInfo}
	 */
	public List<ComponentInfo> getStatus();

}
