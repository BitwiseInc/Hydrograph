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

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeInputOutSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.inputtypes.XmlFile;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.InputUiConverter;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.IXml;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class InputXMLUiConverter implementation for Input XML Component
 * @author Bitwise
 *
 */
public class InputXmlUiConverter extends InputUiConverter{

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(InputXmlUiConverter.class);
	
	public InputXmlUiConverter(TypeBaseComponent typeBaseComponent, Container container){
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new IXml();
		this.propertyMap = new LinkedHashMap<>();
	}
	
	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching Input-Xml-Properties for {}", componentName);
		XmlFile xmlFile = (XmlFile) typeBaseComponent;
		
		String filePath = null;
		if (xmlFile.getPath() != null){
			if(StringUtils.isNotBlank(xmlFile.getPath().getUri())){
				filePath = xmlFile.getPath().getUri();
			} else {
				filePath = getValue(PropertyNameConstants.PATH.value());
			}
		}
		propertyMap.put(PropertyNameConstants.PATH.value(), StringUtils.isNotBlank(filePath)? filePath : "");

		String absolutePath = null;
		if (xmlFile.getAbsoluteXPath() != null) {
			if (StringUtils.isNotBlank(xmlFile.getAbsoluteXPath().getValue())) {
				absolutePath = xmlFile.getAbsoluteXPath().getValue();
			} else {
				absolutePath = getValue(PropertyNameConstants.ABSOLUTE_XPATH.value());
			}
		}
		propertyMap.put(PropertyNameConstants.ABSOLUTE_XPATH.value(), StringUtils.isNotBlank(absolutePath)? absolutePath : "");
		
		String rowTag = null;
		if (xmlFile.getRowTag() != null) {
			if (StringUtils.isNotBlank(xmlFile.getRowTag().getValue())) {
				rowTag = xmlFile.getRowTag().getValue();
			} else {
				rowTag = getValue(PropertyNameConstants.ROW_TAG.value());
			}
		}
		propertyMap.put(PropertyNameConstants.ROW_TAG.value(), StringUtils.isNotBlank(rowTag)? rowTag : "");
		
		String rootTag = null;
		if (xmlFile.getRootTag() != null) {
			if (StringUtils.isNotBlank(xmlFile.getRootTag().getValue())) {
				rootTag = xmlFile.getRootTag().getValue();
			} else {
				rootTag = getValue(PropertyNameConstants.ROOT_TAG.value());
			}
		}
		
		
		propertyMap.put(PropertyNameConstants.ROOT_TAG.value(), StringUtils.isNotBlank(rootTag)? rootTag : "");
	
		propertyMap.put(PropertyNameConstants.CHAR_SET.value(), getCharSet());
		
		propertyMap.put(PropertyNameConstants.IS_SAFE.value(),
				convertBooleanValue(xmlFile.getSafe(), PropertyNameConstants.IS_SAFE.value()));
		
		uiComponent.setType(UIComponentsConstants.XML.value());
		uiComponent.setCategory(UIComponentsConstants.INPUT_CATEGORY.value());
		
		
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(xmlFile.getId());
		uiComponent.setProperties(propertyMap);
		if(StringUtils.isNotBlank(absolutePath)){
			updateAbsolutePathToEachGridRow(absolutePath
					,(Schema)propertyMap.get(PropertyNameConstants.SCHEMA.value()));
			
		}
	}
	
	private Object getCharSet() {
		XmlFile xmlFile = (XmlFile) typeBaseComponent;
		Object value = null;
		if (xmlFile.getCharset() != null) {
			value = xmlFile.getCharset().getValue();
			if (value != null) {
				return xmlFile.getCharset().getValue().value();
			} else {
				value = getValue(PropertyNameConstants.CHAR_SET.value());
			}
		}
		return value;
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
					if (((TypeExternalSchema) record).getUri() != null){
						schema.setExternalSchemaPath(((TypeExternalSchema) record).getUri());
					}
					gridRowList.addAll(converterUiHelper.loadSchemaFromExternalFile(schema.getExternalSchemaPath(),
							Constants.XPATH_GRID_ROW));
					schema.setGridRow(gridRowList);
				} else {
					TypeBaseField typeBaseField = (TypeBaseField) record;
					XPathGridRow xPathGridRow = new XPathGridRow();
					converterUiHelper.getCommonSchema(xPathGridRow, typeBaseField);
					xPathGridRow.setXPath(typeBaseField.getOtherAttributes().get(new QName(Constants.ABSOLUTE_OR_RELATIVE_XPATH_QNAME)));
					xPathGridRow.setAbsolutexPath(StringUtils.isNotBlank(xPathGridRow.getXPath())?xPathGridRow.getXPath():"");
					gridRowList.add(xPathGridRow);
					schema.setGridRow(gridRowList);
					schema.setIsExternal(false);
				}
				saveComponentOutputSchema(outSocket.getId(),gridRowList);
			}
		} 
		return schema;
	}

	
	
	@Override
	protected Map<String, String> getRuntimeProperties() {
		LOGGER.debug("Generating Runtime Properties for -{}", componentName);
		TreeMap<String, String> runtimeMap = null;
		TypeProperties typeProperties = ((XmlFile) typeBaseComponent).getRuntimeProperties();
		if (typeProperties != null) {
			runtimeMap = new TreeMap<>();
			for (Property runtimeProperty : typeProperties.getProperty()) {
				runtimeMap.put(runtimeProperty.getName(), runtimeProperty.getValue());
			}
		}
		return runtimeMap;
	}
	
	private void updateAbsolutePathToEachGridRow(String loopXPath, Schema schema) {
		List<GridRow> gridRows=schema.getGridRow();
		gridRows.stream().forEach(gridRow->{
			XPathGridRow xPathGridRow= (XPathGridRow)(gridRow);
			xPathGridRow.setAbsolutexPath(loopXPath.trim()+Path.SEPARATOR+xPathGridRow.getAbsolutexPath());
		});
		
	}
	
}
