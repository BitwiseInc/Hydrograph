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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.StandardCharsets;
import hydrograph.engine.jaxb.commontypes.StandardWriteMode;
import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.commontypes.TypeSortOrder;
import hydrograph.engine.jaxb.ofexcel.FieldFormat;
import hydrograph.engine.jaxb.ofexcel.FieldFormat.Field.CopyOfFiled;
import hydrograph.engine.jaxb.ofexcel.SortKeyFields;
import hydrograph.engine.jaxb.ofexcel.SortKeyFields.Field;
import hydrograph.engine.jaxb.ofexcel.TypeOutputExcelInSocket;
import hydrograph.engine.jaxb.outputtypes.ExcelFile;
import hydrograph.engine.jaxb.outputtypes.ExcelFile.CellFormat;
import hydrograph.engine.jaxb.outputtypes.ExcelFile.Charset;
import hydrograph.engine.jaxb.outputtypes.ExcelFile.Path;
import hydrograph.engine.jaxb.outputtypes.ExcelFile.WorksheetName;
import hydrograph.engine.jaxb.outputtypes.ExcelFile.WriteMode;
import hydrograph.ui.common.datastructures.messages.Messages;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.datastructure.property.ExcelConfigurationDataStructure;
import hydrograph.ui.datastructure.property.ExcelFormattingDataStructure;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.WorkSheetValueProperty;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.converter.OutputConverter;
import hydrograph.ui.engine.xpath.ComponentXpath;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
import hydrograph.ui.engine.xpath.ComponentsAttributeAndValue;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * Converter implementation for Output Excel Component
 * 
 * @author Bitwise
 *
 */
public class OutputExcelConverter extends OutputConverter {

	private static final Logger logger = LogFactory.INSTANCE.getLogger(OutputExcelConverter.class);
	private ExcelFile excelFile;

	public OutputExcelConverter(Component component) {
		super(component);
		this.component = component;
		this.properties = component.getProperties();
		this.baseComponent = new ExcelFile();
	}

	@Override
	public void prepareForXML() {
		logger.debug("Generating XML for {}", properties.get(Constants.PARAM_NAME));
		super.prepareForXML();

		excelFile = (ExcelFile) baseComponent;

		// path
		Path path = new Path();
		String fileName = ((String) properties.get(PropertyNameConstants.FILE_NAME.value()));
		String filePath = ((String) properties.get(PropertyNameConstants.PATH.value()));
		if(StringUtils.isNotBlank(fileName) && StringUtils.isNotBlank(filePath)){
			path.setUri(filePath + "/" + fileName);
			excelFile.setPath(path);
		}
		
		// worksheet field name
		WorksheetName worksheetName = new WorksheetName();
		WorkSheetValueProperty workSheetValueProperty = (WorkSheetValueProperty) properties
				.get(PropertyNameConstants.WORK_SHEET_NAME.value());
		if (workSheetValueProperty != null) {
			if (StringUtils.isNotBlank(workSheetValueProperty.getWorksheetName())) {
				worksheetName.setName(workSheetValueProperty.getWorksheetName());
			}
			worksheetName.setIsColumn(workSheetValueProperty.isField());
			excelFile.setWorksheetName(worksheetName);
		}

		// Write mode
		WriteMode writeMode = new WriteMode();
		writeMode.setValue(getWriteMode());
		excelFile.setWriteMode(writeMode);

		// Auto size column
		excelFile.setAutoColumnSize(getBoolean(PropertyNameConstants.AUTO_SIZE_COLUMN.value()));

		// Strip leading qoutes
		excelFile.setStripLeadingQuote(getBoolean(PropertyNameConstants.STRIP_LEADING_QOUTES.value()));

		// charset
		Charset charset = new Charset();
		charset.setValue(getCharset());
		excelFile.setCharset(charset);


		// format
		mapExcelFormat();

		getSortKeys();

		excelFile.setRuntimeProperties(getRuntimeProperties());

	}

