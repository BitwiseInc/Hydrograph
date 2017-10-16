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

import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class to check if String is parameter.
 * 
 * @author Bitwise 
 * 
 */

public class ParameterUtil {
	
	private ParameterUtil(){}
	
	
	/**
	 * Checks whether given input is parameter or not.
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isParameter(String input) {
		if (input != null) {
			//String regex = "[\\@]{1}[\\{]{1}[\\w]*[\\}]{1}";
			Matcher matchs = Pattern.compile(Constants.PARAMETER_REGEX).matcher(input);
			if (matchs.matches()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if input string contains any parameter.
	 *  
	 * @param path
	 * @param separator
	 * @return
	 */
	public static boolean containsParameter(String path,char separator) {
		String pathSegments[]=StringUtils.split(path, separator);
		for (String input:pathSegments){
			if(isParameter(input))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns true if input string starts with parameter.
	 * 
	 * @param path
	 * @param separator
	 * @return
	 */
	public static boolean startsWithParameter(String path,char separator) {
		String pathSegments[]=StringUtils.split(path, separator);
		if(isParameter(pathSegments[0])){
				return true;
		}
		return false;
	}
	
	public static void addPrefixSuffixToParameterFields(FilterProperties fieldToBeParameterize,TransformMapping transformMapping)
	{
		
		boolean isParameter=true;
		List<MappingSheetRow> mappingSheetRowList=transformMapping.getMappingSheetRows();
		for(MappingSheetRow mappingSheetRow:mappingSheetRowList)
		{
			for(FilterProperties filterProperties:mappingSheetRow.getOutputList())
			{
				if(filterProperties==fieldToBeParameterize)
				{
					isParameter=false;
					break;
				}	
					
			}
			if(!isParameter)
			break;	
		}
		if(isParameter)
		{
			for(NameValueProperty nameValueProperty:transformMapping.getMapAndPassthroughField())
			{
				if(nameValueProperty.getFilterProperty()==fieldToBeParameterize)
				{
				isParameter=false;
				break;
				}
			}	
		}
		if(isParameter && !(isParameter(fieldToBeParameterize.getPropertyname()))
			&& StringUtils.isNotBlank(fieldToBeParameterize.getPropertyname()))
		{
			fieldToBeParameterize.setPropertyname(fieldToBeParameterize.getPropertyname());
		}	
	}
	
	
}
