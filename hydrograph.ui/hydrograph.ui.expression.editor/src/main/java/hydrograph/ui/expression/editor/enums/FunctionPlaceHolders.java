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

package hydrograph.ui.expression.editor.enums;

import org.apache.commons.lang.StringUtils;


public enum FunctionPlaceHolders {

	String ("String") {
		@Override
		public String getDefaultValue() {
			return "String Value";
		}
	},
	Integer ("Integer") {
		@Override
		public String getDefaultValue() {
			return "0";
		}
	},
	Float ("Float") {
		@Override
		public String getDefaultValue() {
			return "1.0";
		}
	},
	Double ("Double") {
		@Override
		public String getDefaultValue() {
			return "1.0";
		}
	},
	Long ("Long") {
		@Override
		public String getDefaultValue() {
			return "1";
		}
	},
	Others ("Others") {
		@Override
		public String getDefaultValue() {
			return "null";
		}
	},
	Short ("Short") {
		@Override
		public String getDefaultValue() {
			return "1";
		}
	},
	Boolean ("Boolean") {
		@Override
		public String getDefaultValue() {
			return "false";
		}
	};
	
	
	private final String value;
	
	public abstract String getDefaultValue();
	
	FunctionPlaceHolders(String value) {
		this.value = value;
	}
	
	public String value() {
		return value;
	}

	public static String fromStringValue(String value) {
		for(FunctionPlaceHolders functionPlaceHolders:FunctionPlaceHolders.values()){
			if(StringUtils.equalsIgnoreCase(functionPlaceHolders.value,value)){
				return functionPlaceHolders.getDefaultValue();
			}
		}
		return Others.getDefaultValue();
	}
}
