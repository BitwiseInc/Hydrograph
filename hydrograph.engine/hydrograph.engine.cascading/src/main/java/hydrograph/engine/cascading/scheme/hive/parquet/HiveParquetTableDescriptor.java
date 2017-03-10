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
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.metastore.TableType;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import java.sql.Timestamp;
import java.util.*;

public class HiveParquetTableDescriptor extends HiveTableDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5907851571411040963L;

	public static final String HIVE_DEFAULT_INPUT_FORMAT_NAME = "org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat";
	public static final String HIVE_DEFAULT_OUTPUT_FORMAT_NAME = "org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat";
	public static final String HIVE_DEFAULT_SERIALIZATION_LIB_NAME = "org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe";

	private String[] partitionKeys;
	private String tableName;
	private String[] columnNames;
	private String[] columnTypes;
	private String serializationLib;
	private String location = null;

	private boolean transactional;
	private String delimiter;
	private int buckets;
	private String databaseName;

	public HiveParquetTableDescriptor(String tableName, String[] columnNames,
			String[] columnTypes) {
		this(HIVE_DEFAULT_DATABASE_NAME, tableName, columnNames, columnTypes,
				new String[] {}, HIVE_DEFAULT_DELIMITER,
				HIVE_DEFAULT_SERIALIZATION_LIB_NAME, null);
	}

	public HiveParquetTableDescriptor(String tableName, String[] columnNames,
			String[] columnTypes, boolean transactional) {
		this(HIVE_DEFAULT_DATABASE_NAME, tableName, columnNames, columnTypes,
				new String[] {}, HIVE_DEFAULT_DELIMITER,
				HIVE_DEFAULT_SERIALIZATION_LIB_NAME, null, transactional);
	}

	public HiveParquetTableDescriptor(String tableName, String[] columnNames,
			String[] columnTypes, String[] partitionKeys) {
		this(HIVE_DEFAULT_DATABASE_NAME, tableName, columnNames, columnTypes,
				partitionKeys, HIVE_DEFAULT_DELIMITER,
				HIVE_DEFAULT_SERIALIZATION_LIB_NAME, null);
	}

	public HiveParquetTableDescriptor(String tableName, String[] columnNames,
			String[] columnTypes, String[] partitionKeys, boolean transactional) {
		this(HIVE_DEFAULT_DATABASE_NAME, tableName, columnNames, columnTypes,
				partitionKeys, HIVE_DEFAULT_DELIMITER,
				HIVE_DEFAULT_SERIALIZATION_LIB_NAME, null, transactional);
	}

	public HiveParquetTableDescriptor(String tableName, String[] columnNames,
			String[] columnTypes, String[] partitionKeys, String delimiter) {
		this(HIVE_DEFAULT_DATABASE_NAME, tableName, columnNames, columnTypes,
				partitionKeys, delimiter, HIVE_DEFAULT_SERIALIZATION_LIB_NAME,
				null);
	}

	public HiveParquetTableDescriptor(String databaseName, String tableName,
			String[] columnNames, String[] columnTypes, Path location) {
		this(databaseName, tableName, columnNames, columnTypes,
				new String[] {}, HIVE_DEFAULT_DELIMITER,
				HIVE_DEFAULT_SERIALIZATION_LIB_NAME, location);
	}

	public HiveParquetTableDescriptor(String databaseName, String tableName,
			String[] columnNames, String[] columnTypes, boolean transactional) {
		this(databaseName, tableName, columnNames, columnTypes,
				new String[] {}, HIVE_DEFAULT_DELIMITER,
				HIVE_DEFAULT_SERIALIZATION_LIB_NAME, null, transactional);
	}

	public HiveParquetTableDescriptor(String databaseName, String tableName,
			String[] columnNames, String[] columnTypes, String[] partitionKeys,
			Path location) {
		this(databaseName, tableName, columnNames, columnTypes, partitionKeys,
				HIVE_DEFAULT_DELIMITER, HIVE_DEFAULT_SERIALIZATION_LIB_NAME,
				location);
	}

	public HiveParquetTableDescriptor(String databaseName, String tableName,
			String[] columnNames, String[] columnTypes, String[] partitionKeys,
			boolean transactional) {
		this(databaseName, tableName, columnNames, columnTypes, partitionKeys,
				HIVE_DEFAULT_DELIMITER, HIVE_DEFAULT_SERIALIZATION_LIB_NAME,
				null, transactional);
	}

	public HiveParquetTableDescriptor(String databaseName, String tableName,
			String[] columnNames, String[] columnTypes, String[] partitionKeys,
			String delimiter) {
		this(databaseName, tableName, columnNames, columnTypes, partitionKeys,
				delimiter, HIVE_DEFAULT_SERIALIZATION_LIB_NAME, null);
	}

	public HiveParquetTableDescriptor(String databaseName, String tableName,
			String[] columnNames, String[] columnTypes, String[] partitionKeys,
			String delimiter, String serializationLib, Path location) {
		this(databaseName, tableName, columnNames, columnTypes, partitionKeys,
				delimiter, serializationLib, location, false);
	}

	public HiveParquetTableDescriptor(String databaseName, String tableName,
			String[] columnNames, String[] columnTypes, String[] partitionKeys,
			String delimiter, String serializationLib, Path location,
			boolean transactional) {
		this(databaseName, tableName, columnNames, columnTypes, partitionKeys,
				delimiter, serializationLib, location, transactional, 0);
	}

	public HiveParquetTableDescriptor(String databaseName, String tableName,
			String[] columnNames, String[] columnTypes, String[] partitionKeys,
			String delimiter, String serializationLib, Path location,
			boolean transactional, int buckets) {

		super(databaseName, tableName, columnNames, columnTypes, partitionKeys,
				delimiter, serializationLib, location, transactional, buckets);

		if (tableName == null || tableName.isEmpty())
			throw new IllegalArgumentException(
					"tableName cannot be null or empty");

		if (databaseName == null || tableName.isEmpty())
			this.databaseName = HIVE_DEFAULT_DATABASE_NAME;
		else
			this.databaseName = databaseName.toLowerCase();

		this.tableName = tableName.toLowerCase();

		// SonarQube: Constructors and methods receiving arrays should clone
		// objects and
		// store the copy. This prevents that future changes from the user
		// affect the internal functionality
		this.columnNames = columnNames == null ? null : columnNames.clone();
		this.columnTypes = columnTypes == null ? null : columnTypes.clone();
		this.partitionKeys = partitionKeys == null ? null : partitionKeys
				.clone();
		this.serializationLib = serializationLib;
		this.transactional = transactional;
		// Only set the delimiter if the serialization lib is Delimited.
		if (delimiter == null
				&& this.serializationLib
						.equals(HIVE_DEFAULT_SERIALIZATION_LIB_NAME))
			this.delimiter = HIVE_DEFAULT_DELIMITER;
		else
			this.delimiter = delimiter;

		if (isPartitioned())
			verifyPartitionKeys();

		if (columnNames ==null || columnTypes ==null || columnNames.length == 0 || columnTypes.length == 0
				|| columnNames.length != columnTypes.length)
			throw new IllegalArgumentException(
					"columnNames and columnTypes cannot be empty and must have the same size");

		if (location != null) {
			if (!location.isAbsolute())
				throw new IllegalArgumentException(
						"location must be a fully qualified absolute path");

			// Store as string since path is not serialisable
			this.location = location.toString();
		}
		this.buckets = buckets;
	}

	private void verifyPartitionKeys() {
		for (String key : partitionKeys) {
			if (!caseInsensitiveContains(columnNames, key))
				throw new IllegalArgumentException(String.format(
						"Given partition key '%s' not present in column names",
						key));
		}
	}

	/**
	 * Converts the instance to a Hive Table object, which can be used with the
	 * MetaStore API.
	 * 
	 * @param serializationLib
	 *
	 * @return a new Table instance.
	 */
	@Override
	public Table toHiveTable() {
		Table table = new Table();
		table.setDbName(getDatabaseName());
		table.setTableName(tableName);
		table.setTableType(TableType.MANAGED_TABLE.toString());

		StorageDescriptor sd = new StorageDescriptor();
		for (int index = 0; index < columnNames.length; index++) {
			String columnName = columnNames[index];
			if (!caseInsensitiveContains(partitionKeys, columnName)) {
				// calling toLowerCase() on the type to match the behaviour of
				// the hive console
				sd.addToCols(new FieldSchema(columnName, columnTypes[index]
						.toLowerCase(), "created by Cascading"));
			}
		}
		SerDeInfo serDeInfo = new SerDeInfo();
		serDeInfo.setSerializationLib(serializationLib);
		Map<String, String> serDeParameters = new HashMap<String, String>();

		if (getDelimiter() != null) {
			serDeParameters.put("serialization.format", getDelimiter());
			serDeParameters.put("field.delim", getDelimiter());
		} else {
			serDeParameters.put("serialization.format", "1");
		}
		serDeInfo.setParameters(serDeParameters);

		sd.setSerdeInfo(serDeInfo);
		sd.setInputFormat(HIVE_DEFAULT_INPUT_FORMAT_NAME);
		sd.setOutputFormat(HIVE_DEFAULT_OUTPUT_FORMAT_NAME);
		if (location != null) {
			table.setTableType(TableType.EXTERNAL_TABLE.toString());
			// Need to set this as well since setting the table type would be
			// too obvious
			table.putToParameters("EXTERNAL", "TRUE");
			sd.setLocation(location);
		}

		table.setSd(sd);

		if (isPartitioned()) {
			table.setPartitionKeys(createPartitionSchema());
			table.setPartitionKeysIsSet(true);
		}

		if (isTransactional()) {
			table.putToParameters(HIVE_ACID_TABLE_PARAMETER_KEY, "TRUE");
		}

		return table;
	}

	private boolean caseInsensitiveContains(String[] data, String key) {
		boolean found = false;
		for (int i = 0; i < data.length && !found; i++) {
			if (data[i].equalsIgnoreCase(key))
				found = true;
		}
		return found;
	}

	private List<FieldSchema> createPartitionSchema() {
		List<String> names = Arrays.asList(columnNames);
		List<FieldSchema> schema = new LinkedList<FieldSchema>();
		for (String partitionKey : partitionKeys) {
			int index = names.indexOf(partitionKey);
			schema.add(new FieldSchema(columnNames[index], columnTypes[index],
					""));
		}
		return schema;
	}

	/*
	 * Below is the fix for issue of reading/writing partition field - fix
	 * provided by Mark
	 */

	/**
	 * Converts the HiveTableDescriptor to a Fields instance. If the table is
	 * partitioned only the columns not part of the partitioning will be
	 * returned.
	 *
	 * @return A Fields instance.
	 */
	public Fields toFields() {
		return toFields(false); // without partition column
	}

	/**
	 * Converts the HiveTableDescriptor to a Fields instance. All columns
	 * returned, with optional partition column.
	 *
	 * @param withPartition
	 *            Option to include partition columns
	 *
	 * @return A Fields instance.
	 */
	public Fields toFields(boolean withPartition) {
		StructTypeInfo typeInfo = toTypeInfo(withPartition);
		return SchemaFactory.newFields(typeInfo);
	}

	public StructTypeInfo toTypeInfo() {
		return toTypeInfo(false); // without partition column
	}

	/**
	 *
	 * Generates StructTypeInfo from columnName and columnTypes.
	 *
	 * @param withPartition
	 *            Option to include partition columns.
	 *
	 * @return
	 */
	public StructTypeInfo toTypeInfo(boolean withPartition) {
		int columnCount = columnNames.length
				- (withPartition ? 0 : partitionKeys.length);

		String[] typeInfoColumns = Arrays.copyOf(columnNames, columnCount);
		String[] typeInfoTypes = Arrays.copyOf(columnTypes, columnCount);
		return toTypeInfo(typeInfoColumns, typeInfoTypes);
	}

	private static StructTypeInfo toTypeInfo(String[] columnNames,
			String[] types) {
		StringBuilder builder = new StringBuilder("struct<");
		for (int i = 0; i < columnNames.length; i++) {
			if (i != 0)
				builder.append(',');

			builder.append(columnNames[i].toLowerCase());
			builder.append(':');
			builder.append(types[i].toLowerCase());
		}
		builder.append('>');
		try {

			return (StructTypeInfo) TypeInfoUtils
					.getTypeInfoFromTypeString(builder.toString());
		} catch (Exception e) {
			throw new RuntimeException("Scheme: "
					+ builder.toString().substring(6), e); // use substring to
															// strip off
															// 'struct'
		}
	}

	static final class SchemaFactory {

		private static final Map<TypeInfo, Class<?>> PRIMITIVES;

		static {
			Map<TypeInfo, Class<?>> primitives = new HashMap<>();
			primitives.put(TypeInfoFactory.stringTypeInfo, String.class);
			primitives.put(TypeInfoFactory.booleanTypeInfo, Boolean.class);
			primitives.put(TypeInfoFactory.byteTypeInfo, Byte.class);
			primitives.put(TypeInfoFactory.shortTypeInfo, Short.class);
			primitives.put(TypeInfoFactory.intTypeInfo, Integer.class);
			primitives.put(TypeInfoFactory.longTypeInfo, Long.class);
			primitives.put(TypeInfoFactory.floatTypeInfo, Float.class);
			primitives.put(TypeInfoFactory.doubleTypeInfo, Double.class);
			primitives.put(TypeInfoFactory.timestampTypeInfo, Timestamp.class);
			primitives.put(TypeInfoFactory.dateTypeInfo, Date.class);
			primitives.put(TypeInfoFactory.binaryTypeInfo, byte[].class);
			PRIMITIVES = Collections.unmodifiableMap(primitives);
		}

		private SchemaFactory() {
		}

		static Fields newFields(StructTypeInfo structTypeInfo) {
			List<String> existingNames = structTypeInfo
					.getAllStructFieldNames();
			List<String> namesList = new ArrayList<>(existingNames.size());

			namesList.addAll(existingNames);
			String[] names = namesList.toArray(new String[namesList.size()]);

			List<TypeInfo> typeInfos = structTypeInfo
					.getAllStructFieldTypeInfos();
			Class<?>[] types = new Class[typeInfos.size()];
			for (int i = 0; i < types.length; i++) {
				Class<?> type = PRIMITIVES.get(typeInfos.get(i));
				if (type == null) {
					type = Object.class;
				}
				types[i] = type;
			}
			return new Fields(names, types);
		}
	}

}
