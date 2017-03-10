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

 
package hydrograph.ui.tooltip.utils;

import hydrograph.ui.logging.factory.LogFactory;

import org.slf4j.Logger;


/**
 * 
 * ToolTipUtils - Tooltip utils class
 * 
 * @author Bitwise
 *
 */
public class ToolTipUtils {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ToolTipUtils.class);
	
	/**
	 * returns length of longest string in string array
	 * 
	 * @param lines - Array of strings
	 * @return maxlength
	 */
	public static int getMaxLength(String[] lines) {
		
		int maxLength=0;		
		for(int i=0;i<lines.length;i++){
			logger.debug("ToolTipUtils.getMaxLength: lines["+ i+"]=" + lines[i]);
			if(lines[i].length() > maxLength){
				maxLength = lines[i].length();
			}
		}
		logger.debug("ToolTipUtils.getMaxLength: max length=" + maxLength);
		return maxLength;
	}
	
	
	public static String[] getLines(String text) {
		String[] lines = text.split("\\n");
		return lines;
	}
}
