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
import hydrograph.engine.jaxb.commontypes.TypeOperationField;
import hydrograph.engine.jaxb.commontypes.TypeOperationsComponent;
import hydrograph.engine.jaxb.commontypes.TypeOperationsOutSocket;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.FixedWidthGridRow;
import hydrograph.ui.engine.ui.converter.TransformUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.UniqueSequence;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.schema.propagation.helper.SchemaPropagationHelper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

/**
 * This class creates ui-UniqueSequence component from target XML.
 * 
 * @author Bitwise
 * 
 */
public class UniqueSequenceUiConverter extends TransformUiConverter {

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(UniqueSequenceUiConverter.class);
	private String newFieldName = "";

	public UniqueSequenceUiConverter(TypeBaseComponent typeBaseComponent, Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new UniqueSequence();
		this.propertyMap = new LinkedHashMap<>();
	}

	@Override
	public void prepareUIXML() {

		super.prepareUIXML();
		LOGGER.debug("Fetching Transform-Properties for -{}", componentName);
		propertyMap.put(Constants.UNIQUE_SEQUENCE_PROPERTY_NAME, newFieldName);
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(componentName);
		uiComponent.setProperties(propertyMap);
		uiComponent.setType(Constants.UNIQUE_SEQUENCE_TYPE);
		
	}

	protected void getOutPort(TypeOperationsComponent operationsComponent) {
		LOGGER.debug("Generating OutPut Ports for -{}", componentName);
		if (operationsComponent.getOutSocket() != null) {
			for (TypeOperationsOutSocket outSocket : operationsComponent.getOutSocket()) {
				uiComponent.engageOutputPort(outSocket.getId());
				if (outSocket.getPassThroughFieldOrOperationFieldOrExpressionField() != null)
					for (Object outSocketProperties : outSocket.getPassThroughFieldOrOperationFieldOrExpressionField()) {
						if (((TypeOperationField.class).isAssignableFrom(outSocketProperties.getClass()))) {
							newFieldName = ((TypeOperationField) outSocketProperties).getName();
							createComponentOutputSchemaForPropagation(newFieldName);
						}
					}
			}

		}
	}

	private void createComponentOutputSchemaForPropagation(String fieldName) {
		Map<String, ComponentsOutputSchema> schemaMap = new LinkedHashMap<String, ComponentsOutputSchema>();
		ComponentsOutputSchema newComponentsOutputSchema = new ComponentsOutputSchema();
		newComponentsOutputSchema.getFixedWidthGridRowsOutputFields().add(createSchemaForNewField(fieldName));
		schemaMap.put(Constants.FIXED_OUTSOCKET_ID, newComponentsOutputSchema);
		propertyMap.put(Constants.SCHEMA_TO_PROPAGATE, schemaMap);		
	}
	
	private FixedWidthGridRow createSchemaForNewField(String fieldName) {
		FixedWidthGridRow fixedWidthGridRow = SchemaPropagationHelper.INSTANCE.createFixedWidthGridRow(fieldName);
		fixedWidthGridRow.setDataType(8);
		fixedWidthGridRow.setDataTypeValue(Long.class.getCanonicalName());
		return fixedWidthGridRow;
	}
}
