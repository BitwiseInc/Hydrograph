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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.ofexcel.FieldFormat;
import hydrograph.engine.jaxb.ofexcel.SortKeyFields;
import hydrograph.engine.jaxb.ofexcel.SortKeyFields.Field;
import hydrograph.engine.jaxb.outputtypes.ExcelFile;
import hydrograph.engine.jaxb.outputtypes.ExcelFile.CellFormat;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.ExcelConfigurationDataStructure;
import hydrograph.ui.datastructure.property.ExcelFormattingDataStructure;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.WorkSheetValueProperty;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.OutputUiConverter;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.OFExcel;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class OutputFileExcelUiConverter. UiConverter for OutputFileExcel
 * component.
 * 
 * @author Bitwise
 */

public class OutputFileExcelUiConverter extends OutputUiConverter {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(OutputFileExcelUiConverter.class);
	private ExcelFile excelFile;

	public OutputFileExcelUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new OFExcel();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching OutPut-Excel-Component for {}", componentName);

		excelFile = (ExcelFile) typeBaseComponent;
		WorkSheetValueProperty workSheetValueProperty = new WorkSheetValueProperty();

		// file path
		if (excelFile.getPath() != null) {
			String path = excelFile.getPath().getUri();
			int index = path.lastIndexOf("/");
			String fileName = path.substring(index + 1);
			String pathValue = path.substring(0, index);
			propertyMap.put(PropertyNameConstants.PATH.value(), pathValue);
			propertyMap.put(PropertyNameConstants.FILE_NAME.value(), fileName);
		}

		// worksheet fieldname
		if (excelFile.getWorksheetName() != null) {
			if (StringUtils.isNotEmpty(excelFile.getWorksheetName().getName())) {
				workSheetValueProperty.setWorksheetName(excelFile.getWorksheetName().getName());
				workSheetValueProperty.setField(excelFile.getWorksheetName().isIsColumn());
				propertyMap.put(PropertyNameConstants.WORK_SHEET_NAME.value(), workSheetValueProperty);
			}
		}

		// write mode
			propertyMap.put(PropertyNameConstants.WRITE_MODE.value(), getWriteMode());

		// strip leading quote
		propertyMap.put(PropertyNameConstants.STRIP_LEADING_QOUTES.value(), convertBooleanValue(
				excelFile.getStripLeadingQuote(), PropertyNameConstants.STRIP_LEADING_QOUTES.value()));

		// Charset
		propertyMap.put(PropertyNameConstants.CHAR_SET.value(), getCharSet());

		// Auto size column
		propertyMap.put(PropertyNameConstants.AUTO_SIZE_COLUMN.value(),
				convertBooleanValue(excelFile.getAutoColumnSize(), PropertyNameConstants.AUTO_SIZE_COLUMN.value()));

		// excel format
		propertyMap.put(PropertyNameConstants.EXCEL_FORMAT.value(), getExcelFormat());

		propertyMap.put(Constants.PARAM_PRIMARY_COLUMN_KEYS, getPrimaryKeys());

