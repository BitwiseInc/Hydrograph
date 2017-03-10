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

import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.ohiveparquet.HivePartitionFieldsType;
import hydrograph.engine.jaxb.ohiveparquet.HivePathType;
import hydrograph.engine.jaxb.ohiveparquet.HiveType;
import hydrograph.engine.jaxb.ohiveparquet.PartitionFieldBasicType;
import hydrograph.engine.jaxb.ohiveparquet.TypeOutputDelimitedInSocket;
import hydrograph.engine.jaxb.outputtypes.ParquetHiveFile;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.OutputConverter;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
/**
 * Converter implementation for Output Hive Parquet component
 * @author Bitwise
 */
public class OutputHiveParquetConverter extends OutputConverter {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(OutputHiveParquetConverter.class);
	Iterator itr;

	public OutputHiveParquetConverter(Component component) {
		super(component);
		this.component = component;
		this.properties = component.getProperties();
		this.baseComponent = new ParquetHiveFile();
	}

	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for {}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		ParquetHiveFile parquetHive = (ParquetHiveFile) baseComponent;
		parquetHive.setRuntimeProperties(getRuntimeProperties());

		parquetHive.setDatabaseName(getHiveType(PropertyNameConstants.DATABASE_NAME.value()));
		parquetHive.setTableName(getHiveType(PropertyNameConstants.TABLE_NAME.value()));
		if(StringUtils.isNotBlank((String)properties.get(PropertyNameConstants.EXTERNAL_TABLE_PATH.value()))){
			parquetHive.setExternalTablePath(getHivePathType(PropertyNameConstants.EXTERNAL_TABLE_PATH.value()));
		}
		
		if((properties.get(PropertyNameConstants.PARTITION_KEYS.value()))!=null && !((List<String>)properties.get(PropertyNameConstants.PARTITION_KEYS.value())).isEmpty()){
		parquetHive.setPartitionKeys(getPartitionKeys());
		}
		parquetHive.setOverWrite(getTrueFalse(PropertyNameConstants.OVER_WRITE.value()));
	}

	/*
	 * returns hiveType
	 */
	protected HiveType getHiveType(String propertyName) {
		logger.debug("Getting HypeType Value for {}={}", new Object[] {
				propertyName, properties.get(propertyName) });
		if (properties.get(propertyName) != null) {
			HiveType hiveType = new HiveType();
			hiveType.setValue(String.valueOf((String) properties
					.get(propertyName)));
			
				return hiveType;
		}
		return null;
	}

	/*
	 * returns hivePathType
	 */
	protected HivePathType getHivePathType(String propertyName) {
		logger.debug("Getting HypeType Value for {}={}", new Object[] {
				propertyName, properties.get(propertyName) });
		if (properties.get(propertyName) != null) {
			HivePathType hivePathType = new HivePathType();
			hivePathType.setUri(String.valueOf((String) properties
					.get(propertyName)));
			
				return hivePathType;
		}
		return null;
	}

	/*
	 * returns HivePartitionFieldsType
	 */
	private HivePartitionFieldsType getPartitionKeys() {
		List<String> fieldValueSet = (List<String>) properties.get(PropertyNameConstants.PARTITION_KEYS.value());
		
		HivePartitionFieldsType hivePartitionFieldsType = new HivePartitionFieldsType();
		PartitionFieldBasicType partitionFieldBasicType = new PartitionFieldBasicType();
		hivePartitionFieldsType.setField(partitionFieldBasicType);

		if (fieldValueSet != null) {
		itr = fieldValueSet.iterator(); 
		if(itr.hasNext())
		{
		partitionFieldBasicType.setName((String)itr.next());
		}
		if(itr.hasNext())
		{
		addPartitionKey(partitionFieldBasicType);
		}
		}
		return hivePartitionFieldsType;
	}
	
	private void addPartitionKey(PartitionFieldBasicType partfbasic)
	{
	PartitionFieldBasicType field = new PartitionFieldBasicType();
	field.setName((String)itr.next());
	partfbasic.setField(field);
	if(itr.hasNext())
	{
	addPartitionKey(field);
	}

	}
	
	
	@Override
	protected List<TypeOutputInSocket> getOutInSocket() {
		logger.debug("Generating TypeOutputInSocket data");
		List<TypeOutputInSocket> outputinSockets = new ArrayList<>();
		for (Link link : component.getTargetConnections()) {
			TypeOutputDelimitedInSocket outInSocket = new TypeOutputDelimitedInSocket();
			outInSocket.setId(link.getTargetTerminal());
			outInSocket.setFromSocketId(converterHelper.getFromSocketId(link));
			outInSocket.setFromSocketType(link.getSource().getPorts().get(link.getSourceTerminal()).getPortType());
			outInSocket.setType(link.getTarget().getPort(link.getTargetTerminal()).getPortType());
			outInSocket.setSchema(getSchema());
			outInSocket.getOtherAttributes();
			outInSocket.setFromComponentId(link.getSource().getComponentId());
			outputinSockets.add(outInSocket);
		}
		return outputinSockets;
	}

	@Override
	protected List<TypeBaseField> getFieldOrRecord(List<GridRow> gridRowList) {
		logger.debug("Generating data for {} for property {}", new Object[] { properties.get(Constants.PARAM_NAME),
				PropertyNameConstants.SCHEMA.value() });

		List<TypeBaseField> typeBaseFields = new ArrayList<>();
		if (gridRowList != null && gridRowList.size() != 0) {
			for (GridRow object : gridRowList)
				typeBaseFields.add(converterHelper.getSchemaGridTargetData(object));

		}
		return typeBaseFields;
	}
}
