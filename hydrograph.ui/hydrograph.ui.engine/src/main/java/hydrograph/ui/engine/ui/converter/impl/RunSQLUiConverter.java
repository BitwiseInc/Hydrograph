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

package hydrograph.ui.engine.ui.converter.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.CommandUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.RunSQL;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * This class is a UI converter for RunSQL component
 * 
 * @author Bitwise
 * 
 */
public class RunSQLUiConverter extends CommandUiConverter {

	private static final Logger LOGGER = LogFactory.INSTANCE
			.getLogger(RunSQLUiConverter.class);

	public RunSQLUiConverter(TypeBaseComponent typeBaseComponent,
			Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new RunSQL();
		this.propertyMap = new LinkedHashMap<>();		
	}
	
	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching COMMAND-Properties for -{}", componentName);
		hydrograph.engine.jaxb.commandtypes.RunSQL runSQL = (hydrograph.engine.jaxb.commandtypes.RunSQL) typeBaseComponent;	
		
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(runSQL.getId());
		
		propertyMap.put(Constants.BATCH, runSQL.getBatch());
		
		setValueInPropertyMap(PropertyNameConstants.RUN_SQL_DATABASE_CONNECTION_NAME.value(),
				runSQL.getDatabaseConnectionName() == null ? "" : runSQL.getDatabaseConnectionName().getDatabaseConnectionName());
		
		setValueInPropertyMap(PropertyNameConstants.DATABASE_NAME.value(),
				runSQL.getDatabaseName() == null ? "" : runSQL.getDatabaseName().getDatabaseName());
		
		setValueInPropertyMap(PropertyNameConstants.HOST_NAME.value(),
				runSQL.getServerName() == null ? "" : runSQL.getServerName().getIpAddress());

		setValueInPropertyMap(PropertyNameConstants.PORT_NO.value(),
				runSQL.getPortNumber() == null ? "" : runSQL.getPortNumber().getPortNumber());
		
		setValueInPropertyMap(PropertyNameConstants.USER_NAME.value(),
				runSQL.getDbUserName() == null ? "" : runSQL.getDbUserName().getUserName());

		setValueInPropertyMap(PropertyNameConstants.PASSWORD.value(),
				runSQL.getDbPassword() == null ? "" : runSQL.getDbPassword().getPassword());
		
		setValueInPropertyMap(PropertyNameConstants.RUN_SQL_QUERY.value(),
				runSQL.getQueryCommand() == null ? "" : runSQL.getQueryCommand().getQuery());
		
		uiComponent.setProperties(propertyMap);
		
		uiComponent.setType(UIComponentsConstants.RUN_SQL.value());
		
	}

	private void setValueInPropertyMap(String propertyName,Object value){
		propertyMap.put(propertyName, getParameterValue(propertyName,value));
	}
	
	@Override
	protected Map<String, String> getRuntimeProperties() {
		return null;
	}

}
