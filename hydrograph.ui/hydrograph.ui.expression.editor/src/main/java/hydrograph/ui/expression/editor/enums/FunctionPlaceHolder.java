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

public enum FunctionPlaceHolder {

	String {
		@Override
		public String getValue(String fieldName) {
			return " String Value ";
		}

	},
	Integer {
		@Override
		public String getValue(String fieldName) {
			return " 0 ";
		}

	},
	Float {
		@Override
		public String getValue(String fieldName) {
			return " 1.0 ";
		}

	},
	Double {
		@Override
		public String getValue(String fieldName) {
			return " 1.0 ";
		}

	},
	Long {
		@Override
		public String getValue(String fieldName) {
			return " 1 ";
		}

	},
	Others {
		@Override
		public String getValue(String fieldName) {
			return " null ";
		}

	},
	BigDecimal {
		@Override
		public String getValue(String fieldName) {
			return " null ";
		}

	},
	Short {
		@Override
		public String getValue(String fieldName) {
			return " 1 ";
		}

	},
	Boolean {
		@Override
		public String getValue(String fieldName) {
			return " false ";
		}

	};

	public abstract String getValue(String fieldName);
}
