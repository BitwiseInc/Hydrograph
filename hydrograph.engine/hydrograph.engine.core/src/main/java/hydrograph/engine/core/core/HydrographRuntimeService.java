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
package hydrograph.engine.core.core;

import java.util.Properties;
/**
 * The Interface HydrographRuntimeService.
 *
 * @author Bitwise
 */
public interface HydrographRuntimeService {

    /**
     *
     * @param config
     * @param args
     * @param hydrographJob
     * @param jobId
     * @param UDFPath
     */
    public void initialize(Properties config, String[] args, HydrographJob hydrographJob,
                           String jobId, String UDFPath);

    /**
     * Prepares the execution flow before executing it.
     */
    public void prepareToExecute();

    /**
     * Executes the Hydrograph job.
     */
    public void execute();

    /**
     * Method onComplete executes after completion of Hydrograph job.
     */
    public void oncomplete();

    /**
     * Returns the current statistics of Hydrograph job.
     */
    public Object getExecutionStatus();

    /**
     * kills the running Hydrograph job.
     */
    public void kill();

}