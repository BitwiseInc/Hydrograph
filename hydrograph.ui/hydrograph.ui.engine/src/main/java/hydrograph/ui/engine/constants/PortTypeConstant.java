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

 
package hydrograph.ui.engine.constants;

public class PortTypeConstant {

	private static final String INPUT_PORT_TYPE = "in";
	private static final String OUTPUT_PORT_TYPE = "out";
	private static final String UNUSED_PORT_TYPE = "unused";

	public static String getPortType(String PortName) {
		if (PortName.startsWith("in"))
			return INPUT_PORT_TYPE;
		if (PortName.startsWith("out"))
			return OUTPUT_PORT_TYPE;
		if (PortName.startsWith("unused"))
			return UNUSED_PORT_TYPE;

		return null;

	}

}
