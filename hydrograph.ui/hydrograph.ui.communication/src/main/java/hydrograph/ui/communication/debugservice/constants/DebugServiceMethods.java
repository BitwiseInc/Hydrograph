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

package hydrograph.ui.communication.debugservice.constants;

/**
 * The class DebugServiceMethods.
 * This class holds debug service endpoints.
 * 
 * @author Bitwise
 *
 */
public class DebugServiceMethods {
	public static final String GET_DEBUG_FILE_PATH = "/read";
	public static final String DELETE_DEBUG_CSV_FILE="/deleteLocalDebugFile";
	public static final String DELETE_BASEPATH_FILES="/delete";
	public static final String GET_FILTERED_FILE_PATH = "/filter";
	public static final String READ_METASTORE="/readFromMetastore";
	public static final String TEST_CONNECTION="/getConnectionStatus";
}
