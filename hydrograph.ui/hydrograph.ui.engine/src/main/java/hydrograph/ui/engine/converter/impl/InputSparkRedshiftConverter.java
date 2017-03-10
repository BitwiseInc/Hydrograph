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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.ElementValueIntegerType;
import hydrograph.engine.jaxb.commontypes.ElementValueStringType;
import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.inputtypes.Sparkredshift;
import hydrograph.engine.jaxb.isparkredshift.TypeInputRedshiftOutSocket;
import hydrograph.engine.jaxb.isparkredshift.TypeInputSparkredshiftBase;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.DatabaseSelectionConfig;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.InputConverter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * Converter implementation for Input RedShift Component
 * @author Bitwise
 *
 */
public class InputSparkRedshiftConverter extends InputConverter {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(InputSparkRedshiftConverter.class);

	public InputSparkRedshiftConverter(Component component) {
		super(component);
		this.baseComponent = new Sparkredshift();
		this.component = component;
		this.properties = component.getProperties();
	}

	@Override
	protected List<TypeInputOutSocket> getInOutSocket() {
		logger.debug("Generating TypeInputOutSocket data for {}", properties.get(Constants.PARAM_NAME));
		List<TypeInputOutSocket> outSockets = new ArrayList<>();
		for (Link link : component.getSourceConnections()) {
			TypeInputRedshiftOutSocket outSocket = new TypeInputRedshiftOutSocket();
			outSocket.setId(link.getSourceTerminal());
			outSocket.setType(link.getSource().getPort(link.getSourceTerminal()).getPortType());
			outSocket.setSchema(getSchema());
			outSocket.getOtherAttributes();
			outSockets.add(outSocket);
		}
		return outSockets;
	}

	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for {}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		Sparkredshift redshiftInput = (Sparkredshift) baseComponent;
		redshiftInput.setRuntimeProperties(getRuntimeProperties());

		ElementValueStringType dataBaseName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.DATABASE_NAME.value()))){
			dataBaseName.setValue(String.valueOf(properties.get(PropertyNameConstants.DATABASE_NAME.value())));
			redshiftInput.setDatabaseName(dataBaseName);
		}
		
		ElementValueStringType hostName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.HOST_NAME.value()))){
			hostName.setValue(String.valueOf(properties.get(PropertyNameConstants.HOST_NAME.value())));
			redshiftInput.setHostName(hostName);
		}
		
		ElementValueIntegerType portNo = new ElementValueIntegerType();
		BigInteger portValue = getPortValue(PropertyNameConstants.PORT_NO.value());
		portNo.setValue(portValue);
		redshiftInput.setPort(portNo);

		setTemporaryDirectoryName(redshiftInput);
		
		ElementValueStringType userName = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.USER_NAME.value()))){
			userName.setValue(String.valueOf(properties.get(PropertyNameConstants.USER_NAME.value())));
			redshiftInput.setUserName(userName);
		}
		
		ElementValueStringType password = new ElementValueStringType();
		if(StringUtils.isNotBlank((String) properties.get(PropertyNameConstants.PASSWORD.value()))){
			password.setValue(String.valueOf(properties.get(PropertyNameConstants.PASSWORD.value())));
			redshiftInput.setPassword(password);
		}
		
		DatabaseSelectionConfig databaseSelectionConfig = (DatabaseSelectionConfig) properties
				.get(PropertyNameConstants.SELECT_OPTION.value());
		if (databaseSelectionConfig != null) {
			if (databaseSelectionConfig.isTableName()) {
				ElementValueStringType tableName = new ElementValueStringType();
				tableName.setValue(databaseSelectionConfig.getTableName());
				redshiftInput.setTableName(tableName);
				
			} else {
				ElementValueStringType sqlQuery = new ElementValueStringType();
				sqlQuery.setValue(databaseSelectionConfig.getSqlQuery());
				redshiftInput.setSelectQuery(sqlQuery);

				ElementValueStringType sqlQueryCounter = new ElementValueStringType();
				sqlQueryCounter.setValue(databaseSelectionConfig.getSqlQueryCounter());
				//redshiftInput.setCountQuery(sqlQueryCounter);
			}
		}

	}

	private void setTemporaryDirectoryName(Sparkredshift redshiftInput) {
		String temporaryDirectoryName =(String) properties.get(PropertyNameConstants.TEMPORARY_DIRECTORY_NAME.value());
		if (StringUtils.isNotBlank(temporaryDirectoryName)) {
			ElementValueStringType temps3Dir = new ElementValueStringType();
			temps3Dir.setValue(temporaryDirectoryName);
			redshiftInput.setTemps3Dir(temps3Dir);
		}
	}
	
	@Override
	protected List<TypeBaseField> getFieldOrRecord(List<GridRow> list) {
		logger.debug("Generating data for {} for property {}",
				new Object[] { properties.get(Constants.PARAM_NAME), PropertyNameConstants.SCHEMA.value() });

		List<TypeBaseField> typeBaseFields = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (GridRow object : list) {
				typeBaseFields.add(converterHelper.getSchemaGridTargetData(object));
			}
		}
		return typeBaseFields;
	}

}
