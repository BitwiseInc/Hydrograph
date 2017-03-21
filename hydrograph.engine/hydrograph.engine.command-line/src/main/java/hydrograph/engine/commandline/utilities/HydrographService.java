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
package hydrograph.engine.commandline.utilities;

import hydrograph.engine.execution.tracking.ComponentInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class HydrographService.
 *
 * @author Bitwise
 *
 */
public class HydrographService {

	private HydrographExecution execution;
	private boolean isJobRunning = false;
	/**
	 * Class Constructor
	 */
	public HydrographService() {
		execution = new HydrographExecution();
	}

	/**
	 * Execute the Hydrograph job with arguments passed.
	 * 
	 * @param args
	 *            - arguments for execution <br/>
	 *            for example: -xmlpath "xml-file-path"
	 * @return Integer value for execution status: 0 - successful
	 * @throws Exception
	 */
	public int executeGraph(String[] args) throws Exception {
		try{
			isJobRunning = true;
			execution.run(args);
			return 0;
		}
		catch(Exception e){
			throw e;
		}
		finally{
			isJobRunning = false;
		}
	}

	/**
	 * Returns the current statistics of the job in a list of ComponentInfo
	 * objects, Each ComponentInfo represents a component in Hydrograph.
	 * 
	 * @return List of {@link ComponentInfo}
	 */
	public List<ComponentInfo> getStatus() {
		if (execution.getExecutionStatus() != null)
			return new ArrayList<>(execution.getExecutionStatus());
		else
			return Collections.emptyList();
	}
	
	public boolean getJobRunningStatus(){
		return isJobRunning;
	}

	/**
	 * Kills the current running job.
	 */
	public void kill() {
		execution.kill();
	}
}