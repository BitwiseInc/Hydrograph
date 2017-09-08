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

 
package hydrograph.ui.common.message;

import org.eclipse.osgi.util.NLS;

/**
 * The Class Messages. This is a message bundle class.
 * @author Bitwise
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "hydrograph.ui.common.messages"; //$NON-NLS-1$
	public static String XMLConfigUtil_CONFIG_FOLDER;
	public static String XMLConfigUtil_XML_CONFIG_FOLDER;
	public static String XMLConfigUtil_FILE_EXTENTION;
	public static String XMLConfigUtil_COMPONENTCONFIG_XSD_PATH;
	public static String XMLConfigUtil_POLICYCONFIG_XSD_PATH;
	public static String XMLConfigUtil_POLICY;
	public static String EXTERNAL_OPERATION_CONFIG_XSD_PATH;
	public static String EXTERNAL_EXPRESSION_CONFIG_XSD_PATH;
	public static String IMPORT_XML_FORMAT_ERROR;
	public static String SCALE_TYPE_NONE;
	public static String EXTERNAL_MAPPING_FIELDS_CONFIG_XSD_PATH;
	public static String DATATYPELIST;
	public static String SCALETYPELIST;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
