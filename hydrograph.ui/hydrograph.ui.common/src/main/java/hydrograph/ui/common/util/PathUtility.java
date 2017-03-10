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

package hydrograph.ui.common.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 *  Utility class to check path is absolute.
 *  @author Bitwise
 */
public class PathUtility {
	
	/** The Constant INSTANCE. */
	public static final PathUtility INSTANCE = new PathUtility();
	
	/**
	 * Instantiates a new path utility.
	 */
	private PathUtility(){
		
	}
	
	/**
	 * Checks if path is absolute.
	 *
	 * @param filePath
	 * @return true, if is absolute
	 */
	public boolean isAbsolute(String filePath){
		IPath path = new Path(filePath);
		return path.isAbsolute();
	}

}
