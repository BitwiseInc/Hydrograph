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
package hydrograph.ui.common.datastructures.messages;
import org.eclipse.osgi.util.NLS;

/**
 * The Class Messages. This is a message bundle class.
 * @author Bitwise
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "resources.messages"; //$NON-NLS-1$
	public static String FONT ;
	public static String INVALID_EXPRESSION;
	public static String EXCEL_FORMATS;
	public static String EXCEL_FORMAT_WINDOW_LABEL;
	public static String PARAMETER_FILES;
	public static String PARAMETER_NAME;
	public static String PARAMETER_VALUE;
	public static String COLOR;
	public static String EXCEL_CONV_MAP;
	public static String BORDER_STYLE;
	public static String BORDER_RANGE;
	public static String HORIZONTAL_ALIGN;
	public static String VERTICAL_ALIGN;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
