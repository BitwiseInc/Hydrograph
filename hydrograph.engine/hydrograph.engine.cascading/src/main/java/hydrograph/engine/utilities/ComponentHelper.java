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

import cascading.operation.Insert;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.tuple.Fields;

public class ComponentHelper {

	/**
	 * Add field with constant value for each row.
	 * 
	 * @param <T>
	 * 
	 * @param inputFile
	 * @param constantValue
	 * @return pipe array with appended field
	 */
	public static <T> Pipe addConstantField(Pipe inputFile, String fieldName, T constantValue) {

		inputFile = new Each(inputFile, Fields.ALL, // inputFields,
				new Insert(new Fields(fieldName), constantValue), Fields.ALL);

		return inputFile;
	}

	public static String getComponentName(String component, String componentId, String outsocketId) {

		if (outsocketId == null || outsocketId.equals(""))
			return component + Constants.PIPE_NAME_APPENDER + componentId;
		else
			return component + Constants.PIPE_NAME_APPENDER + componentId + "_" + outsocketId;
	}
}
