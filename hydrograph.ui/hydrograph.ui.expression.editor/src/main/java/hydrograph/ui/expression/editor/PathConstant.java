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

package hydrograph.ui.expression.editor;

public class PathConstant {
	public static final String OPERATOR_CONFIG_FILE = "resources/expression_operator.properties";
	public static final String EXPRESSION_EDITOR_EXTERNAL_JARS_PROPERTIES_FILES = "UserFunctions.properties";

	
	// Runtime ELT-Project specific paths
	public static final String PROJECT_RESOURCES_FOLDER = "resources";
	public static final String PROJECT_LIB_FOLDER = "lib";
	public static final String PROJECTS_SETTINGS_FOLDER = ".settings";
	public static final String TEMP_BUILD_PATH_SETTINGS_FOLDER = ".settings/tempClassPathSourceFolder";
	public static final String TEMP_BUILD_PATH_COMPILATION_PACKAGE = ".settings/tempClassPathSourceFolder/hydrograph";
}
