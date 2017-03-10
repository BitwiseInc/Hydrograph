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

package hydrograph.ui.graph.execution.tracking.preferences;


/**
 * The Class Utils.
 * @author Bitwise
 */

import org.eclipse.core.runtime.Platform;

import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.graph.Activator;

public class Utils {

	public static Utils INSTANCE= new Utils();
	
	private Utils(){
		
	}
	/**
	 * 
	 * Return tools Installation path
	 * 
	 * @return {@link String}
	 */
	public String getInstallationPath() {
		String installationPath = Platform.getInstallLocation().getURL().getPath();
		if (OSValidator.isWindows()) {
			if (installationPath.startsWith("/")) {
				installationPath = installationPath.replaceFirst("/", "").replace("/", "\\");
			}
		}
		return installationPath;

	}
	
	
	/**
	 * 
	 * @return console log level
	 */
	public String getConsoleLogLevel(){
		
		String logLevel = Platform.getPreferencesService().getString(
				Activator.PLUGIN_ID,
				JobRunPreference.LOG_LEVEL_PREFERENCE,
				JobRunPreference.DEFUALT_LOG_LEVEL, null);

		return logLevel;
		
	}
}
