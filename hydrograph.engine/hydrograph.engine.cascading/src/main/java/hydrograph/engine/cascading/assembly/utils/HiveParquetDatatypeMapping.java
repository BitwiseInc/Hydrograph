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

import hydrograph.engine.core.component.entity.base.AssemblyEntityBase;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.utilities.Constants;

public enum HiveParquetDatatypeMapping {

	STRING(DataType.COMMON), INTEGER(DataType.INTEGER), DATE(DataType.DATE), LONG(
			DataType.LONG), BIGINT(DataType.LONG), BOOLEAN(DataType.COMMON), FLOAT(
			DataType.COMMON), DOUBLE(DataType.COMMON), SHORT(DataType.SHORT), BIGDECIMAL(
			DataType.BIGDECIMAL), TIMESTAMP(DataType.TIMESTAMP);

	private final DataType hiveType;

	HiveParquetDatatypeMapping(DataType hiveType) {
		this.hiveType = hiveType;
	}

	private enum DataType {

		COMMON {
			@Override
			public String getMapping(SchemaField schemaField,
									 AssemblyEntityBase assemblyEntityBase) {
				return getTypeNameFromDataType(schemaField.getFieldDataType())
						.toLowerCase();
			}
		},

		DATE {
			private final String TIME_STAMP = "hh:mm:ss";
			private final String SQL_TIMESTAMP = "java.sql.Timestamp";
			private final String DATE_STRING = "Date";

			@Override
			String getMapping(SchemaField schemaField,
							  AssemblyEntityBase assemblyEntityBase) {
				return getTypeNameFromDataType(
						schemaField.getFieldDataType().contains(DATE_STRING)
								&& schemaField.getFieldFormat().toLowerCase()
								.contains(TIME_STAMP) ? SQL_TIMESTAMP
								: schemaField.getFieldDataType()).toLowerCase();
			}

		},

		SHORT {
			@Override
			public String getMapping(SchemaField schemaField,
									 AssemblyEntityBase assemblyEntityBase) {
				return "smallint";
			}
		},
		INTEGER {
			@Override
			public String getMapping(SchemaField schemaField,
									 AssemblyEntityBase assemblyEntityBase) {
				return "int";
			}
		},
		LONG {
			@Override
			public String getMapping(SchemaField schemaField,
									 AssemblyEntityBase assemblyEntityBase) {
				return "bigint";
			}
		},
		TIMESTAMP {
			@Override
			public String getMapping(SchemaField schemaField,
									 AssemblyEntityBase assemblyEntityBase) {
				return "timestamp";
			}
		},
		BIGDECIMAL {
			@Override
			public String getMapping(SchemaField schemaField,
									 AssemblyEntityBase assemblyEntityBase) {
				if (isScalePrecisionValid(schemaField))
					return "decimal(" + schemaField.getFieldPrecision() + ","
							+ schemaField.getFieldScale() + ")";
				throw new RuntimeException(precisionScaleMsg(schemaField,
						assemblyEntityBase));
			}

			private String precisionScaleMsg(SchemaField schemaField,
											 AssemblyEntityBase assemblyEntityBase) {
				return "Component: '"
						+ assemblyEntityBase.getComponentId()
						+ "', precision or scale not defined for BigDecimal field: '"
						+ schemaField.getFieldName() + "'";
			}

			private boolean isScalePrecisionValid(SchemaField schemaField) {
				return schemaField.getFieldPrecision() != Constants.DEFAULT_PRECISION
						&& schemaField.getFieldScale() != Constants.DEFAULT_SCALE;
			}
		};

		private static String getTypeNameFromDataType(String javaDataType) {
			try {
				return Class.forName(javaDataType).getSimpleName();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return "";
		}

		abstract String getMapping(SchemaField schemaField,
								   AssemblyEntityBase assemblyEntityBase);

	}

	public String getMappingType(SchemaField schemaField,
								 AssemblyEntityBase assemblyEntityBase) {
		return hiveType.getMapping(schemaField, assemblyEntityBase);

	}
}
