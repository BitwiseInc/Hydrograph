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

package hydrograph.ui.tooltip.tooltips;

import org.eclipse.osgi.util.NLS;

/**
 * The Class Messages.
 * 
 * @author Bitwise
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "resources.tooltipErrorMessages";

	public static String JOIN_KEY_ERROR_MESSAGE;
	public static String DRIVER_KEY_ERROR_MESSAGE;
	public static String LOOKUP_KEY_ERROR_MESSAGE;
	public static String EXTERNAL_SCHEMA_PATH_ERROR_MESSAGE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
	
}
