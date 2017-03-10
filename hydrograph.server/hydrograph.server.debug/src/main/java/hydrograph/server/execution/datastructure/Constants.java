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

package hydrograph.server.execution.datastructure;
/**
 * NOTE : Do not change/modify values for below constants(not even space) until you know 
 * where it is affecting the behavior 
 * @author Bitwise
 */
public class Constants {
	public static final String CLIENTID="client-id";
	public static final String ENGINE_CLIENT="engine-client";
	public static final String JOBID="jobId";
	public static final String JOBID_KEY="-jobid";
	public static final String IS_TRACKING_ENABLE="-isexecutiontracking";
	public static final String POST="post";
	public static final String GET="get";
	public static final String KILL="kill";
	public static final long DELAY_TIME=7000;
	public static final String TRACKING_CLIENT_SOCKET_PORT="-trackingclientsocketport";	
}
