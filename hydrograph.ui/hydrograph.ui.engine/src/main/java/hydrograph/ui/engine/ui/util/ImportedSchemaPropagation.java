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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.JoinMappingGrid;
import hydrograph.ui.datastructure.property.LookupMappingGrid;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.InputField;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.graph.schema.propagation.SchemaPropagation;
import hydrograph.ui.propertywindow.schema.propagation.helper.SchemaPropagationHelper;
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
	public void initiateSchemaPropagationAfterImport(Container container, boolean isUpdateExtrenalSchema) {
		for (Component component : container.getUIComponentList()) {
			Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) component
					.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
			if (schemaMap != null && !StringUtils.equalsIgnoreCase(component.getCategory(), Constants.TRANSFORM))
				SchemaPropagation.INSTANCE.continuousSchemaPropagation(component, schemaMap, isUpdateExtrenalSchema);
	}
		
		
		schemaPropagationForTransformCategory(container);
		removeTempraryProperties(container);
		addPropogatedSchemaToEachComponent(container);
		addOldSchemaMapPropertyToEachComponent(container);
		validateAllComponents(container);
	}
	
	
	private boolean isCheckedAllInputFieldsArePassthrough(Component component) {
		TransformMapping transformMapping = getTransformMapping(component);
		if (transformMapping != null) {
			return transformMapping.isAllInputFieldsArePassthrough();
		}
		return false;
	}
	
	private TransformMapping getTransformMapping(Component component) {
		String componentName = component.getComponentName();
		if (StringUtils.equalsIgnoreCase(Constants.TRANSFORM, componentName)
				|| StringUtils.equalsIgnoreCase(Constants.AGGREGATE, componentName)
				|| StringUtils.equalsIgnoreCase(Constants.NORMALIZE, componentName)
				|| StringUtils.equalsIgnoreCase(Constants.GROUP_COMBINE, componentName)
				|| StringUtils.equalsIgnoreCase(Constants.CUMULATE, componentName)) {
			TransformMapping transformMapping = (TransformMapping) component.getProperties().get(Constants.OPERATION);
			if (transformMapping != null) {
				return transformMapping;
			}
		}
		return null;
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
							if(transformMapping.isAllInputFieldsArePassthrough()){
								addPassThroughFields(transformMapping,component);
								updatePassThroughField(transformMapping,component);
								List<String> passthroughFields=getPassThroughFields(transformMapping.getMapAndPassthroughField());
								addPassthroughFieldsToSchema(passthroughFields,component);
							}
						}
					}
					
				}
			}
		}

	}
	
	// Removed pass through field according to new input.
	private void updatePassThroughField(TransformMapping transformMapping, Component component) {

		List<NameValueProperty> mapAndPassthroughFields = transformMapping.getMapAndPassthroughField();
		Iterator<NameValueProperty> iterator = mapAndPassthroughFields.iterator();
		while (iterator.hasNext()) {
			NameValueProperty nameValueProperty = iterator.next();
			if (isPassThroughField(nameValueProperty) && !isPassThroughFieldPresentInInput(transformMapping, nameValueProperty)) {
				iterator.remove();
			}
		}

	}

	private boolean isPassThroughFieldPresentInInput(TransformMapping transformMapping,
			NameValueProperty nameValueProperty) {
		List<InputField> inputFields = transformMapping.getInputFields();
		boolean isFound = false;
		for (InputField inputField : inputFields) {
			if (inputField.getFieldName().equals(nameValueProperty.getPropertyName())) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}

	private boolean isPassThroughField(NameValueProperty nameValueProperty) {
		if (nameValueProperty.getPropertyName().endsWith(nameValueProperty.getPropertyValue())) {
			return true;
		} else {
			return false;
		}
	}

	private List<String> getPassThroughFields(
			List<NameValueProperty> nameValueProperties) 
			{
		List<String> passthroughField = new LinkedList<>();
		if (!nameValueProperties.isEmpty()) {

			for (NameValueProperty nameValueProperty : nameValueProperties) {
				if (nameValueProperty.getPropertyName().equals(
						nameValueProperty.getPropertyValue())) {
					passthroughField.add(nameValueProperty.getPropertyValue());
				}
			}

		}
		return passthroughField;
			}
	/**
	 * Return gridrow object if schema is present on source component else return null.
	 * @param fieldName
	 * @return 
	 */
	private GridRow getFieldSchema(String fieldName,Component component) {
		List<GridRow> schemaGridRows = getInputFieldSchema(component);
		for (GridRow schemaGridRow : schemaGridRows) {
			if (schemaGridRow.getFieldName().equals(fieldName)) {
				return schemaGridRow;
			}
		}
		return null;
	}
	
	/**
	 * Get Input schema from target link.
	 * @return
	 */
	private List<GridRow> getInputFieldSchema(Component component) {
		ComponentsOutputSchema outputSchema = null;
		List<GridRow> schemaGridRows = new LinkedList<>();
		for (Link link : component.getTargetConnections()) {
			outputSchema = SchemaPropagation.INSTANCE.getComponentsOutputSchema(link);
			if (outputSchema != null)
				for (GridRow row : outputSchema.getSchemaGridOutputFields(null)) {
					schemaGridRows.add(row);
				}
		}
		return schemaGridRows;
	}

	/**
	 * Add Pass through field to schema 
	 * @param passThroughFields
	 */
	private void addPassthroughFieldsToSchema(List<String> passThroughFields,Component component) {
		Schema schema = (Schema)component.getProperties().get(Constants.SCHEMA);
		List<String> currentFieldsInProppogatedSchemaObject = new LinkedList<>();
		for (GridRow gridRow : schema.getGridRow()) {
			currentFieldsInProppogatedSchemaObject.add(gridRow.getFieldName());
		}

		for (String passThroughField : passThroughFields) {
			GridRow schemaGridRow= getFieldSchema(passThroughField,component);
			if(schemaGridRow!=null){
				BasicSchemaGridRow tempSchemaGrid =(BasicSchemaGridRow) schemaGridRow.copy();

				if (!currentFieldsInProppogatedSchemaObject.contains(passThroughField) && !schema.getGridRow().contains(tempSchemaGrid)) {
					schema.getGridRow().add(tempSchemaGrid);
				} else {
					for (int index = 0; index < schema.getGridRow().size(); index++) {
						if (schema.getGridRow().get(index).getFieldName().equals(passThroughField)) {
							schema.getGridRow().set(index, tempSchemaGrid);
						}
					}
				}
			}else{
				schema.getGridRow().add(SchemaPropagationHelper.INSTANCE.createSchemaGridRow(passThroughField));
			}
			}
	}
	
	private void addPassThroughFields(TransformMapping transformMapping,Component component) {
		transformMapping.getInputFields().forEach(inputField->{
			String property=inputField.getFieldName();
			NameValueProperty nameValueProperty=new NameValueProperty();
			nameValueProperty.setPropertyName(property);
			nameValueProperty.setPropertyValue(property);
			nameValueProperty.getFilterProperty().setPropertyname(property);
			if(!transformMapping.getMapAndPassthroughField().contains(nameValueProperty)){
				transformMapping.getMapAndPassthroughField().add(nameValueProperty);
			}
			
			if(!transformMapping.getOutputFieldList().contains(nameValueProperty.getFilterProperty())) {
				transformMapping.getOutputFieldList().add(nameValueProperty.getFilterProperty());
			}
		});
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
				
				if(isCheckedAllInputFieldsArePassthrough(component)) {
					copySchemaFromPreviousComponent(component);
				}
				
				Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) component
						.getProperties().get(Constants.SCHEMA_TO_PROPAGATE);
				
				if (component.getProperties().get(Constants.COPY_FROM_INPUT_PORT_PROPERTY) == null
						&& StringUtils.equalsIgnoreCase(component.getComponentName(), Constants.JOIN)) {
					// For refresh schema case only come here.
					JoinMappingGrid joinMappingGrid = (JoinMappingGrid) component.getProperties()
 							.get(Constants.JOIN_MAP_FIELD);
					
					if (joinMappingGrid != null) {
						String buttonText = joinMappingGrid.getButtonText();
						if (!StringUtils.isEmpty(buttonText) && !"None".endsWith(buttonText)) {
							updateJoinCompOutputSchema(component, buttonText, schemaMap);
						}
					} else {
						component.getProperties().put(Constants.JOIN_MAP_FIELD, new JoinMappingGrid());
					}
				} else if (StringUtils.equalsIgnoreCase(component.getComponentName(), Constants.LOOKUP)
						&& component.getProperties().get(Constants.LOOKUP_MAP_FIELD) == null) {
					component.getProperties().put(Constants.LOOKUP_MAP_FIELD, new LookupMappingGrid());
				}
				
				
				if (schemaMap != null) {
					SchemaPropagation.INSTANCE.continuousSchemaPropagation(component, schemaMap);
					addSchemaForTransformComponents(component);
				} else if(component.getProperties().get(Constants.COPY_FROM_INPUT_PORT_PROPERTY)!=null){
					copySchemaFromInputPort(component);
					SchemaPropagation.INSTANCE.continuousSchemaPropagation(component, (Map) component.getProperties().get(Constants.SCHEMA_TO_PROPAGATE));
				} 
			}
		}

	}

	private void updateJoinCompOutputSchema(Component component, String buttonText, Map<String, ComponentsOutputSchema> schemaMap) {
		String[] split = buttonText.split(" ");
		String inputPortId = split[split.length - 1];
		if ("in0".equals(inputPortId) || "in1".equals(inputPortId)) {
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

	// This method creates rows for schema tab of transform components from propagated schema.
	private void addSchemaForTransformComponents(Component component) {
		if(StringUtils.equalsIgnoreCase(component.getCategory(), Constants.INPUT)
				|| StringUtils.equalsIgnoreCase(component.getCategory(), Constants.OUTPUT)) {
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
	
	private void copySchemaFromPreviousComponent(Component component) {

		List<Link> targetConnections = component.getTargetConnections();
		Map<String, ComponentsOutputSchema> schemaMap = (Map<String, ComponentsOutputSchema>) component.getProperties()
				.get(Constants.SCHEMA_TO_PROPAGATE);
		ComponentsOutputSchema componentsOutputSchema = schemaMap.get(Constants.FIXED_OUTSOCKET_ID);

		for (Link link : targetConnections) {
			ComponentsOutputSchema sourceComponentsOutputSchema = SchemaPropagation.INSTANCE
					.getComponentsOutputSchema(link);
			if (sourceComponentsOutputSchema != null) {
				if (componentsOutputSchema != null && componentsOutputSchema.getBasicGridRowsOutputFields().isEmpty()) {
					componentsOutputSchema.setFixedWidthGridRowsOutputFields(
							sourceComponentsOutputSchema.getFixedWidthGridRowsOutputFields());
				} else {
					updateComponentsOutputSchema(componentsOutputSchema, sourceComponentsOutputSchema);
				}
			}
		}
	}

	private void updateComponentsOutputSchema(ComponentsOutputSchema componentsOutputSchema,
			ComponentsOutputSchema sourceComponentsOutputSchema) {
		List<BasicSchemaGridRow> basicGridRowsOutputFields = componentsOutputSchema.getBasicGridRowsOutputFields();
		List<BasicSchemaGridRow> sourceBasicGridRowsOutputFields = sourceComponentsOutputSchema
				.getBasicGridRowsOutputFields();

		Iterator<BasicSchemaGridRow> iterator = basicGridRowsOutputFields.iterator();
		while (iterator.hasNext()) {
			BasicSchemaGridRow basicSchemaGridRow = iterator.next();
			if (!sourceBasicGridRowsOutputFields.contains(basicSchemaGridRow)
					&& !isMapField(basicSchemaGridRow, componentsOutputSchema)) {
				iterator.remove();
			}
		}

		for (BasicSchemaGridRow sourceBasicGridRowsOutputField : sourceBasicGridRowsOutputFields) {
			if (!basicGridRowsOutputFields.contains(sourceBasicGridRowsOutputField)) {
				basicGridRowsOutputFields.add(sourceBasicGridRowsOutputField);
			}
		}

		componentsOutputSchema.setFixedWidthGridRowsOutputFields(new ArrayList<>());
		for (BasicSchemaGridRow basicSchemaGridRow : basicGridRowsOutputFields) {
			componentsOutputSchema.addSchemaFields(basicSchemaGridRow);
		}
	}


	private boolean isMapField(BasicSchemaGridRow basicSchemaGridRow, ComponentsOutputSchema componentsOutputSchema) {
		boolean isMapField = false;
		Map<String, String> mapFields = componentsOutputSchema.getMapFields();
		if (mapFields != null) {
			Set<String> keySet = mapFields.keySet();
			for (String key : keySet) {
				if (key.equals(basicSchemaGridRow.getFieldName())) {
					isMapField = true;
					break;
				}
			}
		}
		return isMapField;
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
