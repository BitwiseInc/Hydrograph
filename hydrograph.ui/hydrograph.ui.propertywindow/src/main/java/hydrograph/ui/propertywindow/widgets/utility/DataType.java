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

 
package hydrograph.ui.propertywindow.widgets.utility;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Bitwise
 * Enum for various data types.
 */

public enum DataType {
	
	
	STRING_CLASS(String.class.getCanonicalName()),
	INTEGER_CLASS(Integer.class.getCanonicalName()),
	DOUBLE_CLASS(Double.class.getCanonicalName()),
	FLOAT_CLASS(Float.class.getCanonicalName()),
	SHORT_CLASS(Short.class.getCanonicalName()),
	BOOLEAN_CLASS(Boolean.class.getCanonicalName()),
	DATE_CLASS(Date.class.getCanonicalName()),
	BIGDECIMAL_CLASS(BigDecimal.class.getCanonicalName()),
	LONG_CLASS(Long.class.getCanonicalName());
	
	private String value;

	private DataType(String value) {
		this.value = value;
	}

	/**
	 * Returns the value for enum
	*/
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Equals method 
	*/
	public boolean equals(String property) {
		return this.value.equals(property);
	}
	
}
