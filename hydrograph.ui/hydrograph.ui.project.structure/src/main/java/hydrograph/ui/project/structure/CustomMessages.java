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

 
package hydrograph.ui.project.structure;

import org.eclipse.osgi.util.NLS;

/**
 * The Class CustomMessages.
 * 
 * @author Bitwise
 */
public class CustomMessages extends NLS {
	private static final String BUNDLE_NAME = "hydrograph.ui.project.structure.messages"; //$NON-NLS-1$
	public static String ProjectSupport_RESOURCES;
	public static String ProjectSupport_GLOBAL_PARAM;
	public static String ProjectSupport_JOBS;
	public static String ProjectSupport_PARAM;
	public static String ProjectSupport_SCHEMA;
	public static String CustomWizard_CREATE_ETL_PROJECT;
	public static String CustomWizard_PROJECT_DESCRIPTION;
	public static String ProjectSupport_BIN;
	public static String ProjectSupport_LIB;
	public static String ProjectSupport_SCRIPTS;
	public static String ProjectSupport_XML;
	public static String ProjectSupport_GRADLE;
	public static String ProjectSupport_CONFIG_FOLDER;
	public static String GradleNature_ID;
	public static String ProjectSupport_Settings;
	public static String ProjectSupport_EXTERNALTRANSFORMFILES;
	public static String CustomWizard_WINDOW_TITLE;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, CustomMessages.class);
	}

	private CustomMessages() {
	}
}
