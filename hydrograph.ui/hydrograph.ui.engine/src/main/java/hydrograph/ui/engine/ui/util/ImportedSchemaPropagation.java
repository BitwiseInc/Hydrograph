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

package hydrograph.ui.engine.ui.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.graph.schema.propagation.SchemaPropagation;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;

/**
 * @author Bitwise
 * 
 *         This class is used to propagate schema after importing target XML.
 * 
 */
public class ImportedSchemaPropagation {

	public static final ImportedSchemaPropagation INSTANCE = new ImportedSchemaPropagation();

	private ImportedSchemaPropagation() {
	}

	/**
	 * Initiates schema propagation after importing target XML.
	 * 
	 * @param container
	 * @return
	 */
	public void initiateSchemaPropagationAfterImport(Container container) {
		for (Component component : container.getUIComponentList()) {
			Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) component
					.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
			if (schemaMap != null && !StringUtils.equalsIgnoreCase(component.getCategory(), Constants.TRANSFORM))
				SchemaPropagation.INSTANCE.continuousSchemaPropagation(component, schemaMap);
	}
		
		
		schemaPropagationForTransformCategory(container);
		removeTempraryProperties(container);
		addPropogatedSchemaToEachComponent(container);
		addOldSchemaMapPropertyToEachComponent(container);
		validateAllComponents(container);
		
	}

	private void addOldSchemaMapPropertyToEachComponent(Container container) {
         for(Component component:container.getUIComponentList())
         {
        	 SchemaPropagation.INSTANCE.addOldSchemaMapPropertyToEachComponent(component);
         }	 
	}

	private void addPropogatedSchemaToEachComponent(Container container) {
		for(Component component:container.getUIComponentList())
		{
			addSchemaForTransformComponents(component);
			
		}
		
	}
    
	private List<FilterProperties> convertSchemaToFilterProperty(Component component){
		List<FilterProperties> outputFileds = new ArrayList<>();
		Schema schema = (Schema) component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
		    if(schema==null)
		    {	
			 return outputFileds;  
		    }
			 for (GridRow gridRow : schema.getGridRow()) {
				FilterProperties filterProperty = new FilterProperties();
				filterProperty.setPropertyname(gridRow.getFieldName());
				outputFileds.add(filterProperty);
			}
		return outputFileds;
	}
	
	// Validates properties of all components present in graph
	private void validateAllComponents(Container container) {

		if (container != null) {
			for (Component component : container.getUIComponentList()) {
				if (component instanceof SubjobComponent) {
					String previousValidityStatus = component.getValidityStatus();
					component.validateComponentProperties(true);
					if (StringUtils.equalsIgnoreCase(UIComponentsConstants.ERROR.value(), previousValidityStatus)
							&& StringUtils.equalsIgnoreCase(UIComponentsConstants.VALID.value(), component
									.getProperties().get(UIComponentsConstants.VALIDITY_STATUS.value()).toString())) {
						component.setValidityStatus(UIComponentsConstants.WARN.value());
						component.getProperties().put(UIComponentsConstants.VALIDITY_STATUS.value(),
								UIComponentsConstants.WARN.value());
					}
				} else {
					component.validateComponentProperties(true);
					if (StringUtils.equalsIgnoreCase(Constants.TRANSFORM, component.getComponentName())
							|| StringUtils.equalsIgnoreCase(Constants.AGGREGATE, component.getComponentName())
							|| StringUtils.equalsIgnoreCase(Constants.NORMALIZE, component.getComponentName())
							||StringUtils.equalsIgnoreCase(Constants.GROUP_COMBINE, component.getComponentName())
							|| StringUtils.equalsIgnoreCase(Constants.CUMULATE, component.getComponentName())) {
						if ((TransformMapping) component.getProperties().get(Constants.OPERATION) != null) {
							TransformMapping transformMapping = (TransformMapping) component.getProperties()
									.get(Constants.OPERATION);
							List<FilterProperties> sortedList = SchemaSyncUtility.INSTANCE
									.sortOutputFieldToMatchSchemaSequence(convertSchemaToFilterProperty(component),
											transformMapping);
							transformMapping.getOutputFieldList().clear();
							transformMapping.getOutputFieldList().addAll(sortedList);
						}
					}
					
				}
			}
		}

	}

	private ComponentsOutputSchema getComponentOutputSchemaFromInputPort(Component component, String inputPortId) {
		inputPortId = StringUtils.remove(inputPortId, Constants.COPY_FROM_INPUT_PORT_PROPERTY);
		ComponentsOutputSchema componentsOutputSchema = null;
		for (Link link : component.getTargetConnections()) {
			if (StringUtils.equalsIgnoreCase(link.getTargetTerminal(), inputPortId)) {
				componentsOutputSchema = SchemaPropagation.INSTANCE.getComponentsOutputSchema(link);
			}
		}
		return componentsOutputSchema;
	}

	// This method propagates schema from transform components.
	private void schemaPropagationForTransformCategory(Container container) {
		for (Component component : container.getUIComponentList()) {
			if (StringUtils.equalsIgnoreCase(component.getCategory(), Constants.TRANSFORM)) {
				Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) component
						.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
				if (schemaMap != null) {
					SchemaPropagation.INSTANCE.continuousSchemaPropagation(component, schemaMap);
					addSchemaForTransformComponents(component);
				}else if(component.getProperties().get(Constants.COPY_FROM_INPUT_PORT_PROPERTY)!=null){
					copySchemaFromInputPort(component);
					SchemaPropagation.INSTANCE.continuousSchemaPropagation(component, (Map) component.getProperties().get(Constants.SCHEMA_TO_PROPAGATE));
				}
			}
		}

	}

	// This method creates rows for schema tab of transform components from propagated schema.
	private void addSchemaForTransformComponents(Component component) {
		if(StringUtils.equalsIgnoreCase(component.getCategory(), Constants.INPUT)
				|| StringUtils.equalsIgnoreCase(component.getCategory(), Constants.OUTPUT)){
			return;
		}
		
		
		if (component != null && component.getProperties().get(Constants.SCHEMA_TO_PROPAGATE) != null) {
			Map<String, ComponentsOutputSchema> componentOutputSchemaMap = (Map<String, ComponentsOutputSchema>) component
					.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
			if (componentOutputSchemaMap != null && componentOutputSchemaMap.get(Constants.FIXED_OUTSOCKET_ID) != null) {
				Schema schema = new Schema();
				schema.getGridRow().addAll(
						componentOutputSchemaMap.get(Constants.FIXED_OUTSOCKET_ID).getBasicGridRowsOutputFields());
				arrangeSchemaFieldsAndAddToComponentsSchema(schema, component);
			}
		}
	}

	private void copySchemaFromInputPort(Component component) {
		String inputPortId = (String) component.getProperties().get(Constants.COPY_FROM_INPUT_PORT_PROPERTY);
		if (inputPortId != null) {
			Map<String, ComponentsOutputSchema> schemaMap = new LinkedHashMap<>();
			ComponentsOutputSchema componentsOutputSchema = getComponentOutputSchemaFromInputPort(component,
					inputPortId);
			if (componentsOutputSchema != null) {
				Schema schema = new Schema();
				schema.getGridRow().addAll(componentsOutputSchema.getBasicGridRowsOutputFields());
				schemaMap.put(Constants.FIXED_OUTSOCKET_ID, componentsOutputSchema);
				component.getProperties().put(Constants.SCHEMA_TO_PROPAGATE, schemaMap);
				component.getProperties().put(Constants.SCHEMA_PROPERTY_NAME, schema);
			}
		}
	}

	// This method arranges the schema grid rows of schema tab as arranged in the target XML.
	private void arrangeSchemaFieldsAndAddToComponentsSchema(Schema schema, Component component) {
		List<String> schemaFieldSequnce = null;
		if (schema != null && component != null) {
			Schema arrangedSchema = new Schema();
			if (component.getProperties().get(Constants.SCHEMA_FIELD_SEQUENCE) != null) {
				schemaFieldSequnce = (List<String>) component.getProperties().get(Constants.SCHEMA_FIELD_SEQUENCE);
				for (String fieldName : schemaFieldSequnce) {
					if (schema.getGridRow(fieldName) != null) {
						arrangedSchema.getGridRow().add(schema.getGridRow(fieldName));
					}
				}
				component.getProperties().put(Constants.SCHEMA_PROPERTY_NAME, arrangedSchema);
			} else
				component.getProperties().put(Constants.SCHEMA_PROPERTY_NAME, schema);
		}
	}

	// This method removes temporary properties from components.
	private void removeTempraryProperties(Container container) {
		for (Component component : container.getUIComponentList()){
			component.getProperties().remove(Constants.SCHEMA_FIELD_SEQUENCE);
			component.getProperties().remove(Constants.COPY_FROM_INPUT_PORT_PROPERTY);
		}
	}

}
