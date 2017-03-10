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

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GenerateRecordSchemaGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.InputConverter;
import hydrograph.ui.engine.xpath.ComponentXpath;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
import hydrograph.ui.engine.xpath.ComponentsAttributeAndValue;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.FieldDataTypes;
import hydrograph.engine.jaxb.commontypes.ScaleTypeList;
import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.inputtypes.GenerateRecord;
import hydrograph.engine.jaxb.inputtypes.GenerateRecord.RecordCount;

/**
 * This class is used to create target XML for GenerateRecords component.
 * 
 * @author Bitwise
 *
 */
public class GenerateRecordsConverter extends InputConverter {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(GenerateRecordsConverter.class);

	public GenerateRecordsConverter(Component component) {
		super(component);
		this.baseComponent = new GenerateRecord();
		this.component = component;
		this.properties = component.getProperties();
	}


	/* *
	 * This method initiates target XML generation of GenrateRecords component.
	 * 
	 */
	@Override
	public void prepareForXML() {
		LOGGER.debug("Generating XML for {}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();
		GenerateRecord generateRecord = (GenerateRecord) baseComponent;
		generateRecord.setRecordCount(getRecordCount());
		generateRecord.setRuntimeProperties(getRuntimeProperties());
	}

	private RecordCount getRecordCount() {
		RecordCount recordCount = null;
		String recordCountPropertyValue = (String) properties.get(Constants.PARAM_NO_OF_RECORDS);
		if (StringUtils.isNotBlank(recordCountPropertyValue) && !recordCountPropertyValue.trim().isEmpty()) {
			recordCount = new RecordCount();
			try{
			Long longCount=Long.parseLong(recordCountPropertyValue);
			recordCount.setValue(longCount);
			}
			catch(NumberFormatException exception){
				ComponentXpath.INSTANCE.getXpathMap().put(
						(ComponentXpathConstants.COMPONENT_NO_OF_RECORDS_COUNT.value().replace(ID, componentName)),
						new ComponentsAttributeAndValue(null, recordCountPropertyValue));
			}
		}
		return recordCount;
	}

	/* (non-Javadoc)
	 * @see hydrograph.ui.engine.converter.InputConverter#getInOutSocket()
	 */
	@Override
	protected List<TypeInputOutSocket> getInOutSocket() {
		LOGGER.debug("Generating TypeInputOutSocket data for {}", properties.get(Constants.PARAM_NAME));
		List<TypeInputOutSocket> outSockets = new ArrayList<>();
		for (Link link : component.getSourceConnections()) {
			TypeInputOutSocket outSocket = new TypeInputOutSocket();
			outSocket.setId(link.getSourceTerminal());
			outSocket.setType(link.getSource().getPort(link.getSourceTerminal()).getPortType());
			outSocket.setSchema(getSchema());
			outSocket.getOtherAttributes();
			outSockets.add(outSocket);
		}
		return outSockets;
	}

	/* (non-Javadoc)
	 * @see hydrograph.ui.engine.converter.InputConverter#getFieldOrRecord(java.util.List)
	 */
	@Override
	protected List<TypeBaseField> getFieldOrRecord(List<GridRow> gridList) {
		LOGGER.debug("Generating data for {} for property {}", new Object[] { properties.get(Constants.PARAM_NAME),
				PropertyNameConstants.SCHEMA.value() });

		List<TypeBaseField> typeBaseFields = new ArrayList<>();
		if (gridList != null && gridList.size() != 0) {
			for (GridRow object : gridList) {
				typeBaseFields.add(getTypeBaseFieldsFromGenerateRecordsSchema((GenerateRecordSchemaGridRow) object));
			}
		}
		return typeBaseFields;
	}

	private TypeBaseField getTypeBaseFieldsFromGenerateRecordsSchema(
			GenerateRecordSchemaGridRow generateRecordsSchemaGridRow) {

		TypeBaseField typeBaseField = new TypeBaseField();
		typeBaseField.setName(generateRecordsSchemaGridRow.getFieldName());

		for (FieldDataTypes fieldDataType : FieldDataTypes.values()) {
			if (fieldDataType.value().equalsIgnoreCase(generateRecordsSchemaGridRow.getDataTypeValue()))
				typeBaseField.setType(fieldDataType);
		}
		if (FieldDataTypes.JAVA_UTIL_DATE.value().equals(generateRecordsSchemaGridRow.getDataTypeValue())
				&& StringUtils.isNotBlank(generateRecordsSchemaGridRow.getDateFormat()))
			typeBaseField.setFormat(generateRecordsSchemaGridRow.getDateFormat());

		if (generateRecordsSchemaGridRow.getDataTypeValue().equals(FieldDataTypes.JAVA_LANG_DOUBLE.value())
				|| generateRecordsSchemaGridRow.getDataTypeValue().equals(FieldDataTypes.JAVA_MATH_BIG_DECIMAL.value())
				|| generateRecordsSchemaGridRow.getDataTypeValue().equals(FieldDataTypes.JAVA_LANG_FLOAT.value())) {

			for (ScaleTypeList scaleType : ScaleTypeList.values()) {
				if (scaleType.value().equalsIgnoreCase(generateRecordsSchemaGridRow.getScaleTypeValue()))
					typeBaseField.setScaleType(scaleType);
			}

			if (!generateRecordsSchemaGridRow.getScale().trim().isEmpty())
				typeBaseField.setScale(Integer.parseInt(generateRecordsSchemaGridRow.getScale()));
		}
		if (StringUtils.isNotBlank(generateRecordsSchemaGridRow.getScale()))
			typeBaseField.setScale(Integer.parseInt(generateRecordsSchemaGridRow.getScale()));

		if (!generateRecordsSchemaGridRow.getPrecision().trim().isEmpty())
			typeBaseField.setPrecision(Integer.parseInt(generateRecordsSchemaGridRow.getPrecision()));

		typeBaseField.setDescription(generateRecordsSchemaGridRow.getDescription());

		if (StringUtils.isNotBlank(generateRecordsSchemaGridRow.getLength())) {
			typeBaseField.getOtherAttributes().put(new QName(Constants.LENGTH_QNAME),
					generateRecordsSchemaGridRow.getLength());
		}
		if ( StringUtils.isNotBlank(generateRecordsSchemaGridRow.getRangeFrom())) {
			typeBaseField.getOtherAttributes().put(new QName(Constants.RANGE_FROM_QNAME),
					generateRecordsSchemaGridRow.getRangeFrom());
		}
		if (StringUtils.isNotBlank(generateRecordsSchemaGridRow.getRangeTo())) {
			typeBaseField.getOtherAttributes().put(new QName(Constants.RANGE_TO_QNAME),
					generateRecordsSchemaGridRow.getRangeTo());
		}
		if (StringUtils.isNotBlank(generateRecordsSchemaGridRow.getDefaultValue())) {
			typeBaseField.getOtherAttributes().put(new QName(Constants.DEFAULT_VALUE_QNAME),
					generateRecordsSchemaGridRow.getDefaultValue());
		}

		return typeBaseField;
	}
}
