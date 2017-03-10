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

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.ihivetextfile.HivePartitionFieldsType;
import hydrograph.engine.jaxb.ihivetextfile.HivePartitionFilterType;
import hydrograph.engine.jaxb.ihivetextfile.PartitionColumn;
import hydrograph.engine.jaxb.ihivetextfile.PartitionFieldBasicType;
import hydrograph.engine.jaxb.inputtypes.HiveTextFile;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.InputHivePartitionColumn;
import hydrograph.ui.datastructure.property.InputHivePartitionKeyValues;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.InputUiConverter;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.IHiveTextFile;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
/**
 * The class InputHiveTextFileUiConverter
 * 
 * @author Bitwise
 * 
 */

public class InputHiveTextFileUiConverter extends InputUiConverter {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(InputHiveTextFileUiConverter.class);
	private HiveTextFile hiveTextfile;
	private InputHivePartitionKeyValues hivePartitionKeyValues;
	private List<String> partitionKeys;
	

	public InputHiveTextFileUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new IHiveTextFile();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching Input-HiveParquet-Properties for {}", componentName);
		hiveTextfile = (HiveTextFile) typeBaseComponent;
		
		if (hiveTextfile.getDatabaseName() != null){
			propertyMap.put(PropertyNameConstants.DATABASE_NAME.value(), (String)(hiveTextfile.getDatabaseName().getValue()));
		}
		if (hiveTextfile.getTableName() != null){
			propertyMap.put(PropertyNameConstants.TABLE_NAME.value(), (String)(hiveTextfile.getTableName().getValue()));
			}
		if (hiveTextfile.getExternalTablePath() != null && StringUtils.isNotBlank(hiveTextfile.getExternalTablePath().getUri())){
			propertyMap.put(PropertyNameConstants.EXTERNAL_TABLE_PATH.value(), (String)hiveTextfile.getExternalTablePath().getUri());
		}else{
			propertyMap.put(PropertyNameConstants.EXTERNAL_TABLE_PATH.value(),"");
		}
		
