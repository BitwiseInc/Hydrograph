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

 
package hydrograph.ui.parametergrid.constants;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * Class for error messages
 * 
 * @author Bitwise
 *
 */
public class ErrorMessages extends NLS{

	private static final String BUNDLE_NAME = "resources.ErrorMessages"; //$NON-NLS-1$
	
	public static String UNABLE_TO_LOAD_PARAM_FILE1;
	public static String UNABLE_TO_LOAD_PARAM_FILE2;
	public static String UNABLE_TO_LOAD_PARAM_FILE3;
	public static String PARAMETER_NAME_CAN_NOT_BE_BLANK;
	public static String PARAMETER_FILE_NOT_LOADED;
	public static String UNABLE_TO_STORE_PARAMETERS;
	
	public static String SAVE_JOB_BEFORE_OPENING_PARAM_GRID;
	public static String UNABLE_TO_POPULATE_PARAM_FILE;
	public static String FILE_EXIST;
	public static String UNABLE_TO_REMOVE_JOB_SPECIFIC_FILE;
	public static String UNABLE_To_WRITE_PROJECT_METADAT_FILE;
	public static String SELECT_ROW_TO_DELETE;
	public static String NAME_VALUE_CANNOT_BE_BLANK;
	public static String BLANK_PARAMETER_WILL_BE_LOST;
	public static String WARNING;

	public static String IO_EXCEPTION_MESSAGE_FOR_SAME_FILE;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ErrorMessages.class);
	}

	private ErrorMessages() {
	}
}
