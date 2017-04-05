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
package hydrograph.engine.expression.utils;

/**
 * The Enum ClassToDataTypeConversion.
 *
 * @author Bitwise
 *
 */
public enum ClassToDataTypeConversion {

	String {
		@Override
		public String getValue(String fieldName) {
			return "String " + fieldName + "=\"\";";
		}

	},
	Integer {
		@Override
		public String getValue(String fieldName) {
			return "int " + fieldName + "=0;";
		}

	},
	Float {
		@Override
		public String getValue(String fieldName) {
			return "float " + fieldName + "=1.0f;";
		}

	},
	Double {
		@Override
		public String getValue(String fieldName) {
			return "double " + fieldName + "=1.0;";
		}

	},
	Long {
		@Override
		public String getValue(String fieldName) {
			return "long " + fieldName + "=1;";
		}

	},
	Date {
		@Override
		public String getValue(String fieldName) {
			return "Date " + fieldName + "=null;";
		}

	},
	BigDecimal {
		@Override
		public String getValue(String fieldName) {
			return "BigDecimal " + fieldName + "=new BigDecimal(99.99);";
		}

	},
	Short {
		@Override
		public String getValue(String fieldName) {
			return "short " + fieldName + "=1;";
		}

	},
	Boolean {
		@Override
		public String getValue(String fieldName) {
			return "boolean " + fieldName + "=true;";
		}

	};

	private String expr;

	public abstract String getValue(String fieldName);

	public String get() {
		return expr;
	}
}