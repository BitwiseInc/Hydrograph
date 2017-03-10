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
package hydrograph.ui.graph.model;

public enum PortTypeEnum {

	IN("in"), 
	OUT("out"), 
	UNUSED("unused"),
	LOOKUP("lookup"),
	DRIVER("driver")
	;
	private final String value;
	
	PortTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PortTypeEnum fromValue(String v) {
        for (PortTypeEnum c: PortTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
