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
package hydrograph.engine.cascading.assembly.utils;

import hydrograph.engine.cascading.utilities.DataTypeCoerce;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum HiveTypeToCoercibleTypeMapping {

	INT(HiveType.INT), STRING(HiveType.STRING), DOUBLE(HiveType.DOUBLE), FLOAT(
			HiveType.FLOAT), SHORT(HiveType.SHORT), BOOLEAN(HiveType.BOOLEAN), DATE(
			HiveType.DATE), TIMESTAMP(HiveType.TIMESTAMP), DECIMAL(
			HiveType.DECIMAL), LONG(HiveType.LONG), BIGINT(HiveType.LONG);

	private final HiveType hiveType;

	HiveTypeToCoercibleTypeMapping(HiveType hiveType) {
		this.hiveType = hiveType;
	}

	private enum HiveType {

		INT {

			@Override
			Type getMapping(String hiveDataType) {

				return DataTypeCoerce.convertClassToCoercibleType(
						Integer.class, null, -999, null);
			}

		},

		STRING {
			@Override
			Type getMapping(String hiveDataType) {

				return DataTypeCoerce.convertClassToCoercibleType(String.class,
						null, -999, null);
			}
		},
		DOUBLE {
			@Override
			Type getMapping(String hiveDataType) {

				return DataTypeCoerce.convertClassToCoercibleType(Double.class,
						null, -999, null);
			}
		},
		FLOAT {
			@Override
			Type getMapping(String hiveDataType) {

				return DataTypeCoerce.convertClassToCoercibleType(Float.class,
						null, -999, null);
			}
		},
		SHORT {
			@Override
			Type getMapping(String hiveDataType) {

				return DataTypeCoerce.convertClassToCoercibleType(Short.class,
						null, -999, null);
			}
		},
		BOOLEAN {
			@Override
			Type getMapping(String hiveDataType) {

				return DataTypeCoerce.convertClassToCoercibleType(
						Boolean.class, null, -999, null);
			}
		},
		DATE {
			@Override
			Type getMapping(String hiveDataType) {

				return DataTypeCoerce.convertClassToCoercibleType(Date.class,
						"yyyy-MM-dd", -999, null);
			}
		},
		TIMESTAMP {
			@Override
			Type getMapping(String hiveDataType) {

				return DataTypeCoerce.convertClassToCoercibleType(Date.class,
						"yyyy-MM-dd HH:mm:ss", -999, null);
			}
		},
		DECIMAL {
			@Override
			Type getMapping(String hiveDataType) {

				return DataTypeCoerce.convertClassToCoercibleType(
						BigDecimal.class, null, getScale(hiveDataType), null);
			}
		},
		LONG {
			@Override
			Type getMapping(String hiveDataType) {

				return DataTypeCoerce.convertClassToCoercibleType(Long.class,
						null, -999, null);
			}
		},
		;

		abstract Type getMapping(String hiveDataType);

		int getScale(String typeWithScale) {

			String pattern = "decimal\\((\\d+),(\\d+)\\)";
			Pattern r = Pattern.compile(pattern);

			Matcher m = r.matcher(typeWithScale);
			if (m.find()) {
				return Integer.parseInt(m.group(2));
			} else {
				return -999;
			}
		}

	}

	public Type getMappingType(String tableDescriptor) {
		return hiveType.getMapping(tableDescriptor);

	}
}
