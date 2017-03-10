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

import cascading.jdbc.RedshiftScheme;
import cascading.jdbc.RedshiftTableDesc;
import cascading.jdbc.db.DBInputFormat;
import hydrograph.engine.cascading.assembly.infra.ComponentParameters;
import hydrograph.engine.core.component.entity.InputRDBMSEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputRedshiftAssembly extends InputRDBMSAssembly {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2991614126645020920L;
	/**
	 * Redshift Input Component - read records from Redhsift Table.
	 * 
	 */
	
	protected RedshiftTableDesc tableDesc;
	protected RedshiftScheme scheme;
	


	private static Logger LOG = LoggerFactory
			.getLogger(InputRedshiftAssembly.class);

	public InputRedshiftAssembly(InputRDBMSEntity baseComponentEntity,
			ComponentParameters componentParameters) {
		super(baseComponentEntity, componentParameters);
	}


	@Override
	public void intializeRdbmsSpecificDrivers() {

		// For Redshift
		inputFormatClass = DBInputFormat.class;
		driverName = "RedshiftTap.DB_DRIVER";
//	    setJDBCFactory( new RedshiftFactory() );
	}

	

	/**
	 * This method will create the table descriptor and scheme to read the data
	 * from RDBMS Table. In this method, table descriptor and scheme will be
	 * created for specific file format like TextDelimited for Text file, and so
	 * on for other file format like parquet, etc.
	 */

	@Override
	protected void createTableDescAndScheme() {
		// For sql query
		/*if (inputRDBMSEntity.getQuery() != null
				&& inputRDBMSEntity.getQuery() != "") {
			String sql = inputRDBMSEntity.getQuery();

			String countSql = "select count(*) from "
					+ inputRDBMSEntity.getTableName();
			scheme = new RedshiftScheme(columnNames, sql,
					countSql);

		} else {
			tableDesc = new RedshiftTableDesc(inputRDBMSEntity.getTableName(),columnNames
					, columnDefs, null, null);

			scheme = new RedshiftScheme(inputFormatClass, fields, columnNames);
		}*/
	}

	@Override
	protected void initializeRdbmsTap() {
		LOG.debug("Initializing Redshift Tap.");
		
		/*if (inputRDBMSEntity.getQuery() == null
				|| inputRDBMSEntity.getQuery() == "") {
			
			rdbmsTap = new RedshiftTap(inputRDBMSEntity.getJdbcurl() + "/" + inputRDBMSEntity.getDatabaseName(),
					inputRDBMSEntity.getUsername(), inputRDBMSEntity.getPassword(),null,null
					,tableDesc, scheme, SinkMode.REPLACE, false, true);

		} else {
//			rdbmsTap = new JDBCTap(inputRDBMSEntity.getJdbcurl() + "/"
//					+ inputRDBMSEntity.getDatabaseName(),
//					inputRDBMSEntity.getUsername(),
//					inputRDBMSEntity.getPassword(), driverName, scheme);
			rdbmsTap = new RedshiftTap(inputRDBMSEntity.getJdbcurl() + "/" + inputRDBMSEntity.getDatabaseName(),
					inputRDBMSEntity.getUsername(), inputRDBMSEntity.getPassword(),null,null
					,null, scheme, SinkMode.REPLACE, false, true);
			
		}
		((RedshiftTap) rdbmsTap).setBatchSize(inputRDBMSEntity.getBatchSize());
*/
	}


}