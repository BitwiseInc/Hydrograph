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

 
package hydrograph.ui.engine.parsing;




public enum ComponentTypes {
	INPUTS("inputs"),
	OUTPUTS("outputs"),
	OPERATIONS("operations"),
	STRAIGHT_PULLS("straightPulls"),
	COMMANDS("commands"),
	UNKNOWN("unknown");
	
	private final String value;

	ComponentTypes(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static ComponentTypes fromValue(String value) {
		for (ComponentTypes componentTypes : ComponentTypes
				.values()) {
			if (componentTypes.value.equals(value)) {
				return componentTypes;
			}
		}
		return null;
	}
}