		uiComponent.setType(UIComponentsConstants.EXCEL.value());
		uiComponent.setCategory(UIComponentsConstants.OUTPUT_CATEGORY.value());
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(excelFile.getId());
		uiComponent.setProperties(propertyMap);

	}

	private Map<String, String> getPrimaryKeys() {
		LOGGER.debug("Fetching Sort-Primary-Keys-Properties for -{}", componentName);
		Map<String, String> primaryKeyMap = null;
		excelFile = (ExcelFile) typeBaseComponent;
		SortKeyFields sortKeyFields = excelFile.getSortKeyFields();

		if (sortKeyFields != null) {
			primaryKeyMap = new LinkedHashMap<String, String>();
			for (Field sortKeyField : sortKeyFields.getField()) {
				primaryKeyMap.put(sortKeyField.getName(), sortKeyField.getOrder().value());
			}
		}
		return primaryKeyMap;
	}

	private ExcelFormattingDataStructure getExcelFormat() {
		ExcelFile excelFile = (ExcelFile) typeBaseComponent;
		CellFormat cellFormat = excelFile.getCellFormat();
		FieldFormat data = cellFormat.getData();
		FieldFormat header = cellFormat.getHeader();
		String dataCopyOfField;
		ExcelFormattingDataStructure excelFormattingDataStructure = new ExcelFormattingDataStructure();

		List<String> dataCopyFieldList = data.getField().stream()
				.filter(x -> (null != x.getCopyOfFiled() && StringUtils.isNotEmpty(x.getCopyOfFiled().getFieldName())))
				.map(m -> m.getName()).collect(Collectors.toList());
		
		if(dataCopyFieldList !=null && dataCopyFieldList.size()>0){
		 dataCopyOfField = data.getField().stream()
				.filter(x -> (null != x.getCopyOfFiled() && StringUtils.isNotEmpty(x.getCopyOfFiled().getFieldName())))
				.collect(Collectors.toList()).get(0).getCopyOfFiled().getFieldName();
		}else{
			dataCopyOfField ="Select";
		}

		List<ExcelConfigurationDataStructure> listEcds = data.getField().stream()
				.filter(x -> (null != x.getProperty() && x.getProperty().size() > 0)).map(m -> {

					ExcelConfigurationDataStructure ecds = new ExcelConfigurationDataStructure();
					ecds.setFieldName(m.getName());
					Map<String, String> fieldDatapropMap = m.getProperty().stream().collect(Collectors
							.toMap(FieldFormat.Field.Property::getName, FieldFormat.Field.Property::getValue));
					ecds.setDataMap(fieldDatapropMap);
					Map<String, String> fieldHeaderPropMap = header.getField().stream()
							.filter(hf -> (StringUtils.equalsIgnoreCase(hf.getName(), m.getName()))).findFirst().get()
							.getProperty().stream().collect(Collectors.toMap(FieldFormat.Field.Property::getName,
									FieldFormat.Field.Property::getValue));
					ecds.setHeaderMap(fieldHeaderPropMap);
					return ecds;
				}).collect(Collectors.toList());

		excelFormattingDataStructure.setCopyFieldList(dataCopyFieldList);
		excelFormattingDataStructure.setCopyOfField(dataCopyOfField);
		excelFormattingDataStructure.setListOfExcelConfiguration(listEcds);

		return excelFormattingDataStructure;
	}

	private Object getCharSet() {
		ExcelFile excelFile = (ExcelFile) typeBaseComponent;
		Object value = null;
		if (excelFile.getCharset() != null) {
			value = excelFile.getCharset().getValue();
			if (value != null) {
				return excelFile.getCharset().getValue().value();
			} else {
				value = getValue(PropertyNameConstants.CHAR_SET.value());
			}
		}
		return value;

	}

	private Object getWriteMode() {
		ExcelFile excelFile = (ExcelFile) typeBaseComponent;
		Object value = null;
		if (excelFile.getWriteMode() != null) {
			value = excelFile.getWriteMode().getValue();
			if (value != null) {
				return excelFile.getWriteMode().getValue().value();
			} else {
				value = getValue(PropertyNameConstants.WRITE_MODE.value());
			}
		}
		return value;

	}
	
	@Override
	protected Object getSchema(TypeOutputInSocket inSocket) {
		LOGGER.debug("Generating UI-Schema data for OutPut-Delimited-Component - {}", componentName);
		Schema schema = null;
		List<GridRow> gridRow = new ArrayList<>();
		ConverterUiHelper converterUiHelper = new ConverterUiHelper(uiComponent);
		if (inSocket.getSchema() != null
				&& inSocket.getSchema().getFieldOrRecordOrIncludeExternalSchema().size() != 0) {
			schema = new Schema();
			for (Object record : inSocket.getSchema().getFieldOrRecordOrIncludeExternalSchema()) {
				if ((TypeExternalSchema.class).isAssignableFrom(record.getClass())) {
					schema.setIsExternal(true);
					if (((TypeExternalSchema) record).getUri() != null)
						schema.setExternalSchemaPath(((TypeExternalSchema) record).getUri());
					gridRow.addAll(converterUiHelper.loadSchemaFromExternalFile(schema.getExternalSchemaPath(),
							Constants.GENERIC_GRID_ROW));
					schema.setGridRow(gridRow);
				} else {
					gridRow.add(converterUiHelper.getSchema(record));
					schema.setGridRow(gridRow);
					schema.setIsExternal(false);
				}
			}
		}
		return schema;
	}

	@Override
	protected Map<String, String> getRuntimeProperties() {
		LOGGER.debug("Fetching runtime properties for -", componentName);
		TreeMap<String, String> runtimeMap = null;
		TypeProperties typeProperties = ((ExcelFile) typeBaseComponent).getRuntimeProperties();
		if (typeProperties != null) {
			runtimeMap = new TreeMap<>();
			for (Property runtimeProperty : typeProperties.getProperty()) {
				runtimeMap.put(runtimeProperty.getName(), runtimeProperty.getValue());
			}
		}
		return runtimeMap;
	}

}
