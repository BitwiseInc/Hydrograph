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
package hydrograph.engine.cascading.assembly.base;

import cascading.flow.FlowDef;
import cascading.pipe.Pipe;
import cascading.pipe.assembly.Rename;
import cascading.pipe.assembly.Retain;
import cascading.scheme.Scheme;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.assembly.utils.InputOutputFieldsAndTypesCreator;
import hydrograph.engine.core.component.entity.InputFileHiveParquetEntity;
import hydrograph.engine.core.component.entity.InputFileHiveTextEntity;
import hydrograph.engine.core.component.entity.base.HiveEntityBase;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import hydrograph.engine.utilities.ComponentHelper;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public abstract class InputFileHiveBase extends BaseComponent<HiveEntityBase> {

	/**
	 * Hive Input File Component - read records as input from Hive Table.
	 * 
	 */
	private static final long serialVersionUID = -2946197683137950707L;
	private FlowDef flowDef;
	private Pipe pipe;

	protected HiveEntityBase hiveEntityBase;
	@SuppressWarnings("rawtypes")
	protected Scheme scheme;
	@SuppressWarnings("rawtypes")
	protected Tap hiveTap;

	protected final static String TIME_STAMP = "hh:mm:ss";

	protected InputOutputFieldsAndTypesCreator<HiveEntityBase> fieldsCreator;

	private static Logger LOG = LoggerFactory.getLogger(InputFileHiveBase.class);

	public InputFileHiveBase(HiveEntityBase baseComponentEntity, ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hydrograph.engine.cascading.assembly.base.BaseComponent#
	 * castEntityFromBase
	 * (hydrograph.engine.assembly.entity.base.AssemblyEntityBase) This method
	 * case the AssemblyEntityBase to hiveBaseEntity
	 */
	@Override
	public void initializeEntity(HiveEntityBase assemblyEntityBase) {
		hiveEntityBase = assemblyEntityBase;
		castHiveEntityFromBase(hiveEntityBase);
	}

	public abstract void castHiveEntityFromBase(HiveEntityBase hiveEntityBase);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hydrograph.engine.cascading.assembly.base.BaseComponent#createAssembly()
	 * This method call the generate Taps and Pipes and setOutlinks
	 */
	@Override
	protected void createAssembly() {
		fieldsCreator = new InputOutputFieldsAndTypesCreator<HiveEntityBase>(hiveEntityBase);
		validateDateFormatInHive(fieldsCreator.getFieldNames(), fieldsCreator.getFieldDataTypes(),
				fieldsCreator.getFieldFormat());
		generateTapsAndPipes(); // exception handled separately within
		try {
			flowDef = flowDef.addSource(pipe, hiveTap);

			if (LOG.isTraceEnabled()) {
				LOG.trace(hiveEntityBase.toString());
			}
			for (OutSocket outSocket : hiveEntityBase.getOutSocketList()) {

				String[] fieldsArray = new String[hiveEntityBase.getFieldsList().size()];
				int i = 0;
				for (SchemaField Fields : hiveEntityBase.getFieldsList()) {
					fieldsArray[i++] = Fields.getFieldName();
				}

				LOG.trace("Creating input file hive parquet assembly for '" + hiveEntityBase.getComponentId()
						+ "' for socket: '" + outSocket.getSocketId() + "' of type: '" + outSocket.getSocketType()
						+ "'");
				setOutLink(outSocket.getSocketType(), outSocket.getSocketId(), hiveEntityBase.getComponentId(), pipe,
						new Fields(fieldsArray));

			}
		} catch (Exception e) {
			LOG.error("Error in creating assembly for component '" + hiveEntityBase.getComponentId() + "', Error: "
					+ e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method will validate date format for Hive component. It will throw
	 * an exception if unsupported date format is used.
	 * 
	 * @param fieldNames
	 * @param datatypes
	 * @param dateformat
	 */
	private void validateDateFormatInHive(String[] fieldNames, String[] datatypes, String[] dateformat) {
		for (int i = 0; i < datatypes.length; i++) {
			if ((datatypes[i].toLowerCase().contains("date") && !dateformat[i].contains("yyyy-MM-dd"))
					|| (datatypes[i].toLowerCase().contains("timestamp")
							&& !dateformat[i].contains("yyyy-MM-dd HH:mm:ss"))) {
				throw new RuntimeException("Component: \"" + hiveEntityBase.getComponentId() + "\" - Field \""
						+ fieldNames[i] + "\" has unsupported date format \"" + dateformat[i]
						+ "\". Hive supports only \"yyyy-MM-dd\" format for date datatype and \"yyyy-MM-dd HH:mm:ss\" format for timestamp datatype.");
			}
		}
	}

	/**
	 * This method will create the table descriptor and scheme to read the data
	 * from Hive Table. In this method, table descriptor and scheme will be
	 * created for specific file format like TextDelimited for Text file, and so
	 * on for other file format like parquet, etc.
	 */
	protected abstract void prepareScheme();

	/**
	 * This method will initialize the Hive Tap with its file format specific
	 * table descriptor and scheme. This method will also initialize Hive
	 * Partition Tap if table is partitioned.
	 */
	protected abstract void initializeHiveTap();

	public void generateTapsAndPipes() {

		// initializing each pipe and tap
		LOG.debug("Hive Input Component '" + hiveEntityBase.getComponentId() + "': [ Database Name: "
				+ hiveEntityBase.getDatabaseName() + ", Table Name: " + hiveEntityBase.getTableName()
				+ ", Column Names: " + Arrays.toString(fieldsCreator.getFieldNames()) + ", Partition Column: "
				+ Arrays.toString(hiveEntityBase.getPartitionKeys()) + "]");

		// scheme and tap to be initialized in its specific assembly
		try {
			prepareScheme();
		} catch (Exception e) {
			LOG.error("Error in preparing scheme for component '" + hiveEntityBase.getComponentId() + "': "
					+ e.getMessage());
			throw new RuntimeException(e);
		}

		flowDef = componentParameters.getFlowDef();
		initializeHiveTap();

		pipe = new Pipe(ComponentHelper.getComponentName(getComponentType(hiveEntityBase),
				hiveEntityBase.getComponentId(), hiveEntityBase.getOutSocketList().get(0).getSocketId()));
		Fields outFields = new Fields(convertLowerCase(getFieldsFromSchemaFields()));
		pipe = new Retain(pipe, outFields);
		pipe = new Rename(pipe, outFields, new Fields(getFieldsFromSchemaFields()));

		setHadoopProperties(hiveTap.getStepConfigDef());
		setHadoopProperties(pipe.getStepConfigDef());
	}

	private String[] getFieldsFromSchemaFields() {
		String[] fields = new String[hiveEntityBase.getFieldsList().size()];
		int i = 0;
		for (SchemaField field : hiveEntityBase.getFieldsList()) {
			fields[i++] = field.getFieldName();
		}

		return fields;
	}

	protected String[] convertLowerCase(String[] fields) {
		String[] convertedfields = new String[fields.length];
		int i = 0;
		for (String field : fields) {
			convertedfields[i++] = field.toLowerCase();
		}
		return convertedfields;
	}

	private String getComponentType(HiveEntityBase hiveEntityBase2) {
		if (hiveEntityBase2 instanceof InputFileHiveTextEntity)
			return "inputFileHiveText";
		else if (hiveEntityBase2 instanceof InputFileHiveParquetEntity)
			return "inputFileHiveParquet";
		else
			return "InputFileHive";
	}

	protected Path getHiveExternalTableLocationPath() {
		return hiveEntityBase.getExternalTablePathUri() == null ? null
				: new Path(hiveEntityBase.getExternalTablePathUri());
	}
}