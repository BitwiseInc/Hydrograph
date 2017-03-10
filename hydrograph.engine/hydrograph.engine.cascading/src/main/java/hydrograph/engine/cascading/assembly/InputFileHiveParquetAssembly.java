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
package hydrograph.engine.cascading.assembly;

import cascading.tap.hive.HivePartitionTap;
import cascading.tap.hive.HiveTableDescriptor;
import cascading.tap.hive.HiveTableDescriptor.Factory;
import cascading.tap.hive.HiveTap;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.InputFileHiveBase;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.filters.PartitionFilter;
import hydrograph.engine.cascading.scheme.hive.parquet.HiveParquetScheme;
import hydrograph.engine.cascading.scheme.hive.parquet.HiveParquetTableDescriptor;
import hydrograph.engine.core.component.entity.InputFileHiveParquetEntity;
import hydrograph.engine.core.component.entity.base.HiveEntityBase;
import hydrograph.engine.utilities.HiveConfigurationMapping;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputFileHiveParquetAssembly extends InputFileHiveBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2775585858902811182L;
	private static Logger LOG = LoggerFactory.getLogger(InputFileHiveParquetAssembly.class);
	private HiveParquetTableDescriptor tableDesc;
	private InputFileHiveParquetEntity inputFileHiveParquetEntity;

	public InputFileHiveParquetAssembly(InputFileHiveParquetEntity baseComponentEntity,
			ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	@Override
	protected void prepareScheme() {
		LOG.debug("Applying HiveParquetScheme to read data from Hive");

		// HiveParquetTableDescriptor is developed specifically for handling
		// Parquet File format with Hive. Hence, the object of table descriptor
		// is created in its respective assembly and not in its base class.

		Configuration conf = new Configuration();
		conf.addResource(new Path(HiveConfigurationMapping.getHiveConf("path_to_hive_site_xml")));

		Factory factory = new Factory(conf);
		HiveTableDescriptor tb = factory.newInstance(inputFileHiveParquetEntity.getDatabaseName(),
				inputFileHiveParquetEntity.getTableName());

		tableDesc = new HiveParquetTableDescriptor(tb.getDatabaseName(), tb.getTableName(), tb.getColumnNames(),
				tb.getColumnTypes(), tb.getPartitionKeys(), getHiveExternalTableLocationPath());
		scheme = new HiveParquetScheme(tableDesc);
		scheme.setSourceFields(tableDesc.toFields());
		scheme.setSinkFields(tableDesc.toFields());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hydrograph.engine.cascading.assembly.base.InputFileHiveBase#
	 * initializeHiveTap ()
	 */
	@Override
	protected void initializeHiveTap() {
		LOG.debug("Initializing Hive Tap using HiveParquetTableDescriptor");
		hiveTap = new HiveTap(tableDesc, scheme);
		if (inputFileHiveParquetEntity.getPartitionKeys() != null
				&& inputFileHiveParquetEntity.getPartitionKeys().length > 0) {
			hiveTap = new HivePartitionTap((HiveTap) hiveTap);
			if (isPartitionFilterEnabled())
				addPartitionFilter(((HivePartitionTap) hiveTap));
		}
	}

	private boolean isPartitionFilterEnabled() {
		if (inputFileHiveParquetEntity.getPartitionFilterList().size() > 0)
			return false;
		else
			return true;
	}

	private void addPartitionFilter(HivePartitionTap hivePartitionTap) {
		hivePartitionTap.addSourcePartitionFilter(
				new Fields(convertLowerCase(inputFileHiveParquetEntity.getPartitionKeys())),
				new PartitionFilter(inputFileHiveParquetEntity.getPartitionFilterList()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hydrograph.engine.cascading.assembly.base.InputFileHiveBase#
	 * castHiveEntityFromBase
	 * (hydrograph.engine.assembly.entity.base.HiveEntityBase) /* cast the
	 * hiveEntityBase to InputFileHiveParquetEntity
	 */

	@Override
	public void castHiveEntityFromBase(HiveEntityBase hiveEntityBase) {
		inputFileHiveParquetEntity = (InputFileHiveParquetEntity) hiveEntityBase;

	}
}