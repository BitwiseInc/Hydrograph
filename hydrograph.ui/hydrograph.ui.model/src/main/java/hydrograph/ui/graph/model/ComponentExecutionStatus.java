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


/**
 * The Enum ComponentExecutionStatus.
 * <p>
 * Stores the component execution status values
 * 
 * @author Bitwise
 */
public enum ComponentExecutionStatus {
	
	/** The blank. */
	BLANK("BLANK"),
	
	/** The pending. */
	PENDING("PENDING"), 
	
	/** The running. */
	RUNNING("RUNNING"), 
	
	/** The successful. */
	SUCCESSFUL("SUCCESSFUL"),
	
	/** The failed. */
	FAILED("FAILED")
	;
	
	/** The value. */
	private final String value;
	
	/**
	 * Instantiates a new comp status.
	 *
	 * @param v the v
	 */
	ComponentExecutionStatus(String v) {
        value = v;
    }

    /**
     * Value.
     *
     * @return the string
     */
    public String value() {
        return value;
    }

    /**
     * From value.
     *
     * @param v the v
     * @return the comp status
     */
    public static ComponentExecutionStatus fromValue(String v) {
        for (ComponentExecutionStatus c: ComponentExecutionStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}