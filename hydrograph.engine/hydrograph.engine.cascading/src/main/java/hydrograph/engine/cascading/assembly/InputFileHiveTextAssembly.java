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
/**
 * 
 */
package hydrograph.engine.cascading.assembly;

import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.SinkMode;
import cascading.tap.hive.HivePartitionTap;
import cascading.tap.hive.HiveTableDescriptor;
import cascading.tap.hive.HiveTap;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.InputFileHiveBase;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.assembly.utils.HiveTypeToCoercibleTypeMapping;
import hydrograph.engine.cascading.filters.PartitionFilter;
import hydrograph.engine.cascading.scheme.HydrographDelimitedParser;
import hydrograph.engine.cascading.scheme.hive.text.HiveTextTableDescriptor;
import hydrograph.engine.core.component.entity.InputFileHiveTextEntity;
import hydrograph.engine.core.component.entity.base.HiveEntityBase;
import hydrograph.engine.utilities.HiveConfigurationMapping;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Arrays;

public class InputFileHiveTextAssembly extends InputFileHiveBase {

	/**
	 * Hive Input File Component - read records as input from Hive Table stored
	 * in Text format.
	 * 
	 */
	private static final long serialVersionUID = -2946197683137950707L;

	protected InputFileHiveTextEntity inputHiveFileEntity;
	private HiveTextTableDescriptor tableDesc;

	private static Logger LOG = LoggerFactory
			.getLogger(InputFileHiveTextAssembly.class);

	public InputFileHiveTextAssembly(
			InputFileHiveTextEntity baseComponentEntity,
			ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	public void validateParameters() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hydrograph.engine.cascading.assembly.base.InputFileHiveBase#prepareScheme
	 * ()
	 */
	@Override
	protected void prepareScheme() {
		LOG.debug("Applying HiveTextScheme to read data from Hive");

		// HiveTextTableDescriptor is developed specifically for handling
		// Text File format with Hive. Hence, the object of table descriptor
		// is created in its respective assembly and not in its base class.
		
		Configuration conf=new Configuration();
		conf.addResource(new Path(HiveConfigurationMapping
				.getHiveConf("path_to_hive_site_xml")));

		HiveTableDescriptor.Factory factory = new HiveTableDescriptor.Factory(conf);
		HiveTableDescriptor tb = factory.newInstance(
				inputHiveFileEntity.getDatabaseName(),
				inputHiveFileEntity.getTableName());

		tableDesc = new HiveTextTableDescriptor(tb.getDatabaseName(),

		tb.getTableName(), tb.getColumnNames(), tb.getColumnTypes(),
				tb.getPartitionKeys(), tb.getDelimiter(), "",
				getHiveExternalTableLocationPath(), false);

		Fields fields = getFieldsToWrite(tb);
		HydrographDelimitedParser delimitedParser = new HydrographDelimitedParser(
				inputHiveFileEntity.getDelimiter() != null ? inputHiveFileEntity.getDelimiter() : "\1",

				inputHiveFileEntity.getQuote(), null,
				inputHiveFileEntity.isStrict(), inputHiveFileEntity.isSafe());

		scheme = new TextDelimited(fields, null, false, false, "UTF-8",
				delimitedParser);

		// scheme = new
		// TextDelimited(fields,inputHiveFileEntity.getDelimiter());
		scheme.setSourceFields(fields);
		scheme.setSinkFields(fields);
	}

	protected void initializeHiveTap() {
		LOG.debug("Initializing Hive Tap using HiveTextTableDescriptor");
		hiveTap = new HiveTap(tableDesc, scheme, SinkMode.KEEP, true);
		if (inputHiveFileEntity.getPartitionKeys() != null
				&& inputHiveFileEntity.getPartitionKeys().length > 0) {
			hiveTap = new HivePartitionTap((HiveTap) hiveTap);
			if (isPartitionFilterEnabled())
				addPartitionFilter(((HivePartitionTap) hiveTap));
		}
	}

	private boolean isPartitionFilterEnabled() {
		if (inputHiveFileEntity.getPartitionFilterList().size() > 0)
			return false;
		else
			return true;
	}

	private void addPartitionFilter(HivePartitionTap hivePartitionTap) {
		hivePartitionTap.addSourcePartitionFilter(new Fields(
				convertLowerCase(inputHiveFileEntity.getPartitionKeys())), new PartitionFilter(
				inputHiveFileEntity.getPartitionFilterList()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bitwiseglobal.cascading.assembly.base.InputFileHiveBase#
	 * castHiveEntityFromBase
	 * (com.bitwiseglobal.assembly.entity.base.HiveEntityBase)
	 * 
	 * 
	 * 
	 * cast the hiveEntityBase to InputFileHiveTextEntity
	 */
	@Override
	public void castHiveEntityFromBase(HiveEntityBase hiveEntityBase) {
		inputHiveFileEntity = (InputFileHiveTextEntity) hiveEntityBase;

	}

	/**
	 * @return Fields.
	 */
	private Fields getFieldsToWrite(HiveTableDescriptor tb) {
		String[] testField = new String[tb.getColumnNames().length
				- tb.getPartitionKeys().length];
		int i = 0;
		for (String inputfield : tb.getColumnNames()) {
			if (!Arrays.asList(tb.getPartitionKeys()).contains(inputfield)) {
				testField[i++] = inputfield;
			}
		}

		return new Fields(testField).applyTypes(getTypes(tb));

	}

	/**
	 * The datatype support for text to skip the partition key write in file.
	 */
	private Type[] getTypes(HiveTableDescriptor tb) {

		Type[] types = new Type[tb.getColumnTypes().length
				- tb.getPartitionKeys().length];
		String[] colTypes = tb.getColumnTypes();

		for (int i = 0; i < types.length; i++) {

			HiveTypeToCoercibleTypeMapping hiveTo = null;
			if (colTypes[i].contains("decimal")) {
				hiveTo = HiveTypeToCoercibleTypeMapping.valueOf("DECIMAL");
			} else
				hiveTo = HiveTypeToCoercibleTypeMapping.valueOf(colTypes[i]
						.toUpperCase());

			types[i] = hiveTo.getMappingType(colTypes[i]);

		}

		return types;
	}

}