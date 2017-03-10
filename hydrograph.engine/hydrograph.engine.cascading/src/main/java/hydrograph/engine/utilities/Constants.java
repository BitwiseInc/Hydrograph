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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.utilities;

/**
 * This class contains the constants to be used across the project. These
 * constants are stored as static final variables of the class. This class
 * should not be instantiated and hence, the constructor is marked private
 * 
 * @author Prabodh
 */
public class Constants {

	private Constants() {
	}

	/**
	 * The default value of 'scale' attribute of big decimal fields used on input / output schema
	 */
	public static final int DEFAULT_SCALE = -999;
	
	
	public static final String HYDROGRAPH_EXECUTION_ENGINE_TEZ="tez";
	
	public static final String HYDROGRAPH_EXECUTION_ENGINE_MR="mr";

	/**
	 * The default value of 'precision' attribute of big decimal fields used on input / output schema
	 */
	public static final int DEFAULT_PRECISION = -999;
	
	public static final String PIPE_NAME_APPENDER="%%";

}
