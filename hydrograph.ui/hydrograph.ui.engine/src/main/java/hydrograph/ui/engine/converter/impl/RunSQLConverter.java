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
 * limitations under the License.
 *******************************************************************************/
package hydrograph.ui.engine.converter.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commandtypes.RunSQL;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.converter.CommandConverter;
import hydrograph.ui.engine.helper.ConverterHelper;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * 
 * Converter for RunSQL component converter.
 *
 * @author Bitwise
 */
public class RunSQLConverter extends CommandConverter {

	public static final Logger logger = LogFactory.INSTANCE.getLogger(RunSQLConverter.class);

	public RunSQLConverter(Component component) {
		super(component);
		this.baseComponent = new RunSQL();
		this.component = component;
		this.properties = component.getProperties();
		converterHelper = new ConverterHelper(component);
	}

	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for :{}",	properties.get(Constants.PARAM_NAME));
		super.prepareForXML();

		RunSQL runSQL = (RunSQL) baseComponent;

		setDatabaseConnectionNameValue(runSQL);
		setDatabaseNameValue(runSQL);
		setDatabaseServerName(runSQL);
		setDatabasePortNumber(runSQL);
		setDatabaseUserName(runSQL);
		setDatabasePassword(runSQL);
		setDatabaseQueryCommand(runSQL);
	}

	private void setDatabaseConnectionNameValue(RunSQL runSQL) {
		RunSQL.DatabaseConnectionName databaseConnectionName = new RunSQL.DatabaseConnectionName();
		String databaseConnectionNameValue = (String) properties.get(Constants.RUN_SQL_DATABASE_CONNECTION_NAME);
		if(StringUtils.isNotBlank(databaseConnectionNameValue)){
			databaseConnectionName.setDatabaseConnectionName(databaseConnectionNameValue);
			runSQL.setDatabaseConnectionName(databaseConnectionName);
		}
	}
	
	private void setDatabaseNameValue(RunSQL runSQL) {
		RunSQL.DatabaseName databaseConnectionName = new RunSQL.DatabaseName();
		String databaseConnectionNameValue = (String) properties.get(Constants.DATABASE_WIDGET_NAME);
		if(StringUtils.isNotBlank(databaseConnectionNameValue)){
			databaseConnectionName.setDatabaseName(databaseConnectionNameValue);
			runSQL.setDatabaseName(databaseConnectionName);
		}
	}
	
	private void setDatabaseServerName(RunSQL runSQL) {
		RunSQL.ServerName databaseServerName = new RunSQL.ServerName();
		String databaseServerNameValue = (String) properties.get(Constants.HOST_WIDGET_NAME);
		if(StringUtils.isNotBlank(databaseServerNameValue)){
			databaseServerName.setIpAddress(databaseServerNameValue);
			runSQL.setServerName(databaseServerName);
		}
	}

	private void setDatabasePortNumber(RunSQL runSQL) {
		RunSQL.PortNumber databasePortNumber = new RunSQL.PortNumber();
		String databasePortNumberValue = (String) properties.get(Constants.PORT_WIDGET_NAME);
		if(StringUtils.isNotBlank(databasePortNumberValue)){
			databasePortNumber.setPortNumber(databasePortNumberValue);
			runSQL.setPortNumber(databasePortNumber);
		}
	}
	
	private void setDatabaseUserName(RunSQL runSQL) {
		RunSQL.DbUserName databaseUserName = new RunSQL.DbUserName();
		String databaseUserNameValue = (String) properties.get(Constants.USER_NAME_WIDGET_NAME);
		if(StringUtils.isNotBlank(databaseUserNameValue)){
			databaseUserName.setUserName(databaseUserNameValue);
			runSQL.setDbUserName(databaseUserName);
		}
	}

	private void setDatabasePassword(RunSQL runSQL) {
		RunSQL.DbPassword databasePassword = new RunSQL.DbPassword();
		String databasePasswordValue = (String) properties.get(Constants.PASSWORD_WIDGET_NAME);
		if(StringUtils.isNotBlank(databasePasswordValue)){
			databasePassword.setPassword(databasePasswordValue);
			runSQL.setDbPassword(databasePassword);
		}
	}
	
	private void setDatabaseQueryCommand(RunSQL runSQL) {
		RunSQL.QueryCommand databaseQueryCommand = new RunSQL.QueryCommand();
		String databaseQueryCommandValue = (String) properties.get(Constants.RUN_SQL_QUERY);
		if(StringUtils.isNotBlank(databaseQueryCommandValue)){
			databaseQueryCommand.setQuery(databaseQueryCommandValue);
			runSQL.setQueryCommand(databaseQueryCommand);
		}
	}
	
}
