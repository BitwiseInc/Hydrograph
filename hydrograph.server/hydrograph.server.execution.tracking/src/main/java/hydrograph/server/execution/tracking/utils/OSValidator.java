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
package hydrograph.server.execution.tracking.utils;

/**
 * OS Validate find and validate OS.
 * @author bitwise
 *
 */
public class OSValidator {

private static String OS = System.getProperty("os.name").toLowerCase();
	
	/**
	 * Checks if is windows.
	 * 
	 * @return true, if is windows
	 */
	public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}

	/**
	 * Checks if is mac.
	 * 
	 * @return true, if is mac
	 */
	public static boolean isMac() {

		return (OS.indexOf("mac") >= 0);

	}

	/**
	 * Checks if is unix.
	 * 
	 * @return true, if is unix
	 */
	public static boolean isUnix() {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
		
	}

	/**
	 * Checks if is solaris.
	 * 
	 * @return true, if is solaris
	 */
	public static boolean isSolaris() {

		return (OS.indexOf("sunos") >= 0);

	}
}