		propertyMap.put(PropertyNameConstants.PARTITION_KEYS.value(), getPartitionKeys());
		propertyMap.put(PropertyNameConstants.STRICT.value(),
				convertBooleanValue(hiveTextfile.getStrict(), PropertyNameConstants.STRICT.value()));
		if (hiveTextfile.getDelimiter() != null)
			propertyMap.put(PropertyNameConstants.DELIMITER.value(), hiveTextfile.getDelimiter().getValue());
		if (hiveTextfile.getQuote() != null)
			propertyMap.put(PropertyNameConstants.QUOTE.value(), hiveTextfile.getQuote().getValue());
		propertyMap.put(PropertyNameConstants.IS_SAFE.value(),
				convertBooleanValue(hiveTextfile.getSafe(), PropertyNameConstants.IS_SAFE.value()));
		
		
		uiComponent.setType(UIComponentsConstants.HIVE_TEXTFILE.value());
		uiComponent.setCategory(UIComponentsConstants.INPUT_CATEGORY.value());
		
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(hiveTextfile.getId());
		uiComponent.setProperties(propertyMap);
	}
	
	/*
	 * returns Partition keys list
	 */
	private InputHivePartitionKeyValues getPartitionKeys() {
		LOGGER.debug("Fetching Input Hive Parquet-Partition-Keys-Properties for -{}", componentName);
		
		hivePartitionKeyValues= new InputHivePartitionKeyValues();
		partitionKeys=new ArrayList<>();
		
		hiveTextfile = (HiveTextFile) typeBaseComponent;
		HivePartitionFieldsType typeHivePartitionFields = hiveTextfile.getPartitionKeys();
		if (typeHivePartitionFields != null) {
			if(typeHivePartitionFields.getField()!=null){
			PartitionFieldBasicType partitionFieldBasicType = typeHivePartitionFields.getField();
			partitionKeys.add(partitionFieldBasicType.getName());
			if(partitionFieldBasicType.getField()!=null)
			{
				getKey(partitionFieldBasicType);
			}
			}
		}
		
	
		List<InputHivePartitionColumn> inputHivePartitionColumn = new ArrayList<InputHivePartitionColumn>();

		HivePartitionFilterType hivePartitionFilterType=hiveTextfile.getPartitionFilter();
        if(hivePartitionFilterType!=null){
		List<PartitionColumn> partitionColumn=hivePartitionFilterType.getPartitionColumn();

		if(partitionColumn!=null)
		{
			for(PartitionColumn pc:partitionColumn)
			{
				InputHivePartitionColumn inputHivePartitionColumn3 = new InputHivePartitionColumn();
				inputHivePartitionColumn3.setName(pc.getName());
				inputHivePartitionColumn3.setValue(pc.getValue());
			if(pc.getPartitionColumn()!=null)
			{	
				addFilterKey(pc,inputHivePartitionColumn3);
			}
				inputHivePartitionColumn.add(inputHivePartitionColumn3);
			}
			
		}
        }
		hivePartitionKeyValues.setKey(partitionKeys);
		hivePartitionKeyValues.setKeyValues(inputHivePartitionColumn);
		return hivePartitionKeyValues;
		
	}
	
	private void getKey(PartitionFieldBasicType partition)
	{
		PartitionFieldBasicType partitionFieldBasicType1 = partition.getField();
		partitionKeys.add(partitionFieldBasicType1.getName());
				if(partitionFieldBasicType1.getField()!=null)
				{
					getKey(partitionFieldBasicType1);
				}
	}
	
	private void addFilterKey(PartitionColumn partitionColumn1,InputHivePartitionColumn inputHivePartitionColumn1)
	{
		InputHivePartitionColumn inputHivePartitionColumn2 = new InputHivePartitionColumn();
			PartitionColumn partitionColumn2 = partitionColumn1.getPartitionColumn();
			inputHivePartitionColumn2.setName(partitionColumn2.getName());
		    inputHivePartitionColumn2.setValue(partitionColumn2.getValue());
		    inputHivePartitionColumn1.setInputHivePartitionColumn(inputHivePartitionColumn2);
			if(partitionColumn2.getPartitionColumn()!=null)
			{
				if(partitionColumn2.getPartitionColumn().getValue()!="")
				{
				addFilterKey(partitionColumn2,inputHivePartitionColumn2);
				}
			}
	}
	


	@Override
	protected Map<String, String> getRuntimeProperties() {
		LOGGER.debug("Generating Runtime Properties for -{}", componentName);
		TreeMap<String, String> runtimeMap = null;
		TypeProperties typeProperties = ((HiveTextFile) typeBaseComponent).getRuntimeProperties();
		if (typeProperties != null) {
			runtimeMap = new TreeMap<>();
			for (Property runtimeProperty : typeProperties.getProperty()) {
				runtimeMap.put(runtimeProperty.getName(), runtimeProperty.getValue());
			}
		}
		return runtimeMap;
	}

	@Override
	protected Object getSchema(TypeInputOutSocket outSocket) {
		LOGGER.debug("Generating UI-Schema data for {}", componentName);
		Schema schema = null;
		List<GridRow> gridRowList = new ArrayList<>();
		ConverterUiHelper converterUiHelper = new ConverterUiHelper(uiComponent);
		if (outSocket.getSchema() != null
				&& outSocket.getSchema().getFieldOrRecordOrIncludeExternalSchema().size() != 0) {
			schema = new Schema();
			for (Object record : outSocket.getSchema().getFieldOrRecordOrIncludeExternalSchema()) {
				if ((TypeExternalSchema.class).isAssignableFrom(record.getClass())) {
					schema.setIsExternal(true);
					if (((TypeExternalSchema) record).getUri() != null)
						schema.setExternalSchemaPath(((TypeExternalSchema) record).getUri());
				} else {
					gridRowList.add(converterUiHelper.getSchema(record));
					schema.setGridRow(gridRowList);
					schema.setIsExternal(false);
				}
				saveComponentOutputSchema(outSocket.getId(),gridRowList);
			}
		} 
		return schema;

	}
}
