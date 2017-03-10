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

package hydrograph.ui.menus.messages;

import org.eclipse.osgi.util.NLS;

/**
 * The Class Messages.
 * <p>
 * This is a message bundle class.
 * 
 * @author Bitwise
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "hydrograph.ui.menus.messages.messages";
	public static String TITLE;
	public static String ALREADY_EXISTS_ERROR_MESSAGE;
	public static String SOURCE_EMPTY_ERROR_MESSAGE;
	public static String NOTE_MESSAGE_TEXT;
	public static String NOTE_LABEL_HEADER_TEXT;
	public static String NEW_FILE_LABEL_TEXT;
	public static String SELECT_FILE_LABEL_TEXT;
	public static String IMPORT_WINDOW_TITLE_TEXT;
	public static String INVALID_TARGET_FILE_ERROR;
	public static String EXCEPTION_OCCURED;
	public static String INF_FILTER_BASE_CLASS;
	public static String INF_AGGREGATOR_BASE_CLASS;
	public static String INF_TRANFORM_BASE_CLASS;
	public static String INF_CUMULATE_BASE_CLASS;
	public static String INF_NORMALIZE_BASE_CLASS;
	public static String EXTERNAL_DTD_NOT_ALLOWED;

	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