	private void getSortKeys() {
		Map<String, String> primaryKeyRow = (Map<String, String>) properties.get(Constants.PARAM_PRIMARY_COLUMN_KEYS);
		List<SortKeyFields.Field> fieldNameList = null;
		SortKeyFields sortKeyFields = new SortKeyFields();
		if (primaryKeyRow != null && !primaryKeyRow.isEmpty()) {
			fieldNameList = sortKeyFields.getField();
			if (fieldNameList == null) {
				fieldNameList = new ArrayList<SortKeyFields.Field>();
			}
			if (!converterHelper.hasAllKeysAsParams(primaryKeyRow)) {
				for (Map.Entry<String, String> primaryKeyRowEntry : primaryKeyRow.entrySet()) {
					if (!ParameterUtil.isParameter(primaryKeyRowEntry.getKey())) {
						Field field = new Field();
						field.setName(primaryKeyRowEntry.getKey());
						field.setOrder(
								TypeSortOrder.fromValue(primaryKeyRowEntry.getValue().toLowerCase(Locale.ENGLISH)));
						fieldNameList.add(field);

					} else {
						converterHelper.addParamTag(this.ID, primaryKeyRowEntry.getKey(),
								ComponentXpathConstants.EXCEL_PRIMARY_KEYS.value(), false);
					}
				}
			} else {
				StringBuffer parameterFieldNames = new StringBuffer();
				Field fieldsAttributes = new Field();
				fieldsAttributes.setName("");
				fieldNameList.add(fieldsAttributes);
				for (Entry<String, String> secondaryKeyRowEntry : primaryKeyRow.entrySet())
					parameterFieldNames.append(secondaryKeyRowEntry.getKey() + " ");
				converterHelper.addParamTag(this.ID, parameterFieldNames.toString(),
						ComponentXpathConstants.EXCEL_PRIMARY_KEYS.value(), true);
			}
			excelFile.setSortKeyFields(sortKeyFields);
		}
	}

	/**
	 * 
	 */
	private void mapExcelFormat() {
		Map<String, String> typeMap = getTypeMap(Messages.EXCEL_CONV_MAP);

		ExcelFormattingDataStructure formattingDataStructure = (ExcelFormattingDataStructure) properties
				.get(PropertyNameConstants.EXCEL_FORMAT.value());

		if (formattingDataStructure != null) {
			String CopyOfFieldName = formattingDataStructure.getCopyOfField();
			List<String> copyOfFieldList = formattingDataStructure.getCopyFieldList();
			List<ExcelConfigurationDataStructure> listOfExcelConfiguration = formattingDataStructure
					.getListOfExcelConfiguration();
			if (listOfExcelConfiguration != null && listOfExcelConfiguration.size() > 0) {
				CellFormat format = new CellFormat();
				// header
				FieldFormat header = new FieldFormat();
				FieldFormat data = new FieldFormat();
				List<FieldFormat.Field> headerFieldList = new ArrayList<>();
				List<FieldFormat.Field> dataFieldList = new ArrayList<>();

				copyOfFieldList.forEach(fn -> {
					FieldFormat.Field field = new FieldFormat.Field();
					CopyOfFiled copyOfFiled = new CopyOfFiled();
					field.setName(fn);
					copyOfFiled.setFieldName(CopyOfFieldName);
					field.setCopyOfFiled(copyOfFiled);
					headerFieldList.add(field);
					dataFieldList.add(field);

				});

				listOfExcelConfiguration.forEach(fn -> {
					FieldFormat.Field hearderField = new FieldFormat.Field();
					FieldFormat.Field dataField = new FieldFormat.Field();
					hearderField.setName(fn.getFieldName());
					dataField.setName(fn.getFieldName());

					List<FieldFormat.Field.Property> headerFieldProperties = new ArrayList<>();
					List<FieldFormat.Field.Property> dataFieldProperties = new ArrayList<>();

					fn.getHeaderMap().forEach((FormatType, value) -> {
						FieldFormat.Field.Property property = new FieldFormat.Field.Property();
						property.setName(FormatType);
						property.setValue(value);
						property.setType(typeMap.get(FormatType));
						headerFieldProperties.add(property);
					});

					fn.getDataMap().forEach((FormatType, value) -> {
						FieldFormat.Field.Property property = new FieldFormat.Field.Property();
						property.setName(FormatType);
						property.setValue(value);
						property.setType(typeMap.get(FormatType));
						dataFieldProperties.add(property);
					});

					// setting header field properties
					hearderField.getProperty().addAll(headerFieldProperties);
					headerFieldList.add(hearderField);

					// setting data field properties
					dataField.getProperty().addAll(dataFieldProperties);
					dataFieldList.add(dataField);

				});

				header.getField().addAll(headerFieldList);
				data.getField().addAll(dataFieldList);

				format.setHeader(header);
				format.setData(data);

				excelFile.setCellFormat(format);
			}
		}

	}

