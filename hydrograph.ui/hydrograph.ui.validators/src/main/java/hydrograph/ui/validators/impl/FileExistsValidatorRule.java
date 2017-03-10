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

package hydrograph.ui.validators.impl;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


public class FileExistsValidatorRule implements IValidator{

	private String errorMessage;
	
	@Override
	public boolean validateMap(Object object, String propertyName,Map<String,List<FixedWidthGridRow>> inputSchemaMap) {
		Map<String, Object> propertyMap = (Map<String, Object>) object;
		if(propertyMap != null && !propertyMap.isEmpty()){ 
			 return validate(propertyMap.get(propertyName), propertyName,inputSchemaMap,false);
		}
		return false;
	}


	public boolean validate(Object object, String propertyName,Map<String,List<FixedWidthGridRow>> inputSchemaMap
			,boolean isJobImported){
		String value = (String) object;
		if (StringUtils.isNotBlank(value)) {
			if (isFileExistsOnLocalFileSystem(value))
				return true;
			else{
				errorMessage = propertyName + " Invalid file path";
				return false;
			}
		}
		errorMessage= propertyName + " is mandatory";
		return false;
	}

	private boolean isFileExistsOnLocalFileSystem(String value) {
		Matcher matchs=Pattern.compile(Constants.PARAMETER_REGEX).matcher(value);
		if(matchs.find())
			return true;
		IPath jobFilePath = new Path(value);
		try {
			if (jobFilePath.isValidPath(value)) {
				if (ResourcesPlugin.getWorkspace().getRoot().getFile(jobFilePath).exists())
					return true;
				else if (jobFilePath.toFile().exists())
					return true;
			}
		} catch (PatternSyntaxException|SecurityException e) {

		}
		return false;
	}

	
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
}
