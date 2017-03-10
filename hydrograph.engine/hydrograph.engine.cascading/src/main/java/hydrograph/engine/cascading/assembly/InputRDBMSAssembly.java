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

import cascading.flow.FlowDef;
import cascading.jdbc.JDBCScheme;
import cascading.jdbc.JDBCTap;
import cascading.jdbc.TableDesc;
import cascading.jdbc.db.DBInputFormat;
import cascading.pipe.Pipe;
import cascading.tap.SinkMode;
import cascading.tuple.Fields;
import hydrograph.engine.cascading.assembly.base.BaseComponent;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.cascading.assembly.utils.InputOutputFieldsAndTypesCreator;
import hydrograph.engine.core.component.entity.InputRDBMSEntity;
import hydrograph.engine.core.component.entity.elements.OutSocket;
import hydrograph.engine.core.component.entity.elements.SchemaField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public abstract class InputRDBMSAssembly extends BaseComponent<InputRDBMSEntity> {

	/**
	 * RDBMS Input Component - read records from RDBMS Table.
	 *
	 */
	private static final long serialVersionUID = -2946197683137950707L;
	protected FlowDef flowDef;
	protected Pipe pipe;
	protected List<SchemaField> schemaFieldList;
	protected InputRDBMSEntity inputRDBMSEntity;
	protected String[] fieldsDataType;
	@SuppressWarnings("rawtypes")
	Class<? extends DBInputFormat> inputFormatClass;
	protected JDBCScheme scheme;
	private TableDesc tableDesc;
	protected String driverName;
	protected String jdbcURL;
	protected String[] columnDefs = {};
	protected String[] primaryKeys = null;
	protected Fields fields;
	protected String[] columnNames;

	protected JDBCTap rdbmsTap;

	protected final static String TIME_STAMP = "HH:mm:ss";

	protected InputOutputFieldsAndTypesCreator<InputRDBMSEntity> fieldsCreator;

	private static Logger LOG = LoggerFactory.getLogger(InputRDBMSAssembly.class);

	public InputRDBMSAssembly(InputRDBMSEntity baseComponentEntity, ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}

	@Override
	public void initializeEntity(InputRDBMSEntity assemblyEntityBase) {
		inputRDBMSEntity = assemblyEntityBase;

	}

	public abstract  void intializeRdbmsSpecificDrivers();

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.bitwiseglobal.cascading.assembly.base.BaseComponent#createAssembly()
	 * This method call the generate Taps and Pipes and setOutlinks
	 */

	protected void createAssembly() {

		fieldsCreator = new InputOutputFieldsAndTypesCreator<InputRDBMSEntity>(inputRDBMSEntity);
		intializeRdbmsSpecificDrivers();
		generateTapsAndPipes(); // exception handled separately within
		try {
			flowDef = flowDef.addSource(pipe, rdbmsTap);

			if (LOG.isTraceEnabled()) {
				LOG.trace(inputRDBMSEntity.toString());
			}
			for (OutSocket outSocket : inputRDBMSEntity.getOutSocketList()) {

				String[] fieldsArray = new String[inputRDBMSEntity.getFieldsList().size()];
				int i = 0;
				for (SchemaField Fields : inputRDBMSEntity.getFieldsList()) {
					fieldsArray[i++] = Fields.getFieldName();
				}

				LOG.trace("Creating input " + inputRDBMSEntity.getDatabaseType() + " assembly for '"
						+ inputRDBMSEntity.getComponentId() + "' for socket: '" + outSocket.getSocketId()
						+ "' of type: '" + outSocket.getSocketType() + "'");
				setOutLink(outSocket.getSocketType(), outSocket.getSocketId(), inputRDBMSEntity.getComponentId(), pipe,
						new Fields(fieldsArray));
			}
		} catch (Exception e) {
			LOG.error("Error in creating assembly for component '" + inputRDBMSEntity.getComponentId() + "', Error: "
					+ e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method will create the table descriptor and scheme to read the data
	 * from RDBMS Table. In this method, table descriptor and scheme will be
	 * created for specific file format like TextDelimited for Text file, and so
	 * on for other file format like parquet, etc.
	 */
	protected void prepareScheme() {

		fields = fieldsCreator.makeFieldsWithTypes();
		columnNames = fieldsCreator.getFieldNames();
		LOG.debug("Applying " + inputRDBMSEntity.getDatabaseType() + "  schema to read data from RDBMS");

		createTableDescAndScheme();
	}

	protected void createTableDescAndScheme() {
		// For sql query
		if (inputRDBMSEntity.getSelectQuery() != null && inputRDBMSEntity.getSelectQuery() != "") {
			String selectgSql = inputRDBMSEntity.getSelectQuery();

			String countSql = inputRDBMSEntity.getCountQuery();

			scheme = new JDBCScheme(inputFormatClass, fields, columnNames, selectgSql, countSql, -1);

		} else {
			tableDesc = new TableDesc(inputRDBMSEntity.getTableName(), fieldsCreator.getFieldNames(), columnDefs,
					primaryKeys);

			scheme = new JDBCScheme(inputFormatClass, null, fields, columnNames, null, null, -1, null, null, true);
		}
	}

	protected void initializeRdbmsTap() {
		LOG.debug("Initializing RDBMS Tap.");

		if (inputRDBMSEntity.getSelectQuery() == null || inputRDBMSEntity.getSelectQuery() == "") {
			rdbmsTap = new JDBCTap(jdbcURL, inputRDBMSEntity.getUsername(), inputRDBMSEntity.getPassword(), driverName,
					tableDesc, scheme, SinkMode.REPLACE);

		} else {
			rdbmsTap = new JDBCTap(jdbcURL, inputRDBMSEntity.getUsername(), inputRDBMSEntity.getPassword(), driverName,
					scheme);
		}
	}

	public void generateTapsAndPipes() {

		// initializing each pipe and tap
		LOG.debug(inputRDBMSEntity.getDatabaseType() + " Input Component '" + inputRDBMSEntity.getComponentId()
				+ "': [ Database Name: " + inputRDBMSEntity.getDatabaseName()
				+ (inputRDBMSEntity.getTableName() == null ? (", Select Query: " + inputRDBMSEntity.getSelectQuery() + ", Count Query: " + inputRDBMSEntity.getCountQuery())
				: (", Table Name: " + inputRDBMSEntity.getTableName()))
				+ ", Column Names: " + Arrays.toString(fieldsCreator.getFieldNames()) + "]");
		// scheme and tap to be initialized in its specific assembly
		try {
			schemaFieldList = inputRDBMSEntity.getFieldsList();
			fieldsDataType = new String[schemaFieldList.size()];
			int i = 0;
			for (SchemaField eachSchemaField : schemaFieldList) {
				fieldsDataType[i++] = eachSchemaField.getFieldDataType();
			}

			prepareScheme();
		} catch (Exception e) {
			LOG.error("Error in preparing scheme for component '" + inputRDBMSEntity.getComponentId() + "': "
					+ e.getMessage());
			throw new RuntimeException(e);
		}

		flowDef = componentParameters.getFlowDef();
		initializeRdbmsTap();

		pipe = new Pipe(inputRDBMSEntity.getComponentId());
		setHadoopProperties(rdbmsTap.getStepConfigDef());
		setHadoopProperties(pipe.getStepConfigDef());
	}
}