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
package hydrograph.engine.cascading.scheme.hive.parquet;

import cascading.tap.hive.HiveTableDescriptor;
import cascading.tuple.Fields;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hive.ql.io.parquet.convert.HiveSchemaConverter;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class HiveParquetSchemeHelper {

	private final static String COLUMNS = "columns";
	private final static String COLUMNS_TYPES = "columns.types";

	public static List<String> getTableColumns(
			HiveTableDescriptor hiveTableDescriptor) {
		return Arrays.asList(hiveTableDescriptor.getColumnNames());
	}

	public static List<TypeInfo> getColumnsDataTypes(
			HiveTableDescriptor hiveTableDescriptor) {
		String dataTypes = StringUtils.join(
				hiveTableDescriptor.getColumnTypes(), ":");
		return TypeInfoUtils.getTypeInfosFromTypeString(dataTypes);
	}

	public static String getParquetSchemeMessage(
			HiveTableDescriptor hiveTableDescriptor) {
		return HiveSchemaConverter.convert(
				getTableColumns(hiveTableDescriptor),
				getColumnsDataTypes(hiveTableDescriptor)).toString();
	}

	public static Properties getTableProperties(
			HiveTableDescriptor hiveTableDescriptor) {

		Properties properties = new Properties();
		String columns = StringUtils.join(hiveTableDescriptor.getColumnNames(),
				",");
		String columnTypes = StringUtils.join(
				hiveTableDescriptor.getColumnTypes(), ":");
		properties.put(COLUMNS, columns);
		properties.put(COLUMNS_TYPES, columnTypes);
		return properties;
	}

	public static String getParquetSchemeMessage(Fields sinkFields,
			String[] columnTypes) {
		List<String> columnName = new ArrayList<String>();
		for (int i = 0; i < sinkFields.size(); i++) {
			columnName.add(sinkFields.get(i).toString());
		}
		String dataTypes = StringUtils.join(columnTypes, ":");
		return HiveSchemaConverter.convert(columnName,
				TypeInfoUtils.getTypeInfosFromTypeString(dataTypes)).toString();
	}
}
