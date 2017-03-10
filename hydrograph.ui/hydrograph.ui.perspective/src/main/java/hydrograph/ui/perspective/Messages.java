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

package hydrograph.ui.perspective;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "resources.messages";
	public static String STARTUP_JDK_WARNING_WINDOW_DIALOG ;
	public static String TOOL_EXT_MESSAGE;
	public static String TOOL_EXIT_MESSAGE_BOX_TITLE;
	public static String JDK_PATH_DIALOG_TITLE;
	public static String JDK_PATH_DIALOG_MESSAGE;
	public static String JDK_PATH_DIALOG_NOTE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