	@Override
	protected List<TypeOutputInSocket> getOutInSocket() {
		logger.debug("Generating TypeOutputInSocket data");
		List<TypeOutputInSocket> outputinSockets = new ArrayList<>();
		for (Link link : component.getTargetConnections()) {
			TypeOutputExcelInSocket outInSocket = new TypeOutputExcelInSocket();
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
	protected List<TypeBaseField> getFieldOrRecord(List<GridRow> list) {
		logger.debug("Generating data for {} for property {}",
				new Object[] { properties.get(Constants.PARAM_NAME), PropertyNameConstants.SCHEMA.value() });

		List<TypeBaseField> typeBaseFields = new ArrayList<>();
		if (list != null && list.size() != 0) {
			for (GridRow object : list)
				typeBaseFields.add(converterHelper.getSchemaGridTargetData(object));

		}
		return typeBaseFields;
	}

	/**
	 * Converts String value to {@link StandardCharsets}
	 * 
	 * @return {@link StandardCharsets}
	 */
	protected StandardCharsets getCharset() {
		logger.debug("Getting StandardCharsets for {}", properties.get(Constants.PARAM_NAME));
		String charset = (String) properties.get(PropertyNameConstants.CHAR_SET.value());
		StandardCharsets targetCharset = null;
		for (StandardCharsets standardCharsets : StandardCharsets.values()) {
			if (standardCharsets.value().equalsIgnoreCase(charset)) {
				targetCharset = standardCharsets;
				break;
			}
		}
		if (targetCharset == null)
			ComponentXpath.INSTANCE.getXpathMap().put(
					ComponentXpathConstants.COMPONENT_CHARSET_XPATH.value().replace(ID, componentName),
					new ComponentsAttributeAndValue(null, charset));
		return targetCharset;
	}

	/**
	 * Converts String value to {@link StandardWriteMode}
	 * 
	 * @return {@link StandardWriteMode}
	 */
	protected StandardWriteMode getWriteMode() {
		logger.debug("Getting WriteMode for {}", properties.get(Constants.PARAM_NAME));
		String writeMode = (String) properties.get(PropertyNameConstants.WRITE_MODE.value());
		StandardWriteMode targetWriteMode = null;
		for (StandardWriteMode standardWriteMode : StandardWriteMode.values()) {
			if (standardWriteMode.value().equalsIgnoreCase(writeMode)) {
				targetWriteMode = standardWriteMode;
				break;
			}
		}
		if (targetWriteMode == null)
			ComponentXpath.INSTANCE.getXpathMap().put(
					ComponentXpathConstants.COMPONENT_WRITEMODE_XPATH.value().replace(ID, componentName),
					new ComponentsAttributeAndValue(null, writeMode));
		return targetWriteMode;
	}

	private Map<String, String> getTypeMap(String delimitedString) {

		HashMap<String, String> typemap = (HashMap<String, String>) Arrays
				.asList(StringUtils.split(delimitedString, "\\|")).stream().map(s -> s.split("\\$"))
				.collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : ""));

		return typemap;
	}

}
