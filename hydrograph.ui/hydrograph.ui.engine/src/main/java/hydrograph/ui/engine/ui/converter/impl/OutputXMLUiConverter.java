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
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeBaseField;
import hydrograph.engine.jaxb.commontypes.TypeExternalSchema;
import hydrograph.engine.jaxb.commontypes.TypeOutputInSocket;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.outputtypes.XmlFile;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.XPathGridRow;
import hydrograph.ui.engine.constants.PropertyNameConstants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.OutputUiConverter;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.OXml;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The Class OutputXmlUiConverter Converter implementation for Output XML Component
 * @author Bitwise
 *
 */
public class OutputXMLUiConverter extends OutputUiConverter{

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(OutputXMLUiConverter.class);
	
	public OutputXMLUiConverter(TypeBaseComponent typeBaseComponent, Container container){
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new OXml();
		this.propertyMap = new LinkedHashMap<>();
	}
	
	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching Output-XML-Properties for {}", componentName);
		XmlFile outputXML = (XmlFile) typeBaseComponent;
		
		String filePath = null;
		if (outputXML.getPath() != null){
			if(StringUtils.isNotBlank(outputXML.getPath().getUri())){
				filePath = outputXML.getPath().getUri();
			} else {
				filePath = getValue(PropertyNameConstants.PATH.value());
			}
		}
		propertyMap.put(PropertyNameConstants.PATH.value(), StringUtils.isNotBlank(filePath)? filePath : "");

		String absolutePath = null;
		if (outputXML.getAbsoluteXPath() != null) {
			if (StringUtils.isNotBlank(outputXML.getAbsoluteXPath().getValue())) {
				absolutePath = outputXML.getAbsoluteXPath().getValue();
			} else {
				absolutePath = getValue(PropertyNameConstants.ABSOLUTE_XPATH.value());
			}
		}
		propertyMap.put(PropertyNameConstants.ABSOLUTE_XPATH.value(), StringUtils.isNotBlank(absolutePath)? absolutePath : "");
		
		String rowTag = null;
		if (outputXML.getRowTag() != null) {
			if (StringUtils.isNotBlank(outputXML.getRowTag().getValue())) {
				rowTag = outputXML.getRowTag().getValue();
			} else {
				rowTag = getValue(PropertyNameConstants.ROW_TAG.value());
			}
		}
		propertyMap.put(PropertyNameConstants.ROW_TAG.value(), StringUtils.isNotBlank(rowTag)? rowTag : "");
		
		String rootTag = null;
		if (outputXML.getRootTag() != null) {
			if (StringUtils.isNotBlank(outputXML.getRootTag().getValue())) {
				rootTag = outputXML.getRootTag().getValue();
			} else {
				rootTag = getValue(PropertyNameConstants.ROOT_TAG.value());
			}
		}
		propertyMap.put(PropertyNameConstants.ROOT_TAG.value(), StringUtils.isNotBlank(rootTag)? rootTag : "");
		
		propertyMap.put(PropertyNameConstants.CHAR_SET.value(), getCharSet());
		propertyMap.put(PropertyNameConstants.OVER_WRITE.value(),
				convertToTrueFalseValue(outputXML.getOverWrite(), PropertyNameConstants.OVER_WRITE.value()));
		uiComponent.setType(UIComponentsConstants.XML.value());
		uiComponent.setCategory(UIComponentsConstants.OUTPUT_CATEGORY.value());
		
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(outputXML.getId());
		uiComponent.setProperties(propertyMap);
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
	protected Object getSchema(TypeOutputInSocket outSocket) {
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
					gridRowList.add(xPathGridRow);
					schema.setGridRow(gridRowList);
					schema.setIsExternal(false);
				}
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

	
}